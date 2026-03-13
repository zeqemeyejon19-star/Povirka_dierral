package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class RefNPtg extends Ref2DPtgBase {
    public static final byte sid = 44;

    @Override // org.apache.poi.ss.formula.ptg.Ref2DPtgBase, org.apache.poi.ss.formula.ptg.Ptg
    public /* bridge */ /* synthetic */ void write(LittleEndianOutput x0) {
        super.write(x0);
    }

    public RefNPtg(LittleEndianInput in) {
        super(in);
    }

    @Override // org.apache.poi.ss.formula.ptg.Ref2DPtgBase
    protected byte getSid() {
        return sid;
    }
}
