package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class InfiniteLine implements GeometryRow {
    InfiniteLine _master = null;
    Double a;
    Double b;
    Boolean deleted;
    Double x;
    Double y;

    public InfiniteLine(RowType row) {
        this.x = null;
        this.y = null;
        this.a = null;
        this.b = null;
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
            } else if (cellName.equals("B")) {
                this.b = XDGFCell.parseDoubleValue(cell);
            } else {
                throw new POIXMLException("Invalid cell '" + cellName + "' in InfiniteLine row");
            }
        }
    }

    public boolean getDel() {
        Boolean bool = this.deleted;
        if (bool != null) {
            return bool.booleanValue();
        }
        InfiniteLine infiniteLine = this._master;
        if (infiniteLine != null) {
            return infiniteLine.getDel();
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

    public Double getB() {
        Double d = this.b;
        return d == null ? this._master.b : d;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void setupMaster(GeometryRow row) {
        this._master = (InfiniteLine) row;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (getDel()) {
        } else {
            throw new POIXMLException("InfiniteLine elements cannot be part of a path");
        }
    }

    public Path2D.Double getPath() {
        Path2D.Double path = new Path2D.Double();
        double x0 = getX().doubleValue();
        double y0 = getY().doubleValue();
        double x1 = getA().doubleValue();
        double y1 = getB().doubleValue();
        if (x0 == x1) {
            path.moveTo(x0, -100000.0d);
            path.lineTo(x0, 100000.0d);
        } else if (y0 == y1) {
            path.moveTo(-100000.0d, y0);
            path.lineTo(100000.0d, y0);
        } else {
            double m = (y1 - y0) / (x1 - x0);
            double c = y0 - (m * x0);
            path.moveTo(100000.0d, (m * 100000.0d) + c);
            path.lineTo(100000.0d, (100000.0d - c) / m);
        }
        return path;
    }
}
