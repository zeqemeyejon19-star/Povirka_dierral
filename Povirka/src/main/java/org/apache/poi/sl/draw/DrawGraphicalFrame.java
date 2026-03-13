package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.GraphicalFrame;
import org.apache.poi.sl.usermodel.PictureShape;

/* JADX INFO: loaded from: classes.dex */
public class DrawGraphicalFrame extends DrawShape {
    public DrawGraphicalFrame(GraphicalFrame<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D context) {
        PictureShape<?, ?> ps = ((GraphicalFrame) getShape()).getFallbackPicture();
        if (ps == null) {
            return;
        }
        DrawPictureShape dps = DrawFactory.getInstance(context).getDrawable(ps);
        dps.draw(context);
    }
}
