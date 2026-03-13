package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public class Address implements Function {
    public static final int REF_ABSOLUTE = 1;
    public static final int REF_RELATIVE = 4;
    public static final int REF_ROW_ABSOLUTE_COLUMN_RELATIVE = 2;
    public static final int REF_ROW_RELATIVE_RELATIVE_ABSOLUTE = 3;

    @Override // org.apache.poi.ss.formula.functions.Function
    public ValueEval evaluate(ValueEval[] args, int srcRowIndex, int srcColumnIndex) {
        boolean pAbsRow;
        boolean pAbsCol;
        String sheetName;
        if (args.length < 2 || args.length > 5) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            int row = (int) NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            int col = (int) NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            int refType = (args.length <= 2 || args[2] == MissingArgEval.instance) ? 1 : (int) NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
            if (refType == 1) {
                pAbsRow = true;
                pAbsCol = true;
            } else if (refType == 2) {
                pAbsRow = true;
                pAbsCol = false;
            } else if (refType == 3) {
                pAbsRow = false;
                pAbsCol = true;
            } else {
                if (refType != 4) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                pAbsRow = false;
                pAbsCol = false;
            }
            if (args.length == 5) {
                ValueEval ve = OperandResolver.getSingleValue(args[4], srcRowIndex, srcColumnIndex);
                sheetName = ve == MissingArgEval.instance ? null : OperandResolver.coerceValueToString(ve);
            } else {
                sheetName = null;
            }
            CellReference ref = new CellReference(row - 1, col - 1, pAbsRow, pAbsCol);
            StringBuffer sb = new StringBuffer(32);
            if (sheetName != null) {
                SheetNameFormatter.appendFormat(sb, sheetName);
                sb.append('!');
            }
            sb.append(ref.formatAsString());
            return new StringEval(sb.toString());
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
