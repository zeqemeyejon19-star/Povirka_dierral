package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class TabIdRecord extends StandardRecord {
    private static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final short sid = 317;
    public short[] _tabids;

    public TabIdRecord() {
        this._tabids = EMPTY_SHORT_ARRAY;
    }

    public TabIdRecord(RecordInputStream in) {
        int nTabs = in.remaining() / 2;
        this._tabids = new short[nTabs];
        int i = 0;
        while (true) {
            short[] sArr = this._tabids;
            if (i < sArr.length) {
                sArr[i] = in.readShort();
                i++;
            } else {
                return;
            }
        }
    }

    public void setTabIdArray(short[] array) {
        this._tabids = (short[]) array.clone();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[TABID]\n");
        buffer.append("    .elements        = ").append(this._tabids.length).append("\n");
        for (int i = 0; i < this._tabids.length; i++) {
            buffer.append("    .element_").append(i).append(" = ").append((int) this._tabids[i]).append("\n");
        }
        buffer.append("[/TABID]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        short[] tabids = this._tabids;
        for (short s : tabids) {
            out.writeShort(s);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return this._tabids.length * 2;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 317;
    }
}
