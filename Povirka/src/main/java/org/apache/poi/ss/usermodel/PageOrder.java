package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum PageOrder {
    DOWN_THEN_OVER(1),
    OVER_THEN_DOWN(2);

    private static PageOrder[] _table = new PageOrder[3];
    private final int order;

    static {
        PageOrder[] arr$ = values();
        for (PageOrder c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    PageOrder(int order) {
        this.order = order;
    }

    public int getValue() {
        return this.order;
    }

    public static PageOrder valueOf(int value) {
        return _table[value];
    }
}
