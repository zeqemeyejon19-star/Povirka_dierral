package org.apache.poi.ss.usermodel;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class ExcelGeneralNumberFormat extends Format {
    private static final MathContext TO_10_SF = new MathContext(10, RoundingMode.HALF_UP);
    private static final long serialVersionUID = 1;
    private final DecimalFormat decimalFormat;
    private final DecimalFormatSymbols decimalSymbols;
    private final DecimalFormat integerFormat;
    private final DecimalFormat scientificFormat;

    public ExcelGeneralNumberFormat(Locale locale) {
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);
        this.decimalSymbols = decimalFormatSymbols;
        DecimalFormat decimalFormat = new DecimalFormat("0.#####E0", decimalFormatSymbols);
        this.scientificFormat = decimalFormat;
        DataFormatter.setExcelStyleRoundingMode(decimalFormat);
        DecimalFormat decimalFormat2 = new DecimalFormat("#", decimalFormatSymbols);
        this.integerFormat = decimalFormat2;
        DataFormatter.setExcelStyleRoundingMode(decimalFormat2);
        DecimalFormat decimalFormat3 = new DecimalFormat("#.##########", decimalFormatSymbols);
        this.decimalFormat = decimalFormat3;
        DataFormatter.setExcelStyleRoundingMode(decimalFormat3);
    }

    @Override // java.text.Format
    public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
        if (number instanceof Number) {
            double value = ((Number) number).doubleValue();
            if (Double.isInfinite(value) || Double.isNaN(value)) {
                return this.integerFormat.format(number, toAppendTo, pos);
            }
            double abs = Math.abs(value);
            if (abs >= 1.0E11d || (abs <= 1.0E-10d && abs > 0.0d)) {
                return this.scientificFormat.format(number, toAppendTo, pos);
            }
            if (Math.floor(value) == value || abs >= 1.0E10d) {
                return this.integerFormat.format(number, toAppendTo, pos);
            }
            double rounded = new BigDecimal(value).round(TO_10_SF).doubleValue();
            return this.decimalFormat.format(rounded, toAppendTo, pos);
        }
        return this.integerFormat.format(number, toAppendTo, pos);
    }

    @Override // java.text.Format
    public Object parseObject(String source, ParsePosition pos) {
        throw new UnsupportedOperationException();
    }
}
