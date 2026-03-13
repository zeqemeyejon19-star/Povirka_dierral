package org.apache.poi.sl.usermodel;

import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface Background<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> extends Shape<S, P> {
    FillStyle getFillStyle();
}
