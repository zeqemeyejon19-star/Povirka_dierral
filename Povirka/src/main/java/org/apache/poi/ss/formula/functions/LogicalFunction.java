package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.RefListEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public abstract class LogicalFunction extends Fixed1ArgFunction {
    public static final Function ISLOGICAL = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.1
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof BoolEval;
        }
    };
    public static final Function ISNONTEXT = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.2
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return !(arg instanceof StringEval);
        }
    };
    public static final Function ISNUMBER = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.3
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof NumberEval;
        }
    };
    public static final Function ISTEXT = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.4
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof StringEval;
        }
    };
    public static final Function ISBLANK = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.5
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof BlankEval;
        }
    };
    public static final Function ISERROR = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.6
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg instanceof ErrorEval;
        }
    };
    public static final Function ISERR = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.7
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return (arg instanceof ErrorEval) && arg != ErrorEval.NA;
        }
    };
    public static final Function ISNA = new LogicalFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.8
        @Override // org.apache.poi.ss.formula.functions.LogicalFunction
        protected boolean evaluate(ValueEval arg) {
            return arg == ErrorEval.NA;
        }
    };
    public static final Function ISREF = new Fixed1ArgFunction() { // from class: org.apache.poi.ss.formula.functions.LogicalFunction.9
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            if ((arg0 instanceof RefEval) || (arg0 instanceof AreaEval) || (arg0 instanceof RefListEval)) {
                return BoolEval.TRUE;
            }
            return BoolEval.FALSE;
        }
    };

    protected abstract boolean evaluate(ValueEval valueEval);

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        ValueEval ve;
        try {
            ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            ValueEval ve2 = e.getErrorEval();
            ve = ve2;
        }
        return BoolEval.valueOf(evaluate(ve));
    }
}
