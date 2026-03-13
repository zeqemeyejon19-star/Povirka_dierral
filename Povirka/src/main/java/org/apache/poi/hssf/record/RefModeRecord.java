package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class RefModeRecord extends StandardRecord {
    public static final short USE_A1_MODE = 1;
    public static final short USE_R1C1_MODE = 0;
    public static final short sid = 15;
    private short field_1_mode;

    public RefModeRecord() {
    }

    public RefModeRecord(RecordInputStream in) {
        this.field_1_mode = in.readShort();
    }

    public void setMode(short mode) {
        this.field_1_mode = mode;
    }

    public short getMode() {
        return this.field_1_mode;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[REFMODE]\n");
        buffer.append("    .mode           = ").append(Integer.toHexString(getMode())).append("\n");
        buffer.append("[/REFMODE]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(getMode());
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 15;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        RefModeRecord rec = new RefModeRecord();
        rec.field_1_mode = this.field_1_mode;
        return rec;
    }
}
