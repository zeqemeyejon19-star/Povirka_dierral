package org.apache.poi.xdgf.usermodel.shape;

import java.awt.geom.AffineTransform;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public abstract class ShapeVisitor {
    protected ShapeVisitorAcceptor _acceptor = getAcceptor();

    public abstract void visit(XDGFShape xDGFShape, AffineTransform affineTransform, int i);

    protected ShapeVisitorAcceptor getAcceptor() {
        return new ShapeVisitorAcceptor() { // from class: org.apache.poi.xdgf.usermodel.shape.ShapeVisitor.1
            @Override // org.apache.poi.xdgf.usermodel.shape.ShapeVisitorAcceptor
            public boolean accept(XDGFShape shape) {
                return !shape.isDeleted();
            }
        };
    }

    public void setAcceptor(ShapeVisitorAcceptor acceptor) {
        this._acceptor = acceptor;
    }

    public boolean accept(XDGFShape shape) {
        return this._acceptor.accept(shape);
    }
}
