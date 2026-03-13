package org.apache.poi.ss.formula.atp;

import java.util.Calendar;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
final class YearFrac implements FreeRefFunction {
    public static final FreeRefFunction instance = new YearFrac();

    private YearFrac() {
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        int srcCellRow = ec.getRowIndex();
        int srcCellCol = ec.getColumnIndex();
        int basis = 0;
        try {
            int length = args.length;
            if (length != 2) {
                if (length == 3) {
                    basis = evaluateIntArg(args[2], srcCellRow, srcCellCol);
                } else {
                    return ErrorEval.VALUE_INVALID;
                }
            }
            double startDateVal = evaluateDateArg(args[0], srcCellRow, srcCellCol);
            double endDateVal = evaluateDateArg(args[1], srcCellRow, srcCellCol);
            double result = YearFracCalculator.calculate(startDateVal, endDateVal, basis);
            return new NumberEval(result);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static double evaluateDateArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);
        if (ve instanceof StringEval) {
            String strVal = ((StringEval) ve).getStringValue();
            Double dVal = OperandResolver.parseDouble(strVal);
            if (dVal != null) {
                return dVal.doubleValue();
            }
            Calendar date = DateParser.parseDate(strVal);
            return DateUtil.getExcelDate(date, false);
        }
        return OperandResolver.coerceValueToDouble(ve);
    }

    private static int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short) srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }
}
