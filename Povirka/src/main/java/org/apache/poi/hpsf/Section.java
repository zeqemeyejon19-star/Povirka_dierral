package org.apache.poi.hpsf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.hpsf.wellknown.SectionIDMap;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class Section {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) Section.class);
    private final long _offset;
    private Map<Long, String> dictionary;
    private ClassID formatID;
    private final Map<Long, Property> properties;
    private final ByteArrayOutputStream sectionBytes;
    private boolean wasNull;

    public Section() {
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap();
        this._offset = -1L;
    }

    public Section(Section s) {
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap();
        this._offset = -1L;
        setFormatID(s.getFormatID());
        for (Property p : s.properties.values()) {
            this.properties.put(Long.valueOf(p.getID()), new MutableProperty(p));
        }
        setDictionary(s.getDictionary());
    }

    public Section(byte[] src, int offset) throws UnsupportedEncodingException {
        int i;
        int codepage;
        int codepage2;
        this.sectionBytes = new ByteArrayOutputStream();
        this.properties = new LinkedHashMap();
        this.formatID = new ClassID(src, offset);
        int offFix = (int) LittleEndian.getUInt(src, offset + 16);
        if (src[offFix] != 0) {
            i = offFix;
        } else {
            int i2 = 0;
            while (i2 < 3 && src[offFix] == 0) {
                i2++;
                offFix++;
            }
            int i3 = 0;
            while (i3 < 3 && (src[offFix + 3] != 0 || src[offFix + 7] != 0 || src[offFix + 11] != 0)) {
                i3++;
                offFix--;
            }
            i = offFix;
        }
        long j = i;
        this._offset = j;
        LittleEndianByteArrayInputStream leis = new LittleEndianByteArrayInputStream(src, i);
        int size = (int) Math.min(leis.readUInt(), ((long) src.length) - j);
        int propertyCount = (int) leis.readUInt();
        TreeBidiMap<Long, Long> offset2Id = new TreeBidiMap<>();
        for (int i4 = 0; i4 < propertyCount; i4++) {
            offset2Id.put(Long.valueOf((int) leis.readUInt()), Long.valueOf((int) leis.readUInt()));
        }
        Long cpOffset = (Long) offset2Id.getKey(1L);
        if (cpOffset == null) {
            codepage = -1;
        } else {
            leis.setReadIndex((int) (this._offset + cpOffset.longValue()));
            long type = leis.readUInt();
            if (type != 2) {
                throw new HPSFRuntimeException("Value type of property ID 1 is not VT_I2 but " + type + ".");
            }
            int codepage3 = leis.readUShort();
            setCodepage(codepage3);
            codepage = codepage3;
        }
        for (Map.Entry<Long, Long> me : offset2Id.entrySet()) {
            long off = me.getKey().longValue();
            long id = me.getValue().longValue();
            if (id != 1) {
                int pLen = propLen(offset2Id, Long.valueOf(off), size);
                leis.setReadIndex((int) (this._offset + off));
                if (id != 0) {
                    codepage2 = codepage;
                    setProperty(new MutableProperty(id, leis, pLen, codepage));
                } else {
                    leis.mark(100000);
                    if (readDictionary(leis, pLen, codepage)) {
                        codepage2 = codepage;
                    } else {
                        leis.reset();
                        try {
                            codepage2 = codepage;
                            try {
                                setProperty(new MutableProperty(Math.max(31L, ((Long) offset2Id.inverseBidiMap().lastKey()).longValue()) + 1, leis, pLen, codepage));
                            } catch (RuntimeException e) {
                                LOG.log(3, "Dictionary fallback failed - ignoring property");
                            }
                        } catch (RuntimeException e2) {
                            codepage2 = codepage;
                        }
                    }
                }
                codepage = codepage2;
            }
        }
        this.sectionBytes.write(src, (int) this._offset, size);
        padSectionBytes();
    }

    private static int propLen(TreeBidiMap<Long, Long> offset2Id, Long entryOffset, long maxSize) {
        Long nextKey = (Long) offset2Id.nextKey(entryOffset);
        long begin = entryOffset.longValue();
        long end = nextKey != null ? nextKey.longValue() : maxSize;
        return (int) (end - begin);
    }

    public ClassID getFormatID() {
        return this.formatID;
    }

    public void setFormatID(ClassID formatID) {
        this.formatID = formatID;
    }

    public void setFormatID(byte[] formatID) {
        ClassID fid = getFormatID();
        if (fid == null) {
            fid = new ClassID();
            setFormatID(fid);
        }
        fid.setBytes(formatID);
    }

    public long getOffset() {
        return this._offset;
    }

    public int getPropertyCount() {
        return this.properties.size();
    }

    public Property[] getProperties() {
        return (Property[]) this.properties.values().toArray(new Property[this.properties.size()]);
    }

    public void setProperties(Property[] properties) {
        this.properties.clear();
        for (Property p : properties) {
            setProperty(p);
        }
    }

    public Object getProperty(long id) {
        boolean z = !this.properties.containsKey(Long.valueOf(id));
        this.wasNull = z;
        if (z) {
            return null;
        }
        return this.properties.get(Long.valueOf(id)).getValue();
    }

    public void setProperty(int id, String value) {
        setProperty(id, 30L, value);
    }

    public void setProperty(int id, int value) {
        setProperty(id, 3L, Integer.valueOf(value));
    }

    public void setProperty(int id, long value) {
        setProperty(id, 20L, Long.valueOf(value));
    }

    public void setProperty(int id, boolean value) {
        setProperty(id, 11L, Boolean.valueOf(value));
    }

    public void setProperty(int id, long variantType, Object value) {
        setProperty(new MutableProperty(id, variantType, value));
    }

    public void setProperty(Property p) {
        Property old = this.properties.get(Long.valueOf(p.getID()));
        if (old == null || !old.equals(p)) {
            this.properties.put(Long.valueOf(p.getID()), p);
            this.sectionBytes.reset();
        }
    }

    public void setProperty(int id, Object value) {
        if (value instanceof String) {
            setProperty(id, (String) value);
            return;
        }
        if (value instanceof Long) {
            setProperty(id, ((Long) value).longValue());
            return;
        }
        if (value instanceof Integer) {
            setProperty(id, ((Integer) value).intValue());
            return;
        }
        if (value instanceof Short) {
            setProperty(id, ((Short) value).intValue());
        } else if (value instanceof Boolean) {
            setProperty(id, ((Boolean) value).booleanValue());
        } else {
            if (value instanceof java.util.Date) {
                setProperty(id, 64L, value);
                return;
            }
            throw new HPSFRuntimeException("HPSF does not support properties of type " + value.getClass().getName() + ".");
        }
    }

    protected int getPropertyIntValue(long id) {
        Object o = getProperty(id);
        if (o == null) {
            return 0;
        }
        if (!(o instanceof Long) && !(o instanceof Integer)) {
            throw new HPSFRuntimeException("This property is not an integer type, but " + o.getClass().getName() + ".");
        }
        Number i = (Number) o;
        return i.intValue();
    }

    protected boolean getPropertyBooleanValue(int id) {
        Boolean b = (Boolean) getProperty(id);
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }

    protected void setPropertyBooleanValue(int id, boolean value) {
        setProperty(id, 11L, Boolean.valueOf(value));
    }

    public int getSize() {
        int size = this.sectionBytes.size();
        if (size > 0) {
            return size;
        }
        try {
            return calcSize();
        } catch (HPSFRuntimeException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HPSFRuntimeException(ex2);
        }
    }

    private int calcSize() throws IOException, WritingNotSupportedException {
        this.sectionBytes.reset();
        write(this.sectionBytes);
        padSectionBytes();
        return this.sectionBytes.size();
    }

    private void padSectionBytes() {
        byte[] padArray = {0, 0, 0};
        int pad = 3 & (4 - (this.sectionBytes.size() & 3));
        this.sectionBytes.write(padArray, 0, pad);
    }

    public boolean wasNull() {
        return this.wasNull;
    }

    public String getPIDString(long pid) {
        String s = null;
        Map<Long, String> dic = getDictionary();
        if (dic != null) {
            String s2 = dic.get(Long.valueOf(pid));
            s = s2;
        }
        if (s == null) {
            String s3 = SectionIDMap.getPIDString(getFormatID(), pid);
            return s3;
        }
        return s;
    }

    public void clear() {
        Property[] arr$ = getProperties();
        for (Property p : arr$) {
            removeProperty(p.getID());
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Section)) {
            return false;
        }
        Section s = (Section) o;
        if (!s.getFormatID().equals(getFormatID())) {
            return false;
        }
        Set<Long> propIds = new HashSet<>(this.properties.keySet());
        propIds.addAll(s.properties.keySet());
        propIds.remove(0L);
        propIds.remove(1L);
        for (Long id : propIds) {
            Property p1 = this.properties.get(id);
            Property p2 = s.properties.get(id);
            if (p1 == null || p2 == null || !p1.equals(p2)) {
                return false;
            }
        }
        Map<Long, String> d1 = getDictionary();
        Map<Long, String> d2 = s.getDictionary();
        return (d1 == null && d2 == null) || !(d1 == null || d2 == null || !d1.equals(d2));
    }

    public void removeProperty(long id) {
        if (this.properties.remove(Long.valueOf(id)) != null) {
            this.sectionBytes.reset();
        }
    }

    public int write(OutputStream out) throws IOException, WritingNotSupportedException {
        if (this.sectionBytes.size() > 0) {
            this.sectionBytes.writeTo(out);
            return this.sectionBytes.size();
        }
        int codepage = getCodepage();
        if (codepage == -1) {
            LOG.log(5, "The codepage property is not set although a dictionary is present. Defaulting to ISO-8859-1.");
            codepage = 1252;
        }
        ByteArrayOutputStream propertyStream = new ByteArrayOutputStream();
        ByteArrayOutputStream propertyListStream = new ByteArrayOutputStream();
        int position = 0 + (getPropertyCount() * 2 * 4) + 8;
        for (Property p : this.properties.values()) {
            long id = p.getID();
            LittleEndian.putUInt(id, propertyListStream);
            LittleEndian.putUInt(position, propertyListStream);
            if (id != 0) {
                position += p.write(propertyStream, codepage);
            } else {
                if (codepage == -1) {
                    throw new IllegalPropertySetDataException("Codepage (property 1) is undefined.");
                }
                position += writeDictionary(propertyStream, codepage);
            }
        }
        int streamLength = propertyListStream.size() + 8 + propertyStream.size();
        LittleEndian.putInt(streamLength, out);
        LittleEndian.putInt(getPropertyCount(), out);
        propertyListStream.writeTo(out);
        propertyStream.writeTo(out);
        return streamLength;
    }

    private boolean readDictionary(LittleEndianByteArrayInputStream leis, int length, int codepage) throws UnsupportedEncodingException {
        byte[] buf;
        int pad;
        LittleEndianByteArrayInputStream littleEndianByteArrayInputStream = leis;
        Map<Long, String> dic = new HashMap<>();
        long nrEntries = leis.readUInt();
        boolean isCorrupted = false;
        long id = -1;
        int i = 0;
        while (true) {
            if (i >= nrEntries) {
                break;
            }
            String errMsg = "The property set's dictionary contains bogus data. All dictionary entries starting with the one with ID " + id + " will be ignored.";
            id = leis.readUInt();
            long sLength = leis.readUInt();
            int cp = codepage == -1 ? 1252 : codepage;
            long nrEntries2 = nrEntries;
            int nrBytes = (int) ((sLength - 1) * ((long) (cp == 1200 ? 2 : 1)));
            if (nrBytes > 16777215) {
                LOG.log(5, errMsg);
                isCorrupted = true;
                break;
            }
            try {
                buf = new byte[nrBytes];
            } catch (RuntimeException e) {
                ex = e;
            }
            try {
                leis.readFully(buf, 0, nrBytes);
                String str = CodePageUtil.getStringFromCodePage(buf, 0, nrBytes, cp);
                if (cp != 1200) {
                    pad = 1;
                } else {
                    int pad2 = ((4 - ((nrBytes + 2) & 3)) & 3) + 2;
                    pad = pad2;
                }
                int i2 = i;
                try {
                    leis.skip(pad);
                    dic.put(Long.valueOf(id), str);
                    i = i2 + 1;
                    littleEndianByteArrayInputStream = leis;
                    nrEntries = nrEntries2;
                } catch (RuntimeException e2) {
                    ex = e2;
                    LOG.log(5, errMsg, ex);
                    isCorrupted = true;
                    setDictionary(dic);
                    return !isCorrupted;
                }
            } catch (RuntimeException e3) {
                ex = e3;
                LOG.log(5, errMsg, ex);
                isCorrupted = true;
                setDictionary(dic);
                return !isCorrupted;
            }
        }
        setDictionary(dic);
        return !isCorrupted;
    }

    private int writeDictionary(OutputStream out, int codepage) throws IOException {
        byte[] padding = new byte[4];
        Map<Long, String> dic = getDictionary();
        LittleEndian.putUInt(dic.size(), out);
        int length = 4;
        for (Map.Entry<Long, String> ls : dic.entrySet()) {
            LittleEndian.putUInt(ls.getKey().longValue(), out);
            String value = ls.getValue() + "\u0000";
            LittleEndian.putUInt(value.length(), out);
            byte[] bytes = CodePageUtil.getBytesInCodePage(value, codepage);
            out.write(bytes);
            length = length + 4 + 4 + bytes.length;
            if (codepage == 1200) {
                int pad = (4 - (length & 3)) & 3;
                out.write(padding, 0, pad);
                length += pad;
            }
        }
        int pad2 = (4 - (length & 3)) & 3;
        out.write(padding, 0, pad2);
        return length + pad2;
    }

    public void setDictionary(Map<Long, String> dictionary) throws IllegalPropertySetDataException {
        if (dictionary != null) {
            if (this.dictionary == null) {
                this.dictionary = new TreeMap();
            }
            this.dictionary.putAll(dictionary);
            int cp = getCodepage();
            if (cp == -1) {
                setCodepage(1252);
            }
            setProperty(0, -1L, dictionary);
            return;
        }
        removeProperty(0L);
        this.dictionary = null;
    }

    public int hashCode() {
        long hashCode = 0 + ((long) getFormatID().hashCode());
        Property[] pa = getProperties();
        for (Property property : pa) {
            hashCode += (long) property.hashCode();
        }
        int returnHashCode = (int) (4294967295L & hashCode);
        return returnHashCode;
    }

    public String toString() {
        return toString(null);
    }

    public String toString(PropertyIDMap idMap) {
        StringBuffer b = new StringBuffer();
        Property[] pa = getProperties();
        b.append("\n\n\n");
        b.append(getClass().getName());
        b.append('[');
        b.append("formatID: ");
        b.append(getFormatID());
        b.append(", offset: ");
        b.append(getOffset());
        b.append(", propertyCount: ");
        b.append(getPropertyCount());
        b.append(", size: ");
        b.append(getSize());
        b.append(", properties: [\n");
        int codepage = getCodepage();
        if (codepage == -1) {
            codepage = 1252;
        }
        for (Property p : pa) {
            b.append(p.toString(codepage, idMap));
            b.append(",\n");
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }

    public Map<Long, String> getDictionary() {
        if (this.dictionary == null) {
            this.dictionary = (Map) getProperty(0L);
        }
        return this.dictionary;
    }

    public int getCodepage() {
        Integer codepage = (Integer) getProperty(1L);
        if (codepage == null) {
            return -1;
        }
        return codepage.intValue();
    }

    public void setCodepage(int codepage) {
        setProperty(1, 2L, Integer.valueOf(codepage));
    }
}
