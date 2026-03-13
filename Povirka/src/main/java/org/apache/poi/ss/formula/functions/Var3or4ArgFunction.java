package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
abstract class Var3or4ArgFunction implements Function3Arg, Function4Arg {
    Var3or4ArgFunction() {
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        int length = args.length;
        if (length == 3) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        if (length == 4) {
            return evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
