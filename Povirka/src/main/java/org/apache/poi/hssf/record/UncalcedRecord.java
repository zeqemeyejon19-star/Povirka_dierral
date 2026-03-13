package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class UncalcedRecord extends StandardRecord {
    public static final short sid = 94;
    private short _reserved;

    public UncalcedRecord() {
        this._reserved = (short) 0;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 94;
    }

    public UncalcedRecord(RecordInputStream in) {
        this._reserved = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[UNCALCED]\n");
        buffer.append("    _reserved: ").append((int) this._reserved).append('\n');
        buffer.append("[/UNCALCED]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._reserved);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    public static int getStaticRecordSize() {
        return 6;
    }
}
