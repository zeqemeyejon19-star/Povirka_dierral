package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum VerticalAlignment {
    TOP,
    CENTER,
    BOTTOM,
    JUSTIFY,
    DISTRIBUTED;

    public short getCode() {
        return (short) ordinal();
    }

    public static VerticalAlignment forInt(int code) {
        if (code < 0 || code >= values().length) {
            throw new IllegalArgumentException("Invalid VerticalAlignment code: " + code);
        }
        return values()[code];
    }
}
