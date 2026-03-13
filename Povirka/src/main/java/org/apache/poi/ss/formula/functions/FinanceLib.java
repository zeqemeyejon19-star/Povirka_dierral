package org.apache.poi.ss.formula.functions;

/* JADX INFO: loaded from: classes.dex */
public final class FinanceLib {
    private FinanceLib() {
    }

    public static double fv(double r, double n, double y, double p, boolean t) {
        if (r == 0.0d) {
            double retval = (p + (n * y)) * (-1.0d);
            return retval;
        }
        double r1 = r + 1.0d;
        double retval2 = ((((1.0d - Math.pow(r1, n)) * (t ? r1 : 1.0d)) * y) / r) - (Math.pow(r1, n) * p);
        return retval2;
    }

    public static double pv(double r, double n, double y, double f, boolean t) {
        if (r == 0.0d) {
            double retval = ((n * y) + f) * (-1.0d);
            return retval;
        }
        double r1 = r + 1.0d;
        double retval2 = (((((1.0d - Math.pow(r1, n)) / r) * (t ? r1 : 1.0d)) * y) - f) / Math.pow(r1, n);
        return retval2;
    }

    public static double npv(double r, double[] cfs) {
        double npv = 0.0d;
        double r1 = 1.0d + r;
        double trate = r1;
        for (double d : cfs) {
            npv += d / trate;
            trate *= r1;
        }
        return npv;
    }

    public static double pmt(double r, double n, double p, double f, boolean t) {
        if (r == 0.0d) {
            double retval = ((f + p) * (-1.0d)) / n;
            return retval;
        }
        double r1 = r + 1.0d;
        double retval2 = ((f + (Math.pow(r1, n) * p)) * r) / ((t ? r1 : 1.0d) * (1.0d - Math.pow(r1, n)));
        return retval2;
    }

    public static double nper(double r, double y, double p, double f, boolean t) {
        if (r == 0.0d) {
            double retval = ((f + p) * (-1.0d)) / y;
            return retval;
        }
        double r1 = r + 1.0d;
        double retval2 = t ? r1 : 1.0d;
        double ryr = (retval2 * y) / r;
        double a1 = Math.log(ryr - f < 0.0d ? f - ryr : ryr - f);
        double a2 = Math.log(ryr - f < 0.0d ? (-p) - ryr : p + ryr);
        double a3 = Math.log(r1);
        double retval3 = (a1 - a2) / a3;
        return retval3;
    }
}
