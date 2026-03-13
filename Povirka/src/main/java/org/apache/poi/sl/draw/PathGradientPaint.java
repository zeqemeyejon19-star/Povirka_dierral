package org.apache.poi.sl.draw;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
class PathGradientPaint implements Paint {
    protected final int capStyle;
    protected final Color[] colors;
    protected final float[] fractions;
    protected final int joinStyle;
    protected final int transparency;

    public PathGradientPaint(Color[] colors, float[] fractions) {
        this(colors, fractions, 1, 1);
    }

    public PathGradientPaint(Color[] colors, float[] fractions, int capStyle, int joinStyle) {
        this.colors = (Color[]) colors.clone();
        this.fractions = (float[]) fractions.clone();
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        int i = 1;
        int len$ = colors.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            Color c = colors[i$];
            if (c != null) {
                i = (i == 0 || c.getAlpha() != 255) ? 0 : 1;
            }
            i$++;
        }
        this.transparency = i == 0 ? 3 : 1;
    }

    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        return new PathGradientContext(cm, deviceBounds, userBounds, transform, hints);
    }

    public int getTransparency() {
        return this.transparency;
    }

    class PathGradientContext implements PaintContext {
        protected final Rectangle deviceBounds;
        protected final int gradientSteps;
        protected final RenderingHints hints;
        protected final PaintContext pCtx;
        WritableRaster raster;
        protected final Shape shape;
        protected final Rectangle2D userBounds;
        protected final AffineTransform xform;

        /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.geom.IllegalPathStateException */
        public PathGradientContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) throws IllegalPathStateException {
            Shape shape = (Shape) hints.get(Drawable.GRADIENT_SHAPE);
            this.shape = shape;
            if (shape == null) {
                throw new IllegalPathStateException("PathGradientPaint needs a shape to be set via the rendering hint Drawable.GRADIANT_SHAPE.");
            }
            this.deviceBounds = deviceBounds;
            this.userBounds = userBounds;
            this.xform = xform;
            this.hints = hints;
            int gradientSteps = getGradientSteps(shape);
            this.gradientSteps = gradientSteps;
            LinearGradientPaint gradientPaint = new LinearGradientPaint(new Point2D.Double(0.0d, 0.0d), new Point2D.Double(gradientSteps, 0.0d), PathGradientPaint.this.fractions, PathGradientPaint.this.colors, MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.ColorSpaceType.SRGB, new AffineTransform());
            Rectangle bounds = new Rectangle(0, 0, gradientSteps, 1);
            this.pCtx = gradientPaint.createContext(cm, bounds, bounds, new AffineTransform(), hints);
        }

        public void dispose() {
        }

        public ColorModel getColorModel() {
            return this.pCtx.getColorModel();
        }

        public Raster getRaster(int xOffset, int yOffset, int w, int h) {
            ColorModel cm = getColorModel();
            if (this.raster == null) {
                createRaster();
            }
            WritableRaster childRaster = cm.createCompatibleWritableRaster(w, h);
            Rectangle2D.Double r14 = new Rectangle2D.Double(xOffset, yOffset, w, h);
            if (!r14.intersects(this.deviceBounds)) {
                return childRaster;
            }
            Rectangle2D.Double r5 = new Rectangle2D.Double();
            Rectangle2D.intersect(r14, this.deviceBounds, r5);
            int dx = (int) (r5.getX() - this.deviceBounds.getX());
            int dy = (int) (r5.getY() - this.deviceBounds.getY());
            int dw = (int) r5.getWidth();
            int dh = (int) r5.getHeight();
            Object data = this.raster.getDataElements(dx, dy, dw, dh, (Object) null);
            int dx2 = (int) (r5.getX() - r14.getX());
            int dy2 = (int) (r5.getY() - r14.getY());
            childRaster.setDataElements(dx2, dy2, dw, dh, data);
            return childRaster;
        }

        protected int getGradientSteps(Shape gradientShape) {
            Rectangle rect = gradientShape.getBounds();
            int lower = 1;
            int upper = (int) (Math.max(rect.getWidth(), rect.getHeight()) / 2.0d);
            while (lower < upper - 1) {
                int mid = ((upper - lower) / 2) + lower;
                BasicStroke bs = new BasicStroke(mid, PathGradientPaint.this.capStyle, PathGradientPaint.this.joinStyle);
                Area area = new Area(bs.createStrokedShape(gradientShape));
                if (area.isSingular()) {
                    upper = mid;
                } else {
                    lower = mid;
                }
            }
            return upper;
        }

        protected void createRaster() {
            ColorModel cm = getColorModel();
            this.raster = cm.createCompatibleWritableRaster((int) this.deviceBounds.getWidth(), (int) this.deviceBounds.getHeight());
            BufferedImage img = new BufferedImage(cm, this.raster, false, (Hashtable) null);
            Graphics2D graphics = img.createGraphics();
            graphics.setRenderingHints(this.hints);
            graphics.translate(-this.deviceBounds.getX(), -this.deviceBounds.getY());
            graphics.transform(this.xform);
            Raster img2 = this.pCtx.getRaster(0, 0, this.gradientSteps, 1);
            int[] rgb = new int[cm.getNumComponents()];
            for (int i = this.gradientSteps - 1; i >= 0; i--) {
                img2.getPixel(i, 0, rgb);
                Color c = new Color(rgb[0], rgb[1], rgb[2]);
                if (rgb.length == 4) {
                    graphics.setComposite(AlphaComposite.getInstance(2, rgb[3] / 255.0f));
                }
                graphics.setStroke(new BasicStroke(i + 1, PathGradientPaint.this.capStyle, PathGradientPaint.this.joinStyle));
                graphics.setColor(c);
                graphics.draw(this.shape);
            }
            graphics.dispose();
        }
    }
}
