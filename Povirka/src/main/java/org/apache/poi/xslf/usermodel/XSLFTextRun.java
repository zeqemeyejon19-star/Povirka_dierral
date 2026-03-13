package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.model.CharacterPropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFPropertiesDelegate;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontCollection;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

/* JADX INFO: loaded from: classes.dex */
public class XSLFTextRun implements TextRun {
    private final XSLFTextParagraph _p;
    private final XmlObject _r;

    protected XSLFTextRun(XmlObject r, XSLFTextParagraph p) {
        this._r = r;
        this._p = p;
        if (!(r instanceof CTRegularTextRun) && !(r instanceof CTTextLineBreak) && !(r instanceof CTTextField)) {
            throw new OpenXML4JRuntimeException("unsupported text run of type " + r.getClass());
        }
    }

    XSLFTextParagraph getParentParagraph() {
        return this._p;
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public String getRawText() {
        CTTextField cTTextField = this._r;
        if (cTTextField instanceof CTTextField) {
            return cTTextField.getT();
        }
        if (cTTextField instanceof CTTextLineBreak) {
            return "\n";
        }
        return ((CTRegularTextRun) cTTextField).getT();
    }

    String getRenderableText() {
        CTTextField tf = this._r;
        if (tf instanceof CTTextField) {
            CTTextField tf2 = tf;
            XSLFSheet sheet = this._p.getParentShape().getSheet();
            if ("slidenum".equals(tf2.getType()) && (sheet instanceof XSLFSlide)) {
                return Integer.toString(((XSLFSlide) sheet).getSlideNumber());
            }
            return tf2.getT();
        }
        if (tf instanceof CTTextLineBreak) {
            return "\n";
        }
        String txt = ((CTRegularTextRun) tf).getT();
        TextRun.TextCap cap = getTextCap();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (c == '\t') {
                buf.append("  ");
            } else {
                int i2 = AnonymousClass11.$SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap[cap.ordinal()];
                if (i2 == 1) {
                    buf.append(Character.toUpperCase(c));
                } else if (i2 == 2) {
                    buf.append(Character.toLowerCase(c));
                } else {
                    buf.append(c);
                }
            }
        }
        return buf.toString();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setText(String text) {
        CTTextField cTTextField = this._r;
        if (cTTextField instanceof CTTextField) {
            cTTextField.setT(text);
        } else {
            if (cTTextField instanceof CTTextLineBreak) {
                return;
            }
            ((CTRegularTextRun) cTTextField).setT(text);
        }
    }

    public XmlObject getXmlObject() {
        return this._r;
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontColor(Color color) {
        setFontColor(DrawPaint.createSolidPaint(color));
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontColor(PaintStyle color) {
        if (!(color instanceof PaintStyle.SolidPaint)) {
            throw new IllegalArgumentException("Currently only SolidPaint is supported!");
        }
        PaintStyle.SolidPaint sp = (PaintStyle.SolidPaint) color;
        Color c = DrawPaint.applyColorTransform(sp.getSolidColor());
        CTTextCharacterProperties rPr = getRPr(true);
        CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
        XSLFColor col = new XSLFColor(fill, getParentParagraph().getParentShape().getSheet().getTheme(), fill.getSchemeClr());
        col.setColor(c);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public PaintStyle getFontColor() {
        final boolean hasPlaceholder = getParentParagraph().getParentShape().getPlaceholder() != null;
        CharacterPropertyFetcher<PaintStyle> fetcher = new CharacterPropertyFetcher<PaintStyle>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.1
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null) {
                    XSLFShape shape = XSLFTextRun.this._p.getParentShape();
                    CTShapeStyle style = shape.getSpStyle();
                    CTSchemeColor phClr = null;
                    if (style != null && style.getFontRef() != null) {
                        phClr = style.getFontRef().getSchemeClr();
                    }
                    XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(props);
                    XSLFSheet sheet = shape.getSheet();
                    PackagePart pp = sheet.getPackagePart();
                    XSLFTheme theme = sheet.getTheme();
                    PaintStyle ps = XSLFShape.selectPaint(fp, phClr, pp, theme, hasPlaceholder);
                    if (ps == null) {
                        return false;
                    }
                    setValue(ps);
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        return fetcher.getValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontSize(Double fontSize) {
        CTTextCharacterProperties rPr = getRPr(true);
        if (fontSize == null) {
            if (rPr.isSetSz()) {
                rPr.unsetSz();
            }
        } else {
            if (fontSize.doubleValue() < 1.0d) {
                throw new IllegalArgumentException("Minimum font size is 1pt but was " + fontSize);
            }
            rPr.setSz((int) (fontSize.doubleValue() * 100.0d));
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public Double getFontSize() {
        double scale = 1.0d;
        CTTextNormalAutofit afit = getParentParagraph().getParentShape().getTextBodyPr().getNormAutofit();
        if (afit != null) {
            scale = ((double) afit.getFontScale()) / 100000.0d;
        }
        CharacterPropertyFetcher<Double> fetcher = new CharacterPropertyFetcher<Double>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.2
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null && props.isSetSz()) {
                    setValue(Double.valueOf(((double) props.getSz()) * 0.01d));
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return null;
        }
        return Double.valueOf(fetcher.getValue().doubleValue() * scale);
    }

    public double getCharacterSpacing() {
        CharacterPropertyFetcher<Double> fetcher = new CharacterPropertyFetcher<Double>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.3
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null && props.isSetSpc()) {
                    setValue(Double.valueOf(((double) props.getSpc()) * 0.01d));
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return 0.0d;
        }
        return fetcher.getValue().doubleValue();
    }

    public void setCharacterSpacing(double spc) {
        CTTextCharacterProperties rPr = getRPr(true);
        if (spc == 0.0d) {
            if (rPr.isSetSpc()) {
                rPr.unsetSpc();
                return;
            }
            return;
        }
        rPr.setSpc((int) (100.0d * spc));
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontFamily(String typeface) {
        FontGroup fg = FontGroup.getFontGroupFirst(getRawText());
        new XSLFFontInfo(fg).setTypeface(typeface);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontFamily(String typeface, FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).setTypeface(typeface);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setFontInfo(FontInfo fontInfo, FontGroup fontGroup) {
        new XSLFFontInfo(fontGroup).copyFrom(fontInfo);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public String getFontFamily() {
        FontGroup fg = FontGroup.getFontGroupFirst(getRawText());
        return new XSLFFontInfo(fg).getTypeface();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public String getFontFamily(FontGroup fontGroup) {
        return new XSLFFontInfo(fontGroup).getTypeface();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public FontInfo getFontInfo(FontGroup fontGroup) {
        XSLFFontInfo fontInfo = new XSLFFontInfo(fontGroup);
        if (fontInfo.getTypeface() != null) {
            return fontInfo;
        }
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public byte getPitchAndFamily() {
        FontGroup fg = FontGroup.getFontGroupFirst(getRawText());
        XSLFFontInfo fontInfo = new XSLFFontInfo(fg);
        FontPitch pitch = fontInfo.getPitch();
        if (pitch == null) {
            pitch = FontPitch.VARIABLE;
        }
        FontFamily family = fontInfo.getFamily();
        if (family == null) {
            family = FontFamily.FF_SWISS;
        }
        return FontPitch.getNativeId(pitch, family);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setStrikethrough(boolean strike) {
        getRPr(true).setStrike(strike ? STTextStrikeType.SNG_STRIKE : STTextStrikeType.NO_STRIKE);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isStrikethrough() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.4
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props == null || !props.isSetStrike()) {
                    return false;
                }
                setValue(Boolean.valueOf(props.getStrike() != STTextStrikeType.NO_STRIKE));
                return true;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isSuperscript() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.5
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props == null || !props.isSetBaseline()) {
                    return false;
                }
                setValue(Boolean.valueOf(props.getBaseline() > 0));
                return true;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    public void setBaselineOffset(double baselineOffset) {
        getRPr(true).setBaseline(((int) baselineOffset) * 1000);
    }

    public void setSuperscript(boolean flag) {
        setBaselineOffset(flag ? 30.0d : 0.0d);
    }

    public void setSubscript(boolean flag) {
        setBaselineOffset(flag ? -25.0d : 0.0d);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isSubscript() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.6
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props == null || !props.isSetBaseline()) {
                    return false;
                }
                setValue(Boolean.valueOf(props.getBaseline() < 0));
                return true;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public TextRun.TextCap getTextCap() {
        CharacterPropertyFetcher<TextRun.TextCap> fetcher = new CharacterPropertyFetcher<TextRun.TextCap>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.7
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null && props.isSetCap()) {
                    int idx = props.getCap().intValue() - 1;
                    setValue(TextRun.TextCap.values()[idx]);
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        return fetcher.getValue() == null ? TextRun.TextCap.NONE : fetcher.getValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setBold(boolean bold) {
        getRPr(true).setB(bold);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isBold() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.8
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null && props.isSetB()) {
                    setValue(Boolean.valueOf(props.getB()));
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setItalic(boolean italic) {
        getRPr(true).setI(italic);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isItalic() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.9
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props != null && props.isSetI()) {
                    setValue(Boolean.valueOf(props.getI()));
                    return true;
                }
                return false;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public void setUnderlined(boolean underline) {
        getRPr(true).setU(underline ? STTextUnderlineType.SNG : STTextUnderlineType.NONE);
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public boolean isUnderlined() {
        CharacterPropertyFetcher<Boolean> fetcher = new CharacterPropertyFetcher<Boolean>(this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.10
            @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
            public boolean fetch(CTTextCharacterProperties props) {
                if (props == null || !props.isSetU()) {
                    return false;
                }
                setValue(Boolean.valueOf(props.getU() != STTextUnderlineType.NONE));
                return true;
            }
        };
        fetchCharacterProperty(fetcher);
        if (fetcher.getValue() == null) {
            return false;
        }
        return fetcher.getValue().booleanValue();
    }

    protected CTTextCharacterProperties getRPr(boolean create) {
        CTTextField cTTextField = this._r;
        if (cTTextField instanceof CTTextField) {
            CTTextField tf = cTTextField;
            if (tf.isSetRPr()) {
                return tf.getRPr();
            }
            if (create) {
                return tf.addNewRPr();
            }
            return null;
        }
        if (cTTextField instanceof CTTextLineBreak) {
            CTTextLineBreak tlb = (CTTextLineBreak) cTTextField;
            if (tlb.isSetRPr()) {
                return tlb.getRPr();
            }
            if (create) {
                return tlb.addNewRPr();
            }
            return null;
        }
        CTRegularTextRun tr = (CTRegularTextRun) cTTextField;
        if (tr.isSetRPr()) {
            return tr.getRPr();
        }
        if (create) {
            return tr.addNewRPr();
        }
        return null;
    }

    public String toString() {
        return "[" + getClass() + "]" + getRawText();
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public XSLFHyperlink createHyperlink() {
        XSLFHyperlink hl = getHyperlink();
        if (hl != null) {
            return hl;
        }
        CTTextCharacterProperties rPr = getRPr(true);
        return new XSLFHyperlink(rPr.addNewHlinkClick(), this._p.getParentShape().getSheet());
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public XSLFHyperlink getHyperlink() {
        CTHyperlink hl;
        CTTextCharacterProperties rPr = getRPr(false);
        if (rPr == null || (hl = rPr.getHlinkClick()) == null) {
            return null;
        }
        return new XSLFHyperlink(hl, this._p.getParentShape().getSheet());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean fetchCharacterProperty(CharacterPropertyFetcher<?> fetcher) {
        XSLFTextShape shape = this._p.getParentShape();
        XSLFSheet sheet = shape.getSheet();
        CTTextCharacterProperties rPr = getRPr(false);
        if ((rPr != null && fetcher.fetch(rPr)) || shape.fetchShapeProperty(fetcher)) {
            return true;
        }
        CTPlaceholder ph = shape.getCTPlaceholder();
        if (ph == null) {
            XMLSlideShow ppt = sheet.getSlideShow();
            CTTextParagraphProperties themeProps = ppt.getDefaultParagraphStyle(this._p.getIndentLevel());
            if (themeProps != null && fetcher.fetch(themeProps)) {
                return true;
            }
        }
        CTTextParagraphProperties defaultProps = this._p.getDefaultMasterStyle();
        if (defaultProps == null || !fetcher.fetch(defaultProps)) {
            return false;
        }
        return true;
    }

    void copy(XSLFTextRun r) {
        String srcFontFamily = r.getFontFamily();
        if (srcFontFamily != null && !srcFontFamily.equals(getFontFamily())) {
            setFontFamily(srcFontFamily);
        }
        PaintStyle srcFontColor = r.getFontColor();
        if (srcFontColor != null && !srcFontColor.equals(getFontColor())) {
            setFontColor(srcFontColor);
        }
        double srcFontSize = r.getFontSize().doubleValue();
        if (srcFontSize != getFontSize().doubleValue()) {
            setFontSize(Double.valueOf(srcFontSize));
        }
        boolean bold = r.isBold();
        if (bold != isBold()) {
            setBold(bold);
        }
        boolean italic = r.isItalic();
        if (italic != isItalic()) {
            setItalic(italic);
        }
        boolean underline = r.isUnderlined();
        if (underline != isUnderlined()) {
            setUnderlined(underline);
        }
        boolean strike = r.isStrikethrough();
        if (strike != isStrikethrough()) {
            setStrikethrough(strike);
        }
    }

    @Override // org.apache.poi.sl.usermodel.TextRun
    public TextRun.FieldType getFieldType() {
        CTTextField cTTextField = this._r;
        if (cTTextField instanceof CTTextField) {
            CTTextField tf = cTTextField;
            if ("slidenum".equals(tf.getType())) {
                return TextRun.FieldType.SLIDE_NUMBER;
            }
            return null;
        }
        return null;
    }

    private class XSLFFontInfo implements FontInfo {
        private final FontGroup fontGroup;

        private XSLFFontInfo(FontGroup fontGroup) {
            this.fontGroup = fontGroup != null ? fontGroup : FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText());
        }

        public void copyFrom(FontInfo fontInfo) {
            CTTextFont tf = getXmlObject(true);
            setTypeface(fontInfo.getTypeface());
            setCharset(fontInfo.getCharset());
            FontPitch pitch = fontInfo.getPitch();
            FontFamily family = fontInfo.getFamily();
            if (pitch == null && family == null) {
                if (tf.isSetPitchFamily()) {
                    tf.unsetPitchFamily();
                }
            } else {
                setPitch(pitch);
                setFamily(family);
            }
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public Integer getIndex() {
            return null;
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public void setIndex(int index) {
            throw new UnsupportedOperationException("setIndex not supported by XSLFFontInfo.");
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public String getTypeface() {
            CTTextFont tf = getXmlObject(false);
            if (tf == null || !tf.isSetTypeface()) {
                return null;
            }
            return tf.getTypeface();
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public void setTypeface(String typeface) {
            if (typeface != null) {
                getXmlObject(true).setTypeface(typeface);
                return;
            }
            CTTextCharacterProperties props = XSLFTextRun.this.getRPr(false);
            if (props == null) {
                return;
            }
            FontGroup fg = FontGroup.getFontGroupFirst(XSLFTextRun.this.getRawText());
            int i = AnonymousClass11.$SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup[fg.ordinal()];
            if (i == 2) {
                if (props.isSetEa()) {
                    props.unsetEa();
                }
            } else if (i == 3) {
                if (props.isSetCs()) {
                    props.unsetCs();
                }
            } else if (i != 4) {
                if (props.isSetLatin()) {
                    props.unsetLatin();
                }
            } else if (props.isSetSym()) {
                props.unsetSym();
            }
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public FontCharset getCharset() {
            CTTextFont tf = getXmlObject(false);
            if (tf == null || !tf.isSetCharset()) {
                return null;
            }
            return FontCharset.valueOf(tf.getCharset() & 255);
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public void setCharset(FontCharset charset) {
            CTTextFont tf = getXmlObject(true);
            if (charset != null) {
                tf.setCharset((byte) charset.getNativeId());
            } else if (tf.isSetCharset()) {
                tf.unsetCharset();
            }
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public FontFamily getFamily() {
            CTTextFont tf = getXmlObject(false);
            if (tf == null || !tf.isSetPitchFamily()) {
                return null;
            }
            return FontFamily.valueOfPitchFamily(tf.getPitchFamily());
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public void setFamily(FontFamily family) {
            CTTextFont tf = getXmlObject(true);
            if (family == null && !tf.isSetPitchFamily()) {
                return;
            }
            FontPitch pitch = tf.isSetPitchFamily() ? FontPitch.valueOfPitchFamily(tf.getPitchFamily()) : FontPitch.VARIABLE;
            byte pitchFamily = FontPitch.getNativeId(pitch, family != null ? family : FontFamily.FF_SWISS);
            tf.setPitchFamily(pitchFamily);
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public FontPitch getPitch() {
            CTTextFont tf = getXmlObject(false);
            if (tf == null || !tf.isSetPitchFamily()) {
                return null;
            }
            return FontPitch.valueOfPitchFamily(tf.getPitchFamily());
        }

        @Override // org.apache.poi.common.usermodel.fonts.FontInfo
        public void setPitch(FontPitch pitch) {
            CTTextFont tf = getXmlObject(true);
            if (pitch == null && !tf.isSetPitchFamily()) {
                return;
            }
            FontFamily family = tf.isSetPitchFamily() ? FontFamily.valueOfPitchFamily(tf.getPitchFamily()) : FontFamily.FF_SWISS;
            byte pitchFamily = FontPitch.getNativeId(pitch != null ? pitch : FontPitch.VARIABLE, family);
            tf.setPitchFamily(pitchFamily);
        }

        private CTTextFont getXmlObject(boolean create) {
            if (create) {
                return getCTTextFont(XSLFTextRun.this.getRPr(true), true);
            }
            CharacterPropertyFetcher<CTTextFont> visitor = new CharacterPropertyFetcher<CTTextFont>(XSLFTextRun.this._p.getIndentLevel()) { // from class: org.apache.poi.xslf.usermodel.XSLFTextRun.XSLFFontInfo.1
                @Override // org.apache.poi.xslf.model.CharacterPropertyFetcher
                public boolean fetch(CTTextCharacterProperties props) {
                    CTTextFont font = XSLFFontInfo.this.getCTTextFont(props, false);
                    if (font == null) {
                        return false;
                    }
                    setValue(font);
                    return true;
                }
            };
            XSLFTextRun.this.fetchCharacterProperty(visitor);
            return visitor.getValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public CTTextFont getCTTextFont(CTTextCharacterProperties props, boolean create) {
            CTTextFont font;
            if (props == null) {
                return null;
            }
            int i = AnonymousClass11.$SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup[this.fontGroup.ordinal()];
            if (i == 2) {
                font = props.getEa();
                if (font == null && create) {
                    font = props.addNewEa();
                }
            } else if (i == 3) {
                font = props.getCs();
                if (font == null && create) {
                    font = props.addNewCs();
                }
            } else if (i != 4) {
                font = props.getLatin();
                if (font == null && create) {
                    font = props.addNewLatin();
                }
            } else {
                font = props.getSym();
                if (font == null && create) {
                    font = props.addNewSym();
                }
            }
            if (font == null) {
                return null;
            }
            String typeface = font.isSetTypeface() ? font.getTypeface() : "";
            if (!typeface.startsWith("+mj-") && !typeface.startsWith("+mn-")) {
                return font;
            }
            XSLFTheme theme = XSLFTextRun.this._p.getParentShape().getSheet().getTheme();
            CTFontScheme fontTheme = theme.getXmlObject().getThemeElements().getFontScheme();
            CTFontCollection coll = typeface.startsWith("+mj-") ? fontTheme.getMajorFont() : fontTheme.getMinorFont();
            String fgStr = typeface.substring(4);
            CTTextFont font2 = "ea".equals(fgStr) ? coll.getEa() : "cs".equals(fgStr) ? coll.getCs() : coll.getLatin();
            return (font2 == null || !font2.isSetTypeface() || "".equals(font2.getTypeface())) ? coll.getLatin() : font2;
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xslf.usermodel.XSLFTextRun$11, reason: invalid class name */
    static /* synthetic */ class AnonymousClass11 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap;

        static {
            int[] iArr = new int[FontGroup.values().length];
            $SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup = iArr;
            try {
                iArr[FontGroup.LATIN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup[FontGroup.EAST_ASIAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup[FontGroup.COMPLEX_SCRIPT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$fonts$FontGroup[FontGroup.SYMBOL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[TextRun.TextCap.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap = iArr2;
            try {
                iArr2[TextRun.TextCap.ALL.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$TextRun$TextCap[TextRun.TextCap.SMALL.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
        }
    }
}
