package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.CFRuleBase;
import org.apache.poi.hssf.record.cf.BorderFormatting;
import org.apache.poi.hssf.record.cf.ColorGradientFormatting;
import org.apache.poi.hssf.record.cf.DataBarFormatting;
import org.apache.poi.hssf.record.cf.FontFormatting;
import org.apache.poi.hssf.record.cf.IconMultiStateFormatting;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.ConditionFilterData;
import org.apache.poi.ss.usermodel.ConditionFilterType;
import org.apache.poi.ss.usermodel.ConditionType;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFConditionalFormattingRule implements ConditionalFormattingRule {
    private static final byte CELL_COMPARISON = 1;
    private final CFRuleBase cfRuleRecord;
    private final HSSFSheet sheet;
    private final HSSFWorkbook workbook;

    HSSFConditionalFormattingRule(HSSFSheet pSheet, CFRuleBase pRuleRecord) {
        if (pSheet == null) {
            throw new IllegalArgumentException("pSheet must not be null");
        }
        if (pRuleRecord == null) {
            throw new IllegalArgumentException("pRuleRecord must not be null");
        }
        this.sheet = pSheet;
        this.workbook = pSheet.getWorkbook();
        this.cfRuleRecord = pRuleRecord;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public int getPriority() {
        CFRule12Record rule12 = getCFRule12Record(false);
        if (rule12 == null) {
            return 0;
        }
        return rule12.getPriority();
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public boolean getStopIfTrue() {
        return true;
    }

    CFRuleBase getCfRuleRecord() {
        return this.cfRuleRecord;
    }

    private CFRule12Record getCFRule12Record(boolean create) {
        CFRuleBase cFRuleBase = this.cfRuleRecord;
        if (!(cFRuleBase instanceof CFRule12Record)) {
            if (create) {
                throw new IllegalArgumentException("Can't convert a CF into a CF12 record");
            }
            return null;
        }
        return (CFRule12Record) cFRuleBase;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public ExcelNumberFormat getNumberFormat() {
        return null;
    }

    private HSSFFontFormatting getFontFormatting(boolean create) {
        FontFormatting fontFormatting = this.cfRuleRecord.getFontFormatting();
        if (fontFormatting == null) {
            if (!create) {
                return null;
            }
            FontFormatting fontFormatting2 = new FontFormatting();
            this.cfRuleRecord.setFontFormatting(fontFormatting2);
        }
        return new HSSFFontFormatting(this.cfRuleRecord, this.workbook);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public HSSFFontFormatting getFontFormatting() {
        return getFontFormatting(false);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFFontFormatting createFontFormatting() {
        return getFontFormatting(true);
    }

    private HSSFBorderFormatting getBorderFormatting(boolean create) {
        BorderFormatting borderFormatting = this.cfRuleRecord.getBorderFormatting();
        if (borderFormatting == null) {
            if (!create) {
                return null;
            }
            BorderFormatting borderFormatting2 = new BorderFormatting();
            this.cfRuleRecord.setBorderFormatting(borderFormatting2);
        }
        return new HSSFBorderFormatting(this.cfRuleRecord, this.workbook);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public HSSFBorderFormatting getBorderFormatting() {
        return getBorderFormatting(false);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFBorderFormatting createBorderFormatting() {
        return getBorderFormatting(true);
    }

    private HSSFPatternFormatting getPatternFormatting(boolean create) {
        PatternFormatting patternFormatting = this.cfRuleRecord.getPatternFormatting();
        if (patternFormatting == null) {
            if (!create) {
                return null;
            }
            PatternFormatting patternFormatting2 = new PatternFormatting();
            this.cfRuleRecord.setPatternFormatting(patternFormatting2);
        }
        return new HSSFPatternFormatting(this.cfRuleRecord, this.workbook);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public HSSFPatternFormatting getPatternFormatting() {
        return getPatternFormatting(false);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFPatternFormatting createPatternFormatting() {
        return getPatternFormatting(true);
    }

    private HSSFDataBarFormatting getDataBarFormatting(boolean create) {
        CFRule12Record cfRule12Record = getCFRule12Record(create);
        if (cfRule12Record == null) {
            return null;
        }
        DataBarFormatting databarFormatting = cfRule12Record.getDataBarFormatting();
        if (databarFormatting == null) {
            if (!create) {
                return null;
            }
            cfRule12Record.createDataBarFormatting();
        }
        return new HSSFDataBarFormatting(cfRule12Record, this.sheet);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFDataBarFormatting getDataBarFormatting() {
        return getDataBarFormatting(false);
    }

    public HSSFDataBarFormatting createDataBarFormatting() {
        return getDataBarFormatting(true);
    }

    private HSSFIconMultiStateFormatting getMultiStateFormatting(boolean create) {
        CFRule12Record cfRule12Record = getCFRule12Record(create);
        if (cfRule12Record == null) {
            return null;
        }
        IconMultiStateFormatting iconFormatting = cfRule12Record.getMultiStateFormatting();
        if (iconFormatting == null) {
            if (!create) {
                return null;
            }
            cfRule12Record.createMultiStateFormatting();
        }
        return new HSSFIconMultiStateFormatting(cfRule12Record, this.sheet);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFIconMultiStateFormatting getMultiStateFormatting() {
        return getMultiStateFormatting(false);
    }

    public HSSFIconMultiStateFormatting createMultiStateFormatting() {
        return getMultiStateFormatting(true);
    }

    private HSSFColorScaleFormatting getColorScaleFormatting(boolean create) {
        CFRule12Record cfRule12Record = getCFRule12Record(create);
        if (cfRule12Record == null) {
            return null;
        }
        ColorGradientFormatting colorFormatting = cfRule12Record.getColorGradientFormatting();
        if (colorFormatting == null) {
            if (!create) {
                return null;
            }
            cfRule12Record.createColorGradientFormatting();
        }
        return new HSSFColorScaleFormatting(cfRule12Record, this.sheet);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public HSSFColorScaleFormatting getColorScaleFormatting() {
        return getColorScaleFormatting(false);
    }

    public HSSFColorScaleFormatting createColorScaleFormatting() {
        return getColorScaleFormatting(true);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionType getConditionType() {
        byte code = this.cfRuleRecord.getConditionType();
        return ConditionType.forId(code);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionFilterType getConditionFilterType() {
        if (getConditionType() == ConditionType.FILTER) {
            return ConditionFilterType.FILTER;
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionFilterData getFilterConfiguration() {
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public byte getComparisonOperation() {
        return this.cfRuleRecord.getComparisonOperation();
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public String getFormula1() {
        return toFormulaString(this.cfRuleRecord.getParsedExpression1());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public String getFormula2() {
        byte conditionType = this.cfRuleRecord.getConditionType();
        if (conditionType == 1) {
            byte comparisonOperation = this.cfRuleRecord.getComparisonOperation();
            if (comparisonOperation == 1 || comparisonOperation == 2) {
                return toFormulaString(this.cfRuleRecord.getParsedExpression2());
            }
            return null;
        }
        return null;
    }

    protected String toFormulaString(Ptg[] parsedExpression) {
        return toFormulaString(parsedExpression, this.workbook);
    }

    protected static String toFormulaString(Ptg[] parsedExpression, HSSFWorkbook workbook) {
        if (parsedExpression == null || parsedExpression.length == 0) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(workbook, parsedExpression);
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public int getStripeSize() {
        return 0;
    }
}
