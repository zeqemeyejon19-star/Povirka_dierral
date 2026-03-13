package org.apache.poi.xssf.usermodel.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.util.CTColComparator;
import org.apache.poi.xssf.util.NumericRanges;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

/* JADX INFO: loaded from: classes.dex */
public class ColumnHelper {
    private CTWorksheet worksheet;

    public ColumnHelper(CTWorksheet worksheet) {
        this.worksheet = worksheet;
        cleanColumns();
    }

    public void cleanColumns() {
        TreeSet<CTCol> trackedCols = new TreeSet<>((Comparator<? super CTCol>) CTColComparator.BY_MIN_MAX);
        CTCols newCols = CTCols.Factory.newInstance();
        CTCols[] colsArray = this.worksheet.getColsArray();
        int i = 0;
        while (i < colsArray.length) {
            CTCols cols = colsArray[i];
            CTCol[] colArray = cols.getColArray();
            for (CTCol col : colArray) {
                addCleanColIntoCols(newCols, col, trackedCols);
            }
            i++;
        }
        for (int y = i - 1; y >= 0; y--) {
            this.worksheet.removeCols(y);
        }
        int y2 = trackedCols.size();
        newCols.setColArray((CTCol[]) trackedCols.toArray(new CTCol[y2]));
        this.worksheet.addNewCols();
        this.worksheet.setColsArray(0, newCols);
    }

    public CTCols addCleanColIntoCols(CTCols cols, CTCol newCol) {
        TreeSet<CTCol> trackedCols = new TreeSet<>((Comparator<? super CTCol>) CTColComparator.BY_MIN_MAX);
        trackedCols.addAll(cols.getColList());
        addCleanColIntoCols(cols, newCol, trackedCols);
        cols.setColArray((CTCol[]) trackedCols.toArray(new CTCol[0]));
        return cols;
    }

    private void addCleanColIntoCols(CTCols cols, CTCol newCol, TreeSet<CTCol> trackedCols) {
        List<CTCol> overlapping = getOverlappingCols(newCol, trackedCols);
        if (overlapping.isEmpty()) {
            trackedCols.add(cloneCol(cols, newCol));
            return;
        }
        trackedCols.removeAll(overlapping);
        Iterator<CTCol> it = overlapping.iterator();
        while (it.hasNext()) {
            CTCol existing = it.next();
            long[] overlap = getOverlap(newCol, existing);
            CTCol overlapCol = cloneCol(cols, existing, overlap);
            setColumnAttributes(newCol, overlapCol);
            trackedCols.add(overlapCol);
            CTCol beforeCol = existing.getMin() < newCol.getMin() ? existing : newCol;
            long[] before = {Math.min(existing.getMin(), newCol.getMin()), overlap[0] - 1};
            if (before[0] <= before[1]) {
                trackedCols.add(cloneCol(cols, beforeCol, before));
            }
            CTCol afterCol = existing.getMax() > newCol.getMax() ? existing : newCol;
            List<CTCol> overlapping2 = overlapping;
            Iterator<CTCol> it2 = it;
            long[] after = {overlap[1] + 1, Math.max(existing.getMax(), newCol.getMax())};
            if (after[0] <= after[1]) {
                trackedCols.add(cloneCol(cols, afterCol, after));
            }
            overlapping = overlapping2;
            it = it2;
        }
    }

    private CTCol cloneCol(CTCols cols, CTCol col, long[] newRange) {
        CTCol cloneCol = cloneCol(cols, col);
        cloneCol.setMin(newRange[0]);
        cloneCol.setMax(newRange[1]);
        return cloneCol;
    }

    private long[] getOverlap(CTCol col1, CTCol col2) {
        return getOverlappingRange(col1, col2);
    }

    private List<CTCol> getOverlappingCols(CTCol newCol, TreeSet<CTCol> trackedCols) {
        CTCol lower = trackedCols.lower(newCol);
        NavigableSet<CTCol> potentiallyOverlapping = lower == null ? trackedCols : trackedCols.tailSet(lower, overlaps(lower, newCol));
        List<CTCol> overlapping = new ArrayList<>();
        for (CTCol existing : potentiallyOverlapping) {
            if (!overlaps(newCol, existing)) {
                break;
            }
            overlapping.add(existing);
        }
        return overlapping;
    }

    private boolean overlaps(CTCol col1, CTCol col2) {
        return NumericRanges.getOverlappingType(toRange(col1), toRange(col2)) != -1;
    }

    private long[] getOverlappingRange(CTCol col1, CTCol col2) {
        return NumericRanges.getOverlappingRange(toRange(col1), toRange(col2));
    }

    private long[] toRange(CTCol col) {
        return new long[]{col.getMin(), col.getMax()};
    }

    public static void sortColumns(CTCols newCols) {
        CTCol[] colArray = newCols.getColArray();
        Arrays.sort(colArray, CTColComparator.BY_MIN_MAX);
        newCols.setColArray(colArray);
    }

    public CTCol cloneCol(CTCols cols, CTCol col) {
        CTCol newCol = cols.addNewCol();
        newCol.setMin(col.getMin());
        newCol.setMax(col.getMax());
        setColumnAttributes(col, newCol);
        return newCol;
    }

    public CTCol getColumn(long index, boolean splitColumns) {
        return getColumn1Based(1 + index, splitColumns);
    }

    public CTCol getColumn1Based(long index1, boolean splitColumns) {
        CTCol col;
        int i;
        CTCol col2;
        char c = 0;
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol[] colArray = cols.getColArray();
        int len$ = colArray.length;
        int i$ = 0;
        while (i$ < len$) {
            CTCol col3 = colArray[i$];
            long colMin = col3.getMin();
            long colMax = col3.getMax();
            if (colMin > index1 || colMax < index1) {
                i$++;
                c = 0;
            } else {
                if (!splitColumns) {
                    return col3;
                }
                if (colMin >= index1) {
                    col = col3;
                    i = 1;
                } else {
                    CTCol[] cTColArr = new CTCol[1];
                    cTColArr[c] = col3;
                    i = 1;
                    col = col3;
                    insertCol(cols, colMin, index1 - 1, cTColArr);
                }
                if (colMax <= index1) {
                    col2 = col;
                } else {
                    CTCol[] cTColArr2 = new CTCol[i];
                    col2 = col;
                    cTColArr2[0] = col2;
                    insertCol(cols, index1 + 1, colMax, cTColArr2);
                }
                col2.setMin(index1);
                col2.setMax(index1);
                return col2;
            }
        }
        return null;
    }

    private CTCol insertCol(CTCols cols, long min, long max, CTCol[] colsWithAttributes) {
        return insertCol(cols, min, max, colsWithAttributes, false, null);
    }

    private CTCol insertCol(CTCols cols, long min, long max, CTCol[] colsWithAttributes, boolean ignoreExistsCheck, CTCol overrideColumn) {
        if (ignoreExistsCheck || !columnExists(cols, min, max)) {
            CTCol newCol = cols.insertNewCol(0);
            newCol.setMin(min);
            newCol.setMax(max);
            for (CTCol col : colsWithAttributes) {
                setColumnAttributes(col, newCol);
            }
            if (overrideColumn != null) {
                setColumnAttributes(overrideColumn, newCol);
            }
            return newCol;
        }
        return null;
    }

    public boolean columnExists(CTCols cols, long index) {
        return columnExists1Based(cols, 1 + index);
    }

    private boolean columnExists1Based(CTCols cols, long index1) {
        CTCol[] arr$ = cols.getColArray();
        for (CTCol col : arr$) {
            if (col.getMin() == index1) {
                return true;
            }
        }
        return false;
    }

    public void setColumnAttributes(CTCol fromCol, CTCol toCol) {
        if (fromCol.isSetBestFit()) {
            toCol.setBestFit(fromCol.getBestFit());
        }
        if (fromCol.isSetCustomWidth()) {
            toCol.setCustomWidth(fromCol.getCustomWidth());
        }
        if (fromCol.isSetHidden()) {
            toCol.setHidden(fromCol.getHidden());
        }
        if (fromCol.isSetStyle()) {
            toCol.setStyle(fromCol.getStyle());
        }
        if (fromCol.isSetWidth()) {
            toCol.setWidth(fromCol.getWidth());
        }
        if (fromCol.isSetCollapsed()) {
            toCol.setCollapsed(fromCol.getCollapsed());
        }
        if (fromCol.isSetPhonetic()) {
            toCol.setPhonetic(fromCol.getPhonetic());
        }
        if (fromCol.isSetOutlineLevel()) {
            toCol.setOutlineLevel(fromCol.getOutlineLevel());
        }
        toCol.setCollapsed(fromCol.isSetCollapsed());
    }

    public void setColBestFit(long index, boolean bestFit) {
        CTCol col = getOrCreateColumn1Based(1 + index, false);
        col.setBestFit(bestFit);
    }

    public void setCustomWidth(long index, boolean bestFit) {
        CTCol col = getOrCreateColumn1Based(1 + index, true);
        col.setCustomWidth(bestFit);
    }

    public void setColWidth(long index, double width) {
        CTCol col = getOrCreateColumn1Based(1 + index, true);
        col.setWidth(width);
    }

    public void setColHidden(long index, boolean hidden) {
        CTCol col = getOrCreateColumn1Based(1 + index, true);
        col.setHidden(hidden);
    }

    protected CTCol getOrCreateColumn1Based(long index1, boolean splitColumns) {
        CTCol col = getColumn1Based(index1, splitColumns);
        if (col == null) {
            CTCol col2 = this.worksheet.getColsArray(0).addNewCol();
            col2.setMin(index1);
            col2.setMax(index1);
            return col2;
        }
        return col;
    }

    public void setColDefaultStyle(long index, CellStyle style) {
        setColDefaultStyle(index, style.getIndex());
    }

    public void setColDefaultStyle(long index, int styleId) {
        CTCol col = getOrCreateColumn1Based(1 + index, true);
        col.setStyle(styleId);
    }

    public int getColDefaultStyle(long index) {
        if (getColumn(index, false) != null) {
            return (int) getColumn(index, false).getStyle();
        }
        return -1;
    }

    private boolean columnExists(CTCols cols, long min, long max) {
        CTCol[] arr$ = cols.getColArray();
        for (CTCol col : arr$) {
            if (col.getMin() == min && col.getMax() == max) {
                return true;
            }
        }
        return false;
    }

    public int getIndexOfColumn(CTCols cols, CTCol searchCol) {
        if (cols == null || searchCol == null) {
            return -1;
        }
        int i = 0;
        CTCol[] arr$ = cols.getColArray();
        for (CTCol col : arr$) {
            if (col.getMin() != searchCol.getMin() || col.getMax() != searchCol.getMax()) {
                i++;
            } else {
                return i;
            }
        }
        return -1;
    }
}
