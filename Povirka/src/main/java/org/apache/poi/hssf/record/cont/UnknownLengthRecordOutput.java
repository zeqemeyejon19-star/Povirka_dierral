package org.apache.poi.hssf.record.cont;

import org.apache.poi.util.DelayableLittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
final class UnknownLengthRecordOutput implements LittleEndianOutput {
    private static final int MAX_DATA_SIZE = 8224;
    private final byte[] _byteBuffer;
    private final LittleEndianOutput _dataSizeOutput;
    private final LittleEndianOutput _originalOut;
    private LittleEndianOutput _out;
    private int _size;

    public UnknownLengthRecordOutput(LittleEndianOutput out, int sid) {
        this._originalOut = out;
        out.writeShort(sid);
        if (out instanceof DelayableLittleEndianOutput) {
            DelayableLittleEndianOutput dleo = (DelayableLittleEndianOutput) out;
            this._dataSizeOutput = dleo.createDelayedOutput(2);
            this._byteBuffer = null;
            this._out = out;
            return;
        }
        this._dataSizeOutput = out;
        byte[] bArr = new byte[MAX_DATA_SIZE];
        this._byteBuffer = bArr;
        this._out = new LittleEndianByteArrayOutputStream(bArr, 0);
    }

    public int getTotalSize() {
        return this._size + 4;
    }

    public int getAvailableSpace() {
        if (this._out == null) {
            throw new IllegalStateException("Record already terminated");
        }
        return 8224 - this._size;
    }

    public void terminate() {
        if (this._out == null) {
            throw new IllegalStateException("Record already terminated");
        }
        this._dataSizeOutput.writeShort(this._size);
        byte[] bArr = this._byteBuffer;
        if (bArr != null) {
            this._originalOut.write(bArr, 0, this._size);
            this._out = null;
        } else {
            this._out = null;
        }
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b) {
        this._out.write(b);
        this._size += b.length;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void write(byte[] b, int offset, int len) {
        this._out.write(b, offset, len);
        this._size += len;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeByte(int v) {
        this._out.writeByte(v);
        this._size++;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeDouble(double v) {
        this._out.writeDouble(v);
        this._size += 8;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeInt(int v) {
        this._out.writeInt(v);
        this._size += 4;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeLong(long v) {
        this._out.writeLong(v);
        this._size += 8;
    }

    @Override // org.apache.poi.util.LittleEndianOutput
    public void writeShort(int v) {
        this._out.writeShort(v);
        this._size += 2;
    }
}
