package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum PrintCellComments {
    NONE(1),
    AS_DISPLAYED(2),
    AT_END(3);

    private static PrintCellComments[] _table = new PrintCellComments[4];
    private int comments;

    static {
        PrintCellComments[] arr$ = values();
        for (PrintCellComments c : arr$) {
            _table[c.getValue()] = c;
        }
    }

    PrintCellComments(int comments) {
        this.comments = comments;
    }

    public int getValue() {
        return this.comments;
    }

    public static PrintCellComments valueOf(int value) {
        return _table[value];
    }
}
