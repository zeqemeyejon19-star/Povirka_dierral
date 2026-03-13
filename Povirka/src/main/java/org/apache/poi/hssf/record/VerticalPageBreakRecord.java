package org.apache.poi.hssf.record;

import java.util.Iterator;
import org.apache.poi.hssf.record.PageBreakRecord;

/* JADX INFO: loaded from: classes.dex */
public final class VerticalPageBreakRecord extends PageBreakRecord {
    public static final short sid = 26;

    public VerticalPageBreakRecord() {
    }

    public VerticalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 26;
    }

    @Override // org.apache.poi.hssf.record.Record
    public Object clone() {
        PageBreakRecord result = new VerticalPageBreakRecord();
        Iterator<PageBreakRecord.Break> iterator = getBreaksIterator();
        while (iterator.hasNext()) {
            PageBreakRecord.Break original = iterator.next();
            result.addBreak(original.main, original.subFrom, original.subTo);
        }
        return result;
    }
}
