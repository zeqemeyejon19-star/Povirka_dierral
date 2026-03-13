package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class HCenterRecord extends StandardRecord implements Cloneable {
    public static final short sid = 131;
    private short field_1_hcenter;

    public HCenterRecord() {
    }

    public HCenterRecord(RecordInputStream in) {
        this.field_1_hcenter = in.readShort();
    }

    public void setHCenter(boolean hc) {
        if (hc) {
            this.field_1_hcenter = (short) 1;
        } else {
            this.field_1_hcenter = (short) 0;
        }
    }

    public boolean getHCenter() {
        return this.field_1_hcenter == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[HCENTER]\n");
        buffer.append("    .hcenter        = ").append(getHCenter()).append("\n");
        buffer.append("[/HCENTER]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_hcenter);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 131;
    }

    @Override // org.apache.poi.hssf.record.Record
    public HCenterRecord clone() {
        HCenterRecord rec = new HCenterRecord();
        rec.field_1_hcenter = this.field_1_hcenter;
        return rec;
    }
}
