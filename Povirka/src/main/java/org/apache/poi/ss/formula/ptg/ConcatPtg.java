package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public final class ConcatPtg extends ValueOperatorPtg {
    private static final String CONCAT = "&";
    public static final ValueOperatorPtg instance = new ConcatPtg();
    public static final byte sid = 8;

    private ConcatPtg() {
    }

    @Override // org.apache.poi.ss.formula.ptg.ValueOperatorPtg
    protected byte getSid() {
        return (byte) 8;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public int getNumberOfOperands() {
        return 2;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public String toFormulaString(String[] operands) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append(CONCAT);
        buffer.append(operands[1]);
        return buffer.toString();
    }
}
