package org.apache.poi.ss.formula.functions;

import java.util.Date;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DateUtil;

/* JADX INFO: loaded from: classes.dex */
public final class Now extends Fixed0ArgFunction {
    @Override // org.apache.poi.ss.formula.functions.Function0Arg
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex) {
        Date now = new Date(System.currentTimeMillis());
        return new NumberEval(DateUtil.getExcelDate(now));
    }
}
