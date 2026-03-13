package org.apache.poi.xssf.streaming;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
class AutoSizeColumnTracker {
    private final int defaultCharWidth;
    private final DataFormatter dataFormatter = new DataFormatter();
    private final Map<Integer, ColumnWidthPair> maxColumnWidths = new HashMap();
    private final Set<Integer> untrackedColumns = new HashSet();
    private boolean trackAllColumns = false;

    private static class ColumnWidthPair {
        private double withSkipMergedCells;
        private double withUseMergedCells;

        public ColumnWidthPair() {
            this(-1.0d, -1.0d);
        }

        public ColumnWidthPair(double columnWidthSkipMergedCells, double columnWidthUseMergedCells) {
            this.withSkipMergedCells = columnWidthSkipMergedCells;
            this.withUseMergedCells = columnWidthUseMergedCells;
        }

        public double getMaxColumnWidth(boolean useMergedCells) {
            return useMergedCells ? this.withUseMergedCells : this.withSkipMergedCells;
        }

        public void setMaxColumnWidths(double unmergedWidth, double mergedWidth) {
            double dMax = Math.max(this.withUseMergedCells, mergedWidth);
            this.withUseMergedCells = dMax;
            this.withSkipMergedCells = Math.max(dMax, unmergedWidth);
        }
    }

    public AutoSizeColumnTracker(Sheet sheet) {
        this.defaultCharWidth = SheetUtil.getDefaultCharWidth(sheet.getWorkbook());
    }

    public SortedSet<Integer> getTrackedColumns() {
        SortedSet<Integer> sorted = new TreeSet<>(this.maxColumnWidths.keySet());
        return Collections.unmodifiableSortedSet(sorted);
    }

    public boolean isColumnTracked(int column) {
        return this.trackAllColumns || this.maxColumnWidths.containsKey(Integer.valueOf(column));
    }

    public boolean isAllColumnsTracked() {
        return this.trackAllColumns;
    }

    public void trackAllColumns() {
        this.trackAllColumns = true;
        this.untrackedColumns.clear();
    }

    public void untrackAllColumns() {
        this.trackAllColumns = false;
        this.maxColumnWidths.clear();
        this.untrackedColumns.clear();
    }

    public void trackColumns(Collection<Integer> columns) {
        Iterator<Integer> it = columns.iterator();
        while (it.hasNext()) {
            int column = it.next().intValue();
            trackColumn(column);
        }
    }

    public boolean trackColumn(int column) {
        this.untrackedColumns.remove(Integer.valueOf(column));
        if (!this.maxColumnWidths.containsKey(Integer.valueOf(column))) {
            this.maxColumnWidths.put(Integer.valueOf(column), new ColumnWidthPair());
            return true;
        }
        return false;
    }

    private boolean implicitlyTrackColumn(int column) {
        if (!this.untrackedColumns.contains(Integer.valueOf(column))) {
            trackColumn(column);
            return true;
        }
        return false;
    }

    public boolean untrackColumns(Collection<Integer> columns) {
        this.untrackedColumns.addAll(columns);
        return this.maxColumnWidths.keySet().removeAll(columns);
    }

    public boolean untrackColumn(int column) {
        this.untrackedColumns.add(Integer.valueOf(column));
        return this.maxColumnWidths.keySet().remove(Integer.valueOf(column));
    }

    public int getBestFitColumnWidth(int column, boolean useMergedCells) {
        if (!this.maxColumnWidths.containsKey(Integer.valueOf(column))) {
            if (this.trackAllColumns) {
                if (!implicitlyTrackColumn(column)) {
                    Throwable reason = new IllegalStateException("Column was explicitly untracked after trackAllColumns() was called.");
                    throw new IllegalStateException("Cannot get best fit column width on explicitly untracked column " + column + ". Either explicitly track the column or track all columns.", reason);
                }
            } else {
                Throwable reason2 = new IllegalStateException("Column was never explicitly tracked and isAllColumnsTracked() is false (trackAllColumns() was never called or untrackAllColumns() was called after trackAllColumns() was called).");
                throw new IllegalStateException("Cannot get best fit column width on untracked column " + column + ". Either explicitly track the column or track all columns.", reason2);
            }
        }
        double width = this.maxColumnWidths.get(Integer.valueOf(column)).getMaxColumnWidth(useMergedCells);
        return (int) (256.0d * width);
    }

    public void updateColumnWidths(Row row) {
        implicitlyTrackColumnsInRow(row);
        if (this.maxColumnWidths.size() < row.getPhysicalNumberOfCells()) {
            for (Map.Entry<Integer, ColumnWidthPair> e : this.maxColumnWidths.entrySet()) {
                Cell cell = row.getCell(e.getKey().intValue());
                if (cell != null) {
                    ColumnWidthPair pair = e.getValue();
                    updateColumnWidth(cell, pair);
                }
            }
            return;
        }
        for (Cell cell2 : row) {
            int column = cell2.getColumnIndex();
            if (this.maxColumnWidths.containsKey(Integer.valueOf(column))) {
                ColumnWidthPair pair2 = this.maxColumnWidths.get(Integer.valueOf(column));
                updateColumnWidth(cell2, pair2);
            }
        }
    }

    private void implicitlyTrackColumnsInRow(Row row) {
        if (this.trackAllColumns) {
            for (Cell cell : row) {
                int column = cell.getColumnIndex();
                implicitlyTrackColumn(column);
            }
        }
    }

    private void updateColumnWidth(Cell cell, ColumnWidthPair pair) {
        double unmergedWidth = SheetUtil.getCellWidth(cell, this.defaultCharWidth, this.dataFormatter, false);
        double mergedWidth = SheetUtil.getCellWidth(cell, this.defaultCharWidth, this.dataFormatter, true);
        pair.setMaxColumnWidths(unmergedWidth, mergedWidth);
    }
}
