package org.apache.poi.hssf.record.cont;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContinuableRecord extends Record {
    protected abstract void serialize(ContinuableRecordOutput continuableRecordOutput);

    protected ContinuableRecord() {
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public final int getRecordSize() {
        ContinuableRecordOutput out = ContinuableRecordOutput.createForCountingOnly();
        serialize(out);
        out.terminate();
        return out.getTotalSize();
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public final int serialize(int offset, byte[] data) {
        LittleEndianOutput leo = new LittleEndianByteArrayOutputStream(data, offset);
        ContinuableRecordOutput out = new ContinuableRecordOutput(leo, getSid());
        serialize(out);
        out.terminate();
        return out.getTotalSize();
    }
}
