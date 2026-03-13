package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
public final class CalendarFieldFunction extends Fixed1ArgFunction {
    private final int _dateFieldId;
    public static final Function YEAR = new CalendarFieldFunction(1);
    public static final Function MONTH = new CalendarFieldFunction(2);
    public static final Function DAY = new CalendarFieldFunction(5);
    public static final Function HOUR = new CalendarFieldFunction(11);
    public static final Function MINUTE = new CalendarFieldFunction(12);
    public static final Function SECOND = new CalendarFieldFunction(13);

    private CalendarFieldFunction(int dateFieldId) {
        this._dateFieldId = dateFieldId;
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public final ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            double val = OperandResolver.coerceValueToDouble(ve);
            if (val < 0.0d) {
                return ErrorEval.NUM_ERROR;
            }
            return new NumberEval(getCalField(val));
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private int getCalField(double serialDate) {
        if (((int) serialDate) == 0) {
            int i = this._dateFieldId;
            if (i == 1) {
                return 1900;
            }
            if (i == 2) {
                return 1;
            }
            if (i == 5) {
                return 0;
            }
        }
        Calendar c = DateUtil.getJavaCalendarUTC(5.78125E-6d + serialDate, false);
        int result = c.get(this._dateFieldId);
        if (this._dateFieldId == 2) {
            return result + 1;
        }
        return result;
    }
}
