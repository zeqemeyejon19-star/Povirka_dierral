package org.apache.poi.ss.format;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public class CellFormat {
    private static final String INVALID_VALUE_FOR_FORMAT = "###############################################################################################################################################################################################################################################################";
    private final String format;
    private final int formatPartCount;
    private final Locale locale;
    private final CellFormatPart negNumFmt;
    private final CellFormatPart posNumFmt;
    private final CellFormatPart textFmt;
    private final CellFormatPart zeroNumFmt;
    private static final Pattern ONE_PART = Pattern.compile(CellFormatPart.FORMAT_PAT.pattern() + "(;|$)", 6);
    private static String QUOTE = "\"";

    @Removal(version = "3.18")
    @Deprecated
    public static final CellFormat GENERAL_FORMAT = createGeneralFormat(LocaleUtil.getUserLocale());
    private static final Map<Locale, Map<String, CellFormat>> formatCache = new WeakHashMap();

    private static CellFormat createGeneralFormat(final Locale locale) {
        return new CellFormat(locale, "General") { // from class: org.apache.poi.ss.format.CellFormat.1
            @Override // org.apache.poi.ss.format.CellFormat
            public CellFormatResult apply(Object value) {
                String text = new CellGeneralFormatter(locale).format(value);
                return new CellFormatResult(true, text, null);
            }
        };
    }

    public static CellFormat getInstance(String format) {
        return getInstance(LocaleUtil.getUserLocale(), format);
    }

    public static synchronized CellFormat getInstance(Locale locale, String format) {
        CellFormat fmt;
        Map<Locale, Map<String, CellFormat>> map = formatCache;
        Map<String, CellFormat> formatMap = map.get(locale);
        if (formatMap == null) {
            formatMap = new WeakHashMap();
            map.put(locale, formatMap);
        }
        fmt = formatMap.get(format);
        if (fmt == null) {
            if (format.equals("General") || format.equals("@")) {
                fmt = createGeneralFormat(locale);
            } else {
                fmt = new CellFormat(locale, format);
            }
            formatMap.put(format, fmt);
        }
        return fmt;
    }

    private CellFormat(Locale locale, String format) {
        this.locale = locale;
        this.format = format;
        CellFormatPart defaultTextFormat = new CellFormatPart(locale, "@");
        Matcher m = ONE_PART.matcher(format);
        List<CellFormatPart> parts = new ArrayList<>();
        while (m.find()) {
            try {
                String valueDesc = m.group();
                parts.add(new CellFormatPart(locale, valueDesc.endsWith(";") ? valueDesc.substring(0, valueDesc.length() - 1) : valueDesc));
            } catch (RuntimeException e) {
                CellFormatter.logger.log(Level.WARNING, "Invalid format: " + CellFormatter.quote(m.group()), (Throwable) e);
                parts.add(null);
            }
        }
        int size = parts.size();
        this.formatPartCount = size;
        if (size == 1) {
            this.posNumFmt = parts.get(0);
            this.negNumFmt = null;
            this.zeroNumFmt = null;
            this.textFmt = defaultTextFormat;
            return;
        }
        if (size == 2) {
            this.posNumFmt = parts.get(0);
            this.negNumFmt = parts.get(1);
            this.zeroNumFmt = null;
            this.textFmt = defaultTextFormat;
            return;
        }
        if (size == 3) {
            this.posNumFmt = parts.get(0);
            this.negNumFmt = parts.get(1);
            this.zeroNumFmt = parts.get(2);
            this.textFmt = defaultTextFormat;
            return;
        }
        this.posNumFmt = parts.get(0);
        this.negNumFmt = parts.get(1);
        this.zeroNumFmt = parts.get(2);
        this.textFmt = parts.get(3);
    }

    public CellFormatResult apply(Object value) {
        if (value instanceof Number) {
            Number num = (Number) value;
            double val = num.doubleValue();
            if (val < 0.0d && ((this.formatPartCount == 2 && !this.posNumFmt.hasCondition() && !this.negNumFmt.hasCondition()) || ((this.formatPartCount == 3 && !this.negNumFmt.hasCondition()) || (this.formatPartCount == 4 && !this.negNumFmt.hasCondition())))) {
                return this.negNumFmt.apply(Double.valueOf(-val));
            }
            return getApplicableFormatPart(Double.valueOf(val)).apply(Double.valueOf(val));
        }
        if (value instanceof Date) {
            Double numericValue = Double.valueOf(DateUtil.getExcelDate((Date) value));
            if (DateUtil.isValidExcelDate(numericValue.doubleValue())) {
                return getApplicableFormatPart(numericValue).apply(value);
            }
            throw new IllegalArgumentException("value " + numericValue + " of date " + value + " is not a valid Excel date");
        }
        return this.textFmt.apply(value);
    }

    private CellFormatResult apply(Date date, double numericValue) {
        return getApplicableFormatPart(Double.valueOf(numericValue)).apply(date);
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.format.CellFormat$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.BLANK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 2;
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
        }
    }

    public CellFormatResult apply(Cell c) {
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$ss$usermodel$CellType[ultimateTypeEnum(c).ordinal()];
        if (i == 1) {
            return apply("");
        }
        if (i == 2) {
            return apply(Boolean.valueOf(c.getBooleanCellValue()));
        }
        if (i != 3) {
            if (i == 4) {
                return apply(c.getStringCellValue());
            }
            return apply("?");
        }
        Double value = Double.valueOf(c.getNumericCellValue());
        if (getApplicableFormatPart(value).getCellFormatType() == CellFormatType.DATE) {
            if (DateUtil.isValidExcelDate(value.doubleValue())) {
                return apply(c.getDateCellValue(), value.doubleValue());
            }
            return apply(INVALID_VALUE_FOR_FORMAT);
        }
        return apply(value);
    }

    public CellFormatResult apply(JLabel label, Object value) {
        CellFormatResult result = apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }

    private CellFormatResult apply(JLabel label, Date date, double numericValue) {
        CellFormatResult result = apply(date, numericValue);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }

    public CellFormatResult apply(JLabel label, Cell c) {
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$ss$usermodel$CellType[ultimateTypeEnum(c).ordinal()];
        if (i == 1) {
            return apply(label, "");
        }
        if (i == 2) {
            return apply(label, Boolean.valueOf(c.getBooleanCellValue()));
        }
        if (i != 3) {
            if (i == 4) {
                return apply(label, c.getStringCellValue());
            }
            return apply(label, "?");
        }
        Double value = Double.valueOf(c.getNumericCellValue());
        if (getApplicableFormatPart(value).getCellFormatType() == CellFormatType.DATE) {
            if (DateUtil.isValidExcelDate(value.doubleValue())) {
                return apply(label, c.getDateCellValue(), value.doubleValue());
            }
            return apply(label, INVALID_VALUE_FOR_FORMAT);
        }
        return apply(label, value);
    }

    private CellFormatPart getApplicableFormatPart(Object value) {
        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();
            int i = this.formatPartCount;
            if (i == 1) {
                if (!this.posNumFmt.hasCondition() || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(Double.valueOf(val)))) {
                    return this.posNumFmt;
                }
                return new CellFormatPart(this.locale, "General");
            }
            if (i == 2) {
                if ((!this.posNumFmt.hasCondition() && val >= 0.0d) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(Double.valueOf(val)))) {
                    return this.posNumFmt;
                }
                if (!this.negNumFmt.hasCondition() || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(Double.valueOf(val)))) {
                    return this.negNumFmt;
                }
                return new CellFormatPart(QUOTE + INVALID_VALUE_FOR_FORMAT + QUOTE);
            }
            if ((!this.posNumFmt.hasCondition() && val > 0.0d) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(Double.valueOf(val)))) {
                return this.posNumFmt;
            }
            if ((!this.negNumFmt.hasCondition() && val < 0.0d) || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(Double.valueOf(val)))) {
                return this.negNumFmt;
            }
            return this.zeroNumFmt;
        }
        throw new IllegalArgumentException("value must be a Number");
    }

    public static int ultimateType(Cell cell) {
        return ultimateTypeEnum(cell).getCode();
    }

    public static CellType ultimateTypeEnum(Cell cell) {
        CellType type = cell.getCellTypeEnum();
        if (type == CellType.FORMULA) {
            return cell.getCachedFormulaResultTypeEnum();
        }
        return type;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CellFormat) {
            CellFormat that = (CellFormat) obj;
            return this.format.equals(that.format);
        }
        return false;
    }

    public int hashCode() {
        return this.format.hashCode();
    }
}
