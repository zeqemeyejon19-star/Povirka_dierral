package org.apache.poi.ddf;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class EscherChildAnchorRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtChildAnchor";
    public static final short RECORD_ID = -4081;
    private int field_1_dx1;
    private int field_2_dy1;
    private int field_3_dx2;
    private int field_4_dy2;

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int size;
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        if (bytesRemaining == 8) {
            this.field_1_dx1 = LittleEndian.getShort(data, pos + 0);
            int size2 = 0 + 2;
            this.field_2_dy1 = LittleEndian.getShort(data, pos + size2);
            int size3 = size2 + 2;
            this.field_3_dx2 = LittleEndian.getShort(data, pos + size3);
            int size4 = size3 + 2;
            this.field_4_dy2 = LittleEndian.getShort(data, pos + size4);
            size = size4 + 2;
        } else if (bytesRemaining == 16) {
            this.field_1_dx1 = LittleEndian.getInt(data, pos + 0);
            int size5 = 0 + 4;
            this.field_2_dy1 = LittleEndian.getInt(data, pos + size5);
            int size6 = size5 + 4;
            this.field_3_dx2 = LittleEndian.getInt(data, pos + size6);
            int size7 = size6 + 4;
            this.field_4_dy2 = LittleEndian.getInt(data, pos + size7);
            size = size7 + 4;
        } else {
            throw new RuntimeException("Invalid EscherChildAnchorRecord - neither 8 nor 16 bytes.");
        }
        return size + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        int pos = offset + 2;
        LittleEndian.putShort(data, pos, getRecordId());
        int pos2 = pos + 2;
        LittleEndian.putInt(data, pos2, getRecordSize() - 8);
        int pos3 = pos2 + 4;
        LittleEndian.putInt(data, pos3, this.field_1_dx1);
        int pos4 = pos3 + 4;
        LittleEndian.putInt(data, pos4, this.field_2_dy1);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_3_dx2);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_4_dy2);
        int pos7 = pos6 + 4;
        listener.afterRecordSerialize(pos7, getRecordId(), pos7 - offset, this);
        return pos7 - offset;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return 24;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "ChildAnchor";
    }

    public int getDx1() {
        return this.field_1_dx1;
    }

    public void setDx1(int field_1_dx1) {
        this.field_1_dx1 = field_1_dx1;
    }

    public int getDy1() {
        return this.field_2_dy1;
    }

    public void setDy1(int field_2_dy1) {
        this.field_2_dy1 = field_2_dy1;
    }

    public int getDx2() {
        return this.field_3_dx2;
    }

    public void setDx2(int field_3_dx2) {
        this.field_3_dx2 = field_3_dx2;
    }

    public int getDy2() {
        return this.field_4_dy2;
    }

    public void setDy2(int field_4_dy2) {
        this.field_4_dy2 = field_4_dy2;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        return new Object[][]{new Object[]{"X1", Integer.valueOf(this.field_1_dx1)}, new Object[]{"Y1", Integer.valueOf(this.field_2_dy1)}, new Object[]{"X2", Integer.valueOf(this.field_3_dx2)}, new Object[]{"Y2", Integer.valueOf(this.field_4_dy2)}};
    }
}
