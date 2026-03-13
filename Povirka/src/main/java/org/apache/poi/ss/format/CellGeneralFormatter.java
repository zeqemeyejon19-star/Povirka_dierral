package org.apache.poi.ss.format;

import java.util.Formatter;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class CellGeneralFormatter extends CellFormatter {
    public CellGeneralFormatter() {
        this(LocaleUtil.getUserLocale());
    }

    public CellGeneralFormatter(Locale locale) {
        super(locale, "General");
    }

    @Override // org.apache.poi.ss.format.CellFormatter
    public void formatValue(StringBuffer toAppendTo, Object value) {
        String fmt;
        int removeFrom;
        if (value instanceof Number) {
            double val = ((Number) value).doubleValue();
            if (val == 0.0d) {
                toAppendTo.append('0');
                return;
            }
            double exp = Math.log10(Math.abs(val));
            boolean stripZeros = true;
            if (exp > 10.0d || exp < -9.0d) {
                fmt = "%1.5E";
            } else if (((long) val) != val) {
                fmt = "%1.9f";
            } else {
                fmt = "%1.0f";
                stripZeros = false;
            }
            Formatter formatter = new Formatter(toAppendTo, this.locale);
            try {
                formatter.format(this.locale, fmt, value);
                if (stripZeros) {
                    if (fmt.endsWith("E")) {
                        removeFrom = toAppendTo.lastIndexOf("E") - 1;
                    } else {
                        int removeFrom2 = toAppendTo.length();
                        removeFrom = removeFrom2 - 1;
                    }
                    while (toAppendTo.charAt(removeFrom) == '0') {
                        toAppendTo.deleteCharAt(removeFrom);
                        removeFrom--;
                    }
                    if (toAppendTo.charAt(removeFrom) == '.') {
                        int i = removeFrom - 1;
                        toAppendTo.deleteCharAt(removeFrom);
                        return;
                    }
                    return;
                }
                return;
            } finally {
                formatter.close();
            }
        }
        if (value instanceof Boolean) {
            toAppendTo.append(value.toString().toUpperCase(Locale.ROOT));
        } else {
            toAppendTo.append(value);
        }
    }

    @Override // org.apache.poi.ss.format.CellFormatter
    public void simpleValue(StringBuffer toAppendTo, Object value) {
        formatValue(toAppendTo, value);
    }
}
