package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum BreakType {
    PAGE(1),
    COLUMN(2),
    TEXT_WRAPPING(3);

    private static Map<Integer, BreakType> imap = new HashMap();
    private final int value;

    static {
        BreakType[] arr$ = values();
        for (BreakType p : arr$) {
            imap.put(Integer.valueOf(p.getValue()), p);
        }
    }

    BreakType(int val) {
        this.value = val;
    }

    public static BreakType valueOf(int type) {
        BreakType bType = imap.get(Integer.valueOf(type));
        if (bType == null) {
            throw new IllegalArgumentException("Unknown break type: " + type);
        }
        return bType;
    }

    public int getValue() {
        return this.value;
    }
}
