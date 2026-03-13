package org.apache.poi.ss.formula.eval.forked;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
final class ForkedEvaluationWorkbook implements EvaluationWorkbook {
    private final EvaluationWorkbook _masterBook;
    private final Map<String, ForkedEvaluationSheet> _sharedSheetsByName = new HashMap();

    public ForkedEvaluationWorkbook(EvaluationWorkbook master) {
        this._masterBook = master;
    }

    public ForkedEvaluationCell getOrCreateUpdatableCell(String sheetName, int rowIndex, int columnIndex) {
        ForkedEvaluationSheet sheet = getSharedSheet(sheetName);
        return sheet.getOrCreateUpdatableCell(rowIndex, columnIndex);
    }

    public EvaluationCell getEvaluationCell(String sheetName, int rowIndex, int columnIndex) {
        ForkedEvaluationSheet sheet = getSharedSheet(sheetName);
        return sheet.getCell(rowIndex, columnIndex);
    }

    private ForkedEvaluationSheet getSharedSheet(String sheetName) {
        ForkedEvaluationSheet result = this._sharedSheetsByName.get(sheetName);
        if (result == null) {
            EvaluationWorkbook evaluationWorkbook = this._masterBook;
            ForkedEvaluationSheet result2 = new ForkedEvaluationSheet(evaluationWorkbook.getSheet(evaluationWorkbook.getSheetIndex(sheetName)));
            this._sharedSheetsByName.put(sheetName, result2);
            return result2;
        }
        return result;
    }

    public void copyUpdatedCells(Workbook workbook) {
        String[] sheetNames = new String[this._sharedSheetsByName.size()];
        this._sharedSheetsByName.keySet().toArray(sheetNames);
        for (String sheetName : sheetNames) {
            ForkedEvaluationSheet sheet = this._sharedSheetsByName.get(sheetName);
            sheet.copyUpdatedCells(workbook.getSheet(sheetName));
        }
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int convertFromExternSheetIndex(int externSheetIndex) {
        return this._masterBook.convertFromExternSheetIndex(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(int externSheetIndex) {
        return this._masterBook.getExternalSheet(externSheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalSheet getExternalSheet(String firstSheetName, String lastSheetName, int externalWorkbookNumber) {
        return this._masterBook.getExternalSheet(firstSheetName, lastSheetName, externalWorkbookNumber);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public Ptg[] getFormulaTokens(EvaluationCell cell) {
        if (cell instanceof ForkedEvaluationCell) {
            throw new RuntimeException("Updated formulas not supported yet");
        }
        return this._masterBook.getFormulaTokens(cell);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationName getName(NamePtg namePtg) {
        return this._masterBook.getName(namePtg);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public EvaluationName getName(String name, int sheetIndex) {
        return this._masterBook.getName(name, sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationSheet getSheet(int sheetIndex) {
        return getSharedSheet(getSheetName(sheetIndex));
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        return this._masterBook.getExternalName(externSheetIndex, externNameIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public EvaluationWorkbook.ExternalName getExternalName(String nameName, String sheetName, int externalWorkbookNumber) {
        return this._masterBook.getExternalName(nameName, sheetName, externalWorkbookNumber);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int getSheetIndex(EvaluationSheet sheet) {
        if (sheet instanceof ForkedEvaluationSheet) {
            ForkedEvaluationSheet mes = (ForkedEvaluationSheet) sheet;
            return mes.getSheetIndex(this._masterBook);
        }
        return this._masterBook.getSheetIndex(sheet);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public int getSheetIndex(String sheetName) {
        return this._masterBook.getSheetIndex(sheetName);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public String getSheetName(int sheetIndex) {
        return this._masterBook.getSheetName(sheetIndex);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public String resolveNameXText(NameXPtg ptg) {
        return this._masterBook.resolveNameXText(ptg);
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public UDFFinder getUDFFinder() {
        return this._masterBook.getUDFFinder();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook, org.apache.poi.ss.formula.FormulaParsingWorkbook
    public SpreadsheetVersion getSpreadsheetVersion() {
        return this._masterBook.getSpreadsheetVersion();
    }

    @Override // org.apache.poi.ss.formula.EvaluationWorkbook
    public void clearAllCachedResultValues() {
        this._masterBook.clearAllCachedResultValues();
    }
}
