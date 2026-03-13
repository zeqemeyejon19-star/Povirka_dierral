package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.Internal;
import org.apache.poi.wp.usermodel.CharacterRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPictureNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdnRef;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrClear;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalAlignRun;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class XWPFRun implements ISDTContents, IRunElement, CharacterRun {
    private IRunBody parent;
    private String pictureText;
    private List<XWPFPicture> pictures;
    private CTR run;

    public enum FontCharRange {
        ascii,
        cs,
        eastAsia,
        hAnsi
    }

    public XWPFRun(CTR r, IRunBody p) {
        this.run = r;
        this.parent = p;
        CTDrawing[] arr$ = r.getDrawingArray();
        for (CTDrawing ctDrawing : arr$) {
            CTAnchor[] arr$2 = ctDrawing.getAnchorArray();
            for (CTAnchor anchor : arr$2) {
                if (anchor.getDocPr() != null) {
                    getDocument().getDrawingIdManager().reserve(anchor.getDocPr().getId());
                }
            }
            CTInline[] arr$3 = ctDrawing.getInlineArray();
            for (CTInline inline : arr$3) {
                if (inline.getDocPr() != null) {
                    getDocument().getDrawingIdManager().reserve(inline.getDocPr().getId());
                }
            }
        }
        StringBuilder text = new StringBuilder();
        List<XmlObject> pictTextObjs = new ArrayList<>();
        pictTextObjs.addAll(Arrays.asList(r.getPictArray()));
        pictTextObjs.addAll(Arrays.asList(r.getDrawingArray()));
        for (XmlObject o : pictTextObjs) {
            XmlObject[] ts = o.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' .//w:t");
            for (XmlObject t : ts) {
                NodeList kids = t.getDomNode().getChildNodes();
                for (int n = 0; n < kids.getLength(); n++) {
                    if (kids.item(n) instanceof Text) {
                        if (text.length() > 0) {
                            text.append("\n");
                        }
                        text.append(kids.item(n).getNodeValue());
                    }
                }
            }
        }
        this.pictureText = text.toString();
        this.pictures = new ArrayList();
        for (XmlObject o2 : pictTextObjs) {
            for (CTPicture pict : getCTPictures(o2)) {
                XWPFPicture picture = new XWPFPicture(pict, this);
                this.pictures.add(picture);
            }
        }
    }

    public XWPFRun(CTR r, XWPFParagraph p) {
        this(r, (IRunBody) p);
    }

    static void preserveSpaces(XmlString xs) {
        String text = xs.getStringValue();
        if (text != null) {
            if (text.startsWith(" ") || text.endsWith(" ")) {
                XmlCursor c = xs.newCursor();
                c.toNextToken();
                c.insertAttributeWithValue(new QName("http://www.w3.org/XML/1998/namespace", "space"), "preserve");
                c.dispose();
            }
        }
    }

    private List<CTPicture> getCTPictures(XmlObject o) {
        List<CTPicture> pics = new ArrayList<>();
        CTPicture[] cTPictureArrSelectPath = o.selectPath("declare namespace pic='" + CTPicture.type.getName().getNamespaceURI() + "' .//pic:pic");
        int len$ = cTPictureArrSelectPath.length;
        for (int i$ = 0; i$ < len$; i$++) {
            CTPicture cTPicture = cTPictureArrSelectPath[i$];
            if (cTPicture instanceof XmlAnyTypeImpl) {
                try {
                    cTPicture = CTPicture.Factory.parse(cTPicture.toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                } catch (XmlException e) {
                    throw new POIXMLException((Throwable) e);
                }
            }
            if (cTPicture instanceof CTPicture) {
                pics.add(cTPicture);
            }
        }
        return pics;
    }

    @Internal
    public CTR getCTR() {
        return this.run;
    }

    public IRunBody getParent() {
        return this.parent;
    }

    public XWPFParagraph getParagraph() {
        IRunBody iRunBody = this.parent;
        if (iRunBody instanceof XWPFParagraph) {
            return (XWPFParagraph) iRunBody;
        }
        return null;
    }

    public XWPFDocument getDocument() {
        IRunBody iRunBody = this.parent;
        if (iRunBody != null) {
            return iRunBody.getDocument();
        }
        return null;
    }

    private static boolean isCTOnOff(CTOnOff onoff) {
        STOnOff.Enum val;
        return !onoff.isSetVal() || STOnOff.TRUE == (val = onoff.getVal()) || STOnOff.X_1 == val || STOnOff.ON == val;
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isBold() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetB()) {
            return false;
        }
        return isCTOnOff(pr.getB());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setBold(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff bold = pr.isSetB() ? pr.getB() : pr.addNewB();
        bold.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    public String getColor() {
        if (!this.run.isSetRPr()) {
            return null;
        }
        CTRPr pr = this.run.getRPr();
        if (!pr.isSetColor()) {
            return null;
        }
        CTColor clr = pr.getColor();
        String color = clr.xgetVal().getStringValue();
        return color;
    }

    public void setColor(String rgbStr) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTColor color = pr.isSetColor() ? pr.getColor() : pr.addNewColor();
        color.setVal(rgbStr);
    }

    public String getText(int pos) {
        if (this.run.sizeOfTArray() == 0) {
            return null;
        }
        return this.run.getTArray(pos).getStringValue();
    }

    public String getPictureText() {
        return this.pictureText;
    }

    public void setText(String value) {
        setText(value, this.run.sizeOfTArray());
    }

    public void setText(String value, int pos) {
        if (pos > this.run.sizeOfTArray()) {
            throw new ArrayIndexOutOfBoundsException("Value too large for the parameter position in XWPFRun.setText(String value,int pos)");
        }
        CTText t = (pos >= this.run.sizeOfTArray() || pos < 0) ? this.run.addNewT() : this.run.getTArray(pos);
        t.setStringValue(value);
        preserveSpaces(t);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isItalic() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetI()) {
            return false;
        }
        return isCTOnOff(pr.getI());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setItalic(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff italic = pr.isSetI() ? pr.getI() : pr.addNewI();
        italic.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    public UnderlinePatterns getUnderline() {
        CTRPr pr = this.run.getRPr();
        return (pr == null || !pr.isSetU() || pr.getU().getVal() == null) ? UnderlinePatterns.NONE : UnderlinePatterns.valueOf(pr.getU().getVal().intValue());
    }

    public void setUnderline(UnderlinePatterns value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTUnderline underline = pr.getU() == null ? pr.addNewU() : pr.getU();
        underline.setVal(STUnderline.Enum.forInt(value.getValue()));
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isStrikeThrough() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetStrike()) {
            return false;
        }
        return isCTOnOff(pr.getStrike());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setStrikeThrough(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff strike = pr.isSetStrike() ? pr.getStrike() : pr.addNewStrike();
        strike.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Deprecated
    public boolean isStrike() {
        return isStrikeThrough();
    }

    @Deprecated
    public void setStrike(boolean value) {
        setStrikeThrough(value);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isDoubleStrikeThrough() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetDstrike()) {
            return false;
        }
        return isCTOnOff(pr.getDstrike());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setDoubleStrikethrough(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff dstrike = pr.isSetDstrike() ? pr.getDstrike() : pr.addNewDstrike();
        dstrike.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isSmallCaps() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetSmallCaps()) {
            return false;
        }
        return isCTOnOff(pr.getSmallCaps());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setSmallCaps(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff caps = pr.isSetSmallCaps() ? pr.getSmallCaps() : pr.addNewSmallCaps();
        caps.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isCapitalized() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetCaps()) {
            return false;
        }
        return isCTOnOff(pr.getCaps());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setCapitalized(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff caps = pr.isSetCaps() ? pr.getCaps() : pr.addNewCaps();
        caps.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isShadowed() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetShadow()) {
            return false;
        }
        return isCTOnOff(pr.getShadow());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setShadow(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff shadow = pr.isSetShadow() ? pr.getShadow() : pr.addNewShadow();
        shadow.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isImprinted() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetImprint()) {
            return false;
        }
        return isCTOnOff(pr.getImprint());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setImprinted(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff imprinted = pr.isSetImprint() ? pr.getImprint() : pr.addNewImprint();
        imprinted.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isEmbossed() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetEmboss()) {
            return false;
        }
        return isCTOnOff(pr.getEmboss());
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setEmbossed(boolean value) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTOnOff emboss = pr.isSetEmboss() ? pr.getEmboss() : pr.addNewEmboss();
        emboss.setVal(value ? STOnOff.TRUE : STOnOff.FALSE);
    }

    public VerticalAlign getSubscript() {
        CTRPr pr = this.run.getRPr();
        return (pr == null || !pr.isSetVertAlign()) ? VerticalAlign.BASELINE : VerticalAlign.valueOf(pr.getVertAlign().getVal().intValue());
    }

    public void setSubscript(VerticalAlign valign) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTVerticalAlignRun ctValign = pr.isSetVertAlign() ? pr.getVertAlign() : pr.addNewVertAlign();
        ctValign.setVal(STVerticalAlignRun.Enum.forInt(valign.getValue()));
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public int getKerning() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetKern()) {
            return 0;
        }
        return pr.getKern().getVal().intValue();
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setKerning(int kern) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTHpsMeasure kernmes = pr.isSetKern() ? pr.getKern() : pr.addNewKern();
        kernmes.setVal(BigInteger.valueOf(kern));
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public boolean isHighlighted() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetHighlight() || pr.getHighlight().getVal() == STHighlightColor.NONE) {
            return false;
        }
        return true;
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public int getCharacterSpacing() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetSpacing()) {
            return 0;
        }
        return pr.getSpacing().getVal().intValue();
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setCharacterSpacing(int twips) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTSignedTwipsMeasure spc = pr.isSetSpacing() ? pr.getSpacing() : pr.addNewSpacing();
        spc.setVal(BigInteger.valueOf(twips));
    }

    public String getFontFamily() {
        return getFontFamily(null);
    }

    public void setFontFamily(String fontFamily) {
        setFontFamily(fontFamily, null);
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public String getFontName() {
        return getFontFamily();
    }

    public String getFontFamily(FontCharRange fcr) {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetRFonts()) {
            return null;
        }
        CTFonts fonts = pr.getRFonts();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange[(fcr == null ? FontCharRange.ascii : fcr).ordinal()];
        if (i == 2) {
            return fonts.getCs();
        }
        if (i == 3) {
            return fonts.getEastAsia();
        }
        if (i != 4) {
            return fonts.getAscii();
        }
        return fonts.getHAnsi();
    }

    /* JADX INFO: renamed from: org.apache.poi.xwpf.usermodel.XWPFRun$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange;

        static {
            int[] iArr = new int[FontCharRange.values().length];
            $SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange = iArr;
            try {
                iArr[FontCharRange.ascii.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange[FontCharRange.cs.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange[FontCharRange.eastAsia.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange[FontCharRange.hAnsi.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public void setFontFamily(String fontFamily, FontCharRange fcr) {
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTFonts fonts = pr.isSetRFonts() ? pr.getRFonts() : pr.addNewRFonts();
        if (fcr == null) {
            fonts.setAscii(fontFamily);
            if (!fonts.isSetHAnsi()) {
                fonts.setHAnsi(fontFamily);
            }
            if (!fonts.isSetCs()) {
                fonts.setCs(fontFamily);
            }
            if (!fonts.isSetEastAsia()) {
                fonts.setEastAsia(fontFamily);
                return;
            }
            return;
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xwpf$usermodel$XWPFRun$FontCharRange[fcr.ordinal()];
        if (i == 1) {
            fonts.setAscii(fontFamily);
            return;
        }
        if (i == 2) {
            fonts.setCs(fontFamily);
        } else if (i == 3) {
            fonts.setEastAsia(fontFamily);
        } else if (i == 4) {
            fonts.setHAnsi(fontFamily);
        }
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public int getFontSize() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetSz()) {
            return -1;
        }
        return pr.getSz().getVal().divide(new BigInteger("2")).intValue();
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public void setFontSize(int size) {
        BigInteger bint = new BigInteger("" + size);
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTHpsMeasure ctSize = pr.isSetSz() ? pr.getSz() : pr.addNewSz();
        ctSize.setVal(bint.multiply(new BigInteger("2")));
    }

    public int getTextPosition() {
        CTRPr pr = this.run.getRPr();
        if (pr == null || !pr.isSetPosition()) {
            return -1;
        }
        return pr.getPosition().getVal().intValue();
    }

    public void setTextPosition(int val) {
        BigInteger bint = new BigInteger("" + val);
        CTRPr pr = this.run.isSetRPr() ? this.run.getRPr() : this.run.addNewRPr();
        CTSignedHpsMeasure position = pr.isSetPosition() ? pr.getPosition() : pr.addNewPosition();
        position.setVal(bint);
    }

    public void removeBreak() {
    }

    public void addBreak() {
        this.run.addNewBr();
    }

    public void addBreak(BreakType type) {
        CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(type.getValue()));
    }

    public void addBreak(BreakClear clear) {
        CTBr br = this.run.addNewBr();
        br.setType(STBrType.Enum.forInt(BreakType.TEXT_WRAPPING.getValue()));
        br.setClear(STBrClear.Enum.forInt(clear.getValue()));
    }

    public void addTab() {
        this.run.addNewTab();
    }

    public void removeTab() {
    }

    public void addCarriageReturn() {
        this.run.addNewCr();
    }

    public void removeCarriageReturn() {
    }

    public XWPFPicture addPicture(InputStream pictureData, int pictureType, String filename, int width, int height) throws InvalidFormatException, IOException {
        String relationId;
        XWPFPictureData picData;
        CTShapeProperties spPr;
        CTTransform2D xfrm;
        CTPoint2D off;
        if (this.parent.getPart() instanceof XWPFHeaderFooter) {
            XWPFHeaderFooter headerFooter = (XWPFHeaderFooter) this.parent.getPart();
            relationId = headerFooter.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData) headerFooter.getRelationById(relationId);
        } else {
            XWPFDocument doc = this.parent.getDocument();
            relationId = doc.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData) doc.getRelationById(relationId);
        }
        try {
            CTDrawing drawing = this.run.addNewDrawing();
            CTInline inline = drawing.addNewInline();
            String xml = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\"><a:graphicData uri=\"" + CTPicture.type.getName().getNamespaceURI() + "\"><pic:pic xmlns:pic=\"" + CTPicture.type.getName().getNamespaceURI() + "\" /></a:graphicData></a:graphic>";
            InputSource is = new InputSource(new StringReader(xml));
            inline.set(XmlToken.Factory.parse(DocumentHelper.readDocument(is).getDocumentElement(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
            inline.setDistT(0L);
            inline.setDistR(0L);
            inline.setDistB(0L);
            inline.setDistL(0L);
            CTNonVisualDrawingProps docPr = inline.addNewDocPr();
            long id = getParent().getDocument().getDrawingIdManager().reserveNew();
            docPr.setId(id);
            docPr.setName("Drawing " + id);
            docPr.setDescr(filename);
            CTPositiveSize2D extent = inline.addNewExtent();
            extent.setCx(width);
            extent.setCy(height);
            CTGraphicalObject graphic = inline.getGraphic();
            CTGraphicalObjectData graphicData = graphic.getGraphicData();
            CTPicture pic = getCTPictures(graphicData).get(0);
            CTPictureNonVisual nvPicPr = pic.addNewNvPicPr();
            CTNonVisualDrawingProps cNvPr = nvPicPr.addNewCNvPr();
            try {
                cNvPr.setId(0L);
                cNvPr.setName("Picture " + id);
                cNvPr.setDescr(filename);
                CTNonVisualPictureProperties cNvPicPr = nvPicPr.addNewCNvPicPr();
                cNvPicPr.addNewPicLocks().setNoChangeAspect(true);
                CTBlipFillProperties blipFill = pic.addNewBlipFill();
                CTBlip blip = blipFill.addNewBlip();
                blip.setEmbed(this.parent.getPart().getRelationId(picData));
                blipFill.addNewStretch().addNewFillRect();
                spPr = pic.addNewSpPr();
                xfrm = spPr.addNewXfrm();
                off = xfrm.addNewOff();
            } catch (SAXException e) {
                e = e;
            } catch (XmlException e2) {
                e = e2;
            }
            try {
                off.setX(0L);
                off.setY(0L);
                CTPositiveSize2D ext = xfrm.addNewExt();
                ext.setCx(width);
                ext.setCy(height);
                CTPresetGeometry2D prstGeom = spPr.addNewPrstGeom();
                prstGeom.setPrst(STShapeType.RECT);
                prstGeom.addNewAvLst();
                XWPFPicture xwpfPicture = new XWPFPicture(pic, this);
                this.pictures.add(xwpfPicture);
                return xwpfPicture;
            } catch (SAXException e3) {
                e = e3;
                throw new IllegalStateException(e);
            } catch (XmlException e4) {
                e = e4;
                throw new IllegalStateException((Throwable) e);
            }
        } catch (SAXException e5) {
            e = e5;
        } catch (XmlException e6) {
            e = e6;
        }
    }

    public List<XWPFPicture> getEmbeddedPictures() {
        return this.pictures;
    }

    public String toString() {
        String phonetic = getPhonetic();
        if (phonetic.length() > 0) {
            return text() + " (" + phonetic.toString() + ")";
        }
        return text();
    }

    @Override // org.apache.poi.wp.usermodel.CharacterRun
    public String text() {
        StringBuffer text = new StringBuffer();
        XmlCursor c = this.run.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTRuby) {
                handleRuby(o, text, false);
            } else {
                _getText(o, text);
            }
        }
        c.dispose();
        return text.toString();
    }

    public String getPhonetic() {
        StringBuffer text = new StringBuffer();
        XmlCursor c = this.run.newCursor();
        c.selectPath("./*");
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTRuby) {
                handleRuby(o, text, true);
            }
        }
        String str = this.pictureText;
        if (str != null && str.length() > 0) {
            text.append("\n").append(this.pictureText).append("\n");
        }
        c.dispose();
        return text.toString();
    }

    private void handleRuby(XmlObject rubyObj, StringBuffer text, boolean extractPhonetic) {
        XmlCursor c = rubyObj.newCursor();
        c.selectPath(".//*");
        boolean inRT = false;
        boolean inBase = false;
        while (c.toNextSelection()) {
            XmlObject o = c.getObject();
            if (o instanceof CTRubyContent) {
                String tagName = o.getDomNode().getNodeName();
                if ("w:rt".equals(tagName)) {
                    inRT = true;
                } else if ("w:rubyBase".equals(tagName)) {
                    inRT = false;
                    inBase = true;
                }
            } else if (extractPhonetic && inRT) {
                _getText(o, text);
            } else if (!extractPhonetic && inBase) {
                _getText(o, text);
            }
        }
        c.dispose();
    }

    private void _getText(XmlObject o, StringBuffer text) {
        StringBuilder sb;
        String str;
        if ((o instanceof CTText) && !"w:instrText".equals(o.getDomNode().getNodeName())) {
            text.append(((CTText) o).getStringValue());
        }
        if (o instanceof CTFldChar) {
            CTFldChar ctfldChar = (CTFldChar) o;
            if (ctfldChar.getFldCharType() == STFldCharType.BEGIN && ctfldChar.getFfData() != null) {
                for (CTFFCheckBox checkBox : ctfldChar.getFfData().getCheckBoxList()) {
                    if (checkBox.getDefault() != null && checkBox.getDefault().getVal() == STOnOff.X_1) {
                        text.append("|X|");
                    } else {
                        text.append("|_|");
                    }
                }
            }
        }
        if (o instanceof CTPTab) {
            text.append("\t");
        }
        if (o instanceof CTBr) {
            text.append("\n");
        }
        if (o instanceof CTEmpty) {
            String tagName = o.getDomNode().getNodeName();
            if ("w:tab".equals(tagName) || "tab".equals(tagName)) {
                text.append("\t");
            }
            if ("w:br".equals(tagName) || "br".equals(tagName)) {
                text.append("\n");
            }
            if ("w:cr".equals(tagName) || "cr".equals(tagName)) {
                text.append("\n");
            }
        }
        if (o instanceof CTFtnEdnRef) {
            CTFtnEdnRef ftn = (CTFtnEdnRef) o;
            if (ftn.getDomNode().getLocalName().equals("footnoteReference")) {
                sb = new StringBuilder();
                str = "[footnoteRef:";
            } else {
                sb = new StringBuilder();
                str = "[endnoteRef:";
            }
            String footnoteRef = sb.append(str).append(ftn.getId().intValue()).append("]").toString();
            text.append(footnoteRef);
        }
    }
}
