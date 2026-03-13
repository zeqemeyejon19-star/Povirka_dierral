package org.apache.poi.ss.usermodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.format.CellFormatResult;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class DataFormatter implements Observer {
    private static final String defaultFractionFractionPartFormat = "#/##";
    private static final String defaultFractionWholePartFormat = "#";
    private static final String invalidDateTimeString;
    private static POILogger logger;
    private DateFormatSymbols dateSymbols;
    private DecimalFormatSymbols decimalSymbols;
    private DateFormat defaultDateformat;
    private Format defaultNumFormat;
    private final boolean emulateCSV;
    private final Map<String, Format> formats;
    private Format generalNumberFormat;
    private Locale locale;
    private final LocaleChangeObservable localeChangedObservable;
    private boolean localeIsAdapting;
    private static final Pattern numPattern = Pattern.compile("[0#]+");
    private static final Pattern daysAsText = Pattern.compile("([d]{3,})", 2);
    private static final Pattern amPmPattern = Pattern.compile("((A|P)[M/P]*)", 2);
    private static final Pattern rangeConditionalPattern = Pattern.compile(".*\\[\\s*(>|>=|<|<=|=)\\s*[0-9]*\\.*[0-9].*");
    private static final Pattern localePatternGroup = Pattern.compile("(\\[\\$[^-\\]]*-[0-9A-Z]+\\])");
    private static final Pattern colorPattern = Pattern.compile("(\\[BLACK\\])|(\\[BLUE\\])|(\\[CYAN\\])|(\\[GREEN\\])|(\\[MAGENTA\\])|(\\[RED\\])|(\\[WHITE\\])|(\\[YELLOW\\])|(\\[COLOR\\s*\\d\\])|(\\[COLOR\\s*[0-5]\\d\\])", 2);
    private static final Pattern fractionPattern = Pattern.compile("(?:([#\\d]+)\\s+)?(#+)\\s*\\/\\s*([#\\d]+)");
    private static final Pattern fractionStripper = Pattern.compile("(\"[^\"]*\")|([^ \\?#\\d\\/]+)");
    private static final Pattern alternateGrouping = Pattern.compile("([#0]([^.#0])[#0]{3})");

    static {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            buf.append('#');
        }
        invalidDateTimeString = buf.toString();
        logger = POILogFactory.getLogger((Class<?>) DataFormatter.class);
    }

    private class LocaleChangeObservable extends Observable {
        private LocaleChangeObservable() {
        }

        /* synthetic */ LocaleChangeObservable(DataFormatter x0, AnonymousClass1 x1) {
            this();
        }

        void checkForLocaleChange() {
            checkForLocaleChange(LocaleUtil.getUserLocale());
        }

        void checkForLocaleChange(Locale newLocale) {
            if (DataFormatter.this.localeIsAdapting && !newLocale.equals(DataFormatter.this.locale)) {
                super.setChanged();
                notifyObservers(newLocale);
            }
        }
    }

    public DataFormatter() {
        this(false);
    }

    public DataFormatter(boolean emulateCSV) {
        this(LocaleUtil.getUserLocale(), true, emulateCSV);
    }

    public DataFormatter(Locale locale) {
        this(locale, false);
    }

    public DataFormatter(Locale locale, boolean emulateCSV) {
        this(locale, false, emulateCSV);
    }

    private DataFormatter(Locale locale, boolean localeIsAdapting, boolean emulateCSV) {
        this.formats = new HashMap();
        LocaleChangeObservable localeChangeObservable = new LocaleChangeObservable(this, null);
        this.localeChangedObservable = localeChangeObservable;
        this.localeIsAdapting = true;
        localeChangeObservable.addObserver(this);
        localeChangeObservable.checkForLocaleChange(locale);
        this.localeIsAdapting = localeIsAdapting;
        this.emulateCSV = emulateCSV;
    }

    private Format getFormat(Cell cell, ConditionalFormattingEvaluator cfEvaluator) {
        ExcelNumberFormat numFmt;
        if (cell == null || (numFmt = ExcelNumberFormat.from(cell, cfEvaluator)) == null) {
            return null;
        }
        int formatIndex = numFmt.getIdx();
        String formatStr = numFmt.getFormat();
        if (formatStr == null || formatStr.trim().length() == 0) {
            return null;
        }
        return getFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }

    private Format getFormat(double cellValue, int formatIndex, String formatStrIn) {
        this.localeChangedObservable.checkForLocaleChange();
        String formatStr = formatStrIn;
        if (formatStr.contains(";") && (formatStr.indexOf(59) != formatStr.lastIndexOf(59) || rangeConditionalPattern.matcher(formatStr).matches())) {
            try {
                CellFormat cfmt = CellFormat.getInstance(this.locale, formatStr);
                Object cellValueO = Double.valueOf(cellValue);
                if (DateUtil.isADateFormat(formatIndex, formatStr) && ((Double) cellValueO).doubleValue() != 0.0d) {
                    cellValueO = DateUtil.getJavaDate(cellValue);
                }
                return new CellFormatResultWrapper(this, cfmt.apply(cellValueO), null);
            } catch (Exception e) {
                logger.log(5, "Formatting failed for format " + formatStr + ", falling back", e);
            }
        }
        if (this.emulateCSV && cellValue == 0.0d && formatStr.contains(defaultFractionWholePartFormat) && !formatStr.contains("0")) {
            formatStr = formatStr.replaceAll(defaultFractionWholePartFormat, "");
        }
        Format format = this.formats.get(formatStr);
        if (format != null) {
            return format;
        }
        if ("General".equalsIgnoreCase(formatStr) || "@".equals(formatStr)) {
            return this.generalNumberFormat;
        }
        Format format2 = createFormat(cellValue, formatIndex, formatStr);
        this.formats.put(formatStr, format2);
        return format2;
    }

    public Format createFormat(Cell cell) {
        int formatIndex = cell.getCellStyle().getDataFormat();
        String formatStr = cell.getCellStyle().getDataFormatString();
        return createFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r16v0, types: [org.apache.poi.ss.usermodel.DataFormatter] */
    /* JADX WARN: Type inference failed for: r3v2, types: [java.lang.CharSequence, java.lang.Object, java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.lang.CharSequence, java.lang.Object, java.lang.String] */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7 */
    /* JADX WARN: Type inference failed for: r3v8 */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r5v1, types: [java.util.regex.Pattern] */
    /* JADX WARN: Type inference failed for: r6v12, types: [java.util.regex.Pattern] */
    /* JADX WARN: Type inference failed for: r7v8, types: [java.util.regex.Pattern] */
    /* JADX WARN: Type inference failed for: r9v15, types: [java.lang.String] */
    private Format createFormat(double d, int i, String str) {
        String strGroup;
        int iIndexOf;
        this.localeChangedObservable.checkForLocaleChange();
        String str2 = str;
        Matcher matcher = colorPattern.matcher(str2);
        ?? r3 = str2;
        while (matcher.find() && (iIndexOf = r3.indexOf((strGroup = matcher.group()))) != -1) {
            ?? r9 = r3.substring(0, iIndexOf) + r3.substring(strGroup.length() + iIndexOf);
            if (r9.equals(r3)) {
                break;
            }
            ?? r32 = r9;
            matcher = colorPattern.matcher(r32);
            r3 = r32;
        }
        Matcher matcher2 = localePatternGroup.matcher(r3);
        ?? r33 = r3;
        while (matcher2.find()) {
            String strGroup2 = matcher2.group();
            String strSubstring = strGroup2.substring(strGroup2.indexOf(36) + 1, strGroup2.indexOf(45));
            if (strSubstring.indexOf(36) > -1) {
                strSubstring = strSubstring.substring(0, strSubstring.indexOf(36)) + '\\' + strSubstring.substring(strSubstring.indexOf(36), strSubstring.length());
            }
            String strReplaceAll = matcher2.replaceAll(strSubstring);
            matcher2 = localePatternGroup.matcher(strReplaceAll);
            r33 = strReplaceAll;
        }
        if (r33 == 0 || r33.trim().length() == 0) {
            return getDefaultFormat(d);
        }
        if ("General".equalsIgnoreCase(r33) || "@".equals(r33)) {
            return this.generalNumberFormat;
        }
        if (DateUtil.isADateFormat(i, r33) && DateUtil.isValidExcelDate(d)) {
            return createDateFormat(r33, d);
        }
        if (r33.contains("#/") || r33.contains("?/")) {
            String[] strArrSplit = r33.split(";");
            int length = strArrSplit.length;
            int i2 = 0;
            while (true) {
                String str3 = defaultFractionWholePartFormat;
                if (i2 < length) {
                    Matcher matcher3 = fractionPattern.matcher(fractionStripper.matcher(strArrSplit[i2].replaceAll("\\?", defaultFractionWholePartFormat)).replaceAll(" ").replaceAll(" +", " "));
                    if (!matcher3.find()) {
                        i2++;
                    } else {
                        if (matcher3.group(1) == null) {
                            str3 = "";
                        }
                        return new FractionFormat(str3, matcher3.group(3));
                    }
                } else {
                    return new FractionFormat(defaultFractionWholePartFormat, defaultFractionFractionPartFormat);
                }
            }
        } else {
            if (numPattern.matcher(r33).find()) {
                return createNumberFormat(r33, d);
            }
            if (this.emulateCSV) {
                return new ConstantStringFormat(cleanFormatForNumber(r33));
            }
            return null;
        }
    }

    private Format createDateFormat(String pFormatStr, double cellValue) {
        char c;
        String formatStr = pFormatStr.replaceAll("\\\\-", "-").replaceAll("\\\\,", ",").replaceAll("\\\\\\.", ".").replaceAll("\\\\ ", " ").replaceAll("\\\\/", "/").replaceAll(";@", "").replaceAll("\"/\"", "/").replace("\"\"", "'").replaceAll("\\\\T", "'T'");
        boolean hasAmPm = false;
        Matcher amPmMatcher = amPmPattern.matcher(formatStr);
        while (amPmMatcher.find()) {
            formatStr = amPmMatcher.replaceAll("@");
            hasAmPm = true;
            amPmMatcher = amPmPattern.matcher(formatStr);
        }
        String formatStr2 = formatStr.replaceAll("@", "a");
        Matcher dateMatcher = daysAsText.matcher(formatStr2);
        if (dateMatcher.find()) {
            String match = dateMatcher.group(0).toUpperCase(Locale.ROOT).replaceAll("D", "E");
            formatStr2 = dateMatcher.replaceAll(match);
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = formatStr2.toCharArray();
        boolean mIsMonth = true;
        List<Integer> ms = new ArrayList<>();
        boolean isElapsed = false;
        int j = 0;
        while (j < chars.length) {
            char c2 = chars[j];
            if (c2 == '\'') {
                sb.append(c2);
                do {
                    j++;
                    if (j < chars.length) {
                        c = chars[j];
                        sb.append(c);
                    }
                } while (c != '\'');
            } else if (c2 == '[' && !isElapsed) {
                isElapsed = true;
                mIsMonth = false;
                sb.append(c2);
            } else if (c2 == ']' && isElapsed) {
                isElapsed = false;
                sb.append(c2);
            } else {
                char c3 = 'M';
                if (isElapsed) {
                    if (c2 == 'h' || c2 == 'H') {
                        sb.append('H');
                    } else if (c2 == 'm' || c2 == 'M') {
                        sb.append('m');
                    } else if (c2 == 's' || c2 == 'S') {
                        sb.append('s');
                    } else {
                        sb.append(c2);
                    }
                } else if (c2 == 'h' || c2 == 'H') {
                    mIsMonth = false;
                    if (hasAmPm) {
                        sb.append('h');
                    } else {
                        sb.append('H');
                    }
                } else if (c2 == 'm' || c2 == 'M') {
                    if (mIsMonth) {
                        sb.append('M');
                        ms.add(Integer.valueOf(sb.length() - 1));
                    } else {
                        sb.append('m');
                    }
                } else if (c2 == 's' || c2 == 'S') {
                    sb.append('s');
                    Iterator<Integer> it = ms.iterator();
                    while (it.hasNext()) {
                        int index = it.next().intValue();
                        if (sb.charAt(index) == c3) {
                            sb.replace(index, index + 1, "m");
                        }
                        c3 = 'M';
                    }
                    ms.clear();
                    mIsMonth = true;
                } else if (Character.isLetter(c2)) {
                    ms.clear();
                    if (c2 == 'y' || c2 == 'Y') {
                        sb.append('y');
                    } else if (c2 == 'd' || c2 == 'D') {
                        sb.append('d');
                    } else {
                        sb.append(c2);
                    }
                    mIsMonth = true;
                } else {
                    boolean mIsMonth2 = Character.isWhitespace(c2);
                    if (mIsMonth2) {
                        ms.clear();
                    }
                    sb.append(c2);
                }
            }
            j++;
        }
        String formatStr3 = sb.toString();
        try {
            return new ExcelStyleDateFormatter(formatStr3, this.dateSymbols);
        } catch (IllegalArgumentException iae) {
            logger.log(1, "Formatting failed for format " + formatStr3 + ", falling back", iae);
            return getDefaultFormat(cellValue);
        }
    }

    private String cleanFormatForNumber(String formatStr) {
        StringBuilder sb = new StringBuilder(formatStr);
        if (this.emulateCSV) {
            int i = 0;
            while (i < sb.length()) {
                char c = sb.charAt(i);
                if ((c == '_' || c == '*' || c == '?') && (i <= 0 || sb.charAt(i - 1) != '\\')) {
                    if (c == '?') {
                        sb.setCharAt(i, ' ');
                    } else if (i < sb.length() - 1) {
                        if (c == '_') {
                            sb.setCharAt(i + 1, ' ');
                        } else {
                            sb.deleteCharAt(i + 1);
                        }
                        sb.deleteCharAt(i);
                        i--;
                    }
                }
                i++;
            }
        } else {
            int i2 = 0;
            while (i2 < sb.length()) {
                char c2 = sb.charAt(i2);
                if ((c2 == '_' || c2 == '*') && (i2 <= 0 || sb.charAt(i2 - 1) != '\\')) {
                    if (i2 < sb.length() - 1) {
                        sb.deleteCharAt(i2 + 1);
                    }
                    sb.deleteCharAt(i2);
                    i2--;
                }
                i2++;
            }
        }
        int i3 = 0;
        while (i3 < sb.length()) {
            char c3 = sb.charAt(i3);
            if (c3 == '\\' || c3 == '\"') {
                sb.deleteCharAt(i3);
                i3--;
            } else if (c3 == '+' && i3 > 0 && sb.charAt(i3 - 1) == 'E') {
                sb.deleteCharAt(i3);
                i3--;
            }
            i3++;
        }
        return sb.toString();
    }

    private static class InternalDecimalFormatWithScale extends Format {
        private final DecimalFormat df;
        private BigDecimal divider;
        private static final Pattern endsWithCommas = Pattern.compile("(,+)$");
        private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);

        private static final String trimTrailingCommas(String s) {
            return s.replaceAll(",+$", "");
        }

        public InternalDecimalFormatWithScale(String pattern, DecimalFormatSymbols symbols) {
            DecimalFormat decimalFormat = new DecimalFormat(trimTrailingCommas(pattern), symbols);
            this.df = decimalFormat;
            DataFormatter.setExcelStyleRoundingMode(decimalFormat);
            Matcher endsWithCommasMatcher = endsWithCommas.matcher(pattern);
            if (endsWithCommasMatcher.find()) {
                String commas = endsWithCommasMatcher.group(1);
                BigDecimal temp = BigDecimal.ONE;
                for (int i = 0; i < commas.length(); i++) {
                    temp = temp.multiply(ONE_THOUSAND);
                }
                this.divider = temp;
                return;
            }
            this.divider = null;
        }

        private Object scaleInput(Object obj) {
            BigDecimal bigDecimal = this.divider;
            if (bigDecimal != null) {
                if (obj instanceof BigDecimal) {
                    return ((BigDecimal) obj).divide(bigDecimal, RoundingMode.HALF_UP);
                }
                if (obj instanceof Double) {
                    return Double.valueOf(((Double) obj).doubleValue() / this.divider.doubleValue());
                }
                throw new UnsupportedOperationException();
            }
            return obj;
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return this.df.format(scaleInput(obj), toAppendTo, pos);
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            throw new UnsupportedOperationException();
        }
    }

    private Format createNumberFormat(String formatStr, double cellValue) {
        char grouping;
        String format = cleanFormatForNumber(formatStr);
        DecimalFormatSymbols symbols = this.decimalSymbols;
        Matcher agm = alternateGrouping.matcher(format);
        if (agm.find() && (grouping = agm.group(2).charAt(0)) != ',') {
            symbols = DecimalFormatSymbols.getInstance(this.locale);
            symbols.setGroupingSeparator(grouping);
            String oldPart = agm.group(1);
            String newPart = oldPart.replace(grouping, ',');
            format = format.replace(oldPart, newPart);
        }
        try {
            return new InternalDecimalFormatWithScale(format, symbols);
        } catch (IllegalArgumentException iae) {
            logger.log(1, "Formatting failed for format " + formatStr + ", falling back", iae);
            return getDefaultFormat(cellValue);
        }
    }

    public Format getDefaultFormat(Cell cell) {
        return getDefaultFormat(cell.getNumericCellValue());
    }

    private Format getDefaultFormat(double cellValue) {
        this.localeChangedObservable.checkForLocaleChange();
        Format format = this.defaultNumFormat;
        if (format != null) {
            return format;
        }
        return this.generalNumberFormat;
    }

    private String performDateFormatting(Date d, Format dateFormat) {
        return (dateFormat != null ? dateFormat : this.defaultDateformat).format(d);
    }

    private String getFormattedDateString(Cell cell, ConditionalFormattingEvaluator cfEvaluator) {
        Format dateFormat = getFormat(cell, cfEvaluator);
        if (dateFormat instanceof ExcelStyleDateFormatter) {
            ((ExcelStyleDateFormatter) dateFormat).setDateToBeFormatted(cell.getNumericCellValue());
        }
        Date d = cell.getDateCellValue();
        return performDateFormatting(d, dateFormat);
    }

    private String getFormattedNumberString(Cell cell, ConditionalFormattingEvaluator cfEvaluator) {
        Format numberFormat = getFormat(cell, cfEvaluator);
        double d = cell.getNumericCellValue();
        if (numberFormat == null) {
            return String.valueOf(d);
        }
        String formatted = numberFormat.format(new Double(d));
        return formatted.replaceFirst("E(\\d)", "E+$1");
    }

    public String formatRawCellContents(double value, int formatIndex, String formatString) {
        return formatRawCellContents(value, formatIndex, formatString, false);
    }

    public String formatRawCellContents(double value, int formatIndex, String formatString, boolean use1904Windowing) {
        String result;
        this.localeChangedObservable.checkForLocaleChange();
        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            if (DateUtil.isValidExcelDate(value)) {
                Format dateFormat = getFormat(value, formatIndex, formatString);
                if (dateFormat instanceof ExcelStyleDateFormatter) {
                    ((ExcelStyleDateFormatter) dateFormat).setDateToBeFormatted(value);
                }
                Date d = DateUtil.getJavaDate(value, use1904Windowing);
                return performDateFormatting(d, dateFormat);
            }
            if (this.emulateCSV) {
                return invalidDateTimeString;
            }
        }
        Format numberFormat = getFormat(value, formatIndex, formatString);
        if (numberFormat == null) {
            return String.valueOf(value);
        }
        String textValue = NumberToTextConverter.toText(value);
        if (textValue.indexOf(69) > -1) {
            result = numberFormat.format(new Double(value));
        } else {
            result = numberFormat.format(new BigDecimal(textValue));
        }
        if (result.indexOf(69) > -1 && !result.contains("E-")) {
            return result.replaceFirst("E", "E+");
        }
        return result;
    }

    public String formatCellValue(Cell cell) {
        return formatCellValue(cell, null);
    }

    public String formatCellValue(Cell cell, FormulaEvaluator evaluator) {
        return formatCellValue(cell, evaluator, null);
    }

    public String formatCellValue(Cell cell, FormulaEvaluator evaluator, ConditionalFormattingEvaluator cfEvaluator) {
        this.localeChangedObservable.checkForLocaleChange();
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellTypeEnum();
        if (cellType == CellType.FORMULA) {
            if (evaluator == null) {
                return cell.getCellFormula();
            }
            cellType = evaluator.evaluateFormulaCellEnum(cell);
        }
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cellType.ordinal()];
        if (i == 1) {
            if (DateUtil.isCellDateFormatted(cell, cfEvaluator)) {
                return getFormattedDateString(cell, cfEvaluator);
            }
            return getFormattedNumberString(cell, cfEvaluator);
        }
        if (i == 2) {
            return cell.getRichStringCellValue().getString();
        }
        if (i == 3) {
            return cell.getBooleanCellValue() ? "TRUE" : "FALSE";
        }
        if (i == 4) {
            return "";
        }
        if (i == 5) {
            return FormulaError.forInt(cell.getErrorCellValue()).getString();
        }
        throw new RuntimeException("Unexpected celltype (" + cellType + ")");
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.usermodel.DataFormatter$1, reason: invalid class name */
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
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BLANK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void setDefaultNumberFormat(Format format) {
        for (Map.Entry<String, Format> entry : this.formats.entrySet()) {
            if (entry.getValue() == this.generalNumberFormat) {
                entry.setValue(format);
            }
        }
        this.defaultNumFormat = format;
    }

    public void addFormat(String excelFormatStr, Format format) {
        this.formats.put(excelFormatStr, format);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static DecimalFormat createIntegerOnlyFormat(String fmt) {
        DecimalFormatSymbols dsf = DecimalFormatSymbols.getInstance(Locale.ROOT);
        DecimalFormat result = new DecimalFormat(fmt, dsf);
        result.setParseIntegerOnly(true);
        return result;
    }

    public static void setExcelStyleRoundingMode(DecimalFormat format) {
        setExcelStyleRoundingMode(format, RoundingMode.HALF_UP);
    }

    public static void setExcelStyleRoundingMode(DecimalFormat format, RoundingMode roundingMode) {
        format.setRoundingMode(roundingMode);
    }

    public Observable getLocaleChangedObservable() {
        return this.localeChangedObservable;
    }

    @Override // java.util.Observer
    public void update(Observable observable, Object localeObj) {
        if (localeObj instanceof Locale) {
            Locale newLocale = (Locale) localeObj;
            if (!this.localeIsAdapting || newLocale.equals(this.locale)) {
                return;
            }
            this.locale = newLocale;
            this.dateSymbols = DateFormatSymbols.getInstance(newLocale);
            this.decimalSymbols = DecimalFormatSymbols.getInstance(this.locale);
            this.generalNumberFormat = new ExcelGeneralNumberFormat(this.locale);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", this.dateSymbols);
            this.defaultDateformat = simpleDateFormat;
            simpleDateFormat.setTimeZone(LocaleUtil.getUserTimeZone());
            this.formats.clear();
            Format zipFormat = ZipPlusFourFormat.instance;
            addFormat("00000\\-0000", zipFormat);
            addFormat("00000-0000", zipFormat);
            Format phoneFormat = PhoneFormat.instance;
            addFormat("[<=9999999]###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
            addFormat("[<=9999999]###-####;(###) ###-####", phoneFormat);
            addFormat("###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
            addFormat("###-####;(###) ###-####", phoneFormat);
            Format ssnFormat = SSNFormat.instance;
            addFormat("000\\-00\\-0000", ssnFormat);
            addFormat("000-00-0000", ssnFormat);
        }
    }

    private static final class SSNFormat extends Format {
        public static final Format instance = new SSNFormat();
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("000000000");

        private SSNFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            return result.substring(0, 3) + '-' + result.substring(3, 5) + '-' + result.substring(5, 9);
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private static final class ZipPlusFourFormat extends Format {
        public static final Format instance = new ZipPlusFourFormat();
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("000000000");

        private ZipPlusFourFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            return result.substring(0, 5) + '-' + result.substring(5, 9);
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private static final class PhoneFormat extends Format {
        public static final Format instance = new PhoneFormat();
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("##########");

        private PhoneFormat() {
        }

        public static String format(Number num) {
            String result = df.format(num);
            StringBuilder sb = new StringBuilder();
            int len = result.length();
            if (len <= 4) {
                return result;
            }
            String seg3 = result.substring(len - 4, len);
            String seg2 = result.substring(Math.max(0, len - 7), len - 4);
            String seg1 = result.substring(Math.max(0, len - 10), Math.max(0, len - 7));
            if (seg1.trim().length() > 0) {
                sb.append('(').append(seg1).append(") ");
            }
            if (seg2.trim().length() > 0) {
                sb.append(seg2).append('-');
            }
            sb.append(seg3);
            return sb.toString();
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private static final class ConstantStringFormat extends Format {
        private static final DecimalFormat df = DataFormatter.createIntegerOnlyFormat("##########");
        private final String str;

        public ConstantStringFormat(String s) {
            this.str = s;
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(this.str);
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }

    private final class CellFormatResultWrapper extends Format {
        private final CellFormatResult result;

        /* synthetic */ CellFormatResultWrapper(DataFormatter x0, CellFormatResult x1, AnonymousClass1 x2) {
            this(x1);
        }

        private CellFormatResultWrapper(CellFormatResult result) {
            this.result = result;
        }

        @Override // java.text.Format
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            if (DataFormatter.this.emulateCSV) {
                return toAppendTo.append(this.result.text);
            }
            return toAppendTo.append(this.result.text.trim());
        }

        @Override // java.text.Format
        public Object parseObject(String source, ParsePosition pos) {
            return null;
        }
    }
}
