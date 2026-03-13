package org.apache.poi.ss.formula.ptg;

/* JADX INFO: loaded from: classes.dex */
public abstract class OperandPtg extends Ptg implements Cloneable {
    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final boolean isBaseToken() {
        return false;
    }

    public final OperandPtg copy() {
        try {
            return (OperandPtg) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
