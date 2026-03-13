package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
public final class PngUtils {
    private static final byte[] PNG_FILE_HEADER = {-119, 80, 78, 71, 13, 10, 26, 10};

    private PngUtils() {
    }

    public static boolean matchesPngHeader(byte[] data, int offset) {
        if (data == null || data.length - offset < PNG_FILE_HEADER.length) {
            return false;
        }
        int i = 0;
        while (true) {
            byte[] bArr = PNG_FILE_HEADER;
            if (i < bArr.length) {
                if (bArr[i] != data[i + offset]) {
                    return false;
                }
                i++;
            } else {
                return true;
            }
        }
    }
}
