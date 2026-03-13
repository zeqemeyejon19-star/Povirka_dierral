package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public class Bin2Dec extends Fixed1ArgFunction implements FreeRefFunction {
    public static final FreeRefFunction instance = new Bin2Dec();

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval numberVE) {
        String number;
        String unsigned;
        boolean isPositive;
        String value;
        if (numberVE instanceof RefEval) {
            RefEval re = (RefEval) numberVE;
            number = OperandResolver.coerceValueToString(re.getInnerValueEval(re.getFirstSheetIndex()));
        } else {
            number = OperandResolver.coerceValueToString(numberVE);
        }
        if (number.length() > 10) {
            return ErrorEval.NUM_ERROR;
        }
        if (number.length() < 10) {
            unsigned = number;
            isPositive = true;
        } else {
            unsigned = number.substring(1);
            isPositive = number.startsWith("0");
        }
        try {
            if (isPositive) {
                int sum = getDecimalValue(unsigned);
                value = String.valueOf(sum);
            } else {
                String inverted = toggleBits(unsigned);
                int sum2 = getDecimalValue(inverted);
                value = "-" + (sum2 + 1);
            }
            return new NumberEval(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return ErrorEval.NUM_ERROR;
        }
    }

    private int getDecimalValue(String unsigned) {
        int sum = 0;
        int numBits = unsigned.length();
        int power = numBits - 1;
        for (int i = 0; i < numBits; i++) {
            int bit = Integer.parseInt(unsigned.substring(i, i + 1));
            int term = (int) (((double) bit) * Math.pow(2.0d, power));
            sum += term;
            power--;
        }
        return sum;
    }

    private static String toggleBits(String s) {
        long i = Long.parseLong(s, 2);
        long i2 = i ^ ((1 << s.length()) - 1);
        String s2 = Long.toBinaryString(i2);
        while (s2.length() < s.length()) {
            s2 = '0' + s2;
        }
        return s2;
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }
}
