package org.apache.poi.util;

import androidx.appcompat.widget.ActivityChooserView;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class HexDump {
    public static final String EOL = System.getProperty("line.separator");
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private HexDump() {
    }

    public static void dump(byte[] data, long offset, OutputStream stream, int index, int length) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (stream == null) {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
        OutputStreamWriter osw = new OutputStreamWriter(stream, UTF8);
        osw.write(dump(data, offset, index, length));
        osw.flush();
    }

    public static synchronized void dump(byte[] data, long offset, OutputStream stream, int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        dump(data, offset, stream, index, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static String dump(byte[] data, long offset, int index) {
        return dump(data, offset, index, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    public static String dump(byte[] data, long offset, int index, int length) {
        if (data == null || data.length == 0) {
            return "No Data" + EOL;
        }
        int data_length = (length == Integer.MAX_VALUE || length < 0 || index + length < 0) ? data.length : Math.min(data.length, index + length);
        if (index < 0 || index >= data.length) {
            String err = "illegal index: " + index + " into array of length " + data.length;
            throw new ArrayIndexOutOfBoundsException(err);
        }
        long display_offset = offset + ((long) index);
        StringBuilder buffer = new StringBuilder(74);
        for (int j = index; j < data_length; j += 16) {
            int chars_read = data_length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            writeHex(buffer, display_offset, 8, "");
            for (int k = 0; k < 16; k++) {
                if (k < chars_read) {
                    writeHex(buffer, data[k + j], 2, " ");
                } else {
                    buffer.append("   ");
                }
            }
            buffer.append(' ');
            for (int k2 = 0; k2 < chars_read; k2++) {
                buffer.append(toAscii(data[k2 + j]));
            }
            buffer.append(EOL);
            display_offset += (long) chars_read;
        }
        return buffer.toString();
    }

    public static char toAscii(int dataB) {
        char charB = (char) (dataB & 255);
        if (Character.isISOControl(charB) || charB == 221 || charB == 255) {
            return '.';
        }
        return charB;
    }

    public static String toHex(byte[] value) {
        StringBuilder retVal = new StringBuilder();
        retVal.append('[');
        if (value != null && value.length > 0) {
            for (int x = 0; x < value.length; x++) {
                if (x > 0) {
                    retVal.append(", ");
                }
                retVal.append(toHex(value[x]));
            }
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(short[] value) {
        StringBuilder retVal = new StringBuilder();
        retVal.append('[');
        for (int x = 0; x < value.length; x++) {
            if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        retVal.append(']');
        return retVal.toString();
    }

    public static String toHex(byte[] value, int bytesPerLine) {
        if (value.length == 0) {
            return ": 0";
        }
        int digits = (int) Math.round((Math.log(value.length) / Math.log(10.0d)) + 0.5d);
        StringBuilder retVal = new StringBuilder();
        writeHex(retVal, 0L, digits, "");
        retVal.append(": ");
        int i = -1;
        for (int x = 0; x < value.length; x++) {
            i++;
            if (i == bytesPerLine) {
                retVal.append('\n');
                writeHex(retVal, x, digits, "");
                retVal.append(": ");
                i = 0;
            } else if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        return retVal.toString();
    }

    public static String toHex(short value) {
        StringBuilder sb = new StringBuilder(4);
        writeHex(sb, 65535 & value, 4, "");
        return sb.toString();
    }

    public static String toHex(byte value) {
        StringBuilder sb = new StringBuilder(2);
        writeHex(sb, value & 255, 2, "");
        return sb.toString();
    }

    public static String toHex(int value) {
        StringBuilder sb = new StringBuilder(8);
        writeHex(sb, ((long) value) & 4294967295L, 8, "");
        return sb.toString();
    }

    public static String toHex(long value) {
        StringBuilder sb = new StringBuilder(16);
        writeHex(sb, value, 16, "");
        return sb.toString();
    }

    public static String toHex(String value) {
        return (value == null || value.length() == 0) ? "[]" : toHex(value.getBytes(LocaleUtil.CHARSET_1252));
    }

    public static void dump(InputStream in, PrintStream out, int start, int bytesToDump) throws IOException {
        int c;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (bytesToDump == -1) {
            int c2 = in.read();
            while (c2 != -1) {
                buf.write(c2);
                c2 = in.read();
            }
        } else {
            int c3 = bytesToDump;
            while (true) {
                int bytesRemaining = c3 - 1;
                if (c3 <= 0 || (c = in.read()) == -1) {
                    break;
                }
                buf.write(c);
                c3 = bytesRemaining;
            }
        }
        byte[] data = buf.toByteArray();
        dump(data, 0L, out, start, data.length);
    }

    public static String longToHex(long value) {
        StringBuilder sb = new StringBuilder(18);
        writeHex(sb, value, 16, "0x");
        return sb.toString();
    }

    public static String intToHex(int value) {
        StringBuilder sb = new StringBuilder(10);
        writeHex(sb, ((long) value) & 4294967295L, 8, "0x");
        return sb.toString();
    }

    public static String shortToHex(int value) {
        StringBuilder sb = new StringBuilder(6);
        writeHex(sb, ((long) value) & 65535, 4, "0x");
        return sb.toString();
    }

    public static String byteToHex(int value) {
        StringBuilder sb = new StringBuilder(4);
        writeHex(sb, ((long) value) & 255, 2, "0x");
        return sb.toString();
    }

    private static void writeHex(StringBuilder sb, long value, int nDigits, String prefix) {
        sb.append(prefix);
        char[] buf = new char[nDigits];
        long acc = value;
        for (int i = nDigits - 1; i >= 0; i--) {
            int digit = (int) (15 & acc);
            buf[i] = (char) (digit < 10 ? digit + 48 : (digit + 65) - 10);
            acc >>>= 4;
        }
        sb.append(buf);
    }

    public static void main(String[] args) throws IOException {
        InputStream in = new FileInputStream(args[0]);
        byte[] b = IOUtils.toByteArray(in);
        in.close();
        System.out.println(dump(b, 0L, 0));
    }
}
