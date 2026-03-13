package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBCellHeader {
    public static int length = 8;
    private int colNum;
    private int rowNum;
    private boolean showPhonetic;
    private int styleIdx;

    XSSFBCellHeader() {
    }

    public static void parse(byte[] data, int offset, int currentRow, XSSFBCellHeader cell) {
        int colNum = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        int offset2 = offset + 4;
        int styleIdx = XSSFBUtils.get24BitInt(data, offset2);
        int i = offset2 + 3;
        cell.reset(currentRow, colNum, styleIdx, false);
    }

    public void reset(int rowNum, int colNum, int styleIdx, boolean showPhonetic) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.styleIdx = styleIdx;
        this.showPhonetic = showPhonetic;
    }

    int getColNum() {
        return this.colNum;
    }

    int getStyleIdx() {
        return this.styleIdx;
    }
}
