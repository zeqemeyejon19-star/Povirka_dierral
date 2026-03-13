package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DataBarFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;

/* JADX INFO: loaded from: classes.dex */
public class XSSFDataBarFormatting implements DataBarFormatting {
    IndexedColorMap _colorMap;
    CTDataBar _databar;

    XSSFDataBarFormatting(CTDataBar databar, IndexedColorMap colorMap) {
        this._databar = databar;
        this._colorMap = colorMap;
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public boolean isIconOnly() {
        if (this._databar.isSetShowValue()) {
            return !this._databar.getShowValue();
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public void setIconOnly(boolean only) {
        this._databar.setShowValue(!only);
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public boolean isLeftToRight() {
        return true;
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public void setLeftToRight(boolean ltr) {
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public int getWidthMin() {
        return 0;
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public void setWidthMin(int width) {
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public int getWidthMax() {
        return 100;
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public void setWidthMax(int width) {
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public XSSFColor getColor() {
        return new XSSFColor(this._databar.getColor(), this._colorMap);
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public void setColor(Color color) {
        this._databar.setColor(((XSSFColor) color).getCTColor());
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public XSSFConditionalFormattingThreshold getMinThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(0));
    }

    @Override // org.apache.poi.ss.usermodel.DataBarFormatting
    public XSSFConditionalFormattingThreshold getMaxThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.getCfvoArray(1));
    }

    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._databar.addNewCfvo());
    }
}
