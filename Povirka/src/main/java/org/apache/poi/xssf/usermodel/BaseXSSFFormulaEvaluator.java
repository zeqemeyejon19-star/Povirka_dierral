package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.RichTextString;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseXSSFFormulaEvaluator extends BaseFormulaEvaluator {
    protected abstract EvaluationCell toEvaluationCell(Cell cell);

    protected BaseXSSFFormulaEvaluator(WorkbookEvaluator bookEvaluator) {
        super(bookEvaluator);
    }

    @Override // org.apache.poi.ss.formula.BaseFormulaEvaluator
    protected RichTextString createRichTextString(String str) {
        return new XSSFRichTextString(str);
    }

    @Override // org.apache.poi.ss.usermodel.FormulaEvaluator
    public void notifySetFormula(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new XSSFEvaluationCell((XSSFCell) cell));
    }

    @Override // org.apache.poi.ss.usermodel.FormulaEvaluator
    public void notifyDeleteCell(Cell cell) {
        this._bookEvaluator.notifyDeleteCell(new XSSFEvaluationCell((XSSFCell) cell));
    }

    @Override // org.apache.poi.ss.usermodel.FormulaEvaluator
    public void notifyUpdateCell(Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new XSSFEvaluationCell((XSSFCell) cell));
    }

    @Override // org.apache.poi.ss.formula.BaseFormulaEvaluator
    protected CellValue evaluateFormulaCellValue(Cell cell) {
        EvaluationCell evalCell = toEvaluationCell(cell);
        ValueEval eval = this._bookEvaluator.evaluate(evalCell);
        if (eval instanceof NumberEval) {
            NumberEval ne = (NumberEval) eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof BoolEval) {
            BoolEval be = (BoolEval) eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof StringEval) {
            StringEval ne2 = (StringEval) eval;
            return new CellValue(ne2.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval) eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }
}
