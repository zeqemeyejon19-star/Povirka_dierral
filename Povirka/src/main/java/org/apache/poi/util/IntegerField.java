package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class IntegerField implements FixedField {
    private final int _offset;
    private int _value;

    public IntegerField(int offset) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("negative offset");
        }
        this._offset = offset;
    }

    public IntegerField(int offset, int value) throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value);
    }

    public IntegerField(int offset, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }

    public IntegerField(int offset, int value, byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value, data);
    }

    public int get() {
        return this._value;
    }

    public void set(int value) {
        this._value = value;
    }

    public void set(int value, byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = value;
        writeToBytes(data);
    }

    @Override // org.apache.poi.util.FixedField
    public void readFromBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = LittleEndian.getInt(data, this._offset);
    }

    @Override // org.apache.poi.util.FixedField
    public void readFromStream(InputStream stream) throws IOException {
        this._value = LittleEndian.readInt(stream);
    }

    @Override // org.apache.poi.util.FixedField
    public void writeToBytes(byte[] data) throws ArrayIndexOutOfBoundsException {
        LittleEndian.putInt(data, this._offset, this._value);
    }

    @Override // org.apache.poi.util.FixedField
    public String toString() {
        return String.valueOf(this._value);
    }
}
