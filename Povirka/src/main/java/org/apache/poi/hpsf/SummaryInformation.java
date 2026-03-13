package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;

/* JADX INFO: loaded from: classes.dex */
public final class SummaryInformation extends SpecialPropertySet {
    public static final String DEFAULT_STREAM_NAME = "\u0005SummaryInformation";

    @Override // org.apache.poi.hpsf.PropertySet
    public PropertyIDMap getPropertySetIDMap() {
        return PropertyIDMap.getSummaryInformationProperties();
    }

    public SummaryInformation() {
        getFirstSection().setFormatID(SectionIDMap.SUMMARY_INFORMATION_ID);
    }

    public SummaryInformation(PropertySet ps) throws UnexpectedPropertySetTypeException {
        super(ps);
        if (!isSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException("Not a " + getClass().getName());
        }
    }

    public SummaryInformation(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        super(stream);
    }

    public String getTitle() {
        return getPropertyStringValue(2);
    }

    public void setTitle(String title) {
        set1stProperty(2L, title);
    }

    public void removeTitle() {
        remove1stProperty(2L);
    }

    public String getSubject() {
        return getPropertyStringValue(3);
    }

    public void setSubject(String subject) {
        set1stProperty(3L, subject);
    }

    public void removeSubject() {
        remove1stProperty(3L);
    }

    public String getAuthor() {
        return getPropertyStringValue(4);
    }

    public void setAuthor(String author) {
        set1stProperty(4L, author);
    }

    public void removeAuthor() {
        remove1stProperty(4L);
    }

    public String getKeywords() {
        return getPropertyStringValue(5);
    }

    public void setKeywords(String keywords) {
        set1stProperty(5L, keywords);
    }

    public void removeKeywords() {
        remove1stProperty(5L);
    }

    public String getComments() {
        return getPropertyStringValue(6);
    }

    public void setComments(String comments) {
        set1stProperty(6L, comments);
    }

    public void removeComments() {
        remove1stProperty(6L);
    }

    public String getTemplate() {
        return getPropertyStringValue(7);
    }

    public void setTemplate(String template) {
        set1stProperty(7L, template);
    }

    public void removeTemplate() {
        remove1stProperty(7L);
    }

    public String getLastAuthor() {
        return getPropertyStringValue(8);
    }

    public void setLastAuthor(String lastAuthor) {
        set1stProperty(8L, lastAuthor);
    }

    public void removeLastAuthor() {
        remove1stProperty(8L);
    }

    public String getRevNumber() {
        return getPropertyStringValue(9);
    }

    public void setRevNumber(String revNumber) {
        set1stProperty(9L, revNumber);
    }

    public void removeRevNumber() {
        remove1stProperty(9L);
    }

    public long getEditTime() {
        java.util.Date d = (java.util.Date) getProperty(10);
        if (d == null) {
            return 0L;
        }
        return Filetime.dateToFileTime(d);
    }

    public void setEditTime(long time) {
        java.util.Date d = Filetime.filetimeToDate(time);
        getFirstSection().setProperty(10, 64L, d);
    }

    public void removeEditTime() {
        remove1stProperty(10L);
    }

    public java.util.Date getLastPrinted() {
        return (java.util.Date) getProperty(11);
    }

    public void setLastPrinted(java.util.Date lastPrinted) {
        getFirstSection().setProperty(11, 64L, lastPrinted);
    }

    public void removeLastPrinted() {
        remove1stProperty(11L);
    }

    public java.util.Date getCreateDateTime() {
        return (java.util.Date) getProperty(12);
    }

    public void setCreateDateTime(java.util.Date createDateTime) {
        getFirstSection().setProperty(12, 64L, createDateTime);
    }

    public void removeCreateDateTime() {
        remove1stProperty(12L);
    }

    public java.util.Date getLastSaveDateTime() {
        return (java.util.Date) getProperty(13);
    }

    public void setLastSaveDateTime(java.util.Date time) {
        Section s = getFirstSection();
        s.setProperty(13, 64L, time);
    }

    public void removeLastSaveDateTime() {
        remove1stProperty(13L);
    }

    public int getPageCount() {
        return getPropertyIntValue(14);
    }

    public void setPageCount(int pageCount) {
        set1stProperty(14L, pageCount);
    }

    public void removePageCount() {
        remove1stProperty(14L);
    }

    public int getWordCount() {
        return getPropertyIntValue(15);
    }

    public void setWordCount(int wordCount) {
        set1stProperty(15L, wordCount);
    }

    public void removeWordCount() {
        remove1stProperty(15L);
    }

    public int getCharCount() {
        return getPropertyIntValue(16);
    }

    public void setCharCount(int charCount) {
        set1stProperty(16L, charCount);
    }

    public void removeCharCount() {
        remove1stProperty(16L);
    }

    public byte[] getThumbnail() {
        return (byte[]) getProperty(17);
    }

    public Thumbnail getThumbnailThumbnail() {
        byte[] data = getThumbnail();
        if (data == null) {
            return null;
        }
        return new Thumbnail(data);
    }

    public void setThumbnail(byte[] thumbnail) {
        getFirstSection().setProperty(17, 30L, thumbnail);
    }

    public void removeThumbnail() {
        remove1stProperty(17L);
    }

    public String getApplicationName() {
        return getPropertyStringValue(18);
    }

    public void setApplicationName(String applicationName) {
        set1stProperty(18L, applicationName);
    }

    public void removeApplicationName() {
        remove1stProperty(18L);
    }

    public int getSecurity() {
        return getPropertyIntValue(19);
    }

    public void setSecurity(int security) {
        set1stProperty(19L, security);
    }

    public void removeSecurity() {
        remove1stProperty(19L);
    }
}
