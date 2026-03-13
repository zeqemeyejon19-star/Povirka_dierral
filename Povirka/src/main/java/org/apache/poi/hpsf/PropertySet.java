package org.apache.poi.hpsf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.EmptyFileException;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.NotImplemented;

/* JADX INFO: loaded from: classes.dex */
public class PropertySet {
    static final int BYTE_ORDER_ASSERTION = 65534;
    static final int FORMAT_ASSERTION = 0;
    static final int OFFSET_HEADER = 28;
    public static final int OS_MACINTOSH = 1;
    public static final int OS_WIN16 = 0;
    public static final int OS_WIN32 = 2;
    private int byteOrder;
    private ClassID classID;
    private int format;
    private int osVersion;
    private final List<Section> sections;

    public PropertySet() {
        this.sections = new ArrayList();
        this.byteOrder = BYTE_ORDER_ASSERTION;
        this.format = 0;
        this.osVersion = 133636;
        this.classID = new ClassID();
        addSection(new MutableSection());
    }

    public PropertySet(InputStream stream) throws MarkUnsupportedException, NoPropertySetStreamException, IOException {
        this.sections = new ArrayList();
        if (!isPropertySetStream(stream)) {
            throw new NoPropertySetStreamException();
        }
        byte[] buffer = IOUtils.toByteArray(stream);
        init(buffer, 0, buffer.length);
    }

    public PropertySet(byte[] stream, int offset, int length) throws NoPropertySetStreamException, UnsupportedEncodingException {
        this.sections = new ArrayList();
        if (!isPropertySetStream(stream, offset, length)) {
            throw new NoPropertySetStreamException();
        }
        init(stream, offset, length);
    }

    public PropertySet(byte[] stream) throws NoPropertySetStreamException, UnsupportedEncodingException {
        this(stream, 0, stream.length);
    }

    public PropertySet(PropertySet ps) {
        this.sections = new ArrayList();
        setByteOrder(ps.getByteOrder());
        setFormat(ps.getFormat());
        setOSVersion(ps.getOSVersion());
        setClassID(ps.getClassID());
        for (Section section : ps.getSections()) {
            this.sections.add(new MutableSection(section));
        }
    }

    public int getByteOrder() {
        return this.byteOrder;
    }

    public void setByteOrder(int byteOrder) {
        this.byteOrder = byteOrder;
    }

    public int getFormat() {
        return this.format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public int getOSVersion() {
        return this.osVersion;
    }

    public void setOSVersion(int osVersion) {
        this.osVersion = osVersion;
    }

    public ClassID getClassID() {
        return this.classID;
    }

    public void setClassID(ClassID classID) {
        this.classID = classID;
    }

    public int getSectionCount() {
        return this.sections.size();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(this.sections);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public void clearSections() {
        this.sections.clear();
    }

    public PropertyIDMap getPropertySetIDMap() {
        return null;
    }

    public static boolean isPropertySetStream(InputStream stream) throws MarkUnsupportedException, IOException {
        try {
            byte[] buffer = IOUtils.peekFirstNBytes(stream, 50);
            boolean isPropertySetStream = isPropertySetStream(buffer, 0, buffer.length);
            return isPropertySetStream;
        } catch (EmptyFileException e) {
            return false;
        }
    }

    public static boolean isPropertySetStream(byte[] src, int offset, int length) {
        int byteOrder = LittleEndian.getUShort(src, offset);
        int o = offset + 2;
        if (byteOrder != BYTE_ORDER_ASSERTION) {
            return false;
        }
        int format = LittleEndian.getUShort(src, o);
        int o2 = o + 2;
        if (format != 0) {
            return false;
        }
        long sectionCount = LittleEndian.getUInt(src, o2 + 4 + 16);
        return sectionCount >= 0;
    }

    private void init(byte[] src, int offset, int length) throws UnsupportedEncodingException {
        this.byteOrder = LittleEndian.getUShort(src, offset);
        int o = offset + 2;
        this.format = LittleEndian.getUShort(src, o);
        int o2 = o + 2;
        this.osVersion = (int) LittleEndian.getUInt(src, o2);
        int o3 = o2 + 4;
        this.classID = new ClassID(src, o3);
        int o4 = o3 + 16;
        int sectionCount = LittleEndian.getInt(src, o4);
        int o5 = o4 + 4;
        if (sectionCount < 0) {
            throw new HPSFRuntimeException("Section count " + sectionCount + " is negative.");
        }
        for (int i = 0; i < sectionCount; i++) {
            Section s = new MutableSection(src, o5);
            o5 += 20;
            this.sections.add(s);
        }
    }

    public void write(OutputStream out) throws IOException, WritingNotSupportedException {
        int nrSections = getSectionCount();
        LittleEndian.putShort(out, (short) getByteOrder());
        LittleEndian.putShort(out, (short) getFormat());
        LittleEndian.putInt(getOSVersion(), out);
        putClassId(out, getClassID());
        LittleEndian.putInt(nrSections, out);
        int offset = 28 + (nrSections * 20);
        for (Section section : getSections()) {
            ClassID formatID = section.getFormatID();
            if (formatID == null) {
                throw new NoFormatIDException();
            }
            putClassId(out, formatID);
            LittleEndian.putUInt(offset, out);
            try {
                offset += section.getSize();
            } catch (HPSFRuntimeException ex) {
                Throwable cause = ex.getReason();
                if (cause instanceof UnsupportedEncodingException) {
                    throw new IllegalPropertySetDataException(cause);
                }
                throw ex;
            }
        }
        int offset2 = offset;
        Iterator<Section> it = getSections().iterator();
        while (it.hasNext()) {
            offset2 += it.next().write(out);
        }
        out.close();
    }

    public void write(DirectoryEntry dir, String name) throws IOException, WritingNotSupportedException {
        if (dir.hasEntry(name)) {
            Entry e = dir.getEntry(name);
            e.delete();
        }
        dir.createDocument(name, toInputStream());
    }

    public InputStream toInputStream() throws IOException, WritingNotSupportedException {
        ByteArrayOutputStream psStream = new ByteArrayOutputStream();
        try {
            write(psStream);
            psStream.close();
            byte[] streamData = psStream.toByteArray();
            return new ByteArrayInputStream(streamData);
        } catch (Throwable th) {
            psStream.close();
            throw th;
        }
    }

    protected String getPropertyStringValue(int propertyId) {
        Object propertyValue = getProperty(propertyId);
        return getPropertyStringValue(propertyValue);
    }

    public static String getPropertyStringValue(Object propertyValue) {
        if (propertyValue == null) {
            return null;
        }
        if (propertyValue instanceof String) {
            return (String) propertyValue;
        }
        if (propertyValue instanceof byte[]) {
            byte[] b = (byte[]) propertyValue;
            int length = b.length;
            if (length == 0) {
                return "";
            }
            if (length == 1) {
                return Byte.toString(b[0]);
            }
            if (length == 2) {
                return Integer.toString(LittleEndian.getUShort(b));
            }
            if (length == 4) {
                return Long.toString(LittleEndian.getUInt(b));
            }
            try {
                return CodePageUtil.getStringFromCodePage(b, 1252);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
        return propertyValue.toString();
    }

    public boolean isSummaryInformation() {
        return !this.sections.isEmpty() && matchesSummary(getFirstSection().getFormatID(), SectionIDMap.SUMMARY_INFORMATION_ID);
    }

    public boolean isDocumentSummaryInformation() {
        return !this.sections.isEmpty() && matchesSummary(getFirstSection().getFormatID(), SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID);
    }

    static boolean matchesSummary(ClassID actual, ClassID... expected) {
        for (ClassID sum : expected) {
            if (sum.equals(actual) || sum.equalsInverted(actual)) {
                return true;
            }
        }
        return false;
    }

    public Property[] getProperties() throws NoSingleSectionException {
        return getFirstSection().getProperties();
    }

    protected Object getProperty(int id) throws NoSingleSectionException {
        return getFirstSection().getProperty(id);
    }

    protected boolean getPropertyBooleanValue(int id) throws NoSingleSectionException {
        return getFirstSection().getPropertyBooleanValue(id);
    }

    protected int getPropertyIntValue(int id) throws NoSingleSectionException {
        return getFirstSection().getPropertyIntValue(id);
    }

    public boolean wasNull() throws NoSingleSectionException {
        return getFirstSection().wasNull();
    }

    public Section getFirstSection() {
        if (this.sections.isEmpty()) {
            throw new MissingSectionException("Property set does not contain any sections.");
        }
        return this.sections.get(0);
    }

    public Section getSingleSection() {
        int sectionCount = getSectionCount();
        if (sectionCount != 1) {
            throw new NoSingleSectionException("Property set contains " + sectionCount + " sections.");
        }
        return this.sections.get(0);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PropertySet)) {
            return false;
        }
        PropertySet ps = (PropertySet) o;
        int byteOrder1 = ps.getByteOrder();
        int byteOrder2 = getByteOrder();
        ClassID classID1 = ps.getClassID();
        ClassID classID2 = getClassID();
        int format1 = ps.getFormat();
        int format2 = getFormat();
        int osVersion1 = ps.getOSVersion();
        int osVersion2 = getOSVersion();
        int sectionCount1 = ps.getSectionCount();
        int sectionCount2 = getSectionCount();
        if (byteOrder1 != byteOrder2 || !classID1.equals(classID2) || format1 != format2 || osVersion1 != osVersion2 || sectionCount1 != sectionCount2) {
            return false;
        }
        return getSections().containsAll(ps.getSections());
    }

    @NotImplemented
    public int hashCode() {
        throw new UnsupportedOperationException("FIXME: Not yet implemented.");
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        int sectionCount = getSectionCount();
        b.append(getClass().getName());
        b.append('[');
        b.append("byteOrder: ");
        b.append(getByteOrder());
        b.append(", classID: ");
        b.append(getClassID());
        b.append(", format: ");
        b.append(getFormat());
        b.append(", OSVersion: ");
        b.append(getOSVersion());
        b.append(", sectionCount: ");
        b.append(sectionCount);
        b.append(", sections: [\n");
        for (Section section : getSections()) {
            b.append(section.toString(getPropertySetIDMap()));
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }

    protected void remove1stProperty(long id) {
        getFirstSection().removeProperty(id);
    }

    protected void set1stProperty(long id, String value) {
        getFirstSection().setProperty((int) id, value);
    }

    protected void set1stProperty(long id, int value) {
        getFirstSection().setProperty((int) id, value);
    }

    protected void set1stProperty(long id, boolean value) {
        getFirstSection().setProperty((int) id, value);
    }

    protected void set1stProperty(long id, byte[] value) {
        getFirstSection().setProperty((int) id, value);
    }

    private static void putClassId(OutputStream out, ClassID n) throws IOException {
        byte[] b = new byte[16];
        n.write(b, 0);
        out.write(b, 0, b.length);
    }
}
