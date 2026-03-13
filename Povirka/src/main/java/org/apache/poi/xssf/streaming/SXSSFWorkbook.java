package org.apache.poi.xssf.streaming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFChartSheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* JADX INFO: loaded from: classes.dex */
public class SXSSFWorkbook implements Workbook {
    public static final int DEFAULT_WINDOW_SIZE = 100;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SXSSFWorkbook.class);
    private boolean _compressTmpFiles;
    private int _randomAccessWindowSize;
    private final SharedStringsTable _sharedStringSource;
    private final Map<SXSSFSheet, XSSFSheet> _sxFromXHash;
    private final XSSFWorkbook _wb;
    private final Map<XSSFSheet, SXSSFSheet> _xFromSxHash;

    public SXSSFWorkbook() {
        this((XSSFWorkbook) null);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook) {
        this(workbook, 100);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize) {
        this(workbook, rowAccessWindowSize, false);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize, boolean compressTmpFiles) {
        this(workbook, rowAccessWindowSize, compressTmpFiles, false);
    }

    public SXSSFWorkbook(XSSFWorkbook workbook, int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable) {
        this._sxFromXHash = new HashMap();
        this._xFromSxHash = new HashMap();
        this._randomAccessWindowSize = 100;
        setRandomAccessWindowSize(rowAccessWindowSize);
        setCompressTempFiles(compressTmpFiles);
        if (workbook == null) {
            XSSFWorkbook xSSFWorkbook = new XSSFWorkbook();
            this._wb = xSSFWorkbook;
            this._sharedStringSource = useSharedStringsTable ? xSSFWorkbook.getSharedStringSource() : null;
        } else {
            this._wb = workbook;
            this._sharedStringSource = useSharedStringsTable ? workbook.getSharedStringSource() : null;
            for (Sheet sheet : workbook) {
                createAndRegisterSXSSFSheet((XSSFSheet) sheet);
            }
        }
    }

    public SXSSFWorkbook(int rowAccessWindowSize) {
        this(null, rowAccessWindowSize);
    }

    public int getRandomAccessWindowSize() {
        return this._randomAccessWindowSize;
    }

    private void setRandomAccessWindowSize(int rowAccessWindowSize) {
        if (rowAccessWindowSize == 0 || rowAccessWindowSize < -1) {
            throw new IllegalArgumentException("rowAccessWindowSize must be greater than 0 or -1");
        }
        this._randomAccessWindowSize = rowAccessWindowSize;
    }

    public boolean isCompressTempFiles() {
        return this._compressTmpFiles;
    }

    public void setCompressTempFiles(boolean compress) {
        this._compressTmpFiles = compress;
    }

    @Internal
    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }

    protected SheetDataWriter createSheetDataWriter() throws IOException {
        if (this._compressTmpFiles) {
            return new GZIPSheetDataWriter(this._sharedStringSource);
        }
        return new SheetDataWriter(this._sharedStringSource);
    }

    XSSFSheet getXSSFSheet(SXSSFSheet sheet) {
        return this._sxFromXHash.get(sheet);
    }

    SXSSFSheet getSXSSFSheet(XSSFSheet sheet) {
        return this._xFromSxHash.get(sheet);
    }

    void registerSheetMapping(SXSSFSheet sxSheet, XSSFSheet xSheet) {
        this._sxFromXHash.put(sxSheet, xSheet);
        this._xFromSxHash.put(xSheet, sxSheet);
    }

    void deregisterSheetMapping(XSSFSheet xSheet) {
        SXSSFSheet sxSheet = getSXSSFSheet(xSheet);
        try {
            sxSheet.getSheetDataWriter().close();
        } catch (IOException e) {
        }
        this._sxFromXHash.remove(sxSheet);
        this._xFromSxHash.remove(xSheet);
    }

    private XSSFSheet getSheetFromZipEntryName(String sheetRef) {
        for (XSSFSheet sheet : this._sxFromXHash.values()) {
            if (sheetRef.equals(sheet.getPackagePart().getPartName().getName().substring(1))) {
                return sheet;
            }
        }
        return null;
    }

    protected void injectData(ZipEntrySource zipEntrySource, OutputStream out) throws IOException {
        try {
            ZipOutputStream zos = new ZipOutputStream(out);
            try {
                Enumeration<? extends ZipEntry> en = zipEntrySource.getEntries();
                while (en.hasMoreElements()) {
                    ZipEntry ze = en.nextElement();
                    zos.putNextEntry(new ZipEntry(ze.getName()));
                    InputStream is = zipEntrySource.getInputStream(ze);
                    XSSFSheet xSheet = getSheetFromZipEntryName(ze.getName());
                    if (xSheet != null && !(xSheet instanceof XSSFChartSheet)) {
                        SXSSFSheet sxSheet = getSXSSFSheet(xSheet);
                        InputStream xis = sxSheet.getWorksheetXMLInputStream();
                        try {
                            copyStreamAndInjectWorksheet(is, zos, xis);
                            xis.close();
                        } finally {
                        }
                    } else {
                        IOUtils.copy(is, zos);
                    }
                    is.close();
                }
            } finally {
                zos.close();
            }
        } finally {
            zipEntrySource.close();
        }
    }

    private static void copyStreamAndInjectWorksheet(InputStream in, OutputStream out, InputStream worksheetData) throws IOException {
        InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
        OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
        boolean needsStartTag = true;
        int pos = 0;
        String s = "<sheetData";
        int n = "<sheetData".length();
        while (true) {
            int c = inReader.read();
            if (c == -1) {
                break;
            }
            if (c == s.charAt(pos)) {
                pos++;
                if (pos != n) {
                    continue;
                } else {
                    if (!"<sheetData".equals(s)) {
                        break;
                    }
                    int c2 = inReader.read();
                    if (c2 == -1) {
                        outWriter.write(s);
                        break;
                    }
                    if (c2 == 62) {
                        outWriter.write(s);
                        outWriter.write(c2);
                        s = "</sheetData>";
                        n = "</sheetData>".length();
                        pos = 0;
                        needsStartTag = false;
                    } else if (c2 == 47) {
                        int c3 = inReader.read();
                        if (c3 == -1) {
                            outWriter.write(s);
                            break;
                        } else {
                            if (c3 == 62) {
                                break;
                            }
                            outWriter.write(s);
                            outWriter.write(47);
                            outWriter.write(c3);
                            pos = 0;
                        }
                    } else {
                        outWriter.write(s);
                        outWriter.write(47);
                        outWriter.write(c2);
                        pos = 0;
                    }
                }
            } else {
                if (pos > 0) {
                    outWriter.write(s, 0, pos);
                }
                if (c == s.charAt(0)) {
                    pos = 1;
                } else {
                    outWriter.write(c);
                    pos = 0;
                }
            }
        }
        outWriter.flush();
        if (needsStartTag) {
            outWriter.write("<sheetData>\n");
            outWriter.flush();
        }
        IOUtils.copy(worksheetData, out);
        outWriter.write("</sheetData>");
        outWriter.flush();
        while (true) {
            int c4 = inReader.read();
            if (c4 != -1) {
                outWriter.write(c4);
            } else {
                outWriter.flush();
                return;
            }
        }
    }

    public XSSFWorkbook getXSSFWorkbook() {
        return this._wb;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getActiveSheetIndex() {
        return this._wb.getActiveSheetIndex();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setActiveSheet(int sheetIndex) {
        this._wb.setActiveSheet(sheetIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getFirstVisibleTab() {
        return this._wb.getFirstVisibleTab();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setFirstVisibleTab(int sheetIndex) {
        this._wb.setFirstVisibleTab(sheetIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetOrder(String sheetname, int pos) {
        this._wb.setSheetOrder(sheetname, pos);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSelectedTab(int index) {
        this._wb.setSelectedTab(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetName(int sheet, String name) {
        this._wb.setSheetName(sheet, name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public String getSheetName(int sheet) {
        return this._wb.getSheetName(sheet);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getSheetIndex(String name) {
        return this._wb.getSheetIndex(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getSheetIndex(Sheet sheet) {
        return this._wb.getSheetIndex(getXSSFSheet((SXSSFSheet) sheet));
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SXSSFSheet createSheet() {
        return createAndRegisterSXSSFSheet(this._wb.createSheet());
    }

    SXSSFSheet createAndRegisterSXSSFSheet(XSSFSheet xSheet) {
        try {
            SXSSFSheet sxSheet = new SXSSFSheet(this, xSheet);
            registerSheetMapping(sxSheet, xSheet);
            return sxSheet;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SXSSFSheet createSheet(String sheetname) {
        return createAndRegisterSXSSFSheet(this._wb.createSheet(sheetname));
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @NotImplemented
    public Sheet cloneSheet(int sheetNum) {
        throw new RuntimeException("NotImplemented");
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumberOfSheets() {
        return this._wb.getNumberOfSheets();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator();
    }

    private final class SheetIterator<T extends Sheet> implements Iterator<T> {
        private final Iterator<XSSFSheet> it;

        public SheetIterator() {
            this.it = SXSSFWorkbook.this._wb.iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override // java.util.Iterator
        public T next() throws NoSuchElementException {
            XSSFSheet xssfSheet = this.it.next();
            return SXSSFWorkbook.this.getSXSSFSheet(xssfSheet);
        }

        @Override // java.util.Iterator
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on XSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Sheet> iterator() {
        return sheetIterator();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SXSSFSheet getSheetAt(int index) {
        return getSXSSFSheet(this._wb.getSheetAt(index));
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SXSSFSheet getSheet(String name) {
        return getSXSSFSheet(this._wb.getSheet(name));
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeSheetAt(int index) {
        XSSFSheet xSheet = this._wb.getSheetAt(index);
        SXSSFSheet sxSheet = getSXSSFSheet(xSheet);
        this._wb.removeSheetAt(index);
        deregisterSheetMapping(xSheet);
        try {
            sxSheet.dispose();
        } catch (IOException e) {
            logger.log(5, e);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Font createFont() {
        return this._wb.createFont();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Font findFont(boolean bold, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline) {
        return this._wb.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public short getNumberOfFonts() {
        return this._wb.getNumberOfFonts();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Font getFontAt(short idx) {
        return this._wb.getFontAt(idx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public CellStyle createCellStyle() {
        return this._wb.createCellStyle();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumCellStyles() {
        return this._wb.getNumCellStyles();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public CellStyle getCellStyleAt(int idx) {
        return this._wb.getCellStyleAt(idx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        for (SXSSFSheet sheet : this._xFromSxHash.values()) {
            try {
                sheet.getSheetDataWriter().close();
            } catch (IOException e) {
                logger.log(5, "An exception occurred while closing sheet data writer for sheet " + sheet.getSheetName() + ".", e);
            }
        }
        this._wb.close();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void write(OutputStream stream) throws IOException {
        boolean deleted;
        flushSheets();
        File tmplFile = TempFile.createTempFile("poi-sxssf-template", ".xlsx");
        try {
            FileOutputStream os = new FileOutputStream(tmplFile);
            try {
                this._wb.write(os);
                os.close();
                ZipEntrySource source = new ZipFileZipEntrySource(new ZipFile(tmplFile));
                injectData(source, stream);
                if (!deleted) {
                    throw new IOException("Could not delete temporary file after processing: " + tmplFile);
                }
            } catch (Throwable th) {
                os.close();
                throw th;
            }
        } finally {
            tmplFile.delete();
        }
    }

    protected void flushSheets() throws IOException {
        for (SXSSFSheet sheet : this._xFromSxHash.values()) {
            sheet.flushRows();
        }
    }

    public boolean dispose() {
        boolean success = true;
        for (SXSSFSheet sheet : this._sxFromXHash.keySet()) {
            boolean z = false;
            try {
                if (sheet.dispose() && success) {
                    z = true;
                }
                success = z;
            } catch (IOException e) {
                logger.log(5, e);
                success = false;
            }
        }
        return success;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumberOfNames() {
        return this._wb.getNumberOfNames();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Name getName(String name) {
        return this._wb.getName(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<? extends Name> getNames(String name) {
        return this._wb.getNames(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<? extends Name> getAllNames() {
        return this._wb.getAllNames();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public Name getNameAt(int nameIndex) {
        return this._wb.getNameAt(nameIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Name createName() {
        return this._wb.createName();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public int getNameIndex(String name) {
        return this._wb.getNameIndex(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public void removeName(int index) {
        this._wb.removeName(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public void removeName(String name) {
        this._wb.removeName(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeName(Name name) {
        this._wb.removeName(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setPrintArea(int sheetIndex, String reference) {
        this._wb.setPrintArea(sheetIndex, reference);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
        this._wb.setPrintArea(sheetIndex, startColumn, endColumn, startRow, endRow);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public String getPrintArea(int sheetIndex) {
        return this._wb.getPrintArea(sheetIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removePrintArea(int sheetIndex) {
        this._wb.removePrintArea(sheetIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this._wb.getMissingCellPolicy();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy) {
        this._wb.setMissingCellPolicy(missingCellPolicy);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public DataFormat createDataFormat() {
        return this._wb.createDataFormat();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int addPicture(byte[] pictureData, int format) {
        return this._wb.addPicture(pictureData, format);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<? extends PictureData> getAllPictures() {
        return this._wb.getAllPictures();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public CreationHelper getCreationHelper() {
        return new SXSSFCreationHelper(this);
    }

    protected boolean isDate1904() {
        return this._wb.isDate1904();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @NotImplemented("XSSFWorkbook#isHidden is not implemented")
    public boolean isHidden() {
        return this._wb.isHidden();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @NotImplemented("XSSFWorkbook#setHidden is not implemented")
    public void setHidden(boolean hiddenFlag) {
        this._wb.setHidden(hiddenFlag);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean isSheetHidden(int sheetIx) {
        return this._wb.isSheetHidden(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean isSheetVeryHidden(int sheetIx) {
        return this._wb.isSheetVeryHidden(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SheetVisibility getSheetVisibility(int sheetIx) {
        return this._wb.getSheetVisibility(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetHidden(int sheetIx, boolean hidden) {
        this._wb.setSheetHidden(sheetIx, hidden);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public void setSheetHidden(int sheetIx, int hidden) {
        this._wb.setSheetHidden(sheetIx, hidden);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetVisibility(int sheetIx, SheetVisibility visibility) {
        this._wb.setSheetVisibility(sheetIx, visibility);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @NotImplemented
    public int linkExternalWorkbook(String name, Workbook workbook) {
        throw new RuntimeException("NotImplemented");
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void addToolPack(UDFFinder toopack) {
        this._wb.addToolPack(toopack);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setForceFormulaRecalculation(boolean value) {
        this._wb.setForceFormulaRecalculation(value);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean getForceFormulaRecalculation() {
        return this._wb.getForceFormulaRecalculation();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int addOlePackage(byte[] oleData, String label, String fileName, String command) throws IOException {
        return this._wb.addOlePackage(oleData, label, fileName, command);
    }
}
