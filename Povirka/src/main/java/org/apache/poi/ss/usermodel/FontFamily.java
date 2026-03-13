package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum FontFamily {
    NOT_APPLICABLE(0),
    ROMAN(1),
    SWISS(2),
    MODERN(3),
    SCRIPT(4),
    DECORATIVE(5);

    private static FontFamily[] _table = new FontFamily[6];
    private int family;

    static {
        FontFamily[] arr$ = values();
        for (FontFamily c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    FontFamily(int value) {
        this.family = value;
    }

    public int getValue() {
        return this.family;
    }

    public static FontFamily valueOf(int family) {
        return _table[family];
    }
}
