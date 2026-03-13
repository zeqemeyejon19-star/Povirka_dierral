package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
@Internal
class UnicodeString {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) UnicodeString.class);
    private byte[] _value;

    UnicodeString() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        int length = lei.readInt();
        int unicodeBytes = length * 2;
        this._value = new byte[unicodeBytes];
        if (length == 0) {
            return;
        }
        int offset = lei.getReadIndex();
        lei.readFully(this._value);
        byte[] bArr = this._value;
        if (bArr[unicodeBytes - 2] != 0 || bArr[unicodeBytes - 1] != 0) {
            String msg = "UnicodeString started at offset #" + offset + " is not NULL-terminated";
            throw new IllegalPropertySetDataException(msg);
        }
        TypedPropertyValue.skipPadding(lei);
    }

    byte[] getValue() {
        return this._value;
    }

    String toJavaString() {
        byte[] bArr = this._value;
        if (bArr.length == 0) {
            return null;
        }
        String result = StringUtil.getFromUnicodeLE(bArr, 0, bArr.length >> 1);
        int terminator = result.indexOf(0);
        if (terminator == -1) {
            LOG.log(5, "String terminator (\\0) for UnicodeString property value not found.Continue without trimming and hope for the best.");
            return result;
        }
        if (terminator != result.length() - 1) {
            LOG.log(5, "String terminator (\\0) for UnicodeString property value occured before the end of string. Trimming and hope for the best.");
        }
        return result.substring(0, terminator);
    }

    void setJavaValue(String string) throws UnsupportedEncodingException {
        this._value = CodePageUtil.getBytesInCodePage(string + "\u0000", 1200);
    }

    int write(OutputStream out) throws IOException {
        LittleEndian.putUInt(this._value.length / 2, out);
        out.write(this._value);
        return this._value.length + 4;
    }
}
