package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public class Rept extends Fixed2ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval text, ValueEval number_times) {
        try {
            ValueEval veText1 = OperandResolver.getSingleValue(text, srcRowIndex, srcColumnIndex);
            String strText1 = OperandResolver.coerceValueToString(veText1);
            try {
                double numberOfTime = OperandResolver.coerceValueToDouble(number_times);
                int numberOfTimeInt = (int) numberOfTime;
                StringBuffer strb = new StringBuffer(strText1.length() * numberOfTimeInt);
                for (int i = 0; i < numberOfTimeInt; i++) {
                    strb.append(strText1);
                }
                if (strb.toString().length() > 32767) {
                    return ErrorEval.VALUE_INVALID;
                }
                return new StringEval(strb.toString());
            } catch (EvaluationException e) {
                return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e2) {
            return e2.getErrorEval();
        }
    }
}
