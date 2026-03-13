package org.apache.poi.xdgf.usermodel.section.geometry;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import java.awt.geom.Path2D;
import org.apache.poi.POIXMLException;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;

/* JADX INFO: loaded from: classes.dex */
public class PolyLineTo implements GeometryRow {
    PolyLineTo _master = null;
    String a;
    Boolean deleted;
    Double x;
    Double y;

    public PolyLineTo(RowType row) {
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
                this.a = cell.getV();
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
        PolyLineTo polyLineTo = this._master;
        if (polyLineTo != null) {
            return polyLineTo.getDel();
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

    public String getA() {
        String str = this.a;
        return str == null ? this._master.a : str;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    public void setupMaster(GeometryRow row) {
        this._master = (PolyLineTo) row;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow
    @NotImplemented
    public void addToPath(Path2D.Double path, XDGFShape parent) {
        if (getDel()) {
        } else {
            throw new POIXMLException("Polyline support not implemented");
        }
    }
}
