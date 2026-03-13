package org.apache.poi.xssf.streaming;

import java.util.Iterator;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.BaseXSSFFormulaEvaluator;

/* JADX INFO: loaded from: classes.dex */
public final class SXSSFFormulaEvaluator extends BaseXSSFFormulaEvaluator {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SXSSFFormulaEvaluator.class);
    private SXSSFWorkbook wb;

    public SXSSFFormulaEvaluator(SXSSFWorkbook workbook) {
        this(workbook, null, null);
    }

    private SXSSFFormulaEvaluator(SXSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this(workbook, new WorkbookEvaluator(SXSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder));
    }

    private SXSSFFormulaEvaluator(SXSSFWorkbook workbook, WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
        this.wb = workbook;
    }

    public static SXSSFFormulaEvaluator create(SXSSFWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        return new SXSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }

    @Override // org.apache.poi.xssf.usermodel.BaseXSSFFormulaEvaluator
    protected EvaluationCell toEvaluationCell(Cell cell) {
        if (!(cell instanceof SXSSFCell)) {
            throw new IllegalArgumentException("Unexpected type of cell: " + cell.getClass() + ". Only SXSSFCells can be evaluated.");
        }
        return new SXSSFEvaluationCell((SXSSFCell) cell);
    }

    @Override // org.apache.poi.ss.formula.BaseFormulaEvaluator, org.apache.poi.ss.usermodel.FormulaEvaluator
    public SXSSFCell evaluateInCell(Cell cell) {
        return (SXSSFCell) super.evaluateInCell(cell);
    }

    public static void evaluateAllFormulaCells(SXSSFWorkbook wb, boolean skipOutOfWindow) {
        SXSSFFormulaEvaluator eval = new SXSSFFormulaEvaluator(wb);
        Iterator<Sheet> it = wb.iterator();
        while (it.hasNext()) {
            if (((SXSSFSheet) it.next()).areAllRowsFlushed()) {
                throw new SheetsFlushedException();
            }
        }
        for (Sheet sheet : wb) {
            int lastFlushedRowNum = ((SXSSFSheet) sheet).getLastFlushedRowNum();
            if (lastFlushedRowNum > -1) {
                if (!skipOutOfWindow) {
                    throw new RowFlushedException(0);
                }
                logger.log(3, "Rows up to " + lastFlushedRowNum + " have already been flushed, skipping");
            }
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellTypeEnum() == CellType.FORMULA) {
                        eval.evaluateFormulaCellEnum(c);
                    }
                }
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.FormulaEvaluator
    public void evaluateAll() {
        evaluateAllFormulaCells(this.wb, false);
    }

    public static class SheetsFlushedException extends IllegalStateException {
        protected SheetsFlushedException() {
            super("One or more sheets have been flushed, cannot evaluate all cells");
        }
    }

    public static class RowFlushedException extends IllegalStateException {
        protected RowFlushedException(int rowNum) {
            super("Row " + rowNum + " has been flushed, cannot evaluate all cells");
        }
    }
}
