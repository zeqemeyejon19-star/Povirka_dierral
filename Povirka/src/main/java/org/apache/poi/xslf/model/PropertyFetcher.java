package org.apache.poi.xslf.model;

import org.apache.poi.util.Internal;
import org.apache.poi.xslf.usermodel.XSLFShape;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class PropertyFetcher<T> {
    private T _value;

    public abstract boolean fetch(XSLFShape xSLFShape);

    public T getValue() {
        return this._value;
    }

    public void setValue(T val) {
        this._value = val;
    }
}
