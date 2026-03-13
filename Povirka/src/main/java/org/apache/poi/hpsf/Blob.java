package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
@Internal
class Blob {
    private byte[] _value;

    Blob() {
    }

    void read(LittleEndianInput lei) {
        int size = lei.readInt();
        byte[] bArr = new byte[size];
        this._value = bArr;
        if (size > 0) {
            lei.readFully(bArr);
        }
    }
}
