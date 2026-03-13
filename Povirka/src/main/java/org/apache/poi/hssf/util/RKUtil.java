package org.apache.poi.hssf.util;

/* JADX INFO: loaded from: classes.dex */
public final class RKUtil {
    private RKUtil() {
    }

    public static double decodeNumber(int number) {
        double rvalue;
        long raw_number = ((long) number) >> 2;
        if ((number & 2) == 2) {
            rvalue = raw_number;
        } else {
            rvalue = Double.longBitsToDouble(raw_number << 34);
        }
        if ((number & 1) == 1) {
            return rvalue / 100.0d;
        }
        return rvalue;
    }
}
