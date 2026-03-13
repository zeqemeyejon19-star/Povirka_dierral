package org.apache.poi.hssf.record;

import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class CRNCountRecord extends StandardRecord {
    private static final short DATA_SIZE = 4;
    public static final short sid = 89;
    private int field_1_number_crn_records;
    private int field_2_sheet_table_index;

    public CRNCountRecord() {
        throw new RuntimeException("incomplete code");
    }

    public int getNumberOfCRNs() {
        return this.field_1_number_crn_records;
    }

    public CRNCountRecord(RecordInputStream in) {
        short s = in.readShort();
        this.field_1_number_crn_records = s;
        if (s < 0) {
            this.field_1_number_crn_records = (short) (-s);
        }
        this.field_2_sheet_table_index = in.readShort();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append(" [XCT");
        sb.append(" nCRNs=").append(this.field_1_number_crn_records);
        sb.append(" sheetIx=").append(this.field_2_sheet_table_index);
        sb.append("]");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort((short) this.field_1_number_crn_records);
        out.writeShort((short) this.field_2_sheet_table_index);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return 4;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 89;
    }
}
