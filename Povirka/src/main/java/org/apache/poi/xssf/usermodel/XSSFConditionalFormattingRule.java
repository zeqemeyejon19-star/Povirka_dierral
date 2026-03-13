package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.ConditionFilterData;
import org.apache.poi.ss.usermodel.ConditionFilterType;
import org.apache.poi.ss.usermodel.ConditionType;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.xssf.model.StylesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfvo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfvoType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STConditionalFormattingOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STIconSetType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFConditionalFormattingRule implements ConditionalFormattingRule {
    private final CTCfRule _cfRule;
    private XSSFSheet _sh;
    private static Map<STCfType.Enum, ConditionType> typeLookup = new HashMap();
    private static Map<STCfType.Enum, ConditionFilterType> filterTypeLookup = new HashMap();

    static {
        typeLookup.put(STCfType.CELL_IS, ConditionType.CELL_VALUE_IS);
        typeLookup.put(STCfType.EXPRESSION, ConditionType.FORMULA);
        typeLookup.put(STCfType.COLOR_SCALE, ConditionType.COLOR_SCALE);
        typeLookup.put(STCfType.DATA_BAR, ConditionType.DATA_BAR);
        typeLookup.put(STCfType.ICON_SET, ConditionType.ICON_SET);
        typeLookup.put(STCfType.TOP_10, ConditionType.FILTER);
        typeLookup.put(STCfType.UNIQUE_VALUES, ConditionType.FILTER);
        typeLookup.put(STCfType.DUPLICATE_VALUES, ConditionType.FILTER);
        typeLookup.put(STCfType.CONTAINS_TEXT, ConditionType.FILTER);
        typeLookup.put(STCfType.NOT_CONTAINS_TEXT, ConditionType.FILTER);
        typeLookup.put(STCfType.BEGINS_WITH, ConditionType.FILTER);
        typeLookup.put(STCfType.ENDS_WITH, ConditionType.FILTER);
        typeLookup.put(STCfType.CONTAINS_BLANKS, ConditionType.FILTER);
        typeLookup.put(STCfType.NOT_CONTAINS_BLANKS, ConditionType.FILTER);
        typeLookup.put(STCfType.CONTAINS_ERRORS, ConditionType.FILTER);
        typeLookup.put(STCfType.NOT_CONTAINS_ERRORS, ConditionType.FILTER);
        typeLookup.put(STCfType.TIME_PERIOD, ConditionType.FILTER);
        typeLookup.put(STCfType.ABOVE_AVERAGE, ConditionType.FILTER);
        filterTypeLookup.put(STCfType.TOP_10, ConditionFilterType.TOP_10);
        filterTypeLookup.put(STCfType.UNIQUE_VALUES, ConditionFilterType.UNIQUE_VALUES);
        filterTypeLookup.put(STCfType.DUPLICATE_VALUES, ConditionFilterType.DUPLICATE_VALUES);
        filterTypeLookup.put(STCfType.CONTAINS_TEXT, ConditionFilterType.CONTAINS_TEXT);
        filterTypeLookup.put(STCfType.NOT_CONTAINS_TEXT, ConditionFilterType.NOT_CONTAINS_TEXT);
        filterTypeLookup.put(STCfType.BEGINS_WITH, ConditionFilterType.BEGINS_WITH);
        filterTypeLookup.put(STCfType.ENDS_WITH, ConditionFilterType.ENDS_WITH);
        filterTypeLookup.put(STCfType.CONTAINS_BLANKS, ConditionFilterType.CONTAINS_BLANKS);
        filterTypeLookup.put(STCfType.NOT_CONTAINS_BLANKS, ConditionFilterType.NOT_CONTAINS_BLANKS);
        filterTypeLookup.put(STCfType.CONTAINS_ERRORS, ConditionFilterType.CONTAINS_ERRORS);
        filterTypeLookup.put(STCfType.NOT_CONTAINS_ERRORS, ConditionFilterType.NOT_CONTAINS_ERRORS);
        filterTypeLookup.put(STCfType.TIME_PERIOD, ConditionFilterType.TIME_PERIOD);
        filterTypeLookup.put(STCfType.ABOVE_AVERAGE, ConditionFilterType.ABOVE_AVERAGE);
    }

    XSSFConditionalFormattingRule(XSSFSheet sh) {
        this._cfRule = CTCfRule.Factory.newInstance();
        this._sh = sh;
    }

    XSSFConditionalFormattingRule(XSSFSheet sh, CTCfRule cfRule) {
        this._cfRule = cfRule;
        this._sh = sh;
    }

    CTCfRule getCTCfRule() {
        return this._cfRule;
    }

    CTDxf getDxf(boolean create) {
        StylesTable styles = this._sh.getWorkbook().getStylesSource();
        CTDxf dxf = null;
        if (styles._getDXfsSize() > 0 && this._cfRule.isSetDxfId()) {
            int dxfId = (int) this._cfRule.getDxfId();
            dxf = styles.getDxfAt(dxfId);
        }
        if (create && dxf == null) {
            CTDxf dxf2 = CTDxf.Factory.newInstance();
            int dxfId2 = styles.putDxf(dxf2);
            this._cfRule.setDxfId(dxfId2 - 1);
            return dxf2;
        }
        return dxf;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public int getPriority() {
        int priority = this._cfRule.getPriority();
        if (priority >= 1) {
            return priority;
        }
        return 0;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public boolean getStopIfTrue() {
        return this._cfRule.getStopIfTrue();
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFBorderFormatting createBorderFormatting() {
        CTBorder border;
        CTDxf dxf = getDxf(true);
        if (!dxf.isSetBorder()) {
            border = dxf.addNewBorder();
        } else {
            border = dxf.getBorder();
        }
        return new XSSFBorderFormatting(border, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public XSSFBorderFormatting getBorderFormatting() {
        CTDxf dxf = getDxf(false);
        if (dxf == null || !dxf.isSetBorder()) {
            return null;
        }
        return new XSSFBorderFormatting(dxf.getBorder(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFFontFormatting createFontFormatting() {
        CTFont font;
        CTDxf dxf = getDxf(true);
        if (!dxf.isSetFont()) {
            font = dxf.addNewFont();
        } else {
            font = dxf.getFont();
        }
        return new XSSFFontFormatting(font, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public XSSFFontFormatting getFontFormatting() {
        CTDxf dxf = getDxf(false);
        if (dxf == null || !dxf.isSetFont()) {
            return null;
        }
        return new XSSFFontFormatting(dxf.getFont(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFPatternFormatting createPatternFormatting() {
        CTFill fill;
        CTDxf dxf = getDxf(true);
        if (!dxf.isSetFill()) {
            fill = dxf.addNewFill();
        } else {
            fill = dxf.getFill();
        }
        return new XSSFPatternFormatting(fill, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public XSSFPatternFormatting getPatternFormatting() {
        CTDxf dxf = getDxf(false);
        if (dxf == null || !dxf.isSetFill()) {
            return null;
        }
        return new XSSFPatternFormatting(dxf.getFill(), this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    public XSSFDataBarFormatting createDataBarFormatting(XSSFColor color) {
        CTDataBar bar;
        if (this._cfRule.isSetDataBar() && this._cfRule.getType() == STCfType.DATA_BAR) {
            return getDataBarFormatting();
        }
        this._cfRule.setType(STCfType.DATA_BAR);
        if (this._cfRule.isSetDataBar()) {
            bar = this._cfRule.getDataBar();
        } else {
            bar = this._cfRule.addNewDataBar();
        }
        bar.setColor(color.getCTColor());
        CTCfvo min = bar.addNewCfvo();
        min.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MIN.name));
        CTCfvo max = bar.addNewCfvo();
        max.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MAX.name));
        return new XSSFDataBarFormatting(bar, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFDataBarFormatting getDataBarFormatting() {
        if (this._cfRule.isSetDataBar()) {
            CTDataBar bar = this._cfRule.getDataBar();
            return new XSSFDataBarFormatting(bar, this._sh.getWorkbook().getStylesSource().getIndexedColors());
        }
        return null;
    }

    public XSSFIconMultiStateFormatting createMultiStateFormatting(IconMultiStateFormatting.IconSet iconSet) {
        CTIconSet icons;
        if (this._cfRule.isSetIconSet() && this._cfRule.getType() == STCfType.ICON_SET) {
            return getMultiStateFormatting();
        }
        this._cfRule.setType(STCfType.ICON_SET);
        if (this._cfRule.isSetIconSet()) {
            icons = this._cfRule.getIconSet();
        } else {
            icons = this._cfRule.addNewIconSet();
        }
        if (iconSet.name != null) {
            STIconSetType.Enum xIconSet = STIconSetType.Enum.forString(iconSet.name);
            icons.setIconSet(xIconSet);
        }
        int jump = 100 / iconSet.num;
        STCfvoType.Enum type = STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.PERCENT.name);
        for (int i = 0; i < iconSet.num; i++) {
            CTCfvo cfvo = icons.addNewCfvo();
            cfvo.setType(type);
            cfvo.setVal(Integer.toString(i * jump));
        }
        return new XSSFIconMultiStateFormatting(icons);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFIconMultiStateFormatting getMultiStateFormatting() {
        if (this._cfRule.isSetIconSet()) {
            CTIconSet icons = this._cfRule.getIconSet();
            return new XSSFIconMultiStateFormatting(icons);
        }
        return null;
    }

    public XSSFColorScaleFormatting createColorScaleFormatting() {
        CTColorScale scale;
        if (this._cfRule.isSetColorScale() && this._cfRule.getType() == STCfType.COLOR_SCALE) {
            return getColorScaleFormatting();
        }
        this._cfRule.setType(STCfType.COLOR_SCALE);
        if (this._cfRule.isSetColorScale()) {
            scale = this._cfRule.getColorScale();
        } else {
            scale = this._cfRule.addNewColorScale();
        }
        if (scale.sizeOfCfvoArray() == 0) {
            scale.addNewCfvo().setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MIN.name));
            CTCfvo cfvo = scale.addNewCfvo();
            cfvo.setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.PERCENTILE.name));
            cfvo.setVal("50");
            scale.addNewCfvo().setType(STCfvoType.Enum.forString(ConditionalFormattingThreshold.RangeType.MAX.name));
            for (int i = 0; i < 3; i++) {
                scale.addNewColor();
            }
        }
        return new XSSFColorScaleFormatting(scale, this._sh.getWorkbook().getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public XSSFColorScaleFormatting getColorScaleFormatting() {
        if (this._cfRule.isSetColorScale()) {
            CTColorScale scale = this._cfRule.getColorScale();
            return new XSSFColorScaleFormatting(scale, this._sh.getWorkbook().getStylesSource().getIndexedColors());
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule, org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public ExcelNumberFormat getNumberFormat() {
        CTDxf dxf = getDxf(false);
        if (dxf == null || !dxf.isSetNumFmt()) {
            return null;
        }
        CTNumFmt numFmt = dxf.getNumFmt();
        return new ExcelNumberFormat((int) numFmt.getNumFmtId(), numFmt.getFormatCode());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionType getConditionType() {
        return typeLookup.get(this._cfRule.getType());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionFilterType getConditionFilterType() {
        return filterTypeLookup.get(this._cfRule.getType());
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public ConditionFilterData getFilterConfiguration() {
        return new XSSFConditionFilterData(this._cfRule);
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public byte getComparisonOperation() {
        STConditionalFormattingOperator.Enum op = this._cfRule.getOperator();
        if (op == null) {
            return (byte) 0;
        }
        switch (op.intValue()) {
        }
        return (byte) 0;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public String getFormula1() {
        if (this._cfRule.sizeOfFormulaArray() > 0) {
            return this._cfRule.getFormulaArray(0);
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.ConditionalFormattingRule
    public String getFormula2() {
        if (this._cfRule.sizeOfFormulaArray() == 2) {
            return this._cfRule.getFormulaArray(1);
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public int getStripeSize() {
        return 0;
    }
}
