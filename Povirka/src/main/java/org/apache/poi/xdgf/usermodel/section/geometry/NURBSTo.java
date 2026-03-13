package org.apache.poi.xdgf.usermodel.section.geometry;

import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.ShapeMultiPath;
import com.graphbuilder.curve.ValueVector;
import com.graphbuilder.geom.PointFactory;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.geom.SplineRenderer;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class NURBSTo implements GeometryRow {
    NURBSTo _master = null;
    Double a;
    Double b;
    Double c;
    Double d;
    Boolean deleted;
    String e;
    Double x;
    Double y;

    public NURBSTo(RowType row) {
        this.x = null;
        this.y = null;
        this.a = null;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = null;
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
            } else if (cellName.equals("E")) {
                this.e = cell.getV();
            } else {
                throw new POIXMLException("Invalid cell '" + cellName + "' in NURBS row");
            }
        }
    }

    public boolean getDel() {
        Boolean bool = this.deleted;
        if (bool != null) {
            return bool.booleanValue();
        }
        NURBSTo nURBSTo = this._master;
        if (nURBSTo != null) {
            return nURBSTo.getDel();
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

    public String getE() {
        String str = this.e;
        return str == null ? this._master.e : str;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void setupMaster(GeometryRow row) {
        this._master = (NURBSTo) row;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (getDel()) {
            return;
        }
        Point2D last = path.getCurrentPoint();
        String formula = getE().trim();
        if (!formula.startsWith("NURBS(") || !formula.endsWith(")")) {
            throw new POIXMLException("Invalid NURBS formula: " + formula);
        }
        String[] components = formula.substring(6, formula.length() - 1).split(",");
        if (components.length < 8) {
            throw new POIXMLException("Invalid NURBS formula (not enough arguments)");
        }
        if ((components.length - 4) % 4 != 0) {
            throw new POIXMLException("Invalid NURBS formula -- need 4 + n*4 arguments, got " + components.length);
        }
        double lastControlX = getX().doubleValue();
        double lastControlY = getY().doubleValue();
        double secondToLastKnot = getA().doubleValue();
        double lastWeight = getB().doubleValue();
        double firstKnot = getC().doubleValue();
        double firstWeight = getD().doubleValue();
        double lastKnot = Double.parseDouble(components[0].trim());
        int degree = Integer.parseInt(components[1].trim());
        int xType = Integer.parseInt(components[2].trim());
        int yType = Integer.parseInt(components[3].trim());
        double xScale = 1.0d;
        double yScale = 1.0d;
        if (xType == 0) {
            xScale = parent.getWidth().doubleValue();
        }
        if (yType == 0) {
            yScale = parent.getHeight().doubleValue();
        }
        ControlPath controlPath = new ControlPath();
        ValueVector knots = new ValueVector();
        ValueVector weights = new ValueVector();
        knots.add(firstKnot);
        weights.add(firstWeight);
        double firstWeight2 = last.getX();
        int degree2 = degree;
        controlPath.addPoint(PointFactory.create(firstWeight2, last.getY()));
        int sets = (components.length - 4) / 4;
        int i = 0;
        while (i < sets) {
            double x1 = Double.parseDouble(components[(i * 4) + 4 + 0].trim());
            double y1 = Double.parseDouble(components[(i * 4) + 4 + 1].trim());
            Point2D last2 = last;
            double k = Double.parseDouble(components[(i * 4) + 4 + 2].trim());
            double lastWeight2 = lastWeight;
            double w = Double.parseDouble(components[(i * 4) + 4 + 3].trim());
            controlPath.addPoint(PointFactory.create(x1 * xScale, y1 * yScale));
            knots.add(k);
            weights.add(w);
            i++;
            degree2 = degree2;
            components = components;
            last = last2;
            formula = formula;
            lastWeight = lastWeight2;
            lastKnot = lastKnot;
            secondToLastKnot = secondToLastKnot;
        }
        knots.add(secondToLastKnot);
        knots.add(lastKnot);
        weights.add(lastWeight);
        controlPath.addPoint(PointFactory.create(lastControlX, lastControlY));
        ShapeMultiPath shape = SplineRenderer.createNurbsSpline(controlPath, knots, weights, degree2);
        path.append(shape, true);
    }
}
