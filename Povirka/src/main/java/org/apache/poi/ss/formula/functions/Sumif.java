package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.CountUtils;

/* JADX INFO: loaded from: classes.dex */
public final class Sumif extends Var2or3ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            AreaEval aeRange = convertRangeArg(arg0);
            return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeRange);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        AreaEval aeRange;
        try {
            aeRange = convertRangeArg(arg0);
        } catch (EvaluationException e) {
            e = e;
        }
        try {
            AreaEval aeSum = createSumRange(arg2, aeRange);
            return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeSum);
        } catch (EvaluationException e2) {
            e = e2;
            return e.getErrorEval();
        }
    }

    private static ValueEval eval(int srcRowIndex, int srcColumnIndex, ValueEval arg1, AreaEval aeRange, AreaEval aeSum) {
        CountUtils.I_MatchPredicate mp = Countif.createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        if (mp == null) {
            return NumberEval.ZERO;
        }
        double result = sumMatchingCells(aeRange, mp, aeSum);
        return new NumberEval(result);
    }

    private static double sumMatchingCells(AreaEval aeRange, CountUtils.I_MatchPredicate mp, AreaEval aeSum) {
        int height = aeRange.getHeight();
        int width = aeRange.getWidth();
        double result = 0.0d;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                result += accumulate(aeRange, mp, aeSum, r, c);
            }
        }
        return result;
    }

    private static double accumulate(AreaEval aeRange, CountUtils.I_MatchPredicate mp, AreaEval aeSum, int relRowIndex, int relColIndex) {
        if (!mp.matches(aeRange.getRelativeValue(relRowIndex, relColIndex))) {
            return 0.0d;
        }
        ValueEval addend = aeSum.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval) addend).getNumberValue();
        }
        return 0.0d;
    }

    private static AreaEval createSumRange(ValueEval eval, AreaEval aeRange) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return ((AreaEval) eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        if (eval instanceof RefEval) {
            return ((RefEval) eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    private static AreaEval convertRangeArg(ValueEval eval) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return (AreaEval) eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval) eval).offset(0, 0, 0, 0);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
}
