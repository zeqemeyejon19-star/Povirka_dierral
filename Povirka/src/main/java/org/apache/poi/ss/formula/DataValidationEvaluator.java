package org.apache.poi.ss.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.SheetUtil;

/* JADX INFO: loaded from: classes.dex */
public class DataValidationEvaluator {
    private final Map<String, List<? extends DataValidation>> validations = new HashMap();
    private final Workbook workbook;
    private final WorkbookEvaluator workbookEvaluator;

    public enum OperatorEnum {
        BETWEEN { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.1
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) >= 0 && cellValue.compareTo(v2) <= 0;
            }
        },
        NOT_BETWEEN { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.2
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) < 0 || cellValue.compareTo(v2) > 0;
            }
        },
        EQUAL { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.3
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) == 0;
            }
        },
        NOT_EQUAL { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.4
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) != 0;
            }
        },
        GREATER_THAN { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.5
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) > 0;
            }
        },
        LESS_THAN { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.6
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) < 0;
            }
        },
        GREATER_OR_EQUAL { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.7
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) >= 0;
            }
        },
        LESS_OR_EQUAL { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum.8
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.OperatorEnum
            public boolean isValid(Double cellValue, Double v1, Double v2) {
                return cellValue.compareTo(v1) <= 0;
            }
        };

        public static final OperatorEnum IGNORED = BETWEEN;

        public abstract boolean isValid(Double d, Double d2, Double d3);
    }

    public DataValidationEvaluator(Workbook wb, WorkbookEvaluatorProvider provider) {
        this.workbook = wb;
        this.workbookEvaluator = provider._getWorkbookEvaluator();
    }

    protected WorkbookEvaluator getWorkbookEvaluator() {
        return this.workbookEvaluator;
    }

    public void clearAllCachedValues() {
        this.validations.clear();
    }

    private List<? extends DataValidation> getValidations(Sheet sheet) {
        List<? extends DataValidation> dvs = this.validations.get(sheet.getSheetName());
        if (dvs == null && !this.validations.containsKey(sheet.getSheetName())) {
            List<? extends DataValidation> dvs2 = sheet.getDataValidations();
            this.validations.put(sheet.getSheetName(), dvs2);
            return dvs2;
        }
        return dvs;
    }

    public DataValidation getValidationForCell(CellReference cell) {
        DataValidationContext vc = getValidationContextForCell(cell);
        if (vc == null) {
            return null;
        }
        return vc.getValidation();
    }

    public DataValidationContext getValidationContextForCell(CellReference cell) {
        List<? extends DataValidation> dataValidations;
        DataValidation dv;
        CellRangeAddressList regions;
        Sheet sheet = this.workbook.getSheet(cell.getSheetName());
        if (sheet == null || (dataValidations = getValidations(sheet)) == null) {
            return null;
        }
        Iterator<? extends DataValidation> it = dataValidations.iterator();
        while (it.hasNext() && (regions = (dv = it.next()).getRegions()) != null) {
            CellRangeAddressBase[] arr$ = regions.getCellRangeAddresses();
            for (CellRangeAddressBase range : arr$) {
                if (range.isInRange(cell)) {
                    return new DataValidationContext(dv, this, range, cell);
                }
            }
        }
        return null;
    }

    public List<ValueEval> getValidationValuesForCell(CellReference cell) {
        DataValidationContext context = getValidationContextForCell(cell);
        if (context == null) {
            return null;
        }
        return getValidationValuesForConstraint(context);
    }

    protected static List<ValueEval> getValidationValuesForConstraint(DataValidationContext context) {
        DataValidationConstraint val = context.getValidation().getValidationConstraint();
        if (val.getValidationType() != 3) {
            return null;
        }
        String formula = val.getFormula1();
        List<ValueEval> values = new ArrayList<>();
        if (val.getExplicitListValues() != null && val.getExplicitListValues().length > 0) {
            String[] arr$ = val.getExplicitListValues();
            for (String s : arr$) {
                if (s != null) {
                    values.add(new StringEval(s));
                }
            }
        } else if (formula != null) {
            ValueEval eval = context.getEvaluator().getWorkbookEvaluator().evaluateList(formula, context.getTarget(), context.getRegion());
            if (eval instanceof TwoDEval) {
                TwoDEval twod = (TwoDEval) eval;
                for (int i = 0; i < twod.getHeight(); i++) {
                    ValueEval cellValue = twod.getValue(i, 0);
                    values.add(cellValue);
                }
            }
        }
        return Collections.unmodifiableList(values);
    }

    public boolean isValidCell(CellReference cellRef) {
        DataValidationContext context = getValidationContextForCell(cellRef);
        if (context == null) {
            return true;
        }
        Cell cell = SheetUtil.getCell(this.workbook.getSheet(cellRef.getSheetName()), cellRef.getRow(), cellRef.getCol());
        if (cell == null || isType(cell, CellType.BLANK) || (isType(cell, CellType.STRING) && (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()))) {
            return context.getValidation().getEmptyCellAllowed();
        }
        return ValidationEnum.isValid(cell, context);
    }

    public static boolean isType(Cell cell, CellType type) {
        CellType cellType = cell.getCellTypeEnum();
        return cellType == type || (cellType == CellType.FORMULA && cell.getCachedFormulaResultTypeEnum() == type);
    }

    public enum ValidationEnum {
        ANY { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum.1
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum
            public boolean isValidValue(Cell cell, DataValidationContext context) {
                return true;
            }
        },
        INTEGER { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum.2
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum
            public boolean isValidValue(Cell cell, DataValidationContext context) {
                if (!super.isValidValue(cell, context)) {
                    return false;
                }
                double value = cell.getNumericCellValue();
                return Double.valueOf(value).compareTo(Double.valueOf((double) ((int) value))) == 0;
            }
        },
        DECIMAL,
        LIST { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum.3
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum
            public boolean isValidValue(Cell cell, DataValidationContext context) {
                List<ValueEval> valueList = DataValidationEvaluator.getValidationValuesForConstraint(context);
                if (valueList == null) {
                    return true;
                }
                for (ValueEval listVal : valueList) {
                    ValueEval comp = listVal instanceof RefEval ? ((RefEval) listVal).getInnerValueEval(context.getSheetIndex()) : listVal;
                    if (comp instanceof BlankEval) {
                        return true;
                    }
                    if (!(comp instanceof ErrorEval)) {
                        if (comp instanceof BoolEval) {
                            if (DataValidationEvaluator.isType(cell, CellType.BOOLEAN) && ((BoolEval) comp).getBooleanValue() == cell.getBooleanCellValue()) {
                                return true;
                            }
                        } else if (comp instanceof NumberEval) {
                            if (DataValidationEvaluator.isType(cell, CellType.NUMERIC) && ((NumberEval) comp).getNumberValue() == cell.getNumericCellValue()) {
                                return true;
                            }
                        } else if ((comp instanceof StringEval) && DataValidationEvaluator.isType(cell, CellType.STRING) && ((StringEval) comp).getStringValue().equalsIgnoreCase(cell.getStringCellValue())) {
                            return true;
                        }
                    }
                }
                return false;
            }
        },
        DATE,
        TIME,
        TEXT_LENGTH { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum.4
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum
            public boolean isValidValue(Cell cell, DataValidationContext context) {
                if (!DataValidationEvaluator.isType(cell, CellType.STRING)) {
                    return false;
                }
                String v = cell.getStringCellValue();
                return isValidNumericValue(Double.valueOf(v.length()), context);
            }
        },
        FORMULA { // from class: org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum.5
            @Override // org.apache.poi.ss.formula.DataValidationEvaluator.ValidationEnum
            public boolean isValidValue(Cell cell, DataValidationContext context) {
                ValueEval comp = context.getEvaluator().getWorkbookEvaluator().evaluate(context.getFormula1(), context.getTarget(), context.getRegion());
                if (comp instanceof RefEval) {
                    comp = ((RefEval) comp).getInnerValueEval(((RefEval) comp).getFirstSheetIndex());
                }
                if (comp instanceof BlankEval) {
                    return true;
                }
                if (comp instanceof ErrorEval) {
                    return false;
                }
                if (comp instanceof BoolEval) {
                    return ((BoolEval) comp).getBooleanValue();
                }
                return (comp instanceof NumberEval) && ((NumberEval) comp).getNumberValue() != 0.0d;
            }
        };

        public boolean isValidValue(Cell cell, DataValidationContext context) {
            return isValidNumericCell(cell, context);
        }

        protected boolean isValidNumericCell(Cell cell, DataValidationContext context) {
            if (!DataValidationEvaluator.isType(cell, CellType.NUMERIC)) {
                return false;
            }
            Double value = Double.valueOf(cell.getNumericCellValue());
            return isValidNumericValue(value, context);
        }

        protected boolean isValidNumericValue(Double value, DataValidationContext context) {
            try {
                Double t1 = evalOrConstant(context.getFormula1(), context);
                if (t1 == null) {
                    return true;
                }
                Double t2 = null;
                if ((context.getOperator() == 0 || context.getOperator() == 1) && (t2 = evalOrConstant(context.getFormula2(), context)) == null) {
                    return true;
                }
                return OperatorEnum.values()[context.getOperator()].isValid(value, t1, t2);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private Double evalOrConstant(String formula, DataValidationContext context) throws NumberFormatException {
            if (formula == null || formula.trim().isEmpty()) {
                return null;
            }
            try {
                return Double.valueOf(formula);
            } catch (NumberFormatException e) {
                ValueEval eval = context.getEvaluator().getWorkbookEvaluator().evaluate(formula, context.getTarget(), context.getRegion());
                if (eval instanceof RefEval) {
                    eval = ((RefEval) eval).getInnerValueEval(((RefEval) eval).getFirstSheetIndex());
                }
                if (eval instanceof BlankEval) {
                    return null;
                }
                if (eval instanceof NumberEval) {
                    return Double.valueOf(((NumberEval) eval).getNumberValue());
                }
                if (eval instanceof StringEval) {
                    String value = ((StringEval) eval).getStringValue();
                    if (value == null || value.trim().isEmpty()) {
                        return null;
                    }
                    return Double.valueOf(value);
                }
                throw new NumberFormatException("Formula '" + formula + "' evaluates to something other than a number");
            }
        }

        public static boolean isValid(Cell cell, DataValidationContext context) {
            return values()[context.getValidation().getValidationConstraint().getValidationType()].isValidValue(cell, context);
        }
    }

    public static class DataValidationContext {
        private final DataValidation dv;
        private final DataValidationEvaluator dve;
        private final CellRangeAddressBase region;
        private final CellReference target;

        public DataValidationContext(DataValidation dv, DataValidationEvaluator dve, CellRangeAddressBase region, CellReference target) {
            this.dv = dv;
            this.dve = dve;
            this.region = region;
            this.target = target;
        }

        public DataValidation getValidation() {
            return this.dv;
        }

        public DataValidationEvaluator getEvaluator() {
            return this.dve;
        }

        public CellRangeAddressBase getRegion() {
            return this.region;
        }

        public CellReference getTarget() {
            return this.target;
        }

        public int getOffsetColumns() {
            return this.target.getCol() - this.region.getFirstColumn();
        }

        public int getOffsetRows() {
            return this.target.getRow() - this.region.getFirstRow();
        }

        public int getSheetIndex() {
            return this.dve.getWorkbookEvaluator().getSheetIndex(this.target.getSheetName());
        }

        public String getFormula1() {
            return this.dv.getValidationConstraint().getFormula1();
        }

        public String getFormula2() {
            return this.dv.getValidationConstraint().getFormula2();
        }

        public int getOperator() {
            return this.dv.getValidationConstraint().getOperator();
        }
    }
}
