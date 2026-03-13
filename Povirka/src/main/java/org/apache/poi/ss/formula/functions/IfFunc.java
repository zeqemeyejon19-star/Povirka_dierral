package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class IfFunc extends Var2or3ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        try {
            boolean b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
            if (b) {
                if (arg1 == MissingArgEval.instance) {
                    return BlankEval.instance;
                }
                return arg1;
            }
            return BoolEval.FALSE;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        try {
            boolean b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
            if (b) {
                if (arg1 == MissingArgEval.instance) {
                    return BlankEval.instance;
                }
                return arg1;
            }
            if (arg2 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg2;
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    public static boolean evaluateFirstArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }
}
