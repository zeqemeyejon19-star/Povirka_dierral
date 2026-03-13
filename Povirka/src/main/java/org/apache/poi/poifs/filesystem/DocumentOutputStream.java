package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class DocumentOutputStream extends OutputStream {
    private final int _limit;
    private final OutputStream _stream;
    private int _written = 0;

    DocumentOutputStream(OutputStream stream, int limit) {
        this._stream = stream;
        this._limit = limit;
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        limitCheck(1);
        this._stream.write(b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        limitCheck(len);
        this._stream.write(b, off, len);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this._stream.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }

    void writeFiller(int totalLimit, byte fill) throws IOException {
        int i = this._written;
        if (totalLimit > i) {
            byte[] filler = new byte[totalLimit - i];
            Arrays.fill(filler, fill);
            this._stream.write(filler);
        }
    }

    private void limitCheck(int toBeWritten) throws IOException {
        int i = this._written;
        if (i + toBeWritten > this._limit) {
            throw new IOException("tried to write too much data");
        }
        this._written = i + toBeWritten;
    }
}
