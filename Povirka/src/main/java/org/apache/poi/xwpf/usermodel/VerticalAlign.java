package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum VerticalAlign {
    BASELINE(1),
    SUPERSCRIPT(2),
    SUBSCRIPT(3);

    private static Map<Integer, VerticalAlign> imap = new HashMap();
    private final int value;

    static {
        VerticalAlign[] arr$ = values();
        for (VerticalAlign p : arr$) {
            imap.put(Integer.valueOf(p.getValue()), p);
        }
    }

    VerticalAlign(int val) {
        this.value = val;
    }

    public static VerticalAlign valueOf(int type) {
        VerticalAlign align = imap.get(Integer.valueOf(type));
        if (align == null) {
            throw new IllegalArgumentException("Unknown vertical alignment: " + type);
        }
        return align;
    }

    public int getValue() {
        return this.value;
    }
}
