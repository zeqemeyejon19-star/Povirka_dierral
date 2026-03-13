package org.apache.poi.xdgf.usermodel.section.geometry;

import java.awt.geom.Path2D;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public interface GeometryRow {
    void addToPath(Path2D.Double r1, XDGFShape xDGFShape);

    void setupMaster(GeometryRow geometryRow);
}
