package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FontFormatting;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFFontFormatting implements FontFormatting {
    public static final byte U_DOUBLE = 2;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    private final org.apache.poi.hssf.record.cf.FontFormatting fontFormatting;
    private final HSSFWorkbook workbook;

    protected HSSFFontFormatting(CFRuleBase cfRuleRecord, HSSFWorkbook workbook) {
        this.fontFormatting = cfRuleRecord.getFontFormatting();
        this.workbook = workbook;
    }

    protected org.apache.poi.hssf.record.cf.FontFormatting getFontFormattingBlock() {
        return this.fontFormatting;
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public short getEscapementType() {
        return this.fontFormatting.getEscapementType();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public short getFontColorIndex() {
        return this.fontFormatting.getFontColorIndex();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public HSSFColor getFontColor() {
        return this.workbook.getCustomPalette().getColor(getFontColorIndex());
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setFontColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            this.fontFormatting.setFontColorIndex((short) 0);
        } else {
            this.fontFormatting.setFontColorIndex(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public int getFontHeight() {
        return this.fontFormatting.getFontHeight();
    }

    public short getFontWeight() {
        return this.fontFormatting.getFontWeight();
    }

    protected byte[] getRawRecord() {
        return this.fontFormatting.getRawRecord();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public short getUnderlineType() {
        return this.fontFormatting.getUnderlineType();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public boolean isBold() {
        return this.fontFormatting.isFontWeightModified() && this.fontFormatting.isBold();
    }

    public boolean isEscapementTypeModified() {
        return this.fontFormatting.isEscapementTypeModified();
    }

    public boolean isFontCancellationModified() {
        return this.fontFormatting.isFontCancellationModified();
    }

    public boolean isFontOutlineModified() {
        return this.fontFormatting.isFontOutlineModified();
    }

    public boolean isFontShadowModified() {
        return this.fontFormatting.isFontShadowModified();
    }

    public boolean isFontStyleModified() {
        return this.fontFormatting.isFontStyleModified();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public boolean isItalic() {
        return this.fontFormatting.isFontStyleModified() && this.fontFormatting.isItalic();
    }

    public boolean isOutlineOn() {
        return this.fontFormatting.isFontOutlineModified() && this.fontFormatting.isOutlineOn();
    }

    public boolean isShadowOn() {
        return this.fontFormatting.isFontOutlineModified() && this.fontFormatting.isShadowOn();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public boolean isStruckout() {
        return this.fontFormatting.isFontCancellationModified() && this.fontFormatting.isStruckout();
    }

    public boolean isUnderlineTypeModified() {
        return this.fontFormatting.isUnderlineTypeModified();
    }

    public boolean isFontWeightModified() {
        return this.fontFormatting.isFontWeightModified();
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setFontStyle(boolean italic, boolean bold) {
        boolean modified = italic || bold;
        this.fontFormatting.setItalic(italic);
        this.fontFormatting.setBold(bold);
        this.fontFormatting.setFontStyleModified(modified);
        this.fontFormatting.setFontWieghtModified(modified);
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void resetFontStyle() {
        setFontStyle(false, false);
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setEscapementType(short escapementType) {
        if (escapementType == 0) {
            this.fontFormatting.setEscapementType(escapementType);
            this.fontFormatting.setEscapementTypeModified(false);
        } else if (escapementType == 1 || escapementType == 2) {
            this.fontFormatting.setEscapementType(escapementType);
            this.fontFormatting.setEscapementTypeModified(true);
        }
    }

    public void setEscapementTypeModified(boolean modified) {
        this.fontFormatting.setEscapementTypeModified(modified);
    }

    public void setFontCancellationModified(boolean modified) {
        this.fontFormatting.setFontCancellationModified(modified);
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setFontColorIndex(short fci) {
        this.fontFormatting.setFontColorIndex(fci);
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setFontHeight(int height) {
        this.fontFormatting.setFontHeight(height);
    }

    public void setFontOutlineModified(boolean modified) {
        this.fontFormatting.setFontOutlineModified(modified);
    }

    public void setFontShadowModified(boolean modified) {
        this.fontFormatting.setFontShadowModified(modified);
    }

    public void setFontStyleModified(boolean modified) {
        this.fontFormatting.setFontStyleModified(modified);
    }

    public void setOutline(boolean on) {
        this.fontFormatting.setOutline(on);
        this.fontFormatting.setFontOutlineModified(on);
    }

    public void setShadow(boolean on) {
        this.fontFormatting.setShadow(on);
        this.fontFormatting.setFontShadowModified(on);
    }

    public void setStrikeout(boolean strike) {
        this.fontFormatting.setStrikeout(strike);
        this.fontFormatting.setFontCancellationModified(strike);
    }

    @Override // org.apache.poi.ss.usermodel.FontFormatting
    public void setUnderlineType(short underlineType) {
        if (underlineType == 0) {
            this.fontFormatting.setUnderlineType(underlineType);
            setUnderlineTypeModified(false);
        } else if (underlineType == 1 || underlineType == 2 || underlineType == 33 || underlineType == 34) {
            this.fontFormatting.setUnderlineType(underlineType);
            setUnderlineTypeModified(true);
        }
    }

    public void setUnderlineTypeModified(boolean modified) {
        this.fontFormatting.setUnderlineTypeModified(modified);
    }
}
