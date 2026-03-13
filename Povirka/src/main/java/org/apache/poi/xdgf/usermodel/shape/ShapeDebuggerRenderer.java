package org.apache.poi.xdgf.usermodel.shape;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class ShapeDebuggerRenderer extends ShapeRenderer {
    ShapeVisitorAcceptor _debugAcceptor;

    public ShapeDebuggerRenderer() {
        this._debugAcceptor = null;
    }

    public ShapeDebuggerRenderer(Graphics2D g) {
        super(g);
        this._debugAcceptor = null;
    }

    public void setDebugAcceptor(ShapeVisitorAcceptor acceptor) {
        this._debugAcceptor = acceptor;
    }

    @Override // org.apache.poi.xdgf.usermodel.shape.ShapeRenderer
    protected Path2D drawPath(XDGFShape shape) {
        Path2D path = super.drawPath(shape);
        ShapeVisitorAcceptor shapeVisitorAcceptor = this._debugAcceptor;
        if (shapeVisitorAcceptor == null || shapeVisitorAcceptor.accept(shape)) {
            Font f = this._graphics.getFont();
            this._graphics.scale(1.0d, -1.0d);
            this._graphics.setFont(f.deriveFont(0.05f));
            String shapeId = "" + shape.getID();
            float shapeOffset = -0.1f;
            if (shape.hasMasterShape()) {
                shapeId = shapeId + " MS:" + shape.getMasterShape().getID();
                shapeOffset = (-0.1f) - 0.15f;
            }
            this._graphics.drawString(shapeId, shapeOffset, 0.0f);
            this._graphics.setFont(f);
            this._graphics.scale(1.0d, -1.0d);
        }
        return path;
    }
}
