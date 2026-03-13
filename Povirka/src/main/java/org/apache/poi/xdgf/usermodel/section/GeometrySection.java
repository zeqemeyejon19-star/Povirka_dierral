package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.RowType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.geom.SplineCollector;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import org.apache.poi.xdgf.usermodel.section.geometry.Ellipse;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRow;
import org.apache.poi.xdgf.usermodel.section.geometry.GeometryRowFactory;
import org.apache.poi.xdgf.usermodel.section.geometry.InfiniteLine;
import org.apache.poi.xdgf.usermodel.section.geometry.SplineKnot;
import org.apache.poi.xdgf.usermodel.section.geometry.SplineStart;

/* JADX INFO: loaded from: classes.dex */
public class GeometrySection extends XDGFSection {
    GeometrySection _master;
    SortedMap<Long, GeometryRow> _rows;

    public GeometrySection(SectionType section, XDGFSheet containingSheet) {
        super(section, containingSheet);
        this._master = null;
        this._rows = new TreeMap();
        RowType[] arr$ = section.getRowArray();
        for (RowType row : arr$) {
            if (this._rows.containsKey(Long.valueOf(row.getIX()))) {
                throw new POIXMLException("Index element '" + row.getIX() + "' already exists");
            }
            this._rows.put(Long.valueOf(row.getIX()), GeometryRowFactory.load(row));
        }
    }

    @Override // org.apache.poi.xdgf.usermodel.section.XDGFSection
    public void setupMaster(XDGFSection master) {
        this._master = (GeometrySection) master;
        for (Map.Entry<Long, GeometryRow> entry : this._rows.entrySet()) {
            GeometryRow masterRow = this._master._rows.get(entry.getKey());
            if (masterRow != null) {
                try {
                    entry.getValue().setupMaster(masterRow);
                } catch (ClassCastException e) {
                }
            }
        }
    }

    public Boolean getNoShow() {
        Boolean noShow = XDGFCell.maybeGetBoolean(this._cells, "NoShow");
        if (noShow == null) {
            GeometrySection geometrySection = this._master;
            if (geometrySection != null) {
                return geometrySection.getNoShow();
            }
            return false;
        }
        return noShow;
    }

    public Iterable<GeometryRow> getCombinedRows() {
        SortedMap<Long, GeometryRow> sortedMap = this._rows;
        GeometrySection geometrySection = this._master;
        return new CombinedIterable(sortedMap, geometrySection == null ? null : geometrySection._rows);
    }

    public Path2D.Double getPath(XDGFShape parent) {
        GeometryRow row;
        Iterator<GeometryRow> rows = getCombinedRows().iterator();
        GeometryRow first = rows.next();
        if (first instanceof Ellipse) {
            return ((Ellipse) first).getPath();
        }
        if (first instanceof InfiniteLine) {
            return ((InfiniteLine) first).getPath();
        }
        if (first instanceof SplineStart) {
            throw new POIXMLException("SplineStart must be preceded by another type");
        }
        Path2D.Double path = new Path2D.Double();
        SplineCollector renderer = null;
        while (true) {
            if (first != null) {
                row = first;
                first = null;
            } else if (rows.hasNext()) {
                row = rows.next();
            } else {
                if (renderer != null) {
                    renderer.addToPath(path, parent);
                }
                return path;
            }
            if (row instanceof SplineStart) {
                if (renderer != null) {
                    throw new POIXMLException("SplineStart found multiple times!");
                }
                renderer = new SplineCollector((SplineStart) row);
            } else if (row instanceof SplineKnot) {
                if (renderer == null) {
                    throw new POIXMLException("SplineKnot found without SplineStart!");
                }
                renderer.addKnot((SplineKnot) row);
            } else {
                if (renderer != null) {
                    renderer.addToPath(path, parent);
                    renderer = null;
                }
                row.addToPath(path, parent);
            }
        }
    }
}
