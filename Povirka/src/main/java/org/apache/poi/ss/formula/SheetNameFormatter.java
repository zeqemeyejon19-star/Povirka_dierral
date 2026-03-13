package org.apache.poi.ss.formula;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public final class SheetNameFormatter {
    private static final Pattern CELL_REF_PATTERN = Pattern.compile("([A-Za-z]+)([0-9]+)");
    private static final char DELIMITER = '\'';

    private SheetNameFormatter() {
    }

    public static String format(String rawSheetName) {
        StringBuffer sb = new StringBuffer(rawSheetName.length() + 2);
        appendFormat(sb, rawSheetName);
        return sb.toString();
    }

    public static void appendFormat(StringBuffer out, String rawSheetName) {
        boolean needsQuotes = needsDelimiting(rawSheetName);
        if (needsQuotes) {
            out.append(DELIMITER);
            appendAndEscape(out, rawSheetName);
            out.append(DELIMITER);
            return;
        }
        out.append(rawSheetName);
    }

    public static void appendFormat(StringBuffer out, String workbookName, String rawSheetName) {
        boolean needsQuotes = needsDelimiting(workbookName) || needsDelimiting(rawSheetName);
        if (needsQuotes) {
            out.append(DELIMITER);
            out.append('[');
            appendAndEscape(out, workbookName.replace('[', '(').replace(']', ')'));
            out.append(']');
            appendAndEscape(out, rawSheetName);
            out.append(DELIMITER);
            return;
        }
        out.append('[');
        out.append(workbookName);
        out.append(']');
        out.append(rawSheetName);
    }

    private static void appendAndEscape(StringBuffer sb, String rawSheetName) {
        int len = rawSheetName.length();
        for (int i = 0; i < len; i++) {
            char ch = rawSheetName.charAt(i);
            if (ch == '\'') {
                sb.append(DELIMITER);
            }
            sb.append(ch);
        }
    }

    private static boolean needsDelimiting(String rawSheetName) {
        int len = rawSheetName.length();
        if (len < 1) {
            throw new RuntimeException("Zero length string is an invalid sheet name");
        }
        if (Character.isDigit(rawSheetName.charAt(0))) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            char ch = rawSheetName.charAt(i);
            if (isSpecialChar(ch)) {
                return true;
            }
        }
        if ((Character.isLetter(rawSheetName.charAt(0)) && Character.isDigit(rawSheetName.charAt(len - 1)) && nameLooksLikePlainCellReference(rawSheetName)) || nameLooksLikeBooleanLiteral(rawSheetName)) {
            return true;
        }
        return false;
    }

    private static boolean nameLooksLikeBooleanLiteral(String rawSheetName) {
        char cCharAt = rawSheetName.charAt(0);
        if (cCharAt != 'F') {
            if (cCharAt != 'T') {
                if (cCharAt != 'f') {
                    if (cCharAt != 't') {
                        return false;
                    }
                }
            }
            return "TRUE".equalsIgnoreCase(rawSheetName);
        }
        return "FALSE".equalsIgnoreCase(rawSheetName);
    }

    static boolean isSpecialChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return false;
        }
        if (ch == '\t' || ch == '\n' || ch == '\r') {
            throw new RuntimeException("Illegal character (0x" + Integer.toHexString(ch) + ") found in sheet name");
        }
        return (ch == '.' || ch == '_') ? false : true;
    }

    static boolean cellReferenceIsWithinRange(String lettersPrefix, String numbersSuffix) {
        return CellReference.cellReferenceIsWithinRange(lettersPrefix, numbersSuffix, SpreadsheetVersion.EXCEL97);
    }

    static boolean nameLooksLikePlainCellReference(String rawSheetName) {
        Matcher matcher = CELL_REF_PATTERN.matcher(rawSheetName);
        if (!matcher.matches()) {
            return false;
        }
        String lettersPrefix = matcher.group(1);
        String numbersSuffix = matcher.group(2);
        return cellReferenceIsWithinRange(lettersPrefix, numbersSuffix);
    }
}
