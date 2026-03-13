package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public final class SubtractPtg extends ValueOperatorPtg {
    public static final ValueOperatorPtg instance = new SubtractPtg();
    public static final byte sid = 4;

    private SubtractPtg() {
    }

    @Override // org.apache.poi.ss.formula.ptg.ValueOperatorPtg
    protected byte getSid() {
        return (byte) 4;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public int getNumberOfOperands() {
        return 2;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("-");
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
