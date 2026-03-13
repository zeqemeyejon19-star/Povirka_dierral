package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.Shape;

/* JADX INFO: loaded from: classes.dex */
public class DrawGroupShape extends DrawShape {
    public DrawGroupShape(GroupShape<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        Rectangle2D interior = getShape().getInteriorAnchor();
        Rectangle2D exterior = getShape().getAnchor();
        AffineTransform tx = (AffineTransform) graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        AffineTransform tx0 = new AffineTransform(tx);
        double scaleX = interior.getWidth() == 0.0d ? 1.0d : exterior.getWidth() / interior.getWidth();
        double scaleY = interior.getHeight() != 0.0d ? exterior.getHeight() / interior.getHeight() : 1.0d;
        tx.translate(exterior.getX(), exterior.getY());
        tx.scale(scaleX, scaleY);
        tx.translate(-interior.getX(), -interior.getY());
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        AffineTransform at2 = graphics.getTransform();
        Iterator i$ = getShape().iterator();
        while (i$.hasNext()) {
            Shape<?, ?> child = (Shape) i$.next();
            AffineTransform at = graphics.getTransform();
            Rectangle2D interior2 = interior;
            graphics.setRenderingHint(Drawable.GSAVE, true);
            Drawable draw = drawFact.getDrawable(child);
            draw.applyTransform(graphics);
            draw.draw(graphics);
            graphics.setTransform(at);
            graphics.setRenderingHint(Drawable.GRESTORE, true);
            interior = interior2;
        }
        graphics.setTransform(at2);
        graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, tx0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawShape
    public GroupShape<?, ?> getShape() {
        return (GroupShape) this.shape;
    }
}
