package org.apache.poi.hssf.usermodel;

import androidx.appcompat.widget.ActivityChooserView;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class EscherGraphics extends Graphics {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) EscherGraphics.class);
    private Color background;
    private final HSSFShapeGroup escherGroup;
    private Font font;
    private Color foreground;
    private final float verticalPixelsPerPoint;
    private float verticalPointsPerPixel;
    private final HSSFWorkbook workbook;

    public EscherGraphics(HSSFShapeGroup escherGroup, HSSFWorkbook workbook, Color forecolor, float verticalPointsPerPixel) {
        this.verticalPointsPerPixel = 1.0f;
        this.background = Color.white;
        this.escherGroup = escherGroup;
        this.workbook = workbook;
        this.verticalPointsPerPixel = verticalPointsPerPixel;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel;
        this.font = new Font(HSSFFont.FONT_ARIAL, 0, 10);
        this.foreground = forecolor;
    }

    EscherGraphics(HSSFShapeGroup escherGroup, HSSFWorkbook workbook, Color foreground, Font font, float verticalPointsPerPixel) {
        this.verticalPointsPerPixel = 1.0f;
        this.background = Color.white;
        this.escherGroup = escherGroup;
        this.workbook = workbook;
        this.foreground = foreground;
        this.font = font;
        this.verticalPointsPerPixel = verticalPointsPerPixel;
        this.verticalPixelsPerPoint = 1.0f / verticalPointsPerPixel;
    }

    @NotImplemented
    public void clearRect(int x, int y, int width, int height) {
        Color color = this.foreground;
        setColor(this.background);
        fillRect(x, y, width, height);
        setColor(color);
    }

    @NotImplemented
    public void clipRect(int x, int y, int width, int height) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "clipRect not supported");
        }
    }

    @NotImplemented
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "copyArea not supported");
        }
    }

    public Graphics create() {
        EscherGraphics g = new EscherGraphics(this.escherGroup, this.workbook, this.foreground, this.font, this.verticalPointsPerPixel);
        return g;
    }

    public void dispose() {
    }

    @NotImplemented
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawArc not supported");
        }
    }

    @NotImplemented
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawImage not supported");
        }
        return true;
    }

    @NotImplemented
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawImage not supported");
        }
        return true;
    }

    public boolean drawImage(Image image, int i, int j, int k, int l, Color color, ImageObserver imageobserver) {
        return drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageobserver), image.getHeight(imageobserver), color, imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, int k, int l, ImageObserver imageobserver) {
        return drawImage(image, i, j, i + k, j + l, 0, 0, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, Color color, ImageObserver imageobserver) {
        return drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), color, imageobserver);
    }

    public boolean drawImage(Image image, int i, int j, ImageObserver imageobserver) {
        return drawImage(image, i, j, image.getWidth(imageobserver), image.getHeight(imageobserver), imageobserver);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        drawLine(x1, y1, x2, y2, 0);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int width) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x1, y1, x2, y2));
        shape.setShapeType(20);
        shape.setLineWidth(width);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    public void drawOval(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineWidth(0);
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setNoFill(true);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        int right = findBiggest(xPoints);
        int bottom = findBiggest(yPoints);
        int left = findSmallest(xPoints);
        int top = findSmallest(yPoints);
        HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(addToAll(xPoints, -left), addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineWidth(0);
        shape.setNoFill(true);
    }

    private int[] addToAll(int[] values, int amount) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i] + amount;
        }
        return result;
    }

    @NotImplemented
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawPolyline not supported");
        }
    }

    @NotImplemented
    public void drawRect(int x, int y, int width, int height) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawRect not supported");
        }
    }

    @NotImplemented
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawRoundRect not supported");
        }
    }

    public void drawString(String str, int x, int y) {
        Font excelFont;
        if (str == null || str.equals("")) {
            return;
        }
        Font font = this.font;
        if (this.font.getName().equals("SansSerif")) {
            excelFont = new Font(HSSFFont.FONT_ARIAL, this.font.getStyle(), (int) (this.font.getSize() / this.verticalPixelsPerPoint));
        } else {
            excelFont = new Font(this.font.getName(), this.font.getStyle(), (int) (this.font.getSize() / this.verticalPixelsPerPoint));
        }
        FontDetails d = StaticFontMetrics.getFontDetails(excelFont);
        int width = (d.getStringWidth(str) * 8) + 12;
        int height = ((int) ((this.font.getSize() / this.verticalPixelsPerPoint) + 6.0f)) * 2;
        float size = this.font.getSize();
        float f = this.verticalPixelsPerPoint;
        int y2 = (int) (y - ((size / f) + (f * 2.0f)));
        HSSFTextbox textbox = this.escherGroup.createTextbox(new HSSFChildAnchor(x, y2, x + width, y2 + height));
        textbox.setNoFill(true);
        textbox.setLineStyle(-1);
        HSSFRichTextString s = new HSSFRichTextString(str);
        HSSFFont hssfFont = matchFont(excelFont);
        s.applyFont(hssfFont);
        textbox.setString(s);
    }

    private HSSFFont matchFont(Font matchFont) {
        HSSFColor hssfColor = this.workbook.getCustomPalette().findColor((byte) this.foreground.getRed(), (byte) this.foreground.getGreen(), (byte) this.foreground.getBlue());
        if (hssfColor == null) {
            hssfColor = this.workbook.getCustomPalette().findSimilarColor((byte) this.foreground.getRed(), (byte) this.foreground.getGreen(), (byte) this.foreground.getBlue());
        }
        boolean bold = (matchFont.getStyle() & 1) != 0;
        boolean italic = (matchFont.getStyle() & 2) != 0;
        HSSFFont hssfFont = this.workbook.findFont(bold, hssfColor.getIndex(), (short) (matchFont.getSize() * 20), matchFont.getName(), italic, false, (short) 0, (byte) 0);
        if (hssfFont == null) {
            HSSFFont hssfFont2 = this.workbook.createFont();
            hssfFont2.setBold(bold);
            hssfFont2.setColor(hssfColor.getIndex());
            hssfFont2.setFontHeight((short) (matchFont.getSize() * 20));
            hssfFont2.setFontName(matchFont.getName());
            hssfFont2.setItalic(italic);
            hssfFont2.setStrikeout(false);
            hssfFont2.setTypeOffset((short) 0);
            hssfFont2.setUnderline((byte) 0);
            return hssfFont2;
        }
        return hssfFont;
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "drawString not supported");
        }
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "fillArc not supported");
        }
    }

    public void fillOval(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(3);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setNoFill(false);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        int right = findBiggest(xPoints);
        int bottom = findBiggest(yPoints);
        int left = findSmallest(xPoints);
        int top = findSmallest(yPoints);
        HSSFPolygon shape = this.escherGroup.createPolygon(new HSSFChildAnchor(left, top, right, bottom));
        shape.setPolygonDrawArea(right - left, bottom - top);
        shape.setPoints(addToAll(xPoints, -left), addToAll(yPoints, -top));
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    private int findBiggest(int[] values) {
        int result = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > result) {
                result = values[i];
            }
        }
        return result;
    }

    private int findSmallest(int[] values) {
        int result = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (int i = 0; i < values.length; i++) {
            if (values[i] < result) {
                result = values[i];
            }
        }
        return result;
    }

    public void fillRect(int x, int y, int width, int height) {
        HSSFSimpleShape shape = this.escherGroup.createShape(new HSSFChildAnchor(x, y, x + width, y + height));
        shape.setShapeType(1);
        shape.setLineStyle(-1);
        shape.setFillColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
        shape.setLineStyleColor(this.foreground.getRed(), this.foreground.getGreen(), this.foreground.getBlue());
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "fillRoundRect not supported");
        }
    }

    public Shape getClip() {
        return getClipBounds();
    }

    public Rectangle getClipBounds() {
        return null;
    }

    public Color getColor() {
        return this.foreground;
    }

    public Font getFont() {
        return this.font;
    }

    public FontMetrics getFontMetrics(Font f) {
        return Toolkit.getDefaultToolkit().getFontMetrics(f);
    }

    public void setClip(int x, int y, int width, int height) {
        setClip(new Rectangle(x, y, width, height));
    }

    @NotImplemented
    public void setClip(Shape shape) {
    }

    public void setColor(Color color) {
        this.foreground = color;
    }

    public void setFont(Font f) {
        this.font = f;
    }

    @NotImplemented
    public void setPaintMode() {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "setPaintMode not supported");
        }
    }

    @NotImplemented
    public void setXORMode(Color color) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "setXORMode not supported");
        }
    }

    @NotImplemented
    public void translate(int x, int y) {
        POILogger pOILogger = logger;
        if (pOILogger.check(5)) {
            pOILogger.log(5, "translate not supported");
        }
    }

    public Color getBackground() {
        return this.background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    HSSFShapeGroup getEscherGraphics() {
        return this.escherGroup;
    }
}
