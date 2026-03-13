package org.apache.poi.ss.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/* JADX INFO: loaded from: classes.dex */
public class SheetBuilder {
    private final Object[][] cells;
    private final Workbook workbook;
    private boolean shouldCreateEmptyCells = false;
    private String sheetName = null;

    public SheetBuilder(Workbook workbook, Object[][] cells) {
        this.workbook = workbook;
        this.cells = (Object[][]) cells.clone();
    }

    public boolean getCreateEmptyCells() {
        return this.shouldCreateEmptyCells;
    }

    public SheetBuilder setCreateEmptyCells(boolean shouldCreateEmptyCells) {
        this.shouldCreateEmptyCells = shouldCreateEmptyCells;
        return this;
    }

    public SheetBuilder setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public Sheet build() {
        String str = this.sheetName;
        Sheet sheet = str == null ? this.workbook.createSheet() : this.workbook.createSheet(str);
        int rowIndex = 0;
        while (true) {
            Object[][] objArr = this.cells;
            if (rowIndex < objArr.length) {
                Object[] rowArray = objArr[rowIndex];
                Row currentRow = sheet.createRow(rowIndex);
                for (int cellIndex = 0; cellIndex < rowArray.length; cellIndex++) {
                    Object cellValue = rowArray[cellIndex];
                    if (cellValue != null || this.shouldCreateEmptyCells) {
                        Cell currentCell = currentRow.createCell(cellIndex);
                        setCellValue(currentCell, cellValue);
                    }
                }
                rowIndex++;
            } else {
                return sheet;
            }
        }
    }

    private void setCellValue(Cell cell, Object value) {
        if (value == null || cell == null) {
            return;
        }
        if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            cell.setCellValue(doubleValue);
        } else {
            if (value instanceof Date) {
                cell.setCellValue((Date) value);
                return;
            }
            if (value instanceof Calendar) {
                cell.setCellValue((Calendar) value);
            } else if (isFormulaDefinition(value)) {
                cell.setCellFormula(getFormula(value));
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private boolean isFormulaDefinition(Object obj) {
        if (!(obj instanceof String)) {
            return false;
        }
        String str = (String) obj;
        return str.length() >= 2 && ((String) obj).charAt(0) == '=';
    }

    private String getFormula(Object obj) {
        return ((String) obj).substring(1);
    }
}
