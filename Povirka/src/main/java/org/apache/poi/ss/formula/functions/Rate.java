package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class Rate implements Function {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) Rate.class);

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        if (args.length < 3) {
            return ErrorEval.VALUE_INVALID;
        }
        double future_val = 0.0d;
        try {
            ValueEval v1 = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            ValueEval v2 = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
            ValueEval v3 = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
            ValueEval v4 = null;
            if (args.length >= 4) {
                v4 = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
            }
            ValueEval v5 = null;
            if (args.length >= 5) {
                v5 = OperandResolver.getSingleValue(args[4], srcRowIndex, srcColumnIndex);
            }
            ValueEval v6 = null;
            if (args.length >= 6) {
                v6 = OperandResolver.getSingleValue(args[5], srcRowIndex, srcColumnIndex);
            }
            double periods = OperandResolver.coerceValueToDouble(v1);
            try {
                double payment = OperandResolver.coerceValueToDouble(v2);
                try {
                    double present_val = OperandResolver.coerceValueToDouble(v3);
                    try {
                        if (args.length >= 4) {
                            future_val = OperandResolver.coerceValueToDouble(v4);
                        }
                        double type = args.length >= 5 ? OperandResolver.coerceValueToDouble(v5) : 0.0d;
                        double estimate = args.length >= 6 ? OperandResolver.coerceValueToDouble(v6) : 0.1d;
                        double rate = calculateRate(periods, payment, present_val, future_val, type, estimate);
                        try {
                            checkValue(rate);
                            return new NumberEval(rate);
                        } catch (EvaluationException e) {
                            e = e;
                            LOG.log(7, "Can't evaluate rate function", e);
                            return e.getErrorEval();
                        }
                    } catch (EvaluationException e2) {
                        e = e2;
                    }
                } catch (EvaluationException e3) {
                    e = e3;
                }
            } catch (EvaluationException e4) {
                e = e4;
            }
        } catch (EvaluationException e5) {
            e = e5;
        }
    }

    private double calculateRate(double nper, double pmt, double pv, double fv, double type, double guess) {
        double d;
        double d2;
        double d3;
        double f = 0.0d;
        double rate = guess;
        if (Math.abs(rate) < 1.0E-7d) {
            double d4 = (((nper * rate) + 1.0d) * pv) + (((rate * type) + 1.0d) * pmt * nper) + fv;
        } else {
            double y = rate + 1.0d;
            f = Math.exp(Math.log(y) * nper);
            double d5 = (pv * f) + (((1.0d / rate) + type) * pmt * (f - 1.0d)) + fv;
        }
        double y0 = pv + (pmt * nper) + fv;
        double y1 = (pv * f) + (((1.0d / rate) + type) * pmt * (f - 1.0d)) + fv;
        double x0 = 0.0d;
        double i = 0.0d;
        double x1 = rate;
        while (Math.abs(y0 - y1) > 1.0E-7d && i < 20) {
            rate = ((y1 * x0) - (y0 * x1)) / (y1 - y0);
            x0 = x1;
            x1 = rate;
            if (Math.abs(rate) < 1.0E-7d) {
                d = 1.0d;
                d2 = ((nper * rate) + 1.0d) * pv;
                d3 = ((rate * type) + 1.0d) * pmt * nper;
            } else {
                d = 1.0d;
                double f2 = Math.exp(Math.log(rate + 1.0d) * nper);
                d2 = pv * f2;
                d3 = ((1.0d / rate) + type) * pmt * (f2 - 1.0d);
            }
            double y2 = d2 + d3 + fv;
            y0 = y1;
            y1 = y2;
            i += d;
        }
        return rate;
    }

    static final void checkValue(double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }
}
