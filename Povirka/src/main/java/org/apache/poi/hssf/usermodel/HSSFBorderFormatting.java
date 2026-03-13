package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Color;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFBorderFormatting implements BorderFormatting {
    private final org.apache.poi.hssf.record.cf.BorderFormatting borderFormatting;
    private final CFRuleBase cfRuleRecord;
    private final HSSFWorkbook workbook;

    protected HSSFBorderFormatting(CFRuleBase cfRuleRecord, HSSFWorkbook workbook) {
        this.workbook = workbook;
        this.cfRuleRecord = cfRuleRecord;
        this.borderFormatting = cfRuleRecord.getBorderFormatting();
    }

    protected org.apache.poi.hssf.record.cf.BorderFormatting getBorderFormattingBlock() {
        return this.borderFormatting;
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderBottom() {
        return (short) this.borderFormatting.getBorderBottom();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderBottomEnum() {
        return BorderStyle.valueOf((short) this.borderFormatting.getBorderBottom());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderDiagonal() {
        return (short) this.borderFormatting.getBorderDiagonal();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderDiagonalEnum() {
        return BorderStyle.valueOf((short) this.borderFormatting.getBorderDiagonal());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderLeft() {
        return (short) this.borderFormatting.getBorderLeft();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderLeftEnum() {
        return BorderStyle.valueOf((short) this.borderFormatting.getBorderLeft());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderRight() {
        return (short) this.borderFormatting.getBorderRight();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderRightEnum() {
        return BorderStyle.valueOf((short) this.borderFormatting.getBorderRight());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderTop() {
        return (short) this.borderFormatting.getBorderTop();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderTopEnum() {
        return BorderStyle.valueOf((short) this.borderFormatting.getBorderTop());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBottomBorderColor() {
        return (short) this.borderFormatting.getBottomBorderColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public HSSFColor getBottomBorderColorColor() {
        return this.workbook.getCustomPalette().getColor(this.borderFormatting.getBottomBorderColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getDiagonalBorderColor() {
        return (short) this.borderFormatting.getDiagonalBorderColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public HSSFColor getDiagonalBorderColorColor() {
        return this.workbook.getCustomPalette().getColor(this.borderFormatting.getDiagonalBorderColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getLeftBorderColor() {
        return (short) this.borderFormatting.getLeftBorderColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public HSSFColor getLeftBorderColorColor() {
        return this.workbook.getCustomPalette().getColor(this.borderFormatting.getLeftBorderColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getRightBorderColor() {
        return (short) this.borderFormatting.getRightBorderColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public HSSFColor getRightBorderColorColor() {
        return this.workbook.getCustomPalette().getColor(this.borderFormatting.getRightBorderColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getTopBorderColor() {
        return (short) this.borderFormatting.getTopBorderColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public HSSFColor getTopBorderColorColor() {
        return this.workbook.getCustomPalette().getColor(this.borderFormatting.getTopBorderColor());
    }

    public boolean isBackwardDiagonalOn() {
        return this.borderFormatting.isBackwardDiagonalOn();
    }

    public boolean isForwardDiagonalOn() {
        return this.borderFormatting.isForwardDiagonalOn();
    }

    public void setBackwardDiagonalOn(boolean on) {
        this.borderFormatting.setBackwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(on);
        }
    }

    public void setForwardDiagonalOn(boolean on) {
        this.borderFormatting.setForwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(on);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderBottom(short border) {
        this.borderFormatting.setBorderBottom(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        } else {
            this.cfRuleRecord.setBottomBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderBottom(BorderStyle border) {
        setBorderBottom(border.getCode());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderDiagonal(short border) {
        this.borderFormatting.setBorderDiagonal(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        } else {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(false);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderDiagonal(BorderStyle border) {
        setBorderDiagonal(border.getCode());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderLeft(short border) {
        this.borderFormatting.setBorderLeft(border);
        if (border != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        } else {
            this.cfRuleRecord.setLeftBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderLeft(BorderStyle border) {
        setBorderLeft(border.getCode());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderRight(short border) {
        this.borderFormatting.setBorderRight(border);
        if (border != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        } else {
            this.cfRuleRecord.setRightBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderRight(BorderStyle border) {
        setBorderRight(border.getCode());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderTop(short border) {
        this.borderFormatting.setBorderTop(border);
        if (border != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        } else {
            this.cfRuleRecord.setTopBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderTop(BorderStyle border) {
        setBorderTop(border.getCode());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBottomBorderColor(short color) {
        this.borderFormatting.setBottomBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        } else {
            this.cfRuleRecord.setBottomBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBottomBorderColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            setBottomBorderColor((short) 0);
        } else {
            setBottomBorderColor(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setDiagonalBorderColor(short color) {
        this.borderFormatting.setDiagonalBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        } else {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(false);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setDiagonalBorderColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            setDiagonalBorderColor((short) 0);
        } else {
            setDiagonalBorderColor(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setLeftBorderColor(short color) {
        this.borderFormatting.setLeftBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        } else {
            this.cfRuleRecord.setLeftBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setLeftBorderColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            setLeftBorderColor((short) 0);
        } else {
            setLeftBorderColor(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setRightBorderColor(short color) {
        this.borderFormatting.setRightBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        } else {
            this.cfRuleRecord.setRightBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setRightBorderColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            setRightBorderColor((short) 0);
        } else {
            setRightBorderColor(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setTopBorderColor(short color) {
        this.borderFormatting.setTopBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        } else {
            this.cfRuleRecord.setTopBorderModified(false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setTopBorderColor(Color color) {
        HSSFColor hcolor = HSSFColor.toHSSFColor(color);
        if (hcolor == null) {
            setTopBorderColor((short) 0);
        } else {
            setTopBorderColor(hcolor.getIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderVerticalEnum() {
        return BorderStyle.NONE;
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderHorizontalEnum() {
        return BorderStyle.NONE;
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getVerticalBorderColor() {
        return HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public Color getVerticalBorderColorColor() {
        return HSSFColor.HSSFColorPredefined.AUTOMATIC.getColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getHorizontalBorderColor() {
        return HSSFColor.HSSFColorPredefined.AUTOMATIC.getIndex();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public Color getHorizontalBorderColorColor() {
        return HSSFColor.HSSFColorPredefined.AUTOMATIC.getColor();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderHorizontal(BorderStyle border) {
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderVertical(BorderStyle border) {
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setHorizontalBorderColor(short color) {
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setHorizontalBorderColor(Color color) {
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setVerticalBorderColor(short color) {
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setVerticalBorderColor(Color color) {
    }
}
