package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class NumberRecord extends CellRecord implements Cloneable {
    public static final short sid = 515;
    private double field_4_value;

    public NumberRecord() {
    }

    public NumberRecord(RecordInputStream in) {
        super(in);
        this.field_4_value = in.readDouble();
    }

    public void setValue(double value) {
        this.field_4_value = value;
    }

    public double getValue() {
        return this.field_4_value;
    }

    @Override // org.apache.poi.hssf.record.CellRecord
    protected String getRecordName() {
        return "NUMBER";
    }

    @Override // org.apache.poi.hssf.record.CellRecord
    protected void appendValueText(StringBuilder sb) {
        sb.append("  .value= ").append(NumberToTextConverter.toText(this.field_4_value));
    }

    @Override // org.apache.poi.hssf.record.CellRecord
    protected void serializeValue(LittleEndianOutput out) {
        out.writeDouble(getValue());
    }

    @Override // org.apache.poi.hssf.record.CellRecord
    protected int getValueDataSize() {
        return 8;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 515;
    }

    @Override // org.apache.poi.hssf.record.Record
    public NumberRecord clone() {
        NumberRecord rec = new NumberRecord();
        copyBaseFields(rec);
        rec.field_4_value = this.field_4_value;
        return rec;
    }
}
