package org.apache.poi.hssf.record;

import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class DBCellRecord extends StandardRecord implements Cloneable {
    public static final int BLOCK_SIZE = 32;
    public static final short sid = 215;
    private final int field_1_row_offset;
    private final short[] field_2_cell_offsets;

    public static final class Builder {
        private short[] _cellOffsets = new short[4];
        private int _nCellOffsets;

        public void addCellOffset(int cellRefOffset) {
            short[] sArr = this._cellOffsets;
            int length = sArr.length;
            int i = this._nCellOffsets;
            if (length <= i) {
                short[] temp = new short[i * 2];
                System.arraycopy(sArr, 0, temp, 0, i);
                this._cellOffsets = temp;
            }
            short[] sArr2 = this._cellOffsets;
            int i2 = this._nCellOffsets;
            sArr2[i2] = (short) cellRefOffset;
            this._nCellOffsets = i2 + 1;
        }

        public DBCellRecord build(int rowOffset) {
            int i = this._nCellOffsets;
            short[] cellOffsets = new short[i];
            System.arraycopy(this._cellOffsets, 0, cellOffsets, 0, i);
            return new DBCellRecord(rowOffset, cellOffsets);
        }
    }

    DBCellRecord(int rowOffset, short[] cellOffsets) {
        this.field_1_row_offset = rowOffset;
        this.field_2_cell_offsets = cellOffsets;
    }

    public DBCellRecord(RecordInputStream in) {
        this.field_1_row_offset = in.readUShort();
        int size = in.remaining();
        this.field_2_cell_offsets = new short[size / 2];
        int i = 0;
        while (true) {
            short[] sArr = this.field_2_cell_offsets;
            if (i < sArr.length) {
                sArr[i] = in.readShort();
                i++;
            } else {
                return;
            }
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[DBCELL]\n");
        buffer.append("    .rowoffset = ").append(HexDump.intToHex(this.field_1_row_offset)).append("\n");
        for (int k = 0; k < this.field_2_cell_offsets.length; k++) {
            buffer.append("    .cell_").append(k).append(" = ").append(HexDump.shortToHex(this.field_2_cell_offsets[k])).append("\n");
        }
        buffer.append("[/DBCELL]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_row_offset);
        int k = 0;
        while (true) {
            short[] sArr = this.field_2_cell_offsets;
            if (k < sArr.length) {
                out.writeShort(sArr[k]);
                k++;
            } else {
                return;
            }
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (this.field_2_cell_offsets.length * 2) + 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public DBCellRecord clone() {
        return this;
    }
}
