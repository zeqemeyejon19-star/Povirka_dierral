package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Configurator;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFRow implements Row, Comparable<HSSFRow> {
    public static final int INITIAL_CAPACITY = Configurator.getIntValue("HSSFRow.ColInitialCapacity", 5);
    private final HSSFWorkbook book;
    private HSSFCell[] cells;
    private final RowRecord row;
    private int rowNum;
    private final HSSFSheet sheet;

    HSSFRow(HSSFWorkbook book, HSSFSheet sheet, int rowNum) {
        this(book, sheet, new RowRecord(rowNum));
    }

    HSSFRow(HSSFWorkbook book, HSSFSheet sheet, RowRecord record) {
        this.book = book;
        this.sheet = sheet;
        this.row = record;
        setRowNum(record.getRowNumber());
        this.cells = new HSSFCell[record.getLastCol() + INITIAL_CAPACITY];
        record.setEmpty();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFCell createCell(int column) {
        return createCell(column, CellType.BLANK);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFCell createCell(int columnIndex, int type) {
        return createCell(columnIndex, CellType.forInt(type));
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFCell createCell(int columnIndex, CellType type) {
        short shortCellNum = (short) columnIndex;
        if (columnIndex > 32767) {
            shortCellNum = (short) (65535 - columnIndex);
        }
        HSSFCell cell = new HSSFCell(this.book, this.sheet, getRowNum(), shortCellNum, type);
        addCell(cell);
        this.sheet.getSheet().addValueRecord(getRowNum(), cell.getCellValueRecord());
        return cell;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void removeCell(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell must not be null");
        }
        removeCell((HSSFCell) cell, true);
    }

    private void removeCell(HSSFCell cell, boolean alsoRemoveRecords) {
        int column = cell.getColumnIndex();
        if (column < 0) {
            throw new RuntimeException("Negative cell indexes not allowed");
        }
        HSSFCell[] hSSFCellArr = this.cells;
        if (column >= hSSFCellArr.length || cell != hSSFCellArr[column]) {
            throw new RuntimeException("Specified cell is not from this row");
        }
        if (cell.isPartOfArrayFormulaGroup()) {
            cell.notifyArrayFormulaChanging();
        }
        this.cells[column] = null;
        if (alsoRemoveRecords) {
            CellValueRecordInterface cval = cell.getCellValueRecord();
            this.sheet.getSheet().removeValueRecord(getRowNum(), cval);
        }
        if (cell.getColumnIndex() + 1 == this.row.getLastCol()) {
            RowRecord rowRecord = this.row;
            rowRecord.setLastCol(calculateNewLastCellPlusOne(rowRecord.getLastCol()));
        }
        if (cell.getColumnIndex() == this.row.getFirstCol()) {
            RowRecord rowRecord2 = this.row;
            rowRecord2.setFirstCol(calculateNewFirstCell(rowRecord2.getFirstCol()));
        }
    }

    protected void removeAllCells() {
        HSSFCell[] arr$ = this.cells;
        for (HSSFCell cell : arr$) {
            if (cell != null) {
                removeCell(cell, true);
            }
        }
        this.cells = new HSSFCell[INITIAL_CAPACITY];
    }

    HSSFCell createCellFromRecord(CellValueRecordInterface cell) {
        HSSFCell hcell = new HSSFCell(this.book, this.sheet, cell);
        addCell(hcell);
        int colIx = cell.getColumn();
        if (this.row.isEmpty()) {
            this.row.setFirstCol(colIx);
            this.row.setLastCol(colIx + 1);
        } else if (colIx < this.row.getFirstCol()) {
            this.row.setFirstCol(colIx);
        } else if (colIx > this.row.getLastCol()) {
            this.row.setLastCol(colIx + 1);
        }
        return hcell;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowNum(int rowIndex) {
        int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex + ") outside allowable range (0.." + maxrow + ")");
        }
        this.rowNum = rowIndex;
        RowRecord rowRecord = this.row;
        if (rowRecord != null) {
            rowRecord.setRowNumber(rowIndex);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getRowNum() {
        return this.rowNum;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFSheet getSheet() {
        return this.sheet;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getOutlineLevel() {
        return this.row.getOutlineLevel();
    }

    public void moveCell(HSSFCell cell, short newColumn) {
        HSSFCell[] hSSFCellArr = this.cells;
        if (hSSFCellArr.length > newColumn && hSSFCellArr[newColumn] != null) {
            throw new IllegalArgumentException("Asked to move cell to column " + ((int) newColumn) + " but there's already a cell there");
        }
        if (!hSSFCellArr[cell.getColumnIndex()].equals(cell)) {
            throw new IllegalArgumentException("Asked to move a cell, but it didn't belong to our row");
        }
        removeCell(cell, false);
        cell.updateCellNum(newColumn);
        addCell(cell);
    }

    private void addCell(HSSFCell cell) {
        int column = cell.getColumnIndex();
        if (column >= this.cells.length) {
            HSSFCell[] oldCells = this.cells;
            int newSize = ((oldCells.length * 3) / 2) + 1;
            if (newSize < column + 1) {
                newSize = column + INITIAL_CAPACITY;
            }
            HSSFCell[] hSSFCellArr = new HSSFCell[newSize];
            this.cells = hSSFCellArr;
            System.arraycopy(oldCells, 0, hSSFCellArr, 0, oldCells.length);
        }
        this.cells[column] = cell;
        if (this.row.isEmpty() || column < this.row.getFirstCol()) {
            this.row.setFirstCol((short) column);
        }
        if (this.row.isEmpty() || column >= this.row.getLastCol()) {
            this.row.setLastCol((short) (column + 1));
        }
    }

    private HSSFCell retrieveCell(int cellIndex) {
        if (cellIndex < 0) {
            return null;
        }
        HSSFCell[] hSSFCellArr = this.cells;
        if (cellIndex >= hSSFCellArr.length) {
            return null;
        }
        return hSSFCellArr[cellIndex];
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFCell getCell(int cellnum) {
        return getCell(cellnum, this.book.getMissingCellPolicy());
    }

    /* JADX INFO: renamed from: org.apache.poi.hssf.usermodel.HSSFRow$1, reason: invalid class name */
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
    public HSSFCell getCell(int cellnum, Row.MissingCellPolicy policy) {
        HSSFCell cell = retrieveCell(cellnum);
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

    @Override // org.apache.poi.ss.usermodel.Row
    public short getFirstCellNum() {
        if (this.row.isEmpty()) {
            return (short) -1;
        }
        return (short) this.row.getFirstCol();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getLastCellNum() {
        if (this.row.isEmpty()) {
            return (short) -1;
        }
        return (short) this.row.getLastCol();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getPhysicalNumberOfCells() {
        int count = 0;
        HSSFCell[] arr$ = this.cells;
        for (HSSFCell cell : arr$) {
            if (cell != null) {
                count++;
            }
        }
        return count;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeight(short height) {
        if (height == -1) {
            this.row.setHeight((short) -32513);
            this.row.setBadFontHeight(false);
        } else {
            this.row.setBadFontHeight(true);
            this.row.setHeight(height);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setZeroHeight(boolean zHeight) {
        this.row.setZeroHeight(zHeight);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean getZeroHeight() {
        return this.row.getZeroHeight();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeightInPoints(float height) {
        if (height == -1.0f) {
            this.row.setHeight((short) -32513);
            this.row.setBadFontHeight(false);
        } else {
            this.row.setBadFontHeight(true);
            this.row.setHeight((short) (20.0f * height));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getHeight() {
        short height = this.row.getHeight();
        return (Short.MIN_VALUE & height) != 0 ? this.sheet.getSheet().getDefaultRowHeight() : (short) (height & Font.COLOR_NORMAL);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public float getHeightInPoints() {
        return getHeight() / 20.0f;
    }

    protected RowRecord getRowRecord() {
        return this.row;
    }

    private int calculateNewLastCellPlusOne(int lastcell) {
        int cellIx = lastcell - 1;
        HSSFCell r = retrieveCell(cellIx);
        while (r == null) {
            if (cellIx < 0) {
                return 0;
            }
            cellIx--;
            r = retrieveCell(cellIx);
        }
        return cellIx + 1;
    }

    private int calculateNewFirstCell(int firstcell) {
        int cellIx = firstcell + 1;
        HSSFCell r = retrieveCell(cellIx);
        while (r == null) {
            if (cellIx <= this.cells.length) {
                return 0;
            }
            cellIx++;
            r = retrieveCell(cellIx);
        }
        return cellIx;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean isFormatted() {
        return this.row.getFormatted();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public HSSFCellStyle getRowStyle() {
        if (!isFormatted()) {
            return null;
        }
        short styleIndex = this.row.getXFIndex();
        ExtendedFormatRecord xf = this.book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this.book);
    }

    public void setRowStyle(HSSFCellStyle style) {
        this.row.setFormatted(true);
        this.row.setXFIndex(style.getIndex());
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowStyle(CellStyle style) {
        setRowStyle((HSSFCellStyle) style);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public Iterator<Cell> cellIterator() {
        return new CellIterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Cell> iterator() {
        return cellIterator();
    }

    private class CellIterator implements Iterator<Cell> {
        int thisId = -1;
        int nextId = -1;

        public CellIterator() {
            findNext();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nextId < HSSFRow.this.cells.length;
        }

        @Override // java.util.Iterator
        public Cell next() {
            if (hasNext()) {
                HSSFCell[] hSSFCellArr = HSSFRow.this.cells;
                int i = this.nextId;
                HSSFCell cell = hSSFCellArr[i];
                this.thisId = i;
                findNext();
                return cell;
            }
            throw new NoSuchElementException("At last element");
        }

        @Override // java.util.Iterator
        public void remove() {
            if (this.thisId != -1) {
                HSSFRow.this.cells[this.thisId] = null;
                return;
            }
            throw new IllegalStateException("remove() called before next()");
        }

        private void findNext() {
            int i = this.nextId;
            do {
                i++;
                if (i >= HSSFRow.this.cells.length) {
                    break;
                }
            } while (HSSFRow.this.cells[i] == null);
            this.nextId = i;
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(HSSFRow other) {
        if (getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        Integer thisRow = Integer.valueOf(getRowNum());
        Integer otherRow = Integer.valueOf(other.getRowNum());
        return thisRow.compareTo(otherRow);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HSSFRow)) {
            return false;
        }
        HSSFRow other = (HSSFRow) obj;
        return getRowNum() == other.getRowNum() && getSheet() == other.getSheet();
    }

    public int hashCode() {
        return this.row.hashCode();
    }
}
