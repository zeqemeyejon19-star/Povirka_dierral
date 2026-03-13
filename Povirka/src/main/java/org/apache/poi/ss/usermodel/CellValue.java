package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public final class CellValue {
    private final boolean _booleanValue;
    private final CellType _cellType;
    private final int _errorCode;
    private final double _numberValue;
    private final String _textValue;
    public static final CellValue TRUE = new CellValue(CellType.BOOLEAN, 0.0d, true, null, 0);
    public static final CellValue FALSE = new CellValue(CellType.BOOLEAN, 0.0d, false, null, 0);

    private CellValue(CellType cellType, double numberValue, boolean booleanValue, String textValue, int errorCode) {
        this._cellType = cellType;
        this._numberValue = numberValue;
        this._booleanValue = booleanValue;
        this._textValue = textValue;
        this._errorCode = errorCode;
    }

    public CellValue(double numberValue) {
        this(CellType.NUMERIC, numberValue, false, null, 0);
    }

    public static CellValue valueOf(boolean booleanValue) {
        return booleanValue ? TRUE : FALSE;
    }

    public CellValue(String stringValue) {
        this(CellType.STRING, 0.0d, false, stringValue, 0);
    }

    public static CellValue getError(int errorCode) {
        return new CellValue(CellType.ERROR, 0.0d, false, null, errorCode);
    }

    public boolean getBooleanValue() {
        return this._booleanValue;
    }

    public double getNumberValue() {
        return this._numberValue;
    }

    public String getStringValue() {
        return this._textValue;
    }

    @Removal(version = "4.2")
    public CellType getCellTypeEnum() {
        return this._cellType;
    }

    @Deprecated
    public int getCellType() {
        return this._cellType.getCode();
    }

    public byte getErrorValue() {
        return (byte) this._errorCode;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(formatAsString());
        sb.append("]");
        return sb.toString();
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.usermodel.CellValue$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.NUMERIC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.STRING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public String formatAsString() {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 1) {
            return String.valueOf(this._numberValue);
        }
        if (i == 2) {
            return '\"' + this._textValue + '\"';
        }
        if (i == 3) {
            return this._booleanValue ? "TRUE" : "FALSE";
        }
        if (i == 4) {
            return ErrorEval.getText(this._errorCode);
        }
        return "<error unexpected cell type " + this._cellType + ">";
    }
}
