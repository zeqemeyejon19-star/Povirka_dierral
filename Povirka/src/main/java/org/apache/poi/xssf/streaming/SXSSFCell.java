package org.apache.poi.xssf.streaming;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
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
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/* JADX INFO: loaded from: classes.dex */
public class SXSSFCell implements Cell {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SXSSFCell.class);
    private Property _firstProperty;
    private final SXSSFRow _row;
    private CellStyle _style;
    private Value _value;

    interface Value {
        CellType getType();
    }

    public SXSSFCell(SXSSFRow row, CellType cellType) {
        this._row = row;
        setType(cellType);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getColumnIndex() {
        return this._row.getCellIndex(this);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getRowIndex() {
        return this._row.getRowNum();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellAddress getAddress() {
        return new CellAddress(this);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public SXSSFSheet getSheet() {
        return this._row.getSheet();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Row getRow() {
        return this._row;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellType(int cellType) {
        ensureType(CellType.forInt(cellType));
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellType(CellType cellType) {
        ensureType(cellType);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getCellType() {
        return getCellTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCellTypeEnum() {
        return this._value.getType();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public int getCachedFormulaResultType() {
        return getCachedFormulaResultTypeEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellType getCachedFormulaResultTypeEnum() {
        if (this._value.getType() != CellType.FORMULA) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return ((FormulaValue) this._value).getFormulaType();
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
        ensureTypeOrFormulaType(CellType.NUMERIC);
        if (this._value.getType() == CellType.FORMULA) {
            ((NumericFormulaValue) this._value).setPreEvaluatedValue(value);
        } else {
            ((NumericValue) this._value).setValue(value);
        }
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

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(RichTextString value) {
        XSSFRichTextString xvalue = (XSSFRichTextString) value;
        if (xvalue != null && xvalue.getString() != null) {
            ensureRichTextStringType();
            if (xvalue.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
                throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
            }
            if (xvalue.hasFormatting()) {
                logger.log(5, "SXSSF doesn't support Shared Strings, rich text formatting information has be lost");
            }
            ((RichTextValue) this._value).setValue(xvalue);
            return;
        }
        setCellType(CellType.BLANK);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(String value) {
        if (value != null) {
            ensureTypeOrFormulaType(CellType.STRING);
            if (value.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
                throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
            }
            if (this._value.getType() == CellType.FORMULA) {
                Value value2 = this._value;
                if (value2 instanceof NumericFormulaValue) {
                    ((NumericFormulaValue) value2).setPreEvaluatedValue(Double.parseDouble(value));
                    return;
                } else {
                    ((StringFormulaValue) value2).setPreEvaluatedValue(value);
                    return;
                }
            }
            ((PlainStringValue) this._value).setValue(value);
            return;
        }
        setCellType(CellType.BLANK);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellFormula(String formula) throws FormulaParseException {
        if (formula == null) {
            setType(CellType.BLANK);
        } else {
            ensureFormulaType(computeTypeFromFormula(formula));
            ((FormulaValue) this._value).setValue(formula);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getCellFormula() {
        if (this._value.getType() != CellType.FORMULA) {
            throw typeMismatch(CellType.FORMULA, this._value.getType(), false);
        }
        return ((FormulaValue) this._value).getValue();
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.streaming.SXSSFCell$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.BLANK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 3;
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

    @Override // org.apache.poi.ss.usermodel.Cell
    public double getNumericCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            return 0.0d;
        }
        if (i != 2) {
            if (i == 3) {
                return ((NumericValue) this._value).getValue();
            }
            throw typeMismatch(CellType.NUMERIC, cellType, false);
        }
        FormulaValue fv = (FormulaValue) this._value;
        if (fv.getFormulaType() != CellType.NUMERIC) {
            throw typeMismatch(CellType.NUMERIC, CellType.FORMULA, false);
        }
        return ((NumericFormulaValue) this._value).getPreEvaluatedValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Date getDateCellValue() {
        CellType cellType = getCellTypeEnum();
        if (cellType == CellType.BLANK) {
            return null;
        }
        double value = getNumericCellValue();
        boolean date1904 = getSheet().getWorkbook().isDate1904();
        return DateUtil.getJavaDate(value, date1904);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public RichTextString getRichStringCellValue() {
        CellType cellType = getCellTypeEnum();
        if (getCellTypeEnum() != CellType.STRING) {
            throw typeMismatch(CellType.STRING, cellType, false);
        }
        StringValue sval = (StringValue) this._value;
        if (sval.isRichText()) {
            return ((RichTextValue) this._value).getValue();
        }
        String plainText = getStringCellValue();
        return getSheet().getWorkbook().getCreationHelper().createRichTextString(plainText);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public String getStringCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            return "";
        }
        if (i == 2) {
            FormulaValue fv = (FormulaValue) this._value;
            if (fv.getFormulaType() != CellType.STRING) {
                throw typeMismatch(CellType.STRING, CellType.FORMULA, false);
            }
            return ((StringFormulaValue) this._value).getPreEvaluatedValue();
        }
        if (i == 4) {
            if (((StringValue) this._value).isRichText()) {
                return ((RichTextValue) this._value).getValue().getString();
            }
            return ((PlainStringValue) this._value).getValue();
        }
        throw typeMismatch(CellType.STRING, cellType, false);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellValue(boolean value) {
        ensureTypeOrFormulaType(CellType.BOOLEAN);
        if (this._value.getType() == CellType.FORMULA) {
            ((BooleanFormulaValue) this._value).setPreEvaluatedValue(value);
        } else {
            ((BooleanValue) this._value).setValue(value);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellErrorValue(byte value) {
        ensureType(CellType.ERROR);
        if (this._value.getType() == CellType.FORMULA) {
            ((ErrorFormulaValue) this._value).setPreEvaluatedValue(value);
        } else {
            ((ErrorValue) this._value).setValue(value);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public boolean getBooleanCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            return false;
        }
        if (i != 2) {
            if (i == 5) {
                return ((BooleanValue) this._value).getValue();
            }
            throw typeMismatch(CellType.BOOLEAN, cellType, false);
        }
        FormulaValue fv = (FormulaValue) this._value;
        if (fv.getFormulaType() != CellType.BOOLEAN) {
            throw typeMismatch(CellType.BOOLEAN, CellType.FORMULA, false);
        }
        return ((BooleanFormulaValue) this._value).getPreEvaluatedValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public byte getErrorCellValue() {
        CellType cellType = getCellTypeEnum();
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            return (byte) 0;
        }
        if (i != 2) {
            if (i == 6) {
                return ((ErrorValue) this._value).getValue();
            }
            throw typeMismatch(CellType.ERROR, cellType, false);
        }
        FormulaValue fv = (FormulaValue) this._value;
        if (fv.getFormulaType() != CellType.ERROR) {
            throw typeMismatch(CellType.ERROR, CellType.FORMULA, false);
        }
        return ((ErrorFormulaValue) this._value).getPreEvaluatedValue();
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellStyle(CellStyle style) {
        this._style = style;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public CellStyle getCellStyle() {
        CellStyle cellStyle = this._style;
        if (cellStyle == null) {
            SXSSFWorkbook wb = (SXSSFWorkbook) getRow().getSheet().getWorkbook();
            return wb.getCellStyleAt(0);
        }
        return cellStyle;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setAsActiveCell() {
        getSheet().setActiveCell(getAddress());
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setCellComment(Comment comment) {
        setProperty(1, comment);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Comment getCellComment() {
        return (Comment) getPropertyValue(1);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeCellComment() {
        removeProperty(1);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public Hyperlink getHyperlink() {
        return (Hyperlink) getPropertyValue(2);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void setHyperlink(Hyperlink link) {
        if (link == null) {
            removeHyperlink();
            return;
        }
        setProperty(2, link);
        XSSFHyperlink xssfobj = (XSSFHyperlink) link;
        CellReference ref = new CellReference(getRowIndex(), getColumnIndex());
        xssfobj.setCellReference(ref);
        getSheet()._sh.addHyperlink(xssfobj);
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    public void removeHyperlink() {
        removeProperty(2);
        getSheet()._sh.removeHyperlink(getRowIndex(), getColumnIndex());
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    @NotImplemented
    public CellRangeAddress getArrayFormulaRange() {
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Cell
    @NotImplemented
    public boolean isPartOfArrayFormulaGroup() {
        return false;
    }

    public String toString() {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[getCellTypeEnum().ordinal()]) {
            case 1:
                return "";
            case 2:
                return getCellFormula();
            case 3:
                if (DateUtil.isCellDateFormatted(this)) {
                    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", LocaleUtil.getUserLocale());
                    sdf.setTimeZone(LocaleUtil.getUserTimeZone());
                    return sdf.format(getDateCellValue());
                }
                return getNumericCellValue() + "";
            case 4:
                return getRichStringCellValue().toString();
            case 5:
                return getBooleanCellValue() ? "TRUE" : "FALSE";
            case 6:
                return ErrorEval.getText(getErrorCellValue());
            default:
                return "Unknown Cell Type: " + getCellTypeEnum();
        }
    }

    void removeProperty(int type) {
        Property current = this._firstProperty;
        Property previous = null;
        while (current != null && current.getType() != type) {
            previous = current;
            current = current._next;
        }
        if (current != null) {
            if (previous != null) {
                previous._next = current._next;
            } else {
                this._firstProperty = current._next;
            }
        }
    }

    void setProperty(int type, Object value) {
        Property current;
        Property current2 = this._firstProperty;
        Property previous = null;
        while (current2 != null && current2.getType() != type) {
            previous = current2;
            current2 = current2._next;
        }
        if (current2 != null) {
            current2.setValue(value);
            return;
        }
        if (type == 1) {
            current = new CommentProperty(value);
        } else if (type == 2) {
            current = new HyperlinkProperty(value);
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (previous != null) {
            previous._next = current;
        } else {
            this._firstProperty = current;
        }
    }

    Object getPropertyValue(int type) {
        return getPropertyValue(type, null);
    }

    Object getPropertyValue(int type, String defaultValue) {
        Property current = this._firstProperty;
        while (current != null && current.getType() != type) {
            current = current._next;
        }
        return current == null ? defaultValue : current.getValue();
    }

    void ensurePlainStringType() {
        if (this._value.getType() != CellType.STRING || ((StringValue) this._value).isRichText()) {
            this._value = new PlainStringValue();
        }
    }

    void ensureRichTextStringType() {
        if (this._value.getType() != CellType.STRING || !((StringValue) this._value).isRichText()) {
            this._value = new RichTextValue();
        }
    }

    void ensureType(CellType type) {
        if (this._value.getType() != type) {
            setType(type);
        }
    }

    void ensureFormulaType(CellType type) {
        if (this._value.getType() != CellType.FORMULA || ((FormulaValue) this._value).getFormulaType() != type) {
            setFormulaType(type);
        }
    }

    void ensureTypeOrFormulaType(CellType type) {
        if (this._value.getType() == type) {
            if (type == CellType.STRING && ((StringValue) this._value).isRichText()) {
                setType(CellType.STRING);
                return;
            }
            return;
        }
        if (this._value.getType() == CellType.FORMULA) {
            if (((FormulaValue) this._value).getFormulaType() == type) {
                return;
            }
            setFormulaType(type);
            return;
        }
        setType(type);
    }

    void setType(CellType type) {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[type.ordinal()]) {
            case 1:
                this._value = new BlankValue();
                return;
            case 2:
                this._value = new NumericFormulaValue();
                return;
            case 3:
                this._value = new NumericValue();
                return;
            case 4:
                PlainStringValue sval = new PlainStringValue();
                if (this._value != null) {
                    String str = convertCellValueToString();
                    sval.setValue(str);
                }
                this._value = sval;
                return;
            case 5:
                BooleanValue bval = new BooleanValue();
                if (this._value != null) {
                    boolean val = convertCellValueToBoolean();
                    bval.setValue(val);
                }
                this._value = bval;
                return;
            case 6:
                this._value = new ErrorValue();
                return;
            default:
                throw new IllegalArgumentException("Illegal type " + type);
        }
    }

    void setFormulaType(CellType type) {
        Value prevValue = this._value;
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[type.ordinal()];
        if (i == 3) {
            this._value = new NumericFormulaValue();
        } else if (i == 4) {
            this._value = new StringFormulaValue();
        } else if (i == 5) {
            this._value = new BooleanFormulaValue();
        } else if (i == 6) {
            this._value = new ErrorFormulaValue();
        } else {
            throw new IllegalArgumentException("Illegal type " + type);
        }
        if (prevValue instanceof FormulaValue) {
            ((FormulaValue) this._value)._value = ((FormulaValue) prevValue)._value;
        }
    }

    @NotImplemented
    CellType computeTypeFromFormula(String formula) {
        return CellType.NUMERIC;
    }

    private static RuntimeException typeMismatch(CellType expectedTypeCode, CellType actualTypeCode, boolean isFormulaCell) {
        String msg = "Cannot get a " + expectedTypeCode + " value from a " + actualTypeCode + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }

    private boolean convertCellValueToBoolean() {
        CellType cellType = getCellTypeEnum();
        if (cellType == CellType.FORMULA) {
            cellType = getCachedFormulaResultTypeEnum();
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i != 1) {
            if (i == 3) {
                return getNumericCellValue() != 0.0d;
            }
            if (i == 4) {
                String text = getStringCellValue();
                return Boolean.parseBoolean(text);
            }
            if (i == 5) {
                return getBooleanCellValue();
            }
            if (i != 6) {
                throw new RuntimeException("Unexpected cell type (" + cellType + ")");
            }
        }
        return false;
    }

    private String convertCellValueToString() {
        CellType cellType = getCellTypeEnum();
        return convertCellValueToString(cellType);
    }

    private String convertCellValueToString(CellType cellType) {
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()]) {
            case 1:
                return "";
            case 2:
                Value value = this._value;
                if (value != null) {
                    FormulaValue fv = (FormulaValue) value;
                    if (fv.getFormulaType() != CellType.FORMULA) {
                        return convertCellValueToString(fv.getFormulaType());
                    }
                }
                return "";
            case 3:
                return Double.toString(getNumericCellValue());
            case 4:
                return getStringCellValue();
            case 5:
                return getBooleanCellValue() ? "TRUE" : "FALSE";
            case 6:
                byte errVal = getErrorCellValue();
                return FormulaError.forInt(errVal).getString();
            default:
                throw new IllegalStateException("Unexpected cell type (" + cellType + ")");
        }
    }

    static abstract class Property {
        static final int COMMENT = 1;
        static final int HYPERLINK = 2;
        Property _next;
        Object _value;

        abstract int getType();

        public Property(Object value) {
            this._value = value;
        }

        void setValue(Object value) {
            this._value = value;
        }

        Object getValue() {
            return this._value;
        }
    }

    static class CommentProperty extends Property {
        public CommentProperty(Object value) {
            super(value);
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Property
        public int getType() {
            return 1;
        }
    }

    static class HyperlinkProperty extends Property {
        public HyperlinkProperty(Object value) {
            super(value);
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Property
        public int getType() {
            return 2;
        }
    }

    static class NumericValue implements Value {
        double _value;

        NumericValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.NUMERIC;
        }

        void setValue(double value) {
            this._value = value;
        }

        double getValue() {
            return this._value;
        }
    }

    static abstract class StringValue implements Value {
        abstract boolean isRichText();

        StringValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.STRING;
        }
    }

    static class PlainStringValue extends StringValue {
        String _value;

        PlainStringValue() {
        }

        void setValue(String value) {
            this._value = value;
        }

        String getValue() {
            return this._value;
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.StringValue
        boolean isRichText() {
            return false;
        }
    }

    static class RichTextValue extends StringValue {
        RichTextString _value;

        RichTextValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.StringValue, org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.STRING;
        }

        void setValue(RichTextString value) {
            this._value = value;
        }

        RichTextString getValue() {
            return this._value;
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.StringValue
        boolean isRichText() {
            return true;
        }
    }

    static abstract class FormulaValue implements Value {
        String _value;

        abstract CellType getFormulaType();

        FormulaValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.FORMULA;
        }

        void setValue(String value) {
            this._value = value;
        }

        String getValue() {
            return this._value;
        }
    }

    static class NumericFormulaValue extends FormulaValue {
        double _preEvaluatedValue;

        NumericFormulaValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.FormulaValue
        CellType getFormulaType() {
            return CellType.NUMERIC;
        }

        void setPreEvaluatedValue(double value) {
            this._preEvaluatedValue = value;
        }

        double getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }

    static class StringFormulaValue extends FormulaValue {
        String _preEvaluatedValue;

        StringFormulaValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.FormulaValue
        CellType getFormulaType() {
            return CellType.STRING;
        }

        void setPreEvaluatedValue(String value) {
            this._preEvaluatedValue = value;
        }

        String getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }

    static class BooleanFormulaValue extends FormulaValue {
        boolean _preEvaluatedValue;

        BooleanFormulaValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.FormulaValue
        CellType getFormulaType() {
            return CellType.BOOLEAN;
        }

        void setPreEvaluatedValue(boolean value) {
            this._preEvaluatedValue = value;
        }

        boolean getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }

    static class ErrorFormulaValue extends FormulaValue {
        byte _preEvaluatedValue;

        ErrorFormulaValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.FormulaValue
        CellType getFormulaType() {
            return CellType.ERROR;
        }

        void setPreEvaluatedValue(byte value) {
            this._preEvaluatedValue = value;
        }

        byte getPreEvaluatedValue() {
            return this._preEvaluatedValue;
        }
    }

    static class BlankValue implements Value {
        BlankValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.BLANK;
        }
    }

    static class BooleanValue implements Value {
        boolean _value;

        BooleanValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.BOOLEAN;
        }

        void setValue(boolean value) {
            this._value = value;
        }

        boolean getValue() {
            return this._value;
        }
    }

    static class ErrorValue implements Value {
        byte _value;

        ErrorValue() {
        }

        @Override // org.apache.poi.xssf.streaming.SXSSFCell.Value
        public CellType getType() {
            return CellType.ERROR;
        }

        void setValue(byte value) {
            this._value = value;
        }

        byte getValue() {
            return this._value;
        }
    }
}
