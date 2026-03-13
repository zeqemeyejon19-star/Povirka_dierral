package org.apache.poi.ss.util;

import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public class WorkbookUtil {
    public static final String createSafeSheetName(String nameProposal) {
        return createSafeSheetName(nameProposal, ' ');
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0051  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.String createSafeSheetName(java.lang.String r6, char r7) {
        /*
            if (r6 != 0) goto L5
            java.lang.String r0 = "null"
            return r0
        L5:
            int r0 = r6.length()
            r1 = 1
            if (r0 >= r1) goto Lf
            java.lang.String r0 = "empty"
            return r0
        Lf:
            r0 = 31
            int r1 = r6.length()
            int r0 = java.lang.Math.min(r0, r1)
            r1 = 0
            java.lang.String r1 = r6.substring(r1, r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>(r1)
            r3 = 0
        L24:
            if (r3 >= r0) goto L58
            char r4 = r2.charAt(r3)
            if (r4 == 0) goto L51
            r5 = 3
            if (r4 == r5) goto L51
            r5 = 39
            if (r4 == r5) goto L47
            r5 = 42
            if (r4 == r5) goto L51
            r5 = 47
            if (r4 == r5) goto L51
            r5 = 58
            if (r4 == r5) goto L51
            r5 = 63
            if (r4 == r5) goto L51
            switch(r4) {
                case 91: goto L51;
                case 92: goto L51;
                case 93: goto L51;
                default: goto L46;
            }
        L46:
            goto L55
        L47:
            if (r3 == 0) goto L4d
            int r5 = r0 + (-1)
            if (r3 != r5) goto L55
        L4d:
            r2.setCharAt(r3, r7)
            goto L55
        L51:
            r2.setCharAt(r3, r7)
        L55:
            int r3 = r3 + 1
            goto L24
        L58:
            java.lang.String r3 = r2.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.util.WorkbookUtil.createSafeSheetName(java.lang.String, char):java.lang.String");
    }

    public static void validateSheetName(String sheetName) {
        if (sheetName == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        int len = sheetName.length();
        if (len < 1 || len > 31) {
            throw new IllegalArgumentException("sheetName '" + sheetName + "' is invalid - character count MUST be greater than or equal to 1 and less than or equal to 31");
        }
        for (int i = 0; i < len; i++) {
            char ch = sheetName.charAt(i);
            if (ch != '*' && ch != '/' && ch != ':' && ch != '?') {
                switch (ch) {
                    case '[':
                    case '\\':
                    case ']':
                        break;
                    default:
                        break;
                }
            }
            throw new IllegalArgumentException("Invalid char (" + ch + ") found at index (" + i + ") in sheet name '" + sheetName + "'");
        }
        if (sheetName.charAt(0) == '\'' || sheetName.charAt(len - 1) == '\'') {
            throw new IllegalArgumentException("Invalid sheet name '" + sheetName + "'. Sheet names must not begin or end with (').");
        }
    }

    @Removal(version = "3.18")
    @Deprecated
    public static void validateSheetState(int state) {
        if (state != 0 && state != 1 && state != 2) {
            throw new IllegalArgumentException("Invalid sheet state : " + state + "\nSheet state must be one of the Workbook.SHEET_STATE_* constants");
        }
    }
}
