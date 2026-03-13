package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.poi.sl.draw.geom.Outline;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.usermodel.FillStyle;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextShape;

/* JADX INFO: loaded from: classes.dex */
public class DrawFreeformShape extends DrawAutoShape {
    public DrawFreeformShape(FreeformShape<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawSimpleShape
    protected Collection<Outline> computeOutlines(Graphics2D graphics) {
        List<Outline> lst = new ArrayList<>();
        FreeformShape<?, ?> fsh = (FreeformShape) getShape();
        Path2D.Double path = fsh.getPath();
        AffineTransform tx = (AffineTransform) graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        if (tx == null) {
            tx = new AffineTransform();
        }
        Shape canvasShape = tx.createTransformedShape(path);
        FillStyle fs = fsh.getFillStyle();
        StrokeStyle ss = fsh.getStrokeStyle();
        Path path2 = new Path(fs != null, ss != null);
        lst.add(new Outline(canvasShape, path2));
        return lst;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawTextShape, org.apache.poi.sl.draw.DrawSimpleShape, org.apache.poi.sl.draw.DrawShape
    public TextShape<?, ? extends TextParagraph<?, ?, ? extends TextRun>> getShape() {
        return (TextShape) this.shape;
    }
}
