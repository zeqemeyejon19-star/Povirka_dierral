package org.apache.poi.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class LittleEndianOutputStream extends FilterOutputStream implements LittleEndianOutput {
    public LittleEndianOutputStream(OutputStream out) {
        super(out);
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeByte(int v) {
        try {
            this.out.write(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeDouble(double v) {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeInt(int v) {
        int b3 = (v >>> 24) & 255;
        int b2 = (v >>> 16) & 255;
        int b1 = (v >>> 8) & 255;
        int b0 = v & 255;
        try {
            this.out.write(b0);
            this.out.write(b1);
            this.out.write(b2);
            this.out.write(b3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeLong(long v) {
        writeInt((int) v);
        writeInt((int) (v >> 32));
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeShort(int v) {
        int b1 = (v >>> 8) & 255;
        int b0 = v & 255;
        try {
            this.out.write(b0);
            this.out.write(b1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b) {
        try {
            super.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b, int off, int len) {
        try {
            super.write(b, off, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
