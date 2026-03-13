package org.apache.poi.xssf.streaming;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class SXSSFRow implements Row, Comparable<SXSSFRow> {
    private static final Boolean UNDEFINED = null;
    private Boolean _collapsed;
    private Boolean _hidden;
    private final SXSSFSheet _sheet;
    private final SortedMap<Integer, SXSSFCell> _cells = new TreeMap();
    private short _style = -1;
    private short _height = -1;
    private boolean _zHeight = false;
    private int _outlineLevel = 0;

    public SXSSFRow(SXSSFSheet sheet) {
        Boolean bool = UNDEFINED;
        this._hidden = bool;
        this._collapsed = bool;
        this._sheet = sheet;
    }

    public Iterator<Cell> allCellsIterator() {
        return new CellIterator();
    }

    public boolean hasCustomHeight() {
        return this._height != -1;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getOutlineLevel() {
        return this._outlineLevel;
    }

    void setOutlineLevel(int level) {
        this._outlineLevel = level;
    }

    public Boolean getHidden() {
        return this._hidden;
    }

    public void setHidden(Boolean hidden) {
        this._hidden = hidden;
    }

    public Boolean getCollapsed() {
        return this._collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this._collapsed = collapsed;
    }

    @Override // java.lang.Iterable
    public Iterator<Cell> iterator() {
        return new FilledCellIterator();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFCell createCell(int column) {
        return createCell(column, CellType.BLANK);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFCell createCell(int column, int type) {
        return createCell(column, CellType.forInt(type));
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFCell createCell(int column, CellType type) {
        checkBounds(column);
        SXSSFCell cell = new SXSSFCell(this, type);
        this._cells.put(Integer.valueOf(column), cell);
        return cell;
    }

    private static void checkBounds(int cellIndex) {
        SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void removeCell(Cell cell) {
        int index = getCellIndex((SXSSFCell) cell);
        this._cells.remove(Integer.valueOf(index));
    }

    int getCellIndex(SXSSFCell cell) {
        for (Map.Entry<Integer, SXSSFCell> entry : this._cells.entrySet()) {
            if (entry.getValue() == cell) {
                return entry.getKey().intValue();
            }
        }
        return -1;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowNum(int rowNum) {
        this._sheet.changeRowNum(this, rowNum);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getRowNum() {
        return this._sheet.getRowNum(this);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFCell getCell(int cellnum) {
        Row.MissingCellPolicy policy = this._sheet.getWorkbook().getMissingCellPolicy();
        return getCell(cellnum, policy);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFCell getCell(int cellnum, Row.MissingCellPolicy policy) {
        checkBounds(cellnum);
        SXSSFCell cell = this._cells.get(Integer.valueOf(cellnum));
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$Row$MissingCellPolicy[policy.ordinal()];
        if (i == 1) {
            return cell;
        }
        if (i != 2) {
            if (i == 3) {
                return cell == null ? createCell(cellnum, CellType.BLANK) : cell;
            }
            throw new IllegalArgumentException("Illegal policy " + policy);
        }
        boolean isBlank = cell != null && cell.getCellTypeEnum() == CellType.BLANK;
        if (isBlank) {
            return null;
        }
        return cell;
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.streaming.SXSSFRow$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$Row$MissingCellPolicy;

        static {
            int[] iArr = new int[Row.MissingCellPolicy.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$Row$MissingCellPolicy = iArr;
            try {
                iArr[Row.MissingCellPolicy.RETURN_NULL_AND_BLANK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$Row$MissingCellPolicy[Row.MissingCellPolicy.RETURN_BLANK_AS_NULL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$Row$MissingCellPolicy[Row.MissingCellPolicy.CREATE_NULL_AS_BLANK.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getFirstCellNum() {
        try {
            return this._cells.firstKey().shortValue();
        } catch (NoSuchElementException e) {
            return (short) -1;
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getLastCellNum() {
        if (this._cells.isEmpty()) {
            return (short) -1;
        }
        return (short) (this._cells.lastKey().intValue() + 1);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getPhysicalNumberOfCells() {
        return this._cells.size();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeight(short height) {
        this._height = height;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setZeroHeight(boolean zHeight) {
        this._zHeight = zHeight;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean getZeroHeight() {
        return this._zHeight;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeightInPoints(float height) {
        if (height == -1.0f) {
            this._height = (short) -1;
        } else {
            this._height = (short) (20.0f * height);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getHeight() {
        return (short) (this._height == -1 ? getSheet().getDefaultRowHeightInPoints() * 20.0f : r0);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public float getHeightInPoints() {
        short s = this._height;
        return (float) (s == -1 ? getSheet().getDefaultRowHeightInPoints() : ((double) s) / 20.0d);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean isFormatted() {
        return this._style > -1;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public CellStyle getRowStyle() {
        if (isFormatted()) {
            return getSheet().getWorkbook().getCellStyleAt(this._style);
        }
        return null;
    }

    @Internal
    int getRowStyleIndex() {
        return this._style;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowStyle(CellStyle style) {
        if (style == null) {
            this._style = (short) -1;
        } else {
            this._style = style.getIndex();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public Iterator<Cell> cellIterator() {
        return iterator();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public SXSSFSheet getSheet() {
        return this._sheet;
    }

    public class FilledCellIterator implements Iterator<Cell> {
        private final Iterator<SXSSFCell> iter;

        public FilledCellIterator() {
            this.iter = SXSSFRow.this._cells.values().iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override // java.util.Iterator
        public Cell next() throws NoSuchElementException {
            return this.iter.next();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public class CellIterator implements Iterator<Cell> {
        final int maxColumn;
        int pos = 0;

        public CellIterator() {
            this.maxColumn = SXSSFRow.this.getLastCellNum();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.pos < this.maxColumn;
        }

        @Override // java.util.Iterator
        public Cell next() throws NoSuchElementException {
            if (hasNext()) {
                SortedMap sortedMap = SXSSFRow.this._cells;
                int i = this.pos;
                this.pos = i + 1;
                return (Cell) sortedMap.get(Integer.valueOf(i));
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(SXSSFRow other) {
        if (getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        Integer thisRow = Integer.valueOf(getRowNum());
        Integer otherRow = Integer.valueOf(other.getRowNum());
        return thisRow.compareTo(otherRow);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SXSSFRow)) {
            return false;
        }
        SXSSFRow other = (SXSSFRow) obj;
        return getRowNum() == other.getRowNum() && getSheet() == other.getSheet();
    }

    public int hashCode() {
        return this._cells.hashCode();
    }
}
