package org.apache.poi.poifs.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class ByteArrayBackedDataSource extends DataSource {
    private byte[] buffer;
    private long size;

    public ByteArrayBackedDataSource(byte[] data, int size) {
        this.buffer = data;
        this.size = size;
    }

    public ByteArrayBackedDataSource(byte[] data) {
        this(data, data.length);
    }

    @Override // org.apache.poi.poifs.nio.DataSource
    public ByteBuffer read(int length, long position) {
        long j = this.size;
        if (position >= j) {
            throw new IndexOutOfBoundsException("Unable to read " + length + " bytes from " + position + " in stream of length " + this.size);
        }
        int toRead = (int) Math.min(length, j - position);
        return ByteBuffer.wrap(this.buffer, (int) position, toRead);
    }

    @Override // org.apache.poi.poifs.nio.DataSource
    public void write(ByteBuffer src, long position) {
        long endPosition = ((long) src.capacity()) + position;
        if (endPosition > this.buffer.length) {
            extend(endPosition);
        }
        src.get(this.buffer, (int) position, src.capacity());
        if (endPosition > this.size) {
            this.size = endPosition;
        }
    }

    private void extend(long length) {
        byte[] bArr = this.buffer;
        long difference = length - ((long) bArr.length);
        if (difference < ((double) bArr.length) * 0.25d) {
            difference = (long) (((double) bArr.length) * 0.25d);
        }
        if (difference < 4096) {
            difference = 4096;
        }
        byte[] nb = new byte[(int) (((long) bArr.length) + difference)];
        System.arraycopy(bArr, 0, nb, 0, (int) this.size);
        this.buffer = nb;
    }

    @Override // org.apache.poi.poifs.nio.DataSource
    public void copyTo(OutputStream stream) throws IOException {
        stream.write(this.buffer, 0, (int) this.size);
    }

    @Override // org.apache.poi.poifs.nio.DataSource
    public long size() {
        return this.size;
    }

    @Override // org.apache.poi.poifs.nio.DataSource
    public void close() {
        this.buffer = null;
        this.size = -1L;
    }
}
