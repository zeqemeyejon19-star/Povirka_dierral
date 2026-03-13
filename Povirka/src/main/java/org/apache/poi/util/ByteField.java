package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;

/* JADX INFO: loaded from: classes.dex */
public class ByteField implements FixedField {
    private static final byte _default_value = 0;
    private final int _offset;
    private byte _value;

    public ByteField(int offset) throws ArrayIndexOutOfBoundsException {
        this(offset, (byte) 0);
    }

    public ByteField(int offset, byte value) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("offset cannot be negative");
        }
        this._offset = offset;
        set(value);
    }

    public ByteField(int offset, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }

    public ByteField(int offset, byte value, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset, value);
        writeToBytes(data);
    }

    public byte get() {
        return this._value;
    }

    public void set(byte value) {
        this._value = value;
    }

    public void set(byte value, byte[] data) throws ArrayIndexOutOfBoundsException {
        set(value);
        writeToBytes(data);
    }

    @Override // org.apache.poi.util.FixedField
    public void readFromBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = data[this._offset];
    }

    @Override // org.apache.poi.util.FixedField
    public void readFromStream(InputStream stream) throws IOException {
        int ib = stream.read();
        if (ib < 0) {
            throw new BufferUnderflowException();
        }
        this._value = (byte) ib;
    }

    @Override // org.apache.poi.util.FixedField
    public void writeToBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        data[this._offset] = this._value;
    }

    @Override // org.apache.poi.util.FixedField
    public String toString() {
        return String.valueOf((int) this._value);
    }
}
