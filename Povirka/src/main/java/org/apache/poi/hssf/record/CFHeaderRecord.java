package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.CellRangeAddress;

/* JADX INFO: loaded from: classes.dex */
public final class CFHeaderRecord extends CFHeaderBase implements Cloneable {
    public static final short sid = 432;

    public CFHeaderRecord() {
        createEmpty();
    }

    public CFHeaderRecord(CellRangeAddress[] regions, int nRules) {
        super(regions, nRules);
    }

    public CFHeaderRecord(RecordInputStream in) {
        read(in);
    }

    @Override // org.apache.poi.hssf.record.CFHeaderBase
    protected String getRecordName() {
        return "CFHEADER";
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.CFHeaderBase, org.apache.poi.hssf.record.Record
    public CFHeaderRecord clone() {
        CFHeaderRecord result = new CFHeaderRecord();
        super.copyTo(result);
        return result;
    }
}
