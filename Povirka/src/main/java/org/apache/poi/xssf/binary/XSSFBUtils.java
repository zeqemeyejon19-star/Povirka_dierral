package org.apache.poi.xssf.binary;

import java.nio.charset.Charset;
import org.apache.poi.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBUtils {
    static int readXLNullableWideString(byte[] data, int offset, StringBuilder sb) throws XSSFBParseException {
        long numChars = LittleEndian.getUInt(data, offset);
        if (numChars < 0) {
            throw new XSSFBParseException("too few chars to read");
        }
        if (numChars == 4294967295L) {
            return 0;
        }
        if (numChars > 4294967295L) {
            throw new XSSFBParseException("too many chars to read");
        }
        int numBytes = ((int) numChars) * 2;
        int offset2 = offset + 4;
        if (offset2 + numBytes > data.length) {
            throw new XSSFBParseException("trying to read beyond data length:offset=" + offset2 + ", numBytes=" + numBytes + ", data.length=" + data.length);
        }
        sb.append(new String(data, offset2, numBytes, Charset.forName("UTF-16LE")));
        return numBytes + 4;
    }

    public static int readXLWideString(byte[] data, int offset, StringBuilder sb) throws XSSFBParseException {
        long numChars = LittleEndian.getUInt(data, offset);
        if (numChars < 0) {
            throw new XSSFBParseException("too few chars to read");
        }
        if (numChars > 4294967295L) {
            throw new XSSFBParseException("too many chars to read");
        }
        int numBytes = ((int) numChars) * 2;
        int offset2 = offset + 4;
        if (offset2 + numBytes > data.length) {
            throw new XSSFBParseException("trying to read beyond data length");
        }
        sb.append(new String(data, offset2, numBytes, Charset.forName("UTF-16LE")));
        return numBytes + 4;
    }

    static int castToInt(long val) {
        if (val < 2147483647L && val > -2147483648L) {
            return (int) val;
        }
        throw new POIXMLException("val (" + val + ") can't be cast to int");
    }

    static short castToShort(int val) {
        if (val < 32767 && val > -32768) {
            return (short) val;
        }
        throw new POIXMLException("val (" + val + ") can't be cast to short");
    }

    static int get24BitInt(byte[] data, int offset) {
        int i = offset + 1;
        int b0 = data[offset] & 255;
        int i2 = i + 1;
        int b1 = data[i] & 255;
        int b2 = data[i2] & 255;
        return (b2 << 16) + (b1 << 8) + b0;
    }
}
