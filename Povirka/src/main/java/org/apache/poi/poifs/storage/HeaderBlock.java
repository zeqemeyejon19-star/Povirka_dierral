package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LongField;
import org.apache.poi.util.ShortField;

/* JADX INFO: loaded from: classes.dex */
public final class HeaderBlock implements HeaderBlockConstants {
    private static final byte _default_value = -1;
    private int _bat_count;
    private final byte[] _data;
    private int _property_start;
    private int _sbat_count;
    private int _sbat_start;
    private int _xbat_count;
    private int _xbat_start;
    private final POIFSBigBlockSize bigBlockSize;

    public HeaderBlock(InputStream stream) throws IOException {
        this(readFirst512(stream));
        if (this.bigBlockSize.getBigBlockSize() != 512) {
            int rest = this.bigBlockSize.getBigBlockSize() - 512;
            byte[] tmp = new byte[rest];
            IOUtils.readFully(stream, tmp);
        }
    }

    public HeaderBlock(ByteBuffer buffer) throws IOException {
        this(IOUtils.toByteArray(buffer, 512));
    }

    private HeaderBlock(byte[] data) throws IOException {
        byte[] bArr = (byte[]) data.clone();
        this._data = bArr;
        FileMagic fm = FileMagic.valueOf(data);
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[fm.ordinal()]) {
            case 1:
                if (bArr[30] == 12) {
                    this.bigBlockSize = POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
                } else if (bArr[30] == 9) {
                    this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
                } else {
                    throw new IOException("Unsupported blocksize  (2^" + ((int) bArr[30]) + "). Expected 2^9 or 2^12.");
                }
                this._bat_count = new IntegerField(44, data).get();
                this._property_start = new IntegerField(48, bArr).get();
                this._sbat_start = new IntegerField(60, bArr).get();
                this._sbat_count = new IntegerField(64, bArr).get();
                this._xbat_start = new IntegerField(68, bArr).get();
                this._xbat_count = new IntegerField(72, bArr).get();
                return;
            case 2:
                throw new OfficeXmlFileException("The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)");
            case 3:
                throw new NotOLE2FileException("The supplied data appears to be a raw XML file. Formats such as Office 2003 XML are not supported");
            case 4:
                throw new NotOLE2FileException("The supplied data appears to be in the old MS Write format. Apache POI doesn't currently support this format");
            case 5:
            case 6:
            case 7:
                throw new OldExcelFormatException("The supplied data appears to be in " + fm + " format. HSSF only supports the BIFF8 format, try OldExcelExtractor");
            default:
                String exp = HexDump.longToHex(HeaderBlockConstants._signature);
                String act = HexDump.longToHex(LittleEndian.getLong(data, 0));
                throw new NotOLE2FileException("Invalid header signature; read " + act + ", expected " + exp + " - Your file appears not to be a valid OLE2 document");
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.poifs.storage.HeaderBlock$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic;

        static {
            int[] iArr = new int[FileMagic.values().length];
            $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic = iArr;
            try {
                iArr[FileMagic.OLE2.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.OOXML.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.XML.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.MSWRITE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.BIFF2.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.BIFF3.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.BIFF4.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public HeaderBlock(POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
        byte[] bArr = new byte[512];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
        new LongField(0, HeaderBlockConstants._signature, bArr);
        new IntegerField(8, 0, bArr);
        new IntegerField(12, 0, bArr);
        new IntegerField(16, 0, bArr);
        new IntegerField(20, 0, bArr);
        new ShortField(24, (short) 59, bArr);
        new ShortField(26, (short) 3, bArr);
        new ShortField(28, (short) -2, bArr);
        new ShortField(30, bigBlockSize.getHeaderValue(), bArr);
        new IntegerField(32, 6, bArr);
        new IntegerField(36, 0, bArr);
        new IntegerField(40, 0, bArr);
        new IntegerField(52, 0, bArr);
        new IntegerField(56, 4096, bArr);
        this._bat_count = 0;
        this._sbat_count = 0;
        this._xbat_count = 0;
        this._property_start = -2;
        this._sbat_start = -2;
        this._xbat_start = -2;
    }

    private static byte[] readFirst512(InputStream stream) throws IOException {
        byte[] data = new byte[512];
        int bsCount = IOUtils.readFully(stream, data);
        if (bsCount != 512) {
            throw alertShortRead(bsCount, 512);
        }
        return data;
    }

    private static IOException alertShortRead(int pRead, int expectedReadSize) {
        int read;
        if (pRead < 0) {
            read = 0;
        } else {
            read = pRead;
        }
        String type = " byte" + (read == 1 ? "" : "s");
        return new IOException("Unable to read entire header; " + read + type + " read; expected " + expectedReadSize + " bytes");
    }

    public int getPropertyStart() {
        return this._property_start;
    }

    public void setPropertyStart(int startBlock) {
        this._property_start = startBlock;
    }

    public int getSBATStart() {
        return this._sbat_start;
    }

    public int getSBATCount() {
        return this._sbat_count;
    }

    public void setSBATStart(int startBlock) {
        this._sbat_start = startBlock;
    }

    public void setSBATBlockCount(int count) {
        this._sbat_count = count;
    }

    public int getBATCount() {
        return this._bat_count;
    }

    public void setBATCount(int count) {
        this._bat_count = count;
    }

    public int[] getBATArray() {
        int[] result = new int[Math.min(this._bat_count, 109)];
        int offset = 76;
        for (int j = 0; j < result.length; j++) {
            result[j] = LittleEndian.getInt(this._data, offset);
            offset += 4;
        }
        return result;
    }

    public void setBATArray(int[] bat_array) {
        int count = Math.min(bat_array.length, 109);
        int blank = 109 - count;
        int offset = 76;
        for (int i = 0; i < count; i++) {
            LittleEndian.putInt(this._data, offset, bat_array[i]);
            offset += 4;
        }
        for (int i2 = 0; i2 < blank; i2++) {
            LittleEndian.putInt(this._data, offset, -1);
            offset += 4;
        }
    }

    public int getXBATCount() {
        return this._xbat_count;
    }

    public void setXBATCount(int count) {
        this._xbat_count = count;
    }

    public int getXBATIndex() {
        return this._xbat_start;
    }

    public void setXBATStart(int startBlock) {
        this._xbat_start = startBlock;
    }

    public POIFSBigBlockSize getBigBlockSize() {
        return this.bigBlockSize;
    }

    void writeData(OutputStream stream) throws IOException {
        new IntegerField(44, this._bat_count, this._data);
        new IntegerField(48, this._property_start, this._data);
        new IntegerField(60, this._sbat_start, this._data);
        new IntegerField(64, this._sbat_count, this._data);
        new IntegerField(68, this._xbat_start, this._data);
        new IntegerField(72, this._xbat_count, this._data);
        stream.write(this._data, 0, 512);
        for (int i = 512; i < this.bigBlockSize.getBigBlockSize(); i++) {
            stream.write(0);
        }
    }
}
