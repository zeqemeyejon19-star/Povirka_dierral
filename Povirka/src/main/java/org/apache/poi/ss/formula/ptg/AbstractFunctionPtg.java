package org.apache.poi.ss.formula.ptg;

import java.util.Locale;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractFunctionPtg extends OperationPtg {
    private static final short FUNCTION_INDEX_EXTERNAL = 255;
    public static final String FUNCTION_NAME_IF = "IF";
    private final short _functionIndex;
    private final int _numberOfArgs;
    private final byte[] paramClass;
    private final byte returnClass;

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public abstract int getSize();

    protected AbstractFunctionPtg(int functionIndex, int pReturnClass, byte[] paramTypes, int nParams) {
        this._numberOfArgs = nParams;
        if (functionIndex < -32768 || functionIndex > 32767) {
            throw new RuntimeException("functionIndex " + functionIndex + " cannot be cast to short");
        }
        this._functionIndex = (short) functionIndex;
        if (pReturnClass < -128 || pReturnClass > 127) {
            throw new RuntimeException("pReturnClass " + pReturnClass + " cannot be cast to byte");
        }
        this.returnClass = (byte) pReturnClass;
        this.paramClass = paramTypes;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final boolean isBaseToken() {
        return false;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(lookupName(this._functionIndex));
        sb.append(" nArgs=").append(this._numberOfArgs);
        sb.append("]");
        return sb.toString();
    }

    public final short getFunctionIndex() {
        return this._functionIndex;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public final int getNumberOfOperands() {
        return this._numberOfArgs;
    }

    public final String getName() {
        return lookupName(this._functionIndex);
    }

    public final boolean isExternalFunction() {
        return this._functionIndex == 255;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public final String toFormulaString() {
        return getName();
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg
    public String toFormulaString(String[] operands) {
        StringBuilder buf = new StringBuilder();
        if (isExternalFunction()) {
            buf.append(operands[0]);
            appendArgs(buf, 1, operands);
        } else {
            buf.append(getName());
            appendArgs(buf, 0, operands);
        }
        return buf.toString();
    }

    private static void appendArgs(StringBuilder buf, int firstArgIx, String[] operands) {
        buf.append('(');
        for (int i = firstArgIx; i < operands.length; i++) {
            if (i > firstArgIx) {
                buf.append(',');
            }
            buf.append(operands[i]);
        }
        buf.append(")");
    }

    public static final boolean isBuiltInFunctionName(String name) {
        short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase(Locale.ROOT));
        return ix >= 0;
    }

    protected final String lookupName(short index) {
        if (index == 255) {
            return "#external#";
        }
        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(index);
        if (fm == null) {
            throw new RuntimeException("bad function index (" + ((int) index) + ")");
        }
        return fm.getName();
    }

    protected static short lookupIndex(String name) {
        short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase(Locale.ROOT));
        if (ix < 0) {
            return (short) 255;
        }
        return ix;
    }

    @Override // org.apache.poi.ss.formula.ptg.OperationPtg, org.apache.poi.ss.formula.ptg.Ptg
    public byte getDefaultOperandClass() {
        return this.returnClass;
    }

    public final byte getParameterClass(int index) {
        byte[] bArr = this.paramClass;
        if (index >= bArr.length) {
            return bArr[bArr.length - 1];
        }
        return bArr[index];
    }
}
