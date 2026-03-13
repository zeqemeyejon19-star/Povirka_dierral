package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.AggregateFunction;

/* JADX INFO: loaded from: classes.dex */
public final class Irr implements Function {
    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        double guess;
        if (args.length == 0 || args.length > 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double[] values = AggregateFunction.ValueCollector.collectValues(args[0]);
            if (args.length == 2) {
                guess = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            } else {
                guess = 0.1d;
            }
            double result = irr(values, guess);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public static double irr(double[] income) {
        return irr(income, 0.1d);
    }

    public static double irr(double[] values, double guess) {
        int maxIterationCount;
        double[] dArr = values;
        int maxIterationCount2 = 20;
        double x0 = guess;
        int i = 0;
        while (i < 20) {
            double factor = 1.0d + x0;
            int k = 0;
            double fValue = dArr[0];
            double fDerivative = 0.0d;
            double denominator = factor;
            while (true) {
                k++;
                maxIterationCount = maxIterationCount2;
                int maxIterationCount3 = dArr.length;
                if (k >= maxIterationCount3) {
                    break;
                }
                double value = dArr[k];
                fValue += value / denominator;
                denominator *= factor;
                fDerivative -= (((double) k) * value) / denominator;
                dArr = values;
                maxIterationCount2 = maxIterationCount;
            }
            double x1 = x0 - (fValue / fDerivative);
            if (Math.abs(x1 - x0) <= 1.0E-7d) {
                return x1;
            }
            x0 = x1;
            i++;
            dArr = values;
            maxIterationCount2 = maxIterationCount;
        }
        return Double.NaN;
    }
}
