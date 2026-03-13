package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class CellRecord extends StandardRecord implements CellValueRecordInterface {
    private int _columnIndex;
    private int _formatIndex;
    private int _rowIndex;

    protected abstract void appendValueText(StringBuilder sb);

    protected abstract String getRecordName();

    protected abstract int getValueDataSize();

    protected abstract void serializeValue(LittleEndianOutput littleEndianOutput);

    protected CellRecord() {
    }

    protected CellRecord(RecordInputStream in) {
        this._rowIndex = in.readUShort();
        this._columnIndex = in.readUShort();
        this._formatIndex = in.readUShort();
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final void setRow(int row) {
        this._rowIndex = row;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final void setColumn(short col) {
        this._columnIndex = col;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final void setXFIndex(short xf) {
        this._formatIndex = xf;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final int getRow() {
        return this._rowIndex;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final short getColumn() {
        return (short) this._columnIndex;
    }

    @Override // org.apache.poi.hssf.record.CellValueRecordInterface
    public final short getXFIndex() {
        return (short) this._formatIndex;
    }

    @Override // org.apache.poi.hssf.record.Record
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        String recordName = getRecordName();
        sb.append("[").append(recordName).append("]\n");
        sb.append("    .row    = ").append(HexDump.shortToHex(getRow())).append("\n");
        sb.append("    .col    = ").append(HexDump.shortToHex(getColumn())).append("\n");
        sb.append("    .xfindex= ").append(HexDump.shortToHex(getXFIndex())).append("\n");
        appendValueText(sb);
        sb.append("\n");
        sb.append("[/").append(recordName).append("]\n");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public final void serialize(LittleEndianOutput out) {
        out.writeShort(getRow());
        out.writeShort(getColumn());
        out.writeShort(getXFIndex());
        serializeValue(out);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected final int getDataSize() {
        return getValueDataSize() + 6;
    }

    protected final void copyBaseFields(CellRecord rec) {
        rec._rowIndex = this._rowIndex;
        rec._columnIndex = this._columnIndex;
        rec._formatIndex = this._formatIndex;
    }
}
