package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public abstract class ScalarConstantPtg extends Ptg {
    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final boolean isBaseToken() {
        return true;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final byte getDefaultOperandClass() {
        return (byte) 32;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(toFormulaString());
        sb.append("]");
        return sb.toString();
    }
}
