package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
abstract class Var1or2ArgFunction implements Function1Arg, Function2Arg {
    Var1or2ArgFunction() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 1) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0]);
        }
        if (length == 2) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
