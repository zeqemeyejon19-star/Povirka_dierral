package org.apache.poi.ss.usermodel;

import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class ExcelStyleDateFormatter extends SimpleDateFormat {
    public static final char HH_BRACKET_SYMBOL = 57361;
    public static final char H_BRACKET_SYMBOL = 57360;
    public static final char LL_BRACKET_SYMBOL = 57367;
    public static final char L_BRACKET_SYMBOL = 57366;
    public static final char MMMMM_START_SYMBOL = 57345;
    public static final char MMMMM_TRUNCATE_SYMBOL = 57346;
    public static final char MM_BRACKET_SYMBOL = 57363;
    public static final char M_BRACKET_SYMBOL = 57362;
    public static final char SS_BRACKET_SYMBOL = 57365;
    public static final char S_BRACKET_SYMBOL = 57364;
    private static final DecimalFormat format1digit;
    private static final DecimalFormat format2digits;
    private static final DecimalFormat format3digit;
    private static final DecimalFormat format4digits;
    private double dateToBeFormatted;

    static {
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.ROOT);
        DecimalFormat decimalFormat = new DecimalFormat("0", dfs);
        format1digit = decimalFormat;
        DecimalFormat decimalFormat2 = new DecimalFormat("00", dfs);
        format2digits = decimalFormat2;
        DecimalFormat decimalFormat3 = new DecimalFormat("0", dfs);
        format3digit = decimalFormat3;
        DecimalFormat decimalFormat4 = new DecimalFormat("00", dfs);
        format4digits = decimalFormat4;
        DataFormatter.setExcelStyleRoundingMode(decimalFormat, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(decimalFormat2, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(decimalFormat3);
        DataFormatter.setExcelStyleRoundingMode(decimalFormat4);
    }

    public ExcelStyleDateFormatter(String pattern) {
        super(processFormatPattern(pattern), LocaleUtil.getUserLocale());
        setTimeZone(LocaleUtil.getUserTimeZone());
    }

    public ExcelStyleDateFormatter(String pattern, DateFormatSymbols formatSymbols) {
        super(processFormatPattern(pattern), formatSymbols);
        setTimeZone(LocaleUtil.getUserTimeZone());
    }

    public ExcelStyleDateFormatter(String pattern, Locale locale) {
        super(processFormatPattern(pattern), locale);
        setTimeZone(LocaleUtil.getUserTimeZone());
    }

    private static String processFormatPattern(String f) {
        String t = f.replaceAll("MMMMM", "\ue001MMM\ue002");
        return t.replaceAll("\\[H]", String.valueOf(H_BRACKET_SYMBOL)).replaceAll("\\[HH]", String.valueOf(HH_BRACKET_SYMBOL)).replaceAll("\\[m]", String.valueOf(M_BRACKET_SYMBOL)).replaceAll("\\[mm]", String.valueOf(MM_BRACKET_SYMBOL)).replaceAll("\\[s]", String.valueOf(S_BRACKET_SYMBOL)).replaceAll("\\[ss]", String.valueOf(SS_BRACKET_SYMBOL)).replaceAll("s.000", "s.SSS").replaceAll("s.00", "s.\ue017").replaceAll("s.0", "s.\ue016");
    }

    public void setDateToBeFormatted(double date) {
        this.dateToBeFormatted = date;
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
        String s = super.format(date, paramStringBuffer, paramFieldPosition).toString();
        if (s.indexOf(57345) != -1) {
            s = s.replaceAll("\ue001(\\p{L}|\\p{P})[\\p{L}\\p{P}]+\ue002", "$1");
        }
        if (s.indexOf(57360) != -1 || s.indexOf(57361) != -1) {
            float hours = ((float) this.dateToBeFormatted) * 24.0f;
            s = s.replaceAll(String.valueOf(H_BRACKET_SYMBOL), format1digit.format(hours)).replaceAll(String.valueOf(HH_BRACKET_SYMBOL), format2digits.format(hours));
        }
        if (s.indexOf(57362) != -1 || s.indexOf(57363) != -1) {
            float minutes = ((float) this.dateToBeFormatted) * 24.0f * 60.0f;
            s = s.replaceAll(String.valueOf(M_BRACKET_SYMBOL), format1digit.format(minutes)).replaceAll(String.valueOf(MM_BRACKET_SYMBOL), format2digits.format(minutes));
        }
        if (s.indexOf(57364) != -1 || s.indexOf(57365) != -1) {
            float seconds = (float) (this.dateToBeFormatted * 24.0d * 60.0d * 60.0d);
            s = s.replaceAll(String.valueOf(S_BRACKET_SYMBOL), format1digit.format(seconds)).replaceAll(String.valueOf(SS_BRACKET_SYMBOL), format2digits.format(seconds));
        }
        if (s.indexOf(57366) != -1 || s.indexOf(57367) != -1) {
            double d = this.dateToBeFormatted;
            float millisTemp = (float) ((d - Math.floor(d)) * 24.0d * 60.0d * 60.0d);
            float millis = millisTemp - ((int) millisTemp);
            s = s.replaceAll(String.valueOf(L_BRACKET_SYMBOL), format3digit.format(10.0f * millis)).replaceAll(String.valueOf(LL_BRACKET_SYMBOL), format4digits.format(100.0f * millis));
        }
        return new StringBuffer(s);
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public boolean equals(Object o) {
        if (!(o instanceof ExcelStyleDateFormatter)) {
            return false;
        }
        ExcelStyleDateFormatter other = (ExcelStyleDateFormatter) o;
        return this.dateToBeFormatted == other.dateToBeFormatted;
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public int hashCode() {
        return new Double(this.dateToBeFormatted).hashCode();
    }
}
