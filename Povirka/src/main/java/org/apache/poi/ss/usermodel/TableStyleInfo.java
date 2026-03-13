package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface TableStyleInfo {
    String getName();

    TableStyle getStyle();

    boolean isShowColumnStripes();

    boolean isShowFirstColumn();

    boolean isShowLastColumn();

    boolean isShowRowStripes();
}
