package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class SeriesToChartGroupRecord extends StandardRecord {
    public static final short sid = 4165;
    private short field_1_chartGroupIndex;

    public SeriesToChartGroupRecord() {
    }

    public SeriesToChartGroupRecord(RecordInputStream in) {
        this.field_1_chartGroupIndex = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SeriesToChartGroup]\n");
        buffer.append("    .chartGroupIndex      = ").append("0x").append(HexDump.toHex(getChartGroupIndex())).append(" (").append((int) getChartGroupIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/SeriesToChartGroup]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_chartGroupIndex);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 4165;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        SeriesToChartGroupRecord rec = new SeriesToChartGroupRecord();
        rec.field_1_chartGroupIndex = this.field_1_chartGroupIndex;
        return rec;
    }

    public short getChartGroupIndex() {
        return this.field_1_chartGroupIndex;
    }

    public void setChartGroupIndex(short field_1_chartGroupIndex) {
        this.field_1_chartGroupIndex = field_1_chartGroupIndex;
    }
}
