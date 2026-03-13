package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.AreaEvalBase;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
final class LazyAreaEval extends AreaEvalBase {
    private final SheetRangeEvaluator _evaluator;

    LazyAreaEval(AreaI ptg, SheetRangeEvaluator evaluator) {
        super(ptg, evaluator);
        this._evaluator = evaluator;
    }

    public LazyAreaEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex, SheetRangeEvaluator evaluator) {
        super(evaluator, firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
        this._evaluator = evaluator;
    }

    @Override // org.apache.poi.ss.formula.eval.AreaEvalBase, org.apache.poi.ss.formula.eval.AreaEval
    public ValueEval getRelativeValue(int relativeRowIndex, int relativeColumnIndex) {
        return getRelativeValue(getFirstSheetIndex(), relativeRowIndex, relativeColumnIndex);
    }

    @Override // org.apache.poi.ss.formula.eval.AreaEvalBase
    public ValueEval getRelativeValue(int sheetIndex, int relativeRowIndex, int relativeColumnIndex) {
        int rowIx = getFirstRow() + relativeRowIndex;
        int colIx = getFirstColumn() + relativeColumnIndex;
        return this._evaluator.getEvalForCell(sheetIndex, rowIx, colIx);
    }

    @Override // org.apache.poi.ss.formula.eval.AreaEval
    public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
        AreaI area = new AreaI.OffsetArea(getFirstRow(), getFirstColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        return new LazyAreaEval(area, this._evaluator);
    }

    @Override // org.apache.poi.ss.formula.TwoDEval
    public LazyAreaEval getRow(int rowIndex) {
        if (rowIndex >= getHeight()) {
            throw new IllegalArgumentException("Invalid rowIndex " + rowIndex + ".  Allowable range is (0.." + getHeight() + ").");
        }
        int absRowIx = getFirstRow() + rowIndex;
        return new LazyAreaEval(absRowIx, getFirstColumn(), absRowIx, getLastColumn(), this._evaluator);
    }

    @Override // org.apache.poi.ss.formula.TwoDEval
    public LazyAreaEval getColumn(int columnIndex) {
        if (columnIndex >= getWidth()) {
            throw new IllegalArgumentException("Invalid columnIndex " + columnIndex + ".  Allowable range is (0.." + getWidth() + ").");
        }
        int absColIx = getFirstColumn() + columnIndex;
        return new LazyAreaEval(getFirstRow(), absColIx, getLastRow(), absColIx, this._evaluator);
    }

    public String toString() {
        CellReference crA = new CellReference(getFirstRow(), getFirstColumn());
        CellReference crB = new CellReference(getLastRow(), getLastColumn());
        return getClass().getName() + "[" + this._evaluator.getSheetNameRange() + '!' + crA.formatAsString() + ':' + crB.formatAsString() + "]";
    }

    @Override // org.apache.poi.ss.formula.eval.AreaEvalBase, org.apache.poi.ss.formula.TwoDEval
    public boolean isSubTotal(int rowIndex, int columnIndex) {
        SheetRangeEvaluator sheetRangeEvaluator = this._evaluator;
        SheetRefEvaluator _sre = sheetRangeEvaluator.getSheetEvaluator(sheetRangeEvaluator.getFirstSheetIndex());
        return _sre.isSubTotal(getFirstRow() + rowIndex, getFirstColumn() + columnIndex);
    }
}
