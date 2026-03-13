package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFConditionalFormatting implements ConditionalFormatting {
    private final CFRecordsAggregate cfAggregate;
    private final HSSFSheet sheet;

    HSSFConditionalFormatting(HSSFSheet sheet, CFRecordsAggregate cfAggregate) {
        if (sheet == null) {
            throw new IllegalArgumentException("sheet must not be null");
        }
        if (cfAggregate == null) {
            throw new IllegalArgumentException("cfAggregate must not be null");
        }
        this.sheet = sheet;
        this.cfAggregate = cfAggregate;
    }

    CFRecordsAggregate getCFRecordsAggregate() {
        return this.cfAggregate;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public CellRangeAddress[] getFormattingRanges() {
        return this.cfAggregate.getHeader().getCellRanges();
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void setFormattingRanges(CellRangeAddress[] ranges) {
        this.cfAggregate.getHeader().setCellRanges(ranges);
    }

    public void setRule(int idx, HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.setRule(idx, cfRule.getCfRuleRecord());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void setRule(int idx, ConditionalFormattingRule cfRule) {
        setRule(idx, (HSSFConditionalFormattingRule) cfRule);
    }

    public void addRule(HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.addRule(cfRule.getCfRuleRecord());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void addRule(ConditionalFormattingRule cfRule) {
        addRule((HSSFConditionalFormattingRule) cfRule);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public HSSFConditionalFormattingRule getRule(int idx) {
        CFRuleBase ruleRecord = this.cfAggregate.getRule(idx);
        return new HSSFConditionalFormattingRule(this.sheet, ruleRecord);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public int getNumberOfRules() {
        return this.cfAggregate.getNumberOfRules();
    }

    public String toString() {
        return this.cfAggregate.toString();
    }
}
