package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;
import org.apache.poi.ss.formula.functions.Function;

/* JADX INFO: loaded from: classes.dex */
public final class UnaryMinusEval extends Fixed1ArgFunction {
    public static final Function instance = new UnaryMinusEval();

    private UnaryMinusEval() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            double d = OperandResolver.coerceValueToDouble(ve);
            if (d == 0.0d) {
                return NumberEval.ZERO;
            }
            return new NumberEval(-d);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
