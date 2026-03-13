package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum PrintOrientation {
    DEFAULT(1),
    PORTRAIT(2),
    LANDSCAPE(3);

    private static PrintOrientation[] _table = new PrintOrientation[4];
    private int orientation;

    static {
        PrintOrientation[] arr$ = values();
        for (PrintOrientation c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    PrintOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getValue() {
        return this.orientation;
    }

    public static PrintOrientation valueOf(int value) {
        return _table[value];
    }
}
