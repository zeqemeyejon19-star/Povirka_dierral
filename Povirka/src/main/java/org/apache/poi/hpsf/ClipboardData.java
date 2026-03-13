package org.apache.poi.hpsf;

import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
class ClipboardData {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ClipboardData.class);
    private int _format = 0;
    private byte[] _value;

    ClipboardData() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        int offset = lei.getReadIndex();
        int size = lei.readInt();
        if (size < 4) {
            String msg = "ClipboardData at offset " + offset + " size less than 4 bytes (doesn't even have format field!). Setting to format == 0 and hope for the best";
            LOG.log(5, msg);
            this._format = 0;
            this._value = new byte[0];
            return;
        }
        this._format = lei.readInt();
        byte[] bArr = new byte[size - 4];
        this._value = bArr;
        lei.readFully(bArr);
    }

    byte[] getValue() {
        return this._value;
    }

    byte[] toByteArray() {
        byte[] result = new byte[this._value.length + 8];
        LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(result, 0);
        try {
            bos.writeInt(this._value.length + 4);
            bos.writeInt(this._format);
            bos.write(this._value);
            return result;
        } finally {
            IOUtils.closeQuietly(bos);
        }
    }

    void setValue(byte[] value) {
        this._value = (byte[]) value.clone();
    }
}
