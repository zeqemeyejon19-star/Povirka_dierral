package org.apache.poi.ss.formula;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ExternalNameEval;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public final class OperationEvaluationContext {
    public static final FreeRefFunction UDF = UserDefinedFunction.instance;
    private final WorkbookEvaluator _bookEvaluator;
    private final int _columnIndex;
    private final boolean _isSingleValue;
    private final int _rowIndex;
    private final int _sheetIndex;
    private final EvaluationTracker _tracker;
    private final EvaluationWorkbook _workbook;

    public OperationEvaluationContext(WorkbookEvaluator bookEvaluator, EvaluationWorkbook workbook, int sheetIndex, int srcRowNum, int srcColNum, EvaluationTracker tracker) {
        this(bookEvaluator, workbook, sheetIndex, srcRowNum, srcColNum, tracker, true);
    }

    public OperationEvaluationContext(WorkbookEvaluator bookEvaluator, EvaluationWorkbook workbook, int sheetIndex, int srcRowNum, int srcColNum, EvaluationTracker tracker, boolean isSingleValue) {
        this._bookEvaluator = bookEvaluator;
        this._workbook = workbook;
        this._sheetIndex = sheetIndex;
        this._rowIndex = srcRowNum;
        this._columnIndex = srcColNum;
        this._tracker = tracker;
        this._isSingleValue = isSingleValue;
    }

    public EvaluationWorkbook getWorkbook() {
        return this._workbook;
    }

    public int getRowIndex() {
        return this._rowIndex;
    }

    public int getColumnIndex() {
        return this._columnIndex;
    }

    SheetRangeEvaluator createExternSheetRefEvaluator(ExternSheetReferenceToken ptg) {
        return createExternSheetRefEvaluator(ptg.getExternSheetIndex());
    }

    SheetRangeEvaluator createExternSheetRefEvaluator(String firstSheetName, String lastSheetName, int externalWorkbookNumber) {
        EvaluationWorkbook.ExternalSheet externalSheet = this._workbook.getExternalSheet(firstSheetName, lastSheetName, externalWorkbookNumber);
        return createExternSheetRefEvaluator(externalSheet);
    }

    SheetRangeEvaluator createExternSheetRefEvaluator(int externSheetIndex) {
        EvaluationWorkbook.ExternalSheet externalSheet = this._workbook.getExternalSheet(externSheetIndex);
        return createExternSheetRefEvaluator(externalSheet);
    }

    SheetRangeEvaluator createExternSheetRefEvaluator(EvaluationWorkbook.ExternalSheet externalSheet) {
        WorkbookEvaluator targetEvaluator;
        int otherFirstSheetIndex;
        int otherLastSheetIndex = -1;
        if (externalSheet == null || externalSheet.getWorkbookName() == null) {
            targetEvaluator = this._bookEvaluator;
            if (externalSheet == null) {
                otherFirstSheetIndex = 0;
            } else {
                otherFirstSheetIndex = this._workbook.getSheetIndex(externalSheet.getSheetName());
            }
            if (externalSheet instanceof EvaluationWorkbook.ExternalSheetRange) {
                String lastSheetName = ((EvaluationWorkbook.ExternalSheetRange) externalSheet).getLastSheetName();
                otherLastSheetIndex = this._workbook.getSheetIndex(lastSheetName);
            }
        } else {
            String workbookName = externalSheet.getWorkbookName();
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
                otherFirstSheetIndex = targetEvaluator.getSheetIndex(externalSheet.getSheetName());
                if (externalSheet instanceof EvaluationWorkbook.ExternalSheetRange) {
                    String lastSheetName2 = ((EvaluationWorkbook.ExternalSheetRange) externalSheet).getLastSheetName();
                    otherLastSheetIndex = targetEvaluator.getSheetIndex(lastSheetName2);
                }
                if (otherFirstSheetIndex < 0) {
                    throw new RuntimeException("Invalid sheet name '" + externalSheet.getSheetName() + "' in bool '" + workbookName + "'.");
                }
            } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        if (otherLastSheetIndex == -1) {
            otherLastSheetIndex = otherFirstSheetIndex;
        }
        SheetRefEvaluator[] evals = new SheetRefEvaluator[(otherLastSheetIndex - otherFirstSheetIndex) + 1];
        for (int i = 0; i < evals.length; i++) {
            int otherSheetIndex = i + otherFirstSheetIndex;
            evals[i] = new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
        }
        return new SheetRangeEvaluator(otherFirstSheetIndex, otherLastSheetIndex, evals);
    }

    private SheetRefEvaluator createExternSheetRefEvaluator(String workbookName, String sheetName) {
        WorkbookEvaluator targetEvaluator;
        if (workbookName == null) {
            targetEvaluator = this._bookEvaluator;
        } else {
            if (sheetName == null) {
                throw new IllegalArgumentException("sheetName must not be null if workbookName is provided");
            }
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                return null;
            }
        }
        int otherSheetIndex = sheetName == null ? this._sheetIndex : targetEvaluator.getSheetIndex(sheetName);
        if (otherSheetIndex < 0) {
            return null;
        }
        return new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
    }

    public SheetRangeEvaluator getRefEvaluatorForCurrentSheet() {
        SheetRefEvaluator sre = new SheetRefEvaluator(this._bookEvaluator, this._tracker, this._sheetIndex);
        return new SheetRangeEvaluator(this._sheetIndex, sre);
    }

    public ValueEval getDynamicReference(String workbookName, String sheetName, String refStrPart1, String refStrPart2, boolean isA1Style) {
        int lastCol;
        int firstCol;
        int firstRow;
        int lastRow;
        if (!isA1Style) {
            throw new RuntimeException("R1C1 style not supported yet");
        }
        SheetRefEvaluator se = createExternSheetRefEvaluator(workbookName, sheetName);
        if (se == null) {
            return ErrorEval.REF_INVALID;
        }
        SheetRangeEvaluator sre = new SheetRangeEvaluator(this._sheetIndex, se);
        SpreadsheetVersion ssVersion = ((FormulaParsingWorkbook) this._workbook).getSpreadsheetVersion();
        CellReference.NameType part1refType = classifyCellReference(refStrPart1, ssVersion);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
        if (i == 1) {
            return ErrorEval.REF_INVALID;
        }
        if (i == 2) {
            EvaluationName nm = ((FormulaParsingWorkbook) this._workbook).getName(refStrPart1, this._sheetIndex);
            if (nm.isRange()) {
                return this._bookEvaluator.evaluateNameFormula(nm.getNameDefinition(), this);
            }
            throw new RuntimeException("Specified name '" + refStrPart1 + "' is not a range as expected.");
        }
        if (refStrPart2 == null) {
            int i2 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
            if (i2 == 3 || i2 == 4) {
                return ErrorEval.REF_INVALID;
            }
            if (i2 == 5) {
                CellReference cr = new CellReference(refStrPart1);
                return new LazyRefEval(cr.getRow(), cr.getCol(), sre);
            }
            throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
        }
        CellReference.NameType part2refType = classifyCellReference(refStrPart1, ssVersion);
        int i3 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part2refType.ordinal()];
        if (i3 == 1) {
            return ErrorEval.REF_INVALID;
        }
        if (i3 == 2) {
            throw new RuntimeException("Cannot evaluate '" + refStrPart1 + "'. Indirect evaluation of defined names not supported yet");
        }
        if (part2refType != part1refType) {
            return ErrorEval.REF_INVALID;
        }
        int i4 = AnonymousClass1.$SwitchMap$org$apache$poi$ss$util$CellReference$NameType[part1refType.ordinal()];
        if (i4 != 3) {
            if (i4 != 4) {
                if (i4 == 5) {
                    CellReference cr2 = new CellReference(refStrPart1);
                    int firstRow2 = cr2.getRow();
                    int firstCol2 = cr2.getCol();
                    CellReference cr3 = new CellReference(refStrPart2);
                    int lastRow2 = cr3.getRow();
                    int lastCol2 = cr3.getCol();
                    lastCol = lastCol2;
                    firstCol = firstCol2;
                    firstRow = firstRow2;
                    lastRow = lastRow2;
                } else {
                    throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
                }
            } else if (part2refType.equals(CellReference.NameType.ROW)) {
                int firstRow3 = parseColRef(refStrPart1);
                int lastRow3 = parseColRef(refStrPart2);
                lastCol = ssVersion.getLastColumnIndex();
                firstCol = 0;
                firstRow = firstRow3;
                lastRow = lastRow3;
            } else {
                lastCol = ssVersion.getLastColumnIndex();
                int firstRow4 = parseRowRef(refStrPart1);
                int lastRow4 = parseRowRef(refStrPart2);
                firstCol = 0;
                firstRow = firstRow4;
                lastRow = lastRow4;
            }
        } else if (part2refType.equals(CellReference.NameType.COLUMN)) {
            int lastRow5 = ssVersion.getLastRowIndex();
            int firstCol3 = parseRowRef(refStrPart1);
            lastCol = parseRowRef(refStrPart2);
            firstCol = firstCol3;
            firstRow = 0;
            lastRow = lastRow5;
        } else {
            int lastRow6 = ssVersion.getLastRowIndex();
            int firstCol4 = parseColRef(refStrPart1);
            int lastCol3 = parseColRef(refStrPart2);
            lastCol = lastCol3;
            firstCol = firstCol4;
            firstRow = 0;
            lastRow = lastRow6;
        }
        return new LazyAreaEval(firstRow, firstCol, lastRow, lastCol, sre);
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.formula.OperationEvaluationContext$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$util$CellReference$NameType;

        static {
            int[] iArr = new int[CellReference.NameType.values().length];
            $SwitchMap$org$apache$poi$ss$util$CellReference$NameType = iArr;
            try {
                iArr[CellReference.NameType.BAD_CELL_OR_NAMED_RANGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.NAMED_RANGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.COLUMN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.ROW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$util$CellReference$NameType[CellReference.NameType.CELL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private static int parseRowRef(String refStrPart) {
        return CellReference.convertColStringToIndex(refStrPart);
    }

    private static int parseColRef(String refStrPart) {
        return Integer.parseInt(refStrPart) - 1;
    }

    private static CellReference.NameType classifyCellReference(String str, SpreadsheetVersion ssVersion) {
        int len = str.length();
        if (len < 1) {
            return CellReference.NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return CellReference.classifyCellReference(str, ssVersion);
    }

    public FreeRefFunction findUserDefinedFunction(String functionName) {
        return this._bookEvaluator.findUserDefinedFunction(functionName);
    }

    public ValueEval getRefEval(int rowIndex, int columnIndex) {
        SheetRangeEvaluator sre = getRefEvaluatorForCurrentSheet();
        return new LazyRefEval(rowIndex, columnIndex, sre);
    }

    public ValueEval getRef3DEval(Ref3DPtg rptg) {
        SheetRangeEvaluator sre = createExternSheetRefEvaluator(rptg.getExternSheetIndex());
        return new LazyRefEval(rptg.getRow(), rptg.getColumn(), sre);
    }

    public ValueEval getRef3DEval(Ref3DPxg rptg) {
        SheetRangeEvaluator sre = createExternSheetRefEvaluator(rptg.getSheetName(), rptg.getLastSheetName(), rptg.getExternalWorkbookNumber());
        return new LazyRefEval(rptg.getRow(), rptg.getColumn(), sre);
    }

    public ValueEval getAreaEval(int firstRowIndex, int firstColumnIndex, int lastRowIndex, int lastColumnIndex) {
        SheetRangeEvaluator sre = getRefEvaluatorForCurrentSheet();
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, sre);
    }

    public ValueEval getArea3DEval(Area3DPtg aptg) {
        SheetRangeEvaluator sre = createExternSheetRefEvaluator(aptg.getExternSheetIndex());
        return new LazyAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), sre);
    }

    public ValueEval getArea3DEval(Area3DPxg aptg) {
        SheetRangeEvaluator sre = createExternSheetRefEvaluator(aptg.getSheetName(), aptg.getLastSheetName(), aptg.getExternalWorkbookNumber());
        return new LazyAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), sre);
    }

    public ValueEval getNameXEval(NameXPtg nameXPtg) {
        EvaluationWorkbook.ExternalSheet externSheet = this._workbook.getExternalSheet(nameXPtg.getSheetRefIndex());
        if (externSheet == null || externSheet.getWorkbookName() == null) {
            return getLocalNameXEval(nameXPtg);
        }
        String workbookName = externSheet.getWorkbookName();
        EvaluationWorkbook.ExternalName externName = this._workbook.getExternalName(nameXPtg.getSheetRefIndex(), nameXPtg.getNameIndex());
        return getExternalNameXEval(externName, workbookName);
    }

    public ValueEval getNameXEval(NameXPxg nameXPxg) {
        EvaluationWorkbook.ExternalSheet externSheet = this._workbook.getExternalSheet(nameXPxg.getSheetName(), null, nameXPxg.getExternalWorkbookNumber());
        if (externSheet == null || externSheet.getWorkbookName() == null) {
            return getLocalNameXEval(nameXPxg);
        }
        String workbookName = externSheet.getWorkbookName();
        EvaluationWorkbook.ExternalName externName = this._workbook.getExternalName(nameXPxg.getNameName(), nameXPxg.getSheetName(), nameXPxg.getExternalWorkbookNumber());
        return getExternalNameXEval(externName, workbookName);
    }

    private ValueEval getLocalNameXEval(NameXPxg nameXPxg) {
        int sIdx = -1;
        if (nameXPxg.getSheetName() != null) {
            sIdx = this._workbook.getSheetIndex(nameXPxg.getSheetName());
        }
        String name = nameXPxg.getNameName();
        EvaluationName evalName = this._workbook.getName(name, sIdx);
        if (evalName != null) {
            return new ExternalNameEval(evalName);
        }
        return new FunctionNameEval(name);
    }

    private ValueEval getLocalNameXEval(NameXPtg nameXPtg) {
        EvaluationName evalName;
        String name = this._workbook.resolveNameXText(nameXPtg);
        int sheetNameAt = name.indexOf(33);
        if (sheetNameAt <= -1) {
            evalName = this._workbook.getName(name, -1);
        } else {
            String sheetName = name.substring(0, sheetNameAt);
            String nameName = name.substring(sheetNameAt + 1);
            EvaluationWorkbook evaluationWorkbook = this._workbook;
            evalName = evaluationWorkbook.getName(nameName, evaluationWorkbook.getSheetIndex(sheetName));
        }
        if (evalName != null) {
            return new ExternalNameEval(evalName);
        }
        return new FunctionNameEval(name);
    }

    public int getSheetIndex() {
        return this._sheetIndex;
    }

    public boolean isSingleValue() {
        return this._isSingleValue;
    }

    private ValueEval getExternalNameXEval(EvaluationWorkbook.ExternalName externName, String workbookName) {
        try {
            WorkbookEvaluator refWorkbookEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            EvaluationName evaluationName = refWorkbookEvaluator.getName(externName.getName(), externName.getIx() - 1);
            if (evaluationName != null && evaluationName.hasFormula()) {
                if (evaluationName.getNameDefinition().length > 1) {
                    throw new RuntimeException("Complex name formulas not supported yet");
                }
                OperationEvaluationContext refWorkbookContext = new OperationEvaluationContext(refWorkbookEvaluator, refWorkbookEvaluator.getWorkbook(), -1, -1, -1, this._tracker);
                Ptg ptg = evaluationName.getNameDefinition()[0];
                if (ptg instanceof Ref3DPtg) {
                    Ref3DPtg ref3D = (Ref3DPtg) ptg;
                    return refWorkbookContext.getRef3DEval(ref3D);
                }
                if (ptg instanceof Ref3DPxg) {
                    Ref3DPxg ref3D2 = (Ref3DPxg) ptg;
                    return refWorkbookContext.getRef3DEval(ref3D2);
                }
                if (ptg instanceof Area3DPtg) {
                    Area3DPtg area3D = (Area3DPtg) ptg;
                    return refWorkbookContext.getArea3DEval(area3D);
                }
                if (ptg instanceof Area3DPxg) {
                    Area3DPxg area3D2 = (Area3DPxg) ptg;
                    return refWorkbookContext.getArea3DEval(area3D2);
                }
            }
            return ErrorEval.REF_INVALID;
        } catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
            return ErrorEval.REF_INVALID;
        }
    }
}
