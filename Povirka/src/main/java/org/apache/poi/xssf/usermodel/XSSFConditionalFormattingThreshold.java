package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFConditionalFormattingThreshold implements ConditionalFormattingThreshold {
    private CTCfvo cfvo;

    protected XSSFConditionalFormattingThreshold(CTCfvo cfvo) {
        this.cfvo = cfvo;
    }

    protected CTCfvo getCTCfvo() {
        return this.cfvo;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public ConditionalFormattingThreshold.RangeType getRangeType() {
        return ConditionalFormattingThreshold.RangeType.byName(this.cfvo.getType().toString());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public void setRangeType(ConditionalFormattingThreshold.RangeType type) {
        STCfvoType.Enum xtype = STCfvoType.Enum.forString(type.name);
        this.cfvo.setType(xtype);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public String getFormula() {
        if (this.cfvo.getType() == STCfvoType.FORMULA) {
            return this.cfvo.getVal();
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public void setFormula(String formula) {
        this.cfvo.setVal(formula);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public Double getValue() {
        if (this.cfvo.getType() == STCfvoType.FORMULA || this.cfvo.getType() == STCfvoType.MIN || this.cfvo.getType() == STCfvoType.MAX || !this.cfvo.isSetVal()) {
            return null;
        }
        return Double.valueOf(Double.parseDouble(this.cfvo.getVal()));
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingThreshold
    public void setValue(Double value) {
        if (value == null) {
            this.cfvo.unsetVal();
        } else {
            this.cfvo.setVal(value.toString());
        }
    }
}
