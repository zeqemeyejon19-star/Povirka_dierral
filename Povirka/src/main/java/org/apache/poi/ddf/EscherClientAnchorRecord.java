package org.apache.poi.ddf;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class EscherClientAnchorRecord extends EscherRecord {
    public static final String RECORD_DESCRIPTION = "MsofbtClientAnchor";
    public static final short RECORD_ID = -4080;
    private short field_1_flag;
    private short field_2_col1;
    private short field_3_dx1;
    private short field_4_row1;
    private short field_5_dy1;
    private short field_6_col2;
    private short field_7_dx2;
    private short field_8_row2;
    private short field_9_dy2;
    private byte[] remainingData = new byte[0];
    private boolean shortRecord = false;

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        int size = 0;
        if (bytesRemaining != 4) {
            this.field_1_flag = LittleEndian.getShort(data, pos + 0);
            int size2 = 0 + 2;
            this.field_2_col1 = LittleEndian.getShort(data, pos + size2);
            int size3 = size2 + 2;
            this.field_3_dx1 = LittleEndian.getShort(data, pos + size3);
            int size4 = size3 + 2;
            this.field_4_row1 = LittleEndian.getShort(data, pos + size4);
            size = size4 + 2;
            if (bytesRemaining >= 18) {
                this.field_5_dy1 = LittleEndian.getShort(data, pos + size);
                int size5 = size + 2;
                this.field_6_col2 = LittleEndian.getShort(data, pos + size5);
                int size6 = size5 + 2;
                this.field_7_dx2 = LittleEndian.getShort(data, pos + size6);
                int size7 = size6 + 2;
                this.field_8_row2 = LittleEndian.getShort(data, pos + size7);
                int size8 = size7 + 2;
                this.field_9_dy2 = LittleEndian.getShort(data, pos + size8);
                size = size8 + 2;
                this.shortRecord = false;
            } else {
                this.shortRecord = true;
            }
        }
        int bytesRemaining2 = bytesRemaining - size;
        byte[] bArr = new byte[bytesRemaining2];
        this.remainingData = bArr;
        System.arraycopy(data, pos + size, bArr, 0, bytesRemaining2);
        return size + 8 + bytesRemaining2;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        if (this.remainingData == null) {
            this.remainingData = new byte[0];
        }
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        int remainingBytes = this.remainingData.length + (this.shortRecord ? 8 : 18);
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        LittleEndian.putShort(data, offset + 8, this.field_1_flag);
        LittleEndian.putShort(data, offset + 10, this.field_2_col1);
        LittleEndian.putShort(data, offset + 12, this.field_3_dx1);
        LittleEndian.putShort(data, offset + 14, this.field_4_row1);
        if (!this.shortRecord) {
            LittleEndian.putShort(data, offset + 16, this.field_5_dy1);
            LittleEndian.putShort(data, offset + 18, this.field_6_col2);
            LittleEndian.putShort(data, offset + 20, this.field_7_dx2);
            LittleEndian.putShort(data, offset + 22, this.field_8_row2);
            LittleEndian.putShort(data, offset + 24, this.field_9_dy2);
        }
        byte[] bArr = this.remainingData;
        System.arraycopy(bArr, 0, data, (this.shortRecord ? 16 : 26) + offset, bArr.length);
        int pos = offset + 8 + (this.shortRecord ? 8 : 18) + this.remainingData.length;
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        int i = (this.shortRecord ? 8 : 18) + 8;
        byte[] bArr = this.remainingData;
        return i + (bArr == null ? 0 : bArr.length);
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public short getRecordId() {
        return RECORD_ID;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "ClientAnchor";
    }

    public short getFlag() {
        return this.field_1_flag;
    }

    public void setFlag(short field_1_flag) {
        this.field_1_flag = field_1_flag;
    }

    public short getCol1() {
        return this.field_2_col1;
    }

    public void setCol1(short field_2_col1) {
        this.field_2_col1 = field_2_col1;
    }

    public short getDx1() {
        return this.field_3_dx1;
    }

    public void setDx1(short field_3_dx1) {
        this.field_3_dx1 = field_3_dx1;
    }

    public short getRow1() {
        return this.field_4_row1;
    }

    public void setRow1(short field_4_row1) {
        this.field_4_row1 = field_4_row1;
    }

    public short getDy1() {
        return this.field_5_dy1;
    }

    public void setDy1(short field_5_dy1) {
        this.shortRecord = false;
        this.field_5_dy1 = field_5_dy1;
    }

    public short getCol2() {
        return this.field_6_col2;
    }

    public void setCol2(short field_6_col2) {
        this.shortRecord = false;
        this.field_6_col2 = field_6_col2;
    }

    public short getDx2() {
        return this.field_7_dx2;
    }

    public void setDx2(short field_7_dx2) {
        this.shortRecord = false;
        this.field_7_dx2 = field_7_dx2;
    }

    public short getRow2() {
        return this.field_8_row2;
    }

    public void setRow2(short field_8_row2) {
        this.shortRecord = false;
        this.field_8_row2 = field_8_row2;
    }

    public short getDy2() {
        return this.field_9_dy2;
    }

    public void setDy2(short field_9_dy2) {
        this.shortRecord = false;
        this.field_9_dy2 = field_9_dy2;
    }

    public byte[] getRemainingData() {
        return this.remainingData;
    }

    public void setRemainingData(byte[] remainingData) {
        if (remainingData == null) {
            this.remainingData = new byte[0];
        } else {
            this.remainingData = (byte[]) remainingData.clone();
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        return new Object[][]{new Object[]{"Flag", Short.valueOf(this.field_1_flag)}, new Object[]{"Col1", Short.valueOf(this.field_2_col1)}, new Object[]{"DX1", Short.valueOf(this.field_3_dx1)}, new Object[]{"Row1", Short.valueOf(this.field_4_row1)}, new Object[]{"DY1", Short.valueOf(this.field_5_dy1)}, new Object[]{"Col2", Short.valueOf(this.field_6_col2)}, new Object[]{"DX2", Short.valueOf(this.field_7_dx2)}, new Object[]{"Row2", Short.valueOf(this.field_8_row2)}, new Object[]{"DY2", Short.valueOf(this.field_9_dy2)}, new Object[]{"Extra Data", this.remainingData}};
    }
}
