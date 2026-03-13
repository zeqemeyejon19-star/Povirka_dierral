package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class CalcCountRecord extends StandardRecord implements Cloneable {
    public static final short sid = 12;
    private short field_1_iterations;

    public CalcCountRecord() {
    }

    public CalcCountRecord(RecordInputStream in) {
        this.field_1_iterations = in.readShort();
    }

    public void setIterations(short iterations) {
        this.field_1_iterations = iterations;
    }

    public short getIterations() {
        return this.field_1_iterations;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CALCCOUNT]\n");
        buffer.append("    .iterations     = ").append(Integer.toHexString(getIterations())).append("\n");
        buffer.append("[/CALCCOUNT]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(getIterations());
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 12;
    }

    @Override // org.apache.poi.hssf.record.Record
    public CalcCountRecord clone() {
        CalcCountRecord rec = new CalcCountRecord();
        rec.field_1_iterations = this.field_1_iterations;
        return rec;
    }
}
