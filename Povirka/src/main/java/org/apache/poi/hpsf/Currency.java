package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

/* JADX INFO: loaded from: classes.dex */
@Internal
class Currency {
    private static final int SIZE = 8;
    private final byte[] _value = new byte[8];

    Currency() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        lei.readFully(this._value);
    }
}
