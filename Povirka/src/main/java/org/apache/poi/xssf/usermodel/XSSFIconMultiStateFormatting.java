package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFIconMultiStateFormatting implements IconMultiStateFormatting {
    CTIconSet _iconset;

    XSSFIconMultiStateFormatting(CTIconSet iconset) {
        this._iconset = iconset;
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public IconMultiStateFormatting.IconSet getIconSet() {
        String set = this._iconset.getIconSet().toString();
        return IconMultiStateFormatting.IconSet.byName(set);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setIconSet(IconMultiStateFormatting.IconSet set) {
        STIconSetType.Enum xIconSet = STIconSetType.Enum.forString(set.name);
        this._iconset.setIconSet(xIconSet);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public boolean isIconOnly() {
        if (this._iconset.isSetShowValue()) {
            return !this._iconset.getShowValue();
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setIconOnly(boolean only) {
        this._iconset.setShowValue(!only);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public boolean isReversed() {
        if (this._iconset.isSetReverse()) {
            return this._iconset.getReverse();
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setReversed(boolean reversed) {
        this._iconset.setReverse(reversed);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public XSSFConditionalFormattingThreshold[] getThresholds() {
        CTCfvo[] cfvos = this._iconset.getCfvoArray();
        XSSFConditionalFormattingThreshold[] t = new XSSFConditionalFormattingThreshold[cfvos.length];
        for (int i = 0; i < cfvos.length; i++) {
            t[i] = new XSSFConditionalFormattingThreshold(cfvos[i]);
        }
        return t;
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setThresholds(ConditionalFormattingThreshold[] thresholds) {
        CTCfvo[] cfvos = new CTCfvo[thresholds.length];
        for (int i = 0; i < thresholds.length; i++) {
            cfvos[i] = ((XSSFConditionalFormattingThreshold) thresholds[i]).getCTCfvo();
        }
        this._iconset.setCfvoArray(cfvos);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public XSSFConditionalFormattingThreshold createThreshold() {
        return new XSSFConditionalFormattingThreshold(this._iconset.addNewCfvo());
    }
}
