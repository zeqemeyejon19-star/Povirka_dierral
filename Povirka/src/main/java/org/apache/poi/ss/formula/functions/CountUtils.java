package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
final class CountUtils {

    public interface I_MatchAreaPredicate extends I_MatchPredicate {
        boolean matches(TwoDEval twoDEval, int i, int i2);
    }

    public interface I_MatchPredicate {
        boolean matches(ValueEval valueEval);
    }

    private CountUtils() {
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x002c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static int countMatchingCellsInArea(org.apache.poi.ss.formula.ThreeDEval r11, org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate r12) {
        /*
            r0 = 0
            int r1 = r11.getFirstSheetIndex()
            int r2 = r11.getLastSheetIndex()
            r3 = r1
        La:
            if (r3 > r2) goto L3d
            int r4 = r11.getHeight()
            int r5 = r11.getWidth()
            r6 = 0
        L15:
            if (r6 >= r4) goto L3a
            r7 = 0
        L18:
            if (r7 >= r5) goto L37
            org.apache.poi.ss.formula.eval.ValueEval r8 = r11.getValue(r3, r6, r7)
            boolean r9 = r12 instanceof org.apache.poi.ss.formula.functions.CountUtils.I_MatchAreaPredicate
            if (r9 == 0) goto L2c
            r9 = r12
            org.apache.poi.ss.formula.functions.CountUtils$I_MatchAreaPredicate r9 = (org.apache.poi.ss.formula.functions.CountUtils.I_MatchAreaPredicate) r9
            boolean r10 = r9.matches(r11, r6, r7)
            if (r10 != 0) goto L2c
            goto L34
        L2c:
            boolean r9 = r12.matches(r8)
            if (r9 == 0) goto L34
            int r0 = r0 + 1
        L34:
            int r7 = r7 + 1
            goto L18
        L37:
            int r6 = r6 + 1
            goto L15
        L3a:
            int r3 = r3 + 1
            goto La
        L3d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.formula.functions.CountUtils.countMatchingCellsInArea(org.apache.poi.ss.formula.ThreeDEval, org.apache.poi.ss.formula.functions.CountUtils$I_MatchPredicate):int");
    }

    public static int countMatchingCellsInRef(RefEval refEval, I_MatchPredicate criteriaPredicate) {
        int result = 0;
        int firstSheetIndex = refEval.getFirstSheetIndex();
        int lastSheetIndex = refEval.getLastSheetIndex();
        for (int sIx = firstSheetIndex; sIx <= lastSheetIndex; sIx++) {
            ValueEval ve = refEval.getInnerValueEval(sIx);
            if (criteriaPredicate.matches(ve)) {
                result++;
            }
        }
        return result;
    }

    public static int countArg(ValueEval valueEval, I_MatchPredicate i_MatchPredicate) {
        if (valueEval == null) {
            throw new IllegalArgumentException("eval must not be null");
        }
        if (valueEval instanceof ThreeDEval) {
            return countMatchingCellsInArea((ThreeDEval) valueEval, i_MatchPredicate);
        }
        if (valueEval instanceof TwoDEval) {
            throw new IllegalArgumentException("Count requires 3D Evals, 2D ones aren't supported");
        }
        if (valueEval instanceof RefEval) {
            return countMatchingCellsInRef((RefEval) valueEval, i_MatchPredicate);
        }
        return i_MatchPredicate.matches(valueEval) ? 1 : 0;
    }
}
