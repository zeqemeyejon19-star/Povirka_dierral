package org.apache.poi.hssf.record.chart;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class CatLabRecord extends StandardRecord {
    public static final short sid = 2134;
    private short at;
    private short grbit;
    private short grbitFrt;
    private short rt;
    private Short unused;
    private short wOffset;

    public CatLabRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.wOffset = in.readShort();
        this.at = in.readShort();
        this.grbit = in.readShort();
        if (in.available() == 0) {
            this.unused = null;
        } else {
            this.unused = Short.valueOf(in.readShort());
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (this.unused == null ? 0 : 2) + 10;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeShort(this.wOffset);
        out.writeShort(this.at);
        out.writeShort(this.grbit);
        Short sh = this.unused;
        if (sh != null) {
            out.writeShort(sh.shortValue());
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CATLAB]\n");
        buffer.append("    .rt      =").append(HexDump.shortToHex(this.rt)).append('\n');
        buffer.append("    .grbitFrt=").append(HexDump.shortToHex(this.grbitFrt)).append('\n');
        buffer.append("    .wOffset =").append(HexDump.shortToHex(this.wOffset)).append('\n');
        buffer.append("    .at      =").append(HexDump.shortToHex(this.at)).append('\n');
        buffer.append("    .grbit   =").append(HexDump.shortToHex(this.grbit)).append('\n');
        if (this.unused != null) {
            buffer.append("    .unused  =").append(HexDump.shortToHex(this.unused.shortValue())).append('\n');
        }
        buffer.append("[/CATLAB]\n");
        return buffer.toString();
    }
}
