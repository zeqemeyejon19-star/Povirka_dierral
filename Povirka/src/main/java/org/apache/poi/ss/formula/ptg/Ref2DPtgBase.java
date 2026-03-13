package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
abstract class Ref2DPtgBase extends RefPtgBase {
    private static final int SIZE = 5;

    protected abstract byte getSid();

    protected Ref2DPtgBase(int row, int column, boolean isRowRelative, boolean isColumnRelative) {
        setRow(row);
        setColumn(column);
        setRowRelative(isRowRelative);
        setColRelative(isColumnRelative);
    }

    protected Ref2DPtgBase(LittleEndianInput in) {
        readCoordinates(in);
    }

    protected Ref2DPtgBase(CellReference cr) {
        super(cr);
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public void write(LittleEndianOutput out) {
        out.writeByte(getSid() + getPtgClass());
        writeCoordinates(out);
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final String toFormulaString() {
        return formatReferenceAsString();
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final int getSize() {
        return 5;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
}
