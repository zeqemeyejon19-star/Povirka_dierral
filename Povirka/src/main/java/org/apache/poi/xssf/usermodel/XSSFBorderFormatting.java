package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Color;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

/* JADX INFO: loaded from: classes.dex */
public class XSSFBorderFormatting implements BorderFormatting {
    CTBorder _border;
    IndexedColorMap _colorMap;

    XSSFBorderFormatting(CTBorder border, IndexedColorMap colorMap) {
        this._border = border;
        this._colorMap = colorMap;
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderBottom() {
        return getBorderBottomEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderBottomEnum() {
        return getBorderStyle(this._border.getBottom());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderDiagonal() {
        return getBorderDiagonalEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderDiagonalEnum() {
        return getBorderStyle(this._border.getDiagonal());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderLeft() {
        return getBorderLeftEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderLeftEnum() {
        return getBorderStyle(this._border.getLeft());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderRight() {
        return getBorderRightEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderRightEnum() {
        return getBorderStyle(this._border.getRight());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBorderTop() {
        return getBorderTopEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderTopEnum() {
        return getBorderStyle(this._border.getTop());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getBottomBorderColorColor() {
        return getColor(this._border.getBottom());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getBottomBorderColor() {
        return getIndexedColor(getBottomBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getDiagonalBorderColorColor() {
        return getColor(this._border.getDiagonal());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getDiagonalBorderColor() {
        return getIndexedColor(getDiagonalBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getLeftBorderColorColor() {
        return getColor(this._border.getLeft());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getLeftBorderColor() {
        return getIndexedColor(getLeftBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getRightBorderColorColor() {
        return getColor(this._border.getRight());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getRightBorderColor() {
        return getIndexedColor(getRightBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getTopBorderColorColor() {
        return getColor(this._border.getTop());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getTopBorderColor() {
        return getIndexedColor(getRightBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderBottom(short border) {
        setBorderBottom(BorderStyle.valueOf(border));
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderBottom(BorderStyle border) {
        CTBorderPr pr = this._border.isSetBottom() ? this._border.getBottom() : this._border.addNewBottom();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetBottom();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderDiagonal(short border) {
        setBorderDiagonal(BorderStyle.valueOf(border));
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderDiagonal(BorderStyle border) {
        CTBorderPr pr = this._border.isSetDiagonal() ? this._border.getDiagonal() : this._border.addNewDiagonal();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetDiagonal();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderLeft(short border) {
        setBorderLeft(BorderStyle.valueOf(border));
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderLeft(BorderStyle border) {
        CTBorderPr pr = this._border.isSetLeft() ? this._border.getLeft() : this._border.addNewLeft();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetLeft();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderRight(short border) {
        setBorderRight(BorderStyle.valueOf(border));
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderRight(BorderStyle border) {
        CTBorderPr pr = this._border.isSetRight() ? this._border.getRight() : this._border.addNewRight();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetRight();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderTop(short border) {
        setBorderTop(BorderStyle.valueOf(border));
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderTop(BorderStyle border) {
        CTBorderPr pr = this._border.isSetTop() ? this._border.getTop() : this._border.addNewTop();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetTop();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBottomBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setBottomBorderColor(xcolor.getCTColor());
        } else {
            setBottomBorderColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBottomBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setBottomBorderColor(ctColor);
    }

    public void setBottomBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetBottom() ? this._border.getBottom() : this._border.addNewBottom();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setDiagonalBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setDiagonalBorderColor(xcolor.getCTColor());
        } else {
            setDiagonalBorderColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setDiagonalBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setDiagonalBorderColor(ctColor);
    }

    public void setDiagonalBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetDiagonal() ? this._border.getDiagonal() : this._border.addNewDiagonal();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setLeftBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setLeftBorderColor(xcolor.getCTColor());
        } else {
            setLeftBorderColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setLeftBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setLeftBorderColor(ctColor);
    }

    public void setLeftBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetLeft() ? this._border.getLeft() : this._border.addNewLeft();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setRightBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setRightBorderColor(xcolor.getCTColor());
        } else {
            setRightBorderColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setRightBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setRightBorderColor(ctColor);
    }

    public void setRightBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetRight() ? this._border.getRight() : this._border.addNewRight();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setTopBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setTopBorderColor(xcolor.getCTColor());
        } else {
            setTopBorderColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setTopBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setTopBorderColor(ctColor);
    }

    public void setTopBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetTop() ? this._border.getTop() : this._border.addNewTop();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderVerticalEnum() {
        return getBorderStyle(this._border.getVertical());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public BorderStyle getBorderHorizontalEnum() {
        return getBorderStyle(this._border.getHorizontal());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getVerticalBorderColor() {
        return getIndexedColor(getVerticalBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getVerticalBorderColorColor() {
        return getColor(this._border.getVertical());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public short getHorizontalBorderColor() {
        return getIndexedColor(getHorizontalBorderColorColor());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public XSSFColor getHorizontalBorderColorColor() {
        return getColor(this._border.getHorizontal());
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderHorizontal(BorderStyle border) {
        CTBorderPr pr = this._border.isSetHorizontal() ? this._border.getHorizontal() : this._border.addNewHorizontal();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetHorizontal();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setBorderVertical(BorderStyle border) {
        CTBorderPr pr = this._border.isSetVertical() ? this._border.getVertical() : this._border.addNewVertical();
        if (border != BorderStyle.NONE) {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        } else {
            this._border.unsetVertical();
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setHorizontalBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setHorizontalBorderColor(ctColor);
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setHorizontalBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setHorizontalBorderColor(xcolor.getCTColor());
        } else {
            setBottomBorderColor((CTColor) null);
        }
    }

    public void setHorizontalBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetHorizontal() ? this._border.getHorizontal() : this._border.addNewHorizontal();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setVerticalBorderColor(short color) {
        CTColor ctColor = CTColor.Factory.newInstance();
        ctColor.setIndexed(color);
        setVerticalBorderColor(ctColor);
    }

    @Override // org.apache.poi.ss.usermodel.BorderFormatting
    public void setVerticalBorderColor(Color color) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(color);
        if (xcolor != null) {
            setVerticalBorderColor(xcolor.getCTColor());
        } else {
            setBottomBorderColor((CTColor) null);
        }
    }

    public void setVerticalBorderColor(CTColor color) {
        CTBorderPr pr = this._border.isSetVertical() ? this._border.getVertical() : this._border.addNewVertical();
        if (color == null) {
            pr.unsetColor();
        } else {
            pr.setColor(color);
        }
    }

    private BorderStyle getBorderStyle(CTBorderPr borderPr) {
        STBorderStyle.Enum ptrn;
        if (borderPr != null && (ptrn = borderPr.getStyle()) != null) {
            return BorderStyle.valueOf((short) (ptrn.intValue() - 1));
        }
        return BorderStyle.NONE;
    }

    private short getIndexedColor(XSSFColor color) {
        if (color == null) {
            return (short) 0;
        }
        return color.getIndexed();
    }

    private XSSFColor getColor(CTBorderPr pr) {
        if (pr == null) {
            return null;
        }
        return new XSSFColor(pr.getColor(), this._colorMap);
    }
}
