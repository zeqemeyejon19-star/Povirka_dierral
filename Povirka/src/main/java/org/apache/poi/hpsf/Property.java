package org.apache.poi.hpsf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.DatatypeConverter;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class Property {
    public static final int DEFAULT_CODEPAGE = 1252;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) Property.class);
    private long id;
    private long type;
    private Object value;

    public Property() {
    }

    public Property(Property p) {
        this(p.id, p.type, p.value);
    }

    public Property(long id, long type, Object value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public Property(long id, byte[] src, long offset, int length, int codepage) throws UnsupportedEncodingException {
        this.id = id;
        if (id == 0) {
            throw new UnsupportedEncodingException("Dictionary not allowed here");
        }
        int o = (int) offset;
        this.type = LittleEndian.getUInt(src, o);
        try {
            this.value = VariantSupport.read(src, o + 4, length, (int) r6, codepage);
        } catch (UnsupportedVariantTypeException ex) {
            VariantSupport.writeUnsupportedTypeMessage(ex);
            this.value = ex.getValue();
        }
    }

    public Property(long id, LittleEndianByteArrayInputStream leis, int length, int codepage) throws UnsupportedEncodingException {
        this.id = id;
        if (id == 0) {
            throw new UnsupportedEncodingException("Dictionary not allowed here");
        }
        this.type = leis.readUInt();
        try {
            this.value = VariantSupport.read(leis, length, (int) r0, codepage);
        } catch (UnsupportedVariantTypeException ex) {
            VariantSupport.writeUnsupportedTypeMessage(ex);
            this.value = ex.getValue();
        }
    }

    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    protected int getSize(int codepage) throws WritingNotSupportedException {
        int length = Variant.getVariantLength(this.type);
        if (length < 0) {
            long j = this.type;
            if (j != 0) {
                if (length == -2) {
                    throw new WritingNotSupportedException(this.type, null);
                }
                if (j == 30 || j == 31) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        int length2 = write(bos, codepage) - 8;
                        return length2 + ((4 - (length2 & 3)) & 3);
                    } catch (IOException e) {
                        throw new WritingNotSupportedException(this.type, this.value);
                    }
                }
                throw new WritingNotSupportedException(this.type, this.value);
            }
        }
        return length;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Property)) {
            return false;
        }
        Property p = (Property) o;
        Object pValue = p.getValue();
        long pId = p.getID();
        long j = this.id;
        if (j != pId || (j != 0 && !typesAreEqual(this.type, p.getType()))) {
            return false;
        }
        Object obj = this.value;
        if (obj == null && pValue == null) {
            return true;
        }
        if (obj == null || pValue == null) {
            return false;
        }
        Class<?> valueClass = obj.getClass();
        Class<?> pValueClass = pValue.getClass();
        if (!valueClass.isAssignableFrom(pValueClass) && !pValueClass.isAssignableFrom(valueClass)) {
            return false;
        }
        Object obj2 = this.value;
        if (obj2 instanceof byte[]) {
            byte[] thisVal = (byte[]) obj2;
            byte[] otherVal = (byte[]) pValue;
            int len = unpaddedLength(thisVal);
            if (len != unpaddedLength(otherVal)) {
                return false;
            }
            for (int i = 0; i < len; i++) {
                if (thisVal[i] != otherVal[i]) {
                    return false;
                }
            }
            return true;
        }
        return obj2.equals(pValue);
    }

    private static int unpaddedLength(byte[] buf) {
        int len = buf.length;
        while (len > 0 && len > buf.length - 4 && buf[len - 1] == 0) {
            len--;
        }
        return len;
    }

    private boolean typesAreEqual(long t1, long t2) {
        return t1 == t2 || (t1 == 30 && t2 == 31) || (t2 == 30 && t1 == 31);
    }

    public int hashCode() {
        long hashCode = 0 + this.id + this.type;
        Object obj = this.value;
        if (obj != null) {
            hashCode += (long) obj.hashCode();
        }
        return (int) (4294967295L & hashCode);
    }

    public String toString() {
        return toString(1252, null);
    }

    public String toString(int codepage, PropertyIDMap idMap) {
        String idName;
        StringBuilder b = new StringBuilder();
        b.append("Property[");
        b.append("id: ");
        b.append(this.id);
        String idName2 = idMap == null ? null : idMap.get((Object) Long.valueOf(this.id));
        if (idName2 == null) {
            idName = PropertyIDMap.getFallbackProperties().get((Object) Long.valueOf(this.id));
        } else {
            idName = idName2;
        }
        if (idName != null) {
            b.append(" (");
            b.append(idName);
            b.append(")");
        }
        b.append(", type: ");
        b.append(getType());
        b.append(" (");
        b.append(getVariantName());
        b.append(") ");
        Object value = getValue();
        b.append(", value: ");
        if (value instanceof String) {
            b.append((String) value);
            b.append("\n");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                write(bos, codepage);
            } catch (Exception e) {
                LOG.log(5, "can't serialize string", e);
            }
            if (bos.size() > 8) {
                String hex = HexDump.dump(bos.toByteArray(), -8L, 8);
                b.append(hex);
            }
        } else if (value instanceof byte[]) {
            b.append("\n");
            byte[] bytes = (byte[]) value;
            if (bytes.length > 0) {
                String hex2 = HexDump.dump(bytes, 0L, 0);
                b.append(hex2);
            }
        } else if (!(value instanceof java.util.Date)) {
            long j = this.type;
            if (j == 0 || j == 1 || value == null) {
                b.append("null");
            } else {
                b.append(value.toString());
                String decoded = decodeValueFromID();
                if (decoded != null) {
                    b.append(" (");
                    b.append(decoded);
                    b.append(")");
                }
            }
        } else {
            java.util.Date d = (java.util.Date) value;
            long filetime = Filetime.dateToFileTime(d);
            if (Filetime.isUndefined(d)) {
                b.append("<undefined>");
            } else if ((filetime >>> 32) == 0) {
                long l = 100 * filetime;
                TimeUnit tu = TimeUnit.NANOSECONDS;
                long hr = tu.toHours(l);
                long l2 = l - TimeUnit.HOURS.toNanos(hr);
                long min = tu.toMinutes(l2);
                long l3 = l2 - TimeUnit.MINUTES.toNanos(min);
                long sec = tu.toSeconds(l3);
                long ms = tu.toMillis(l3 - TimeUnit.SECONDS.toNanos(sec));
                String str = String.format(Locale.ROOT, "%02d:%02d:%02d.%03d", Long.valueOf(hr), Long.valueOf(min), Long.valueOf(sec), Long.valueOf(ms));
                b.append(str);
            } else {
                Calendar cal = Calendar.getInstance(LocaleUtil.TIMEZONE_UTC, Locale.ROOT);
                cal.setTime(d);
                b.append(DatatypeConverter.printDateTime(cal));
            }
        }
        b.append(']');
        return b.toString();
    }

    private String getVariantName() {
        if (getID() == 0) {
            return "dictionary";
        }
        return Variant.getVariantName(getType());
    }

    private String decodeValueFromID() {
        try {
            int id = (int) getID();
            if (id != Integer.MIN_VALUE) {
                if (id == 1) {
                    return CodePageUtil.codepageToEncoding(((Number) this.value).intValue());
                }
                return null;
            }
            return LocaleUtil.getLocaleFromLCID(((Number) this.value).intValue());
        } catch (Exception e) {
            LOG.log(5, "Can't decode id " + getID());
            return null;
        }
    }

    public int write(OutputStream out, int codepage) throws IOException, WritingNotSupportedException {
        long variantType = getType();
        if (variantType == 30 && codepage != 1200) {
            String csStr = CodePageUtil.codepageToEncoding(codepage > 0 ? codepage : 1252);
            if (!Charset.forName(csStr).newEncoder().canEncode((String) this.value)) {
                variantType = 31;
            }
        }
        LittleEndian.putUInt(variantType, out);
        int length = 0 + 4;
        return length + VariantSupport.write(out, variantType, getValue(), codepage);
    }
}
