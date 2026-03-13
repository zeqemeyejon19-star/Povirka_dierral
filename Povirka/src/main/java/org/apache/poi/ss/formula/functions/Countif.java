package org.apache.poi.ss.formula.functions;

import java.util.regex.Pattern;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.CountUtils;
import org.apache.poi.ss.usermodel.FormulaError;

/* JADX INFO: loaded from: classes.dex */
public final class Countif extends Fixed2ArgFunction {

    private static final class CmpOp {
        public static final int EQ = 1;
        public static final int GE = 6;
        public static final int GT = 5;
        public static final int LE = 3;
        public static final int LT = 4;
        public static final int NE = 2;
        public static final int NONE = 0;
        private final int _code;
        private final String _representation;
        public static final CmpOp OP_NONE = op("", 0);
        public static final CmpOp OP_EQ = op("=", 1);
        public static final CmpOp OP_NE = op("<>", 2);
        public static final CmpOp OP_LE = op("<=", 3);
        public static final CmpOp OP_LT = op("<", 4);
        public static final CmpOp OP_GT = op(">", 5);
        public static final CmpOp OP_GE = op(">=", 6);

        private static CmpOp op(String rep, int code) {
            return new CmpOp(rep, code);
        }

        private CmpOp(String representation, int code) {
            this._representation = representation;
            this._code = code;
        }

        public int getLength() {
            return this._representation.length();
        }

        public int getCode() {
            return this._code;
        }

        public static CmpOp getOperator(String value) {
            int len = value.length();
            if (len < 1) {
                return OP_NONE;
            }
            char firstChar = value.charAt(0);
            switch (firstChar) {
                case '<':
                    if (len > 1) {
                        char cCharAt = value.charAt(1);
                        if (cCharAt != '=') {
                            if (cCharAt == '>') {
                            }
                        }
                    }
                    break;
                case '>':
                    if (len > 1 && value.charAt(1) == '=') {
                    }
                    break;
            }
            return OP_NONE;
        }

        public boolean evaluate(boolean cmpResult) {
            int i = this._code;
            if (i == 0 || i == 1) {
                return cmpResult;
            }
            if (i == 2) {
                return !cmpResult;
            }
            throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
        }

        public boolean evaluate(int cmpResult) {
            switch (this._code) {
                case 0:
                case 1:
                    return cmpResult == 0;
                case 2:
                    return cmpResult != 0;
                case 3:
                    return cmpResult <= 0;
                case 4:
                    return cmpResult < 0;
                case 5:
                    return cmpResult > 0;
                case 6:
                    return cmpResult >= 0;
                default:
                    throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
            }
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [").append(this._representation).append("]");
            return sb.toString();
        }

        public String getRepresentation() {
            return this._representation;
        }
    }

    private static abstract class MatcherBase implements CountUtils.I_MatchPredicate {
        private final CmpOp _operator;

        protected abstract String getValueText();

        MatcherBase(CmpOp operator) {
            this._operator = operator;
        }

        protected final int getCode() {
            return this._operator.getCode();
        }

        protected final boolean evaluate(int cmpResult) {
            return this._operator.evaluate(cmpResult);
        }

        protected final boolean evaluate(boolean cmpResult) {
            return this._operator.evaluate(cmpResult);
        }

        public final String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(this._operator.getRepresentation());
            sb.append(getValueText());
            sb.append("]");
            return sb.toString();
        }
    }

    private static final class NumberMatcher extends MatcherBase {
        private final double _value;

        public NumberMatcher(double value, CmpOp operator) {
            super(operator);
            this._value = value;
        }

        @Override // org.apache.poi.ss.formula.functions.Countif.MatcherBase
        protected String getValueText() {
            return String.valueOf(this._value);
        }

        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval x) {
            if (x instanceof StringEval) {
                int code = getCode();
                if (code != 0 && code != 1) {
                    return code == 2;
                }
                StringEval se = (StringEval) x;
                Double val = OperandResolver.parseDouble(se.getStringValue());
                return val != null && this._value == val.doubleValue();
            }
            if (!(x instanceof NumberEval)) {
                return (x instanceof BlankEval) && getCode() == 2;
            }
            NumberEval ne = (NumberEval) x;
            double testValue = ne.getNumberValue();
            return evaluate(Double.compare(testValue, this._value));
        }
    }

    private static final class BooleanMatcher extends MatcherBase {
        private final int _value;

        public BooleanMatcher(boolean value, CmpOp operator) {
            super(operator);
            this._value = boolToInt(value);
        }

        @Override // org.apache.poi.ss.formula.functions.Countif.MatcherBase
        protected String getValueText() {
            return this._value == 1 ? "TRUE" : "FALSE";
        }

        private static int boolToInt(boolean z) {
            return z ? 1 : 0;
        }

        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval x) {
            if (x instanceof StringEval) {
                return false;
            }
            if (!(x instanceof BoolEval)) {
                return x instanceof BlankEval ? getCode() == 2 : (x instanceof NumberEval) && getCode() == 2;
            }
            BoolEval be = (BoolEval) x;
            int testValue = boolToInt(be.getBooleanValue());
            return evaluate(testValue - this._value);
        }
    }

    public static final class ErrorMatcher extends MatcherBase {
        private final int _value;

        public ErrorMatcher(int errorCode, CmpOp operator) {
            super(operator);
            this._value = errorCode;
        }

        @Override // org.apache.poi.ss.formula.functions.Countif.MatcherBase
        protected String getValueText() {
            return FormulaError.forInt(this._value).getString();
        }

        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval x) {
            if (x instanceof ErrorEval) {
                int testValue = ((ErrorEval) x).getErrorCode();
                return evaluate(testValue - this._value);
            }
            return false;
        }

        public int getValue() {
            return this._value;
        }
    }

    public static final class StringMatcher extends MatcherBase {
        private final Pattern _pattern;
        private final String _value;

        public StringMatcher(String value, CmpOp operator) {
            super(operator);
            this._value = value;
            int code = operator.getCode();
            if (code == 0 || code == 1 || code == 2) {
                this._pattern = getWildCardPattern(value);
            } else {
                this._pattern = null;
            }
        }

        @Override // org.apache.poi.ss.formula.functions.Countif.MatcherBase
        protected String getValueText() {
            Pattern pattern = this._pattern;
            if (pattern == null) {
                return this._value;
            }
            return pattern.pattern();
        }

        @Override // org.apache.poi.ss.formula.functions.CountUtils.I_MatchPredicate
        public boolean matches(ValueEval x) {
            if (x instanceof BlankEval) {
                int code = getCode();
                return (code == 0 || code == 1) ? this._value.length() == 0 : code == 2 && this._value.length() != 0;
            }
            if (!(x instanceof StringEval)) {
                return false;
            }
            String testedValue = ((StringEval) x).getStringValue();
            if (testedValue.length() < 1 && this._value.length() < 1) {
                int code2 = getCode();
                return code2 == 0 || code2 == 2;
            }
            Pattern pattern = this._pattern;
            if (pattern != null) {
                return evaluate(pattern.matcher(testedValue).matches());
            }
            return evaluate(testedValue.compareToIgnoreCase(this._value));
        }

        /* JADX WARN: Removed duplicated region for block: B:29:0x0064  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public static java.util.regex.Pattern getWildCardPattern(java.lang.String r10) {
            /*
                int r0 = r10.length()
                java.lang.StringBuffer r1 = new java.lang.StringBuffer
                r1.<init>(r0)
                r2 = 0
                r3 = 0
            Lb:
                if (r3 >= r0) goto L71
                char r4 = r10.charAt(r3)
                r5 = 36
                if (r4 == r5) goto L64
                r5 = 46
                if (r4 == r5) goto L64
                r6 = 63
                if (r4 == r6) goto L5f
                r5 = 91
                if (r4 == r5) goto L64
                r7 = 93
                r8 = 126(0x7e, float:1.77E-43)
                if (r4 == r8) goto L3b
                if (r4 == r7) goto L64
                r5 = 94
                if (r4 == r5) goto L64
                switch(r4) {
                    case 40: goto L64;
                    case 41: goto L64;
                    case 42: goto L34;
                    default: goto L30;
                }
            L30:
                r1.append(r4)
                goto L6e
            L34:
                r2 = 1
                java.lang.String r5 = ".*"
                r1.append(r5)
                goto L6e
            L3b:
                int r9 = r3 + 1
                if (r9 >= r0) goto L5b
                int r9 = r3 + 1
                char r4 = r10.charAt(r9)
                r9 = 42
                if (r4 == r9) goto L4c
                if (r4 == r6) goto L4c
                goto L5b
            L4c:
                r2 = 1
                java.lang.StringBuffer r5 = r1.append(r5)
                java.lang.StringBuffer r5 = r5.append(r4)
                r5.append(r7)
                int r3 = r3 + 1
                goto L6e
            L5b:
                r1.append(r8)
                goto L6e
            L5f:
                r2 = 1
                r1.append(r5)
                goto L6e
            L64:
                java.lang.String r5 = "\\"
                java.lang.StringBuffer r5 = r1.append(r5)
                r5.append(r4)
            L6e:
                int r3 = r3 + 1
                goto Lb
            L71:
                if (r2 == 0) goto L7d
                java.lang.String r3 = r1.toString()
                r4 = 2
                java.util.regex.Pattern r3 = java.util.regex.Pattern.compile(r3, r4)
                return r3
            L7d:
                r3 = 0
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.formula.functions.Countif.StringMatcher.getWildCardPattern(java.lang.String):java.util.regex.Pattern");
        }
    }

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        CountUtils.I_MatchPredicate mp = createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        if (mp == null) {
            return NumberEval.ZERO;
        }
        double result = countMatchingCellsInArea(arg0, mp);
        return new NumberEval(result);
    }

    private double countMatchingCellsInArea(ValueEval rangeArg, CountUtils.I_MatchPredicate criteriaPredicate) {
        if (rangeArg instanceof RefEval) {
            return CountUtils.countMatchingCellsInRef((RefEval) rangeArg, criteriaPredicate);
        }
        if (rangeArg instanceof ThreeDEval) {
            return CountUtils.countMatchingCellsInArea((ThreeDEval) rangeArg, criteriaPredicate);
        }
        throw new IllegalArgumentException("Bad range arg type (" + rangeArg.getClass().getName() + ")");
    }

    static CountUtils.I_MatchPredicate createCriteriaPredicate(ValueEval arg, int srcRowIndex, int srcColumnIndex) {
        ValueEval evaluatedCriteriaArg = evaluateCriteriaArg(arg, srcRowIndex, srcColumnIndex);
        if (evaluatedCriteriaArg instanceof NumberEval) {
            return new NumberMatcher(((NumberEval) evaluatedCriteriaArg).getNumberValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof BoolEval) {
            return new BooleanMatcher(((BoolEval) evaluatedCriteriaArg).getBooleanValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof StringEval) {
            return createGeneralMatchPredicate((StringEval) evaluatedCriteriaArg);
        }
        if (evaluatedCriteriaArg instanceof ErrorEval) {
            return new ErrorMatcher(((ErrorEval) evaluatedCriteriaArg).getErrorCode(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg == BlankEval.instance) {
            return null;
        }
        throw new RuntimeException("Unexpected type for criteria (" + evaluatedCriteriaArg.getClass().getName() + ")");
    }

    private static ValueEval evaluateCriteriaArg(ValueEval arg, int srcRowIndex, int srcColumnIndex) {
        try {
            return OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static CountUtils.I_MatchPredicate createGeneralMatchPredicate(StringEval stringEval) {
        String value = stringEval.getStringValue();
        CmpOp operator = CmpOp.getOperator(value);
        String value2 = value.substring(operator.getLength());
        Boolean booleanVal = parseBoolean(value2);
        if (booleanVal != null) {
            return new BooleanMatcher(booleanVal.booleanValue(), operator);
        }
        Double doubleVal = OperandResolver.parseDouble(value2);
        if (doubleVal != null) {
            return new NumberMatcher(doubleVal.doubleValue(), operator);
        }
        ErrorEval ee = parseError(value2);
        if (ee != null) {
            return new ErrorMatcher(ee.getErrorCode(), operator);
        }
        return new StringMatcher(value2, operator);
    }

    private static ErrorEval parseError(String value) {
        if (value.length() < 4 || value.charAt(0) != '#') {
            return null;
        }
        if (value.equals("#NULL!")) {
            return ErrorEval.NULL_INTERSECTION;
        }
        if (value.equals("#DIV/0!")) {
            return ErrorEval.DIV_ZERO;
        }
        if (value.equals("#VALUE!")) {
            return ErrorEval.VALUE_INVALID;
        }
        if (value.equals("#REF!")) {
            return ErrorEval.REF_INVALID;
        }
        if (value.equals("#NAME?")) {
            return ErrorEval.NAME_INVALID;
        }
        if (value.equals("#NUM!")) {
            return ErrorEval.NUM_ERROR;
        }
        if (value.equals("#N/A")) {
            return ErrorEval.NA;
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x001f  */
    /* JADX WARN: Removed duplicated region for block: B:18:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static java.lang.Boolean parseBoolean(java.lang.String r3) {
        /*
            int r0 = r3.length()
            r1 = 0
            r2 = 1
            if (r0 >= r2) goto L9
            return r1
        L9:
            r0 = 0
            char r0 = r3.charAt(r0)
            r2 = 70
            if (r0 == r2) goto L2a
            r2 = 84
            if (r0 == r2) goto L1f
            r2 = 102(0x66, float:1.43E-43)
            if (r0 == r2) goto L2a
            r2 = 116(0x74, float:1.63E-43)
            if (r0 == r2) goto L1f
            goto L35
        L1f:
            java.lang.String r0 = "TRUE"
            boolean r0 = r0.equalsIgnoreCase(r3)
            if (r0 == 0) goto L35
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            return r0
        L2a:
            java.lang.String r0 = "FALSE"
            boolean r0 = r0.equalsIgnoreCase(r3)
            if (r0 == 0) goto L35
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            return r0
        L35:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.formula.functions.Countif.parseBoolean(java.lang.String):java.lang.Boolean");
    }
}
