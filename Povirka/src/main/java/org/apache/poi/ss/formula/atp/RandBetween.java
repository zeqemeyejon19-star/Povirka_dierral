package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

/* JADX INFO: loaded from: classes.dex */
final class RandBetween implements FreeRefFunction {
    public static final FreeRefFunction instance = new RandBetween();

    private RandBetween() {
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        double bottom;
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            bottom = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
        } catch (EvaluationException e) {
            e = e;
        }
        try {
            double top = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
            if (bottom > top) {
                try {
                    return ErrorEval.NUM_ERROR;
                } catch (EvaluationException e2) {
                    return ErrorEval.VALUE_INVALID;
                }
            }
            double bottom2 = Math.ceil(bottom);
            double top2 = Math.floor(top);
            if (bottom2 > top2) {
                top2 = bottom2;
            }
            return new NumberEval(((double) ((int) (Math.random() * ((top2 - bottom2) + 1.0d)))) + bottom2);
        } catch (EvaluationException e3) {
            e = e3;
            return ErrorEval.VALUE_INVALID;
        }
    }
}
