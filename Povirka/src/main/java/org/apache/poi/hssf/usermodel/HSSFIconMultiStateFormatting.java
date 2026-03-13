package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.cf.IconMultiStateThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFIconMultiStateFormatting implements IconMultiStateFormatting {
    private final CFRule12Record cfRule12Record;
    private final org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconFormatting;
    private final HSSFSheet sheet;

    protected HSSFIconMultiStateFormatting(CFRule12Record cfRule12Record, HSSFSheet sheet) {
        this.sheet = sheet;
        this.cfRule12Record = cfRule12Record;
        this.iconFormatting = cfRule12Record.getMultiStateFormatting();
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public IconMultiStateFormatting.IconSet getIconSet() {
        return this.iconFormatting.getIconSet();
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setIconSet(IconMultiStateFormatting.IconSet set) {
        this.iconFormatting.setIconSet(set);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public boolean isIconOnly() {
        return this.iconFormatting.isIconOnly();
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setIconOnly(boolean only) {
        this.iconFormatting.setIconOnly(only);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public boolean isReversed() {
        return this.iconFormatting.isReversed();
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setReversed(boolean reversed) {
        this.iconFormatting.setReversed(reversed);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public HSSFConditionalFormattingThreshold[] getThresholds() {
        Threshold[] t = this.iconFormatting.getThresholds();
        HSSFConditionalFormattingThreshold[] ht = new HSSFConditionalFormattingThreshold[t.length];
        for (int i = 0; i < t.length; i++) {
            ht[i] = new HSSFConditionalFormattingThreshold(t[i], this.sheet);
        }
        return ht;
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public void setThresholds(ConditionalFormattingThreshold[] thresholds) {
        Threshold[] t = new Threshold[thresholds.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = ((HSSFConditionalFormattingThreshold) thresholds[i]).getThreshold();
        }
        this.iconFormatting.setThresholds(t);
    }

    @Override // org.apache.poi.ss.usermodel.IconMultiStateFormatting
    public HSSFConditionalFormattingThreshold createThreshold() {
        return new HSSFConditionalFormattingThreshold(new IconMultiStateThreshold(), this.sheet);
    }
}
