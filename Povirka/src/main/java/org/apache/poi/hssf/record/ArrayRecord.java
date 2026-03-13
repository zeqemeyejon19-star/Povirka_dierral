package org.apache.poi.hssf.record;

import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class ArrayRecord extends SharedValueRecordBase implements Cloneable {
    private static final int OPT_ALWAYS_RECALCULATE = 1;
    private static final int OPT_CALCULATE_ON_OPEN = 2;
    public static final short sid = 545;
    private int _field3notUsed;
    private Formula _formula;
    private int _options;

    public ArrayRecord(RecordInputStream in) {
        super(in);
        this._options = in.readUShort();
        this._field3notUsed = in.readInt();
        int formulaTokenLen = in.readUShort();
        int totalFormulaLen = in.available();
        this._formula = Formula.read(formulaTokenLen, in, totalFormulaLen);
    }

    public ArrayRecord(Formula formula, CellRangeAddress8Bit range) {
        super(range);
        this._options = 0;
        this._field3notUsed = 0;
        this._formula = formula;
    }

    public boolean isAlwaysRecalculate() {
        return (this._options & 1) != 0;
    }

    public boolean isCalculateOnOpen() {
        return (this._options & 2) != 0;
    }

    public Ptg[] getFormulaTokens() {
        return this._formula.getTokens();
    }

    @Override // org.apache.poi.hssf.record.SharedValueRecordBase
    protected int getExtraDataSize() {
        return this._formula.getEncodedSize() + 6;
    }

    @Override // org.apache.poi.hssf.record.SharedValueRecordBase
    protected void serializeExtraData(LittleEndianOutput out) {
        out.writeShort(this._options);
        out.writeInt(this._field3notUsed);
        this._formula.serialize(out);
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append(" [ARRAY]\n");
        sb.append(" range=").append(getRange()).append("\n");
        sb.append(" options=").append(HexDump.shortToHex(this._options)).append("\n");
        sb.append(" notUsed=").append(HexDump.intToHex(this._field3notUsed)).append("\n");
        sb.append(" formula:").append("\n");
        Ptg[] ptgs = this._formula.getTokens();
        for (Ptg ptg : ptgs) {
            sb.append(ptg).append(ptg.getRVAType()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public ArrayRecord clone() {
        ArrayRecord rec = new ArrayRecord(this._formula.copy(), getRange());
        rec._options = this._options;
        rec._field3notUsed = this._field3notUsed;
        return rec;
    }
}
