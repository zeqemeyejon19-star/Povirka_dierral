package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class NumberFormatIndexRecord extends StandardRecord implements Cloneable {
    public static final short sid = 4174;
    private short field_1_formatIndex;

    public NumberFormatIndexRecord() {
    }

    public NumberFormatIndexRecord(RecordInputStream in) {
        this.field_1_formatIndex = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[IFMT]\n");
        buffer.append("    .formatIndex          = ").append("0x").append(HexDump.toHex(getFormatIndex())).append(" (").append((int) getFormatIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/IFMT]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_formatIndex);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public NumberFormatIndexRecord clone() {
        NumberFormatIndexRecord rec = new NumberFormatIndexRecord();
        rec.field_1_formatIndex = this.field_1_formatIndex;
        return rec;
    }

    public short getFormatIndex() {
        return this.field_1_formatIndex;
    }

    public void setFormatIndex(short field_1_formatIndex) {
        this.field_1_formatIndex = field_1_formatIndex;
    }
}
