package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class AxisLineFormatRecord extends StandardRecord implements Cloneable {
    public static final short AXIS_TYPE_AXIS_LINE = 0;
    public static final short AXIS_TYPE_MAJOR_GRID_LINE = 1;
    public static final short AXIS_TYPE_MINOR_GRID_LINE = 2;
    public static final short AXIS_TYPE_WALLS_OR_FLOOR = 3;
    public static final short sid = 4129;
    private short field_1_axisType;

    public AxisLineFormatRecord() {
    }

    public AxisLineFormatRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXISLINEFORMAT]\n");
        buffer.append("    .axisType             = ").append("0x").append(HexDump.toHex(getAxisType())).append(" (").append((int) getAxisType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXISLINEFORMAT]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
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
    public AxisLineFormatRecord clone() {
        AxisLineFormatRecord rec = new AxisLineFormatRecord();
        rec.field_1_axisType = this.field_1_axisType;
        return rec;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }
}
