package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
public final class WeekdayFunc implements Function {
    public static final Function instance = new WeekdayFunc();

    private WeekdayFunc() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        double result;
        try {
            if (args.length >= 1 && args.length <= 2) {
                ValueEval serialDateVE = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
                double serialDate = OperandResolver.coerceValueToDouble(serialDateVE);
                if (!DateUtil.isValidExcelDate(serialDate)) {
                    return ErrorEval.NUM_ERROR;
                }
                Calendar date = DateUtil.getJavaCalendar(serialDate, false);
                int weekday = date.get(7);
                int returnOption = 1;
                if (args.length == 2) {
                    ValueEval ve = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
                    if (ve != MissingArgEval.instance && ve != BlankEval.instance) {
                        returnOption = OperandResolver.coerceValueToInt(ve);
                        if (returnOption == 2) {
                            returnOption = 11;
                        }
                    }
                    return ErrorEval.NUM_ERROR;
                }
                if (returnOption == 1) {
                    result = weekday;
                } else if (returnOption == 3) {
                    result = ((weekday + 6) - 1) % 7;
                } else if (returnOption >= 11 && returnOption <= 17) {
                    result = (((weekday + 6) - (returnOption - 10)) % 7) + 1;
                } else {
                    return ErrorEval.NUM_ERROR;
                }
                return new NumberEval(result);
            }
            return ErrorEval.VALUE_INVALID;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
