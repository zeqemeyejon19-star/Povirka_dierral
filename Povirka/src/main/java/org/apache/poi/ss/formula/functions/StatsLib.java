package org.apache.poi.ss.formula.functions;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
final class StatsLib {
    private StatsLib() {
    }

    public static double avedev(double[] v) {
        double s = 0.0d;
        for (double d : v) {
            s += d;
        }
        int i = v.length;
        double m = s / ((double) i);
        double s2 = 0.0d;
        for (double d2 : v) {
            s2 += Math.abs(d2 - m);
        }
        int i2 = v.length;
        double r = s2 / ((double) i2);
        return r;
    }

    public static double stdev(double[] v) {
        if (v == null || v.length <= 1) {
            return Double.NaN;
        }
        double r = Math.sqrt(devsq(v) / ((double) (v.length - 1)));
        return r;
    }

    public static double var(double[] v) {
        if (v == null || v.length <= 1) {
            return Double.NaN;
        }
        double r = devsq(v) / ((double) (v.length - 1));
        return r;
    }

    public static double varp(double[] v) {
        if (v == null || v.length <= 1) {
            return Double.NaN;
        }
        double r = devsq(v) / ((double) v.length);
        return r;
    }

    public static double median(double[] v) {
        if (v == null || v.length < 1) {
            return Double.NaN;
        }
        int n = v.length;
        Arrays.sort(v);
        double r = n % 2 == 0 ? (v[n / 2] + v[(n / 2) - 1]) / 2.0d : v[n / 2];
        return r;
    }

    public static double devsq(double[] v) {
        if (v == null || v.length < 1) {
            return Double.NaN;
        }
        double s = 0.0d;
        int n = v.length;
        for (double d : v) {
            s += d;
        }
        double m = s / ((double) n);
        double s2 = 0.0d;
        for (int i = 0; i < n; i++) {
            s2 += (v[i] - m) * (v[i] - m);
        }
        double r = n == 1 ? 0.0d : s2;
        return r;
    }

    public static double kthLargest(double[] v, int k) {
        int index = k - 1;
        if (v == null || v.length <= index || index < 0) {
            return Double.NaN;
        }
        Arrays.sort(v);
        double r = v[(v.length - index) - 1];
        return r;
    }

    public static double kthSmallest(double[] v, int k) {
        int index = k - 1;
        if (v == null || v.length <= index || index < 0) {
            return Double.NaN;
        }
        Arrays.sort(v);
        double r = v[index];
        return r;
    }
}
