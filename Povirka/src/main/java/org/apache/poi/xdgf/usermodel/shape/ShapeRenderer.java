package org.apache.poi.xdgf.usermodel.shape;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.XDGFText;

/* JADX INFO: loaded from: classes.dex */
public class ShapeRenderer extends ShapeVisitor {
    protected Graphics2D _graphics;

    public ShapeRenderer() {
        this._graphics = null;
    }

    public ShapeRenderer(Graphics2D g) {
        this._graphics = g;
    }

    public void setGraphics(Graphics2D g) {
        this._graphics = g;
    }

    @Override // org.apache.poi.xdgf.usermodel.shape.ShapeVisitor
    public void visit(XDGFShape shape, AffineTransform globalTransform, int level) {
        AffineTransform savedTr = this._graphics.getTransform();
        this._graphics.transform(globalTransform);
        drawPath(shape);
        drawText(shape);
        this._graphics.setTransform(savedTr);
    }

    protected Path2D drawPath(XDGFShape shape) {
        Path2D.Double path = shape.getPath();
        if (path != null) {
            this._graphics.setColor(shape.getLineColor());
            this._graphics.setStroke(shape.getStroke());
            this._graphics.draw(path);
        }
        return path;
    }

    protected void drawText(XDGFShape shape) {
        XDGFText text = shape.getText();
        if (text != null) {
            if (text.getTextContent().equals("Header")) {
                text.getTextBounds();
            }
            Font oldFont = this._graphics.getFont();
            this._graphics.setFont(oldFont.deriveFont(shape.getFontSize().floatValue()));
            this._graphics.setColor(shape.getFontColor());
            text.draw(this._graphics);
            this._graphics.setFont(oldFont);
        }
    }
}
