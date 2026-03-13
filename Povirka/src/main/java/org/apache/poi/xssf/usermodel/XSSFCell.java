package org.apache.poi.xssf.usermodel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.SharedFormula;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.Removal;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFCell implements Cell {
    private static final String FALSE = "FALSE";
    private static final String FALSE_AS_STRING = "0";
    private static final String TRUE = "TRUE";
    private static final String TRUE_AS_STRING = "1";
    private CTCell _cell;
    private int _cellNum;
    private final XSSFRow _row;
    private SharedStringsTable _sharedStringSource;
    private StylesTable _stylesSource;

    protected XSSFCell(XSSFRow row, CTCell cell) {
        this._cell = cell;
        this._row = row;
        if (cell.getR() != null) {
            this._cellNum = new CellReference(cell.getR()).getCol();
        } else {
            int prevNum = row.getLastCellNum();
            if (prevNum != -1) {
                this._cellNum = row.getCell(prevNum - 1, Row.MissingCellPolicy.RETURN_NULL_AND_BLANK).getColumnIndex() + 1;
            }
        }
        this._sharedStringSource = row.getSheet().getWorkbook().getSharedStringSource();
        this._stylesSource = row.getSheet().getWorkbook().getStylesSource();
    }

    @Internal
    public void copyCellFrom(Cell srcCell, CellCopyPolicy policy) {
        if (policy.isCopyCellValue()) {
            if (srcCell != null) {
                CellType copyCellType = srcCell.getCellTypeEnum();
                if (copyCellType == CellType.FORMULA && !policy.isCopyCellFormula()) {
                    copyCellType = srcCell.getCachedFormulaResultTypeEnum();
                }
                switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[copyCellType.ordinal()]) {
                    case 1:
                        if (DateUtil.isCellDateFormatted(srcCell)) {
                            setCellValue(srcCell.getDateCellValue());
                        } else {
                            setCellValue(srcCell.getNumericCellValue());
                        }
                        break;
                    case 2:
                        setCellValue(srcCell.getStringCellValue());
                        break;
                    case 3:
                        setCellFormula(srcCell.getCellFormula());
                        break;
                    case 4:
                        setBlank();
                        break;
                    case 5:
                        setCellValue(srcCell.getBooleanCellValue());
                        break;
                    case 6:
                        setCellErrorValue(srcCell.getErrorCellValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid cell type " + srcCell.getCellTypeEnum());
                }
            } else {
                setBlank();
            }
        }
        if (policy.isCopyCellStyle()) {
            setCellStyle(srcCell == null ? null : srcCell.getCellStyle());
        }
        Hyperlink srcHyperlink = srcCell == null ? null : srcCell.getHyperlink();
        if (policy.isMergeHyperlink()) {
            if (srcHyperlink != null) {
                setHyperlink(new XSSFHyperlink(srcHyperlink));
            }
        } else if (policy.isCopyHyperlink()) {
            setHyperlink(srcHyperlink != null ? new XSSFHyperlink(srcHyperlink) : null);
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.XSSFCell$1, reason: invalid class name */
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
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BLANK.ordinal()] = 4;
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

    protected SharedStringsTable getSharedStringSource() {
        return this._sharedStringSource;
    }

    protected StylesTable getStylesSource() {
        return this._stylesSource;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFSheet getSheet() {
        return getRow().getSheet();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFRow getRow() {
        return this._row;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public boolean getBooleanCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 3) {
            return this._cell.isSetV() && TRUE_AS_STRING.equals(this._cell.getV());
        }
        if (i == 4) {
            return false;
        }
        if (i == 5) {
            return this._cell.isSetV() && TRUE_AS_STRING.equals(this._cell.getV());
        }
        throw typeMismatch(CellType.BOOLEAN, cellType, false);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(boolean value) {
        this._cell.setT(STCellType.B);
        this._cell.setV(value ? TRUE_AS_STRING : FALSE_AS_STRING);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public double getNumericCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i != 1 && i != 3) {
            if (i == 4) {
                return 0.0d;
            }
            throw typeMismatch(CellType.NUMERIC, cellType, false);
        }
        if (!this._cell.isSetV()) {
            return 0.0d;
        }
        String v = this._cell.getV();
        if (v.isEmpty()) {
            return 0.0d;
        }
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            throw typeMismatch(CellType.NUMERIC, CellType.STRING, false);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.DIV0.getString());
        } else if (Double.isNaN(value)) {
            this._cell.setT(STCellType.E);
            this._cell.setV(FormulaError.NUM.getString());
        } else {
            this._cell.setT(STCellType.N);
            this._cell.setV(String.valueOf(value));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getStringCellValue() {
        return getRichStringCellValue().getString();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFRichTextString getRichStringCellValue() {
        XSSFRichTextString rt;
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i != 2) {
            if (i == 3) {
                checkFormulaCachedValueType(CellType.STRING, getBaseCellType(false));
                rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
            } else {
                if (i != 4) {
                    throw typeMismatch(CellType.STRING, cellType, false);
                }
                rt = new XSSFRichTextString("");
            }
        } else if (this._cell.getT() == STCellType.INLINE_STR) {
            rt = this._cell.isSetIs() ? new XSSFRichTextString(this._cell.getIs()) : this._cell.isSetV() ? new XSSFRichTextString(this._cell.getV()) : new XSSFRichTextString("");
        } else if (this._cell.getT() == STCellType.STR) {
            rt = new XSSFRichTextString(this._cell.isSetV() ? this._cell.getV() : "");
        } else if (this._cell.isSetV()) {
            int idx = Integer.parseInt(this._cell.getV());
            rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(idx));
        } else {
            rt = new XSSFRichTextString("");
        }
        rt.setStylesTableReference(this._stylesSource);
        return rt;
    }

    private static void checkFormulaCachedValueType(CellType expectedTypeCode, CellType cachedValueType) {
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(String str) {
        setCellValue(str == null ? null : new XSSFRichTextString(str));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(RichTextString str) {
        if (str == null || str.getString() == null) {
            setCellType(CellType.BLANK);
            return;
        }
        if (str.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }
        CellType cellType = getCellTypeEnum();
        if (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()] == 3) {
            this._cell.setV(str.getString());
            this._cell.setT(STCellType.STR);
        } else {
            if (this._cell.getT() == STCellType.INLINE_STR) {
                this._cell.setV(str.getString());
                return;
            }
            this._cell.setT(STCellType.S);
            XSSFRichTextString rt = (XSSFRichTextString) str;
            rt.setStylesTableReference(this._stylesSource);
            int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
            this._cell.setV(Integer.toString(sRef));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getCellFormula() {
        return getCellFormula(null);
    }

    protected String getCellFormula(XSSFEvaluationWorkbook fpb) {
        CellType cellType = getCellTypeEnum();
        if (cellType != CellType.FORMULA) {
            throw typeMismatch(CellType.FORMULA, cellType, false);
        }
        CTCellFormula f = this._cell.getF();
        if (isPartOfArrayFormulaGroup() && f == null) {
            XSSFCell cell = getSheet().getFirstCellInArrayFormula(this);
            return cell.getCellFormula(fpb);
        }
        if (f.getT() == STCellFormulaType.SHARED) {
            return convertSharedFormula((int) f.getSi(), fpb == null ? XSSFEvaluationWorkbook.create(getSheet().getWorkbook()) : fpb);
        }
        return f.getStringValue();
    }

    private String convertSharedFormula(int si, XSSFEvaluationWorkbook fpb) {
        XSSFSheet sheet = getSheet();
        CTCellFormula f = sheet.getSharedFormula(si);
        if (f == null) {
            throw new IllegalStateException("Master cell of a shared formula with sid=" + si + " was not found");
        }
        String sharedFormula = f.getStringValue();
        String sharedFormulaRange = f.getRef();
        CellRangeAddress ref = CellRangeAddress.valueOf(sharedFormulaRange);
        int sheetIndex = sheet.getWorkbook().getSheetIndex(sheet);
        SharedFormula sf = new SharedFormula(SpreadsheetVersion.EXCEL2007);
        Ptg[] ptgs = FormulaParser.parse(sharedFormula, fpb, FormulaType.CELL, sheetIndex, getRowIndex());
        Ptg[] fmla = sf.convertSharedFormulas(ptgs, getRowIndex() - ref.getFirstRow(), getColumnIndex() - ref.getFirstColumn());
        return FormulaRenderer.toFormulaString(fpb, fmla);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellFormula(String formula) {
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        setFormula(formula, FormulaType.CELL);
    }

    void setCellArrayFormula(String formula, CellRangeAddress range) {
        setFormula(formula, FormulaType.ARRAY);
        CTCellFormula cellFormula = this._cell.getF();
        cellFormula.setT(STCellFormulaType.ARRAY);
        cellFormula.setRef(range.formatAsString());
    }

    private void setFormula(String formula, FormulaType formulaType) {
        XSSFWorkbook wb = this._row.getSheet().getWorkbook();
        if (formula == null) {
            wb.onDeleteFormula(this);
            if (this._cell.isSetF()) {
                this._cell.unsetF();
                return;
            }
            return;
        }
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
        FormulaParser.parse(formula, fpb, formulaType, wb.getSheetIndex(getSheet()), getRowIndex());
        CTCellFormula f = CTCellFormula.Factory.newInstance();
        f.setStringValue(formula);
        this._cell.setF(f);
        if (this._cell.isSetV()) {
            this._cell.unsetV();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getColumnIndex() {
        return this._cellNum;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getRowIndex() {
        return this._row.getRowNum();
    }

    public String getReference() {
        String ref = this._cell.getR();
        if (ref == null) {
            return getAddress().formatAsString();
        }
        return ref;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellAddress getAddress() {
        return new CellAddress(this);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFCellStyle getCellStyle() {
        if (this._stylesSource.getNumCellStyles() <= 0) {
            return null;
        }
        long idx = this._cell.isSetS() ? this._cell.getS() : 0L;
        XSSFCellStyle style = this._stylesSource.getStyleAt((int) idx);
        return style;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellStyle(CellStyle style) {
        if (style == null) {
            if (this._cell.isSetS()) {
                this._cell.unsetS();
            }
        } else {
            XSSFCellStyle xStyle = (XSSFCellStyle) style;
            xStyle.verifyBelongsToStylesSource(this._stylesSource);
            long idx = this._stylesSource.putStyle(xStyle);
            this._cell.setS(idx);
        }
    }

    private boolean isFormulaCell() {
        if ((this._cell.isSetF() && this._cell.getF().getT() != STCellFormulaType.DATA_TABLE) || getSheet().isCellInArrayFormulaContext(this)) {
            return true;
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    @Removal(version = "3.17")
    @Deprecated
    public int getCellType() {
        return getCellTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCellTypeEnum() {
        if (isFormulaCell()) {
            return CellType.FORMULA;
        }
        return getBaseCellType(true);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    @Removal(version = "3.17")
    @Deprecated
    public int getCachedFormulaResultType() {
        return getCachedFormulaResultTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCachedFormulaResultTypeEnum() {
        if (!isFormulaCell()) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return getBaseCellType(false);
    }

    private CellType getBaseCellType(boolean blankCells) {
        switch (this._cell.getT().intValue()) {
            case 1:
                return CellType.BOOLEAN;
            case 2:
                if (!this._cell.isSetV() && blankCells) {
                    return CellType.BLANK;
                }
                return CellType.NUMERIC;
            case 3:
                return CellType.ERROR;
            case 4:
            case 5:
            case 6:
                return CellType.STRING;
            default:
                throw new IllegalStateException("Illegal cell type: " + this._cell.getT());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Date getDateCellValue() {
        if (getCellTypeEnum() == CellType.BLANK) {
            return null;
        }
        double value = getNumericCellValue();
        boolean date1904 = getSheet().getWorkbook().isDate1904();
        return DateUtil.getJavaDate(value, date1904);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(Date value) {
        if (value == null) {
            setCellType(CellType.BLANK);
        } else {
            boolean date1904 = getSheet().getWorkbook().isDate1904();
            setCellValue(DateUtil.getExcelDate(value, date1904));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(Calendar value) {
        if (value == null) {
            setCellType(CellType.BLANK);
        } else {
            boolean date1904 = getSheet().getWorkbook().isDate1904();
            setCellValue(DateUtil.getExcelDate(value, date1904));
        }
    }

    public String getErrorCellString() throws IllegalStateException {
        CellType cellType = getBaseCellType(true);
        if (cellType != CellType.ERROR) {
            throw typeMismatch(CellType.ERROR, cellType, false);
        }
        return this._cell.getV();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public byte getErrorCellValue() throws IllegalStateException {
        String code = getErrorCellString();
        if (code == null) {
            return (byte) 0;
        }
        try {
            return FormulaError.forString(code).getCode();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unexpected error code", e);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellErrorValue(byte errorCode) {
        FormulaError error = FormulaError.forInt(errorCode);
        setCellErrorValue(error);
    }

    public void setCellErrorValue(FormulaError error) {
        this._cell.setT(STCellType.E);
        this._cell.setV(error.getString());
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setAsActiveCell() {
        getSheet().setActiveCell(getAddress());
    }

    private void setBlank() {
        CTCell blank = CTCell.Factory.newInstance();
        blank.setR(this._cell.getR());
        if (this._cell.isSetS()) {
            blank.setS(this._cell.getS());
        }
        this._cell.set(blank);
    }

    protected void setCellNum(int num) {
        checkBounds(num);
        this._cellNum = num;
        String ref = new CellReference(getRowIndex(), getColumnIndex()).formatAsString();
        this._cell.setR(ref);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    @Removal(version = "3.17")
    @Deprecated
    public void setCellType(int cellType) {
        setCellType(CellType.forInt(cellType));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellType(CellType cellType) {
        CellType prevType = getCellTypeEnum();
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        if (prevType == CellType.FORMULA && cellType != CellType.FORMULA) {
            getSheet().getWorkbook().onDeleteFormula(this);
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        String str = FALSE_AS_STRING;
        switch (i) {
            case 1:
                this._cell.setT(STCellType.N);
                break;
            case 2:
                if (prevType != CellType.STRING) {
                    String str2 = convertCellValueToString();
                    XSSFRichTextString rt = new XSSFRichTextString(str2);
                    rt.setStylesTableReference(this._stylesSource);
                    int sRef = this._sharedStringSource.addEntry(rt.getCTRst());
                    this._cell.setV(Integer.toString(sRef));
                }
                this._cell.setT(STCellType.S);
                break;
            case 3:
                if (!this._cell.isSetF()) {
                    CTCellFormula f = CTCellFormula.Factory.newInstance();
                    f.setStringValue(FALSE_AS_STRING);
                    this._cell.setF(f);
                    if (this._cell.isSetT()) {
                        this._cell.unsetT();
                    }
                }
                break;
            case 4:
                setBlank();
                break;
            case 5:
                if (convertCellValueToBoolean()) {
                    str = TRUE_AS_STRING;
                }
                String newVal = str;
                this._cell.setT(STCellType.B);
                this._cell.setV(newVal);
                break;
            case 6:
                this._cell.setT(STCellType.E);
                break;
            default:
                throw new IllegalArgumentException("Illegal cell type: " + cellType);
        }
        if (cellType != CellType.FORMULA && this._cell.isSetF()) {
            this._cell.unsetF();
        }
    }

    public String toString() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[getCellTypeEnum().ordinal()]) {
            case 1:
                if (DateUtil.isCellDateFormatted(this)) {
                    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(getDateCellValue());
                }
                return Double.toString(getNumericCellValue());
            case 2:
                return getRichStringCellValue().toString();
            case 3:
                return getCellFormula();
            case 4:
                return "";
            case 5:
                return getBooleanCellValue() ? TRUE : FALSE;
            case 6:
                return ErrorEval.getText(getErrorCellValue());
            default:
                return "Unknown Cell Type: " + getCellTypeEnum();
        }
    }

    public String getRawValue() {
        return this._cell.getV();
    }

    private static RuntimeException typeMismatch(CellType expectedType, CellType actualType, boolean isFormulaCell) {
        String msg = "Cannot get a " + expectedType + " value from a " + actualType + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private static void checkBounds(int cellIndex) {
        SpreadsheetVersion v = SpreadsheetVersion.EXCEL2007;
        int maxcol = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        if (cellIndex < 0 || cellIndex > maxcol) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + v.name() + " is (0.." + maxcol + ") or ('A'..'" + v.getLastColumnName() + "')");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFComment getCellComment() {
        return getSheet().getCellComment(new CellAddress(this));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellComment(Comment comment) {
        if (comment == null) {
            removeCellComment();
        } else {
            comment.setAddress(getRowIndex(), getColumnIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeCellComment() {
        XSSFComment comment = getCellComment();
        if (comment != null) {
            CellAddress ref = new CellAddress(getReference());
            XSSFSheet sh = getSheet();
            sh.getCommentsTable(false).removeComment(ref);
            sh.getVMLDrawing(false).removeCommentShape(getRowIndex(), getColumnIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public XSSFHyperlink getHyperlink() {
        return getSheet().getHyperlink(this._row.getRowNum(), this._cellNum);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setHyperlink(Hyperlink hyperlink) {
        if (hyperlink == null) {
            removeHyperlink();
            return;
        }
        XSSFHyperlink link = (XSSFHyperlink) hyperlink;
        link.setCellReference(new CellReference(this._row.getRowNum(), this._cellNum).formatAsString());
        getSheet().addHyperlink(link);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeHyperlink() {
        getSheet().removeHyperlink(this._row.getRowNum(), this._cellNum);
    }

    @Internal
    public CTCell getCTCell() {
        return this._cell;
    }

    @Internal
    public void setCTCell(CTCell cell) {
        this._cell = cell;
    }

    private boolean convertCellValueToBoolean() {
        CellType cellType = getCellTypeEnum();
        if (cellType == CellType.FORMULA) {
            cellType = getBaseCellType(false);
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            return Double.parseDouble(this._cell.getV()) != 0.0d;
        }
        if (i == 2) {
            int sstIndex = Integer.parseInt(this._cell.getV());
            XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
            String text = rt.getString();
            return Boolean.parseBoolean(text);
        }
        if (i != 4) {
            if (i == 5) {
                return TRUE_AS_STRING.equals(this._cell.getV());
            }
            if (i != 6) {
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            }
        }
        return false;
    }

    private String convertCellValueToString() {
        CellType cellType = getCellTypeEnum();
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
            case 6:
                return this._cell.getV();
            case 2:
                int sstIndex = Integer.parseInt(this._cell.getV());
                XSSFRichTextString rt = new XSSFRichTextString(this._sharedStringSource.getEntryAt(sstIndex));
                return rt.getString();
            case 3:
                CellType cellType2 = getBaseCellType(false);
                String textValue = this._cell.getV();
                int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType2.ordinal()];
                if (i != 1 && i != 2) {
                    if (i == 5) {
                        if (TRUE_AS_STRING.equals(textValue)) {
                            return TRUE;
                        }
                        if (FALSE_AS_STRING.equals(textValue)) {
                            return FALSE;
                        }
                        throw new IllegalStateException("Unexpected boolean cached formula value '" + textValue + "'.");
                    }
                    if (i != 6) {
                        throw new IllegalStateException("Unexpected formula result type (" + cellType2 + ")");
                    }
                }
                return textValue;
            case 4:
                return "";
            case 5:
                return TRUE_AS_STRING.equals(this._cell.getV()) ? TRUE : FALSE;
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellRangeAddress getArrayFormulaRange() {
        XSSFCell cell = getSheet().getFirstCellInArrayFormula(this);
        if (cell == null) {
            throw new IllegalStateException("Cell " + getReference() + " is not part of an array formula.");
        }
        String formulaRef = cell._cell.getF().getRef();
        return CellRangeAddress.valueOf(formulaRef);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public boolean isPartOfArrayFormulaGroup() {
        return getSheet().isCellInArrayFormulaContext(this);
    }

    void notifyArrayFormulaChanging(String msg) {
        if (isPartOfArrayFormulaGroup()) {
            CellRangeAddress cra = getArrayFormulaRange();
            if (cra.getNumberOfCells() > 1) {
                throw new IllegalStateException(msg);
            }
            getRow().getSheet().removeArrayFormula(this);
        }
    }

    void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference(this);
        String msg = "Cell " + ref.formatAsString() + " is part of a multi-cell array formula. You cannot change part of an array.";
        notifyArrayFormulaChanging(msg);
    }
}
