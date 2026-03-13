package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/* JADX INFO: loaded from: classes.dex */
public class DataSources {
    private DataSources() {
    }

    public static <T> ChartDataSource<T> fromArray(T[] elements) {
        return new ArrayDataSource(elements);
    }

    public static ChartDataSource<Number> fromNumericCellRange(Sheet sheet, CellRangeAddress cellRangeAddress) {
        return new AbstractCellRangeDataSource<Number>(sheet, cellRangeAddress) { // from class: org.apache.poi.ss.usermodel.charts.DataSources.1
            @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
            public Number getPointAt(int index) {
                CellValue cellValue = getCellValueAt(index);
                if (cellValue != null && cellValue.getCellTypeEnum() == CellType.NUMERIC) {
                    return Double.valueOf(cellValue.getNumberValue());
                }
                return null;
            }

            @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
            public boolean isNumeric() {
                return true;
            }
        };
    }

    public static ChartDataSource<String> fromStringCellRange(Sheet sheet, CellRangeAddress cellRangeAddress) {
        return new AbstractCellRangeDataSource<String>(sheet, cellRangeAddress) { // from class: org.apache.poi.ss.usermodel.charts.DataSources.2
            @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
            public String getPointAt(int index) {
                CellValue cellValue = getCellValueAt(index);
                if (cellValue != null && cellValue.getCellTypeEnum() == CellType.STRING) {
                    return cellValue.getStringValue();
                }
                return null;
            }

            @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
            public boolean isNumeric() {
                return false;
            }
        };
    }

    private static class ArrayDataSource<T> implements ChartDataSource<T> {
        private final T[] elements;

        public ArrayDataSource(T[] tArr) {
            this.elements = (T[]) ((Object[]) tArr.clone());
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public int getPointCount() {
            return this.elements.length;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public T getPointAt(int index) {
            return this.elements[index];
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public boolean isReference() {
            return false;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public boolean isNumeric() {
            Class<?> arrayComponentType = this.elements.getClass().getComponentType();
            return Number.class.isAssignableFrom(arrayComponentType);
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public String getFormulaString() {
            throw new UnsupportedOperationException("Literal data source can not be expressed by reference.");
        }
    }

    private static abstract class AbstractCellRangeDataSource<T> implements ChartDataSource<T> {
        private final CellRangeAddress cellRangeAddress;
        private FormulaEvaluator evaluator;
        private final int numOfCells;
        private final Sheet sheet;

        protected AbstractCellRangeDataSource(Sheet sheet, CellRangeAddress cellRangeAddress) {
            this.sheet = sheet;
            CellRangeAddress cellRangeAddressCopy = cellRangeAddress.copy();
            this.cellRangeAddress = cellRangeAddressCopy;
            this.numOfCells = cellRangeAddressCopy.getNumberOfCells();
            this.evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public int getPointCount() {
            return this.numOfCells;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public boolean isReference() {
            return true;
        }

        @Override // org.apache.poi.ss.usermodel.charts.ChartDataSource
        public String getFormulaString() {
            return this.cellRangeAddress.formatAsString(this.sheet.getSheetName(), true);
        }

        protected CellValue getCellValueAt(int index) {
            if (index < 0 || index >= this.numOfCells) {
                throw new IndexOutOfBoundsException("Index must be between 0 and " + (this.numOfCells - 1) + " (inclusive), given: " + index);
            }
            int firstRow = this.cellRangeAddress.getFirstRow();
            int firstCol = this.cellRangeAddress.getFirstColumn();
            int lastCol = this.cellRangeAddress.getLastColumn();
            int width = (lastCol - firstCol) + 1;
            int rowIndex = (index / width) + firstRow;
            int cellIndex = (index % width) + firstCol;
            Row row = this.sheet.getRow(rowIndex);
            if (row == null) {
                return null;
            }
            return this.evaluator.evaluate(row.getCell(cellIndex));
        }
    }
}
