package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class DMin implements IDStarAlgorithm {
    private ValueEval minimumValue;

    @Override // org.apache.poi.ss.formula.functions.IDStarAlgorithm
    public boolean processMatch(ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            if (this.minimumValue == null) {
                this.minimumValue = eval;
                return true;
            }
            double currentValue = ((NumericValueEval) eval).getNumberValue();
            double oldValue = ((NumericValueEval) this.minimumValue).getNumberValue();
            if (currentValue < oldValue) {
                this.minimumValue = eval;
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // org.apache.poi.ss.formula.functions.IDStarAlgorithm
    public ValueEval getResult() {
        ValueEval valueEval = this.minimumValue;
        if (valueEval == null) {
            return NumberEval.ZERO;
        }
        return valueEval;
    }
}
