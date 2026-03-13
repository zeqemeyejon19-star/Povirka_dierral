package org.apache.poi.hssf.record;

import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class DConRefRecord extends StandardRecord {
    public static final short sid = 81;
    private byte[] _unused;
    private int charCount;
    private int charType;
    private int firstCol;
    private int firstRow;
    private int lastCol;
    private int lastRow;
    private byte[] path;

    public DConRefRecord(byte[] data) {
        if (LittleEndian.getShort(data, 0) != 81) {
            throw new RecordFormatException("incompatible sid.");
        }
        int offset = 0 + 2 + 2;
        this.firstRow = LittleEndian.getUShort(data, offset);
        int offset2 = offset + 2;
        this.lastRow = LittleEndian.getUShort(data, offset2);
        int offset3 = offset2 + 2;
        this.firstCol = LittleEndian.getUByte(data, offset3);
        int offset4 = offset3 + 1;
        this.lastCol = LittleEndian.getUByte(data, offset4);
        int offset5 = offset4 + 1;
        int uShort = LittleEndian.getUShort(data, offset5);
        this.charCount = uShort;
        int offset6 = offset5 + 2;
        if (uShort < 2) {
            throw new RecordFormatException("Character count must be >= 2");
        }
        short uByte = LittleEndian.getUByte(data, offset6);
        this.charType = uByte;
        int offset7 = offset6 + 1;
        int byteLength = this.charCount * ((uByte & 1) + 1);
        byte[] byteArray = LittleEndian.getByteArray(data, offset7, byteLength);
        this.path = byteArray;
        int offset8 = offset7 + byteLength;
        if (byteArray[0] == 2) {
            this._unused = LittleEndian.getByteArray(data, offset8, this.charType + 1);
        }
    }

    public DConRefRecord(RecordInputStream inStream) {
        if (inStream.getSid() != 81) {
            throw new RecordFormatException("Wrong sid: " + ((int) inStream.getSid()));
        }
        this.firstRow = inStream.readUShort();
        this.lastRow = inStream.readUShort();
        this.firstCol = inStream.readUByte();
        this.lastCol = inStream.readUByte();
        this.charCount = inStream.readUShort();
        int uByte = inStream.readUByte() & 1;
        this.charType = uByte;
        int byteLength = this.charCount * (uByte + 1);
        byte[] bArr = new byte[byteLength];
        this.path = bArr;
        inStream.readFully(bArr);
        if (this.path[0] == 2) {
            this._unused = inStream.readRemainder();
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        byte[] bArr = this.path;
        int sz = bArr.length + 9;
        if (bArr[0] == 2) {
            return sz + this._unused.length;
        }
        return sz;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this.firstRow);
        out.writeShort(this.lastRow);
        out.writeByte(this.firstCol);
        out.writeByte(this.lastCol);
        out.writeShort(this.charCount);
        out.writeByte(this.charType);
        out.write(this.path);
        if (this.path[0] == 2) {
            out.write(this._unused);
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 81;
    }

    public int getFirstColumn() {
        return this.firstCol;
    }

    public int getFirstRow() {
        return this.firstRow;
    }

    public int getLastColumn() {
        return this.lastCol;
    }

    public int getLastRow() {
        return this.lastRow;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("[DCONREF]\n");
        b.append("    .ref\n");
        b.append("        .firstrow   = ").append(this.firstRow).append("\n");
        b.append("        .lastrow    = ").append(this.lastRow).append("\n");
        b.append("        .firstcol   = ").append(this.firstCol).append("\n");
        b.append("        .lastcol    = ").append(this.lastCol).append("\n");
        b.append("    .cch            = ").append(this.charCount).append("\n");
        b.append("    .stFile\n");
        b.append("        .h          = ").append(this.charType).append("\n");
        b.append("        .rgb        = ").append(getReadablePath()).append("\n");
        b.append("[/DCONREF]\n");
        return b.toString();
    }

    public byte[] getPath() {
        byte[] bArr = this.path;
        return Arrays.copyOf(bArr, bArr.length);
    }

    public String getReadablePath() {
        if (this.path != null) {
            int offset = 1;
            while (true) {
                byte[] bArr = this.path;
                if (bArr[offset] >= 32 || offset >= bArr.length) {
                    break;
                }
                offset++;
            }
            byte[] bArr2 = this.path;
            String out = new String(Arrays.copyOfRange(bArr2, offset, bArr2.length), StringUtil.UTF8);
            return out.replaceAll("\u0003", "/");
        }
        return null;
    }

    public boolean isExternalRef() {
        return this.path[0] == 1;
    }
}
