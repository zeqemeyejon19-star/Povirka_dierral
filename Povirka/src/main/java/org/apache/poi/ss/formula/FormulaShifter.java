package org.apache.poi.ss.formula;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Deleted3DPxg;
import org.apache.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;

/* JADX INFO: loaded from: classes.dex */
public final class FormulaShifter {
    private final int _amountToMove;
    private final int _dstSheetIndex;
    private final int _externSheetIndex;
    private final int _firstMovedIndex;
    private final int _lastMovedIndex;
    private final ShiftMode _mode;
    private final String _sheetName;
    private final int _srcSheetIndex;
    private final SpreadsheetVersion _version;

    private enum ShiftMode {
        RowMove,
        RowCopy,
        SheetMove
    }

    private FormulaShifter(int externSheetIndex, String sheetName, int firstMovedIndex, int lastMovedIndex, int amountToMove, ShiftMode mode, SpreadsheetVersion version) {
        if (amountToMove == 0) {
            throw new IllegalArgumentException("amountToMove must not be zero");
        }
        if (firstMovedIndex > lastMovedIndex) {
            throw new IllegalArgumentException("firstMovedIndex, lastMovedIndex out of order");
        }
        this._externSheetIndex = externSheetIndex;
        this._sheetName = sheetName;
        this._firstMovedIndex = firstMovedIndex;
        this._lastMovedIndex = lastMovedIndex;
        this._amountToMove = amountToMove;
        this._mode = mode;
        this._version = version;
        this._dstSheetIndex = -1;
        this._srcSheetIndex = -1;
    }

    private FormulaShifter(int srcSheetIndex, int dstSheetIndex) {
        this._amountToMove = -1;
        this._lastMovedIndex = -1;
        this._firstMovedIndex = -1;
        this._externSheetIndex = -1;
        this._sheetName = null;
        this._version = null;
        this._srcSheetIndex = srcSheetIndex;
        this._dstSheetIndex = dstSheetIndex;
        this._mode = ShiftMode.SheetMove;
    }

    public static FormulaShifter createForRowShift(int externSheetIndex, String sheetName, int firstMovedRowIndex, int lastMovedRowIndex, int numberOfRowsToMove, SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove, ShiftMode.RowMove, version);
    }

    public static FormulaShifter createForRowCopy(int externSheetIndex, String sheetName, int firstMovedRowIndex, int lastMovedRowIndex, int numberOfRowsToMove, SpreadsheetVersion version) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove, ShiftMode.RowCopy, version);
    }

    public static FormulaShifter createForSheetShift(int srcSheetIndex, int dstSheetIndex) {
        return new FormulaShifter(srcSheetIndex, dstSheetIndex);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append(" [");
        sb.append(this._firstMovedIndex);
        sb.append(this._lastMovedIndex);
        sb.append(this._amountToMove);
        return sb.toString();
    }

    public boolean adjustFormula(Ptg[] ptgs, int currentExternSheetIx) {
        boolean refsWereChanged = false;
        for (int i = 0; i < ptgs.length; i++) {
            Ptg newPtg = adjustPtg(ptgs[i], currentExternSheetIx);
            if (newPtg != null) {
                refsWereChanged = true;
                ptgs[i] = newPtg;
            }
        }
        return refsWereChanged;
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.FormulaShifter$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$formula$FormulaShifter$ShiftMode;

        static {
            int[] iArr = new int[ShiftMode.values().length];
            $SwitchMap$org$apache$poi$ss$formula$FormulaShifter$ShiftMode = iArr;
            try {
                iArr[ShiftMode.RowMove.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$FormulaShifter$ShiftMode[ShiftMode.RowCopy.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$formula$FormulaShifter$ShiftMode[ShiftMode.SheetMove.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private Ptg adjustPtg(Ptg ptg, int currentExternSheetIx) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$formula$FormulaShifter$ShiftMode[this._mode.ordinal()];
        if (i == 1) {
            return adjustPtgDueToRowMove(ptg, currentExternSheetIx);
        }
        if (i == 2) {
            return adjustPtgDueToRowCopy(ptg);
        }
        if (i == 3) {
            return adjustPtgDueToSheetMove(ptg);
        }
        throw new IllegalStateException("Unsupported shift mode: " + this._mode);
    }

    private Ptg adjustPtgDueToRowMove(Ptg ptg, int currentExternSheetIx) {
        if (ptg instanceof RefPtg) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return null;
            }
            return rowMoveRefPtg((RefPtg) ptg);
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            if (this._externSheetIndex != rptg.getExternSheetIndex()) {
                return null;
            }
            return rowMoveRefPtg(rptg);
        }
        if (ptg instanceof Ref3DPxg) {
            Ref3DPxg rpxg = (Ref3DPxg) ptg;
            if (rpxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equals(rpxg.getSheetName())) {
                return null;
            }
            return rowMoveRefPtg(rpxg);
        }
        if (ptg instanceof Area2DPtgBase) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return ptg;
            }
            return rowMoveAreaPtg((Area2DPtgBase) ptg);
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg aptg = (Area3DPtg) ptg;
            if (this._externSheetIndex != aptg.getExternSheetIndex()) {
                return null;
            }
            return rowMoveAreaPtg(aptg);
        }
        if (!(ptg instanceof Area3DPxg)) {
            return null;
        }
        Area3DPxg apxg = (Area3DPxg) ptg;
        if (apxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equals(apxg.getSheetName())) {
            return null;
        }
        return rowMoveAreaPtg(apxg);
    }

    private Ptg adjustPtgDueToRowCopy(Ptg ptg) {
        if (ptg instanceof RefPtg) {
            RefPtg rptg = (RefPtg) ptg;
            return rowCopyRefPtg(rptg);
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg2 = (Ref3DPtg) ptg;
            return rowCopyRefPtg(rptg2);
        }
        if (ptg instanceof Ref3DPxg) {
            Ref3DPxg rpxg = (Ref3DPxg) ptg;
            return rowCopyRefPtg(rpxg);
        }
        if (ptg instanceof Area2DPtgBase) {
            return rowCopyAreaPtg((Area2DPtgBase) ptg);
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg aptg = (Area3DPtg) ptg;
            return rowCopyAreaPtg(aptg);
        }
        if (ptg instanceof Area3DPxg) {
            Area3DPxg apxg = (Area3DPxg) ptg;
            return rowCopyAreaPtg(apxg);
        }
        return null;
    }

    private Ptg adjustPtgDueToSheetMove(Ptg ptg) {
        Ref3DPtg ref;
        int oldSheetIndex;
        int i;
        if (!(ptg instanceof Ref3DPtg) || ((oldSheetIndex = (ref = (Ref3DPtg) ptg).getExternSheetIndex()) < (i = this._srcSheetIndex) && oldSheetIndex < this._dstSheetIndex)) {
            return null;
        }
        if (oldSheetIndex > i && oldSheetIndex > this._dstSheetIndex) {
            return null;
        }
        if (oldSheetIndex == i) {
            ref.setExternSheetIndex(this._dstSheetIndex);
            return ref;
        }
        int i2 = this._dstSheetIndex;
        if (i2 < i) {
            ref.setExternSheetIndex(oldSheetIndex + 1);
            return ref;
        }
        if (i2 > i) {
            ref.setExternSheetIndex(oldSheetIndex - 1);
            return ref;
        }
        return null;
    }

    private Ptg rowMoveRefPtg(RefPtgBase rptg) {
        int refRow = rptg.getRow();
        int i = this._firstMovedIndex;
        if (i <= refRow && refRow <= this._lastMovedIndex) {
            rptg.setRow(this._amountToMove + refRow);
            return rptg;
        }
        int i2 = this._amountToMove;
        int destFirstRowIndex = i + i2;
        int destLastRowIndex = this._lastMovedIndex + i2;
        if (destLastRowIndex < refRow || refRow < destFirstRowIndex) {
            return null;
        }
        if (destFirstRowIndex <= refRow && refRow <= destLastRowIndex) {
            return createDeletedRef(rptg);
        }
        throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + refRow + ", " + refRow + ")");
    }

    private Ptg rowMoveAreaPtg(AreaPtgBase aptg) {
        int aFirstRow = aptg.getFirstRow();
        int aLastRow = aptg.getLastRow();
        int i = this._firstMovedIndex;
        if (i <= aFirstRow && aLastRow <= this._lastMovedIndex) {
            aptg.setFirstRow(this._amountToMove + aFirstRow);
            aptg.setLastRow(this._amountToMove + aLastRow);
            return aptg;
        }
        int i2 = this._amountToMove;
        int destFirstRowIndex = i + i2;
        int i3 = this._lastMovedIndex;
        int destLastRowIndex = i3 + i2;
        if (aFirstRow < i && i3 < aLastRow) {
            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            }
            if (destFirstRowIndex > aLastRow || aLastRow >= destLastRowIndex) {
                return null;
            }
            aptg.setLastRow(destFirstRowIndex - 1);
            return aptg;
        }
        if (i <= aFirstRow && aFirstRow <= i3) {
            if (i2 < 0) {
                aptg.setFirstRow(i2 + aFirstRow);
                return aptg;
            }
            if (destFirstRowIndex > aLastRow) {
                return null;
            }
            int newFirstRowIx = i2 + aFirstRow;
            if (destLastRowIndex < aLastRow) {
                aptg.setFirstRow(newFirstRowIx);
                return aptg;
            }
            int areaRemainingTopRowIx = i3 + 1;
            if (destFirstRowIndex > areaRemainingTopRowIx) {
                newFirstRowIx = areaRemainingTopRowIx;
            }
            aptg.setFirstRow(newFirstRowIx);
            aptg.setLastRow(Math.max(aLastRow, destLastRowIndex));
            return aptg;
        }
        if (i <= aLastRow && aLastRow <= i3) {
            if (i2 > 0) {
                aptg.setLastRow(i2 + aLastRow);
                return aptg;
            }
            if (destLastRowIndex < aFirstRow) {
                return null;
            }
            int newLastRowIx = i2 + aLastRow;
            if (destFirstRowIndex > aFirstRow) {
                aptg.setLastRow(newLastRowIx);
                return aptg;
            }
            int areaRemainingBottomRowIx = i - 1;
            if (destLastRowIndex < areaRemainingBottomRowIx) {
                newLastRowIx = areaRemainingBottomRowIx;
            }
            aptg.setFirstRow(Math.min(aFirstRow, destFirstRowIndex));
            aptg.setLastRow(newLastRowIx);
            return aptg;
        }
        if (destLastRowIndex < aFirstRow || aLastRow < destFirstRowIndex) {
            return null;
        }
        if (destFirstRowIndex <= aFirstRow && aLastRow <= destLastRowIndex) {
            return createDeletedRef(aptg);
        }
        if (aFirstRow <= destFirstRowIndex && destLastRowIndex <= aLastRow) {
            return null;
        }
        if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
            aptg.setFirstRow(destLastRowIndex + 1);
            return aptg;
        }
        if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {
            aptg.setLastRow(destFirstRowIndex - 1);
            return aptg;
        }
        throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + aFirstRow + ", " + aLastRow + ")");
    }

    private Ptg rowCopyRefPtg(RefPtgBase rptg) {
        int refRow = rptg.getRow();
        if (rptg.isRowRelative()) {
            int destRowIndex = this._firstMovedIndex + this._amountToMove;
            if (destRowIndex < 0 || this._version.getLastRowIndex() < destRowIndex) {
                return createDeletedRef(rptg);
            }
            rptg.setRow(this._amountToMove + refRow);
            return rptg;
        }
        return null;
    }

    private Ptg rowCopyAreaPtg(AreaPtgBase aptg) {
        boolean changed = false;
        int aFirstRow = aptg.getFirstRow();
        int aLastRow = aptg.getLastRow();
        if (aptg.isFirstRowRelative()) {
            int destFirstRowIndex = this._amountToMove + aFirstRow;
            if (destFirstRowIndex < 0 || this._version.getLastRowIndex() < destFirstRowIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setFirstRow(destFirstRowIndex);
            changed = true;
        }
        if (aptg.isLastRowRelative()) {
            int destLastRowIndex = this._amountToMove + aLastRow;
            if (destLastRowIndex < 0 || this._version.getLastRowIndex() < destLastRowIndex) {
                return createDeletedRef(aptg);
            }
            aptg.setLastRow(destLastRowIndex);
            changed = true;
        }
        if (changed) {
            aptg.sortTopLeftToBottomRight();
        }
        if (changed) {
            return aptg;
        }
        return null;
    }

    private static Ptg createDeletedRef(Ptg ptg) {
        if (ptg instanceof RefPtg) {
            return new RefErrorPtg();
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            return new DeletedRef3DPtg(rptg.getExternSheetIndex());
        }
        if (ptg instanceof AreaPtg) {
            return new AreaErrPtg();
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg area3DPtg = (Area3DPtg) ptg;
            return new DeletedArea3DPtg(area3DPtg.getExternSheetIndex());
        }
        if (ptg instanceof Ref3DPxg) {
            Ref3DPxg pxg = (Ref3DPxg) ptg;
            return new Deleted3DPxg(pxg.getExternalWorkbookNumber(), pxg.getSheetName());
        }
        if (ptg instanceof Area3DPxg) {
            Area3DPxg pxg2 = (Area3DPxg) ptg;
            return new Deleted3DPxg(pxg2.getExternalWorkbookNumber(), pxg2.getSheetName());
        }
        throw new IllegalArgumentException("Unexpected ref ptg class (" + ptg.getClass().getName() + ")");
    }
}
