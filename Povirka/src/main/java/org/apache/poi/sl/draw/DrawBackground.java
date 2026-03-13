package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import org.apache.poi.sl.usermodel.Sheet;

/* JADX INFO: loaded from: classes.dex */
public class DrawBackground extends DrawShape {
    public DrawBackground(Background<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        Dimension pg = this.shape.getSheet().getSlideShow().getPageSize();
        final Rectangle2D.Double r10 = new Rectangle2D.Double(0.0d, 0.0d, pg.getWidth(), pg.getHeight());
        PlaceableShape<?, ?> ps = new PlaceableShape() { // from class: org.apache.poi.sl.draw.DrawBackground.1
            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public ShapeContainer<?, ?> getParent() {
                return null;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public Rectangle2D getAnchor() {
                return r10;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setAnchor(Rectangle2D newAnchor) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public double getRotation() {
                return 0.0d;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setRotation(double theta) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setFlipHorizontal(boolean flip) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public void setFlipVertical(boolean flip) {
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public boolean getFlipHorizontal() {
                return false;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public boolean getFlipVertical() {
                return false;
            }

            @Override // org.apache.poi.sl.usermodel.PlaceableShape
            public Sheet<?, ?> getSheet() {
                return DrawBackground.this.shape.getSheet();
            }
        };
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        DrawPaint dp = drawFact.getPaint(ps);
        Paint fill = dp.getPaint(graphics, getShape().getFillStyle().getPaint());
        Rectangle2D anchor2 = getAnchor(graphics, (Rectangle2D) r10);
        if (fill != null) {
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, r10);
            graphics.setPaint(fill);
            graphics.fill(anchor2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawShape
    public Background<?, ?> getShape() {
        return (Background) this.shape;
    }
}
