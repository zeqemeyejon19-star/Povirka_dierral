package org.apache.poi.ddf;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.poi.hssf.usermodel.HSSFPictureData;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public final class EscherMetafileBlip extends EscherBlipRecord {
    private static final int HEADER_SIZE = 8;
    public static final short RECORD_ID_EMF = -4070;
    public static final short RECORD_ID_PICT = -4068;
    public static final short RECORD_ID_WMF = -4069;
    private static final POILogger log = POILogFactory.getLogger((Class<?>) EscherMetafileBlip.class);
    private final byte[] field_1_UID = new byte[16];
    private final byte[] field_2_UID = new byte[16];
    private int field_2_cb;
    private int field_3_rcBounds_x1;
    private int field_3_rcBounds_x2;
    private int field_3_rcBounds_y1;
    private int field_3_rcBounds_y2;
    private int field_4_ptSize_h;
    private int field_4_ptSize_w;
    private int field_5_cbSave;
    private byte field_6_fCompression;
    private byte field_7_fFilter;
    private byte[] raw_pictureData;
    private byte[] remainingData;

    @Override // org.apache.poi.ddf.EscherBlipRecord, org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + 8;
        System.arraycopy(data, pos, this.field_1_UID, 0, 16);
        int pos2 = pos + 16;
        if ((getOptions() ^ getSignature()) == 16) {
            System.arraycopy(data, pos2, this.field_2_UID, 0, 16);
            pos2 += 16;
        }
        this.field_2_cb = LittleEndian.getInt(data, pos2);
        int pos3 = pos2 + 4;
        this.field_3_rcBounds_x1 = LittleEndian.getInt(data, pos3);
        int pos4 = pos3 + 4;
        this.field_3_rcBounds_y1 = LittleEndian.getInt(data, pos4);
        int pos5 = pos4 + 4;
        this.field_3_rcBounds_x2 = LittleEndian.getInt(data, pos5);
        int pos6 = pos5 + 4;
        this.field_3_rcBounds_y2 = LittleEndian.getInt(data, pos6);
        int pos7 = pos6 + 4;
        this.field_4_ptSize_w = LittleEndian.getInt(data, pos7);
        int pos8 = pos7 + 4;
        this.field_4_ptSize_h = LittleEndian.getInt(data, pos8);
        int pos9 = pos8 + 4;
        int i = LittleEndian.getInt(data, pos9);
        this.field_5_cbSave = i;
        int pos10 = pos9 + 4;
        this.field_6_fCompression = data[pos10];
        int pos11 = pos10 + 1;
        this.field_7_fFilter = data[pos11];
        int pos12 = pos11 + 1;
        byte[] bArr = new byte[i];
        this.raw_pictureData = bArr;
        System.arraycopy(data, pos12, bArr, 0, i);
        int pos13 = pos12 + this.field_5_cbSave;
        if (this.field_6_fCompression == 0) {
            super.setPictureData(inflatePictureData(this.raw_pictureData));
        } else {
            super.setPictureData(this.raw_pictureData);
        }
        int remaining = (bytesAfterHeader - pos13) + offset + 8;
        if (remaining > 0) {
            byte[] bArr2 = new byte[remaining];
            this.remainingData = bArr2;
            System.arraycopy(data, pos13, bArr2, 0, remaining);
        }
        return bytesAfterHeader + 8;
    }

    @Override // org.apache.poi.ddf.EscherBlipRecord, org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        int pos = offset + 2;
        LittleEndian.putShort(data, pos, getRecordId());
        int pos2 = pos + 2;
        LittleEndian.putInt(data, pos2, getRecordSize() - 8);
        int pos3 = pos2 + 4;
        byte[] bArr = this.field_1_UID;
        System.arraycopy(bArr, 0, data, pos3, bArr.length);
        int pos4 = pos3 + this.field_1_UID.length;
        if ((getOptions() ^ getSignature()) == 16) {
            byte[] bArr2 = this.field_2_UID;
            System.arraycopy(bArr2, 0, data, pos4, bArr2.length);
            pos4 += this.field_2_UID.length;
        }
        LittleEndian.putInt(data, pos4, this.field_2_cb);
        int pos5 = pos4 + 4;
        LittleEndian.putInt(data, pos5, this.field_3_rcBounds_x1);
        int pos6 = pos5 + 4;
        LittleEndian.putInt(data, pos6, this.field_3_rcBounds_y1);
        int pos7 = pos6 + 4;
        LittleEndian.putInt(data, pos7, this.field_3_rcBounds_x2);
        int pos8 = pos7 + 4;
        LittleEndian.putInt(data, pos8, this.field_3_rcBounds_y2);
        int pos9 = pos8 + 4;
        LittleEndian.putInt(data, pos9, this.field_4_ptSize_w);
        int pos10 = pos9 + 4;
        LittleEndian.putInt(data, pos10, this.field_4_ptSize_h);
        int pos11 = pos10 + 4;
        LittleEndian.putInt(data, pos11, this.field_5_cbSave);
        int pos12 = pos11 + 4;
        data[pos12] = this.field_6_fCompression;
        int pos13 = pos12 + 1;
        data[pos13] = this.field_7_fFilter;
        int pos14 = pos13 + 1;
        byte[] bArr3 = this.raw_pictureData;
        System.arraycopy(bArr3, 0, data, pos14, bArr3.length);
        int pos15 = pos14 + this.raw_pictureData.length;
        byte[] bArr4 = this.remainingData;
        if (bArr4 != null) {
            System.arraycopy(bArr4, 0, data, pos15, bArr4.length);
            int length = pos15 + this.remainingData.length;
        }
        listener.afterRecordSerialize(getRecordSize() + offset, getRecordId(), getRecordSize(), this);
        return getRecordSize();
    }

    private static byte[] inflatePictureData(byte[] data) {
        try {
            InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(data));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            while (true) {
                int readBytes = in.read(buf);
                if (readBytes > 0) {
                    out.write(buf, 0, readBytes);
                } else {
                    return out.toByteArray();
                }
            }
        } catch (IOException e) {
            log.log(5, "Possibly corrupt compression or non-compressed data", e);
            return data;
        }
    }

    @Override // org.apache.poi.ddf.EscherBlipRecord, org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        int size = this.raw_pictureData.length + 58;
        byte[] bArr = this.remainingData;
        if (bArr != null) {
            size += bArr.length;
        }
        if ((getOptions() ^ getSignature()) == 16) {
            return size + this.field_2_UID.length;
        }
        return size;
    }

    public byte[] getUID() {
        return this.field_1_UID;
    }

    public void setUID(byte[] uid) {
        if (uid == null || uid.length != 16) {
            throw new IllegalArgumentException("uid must be byte[16]");
        }
        byte[] bArr = this.field_1_UID;
        System.arraycopy(uid, 0, bArr, 0, bArr.length);
    }

    public byte[] getPrimaryUID() {
        return this.field_2_UID;
    }

    public void setPrimaryUID(byte[] primaryUID) {
        if (primaryUID == null || primaryUID.length != 16) {
            throw new IllegalArgumentException("primaryUID must be byte[16]");
        }
        byte[] bArr = this.field_2_UID;
        System.arraycopy(primaryUID, 0, bArr, 0, bArr.length);
    }

    public int getUncompressedSize() {
        return this.field_2_cb;
    }

    public void setUncompressedSize(int uncompressedSize) {
        this.field_2_cb = uncompressedSize;
    }

    public Rectangle getBounds() {
        int i = this.field_3_rcBounds_x1;
        int i2 = this.field_3_rcBounds_y1;
        return new Rectangle(i, i2, this.field_3_rcBounds_x2 - i, this.field_3_rcBounds_y2 - i2);
    }

    public void setBounds(Rectangle bounds) {
        this.field_3_rcBounds_x1 = bounds.x;
        this.field_3_rcBounds_y1 = bounds.y;
        this.field_3_rcBounds_x2 = bounds.x + bounds.width;
        this.field_3_rcBounds_y2 = bounds.y + bounds.height;
    }

    public Dimension getSizeEMU() {
        return new Dimension(this.field_4_ptSize_w, this.field_4_ptSize_h);
    }

    public void setSizeEMU(Dimension sizeEMU) {
        this.field_4_ptSize_w = sizeEMU.width;
        this.field_4_ptSize_h = sizeEMU.height;
    }

    public int getCompressedSize() {
        return this.field_5_cbSave;
    }

    public void setCompressedSize(int compressedSize) {
        this.field_5_cbSave = compressedSize;
    }

    public boolean isCompressed() {
        return this.field_6_fCompression == 0;
    }

    public void setCompressed(boolean compressed) {
        this.field_6_fCompression = compressed ? (byte) 0 : (byte) -2;
    }

    public byte getFilter() {
        return this.field_7_fFilter;
    }

    public void setFilter(byte filter) {
        this.field_7_fFilter = filter;
    }

    public byte[] getRemainingData() {
        return this.remainingData;
    }

    public short getSignature() {
        switch (getRecordId()) {
            case -4070:
                return HSSFPictureData.MSOBI_EMF;
            case -4069:
                return HSSFPictureData.MSOBI_WMF;
            case -4068:
                return HSSFPictureData.MSOBI_PICT;
            default:
                POILogger pOILogger = log;
                if (pOILogger.check(5)) {
                    pOILogger.log(5, "Unknown metafile: " + ((int) getRecordId()));
                }
                return (short) 0;
        }
    }

    @Override // org.apache.poi.ddf.EscherBlipRecord
    public void setPictureData(byte[] pictureData) {
        super.setPictureData(pictureData);
        setUncompressedSize(pictureData.length);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(bos);
            dos.write(pictureData);
            dos.close();
            byte[] byteArray = bos.toByteArray();
            this.raw_pictureData = byteArray;
            setCompressedSize(byteArray.length);
            setCompressed(true);
        } catch (IOException e) {
            throw new RuntimeException("Can't compress metafile picture data", e);
        }
    }

    @Override // org.apache.poi.ddf.EscherBlipRecord, org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        return new Object[][]{new Object[]{"UID", this.field_1_UID, "UID2", this.field_2_UID}, new Object[]{"Uncompressed Size", Integer.valueOf(this.field_2_cb)}, new Object[]{"Bounds", getBounds().toString()}, new Object[]{"Size in EMU", getSizeEMU().toString()}, new Object[]{"Compressed Size", Integer.valueOf(this.field_5_cbSave)}, new Object[]{"Compression", Byte.valueOf(this.field_6_fCompression)}, new Object[]{"Filter", Byte.valueOf(this.field_7_fFilter)}, new Object[]{"Extra Data", ""}, new Object[]{"Remaining Data", this.remainingData}};
    }
}
