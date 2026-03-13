package org.apache.poi.ss.formula.atp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.poi.ss.formula.eval.AreaEvalBase;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
final class ArgumentsEvaluator {
    public static final ArgumentsEvaluator instance = new ArgumentsEvaluator();

    private ArgumentsEvaluator() {
    }

    public double evaluateDateArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
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

    public double[] evaluateDatesArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        if (arg == null) {
            return new double[0];
        }
        if (arg instanceof StringEval) {
            return new double[]{evaluateDateArg(arg, srcCellRow, srcCellCol)};
        }
        if (arg instanceof AreaEvalBase) {
            List<Double> valuesList = new ArrayList<>();
            AreaEvalBase area = (AreaEvalBase) arg;
            for (int i = area.getFirstRow(); i <= area.getLastRow(); i++) {
                for (int j = area.getFirstColumn(); j <= area.getLastColumn(); j++) {
                    valuesList.add(Double.valueOf(evaluateDateArg(area.getAbsoluteValue(i, j), i, j)));
                }
            }
            int i2 = valuesList.size();
            double[] values = new double[i2];
            for (int i3 = 0; i3 < valuesList.size(); i3++) {
                values[i3] = valuesList.get(i3).doubleValue();
            }
            return values;
        }
        return new double[]{OperandResolver.coerceValueToDouble(arg)};
    }

    public double evaluateNumberArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        if (arg == null) {
            return 0.0d;
        }
        return OperandResolver.coerceValueToDouble(arg);
    }
}
