package org.apache.poi.hpsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;

/* JADX INFO: loaded from: classes.dex */
public class DocumentSummaryInformation extends SpecialPropertySet {
    public static final String DEFAULT_STREAM_NAME = "\u0005DocumentSummaryInformation";

    @Override // org.apache.poi.hpsf.PropertySet
    public PropertyIDMap getPropertySetIDMap() {
        return PropertyIDMap.getDocumentSummaryInformationProperties();
    }

    public DocumentSummaryInformation() {
        getFirstSection().setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[0]);
    }

    public DocumentSummaryInformation(PropertySet ps) throws UnexpectedPropertySetTypeException {
        super(ps);
        if (!isDocumentSummaryInformation()) {
            throw new UnexpectedPropertySetTypeException("Not a " + getClass().getName());
        }
    }

    public DocumentSummaryInformation(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        super(stream);
    }

    public String getCategory() {
        return getPropertyStringValue(2);
    }

    public void setCategory(String category) {
        getFirstSection().setProperty(2, category);
    }

    public void removeCategory() {
        remove1stProperty(2L);
    }

    public String getPresentationFormat() {
        return getPropertyStringValue(3);
    }

    public void setPresentationFormat(String presentationFormat) {
        getFirstSection().setProperty(3, presentationFormat);
    }

    public void removePresentationFormat() {
        remove1stProperty(3L);
    }

    public int getByteCount() {
        return getPropertyIntValue(4);
    }

    public void setByteCount(int byteCount) {
        set1stProperty(4L, byteCount);
    }

    public void removeByteCount() {
        remove1stProperty(4L);
    }

    public int getLineCount() {
        return getPropertyIntValue(5);
    }

    public void setLineCount(int lineCount) {
        set1stProperty(5L, lineCount);
    }

    public void removeLineCount() {
        remove1stProperty(5L);
    }

    public int getParCount() {
        return getPropertyIntValue(6);
    }

    public void setParCount(int parCount) {
        set1stProperty(6L, parCount);
    }

    public void removeParCount() {
        remove1stProperty(6L);
    }

    public int getSlideCount() {
        return getPropertyIntValue(7);
    }

    public void setSlideCount(int slideCount) {
        set1stProperty(7L, slideCount);
    }

    public void removeSlideCount() {
        remove1stProperty(7L);
    }

    public int getNoteCount() {
        return getPropertyIntValue(8);
    }

    public void setNoteCount(int noteCount) {
        set1stProperty(8L, noteCount);
    }

    public void removeNoteCount() {
        remove1stProperty(8L);
    }

    public int getHiddenCount() {
        return getPropertyIntValue(9);
    }

    public void setHiddenCount(int hiddenCount) {
        set1stProperty(9L, hiddenCount);
    }

    public void removeHiddenCount() {
        remove1stProperty(9L);
    }

    public int getMMClipCount() {
        return getPropertyIntValue(10);
    }

    public void setMMClipCount(int mmClipCount) {
        set1stProperty(10L, mmClipCount);
    }

    public void removeMMClipCount() {
        remove1stProperty(10L);
    }

    public boolean getScale() {
        return getPropertyBooleanValue(11);
    }

    public void setScale(boolean scale) {
        set1stProperty(11L, scale);
    }

    public void removeScale() {
        remove1stProperty(11L);
    }

    public byte[] getHeadingPair() {
        notYetImplemented("Reading byte arrays ");
        return (byte[]) getProperty(12);
    }

    public void setHeadingPair(byte[] headingPair) {
        notYetImplemented("Writing byte arrays ");
    }

    public void removeHeadingPair() {
        remove1stProperty(12L);
    }

    public byte[] getDocparts() {
        notYetImplemented("Reading byte arrays");
        return (byte[]) getProperty(13);
    }

    public void setDocparts(byte[] docparts) {
        notYetImplemented("Writing byte arrays");
    }

    public void removeDocparts() {
        remove1stProperty(13L);
    }

    public String getManager() {
        return getPropertyStringValue(14);
    }

    public void setManager(String manager) {
        set1stProperty(14L, manager);
    }

    public void removeManager() {
        remove1stProperty(14L);
    }

    public String getCompany() {
        return getPropertyStringValue(15);
    }

    public void setCompany(String company) {
        set1stProperty(15L, company);
    }

    public void removeCompany() {
        remove1stProperty(15L);
    }

    public boolean getLinksDirty() {
        return getPropertyBooleanValue(16);
    }

    public void setLinksDirty(boolean linksDirty) {
        set1stProperty(16L, linksDirty);
    }

    public void removeLinksDirty() {
        remove1stProperty(16L);
    }

    public int getCharCountWithSpaces() {
        return getPropertyIntValue(17);
    }

    public void setCharCountWithSpaces(int count) {
        set1stProperty(17L, count);
    }

    public void removeCharCountWithSpaces() {
        remove1stProperty(17L);
    }

    public boolean getHyperlinksChanged() {
        return getPropertyBooleanValue(22);
    }

    public void setHyperlinksChanged(boolean changed) {
        set1stProperty(22L, changed);
    }

    public void removeHyperlinksChanged() {
        remove1stProperty(22L);
    }

    public int getApplicationVersion() {
        return getPropertyIntValue(23);
    }

    public void setApplicationVersion(int version) {
        set1stProperty(23L, version);
    }

    public void removeApplicationVersion() {
        remove1stProperty(23L);
    }

    public byte[] getVBADigitalSignature() {
        Object value = getProperty(24);
        if (value != null && (value instanceof byte[])) {
            return (byte[]) value;
        }
        return null;
    }

    public void setVBADigitalSignature(byte[] signature) {
        set1stProperty(24L, signature);
    }

    public void removeVBADigitalSignature() {
        remove1stProperty(24L);
    }

    public String getContentType() {
        return getPropertyStringValue(26);
    }

    public void setContentType(String type) {
        set1stProperty(26L, type);
    }

    public void removeContentType() {
        remove1stProperty(26L);
    }

    public String getContentStatus() {
        return getPropertyStringValue(27);
    }

    public void setContentStatus(String status) {
        set1stProperty(27L, status);
    }

    public void removeContentStatus() {
        remove1stProperty(27L);
    }

    public String getLanguage() {
        return getPropertyStringValue(28);
    }

    public void setLanguage(String language) {
        set1stProperty(28L, language);
    }

    public void removeLanguage() {
        remove1stProperty(28L);
    }

    public String getDocumentVersion() {
        return getPropertyStringValue(29);
    }

    public void setDocumentVersion(String version) {
        set1stProperty(29L, version);
    }

    public void removeDocumentVersion() {
        remove1stProperty(29L);
    }

    public CustomProperties getCustomProperties() throws UnsupportedEncodingException {
        CustomProperties cps = null;
        if (getSectionCount() >= 2) {
            cps = new CustomProperties();
            Section section = getSections().get(1);
            Map<Long, String> dictionary = section.getDictionary();
            Property[] properties = section.getProperties();
            int propertyCount = 0;
            for (Property p : properties) {
                long id = p.getID();
                if (id == 1) {
                    cps.setCodepage(((Integer) p.getValue()).intValue());
                } else if (id > 1) {
                    propertyCount++;
                    CustomProperty cp = new CustomProperty(p, dictionary.get(Long.valueOf(id)));
                    cps.put(cp.getName(), cp);
                }
            }
            if (cps.size() != propertyCount) {
                cps.setPure(false);
            }
        }
        return cps;
    }

    public void setCustomProperties(CustomProperties customProperties) {
        ensureSection2();
        Section section = getSections().get(1);
        Map<Long, String> dictionary = customProperties.getDictionary();
        int cpCodepage = customProperties.getCodepage();
        if (cpCodepage < 0) {
            cpCodepage = section.getCodepage();
        }
        if (cpCodepage < 0) {
            cpCodepage = 1252;
        }
        customProperties.setCodepage(cpCodepage);
        section.setCodepage(cpCodepage);
        section.setDictionary(dictionary);
        for (CustomProperty p : customProperties.properties()) {
            section.setProperty(p);
        }
    }

    private void ensureSection2() {
        if (getSectionCount() < 2) {
            Section s2 = new MutableSection();
            s2.setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[1]);
            addSection(s2);
        }
    }

    public void removeCustomProperties() {
        if (getSectionCount() < 2) {
            throw new HPSFRuntimeException("Illegal internal format of Document SummaryInformation stream: second section is missing.");
        }
        List<Section> l = new LinkedList<>(getSections());
        clearSections();
        int idx = 0;
        for (Section s : l) {
            int idx2 = idx + 1;
            if (idx != 1) {
                addSection(s);
            }
            idx = idx2;
        }
    }

    private void notYetImplemented(String msg) {
        throw new UnsupportedOperationException(msg + " is not yet implemented.");
    }
}
