package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class FormatRecord extends StandardRecord implements Cloneable {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) FormatRecord.class);
    public static final short sid = 1054;
    private final int field_1_index_code;
    private final boolean field_3_hasMultibyte;
    private final String field_4_formatstring;

    private FormatRecord(FormatRecord other) {
        this.field_1_index_code = other.field_1_index_code;
        this.field_3_hasMultibyte = other.field_3_hasMultibyte;
        this.field_4_formatstring = other.field_4_formatstring;
    }

    public FormatRecord(int indexCode, String fs) {
        this.field_1_index_code = indexCode;
        this.field_4_formatstring = fs;
        this.field_3_hasMultibyte = StringUtil.hasMultibyte(fs);
    }

    public FormatRecord(RecordInputStream in) {
        this.field_1_index_code = in.readShort();
        int field_3_unicode_len = in.readUShort();
        boolean z = (in.readByte() & 1) != 0;
        this.field_3_hasMultibyte = z;
        if (z) {
            this.field_4_formatstring = readStringCommon(in, field_3_unicode_len, false);
        } else {
            this.field_4_formatstring = readStringCommon(in, field_3_unicode_len, true);
        }
    }

    public int getIndexCode() {
        return this.field_1_index_code;
    }

    public String getFormatString() {
        return this.field_4_formatstring;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FORMAT]\n");
        buffer.append("    .indexcode       = ").append(HexDump.shortToHex(getIndexCode())).append("\n");
        buffer.append("    .isUnicode       = ").append(this.field_3_hasMultibyte).append("\n");
        buffer.append("    .formatstring    = ").append(getFormatString()).append("\n");
        buffer.append("[/FORMAT]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        String formatString = getFormatString();
        littleEndianOutput.writeShort(getIndexCode());
        littleEndianOutput.writeShort(formatString.length());
        littleEndianOutput.writeByte(this.field_3_hasMultibyte ? 1 : 0);
        if (this.field_3_hasMultibyte) {
            StringUtil.putUnicodeLE(formatString, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(formatString, littleEndianOutput);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (getFormatString().length() * (this.field_3_hasMultibyte ? 2 : 1)) + 5;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public FormatRecord clone() {
        return new FormatRecord(this);
    }

    private static String readStringCommon(RecordInputStream ris, int requestedLength, boolean pIsCompressedEncoding) {
        char[] buf;
        int uByte;
        if (requestedLength < 0 || requestedLength > 1048576) {
            throw new IllegalArgumentException("Bad requested string length (" + requestedLength + ")");
        }
        int availableChars = ris.remaining();
        if (!pIsCompressedEncoding) {
            availableChars /= 2;
        }
        ris.remaining();
        if (requestedLength == availableChars) {
            buf = new char[requestedLength];
        } else {
            buf = new char[availableChars];
        }
        for (int i = 0; i < buf.length; i++) {
            if (pIsCompressedEncoding) {
                uByte = ris.readUByte();
            } else {
                uByte = ris.readShort();
            }
            char ch = (char) uByte;
            buf[i] = ch;
        }
        int i2 = ris.available();
        if (i2 == 1) {
            char[] tmp = new char[buf.length + 1];
            System.arraycopy(buf, 0, tmp, 0, buf.length);
            tmp[buf.length] = (char) ris.readUByte();
            buf = tmp;
        }
        if (ris.available() > 0) {
            logger.log(3, "FormatRecord has " + ris.available() + " unexplained bytes. Silently skipping");
            while (ris.available() > 0) {
                ris.readByte();
            }
        }
        return new String(buf);
    }
}
