package org.apache.poi.ss;

import androidx.appcompat.widget.ActivityChooserView;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public enum SpreadsheetVersion {
    EXCEL97(65536, 256, 30, 3, 4000, 32767),
    EXCEL2007(1048576, 16384, 255, ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, 64000, 32767);

    private final int _maxCellStyles;
    private final int _maxColumns;
    private final int _maxCondFormats;
    private final int _maxFunctionArgs;
    private final int _maxRows;
    private final int _maxTextLength;

    SpreadsheetVersion(int maxRows, int maxColumns, int maxFunctionArgs, int maxCondFormats, int maxCellStyles, int maxText) {
        this._maxRows = maxRows;
        this._maxColumns = maxColumns;
        this._maxFunctionArgs = maxFunctionArgs;
        this._maxCondFormats = maxCondFormats;
        this._maxCellStyles = maxCellStyles;
        this._maxTextLength = maxText;
    }

    public int getMaxRows() {
        return this._maxRows;
    }

    public int getLastRowIndex() {
        return this._maxRows - 1;
    }

    public int getMaxColumns() {
        return this._maxColumns;
    }

    public int getLastColumnIndex() {
        return this._maxColumns - 1;
    }

    public int getMaxFunctionArgs() {
        return this._maxFunctionArgs;
    }

    public int getMaxConditionalFormats() {
        return this._maxCondFormats;
    }

    public int getMaxCellStyles() {
        return this._maxCellStyles;
    }

    public String getLastColumnName() {
        return CellReference.convertNumToColString(getLastColumnIndex());
    }

    public int getMaxTextLength() {
        return this._maxTextLength;
    }
}
