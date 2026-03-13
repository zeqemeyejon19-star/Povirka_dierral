package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum TextAlignment {
    TOP(1),
    CENTER(2),
    BASELINE(3),
    BOTTOM(4),
    AUTO(5);

    private static Map<Integer, TextAlignment> imap = new HashMap();
    private final int value;

    static {
        TextAlignment[] arr$ = values();
        for (TextAlignment p : arr$) {
            imap.put(Integer.valueOf(p.getValue()), p);
        }
    }

    TextAlignment(int val) {
        this.value = val;
    }

    public static TextAlignment valueOf(int type) {
        TextAlignment align = imap.get(Integer.valueOf(type));
        if (align == null) {
            throw new IllegalArgumentException("Unknown text alignment: " + type);
        }
        return align;
    }

    public int getValue() {
        return this.value;
    }
}
