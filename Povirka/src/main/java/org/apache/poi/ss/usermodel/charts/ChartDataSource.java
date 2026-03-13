package org.apache.poi.ss.usermodel.charts;

/* JADX INFO: loaded from: classes.dex */
public interface ChartDataSource<T> {
    String getFormulaString();

    T getPointAt(int i);

    int getPointCount();

    boolean isNumeric();

    boolean isReference();
}
