package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

/* JADX INFO: loaded from: classes.dex */
@Internal
class GUID {
    private int _data1;
    private short _data2;
    private short _data3;
    private long _data4;

    GUID() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        this._data1 = lei.readInt();
        this._data2 = lei.readShort();
        this._data3 = lei.readShort();
        this._data4 = lei.readLong();
    }
}
