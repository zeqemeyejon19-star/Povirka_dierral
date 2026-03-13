package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFPatternFormatting implements PatternFormatting {
    IndexedColorMap _colorMap;
    CTFill _fill;

    XSSFPatternFormatting(CTFill fill, IndexedColorMap colorMap) {
        this._fill = fill;
        this._colorMap = colorMap;
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public XSSFColor getFillBackgroundColorColor() {
        if (this._fill.isSetPatternFill()) {
            return new XSSFColor(this._fill.getPatternFill().getBgColor(), this._colorMap);
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public XSSFColor getFillForegroundColorColor() {
        if (!this._fill.isSetPatternFill() || !this._fill.getPatternFill().isSetFgColor()) {
            return null;
        }
        return new XSSFColor(this._fill.getPatternFill().getFgColor(), this._colorMap);
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public short getFillPattern() {
        if (!this._fill.isSetPatternFill() || !this._fill.getPatternFill().isSetPatternType()) {
            return (short) 0;
        }
        return (short) (this._fill.getPatternFill().getPatternType().intValue() - 1);
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public short getFillBackgroundColor() {
        XSSFColor color = getFillBackgroundColorColor();
        if (color == null) {
            return (short) 0;
        }
        return color.getIndexed();
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public short getFillForegroundColor() {
        XSSFColor color = getFillForegroundColorColor();
        if (color == null) {
            return (short) 0;
        }
        return color.getIndexed();
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public void setFillBackgroundColor(Color bg) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(bg);
        if (xcolor != null) {
            setFillBackgroundColor(xcolor.getCTColor());
        } else {
            setFillBackgroundColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public void setFillBackgroundColor(short bg) {
        CTColor bgColor = CTColor.Factory.newInstance();
        bgColor.setIndexed(bg);
        setFillBackgroundColor(bgColor);
    }

    private void setFillBackgroundColor(CTColor color) {
        CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (color == null) {
            ptrn.unsetBgColor();
        } else {
            ptrn.setBgColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public void setFillForegroundColor(Color fg) {
        XSSFColor xcolor = XSSFColor.toXSSFColor(fg);
        if (xcolor != null) {
            setFillForegroundColor(xcolor.getCTColor());
        } else {
            setFillForegroundColor((CTColor) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public void setFillForegroundColor(short fg) {
        CTColor fgColor = CTColor.Factory.newInstance();
        fgColor.setIndexed(fg);
        setFillForegroundColor(fgColor);
    }

    private void setFillForegroundColor(CTColor color) {
        CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (color == null) {
            ptrn.unsetFgColor();
        } else {
            ptrn.setFgColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.PatternFormatting
    public void setFillPattern(short fp) {
        CTPatternFill ptrn = this._fill.isSetPatternFill() ? this._fill.getPatternFill() : this._fill.addNewPatternFill();
        if (fp != 0) {
            ptrn.setPatternType(STPatternType.Enum.forInt(fp + 1));
        } else {
            ptrn.unsetPatternType();
        }
    }
}
