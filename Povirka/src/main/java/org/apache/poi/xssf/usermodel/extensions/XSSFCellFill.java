package org.apache.poi.xssf.usermodel.extensions;

import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFCellFill {
    private CTFill _fill;
    private IndexedColorMap _indexedColorMap;

    public XSSFCellFill(CTFill fill, IndexedColorMap colorMap) {
        this._fill = fill;
        this._indexedColorMap = colorMap;
    }

    public XSSFCellFill() {
        this._fill = CTFill.Factory.newInstance();
    }

    public XSSFColor getFillBackgroundColor() {
        CTColor ctColor;
        CTPatternFill ptrn = this._fill.getPatternFill();
        if (ptrn == null || (ctColor = ptrn.getBgColor()) == null) {
            return null;
        }
        return new XSSFColor(ctColor, this._indexedColorMap);
    }

    public void setFillBackgroundColor(int index) {
        CTPatternFill ptrn = ensureCTPatternFill();
        CTColor ctColor = ptrn.isSetBgColor() ? ptrn.getBgColor() : ptrn.addNewBgColor();
        ctColor.setIndexed(index);
    }

    public void setFillBackgroundColor(XSSFColor color) {
        CTPatternFill ptrn = ensureCTPatternFill();
        ptrn.setBgColor(color.getCTColor());
    }

    public XSSFColor getFillForegroundColor() {
        CTColor ctColor;
        CTPatternFill ptrn = this._fill.getPatternFill();
        if (ptrn == null || (ctColor = ptrn.getFgColor()) == null) {
            return null;
        }
        return new XSSFColor(ctColor, this._indexedColorMap);
    }

    public void setFillForegroundColor(int index) {
        CTPatternFill ptrn = ensureCTPatternFill();
        CTColor ctColor = ptrn.isSetFgColor() ? ptrn.getFgColor() : ptrn.addNewFgColor();
        ctColor.setIndexed(index);
    }

    public void setFillForegroundColor(XSSFColor color) {
        CTPatternFill ptrn = ensureCTPatternFill();
        ptrn.setFgColor(color.getCTColor());
    }

    public STPatternType.Enum getPatternType() {
        CTPatternFill ptrn = this._fill.getPatternFill();
        if (ptrn == null) {
            return null;
        }
        return ptrn.getPatternType();
    }

    public void setPatternType(STPatternType.Enum patternType) {
        CTPatternFill ptrn = ensureCTPatternFill();
        ptrn.setPatternType(patternType);
    }

    private CTPatternFill ensureCTPatternFill() {
        CTPatternFill patternFill = this._fill.getPatternFill();
        if (patternFill == null) {
            return this._fill.addNewPatternFill();
        }
        return patternFill;
    }

    @Internal
    public CTFill getCTFill() {
        return this._fill;
    }

    public int hashCode() {
        return this._fill.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof XSSFCellFill)) {
            return false;
        }
        XSSFCellFill cf = (XSSFCellFill) o;
        return this._fill.toString().equals(cf.getCTFill().toString());
    }
}
