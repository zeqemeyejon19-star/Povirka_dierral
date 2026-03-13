package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFRichTextString implements Comparable<HSSFRichTextString>, RichTextString {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final short NO_FONT = 0;
    private InternalWorkbook _book;
    private LabelSSTRecord _record;
    private UnicodeString _string;

    public HSSFRichTextString() {
        this("");
    }

    public HSSFRichTextString(String string) {
        if (string == null) {
            this._string = new UnicodeString("");
        } else {
            this._string = new UnicodeString(string);
        }
    }

    HSSFRichTextString(InternalWorkbook book, LabelSSTRecord record) {
        setWorkbookReferences(book, record);
        this._string = book.getSSTString(record.getSSTIndex());
    }

    void setWorkbookReferences(InternalWorkbook book, LabelSSTRecord record) {
        this._book = book;
        this._record = record;
    }

    private UnicodeString cloneStringIfRequired() {
        if (this._book == null) {
            return this._string;
        }
        UnicodeString s = (UnicodeString) this._string.clone();
        return s;
    }

    private void addToSSTIfRequired() {
        InternalWorkbook internalWorkbook = this._book;
        if (internalWorkbook != null) {
            int index = internalWorkbook.addSSTString(this._string);
            this._record.setSSTIndex(index);
            this._string = this._book.getSSTString(index);
        }
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public void applyFont(int startIndex, int endIndex, short fontIndex) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index must be less than end index.");
        }
        if (startIndex < 0 || endIndex > length()) {
            throw new IllegalArgumentException("Start and end index not in range.");
        }
        if (startIndex == endIndex) {
            return;
        }
        short currentFont = 0;
        if (endIndex != length()) {
            currentFont = getFontAtIndex(endIndex);
        }
        UnicodeString unicodeStringCloneStringIfRequired = cloneStringIfRequired();
        this._string = unicodeStringCloneStringIfRequired;
        Iterator<UnicodeString.FormatRun> formatting = unicodeStringCloneStringIfRequired.formatIterator();
        if (formatting != null) {
            while (formatting.hasNext()) {
                UnicodeString.FormatRun r = formatting.next();
                if (r.getCharacterPos() >= startIndex && r.getCharacterPos() < endIndex) {
                    formatting.remove();
                }
            }
        }
        this._string.addFormatRun(new UnicodeString.FormatRun((short) startIndex, fontIndex));
        if (endIndex != length()) {
            this._string.addFormatRun(new UnicodeString.FormatRun((short) endIndex, currentFont));
        }
        addToSSTIfRequired();
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public void applyFont(int startIndex, int endIndex, Font font) {
        applyFont(startIndex, endIndex, ((HSSFFont) font).getIndex());
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public void applyFont(Font font) {
        applyFont(0, this._string.getCharCount(), font);
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public void clearFormatting() {
        UnicodeString unicodeStringCloneStringIfRequired = cloneStringIfRequired();
        this._string = unicodeStringCloneStringIfRequired;
        unicodeStringCloneStringIfRequired.clearFormatting();
        addToSSTIfRequired();
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public String getString() {
        return this._string.getString();
    }

    UnicodeString getUnicodeString() {
        return cloneStringIfRequired();
    }

    UnicodeString getRawUnicodeString() {
        return this._string;
    }

    void setUnicodeString(UnicodeString str) {
        this._string = str;
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public int length() {
        return this._string.getCharCount();
    }

    public short getFontAtIndex(int index) {
        int size = this._string.getFormatRunCount();
        UnicodeString.FormatRun currentRun = null;
        for (int i = 0; i < size; i++) {
            UnicodeString.FormatRun r = this._string.getFormatRun(i);
            if (r.getCharacterPos() > index) {
                break;
            }
            currentRun = r;
        }
        if (currentRun == null) {
            return (short) 0;
        }
        return currentRun.getFontIndex();
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public int numFormattingRuns() {
        return this._string.getFormatRunCount();
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public int getIndexOfFormattingRun(int index) {
        UnicodeString.FormatRun r = this._string.getFormatRun(index);
        return r.getCharacterPos();
    }

    public short getFontOfFormattingRun(int index) {
        UnicodeString.FormatRun r = this._string.getFormatRun(index);
        return r.getFontIndex();
    }

    @Override // java.lang.Comparable
    public int compareTo(HSSFRichTextString r) {
        return this._string.compareTo(r._string);
    }

    public boolean equals(Object o) {
        if (o instanceof HSSFRichTextString) {
            return this._string.equals(((HSSFRichTextString) o)._string);
        }
        return false;
    }

    public int hashCode() {
        throw new AssertionError("hashCode not designed");
    }

    public String toString() {
        return this._string.toString();
    }

    @Override // org.apache.poi.ss.usermodel.RichTextString
    public void applyFont(short fontIndex) {
        applyFont(0, this._string.getCharCount(), fontIndex);
    }
}
