package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface TableStyle {
    int getIndex();

    String getName();

    DifferentialStyleProvider getStyle(TableStyleType tableStyleType);

    boolean isBuiltin();
}
