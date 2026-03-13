package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public abstract class FinanceFunction implements Function3Arg, Function4Arg {
    private static final ValueEval DEFAULT_ARG3 = NumberEval.ZERO;
    private static final ValueEval DEFAULT_ARG4 = BoolEval.FALSE;
    public static final Function FV = new FinanceFunction() { // from class: org.apache.poi.ss.formula.functions.FinanceFunction.1
        @Override // org.apache.poi.ss.formula.functions.FinanceFunction
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.fv(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function NPER = new FinanceFunction() { // from class: org.apache.poi.ss.formula.functions.FinanceFunction.2
        @Override // org.apache.poi.ss.formula.functions.FinanceFunction
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.nper(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PMT = new FinanceFunction() { // from class: org.apache.poi.ss.formula.functions.FinanceFunction.3
        @Override // org.apache.poi.ss.formula.functions.FinanceFunction
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pmt(rate, arg1, arg2, arg3, type);
        }
    };
    public static final Function PV = new FinanceFunction() { // from class: org.apache.poi.ss.formula.functions.FinanceFunction.4
        @Override // org.apache.poi.ss.formula.functions.FinanceFunction
        protected double evaluate(double rate, double arg1, double arg2, double arg3, boolean type) {
            return FinanceLib.pv(rate, arg1, arg2, arg3, type);
        }
    };

    protected abstract double evaluate(double d, double d2, double d3, double d4, boolean z) throws EvaluationException;

    protected FinanceFunction() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, DEFAULT_ARG3);
    }

    @Override // org.apache.poi.ss.formula.functions.Function4Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, arg3, DEFAULT_ARG4);
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3, ValueEval arg4) {
        try {
            double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
            double d3 = NumericFunction.singleOperandEvaluate(arg3, srcRowIndex, srcColumnIndex);
            try {
                double d4 = NumericFunction.singleOperandEvaluate(arg4, srcRowIndex, srcColumnIndex);
                double result = evaluate(d0, d1, d2, d3, d4 != 0.0d);
                try {
                    NumericFunction.checkValue(result);
                    return new NumberEval(result);
                } catch (EvaluationException e) {
                    e = e;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        } catch (EvaluationException e3) {
            e = e3;
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], DEFAULT_ARG3, DEFAULT_ARG4);
        }
        if (length == 4) {
            ValueEval arg3 = args[3];
            if (arg3 == MissingArgEval.instance) {
                arg3 = DEFAULT_ARG3;
            }
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], arg3, DEFAULT_ARG4);
        }
        if (length == 5) {
            ValueEval arg32 = args[3];
            if (arg32 == MissingArgEval.instance) {
                arg32 = DEFAULT_ARG3;
            }
            ValueEval arg4 = args[4];
            if (arg4 == MissingArgEval.instance) {
                arg4 = DEFAULT_ARG4;
            }
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], arg32, arg4);
        }
        return ErrorEval.VALUE_INVALID;
    }

    protected double evaluate(double[] ds) throws EvaluationException {
        double arg3 = 0.0d;
        double arg4 = 0.0d;
        int length = ds.length;
        if (length != 3) {
            if (length != 4) {
                if (length == 5) {
                    arg4 = ds[4];
                } else {
                    throw new IllegalStateException("Wrong number of arguments");
                }
            }
            arg3 = ds[3];
        }
        return evaluate(ds[0], ds[1], ds[2], arg3, arg4 != 0.0d);
    }
}
