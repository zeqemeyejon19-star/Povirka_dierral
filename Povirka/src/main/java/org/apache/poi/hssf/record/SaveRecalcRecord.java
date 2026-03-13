package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class SaveRecalcRecord extends StandardRecord {
    public static final short sid = 95;
    private short field_1_recalc;

    public SaveRecalcRecord() {
    }

    public SaveRecalcRecord(RecordInputStream in) {
        this.field_1_recalc = in.readShort();
    }

    public void setRecalc(boolean recalc) {
        this.field_1_recalc = (short) (!recalc ? 0 : 1);
    }

    public boolean getRecalc() {
        return this.field_1_recalc == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SAVERECALC]\n");
        buffer.append("    .recalc         = ").append(getRecalc()).append("\n");
        buffer.append("[/SAVERECALC]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_recalc);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 95;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SaveRecalcRecord rec = new SaveRecalcRecord();
        rec.field_1_recalc = this.field_1_recalc;
        return rec;
    }
}
