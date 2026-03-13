package org.apache.poi.hssf.record;

import java.util.Iterator;
import org.apache.poi.hssf.record.PageBreakRecord;

/* JADX INFO: loaded from: classes.dex */
public final class HorizontalPageBreakRecord extends PageBreakRecord implements Cloneable {
    public static final short sid = 27;

    public HorizontalPageBreakRecord() {
    }

    public HorizontalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 27;
    }

    @Override // org.apache.poi.hssf.record.Record
    public PageBreakRecord clone() {
        PageBreakRecord result = new HorizontalPageBreakRecord();
        Iterator<PageBreakRecord.Break> iterator = getBreaksIterator();
        while (iterator.hasNext()) {
            PageBreakRecord.Break original = iterator.next();
            result.addBreak(original.main, original.subFrom, original.subTo);
        }
        return result;
    }
}
