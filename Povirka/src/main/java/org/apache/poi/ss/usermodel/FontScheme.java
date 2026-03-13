package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum FontScheme {
    NONE(1),
    MAJOR(2),
    MINOR(3);

    private static FontScheme[] _table = new FontScheme[4];
    private int value;

    static {
        FontScheme[] arr$ = values();
        for (FontScheme c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    FontScheme(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static FontScheme valueOf(int value) {
        return _table[value];
    }
}
