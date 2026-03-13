package org.apache.poi.xssf.model;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class ParagraphPropertyFetcher<T> {
    private int _level;
    private T _value;

    public abstract boolean fetch(CTTextParagraphProperties cTTextParagraphProperties);

    public T getValue() {
        return this._value;
    }

    public void setValue(T val) {
        this._value = val;
    }

    public ParagraphPropertyFetcher(int level) {
        this._level = level;
    }

    public boolean fetch(CTShape shape) {
        CTTextParagraphProperties[] cTTextParagraphPropertiesArrSelectPath = shape.selectPath("declare namespace xdr='http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing' declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//xdr:txBody/a:lstStyle/a:lvl" + (this._level + 1) + "pPr");
        if (cTTextParagraphPropertiesArrSelectPath.length != 1) {
            return false;
        }
        CTTextParagraphProperties props = cTTextParagraphPropertiesArrSelectPath[0];
        return fetch(props);
    }
}
