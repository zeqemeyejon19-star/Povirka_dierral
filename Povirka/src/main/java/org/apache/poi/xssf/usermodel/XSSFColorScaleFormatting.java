package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;

/* JADX INFO: loaded from: classes.dex */
public class XSSFColorScaleFormatting implements ColorScaleFormatting {
    private IndexedColorMap _indexedColorMap;
    private CTColorScale _scale;

    XSSFColorScaleFormatting(CTColorScale scale, IndexedColorMap colorMap) {
        this._scale = scale;
        this._indexedColorMap = colorMap;
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public int getNumControlPoints() {
        return this._scale.sizeOfCfvoArray();
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public void setNumControlPoints(int num) {
        while (num < this._scale.sizeOfCfvoArray()) {
            this._scale.removeCfvo(r0.sizeOfCfvoArray() - 1);
            this._scale.removeColor(r0.sizeOfColorArray() - 1);
        }
        while (num > this._scale.sizeOfCfvoArray()) {
            this._scale.addNewCfvo();
            this._scale.addNewColor();
        }
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public XSSFColor[] getColors() {
        CTColor[] ctcols = this._scale.getColorArray();
        XSSFColor[] c = new XSSFColor[ctcols.length];
        for (int i = 0; i < ctcols.length; i++) {
            c[i] = new XSSFColor(ctcols[i], this._indexedColorMap);
        }
        return c;
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public void setColors(Color[] colors) {
        CTColor[] ctcols = new CTColor[colors.length];
        for (int i = 0; i < colors.length; i++) {
            ctcols[i] = ((XSSFColor) colors[i]).getCTColor();
        }
        this._scale.setColorArray(ctcols);
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public XSSFConditionalFormattingThreshold[] getThresholds() {
        CTCfvo[] cfvos = this._scale.getCfvoArray();
        XSSFConditionalFormattingThreshold[] t = new XSSFConditionalFormattingThreshold[cfvos.length];
        for (int i = 0; i < cfvos.length; i++) {
            t[i] = new XSSFConditionalFormattingThreshold(cfvos[i]);
        }
        return t;
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public void setThresholds(ConditionalFormattingThreshold[] thresholds) {
        CTCfvo[] cfvos = new CTCfvo[thresholds.length];
        for (int i = 0; i < thresholds.length; i++) {
            cfvos[i] = ((XSSFConditionalFormattingThreshold) thresholds[i]).getCTCfvo();
        }
        this._scale.setCfvoArray(cfvos);
    }

    public XSSFColor createColor() {
        return new XSSFColor(this._scale.addNewColor(), this._indexedColorMap);
    }

    @Override // org.apache.poi.ss.usermodel.ColorScaleFormatting
    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._scale.addNewCfvo());
    }
}
