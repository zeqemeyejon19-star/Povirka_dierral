package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public abstract class MultiOperandNumericFunction implements Function {
    private final boolean _isBlankCounted;
    private final boolean _isReferenceBoolCounted;
    static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    private static final int DEFAULT_MAX_NUM_OPERANDS = SpreadsheetVersion.EXCEL2007.getMaxFunctionArgs();

    protected abstract double evaluate(double[] dArr) throws EvaluationException;

    protected MultiOperandNumericFunction(boolean isReferenceBoolCounted, boolean isBlankCounted) {
        this._isReferenceBoolCounted = isReferenceBoolCounted;
        this._isBlankCounted = isBlankCounted;
    }

    private static class DoubleList {
        private double[] _array = new double[8];
        private int _count = 0;

        public double[] toArray() {
            int i = this._count;
            if (i < 1) {
                return MultiOperandNumericFunction.EMPTY_DOUBLE_ARRAY;
            }
            double[] result = new double[i];
            System.arraycopy(this._array, 0, result, 0, i);
            return result;
        }

        private void ensureCapacity(int reqSize) {
            double[] dArr = this._array;
            if (reqSize > dArr.length) {
                int newSize = (reqSize * 3) / 2;
                double[] newArr = new double[newSize];
                System.arraycopy(dArr, 0, newArr, 0, this._count);
                this._array = newArr;
            }
        }

        public void add(double value) {
            ensureCapacity(this._count + 1);
            double[] dArr = this._array;
            int i = this._count;
            dArr[i] = value;
            this._count = i + 1;
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            double[] values = getNumberArray(args);
            double d = evaluate(values);
            if (Double.isNaN(d) || Double.isInfinite(d)) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(d);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    protected int getMaxNumOperands() {
        return DEFAULT_MAX_NUM_OPERANDS;
    }

    protected final double[] getNumberArray(ValueEval[] operands) throws EvaluationException {
        if (operands.length > getMaxNumOperands()) {
            throw EvaluationException.invalidValue();
        }
        DoubleList retval = new DoubleList();
        for (ValueEval valueEval : operands) {
            collectValues(valueEval, retval);
        }
        return retval.toArray();
    }

    public boolean isSubtotalCounted() {
        return true;
    }

    private void collectValues(ValueEval operand, DoubleList temp) throws EvaluationException {
        if (operand instanceof ThreeDEval) {
            ThreeDEval ae = (ThreeDEval) operand;
            for (int sIx = ae.getFirstSheetIndex(); sIx <= ae.getLastSheetIndex(); sIx++) {
                int width = ae.getWidth();
                int height = ae.getHeight();
                for (int rrIx = 0; rrIx < height; rrIx++) {
                    for (int rcIx = 0; rcIx < width; rcIx++) {
                        ValueEval ve = ae.getValue(sIx, rrIx, rcIx);
                        if (isSubtotalCounted() || !ae.isSubTotal(rrIx, rcIx)) {
                            collectValue(ve, true, temp);
                        }
                    }
                }
            }
            return;
        }
        if (operand instanceof TwoDEval) {
            TwoDEval ae2 = (TwoDEval) operand;
            int width2 = ae2.getWidth();
            int height2 = ae2.getHeight();
            for (int rrIx2 = 0; rrIx2 < height2; rrIx2++) {
                for (int rcIx2 = 0; rcIx2 < width2; rcIx2++) {
                    ValueEval ve2 = ae2.getValue(rrIx2, rcIx2);
                    if (isSubtotalCounted() || !ae2.isSubTotal(rrIx2, rcIx2)) {
                        collectValue(ve2, true, temp);
                    }
                }
            }
            return;
        }
        if (operand instanceof RefEval) {
            RefEval re = (RefEval) operand;
            for (int sIx2 = re.getFirstSheetIndex(); sIx2 <= re.getLastSheetIndex(); sIx2++) {
                collectValue(re.getInnerValueEval(sIx2), true, temp);
            }
            return;
        }
        collectValue(operand, false, temp);
    }

    private void collectValue(ValueEval ve, boolean isViaReference, DoubleList temp) throws EvaluationException {
        if (ve == null) {
            throw new IllegalArgumentException("ve must not be null");
        }
        if (ve instanceof BoolEval) {
            if (!isViaReference || this._isReferenceBoolCounted) {
                BoolEval boolEval = (BoolEval) ve;
                temp.add(boolEval.getNumberValue());
                return;
            }
            return;
        }
        if (ve instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval) ve;
            temp.add(ne.getNumberValue());
            return;
        }
        if (ve instanceof StringValueEval) {
            if (isViaReference) {
                return;
            }
            String s = ((StringValueEval) ve).getStringValue();
            Double d = OperandResolver.parseDouble(s);
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            temp.add(d.doubleValue());
            return;
        }
        if (ve instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) ve);
        }
        if (ve == BlankEval.instance) {
            if (this._isBlankCounted) {
                temp.add(0.0d);
                return;
            }
            return;
        }
        throw new RuntimeException("Invalid ValueEval type passed for conversion: (" + ve.getClass() + ")");
    }
}
