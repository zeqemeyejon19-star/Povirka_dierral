package org.apache.poi.ss.formula.constant;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class ConstantValueParser {
    private static final Object EMPTY_REPRESENTATION = null;
    private static final int FALSE_ENCODING = 0;
    private static final int TRUE_ENCODING = 1;
    private static final int TYPE_BOOLEAN = 4;
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_ERROR_CODE = 16;
    private static final int TYPE_NUMBER = 1;
    private static final int TYPE_STRING = 2;

    private ConstantValueParser() {
    }

    public static Object[] parse(LittleEndianInput in, int nValues) {
        Object[] result = new Object[nValues];
        for (int i = 0; i < result.length; i++) {
            result[i] = readAConstantValue(in);
        }
        return result;
    }

    private static Object readAConstantValue(LittleEndianInput in) {
        byte grbit = in.readByte();
        if (grbit == 0) {
            in.readLong();
            return EMPTY_REPRESENTATION;
        }
        if (grbit == 1) {
            return new Double(in.readDouble());
        }
        if (grbit == 2) {
            return StringUtil.readUnicodeString(in);
        }
        if (grbit == 4) {
            return readBoolean(in);
        }
        if (grbit == 16) {
            int errCode = in.readUShort();
            in.readUShort();
            in.readInt();
            return ErrorConstant.valueOf(errCode);
        }
        throw new RuntimeException("Unknown grbit value (" + ((int) grbit) + ")");
    }

    private static Object readBoolean(LittleEndianInput in) {
        byte val = (byte) in.readLong();
        if (val == 0) {
            return Boolean.FALSE;
        }
        if (val == 1) {
            return Boolean.TRUE;
        }
        throw new RuntimeException("unexpected boolean encoding (" + ((int) val) + ")");
    }

    public static int getEncodedSize(Object[] values) {
        int result = values.length * 1;
        for (Object obj : values) {
            result += getEncodedSize(obj);
        }
        return result;
    }

    private static int getEncodedSize(Object object) {
        Class<?> cls;
        if (object == EMPTY_REPRESENTATION || (cls = object.getClass()) == Boolean.class || cls == Double.class || cls == ErrorConstant.class) {
            return 8;
        }
        String strVal = (String) object;
        return StringUtil.getEncodedSize(strVal);
    }

    public static void encode(LittleEndianOutput out, Object[] values) {
        for (Object obj : values) {
            encodeSingleValue(out, obj);
        }
    }

    private static void encodeSingleValue(LittleEndianOutput out, Object value) {
        if (value == EMPTY_REPRESENTATION) {
            out.writeByte(0);
            out.writeLong(0L);
            return;
        }
        if (value instanceof Boolean) {
            Boolean bVal = (Boolean) value;
            out.writeByte(4);
            long longVal = bVal.booleanValue() ? 1L : 0L;
            out.writeLong(longVal);
            return;
        }
        if (value instanceof Double) {
            Double dVal = (Double) value;
            out.writeByte(1);
            out.writeDouble(dVal.doubleValue());
        } else if (value instanceof String) {
            String val = (String) value;
            out.writeByte(2);
            StringUtil.writeUnicodeString(out, val);
        } else {
            if (value instanceof ErrorConstant) {
                ErrorConstant ecVal = (ErrorConstant) value;
                out.writeByte(16);
                long longVal2 = ecVal.getErrorCode();
                out.writeLong(longVal2);
                return;
            }
            throw new IllegalStateException("Unexpected value type (" + value.getClass().getName() + "'");
        }
    }
}
