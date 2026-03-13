package org.apache.poi.xdgf.usermodel.shape;

import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class ShapeDataAcceptor implements ShapeVisitorAcceptor {
    @Override // org.apache.poi.xdgf.usermodel.shape.ShapeVisitorAcceptor
    public boolean accept(XDGFShape shape) {
        if (shape.isDeleted()) {
            return false;
        }
        if ((shape.hasText() && shape.getTextAsString().length() != 0) || shape.isShape1D()) {
            return true;
        }
        if (!shape.hasMaster() && !shape.hasMasterShape()) {
            return true;
        }
        if (!shape.hasMaster() || shape.hasMasterShape()) {
            return shape.hasMasterShape() && shape.getMasterShape().isTopmost();
        }
        return true;
    }
}
