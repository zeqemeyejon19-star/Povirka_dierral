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

/* JADX INFO: loaded from: classes.dex */
@Internal
class CodePageString {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) CodePageString.class);
    private byte[] _value;

    CodePageString() {
    }

    void read(LittleEndianByteArrayInputStream lei) {
        int offset = lei.getReadIndex();
        int size = lei.readInt();
        byte[] bArr = new byte[size];
        this._value = bArr;
        if (size == 0) {
            return;
        }
        lei.readFully(bArr);
        if (this._value[size - 1] != 0) {
            String msg = "CodePageString started at offset #" + offset + " is not NULL-terminated";
            LOG.log(5, msg);
        }
        TypedPropertyValue.skipPadding(lei);
    }

    String getJavaValue(int codepage) throws UnsupportedEncodingException {
        int cp = codepage == -1 ? 1252 : codepage;
        String result = CodePageUtil.getStringFromCodePage(this._value, cp);
        int terminator = result.indexOf(0);
        if (terminator == -1) {
            LOG.log(5, "String terminator (\\0) for CodePageString property value not found.Continue without trimming and hope for the best.");
            return result;
        }
        if (terminator != result.length() - 1) {
            LOG.log(5, "String terminator (\\0) for CodePageString property value occured before the end of string. Trimming and hope for the best.");
        }
        String msg = result.substring(0, terminator);
        return msg;
    }

    int getSize() {
        return this._value.length + 4;
    }

    void setJavaValue(String string, int codepage) throws UnsupportedEncodingException {
        int cp = codepage == -1 ? 1252 : codepage;
        this._value = CodePageUtil.getBytesInCodePage(string + "\u0000", cp);
    }

    int write(OutputStream out) throws IOException {
        LittleEndian.putUInt(this._value.length, out);
        out.write(this._value);
        return this._value.length + 4;
    }
}
