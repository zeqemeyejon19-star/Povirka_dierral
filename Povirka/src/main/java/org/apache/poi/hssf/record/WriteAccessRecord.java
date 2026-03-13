package org.apache.poi.hssf.record;

import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class WriteAccessRecord extends StandardRecord {
    private static final int DATA_SIZE = 112;
    private static final byte[] PADDING;
    private static final byte PAD_CHAR = 32;
    public static final short sid = 92;
    private String field_1_username;

    static {
        byte[] bArr = new byte[112];
        PADDING = bArr;
        Arrays.fill(bArr, (byte) 32);
    }

    public WriteAccessRecord() {
        setUsername("");
    }

    public WriteAccessRecord(RecordInputStream in) {
        String rawText;
        if (in.remaining() > 112) {
            throw new RecordFormatException("Expected data size (112) but got (" + in.remaining() + ")");
        }
        int nChars = in.readUShort();
        int is16BitFlag = in.readUByte();
        if (nChars > 112 || (is16BitFlag & 254) != 0) {
            byte[] data = new byte[in.remaining() + 3];
            LittleEndian.putUShort(data, 0, nChars);
            LittleEndian.putByte(data, 2, is16BitFlag);
            in.readFully(data, 3, data.length - 3);
            String rawValue = new String(data, StringUtil.UTF8);
            setUsername(rawValue.trim());
            return;
        }
        if ((is16BitFlag & 1) == 0) {
            rawText = StringUtil.readCompressedUnicode(in, nChars);
        } else {
            rawText = StringUtil.readUnicodeLE(in, nChars);
        }
        this.field_1_username = rawText.trim();
        for (int padSize = in.remaining(); padSize > 0; padSize--) {
            in.readUByte();
        }
    }

    public void setUsername(String username) {
        boolean is16bit = StringUtil.hasMultibyte(username);
        int encodedByteCount = (username.length() * (is16bit ? 2 : 1)) + 3;
        int paddingSize = 112 - encodedByteCount;
        if (paddingSize < 0) {
            throw new IllegalArgumentException("Name is too long: " + username);
        }
        this.field_1_username = username;
    }

    public String getUsername() {
        return this.field_1_username;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[WRITEACCESS]\n");
        buffer.append("    .name = ").append(this.field_1_username).append("\n");
        buffer.append("[/WRITEACCESS]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        String username = getUsername();
        boolean zHasMultibyte = StringUtil.hasMultibyte(username);
        littleEndianOutput.writeShort(username.length());
        littleEndianOutput.writeByte(zHasMultibyte ? 1 : 0);
        if (zHasMultibyte) {
            StringUtil.putUnicodeLE(username, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(username, littleEndianOutput);
        }
        littleEndianOutput.write(PADDING, 0, 112 - ((username.length() * (zHasMultibyte ? 2 : 1)) + 3));
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 112;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 92;
    }
}
