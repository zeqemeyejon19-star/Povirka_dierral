package org.apache.poi.ss.formula.functions;

import java.util.Locale;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public class Complex extends Var2or3ArgFunction implements FreeRefFunction {
    public static final String DEFAULT_SUFFIX = "i";
    public static final String SUPPORTED_SUFFIX = "j";
    public static final FreeRefFunction instance = new Complex();

    @Override // org.apache.poi.ss.formula.functions.Function2Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval real_num, ValueEval i_num) {
        return evaluate(srcRowIndex, srcColumnIndex, real_num, i_num, new StringEval(DEFAULT_SUFFIX));
    }

    @Override // org.apache.poi.ss.formula.functions.Function3Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval real_num, ValueEval i_num, ValueEval suffix) {
        try {
            ValueEval veText1 = OperandResolver.getSingleValue(real_num, srcRowIndex, srcColumnIndex);
            try {
                double realNum = OperandResolver.coerceValueToDouble(veText1);
                try {
                    ValueEval veINum = OperandResolver.getSingleValue(i_num, srcRowIndex, srcColumnIndex);
                    try {
                        double realINum = OperandResolver.coerceValueToDouble(veINum);
                        String suffixValue = OperandResolver.coerceValueToString(suffix);
                        if (suffixValue.length() == 0) {
                            suffixValue = DEFAULT_SUFFIX;
                        }
                        if (suffixValue.equals(DEFAULT_SUFFIX.toUpperCase(Locale.ROOT)) || suffixValue.equals(SUPPORTED_SUFFIX.toUpperCase(Locale.ROOT))) {
                            return ErrorEval.VALUE_INVALID;
                        }
                        if (!suffixValue.equals(DEFAULT_SUFFIX) && !suffixValue.equals(SUPPORTED_SUFFIX)) {
                            return ErrorEval.VALUE_INVALID;
                        }
                        StringBuffer strb = new StringBuffer("");
                        if (realNum != 0.0d) {
                            if (isDoubleAnInt(realNum)) {
                                strb.append((int) realNum);
                            } else {
                                strb.append(realNum);
                            }
                        }
                        if (realINum != 0.0d) {
                            if (strb.length() != 0 && realINum > 0.0d) {
                                strb.append("+");
                            }
                            if (realINum != 1.0d && realINum != -1.0d) {
                                if (isDoubleAnInt(realINum)) {
                                    strb.append((int) realINum);
                                } else {
                                    strb.append(realINum);
                                }
                            }
                            strb.append(suffixValue);
                        }
                        return new StringEval(strb.toString());
                    } catch (EvaluationException e) {
                        return ErrorEval.VALUE_INVALID;
                    }
                } catch (EvaluationException e2) {
                    return e2.getErrorEval();
                }
            } catch (EvaluationException e3) {
                return ErrorEval.VALUE_INVALID;
            }
        } catch (EvaluationException e4) {
            return e4.getErrorEval();
        }
    }

    private boolean isDoubleAnInt(double number) {
        return number == Math.floor(number) && !Double.isInfinite(number);
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 2) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        if (args.length == 3) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }
}
