package org.apache.poi.ss.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public class ConditionalFormattingEvaluator {
    private final Map<String, List<EvaluationConditionalFormatRule>> formats = new HashMap();
    private final Map<CellReference, List<EvaluationConditionalFormatRule>> values = new HashMap();
    private final Workbook workbook;
    private final WorkbookEvaluator workbookEvaluator;

    public ConditionalFormattingEvaluator(Workbook wb, WorkbookEvaluatorProvider provider) {
        this.workbook = wb;
        this.workbookEvaluator = provider._getWorkbookEvaluator();
    }

    protected WorkbookEvaluator getWorkbookEvaluator() {
        return this.workbookEvaluator;
    }

    public void clearAllCachedFormats() {
        this.formats.clear();
    }

    public void clearAllCachedValues() {
        this.values.clear();
    }

    protected List<EvaluationConditionalFormatRule> getRules(Sheet sheet) {
        ConditionalFormattingEvaluator conditionalFormattingEvaluator = this;
        String sheetName = sheet.getSheetName();
        List<EvaluationConditionalFormatRule> rules = conditionalFormattingEvaluator.formats.get(sheetName);
        if (rules == null) {
            if (conditionalFormattingEvaluator.formats.containsKey(sheetName)) {
                return Collections.emptyList();
            }
            SheetConditionalFormatting scf = sheet.getSheetConditionalFormatting();
            int count = scf.getNumConditionalFormattings();
            rules = new ArrayList(count);
            conditionalFormattingEvaluator.formats.put(sheetName, rules);
            int i = 0;
            while (i < count) {
                ConditionalFormatting f = scf.getConditionalFormattingAt(i);
                CellRangeAddress[] regions = f.getFormattingRanges();
                int r = 0;
                while (r < f.getNumberOfRules()) {
                    ConditionalFormattingRule rule = f.getRule(r);
                    rules.add(new EvaluationConditionalFormatRule(conditionalFormattingEvaluator.workbookEvaluator, sheet, f, i, rule, r, regions));
                    r++;
                    conditionalFormattingEvaluator = this;
                }
                i++;
                conditionalFormattingEvaluator = this;
            }
            Collections.sort(rules);
        }
        return Collections.unmodifiableList(rules);
    }

    public List<EvaluationConditionalFormatRule> getConditionalFormattingForCell(CellReference cellRef) {
        Sheet sheet;
        List<EvaluationConditionalFormatRule> rules = this.values.get(cellRef);
        if (rules == null) {
            rules = new ArrayList();
            if (cellRef.getSheetName() != null) {
                sheet = this.workbook.getSheet(cellRef.getSheetName());
            } else {
                Workbook workbook = this.workbook;
                sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
            }
            boolean stopIfTrue = false;
            for (EvaluationConditionalFormatRule rule : getRules(sheet)) {
                if (!stopIfTrue && rule.matches(cellRef)) {
                    rules.add(rule);
                    stopIfTrue = rule.getRule().getStopIfTrue();
                }
            }
            Collections.sort(rules);
            this.values.put(cellRef, rules);
        }
        return Collections.unmodifiableList(rules);
    }

    public List<EvaluationConditionalFormatRule> getConditionalFormattingForCell(Cell cell) {
        return getConditionalFormattingForCell(getRef(cell));
    }

    public static CellReference getRef(Cell cell) {
        return new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), false, false);
    }

    public List<EvaluationConditionalFormatRule> getFormatRulesForSheet(String sheetName) {
        return getFormatRulesForSheet(this.workbook.getSheet(sheetName));
    }

    public List<EvaluationConditionalFormatRule> getFormatRulesForSheet(Sheet sheet) {
        return getRules(sheet);
    }

    public List<Cell> getMatchingCells(Sheet sheet, int conditionalFormattingIndex, int ruleIndex) {
        for (EvaluationConditionalFormatRule rule : getRules(sheet)) {
            if (rule.getSheet().equals(sheet) && rule.getFormattingIndex() == conditionalFormattingIndex && rule.getRuleIndex() == ruleIndex) {
                return getMatchingCells(rule);
            }
        }
        return Collections.emptyList();
    }

    public List<Cell> getMatchingCells(EvaluationConditionalFormatRule rule) {
        List<Cell> cells = new ArrayList<>();
        Sheet sheet = rule.getSheet();
        CellRangeAddress[] arr$ = rule.getRegions();
        for (CellRangeAddress region : arr$) {
            for (int r = region.getFirstRow(); r <= region.getLastRow(); r++) {
                Row row = sheet.getRow(r);
                if (row != null) {
                    for (int c = region.getFirstColumn(); c <= region.getLastColumn(); c++) {
                        Cell cell = row.getCell(c);
                        if (cell != null) {
                            List<EvaluationConditionalFormatRule> cellRules = getConditionalFormattingForCell(cell);
                            if (cellRules.contains(rule)) {
                                cells.add(cell);
                            }
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableList(cells);
    }
}
