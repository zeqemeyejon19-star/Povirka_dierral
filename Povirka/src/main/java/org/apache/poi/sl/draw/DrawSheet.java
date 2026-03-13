package org.apache.poi.sl.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.Sheet;

/* JADX INFO: loaded from: classes.dex */
public class DrawSheet implements Drawable {
    protected final Sheet<?, ?> sheet;

    public DrawSheet(Sheet<?, ?> sheet) {
        this.sheet = sheet;
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
        Dimension dim = this.sheet.getSlideShow().getPageSize();
        Color whiteTrans = new Color(1.0f, 1.0f, 1.0f, 0.0f);
        graphics.setColor(whiteTrans);
        graphics.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        MasterSheet<?, ?> master = this.sheet.getMasterSheet();
        if (this.sheet.getFollowMasterGraphics() && master != null) {
            drawFact.getDrawable(master).draw(graphics);
        }
        graphics.setRenderingHint(Drawable.GROUP_TRANSFORM, new AffineTransform());
        Iterator<?> it = this.sheet.getShapes().iterator();
        while (it.hasNext()) {
            Shape<?, ?> shape = (Shape) it.next();
            if (canDraw(graphics, shape)) {
                AffineTransform at = graphics.getTransform();
                graphics.setRenderingHint(Drawable.GSAVE, true);
                Drawable drawer = drawFact.getDrawable(shape);
                drawer.applyTransform(graphics);
                drawer.draw(graphics);
                graphics.setTransform(at);
                graphics.setRenderingHint(Drawable.GRESTORE, true);
            }
        }
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D context) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D context) {
    }

    protected boolean canDraw(Graphics2D graphics, Shape<?, ?> shape) {
        return true;
    }
}
