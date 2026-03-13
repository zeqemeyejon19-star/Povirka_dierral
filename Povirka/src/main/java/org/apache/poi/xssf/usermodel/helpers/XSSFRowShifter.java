package org.apache.poi.xssf.usermodel.helpers;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.helpers.RowShifter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFRowShifter extends RowShifter {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) XSSFRowShifter.class);

    public XSSFRowShifter(XSSFSheet sh) {
        super(sh);
    }

    public List<CellRangeAddress> shiftMerged(int startRow, int endRow, int n) {
        return shiftMergedRegions(startRow, endRow, n);
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    public void updateNamedRanges(FormulaShifter shifter) {
        Workbook wb = this.sheet.getWorkbook();
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);
        for (Name name : wb.getAllNames()) {
            String formula = name.getRefersToFormula();
            int sheetIndex = name.getSheetIndex();
            Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.NAMEDRANGE, sheetIndex, -1);
            if (shifter.adjustFormula(ptgs, sheetIndex)) {
                String shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
                name.setRefersToFormula(shiftedFmla);
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    public void updateFormulas(FormulaShifter shifter) {
        updateSheetFormulas(this.sheet, shifter);
        Workbook wb = this.sheet.getWorkbook();
        for (Sheet sh : wb) {
            if (this.sheet != sh) {
                updateSheetFormulas(sh, shifter);
            }
        }
    }

    private void updateSheetFormulas(Sheet sh, FormulaShifter shifter) {
        for (Row r : sh) {
            XSSFRow row = (XSSFRow) r;
            updateRowFormulas(row, shifter);
        }
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    @Internal
    public void updateRowFormulas(Row row, FormulaShifter shifter) {
        String shiftedFormula;
        XSSFSheet sheet = (XSSFSheet) row.getSheet();
        for (Cell c : row) {
            XSSFCell cell = (XSSFCell) c;
            CTCell ctCell = cell.getCTCell();
            if (ctCell.isSetF()) {
                CTCellFormula f = ctCell.getF();
                String formula = f.getStringValue();
                if (formula.length() > 0 && (shiftedFormula = shiftFormula(row, formula, shifter)) != null) {
                    f.setStringValue(shiftedFormula);
                    if (f.getT() == STCellFormulaType.SHARED) {
                        int si = (int) f.getSi();
                        CTCellFormula sf = sheet.getSharedFormula(si);
                        sf.setStringValue(shiftedFormula);
                        updateRefInCTCellFormula(row, shifter, sf);
                    }
                }
                updateRefInCTCellFormula(row, shifter, f);
            }
        }
    }

    private void updateRefInCTCellFormula(Row row, FormulaShifter shifter, CTCellFormula f) {
        if (f.isSetRef()) {
            String ref = f.getRef();
            String shiftedRef = shiftFormula(row, ref, shifter);
            if (shiftedRef != null) {
                f.setRef(shiftedRef);
            }
        }
    }

    private static String shiftFormula(Row row, String formula, FormulaShifter shifter) {
        Sheet sheet = row.getSheet();
        Workbook wb = sheet.getWorkbook();
        int sheetIndex = wb.getSheetIndex(sheet);
        int rowIndex = row.getRowNum();
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create((XSSFWorkbook) wb);
        try {
            Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.CELL, sheetIndex, rowIndex);
            if (!shifter.adjustFormula(ptgs, sheetIndex)) {
                return null;
            }
            String shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
            return shiftedFmla;
        } catch (FormulaParseException fpe) {
            logger.log(5, "Error shifting formula on row ", Integer.valueOf(row.getRowNum()), fpe);
            return formula;
        }
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    public void updateConditionalFormatting(FormulaShifter shifter) {
        List<CellRangeAddress> temp;
        XSSFSheet xsheet = (XSSFSheet) this.sheet;
        XSSFWorkbook wb = xsheet.getWorkbook();
        int sheetIndex = wb.getSheetIndex(this.sheet);
        int rowIndex = -1;
        XSSFEvaluationWorkbook fpb = XSSFEvaluationWorkbook.create(wb);
        CTWorksheet ctWorksheet = xsheet.getCTWorksheet();
        CTConditionalFormatting[] conditionalFormattingArray = ctWorksheet.getConditionalFormattingArray();
        int j = conditionalFormattingArray.length - 1;
        while (j >= 0) {
            CTConditionalFormatting cf = conditionalFormattingArray[j];
            ArrayList<CellRangeAddress> cellRanges = new ArrayList<>();
            for (Object stRef : cf.getSqref()) {
                String[] regions = stRef.toString().split(" ");
                XSSFSheet xsheet2 = xsheet;
                int i$ = 0;
                for (int len$ = regions.length; i$ < len$; len$ = len$) {
                    String region = regions[i$];
                    cellRanges.add(CellRangeAddress.valueOf(region));
                    i$++;
                }
                xsheet = xsheet2;
            }
            XSSFSheet xsheet3 = xsheet;
            boolean changed = false;
            List<CellRangeAddress> temp2 = new ArrayList<>();
            for (CellRangeAddress craOld : cellRanges) {
                CellRangeAddress craNew = shiftRange(shifter, craOld, sheetIndex);
                if (craNew == null) {
                    changed = true;
                } else {
                    temp2.add(craNew);
                    if (craNew != craOld) {
                        changed = true;
                    }
                }
            }
            if (changed) {
                int nRanges = temp2.size();
                if (nRanges == 0) {
                    ctWorksheet.removeConditionalFormatting(j);
                    j--;
                    xsheet = xsheet3;
                    wb = wb;
                    rowIndex = rowIndex;
                } else {
                    List<String> refs = new ArrayList<>();
                    for (CellRangeAddress a : temp2) {
                        refs.add(a.formatAsString());
                        changed = changed;
                    }
                    cf.setSqref(refs);
                }
            }
            CTCfRule[] arr$ = cf.getCfRuleArray();
            int len$2 = arr$.length;
            int i$2 = 0;
            while (i$2 < len$2) {
                CTCfRule cfRule = arr$[i$2];
                String[] formulaArray = cfRule.getFormulaArray();
                CTCfRule[] arr$2 = arr$;
                int i = 0;
                while (true) {
                    temp = temp2;
                    if (i < formulaArray.length) {
                        String formula = formulaArray[i];
                        XSSFWorkbook wb2 = wb;
                        int rowIndex2 = rowIndex;
                        Ptg[] ptgs = FormulaParser.parse(formula, fpb, FormulaType.CELL, sheetIndex, -1);
                        if (shifter.adjustFormula(ptgs, sheetIndex)) {
                            String shiftedFmla = FormulaRenderer.toFormulaString(fpb, ptgs);
                            cfRule.setFormulaArray(i, shiftedFmla);
                        }
                        i++;
                        temp2 = temp;
                        wb = wb2;
                        rowIndex = rowIndex2;
                    }
                }
                i$2++;
                temp2 = temp;
                arr$ = arr$2;
            }
            j--;
            xsheet = xsheet3;
            wb = wb;
            rowIndex = rowIndex;
        }
    }

    @Override // org.apache.poi.ss.usermodel.helpers.RowShifter
    public void updateHyperlinks(FormulaShifter shifter) {
        int sheetIndex = this.sheet.getWorkbook().getSheetIndex(this.sheet);
        List<? extends Hyperlink> hyperlinkList = this.sheet.getHyperlinkList();
        for (Hyperlink hyperlink : hyperlinkList) {
            XSSFHyperlink xhyperlink = (XSSFHyperlink) hyperlink;
            String cellRef = xhyperlink.getCellRef();
            CellRangeAddress cra = CellRangeAddress.valueOf(cellRef);
            CellRangeAddress shiftedRange = shiftRange(shifter, cra, sheetIndex);
            if (shiftedRange != null && shiftedRange != cra) {
                xhyperlink.setCellReference(shiftedRange.formatAsString());
            }
        }
    }

    private static CellRangeAddress shiftRange(FormulaShifter shifter, CellRangeAddress cra, int currentExternSheetIx) {
        AreaPtg aptg = new AreaPtg(cra.getFirstRow(), cra.getLastRow(), cra.getFirstColumn(), cra.getLastColumn(), false, false, false, false);
        Ptg[] ptgs = {aptg};
        if (!shifter.adjustFormula(ptgs, currentExternSheetIx)) {
            return cra;
        }
        Ptg ptg0 = ptgs[0];
        if (ptg0 instanceof AreaPtg) {
            AreaPtg bptg = (AreaPtg) ptg0;
            return new CellRangeAddress(bptg.getFirstRow(), bptg.getLastRow(), bptg.getFirstColumn(), bptg.getLastColumn());
        }
        if (ptg0 instanceof AreaErrPtg) {
            return null;
        }
        throw new IllegalStateException("Unexpected shifted ptg class (" + ptg0.getClass().getName() + ")");
    }
}
