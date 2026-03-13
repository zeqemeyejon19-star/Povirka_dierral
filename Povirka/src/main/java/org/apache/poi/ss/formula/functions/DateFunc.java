package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public final class DateFunc extends Fixed3ArgFunction {
    public static final Function instance = new DateFunc();

    private DateFunc() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        double d0;
        double result;
        try {
            d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            e = e;
        }
        try {
            double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            try {
                double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
                result = evaluate(getYear(d0), (int) (d1 - 1.0d), (int) d2);
            } catch (EvaluationException e2) {
                e = e2;
            }
            try {
                NumericFunction.checkValue(result);
                return new NumberEval(result);
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        } catch (EvaluationException e4) {
            e = e4;
            return e.getErrorEval();
        }
    }

    private static double evaluate(int year, int month, int pDay) throws EvaluationException {
        if (year < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        while (month < 0) {
            year--;
            month += 12;
        }
        if (year == 1900 && month == 1 && pDay == 29) {
            return 60.0d;
        }
        int day = pDay;
        if (year == 1900 && ((month == 0 && day >= 60) || (month == 1 && day >= 30))) {
            day--;
        }
        Calendar c = LocaleUtil.getLocaleCalendar(year, month, day);
        if (pDay < 0 && c.get(1) == 1900 && month > 1 && c.get(2) < 2) {
            c.add(5, 1);
        }
        return DateUtil.getExcelDate(c.getTime(), false);
    }

    private static int getYear(double d) {
        int year = (int) d;
        if (year < 0) {
            return -1;
        }
        return year < 1900 ? year + 1900 : year;
    }
}
