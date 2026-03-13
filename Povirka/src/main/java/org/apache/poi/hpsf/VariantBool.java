package org.apache.poi.hpsf;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
class VariantBool {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) VariantBool.class);
    static final int SIZE = 2;
    private boolean _value;

    VariantBool() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        short value = lei.readShort();
        if (value == -1) {
            this._value = true;
        } else if (value == 0) {
            this._value = false;
        } else {
            LOG.log(5, "VARIANT_BOOL value '" + ((int) value) + "' is incorrect");
            this._value = true;
        }
    }

    boolean getValue() {
        return this._value;
    }

    void setValue(boolean value) {
        this._value = value;
    }
}
