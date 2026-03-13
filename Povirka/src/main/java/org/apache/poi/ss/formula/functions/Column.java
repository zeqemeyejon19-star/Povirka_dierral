package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Column implements Function0Arg, Function1Arg {
    @Override // org.apache.poi.ss.formula.functions.Function0Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        return new NumberEval(srcColumnIndex + 1);
    }

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        int rnum;
        if (arg0 instanceof AreaEval) {
            rnum = ((AreaEval) arg0).getFirstColumn();
        } else if (arg0 instanceof RefEval) {
            rnum = ((RefEval) arg0).getColumn();
        } else {
            return ErrorEval.VALUE_INVALID;
        }
        return new NumberEval(rnum + 1);
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 0) {
            return new NumberEval(srcColumnIndex + 1);
        }
        if (length == 1) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
