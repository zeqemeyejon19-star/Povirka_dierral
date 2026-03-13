package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.Function;

/* JADX INFO: loaded from: classes.dex */
public final class IntersectionEval extends Fixed2ArgFunction {
    public static final Function instance = new IntersectionEval();

    private IntersectionEval() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            AreaEval reA = evaluateRef(arg0);
            AreaEval reB = evaluateRef(arg1);
            AreaEval result = resolveRange(reA, reB);
            if (result == null) {
                return ErrorEval.NULL_INTERSECTION;
            }
            return result;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static AreaEval resolveRange(AreaEval aeA, AreaEval aeB) {
        int aeBfc;
        int aeBlr;
        int aeBfr;
        int aeAlr;
        int aeAfr = aeA.getFirstRow();
        int aeAfc = aeA.getFirstColumn();
        int aeBlc = aeB.getLastColumn();
        if (aeAfc > aeBlc || (aeBfc = aeB.getFirstColumn()) > aeA.getLastColumn() || aeAfr > (aeBlr = aeB.getLastRow()) || (aeBfr = aeB.getFirstRow()) > (aeAlr = aeA.getLastRow())) {
            return null;
        }
        int top = Math.max(aeAfr, aeBfr);
        int bottom = Math.min(aeAlr, aeBlr);
        int left = Math.max(aeAfc, aeBfc);
        int right = Math.min(aeA.getLastColumn(), aeBlc);
        return aeA.offset(top - aeAfr, bottom - aeAfr, left - aeAfc, right - aeAfc);
    }

    private static AreaEval evaluateRef(ValueEval arg) throws EvaluationException {
        if (arg instanceof AreaEval) {
            return (AreaEval) arg;
        }
        if (arg instanceof RefEval) {
            return ((RefEval) arg).offset(0, 0, 0, 0);
        }
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) arg);
        }
        throw new IllegalArgumentException("Unexpected ref arg class (" + arg.getClass().getName() + ")");
    }
}
