package org.apache.poi.hpsf;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

/* JADX INFO: loaded from: classes.dex */
@Internal
class Vector {
    private final short _type;
    private TypedPropertyValue[] _values;

    Vector(short type) {
        this._type = type;
    }

    void read(LittleEndianByteArrayInputStream lei) {
        long longLength = lei.readUInt();
        if (longLength > 2147483647L) {
            throw new UnsupportedOperationException("Vector is too long -- " + longLength);
        }
        int length = (int) longLength;
        List<TypedPropertyValue> values = new ArrayList<>();
        int paddedType = this._type;
        if (paddedType == 12) {
            paddedType = 0;
        }
        for (int i = 0; i < length; i++) {
            TypedPropertyValue value = new TypedPropertyValue(paddedType, null);
            if (paddedType == 0) {
                value.read(lei);
            } else {
                value.readValue(lei);
            }
            values.add(value);
        }
        int i2 = values.size();
        this._values = (TypedPropertyValue[]) values.toArray(new TypedPropertyValue[i2]);
    }

    TypedPropertyValue[] getValues() {
        return this._values;
    }
}
