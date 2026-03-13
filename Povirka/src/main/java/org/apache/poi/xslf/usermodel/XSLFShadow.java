package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Shadow;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;

/* JADX INFO: loaded from: classes.dex */
public class XSLFShadow extends XSLFShape implements Shadow<XSLFShape, XSLFTextParagraph> {
    private XSLFSimpleShape _parent;

    XSLFShadow(CTOuterShadowEffect shape, XSLFSimpleShape parentShape) {
        super(shape, parentShape.getSheet());
        this._parent = parentShape;
    }

    @Override // org.apache.poi.sl.usermodel.Shadow
    public XSLFSimpleShape getShadowParent() {
        return this._parent;
    }

    @Override // org.apache.poi.sl.usermodel.Shape, org.apache.poi.sl.usermodel.PlaceableShape
    public Rectangle2D getAnchor() {
        return this._parent.getAnchor();
    }

    public void setAnchor(Rectangle2D anchor) {
        throw new IllegalStateException("You can't set anchor of a shadow");
    }

    @Override // org.apache.poi.sl.usermodel.Shadow
    public double getDistance() {
        CTOuterShadowEffect ct = getXmlObject();
        if (ct.isSetDist()) {
            return Units.toPoints(ct.getDist());
        }
        return 0.0d;
    }

    @Override // org.apache.poi.sl.usermodel.Shadow
    public double getAngle() {
        CTOuterShadowEffect ct = getXmlObject();
        if (ct.isSetDir()) {
            return ((double) ct.getDir()) / 60000.0d;
        }
        return 0.0d;
    }

    @Override // org.apache.poi.sl.usermodel.Shadow
    public double getBlur() {
        CTOuterShadowEffect ct = getXmlObject();
        if (ct.isSetBlurRad()) {
            return Units.toPoints(ct.getBlurRad());
        }
        return 0.0d;
    }

    public Color getFillColor() {
        PaintStyle.SolidPaint ps = getFillStyle();
        if (ps == null) {
            return null;
        }
        Color col = DrawPaint.applyColorTransform(ps.getSolidColor());
        return col;
    }

    @Override // org.apache.poi.sl.usermodel.Shadow
    public PaintStyle.SolidPaint getFillStyle() {
        XSLFTheme theme = getSheet().getTheme();
        CTOuterShadowEffect ct = getXmlObject();
        if (ct == null) {
            return null;
        }
        CTSchemeColor phClr = ct.getSchemeClr();
        XSLFColor xc = new XSLFColor(ct, theme, phClr);
        return DrawPaint.createSolidPaint(xc.getColorStyle());
    }
}
