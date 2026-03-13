package org.apache.poi.xssf.usermodel;

import java.awt.Color;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextStrikeType;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextUnderlineType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFTextRun {
    private final XSSFTextParagraph _p;
    private final CTRegularTextRun _r;

    XSSFTextRun(CTRegularTextRun r, XSSFTextParagraph p) {
        this._r = r;
        this._p = p;
    }

    XSSFTextParagraph getParentParagraph() {
        return this._p;
    }

    public String getText() {
        return this._r.getT();
    }

    public void setText(String text) {
        this._r.setT(text);
    }

    public CTRegularTextRun getXmlObject() {
        return this._r;
    }

    public void setFontColor(Color color) {
        CTTextCharacterProperties rPr = getRPr();
        CTSolidColorFillProperties fill = rPr.isSetSolidFill() ? rPr.getSolidFill() : rPr.addNewSolidFill();
        CTSRgbColor clr = fill.isSetSrgbClr() ? fill.getSrgbClr() : fill.addNewSrgbClr();
        clr.setVal(new byte[]{(byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue()});
        if (fill.isSetHslClr()) {
            fill.unsetHslClr();
        }
        if (fill.isSetPrstClr()) {
            fill.unsetPrstClr();
        }
        if (fill.isSetSchemeClr()) {
            fill.unsetSchemeClr();
        }
        if (fill.isSetScrgbClr()) {
            fill.unsetScrgbClr();
        }
        if (fill.isSetSysClr()) {
            fill.unsetSysClr();
        }
    }

    public Color getFontColor() {
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetSolidFill()) {
            CTSolidColorFillProperties fill = rPr.getSolidFill();
            if (fill.isSetSrgbClr()) {
                CTSRgbColor clr = fill.getSrgbClr();
                byte[] rgb = clr.getVal();
                return new Color(rgb[0] & 255, rgb[1] & 255, rgb[2] & 255);
            }
        }
        return new Color(0, 0, 0);
    }

    public void setFontSize(double fontSize) {
        CTTextCharacterProperties rPr = getRPr();
        if (fontSize == -1.0d) {
            if (rPr.isSetSz()) {
                rPr.unsetSz();
            }
        } else {
            if (fontSize < 1.0d) {
                throw new IllegalArgumentException("Minimum font size is 1pt but was " + fontSize);
            }
            rPr.setSz((int) (100.0d * fontSize));
        }
    }

    public double getFontSize() {
        double size = 11.0d;
        CTTextNormalAutofit afit = getParentParagraph().getParentShape().getTxBody().getBodyPr().getNormAutofit();
        double scale = afit != null ? ((double) afit.getFontScale()) / 100000.0d : 1.0d;
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetSz()) {
            size = ((double) rPr.getSz()) * 0.01d;
        }
        return size * scale;
    }

    public double getCharacterSpacing() {
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetSpc()) {
            return ((double) rPr.getSpc()) * 0.01d;
        }
        return 0.0d;
    }

    public void setCharacterSpacing(double spc) {
        CTTextCharacterProperties rPr = getRPr();
        if (spc == 0.0d) {
            if (rPr.isSetSpc()) {
                rPr.unsetSpc();
                return;
            }
            return;
        }
        rPr.setSpc((int) (100.0d * spc));
    }

    public void setFont(String typeface) {
        setFontFamily(typeface, (byte) -1, (byte) -1, false);
    }

    public void setFontFamily(String typeface, byte charset, byte pictAndFamily, boolean isSymbol) {
        CTTextCharacterProperties rPr = getRPr();
        if (typeface == null) {
            if (rPr.isSetLatin()) {
                rPr.unsetLatin();
            }
            if (rPr.isSetCs()) {
                rPr.unsetCs();
            }
            if (rPr.isSetSym()) {
                rPr.unsetSym();
                return;
            }
            return;
        }
        if (isSymbol) {
            CTTextFont font = rPr.isSetSym() ? rPr.getSym() : rPr.addNewSym();
            font.setTypeface(typeface);
            return;
        }
        CTTextFont latin = rPr.isSetLatin() ? rPr.getLatin() : rPr.addNewLatin();
        latin.setTypeface(typeface);
        if (charset != -1) {
            latin.setCharset(charset);
        }
        if (pictAndFamily != -1) {
            latin.setPitchFamily(pictAndFamily);
        }
    }

    public String getFontFamily() {
        CTTextCharacterProperties rPr = getRPr();
        CTTextFont font = rPr.getLatin();
        if (font != null) {
            return font.getTypeface();
        }
        return XSSFFont.DEFAULT_FONT_NAME;
    }

    public byte getPitchAndFamily() {
        CTTextCharacterProperties rPr = getRPr();
        CTTextFont font = rPr.getLatin();
        if (font != null) {
            return font.getPitchFamily();
        }
        return (byte) 0;
    }

    public void setStrikethrough(boolean strike) {
        getRPr().setStrike(strike ? STTextStrikeType.SNG_STRIKE : STTextStrikeType.NO_STRIKE);
    }

    public boolean isStrikethrough() {
        CTTextCharacterProperties rPr = getRPr();
        return rPr.isSetStrike() && rPr.getStrike() != STTextStrikeType.NO_STRIKE;
    }

    public boolean isSuperscript() {
        CTTextCharacterProperties rPr = getRPr();
        return rPr.isSetBaseline() && rPr.getBaseline() > 0;
    }

    public void setBaselineOffset(double baselineOffset) {
        getRPr().setBaseline(((int) baselineOffset) * 1000);
    }

    public void setSuperscript(boolean flag) {
        setBaselineOffset(flag ? 30.0d : 0.0d);
    }

    public void setSubscript(boolean flag) {
        setBaselineOffset(flag ? -25.0d : 0.0d);
    }

    public boolean isSubscript() {
        CTTextCharacterProperties rPr = getRPr();
        return rPr.isSetBaseline() && rPr.getBaseline() < 0;
    }

    public TextCap getTextCap() {
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetCap()) {
            return TextCap.values()[rPr.getCap().intValue() - 1];
        }
        return TextCap.NONE;
    }

    public void setBold(boolean bold) {
        getRPr().setB(bold);
    }

    public boolean isBold() {
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetB()) {
            return rPr.getB();
        }
        return false;
    }

    public void setItalic(boolean italic) {
        getRPr().setI(italic);
    }

    public boolean isItalic() {
        CTTextCharacterProperties rPr = getRPr();
        if (rPr.isSetI()) {
            return rPr.getI();
        }
        return false;
    }

    public void setUnderline(boolean underline) {
        getRPr().setU(underline ? STTextUnderlineType.SNG : STTextUnderlineType.NONE);
    }

    public boolean isUnderline() {
        CTTextCharacterProperties rPr = getRPr();
        return rPr.isSetU() && rPr.getU() != STTextUnderlineType.NONE;
    }

    protected CTTextCharacterProperties getRPr() {
        return this._r.isSetRPr() ? this._r.getRPr() : this._r.addNewRPr();
    }

    public String toString() {
        return "[" + getClass() + "]" + getText();
    }
}
