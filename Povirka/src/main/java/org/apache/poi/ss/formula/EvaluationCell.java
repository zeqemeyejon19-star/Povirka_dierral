package org.apache.poi.ss.formula;

import org.apache.poi.ss.usermodel.CellType;

/* JADX INFO: loaded from: classes.dex */
public interface EvaluationCell {
    boolean getBooleanCellValue();

    int getCachedFormulaResultType();

    CellType getCachedFormulaResultTypeEnum();

    int getCellType();

    CellType getCellTypeEnum();

    int getColumnIndex();

    int getErrorCellValue();

    Object getIdentityKey();

    double getNumericCellValue();

    int getRowIndex();

    EvaluationSheet getSheet();

    String getStringCellValue();
}
