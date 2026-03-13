package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class GridsetRecord extends StandardRecord implements Cloneable {
    public static final short sid = 130;
    public short field_1_gridset_flag;

    public GridsetRecord() {
    }

    public GridsetRecord(RecordInputStream in) {
        this.field_1_gridset_flag = in.readShort();
    }

    public void setGridset(boolean gridset) {
        if (gridset) {
            this.field_1_gridset_flag = (short) 1;
        } else {
            this.field_1_gridset_flag = (short) 0;
        }
    }

    public boolean getGridset() {
        return this.field_1_gridset_flag == 1;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[GRIDSET]\n");
        buffer.append("    .gridset        = ").append(getGridset()).append("\n");
        buffer.append("[/GRIDSET]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_gridset_flag);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 130;
    }

    @Override // org.apache.poi.hssf.record.Record
    public GridsetRecord clone() {
        GridsetRecord rec = new GridsetRecord();
        rec.field_1_gridset_flag = this.field_1_gridset_flag;
        return rec;
    }
}
