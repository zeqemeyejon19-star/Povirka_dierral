package org.apache.poi.ss.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum CellType {
    _NONE(-1),
    NUMERIC(0),
    STRING(1),
    FORMULA(2),
    BLANK(3),
    BOOLEAN(4),
    ERROR(5);

    private final int code;

    CellType(int code) {
        this.code = code;
    }

    public static CellType forInt(int code) {
        CellType[] arr$ = values();
        for (CellType type : arr$) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CellType code: " + code);
    }

    public int getCode() {
        return this.code;
    }
}
