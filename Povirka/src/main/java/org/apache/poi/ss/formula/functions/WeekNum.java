package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class WeekNum extends Fixed2ArgFunction implements FreeRefFunction {
    public static final FreeRefFunction instance = new WeekNum();

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval serialNumVE, ValueEval returnTypeVE) {
        try {
            double serialNum = NumericFunction.singleOperandEvaluate(serialNumVE, srcRowIndex, srcColumnIndex);
            Calendar serialNumCalendar = LocaleUtil.getLocaleCalendar();
            serialNumCalendar.setTime(DateUtil.getJavaDate(serialNum, false));
            try {
                ValueEval ve = OperandResolver.getSingleValue(returnTypeVE, srcRowIndex, srcColumnIndex);
                int returnType = OperandResolver.coerceValueToInt(ve);
                if (returnType != 1 && returnType != 2) {
                    return ErrorEval.NUM_ERROR;
                }
                return new NumberEval(getWeekNo(serialNumCalendar, returnType));
            } catch (EvaluationException e) {
                return ErrorEval.NUM_ERROR;
            }
        } catch (EvaluationException e2) {
            return ErrorEval.VALUE_INVALID;
        }
    }

    public int getWeekNo(Calendar cal, int weekStartOn) {
        if (weekStartOn == 1) {
            cal.setFirstDayOfWeek(1);
        } else {
            cal.setFirstDayOfWeek(2);
        }
        return cal.get(3);
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 2) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
