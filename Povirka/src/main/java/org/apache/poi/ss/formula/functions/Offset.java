package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Offset implements Function {
    private static final int LAST_VALID_COLUMN_INDEX = 255;
    private static final int LAST_VALID_ROW_INDEX = 65535;

    static final class LinearOffsetRange {
        private final int _length;
        private final int _offset;

        public LinearOffsetRange(int offset, int length) {
            if (length == 0) {
                throw new RuntimeException("length may not be zero");
            }
            this._offset = offset;
            this._length = length;
        }

        public short getFirstIndex() {
            return (short) this._offset;
        }

        public short getLastIndex() {
            return (short) ((this._offset + this._length) - 1);
        }

        public LinearOffsetRange normaliseAndTranslate(int translationAmount) {
            if (this._length > 0) {
                if (translationAmount == 0) {
                    return this;
                }
                return new LinearOffsetRange(this._offset + translationAmount, this._length);
            }
            int i = this._offset + translationAmount;
            int i2 = this._length;
            return new LinearOffsetRange(i + i2 + 1, -i2);
        }

        public boolean isOutOfBounds(int lowValidIx, int highValidIx) {
            return this._offset < lowValidIx || getLastIndex() > highValidIx;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(this._offset).append("...").append((int) getLastIndex());
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class BaseRef {
        private final AreaEval _areaEval;
        private final int _firstColumnIndex;
        private final int _firstRowIndex;
        private final int _height;
        private final RefEval _refEval;
        private final int _width;

        public BaseRef(RefEval re) {
            this._refEval = re;
            this._areaEval = null;
            this._firstRowIndex = re.getRow();
            this._firstColumnIndex = re.getColumn();
            this._height = 1;
            this._width = 1;
        }

        public BaseRef(AreaEval ae) {
            this._refEval = null;
            this._areaEval = ae;
            this._firstRowIndex = ae.getFirstRow();
            this._firstColumnIndex = ae.getFirstColumn();
            this._height = (ae.getLastRow() - ae.getFirstRow()) + 1;
            this._width = (ae.getLastColumn() - ae.getFirstColumn()) + 1;
        }

        public int getWidth() {
            return this._width;
        }

        public int getHeight() {
            return this._height;
        }

        public int getFirstRowIndex() {
            return this._firstRowIndex;
        }

        public int getFirstColumnIndex() {
            return this._firstColumnIndex;
        }

        public AreaEval offset(int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
            RefEval refEval = this._refEval;
            if (refEval == null) {
                return this._areaEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
            }
            return refEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0040 A[Catch: EvaluationException -> 0x005e, TryCatch #0 {EvaluationException -> 0x005e, blocks: (B:8:0x000a, B:21:0x004c, B:23:0x005b, B:12:0x002d, B:14:0x0033, B:15:0x003a, B:17:0x0040), top: B:30:0x000a }] */
    @Override // org.apache.poi.ss.formula.functions.Function
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.apache.poi.ss.formula.eval.ValueEval evaluate(org.apache.poi.ss.formula.eval.ValueEval[] r10, int r11, int r12) {
        /*
            r9 = this;
            int r0 = r10.length
            r1 = 3
            if (r0 < r1) goto L64
            int r0 = r10.length
            r2 = 5
            if (r0 <= r2) goto L9
            goto L64
        L9:
            r0 = 0
            r0 = r10[r0]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            org.apache.poi.ss.formula.functions.Offset$BaseRef r0 = evaluateBaseRef(r0)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r3 = 1
            r3 = r10[r3]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r3 = evaluateIntArg(r3, r11, r12)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r4 = 2
            r4 = r10[r4]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r4 = evaluateIntArg(r4, r11, r12)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r5 = r0.getHeight()     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r6 = r0.getWidth()     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r7 = r10.length     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r8 = 4
            if (r7 == r8) goto L3a
            if (r7 == r2) goto L2d
            goto L47
        L2d:
            r2 = r10[r8]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            boolean r2 = r2 instanceof org.apache.poi.ss.formula.eval.MissingArgEval     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            if (r2 != 0) goto L3a
            r2 = r10[r8]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r2 = evaluateIntArg(r2, r11, r12)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r6 = r2
        L3a:
            r2 = r10[r1]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            boolean r2 = r2 instanceof org.apache.poi.ss.formula.eval.MissingArgEval     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            if (r2 != 0) goto L47
            r1 = r10[r1]     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            int r1 = evaluateIntArg(r1, r11, r12)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r5 = r1
        L47:
            if (r5 == 0) goto L5b
            if (r6 != 0) goto L4c
            goto L5b
        L4c:
            org.apache.poi.ss.formula.functions.Offset$LinearOffsetRange r1 = new org.apache.poi.ss.formula.functions.Offset$LinearOffsetRange     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r1.<init>(r3, r5)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            org.apache.poi.ss.formula.functions.Offset$LinearOffsetRange r2 = new org.apache.poi.ss.formula.functions.Offset$LinearOffsetRange     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            r2.<init>(r4, r6)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            org.apache.poi.ss.formula.eval.AreaEval r7 = createOffset(r0, r1, r2)     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            return r7
        L5b:
            org.apache.poi.ss.formula.eval.ErrorEval r1 = org.apache.poi.ss.formula.eval.ErrorEval.REF_INVALID     // Catch: org.apache.poi.ss.formula.eval.EvaluationException -> L5e
            return r1
        L5e:
            r0 = move-exception
            org.apache.poi.ss.formula.eval.ErrorEval r1 = r0.getErrorEval()
            return r1
        L64:
            org.apache.poi.ss.formula.eval.ErrorEval r0 = org.apache.poi.ss.formula.eval.ErrorEval.VALUE_INVALID
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.formula.functions.Offset.evaluate(org.apache.poi.ss.formula.eval.ValueEval[], int, int):org.apache.poi.ss.formula.eval.ValueEval");
    }

    private static AreaEval createOffset(BaseRef baseRef, LinearOffsetRange orRow, LinearOffsetRange orCol) throws EvaluationException {
        LinearOffsetRange absRows = orRow.normaliseAndTranslate(baseRef.getFirstRowIndex());
        LinearOffsetRange absCols = orCol.normaliseAndTranslate(baseRef.getFirstColumnIndex());
        if (!absRows.isOutOfBounds(0, 65535)) {
            if (absCols.isOutOfBounds(0, 255)) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            return baseRef.offset(orRow.getFirstIndex(), orRow.getLastIndex(), orCol.getFirstIndex(), orCol.getLastIndex());
        }
        throw new EvaluationException(ErrorEval.REF_INVALID);
    }

    private static BaseRef evaluateBaseRef(ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            return new BaseRef((RefEval) eval);
        }
        if (eval instanceof AreaEval) {
            return new BaseRef((AreaEval) eval);
        }
        if (eval instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) eval);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }

    static int evaluateIntArg(ValueEval eval, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(eval, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }
}
