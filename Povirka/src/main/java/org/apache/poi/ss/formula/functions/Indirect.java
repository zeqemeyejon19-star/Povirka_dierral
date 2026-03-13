package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.usermodel.Table;

/* JADX INFO: loaded from: classes.dex */
public final class Indirect implements FreeRefFunction {
    public static final FreeRefFunction instance = new Indirect();

    private Indirect() {
    }

    @Override // org.apache.poi.ss.formula.functions.FreeRefFunction
    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) throws EvaluationException {
        boolean isA1style;
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            ValueEval ve = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
            String text = OperandResolver.coerceValueToString(ve);
            try {
                int length = args.length;
                if (length == 1) {
                    isA1style = true;
                } else {
                    try {
                        if (length == 2) {
                            isA1style = evaluateBooleanArg(args[1], ec);
                        } else {
                            return ErrorEval.VALUE_INVALID;
                        }
                    } catch (EvaluationException e) {
                        e = e;
                        return e.getErrorEval();
                    }
                }
                return evaluateIndirect(ec, text, isA1style);
            } catch (EvaluationException e2) {
                e = e2;
            }
        } catch (EvaluationException e3) {
            e = e3;
        }
    }

    private static boolean evaluateBooleanArg(ValueEval arg, OperationEvaluationContext ec) throws EvaluationException {
        ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());
        if (ve == BlankEval.instance || ve == MissingArgEval.instance) {
            return false;
        }
        return OperandResolver.coerceValueToBoolean(ve, false).booleanValue();
    }

    private static ValueEval evaluateIndirect(OperationEvaluationContext ec, String text, boolean isA1style) {
        String workbookName;
        String workbookName2;
        String sheetName;
        String refStrPart1;
        String refStrPart2;
        int plingPos = text.lastIndexOf(33);
        if (plingPos < 0) {
            workbookName = null;
            workbookName2 = null;
            sheetName = text;
        } else {
            String[] parts = parseWorkbookAndSheetName(text.subSequence(0, plingPos));
            if (parts == null) {
                return ErrorEval.REF_INVALID;
            }
            String workbookName3 = parts[0];
            String sheetName2 = parts[1];
            workbookName = workbookName3;
            workbookName2 = sheetName2;
            sheetName = text.substring(plingPos + 1);
        }
        if (Table.isStructuredReference.matcher(sheetName).matches()) {
            try {
                Area3DPxg areaPtg = FormulaParser.parseStructuredReference(sheetName, (FormulaParsingWorkbook) ec.getWorkbook(), ec.getRowIndex());
                return ec.getArea3DEval(areaPtg);
            } catch (FormulaParseException e) {
                return ErrorEval.REF_INVALID;
            }
        }
        int colonPos = sheetName.indexOf(58);
        if (colonPos < 0) {
            refStrPart1 = sheetName.trim();
            refStrPart2 = null;
        } else {
            String refStrPart12 = sheetName.substring(0, colonPos);
            refStrPart1 = refStrPart12.trim();
            refStrPart2 = sheetName.substring(colonPos + 1).trim();
        }
        return ec.getDynamicReference(workbookName, workbookName2, refStrPart1, refStrPart2, isA1style);
    }

    private static String[] parseWorkbookAndSheetName(CharSequence text) {
        String wbName;
        int rbPos;
        int lastIx = text.length() - 1;
        if (lastIx < 0 || canTrim(text)) {
            return null;
        }
        char firstChar = text.charAt(0);
        if (Character.isWhitespace(firstChar)) {
            return null;
        }
        if (firstChar == '\'') {
            if (text.charAt(lastIx) != '\'') {
                return null;
            }
            char firstChar2 = text.charAt(1);
            if (Character.isWhitespace(firstChar2)) {
                return null;
            }
            if (firstChar2 == '[') {
                int rbPos2 = text.toString().lastIndexOf(93);
                if (rbPos2 < 0 || (wbName = unescapeString(text.subSequence(2, rbPos2))) == null || canTrim(wbName)) {
                    return null;
                }
                rbPos = rbPos2 + 1;
            } else {
                wbName = null;
                rbPos = 1;
            }
            String sheetName = unescapeString(text.subSequence(rbPos, lastIx));
            if (sheetName == null) {
                return null;
            }
            return new String[]{wbName, sheetName};
        }
        if (firstChar == '[') {
            int rbPos3 = text.toString().lastIndexOf(93);
            if (rbPos3 < 0) {
                return null;
            }
            CharSequence wbName2 = text.subSequence(1, rbPos3);
            if (canTrim(wbName2)) {
                return null;
            }
            CharSequence sheetName2 = text.subSequence(rbPos3 + 1, text.length());
            if (canTrim(sheetName2)) {
                return null;
            }
            return new String[]{wbName2.toString(), sheetName2.toString()};
        }
        return new String[]{null, text.toString()};
    }

    private static String unescapeString(CharSequence text) {
        int len = text.length();
        StringBuilder sb = new StringBuilder(len);
        int i = 0;
        while (i < len) {
            char ch = text.charAt(i);
            if (ch == '\'' && ((i = i + 1) >= len || (ch = text.charAt(i)) != '\'')) {
                return null;
            }
            sb.append(ch);
            i++;
        }
        return sb.toString();
    }

    private static boolean canTrim(CharSequence text) {
        int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return false;
        }
        return Character.isWhitespace(text.charAt(0)) || Character.isWhitespace(text.charAt(lastIx));
    }
}
