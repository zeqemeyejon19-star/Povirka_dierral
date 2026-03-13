package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
final class WorkdayFunction implements FreeRefFunction {
    public static final FreeRefFunction instance = new WorkdayFunction(ArgumentsEvaluator.instance);
    private ArgumentsEvaluator evaluator;

    private WorkdayFunction(ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        double start;
        int days;
        double[] holidays;
        if (args.length < 2 || args.length > 3) {
            return ErrorEval.VALUE_INVALID;
        }
        int srcCellRow = ec.getRowIndex();
        int srcCellCol = ec.getColumnIndex();
        try {
            start = this.evaluator.evaluateDateArg(args[0], srcCellRow, srcCellCol);
            try {
                days = (int) Math.floor(this.evaluator.evaluateNumberArg(args[1], srcCellRow, srcCellCol));
                try {
                    ValueEval holidaysCell = args.length == 3 ? args[2] : null;
                    holidays = this.evaluator.evaluateDatesArg(holidaysCell, srcCellRow, srcCellCol);
                } catch (EvaluationException e) {
                }
            } catch (EvaluationException e2) {
            }
        } catch (EvaluationException e3) {
        }
        try {
            return new NumberEval(DateUtil.getExcelDate(WorkdayCalculator.instance.calculateWorkdays(start, days, holidays)));
        } catch (EvaluationException e4) {
            return ErrorEval.VALUE_INVALID;
        }
    }
}
