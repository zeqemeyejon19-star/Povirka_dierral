package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class StandardRecord extends Record {
    protected abstract int getDataSize();

    protected abstract void serialize(LittleEndianOutput littleEndianOutput);

    @Override // org.apache.poi.hssf.record.RecordBase
    public final int getRecordSize() {
        return getDataSize() + 4;
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public final int serialize(int offset, byte[] data) {
        int dataSize = getDataSize();
        int recSize = dataSize + 4;
        LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize);
        out.writeShort(getSid());
        out.writeShort(dataSize);
        serialize(out);
        if (out.getWriteIndex() - offset != recSize) {
            throw new IllegalStateException("Error in serialization of (" + getClass().getName() + "): Incorrect number of bytes written - expected " + recSize + " but got " + (out.getWriteIndex() - offset));
        }
        return recSize;
    }
}
