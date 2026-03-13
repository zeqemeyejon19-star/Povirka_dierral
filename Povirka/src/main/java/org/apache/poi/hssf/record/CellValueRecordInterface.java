package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes.dex */
public interface CellValueRecordInterface {
    short getColumn();

    int getRow();

    short getXFIndex();

    void setColumn(short s);

    void setRow(int i);

    void setXFIndex(short s);
}
