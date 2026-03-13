package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class NameCommentRecord extends StandardRecord {
    public static final short sid = 2196;
    private final short field_1_record_type;
    private final short field_2_frt_cell_ref_flag;
    private final long field_3_reserved;
    private String field_6_name_text;
    private String field_7_comment_text;

    public NameCommentRecord(String name, String comment) {
        this.field_1_record_type = (short) 0;
        this.field_2_frt_cell_ref_flag = (short) 0;
        this.field_3_reserved = 0L;
        this.field_6_name_text = name;
        this.field_7_comment_text = comment;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        int length = this.field_6_name_text.length();
        int length2 = this.field_7_comment_text.length();
        littleEndianOutput.writeShort(this.field_1_record_type);
        littleEndianOutput.writeShort(this.field_2_frt_cell_ref_flag);
        littleEndianOutput.writeLong(this.field_3_reserved);
        littleEndianOutput.writeShort(length);
        littleEndianOutput.writeShort(length2);
        boolean zHasMultibyte = StringUtil.hasMultibyte(this.field_6_name_text);
        littleEndianOutput.writeByte(zHasMultibyte ? 1 : 0);
        if (zHasMultibyte) {
            StringUtil.putUnicodeLE(this.field_6_name_text, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(this.field_6_name_text, littleEndianOutput);
        }
        boolean zHasMultibyte2 = StringUtil.hasMultibyte(this.field_7_comment_text);
        littleEndianOutput.writeByte(zHasMultibyte2 ? 1 : 0);
        if (zHasMultibyte2) {
            StringUtil.putUnicodeLE(this.field_7_comment_text, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(this.field_7_comment_text, littleEndianOutput);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (StringUtil.hasMultibyte(this.field_6_name_text) ? this.field_6_name_text.length() * 2 : this.field_6_name_text.length()) + 18 + (StringUtil.hasMultibyte(this.field_7_comment_text) ? this.field_7_comment_text.length() * 2 : this.field_7_comment_text.length());
    }

    public NameCommentRecord(RecordInputStream ris) {
        this.field_1_record_type = ris.readShort();
        this.field_2_frt_cell_ref_flag = ris.readShort();
        this.field_3_reserved = ris.readLong();
        int field_4_name_length = ris.readShort();
        int field_5_comment_length = ris.readShort();
        if (ris.readByte() == 0) {
            this.field_6_name_text = StringUtil.readCompressedUnicode(ris, field_4_name_length);
        } else {
            this.field_6_name_text = StringUtil.readUnicodeLE(ris, field_4_name_length);
        }
        if (ris.readByte() == 0) {
            this.field_7_comment_text = StringUtil.readCompressedUnicode(ris, field_5_comment_length);
        } else {
            this.field_7_comment_text = StringUtil.readUnicodeLE(ris, field_5_comment_length);
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[NAMECMT]\n");
        sb.append("    .record type            = ").append(HexDump.shortToHex(this.field_1_record_type)).append("\n");
        sb.append("    .frt cell ref flag      = ").append(HexDump.byteToHex(this.field_2_frt_cell_ref_flag)).append("\n");
        sb.append("    .reserved               = ").append(this.field_3_reserved).append("\n");
        sb.append("    .name length            = ").append(this.field_6_name_text.length()).append("\n");
        sb.append("    .comment length         = ").append(this.field_7_comment_text.length()).append("\n");
        sb.append("    .name                   = ").append(this.field_6_name_text).append("\n");
        sb.append("    .comment                = ").append(this.field_7_comment_text).append("\n");
        sb.append("[/NAMECMT]\n");
        return sb.toString();
    }

    public String getNameText() {
        return this.field_6_name_text;
    }

    public void setNameText(String newName) {
        this.field_6_name_text = newName;
    }

    public String getCommentText() {
        return this.field_7_comment_text;
    }

    public void setCommentText(String comment) {
        this.field_7_comment_text = comment;
    }

    public short getRecordType() {
        return this.field_1_record_type;
    }
}
