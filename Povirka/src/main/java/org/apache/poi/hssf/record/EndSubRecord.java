package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class EndSubRecord extends SubRecord implements Cloneable {
    private static final int ENCODED_SIZE = 0;
    public static final short sid = 0;

    public EndSubRecord() {
    }

    public EndSubRecord(LittleEndianInput in, int size) {
        if ((size & 255) != 0) {
            throw new RecordFormatException("Unexpected size (" + size + ")");
        }
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    public boolean isTerminating() {
        return true;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[ftEnd]\n");
        buffer.append("[/ftEnd]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeShort(0);
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    protected int getDataSize() {
        return 0;
    }

    public short getSid() {
        return (short) 0;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    /* JADX INFO: renamed from: clone */
    public EndSubRecord mo9clone() {
        EndSubRecord rec = new EndSubRecord();
        return rec;
    }
}
