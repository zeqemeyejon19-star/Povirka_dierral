package org.apache.poi.sl.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface Shape<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> {
    void draw(Graphics2D graphics2D, Rectangle2D rectangle2D);

    Rectangle2D getAnchor();

    ShapeContainer<S, P> getParent();

    Sheet<S, P> getSheet();
}
