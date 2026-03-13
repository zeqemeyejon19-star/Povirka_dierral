package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class DefaultColWidthRecord extends StandardRecord implements Cloneable {
    public static final int DEFAULT_COLUMN_WIDTH = 8;
    public static final short sid = 85;
    private int field_1_col_width;

    public DefaultColWidthRecord() {
        this.field_1_col_width = 8;
    }

    public DefaultColWidthRecord(RecordInputStream in) {
        this.field_1_col_width = in.readUShort();
    }

    public void setColWidth(int width) {
        this.field_1_col_width = width;
    }

    public int getColWidth() {
        return this.field_1_col_width;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DEFAULTCOLWIDTH]\n");
        buffer.append("    .colwidth      = ").append(Integer.toHexString(getColWidth())).append("\n");
        buffer.append("[/DEFAULTCOLWIDTH]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(getColWidth());
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 85;
    }

    @Override // org.apache.poi.hssf.record.Record
    public DefaultColWidthRecord clone() {
        DefaultColWidthRecord rec = new DefaultColWidthRecord();
        rec.field_1_col_width = this.field_1_col_width;
        return rec;
    }
}
