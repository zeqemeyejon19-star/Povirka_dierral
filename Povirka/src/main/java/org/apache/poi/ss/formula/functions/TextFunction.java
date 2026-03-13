package org.apache.poi.ss.formula.functions;

import java.util.Locale;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DataFormatter;

/* JADX INFO: loaded from: classes.dex */
public abstract class TextFunction implements Function {
    protected static final DataFormatter formatter = new DataFormatter();
    public static final Function CHAR = new Fixed1ArgFunction() { // from class: org.apache.poi.ss.formula.functions.TextFunction.1
        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            try {
                int arg = TextFunction.evaluateIntArg(arg0, srcRowIndex, srcColumnIndex);
                if (arg < 0 || arg >= 256) {
                    try {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    } catch (EvaluationException e) {
                        e = e;
                        return e.getErrorEval();
                    }
                }
                return new StringEval(String.valueOf((char) arg));
            } catch (EvaluationException e2) {
                e = e2;
            }
        }
    };
    public static final Function LEN = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.2
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String arg) {
            return new NumberEval(arg.length());
        }
    };
    public static final Function LOWER = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.3
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.toLowerCase(Locale.ROOT));
        }
    };
    public static final Function UPPER = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.4
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.toUpperCase(Locale.ROOT));
        }
    };
    public static final Function PROPER = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.5
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String text) {
            StringBuilder sb = new StringBuilder();
            boolean shouldMakeUppercase = true;
            char[] arr$ = text.toCharArray();
            for (char ch : arr$) {
                if (shouldMakeUppercase) {
                    sb.append(String.valueOf(ch).toUpperCase(Locale.ROOT));
                } else {
                    sb.append(String.valueOf(ch).toLowerCase(Locale.ROOT));
                }
                shouldMakeUppercase = !Character.isLetter(ch);
            }
            return new StringEval(sb.toString());
        }
    };
    public static final Function TRIM = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.6
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String arg) {
            return new StringEval(arg.trim());
        }
    };
    public static final Function CLEAN = new SingleArgTextFunc() { // from class: org.apache.poi.ss.formula.functions.TextFunction.7
        @Override // org.apache.poi.ss.formula.functions.TextFunction.SingleArgTextFunc
        protected ValueEval evaluate(String arg) {
            StringBuilder result = new StringBuilder();
            char[] arr$ = arg.toCharArray();
            for (char c : arr$) {
                if (isPrintable(c)) {
                    result.append(c);
                }
            }
            return new StringEval(result.toString());
        }

        private boolean isPrintable(char c) {
            return c >= ' ';
        }
    };
    public static final Function MID = new Fixed3ArgFunction() { // from class: org.apache.poi.ss.formula.functions.TextFunction.8
        @Override // org.apache.poi.ss.formula.functions.Function3Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            int startCharNum;
            try {
                String text = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                try {
                    startCharNum = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                } catch (EvaluationException e) {
                    e = e;
                    return e.getErrorEval();
                }
                try {
                    int numChars = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
                    int startIx = startCharNum - 1;
                    if (startIx < 0) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    if (numChars < 0) {
                        return ErrorEval.VALUE_INVALID;
                    }
                    int len = text.length();
                    if (numChars < 0 || startIx > len) {
                        return new StringEval("");
                    }
                    int endIx = Math.min(startIx + numChars, len);
                    String result = text.substring(startIx, endIx);
                    return new StringEval(result);
                } catch (EvaluationException e2) {
                    e = e2;
                    return e.getErrorEval();
                }
            } catch (EvaluationException e3) {
                e = e3;
            }
        }
    };
    public static final Function LEFT = new LeftRight(true);
    public static final Function RIGHT = new LeftRight(false);
    public static final Function CONCATENATE = new Function() { // from class: org.apache.poi.ss.formula.functions.TextFunction.9
        @Override // org.apache.poi.ss.formula.functions.Function
        public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
            StringBuilder sb = new StringBuilder();
            for (ValueEval arg : args) {
                try {
                    sb.append(TextFunction.evaluateStringArg(arg, srcRowIndex, srcColumnIndex));
                } catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
            return new StringEval(sb.toString());
        }
    };
    public static final Function EXACT = new Fixed2ArgFunction() { // from class: org.apache.poi.ss.formula.functions.TextFunction.10
        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String s0;
            try {
                s0 = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                String s1 = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                return BoolEval.valueOf(s0.equals(s1));
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    };
    public static final Function TEXT = new Fixed2ArgFunction() { // from class: org.apache.poi.ss.formula.functions.TextFunction.11
        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            double s0;
            try {
                s0 = TextFunction.evaluateDoubleArg(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                String s1 = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                try {
                    String formattedStr = TextFunction.formatter.formatRawCellContents(s0, -1, s1);
                    return new StringEval(formattedStr);
                } catch (Exception e2) {
                    return ErrorEval.VALUE_INVALID;
                }
            } catch (EvaluationException e3) {
                e = e3;
                return e.getErrorEval();
            }
        }
    };
    public static final Function FIND = new SearchFind(true);
    public static final Function SEARCH = new SearchFind(false);

    protected abstract ValueEval evaluateFunc(ValueEval[] valueEvalArr, int i, int i2) throws EvaluationException;

    protected static String evaluateStringArg(ValueEval eval, int srcRow, int srcCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(eval, srcRow, srcCol);
        return OperandResolver.coerceValueToString(ve);
    }

    protected static int evaluateIntArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }

    protected static double evaluateDoubleArg(ValueEval arg, int srcCellRow, int srcCellCol) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }

    @Override // org.apache.poi.ss.formula.functions.Function
    public final ValueEval evaluate(ValueEval[] args, int srcCellRow, int srcCellCol) {
        try {
            return evaluateFunc(args, srcCellRow, srcCellCol);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static abstract class SingleArgTextFunc extends Fixed1ArgFunction {
        protected abstract ValueEval evaluate(String str);

        protected SingleArgTextFunc() {
        }

        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            try {
                String arg = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                return evaluate(arg);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
    }

    private static final class LeftRight extends Var1or2ArgFunction {
        private static final ValueEval DEFAULT_ARG1 = new NumberEval(1.0d);
        private final boolean _isLeft;

        protected LeftRight(boolean isLeft) {
            this._isLeft = isLeft;
        }

        @Override // org.apache.poi.ss.formula.functions.Function1Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
            return evaluate(srcRowIndex, srcColumnIndex, arg0, DEFAULT_ARG1);
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            String arg;
            try {
                arg = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            } catch (EvaluationException e) {
                e = e;
            }
            try {
                int index = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                if (index < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                String result = this._isLeft ? arg.substring(0, Math.min(arg.length(), index)) : arg.substring(Math.max(0, arg.length() - index));
                return new StringEval(result);
            } catch (EvaluationException e2) {
                e = e2;
                return e.getErrorEval();
            }
        }
    }

    private static final class SearchFind extends Var2or3ArgFunction {
        private final boolean _isCaseSensitive;

        public SearchFind(boolean isCaseSensitive) {
            this._isCaseSensitive = isCaseSensitive;
        }

        @Override // org.apache.poi.ss.formula.functions.Function2Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                return eval(haystack, needle, 0);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        @Override // org.apache.poi.ss.formula.functions.Function3Arg
        public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
            try {
                String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
                if (startpos < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                return eval(haystack, needle, startpos);
            } catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }

        private ValueEval eval(String haystack, String needle, int startIndex) {
            int result;
            if (this._isCaseSensitive) {
                result = haystack.indexOf(needle, startIndex);
            } else {
                result = haystack.toUpperCase(Locale.ROOT).indexOf(needle.toUpperCase(Locale.ROOT), startIndex);
            }
            if (result == -1) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(result + 1);
        }
    }
}
