package org.apache.poi.ss.format;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public class CellFormatPart {
    public static final int COLOR_GROUP;
    public static final Pattern COLOR_PAT;
    public static final int CONDITION_OPERATOR_GROUP;
    public static final Pattern CONDITION_PAT;
    public static final int CONDITION_VALUE_GROUP;
    public static final Pattern CURRENCY_PAT;
    public static final Pattern FORMAT_PAT;
    private static final Map<String, Color> NAMED_COLORS = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    public static final int SPECIFICATION_GROUP;
    public static final Pattern SPECIFICATION_PAT;
    private final Color color;
    private CellFormatCondition condition;
    private final CellFormatter format;
    private final CellFormatType type;

    interface PartHandler {
        String handlePart(Matcher matcher, String str, CellFormatType cellFormatType, StringBuffer stringBuffer);
    }

    static {
        Map<Integer, HSSFColor> colors = HSSFColor.getIndexHash();
        for (HSSFColor color : colors.values()) {
            String name = color.getClass().getSimpleName();
            if (name.equals(name.toUpperCase(Locale.ROOT))) {
                short[] rgb = color.getTriplet();
                Color c = new Color(rgb[0], rgb[1], rgb[2]);
                Map<String, Color> map = NAMED_COLORS;
                map.put(name, c);
                if (name.indexOf(95) > 0) {
                    map.put(name.replace('_', ' '), c);
                }
                if (name.indexOf("_PERCENT") > 0) {
                    map.put(name.replace("_PERCENT", "%").replace('_', ' '), c);
                }
            }
        }
        String part = "\\\\.                 # Quoted single character\n|\"([^\\\\\"]|\\\\.)*\"         # Quoted string of characters (handles escaped quotes like \\\") \n|(\\[\\$.{0,3}-[0-9a-f]{3}\\])                   # Currency symbol in a given locale\n|_.                             # Space as wide as a given character\n|\\*.                           # Repeating fill character\n|@                              # Text: cell text\n|([0?\\#](?:[0?\\#,]*))         # Number: digit + other digits and commas\n|e[-+]                          # Number: Scientific: Exponent\n|m{1,5}                         # Date: month or minute spec\n|d{1,4}                         # Date: day/date spec\n|y{2,4}                         # Date: year spec\n|h{1,2}                         # Date: hour spec\n|s{1,2}                         # Date: second spec\n|am?/pm?                        # Date: am/pm spec\n|\\[h{1,2}\\]                   # Elapsed time: hour spec\n|\\[m{1,2}\\]                   # Elapsed time: minute spec\n|\\[s{1,2}\\]                   # Elapsed time: second spec\n|[^;]                           # A character\n";
        String format = "(?:\\[(black|blue|cyan|green|magenta|red|white|yellow|color [0-9]+)\\])?                 # Text color\n(?:\\[([<>=]=?|!=|<>)    # The operator\n  \\s*([0-9]+(?:\\.[0-9]*)?)\\s*  # The constant to test against\n\\])?               # Condition\n(?:\\[\\$-[0-9a-fA-F]+\\])?                # Optional locale id, ignored currently\n((?:" + part + ")+)                        # Format spec\n";
        COLOR_PAT = Pattern.compile("\\[(black|blue|cyan|green|magenta|red|white|yellow|color [0-9]+)\\]", 6);
        CONDITION_PAT = Pattern.compile("([<>=]=?|!=|<>)    # The operator\n  \\s*([0-9]+(?:\\.[0-9]*)?)\\s*  # The constant to test against\n", 6);
        SPECIFICATION_PAT = Pattern.compile(part, 6);
        CURRENCY_PAT = Pattern.compile("(\\[\\$.{0,3}-[0-9a-f]{3}\\])", 6);
        Pattern patternCompile = Pattern.compile(format, 6);
        FORMAT_PAT = patternCompile;
        COLOR_GROUP = findGroup(patternCompile, "[Blue]@", "Blue");
        CONDITION_OPERATOR_GROUP = findGroup(patternCompile, "[>=1]@", ">=");
        CONDITION_VALUE_GROUP = findGroup(patternCompile, "[>=1]@", "1");
        SPECIFICATION_GROUP = findGroup(patternCompile, "[Blue][>1]\\a ?", "\\a ?");
    }

    public CellFormatPart(String desc) {
        this(LocaleUtil.getUserLocale(), desc);
    }

    public CellFormatPart(Locale locale, String desc) {
        Matcher m = FORMAT_PAT.matcher(desc);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unrecognized format: " + CellFormatter.quote(desc));
        }
        this.color = getColor(m);
        this.condition = getCondition(m);
        this.type = getCellFormatType(m);
        this.format = getFormatter(locale, m);
    }

    public boolean applies(Object valueObject) {
        CellFormatCondition cellFormatCondition = this.condition;
        if (cellFormatCondition == null || !(valueObject instanceof Number)) {
            if (valueObject == null) {
                throw new NullPointerException("valueObject");
            }
            return true;
        }
        Number num = (Number) valueObject;
        return cellFormatCondition.pass(num.doubleValue());
    }

    private static int findGroup(Pattern pat, String str, String marker) {
        Matcher m = pat.matcher(str);
        if (!m.find()) {
            throw new IllegalArgumentException("Pattern \"" + pat.pattern() + "\" doesn't match \"" + str + "\"");
        }
        for (int i = 1; i <= m.groupCount(); i++) {
            String grp = m.group(i);
            if (grp != null && grp.equals(marker)) {
                return i;
            }
        }
        throw new IllegalArgumentException("\"" + marker + "\" not found in \"" + pat.pattern() + "\"");
    }

    private static Color getColor(Matcher m) {
        String cdesc = m.group(COLOR_GROUP);
        if (cdesc == null || cdesc.length() == 0) {
            return null;
        }
        Color c = NAMED_COLORS.get(cdesc);
        if (c == null) {
            CellFormatter.logger.warning("Unknown color: " + CellFormatter.quote(cdesc));
        }
        return c;
    }

    private CellFormatCondition getCondition(Matcher m) {
        int i = CONDITION_OPERATOR_GROUP;
        String mdesc = m.group(i);
        if (mdesc == null || mdesc.length() == 0) {
            return null;
        }
        return CellFormatCondition.getInstance(m.group(i), m.group(CONDITION_VALUE_GROUP));
    }

    private CellFormatType getCellFormatType(Matcher matcher) {
        String fdesc = matcher.group(SPECIFICATION_GROUP);
        return formatType(fdesc);
    }

    private CellFormatter getFormatter(Locale locale, Matcher matcher) {
        String currencyRepl;
        String fdesc = matcher.group(SPECIFICATION_GROUP);
        Matcher currencyM = CURRENCY_PAT.matcher(fdesc);
        if (currencyM.find()) {
            String currencyPart = currencyM.group(1);
            if (currencyPart.startsWith("[$-")) {
                currencyRepl = "$";
            } else {
                currencyRepl = currencyPart.substring(2, currencyPart.lastIndexOf(45));
            }
            fdesc = fdesc.replace(currencyPart, currencyRepl);
        }
        return this.type.formatter(locale, fdesc);
    }

    private CellFormatType formatType(String fdesc) {
        String fdesc2 = fdesc.trim();
        if (fdesc2.equals("") || fdesc2.equalsIgnoreCase("General")) {
            return CellFormatType.GENERAL;
        }
        Matcher m = SPECIFICATION_PAT.matcher(fdesc2);
        boolean couldBeDate = false;
        boolean seenZero = false;
        while (m.find()) {
            String repl = m.group(0);
            if (repl.length() > 0) {
                char c1 = repl.charAt(0);
                char c2 = 0;
                if (repl.length() > 1) {
                    c2 = Character.toLowerCase(repl.charAt(1));
                }
                switch (c1) {
                    case '#':
                    case '?':
                        return CellFormatType.NUMBER;
                    case '0':
                        seenZero = true;
                        break;
                    case '@':
                        return CellFormatType.TEXT;
                    case 'D':
                    case 'Y':
                    case 'd':
                    case 'y':
                        return CellFormatType.DATE;
                    case 'H':
                    case 'M':
                    case 'S':
                    case 'h':
                    case 'm':
                    case 's':
                        couldBeDate = true;
                        break;
                    case '[':
                        if (c2 == 'h' || c2 == 'm' || c2 == 's') {
                            return CellFormatType.ELAPSED;
                        }
                        if (c2 == '$') {
                            return CellFormatType.NUMBER;
                        }
                        throw new IllegalArgumentException("Unsupported [] format block '" + repl + "' in '" + fdesc2 + "' with c2: " + c2);
                }
            }
        }
        if (couldBeDate) {
            return CellFormatType.DATE;
        }
        if (seenZero) {
            return CellFormatType.NUMBER;
        }
        return CellFormatType.TEXT;
    }

    static String quoteSpecial(String repl, CellFormatType type) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repl.length(); i++) {
            char ch = repl.charAt(i);
            if (ch == '\'' && type.isSpecial('\'')) {
                sb.append((char) 0);
            } else {
                boolean special = type.isSpecial(ch);
                if (special) {
                    sb.append("'");
                }
                sb.append(ch);
                if (special) {
                    sb.append("'");
                }
            }
        }
        return sb.toString();
    }

    public CellFormatResult apply(Object value) {
        String text;
        Color textColor;
        boolean applies = applies(value);
        if (applies) {
            text = this.format.format(value);
            textColor = this.color;
        } else {
            text = this.format.simpleFormat(value);
            textColor = null;
        }
        return new CellFormatResult(applies, text, textColor);
    }

    public CellFormatResult apply(JLabel label, Object value) {
        CellFormatResult result = apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }

    CellFormatType getCellFormatType() {
        return this.type;
    }

    boolean hasCondition() {
        return this.condition != null;
    }

    public static StringBuffer parseFormat(String fdesc, CellFormatType type, PartHandler partHandler) {
        Matcher m = SPECIFICATION_PAT.matcher(fdesc);
        StringBuffer fmt = new StringBuffer();
        while (m.find()) {
            String part = group(m, 0);
            if (part.length() > 0) {
                String repl = partHandler.handlePart(m, part, type, fmt);
                if (repl == null) {
                    char cCharAt = part.charAt(0);
                    if (cCharAt == '\"') {
                        repl = quoteSpecial(part.substring(1, part.length() - 1), type);
                    } else if (cCharAt == '*') {
                        repl = expandChar(part);
                    } else if (cCharAt == '\\') {
                        repl = quoteSpecial(part.substring(1), type);
                    } else if (cCharAt == '_') {
                        repl = " ";
                    } else {
                        repl = part;
                    }
                }
                m.appendReplacement(fmt, Matcher.quoteReplacement(repl));
            }
        }
        m.appendTail(fmt);
        if (type.isSpecial('\'')) {
            int pos = 0;
            while (true) {
                int iIndexOf = fmt.indexOf("''", pos);
                pos = iIndexOf;
                if (iIndexOf < 0) {
                    break;
                }
                fmt.delete(pos, pos + 2);
            }
            int pos2 = 0;
            while (true) {
                int iIndexOf2 = fmt.indexOf("\u0000", pos2);
                pos2 = iIndexOf2;
                if (iIndexOf2 < 0) {
                    break;
                }
                fmt.replace(pos2, pos2 + 1, "''");
            }
        }
        return fmt;
    }

    static String expandChar(String part) {
        char ch = part.charAt(1);
        String repl = "" + ch + ch + ch;
        return repl;
    }

    public static String group(Matcher m, int g) {
        String str = m.group(g);
        return str == null ? "" : str;
    }

    public String toString() {
        return this.format.format;
    }
}
