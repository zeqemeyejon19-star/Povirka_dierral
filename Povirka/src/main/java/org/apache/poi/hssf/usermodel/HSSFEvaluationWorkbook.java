package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.formula.SheetRangeIdentifier;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public final class HSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook {
    private final InternalWorkbook _iBook;
    private final HSSFWorkbook _uBook;

    public static HSSFEvaluationWorkbook create(HSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new HSSFEvaluationWorkbook(book);
    }

    private HSSFEvaluationWorkbook(HSSFWorkbook book) {
        this._uBook = book;
        this._iBook = book.getWorkbook();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public void clearAllCachedResultValues() {
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public HSSFName createName() {
        return this._uBook.createName();
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public int getExternalSheetIndex(String sheetName) {
        int sheetIndex = this._uBook.getSheetIndex(sheetName);
        return this._iBook.checkExternSheet(sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public int getExternalSheetIndex(String workbookName, String sheetName) {
        return this._iBook.getExternalSheetIndex(workbookName, sheetName);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public Ptg get3DReferencePtg(CellReference cr, SheetIdentifier sheet) {
        int extIx = getSheetExtIx(sheet);
        return new Ref3DPtg(cr, extIx);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public Ptg get3DReferencePtg(AreaReference areaRef, SheetIdentifier sheet) {
        int extIx = getSheetExtIx(sheet);
        return new Area3DPtg(areaRef, extIx);
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public NameXPtg getNameXPtg(String name, SheetIdentifier sheet) {
        int sheetRefIndex = getSheetExtIx(sheet);
        return this._iBook.getNameXPtg(name, sheetRefIndex, this._uBook.getUDFFinder());
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public EvaluationName getName(String name, int sheetIndex) {
        for (int i = 0; i < this._iBook.getNumNames(); i++) {
            NameRecord nr = this._iBook.getNameRecord(i);
            if (nr.getSheetNumber() == sheetIndex + 1 && name.equalsIgnoreCase(nr.getNameText())) {
                return new Name(nr, i);
            }
        }
        if (sheetIndex == -1) {
            return null;
        }
        return getName(name, -1);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int getSheetIndex(EvaluationSheet evalSheet) {
        HSSFSheet sheet = ((HSSFEvaluationSheet) evalSheet).getHSSFSheet();
        return this._uBook.getSheetIndex(sheet);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int getSheetIndex(String sheetName) {
        return this._uBook.getSheetIndex(sheetName);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public String getSheetName(int sheetIndex) {
        return this._uBook.getSheetName(sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationSheet getSheet(int sheetIndex) {
        return new HSSFEvaluationSheet(this._uBook.getSheetAt(sheetIndex));
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int convertFromExternSheetIndex(int externSheetIndex) {
        return this._iBook.getFirstSheetIndexFromExternSheetIndex(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook, org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        EvaluationWorkbook.ExternalSheet sheet = this._iBook.getExternalSheet(externSheetIndex);
        if (sheet == null) {
            int localSheetIndex = convertFromExternSheetIndex(externSheetIndex);
            if (localSheetIndex == -1 || localSheetIndex == -2) {
                return null;
            }
            String sheetName = getSheetName(localSheetIndex);
            int lastLocalSheetIndex = this._iBook.getLastSheetIndexFromExternSheetIndex(externSheetIndex);
            if (lastLocalSheetIndex == localSheetIndex) {
                return new EvaluationWorkbook.ExternalSheet(null, sheetName);
            }
            String lastSheetName = getSheetName(lastLocalSheetIndex);
            return new EvaluationWorkbook.ExternalSheetRange(null, sheetName, lastSheetName);
        }
        return sheet;
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(String firstSheetName, String lastSheetName, int externalWorkbookNumber) {
        throw new IllegalStateException("XSSF-style external references are not supported for HSSF");
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        return this._iBook.getExternalName(externSheetIndex, externNameIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(String nameName, String sheetName, int externalWorkbookNumber) {
        throw new IllegalStateException("XSSF-style external names are not supported for HSSF");
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook, org.apache.poi.ss.formula.EvaluationWorkbook
    public String resolveNameXText(NameXPtg n) {
        return this._iBook.resolveNameXText(n.getSheetRefIndex(), n.getNameIndex());
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getSheetFirstNameByExternSheet(int externSheetIndex) {
        return this._iBook.findSheetFirstNameFromExternSheet(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getSheetLastNameByExternSheet(int externSheetIndex) {
        return this._iBook.findSheetLastNameFromExternSheet(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.FormulaRenderingWorkbook
    public String getNameText(NamePtg namePtg) {
        return this._iBook.getNameRecord(namePtg.getIndex()).getNameText();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationName getName(NamePtg namePtg) {
        int ix = namePtg.getIndex();
        return new Name(this._iBook.getNameRecord(ix), ix);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public Ptg[] getFormulaTokens(EvaluationCell evalCell) {
        HSSFCell cell = ((HSSFEvaluationCell) evalCell).getHSSFCell();
        FormulaRecordAggregate fra = (FormulaRecordAggregate) cell.getCellValueRecord();
        return fra.getFormulaTokens();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public UDFFinder getUDFFinder() {
        return this._uBook.getUDFFinder();
    }

    private static final class Name implements EvaluationName {
        private final int _index;
        private final NameRecord _nameRecord;

        public Name(NameRecord nameRecord, int index) {
            this._nameRecord = nameRecord;
            this._index = index;
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public Ptg[] getNameDefinition() {
            return this._nameRecord.getNameDefinition();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public String getNameText() {
            return this._nameRecord.getNameText();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean hasFormula() {
            return this._nameRecord.hasFormula();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean isFunctionName() {
            return this._nameRecord.isFunctionName();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public boolean isRange() {
            return this._nameRecord.hasFormula();
        }

        @Override // org.apache.poi.ss.formula.EvaluationName
        public NamePtg createPtg() {
            return new NamePtg(this._index);
        }
    }

    private int getSheetExtIx(SheetIdentifier sheetIden) {
        if (sheetIden == null) {
            return -1;
        }
        String workbookName = sheetIden.getBookName();
        String firstSheetName = sheetIden.getSheetIdentifier().getName();
        String lastSheetName = firstSheetName;
        if (sheetIden instanceof SheetRangeIdentifier) {
            lastSheetName = ((SheetRangeIdentifier) sheetIden).getLastSheetIdentifier().getName();
        }
        if (workbookName == null) {
            int firstSheetIndex = this._uBook.getSheetIndex(firstSheetName);
            int lastSheetIndex = this._uBook.getSheetIndex(lastSheetName);
            return this._iBook.checkExternSheet(firstSheetIndex, lastSheetIndex);
        }
        int extIx = this._iBook.getExternalSheetIndex(workbookName, firstSheetName, lastSheetName);
        return extIx;
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }

    @Override // org.apache.poi.ss.formula.FormulaParsingWorkbook
    public Table getTable(String name) {
        throw new IllegalStateException("XSSF-style tables are not supported for HSSF");
    }
}
