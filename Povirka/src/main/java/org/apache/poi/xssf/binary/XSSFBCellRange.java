package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBCellRange {
    public static final int length = 16;
    int firstCol;
    int firstRow;
    int lastCol;
    int lastRow;

    XSSFBCellRange() {
    }

    public static XSSFBCellRange parse(byte[] data, int offset, XSSFBCellRange cellRange) {
        if (cellRange == null) {
            cellRange = new XSSFBCellRange();
        }
        cellRange.firstRow = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset));
        int offset2 = offset + 4;
        cellRange.lastRow = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset2));
        int offset3 = offset2 + 4;
        cellRange.firstCol = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset3));
        cellRange.lastCol = XSSFBUtils.castToInt(LittleEndian.getUInt(data, offset3 + 4));
        return cellRange;
    }
}
