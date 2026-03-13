package org.apache.poi.ss.formula.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Fixed implements Function1Arg, Function2Arg, Function3Arg {
    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        return fixed(arg0, arg1, arg2, srcRowIndex, srcColumnIndex);
    }

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        return fixed(arg0, arg1, BoolEval.FALSE, srcRowIndex, srcColumnIndex);
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        return fixed(arg0, new NumberEval(2.0d), BoolEval.FALSE, srcRowIndex, srcColumnIndex);
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 1) {
            return fixed(args[0], new NumberEval(2.0d), BoolEval.FALSE, srcRowIndex, srcColumnIndex);
        }
        if (length == 2) {
            return fixed(args[0], args[1], BoolEval.FALSE, srcRowIndex, srcColumnIndex);
        }
        if (length == 3) {
            return fixed(args[0], args[1], args[2], srcRowIndex, srcColumnIndex);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private ValueEval fixed(ValueEval numberParam, ValueEval placesParam, ValueEval skipThousandsSeparatorParam, int srcRowIndex, int srcColumnIndex) {
        try {
            ValueEval numberValueEval = OperandResolver.getSingleValue(numberParam, srcRowIndex, srcColumnIndex);
            BigDecimal number = new BigDecimal(OperandResolver.coerceValueToDouble(numberValueEval));
            try {
                ValueEval placesValueEval = OperandResolver.getSingleValue(placesParam, srcRowIndex, srcColumnIndex);
                int places = OperandResolver.coerceValueToInt(placesValueEval);
                ValueEval skipThousandsSeparatorValueEval = OperandResolver.getSingleValue(skipThousandsSeparatorParam, srcRowIndex, srcColumnIndex);
                Boolean skipThousandsSeparator = OperandResolver.coerceValueToBoolean(skipThousandsSeparatorValueEval, false);
                BigDecimal number2 = number.setScale(places, RoundingMode.HALF_UP);
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) nf;
                formatter.setGroupingUsed(skipThousandsSeparator == null || !skipThousandsSeparator.booleanValue());
                formatter.setMinimumFractionDigits(places >= 0 ? places : 0);
                formatter.setMaximumFractionDigits(places >= 0 ? places : 0);
                String numberString = formatter.format(number2.doubleValue());
                return new StringEval(numberString);
            } catch (EvaluationException e) {
                e = e;
                return e.getErrorEval();
            }
        } catch (EvaluationException e2) {
            e = e2;
        }
    }
}
