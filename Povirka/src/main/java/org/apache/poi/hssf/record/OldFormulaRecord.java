package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.CellType;

/* JADX INFO: loaded from: classes.dex */
public final class OldFormulaRecord extends OldCellRecord {
    public static final short biff2_sid = 6;
    public static final short biff3_sid = 518;
    public static final short biff4_sid = 1030;
    public static final short biff5_sid = 6;
    private double field_4_value;
    private short field_5_options;
    private Formula field_6_parsed_expr;
    private FormulaRecord.SpecialCachedValue specialCachedValue;

    public OldFormulaRecord(RecordInputStream ris) {
        super(ris, ris.getSid() == 6);
        if (isBiff2()) {
            this.field_4_value = ris.readDouble();
        } else {
            long valueLongBits = ris.readLong();
            FormulaRecord.SpecialCachedValue specialCachedValueCreate = FormulaRecord.SpecialCachedValue.create(valueLongBits);
            this.specialCachedValue = specialCachedValueCreate;
            if (specialCachedValueCreate == null) {
                this.field_4_value = Double.longBitsToDouble(valueLongBits);
            }
        }
        if (isBiff2()) {
            this.field_5_options = (short) ris.readUByte();
        } else {
            this.field_5_options = ris.readShort();
        }
        int expression_len = ris.readShort();
        int nBytesAvailable = ris.available();
        this.field_6_parsed_expr = Formula.read(expression_len, ris, nBytesAvailable);
    }

    public int getCachedResultType() {
        FormulaRecord.SpecialCachedValue specialCachedValue = this.specialCachedValue;
        if (specialCachedValue == null) {
            return CellType.NUMERIC.getCode();
        }
        return specialCachedValue.getValueType();
    }

    public boolean getCachedBooleanValue() {
        return this.specialCachedValue.getBooleanValue();
    }

    public int getCachedErrorValue() {
        return this.specialCachedValue.getErrorValue();
    }

    public double getValue() {
        return this.field_4_value;
    }

    public short getOptions() {
        return this.field_5_options;
    }

    public Ptg[] getParsedExpression() {
        return this.field_6_parsed_expr.getTokens();
    }

    public Formula getFormula() {
        return this.field_6_parsed_expr;
    }

    @Override // org.apache.poi.hssf.record.OldCellRecord
    protected void appendValueText(StringBuilder sb) {
        sb.append("    .value       = ").append(getValue()).append("\n");
    }

    @Override // org.apache.poi.hssf.record.OldCellRecord
    protected String getRecordName() {
        return "Old Formula";
    }
}
