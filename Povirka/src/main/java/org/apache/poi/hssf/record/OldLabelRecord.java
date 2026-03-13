package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class OldLabelRecord extends OldCellRecord {
    public static final short biff2_sid = 4;
    public static final short biff345_sid = 516;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) OldLabelRecord.class);
    private CodepageRecord codepage;
    private short field_4_string_len;
    private final byte[] field_5_bytes;

    public OldLabelRecord(RecordInputStream in) {
        super(in, in.getSid() == 4);
        if (isBiff2()) {
            this.field_4_string_len = (short) in.readUByte();
        } else {
            this.field_4_string_len = in.readShort();
        }
        int i = this.field_4_string_len;
        byte[] bArr = new byte[i];
        this.field_5_bytes = bArr;
        in.read(bArr, 0, i);
        if (in.remaining() > 0) {
            logger.log(3, "LabelRecord data remains: " + in.remaining() + " : " + HexDump.toHex(in.readRemainder()));
        }
    }

    public void setCodePage(CodepageRecord codepage) {
        this.codepage = codepage;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public String getValue() {
        return OldStringRecord.getString(this.field_5_bytes, this.codepage);
    }

    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Old Label Records are supported READ ONLY");
    }

    public int getRecordSize() {
        throw new RecordFormatException("Old Label Records are supported READ ONLY");
    }

    @Override // org.apache.poi.hssf.record.OldCellRecord
    protected void appendValueText(StringBuilder sb) {
        sb.append("    .string_len= ").append(HexDump.shortToHex(this.field_4_string_len)).append("\n");
        sb.append("    .value       = ").append(getValue()).append("\n");
    }

    @Override // org.apache.poi.hssf.record.OldCellRecord
    protected String getRecordName() {
        return "OLD LABEL";
    }
}
