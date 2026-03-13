package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class AxisParentRecord extends StandardRecord implements Cloneable {
    public static final short AXIS_TYPE_MAIN = 0;
    public static final short AXIS_TYPE_SECONDARY = 1;
    public static final short sid = 4161;
    private short field_1_axisType;
    private int field_2_x;
    private int field_3_y;
    private int field_4_width;
    private int field_5_height;

    public AxisParentRecord() {
    }

    public AxisParentRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
        this.field_2_x = in.readInt();
        this.field_3_y = in.readInt();
        this.field_4_width = in.readInt();
        this.field_5_height = in.readInt();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[AXISPARENT]\n");
        buffer.append("    .axisType             = ").append("0x").append(HexDump.toHex(getAxisType())).append(" (").append((int) getAxisType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .x                    = ").append("0x").append(HexDump.toHex(getX())).append(" (").append(getX()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .y                    = ").append("0x").append(HexDump.toHex(getY())).append(" (").append(getY()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .width                = ").append("0x").append(HexDump.toHex(getWidth())).append(" (").append(getWidth()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .height               = ").append("0x").append(HexDump.toHex(getHeight())).append(" (").append(getHeight()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("[/AXISPARENT]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
        out.writeInt(this.field_2_x);
        out.writeInt(this.field_3_y);
        out.writeInt(this.field_4_width);
        out.writeInt(this.field_5_height);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 18;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public AxisParentRecord clone() {
        AxisParentRecord rec = new AxisParentRecord();
        rec.field_1_axisType = this.field_1_axisType;
        rec.field_2_x = this.field_2_x;
        rec.field_3_y = this.field_3_y;
        rec.field_4_width = this.field_4_width;
        rec.field_5_height = this.field_5_height;
        return rec;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }

    public int getX() {
        return this.field_2_x;
    }

    public void setX(int field_2_x) {
        this.field_2_x = field_2_x;
    }

    public int getY() {
        return this.field_3_y;
    }

    public void setY(int field_3_y) {
        this.field_3_y = field_3_y;
    }

    public int getWidth() {
        return this.field_4_width;
    }

    public void setWidth(int field_4_width) {
        this.field_4_width = field_4_width;
    }

    public int getHeight() {
        return this.field_5_height;
    }

    public void setHeight(int field_5_height) {
        this.field_5_height = field_5_height;
    }
}
