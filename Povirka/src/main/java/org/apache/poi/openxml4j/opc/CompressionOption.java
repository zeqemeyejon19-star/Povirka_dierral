package org.apache.poi.openxml4j.opc;

/* JADX INFO: loaded from: classes.dex */
public enum CompressionOption {
    FAST(1),
    MAXIMUM(9),
    NORMAL(-1),
    NOT_COMPRESSED(0);

    private final int value;

    CompressionOption(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
