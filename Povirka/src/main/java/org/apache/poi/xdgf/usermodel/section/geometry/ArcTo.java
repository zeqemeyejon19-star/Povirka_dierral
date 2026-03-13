package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class ArcTo implements GeometryRow {
    ArcTo _master = null;
    Double a;
    Boolean deleted;
    Double x;
    Double y;

    public ArcTo(RowType row) {
        this.x = null;
        this.y = null;
        this.a = null;
        this.deleted = null;
        if (row.isSetDel()) {
            this.deleted = Boolean.valueOf(row.getDel());
        }
        CellType[] arr$ = row.getCellArray();
        for (CellType cell : arr$) {
            String cellName = cell.getN();
            if (cellName.equals("X")) {
                this.x = XDGFCell.parseDoubleValue(cell);
            } else if (cellName.equals("Y")) {
                this.y = XDGFCell.parseDoubleValue(cell);
            } else if (cellName.equals("A")) {
                this.a = XDGFCell.parseDoubleValue(cell);
            } else {
                throw new POIXMLException("Invalid cell '" + cellName + "' in ArcTo row");
            }
        }
    }

    public boolean getDel() {
        Boolean bool = this.deleted;
        if (bool != null) {
            return bool.booleanValue();
        }
        ArcTo arcTo = this._master;
        if (arcTo != null) {
            return arcTo.getDel();
        }
        return false;
    }

    public Double getX() {
        Double d = this.x;
        return d == null ? this._master.x : d;
    }

    public Double getY() {
        Double d = this.y;
        return d == null ? this._master.y : d;
    }

    public Double getA() {
        Double d = this.a;
        return d == null ? this._master.a : d;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void setupMaster(GeometryRow row) {
        this._master = (ArcTo) row;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (getDel()) {
            return;
        }
        Point2D last = path.getCurrentPoint();
        double x = getX().doubleValue();
        double y = getY().doubleValue();
        double a = getA().doubleValue();
        if (a == 0.0d) {
            path.lineTo(x, y);
            return;
        }
        double x0 = last.getX();
        double y0 = last.getY();
        double chordLength = Math.hypot(y - y0, x - x0);
        double radius = (((4.0d * a) * a) + (chordLength * chordLength)) / (Math.abs(a) * 8.0d);
        double cx = x0 + ((x - x0) / 2.0d);
        double cy = y0 + ((y - y0) / 2.0d);
        double rotate = Math.atan2(y - cy, x - cx);
        path.append(AffineTransform.getRotateInstance(rotate, x0, y0).createTransformedShape(new Arc2D.Double(x0, y0 - radius, chordLength, radius * 2.0d, 180.0d, x0 < x ? 180.0d : -180.0d, 0)), true);
    }
}
