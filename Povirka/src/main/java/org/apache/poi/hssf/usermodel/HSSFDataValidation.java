package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFDataValidation implements DataValidation {
    private DVConstraint _constraint;
    private String _error_text;
    private String _error_title;
    private String _prompt_text;
    private String _prompt_title;
    private CellRangeAddressList _regions;
    private int _errorStyle = 0;
    private boolean _emptyCellAllowed = true;
    private boolean _suppress_dropdown_arrow = false;
    private boolean _showPromptBox = true;
    private boolean _showErrorBox = true;

    public HSSFDataValidation(CellRangeAddressList regions, DataValidationConstraint constraint) {
        this._regions = regions;
        this._constraint = (DVConstraint) constraint;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public DataValidationConstraint getValidationConstraint() {
        return this._constraint;
    }

    public DVConstraint getConstraint() {
        return this._constraint;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public CellRangeAddressList getRegions() {
        return this._regions;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void setErrorStyle(int error_style) {
        this._errorStyle = error_style;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public int getErrorStyle() {
        return this._errorStyle;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void setEmptyCellAllowed(boolean allowed) {
        this._emptyCellAllowed = allowed;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public boolean getEmptyCellAllowed() {
        return this._emptyCellAllowed;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void setSuppressDropDownArrow(boolean suppress) {
        this._suppress_dropdown_arrow = suppress;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public boolean getSuppressDropDownArrow() {
        if (this._constraint.getValidationType() == 3) {
            return this._suppress_dropdown_arrow;
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void setShowPromptBox(boolean show) {
        this._showPromptBox = show;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public boolean getShowPromptBox() {
        return this._showPromptBox;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void setShowErrorBox(boolean show) {
        this._showErrorBox = show;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public boolean getShowErrorBox() {
        return this._showErrorBox;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void createPromptBox(String title, String text) {
        if (title != null && title.length() > 32) {
            throw new IllegalStateException("Prompt-title cannot be longer than 32 characters, but had: " + title);
        }
        if (text != null && text.length() > 255) {
            throw new IllegalStateException("Prompt-text cannot be longer than 255 characters, but had: " + text);
        }
        this._prompt_title = title;
        this._prompt_text = text;
        setShowPromptBox(true);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public String getPromptBoxTitle() {
        return this._prompt_title;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public String getPromptBoxText() {
        return this._prompt_text;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public void createErrorBox(String title, String text) {
        if (title != null && title.length() > 32) {
            throw new IllegalStateException("Error-title cannot be longer than 32 characters, but had: " + title);
        }
        if (text != null && text.length() > 255) {
            throw new IllegalStateException("Error-text cannot be longer than 255 characters, but had: " + text);
        }
        this._error_title = title;
        this._error_text = text;
        setShowErrorBox(true);
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public String getErrorBoxTitle() {
        return this._error_title;
    }

    @Override // org.apache.poi.ss.usermodel.DataValidation
    public String getErrorBoxText() {
        return this._error_text;
    }

    public DVRecord createDVRecord(HSSFSheet sheet) {
        DVConstraint.FormulaPair fp = this._constraint.createFormulas(sheet);
        return new DVRecord(this._constraint.getValidationType(), this._constraint.getOperator(), this._errorStyle, this._emptyCellAllowed, getSuppressDropDownArrow(), this._constraint.getValidationType() == 3 && this._constraint.getExplicitListValues() != null, this._showPromptBox, this._prompt_title, this._prompt_text, this._showErrorBox, this._error_title, this._error_text, fp.getFormula1(), fp.getFormula2(), this._regions);
    }
}
