package org.apache.poi.sl.draw;

import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Background;
import org.apache.poi.sl.usermodel.Slide;

/* JADX INFO: loaded from: classes.dex */
public class DrawSlide extends DrawSheet {
    public DrawSlide(Slide<?, ?> slide) {
        super(slide);
    }

    @Override // org.apache.poi.sl.draw.DrawSheet, org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        graphics.setRenderingHint(Drawable.CURRENT_SLIDE, this.sheet);
        Background<S, P> background = this.sheet.getBackground();
        if (background != 0) {
            DrawBackground db = DrawFactory.getInstance(graphics).getDrawable((Background<?, ?>) background);
            db.draw(graphics);
        }
        super.draw(graphics);
        graphics.setRenderingHint(Drawable.CURRENT_SLIDE, (Object) null);
    }
}
