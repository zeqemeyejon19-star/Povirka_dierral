package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;

/* JADX INFO: loaded from: classes.dex */
public class Mirr extends MultiOperandNumericFunction {
    public Mirr() {
        super(false, false);
    }

    @Override // org.apache.poi.ss.formula.functions.MultiOperandNumericFunction
    protected int getMaxNumOperands() {
        return 3;
    }

    @Override // org.apache.poi.ss.formula.functions.MultiOperandNumericFunction
    protected double evaluate(double[] values) throws EvaluationException {
        double financeRate = values[values.length - 1];
        double reinvestRate = values[values.length - 2];
        double[] mirrValues = new double[values.length - 2];
        System.arraycopy(values, 0, mirrValues, 0, mirrValues.length);
        boolean mirrValuesAreAllNegatives = true;
        int len$ = mirrValues.length;
        for (int i$ = 0; i$ < len$; i$++) {
            double mirrValue = mirrValues[i$];
            mirrValuesAreAllNegatives &= mirrValue < 0.0d;
        }
        if (mirrValuesAreAllNegatives) {
            return -1.0d;
        }
        boolean mirrValuesAreAllPositives = true;
        int len$2 = mirrValues.length;
        for (int i$2 = 0; i$2 < len$2; i$2++) {
            double mirrValue2 = mirrValues[i$2];
            mirrValuesAreAllPositives &= mirrValue2 > 0.0d;
        }
        if (mirrValuesAreAllPositives) {
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
        return mirr(mirrValues, financeRate, reinvestRate);
    }

    private static double mirr(double[] in, double financeRate, double reinvestRate) {
        double d;
        double d2;
        double value;
        double value2 = 0.0d;
        int numOfYears = in.length - 1;
        double pv = 0.0d;
        double fv = 0.0d;
        int indexN = 0;
        int len$ = in.length;
        int i$ = 0;
        while (true) {
            d = 1.0d;
            d2 = 0.0d;
            if (i$ >= len$) {
                break;
            }
            double anIn = in[i$];
            if (anIn >= 0.0d) {
                value = value2;
            } else {
                value = value2;
                double value3 = indexN;
                pv += anIn / Math.pow((financeRate + 1.0d) + reinvestRate, value3);
                indexN++;
            }
            i$++;
            value2 = value;
        }
        double value4 = value2;
        int len$2 = in.length;
        int i$2 = 0;
        while (i$2 < len$2) {
            double anIn2 = in[i$2];
            if (anIn2 > d2) {
                fv += Math.pow(financeRate + d, numOfYears - indexN) * anIn2;
                indexN++;
            }
            i$2++;
            d = 1.0d;
            d2 = 0.0d;
        }
        if (fv != 0.0d && pv != 0.0d) {
            double value5 = Math.pow((-fv) / pv, 1.0d / ((double) numOfYears)) - 1.0d;
            return value5;
        }
        return value4;
    }
}
