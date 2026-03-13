package org.apache.poi.hssf.eventusermodel.dummyrecord;

/* JADX INFO: loaded from: classes.dex */
public final class MissingRowDummyRecord extends DummyRecordBase {
    private int rowNumber;

    @Override // org.apache.poi.hssf.eventusermodel.dummyrecord.DummyRecordBase, org.apache.poi.hssf.record.RecordBase
    public /* bridge */ /* synthetic */ int serialize(int x0, byte[] x1) {
        return super.serialize(x0, x1);
    }

    public MissingRowDummyRecord(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getRowNumber() {
        return this.rowNumber;
    }
}
