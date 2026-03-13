package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.functions.NumericFunction;

/* JADX INFO: loaded from: classes.dex */
public final class Odd extends NumericFunction.OneArg {
    private static final long PARITY_MASK = -2;

    @Override // org.apache.poi.ss.formula.functions.NumericFunction.OneArg
    protected double evaluate(double d) {
        if (d == 0.0d) {
            return 1.0d;
        }
        return d > 0.0d ? calcOdd(d) : -calcOdd(-d);
    }

    private static long calcOdd(double d) {
        double dpm1 = 1.0d + d;
        long x = ((long) dpm1) & PARITY_MASK;
        return Double.compare((double) x, dpm1) == 0 ? x - 1 : x + 1;
    }
}
