package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public class Code extends Fixed1ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval textArg) {
        try {
            ValueEval veText1 = OperandResolver.getSingleValue(textArg, srcRowIndex, srcColumnIndex);
            String text = OperandResolver.coerceValueToString(veText1);
            if (text.length() == 0) {
                return ErrorEval.VALUE_INVALID;
            }
            int code = text.charAt(0);
            return new StringEval(String.valueOf(code));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
