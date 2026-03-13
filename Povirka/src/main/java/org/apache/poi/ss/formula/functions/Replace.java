package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Replace extends Fixed4ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function4Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        String oldStr;
        int startNum;
        int numChars;
        try {
            oldStr = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            try {
                startNum = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                try {
                    numChars = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
                } catch (EvaluationException e) {
                    e = e;
                }
            } catch (EvaluationException e2) {
                e = e2;
            }
        } catch (EvaluationException e3) {
            e = e3;
        }
        try {
            String newStr = TextFunction.evaluateStringArg(arg3, srcRowIndex, srcColumnIndex);
            if (startNum < 1 || numChars < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            StringBuffer strBuff = new StringBuffer(oldStr);
            if (startNum <= oldStr.length() && numChars != 0) {
                strBuff.delete(startNum - 1, (startNum - 1) + numChars);
            }
            if (startNum > strBuff.length()) {
                strBuff.append(newStr);
            } else {
                strBuff.insert(startNum - 1, newStr);
            }
            return new StringEval(strBuff.toString());
        } catch (EvaluationException e4) {
            e = e4;
            return e.getErrorEval();
        }
    }
}
