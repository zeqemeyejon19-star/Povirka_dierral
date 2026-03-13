package org.apache.poi.hssf.record.pivottable;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class ViewFieldsRecord extends StandardRecord {
    private static final int BASE_SIZE = 10;
    private static final int STRING_NOT_PRESENT_LEN = 65535;
    public static final short sid = 177;
    private int _cItm;
    private int _cSub;
    private int _grbitSub;
    private String _name;
    private int _sxaxis;

    private static final class Axis {
        public static final int COLUMN = 2;
        public static final int DATA = 8;
        public static final int NO_AXIS = 0;
        public static final int PAGE = 4;
        public static final int ROW = 1;

        private Axis() {
        }
    }

    public ViewFieldsRecord(RecordInputStream in) {
        this._sxaxis = in.readShort();
        this._cSub = in.readShort();
        this._grbitSub = in.readShort();
        this._cItm = in.readShort();
        int cchName = in.readUShort();
        if (cchName != 65535) {
            int flag = in.readByte();
            if ((flag & 1) != 0) {
                this._name = in.readUnicodeLEString(cchName);
            } else {
                this._name = in.readCompressedUnicode(cchName);
            }
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected void serialize(LittleEndianOutput out) {
        out.writeShort(this._sxaxis);
        out.writeShort(this._cSub);
        out.writeShort(this._grbitSub);
        out.writeShort(this._cItm);
        String str = this._name;
        if (str != null) {
            StringUtil.writeUnicodeString(out, str);
        } else {
            out.writeShort(65535);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        String str = this._name;
        if (str == null) {
            return 10;
        }
        return (str.length() * (StringUtil.hasMultibyte(this._name) ? 2 : 1)) + 11;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 177;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXVD]\n");
        buffer.append("    .sxaxis    = ").append(HexDump.shortToHex(this._sxaxis)).append('\n');
        buffer.append("    .cSub      = ").append(HexDump.shortToHex(this._cSub)).append('\n');
        buffer.append("    .grbitSub  = ").append(HexDump.shortToHex(this._grbitSub)).append('\n');
        buffer.append("    .cItm      = ").append(HexDump.shortToHex(this._cItm)).append('\n');
        buffer.append("    .name      = ").append(this._name).append('\n');
        buffer.append("[/SXVD]\n");
        return buffer.toString();
    }
}
