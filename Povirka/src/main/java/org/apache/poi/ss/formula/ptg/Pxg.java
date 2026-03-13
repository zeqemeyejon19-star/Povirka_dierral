package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public interface Pxg {
    int getExternalWorkbookNumber();

    String getSheetName();

    void setSheetName(String str);

    String toFormulaString();
}
