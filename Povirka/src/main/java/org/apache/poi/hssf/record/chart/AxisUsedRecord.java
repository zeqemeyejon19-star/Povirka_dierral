package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class AxisUsedRecord extends StandardRecord implements Cloneable {
    public static final short sid = 4166;
    private short field_1_numAxis;

    public AxisUsedRecord() {
    }

    public AxisUsedRecord(RecordInputStream in) {
        this.field_1_numAxis = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXISUSED]\n");
        buffer.append("    .numAxis              = ").append("0x").append(HexDump.toHex(getNumAxis())).append(" (").append((int) getNumAxis()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXISUSED]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numAxis);
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
    public AxisUsedRecord clone() {
        AxisUsedRecord rec = new AxisUsedRecord();
        rec.field_1_numAxis = this.field_1_numAxis;
        return rec;
    }

    public short getNumAxis() {
        return this.field_1_numAxis;
    }

    public void setNumAxis(short field_1_numAxis) {
        this.field_1_numAxis = field_1_numAxis;
    }
}
