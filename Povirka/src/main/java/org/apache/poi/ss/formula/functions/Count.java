package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.CountUtils;

/* JADX INFO: loaded from: classes.dex */
public final class Count implements Function {
    private static final CountUtils.I_MatchPredicate defaultPredicate = new CountUtils.I_MatchPredicate() { // from class: org.apache.poi.ss.formula.functions.Count.1
        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval valueEval) {
            return (valueEval instanceof NumberEval) || valueEval == MissingArgEval.instance;
        }
    };
    private static final CountUtils.I_MatchPredicate subtotalPredicate = new CountUtils.I_MatchAreaPredicate() { // from class: org.apache.poi.ss.formula.functions.Count.2
        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval valueEval) {
            return Count.defaultPredicate.matches(valueEval);
        }

        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchAreaPredicate
        public boolean matches(TwoDEval areEval, int rowIndex, int columnIndex) {
            return !areEval.isSubTotal(rowIndex, columnIndex);
        }
    };
    private final CountUtils.I_MatchPredicate _predicate;

    public Count() {
        this._predicate = defaultPredicate;
    }

    private Count(CountUtils.I_MatchPredicate criteriaPredicate) {
        this._predicate = criteriaPredicate;
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        int nArgs = args.length;
        if (nArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        if (nArgs > 30) {
            return ErrorEval.VALUE_INVALID;
        }
        int temp = 0;
        for (ValueEval valueEval : args) {
            temp += CountUtils.countArg(valueEval, this._predicate);
        }
        return new NumberEval(temp);
    }

    public static Count subtotalInstance() {
        return new Count(subtotalPredicate);
    }
}
