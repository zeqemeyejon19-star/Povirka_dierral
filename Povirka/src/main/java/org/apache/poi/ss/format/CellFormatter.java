package org.apache.poi.ss.format;

import java.util.Locale;
import java.util.logging.Logger;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public abstract class CellFormatter {
    static final Logger logger = Logger.getLogger(CellFormatter.class.getName());
    protected final String format;
    protected final Locale locale;

    public abstract void formatValue(StringBuffer stringBuffer, Object obj);

    public abstract void simpleValue(StringBuffer stringBuffer, Object obj);

    public CellFormatter(String format) {
        this(LocaleUtil.getUserLocale(), format);
    }

    public CellFormatter(Locale locale, String format) {
        this.locale = locale;
        this.format = format;
    }

    public String format(Object value) {
        StringBuffer sb = new StringBuffer();
        formatValue(sb, value);
        return sb.toString();
    }

    public String simpleFormat(Object value) {
        StringBuffer sb = new StringBuffer();
        simpleValue(sb, value);
        return sb.toString();
    }

    static String quote(String str) {
        return '\"' + str + '\"';
    }
}
