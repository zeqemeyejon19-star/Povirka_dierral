package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public enum BreakClear {
    NONE(1),
    LEFT(2),
    RIGHT(3),
    ALL(4);

    private static Map<Integer, BreakClear> imap = new HashMap();
    private final int value;

    static {
        BreakClear[] arr$ = values();
        for (BreakClear p : arr$) {
            imap.put(Integer.valueOf(p.getValue()), p);
        }
    }

    BreakClear(int val) {
        this.value = val;
    }

    public static BreakClear valueOf(int type) {
        BreakClear bType = imap.get(Integer.valueOf(type));
        if (bType == null) {
            throw new IllegalArgumentException("Unknown break clear type: " + type);
        }
        return bType;
    }

    public int getValue() {
        return this.value;
    }
}
