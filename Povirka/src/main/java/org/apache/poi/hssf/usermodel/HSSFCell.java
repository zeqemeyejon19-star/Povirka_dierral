package org.apache.poi.hssf.usermodel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class HSSFCell implements Cell {
    public static final short ENCODING_COMPRESSED_UNICODE = 0;
    public static final short ENCODING_UNCHANGED = -1;
    public static final short ENCODING_UTF_16 = 1;
    private static final String FILE_FORMAT_NAME = "BIFF8";
    private final HSSFWorkbook _book;
    private CellType _cellType;
    private HSSFComment _comment;
    private CellValueRecordInterface _record;
    private final HSSFSheet _sheet;
    private HSSFRichTextString _stringValue;
    public static final int LAST_COLUMN_NUMBER = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    private static final String LAST_COLUMN_NAME = SpreadsheetVersion.EXCEL97.getLastColumnName();

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col) {
        checkBounds(col);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        setCellType(CellType.BLANK, false, row, col, xfindex);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFSheet getSheet() {
        return this._sheet;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFRow getRow() {
        int rowIndex = getRowIndex();
        return this._sheet.getRow(rowIndex);
    }

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, int row, short col, CellType type) {
        checkBounds(col);
        this._cellType = CellType._NONE;
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        setCellType(type, false, row, col, xfindex);
    }

    protected HSSFCell(HSSFWorkbook book, HSSFSheet sheet, CellValueRecordInterface cval) {
        this._record = cval;
        this._cellType = determineType(cval);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 1) {
            this._stringValue = new HSSFRichTextString(book.getWorkbook(), (LabelSSTRecord) cval);
        } else if (i == 3) {
            this._stringValue = new HSSFRichTextString(((FormulaRecordAggregate) cval).getStringValue());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static CellType determineType(CellValueRecordInterface cellValueRecordInterface) {
        if (cellValueRecordInterface instanceof FormulaRecordAggregate) {
            return CellType.FORMULA;
        }
        Record record = (Record) cellValueRecordInterface;
        short sid = record.getSid();
        if (sid == 253) {
            return CellType.STRING;
        }
        if (sid == 513) {
            return CellType.BLANK;
        }
        if (sid == 515) {
            return CellType.NUMERIC;
        }
        if (sid == 517) {
            BoolErrRecord boolErrRecord = (BoolErrRecord) record;
            return boolErrRecord.isBoolean() ? CellType.BOOLEAN : CellType.ERROR;
        }
        throw new RuntimeException("Bad cell value rec (" + cellValueRecordInterface.getClass().getName() + ")");
    }

    protected InternalWorkbook getBoundWorkbook() {
        return this._book.getWorkbook();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getRowIndex() {
        return this._record.getRow();
    }

    protected void updateCellNum(short num) {
        this._record.setColumn(num);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getColumnIndex() {
        return this._record.getColumn() & 65535;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellAddress getAddress() {
        return new CellAddress(this);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellType(int cellType) {
        setCellType(CellType.forInt(cellType));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellType(CellType cellType) {
        notifyFormulaChanging();
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        setCellType(cellType, true, row, col, styleIndex);
    }

    private void setCellType(CellType cellType, boolean setValue, int row, short col, short styleIndex) {
        LabelSSTRecord lrec;
        FormulaRecordAggregate frec;
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
                if (cellType == this._cellType) {
                    lrec = (LabelSSTRecord) this._record;
                } else {
                    lrec = new LabelSSTRecord();
                    lrec.setColumn(col);
                    lrec.setRow(row);
                    lrec.setXFIndex(styleIndex);
                }
                if (setValue) {
                    String str = convertCellValueToString();
                    if (str == null) {
                        setCellType(CellType.BLANK, false, row, col, styleIndex);
                        return;
                    }
                    int sstIndex = this._book.getWorkbook().addSSTString(new UnicodeString(str));
                    lrec.setSSTIndex(sstIndex);
                    UnicodeString us = this._book.getWorkbook().getSSTString(sstIndex);
                    HSSFRichTextString hSSFRichTextString = new HSSFRichTextString();
                    this._stringValue = hSSFRichTextString;
                    hSSFRichTextString.setUnicodeString(us);
                }
                this._record = lrec;
                break;
            case 2:
                BlankRecord brec = cellType != this._cellType ? new BlankRecord() : (BlankRecord) this._record;
                brec.setColumn(col);
                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                this._record = brec;
                break;
            case 3:
                if (cellType != this._cellType) {
                    frec = this._sheet.getSheet().getRowsAggregate().createFormula(row, col);
                } else {
                    frec = (FormulaRecordAggregate) this._record;
                    frec.setRow(row);
                    frec.setColumn(col);
                }
                if (setValue) {
                    frec.getFormulaRecord().setValue(getNumericCellValue());
                }
                frec.setXFIndex(styleIndex);
                this._record = frec;
                break;
            case 4:
                NumberRecord nrec = cellType != this._cellType ? new NumberRecord() : (NumberRecord) this._record;
                nrec.setColumn(col);
                if (setValue) {
                    nrec.setValue(getNumericCellValue());
                }
                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                this._record = nrec;
                break;
            case 5:
                BoolErrRecord boolRec = cellType != this._cellType ? new BoolErrRecord() : (BoolErrRecord) this._record;
                boolRec.setColumn(col);
                if (setValue) {
                    boolRec.setValue(convertCellValueToBoolean());
                }
                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                this._record = boolRec;
                break;
            case 6:
                BoolErrRecord errRec = cellType != this._cellType ? new BoolErrRecord() : (BoolErrRecord) this._record;
                errRec.setColumn(col);
                if (setValue) {
                    errRec.setValue(FormulaError.VALUE.getCode());
                }
                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                this._record = errRec;
                break;
            default:
                throw new IllegalStateException("Invalid cell type: " + cellType);
        }
        CellType cellType2 = this._cellType;
        if (cellType != cellType2 && cellType2 != CellType._NONE) {
            this._sheet.getSheet().replaceValueRecord(this._record);
        }
        this._cellType = cellType;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getCellType() {
        return getCellTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCellTypeEnum() {
        return this._cellType;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(double value) {
        if (Double.isInfinite(value)) {
            setCellErrorValue(FormulaError.DIV0.getCode());
            return;
        }
        if (Double.isNaN(value)) {
            setCellErrorValue(FormulaError.NUM.getCode());
            return;
        }
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i != 3) {
            if (i != 4) {
                setCellType(CellType.NUMERIC, false, row, col, styleIndex);
            }
            ((NumberRecord) this._record).setValue(value);
            return;
        }
        ((FormulaRecordAggregate) this._record).setCachedDoubleResult(value);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(Date value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(Calendar value) {
        setCellValue(HSSFDateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(String value) {
        HSSFRichTextString str = value == null ? null : new HSSFRichTextString(value);
        setCellValue(str);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(RichTextString value) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        if (value == null) {
            notifyFormulaChanging();
            setCellType(CellType.BLANK, false, row, col, styleIndex);
            return;
        }
        if (value.length() > SpreadsheetVersion.EXCEL97.getMaxTextLength()) {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }
        if (this._cellType == CellType.FORMULA) {
            FormulaRecordAggregate fr = (FormulaRecordAggregate) this._record;
            fr.setCachedStringResult(value.getString());
            this._stringValue = new HSSFRichTextString(value.getString());
            return;
        }
        if (this._cellType != CellType.STRING) {
            setCellType(CellType.STRING, false, row, col, styleIndex);
        }
        HSSFRichTextString hvalue = (HSSFRichTextString) value;
        UnicodeString str = hvalue.getUnicodeString();
        int index = this._book.getWorkbook().addSSTString(str);
        ((LabelSSTRecord) this._record).setSSTIndex(index);
        this._stringValue = hvalue;
        hvalue.setWorkbookReferences(this._book.getWorkbook(), (LabelSSTRecord) this._record);
        this._stringValue.setUnicodeString(this._book.getWorkbook().getSSTString(index));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellFormula(String formula) {
        if (isPartOfArrayFormulaGroup()) {
            notifyArrayFormulaChanging();
        }
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        if (formula == null) {
            notifyFormulaChanging();
            setCellType(CellType.BLANK, false, row, col, styleIndex);
            return;
        }
        int sheetIndex = this._book.getSheetIndex(this._sheet);
        Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._book, FormulaType.CELL, sheetIndex);
        setCellType(CellType.FORMULA, false, row, col, styleIndex);
        FormulaRecordAggregate agg = (FormulaRecordAggregate) this._record;
        FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions((short) 2);
        frec.setValue(0.0d);
        if (agg.getXFIndex() == 0) {
            agg.setXFIndex((short) 15);
        }
        agg.setParsedExpression(ptgs);
    }

    private void notifyFormulaChanging() {
        CellValueRecordInterface cellValueRecordInterface = this._record;
        if (cellValueRecordInterface instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate) cellValueRecordInterface).notifyFormulaChanging();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getCellFormula() {
        CellValueRecordInterface cellValueRecordInterface = this._record;
        if (!(cellValueRecordInterface instanceof FormulaRecordAggregate)) {
            throw typeMismatch(CellType.FORMULA, this._cellType, true);
        }
        return HSSFFormulaParser.toFormulaString(this._book, ((FormulaRecordAggregate) cellValueRecordInterface).getFormulaTokens());
    }

    private static RuntimeException typeMismatch(CellType expectedTypeCode, CellType actualTypeCode, boolean isFormulaCell) {
        String msg = "Cannot get a " + expectedTypeCode + " value from a " + actualTypeCode + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private static void checkFormulaCachedValueType(CellType expectedTypeCode, FormulaRecord fr) {
        CellType cachedValueType = CellType.forInt(fr.getCachedResultType());
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public double getNumericCellValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 2) {
            return 0.0d;
        }
        if (i != 3) {
            if (i == 4) {
                return ((NumberRecord) this._record).getValue();
            }
            throw typeMismatch(CellType.NUMERIC, this._cellType, false);
        }
        FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
        checkFormulaCachedValueType(CellType.NUMERIC, fr);
        return fr.getValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Date getDateCellValue() {
        if (this._cellType == CellType.BLANK) {
            return null;
        }
        double value = getNumericCellValue();
        if (this._book.getWorkbook().isUsing1904DateWindowing()) {
            return HSSFDateUtil.getJavaDate(value, true);
        }
        return HSSFDateUtil.getJavaDate(value, false);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getStringCellValue() {
        HSSFRichTextString str = getRichStringCellValue();
        return str.getString();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFRichTextString getRichStringCellValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 1) {
            return this._stringValue;
        }
        if (i == 2) {
            return new HSSFRichTextString("");
        }
        if (i != 3) {
            throw typeMismatch(CellType.STRING, this._cellType, false);
        }
        FormulaRecordAggregate fra = (FormulaRecordAggregate) this._record;
        checkFormulaCachedValueType(CellType.STRING, fra.getFormulaRecord());
        String strVal = fra.getStringValue();
        return new HSSFRichTextString(strVal != null ? strVal : "");
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(boolean value) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i != 3) {
            if (i != 5) {
                setCellType(CellType.BOOLEAN, false, row, col, styleIndex);
            }
            ((BoolErrRecord) this._record).setValue(value);
            return;
        }
        ((FormulaRecordAggregate) this._record).setCachedBooleanResult(value);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellErrorValue(byte errorCode) {
        FormulaError error = FormulaError.forInt(errorCode);
        setCellErrorValue(error);
    }

    public void setCellErrorValue(FormulaError error) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i != 3) {
            if (i != 6) {
                setCellType(CellType.ERROR, false, row, col, styleIndex);
            }
            ((BoolErrRecord) this._record).setValue(error);
            return;
        }
        ((FormulaRecordAggregate) this._record).setCachedErrorResult(error.getCode());
    }

    private boolean convertCellValueToBoolean() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()]) {
            case 1:
                int sstIndex = ((LabelSSTRecord) this._record).getSSTIndex();
                String text = this._book.getWorkbook().getSSTString(sstIndex).getString();
                return Boolean.valueOf(text).booleanValue();
            case 2:
            case 6:
                return false;
            case 3:
                FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
                checkFormulaCachedValueType(CellType.BOOLEAN, fr);
                return fr.getCachedBooleanValue();
            case 4:
                return ((NumberRecord) this._record).getValue() != 0.0d;
            case 5:
                return ((BoolErrRecord) this._record).getBooleanValue();
            default:
                throw new RuntimeException("Unexpected cell type (" + this._cellType + ")");
        }
    }

    private String convertCellValueToString() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()]) {
            case 1:
                int sstIndex = ((LabelSSTRecord) this._record).getSSTIndex();
                return this._book.getWorkbook().getSSTString(sstIndex).getString();
            case 2:
                return "";
            case 3:
                FormulaRecordAggregate fra = (FormulaRecordAggregate) this._record;
                FormulaRecord fr = fra.getFormulaRecord();
                int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.forInt(fr.getCachedResultType()).ordinal()];
                if (i == 1) {
                    return fra.getStringValue();
                }
                if (i == 4) {
                    return NumberToTextConverter.toText(fr.getValue());
                }
                if (i == 5) {
                    return fr.getCachedBooleanValue() ? "TRUE" : "FALSE";
                }
                if (i == 6) {
                    return FormulaError.forInt(fr.getCachedErrorValue()).getString();
                }
                throw new IllegalStateException("Unexpected formula result type (" + this._cellType + ")");
            case 4:
                return NumberToTextConverter.toText(((NumberRecord) this._record).getValue());
            case 5:
                return ((BoolErrRecord) this._record).getBooleanValue() ? "TRUE" : "FALSE";
            case 6:
                return FormulaError.forInt(((BoolErrRecord) this._record).getErrorValue()).getString();
            default:
                throw new IllegalStateException("Unexpected cell type (" + this._cellType + ")");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public boolean getBooleanCellValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 2) {
            return false;
        }
        if (i != 3) {
            if (i == 5) {
                return ((BoolErrRecord) this._record).getBooleanValue();
            }
            throw typeMismatch(CellType.BOOLEAN, this._cellType, false);
        }
        FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
        checkFormulaCachedValueType(CellType.BOOLEAN, fr);
        return fr.getCachedBooleanValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public byte getErrorCellValue() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i != 3) {
            if (i == 6) {
                return ((BoolErrRecord) this._record).getErrorValue();
            }
            throw typeMismatch(CellType.ERROR, this._cellType, false);
        }
        FormulaRecord fr = ((FormulaRecordAggregate) this._record).getFormulaRecord();
        checkFormulaCachedValueType(CellType.ERROR, fr);
        return (byte) fr.getCachedErrorValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellStyle(CellStyle style) {
        setCellStyle((HSSFCellStyle) style);
    }

    public void setCellStyle(HSSFCellStyle style) {
        short styleIndex;
        if (style == null) {
            this._record.setXFIndex((short) 15);
            return;
        }
        style.verifyBelongsToWorkbook(this._book);
        if (style.getUserStyleName() != null) {
            styleIndex = applyUserCellStyle(style);
        } else {
            styleIndex = style.getIndex();
        }
        this._record.setXFIndex(styleIndex);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFCellStyle getCellStyle() {
        short styleIndex = this._record.getXFIndex();
        ExtendedFormatRecord xf = this._book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this._book);
    }

    protected CellValueRecordInterface getCellValueRecord() {
        return this._record;
    }

    private static void checkBounds(int cellIndex) {
        if (cellIndex < 0 || cellIndex > LAST_COLUMN_NUMBER) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + FILE_FORMAT_NAME + " is (0.." + LAST_COLUMN_NUMBER + ") or ('A'..'" + LAST_COLUMN_NAME + "')");
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setAsActiveCell() {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        this._sheet.getSheet().setActiveCellRow(row);
        this._sheet.getSheet().setActiveCellCol(col);
    }

    public String toString() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[getCellTypeEnum().ordinal()]) {
            case 1:
                return getStringCellValue();
            case 2:
                return "";
            case 3:
                return getCellFormula();
            case 4:
                if (HSSFDateUtil.isCellDateFormatted(this)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(getDateCellValue());
                }
                return String.valueOf(getNumericCellValue());
            case 5:
                return getBooleanCellValue() ? "TRUE" : "FALSE";
            case 6:
                return ErrorEval.getText(((BoolErrRecord) this._record).getErrorValue());
            default:
                return "Unknown Cell Type: " + getCellTypeEnum();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellComment(Comment comment) {
        if (comment == null) {
            removeCellComment();
            return;
        }
        comment.setRow(this._record.getRow());
        comment.setColumn(this._record.getColumn());
        this._comment = (HSSFComment) comment;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFComment getCellComment() {
        if (this._comment == null) {
            this._comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        }
        return this._comment;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeCellComment() {
        HSSFComment comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        this._comment = null;
        if (comment == null) {
            return;
        }
        this._sheet.getDrawingPatriarch().removeShape(comment);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public HSSFHyperlink getHyperlink() {
        return this._sheet.getHyperlink(this._record.getRow(), (int) this._record.getColumn());
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setHyperlink(Hyperlink hyperlink) {
        if (hyperlink == null) {
            removeHyperlink();
            return;
        }
        HSSFHyperlink link = (HSSFHyperlink) hyperlink;
        link.setFirstRow(this._record.getRow());
        link.setLastRow(this._record.getRow());
        link.setFirstColumn(this._record.getColumn());
        link.setLastColumn(this._record.getColumn());
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[link.getTypeEnum().ordinal()];
        if (i == 1 || i == 2) {
            link.setLabel("url");
        } else if (i == 3) {
            link.setLabel("file");
        } else if (i == 4) {
            link.setLabel("place");
        }
        List<RecordBase> records = this._sheet.getSheet().getRecords();
        int eofLoc = records.size() - 1;
        records.add(eofLoc, link.record);
    }

    /* JADX INFO: renamed from: org.apache.poi.hssf.usermodel.HSSFCell$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[HyperlinkType.values().length];
            $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType = iArr;
            try {
                iArr[HyperlinkType.EMAIL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.URL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.FILE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.DOCUMENT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            int[] iArr2 = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr2;
            try {
                iArr2[CellType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BLANK.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeHyperlink() {
        Iterator<RecordBase> it = this._sheet.getSheet().getRecords().iterator();
        while (it.hasNext()) {
            RecordBase rec = it.next();
            if (rec instanceof HyperlinkRecord) {
                HyperlinkRecord link = (HyperlinkRecord) rec;
                if (link.getFirstColumn() == this._record.getColumn() && link.getFirstRow() == this._record.getRow()) {
                    it.remove();
                    return;
                }
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getCachedFormulaResultType() {
        return getCachedFormulaResultTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCachedFormulaResultTypeEnum() {
        if (this._cellType != CellType.FORMULA) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        int code = ((FormulaRecordAggregate) this._record).getFormulaRecord().getCachedResultType();
        return CellType.forInt(code);
    }

    void setCellArrayFormula(CellRangeAddress range) {
        int row = this._record.getRow();
        short col = this._record.getColumn();
        short styleIndex = this._record.getXFIndex();
        setCellType(CellType.FORMULA, false, row, col, styleIndex);
        Ptg[] ptgsForCell = {new ExpPtg(range.getFirstRow(), range.getFirstColumn())};
        FormulaRecordAggregate agg = (FormulaRecordAggregate) this._record;
        agg.setParsedExpression(ptgsForCell);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellRangeAddress getArrayFormulaRange() {
        if (this._cellType != CellType.FORMULA) {
            String ref = new CellReference(this).formatAsString();
            throw new IllegalStateException("Cell " + ref + " is not part of an array formula.");
        }
        return ((FormulaRecordAggregate) this._record).getArrayFormulaRange();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public boolean isPartOfArrayFormulaGroup() {
        if (this._cellType != CellType.FORMULA) {
            return false;
        }
        return ((FormulaRecordAggregate) this._record).isPartOfArrayFormula();
    }

    void notifyArrayFormulaChanging(String msg) {
        CellRangeAddress cra = getArrayFormulaRange();
        if (cra.getNumberOfCells() > 1) {
            throw new IllegalStateException(msg);
        }
        getRow().getSheet().removeArrayFormula(this);
    }

    void notifyArrayFormulaChanging() {
        CellReference ref = new CellReference(this);
        String msg = "Cell " + ref.formatAsString() + " is part of a multi-cell array formula. You cannot change part of an array.";
        notifyArrayFormulaChanging(msg);
    }

    private short applyUserCellStyle(HSSFCellStyle style) {
        if (style.getUserStyleName() == null) {
            throw new IllegalArgumentException("Expected user-defined style");
        }
        InternalWorkbook iwb = this._book.getWorkbook();
        short userXf = -1;
        int numfmt = iwb.getNumExFormats();
        short i = 0;
        while (true) {
            if (i >= numfmt) {
                break;
            }
            ExtendedFormatRecord xf = iwb.getExFormatAt(i);
            if (xf.getXFType() != 0 || xf.getParentIndex() != style.getIndex()) {
                i = (short) (i + 1);
            } else {
                userXf = i;
                break;
            }
        }
        if (userXf == -1) {
            ExtendedFormatRecord xfr = iwb.createCellXF();
            xfr.cloneStyleFrom(iwb.getExFormatAt(style.getIndex()));
            xfr.setIndentionOptions((short) 0);
            xfr.setXFType((short) 0);
            xfr.setParentIndex(style.getIndex());
            short styleIndex = (short) numfmt;
            return styleIndex;
        }
        short styleIndex2 = userXf;
        return styleIndex2;
    }
}
