package org.apache.poi.ss.format;

import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import org.apache.poi.ss.format.CellFormatPart;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class CellDateFormatter extends CellFormatter {
    private static CellDateFormatter SIMPLE_DATE = null;
    private final Calendar EXCEL_EPOCH_CAL;
    private boolean amPmUpper;
    private final DateFormat dateFmt;
    private String sFmt;
    private boolean showAmPm;
    private boolean showM;

    private class DatePartHandler implements CellFormatPart.PartHandler {
        private int hLen;
        private int hStart;
        private int mLen;
        private int mStart;

        private DatePartHandler() {
            this.mStart = -1;
            this.hStart = -1;
        }

        @Override // org.apache.poi.ss.format.CellFormatPart.PartHandler
        public String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc) {
            int pos = desc.length();
            char firstCh = part.charAt(0);
            switch (firstCh) {
                case '0':
                    this.mStart = -1;
                    int sLen = part.length();
                    CellDateFormatter.this.sFmt = "%0" + (sLen + 2) + "." + sLen + "f";
                    return part.replace('0', 'S');
                case 'A':
                case 'P':
                case 'a':
                case 'p':
                    if (part.length() > 1) {
                        this.mStart = -1;
                        CellDateFormatter.this.showAmPm = true;
                        CellDateFormatter.this.showM = Character.toLowerCase(part.charAt(1)) == 'm';
                        CellDateFormatter cellDateFormatter = CellDateFormatter.this;
                        cellDateFormatter.amPmUpper = cellDateFormatter.showM || Character.isUpperCase(part.charAt(0));
                        return "a";
                    }
                    return null;
                case 'D':
                case 'd':
                    this.mStart = -1;
                    if (part.length() <= 2) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toLowerCase(Locale.ROOT).replace('d', 'E');
                case 'H':
                case 'h':
                    this.mStart = -1;
                    this.hStart = pos;
                    this.hLen = part.length();
                    return part.toLowerCase(Locale.ROOT);
                case 'M':
                case 'm':
                    this.mStart = pos;
                    this.mLen = part.length();
                    if (this.hStart >= 0) {
                        return part.toLowerCase(Locale.ROOT);
                    }
                    return part.toUpperCase(Locale.ROOT);
                case 'S':
                case 's':
                    if (this.mStart >= 0) {
                        for (int i = 0; i < this.mLen; i++) {
                            desc.setCharAt(this.mStart + i, 'm');
                        }
                        this.mStart = -1;
                    }
                    return part.toLowerCase(Locale.ROOT);
                case 'Y':
                case 'y':
                    this.mStart = -1;
                    if (part.length() == 3) {
                        part = "yyyy";
                    }
                    return part.toLowerCase(Locale.ROOT);
                default:
                    return null;
            }
        }

        public void finish(StringBuffer toAppendTo) {
            if (this.hStart >= 0 && !CellDateFormatter.this.showAmPm) {
                for (int i = 0; i < this.hLen; i++) {
                    toAppendTo.setCharAt(this.hStart + i, 'H');
                }
            }
        }
    }

    public CellDateFormatter(String format) {
        this(LocaleUtil.getUserLocale(), format);
    }

    public CellDateFormatter(Locale locale, String format) {
        super(format);
        this.EXCEL_EPOCH_CAL = LocaleUtil.getLocaleCalendar(1904, 0, 1);
        DatePartHandler partHandler = new DatePartHandler();
        StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.DATE, partHandler);
        partHandler.finish(descBuf);
        String ptrn = descBuf.toString().replaceAll("((y)(?!y))(?<!yy)", "yy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ptrn, locale);
        this.dateFmt = simpleDateFormat;
        simpleDateFormat.setTimeZone(LocaleUtil.getUserTimeZone());
    }

    @Override // org.apache.poi.ss.format.CellFormatter
    public void formatValue(StringBuffer toAppendTo, Object value) throws Throwable {
        Object value2;
        Object value3;
        AttributedCharacterIterator it;
        long msecs;
        Locale locale;
        String str;
        Object[] objArr;
        AttributedCharacterIterator it2;
        if (value != null) {
            value2 = value;
        } else {
            value2 = Double.valueOf(0.0d);
        }
        long j = 1000;
        if (!(value2 instanceof Number)) {
            value3 = value2;
        } else {
            Number num = (Number) value2;
            long v = num.longValue();
            if (v == 0) {
                value3 = this.EXCEL_EPOCH_CAL.getTime();
            } else {
                Calendar c = (Calendar) this.EXCEL_EPOCH_CAL.clone();
                c.add(13, (int) (v / 1000));
                c.add(14, (int) (v % 1000));
                value3 = c.getTime();
            }
        }
        AttributedCharacterIterator it3 = this.dateFmt.formatToCharacterIterator(value3);
        it3.first();
        char ch = it3.first();
        boolean doneMillis = false;
        boolean doneMillis2 = false;
        while (ch != 65535) {
            if (it3.getAttribute(DateFormat.Field.MILLISECOND) != null) {
                if (doneMillis) {
                    it = it3;
                } else {
                    Date dateObj = (Date) value3;
                    int pos = toAppendTo.length();
                    Formatter formatter = new Formatter(toAppendTo, Locale.ROOT);
                    try {
                        msecs = dateObj.getTime() % j;
                        locale = this.locale;
                        str = this.sFmt;
                        objArr = new Object[1];
                        it2 = it3;
                    } catch (Throwable th) {
                        th = th;
                    }
                    try {
                        objArr[0] = Double.valueOf(msecs / 1000.0d);
                        formatter.format(locale, str, objArr);
                        formatter.close();
                        toAppendTo.delete(pos, pos + 2);
                        it = it2;
                        doneMillis = true;
                    } catch (Throwable th2) {
                        th = th2;
                        formatter.close();
                        throw th;
                    }
                }
            } else {
                it = it3;
                if (it.getAttribute(DateFormat.Field.AM_PM) != null) {
                    if (!doneMillis2) {
                        if (this.showAmPm) {
                            if (this.amPmUpper) {
                                toAppendTo.append(Character.toUpperCase(ch));
                                if (this.showM) {
                                    toAppendTo.append('M');
                                }
                            } else {
                                toAppendTo.append(Character.toLowerCase(ch));
                                if (this.showM) {
                                    toAppendTo.append('m');
                                }
                            }
                        }
                        doneMillis2 = true;
                    }
                } else {
                    toAppendTo.append(ch);
                }
            }
            ch = it.next();
            it3 = it;
            j = 1000;
        }
    }

    @Override // org.apache.poi.ss.format.CellFormatter
    public void simpleValue(StringBuffer toAppendTo, Object value) throws Throwable {
        synchronized (CellDateFormatter.class) {
            CellDateFormatter cellDateFormatter = SIMPLE_DATE;
            if (cellDateFormatter == null || !cellDateFormatter.EXCEL_EPOCH_CAL.equals(this.EXCEL_EPOCH_CAL)) {
                SIMPLE_DATE = new CellDateFormatter("mm/d/y");
            }
        }
        SIMPLE_DATE.formatValue(toAppendTo, value);
    }
}
