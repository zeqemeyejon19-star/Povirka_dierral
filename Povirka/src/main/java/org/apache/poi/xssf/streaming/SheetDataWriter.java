package org.apache.poi.xssf.streaming;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

/* JADX INFO: loaded from: classes.dex */
public class SheetDataWriter implements Closeable {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SheetDataWriter.class);
    private final File _fd;
    private int _lowestIndexOfFlushedRows;
    private int _numberLastFlushedRow;
    private int _numberOfCellsOfLastFlushedRow;
    private int _numberOfFlushedRows;
    private final Writer _out;
    private int _rownum;
    private SharedStringsTable _sharedStringSource;

    public SheetDataWriter() throws IOException {
        this._numberLastFlushedRow = -1;
        File fileCreateTempFile = createTempFile();
        this._fd = fileCreateTempFile;
        this._out = createWriter(fileCreateTempFile);
    }

    public SheetDataWriter(SharedStringsTable sharedStringsTable) throws IOException {
        this();
        this._sharedStringSource = sharedStringsTable;
    }

    public File createTempFile() throws IOException {
        return TempFile.createTempFile("poi-sxssf-sheet", ".xml");
    }

    public Writer createWriter(File fd) throws IOException {
        FileOutputStream fos = new FileOutputStream(fd);
        try {
            OutputStream decorated = decorateOutputStream(fos);
            return new BufferedWriter(new OutputStreamWriter(decorated, "UTF-8"));
        } catch (IOException e) {
            fos.close();
            throw e;
        }
    }

    protected OutputStream decorateOutputStream(FileOutputStream fos) throws IOException {
        return fos;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this._out.flush();
        this._out.close();
    }

    protected File getTempFile() {
        return this._fd;
    }

    public InputStream getWorksheetXMLInputStream() throws IOException {
        File fd = getTempFile();
        FileInputStream fis = new FileInputStream(fd);
        try {
            return decorateInputStream(fis);
        } catch (IOException e) {
            fis.close();
            throw e;
        }
    }

    protected InputStream decorateInputStream(FileInputStream fis) throws IOException {
        return fis;
    }

    public int getNumberOfFlushedRows() {
        return this._numberOfFlushedRows;
    }

    public int getNumberOfCellsOfLastFlushedRow() {
        return this._numberOfCellsOfLastFlushedRow;
    }

    public int getLowestIndexOfFlushedRows() {
        return this._lowestIndexOfFlushedRows;
    }

    public int getLastFlushedRow() {
        return this._numberLastFlushedRow;
    }

    protected void finalize() throws Throwable {
        if (!this._fd.delete()) {
            logger.log(7, "Can't delete temporary encryption file: " + this._fd);
        }
        super.finalize();
    }

    public void writeRow(int rownum, SXSSFRow row) throws IOException {
        if (this._numberOfFlushedRows == 0) {
            this._lowestIndexOfFlushedRows = rownum;
        }
        this._numberLastFlushedRow = Math.max(rownum, this._numberLastFlushedRow);
        this._numberOfCellsOfLastFlushedRow = row.getLastCellNum();
        this._numberOfFlushedRows++;
        beginRow(rownum, row);
        Iterator<Cell> cells = row.allCellsIterator();
        int columnIndex = 0;
        while (cells.hasNext()) {
            writeCell(columnIndex, cells.next());
            columnIndex++;
        }
        endRow();
    }

    void beginRow(int rownum, SXSSFRow row) throws IOException {
        this._out.write("<row");
        writeAttribute("r", Integer.toString(rownum + 1));
        if (row.hasCustomHeight()) {
            writeAttribute("customHeight", "true");
            writeAttribute("ht", Float.toString(row.getHeightInPoints()));
        }
        if (row.getZeroHeight()) {
            writeAttribute(CellUtil.HIDDEN, "true");
        }
        if (row.isFormatted()) {
            writeAttribute("s", Integer.toString(row.getRowStyleIndex()));
            writeAttribute("customFormat", "1");
        }
        if (row.getOutlineLevel() != 0) {
            writeAttribute("outlineLevel", Integer.toString(row.getOutlineLevel()));
        }
        if (row.getHidden() != null) {
            writeAttribute(CellUtil.HIDDEN, row.getHidden().booleanValue() ? "1" : "0");
        }
        if (row.getCollapsed() != null) {
            writeAttribute("collapsed", row.getCollapsed().booleanValue() ? "1" : "0");
        }
        this._out.write(">\n");
        this._rownum = rownum;
    }

    void endRow() throws IOException {
        this._out.write("</row>\n");
    }

    public void writeCell(int columnIndex, Cell cell) throws IOException {
        if (cell == null) {
            return;
        }
        String ref = new CellReference(this._rownum, columnIndex).formatAsString();
        this._out.write("<c");
        writeAttribute("r", ref);
        CellStyle cellStyle = cell.getCellStyle();
        if (cellStyle.getIndex() != 0) {
            writeAttribute("s", Integer.toString(cellStyle.getIndex() & 65535));
        }
        CellType cellType = cell.getCellTypeEnum();
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
                writeAttribute("t", "n");
                this._out.write("><v>");
                this._out.write(Double.toString(cell.getNumericCellValue()));
                this._out.write("</v>");
                break;
            case 2:
                this._out.write(62);
                break;
            case 3:
                this._out.write("><f>");
                outputQuotedString(cell.getCellFormula());
                this._out.write("</f>");
                if (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cell.getCachedFormulaResultTypeEnum().ordinal()] == 1) {
                    double nval = cell.getNumericCellValue();
                    if (!Double.isNaN(nval)) {
                        this._out.write("<v>");
                        this._out.write(Double.toString(nval));
                        this._out.write("</v>");
                    }
                }
                break;
            case 4:
                if (this._sharedStringSource != null) {
                    XSSFRichTextString rt = new XSSFRichTextString(cell.getStringCellValue());
                    int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                    writeAttribute("t", STCellType.S.toString());
                    this._out.write("><v>");
                    this._out.write(String.valueOf(sRef));
                    this._out.write("</v>");
                } else {
                    writeAttribute("t", "inlineStr");
                    this._out.write("><is><t");
                    if (hasLeadingTrailingSpaces(cell.getStringCellValue())) {
                        writeAttribute("xml:space", "preserve");
                    }
                    this._out.write(">");
                    outputQuotedString(cell.getStringCellValue());
                    this._out.write("</t></is>");
                }
                break;
            case 5:
                writeAttribute("t", "b");
                this._out.write("><v>");
                this._out.write(cell.getBooleanCellValue() ? "1" : "0");
                this._out.write("</v>");
                break;
            case 6:
                FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
                writeAttribute("t", "e");
                this._out.write("><v>");
                this._out.write(error.getString());
                this._out.write("</v>");
                break;
            default:
                throw new IllegalStateException("Invalid cell type: " + cellType);
        }
        this._out.write("</c>");
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.streaming.SheetDataWriter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.NUMERIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BLANK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.STRING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    private void writeAttribute(String name, String value) throws IOException {
        this._out.write(32);
        this._out.write(name);
        this._out.write("=\"");
        this._out.write(value);
        this._out.write(34);
    }

    boolean hasLeadingTrailingSpaces(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }
        char firstChar = str.charAt(0);
        char lastChar = str.charAt(str.length() - 1);
        return Character.isWhitespace(firstChar) || Character.isWhitespace(lastChar);
    }

    protected void outputQuotedString(String s) throws IOException {
        if (s == null || s.length() == 0) {
            return;
        }
        char[] chars = s.toCharArray();
        int last = 0;
        int length = s.length();
        for (int counter = 0; counter < length; counter++) {
            char c = chars[counter];
            if (c == '\t') {
                writeLastChars(this._out, chars, last, counter);
                this._out.write("&#x9;");
                last = counter + 1;
            } else if (c == '\n') {
                writeLastChars(this._out, chars, last, counter);
                this._out.write("&#xa;");
                last = counter + 1;
            } else if (c == '\r') {
                writeLastChars(this._out, chars, last, counter);
                this._out.write("&#xd;");
                last = counter + 1;
            } else if (c == '\"') {
                writeLastChars(this._out, chars, last, counter);
                last = counter + 1;
                this._out.write("&quot;");
            } else if (c == '&') {
                writeLastChars(this._out, chars, last, counter);
                last = counter + 1;
                this._out.write("&amp;");
            } else if (c == '<') {
                writeLastChars(this._out, chars, last, counter);
                last = counter + 1;
                this._out.write("&lt;");
            } else if (c == '>') {
                writeLastChars(this._out, chars, last, counter);
                last = counter + 1;
                this._out.write("&gt;");
            } else if (c == 160) {
                writeLastChars(this._out, chars, last, counter);
                this._out.write("&#xa0;");
                last = counter + 1;
            } else if (replaceWithQuestionMark(c)) {
                writeLastChars(this._out, chars, last, counter);
                this._out.write(63);
                last = counter + 1;
            } else if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
                writeLastChars(this._out, chars, last, counter);
                this._out.write(c);
                last = counter + 1;
            } else if (c > 127) {
                writeLastChars(this._out, chars, last, counter);
                last = counter + 1;
                this._out.write("&#");
                this._out.write(String.valueOf((int) c));
                this._out.write(";");
            }
        }
        if (last < length) {
            this._out.write(chars, last, length - last);
        }
    }

    private static void writeLastChars(Writer out, char[] chars, int last, int counter) throws IOException {
        if (counter > last) {
            out.write(chars, last, counter - last);
        }
    }

    static boolean replaceWithQuestionMark(char c) {
        return c < ' ' || (65534 <= c && c <= 65535);
    }

    boolean dispose() throws IOException {
        boolean ret;
        try {
            this._out.close();
            return ret;
        } finally {
            this._fd.delete();
        }
    }
}
