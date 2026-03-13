package org.apache.poi.util;

import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class LittleEndianByteArrayOutputStream extends OutputStream implements LittleEndianOutput, DelayableLittleEndianOutput {
    private final byte[] _buf;
    private final int _endIndex;
    private int _writeIndex;

    public LittleEndianByteArrayOutputStream(byte[] buf, int startOffset, int maxWriteLen) {
        if (startOffset < 0 || startOffset > buf.length) {
            throw new IllegalArgumentException("Specified startOffset (" + startOffset + ") is out of allowable range (0.." + buf.length + ")");
        }
        this._buf = buf;
        this._writeIndex = startOffset;
        int i = startOffset + maxWriteLen;
        this._endIndex = i;
        if (i < startOffset || i > buf.length) {
            throw new IllegalArgumentException("calculated end index (" + i + ") is out of allowable range (" + this._writeIndex + ".." + buf.length + ")");
        }
    }

    public LittleEndianByteArrayOutputStream(byte[] buf, int startOffset) {
        this(buf, startOffset, buf.length - startOffset);
    }

    private void checkPosition(int i) {
        if (i > this._endIndex - this._writeIndex) {
            throw new RuntimeException("Buffer overrun");
        }
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeByte(int v) {
        checkPosition(1);
        byte[] bArr = this._buf;
        int i = this._writeIndex;
        this._writeIndex = i + 1;
        bArr[i] = (byte) v;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeDouble(double v) {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeInt(int v) {
        checkPosition(4);
        int i = this._writeIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        bArr[i] = (byte) ((v >>> 0) & 255);
        int i3 = i2 + 1;
        bArr[i2] = (byte) ((v >>> 8) & 255);
        int i4 = i3 + 1;
        bArr[i3] = (byte) ((v >>> 16) & 255);
        bArr[i4] = (byte) ((v >>> 24) & 255);
        this._writeIndex = i4 + 1;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeLong(long v) {
        writeInt((int) (v >> 0));
        writeInt((int) (v >> 32));
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeShort(int v) {
        checkPosition(2);
        int i = this._writeIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        bArr[i] = (byte) ((v >>> 0) & 255);
        bArr[i2] = (byte) ((v >>> 8) & 255);
        this._writeIndex = i2 + 1;
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        writeByte(b);
    }

    @Override // java.io.OutputStream, org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b) {
        int len = b.length;
        checkPosition(len);
        System.arraycopy(b, 0, this._buf, this._writeIndex, len);
        this._writeIndex += len;
    }

    @Override // java.io.OutputStream, org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b, int offset, int len) {
        checkPosition(len);
        System.arraycopy(b, offset, this._buf, this._writeIndex, len);
        this._writeIndex += len;
    }

    public int getWriteIndex() {
        return this._writeIndex;
    }

    @Override // org.apache.poi.util.DelayableLittleEndianOutput
    public LittleEndianOutput createDelayedOutput(int size) {
        checkPosition(size);
        LittleEndianOutput result = new LittleEndianByteArrayOutputStream(this._buf, this._writeIndex, size);
        this._writeIndex += size;
        return result;
    }
}
