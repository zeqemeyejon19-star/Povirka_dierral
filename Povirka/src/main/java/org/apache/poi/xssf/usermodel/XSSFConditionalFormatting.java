package org.apache.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;

/* JADX INFO: loaded from: classes.dex */
public class XSSFConditionalFormatting implements ConditionalFormatting {
    private final CTConditionalFormatting _cf;
    private final XSSFSheet _sh;

    XSSFConditionalFormatting(XSSFSheet sh) {
        this._cf = CTConditionalFormatting.Factory.newInstance();
        this._sh = sh;
    }

    XSSFConditionalFormatting(XSSFSheet sh, CTConditionalFormatting cf) {
        this._cf = cf;
        this._sh = sh;
    }

    CTConditionalFormatting getCTConditionalFormatting() {
        return this._cf;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public CellRangeAddress[] getFormattingRanges() {
        ArrayList<CellRangeAddress> lst = new ArrayList<>();
        for (Object stRef : this._cf.getSqref()) {
            String[] regions = stRef.toString().split(" ");
            for (String region : regions) {
                lst.add(CellRangeAddress.valueOf(region));
            }
        }
        return (CellRangeAddress[]) lst.toArray(new CellRangeAddress[lst.size()]);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void setFormattingRanges(CellRangeAddress[] ranges) {
        if (ranges == null) {
            throw new IllegalArgumentException("cellRanges must not be null");
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (CellRangeAddress range : ranges) {
            if (!first) {
                sb.append(" ");
            } else {
                first = false;
            }
            sb.append(range.formatAsString());
        }
        this._cf.setSqref(Collections.singletonList(sb.toString()));
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void setRule(int idx, ConditionalFormattingRule cfRule) {
        XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule) cfRule;
        this._cf.getCfRuleArray(idx).set(xRule.getCTCfRule());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public void addRule(ConditionalFormattingRule cfRule) {
        XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule) cfRule;
        this._cf.addNewCfRule().set(xRule.getCTCfRule());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public XSSFConditionalFormattingRule getRule(int idx) {
        return new XSSFConditionalFormattingRule(this._sh, this._cf.getCfRuleArray(idx));
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormatting
    public int getNumberOfRules() {
        return this._cf.sizeOfCfRuleArray();
    }

    public String toString() {
        return this._cf.toString();
    }
}
