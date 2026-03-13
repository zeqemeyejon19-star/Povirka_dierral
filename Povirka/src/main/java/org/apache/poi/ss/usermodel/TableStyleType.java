package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressBase;

/* JADX INFO: loaded from: classes.dex */
public enum TableStyleType {
    wholeTable { // from class: org.apache.poi.ss.usermodel.TableStyleType.1
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getStartColIndex(), table.getEndColIndex());
        }
    },
    pageFieldLabels,
    pageFieldValues,
    firstColumnStripe { // from class: org.apache.poi.ss.usermodel.TableStyleType.2
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            TableStyleInfo info = table.getStyle();
            if (!info.isShowColumnStripes()) {
                return null;
            }
            DifferentialStyleProvider c1Style = info.getStyle().getStyle(firstColumnStripe);
            DifferentialStyleProvider c2Style = info.getStyle().getStyle(secondColumnStripe);
            int c1Stripe = c1Style == null ? 1 : Math.max(1, c1Style.getStripeSize());
            int c2Stripe = c2Style != null ? Math.max(1, c2Style.getStripeSize()) : 1;
            int firstStart = table.getStartColIndex();
            int secondStart = firstStart + c1Stripe;
            int c = cell.getColumnIndex();
            while (firstStart <= c) {
                if (c >= firstStart && c <= secondStart - 1) {
                    return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), firstStart, secondStart - 1);
                }
                firstStart = secondStart + c2Stripe;
                secondStart = firstStart + c1Stripe;
            }
            return null;
        }
    },
    secondColumnStripe { // from class: org.apache.poi.ss.usermodel.TableStyleType.3
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            TableStyleInfo info = table.getStyle();
            if (!info.isShowColumnStripes()) {
                return null;
            }
            DifferentialStyleProvider c1Style = info.getStyle().getStyle(firstColumnStripe);
            DifferentialStyleProvider c2Style = info.getStyle().getStyle(secondColumnStripe);
            int c1Stripe = c1Style == null ? 1 : Math.max(1, c1Style.getStripeSize());
            int c2Stripe = c2Style == null ? 1 : Math.max(1, c2Style.getStripeSize());
            int firstStart = table.getStartColIndex();
            int secondStart = firstStart + c1Stripe;
            int c = cell.getColumnIndex();
            while (firstStart <= c) {
                if (c >= secondStart && c <= (secondStart + c2Stripe) - 1) {
                    return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), secondStart, (secondStart + c2Stripe) - 1);
                }
                firstStart = secondStart + c2Stripe;
                secondStart = firstStart + c1Stripe;
            }
            return null;
        }
    },
    firstRowStripe { // from class: org.apache.poi.ss.usermodel.TableStyleType.4
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            TableStyleInfo info = table.getStyle();
            if (!info.isShowRowStripes()) {
                return null;
            }
            DifferentialStyleProvider c1Style = info.getStyle().getStyle(firstRowStripe);
            DifferentialStyleProvider c2Style = info.getStyle().getStyle(secondRowStripe);
            int c1Stripe = c1Style == null ? 1 : Math.max(1, c1Style.getStripeSize());
            int c2Stripe = c2Style != null ? Math.max(1, c2Style.getStripeSize()) : 1;
            int firstStart = table.getStartRowIndex() + table.getHeaderRowCount();
            int secondStart = firstStart + c1Stripe;
            int c = cell.getRowIndex();
            while (firstStart <= c) {
                if (c >= firstStart && c <= secondStart - 1) {
                    return new CellRangeAddress(firstStart, secondStart - 1, table.getStartColIndex(), table.getEndColIndex());
                }
                firstStart = secondStart + c2Stripe;
                secondStart = firstStart + c1Stripe;
            }
            return null;
        }
    },
    secondRowStripe { // from class: org.apache.poi.ss.usermodel.TableStyleType.5
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            TableStyleInfo info = table.getStyle();
            if (!info.isShowRowStripes()) {
                return null;
            }
            DifferentialStyleProvider c1Style = info.getStyle().getStyle(firstRowStripe);
            DifferentialStyleProvider c2Style = info.getStyle().getStyle(secondRowStripe);
            int c1Stripe = c1Style == null ? 1 : Math.max(1, c1Style.getStripeSize());
            int c2Stripe = c2Style == null ? 1 : Math.max(1, c2Style.getStripeSize());
            int firstStart = table.getStartRowIndex() + table.getHeaderRowCount();
            int secondStart = firstStart + c1Stripe;
            int c = cell.getRowIndex();
            while (firstStart <= c) {
                if (c >= secondStart && c <= (secondStart + c2Stripe) - 1) {
                    return new CellRangeAddress(secondStart, (secondStart + c2Stripe) - 1, table.getStartColIndex(), table.getEndColIndex());
                }
                firstStart = secondStart + c2Stripe;
                secondStart = firstStart + c1Stripe;
            }
            return null;
        }
    },
    lastColumn { // from class: org.apache.poi.ss.usermodel.TableStyleType.6
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getStyle().isShowLastColumn()) {
                return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getEndColIndex(), table.getEndColIndex());
            }
            return null;
        }
    },
    firstColumn { // from class: org.apache.poi.ss.usermodel.TableStyleType.7
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getStyle().isShowFirstColumn()) {
                return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getStartColIndex(), table.getStartColIndex());
            }
            return null;
        }
    },
    headerRow { // from class: org.apache.poi.ss.usermodel.TableStyleType.8
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), (table.getStartRowIndex() + table.getHeaderRowCount()) - 1, table.getStartColIndex(), table.getEndColIndex());
        }
    },
    totalRow { // from class: org.apache.poi.ss.usermodel.TableStyleType.9
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress((table.getEndRowIndex() - table.getTotalsRowCount()) + 1, table.getEndRowIndex(), table.getStartColIndex(), table.getEndColIndex());
        }
    },
    firstHeaderCell { // from class: org.apache.poi.ss.usermodel.TableStyleType.10
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getStartRowIndex(), table.getStartColIndex(), table.getStartColIndex());
        }
    },
    lastHeaderCell { // from class: org.apache.poi.ss.usermodel.TableStyleType.11
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getStartRowIndex(), table.getEndColIndex(), table.getEndColIndex());
        }
    },
    firstTotalCell { // from class: org.apache.poi.ss.usermodel.TableStyleType.12
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress((table.getEndRowIndex() - table.getTotalsRowCount()) + 1, table.getEndRowIndex(), table.getStartColIndex(), table.getStartColIndex());
        }
    },
    lastTotalCell { // from class: org.apache.poi.ss.usermodel.TableStyleType.13
        @Override // org.apache.poi.ss.usermodel.TableStyleType
        CellRangeAddressBase getRange(Table table, Cell cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress((table.getEndRowIndex() - table.getTotalsRowCount()) + 1, table.getEndRowIndex(), table.getEndColIndex(), table.getEndColIndex());
        }
    },
    firstSubtotalColumn,
    secondSubtotalColumn,
    thirdSubtotalColumn,
    blankRow,
    firstSubtotalRow,
    secondSubtotalRow,
    thirdSubtotalRow,
    firstColumnSubheading,
    secondColumnSubheading,
    thirdColumnSubheading,
    firstRowSubheading,
    secondRowSubheading,
    thirdRowSubheading;

    public CellRangeAddressBase appliesTo(Table table, Cell cell) {
        CellRangeAddressBase range;
        if (table == null || cell == null || !cell.getSheet().getSheetName().equals(table.getSheetName()) || !table.contains(cell) || (range = getRange(table, cell)) == null || !range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
            return null;
        }
        return range;
    }

    CellRangeAddressBase getRange(Table table, Cell cell) {
        return null;
    }
}
