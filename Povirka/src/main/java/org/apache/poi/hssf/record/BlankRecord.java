package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class BlankRecord extends StandardRecord implements CellValueRecordInterface, Cloneable {
    public static final short sid = 513;
    private int field_1_row;
    private short field_2_col;
    private short field_3_xf;

    public BlankRecord() {
    }

    public BlankRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_xf = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int row) {
        this.field_1_row = row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_col;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short xf) {
        this.field_3_xf = xf;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short col) {
        this.field_2_col = col;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 513;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[BLANK]\n");
        sb.append("    row= ").append(HexDump.shortToHex(getRow())).append("\n");
        sb.append("    col= ").append(HexDump.shortToHex(getColumn())).append("\n");
        sb.append("    xf = ").append(HexDump.shortToHex(getXFIndex())).append("\n");
        sb.append("[/BLANK]\n");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(getRow());
        out.writeShort(getColumn());
        out.writeShort(getXFIndex());
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 6;
    }

    @Override // org.apache.poi.hssf.record.Record
    public BlankRecord clone() {
        BlankRecord rec = new BlankRecord();
        rec.field_1_row = this.field_1_row;
        rec.field_2_col = this.field_2_col;
        rec.field_3_xf = this.field_3_xf;
        return rec;
    }
}
