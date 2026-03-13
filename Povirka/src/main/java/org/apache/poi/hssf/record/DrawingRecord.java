package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class DrawingRecord extends StandardRecord implements Cloneable {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final short sid = 236;
    private byte[] contd;
    private byte[] recordData;

    public DrawingRecord() {
        this.recordData = EMPTY_BYTE_ARRAY;
    }

    public DrawingRecord(RecordInputStream in) {
        this.recordData = in.readRemainder();
    }

    @Deprecated
    public void processContinueRecord(byte[] record) {
        this.contd = record;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.write(this.recordData);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return this.recordData.length;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 236;
    }

    public byte[] getRecordData() {
        return this.recordData;
    }

    public void setData(byte[] thedata) {
        if (thedata == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        this.recordData = thedata;
    }

    @Override // org.apache.poi.hssf.record.Record
    public DrawingRecord clone() {
        DrawingRecord rec = new DrawingRecord();
        rec.recordData = (byte[]) this.recordData.clone();
        byte[] bArr = this.contd;
        if (bArr != null) {
            rec.contd = (byte[]) bArr.clone();
        }
        return rec;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        return "DrawingRecord[" + this.recordData.length + "]";
    }
}
