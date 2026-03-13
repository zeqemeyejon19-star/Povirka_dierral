package org.apache.poi.xssf.usermodel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.CalculationChain;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.helpers.XSSFRowShifter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;

/* JADX INFO: loaded from: classes.dex */
public class XSSFRow implements Row, Comparable<XSSFRow> {
    private final TreeMap<Integer, XSSFCell> _cells = new TreeMap<>();
    private final CTRow _row;
    private final XSSFSheet _sheet;

    protected XSSFRow(CTRow row, XSSFSheet sheet) {
        this._row = row;
        this._sheet = sheet;
        CTCell[] arr$ = row.getCArray();
        for (CTCell c : arr$) {
            XSSFCell cell = new XSSFCell(this, c);
            Integer colI = new Integer(cell.getColumnIndex());
            this._cells.put(colI, cell);
            sheet.onReadCell(cell);
        }
        if (!row.isSetR()) {
            int nextRowNum = sheet.getLastRowNum() + 2;
            if (nextRowNum == 2 && sheet.getPhysicalNumberOfRows() == 0) {
                nextRowNum = 1;
            }
            row.setR(nextRowNum);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFSheet getSheet() {
        return this._sheet;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public Iterator<Cell> cellIterator() {
        return this._cells.values().iterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Cell> iterator() {
        return cellIterator();
    }

    @Override // java.lang.Comparable
    public int compareTo(XSSFRow other) {
        if (getSheet() != other.getSheet()) {
            throw new IllegalArgumentException("The compared rows must belong to the same sheet");
        }
        Integer thisRow = Integer.valueOf(getRowNum());
        Integer otherRow = Integer.valueOf(other.getRowNum());
        return thisRow.compareTo(otherRow);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XSSFRow)) {
            return false;
        }
        XSSFRow other = (XSSFRow) obj;
        return getRowNum() == other.getRowNum() && getSheet() == other.getSheet();
    }

    public int hashCode() {
        return this._row.hashCode();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCell createCell(int columnIndex) {
        return createCell(columnIndex, CellType.BLANK);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCell createCell(int columnIndex, int type) {
        return createCell(columnIndex, CellType.forInt(type));
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCell createCell(int columnIndex, CellType type) {
        CTCell ctCell;
        Integer colI = new Integer(columnIndex);
        XSSFCell prev = this._cells.get(colI);
        if (prev != null) {
            ctCell = prev.getCTCell();
            ctCell.set(CTCell.Factory.newInstance());
        } else {
            ctCell = this._row.addNewC();
        }
        XSSFCell xcell = new XSSFCell(this, ctCell);
        xcell.setCellNum(columnIndex);
        if (type != CellType.BLANK) {
            xcell.setCellType(type);
        }
        this._cells.put(colI, xcell);
        return xcell;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCell getCell(int cellnum) {
        return getCell(cellnum, this._sheet.getWorkbook().getMissingCellPolicy());
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCell getCell(int cellnum, Row.MissingCellPolicy policy) {
        if (cellnum < 0) {
            throw new IllegalArgumentException("Cell index must be >= 0");
        }
        Integer colI = new Integer(cellnum);
        XSSFCell cell = this._cells.get(colI);
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

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.XSSFRow$1, reason: invalid class name */
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
        return (short) (this._cells.size() == 0 ? -1 : this._cells.firstKey().intValue());
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getLastCellNum() {
        return (short) (this._cells.size() == 0 ? -1 : this._cells.lastKey().intValue() + 1);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public short getHeight() {
        return (short) (getHeightInPoints() * 20.0f);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public float getHeightInPoints() {
        if (this._row.isSetHt()) {
            return (float) this._row.getHt();
        }
        return this._sheet.getDefaultRowHeightInPoints();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeight(short height) {
        if (height == -1) {
            if (this._row.isSetHt()) {
                this._row.unsetHt();
            }
            if (this._row.isSetCustomHeight()) {
                this._row.unsetCustomHeight();
                return;
            }
            return;
        }
        this._row.setHt(((double) height) / 20.0d);
        this._row.setCustomHeight(true);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setHeightInPoints(float height) {
        setHeight((short) (height != -1.0f ? 20.0f * height : -1.0f));
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getPhysicalNumberOfCells() {
        return this._cells.size();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getRowNum() {
        return (int) (this._row.getR() - 1);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowNum(int rowIndex) {
        int maxrow = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        if (rowIndex < 0 || rowIndex > maxrow) {
            throw new IllegalArgumentException("Invalid row number (" + rowIndex + ") outside allowable range (0.." + maxrow + ")");
        }
        this._row.setR(rowIndex + 1);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean getZeroHeight() {
        return this._row.getHidden();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setZeroHeight(boolean height) {
        this._row.setHidden(height);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public boolean isFormatted() {
        return this._row.isSetS();
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public XSSFCellStyle getRowStyle() {
        if (!isFormatted()) {
            return null;
        }
        StylesTable stylesSource = getSheet().getWorkbook().getStylesSource();
        if (stylesSource.getNumCellStyles() > 0) {
            return stylesSource.getStyleAt((int) this._row.getS());
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void setRowStyle(CellStyle style) {
        if (style == null) {
            if (this._row.isSetS()) {
                this._row.unsetS();
                this._row.unsetCustomFormat();
                return;
            }
            return;
        }
        StylesTable styleSource = getSheet().getWorkbook().getStylesSource();
        XSSFCellStyle xStyle = (XSSFCellStyle) style;
        xStyle.verifyBelongsToStylesSource(styleSource);
        long idx = styleSource.putStyle(xStyle);
        this._row.setS(idx);
        this._row.setCustomFormat(true);
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public void removeCell(Cell cell) {
        if (cell.getRow() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this row");
        }
        XSSFCell xcell = (XSSFCell) cell;
        if (xcell.isPartOfArrayFormulaGroup()) {
            xcell.notifyArrayFormulaChanging();
        }
        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            this._sheet.getWorkbook().onDeleteFormula(xcell);
        }
        Integer colI = new Integer(cell.getColumnIndex());
        this._cells.remove(colI);
    }

    @Internal
    public CTRow getCTRow() {
        return this._row;
    }

    protected void onDocumentWrite() {
        boolean isOrdered = true;
        CTCell[] cArray = this._row.getCArray();
        if (cArray.length != this._cells.size()) {
            isOrdered = false;
        } else {
            int i = 0;
            for (XSSFCell cell : this._cells.values()) {
                CTCell c1 = cell.getCTCell();
                int i2 = i + 1;
                CTCell c2 = cArray[i];
                String r1 = c1.getR();
                String r2 = c2.getR();
                if (r1 == null) {
                    if (r2 != null) {
                        isOrdered = false;
                        break;
                    }
                    i = i2;
                } else {
                    if (!r1.equals(r2)) {
                        isOrdered = false;
                        break;
                    }
                    i = i2;
                }
            }
        }
        if (!isOrdered) {
            CTCell[] cArray2 = new CTCell[this._cells.size()];
            int i3 = 0;
            for (XSSFCell xssfCell : this._cells.values()) {
                cArray2[i3] = (CTCell) xssfCell.getCTCell().copy();
                xssfCell.setCTCell(cArray2[i3]);
                i3++;
            }
            this._row.setCArray(cArray2);
        }
    }

    public String toString() {
        return this._row.toString();
    }

    protected void shift(int n) {
        int rownum = getRowNum() + n;
        CalculationChain calcChain = this._sheet.getWorkbook().getCalculationChain();
        int sheetId = (int) this._sheet.sheet.getSheetId();
        String msg = "Row[rownum=" + getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
        for (Cell c : this) {
            XSSFCell cell = (XSSFCell) c;
            if (cell.isPartOfArrayFormulaGroup()) {
                cell.notifyArrayFormulaChanging(msg);
            }
            if (calcChain != null) {
                calcChain.removeItem(sheetId, cell.getReference());
            }
            CTCell ctCell = cell.getCTCell();
            String r = new CellReference(rownum, cell.getColumnIndex()).formatAsString();
            ctCell.setR(r);
        }
        setRowNum(rownum);
    }

    public void copyRowFrom(Row srcRow, CellCopyPolicy policy) {
        if (srcRow == null) {
            for (Cell destCell : this) {
                ((XSSFCell) destCell).copyCellFrom(null, policy);
            }
            if (policy.isCopyMergedRegions()) {
                int destRowNum = getRowNum();
                int index = 0;
                Set<Integer> indices = new HashSet<>();
                for (CellRangeAddress destRegion : getSheet().getMergedRegions()) {
                    if (destRowNum == destRegion.getFirstRow() && destRowNum == destRegion.getLastRow()) {
                        indices.add(Integer.valueOf(index));
                    }
                    index++;
                }
                getSheet().removeMergedRegions(indices);
            }
            if (policy.isCopyRowHeight()) {
                setHeight((short) -1);
                return;
            }
            return;
        }
        for (Cell c : srcRow) {
            XSSFCell srcCell = (XSSFCell) c;
            XSSFCell destCell2 = createCell(srcCell.getColumnIndex(), srcCell.getCellTypeEnum());
            destCell2.copyCellFrom(srcCell, policy);
        }
        XSSFRowShifter rowShifter = new XSSFRowShifter(this._sheet);
        int sheetIndex = this._sheet.getWorkbook().getSheetIndex(this._sheet);
        String sheetName = this._sheet.getWorkbook().getSheetName(sheetIndex);
        int srcRowNum = srcRow.getRowNum();
        int destRowNum2 = getRowNum();
        int rowDifference = destRowNum2 - srcRowNum;
        FormulaShifter shifter = FormulaShifter.createForRowCopy(sheetIndex, sheetName, srcRowNum, srcRowNum, rowDifference, SpreadsheetVersion.EXCEL2007);
        rowShifter.updateRowFormulas(this, shifter);
        if (policy.isCopyMergedRegions()) {
            for (CellRangeAddress srcRegion : srcRow.getSheet().getMergedRegions()) {
                if (srcRowNum == srcRegion.getFirstRow() && srcRowNum == srcRegion.getLastRow()) {
                    CellRangeAddress destRegion2 = srcRegion.copy();
                    destRegion2.setFirstRow(destRowNum2);
                    destRegion2.setLastRow(destRowNum2);
                    getSheet().addMergedRegion(destRegion2);
                }
            }
        }
        if (policy.isCopyRowHeight()) {
            setHeight(srcRow.getHeight());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Row
    public int getOutlineLevel() {
        return this._row.getOutlineLevel();
    }
}
