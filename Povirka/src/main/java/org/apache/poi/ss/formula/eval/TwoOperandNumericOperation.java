package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.Function;

/* JADX INFO: loaded from: classes.dex */
public abstract class TwoOperandNumericOperation extends Fixed2ArgFunction {
    public static final Function AddEval = new TwoOperandNumericOperation() { // from class: org.apache.poi.ss.formula.eval.TwoOperandNumericOperation.1
        @Override // org.apache.poi.ss.formula.eval.TwoOperandNumericOperation
        protected double evaluate(double d0, double d1) {
            return d0 + d1;
        }
    };
    public static final Function DivideEval = new TwoOperandNumericOperation() { // from class: org.apache.poi.ss.formula.eval.TwoOperandNumericOperation.2
        @Override // org.apache.poi.ss.formula.eval.TwoOperandNumericOperation
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d1 == 0.0d) {
                throw new EvaluationException(ErrorEval.DIV_ZERO);
            }
            return d0 / d1;
        }
    };
    public static final Function MultiplyEval = new TwoOperandNumericOperation() { // from class: org.apache.poi.ss.formula.eval.TwoOperandNumericOperation.3
        @Override // org.apache.poi.ss.formula.eval.TwoOperandNumericOperation
        protected double evaluate(double d0, double d1) {
            return d0 * d1;
        }
    };
    public static final Function PowerEval = new TwoOperandNumericOperation() { // from class: org.apache.poi.ss.formula.eval.TwoOperandNumericOperation.4
        @Override // org.apache.poi.ss.formula.eval.TwoOperandNumericOperation
        protected double evaluate(double d0, double d1) {
            return Math.pow(d0, d1);
        }
    };
    public static final Function SubtractEval = new SubtractEvalClass();

    protected abstract double evaluate(double d, double d2) throws EvaluationException;

    protected final double singleOperandEvaluate(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        EvaluationException e;
        try {
            double d0 = singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            double result = evaluate(d0, d1);
            if (result == 0.0d) {
                try {
                    if (!(this instanceof SubtractEvalClass)) {
                        return NumberEval.ZERO;
                    }
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            }
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(result);
        } catch (EvaluationException e3) {
            e = e3;
        }
    }

    private static final class SubtractEvalClass extends TwoOperandNumericOperation {
        @Override // org.apache.poi.ss.formula.eval.TwoOperandNumericOperation
        protected double evaluate(double d0, double d1) {
            return d0 - d1;
        }
    }
}
