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
public class EllipticalArcTo implements GeometryRow {
    public static int draw = 0;
    EllipticalArcTo _master = null;
    Double a;
    Double b;
    Double c;
    Double d;
    Boolean deleted;
    Double x;
    Double y;

    public EllipticalArcTo(RowType row) {
        this.x = null;
        this.y = null;
        this.a = null;
        this.b = null;
        this.c = null;
        this.d = null;
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
            } else if (cellName.equals("C")) {
                this.c = XDGFCell.parseDoubleValue(cell);
            } else if (cellName.equals("D")) {
                this.d = XDGFCell.parseDoubleValue(cell);
            } else {
                throw new POIXMLException("Invalid cell '" + cellName + "' in EllipticalArcTo row");
            }
        }
    }

    public boolean getDel() {
        Boolean bool = this.deleted;
        if (bool != null) {
            return bool.booleanValue();
        }
        EllipticalArcTo ellipticalArcTo = this._master;
        if (ellipticalArcTo != null) {
            return ellipticalArcTo.getDel();
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

    public Double getC() {
        Double d = this.c;
        return d == null ? this._master.c : d;
    }

    public Double getD() {
        Double d = this.d;
        return d == null ? this._master.d : d;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void setupMaster(GeometryRow row) {
        this._master = (EllipticalArcTo) row;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (getDel()) {
            return;
        }
        double x = getX().doubleValue();
        double y = getY().doubleValue();
        double a = getA().doubleValue();
        double b = getB().doubleValue();
        double c = getC().doubleValue();
        double d = getD().doubleValue();
        createEllipticalArc(x, y, a, b, c, d, path);
    }

    public static void createEllipticalArc(double x, double y, double a, double b, double c, double d, Path2D.Double path) {
        Point2D last = path.getCurrentPoint();
        double x0 = last.getX();
        double y0 = last.getY();
        AffineTransform at = AffineTransform.getRotateInstance(-c);
        double[] pts = {x0, y0, x, y, a, b};
        at.transform(pts, 0, pts, 0, 3);
        double x02 = pts[0];
        double y02 = pts[1];
        double x2 = pts[2];
        double y2 = pts[3];
        double a2 = pts[4];
        double b2 = pts[5];
        double d2 = d * d;
        double cx = (((((x02 - x2) * (x02 + x2)) * (y2 - b2)) - (((x2 - a2) * (x2 + a2)) * (y02 - y2))) + ((((y02 - y2) * d2) * (y2 - b2)) * (y02 - b2))) / ((((x02 - x2) * (y2 - b2)) - ((x2 - a2) * (y02 - y2))) * 2.0d);
        double cy = ((((((x02 - x2) * (x2 - a2)) * (x02 - a2)) / d2) + (((x2 - a2) * (y02 - y2)) * (y02 + y2))) - (((x02 - x2) * (y2 - b2)) * (y2 + b2))) / ((((x2 - a2) * (y02 - y2)) - ((x02 - x2) * (y2 - b2))) * 2.0d);
        double x3 = y02 - cy;
        double rx = Math.sqrt(Math.pow(x02 - cx, 2.0d) + (Math.pow(x3, 2.0d) * d2));
        double ry = rx / d;
        double ctrlAngle = Math.toDegrees(Math.atan2((b2 - cy) / ry, (a2 - cx) / rx));
        double startAngle = Math.toDegrees(Math.atan2((y02 - cy) / ry, (x02 - cx) / rx));
        double endAngle = Math.toDegrees(Math.atan2((y2 - cy) / ry, (x2 - cx) / rx));
        double sweep = computeSweep(startAngle, endAngle, ctrlAngle);
        double ctrlAngle2 = -startAngle;
        Arc2D.Double r6 = new Arc2D.Double(cx - rx, cy - ry, rx * 2.0d, ry * 2.0d, ctrlAngle2, sweep, 0);
        at.setToRotation(c);
        path.append(at.createTransformedShape(r6), false);
    }

    protected static double computeSweep(double startAngle, double endAngle, double ctrlAngle) {
        double startAngle2 = (startAngle + 360.0d) % 360.0d;
        double startAngle3 = endAngle + 360.0d;
        double endAngle2 = startAngle3 % 360.0d;
        double endAngle3 = ctrlAngle + 360.0d;
        double ctrlAngle2 = endAngle3 % 360.0d;
        if (startAngle2 < endAngle2) {
            if (startAngle2 < ctrlAngle2 && ctrlAngle2 < endAngle2) {
                double sweep = startAngle2 - endAngle2;
                return sweep;
            }
            double sweep2 = startAngle2 - endAngle2;
            return sweep2 + 360.0d;
        }
        if (endAngle2 < ctrlAngle2 && ctrlAngle2 < startAngle2) {
            double sweep3 = startAngle2 - endAngle2;
            return sweep3;
        }
        double sweep4 = startAngle2 - endAngle2;
        return -(360.0d - sweep4);
    }
}
