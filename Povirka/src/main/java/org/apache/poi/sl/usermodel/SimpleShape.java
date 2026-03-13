package org.apache.poi.sl.usermodel;

import java.awt.Color;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.IAdjustableShape;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TextParagraph;

/* JADX INFO: loaded from: classes.dex */
public interface SimpleShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ?>> extends Shape<S, P>, IAdjustableShape, PlaceableShape<S, P> {
    Hyperlink<S, P> createHyperlink();

    Color getFillColor();

    FillStyle getFillStyle();

    CustomGeometry getGeometry();

    Hyperlink<S, P> getHyperlink();

    LineDecoration getLineDecoration();

    Placeholder getPlaceholder();

    Shadow<S, P> getShadow();

    ShapeType getShapeType();

    StrokeStyle getStrokeStyle();

    void setFillColor(Color color);

    void setPlaceholder(Placeholder placeholder);

    void setShapeType(ShapeType shapeType);

    void setStrokeStyle(Object... objArr);
}
