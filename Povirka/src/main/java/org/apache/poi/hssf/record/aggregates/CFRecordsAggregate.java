package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.CFHeader12Record;
import org.apache.poi.hssf.record.CFHeaderBase;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class CFRecordsAggregate extends RecordAggregate {
    private static final int MAX_97_2003_CONDTIONAL_FORMAT_RULES = 3;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) CFRecordsAggregate.class);
    private final CFHeaderBase header;
    private final List<CFRuleBase> rules;

    private CFRecordsAggregate(CFHeaderBase pHeader, CFRuleBase[] pRules) {
        if (pHeader == null) {
            throw new IllegalArgumentException("header must not be null");
        }
        if (pRules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        if (pRules.length > 3) {
            logger.log(5, "Excel versions before 2007 require that No more than 3 rules may be specified, " + pRules.length + " were found, this file will cause problems with old Excel versions");
        }
        if (pRules.length != pHeader.getNumberOfConditionalFormats()) {
            throw new RecordFormatException("Mismatch number of rules");
        }
        this.header = pHeader;
        this.rules = new ArrayList(pRules.length);
        for (CFRuleBase pRule : pRules) {
            checkRuleType(pRule);
            this.rules.add(pRule);
        }
    }

    public CFRecordsAggregate(CellRangeAddress[] regions, CFRuleBase[] rules) {
        this(createHeader(regions, rules), rules);
    }

    private static CFHeaderBase createHeader(CellRangeAddress[] regions, CFRuleBase[] rules) {
        CFHeaderBase header;
        if (rules.length == 0 || (rules[0] instanceof CFRuleRecord)) {
            header = new CFHeaderRecord(regions, rules.length);
        } else {
            header = new CFHeader12Record(regions, rules.length);
        }
        header.setNeedRecalculation(true);
        return header;
    }

    public static CFRecordsAggregate createCFAggregate(RecordStream rs) {
        Record rec = rs.getNext();
        if (rec.getSid() != 432 && rec.getSid() != 2169) {
            throw new IllegalStateException("next record sid was " + ((int) rec.getSid()) + " instead of 432 or 2169 as expected");
        }
        CFHeaderBase header = (CFHeaderBase) rec;
        int nRules = header.getNumberOfConditionalFormats();
        CFRuleBase[] rules = new CFRuleBase[nRules];
        for (int i = 0; i < rules.length; i++) {
            rules[i] = (CFRuleBase) rs.getNext();
        }
        return new CFRecordsAggregate(header, rules);
    }

    public CFRecordsAggregate cloneCFAggregate() {
        CFRuleBase[] newRecs = new CFRuleBase[this.rules.size()];
        for (int i = 0; i < newRecs.length; i++) {
            newRecs[i] = getRule(i).clone();
        }
        return new CFRecordsAggregate(this.header.clone(), newRecs);
    }

    public CFHeaderBase getHeader() {
        return this.header;
    }

    private void checkRuleIndex(int idx) {
        if (idx < 0 || idx >= this.rules.size()) {
            throw new IllegalArgumentException("Bad rule record index (" + idx + ") nRules=" + this.rules.size());
        }
    }

    private void checkRuleType(CFRuleBase r) {
        CFHeaderBase cFHeaderBase = this.header;
        if ((cFHeaderBase instanceof CFHeaderRecord) && (r instanceof CFRuleRecord)) {
            return;
        }
        if ((cFHeaderBase instanceof CFHeader12Record) && (r instanceof CFRule12Record)) {
        } else {
            throw new IllegalArgumentException("Header and Rule must both be CF or both be CF12, can't mix");
        }
    }

    public CFRuleBase getRule(int idx) {
        checkRuleIndex(idx);
        return this.rules.get(idx);
    }

    public void setRule(int idx, CFRuleBase r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        checkRuleIndex(idx);
        checkRuleType(r);
        this.rules.set(idx, r);
    }

    public void addRule(CFRuleBase r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        if (this.rules.size() >= 3) {
            logger.log(5, "Excel versions before 2007 cannot cope with any more than 3 - this file will cause problems with old Excel versions");
        }
        checkRuleType(r);
        this.rules.add(r);
        this.header.setNumberOfConditionalFormats(this.rules.size());
    }

    public int getNumberOfRules() {
        return this.rules.size();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String type = "CF";
        if (this.header instanceof CFHeader12Record) {
            type = "CF12";
        }
        buffer.append("[").append(type).append("]\n");
        CFHeaderBase cFHeaderBase = this.header;
        if (cFHeaderBase != null) {
            buffer.append(cFHeaderBase);
        }
        for (CFRuleBase cfRule : this.rules) {
            buffer.append(cfRule);
        }
        buffer.append("[/").append(type).append("]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate
    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        rv.visitRecord(this.header);
        for (CFRuleBase rule : this.rules) {
            rv.visitRecord(rule);
        }
    }

    public boolean updateFormulasAfterCellShift(FormulaShifter shifter, int currentExternSheetIx) {
        CFRule12Record rule12;
        Ptg[] ptgs;
        CellRangeAddress[] cellRanges = this.header.getCellRanges();
        boolean changed = false;
        List<CellRangeAddress> temp = new ArrayList<>();
        for (CellRangeAddress craOld : cellRanges) {
            CellRangeAddress craNew = shiftRange(shifter, craOld, currentExternSheetIx);
            if (craNew == null) {
                changed = true;
            } else {
                temp.add(craNew);
                if (craNew != craOld) {
                    changed = true;
                }
            }
        }
        if (changed) {
            int nRanges = temp.size();
            if (nRanges == 0) {
                return false;
            }
            CellRangeAddress[] newRanges = new CellRangeAddress[nRanges];
            temp.toArray(newRanges);
            this.header.setCellRanges(newRanges);
        }
        for (CFRuleBase rule : this.rules) {
            Ptg[] ptgs2 = rule.getParsedExpression1();
            if (ptgs2 != null && shifter.adjustFormula(ptgs2, currentExternSheetIx)) {
                rule.setParsedExpression1(ptgs2);
            }
            Ptg[] ptgs3 = rule.getParsedExpression2();
            if (ptgs3 != null && shifter.adjustFormula(ptgs3, currentExternSheetIx)) {
                rule.setParsedExpression2(ptgs3);
            }
            if ((rule instanceof CFRule12Record) && (ptgs = (rule12 = (CFRule12Record) rule).getParsedExpressionScale()) != null && shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                rule12.setParsedExpressionScale(ptgs);
            }
        }
        return true;
    }

    private static CellRangeAddress shiftRange(FormulaShifter shifter, CellRangeAddress cra, int currentExternSheetIx) {
        AreaPtg aptg = new AreaPtg(cra.getFirstRow(), cra.getLastRow(), cra.getFirstColumn(), cra.getLastColumn(), false, false, false, false);
        Ptg[] ptgs = {aptg};
        if (!shifter.adjustFormula(ptgs, currentExternSheetIx)) {
            return cra;
        }
        Ptg ptg0 = ptgs[0];
        if (ptg0 instanceof AreaPtg) {
            AreaPtg bptg = (AreaPtg) ptg0;
            return new CellRangeAddress(bptg.getFirstRow(), bptg.getLastRow(), bptg.getFirstColumn(), bptg.getLastColumn());
        }
        if (ptg0 instanceof AreaErrPtg) {
            return null;
        }
        throw new IllegalStateException("Unexpected shifted ptg class (" + ptg0.getClass().getName() + ")");
    }
}
