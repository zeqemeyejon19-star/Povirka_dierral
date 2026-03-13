package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;

/* JADX INFO: loaded from: classes.dex */
public class DrawNothing implements Drawable {
    protected final Shape<?, ?> shape;

    public DrawNothing(Shape<?, ?> shape) {
        this.shape = shape;
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D graphics) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D context) {
    }
}
