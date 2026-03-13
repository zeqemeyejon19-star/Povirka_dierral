package org.apache.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeUtil;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STConditionalFormattingOperator;

/* JADX INFO: loaded from: classes.dex */
public class XSSFSheetConditionalFormatting implements SheetConditionalFormatting {
    protected static final String CF_EXT_2009_NS_X14 = "http://schemas.microsoft.com/office/spreadsheetml/2009/9/main";
    private final XSSFSheet _sheet;

    XSSFSheetConditionalFormatting(XSSFSheet sheet) {
        this._sheet = sheet;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingRule(byte comparisonOperation, String formula1, String formula2) {
        STConditionalFormattingOperator.Enum operator;
        XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        CTCfRule cfRule = rule.getCTCfRule();
        cfRule.addFormula(formula1);
        if (formula2 != null) {
            cfRule.addFormula(formula2);
        }
        cfRule.setType(STCfType.CELL_IS);
        switch (comparisonOperation) {
            case 1:
                operator = STConditionalFormattingOperator.BETWEEN;
                break;
            case 2:
                operator = STConditionalFormattingOperator.NOT_BETWEEN;
                break;
            case 3:
                operator = STConditionalFormattingOperator.EQUAL;
                break;
            case 4:
                operator = STConditionalFormattingOperator.NOT_EQUAL;
                break;
            case 5:
                operator = STConditionalFormattingOperator.GREATER_THAN;
                break;
            case 6:
                operator = STConditionalFormattingOperator.LESS_THAN;
                break;
            case 7:
                operator = STConditionalFormattingOperator.GREATER_THAN_OR_EQUAL;
                break;
            case 8:
                operator = STConditionalFormattingOperator.LESS_THAN_OR_EQUAL;
                break;
            default:
                throw new IllegalArgumentException("Unknown comparison operator: " + ((int) comparisonOperation));
        }
        cfRule.setOperator(operator);
        return rule;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingRule(byte comparisonOperation, String formula) {
        return createConditionalFormattingRule(comparisonOperation, formula, (String) null);
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingRule(String formula) {
        XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        CTCfRule cfRule = rule.getCTCfRule();
        cfRule.addFormula(formula);
        cfRule.setType(STCfType.EXPRESSION);
        return rule;
    }

    public XSSFConditionalFormattingRule createConditionalFormattingRule(XSSFColor color) {
        XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createDataBarFormatting(color);
        return rule;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingRule(ExtendedColor color) {
        return createConditionalFormattingRule((XSSFColor) color);
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingRule(IconMultiStateFormatting.IconSet iconSet) {
        XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createMultiStateFormatting(iconSet);
        return rule;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormattingRule createConditionalFormattingColorScaleRule() {
        XSSFConditionalFormattingRule rule = new XSSFConditionalFormattingRule(this._sheet);
        rule.createColorScaleFormatting();
        return rule;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public int addConditionalFormatting(CellRangeAddress[] regions, ConditionalFormattingRule[] cfRules) {
        if (regions == null) {
            throw new IllegalArgumentException("regions must not be null");
        }
        for (CellRangeAddress range : regions) {
            range.validate(SpreadsheetVersion.EXCEL2007);
        }
        if (cfRules == null) {
            throw new IllegalArgumentException("cfRules must not be null");
        }
        if (cfRules.length == 0) {
            throw new IllegalArgumentException("cfRules must not be empty");
        }
        if (cfRules.length > 3) {
            throw new IllegalArgumentException("Number of rules must not exceed 3");
        }
        CellRangeAddress[] mergeCellRanges = CellRangeUtil.mergeCellRanges(regions);
        CTConditionalFormatting cf = this._sheet.getCTWorksheet().addNewConditionalFormatting();
        List<String> refs = new ArrayList<>();
        for (CellRangeAddress a : mergeCellRanges) {
            refs.add(a.formatAsString());
        }
        cf.setSqref(refs);
        int priority = 1;
        CTConditionalFormatting[] arr$ = this._sheet.getCTWorksheet().getConditionalFormattingArray();
        for (CTConditionalFormatting c : arr$) {
            priority += c.sizeOfCfRuleArray();
        }
        int len$ = cfRules.length;
        int i$ = 0;
        while (i$ < len$) {
            ConditionalFormattingRule rule = cfRules[i$];
            XSSFConditionalFormattingRule xRule = (XSSFConditionalFormattingRule) rule;
            xRule.getCTCfRule().setPriority(priority);
            cf.addNewCfRule().set(xRule.getCTCfRule());
            i$++;
            priority++;
        }
        return this._sheet.getCTWorksheet().sizeOfConditionalFormattingArray() - 1;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public int addConditionalFormatting(CellRangeAddress[] regions, ConditionalFormattingRule rule1) {
        return addConditionalFormatting(regions, rule1 == null ? null : new XSSFConditionalFormattingRule[]{(XSSFConditionalFormattingRule) rule1});
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public int addConditionalFormatting(CellRangeAddress[] regions, ConditionalFormattingRule rule1, ConditionalFormattingRule rule2) {
        return addConditionalFormatting(regions, rule1 == null ? null : new XSSFConditionalFormattingRule[]{(XSSFConditionalFormattingRule) rule1, (XSSFConditionalFormattingRule) rule2});
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public int addConditionalFormatting(ConditionalFormatting cf) {
        XSSFConditionalFormatting xcf = (XSSFConditionalFormatting) cf;
        CTWorksheet sh = this._sheet.getCTWorksheet();
        sh.addNewConditionalFormatting().set(xcf.getCTConditionalFormatting().copy());
        return sh.sizeOfConditionalFormattingArray() - 1;
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public XSSFConditionalFormatting getConditionalFormattingAt(int index) {
        checkIndex(index);
        CTConditionalFormatting cf = this._sheet.getCTWorksheet().getConditionalFormattingArray(index);
        return new XSSFConditionalFormatting(this._sheet, cf);
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public int getNumConditionalFormattings() {
        return this._sheet.getCTWorksheet().sizeOfConditionalFormattingArray();
    }

    @Override // org.apache.poi.ss.usermodel.SheetConditionalFormatting
    public void removeConditionalFormatting(int index) {
        checkIndex(index);
        this._sheet.getCTWorksheet().removeConditionalFormatting(index);
    }

    private void checkIndex(int index) {
        int cnt = getNumConditionalFormattings();
        if (index < 0 || index >= cnt) {
            throw new IllegalArgumentException("Specified CF index " + index + " is outside the allowable range (0.." + (cnt - 1) + ")");
        }
    }
}
