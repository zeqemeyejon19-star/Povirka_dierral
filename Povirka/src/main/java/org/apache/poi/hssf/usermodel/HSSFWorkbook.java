package org.apache.poi.hssf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIDocument;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.ddf.EscherBitmapBlip;
import org.apache.poi.ddf.EscherBlipRecord;
import org.apache.poi.ddf.EscherMetafileBlip;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.model.WorkbookRecordList;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.crypto.Biff8DecryptingStream;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.FilteringDirectoryNode;
import org.apache.poi.poifs.filesystem.NPOIFSDocument;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.Ole10Native;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Configurator;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFWorkbook extends POIDocument implements Workbook {
    private static final int DEBUG = 1;
    private static final int MAX_STYLES = 4030;
    protected List<HSSFSheet> _sheets;
    private UDFFinder _udfFinder;
    private Map<Short, HSSFFont> fonts;
    private HSSFDataFormat formatter;
    private Row.MissingCellPolicy missingCellPolicy;
    private ArrayList<HSSFName> names;
    private boolean preserveNodes;
    private InternalWorkbook workbook;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    public static final int INITIAL_CAPACITY = Configurator.getIntValue("HSSFWorkbook.SheetInitialCapacity", 3);
    private static final POILogger log = POILogFactory.getLogger((Class<?>) HSSFWorkbook.class);

    public static HSSFWorkbook create(InternalWorkbook book) {
        return new HSSFWorkbook(book);
    }

    public HSSFWorkbook() {
        this(InternalWorkbook.createWorkbook());
    }

    private HSSFWorkbook(InternalWorkbook book) {
        super((DirectoryNode) null);
        this.missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(AggregatingUDFFinder.DEFAULT);
        this.workbook = book;
        int i = INITIAL_CAPACITY;
        this._sheets = new ArrayList(i);
        this.names = new ArrayList<>(i);
    }

    public HSSFWorkbook(POIFSFileSystem fs) throws IOException {
        this(fs, true);
    }

    public HSSFWorkbook(NPOIFSFileSystem fs) throws IOException {
        this(fs.getRoot(), true);
    }

    public HSSFWorkbook(POIFSFileSystem fs, boolean preserveNodes) throws IOException {
        this(fs.getRoot(), fs, preserveNodes);
    }

    public static String getWorkbookDirEntryName(DirectoryNode directory) {
        String[] arr$ = InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES;
        for (String wbName : arr$) {
            try {
                directory.getEntry(wbName);
                return wbName;
            } catch (FileNotFoundException e) {
            }
        }
        try {
            directory.getEntry(Decryptor.DEFAULT_POIFS_ENTRY);
            throw new EncryptedDocumentException("The supplied spreadsheet seems to be an Encrypted .xlsx file. It must be decrypted before use by XSSF, it cannot be used by HSSF");
        } catch (FileNotFoundException e2) {
            try {
                directory.getEntry(InternalWorkbook.OLD_WORKBOOK_DIR_ENTRY_NAME);
                throw new OldExcelFormatException("The supplied spreadsheet seems to be Excel 5.0/7.0 (BIFF5) format. POI only supports BIFF8 format (from Excel versions 97/2000/XP/2003)");
            } catch (FileNotFoundException e3) {
                throw new IllegalArgumentException("The supplied POIFSFileSystem does not contain a BIFF8 'Workbook' entry. Is it really an excel file? Had: " + directory.getEntryNames());
            }
        }
    }

    public HSSFWorkbook(DirectoryNode directory, POIFSFileSystem fs, boolean preserveNodes) throws IOException {
        this(directory, preserveNodes);
    }

    public HSSFWorkbook(DirectoryNode directory, boolean preserveNodes) throws IOException {
        super(directory);
        this.missingCellPolicy = Row.MissingCellPolicy.RETURN_NULL_AND_BLANK;
        this._udfFinder = new IndexedUDFFinder(AggregatingUDFFinder.DEFAULT);
        String workbookName = getWorkbookDirEntryName(directory);
        this.preserveNodes = preserveNodes;
        if (!preserveNodes) {
            clearDirectory();
        }
        int i = INITIAL_CAPACITY;
        this._sheets = new ArrayList(i);
        this.names = new ArrayList<>(i);
        InputStream stream = directory.createDocumentInputStream(workbookName);
        List<Record> records = RecordFactory.createRecords(stream);
        InternalWorkbook internalWorkbookCreateWorkbook = InternalWorkbook.createWorkbook(records);
        this.workbook = internalWorkbookCreateWorkbook;
        setPropertiesFromWorkbook(internalWorkbookCreateWorkbook);
        int recOffset = this.workbook.getNumRecords();
        convertLabelRecords(records, recOffset);
        RecordStream rs = new RecordStream(records, recOffset);
        while (rs.hasNext()) {
            try {
                InternalSheet sheet = InternalSheet.createSheet(rs);
                this._sheets.add(new HSSFSheet(this, sheet));
            } catch (InternalSheet.UnsupportedBOFType eb) {
                log.log(5, "Unsupported BOF found of type " + eb.getType());
            }
        }
        for (int i2 = 0; i2 < this.workbook.getNumNames(); i2++) {
            NameRecord nameRecord = this.workbook.getNameRecord(i2);
            HSSFName name = new HSSFName(this, nameRecord, this.workbook.getNameCommentRecord(nameRecord));
            this.names.add(name);
        }
    }

    public HSSFWorkbook(InputStream s) throws IOException {
        this(s, true);
    }

    public HSSFWorkbook(InputStream s, boolean preserveNodes) throws IOException {
        this(new NPOIFSFileSystem(s).getRoot(), preserveNodes);
    }

    private void setPropertiesFromWorkbook(InternalWorkbook book) {
        this.workbook = book;
    }

    private void convertLabelRecords(List<Record> records, int offset) {
        POILogger pOILogger = log;
        if (pOILogger.check(1)) {
            pOILogger.log(1, "convertLabelRecords called");
        }
        for (int k = offset; k < records.size(); k++) {
            Record rec = records.get(k);
            if (rec.getSid() == 516) {
                LabelRecord oldrec = (LabelRecord) rec;
                records.remove(k);
                LabelSSTRecord newrec = new LabelSSTRecord();
                int stringid = this.workbook.addSSTString(new UnicodeString(oldrec.getValue()));
                newrec.setRow(oldrec.getRow());
                newrec.setColumn(oldrec.getColumn());
                newrec.setXFIndex(oldrec.getXFIndex());
                newrec.setSSTIndex(stringid);
                records.add(k, newrec);
            }
        }
        POILogger pOILogger2 = log;
        if (pOILogger2.check(1)) {
            pOILogger2.log(1, "convertLabelRecords exit");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return this.missingCellPolicy;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy) {
        this.missingCellPolicy = missingCellPolicy;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetOrder(String sheetname, int pos) {
        int oldSheetIndex = getSheetIndex(sheetname);
        List<HSSFSheet> list = this._sheets;
        list.add(pos, list.remove(oldSheetIndex));
        this.workbook.setSheetOrder(sheetname, pos);
        FormulaShifter shifter = FormulaShifter.createForSheetShift(oldSheetIndex, pos);
        for (HSSFSheet sheet : this._sheets) {
            sheet.getSheet().updateFormulasAfterCellShift(shifter, -1);
        }
        this.workbook.updateNamesAfterCellShift(shifter);
        updateNamedRangesAfterSheetReorder(oldSheetIndex, pos);
        updateActiveSheetAfterSheetReorder(oldSheetIndex, pos);
    }

    private void updateNamedRangesAfterSheetReorder(int oldIndex, int newIndex) {
        for (HSSFName name : this.names) {
            int i = name.getSheetIndex();
            if (i != -1) {
                if (i == oldIndex) {
                    name.setSheetIndex(newIndex);
                } else if (newIndex <= i && i < oldIndex) {
                    name.setSheetIndex(i + 1);
                } else if (oldIndex < i && i <= newIndex) {
                    name.setSheetIndex(i - 1);
                }
            }
        }
    }

    private void updateActiveSheetAfterSheetReorder(int oldIndex, int newIndex) {
        int active = getActiveSheetIndex();
        if (active == oldIndex) {
            setActiveSheet(newIndex);
            return;
        }
        if (active >= oldIndex || active >= newIndex) {
            if (active <= oldIndex || active <= newIndex) {
                if (newIndex > oldIndex) {
                    setActiveSheet(active - 1);
                } else {
                    setActiveSheet(active + 1);
                }
            }
        }
    }

    private void validateSheetIndex(int index) {
        int lastSheetIx = this._sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            String range = "(0.." + lastSheetIx + ")";
            if (lastSheetIx == -1) {
                range = "(no sheets)";
            }
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range " + range);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSelectedTab(int index) {
        validateSheetIndex(index);
        int nSheets = this._sheets.size();
        int i = 0;
        while (true) {
            boolean z = true;
            if (i < nSheets) {
                HSSFSheet sheetAt = getSheetAt(i);
                if (i != index) {
                    z = false;
                }
                sheetAt.setSelected(z);
                i++;
            } else {
                this.workbook.getWindowOne().setNumSelectedTabs((short) 1);
                return;
            }
        }
    }

    public void setSelectedTabs(int[] indexes) {
        Collection<Integer> list = new ArrayList<>(indexes.length);
        for (int index : indexes) {
            list.add(Integer.valueOf(index));
        }
        setSelectedTabs(list);
    }

    public void setSelectedTabs(Collection<Integer> indexes) {
        Iterator<Integer> it = indexes.iterator();
        while (it.hasNext()) {
            int index = it.next().intValue();
            validateSheetIndex(index);
        }
        Set<Integer> set = new HashSet<>(indexes);
        int nSheets = this._sheets.size();
        for (int i = 0; i < nSheets; i++) {
            boolean bSelect = set.contains(Integer.valueOf(i));
            getSheetAt(i).setSelected(bSelect);
        }
        int i2 = set.size();
        short nSelected = (short) i2;
        this.workbook.getWindowOne().setNumSelectedTabs(nSelected);
    }

    public Collection<Integer> getSelectedTabs() {
        Collection<Integer> indexes = new ArrayList<>();
        int nSheets = this._sheets.size();
        for (int i = 0; i < nSheets; i++) {
            HSSFSheet sheet = getSheetAt(i);
            if (sheet.isSelected()) {
                indexes.add(Integer.valueOf(i));
            }
        }
        return Collections.unmodifiableCollection(indexes);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setActiveSheet(int index) {
        validateSheetIndex(index);
        int nSheets = this._sheets.size();
        int i = 0;
        while (i < nSheets) {
            getSheetAt(i).setActive(i == index);
            i++;
        }
        this.workbook.getWindowOne().setActiveSheetIndex(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getActiveSheetIndex() {
        return this.workbook.getWindowOne().getActiveSheetIndex();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setFirstVisibleTab(int index) {
        this.workbook.getWindowOne().setFirstVisibleTab(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getFirstVisibleTab() {
        return this.workbook.getWindowOne().getFirstVisibleTab();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetName(int sheetIx, String name) {
        if (name == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(name, sheetIx)) {
            throw new IllegalArgumentException("The workbook already contains a sheet named '" + name + "'");
        }
        validateSheetIndex(sheetIx);
        this.workbook.setSheetName(sheetIx, name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public String getSheetName(int sheetIndex) {
        validateSheetIndex(sheetIndex);
        return this.workbook.getSheetName(sheetIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean isHidden() {
        return this.workbook.getWindowOne().getHidden();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setHidden(boolean hiddenFlag) {
        this.workbook.getWindowOne().setHidden(hiddenFlag);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean isSheetHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return this.workbook.isSheetHidden(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean isSheetVeryHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return this.workbook.isSheetVeryHidden(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SheetVisibility getSheetVisibility(int sheetIx) {
        return this.workbook.getSheetVisibility(sheetIx);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetHidden(int sheetIx, boolean hidden) {
        setSheetVisibility(sheetIx, hidden ? SheetVisibility.HIDDEN : SheetVisibility.VISIBLE);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    @Removal(version = "3.18")
    @Deprecated
    public void setSheetHidden(int sheetIx, int hidden) {
        if (hidden == 0) {
            setSheetVisibility(sheetIx, SheetVisibility.VISIBLE);
        } else if (hidden == 1) {
            setSheetVisibility(sheetIx, SheetVisibility.HIDDEN);
        } else {
            if (hidden == 2) {
                setSheetVisibility(sheetIx, SheetVisibility.VERY_HIDDEN);
                return;
            }
            throw new IllegalArgumentException("Invalid sheet state : " + hidden + "\nSheet state must beone of the Workbook.SHEET_STATE_* constants");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setSheetVisibility(int sheetIx, SheetVisibility visibility) {
        validateSheetIndex(sheetIx);
        this.workbook.setSheetHidden(sheetIx, visibility);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getSheetIndex(String name) {
        return this.workbook.getSheetIndex(name);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getSheetIndex(Sheet sheet) {
        return this._sheets.indexOf(sheet);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFSheet createSheet() {
        HSSFSheet sheet = new HSSFSheet(this);
        this._sheets.add(sheet);
        this.workbook.setSheetName(this._sheets.size() - 1, "Sheet" + (this._sheets.size() - 1));
        boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFSheet cloneSheet(int sheetIndex) {
        validateSheetIndex(sheetIndex);
        HSSFSheet srcSheet = this._sheets.get(sheetIndex);
        String srcName = this.workbook.getSheetName(sheetIndex);
        HSSFSheet clonedSheet = srcSheet.cloneSheet(this);
        clonedSheet.setSelected(false);
        clonedSheet.setActive(false);
        String name = getUniqueSheetName(srcName);
        int newSheetIndex = this._sheets.size();
        this._sheets.add(clonedSheet);
        this.workbook.setSheetName(newSheetIndex, name);
        int filterDbNameIndex = findExistingBuiltinNameRecordIdx(sheetIndex, (byte) 13);
        if (filterDbNameIndex != -1) {
            NameRecord newNameRecord = this.workbook.cloneFilter(filterDbNameIndex, newSheetIndex);
            HSSFName newName = new HSSFName(this, newNameRecord);
            this.names.add(newName);
        }
        return clonedSheet;
    }

    private String getUniqueSheetName(String srcName) {
        String name;
        int uniqueIndex = 2;
        String baseName = srcName;
        int bracketPos = srcName.lastIndexOf(40);
        if (bracketPos > 0 && srcName.endsWith(")")) {
            String suffix = srcName.substring(bracketPos + 1, srcName.length() - ")".length());
            try {
                uniqueIndex = Integer.parseInt(suffix.trim()) + 1;
                baseName = srcName.substring(0, bracketPos).trim();
            } catch (NumberFormatException e) {
            }
        }
        while (true) {
            int uniqueIndex2 = uniqueIndex + 1;
            String index = Integer.toString(uniqueIndex);
            if (baseName.length() + index.length() + 2 >= 31) {
                name = baseName.substring(0, (31 - index.length()) - 2) + "(" + index + ")";
            } else {
                name = baseName + " (" + index + ")";
            }
            if (this.workbook.getSheetIndex(name) == -1) {
                return name;
            }
            uniqueIndex = uniqueIndex2;
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFSheet createSheet(String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        if (this.workbook.doesContainsSheetName(sheetname, this._sheets.size())) {
            throw new IllegalArgumentException("The workbook already contains a sheet named '" + sheetname + "'");
        }
        HSSFSheet sheet = new HSSFSheet(this);
        this.workbook.setSheetName(this._sheets.size(), sheetname);
        this._sheets.add(sheet);
        boolean isOnlySheet = this._sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public Iterator<Sheet> sheetIterator() {
        return new SheetIterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Sheet> iterator() {
        return sheetIterator();
    }

    private final class SheetIterator<T extends Sheet> implements Iterator<T> {
        private T cursor = null;
        private final Iterator<T> it;

        public SheetIterator() {
            this.it = HSSFWorkbook.this._sheets.iterator();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override // java.util.Iterator
        public T next() throws NoSuchElementException {
            T next = this.it.next();
            this.cursor = next;
            return next;
        }

        @Override // java.util.Iterator
        public void remove() throws IllegalStateException {
            throw new UnsupportedOperationException("remove method not supported on HSSFWorkbook.iterator(). Use Sheet.removeSheetAt(int) instead.");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumberOfSheets() {
        return this._sheets.size();
    }

    private HSSFSheet[] getSheets() {
        HSSFSheet[] result = new HSSFSheet[this._sheets.size()];
        this._sheets.toArray(result);
        return result;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFSheet getSheetAt(int index) {
        validateSheetIndex(index);
        return this._sheets.get(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFSheet getSheet(String name) {
        HSSFSheet retval = null;
        for (int k = 0; k < this._sheets.size(); k++) {
            String sheetname = this.workbook.getSheetName(k);
            if (sheetname.equalsIgnoreCase(name)) {
                HSSFSheet retval2 = this._sheets.get(k);
                retval = retval2;
            }
        }
        return retval;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeSheetAt(int index) {
        validateSheetIndex(index);
        boolean wasSelected = getSheetAt(index).isSelected();
        this._sheets.remove(index);
        this.workbook.removeSheet(index);
        int nSheets = this._sheets.size();
        if (nSheets < 1) {
            return;
        }
        int newSheetIndex = index;
        if (newSheetIndex >= nSheets) {
            newSheetIndex = nSheets - 1;
        }
        if (wasSelected) {
            boolean someOtherSheetIsStillSelected = false;
            int i = 0;
            while (true) {
                if (i >= nSheets) {
                    break;
                }
                if (!getSheetAt(i).isSelected()) {
                    i++;
                } else {
                    someOtherSheetIsStillSelected = true;
                    break;
                }
            }
            if (!someOtherSheetIsStillSelected) {
                setSelectedTab(newSheetIndex);
            }
        }
        int active = getActiveSheetIndex();
        if (active == index) {
            setActiveSheet(newSheetIndex);
        } else if (active > index) {
            setActiveSheet(active - 1);
        }
    }

    public void setBackupFlag(boolean z) {
        this.workbook.getBackupRecord().setBackup(z ? (short) 1 : (short) 0);
    }

    public boolean getBackupFlag() {
        BackupRecord backupRecord = this.workbook.getBackupRecord();
        return backupRecord.getBackup() != 0;
    }

    int findExistingBuiltinNameRecordIdx(int sheetIndex, byte builtinCode) {
        for (int defNameIndex = 0; defNameIndex < this.names.size(); defNameIndex++) {
            NameRecord r = this.workbook.getNameRecord(defNameIndex);
            if (r == null) {
                throw new RuntimeException("Unable to find all defined names to iterate over");
            }
            if (r.isBuiltInName() && r.getBuiltInName() == builtinCode && r.getSheetNumber() - 1 == sheetIndex) {
                return defNameIndex;
            }
        }
        return -1;
    }

    HSSFName createBuiltInName(byte builtinCode, int sheetIndex) {
        NameRecord nameRecord = this.workbook.createBuiltInName(builtinCode, sheetIndex + 1);
        HSSFName newName = new HSSFName(this, nameRecord, null);
        this.names.add(newName);
        return newName;
    }

    HSSFName getBuiltInName(byte builtinCode, int sheetIndex) {
        int index = findExistingBuiltinNameRecordIdx(sheetIndex, builtinCode);
        if (index < 0) {
            return null;
        }
        return this.names.get(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFFont createFont() {
        this.workbook.createNewFont();
        short fontindex = (short) (getNumberOfFonts() - 1);
        if (fontindex > 3) {
            fontindex = (short) (fontindex + 1);
        }
        if (fontindex == Short.MAX_VALUE) {
            throw new IllegalArgumentException("Maximum number of fonts was exceeded");
        }
        return getFontAt(fontindex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFFont findFont(boolean bold, short color, short fontHeight, String name, boolean italic, boolean strikeout, short typeOffset, byte underline) {
        short numberOfFonts = getNumberOfFonts();
        for (short i = 0; i <= numberOfFonts; i = (short) (i + 1)) {
            if (i != 4) {
                HSSFFont hssfFont = getFontAt(i);
                if (hssfFont.getBold() == bold && hssfFont.getColor() == color && hssfFont.getFontHeight() == fontHeight && hssfFont.getFontName().equals(name) && hssfFont.getItalic() == italic && hssfFont.getStrikeout() == strikeout && hssfFont.getTypeOffset() == typeOffset && hssfFont.getUnderline() == underline) {
                    return hssfFont;
                }
            }
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public short getNumberOfFonts() {
        return (short) this.workbook.getNumberOfFontRecords();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFFont getFontAt(short idx) {
        if (this.fonts == null) {
            this.fonts = new HashMap();
        }
        Short sIdx = Short.valueOf(idx);
        if (this.fonts.containsKey(sIdx)) {
            return this.fonts.get(sIdx);
        }
        FontRecord font = this.workbook.getFontRecordAt(idx);
        HSSFFont retval = new HSSFFont(idx, font);
        this.fonts.put(sIdx, retval);
        return retval;
    }

    protected void resetFontCache() {
        this.fonts = new HashMap();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFCellStyle createCellStyle() {
        if (this.workbook.getNumExFormats() == MAX_STYLES) {
            throw new IllegalStateException("The maximum number of cell styles was exceeded. You can define up to 4000 styles in a .xls workbook");
        }
        ExtendedFormatRecord xfr = this.workbook.createCellXF();
        short index = (short) (getNumCellStyles() - 1);
        return new HSSFCellStyle(index, xfr, this);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumCellStyles() {
        return this.workbook.getNumExFormats();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFCellStyle getCellStyleAt(int idx) {
        ExtendedFormatRecord xfr = this.workbook.getExFormatAt(idx);
        return new HSSFCellStyle((short) idx, xfr, this);
    }

    @Override // org.apache.poi.POIDocument, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }

    @Override // org.apache.poi.POIDocument
    public void write() throws IOException {
        validateInPlaceWritePossible();
        DirectoryNode dir = getDirectory();
        DocumentNode workbookNode = (DocumentNode) dir.getEntry(getWorkbookDirEntryName(dir));
        NPOIFSDocument workbookDoc = new NPOIFSDocument(workbookNode);
        workbookDoc.replaceContents(new ByteArrayInputStream(getBytes()));
        writeProperties();
        dir.getFileSystem().writeFilesystem();
    }

    @Override // org.apache.poi.POIDocument
    public void write(File newFile) throws IOException {
        POIFSFileSystem fs = POIFSFileSystem.create(newFile);
        try {
            write(fs);
            fs.writeFilesystem();
        } finally {
            fs.close();
        }
    }

    @Override // org.apache.poi.POIDocument
    public void write(OutputStream stream) throws IOException {
        NPOIFSFileSystem fs = new NPOIFSFileSystem();
        try {
            write(fs);
            fs.writeFilesystem(stream);
        } finally {
            fs.close();
        }
    }

    private void write(NPOIFSFileSystem fs) throws IOException {
        List<String> excepts = new ArrayList<>(1);
        fs.createDocument(new ByteArrayInputStream(getBytes()), "Workbook");
        writeProperties(fs, excepts);
        if (this.preserveNodes) {
            excepts.addAll(Arrays.asList(InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES));
            excepts.addAll(Arrays.asList(DocumentSummaryInformation.DEFAULT_STREAM_NAME, SummaryInformation.DEFAULT_STREAM_NAME, getEncryptedPropertyStreamName()));
            EntryUtils.copyNodes(new FilteringDirectoryNode(getDirectory(), excepts), new FilteringDirectoryNode(fs.getRoot(), excepts));
            fs.getRoot().setStorageClsid(getDirectory().getStorageClsid());
        }
    }

    private static final class SheetRecordCollector implements RecordAggregate.RecordVisitor {
        private int _totalSize = 0;
        private List<Record> _list = new ArrayList(128);

        public int getTotalSize() {
            return this._totalSize;
        }

        @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor
        public void visitRecord(Record r) {
            this._list.add(r);
            this._totalSize += r.getRecordSize();
        }

        public int serialize(int offset, byte[] data) {
            int result = 0;
            for (Record rec : this._list) {
                result += rec.serialize(offset + result, data);
            }
            return result;
        }
    }

    public byte[] getBytes() {
        POILogger pOILogger = log;
        if (pOILogger.check(1)) {
            pOILogger.log(1, "HSSFWorkbook.getBytes()");
        }
        HSSFSheet[] sheets = getSheets();
        int nSheets = sheets.length;
        updateEncryptionInfo();
        this.workbook.preSerialize();
        for (HSSFSheet sheet : sheets) {
            sheet.getSheet().preSerialize();
            sheet.preSerialize();
        }
        int totalsize = this.workbook.getSize();
        SheetRecordCollector[] srCollectors = new SheetRecordCollector[nSheets];
        for (int k = 0; k < nSheets; k++) {
            this.workbook.setSheetBof(k, totalsize);
            SheetRecordCollector src = new SheetRecordCollector();
            sheets[k].getSheet().visitContainedRecords(src, totalsize);
            totalsize += src.getTotalSize();
            srCollectors[k] = src;
        }
        byte[] retval = new byte[totalsize];
        int pos = this.workbook.serialize(0, retval);
        for (int k2 = 0; k2 < nSheets; k2++) {
            SheetRecordCollector src2 = srCollectors[k2];
            int serializedSize = src2.serialize(pos, retval);
            if (serializedSize != src2.getTotalSize()) {
                throw new IllegalStateException("Actual serialized sheet size (" + serializedSize + ") differs from pre-calculated size (" + src2.getTotalSize() + ") for sheet (" + k2 + ")");
            }
            pos += serializedSize;
        }
        encryptBytes(retval);
        return retval;
    }

    protected void encryptBytes(byte[] buf) {
        EncryptionInfo ei = getEncryptionInfo();
        if (ei == null) {
            return;
        }
        Encryptor enc = ei.getEncryptor();
        LittleEndianByteArrayInputStream plain = new LittleEndianByteArrayInputStream(buf, 0);
        LittleEndianByteArrayOutputStream leos = new LittleEndianByteArrayOutputStream(buf, 0);
        enc.setChunkSize(1024);
        byte[] tmp = new byte[1024];
        try {
            ChunkedCipherOutputStream os = enc.getDataStream(leos, 0);
            int totalBytes = 0;
            while (totalBytes < buf.length) {
                plain.read(tmp, 0, 4);
                int sid = LittleEndian.getUShort(tmp, 0);
                int len = LittleEndian.getUShort(tmp, 2);
                boolean isPlain = Biff8DecryptingStream.isNeverEncryptedRecord(sid);
                os.setNextRecordSize(len, isPlain);
                os.writePlain(tmp, 0, 4);
                if (sid == 133) {
                    byte[] bsrBuf = new byte[len];
                    plain.readFully(bsrBuf);
                    os.writePlain(bsrBuf, 0, 4);
                    os.write(bsrBuf, 4, len - 4);
                } else {
                    int todo = len;
                    while (todo > 0) {
                        int nextLen = Math.min(todo, tmp.length);
                        plain.readFully(tmp, 0, nextLen);
                        if (isPlain) {
                            os.writePlain(tmp, 0, nextLen);
                        } else {
                            os.write(tmp, 0, nextLen);
                        }
                        todo -= nextLen;
                    }
                }
                int todo2 = len + 4;
                totalBytes += todo2;
            }
            os.close();
        } catch (Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }

    InternalWorkbook getWorkbook() {
        return this.workbook;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNumberOfNames() {
        return this.names.size();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFName getName(String name) {
        int nameIndex = getNameIndex(name);
        if (nameIndex < 0) {
            return null;
        }
        return this.names.get(nameIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<HSSFName> getNames(String name) {
        List<HSSFName> nameList = new ArrayList<>();
        for (HSSFName nr : this.names) {
            if (nr.getNameName().equals(name)) {
                nameList.add(nr);
            }
        }
        return Collections.unmodifiableList(nameList);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFName getNameAt(int nameIndex) {
        int nNames = this.names.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        }
        if (nameIndex < 0 || nameIndex > nNames) {
            throw new IllegalArgumentException("Specified name index " + nameIndex + " is outside the allowable range (0.." + (nNames - 1) + ").");
        }
        return this.names.get(nameIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<HSSFName> getAllNames() {
        return Collections.unmodifiableList(this.names);
    }

    public NameRecord getNameRecord(int nameIndex) {
        return getWorkbook().getNameRecord(nameIndex);
    }

    public String getNameName(int index) {
        return getNameAt(index).getNameName();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setPrintArea(int sheetIndex, String reference) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte) 6, sheetIndex + 1);
        if (name == null) {
            name = this.workbook.createBuiltInName((byte) 6, sheetIndex + 1);
        }
        String[] parts = COMMA_PATTERN.split(reference);
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            SheetNameFormatter.appendFormat(sb, getSheetName(sheetIndex));
            sb.append("!");
            sb.append(parts[i]);
        }
        name.setNameDefinition(HSSFFormulaParser.parse(sb.toString(), this, FormulaType.NAMEDRANGE, sheetIndex));
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow, int endRow) {
        CellReference cell = new CellReference(startRow, startColumn, true, true);
        String reference = cell.formatAsString();
        CellReference cell2 = new CellReference(endRow, endColumn, true, true);
        setPrintArea(sheetIndex, reference + ":" + cell2.formatAsString());
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public String getPrintArea(int sheetIndex) {
        NameRecord name = this.workbook.getSpecificBuiltinRecord((byte) 6, sheetIndex + 1);
        if (name == null) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this, name.getNameDefinition());
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removePrintArea(int sheetIndex) {
        getWorkbook().removeBuiltinRecord((byte) 6, sheetIndex + 1);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFName createName() {
        NameRecord nameRecord = this.workbook.createName();
        HSSFName newName = new HSSFName(this, nameRecord);
        this.names.add(newName);
        return newName;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int getNameIndex(String name) {
        for (int k = 0; k < this.names.size(); k++) {
            String nameName = getNameName(k);
            if (nameName.equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }

    int getNameIndex(HSSFName name) {
        for (int k = 0; k < this.names.size(); k++) {
            if (name == this.names.get(k)) {
                return k;
            }
        }
        return -1;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeName(int index) {
        this.names.remove(index);
        this.workbook.removeName(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFDataFormat createDataFormat() {
        if (this.formatter == null) {
            this.formatter = new HSSFDataFormat(this.workbook);
        }
        return this.formatter;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeName(String name) {
        int index = getNameIndex(name);
        removeName(index);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void removeName(Name name) {
        int index = getNameIndex((HSSFName) name);
        removeName(index);
    }

    public HSSFPalette getCustomPalette() {
        return new HSSFPalette(this.workbook.getCustomPalette());
    }

    public void insertChartRecord() {
        int loc = this.workbook.findFirstRecordLocBySid((short) 252);
        byte[] data = {IntersectionPtg.sid, 0, 0, -16, 82, 0, 0, 0, 0, 0, 6, -16, 24, 0, 0, 0, 1, 8, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 51, 0, 11, -16, UnaryPlusPtg.sid, 0, 0, 0, -65, 0, 8, 0, 8, 0, -127, 1, 9, 0, 0, 8, -64, 1, Ptg.CLASS_ARRAY, 0, 0, 8, Ptg.CLASS_ARRAY, 0, IntPtg.sid, -15, UnionPtg.sid, 0, 0, 0, 13, 0, 0, 8, 12, 0, 0, 8, StringPtg.sid, 0, 0, 8, -9, 0, 0, UnionPtg.sid};
        UnknownRecord r = new UnknownRecord(235, data);
        this.workbook.getRecords().add(loc, r);
    }

    public void dumpDrawingGroupRecords(boolean fat) {
        DrawingGroupRecord r = (DrawingGroupRecord) this.workbook.findFirstRecordBySid(DrawingGroupRecord.sid);
        r.decode();
        List<EscherRecord> escherRecords = r.getEscherRecords();
        PrintWriter w = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()));
        for (EscherRecord escherRecord : escherRecords) {
            if (fat) {
                System.out.println(escherRecord);
            } else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }

    void initDrawings() {
        DrawingManager2 mgr = this.workbook.findDrawingGroup();
        if (mgr != null) {
            for (HSSFSheet sh : this._sheets) {
                sh.getDrawingPatriarch();
            }
            return;
        }
        this.workbook.createDrawingGroup();
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int addPicture(byte[] pictureData, int format) {
        EscherBlipRecord blipRecord;
        int blipSize;
        short escherTag;
        initDrawings();
        byte[] uid = DigestUtils.md5(pictureData);
        if (format == 2) {
            EscherMetafileBlip blipRecordMeta = new EscherMetafileBlip();
            blipRecord = blipRecordMeta;
            blipRecordMeta.setUID(uid);
            blipRecordMeta.setPictureData(pictureData);
            blipRecordMeta.setFilter((byte) -2);
            blipSize = blipRecordMeta.getCompressedSize() + 58;
            escherTag = 0;
        } else if (format == 3) {
            if (LittleEndian.getInt(pictureData) == -1698247209) {
                byte[] picDataNoHeader = new byte[pictureData.length - 22];
                System.arraycopy(pictureData, 22, picDataNoHeader, 0, pictureData.length - 22);
                pictureData = picDataNoHeader;
            }
            EscherMetafileBlip blipRecordMeta2 = new EscherMetafileBlip();
            blipRecord = blipRecordMeta2;
            blipRecordMeta2.setUID(uid);
            blipRecordMeta2.setPictureData(pictureData);
            blipRecordMeta2.setFilter((byte) -2);
            blipSize = blipRecordMeta2.getCompressedSize() + 58;
            escherTag = 0;
        } else {
            EscherBitmapBlip blipRecordBitmap = new EscherBitmapBlip();
            blipRecord = blipRecordBitmap;
            blipRecordBitmap.setUID(uid);
            blipRecordBitmap.setMarker((byte) -1);
            blipRecordBitmap.setPictureData(pictureData);
            blipSize = pictureData.length + 25;
            escherTag = 255;
        }
        blipRecord.setRecordId((short) (format - 4072));
        switch (format) {
            case 2:
                blipRecord.setOptions(HSSFPictureData.MSOBI_EMF);
                break;
            case 3:
                blipRecord.setOptions(HSSFPictureData.MSOBI_WMF);
                break;
            case 4:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PICT);
                break;
            case 5:
                blipRecord.setOptions(HSSFPictureData.MSOBI_JPEG);
                break;
            case 6:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PNG);
                break;
            case 7:
                blipRecord.setOptions(HSSFPictureData.MSOBI_DIB);
                break;
            default:
                throw new IllegalStateException("Unexpected picture format: " + format);
        }
        EscherBSERecord r = new EscherBSERecord();
        r.setRecordId(EscherBSERecord.RECORD_ID);
        r.setOptions((short) (2 | (format << 4)));
        r.setBlipTypeMacOS((byte) format);
        r.setBlipTypeWin32((byte) format);
        r.setUid(uid);
        r.setTag(escherTag);
        r.setSize(blipSize);
        r.setRef(0);
        r.setOffset(0);
        r.setBlipRecord(blipRecord);
        return this.workbook.addBSERecord(r);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public List<HSSFPictureData> getAllPictures() {
        List<HSSFPictureData> pictures = new ArrayList<>();
        for (Record r : this.workbook.getRecords()) {
            if (r instanceof AbstractEscherHolderRecord) {
                ((AbstractEscherHolderRecord) r).decode();
                List<EscherRecord> escherRecords = ((AbstractEscherHolderRecord) r).getEscherRecords();
                searchForPictures(escherRecords, pictures);
            }
        }
        return Collections.unmodifiableList(pictures);
    }

    private void searchForPictures(List<EscherRecord> escherRecords, List<HSSFPictureData> pictures) {
        EscherBlipRecord blip;
        for (EscherRecord escherRecord : escherRecords) {
            if ((escherRecord instanceof EscherBSERecord) && (blip = ((EscherBSERecord) escherRecord).getBlipRecord()) != null) {
                HSSFPictureData picture = new HSSFPictureData(blip);
                pictures.add(picture);
            }
            searchForPictures(escherRecord.getChildRecords(), pictures);
        }
    }

    protected static Map<String, ClassID> getOleMap() {
        Map<String, ClassID> olemap = new HashMap<>();
        olemap.put("PowerPoint Document", ClassID.PPT_SHOW);
        String[] arr$ = InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES;
        for (String str : arr$) {
            olemap.put(str, ClassID.XLS_WORKBOOK);
        }
        return olemap;
    }

    public int addOlePackage(POIFSFileSystem poiData, String label, String fileName, String command) throws IOException {
        DirectoryNode root = poiData.getRoot();
        Map<String, ClassID> olemap = getOleMap();
        Iterator<Map.Entry<String, ClassID>> it = olemap.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<String, ClassID> entry = it.next();
            if (root.hasEntry(entry.getKey())) {
                root.setStorageClsid(entry.getValue());
                break;
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        poiData.writeFilesystem(bos);
        return addOlePackage(bos.toByteArray(), label, fileName, command);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int addOlePackage(byte[] oleData, String label, String fileName, String command) throws IOException {
        if (initDirectory()) {
            this.preserveNodes = true;
        }
        int storageId = 0;
        DirectoryEntry oleDir = null;
        do {
            storageId++;
            String storageStr = "MBD" + HexDump.toHex(storageId);
            if (!getDirectory().hasEntry(storageStr)) {
                oleDir = getDirectory().createDirectory(storageStr);
                oleDir.setStorageClsid(ClassID.OLE10_PACKAGE);
            }
        } while (oleDir == null);
        byte[] oleBytes = {1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        oleDir.createDocument("\u0001Ole", new ByteArrayInputStream(oleBytes));
        Ole10Native oleNative = new Ole10Native(label, fileName, command, oleData);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        oleNative.writeOut(bos);
        oleDir.createDocument(Ole10Native.OLE10_NATIVE, new ByteArrayInputStream(bos.toByteArray()));
        return storageId;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public int linkExternalWorkbook(String name, Workbook workbook) {
        return this.workbook.linkExternalWorkbook(name, workbook);
    }

    public boolean isWriteProtected() {
        return this.workbook.isWriteProtected();
    }

    public void writeProtectWorkbook(String password, String username) {
        this.workbook.writeProtectWorkbook(password, username);
    }

    public void unwriteProtectWorkbook() {
        this.workbook.unwriteProtectWorkbook();
    }

    public List<HSSFObjectData> getAllEmbeddedObjects() {
        List<HSSFObjectData> objects = new ArrayList<>();
        for (HSSFSheet sheet : this._sheets) {
            getAllEmbeddedObjects(sheet, objects);
        }
        return Collections.unmodifiableList(objects);
    }

    private void getAllEmbeddedObjects(HSSFSheet sheet, List<HSSFObjectData> objects) {
        HSSFPatriarch patriarch = sheet.getDrawingPatriarch();
        if (patriarch == null) {
            return;
        }
        getAllEmbeddedObjects(patriarch, objects);
    }

    private void getAllEmbeddedObjects(HSSFShapeContainer parent, List<HSSFObjectData> objects) {
        for (Shape shape : parent.getChildren()) {
            if (shape instanceof HSSFObjectData) {
                objects.add((HSSFObjectData) shape);
            } else if (shape instanceof HSSFShapeContainer) {
                getAllEmbeddedObjects((HSSFShapeContainer) shape, objects);
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public HSSFCreationHelper getCreationHelper() {
        return new HSSFCreationHelper(this);
    }

    UDFFinder getUDFFinder() {
        return this._udfFinder;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void addToolPack(UDFFinder toopack) {
        AggregatingUDFFinder udfs = (AggregatingUDFFinder) this._udfFinder;
        udfs.add(toopack);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public void setForceFormulaRecalculation(boolean value) {
        InternalWorkbook iwb = getWorkbook();
        RecalcIdRecord recalc = iwb.getRecalcId();
        recalc.setEngineId(0);
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public boolean getForceFormulaRecalculation() {
        InternalWorkbook iwb = getWorkbook();
        RecalcIdRecord recalc = (RecalcIdRecord) iwb.findFirstRecordBySid((short) 449);
        return (recalc == null || recalc.getEngineId() == 0) ? false : true;
    }

    public boolean changeExternalReference(String oldUrl, String newUrl) {
        return this.workbook.changeExternalReference(oldUrl, newUrl);
    }

    @Removal(version = "3.18")
    @Deprecated
    public DirectoryNode getRootDirectory() {
        return getDirectory();
    }

    @Internal
    public InternalWorkbook getInternalWorkbook() {
        return this.workbook;
    }

    @Override // org.apache.poi.ss.usermodel.Workbook
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }

    @Override // org.apache.poi.POIDocument
    public EncryptionInfo getEncryptionInfo() {
        FilePassRecord fpr = (FilePassRecord) this.workbook.findFirstRecordBySid((short) 47);
        if (fpr != null) {
            return fpr.getEncryptionInfo();
        }
        return null;
    }

    private void updateEncryptionInfo() {
        readProperties();
        FilePassRecord fpr = (FilePassRecord) this.workbook.findFirstRecordBySid((short) 47);
        String password = Biff8EncryptionKey.getCurrentUserPassword();
        WorkbookRecordList wrl = this.workbook.getWorkbookRecordList();
        if (password == null) {
            if (fpr != null) {
                wrl.remove(fpr);
                return;
            }
            return;
        }
        if (fpr == null) {
            fpr = new FilePassRecord(EncryptionMode.cryptoAPI);
            wrl.add(1, fpr);
        }
        EncryptionInfo ei = fpr.getEncryptionInfo();
        EncryptionVerifier ver = ei.getVerifier();
        byte[] encVer = ver.getEncryptedVerifier();
        Decryptor dec = ei.getDecryptor();
        Encryptor enc = ei.getEncryptor();
        if (encVer == null) {
            enc.confirmPassword(password);
        } else {
            try {
                if (!dec.verifyPassword(password)) {
                    enc.confirmPassword(password);
                } else {
                    byte[] verifier = dec.getVerifier();
                    byte[] salt = ver.getSalt();
                    enc.confirmPassword(password, null, null, verifier, salt, null);
                }
            } catch (GeneralSecurityException e) {
                throw new EncryptedDocumentException("can't validate/update encryption setting", e);
            }
        }
    }
}
