package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Index implements Function2Arg, Function3Arg, Function4Arg {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        TwoDEval reference = convertFirstArg(arg0);
        int columnIx = 0;
        try {
            int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);
            if (!reference.isColumn()) {
                if (!reference.isRow()) {
                    return ErrorEval.REF_INVALID;
                }
                columnIx = rowIx;
                rowIx = 0;
            }
            return getValueFromArea(reference, rowIx, columnIx);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
        TwoDEval reference = convertFirstArg(arg0);
        try {
            int columnIx = resolveIndexArg(arg2, srcRowIndex, srcColumnIndex);
            int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);
            return getValueFromArea(reference, rowIx, columnIx);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function4Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2, ValueEval arg3) {
        throw new RuntimeException("Incomplete code - don't know how to support the 'area_num' parameter yet)");
    }

    private static TwoDEval convertFirstArg(ValueEval arg0) {
        if (arg0 instanceof RefEval) {
            return ((RefEval) arg0).offset(0, 0, 0, 0);
        }
        if (arg0 instanceof TwoDEval) {
            return (TwoDEval) arg0;
        }
        throw new RuntimeException("Incomplete code - cannot handle first arg of type (" + arg0.getClass().getName() + ")");
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 2) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
        }
        if (length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        if (length == 4) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3]);
        }
        return ErrorEval.VALUE_INVALID;
    }

    private static ValueEval getValueFromArea(TwoDEval ae, int pRowIx, int pColumnIx) throws EvaluationException {
        if (pRowIx < 0) {
            throw new AssertionError();
        }
        if (pColumnIx < 0) {
            throw new AssertionError();
        }
        TwoDEval result = ae;
        if (pRowIx != 0) {
            if (pRowIx > ae.getHeight()) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            result = result.getRow(pRowIx - 1);
        }
        if (pColumnIx != 0) {
            if (pColumnIx > ae.getWidth()) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            return result.getColumn(pColumnIx - 1);
        }
        return result;
    }

    private static int resolveIndexArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ev = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (ev == MissingArgEval.instance || ev == BlankEval.instance) {
            return 0;
        }
        int result = OperandResolver.coerceValueToInt(ev);
        if (result < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }
}
