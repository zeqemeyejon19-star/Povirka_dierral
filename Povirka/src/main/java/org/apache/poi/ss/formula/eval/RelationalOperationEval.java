package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.util.NumberComparer;

/* JADX INFO: loaded from: classes.dex */
public abstract class RelationalOperationEval extends Fixed2ArgFunction {
    public static final Function EqualEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.1
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult == 0;
        }
    };
    public static final Function GreaterEqualEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.2
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult >= 0;
        }
    };
    public static final Function GreaterThanEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.3
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult > 0;
        }
    };
    public static final Function LessEqualEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.4
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult <= 0;
        }
    };
    public static final Function LessThanEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.5
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult < 0;
        }
    };
    public static final Function NotEqualEval = new RelationalOperationEval() { // from class: org.apache.poi.ss.formula.eval.RelationalOperationEval.6
        @Override // org.apache.poi.ss.formula.eval.RelationalOperationEval
        protected boolean convertComparisonResult(int cmpResult) {
            return cmpResult != 0;
        }
    };

    protected abstract boolean convertComparisonResult(int i);

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        ValueEval vA;
        try {
            vA = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            e = e;
        }
        try {
            ValueEval vB = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
            int cmpResult = doCompare(vA, vB);
            boolean result = convertComparisonResult(cmpResult);
            return BoolEval.valueOf(result);
        } catch (EvaluationException e2) {
            e = e2;
            return e.getErrorEval();
        }
    }

    private static int doCompare(ValueEval va, ValueEval vb) {
        if (va == BlankEval.instance) {
            return compareBlank(vb);
        }
        if (vb == BlankEval.instance) {
            return -compareBlank(va);
        }
        if (va instanceof BoolEval) {
            if (!(vb instanceof BoolEval)) {
                return 1;
            }
            BoolEval bA = (BoolEval) va;
            BoolEval bB = (BoolEval) vb;
            if (bA.getBooleanValue() == bB.getBooleanValue()) {
                return 0;
            }
            return bA.getBooleanValue() ? 1 : -1;
        }
        if (vb instanceof BoolEval) {
            return -1;
        }
        if (va instanceof StringEval) {
            if (!(vb instanceof StringEval)) {
                return 1;
            }
            StringEval sA = (StringEval) va;
            StringEval sB = (StringEval) vb;
            return sA.getStringValue().compareToIgnoreCase(sB.getStringValue());
        }
        if (vb instanceof StringEval) {
            return -1;
        }
        if ((va instanceof NumberEval) && (vb instanceof NumberEval)) {
            NumberEval nA = (NumberEval) va;
            NumberEval nB = (NumberEval) vb;
            return NumberComparer.compare(nA.getNumberValue(), nB.getNumberValue());
        }
        throw new IllegalArgumentException("Bad operand types (" + va.getClass().getName() + "), (" + vb.getClass().getName() + ")");
    }

    private static int compareBlank(ValueEval v) {
        if (v == BlankEval.instance) {
            return 0;
        }
        if (v instanceof BoolEval) {
            BoolEval boolEval = (BoolEval) v;
            return boolEval.getBooleanValue() ? -1 : 0;
        }
        if (v instanceof NumberEval) {
            NumberEval ne = (NumberEval) v;
            return NumberComparer.compare(0.0d, ne.getNumberValue());
        }
        if (v instanceof StringEval) {
            StringEval se = (StringEval) v;
            return se.getStringValue().length() < 1 ? 0 : -1;
        }
        throw new IllegalArgumentException("bad value class (" + v.getClass().getName() + ")");
    }
}
