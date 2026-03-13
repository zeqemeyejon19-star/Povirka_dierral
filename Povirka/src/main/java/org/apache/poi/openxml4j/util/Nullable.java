package org.apache.poi.openxml4j.util;

/* JADX INFO: loaded from: classes.dex */
public final class Nullable<E> {
    private E value;

    public Nullable() {
    }

    public Nullable(E value) {
        this.value = value;
    }

    public E getValue() {
        return this.value;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public void nullify() {
        this.value = null;
    }
}
