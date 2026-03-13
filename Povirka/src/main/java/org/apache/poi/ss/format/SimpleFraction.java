package org.apache.poi.ss.format;

/* JADX INFO: loaded from: classes.dex */
public class SimpleFraction {
    private final int denominator;
    private final int numerator;

    public static SimpleFraction buildFractionExactDenominator(double val, int exactDenom) {
        int num = (int) Math.round(((double) exactDenom) * val);
        return new SimpleFraction(num, exactDenom);
    }

    public static SimpleFraction buildFractionMaxDenominator(double value, int maxDenominator) {
        return buildFractionMaxDenominator(value, 0.0d, maxDenominator, 100);
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x0110 A[LOOP:0: B:9:0x004b->B:43:0x0110, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00cc A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.apache.poi.ss.format.SimpleFraction buildFractionMaxDenominator(double r42, double r44, int r46, int r47) {
        /*
            Method dump skipped, instruction units count: 395
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.format.SimpleFraction.buildFractionMaxDenominator(double, double, int, int):org.apache.poi.ss.format.SimpleFraction");
    }

    public SimpleFraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public int getDenominator() {
        return this.denominator;
    }

    public int getNumerator() {
        return this.numerator;
    }
}
