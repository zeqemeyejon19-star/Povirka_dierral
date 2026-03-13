package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

/* JADX INFO: loaded from: classes.dex */
public final class Value extends Fixed1ArgFunction {
    private static final int MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR = 4;
    private static final Double ZERO = new Double(0.0d);

    @Override // org.apache.poi.ss.formula.functions.Function1Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0) {
        try {
            ValueEval veText = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            String strText = OperandResolver.coerceValueToString(veText);
            Double result = convertTextToNumber(strText);
            if (result == null) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(result.doubleValue());
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }

    private static Double convertTextToNumber(String str) {
        int length = str.length();
        boolean z = false;
        int i = 0;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (i < length) {
            char cCharAt = str.charAt(i);
            if (Character.isDigit(cCharAt) || cCharAt == '.') {
                break;
            }
            if (cCharAt != ' ') {
                if (cCharAt != '$') {
                    if (cCharAt == '+') {
                        if (z3 || z4) {
                            return null;
                        }
                        z4 = true;
                    } else {
                        if (cCharAt != '-' || z3 || z4) {
                            return null;
                        }
                        z3 = true;
                    }
                } else {
                    if (z2) {
                        return null;
                    }
                    z2 = true;
                }
            }
            i++;
        }
        if (i >= length) {
            if (z2 || z3 || z4) {
                return null;
            }
            return ZERO;
        }
        StringBuffer stringBuffer = new StringBuffer(length);
        int i2 = i;
        boolean z5 = false;
        int i3 = -32768;
        while (i2 < length) {
            char cCharAt2 = str.charAt(i2);
            if (Character.isDigit(cCharAt2)) {
                stringBuffer.append(cCharAt2);
            } else if (cCharAt2 == ' ') {
                String strTrim = str.substring(i2).trim();
                if (strTrim.equals("%")) {
                    z5 = true;
                } else if (strTrim.length() > 0) {
                    return null;
                }
            } else if (cCharAt2 == '%') {
                z5 = true;
            } else if (cCharAt2 != ',') {
                if (cCharAt2 != '.') {
                    if ((cCharAt2 != 'E' && cCharAt2 != 'e') || i2 - i3 < 4) {
                        return null;
                    }
                    stringBuffer.append(str.substring(i2));
                    i2 = length;
                } else {
                    if (z || i2 - i3 < 4) {
                        return null;
                    }
                    stringBuffer.append('.');
                    z = true;
                }
            } else {
                if (z || i2 - i3 < 4) {
                    return null;
                }
                i3 = i2;
            }
            i2++;
        }
        if (!z && i2 - i3 < 4) {
            return null;
        }
        try {
            double d = Double.parseDouble(stringBuffer.toString());
            if (z3) {
                d = -d;
            }
            if (z5) {
                d /= 100.0d;
            }
            return Double.valueOf(d);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
