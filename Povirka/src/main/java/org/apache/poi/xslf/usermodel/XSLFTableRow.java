package org.apache.poi.xslf.usermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;

/* JADX INFO: loaded from: classes.dex */
public class XSLFTableRow implements Iterable<XSLFTableCell> {
    private final List<XSLFTableCell> _cells;
    private final CTTableRow _row;
    private final XSLFTable _table;

    XSLFTableRow(CTTableRow row, XSLFTable table) {
        this._row = row;
        this._table = table;
        CTTableCell[] tcArray = row.getTcArray();
        this._cells = new ArrayList(tcArray.length);
        for (CTTableCell cell : tcArray) {
            this._cells.add(new XSLFTableCell(cell, table));
        }
    }

    public CTTableRow getXmlObject() {
        return this._row;
    }

    @Override // java.lang.Iterable
    public Iterator<XSLFTableCell> iterator() {
        return this._cells.iterator();
    }

    public List<XSLFTableCell> getCells() {
        return Collections.unmodifiableList(this._cells);
    }

    public double getHeight() {
        return Units.toPoints(this._row.getH());
    }

    public void setHeight(double height) {
        this._row.setH(Units.toEMU(height));
    }

    public XSLFTableCell addCell() {
        CTTableCell c = this._row.addNewTc();
        c.set(XSLFTableCell.prototype());
        XSLFTableCell cell = new XSLFTableCell(c, this._table);
        this._cells.add(cell);
        if (this._table.getNumberOfColumns() < this._row.sizeOfTcArray()) {
            this._table.getCTTable().getTblGrid().addNewGridCol().setW(Units.toEMU(100.0d));
        }
        this._table.updateRowColIndexes();
        return cell;
    }

    public void mergeCells(int firstCol, int lastCol) {
        if (firstCol >= lastCol) {
            throw new IllegalArgumentException("Cannot merge, first column >= last column : " + firstCol + " >= " + lastCol);
        }
        int colSpan = (lastCol - firstCol) + 1;
        this._cells.get(firstCol).setGridSpan(colSpan);
        for (XSLFTableCell cell : this._cells.subList(firstCol + 1, lastCol + 1)) {
            cell.setHMerge(true);
        }
    }
}
