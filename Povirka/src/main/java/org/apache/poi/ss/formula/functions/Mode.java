package org.apache.poi.ss.formula.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Mode implements Function {
    public static double evaluate(double[] v) throws EvaluationException {
        if (v.length < 2) {
            throw new EvaluationException(ErrorEval.NA);
        }
        int[] counts = new int[v.length];
        Arrays.fill(counts, 1);
        int iSize = v.length;
        for (int i = 0; i < iSize; i++) {
            int jSize = v.length;
            for (int j = i + 1; j < jSize; j++) {
                if (v[i] == v[j]) {
                    counts[i] = counts[i] + 1;
                }
            }
        }
        double maxv = 0.0d;
        int maxc = 0;
        int iSize2 = counts.length;
        for (int i2 = 0; i2 < iSize2; i2++) {
            if (counts[i2] > maxc) {
                maxv = v[i2];
                maxc = counts[i2];
            }
        }
        if (maxc > 1) {
            return maxv;
        }
        throw new EvaluationException(ErrorEval.NA);
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            List<Double> temp = new ArrayList<>();
            for (ValueEval valueEval : args) {
                collectValues(valueEval, temp);
            }
            int i = temp.size();
            double[] values = new double[i];
            for (int i2 = 0; i2 < values.length; i2++) {
                values[i2] = temp.get(i2).doubleValue();
            }
            double result = evaluate(values);
            return new NumberEval(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static void collectValues(ValueEval arg, List<Double> temp) throws EvaluationException {
        if (arg instanceof TwoDEval) {
            TwoDEval ae = (TwoDEval) arg;
            int width = ae.getWidth();
            int height = ae.getHeight();
            for (int rrIx = 0; rrIx < height; rrIx++) {
                for (int rcIx = 0; rcIx < width; rcIx++) {
                    ValueEval ve1 = ae.getValue(rrIx, rcIx);
                    collectValue(ve1, temp, false);
                }
            }
            return;
        }
        if (arg instanceof RefEval) {
            RefEval re = (RefEval) arg;
            int firstSheetIndex = re.getFirstSheetIndex();
            int lastSheetIndex = re.getLastSheetIndex();
            for (int sIx = firstSheetIndex; sIx <= lastSheetIndex; sIx++) {
                collectValue(re.getInnerValueEval(sIx), temp, true);
            }
            return;
        }
        collectValue(arg, temp, true);
    }

    private static void collectValue(ValueEval arg, List<Double> temp, boolean mustBeNumber) throws EvaluationException {
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) arg);
        }
        if (arg == BlankEval.instance || (arg instanceof BoolEval) || (arg instanceof StringEval)) {
            if (mustBeNumber) {
                throw EvaluationException.invalidValue();
            }
        } else {
            if (arg instanceof NumberEval) {
                temp.add(new Double(((NumberEval) arg).getNumberValue()));
                return;
            }
            throw new RuntimeException("Unexpected value type (" + arg.getClass().getName() + ")");
        }
    }
}
