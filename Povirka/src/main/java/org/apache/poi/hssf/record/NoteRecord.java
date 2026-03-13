package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class NoteRecord extends StandardRecord implements Cloneable {
    public static final short NOTE_HIDDEN = 0;
    public static final short NOTE_VISIBLE = 2;
    public static final short sid = 28;
    private int field_1_row;
    private int field_2_col;
    private short field_3_flags;
    private int field_4_shapeid;
    private boolean field_5_hasMultibyte;
    private String field_6_author;
    private Byte field_7_padding;
    public static final NoteRecord[] EMPTY_ARRAY = new NoteRecord[0];
    private static final Byte DEFAULT_PADDING = (byte) 0;

    public NoteRecord() {
        this.field_6_author = "";
        this.field_3_flags = (short) 0;
        this.field_7_padding = DEFAULT_PADDING;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 28;
    }

    public NoteRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readShort();
        this.field_3_flags = in.readShort();
        this.field_4_shapeid = in.readUShort();
        int length = in.readShort();
        boolean z = in.readByte() != 0;
        this.field_5_hasMultibyte = z;
        if (z) {
            this.field_6_author = StringUtil.readUnicodeLE(in, length);
        } else {
            this.field_6_author = StringUtil.readCompressedUnicode(in, length);
        }
        if (in.available() == 1) {
            this.field_7_padding = Byte.valueOf(in.readByte());
        } else if (in.available() == 2 && length == 0) {
            this.field_7_padding = Byte.valueOf(in.readByte());
            in.readByte();
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        littleEndianOutput.writeShort(this.field_1_row);
        littleEndianOutput.writeShort(this.field_2_col);
        littleEndianOutput.writeShort(this.field_3_flags);
        littleEndianOutput.writeShort(this.field_4_shapeid);
        littleEndianOutput.writeShort(this.field_6_author.length());
        littleEndianOutput.writeByte(this.field_5_hasMultibyte ? 1 : 0);
        if (this.field_5_hasMultibyte) {
            StringUtil.putUnicodeLE(this.field_6_author, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(this.field_6_author, littleEndianOutput);
        }
        Byte b = this.field_7_padding;
        if (b != null) {
            littleEndianOutput.writeByte(b.intValue());
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (this.field_6_author.length() * (this.field_5_hasMultibyte ? 2 : 1)) + 11 + (this.field_7_padding == null ? 0 : 1);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[NOTE]\n");
        buffer.append("    .row    = ").append(this.field_1_row).append("\n");
        buffer.append("    .col    = ").append(this.field_2_col).append("\n");
        buffer.append("    .flags  = ").append((int) this.field_3_flags).append("\n");
        buffer.append("    .shapeid= ").append(this.field_4_shapeid).append("\n");
        buffer.append("    .author = ").append(this.field_6_author).append("\n");
        buffer.append("[/NOTE]\n");
        return buffer.toString();
    }

    public int getRow() {
        return this.field_1_row;
    }

    public void setRow(int row) {
        this.field_1_row = row;
    }

    public int getColumn() {
        return this.field_2_col;
    }

    public void setColumn(int col) {
        this.field_2_col = col;
    }

    public short getFlags() {
        return this.field_3_flags;
    }

    public void setFlags(short flags) {
        this.field_3_flags = flags;
    }

    protected boolean authorIsMultibyte() {
        return this.field_5_hasMultibyte;
    }

    public int getShapeId() {
        return this.field_4_shapeid;
    }

    public void setShapeId(int id) {
        this.field_4_shapeid = id;
    }

    public String getAuthor() {
        return this.field_6_author;
    }

    public void setAuthor(String author) {
        this.field_6_author = author;
        this.field_5_hasMultibyte = StringUtil.hasMultibyte(author);
    }

    @Override // org.apache.poi.hssf.record.Record
    public NoteRecord clone() {
        NoteRecord rec = new NoteRecord();
        rec.field_1_row = this.field_1_row;
        rec.field_2_col = this.field_2_col;
        rec.field_3_flags = this.field_3_flags;
        rec.field_4_shapeid = this.field_4_shapeid;
        rec.field_6_author = this.field_6_author;
        return rec;
    }
}
