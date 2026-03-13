package org.apache.poi.ss.util;

import java.util.EnumSet;
import java.util.Set;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;

/* JADX INFO: loaded from: classes.dex */
public abstract class CellRangeAddressBase {
    private int _firstCol;
    private int _firstRow;
    private int _lastCol;
    private int _lastRow;

    public enum CellPosition {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        INSIDE
    }

    protected CellRangeAddressBase(int firstRow, int lastRow, int firstCol, int lastCol) {
        this._firstRow = firstRow;
        this._lastRow = lastRow;
        this._firstCol = firstCol;
        this._lastCol = lastCol;
    }

    public void validate(SpreadsheetVersion ssVersion) {
        validateRow(this._firstRow, ssVersion);
        validateRow(this._lastRow, ssVersion);
        validateColumn(this._firstCol, ssVersion);
        validateColumn(this._lastCol, ssVersion);
    }

    private static void validateRow(int row, SpreadsheetVersion ssVersion) {
        int maxrow = ssVersion.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }

    private static void validateColumn(int column, SpreadsheetVersion ssVersion) {
        int maxcol = ssVersion.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        }
        if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }

    public final boolean isFullColumnRange() {
        return (this._firstRow == 0 && this._lastRow == SpreadsheetVersion.EXCEL97.getLastRowIndex()) || (this._firstRow == -1 && this._lastRow == -1);
    }

    public final boolean isFullRowRange() {
        return (this._firstCol == 0 && this._lastCol == SpreadsheetVersion.EXCEL97.getLastColumnIndex()) || (this._firstCol == -1 && this._lastCol == -1);
    }

    public final int getFirstColumn() {
        return this._firstCol;
    }

    public final int getFirstRow() {
        return this._firstRow;
    }

    public final int getLastColumn() {
        return this._lastCol;
    }

    public final int getLastRow() {
        return this._lastRow;
    }

    public boolean isInRange(int rowInd, int colInd) {
        return this._firstRow <= rowInd && rowInd <= this._lastRow && this._firstCol <= colInd && colInd <= this._lastCol;
    }

    public boolean isInRange(CellReference ref) {
        return isInRange(ref.getRow(), ref.getCol());
    }

    public boolean isInRange(Cell cell) {
        return isInRange(cell.getRowIndex(), cell.getColumnIndex());
    }

    public boolean containsRow(int rowInd) {
        return this._firstRow <= rowInd && rowInd <= this._lastRow;
    }

    public boolean containsColumn(int colInd) {
        return this._firstCol <= colInd && colInd <= this._lastCol;
    }

    public boolean intersects(CellRangeAddressBase other) {
        return this._firstRow <= other._lastRow && this._firstCol <= other._lastCol && other._firstRow <= this._lastRow && other._firstCol <= this._lastCol;
    }

    public Set<CellPosition> getPosition(int rowInd, int colInd) {
        Set<CellPosition> positions = EnumSet.noneOf(CellPosition.class);
        if (rowInd > getFirstRow() && rowInd < getLastRow() && colInd > getFirstColumn() && colInd < getLastColumn()) {
            positions.add(CellPosition.INSIDE);
            return positions;
        }
        if (rowInd == getFirstRow()) {
            positions.add(CellPosition.TOP);
        }
        if (rowInd == getLastRow()) {
            positions.add(CellPosition.BOTTOM);
        }
        if (colInd == getFirstColumn()) {
            positions.add(CellPosition.LEFT);
        }
        if (colInd == getLastColumn()) {
            positions.add(CellPosition.RIGHT);
        }
        return positions;
    }

    public final void setFirstColumn(int firstCol) {
        this._firstCol = firstCol;
    }

    public final void setFirstRow(int firstRow) {
        this._firstRow = firstRow;
    }

    public final void setLastColumn(int lastCol) {
        this._lastCol = lastCol;
    }

    public final void setLastRow(int lastRow) {
        this._lastRow = lastRow;
    }

    public int getNumberOfCells() {
        return ((this._lastRow - this._firstRow) + 1) * ((this._lastCol - this._firstCol) + 1);
    }

    public final String toString() {
        CellReference crA = new CellReference(this._firstRow, this._firstCol);
        CellReference crB = new CellReference(this._lastRow, this._lastCol);
        return getClass().getName() + " [" + crA.formatAsString() + ":" + crB.formatAsString() + "]";
    }

    protected int getMinRow() {
        return Math.min(this._firstRow, this._lastRow);
    }

    protected int getMaxRow() {
        return Math.max(this._firstRow, this._lastRow);
    }

    protected int getMinColumn() {
        return Math.min(this._firstCol, this._lastCol);
    }

    protected int getMaxColumn() {
        return Math.max(this._firstCol, this._lastCol);
    }

    public boolean equals(Object other) {
        if (!(other instanceof CellRangeAddressBase)) {
            return false;
        }
        CellRangeAddressBase o = (CellRangeAddressBase) other;
        return getMinRow() == o.getMinRow() && getMaxRow() == o.getMaxRow() && getMinColumn() == o.getMinColumn() && getMaxColumn() == o.getMaxColumn();
    }

    public int hashCode() {
        int code = getMinColumn() + (getMaxColumn() << 8) + (getMinRow() << 16) + (getMaxRow() << 24);
        return code;
    }
}
