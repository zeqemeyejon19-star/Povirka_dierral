package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public abstract class NumericFunction implements Function {
    static final double ZERO = 0.0d;
    static final double TEN = 10.0d;
    static final double LOG_10_TO_BASE_e = Math.log(TEN);
    public static final Function ABS = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.1
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.abs(d);
        }
    };
    public static final Function ACOS = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.2
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.acos(d);
        }
    };
    public static final Function ACOSH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.3
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.acosh(d);
        }
    };
    public static final Function ASIN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.4
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.asin(d);
        }
    };
    public static final Function ASINH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.5
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.asinh(d);
        }
    };
    public static final Function ATAN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.6
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.atan(d);
        }
    };
    public static final Function ATANH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.7
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.atanh(d);
        }
    };
    public static final Function COS = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.8
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.cos(d);
        }
    };
    public static final Function COSH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.9
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.cosh(d);
        }
    };
    public static final Function DEGREES = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.10
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.toDegrees(d);
        }
    };
    static final NumberEval DOLLAR_ARG2_DEFAULT = new NumberEval(2.0d);
    public static final Function DOLLAR = new Var1or2ArgFunction() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.11
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, NumericFunction.DOLLAR_ARG2_DEFAULT);
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double val;
            try {
                val = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                int nPlaces = (int) d1;
                if (nPlaces > 127) {
                    return ErrorEval.VALUE_INVALID;
                }
                return new NumberEval(val);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    };
    public static final Function EXP = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.12
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.pow(2.718281828459045d, d);
        }
    };
    public static final Function FACT = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.13
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.factorial((int) d);
        }
    };
    public static final Function INT = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.14
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.round(d - 0.5d);
        }
    };
    public static final Function LN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.15
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.log(d);
        }
    };
    public static final Function LOG10 = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.16
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.log(d) / NumericFunction.LOG_10_TO_BASE_e;
        }
    };
    public static final Function RADIANS = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.17
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.toRadians(d);
        }
    };
    public static final Function SIGN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.18
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.sign(d);
        }
    };
    public static final Function SIN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.19
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.sin(d);
        }
    };
    public static final Function SINH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.20
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.sinh(d);
        }
    };
    public static final Function SQRT = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.21
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.sqrt(d);
        }
    };
    public static final Function TAN = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.22
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return Math.tan(d);
        }
    };
    public static final Function TANH = new OneArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.23
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
        protected double evaluate(double d) {
            return MathX.tanh(d);
        }
    };
    public static final Function ATAN2 = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.24
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d0 == 0.0d && d1 == 0.0d) {
                throw new EvaluationException(ErrorEval.DIV_ZERO);
            }
            return Math.atan2(d1, d0);
        }
    };
    public static final Function CEILING = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.25
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) {
            return MathX.ceiling(d0, d1);
        }
    };
    public static final Function COMBIN = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.26
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d0 > 2.147483647E9d || d1 > 2.147483647E9d) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
            return MathX.nChooseK((int) d0, (int) d1);
        }
    };
    public static final Function FLOOR = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.27
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d1 != 0.0d) {
                return MathX.floor(d0, d1);
            }
            if (d0 == 0.0d) {
                return 0.0d;
            }
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
    };
    public static final Function MOD = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.28
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) throws EvaluationException {
            if (d1 == 0.0d) {
                throw new EvaluationException(ErrorEval.DIV_ZERO);
            }
            return MathX.mod(d0, d1);
        }
    };
    public static final Function POWER = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.29
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) {
            return Math.pow(d0, d1);
        }
    };
    public static final Function ROUND = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.30
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) {
            return MathX.round(d0, (int) d1);
        }
    };
    public static final Function ROUNDDOWN = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.31
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) {
            return MathX.roundDown(d0, (int) d1);
        }
    };
    public static final Function ROUNDUP = new TwoArg() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.32
        @Override // org.apache.poi.ss.formula.functions.NumericFunction.TwoArg
        protected double evaluate(double d0, double d1) {
            return MathX.roundUp(d0, (int) d1);
        }
    };
    static final NumberEval TRUNC_ARG2_DEFAULT = new NumberEval(0.0d);
    public static final Function TRUNC = new Var1or2ArgFunction() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.33
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, NumericFunction.TRUNC_ARG2_DEFAULT);
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double result;
            try {
                double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                double multi = Math.pow(NumericFunction.TEN, d1);
                result = d0 < 0.0d ? (-Math.floor((-d0) * multi)) / multi : Math.floor(d0 * multi) / multi;
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    };
    public static final Function LOG = new Log();
    static final NumberEval PI_EVAL = new NumberEval(3.141592653589793d);
    public static final Function PI = new Fixed0ArgFunction() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.34
        @Override // org.apache.poi.ss.formula.functions.Function0Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return NumericFunction.PI_EVAL;
        }
    };
    public static final Function RAND = new Fixed0ArgFunction() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.35
        @Override // org.apache.poi.ss.formula.functions.Function0Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return new NumberEval(Math.random());
        }
    };
    public static final Function POISSON = new Fixed3ArgFunction() { // from class: org.apache.poi.ss.formula.functions.NumericFunction.36
        private static final double DEFAULT_RETURN_RESULT = 1.0d;
        private final long[] FACTORIALS = {1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};

        private boolean isDefaultResult(double x, double mean) {
            if (x == 0.0d && mean == 0.0d) {
                return true;
            }
            return false;
        }

        private boolean checkArgument(double aDouble) throws EvaluationException {
            NumericFunction.checkValue(aDouble);
            if (aDouble < 0.0d) {
                throw new EvaluationException(ErrorEval.NUM_ERROR);
            }
            return true;
        }

        private double probability(int k, double lambda) {
            return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
        }

        private double cumulativeProbability(int x, double lambda) {
            double result = 0.0d;
            for (int k = 0; k <= x; k++) {
                result += probability(k, lambda);
            }
            return result;
        }

        public long factorial(int n) {
            if (n < 0 || n > 20) {
                throw new IllegalArgumentException("Valid argument should be in the range [0..20]");
            }
            return this.FACTORIALS[n];
        }

        @Override // org.apache.poi.ss.formula.functions.Function3Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            double result;
            boolean cumulative = ((BoolEval) arg2).getBooleanValue();
            try {
                double x = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                double mean = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                if (isDefaultResult(x, mean)) {
                    return new NumberEval(DEFAULT_RETURN_RESULT);
                }
                checkArgument(x);
                checkArgument(mean);
                if (cumulative) {
                    result = cumulativeProbability((int) x, mean);
                } else {
                    result = probability((int) x, mean);
                }
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
    };

    protected abstract double eval(ValueEval[] valueEvalArr, int i, int i2) throws EvaluationException;

    protected static final double singleOperandEvaluate(ValueEval arg, int srcRowIndex, int srcColumnIndex) throws EvaluationException {
        if (arg == null) {
            throw new IllegalArgumentException("arg must not be null");
        }
        ValueEval ve = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        double result = OperandResolver.coerceValueToDouble(ve);
        checkValue(result);
        return result;
    }

    public static final void checkValue(double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            double result = eval(args, srcCellRow, srcCellCol);
            try {
                checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e) {
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e2) {
            e = e2;
        }
    }

    public static abstract class OneArg extends Fixed1ArgFunction {
        protected abstract double evaluate(double d) throws EvaluationException;

        protected OneArg() {
        }

        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            double result;
            try {
                double d = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                result = evaluate(d);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }

        protected final double eval(ValueEval[] args, int srcCellRow, int srcCellCol) throws EvaluationException {
            if (args.length != 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            double d = NumericFunction.singleOperandEvaluate(args[0], srcCellRow, srcCellCol);
            return evaluate(d);
        }
    }

    public static abstract class TwoArg extends Fixed2ArgFunction {
        protected abstract double evaluate(double d, double d2) throws EvaluationException;

        protected TwoArg() {
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double result;
            try {
                double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                result = evaluate(d0, d1);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    }

    private static final class Log extends Var1or2ArgFunction {
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            EvaluationException e;
            double result;
            try {
                double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                result = Math.log(d0) / NumericFunction.LOG_10_TO_BASE_e;
            } catch (EvaluationException e2) {
                e = e2;
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double d0;
            double result;
            try {
                d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                double logE = Math.log(d0);
                if (Double.compare(d1, 2.718281828459045d) == 0) {
                    result = logE;
                } else {
                    double result2 = Math.log(d1);
                    result = logE / result2;
                }
                try {
                    NumericFunction.checkValue(result);
                    return new NumberEval(result);
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        }
    }
}
