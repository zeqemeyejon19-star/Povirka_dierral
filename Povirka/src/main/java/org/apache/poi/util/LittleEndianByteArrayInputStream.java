package org.apache.poi.util;

import java.io.ByteArrayInputStream;

/* JADX INFO: loaded from: classes.dex */
public class LittleEndianByteArrayInputStream extends ByteArrayInputStream implements LittleEndianInput {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    public LittleEndianByteArrayInputStream(byte[] buf, int startOffset, int maxReadLen) {
        super(buf, startOffset, maxReadLen);
    }

    public LittleEndianByteArrayInputStream(byte[] buf, int startOffset) {
        this(buf, startOffset, buf.length - startOffset);
    }

    public LittleEndianByteArrayInputStream(byte[] buf) {
        this(buf, 0);
    }

    protected void checkPosition(int i) {
        if (i > this.count - this.pos) {
            throw new RuntimeException("Buffer overrun");
        }
    }

    public int getReadIndex() {
        return this.pos;
    }

    public void setReadIndex(int pos) {
        if (pos < 0 || pos >= this.count) {
            throw new IndexOutOfBoundsException();
        }
        this.pos = pos;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public byte readByte() {
        checkPosition(1);
        return (byte) read();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readInt() {
        checkPosition(4);
        int le = LittleEndian.getInt(this.buf, this.pos);
        long skipped = super.skip(4L);
        if (skipped != 4) {
            throw new AssertionError("Buffer overrun");
        }
        return le;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public long readLong() {
        checkPosition(8);
        long le = LittleEndian.getLong(this.buf, this.pos);
        long skipped = super.skip(8L);
        if (skipped != 8) {
            throw new AssertionError("Buffer overrun");
        }
        return le;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public short readShort() {
        checkPosition(2);
        short le = LittleEndian.getShort(this.buf, this.pos);
        long skipped = super.skip(2L);
        if (skipped != 2) {
            throw new AssertionError("Buffer overrun");
        }
        return le;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUByte() {
        return readByte() & 255;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUShort() {
        return readShort() & 65535;
    }

    public long readUInt() {
        return ((long) readInt()) & 4294967295L;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buffer, int off, int len) {
        checkPosition(len);
        read(buffer, off, len);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buffer) {
        checkPosition(buffer.length);
        read(buffer, 0, buffer.length);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readPlain(byte[] buf, int off, int len) {
        readFully(buf, off, len);
    }
}
