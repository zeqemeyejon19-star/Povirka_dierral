package org.apache.poi.sl.usermodel;

/* JADX INFO: loaded from: classes.dex */
public enum RectAlign {
    TOP_LEFT("tl"),
    TOP("t"),
    TOP_RIGHT("tr"),
    LEFT("l"),
    CENTER("ctr"),
    RIGHT("r"),
    BOTTOM_LEFT("bl"),
    BOTTOM("b"),
    BOTTOM_RIGHT("br");

    private final String dir;

    RectAlign(String dir) {
        this.dir = dir;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.dir;
    }
}
