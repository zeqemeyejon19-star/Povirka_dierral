package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
final class XSSFEvaluationSheet implements EvaluationSheet {
    private Map<CellKey, EvaluationCell> _cellCache;
    private final XSSFSheet _xs;

    public XSSFEvaluationSheet(XSSFSheet sheet) {
        this._xs = sheet;
    }

    public XSSFSheet getXSSFSheet() {
        return this._xs;
    }

    @Override // org.apache.poi.ss.formula.EvaluationSheet
    public void clearAllCachedResultValues() {
        this._cellCache = null;
    }

    @Override // org.apache.poi.ss.formula.EvaluationSheet
    public EvaluationCell getCell(int rowIndex, int columnIndex) {
        XSSFCell cell;
        if (this._cellCache == null) {
            this._cellCache = new HashMap(this._xs.getLastRowNum() * 3);
            for (Row row : this._xs) {
                int rowNum = row.getRowNum();
                for (Cell cell2 : row) {
                    this._cellCache.put(new CellKey(rowNum, cell2.getColumnIndex()), new XSSFEvaluationCell((XSSFCell) cell2, this));
                }
            }
        }
        CellKey key = new CellKey(rowIndex, columnIndex);
        EvaluationCell evalcell = this._cellCache.get(key);
        if (evalcell == null) {
            XSSFRow row2 = this._xs.getRow(rowIndex);
            if (row2 == null || (cell = row2.getCell(columnIndex)) == null) {
                return null;
            }
            EvaluationCell evalcell2 = new XSSFEvaluationCell(cell, this);
            this._cellCache.put(key, evalcell2);
            return evalcell2;
        }
        return evalcell;
    }

    private static class CellKey {
        private final int _col;
        private int _hash = -1;
        private final int _row;

        protected CellKey(int row, int col) {
            this._row = row;
            this._col = col;
        }

        public int hashCode() {
            if (this._hash == -1) {
                this._hash = ((this._row + 629) * 37) + this._col;
            }
            return this._hash;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof CellKey)) {
                return false;
            }
            CellKey oKey = (CellKey) obj;
            return this._row == oKey._row && this._col == oKey._col;
        }
    }
}
