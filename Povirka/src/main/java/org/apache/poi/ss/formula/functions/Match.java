package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.LookupUtils;

/* JADX INFO: loaded from: classes.dex */
public final class Match extends Var2or3ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, 1.0d);
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            double match_type = evaluateMatchTypeArg(arg2, srcRowIndex, srcColumnIndex);
            return eval(srcRowIndex, srcColumnIndex, arg0, arg1, match_type);
        } catch (EvaluationException e) {
            return ErrorEval.REF_INVALID;
        }
    }

    private static ValueEval eval(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, double match_type) {
        boolean matchExact = match_type == 0.0d;
        boolean findLargestLessThanOrEqual = match_type > 0.0d;
        try {
            ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            LookupUtils.ValueVector lookupRange = evaluateLookupRange(arg1);
            int index = findIndexOfValue(lookupValue, lookupRange, matchExact, findLargestLessThanOrEqual);
            return new NumberEval(index + 1);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static final class SingleValueVector implements LookupUtils.ValueVector {
        private final ValueEval _value;

        public SingleValueVector(ValueEval value) {
            this._value = value;
        }

        @Override // org.apache.poi.ss.formula.functions.LookupUtils.ValueVector
        public ValueEval getItem(int index) {
            if (index != 0) {
                throw new RuntimeException("Invalid index (" + index + ") only zero is allowed");
            }
            return this._value;
        }

        @Override // org.apache.poi.ss.formula.functions.LookupUtils.ValueVector
        public int getSize() {
            return 1;
        }
    }

    private static LookupUtils.ValueVector evaluateLookupRange(ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            RefEval re = (RefEval) eval;
            if (re.getNumberOfSheets() == 1) {
                return new SingleValueVector(re.getInnerValueEval(re.getFirstSheetIndex()));
            }
            return LookupUtils.createVector(re);
        }
        if (eval instanceof TwoDEval) {
            LookupUtils.ValueVector result = LookupUtils.createVector((TwoDEval) eval);
            if (result == null) {
                throw new EvaluationException(ErrorEval.NA);
            }
            return result;
        }
        if (eval instanceof NumericValueEval) {
            throw new EvaluationException(ErrorEval.NA);
        }
        if (eval instanceof StringEval) {
            StringEval se = (StringEval) eval;
            Double d = OperandResolver.parseDouble(se.getStringValue());
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            throw new EvaluationException(ErrorEval.NA);
        }
        throw new RuntimeException("Unexpected eval type (" + eval + ")");
    }

    private static double evaluateMatchTypeArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval match_type = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (match_type instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) match_type);
        }
        if (match_type instanceof NumericValueEval) {
            NumericValueEval ne = (NumericValueEval) match_type;
            return ne.getNumberValue();
        }
        if (match_type instanceof StringEval) {
            StringEval se = (StringEval) match_type;
            Double d = OperandResolver.parseDouble(se.getStringValue());
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return d.doubleValue();
        }
        throw new RuntimeException("Unexpected match_type type (" + match_type.getClass().getName() + ")");
    }

    private static int findIndexOfValue(ValueEval lookupValue, LookupUtils.ValueVector lookupRange, boolean matchExact, boolean findLargestLessThanOrEqual) throws EvaluationException {
        LookupUtils.LookupValueComparer lookupComparer = createLookupComparer(lookupValue, matchExact);
        int size = lookupRange.getSize();
        if (matchExact) {
            for (int i = 0; i < size; i++) {
                if (lookupComparer.compareTo(lookupRange.getItem(i)).isEqual()) {
                    return i;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }
        if (findLargestLessThanOrEqual) {
            for (int i2 = size - 1; i2 >= 0; i2--) {
                LookupUtils.CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i2));
                if (!cmp.isTypeMismatch() && !cmp.isLessThan()) {
                    return i2;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }
        for (int i3 = 0; i3 < size; i3++) {
            LookupUtils.CompareResult cmp2 = lookupComparer.compareTo(lookupRange.getItem(i3));
            if (cmp2.isEqual()) {
                return i3;
            }
            if (cmp2.isGreaterThan()) {
                if (i3 < 1) {
                    throw new EvaluationException(ErrorEval.NA);
                }
                return i3 - 1;
            }
        }
        int i4 = size - 1;
        return i4;
    }

    private static LookupUtils.LookupValueComparer createLookupComparer(ValueEval lookupValue, boolean matchExact) {
        return LookupUtils.createLookupComparer(lookupValue, matchExact, true);
    }
}
