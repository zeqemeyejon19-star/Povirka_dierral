package org.apache.poi.ddf;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public abstract class EscherRecord implements Cloneable {
    private static final BitField fInstance = BitFieldFactory.getInstance(65520);
    private static final BitField fVersion = BitFieldFactory.getInstance(15);
    private short _options;
    private short _recordId;

    public abstract int fillFields(byte[] bArr, int i, EscherRecordFactory escherRecordFactory);

    @Internal
    protected abstract Object[][] getAttributeMap();

    public abstract String getRecordName();

    public abstract int getRecordSize();

    public abstract int serialize(int i, byte[] bArr, EscherSerializationListener escherSerializationListener);

    protected int fillFields(byte[] data, EscherRecordFactory f) {
        return fillFields(data, 0, f);
    }

    protected int readHeader(byte[] data, int offset) {
        this._options = LittleEndian.getShort(data, offset);
        this._recordId = LittleEndian.getShort(data, offset + 2);
        return LittleEndian.getInt(data, offset + 4);
    }

    protected static short readInstance(byte[] data, int offset) {
        short options = LittleEndian.getShort(data, offset);
        return fInstance.getShortValue(options);
    }

    public boolean isContainerRecord() {
        return getVersion() == 15;
    }

    @Internal
    public short getOptions() {
        return this._options;
    }

    @Internal
    public void setOptions(short options) {
        setVersion(fVersion.getShortValue(options));
        setInstance(fInstance.getShortValue(options));
        this._options = options;
    }

    public byte[] serialize() {
        byte[] retval = new byte[getRecordSize()];
        serialize(0, retval);
        return retval;
    }

    public int serialize(int offset, byte[] data) {
        return serialize(offset, data, new NullEscherSerializationListener());
    }

    public short getRecordId() {
        return this._recordId;
    }

    public void setRecordId(short recordId) {
        this._recordId = recordId;
    }

    public List<EscherRecord> getChildRecords() {
        return Collections.emptyList();
    }

    public void setChildRecords(List<EscherRecord> childRecords) {
        throw new UnsupportedOperationException("This record does not support child records.");
    }

    @Override // 
    public EscherRecord clone() throws CloneNotSupportedException {
        return (EscherRecord) super.clone();
    }

    public EscherRecord getChild(int index) {
        return getChildRecords().get(index);
    }

    public void display(PrintWriter w, int indent) {
        for (int i = 0; i < indent * 4; i++) {
            w.print(' ');
        }
        w.println(getRecordName());
    }

    public short getInstance() {
        return fInstance.getShortValue(this._options);
    }

    public void setInstance(short value) {
        this._options = fInstance.setShortValue(this._options, value);
    }

    public short getVersion() {
        return fVersion.getShortValue(this._options);
    }

    public void setVersion(short value) {
        this._options = fVersion.setShortValue(this._options, value);
    }

    public String toXml() {
        return toXml("");
    }

    public final String toXml(String tab) {
        Object[][] arr$;
        int len$;
        String str;
        Object[] attrs;
        String nl = System.getProperty("line.separator");
        String clsNm = getClass().getSimpleName();
        StringBuilder sb = new StringBuilder(1000);
        String str2 = "<";
        sb.append(tab).append("<").append(clsNm).append(" recordId=\"0x").append(HexDump.toHex(getRecordId())).append("\" version=\"0x").append(HexDump.toHex(getVersion())).append("\" instance=\"0x").append(HexDump.toHex(getInstance())).append("\" options=\"0x").append(HexDump.toHex(getOptions())).append("\" recordSize=\"").append(getRecordSize());
        Object[][] attrList = getAttributeMap();
        if (attrList == null || attrList.length == 0) {
            sb.append("\" />").append(nl);
            return sb.toString();
        }
        sb.append("\">").append(nl);
        String childTab = tab + "   ";
        Object[][] arr$2 = attrList;
        int len$2 = arr$2.length;
        int i$ = 0;
        while (i$ < len$2) {
            Object[] attrs2 = arr$2[i$];
            String tagName = capitalizeAndTrim((String) attrs2[0]);
            boolean hasValue = false;
            boolean lastChildComplex = false;
            Object[][] attrList2 = attrList;
            int i = 0;
            while (true) {
                arr$ = arr$2;
                if (i >= attrs2.length) {
                    break;
                }
                Object value = attrs2[i + 1];
                if (value == null) {
                    str = str2;
                    len$ = len$2;
                    attrs = attrs2;
                } else {
                    if (hasValue) {
                        len$ = len$2;
                    } else {
                        len$ = len$2;
                        sb.append(childTab).append(str2).append(tagName).append(">");
                    }
                    int len$3 = i + 0;
                    String optName = capitalizeAndTrim((String) attrs2[len$3]);
                    if (i <= 0) {
                        str = str2;
                        attrs = attrs2;
                    } else {
                        str = str2;
                        attrs = attrs2;
                        sb.append(nl).append(childTab).append("  <").append(optName).append(">");
                    }
                    boolean lastChildComplex2 = appendValue(sb, value, true, childTab);
                    if (i > 0) {
                        sb.append(nl).append(childTab).append("  </").append(optName).append(">");
                    }
                    lastChildComplex = lastChildComplex2;
                    hasValue = true;
                }
                i += 2;
                arr$2 = arr$;
                len$2 = len$;
                str2 = str;
                attrs2 = attrs;
            }
            String str3 = str2;
            int len$4 = len$2;
            if (hasValue) {
                if (lastChildComplex) {
                    sb.append(nl).append(childTab);
                }
                sb.append("</").append(tagName).append(">").append(nl);
            }
            i$++;
            attrList = attrList2;
            arr$2 = arr$;
            len$2 = len$4;
            str2 = str3;
        }
        sb.append(tab).append("</").append(clsNm).append(">");
        return sb.toString();
    }

    public final String toString() {
        String nl = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(1000);
        sb.append(getClass().getName()).append(" (").append(getRecordName()).append("):").append(nl).append("  RecordId: 0x").append(HexDump.toHex(getRecordId())).append(nl).append("  Version: 0x").append(HexDump.toHex(getVersion())).append(nl).append("  Instance: 0x").append(HexDump.toHex(getInstance())).append(nl).append("  Options: 0x").append(HexDump.toHex(getOptions())).append(nl).append("  Record Size: ").append(getRecordSize());
        Object[][] attrList = getAttributeMap();
        if (attrList != null && attrList.length > 0) {
            for (Object[] attrs : attrList) {
                for (int i = 0; i < attrs.length; i += 2) {
                    Object value = attrs[i + 1];
                    if (value != null) {
                        String name = (String) attrs[i + 0];
                        sb.append(nl).append("  ").append(name).append(": ");
                        appendValue(sb, value, false, "  ");
                    }
                }
            }
        }
        String childTab = sb.toString();
        return childTab;
    }

    private static boolean appendValue(StringBuilder sb, Object value, boolean toXML, String childTab) {
        String nl = System.getProperty("line.separator");
        if (value instanceof String) {
            if (toXML) {
                escapeXML((String) value, sb);
                return false;
            }
            sb.append((String) value);
            return false;
        }
        if (value instanceof Byte) {
            sb.append("0x").append(HexDump.toHex(((Byte) value).byteValue()));
            return false;
        }
        if (value instanceof Short) {
            sb.append("0x").append(HexDump.toHex(((Short) value).shortValue()));
            return false;
        }
        if (value instanceof Integer) {
            sb.append("0x").append(HexDump.toHex(((Integer) value).intValue()));
            return false;
        }
        if (value instanceof byte[]) {
            sb.append(nl).append(HexDump.toHex((byte[]) value, 32).replaceAll("(?m)^", childTab + "   "));
            return false;
        }
        if (value instanceof Boolean) {
            sb.append(((Boolean) value).booleanValue());
            return false;
        }
        if (value instanceof EscherRecord) {
            EscherRecord er = (EscherRecord) value;
            if (toXML) {
                sb.append(nl).append(er.toXml(childTab + "    "));
            } else {
                sb.append(er.toString().replaceAll("(?m)^", childTab));
            }
            return true;
        }
        if (value instanceof EscherProperty) {
            EscherProperty ep = (EscherProperty) value;
            if (toXML) {
                sb.append(nl).append(ep.toXml(childTab + "  "));
            } else {
                sb.append(ep.toString().replaceAll("(?m)^", childTab));
            }
            return true;
        }
        throw new IllegalArgumentException("unknown attribute type " + value.getClass().getSimpleName());
    }

    private static String capitalizeAndTrim(String str) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        boolean capitalizeNext = true;
        char[] arr$ = str.toCharArray();
        for (char ch : arr$) {
            if (!Character.isLetterOrDigit(ch)) {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    if (!Character.isLetter(ch)) {
                        sb.append('_');
                    } else {
                        ch = Character.toTitleCase(ch);
                    }
                    capitalizeNext = false;
                }
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static void escapeXML(String s, StringBuilder out) {
        if (s == null || s.isEmpty()) {
            return;
        }
        char[] arr$ = s.toCharArray();
        for (char c : arr$) {
            if (c > 127 || c == '\"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
    }
}
