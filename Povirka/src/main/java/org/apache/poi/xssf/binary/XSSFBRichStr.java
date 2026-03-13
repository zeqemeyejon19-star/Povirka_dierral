package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBRichStr {
    private final String phoneticString;
    private final String string;

    public static XSSFBRichStr build(byte[] bytes, int offset) throws XSSFBParseException {
        byte first = bytes[offset];
        if (((first >> 7) & 1) == 1) {
        }
        if (((first >> 6) & 1) != 1) {
        }
        StringBuilder sb = new StringBuilder();
        XSSFBUtils.readXLWideString(bytes, offset + 1, sb);
        return new XSSFBRichStr(sb.toString(), "");
    }

    XSSFBRichStr(String string, String phoneticString) {
        this.string = string;
        this.phoneticString = phoneticString;
    }

    public String getString() {
        return this.string;
    }
}
