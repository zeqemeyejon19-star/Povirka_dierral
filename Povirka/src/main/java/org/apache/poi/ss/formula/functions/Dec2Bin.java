package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public class Dec2Bin extends Var1or2ArgFunction implements FreeRefFunction {
    private static final int DEFAULT_PLACES_VALUE = 10;
    private static final long MAX_VALUE = 511;
    private static final long MIN_VALUE = -512;
    public static final FreeRefFunction instance = new Dec2Bin();

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval numberVE, ValueEval placesVE) {
        int placesNumber;
        try {
            ValueEval veText1 = OperandResolver.getSingleValue(numberVE, srcRowIndex, srcColumnIndex);
            String strText1 = OperandResolver.coerceValueToString(veText1);
            Double number = OperandResolver.parseDouble(strText1);
            if (number == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (number.longValue() < MIN_VALUE || number.longValue() > MAX_VALUE) {
                return ErrorEval.NUM_ERROR;
            }
            if (number.doubleValue() < 0.0d || placesVE == null) {
                placesNumber = 10;
            } else {
                try {
                    ValueEval placesValueEval = OperandResolver.getSingleValue(placesVE, srcRowIndex, srcColumnIndex);
                    String placesStr = OperandResolver.coerceValueToString(placesValueEval);
                    Double placesNumberDouble = OperandResolver.parseDouble(placesStr);
                    if (placesNumberDouble == null) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    placesNumber = placesNumberDouble.intValue();
                    if (placesNumber < 0 || placesNumber == 0) {
                        return ErrorEval.NUM_ERROR;
                    }
                } catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
            String binary = Integer.toBinaryString(number.intValue());
            if (binary.length() > 10) {
                binary = binary.substring(binary.length() - 10, binary.length());
            }
            if (binary.length() > placesNumber) {
                return ErrorEval.NUM_ERROR;
            }
            return new StringEval(binary);
        } catch (EvaluationException e2) {
            return e2.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval numberVE) {
        return evaluate(srcRowIndex, srcColumnIndex, numberVE, null);
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 1) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
        }
        if (args.length == 2) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
