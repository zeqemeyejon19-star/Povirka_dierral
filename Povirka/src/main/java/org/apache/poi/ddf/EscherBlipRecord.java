package org.apache.poi.ddf;

import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class EscherBlipRecord extends EscherRecord {
    private static final int HEADER_SIZE = 8;
    public static final String RECORD_DESCRIPTION = "msofbtBlip";
    public static final short RECORD_ID_END = -3817;
    public static final short RECORD_ID_START = -4072;
    private byte[] field_pictureData;

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + 8;
        byte[] bArr = new byte[bytesAfterHeader];
        this.field_pictureData = bArr;
        System.arraycopy(data, pos, bArr, 0, bytesAfterHeader);
        return bytesAfterHeader + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        byte[] bArr = this.field_pictureData;
        System.arraycopy(bArr, 0, data, offset + 4, bArr.length);
        listener.afterRecordSerialize(offset + 4 + this.field_pictureData.length, getRecordId(), this.field_pictureData.length + 4, this);
        return this.field_pictureData.length + 4;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.field_pictureData.length + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Blip";
    }

    public byte[] getPicturedata() {
        return this.field_pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        setPictureData(pictureData, 0, pictureData == null ? 0 : pictureData.length);
    }

    public void setPictureData(byte[] pictureData, int offset, int length) {
        if (pictureData == null || offset < 0 || length < 0 || pictureData.length < offset + length) {
            throw new IllegalArgumentException("picture data can't be null");
        }
        byte[] bArr = new byte[length];
        this.field_pictureData = bArr;
        System.arraycopy(pictureData, offset, bArr, 0, length);
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        return new Object[][]{new Object[]{"Extra Data", getPicturedata()}};
    }
}
