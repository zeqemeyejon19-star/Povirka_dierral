package org.apache.poi.wp.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum HeaderFooterType {
    DEFAULT(2),
    EVEN(1),
    FIRST(3);

    private final int code;

    HeaderFooterType(int i) {
        this.code = i;
    }

    public int toInt() {
        return this.code;
    }

    public static HeaderFooterType forInt(int i) {
        HeaderFooterType[] arr$ = values();
        for (HeaderFooterType type : arr$) {
            if (type.code == i) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid HeaderFooterType code: " + i);
    }
}
