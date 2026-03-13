package org.apache.poi.hpsf;

import java.io.UnsupportedEncodingException;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
@Deprecated
public class MutableProperty extends Property {
    public MutableProperty() {
    }

    public MutableProperty(Property p) {
        super(p);
    }

    public MutableProperty(long id, long type, Object value) {
        super(id, type, value);
    }

    public MutableProperty(long id, byte[] src, long offset, int length, int codepage) throws UnsupportedEncodingException {
        super(id, src, offset, length, codepage);
    }

    public MutableProperty(long id, LittleEndianByteArrayInputStream leis, int length, int codepage) throws UnsupportedEncodingException {
        super(id, leis, length, codepage);
    }
}
