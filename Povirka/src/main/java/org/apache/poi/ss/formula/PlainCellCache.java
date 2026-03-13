package org.apache.poi.ss.formula;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class PlainCellCache {
    private Map<Loc, PlainValueCellCacheEntry> _plainValueEntriesByLoc = new HashMap();

    public static final class Loc {
        private final long _bookSheetColumn;
        private final int _rowIndex;

        public Loc(int bookIndex, int sheetIndex, int rowIndex, int columnIndex) {
            this._bookSheetColumn = toBookSheetColumn(bookIndex, sheetIndex, columnIndex);
            this._rowIndex = rowIndex;
        }

        public static long toBookSheetColumn(int bookIndex, int sheetIndex, int columnIndex) {
            return ((((long) bookIndex) & 65535) << 48) + ((((long) sheetIndex) & 65535) << 32) + ((65535 & ((long) columnIndex)) << 0);
        }

        public Loc(long bookSheetColumn, int rowIndex) {
            this._bookSheetColumn = bookSheetColumn;
            this._rowIndex = rowIndex;
        }

        public int hashCode() {
            long j = this._bookSheetColumn;
            return ((int) (j ^ (j >>> 32))) + (this._rowIndex * 17);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Loc)) {
                return false;
            }
            Loc other = (Loc) obj;
            return this._bookSheetColumn == other._bookSheetColumn && this._rowIndex == other._rowIndex;
        }

        public int getRowIndex() {
            return this._rowIndex;
        }

        public int getColumnIndex() {
            return (int) (this._bookSheetColumn & 65535);
        }

        public int getSheetIndex() {
            return (int) ((this._bookSheetColumn >> 32) & 65535);
        }

        public int getBookIndex() {
            return (int) ((this._bookSheetColumn >> 48) & 65535);
        }
    }

    public void put(Loc key, PlainValueCellCacheEntry cce) {
        this._plainValueEntriesByLoc.put(key, cce);
    }

    public void clear() {
        this._plainValueEntriesByLoc.clear();
    }

    public PlainValueCellCacheEntry get(Loc key) {
        return this._plainValueEntriesByLoc.get(key);
    }

    public void remove(Loc key) {
        this._plainValueEntriesByLoc.remove(key);
    }
}
