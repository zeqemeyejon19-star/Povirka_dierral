package org.apache.poi.xssf.binary;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.util.Internal;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBRichTextString extends XSSFRichTextString {
    private final String string;

    XSSFBRichTextString(String string) {
        this.string = string;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public void applyFont(int startIndex, int endIndex, short fontIndex) {
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public void applyFont(int startIndex, int endIndex, Font font) {
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public void applyFont(Font font) {
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public void clearFormatting() {
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    public String getString() {
        return this.string;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    public int length() {
        return this.string.length();
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public int numFormattingRuns() {
        return 0;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public int getIndexOfFormattingRun(int index) {
        return 0;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFRichTextString, org.apache.poi.ss.usermodel.RichTextString
    @NotImplemented
    public void applyFont(short fontIndex) {
    }
}
