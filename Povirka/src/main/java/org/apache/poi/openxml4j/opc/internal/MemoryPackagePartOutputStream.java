package org.apache.poi.openxml4j.opc.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class MemoryPackagePartOutputStream extends OutputStream {
    private ByteArrayOutputStream _buff = new ByteArrayOutputStream();
    private MemoryPackagePart _part;

    public MemoryPackagePartOutputStream(MemoryPackagePart part) {
        this._part = part;
    }

    @Override // java.io.OutputStream
    public void write(int b) {
        this._buff.write(b);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        flush();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this._buff.flush();
        if (this._part.data != null) {
            byte[] newArray = new byte[this._part.data.length + this._buff.size()];
            System.arraycopy(this._part.data, 0, newArray, 0, this._part.data.length);
            byte[] buffArr = this._buff.toByteArray();
            System.arraycopy(buffArr, 0, newArray, this._part.data.length, buffArr.length);
            this._part.data = newArray;
        } else {
            this._part.data = this._buff.toByteArray();
        }
        this._buff.reset();
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        this._buff.write(b, off, len);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        this._buff.write(b);
    }
}
