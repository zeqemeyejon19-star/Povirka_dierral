package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/* JADX INFO: loaded from: classes.dex */
final class ForkedEvaluationCell implements EvaluationCell {
    private boolean _booleanValue;
    private CellType _cellType;
    private int _errorValue;
    private final EvaluationCell _masterCell;
    private double _numberValue;
    private final EvaluationSheet _sheet;
    private String _stringValue;

    public ForkedEvaluationCell(ForkedEvaluationSheet sheet, EvaluationCell masterCell) {
        this._sheet = sheet;
        this._masterCell = masterCell;
        setValue(BlankEval.instance);
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public Object getIdentityKey() {
        return this._masterCell.getIdentityKey();
    }

    public void setValue(ValueEval value) {
        Class<?> cls = value.getClass();
        if (cls == NumberEval.class) {
            this._cellType = CellType.NUMERIC;
            this._numberValue = ((NumberEval) value).getNumberValue();
            return;
        }
        if (cls == StringEval.class) {
            this._cellType = CellType.STRING;
            this._stringValue = ((StringEval) value).getStringValue();
            return;
        }
        if (cls == BoolEval.class) {
            this._cellType = CellType.BOOLEAN;
            this._booleanValue = ((BoolEval) value).getBooleanValue();
        } else if (cls == ErrorEval.class) {
            this._cellType = CellType.ERROR;
            this._errorValue = ((ErrorEval) value).getErrorCode();
        } else {
            if (cls == BlankEval.class) {
                this._cellType = CellType.BLANK;
                return;
            }
            throw new IllegalArgumentException("Unexpected value class (" + cls.getName() + ")");
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.eval.forked.ForkedEvaluationCell$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.BLANK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.STRING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void copyValue(Cell destCell) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[this._cellType.ordinal()];
        if (i == 1) {
            destCell.setCellType(CellType.BLANK);
            return;
        }
        if (i == 2) {
            destCell.setCellValue(this._numberValue);
            return;
        }
        if (i == 3) {
            destCell.setCellValue(this._booleanValue);
        } else if (i == 4) {
            destCell.setCellValue(this._stringValue);
        } else {
            if (i == 5) {
                destCell.setCellErrorValue((byte) this._errorValue);
                return;
            }
            throw new IllegalStateException("Unexpected data type (" + this._cellType + ")");
        }
    }

    private void checkCellType(CellType expectedCellType) {
        if (this._cellType != expectedCellType) {
            throw new RuntimeException("Wrong data type (" + this._cellType + ")");
        }
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public int getCellType() {
        return this._cellType.getCode();
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public CellType getCellTypeEnum() {
        return this._cellType;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public boolean getBooleanCellValue() {
        checkCellType(CellType.BOOLEAN);
        return this._booleanValue;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public int getErrorCellValue() {
        checkCellType(CellType.ERROR);
        return this._errorValue;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public double getNumericCellValue() {
        checkCellType(CellType.NUMERIC);
        return this._numberValue;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public String getStringCellValue() {
        checkCellType(CellType.STRING);
        return this._stringValue;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public EvaluationSheet getSheet() {
        return this._sheet;
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public int getRowIndex() {
        return this._masterCell.getRowIndex();
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public int getColumnIndex() {
        return this._masterCell.getColumnIndex();
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public int getCachedFormulaResultType() {
        return this._masterCell.getCachedFormulaResultType();
    }

    @Override // org.apache.poi.ss.formula.EvaluationCell
    public CellType getCachedFormulaResultTypeEnum() {
        return this._masterCell.getCachedFormulaResultTypeEnum();
    }
}
