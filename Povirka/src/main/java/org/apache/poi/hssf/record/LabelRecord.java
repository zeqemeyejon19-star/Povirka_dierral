package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class LabelRecord extends Record implements CellValueRecordInterface, Cloneable {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) LabelRecord.class);
    public static final short sid = 516;
    private int field_1_row;
    private short field_2_column;
    private short field_3_xf_index;
    private short field_4_string_len;
    private byte field_5_unicode_flag;
    private String field_6_value;

    public LabelRecord() {
    }

    public LabelRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_column = in.readShort();
        this.field_3_xf_index = in.readShort();
        this.field_4_string_len = in.readShort();
        this.field_5_unicode_flag = in.readByte();
        if (this.field_4_string_len > 0) {
            if (isUnCompressedUnicode()) {
                this.field_6_value = in.readUnicodeLEString(this.field_4_string_len);
            } else {
                this.field_6_value = in.readCompressedUnicode(this.field_4_string_len);
            }
        } else {
            this.field_6_value = "";
        }
        if (in.remaining() > 0) {
            logger.log(3, "LabelRecord data remains: " + in.remaining() + " : " + HexDump.toHex(in.readRemainder()));
        }
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public int getRow() {
        return this.field_1_row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getColumn() {
        return this.field_2_column;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public short getXFIndex() {
        return this.field_3_xf_index;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public boolean isUnCompressedUnicode() {
        return (this.field_5_unicode_flag & 1) != 0;
    }

    public String getValue() {
        return this.field_6_value;
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public int getRecordSize() {
        throw new RecordFormatException("Label Records are supported READ ONLY...convert to LabelSST");
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 516;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[LABEL]\n");
        sb.append("    .row       = ").append(HexDump.shortToHex(getRow())).append("\n");
        sb.append("    .column    = ").append(HexDump.shortToHex(getColumn())).append("\n");
        sb.append("    .xfindex   = ").append(HexDump.shortToHex(getXFIndex())).append("\n");
        sb.append("    .string_len= ").append(HexDump.shortToHex(this.field_4_string_len)).append("\n");
        sb.append("    .unicode_flag= ").append(HexDump.byteToHex(this.field_5_unicode_flag)).append("\n");
        sb.append("    .value       = ").append(getValue()).append("\n");
        sb.append("[/LABEL]\n");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setColumn(short col) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setRow(int row) {
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public void setXFIndex(short xf) {
    }

    @Override // org.apache.poi.hssf.record.Record
    public LabelRecord clone() {
        LabelRecord rec = new LabelRecord();
        rec.field_1_row = this.field_1_row;
        rec.field_2_column = this.field_2_column;
        rec.field_3_xf_index = this.field_3_xf_index;
        rec.field_4_string_len = this.field_4_string_len;
        rec.field_5_unicode_flag = this.field_5_unicode_flag;
        rec.field_6_value = this.field_6_value;
        return rec;
    }
}
