package org.apache.poi.poifs.storage;

/* JADX INFO: loaded from: classes.dex */
public final class DataInputBlock {
    private final byte[] _buf;
    private int _maxIndex;
    private int _readIndex;

    DataInputBlock(byte[] data, int startOffset) {
        this._buf = data;
        this._readIndex = startOffset;
        this._maxIndex = data.length;
    }

    public int available() {
        return this._maxIndex - this._readIndex;
    }

    public int readUByte() {
        byte[] bArr = this._buf;
        int i = this._readIndex;
        this._readIndex = i + 1;
        return bArr[i] & 255;
    }

    public int readUShortLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int b0 = bArr[i] & 255;
        int b1 = bArr[i2] & 255;
        this._readIndex = i2 + 1;
        return (b1 << 8) + (b0 << 0);
    }

    public int readUShortLE(DataInputBlock prevBlock) {
        byte[] bArr = prevBlock._buf;
        int i = bArr.length - 1;
        int b0 = bArr[i] & 255;
        byte[] bArr2 = this._buf;
        int i2 = this._readIndex;
        this._readIndex = i2 + 1;
        int b1 = bArr2[i2] & 255;
        return (b1 << 8) + (b0 << 0);
    }

    public int readIntLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int b0 = bArr[i] & 255;
        int i3 = i2 + 1;
        int b1 = bArr[i2] & 255;
        int i4 = i3 + 1;
        int b2 = bArr[i3] & 255;
        int b3 = bArr[i4] & 255;
        this._readIndex = i4 + 1;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }

    public int readIntLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[4];
        readSpanning(prevBlock, prevBlockAvailable, buf);
        int b0 = buf[0] & 255;
        int b1 = buf[1] & 255;
        int b2 = buf[2] & 255;
        int b3 = buf[3] & 255;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }

    public long readLongLE() {
        int i = this._readIndex;
        byte[] bArr = this._buf;
        int i2 = i + 1;
        int b0 = bArr[i] & 255;
        int i3 = i2 + 1;
        int b1 = bArr[i2] & 255;
        int i4 = i3 + 1;
        int b2 = bArr[i3] & 255;
        int i5 = i4 + 1;
        int b3 = bArr[i4] & 255;
        int i6 = i5 + 1;
        int b4 = bArr[i5] & 255;
        int i7 = i6 + 1;
        int b5 = bArr[i6] & 255;
        int i8 = i7 + 1;
        int b6 = bArr[i7] & 255;
        int b7 = bArr[i8] & 255;
        this._readIndex = i8 + 1;
        return (((long) b7) << 56) + (((long) b6) << 48) + (((long) b5) << 40) + (((long) b4) << 32) + (((long) b3) << 24) + ((long) (b2 << 16)) + ((long) (b1 << 8)) + ((long) (b0 << 0));
    }

    public long readLongLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[8];
        readSpanning(prevBlock, prevBlockAvailable, buf);
        int b0 = buf[0] & 255;
        int b1 = buf[1] & 255;
        int b2 = buf[2] & 255;
        int b3 = buf[3] & 255;
        int b4 = buf[4] & 255;
        int b5 = buf[5] & 255;
        int b6 = buf[6] & 255;
        int b7 = buf[7] & 255;
        return (((long) b7) << 56) + (((long) b6) << 48) + (((long) b5) << 40) + (((long) b4) << 32) + (((long) b3) << 24) + ((long) (b2 << 16)) + ((long) (b1 << 8)) + ((long) (b0 << 0));
    }

    private void readSpanning(DataInputBlock prevBlock, int prevBlockAvailable, byte[] buf) {
        System.arraycopy(prevBlock._buf, prevBlock._readIndex, buf, 0, prevBlockAvailable);
        int secondReadLen = buf.length - prevBlockAvailable;
        System.arraycopy(this._buf, 0, buf, prevBlockAvailable, secondReadLen);
        this._readIndex = secondReadLen;
    }

    public void readFully(byte[] buf, int off, int len) {
        System.arraycopy(this._buf, this._readIndex, buf, off, len);
        this._readIndex += len;
    }
}
