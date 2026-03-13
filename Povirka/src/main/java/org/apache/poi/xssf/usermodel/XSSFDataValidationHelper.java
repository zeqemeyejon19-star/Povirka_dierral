package org.apache.poi.xssf.usermodel;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationErrorStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFDataValidationHelper implements DataValidationHelper {
    public XSSFDataValidationHelper(XSSFSheet xssfSheet) {
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createDateConstraint(int operatorType, String formula1, String formula2, String dateFormat) {
        return new XSSFDataValidationConstraint(4, operatorType, formula1, formula2);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createDecimalConstraint(int operatorType, String formula1, String formula2) {
        return new XSSFDataValidationConstraint(2, operatorType, formula1, formula2);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createExplicitListConstraint(String[] listOfValues) {
        return new XSSFDataValidationConstraint(listOfValues);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createFormulaListConstraint(String listFormula) {
        return new XSSFDataValidationConstraint(3, listFormula);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createNumericConstraint(int validationType, int operatorType, String formula1, String formula2) {
        if (validationType == 1) {
            return createIntegerConstraint(operatorType, formula1, formula2);
        }
        if (validationType == 2) {
            return createDecimalConstraint(operatorType, formula1, formula2);
        }
        if (validationType == 6) {
            return createTextLengthConstraint(operatorType, formula1, formula2);
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createIntegerConstraint(int operatorType, String formula1, String formula2) {
        return new XSSFDataValidationConstraint(1, operatorType, formula1, formula2);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createTextLengthConstraint(int operatorType, String formula1, String formula2) {
        return new XSSFDataValidationConstraint(6, operatorType, formula1, formula2);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createTimeConstraint(int operatorType, String formula1, String formula2) {
        return new XSSFDataValidationConstraint(5, operatorType, formula1, formula2);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidationConstraint createCustomConstraint(String formula) {
        return new XSSFDataValidationConstraint(7, formula);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidationHelper
    public DataValidation createValidation(DataValidationConstraint constraint, CellRangeAddressList cellRangeAddressList) {
        XSSFDataValidationConstraint dataValidationConstraint = (XSSFDataValidationConstraint) constraint;
        CTDataValidation newDataValidation = CTDataValidation.Factory.newInstance();
        int validationType = constraint.getValidationType();
        switch (validationType) {
            case 0:
                newDataValidation.setType(STDataValidationType.NONE);
                break;
            case 1:
                newDataValidation.setType(STDataValidationType.WHOLE);
                break;
            case 2:
                newDataValidation.setType(STDataValidationType.DECIMAL);
                break;
            case 3:
                newDataValidation.setType(STDataValidationType.LIST);
                newDataValidation.setFormula1(constraint.getFormula1());
                break;
            case 4:
                newDataValidation.setType(STDataValidationType.DATE);
                break;
            case 5:
                newDataValidation.setType(STDataValidationType.TIME);
                break;
            case 6:
                newDataValidation.setType(STDataValidationType.TEXT_LENGTH);
                break;
            case 7:
                newDataValidation.setType(STDataValidationType.CUSTOM);
                break;
            default:
                newDataValidation.setType(STDataValidationType.NONE);
                break;
        }
        if (validationType != 0 && validationType != 3) {
            STDataValidationOperator.Enum op = XSSFDataValidation.operatorTypeMappings.get(Integer.valueOf(constraint.getOperator()));
            if (op != null) {
                newDataValidation.setOperator(op);
            }
            if (constraint.getFormula1() != null) {
                newDataValidation.setFormula1(constraint.getFormula1());
            }
            if (constraint.getFormula2() != null) {
                newDataValidation.setFormula2(constraint.getFormula2());
            }
        }
        CellRangeAddress[] cellRangeAddresses = cellRangeAddressList.getCellRangeAddresses();
        List<String> sqref = new ArrayList<>();
        for (CellRangeAddress cellRangeAddress : cellRangeAddresses) {
            sqref.add(cellRangeAddress.formatAsString());
        }
        newDataValidation.setSqref(sqref);
        newDataValidation.setAllowBlank(true);
        newDataValidation.setErrorStyle(STDataValidationErrorStyle.STOP);
        return new XSSFDataValidation(dataValidationConstraint, cellRangeAddressList, newDataValidation);
    }
}
