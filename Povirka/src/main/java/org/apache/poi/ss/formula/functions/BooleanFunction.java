package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public abstract class BooleanFunction implements Function {
    public static final Function AND = new BooleanFunction() { // from class: org.apache.poi.ss.formula.functions.BooleanFunction.1
        @Override // org.apache.poi.ss.formula.functions.BooleanFunction
        protected boolean getInitialResultValue() {
            return true;
        }

        @Override // org.apache.poi.ss.formula.functions.BooleanFunction
        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult && currentValue;
        }
    };
    public static final Function OR = new BooleanFunction() { // from class: org.apache.poi.ss.formula.functions.BooleanFunction.2
        @Override // org.apache.poi.ss.formula.functions.BooleanFunction
        protected boolean getInitialResultValue() {
            return false;
        }

        @Override // org.apache.poi.ss.formula.functions.BooleanFunction
        protected boolean partialEvaluate(boolean cumulativeResult, boolean currentValue) {
            return cumulativeResult || currentValue;
        }
    };
    public static final Function FALSE = new Fixed0ArgFunction() { // from class: org.apache.poi.ss.formula.functions.BooleanFunction.3
        @Override // org.apache.poi.ss.formula.functions.Function0Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.FALSE;
        }
    };
    public static final Function TRUE = new Fixed0ArgFunction() { // from class: org.apache.poi.ss.formula.functions.BooleanFunction.4
        @Override // org.apache.poi.ss.formula.functions.Function0Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
            return BoolEval.TRUE;
        }
    };
    public static final Function NOT = new Fixed1ArgFunction() { // from class: org.apache.poi.ss.formula.functions.BooleanFunction.5
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            try {
                ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
                Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
                boolean boolArgVal = b == null ? false : b.booleanValue();
                return BoolEval.valueOf(boolArgVal ? false : true);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
    };

    protected abstract boolean getInitialResultValue();

    protected abstract boolean partialEvaluate(boolean z, boolean z2);

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcRow, int srcCol) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            boolean boolResult = calculate(args);
            return BoolEval.valueOf(boolResult);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private boolean calculate(ValueEval[] args) throws EvaluationException {
        Boolean tempVe;
        boolean result = getInitialResultValue();
        boolean atleastOneNonBlank = false;
        for (ValueEval arg : args) {
            if (arg instanceof TwoDEval) {
                TwoDEval ae = (TwoDEval) arg;
                int height = ae.getHeight();
                int width = ae.getWidth();
                for (int rrIx = 0; rrIx < height; rrIx++) {
                    for (int rcIx = 0; rcIx < width; rcIx++) {
                        ValueEval ve = ae.getValue(rrIx, rcIx);
                        Boolean tempVe2 = OperandResolver.coerceValueToBoolean(ve, true);
                        if (tempVe2 != null) {
                            result = partialEvaluate(result, tempVe2.booleanValue());
                            atleastOneNonBlank = true;
                        }
                    }
                }
            } else if (arg instanceof RefEval) {
                RefEval re = (RefEval) arg;
                int firstSheetIndex = re.getFirstSheetIndex();
                int lastSheetIndex = re.getLastSheetIndex();
                for (int sIx = firstSheetIndex; sIx <= lastSheetIndex; sIx++) {
                    ValueEval ve2 = re.getInnerValueEval(sIx);
                    Boolean tempVe3 = OperandResolver.coerceValueToBoolean(ve2, true);
                    if (tempVe3 != null) {
                        result = partialEvaluate(result, tempVe3.booleanValue());
                        atleastOneNonBlank = true;
                    }
                }
            } else {
                if (arg == MissingArgEval.instance) {
                    tempVe = null;
                } else {
                    tempVe = OperandResolver.coerceValueToBoolean(arg, false);
                }
                if (tempVe != null) {
                    result = partialEvaluate(result, tempVe.booleanValue());
                    atleastOneNonBlank = true;
                }
            }
        }
        if (!atleastOneNonBlank) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }
}
