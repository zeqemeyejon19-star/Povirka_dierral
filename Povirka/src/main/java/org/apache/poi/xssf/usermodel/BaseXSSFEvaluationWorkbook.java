package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.udf.IndexedUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xssf.model.ExternalLinksTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class BaseXSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook {
    private Map<String, XSSFTable> _tableCache = null;
    protected final XSSFWorkbook _uBook;

    protected BaseXSSFEvaluationWorkbook(XSSFWorkbook book) {
        this._uBook = book;
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public void clearAllCachedResultValues() {
        this._tableCache = null;
    }

    private int convertFromExternalSheetIndex(int externSheetIndex) {
        return externSheetIndex;
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int convertFromExternSheetIndex(int externSheetIndex) {
        return externSheetIndex;
    }

    private int convertToExternalSheetIndex(int sheetIndex) {
        return sheetIndex;
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public int getExternalSheetIndex(String sheetName) {
        int sheetIndex = this._uBook.getSheetIndex(sheetName);
        return convertToExternalSheetIndex(sheetIndex);
    }

    private int resolveBookIndex(String bookName) {
        if (bookName.startsWith("[") && bookName.endsWith("]")) {
            bookName = bookName.substring(1, bookName.length() - 2);
        }
        try {
            return Integer.parseInt(bookName);
        } catch (NumberFormatException e) {
            List<ExternalLinksTable> tables = this._uBook.getExternalLinksTable();
            int index = findExternalLinkIndex(bookName, tables);
            if (index != -1) {
                return index;
            }
            if (bookName.startsWith("'file:///") && bookName.endsWith("'")) {
                String relBookName = bookName.substring(bookName.lastIndexOf(47) + 1);
                String relBookName2 = relBookName.substring(0, relBookName.length() - 1);
                int index2 = findExternalLinkIndex(relBookName2, tables);
                if (index2 != -1) {
                    return index2;
                }
                ExternalLinksTable fakeLinkTable = new FakeExternalLinksTable(relBookName2);
                tables.add(fakeLinkTable);
                return tables.size();
            }
            throw new RuntimeException("Book not linked for filename " + bookName);
        }
    }

    private int findExternalLinkIndex(String bookName, List<ExternalLinksTable> tables) {
        int i = 0;
        for (ExternalLinksTable table : tables) {
            if (table.getLinkedFileName().equals(bookName)) {
                return i + 1;
            }
            i++;
        }
        return -1;
    }

    private static class FakeExternalLinksTable extends ExternalLinksTable {
        private final String fileName;

        private FakeExternalLinksTable(String fileName) {
            this.fileName = fileName;
        }

        @Override // org.apache.poi.xssf.model.ExternalLinksTable
        public String getLinkedFileName() {
            return this.fileName;
        }
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public EvaluationName getName(String name, int sheetIndex) {
        for (int i = 0; i < this._uBook.getNumberOfNames(); i++) {
            XSSFName nm = this._uBook.getNameAt(i);
            String nameText = nm.getNameName();
            int nameSheetindex = nm.getSheetIndex();
            if (name.equalsIgnoreCase(nameText) && (nameSheetindex == -1 || nameSheetindex == sheetIndex)) {
                return new Name(nm, i, this);
            }
        }
        if (sheetIndex == -1) {
            return null;
        }
        return getName(name, -1);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public String getSheetName(int sheetIndex) {
        return this._uBook.getSheetName(sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        throw new IllegalStateException("HSSF-style external references are not supported for XSSF");
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(String nameName, String sheetName, int externalWorkbookNumber) {
        if (externalWorkbookNumber > 0) {
            int linkNumber = externalWorkbookNumber - 1;
            ExternalLinksTable linkTable = this._uBook.getExternalLinksTable().get(linkNumber);
            for (org.apache.poi.ss.usermodel.Name name : linkTable.getDefinedNames()) {
                if (name.getNameName().equals(nameName)) {
                    int nameSheetIndex = name.getSheetIndex() + 1;
                    return new EvaluationWorkbook.ExternalName(nameName, -1, nameSheetIndex);
                }
            }
            throw new IllegalArgumentException("Name '" + nameName + "' not found in reference to " + linkTable.getLinkedFileName());
        }
        int nameIdx = this._uBook.getNameIndex(nameName);
        return new EvaluationWorkbook.ExternalName(nameName, nameIdx, 0);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public NameXPxg getNameXPtg(String name, SheetIdentifier sheet) {
        IndexedUDFFinder udfFinder = (IndexedUDFFinder) getUDFFinder();
        FreeRefFunction func = udfFinder.findFunction(name);
        if (func != null) {
            return new NameXPxg(null, name);
        }
        if (sheet == null) {
            if (this._uBook.getNames(name).isEmpty()) {
                return null;
            }
            return new NameXPxg(null, name);
        }
        if (sheet._sheetIdentifier == null) {
            int bookIndex = resolveBookIndex(sheet._bookName);
            return new NameXPxg(bookIndex, null, name);
        }
        String sheetName = sheet._sheetIdentifier.getName();
        if (sheet._bookName != null) {
            int bookIndex2 = resolveBookIndex(sheet._bookName);
            return new NameXPxg(bookIndex2, sheetName, name);
        }
        return new NameXPxg(sheetName, name);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public Ptg get3DReferencePtg(CellReference cell, SheetIdentifier sheet) {
        if (sheet._bookName != null) {
            int bookIndex = resolveBookIndex(sheet._bookName);
            return new Ref3DPxg(bookIndex, sheet, cell);
        }
        return new Ref3DPxg(sheet, cell);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public Ptg get3DReferencePtg(AreaReference area, SheetIdentifier sheet) {
        if (sheet._bookName != null) {
            int bookIndex = resolveBookIndex(sheet._bookName);
            return new Area3DPxg(bookIndex, sheet, area);
        }
        return new Area3DPxg(sheet, area);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook, org.apache.poi.ss.formula.EvaluationWorkbook
    public String resolveNameXText(NameXPtg n) {
        XSSFName xname;
        int idx = n.getNameIndex();
        IndexedUDFFinder udfFinder = (IndexedUDFFinder) getUDFFinder();
        String name = udfFinder.getFunctionName(idx);
        if (name == null && (xname = this._uBook.getNameAt(idx)) != null) {
            return xname.getNameName();
        }
        return name;
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook, org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        throw new IllegalStateException("HSSF-style external references are not supported for XSSF");
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(String firstSheetName, String lastSheetName, int externalWorkbookNumber) {
        String workbookName;
        if (externalWorkbookNumber > 0) {
            int linkNumber = externalWorkbookNumber - 1;
            ExternalLinksTable linkTable = this._uBook.getExternalLinksTable().get(linkNumber);
            workbookName = linkTable.getLinkedFileName();
        } else {
            workbookName = null;
        }
        if (lastSheetName == null || firstSheetName.equals(lastSheetName)) {
            return new EvaluationWorkbook.ExternalSheet(workbookName, firstSheetName);
        }
        return new EvaluationWorkbook.ExternalSheetRange(workbookName, firstSheetName, lastSheetName);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    @NotImplemented
    public int getExternalSheetIndex(String workbookName, String sheetName) {
        throw new RuntimeException("not implemented yet");
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int getSheetIndex(String sheetName) {
        return this._uBook.getSheetIndex(sheetName);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getSheetFirstNameByExternSheet(int externSheetIndex) {
        int sheetIndex = convertFromExternalSheetIndex(externSheetIndex);
        return this._uBook.getSheetName(sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getSheetLastNameByExternSheet(int externSheetIndex) {
        return getSheetFirstNameByExternSheet(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getNameText(NamePtg namePtg) {
        return this._uBook.getNameAt(namePtg.getIndex()).getNameName();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationName getName(NamePtg namePtg) {
        int ix = namePtg.getIndex();
        return new Name(this._uBook.getNameAt(ix), ix, this);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public XSSFName createName() {
        return this._uBook.createName();
    }

    private static String caseInsensitive(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    private Map<String, XSSFTable> getTableCache() {
        Map<String, XSSFTable> map = this._tableCache;
        if (map != null) {
            return map;
        }
        this._tableCache = new HashMap();
        for (Sheet sheet : this._uBook) {
            for (XSSFTable tbl : ((XSSFSheet) sheet).getTables()) {
                String lname = caseInsensitive(tbl.getName());
                this._tableCache.put(lname, tbl);
            }
        }
        return this._tableCache;
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public XSSFTable getTable(String name) {
        if (name == null) {
            return null;
        }
        String lname = caseInsensitive(name);
        return getTableCache().get(lname);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public UDFFinder getUDFFinder() {
        return this._uBook.getUDFFinder();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL2007;
    }

    private static final class Name implements EvaluationName {
        private final FormulaParsingWorkbook _fpBook;
        private final int _index;
        private final XSSFName _nameRecord;

        public Name(XSSFName name, int index, FormulaParsingWorkbook fpBook) {
            this._nameRecord = name;
            this._index = index;
            this._fpBook = fpBook;
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public Ptg[] getNameDefinition() {
            return FormulaParser.parse(this._nameRecord.getRefersToFormula(), this._fpBook, FormulaType.NAMEDRANGE, this._nameRecord.getSheetIndex());
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public String getNameText() {
            return this._nameRecord.getNameName();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean hasFormula() {
            CTDefinedName ctn = this._nameRecord.getCTName();
            String strVal = ctn.getStringValue();
            return (ctn.getFunction() || strVal == null || strVal.length() <= 0) ? false : true;
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean isFunctionName() {
            return this._nameRecord.isFunctionName();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean isRange() {
            return hasFormula();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public NamePtg createPtg() {
            return new NamePtg(this._index);
        }
    }
}
