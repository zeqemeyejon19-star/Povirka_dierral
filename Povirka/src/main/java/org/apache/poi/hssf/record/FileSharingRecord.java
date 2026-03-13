package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class FileSharingRecord extends StandardRecord implements Cloneable {
    public static final short sid = 91;
    private short field_1_readonly;
    private short field_2_password;
    private byte field_3_username_unicode_options;
    private String field_3_username_value;

    public FileSharingRecord() {
    }

    public FileSharingRecord(RecordInputStream in) {
        this.field_1_readonly = in.readShort();
        this.field_2_password = in.readShort();
        int nameLen = in.readShort();
        if (nameLen > 0) {
            this.field_3_username_unicode_options = in.readByte();
            this.field_3_username_value = in.readCompressedUnicode(nameLen);
        } else {
            this.field_3_username_value = "";
        }
    }

    public void setReadOnly(short readonly) {
        this.field_1_readonly = readonly;
    }

    public short getReadOnly() {
        return this.field_1_readonly;
    }

    public void setPassword(short password) {
        this.field_2_password = password;
    }

    public short getPassword() {
        return this.field_2_password;
    }

    public String getUsername() {
        return this.field_3_username_value;
    }

    public void setUsername(String username) {
        this.field_3_username_value = username;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FILESHARING]\n");
        buffer.append("    .readonly       = ").append(getReadOnly() == 1 ? "true" : "false").append("\n");
        buffer.append("    .password       = ").append(Integer.toHexString(getPassword())).append("\n");
        buffer.append("    .username       = ").append(getUsername()).append("\n");
        buffer.append("[/FILESHARING]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(getReadOnly());
        out.writeShort(getPassword());
        out.writeShort(this.field_3_username_value.length());
        if (this.field_3_username_value.length() > 0) {
            out.writeByte(this.field_3_username_unicode_options);
            StringUtil.putCompressedUnicode(getUsername(), out);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        int nameLen = this.field_3_username_value.length();
        if (nameLen < 1) {
            return 6;
        }
        return nameLen + 7;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 91;
    }

    @Override // org.apache.poi.hssf.record.Record
    public FileSharingRecord clone() {
        FileSharingRecord clone = new FileSharingRecord();
        clone.setReadOnly(this.field_1_readonly);
        clone.setPassword(this.field_2_password);
        clone.setUsername(this.field_3_username_value);
        return clone;
    }
}
