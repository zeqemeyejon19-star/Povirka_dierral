package org.apache.poi.ss.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public class AreaReference {
    private static final char CELL_DELIMITER = ':';
    private static final SpreadsheetVersion DEFAULT_SPREADSHEET_VERSION = SpreadsheetVersion.EXCEL97;
    private static final char SHEET_NAME_DELIMITER = '!';
    private static final char SPECIAL_NAME_DELIMITER = '\'';
    private final CellReference _firstCell;
    private final boolean _isSingleCell;
    private final CellReference _lastCell;
    private final SpreadsheetVersion _version;

    public AreaReference(String reference, SpreadsheetVersion version) {
        this._version = version != null ? version : DEFAULT_SPREADSHEET_VERSION;
        if (!isContiguous(reference)) {
            throw new IllegalArgumentException("References passed to the AreaReference must be contiguous, use generateContiguous(ref) if you have non-contiguous references");
        }
        String[] parts = separateAreaRefs(reference);
        String part0 = parts[0];
        if (parts.length == 1) {
            CellReference cellReference = new CellReference(part0);
            this._firstCell = cellReference;
            this._lastCell = cellReference;
            this._isSingleCell = true;
            return;
        }
        if (parts.length != 2) {
            throw new IllegalArgumentException("Bad area ref '" + reference + "'");
        }
        String part1 = parts[1];
        if (isPlainColumn(part0)) {
            if (!isPlainColumn(part1)) {
                throw new RuntimeException("Bad area ref '" + reference + "'");
            }
            boolean firstIsAbs = CellReference.isPartAbsolute(part0);
            boolean lastIsAbs = CellReference.isPartAbsolute(part1);
            int col0 = CellReference.convertColStringToIndex(part0);
            int col1 = CellReference.convertColStringToIndex(part1);
            this._firstCell = new CellReference(0, col0, true, firstIsAbs);
            this._lastCell = new CellReference(65535, col1, true, lastIsAbs);
            this._isSingleCell = false;
            return;
        }
        this._firstCell = new CellReference(part0);
        this._lastCell = new CellReference(part1);
        this._isSingleCell = part0.equals(part1);
    }

    private static boolean isPlainColumn(String refPart) {
        for (int i = refPart.length() - 1; i >= 0; i--) {
            int ch = refPart.charAt(i);
            if ((ch != 36 || i != 0) && (ch < 65 || ch > 90)) {
                return false;
            }
        }
        return true;
    }

    @Removal(version = "3.19")
    @Deprecated
    public AreaReference(CellReference topLeft, CellReference botRight) {
        this(topLeft, botRight, DEFAULT_SPREADSHEET_VERSION);
    }

    public AreaReference(CellReference topLeft, CellReference botRight, SpreadsheetVersion version) {
        int firstRow;
        boolean firstRowAbs;
        int lastRow;
        boolean lastRowAbs;
        int firstColumn;
        boolean firstColAbs;
        int lastColumn;
        boolean lastColAbs;
        this._version = version != null ? version : DEFAULT_SPREADSHEET_VERSION;
        boolean swapRows = topLeft.getRow() > botRight.getRow();
        boolean swapCols = topLeft.getCol() > botRight.getCol();
        if (swapRows || swapCols) {
            if (swapRows) {
                firstRow = botRight.getRow();
                firstRowAbs = botRight.isRowAbsolute();
                lastRow = topLeft.getRow();
                lastRowAbs = topLeft.isRowAbsolute();
            } else {
                firstRow = topLeft.getRow();
                firstRowAbs = topLeft.isRowAbsolute();
                lastRow = botRight.getRow();
                lastRowAbs = botRight.isRowAbsolute();
            }
            if (swapCols) {
                firstColumn = botRight.getCol();
                firstColAbs = botRight.isColAbsolute();
                lastColumn = topLeft.getCol();
                lastColAbs = topLeft.isColAbsolute();
            } else {
                firstColumn = topLeft.getCol();
                firstColAbs = topLeft.isColAbsolute();
                lastColumn = botRight.getCol();
                lastColAbs = botRight.isColAbsolute();
            }
            this._firstCell = new CellReference(firstRow, firstColumn, firstRowAbs, firstColAbs);
            this._lastCell = new CellReference(lastRow, lastColumn, lastRowAbs, lastColAbs);
        } else {
            this._firstCell = topLeft;
            this._lastCell = botRight;
        }
        this._isSingleCell = false;
    }

    public static boolean isContiguous(String reference) {
        int sheetRefEnd = reference.indexOf(33);
        if (sheetRefEnd != -1) {
            reference = reference.substring(sheetRefEnd);
        }
        return !reference.contains(",");
    }

    public static AreaReference getWholeRow(SpreadsheetVersion version, String start, String end) {
        if (version == null) {
            version = DEFAULT_SPREADSHEET_VERSION;
        }
        return new AreaReference("$A" + start + ":$" + version.getLastColumnName() + end, version);
    }

    public static AreaReference getWholeColumn(SpreadsheetVersion version, String start, String end) {
        if (version == null) {
            version = DEFAULT_SPREADSHEET_VERSION;
        }
        return new AreaReference(start + "$1:" + end + "$" + version.getMaxRows(), version);
    }

    public static boolean isWholeColumnReference(SpreadsheetVersion version, CellReference topLeft, CellReference botRight) {
        if (version == null) {
            version = DEFAULT_SPREADSHEET_VERSION;
        }
        if (topLeft.getRow() == 0 && topLeft.isRowAbsolute() && botRight.getRow() == version.getLastRowIndex() && botRight.isRowAbsolute()) {
            return true;
        }
        return false;
    }

    public boolean isWholeColumnReference() {
        return isWholeColumnReference(this._version, this._firstCell, this._lastCell);
    }

    @Removal(version = "3.19")
    @Deprecated
    public static AreaReference[] generateContiguous(String reference) {
        return generateContiguous(DEFAULT_SPREADSHEET_VERSION, reference);
    }

    public static AreaReference[] generateContiguous(SpreadsheetVersion version, String reference) {
        if (version == null) {
            version = DEFAULT_SPREADSHEET_VERSION;
        }
        List<AreaReference> refs = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(reference, ",");
        while (st.hasMoreTokens()) {
            refs.add(new AreaReference(st.nextToken(), version));
        }
        return (AreaReference[]) refs.toArray(new AreaReference[refs.size()]);
    }

    public boolean isSingleCell() {
        return this._isSingleCell;
    }

    public CellReference getFirstCell() {
        return this._firstCell;
    }

    public CellReference getLastCell() {
        return this._lastCell;
    }

    public CellReference[] getAllReferencedCells() {
        if (this._isSingleCell) {
            return new CellReference[]{this._firstCell};
        }
        int minRow = Math.min(this._firstCell.getRow(), this._lastCell.getRow());
        int maxRow = Math.max(this._firstCell.getRow(), this._lastCell.getRow());
        int minCol = Math.min((int) this._firstCell.getCol(), (int) this._lastCell.getCol());
        int maxCol = Math.max((int) this._firstCell.getCol(), (int) this._lastCell.getCol());
        String sheetName = this._firstCell.getSheetName();
        List<CellReference> refs = new ArrayList<>();
        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                CellReference ref = new CellReference(sheetName, row, col, this._firstCell.isRowAbsolute(), this._firstCell.isColAbsolute());
                refs.add(ref);
            }
        }
        return (CellReference[]) refs.toArray(new CellReference[refs.size()]);
    }

    public String formatAsString() {
        if (isWholeColumnReference()) {
            return CellReference.convertNumToColString(this._firstCell.getCol()) + ":" + CellReference.convertNumToColString(this._lastCell.getCol());
        }
        StringBuffer sb = new StringBuffer(32);
        sb.append(this._firstCell.formatAsString());
        if (!this._isSingleCell) {
            sb.append(CELL_DELIMITER);
            if (this._lastCell.getSheetName() == null) {
                sb.append(this._lastCell.formatAsString());
            } else {
                this._lastCell.appendCellReference(sb);
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    private static String[] separateAreaRefs(String reference) {
        int len = reference.length();
        int delimiterPos = -1;
        boolean insideDelimitedName = false;
        int i = 0;
        while (i < len) {
            char cCharAt = reference.charAt(i);
            if (cCharAt != '\'') {
                if (cCharAt == ':' && !insideDelimitedName) {
                    if (delimiterPos >= 0) {
                        throw new IllegalArgumentException("More than one cell delimiter ':' appears in area reference '" + reference + "'");
                    }
                    delimiterPos = i;
                }
            } else if (!insideDelimitedName) {
                insideDelimitedName = true;
            } else {
                if (i >= len - 1) {
                    throw new IllegalArgumentException("Area reference '" + reference + "' ends with special name delimiter '" + SPECIAL_NAME_DELIMITER + "'");
                }
                if (reference.charAt(i + 1) == '\'') {
                    i++;
                } else {
                    insideDelimitedName = false;
                }
            }
            i++;
        }
        if (delimiterPos < 0) {
            return new String[]{reference};
        }
        String partA = reference.substring(0, delimiterPos);
        String partB = reference.substring(delimiterPos + 1);
        if (partB.indexOf(33) >= 0) {
            throw new RuntimeException("Unexpected ! in second cell reference of '" + reference + "'");
        }
        int plingPos = partA.lastIndexOf(33);
        if (plingPos >= 0) {
            String sheetName = partA.substring(0, plingPos + 1);
            return new String[]{partA, sheetName + partB};
        }
        return new String[]{partA, partB};
    }
}
