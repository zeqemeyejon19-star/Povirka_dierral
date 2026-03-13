package org.apache.poi.ss.formula.functions;

import java.util.Locale;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Dec2Hex extends Var1or2ArgFunction implements FreeRefFunction {
    private static final int DEFAULT_PLACES_VALUE = 10;
    public static final FreeRefFunction instance = new Dec2Hex();
    private static final long MIN_VALUE = Long.parseLong("-549755813888");
    private static final long MAX_VALUE = Long.parseLong("549755813887");

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval number, ValueEval places) {
        String hex;
        try {
            ValueEval veText1 = OperandResolver.getSingleValue(number, srcRowIndex, srcColumnIndex);
            String strText1 = OperandResolver.coerceValueToString(veText1);
            Double number1 = OperandResolver.parseDouble(strText1);
            if (number1 == null) {
                return ErrorEval.VALUE_INVALID;
            }
            if (number1.longValue() < MIN_VALUE || number1.longValue() > MAX_VALUE) {
                return ErrorEval.NUM_ERROR;
            }
            int placesNumber = 0;
            if (number1.doubleValue() < 0.0d) {
                placesNumber = 10;
            } else if (places != null) {
                try {
                    ValueEval placesValueEval = OperandResolver.getSingleValue(places, srcRowIndex, srcColumnIndex);
                    String placesStr = OperandResolver.coerceValueToString(placesValueEval);
                    Double placesNumberDouble = OperandResolver.parseDouble(placesStr);
                    if (placesNumberDouble == null) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    placesNumber = placesNumberDouble.intValue();
                    if (placesNumber < 0) {
                        return ErrorEval.NUM_ERROR;
                    }
                } catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
            if (placesNumber != 0) {
                hex = String.format(Locale.ROOT, "%0" + placesNumber + "X", Integer.valueOf(number1.intValue()));
            } else {
                hex = Long.toHexString(number1.longValue());
            }
            if (number1.doubleValue() < 0.0d) {
                hex = "FF" + hex.substring(2);
            }
            return new StringEval(hex.toUpperCase(Locale.ROOT));
        } catch (EvaluationException e2) {
            return e2.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        return evaluate(srcRowIndex, srcColumnIndex, arg0, null);
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
