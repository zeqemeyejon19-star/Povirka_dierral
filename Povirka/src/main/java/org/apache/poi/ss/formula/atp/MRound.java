package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.functions.NumericFunction;

/* JADX INFO: loaded from: classes.dex */
final class MRound implements FreeRefFunction {
    public static final FreeRefFunction instance = new MRound();

    private MRound() {
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        EvaluationException e;
        double result;
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double number = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            try {
                double multiple = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
                if (multiple == 0.0d) {
                    result = 0.0d;
                } else {
                    try {
                        if (number * multiple < 0.0d) {
                            throw new EvaluationException(ErrorEval.NUM_ERROR);
                        }
                        result = Math.round(number / multiple) * multiple;
                    } catch (EvaluationException e2) {
                        e = e2;
                        return e.getErrorEval();
                    }
                }
                try {
                    NumericFunction.checkValue(result);
                    return new NumberEval(result);
                } catch (EvaluationException e3) {
                    e = e3;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e4) {
                e = e4;
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e5) {
            e = e5;
        }
    }
}
