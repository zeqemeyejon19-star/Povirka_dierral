package org.apache.poi.sl.usermodel;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface Shadow<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> {
    double getAngle();

    double getBlur();

    double getDistance();

    PaintStyle.SolidPaint getFillStyle();

    SimpleShape<S, P> getShadowParent();
}
