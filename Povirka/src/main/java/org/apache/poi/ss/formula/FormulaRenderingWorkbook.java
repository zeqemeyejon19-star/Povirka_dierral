package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;

/* JADX INFO: loaded from: classes.dex */
public interface FormulaRenderingWorkbook {
    EvaluationWorkbook.ExternalSheet getExternalSheet(int i);

    String getNameText(NamePtg namePtg);

    String getSheetFirstNameByExternSheet(int i);

    String getSheetLastNameByExternSheet(int i);

    String resolveNameXText(NameXPtg nameXPtg);
}
