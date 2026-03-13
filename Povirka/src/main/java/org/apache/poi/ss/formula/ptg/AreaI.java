package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public interface AreaI {
    int getFirstColumn();

    int getFirstRow();

    int getLastColumn();

    int getLastRow();

    public static class OffsetArea implements AreaI {
        private final int _firstColumn;
        private final int _firstRow;
        private final int _lastColumn;
        private final int _lastRow;

        public OffsetArea(int baseRow, int baseColumn, int relFirstRowIx, int relLastRowIx, int relFirstColIx, int relLastColIx) {
            this._firstRow = Math.min(relFirstRowIx, relLastRowIx) + baseRow;
            this._lastRow = Math.max(relFirstRowIx, relLastRowIx) + baseRow;
            this._firstColumn = Math.min(relFirstColIx, relLastColIx) + baseColumn;
            this._lastColumn = Math.max(relFirstColIx, relLastColIx) + baseColumn;
        }

        @Override // org.apache.poi.ss.formula.ptg.AreaI
        public int getFirstColumn() {
            return this._firstColumn;
        }

        @Override // org.apache.poi.ss.formula.ptg.AreaI
        public int getFirstRow() {
            return this._firstRow;
        }

        @Override // org.apache.poi.ss.formula.ptg.AreaI
        public int getLastColumn() {
            return this._lastColumn;
        }

        @Override // org.apache.poi.ss.formula.ptg.AreaI
        public int getLastRow() {
            return this._lastRow;
        }
    }
}
