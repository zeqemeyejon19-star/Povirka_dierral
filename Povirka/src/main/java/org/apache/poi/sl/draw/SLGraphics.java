package org.apache.poi.sl.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.sl.usermodel.FreeformShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.sl.usermodel.TextBox;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public final class SLGraphics extends Graphics2D implements Cloneable {
    private GroupShape<?, ?> _group;
    protected POILogger log = POILogFactory.getLogger(getClass());
    private AffineTransform _transform = new AffineTransform();
    private Stroke _stroke = new BasicStroke();
    private Paint _paint = Color.black;
    private Font _font = new Font(HSSFFont.FONT_ARIAL, 0, 12);
    private Color _background = Color.black;
    private Color _foreground = Color.white;
    private RenderingHints _hints = new RenderingHints((Map) null);

    public SLGraphics(GroupShape<?, ?> group) {
        this._group = group;
    }

    public GroupShape<?, ?> getShapeGroup() {
        return this._group;
    }

    public Font getFont() {
        return this._font;
    }

    public void setFont(Font font) {
        this._font = font;
    }

    public Color getColor() {
        return this._foreground;
    }

    public void setColor(Color c) {
        setPaint(c);
    }

    public Stroke getStroke() {
        return this._stroke;
    }

    public void setStroke(Stroke s) {
        this._stroke = s;
    }

    public Paint getPaint() {
        return this._paint;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            return;
        }
        this._paint = paint;
        if (paint instanceof Color) {
            this._foreground = (Color) paint;
        }
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this._transform);
    }

    public void setTransform(AffineTransform Tx) {
        this._transform = new AffineTransform(Tx);
    }

    public void draw(Shape shape) {
        Path2D.Double path = new Path2D.Double(this._transform.createTransformedShape(shape));
        FreeformShape<?, ?> p = this._group.createFreeform();
        p.setPath(path);
        p.setFillColor(null);
        applyStroke(p);
        Color color = this._paint;
        if (color instanceof Color) {
            p.setStrokeStyle(color);
        }
    }

    public void drawString(String s, float x, float y) {
        TextBox<?, ?> txt = this._group.createTextBox();
        TextRun rt = (TextRun) txt.getTextParagraphs().get(0).getTextRuns().get(0);
        rt.setFontSize(Double.valueOf(this._font.getSize()));
        rt.setFontFamily(this._font.getFamily());
        if (getColor() != null) {
            rt.setFontColor(DrawPaint.createSolidPaint(getColor()));
        }
        if (this._font.isBold()) {
            rt.setBold(true);
        }
        if (this._font.isItalic()) {
            rt.setItalic(true);
        }
        txt.setText(s);
        txt.setInsets(new Insets2D(0.0d, 0.0d, 0.0d, 0.0d));
        txt.setWordWrap(false);
        txt.setHorizontalCentered(false);
        txt.setVerticalAlignment(VerticalAlignment.MIDDLE);
        TextLayout layout = new TextLayout(s, this._font, getFontRenderContext());
        float ascent = layout.getAscent();
        float width = (float) Math.floor(layout.getAdvance());
        float height = ascent * 2.0f;
        txt.setAnchor(new Rectangle((int) x, (int) (y - ((height / 2.0f) + (ascent / 2.0f))), (int) width, (int) height));
    }

    public void fill(Shape shape) {
        Path2D.Double path = new Path2D.Double(this._transform.createTransformedShape(shape));
        FreeformShape<?, ?> p = this._group.createFreeform();
        p.setPath(path);
        applyPaint(p);
        p.setStrokeStyle(new Object[0]);
    }

    public void translate(int x, int y) {
        this._transform.translate(x, y);
    }

    @NotImplemented
    public void clip(Shape s) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    @NotImplemented
    public Shape getClip() {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
            return null;
        }
        return null;
    }

    public void scale(double sx, double sy) {
        this._transform.scale(sx, sy);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void drawString(String str, int x, int y) {
        drawString(str, x, y);
    }

    public void fillOval(int x, int y, int width, int height) {
        fill(new Ellipse2D.Double(x, y, width, height));
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        fill(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 2));
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        draw(new Arc2D.Double(x, y, width, height, startAngle, arcAngle, 0));
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints > 0) {
            GeneralPath path = new GeneralPath();
            path.moveTo(xPoints[0], yPoints[0]);
            for (int i = 1; i < nPoints; i++) {
                path.lineTo(xPoints[i], yPoints[i]);
            }
            draw(path);
        }
    }

    public void drawOval(int x, int y, int width, int height) {
        draw(new Ellipse2D.Double(x, y, width, height));
    }

    @NotImplemented
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    @NotImplemented
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    @NotImplemented
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    @NotImplemented
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    @NotImplemented
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    public void dispose() {
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        draw(new Line2D.Double(x1, y1, x2, y2));
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        fill(polygon);
    }

    public void fillRect(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        fill(rect);
    }

    public void drawRect(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        draw(rect);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        draw(polygon);
    }

    public void clipRect(int x, int y, int width, int height) {
        clip(new Rectangle(x, y, width, height));
    }

    @NotImplemented
    public void setClip(Shape clip) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    public Rectangle getClipBounds() {
        Shape c = getClip();
        if (c == null) {
            return null;
        }
        return c.getBounds();
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        drawString(iterator, x, y);
    }

    public void clearRect(int x, int y, int width, int height) {
        Paint paint = getPaint();
        setColor(getBackground());
        fillRect(x, y, width, height);
        setPaint(paint);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    public void rotate(double theta) {
        this._transform.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        this._transform.rotate(theta, x, y);
    }

    public void shear(double shx, double shy) {
        this._transform.shear(shx, shy);
    }

    public FontRenderContext getFontRenderContext() {
        boolean isAntiAliased = RenderingHints.VALUE_TEXT_ANTIALIAS_ON.equals(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
        boolean usesFractionalMetrics = RenderingHints.VALUE_FRACTIONALMETRICS_ON.equals(getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
        return new FontRenderContext(new AffineTransform(), isAntiAliased, usesFractionalMetrics);
    }

    public void transform(AffineTransform Tx) {
        this._transform.concatenate(Tx);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        drawImage((Image) op.filter(img, (BufferedImage) null), x, y, (ImageObserver) null);
    }

    public void setBackground(Color color) {
        if (color == null) {
            return;
        }
        this._background = color;
    }

    public Color getBackground() {
        return this._background;
    }

    @NotImplemented
    public void setComposite(Composite comp) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    @NotImplemented
    public Composite getComposite() {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
            return null;
        }
        return null;
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this._hints.get(hintKey);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this._hints.put(hintKey, hintValue);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape glyphOutline = g.getOutline(x, y);
        fill(glyphOutline);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public void addRenderingHints(Map<?, ?> hints) {
        this._hints.putAll(hints);
    }

    public void translate(double tx, double ty) {
        this._transform.translate(tx, ty);
    }

    @NotImplemented
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke) {
            s = getStroke().createStrokedShape(s);
        }
        return getTransform().createTransformedShape(s).intersects(rect);
    }

    public RenderingHints getRenderingHints() {
        return this._hints;
    }

    public void setRenderingHints(Map<?, ?> hints) {
        RenderingHints renderingHints = new RenderingHints((Map) null);
        this._hints = renderingHints;
        renderingHints.putAll(hints);
    }

    @NotImplemented
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    @NotImplemented
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
        return false;
    }

    public Graphics create() {
        try {
            return (Graphics) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public FontMetrics getFontMetrics(Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }

    @NotImplemented
    public void setXORMode(Color c1) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    @NotImplemented
    public void setPaintMode() {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    @NotImplemented
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    @NotImplemented
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        if (this.log.check(5)) {
            this.log.log(5, "Not implemented");
        }
    }

    protected void applyStroke(SimpleShape<?, ?> shape) {
        BasicStroke basicStroke = this._stroke;
        if (basicStroke instanceof BasicStroke) {
            BasicStroke bs = basicStroke;
            shape.setStrokeStyle(Double.valueOf(bs.getLineWidth()));
            float[] dash = bs.getDashArray();
            if (dash != null) {
                shape.setStrokeStyle(StrokeStyle.LineDash.DASH);
            }
        }
    }

    protected void applyPaint(SimpleShape<?, ?> shape) {
        Color color = this._paint;
        if (color instanceof Color) {
            shape.setFillColor(color);
        }
    }
}
