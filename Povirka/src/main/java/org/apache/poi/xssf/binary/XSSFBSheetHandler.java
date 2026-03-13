package org.apache.poi.xssf.binary;

import java.io.InputStream;
import java.util.Queue;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBSheetHandler extends XSSFBParser {
    private static final int CHECK_ALL_ROWS = -1;
    private final XSSFBCellHeader cellBuffer;
    private final XSSFBCommentsTable comments;
    private int currentRow;
    private final DataFormatter dataFormatter;
    private final boolean formulasNotResults;
    private final XSSFSheetXMLHandler.SheetContentsHandler handler;
    private XSSFBCellRange hyperlinkCellRange;
    private int lastEndedRow;
    private int lastStartedRow;
    private byte[] rkBuffer;
    private final XSSFBSharedStringsTable stringsTable;
    private final XSSFBStylesTable styles;
    private StringBuilder xlWideStringBuffer;

    public interface SheetContentsHandler extends XSSFSheetXMLHandler.SheetContentsHandler {
        void hyperlinkCell(String str, String str2, String str3, String str4, XSSFComment xSSFComment);
    }

    public XSSFBSheetHandler(InputStream is, XSSFBStylesTable styles, XSSFBCommentsTable comments, XSSFBSharedStringsTable strings, XSSFSheetXMLHandler.SheetContentsHandler sheetContentsHandler, DataFormatter dataFormatter, boolean formulasNotResults) {
        super(is);
        this.lastEndedRow = -1;
        this.lastStartedRow = -1;
        this.currentRow = 0;
        this.rkBuffer = new byte[8];
        this.hyperlinkCellRange = null;
        this.xlWideStringBuffer = new StringBuilder();
        this.cellBuffer = new XSSFBCellHeader();
        this.styles = styles;
        this.comments = comments;
        this.stringsTable = strings;
        this.handler = sheetContentsHandler;
        this.dataFormatter = dataFormatter;
        this.formulasNotResults = formulasNotResults;
    }

    @Override // org.apache.poi.xssf.binary.XSSFBParser
    public void handleRecord(int id, byte[] data) throws XSSFBParseException {
        XSSFBRecordType type = XSSFBRecordType.lookup(id);
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[type.ordinal()]) {
            case 1:
                int rw = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 0));
                if (rw > 1048576) {
                    throw new XSSFBParseException("Row number beyond allowable range: " + rw);
                }
                this.currentRow = rw;
                checkMissedComments(rw);
                startRow(this.currentRow);
                return;
            case 2:
                handleBrtCellIsst(data);
                return;
            case 3:
                handleCellSt(data);
                return;
            case 4:
                handleCellRk(data);
                return;
            case 5:
                handleCellReal(data);
                return;
            case 6:
                handleBoolean(data);
                return;
            case 7:
                handleCellError(data);
                return;
            case 8:
                beforeCellValue(data);
                return;
            case 9:
                handleFmlaString(data);
                return;
            case 10:
                handleFmlaNum(data);
                return;
            case 11:
                handleFmlaError(data);
                return;
            case 12:
                checkMissedComments(-1);
                endRow(this.lastStartedRow);
                return;
            case 13:
                handleHeaderFooter(data);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.binary.XSSFBSheetHandler$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType;

        static {
            int[] iArr = new int[XSSFBRecordType.values().length];
            $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType = iArr;
            try {
                iArr[XSSFBRecordType.BrtRowHdr.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellIsst.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellSt.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellRk.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellReal.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellBool.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellError.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCellBlank.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtFmlaString.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtFmlaNum.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtFmlaError.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtEndSheetData.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtBeginHeaderFooter.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    private void beforeCellValue(byte[] data) {
        XSSFBCellHeader.parse(data, 0, this.currentRow, this.cellBuffer);
        checkMissedComments(this.currentRow, this.cellBuffer.getColNum());
    }

    private void handleCellValue(String formattedValue) {
        CellAddress cellAddress = new CellAddress(this.currentRow, this.cellBuffer.getColNum());
        XSSFBComment comment = null;
        XSSFBCommentsTable xSSFBCommentsTable = this.comments;
        if (xSSFBCommentsTable != null) {
            comment = xSSFBCommentsTable.get(cellAddress);
        }
        this.handler.cell(cellAddress.formatAsString(), formattedValue, comment);
    }

    private void handleFmlaNum(byte[] data) {
        beforeCellValue(data);
        double val = LittleEndian.getDouble(data, XSSFBCellHeader.length);
        handleCellValue(formatVal(val, this.cellBuffer.getStyleIdx()));
    }

    private void handleCellSt(byte[] data) {
        beforeCellValue(data);
        this.xlWideStringBuffer.setLength(0);
        XSSFBUtils.readXLWideString(data, XSSFBCellHeader.length, this.xlWideStringBuffer);
        handleCellValue(this.xlWideStringBuffer.toString());
    }

    private void handleFmlaString(byte[] data) {
        beforeCellValue(data);
        this.xlWideStringBuffer.setLength(0);
        XSSFBUtils.readXLWideString(data, XSSFBCellHeader.length, this.xlWideStringBuffer);
        handleCellValue(this.xlWideStringBuffer.toString());
    }

    private void handleCellError(byte[] data) {
        beforeCellValue(data);
        handleCellValue("ERROR");
    }

    private void handleFmlaError(byte[] data) {
        beforeCellValue(data);
        handleCellValue("ERROR");
    }

    private void handleBoolean(byte[] data) {
        beforeCellValue(data);
        String formattedVal = data[XSSFBCellHeader.length] == 1 ? "TRUE" : "FALSE";
        handleCellValue(formattedVal);
    }

    private void handleCellReal(byte[] data) {
        beforeCellValue(data);
        double val = LittleEndian.getDouble(data, XSSFBCellHeader.length);
        handleCellValue(formatVal(val, this.cellBuffer.getStyleIdx()));
    }

    private void handleCellRk(byte[] data) {
        beforeCellValue(data);
        double val = rkNumber(data, XSSFBCellHeader.length);
        handleCellValue(formatVal(val, this.cellBuffer.getStyleIdx()));
    }

    private String formatVal(double val, int styleIdx) {
        String formatString = this.styles.getNumberFormatString(styleIdx);
        short styleIndex = this.styles.getNumberFormatIndex(styleIdx);
        if (formatString == null) {
            formatString = BuiltinFormats.getBuiltinFormat(0);
            styleIndex = 0;
        }
        return this.dataFormatter.formatRawCellContents(val, styleIndex, formatString);
    }

    private void handleBrtCellIsst(byte[] data) {
        beforeCellValue(data);
        int idx = XSSFBUtils.castToInt(LittleEndian.getUInt(data, XSSFBCellHeader.length));
        XSSFRichTextString rtss = new XSSFRichTextString(this.stringsTable.getEntryAt(idx));
        handleCellValue(rtss.getString());
    }

    private void handleHeaderFooter(byte[] data) {
        XSSFBHeaderFooters headerFooter = XSSFBHeaderFooters.parse(data);
        outputHeaderFooter(headerFooter.getHeader());
        outputHeaderFooter(headerFooter.getFooter());
        outputHeaderFooter(headerFooter.getHeaderEven());
        outputHeaderFooter(headerFooter.getFooterEven());
        outputHeaderFooter(headerFooter.getHeaderFirst());
        outputHeaderFooter(headerFooter.getFooterFirst());
    }

    private void outputHeaderFooter(XSSFBHeaderFooter headerFooter) {
        String text = headerFooter.getString();
        if (text != null && text.trim().length() > 0) {
            this.handler.headerFooter(text, headerFooter.isHeader(), headerFooter.getHeaderFooterTypeLabel());
        }
    }

    private void checkMissedComments(int currentRow, int colNum) {
        XSSFBCommentsTable xSSFBCommentsTable = this.comments;
        if (xSSFBCommentsTable == null) {
            return;
        }
        Queue<CellAddress> queue = xSSFBCommentsTable.getAddresses();
        while (queue.size() > 0) {
            CellAddress cellAddress = queue.peek();
            if (cellAddress.getRow() == currentRow && cellAddress.getColumn() < colNum) {
                CellAddress cellAddress2 = queue.remove();
                dumpEmptyCellComment(cellAddress2, this.comments.get(cellAddress2));
            } else if (cellAddress.getRow() == currentRow && cellAddress.getColumn() == colNum) {
                queue.remove();
                return;
            } else if ((cellAddress.getRow() == currentRow && cellAddress.getColumn() > colNum) || cellAddress.getRow() > currentRow) {
                return;
            }
        }
    }

    private void checkMissedComments(int currentRow) {
        XSSFBCommentsTable xSSFBCommentsTable = this.comments;
        if (xSSFBCommentsTable == null) {
            return;
        }
        Queue<CellAddress> queue = xSSFBCommentsTable.getAddresses();
        int lastInterpolatedRow = -1;
        while (queue.size() > 0) {
            CellAddress cellAddress = queue.peek();
            if (currentRow == -1 || cellAddress.getRow() < currentRow) {
                CellAddress cellAddress2 = queue.remove();
                CellAddress cellAddress3 = cellAddress2;
                if (cellAddress3.getRow() != lastInterpolatedRow) {
                    startRow(cellAddress3.getRow());
                }
                dumpEmptyCellComment(cellAddress3, this.comments.get(cellAddress3));
                lastInterpolatedRow = cellAddress3.getRow();
            } else {
                return;
            }
        }
    }

    private void startRow(int row) {
        int i = this.lastStartedRow;
        if (row == i) {
            return;
        }
        if (i != this.lastEndedRow) {
            endRow(i);
        }
        this.handler.startRow(row);
        this.lastStartedRow = row;
    }

    private void endRow(int row) {
        if (this.lastEndedRow == row) {
            return;
        }
        this.handler.endRow(row);
        this.lastEndedRow = row;
    }

    private void dumpEmptyCellComment(CellAddress cellAddress, XSSFBComment comment) {
        this.handler.cell(cellAddress.formatAsString(), null, comment);
    }

    private double rkNumber(byte[] data, int offset) {
        double d;
        byte b0 = data[offset];
        Integer.toString(b0, 2);
        boolean numDivBy100 = (b0 & 1) == 1;
        boolean floatingPoint = ((b0 >> 1) & 1) == 0;
        this.rkBuffer[4] = (byte) (((byte) (b0 & (-2))) & (-3));
        for (int i = 1; i < 4; i++) {
            this.rkBuffer[i + 4] = data[offset + i];
        }
        if (floatingPoint) {
            d = LittleEndian.getDouble(this.rkBuffer);
        } else {
            d = LittleEndian.getInt(this.rkBuffer);
        }
        return numDivBy100 ? d / 100.0d : d;
    }
}
