package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.RefEvalBase;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public final class LazyRefEval extends RefEvalBase {
    private final SheetRangeEvaluator _evaluator;

    public LazyRefEval(int rowIndex, int columnIndex, SheetRangeEvaluator sre) {
        super(sre, rowIndex, columnIndex);
        this._evaluator = sre;
    }

    @Override // org.apache.poi.ss.formula.eval.RefEval
    public ValueEval getInnerValueEval(int sheetIndex) {
        return this._evaluator.getEvalForCell(sheetIndex, getRow(), getColumn());
    }

    @Override // org.apache.poi.ss.formula.eval.RefEval
    public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
        AreaI area = new AreaI.OffsetArea(getRow(), getColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        return new LazyAreaEval(area, this._evaluator);
    }

    public boolean isSubTotal() {
        SheetRefEvaluator sheetEvaluator = this._evaluator.getSheetEvaluator(getFirstSheetIndex());
        return sheetEvaluator.isSubTotal(getRow(), getColumn());
    }

    public String toString() {
        CellReference cr = new CellReference(getRow(), getColumn());
        return getClass().getName() + "[" + this._evaluator.getSheetNameRange() + '!' + cr.formatAsString() + "]";
    }
}
