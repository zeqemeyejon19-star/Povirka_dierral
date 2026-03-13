package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
class TypedPropertyValue {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) TypedPropertyValue.class);
    private int _type;
    private Object _value;

    TypedPropertyValue(int type, Object value) {
        this._type = type;
        this._value = value;
    }

    Object getValue() {
        return this._value;
    }

    void read(LittleEndianByteArrayInputStream lei) {
        this._type = lei.readShort();
        short padding = lei.readShort();
        if (padding != 0) {
            LOG.log(5, "TypedPropertyValue padding at offset " + lei.getReadIndex() + " MUST be 0, but it's value is " + ((int) padding));
        }
        readValue(lei);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:49:0x018c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void readValue(org.apache.poi.util.LittleEndianByteArrayInputStream r19) {
        /*
            Method dump skipped, instruction units count: 708
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.hpsf.TypedPropertyValue.readValue(org.apache.poi.util.LittleEndianByteArrayInputStream):void");
    }

    static void skipPadding(LittleEndianByteArrayInputStream lei) {
        int offset = lei.getReadIndex();
        int skipBytes = (4 - (offset & 3)) & 3;
        for (int i = 0; i < skipBytes; i++) {
            lei.mark(1);
            int b = lei.read();
            if (b == -1 || b != 0) {
                lei.reset();
                return;
            }
        }
    }
}
