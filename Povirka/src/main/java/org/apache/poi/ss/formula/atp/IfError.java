package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

/* JADX INFO: loaded from: classes.dex */
final class IfError implements FreeRefFunction {
    public static final FreeRefFunction instance = new IfError();

    private IfError() {
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            ValueEval val = evaluateInternal(args[0], args[1], ec.getRowIndex(), ec.getColumnIndex());
            return val;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static ValueEval evaluateInternal(ValueEval arg, ValueEval iferror, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval arg2 = WorkbookEvaluator.dereferenceResult(arg, srcCellRow, srcCellCol);
        if (arg2 instanceof ErrorEval) {
            return iferror;
        }
        return arg2;
    }
}
