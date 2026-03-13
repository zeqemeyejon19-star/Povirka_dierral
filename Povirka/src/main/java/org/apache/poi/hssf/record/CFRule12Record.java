package org.apache.poi.hssf.record;

import java.util.Arrays;
import org.apache.poi.hssf.record.cf.ColorGradientFormatting;
import org.apache.poi.hssf.record.cf.ColorGradientThreshold;
import org.apache.poi.hssf.record.cf.DataBarFormatting;
import org.apache.poi.hssf.record.cf.DataBarThreshold;
import org.apache.poi.hssf.record.cf.IconMultiStateFormatting;
import org.apache.poi.hssf.record.cf.IconMultiStateThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.hssf.record.common.FtrHeader;
import org.apache.poi.hssf.record.common.FutureRecord;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class CFRule12Record extends CFRuleBase implements FutureRecord, Cloneable {
    public static final short sid = 2170;
    private ColorGradientFormatting color_gradient;
    private DataBarFormatting data_bar;
    private byte[] ext_formatting_data;
    private int ext_formatting_length;
    private byte ext_opts;
    private byte[] filter_data;
    private Formula formula_scale;
    private FtrHeader futureHeader;
    private IconMultiStateFormatting multistate;
    private int priority;
    private byte template_param_length;
    private byte[] template_params;
    private int template_type;

    private CFRule12Record(byte conditionType, byte comparisonOperation) {
        super(conditionType, comparisonOperation);
        setDefaults();
    }

    private CFRule12Record(byte conditionType, byte comparisonOperation, Ptg[] formula1, Ptg[] formula2, Ptg[] formulaScale) {
        super(conditionType, comparisonOperation, formula1, formula2);
        setDefaults();
        this.formula_scale = Formula.create(formulaScale);
    }

    private void setDefaults() {
        FtrHeader ftrHeader = new FtrHeader();
        this.futureHeader = ftrHeader;
        ftrHeader.setRecordType(sid);
        this.ext_formatting_length = 0;
        this.ext_formatting_data = new byte[4];
        this.formula_scale = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        this.ext_opts = (byte) 0;
        this.priority = 0;
        this.template_type = getConditionType();
        this.template_param_length = UnionPtg.sid;
        this.template_params = new byte[16];
    }

    public static CFRule12Record create(HSSFSheet sheet, String formulaText) {
        Ptg[] formula1 = parseFormula(formulaText, sheet);
        return new CFRule12Record((byte) 2, (byte) 0, formula1, null, null);
    }

    public static CFRule12Record create(HSSFSheet sheet, byte comparisonOperation, String formulaText1, String formulaText2) {
        Ptg[] formula1 = parseFormula(formulaText1, sheet);
        Ptg[] formula2 = parseFormula(formulaText2, sheet);
        return new CFRule12Record((byte) 1, comparisonOperation, formula1, formula2, null);
    }

    public static CFRule12Record create(HSSFSheet sheet, byte comparisonOperation, String formulaText1, String formulaText2, String formulaTextScale) {
        Ptg[] formula1 = parseFormula(formulaText1, sheet);
        Ptg[] formula2 = parseFormula(formulaText2, sheet);
        Ptg[] formula3 = parseFormula(formulaTextScale, sheet);
        return new CFRule12Record((byte) 1, comparisonOperation, formula1, formula2, formula3);
    }

    public static CFRule12Record create(HSSFSheet sheet, ExtendedColor color) {
        CFRule12Record r = new CFRule12Record((byte) 4, (byte) 0);
        DataBarFormatting dbf = r.createDataBarFormatting();
        dbf.setColor(color);
        dbf.setPercentMin((byte) 0);
        dbf.setPercentMax((byte) 100);
        DataBarThreshold min = new DataBarThreshold();
        min.setType(ConditionalFormattingThreshold.RangeType.MIN.id);
        dbf.setThresholdMin(min);
        DataBarThreshold max = new DataBarThreshold();
        max.setType(ConditionalFormattingThreshold.RangeType.MAX.id);
        dbf.setThresholdMax(max);
        return r;
    }

    public static CFRule12Record create(HSSFSheet sheet, IconMultiStateFormatting.IconSet iconSet) {
        Threshold[] ts = new Threshold[iconSet.num];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new IconMultiStateThreshold();
        }
        CFRule12Record r = new CFRule12Record((byte) 6, (byte) 0);
        org.apache.poi.hssf.record.cf.IconMultiStateFormatting imf = r.createMultiStateFormatting();
        imf.setIconSet(iconSet);
        imf.setThresholds(ts);
        return r;
    }

    public static CFRule12Record createColorScale(HSSFSheet sheet) {
        ExtendedColor[] colors = new ExtendedColor[3];
        ColorGradientThreshold[] ts = new ColorGradientThreshold[3];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = new ColorGradientThreshold();
            colors[i] = new ExtendedColor();
        }
        CFRule12Record r = new CFRule12Record((byte) 3, (byte) 0);
        ColorGradientFormatting cgf = r.createColorGradientFormatting();
        cgf.setNumControlPoints(3);
        cgf.setThresholds(ts);
        cgf.setColors(colors);
        return r;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public CFRule12Record(RecordInputStream in) {
        this.futureHeader = new FtrHeader(in);
        setConditionType(in.readByte());
        setComparisonOperation(in.readByte());
        int field_3_formula1_len = in.readUShort();
        int field_4_formula2_len = in.readUShort();
        int i = in.readInt();
        this.ext_formatting_length = i;
        this.ext_formatting_data = new byte[0];
        if (i == 0) {
            in.readUShort();
        } else {
            int len = readFormatOptions(in);
            int i2 = this.ext_formatting_length;
            if (len < i2) {
                byte[] bArr = new byte[i2 - len];
                this.ext_formatting_data = bArr;
                in.readFully(bArr);
            }
        }
        setFormula1(Formula.read(field_3_formula1_len, in));
        setFormula2(Formula.read(field_4_formula2_len, in));
        int formula_scale_len = in.readUShort();
        this.formula_scale = Formula.read(formula_scale_len, in);
        this.ext_opts = in.readByte();
        this.priority = in.readUShort();
        this.template_type = in.readUShort();
        int i3 = in.readByte();
        this.template_param_length = i3;
        if (i3 != 0 && i3 != 16) {
            logger.log(5, "CF Rule v12 template params length should be 0 or 16, found " + ((int) this.template_param_length));
            in.readRemainder();
        } else {
            byte[] bArr2 = new byte[i3];
            this.template_params = bArr2;
            in.readFully(bArr2);
        }
        byte type = getConditionType();
        if (type == 3) {
            this.color_gradient = new ColorGradientFormatting(in);
            return;
        }
        if (type == 4) {
            this.data_bar = new DataBarFormatting(in);
        } else if (type == 5) {
            this.filter_data = in.readRemainder();
        } else if (type == 6) {
            this.multistate = new org.apache.poi.hssf.record.cf.IconMultiStateFormatting(in);
        }
    }

    public boolean containsDataBarBlock() {
        return this.data_bar != null;
    }

    public DataBarFormatting getDataBarFormatting() {
        return this.data_bar;
    }

    public DataBarFormatting createDataBarFormatting() {
        DataBarFormatting dataBarFormatting = this.data_bar;
        if (dataBarFormatting != null) {
            return dataBarFormatting;
        }
        setConditionType((byte) 4);
        DataBarFormatting dataBarFormatting2 = new DataBarFormatting();
        this.data_bar = dataBarFormatting2;
        return dataBarFormatting2;
    }

    public boolean containsMultiStateBlock() {
        return this.multistate != null;
    }

    public org.apache.poi.hssf.record.cf.IconMultiStateFormatting getMultiStateFormatting() {
        return this.multistate;
    }

    public org.apache.poi.hssf.record.cf.IconMultiStateFormatting createMultiStateFormatting() {
        org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconMultiStateFormatting = this.multistate;
        if (iconMultiStateFormatting != null) {
            return iconMultiStateFormatting;
        }
        setConditionType((byte) 6);
        org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconMultiStateFormatting2 = new org.apache.poi.hssf.record.cf.IconMultiStateFormatting();
        this.multistate = iconMultiStateFormatting2;
        return iconMultiStateFormatting2;
    }

    public boolean containsColorGradientBlock() {
        return this.color_gradient != null;
    }

    public ColorGradientFormatting getColorGradientFormatting() {
        return this.color_gradient;
    }

    public ColorGradientFormatting createColorGradientFormatting() {
        ColorGradientFormatting colorGradientFormatting = this.color_gradient;
        if (colorGradientFormatting != null) {
            return colorGradientFormatting;
        }
        setConditionType((byte) 3);
        ColorGradientFormatting colorGradientFormatting2 = new ColorGradientFormatting();
        this.color_gradient = colorGradientFormatting2;
        return colorGradientFormatting2;
    }

    public Ptg[] getParsedExpressionScale() {
        return this.formula_scale.getTokens();
    }

    public void setParsedExpressionScale(Ptg[] ptgs) {
        this.formula_scale = Formula.create(ptgs);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        this.futureHeader.serialize(out);
        int formula1Len = getFormulaSize(getFormula1());
        int formula2Len = getFormulaSize(getFormula2());
        out.writeByte(getConditionType());
        out.writeByte(getComparisonOperation());
        out.writeShort(formula1Len);
        out.writeShort(formula2Len);
        int i = this.ext_formatting_length;
        if (i == 0) {
            out.writeInt(0);
            out.writeShort(0);
        } else {
            out.writeInt(i);
            serializeFormattingBlock(out);
            out.write(this.ext_formatting_data);
        }
        getFormula1().serializeTokens(out);
        getFormula2().serializeTokens(out);
        out.writeShort(getFormulaSize(this.formula_scale));
        this.formula_scale.serializeTokens(out);
        out.writeByte(this.ext_opts);
        out.writeShort(this.priority);
        out.writeShort(this.template_type);
        out.writeByte(this.template_param_length);
        out.write(this.template_params);
        byte type = getConditionType();
        if (type == 3) {
            this.color_gradient.serialize(out);
            return;
        }
        if (type == 4) {
            this.data_bar.serialize(out);
        } else if (type == 5) {
            out.write(this.filter_data);
        } else if (type == 6) {
            this.multistate.serialize(out);
        }
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        int len;
        int len2 = FtrHeader.getDataSize() + 6;
        if (this.ext_formatting_length == 0) {
            len = len2 + 6;
        } else {
            len = len2 + getFormattingBlockSize() + 4 + this.ext_formatting_data.length;
        }
        int len3 = len + getFormulaSize(getFormula1()) + getFormulaSize(getFormula2()) + getFormulaSize(this.formula_scale) + 2 + this.template_params.length + 6;
        byte type = getConditionType();
        if (type == 3) {
            return len3 + this.color_gradient.getDataLength();
        }
        if (type == 4) {
            return len3 + this.data_bar.getDataLength();
        }
        if (type == 5) {
            return len3 + this.filter_data.length;
        }
        if (type == 6) {
            return len3 + this.multistate.getDataLength();
        }
        return len3;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[CFRULE12]\n");
        buffer.append("    .condition_type=").append((int) getConditionType()).append("\n");
        buffer.append("    .dxfn12_length =0x").append(Integer.toHexString(this.ext_formatting_length)).append("\n");
        buffer.append("    .option_flags  =0x").append(Integer.toHexString(getOptions())).append("\n");
        if (containsFontFormattingBlock()) {
            buffer.append(this._fontFormatting).append("\n");
        }
        if (containsBorderFormattingBlock()) {
            buffer.append(this._borderFormatting).append("\n");
        }
        if (containsPatternFormattingBlock()) {
            buffer.append(this._patternFormatting).append("\n");
        }
        buffer.append("    .dxfn12_ext=").append(HexDump.toHex(this.ext_formatting_data)).append("\n");
        buffer.append("    .formula_1 =").append(Arrays.toString(getFormula1().getTokens())).append("\n");
        buffer.append("    .formula_2 =").append(Arrays.toString(getFormula2().getTokens())).append("\n");
        buffer.append("    .formula_S =").append(Arrays.toString(this.formula_scale.getTokens())).append("\n");
        buffer.append("    .ext_opts  =").append((int) this.ext_opts).append("\n");
        buffer.append("    .priority  =").append(this.priority).append("\n");
        buffer.append("    .template_type  =").append(this.template_type).append("\n");
        buffer.append("    .template_params=").append(HexDump.toHex(this.template_params)).append("\n");
        buffer.append("    .filter_data    =").append(HexDump.toHex(this.filter_data)).append("\n");
        ColorGradientFormatting colorGradientFormatting = this.color_gradient;
        if (colorGradientFormatting != null) {
            buffer.append(colorGradientFormatting);
        }
        org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconMultiStateFormatting = this.multistate;
        if (iconMultiStateFormatting != null) {
            buffer.append(iconMultiStateFormatting);
        }
        DataBarFormatting dataBarFormatting = this.data_bar;
        if (dataBarFormatting != null) {
            buffer.append(dataBarFormatting);
        }
        buffer.append("[/CFRULE12]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.CFRuleBase, org.apache.poi.hssf.record.Record
    public CFRule12Record clone() {
        CFRule12Record rec = new CFRule12Record(getConditionType(), getComparisonOperation());
        rec.futureHeader.setAssociatedRange(this.futureHeader.getAssociatedRange().copy());
        super.copyTo(rec);
        int iMin = Math.min(this.ext_formatting_length, this.ext_formatting_data.length);
        rec.ext_formatting_length = iMin;
        byte[] bArr = new byte[this.ext_formatting_length];
        rec.ext_formatting_data = bArr;
        System.arraycopy(this.ext_formatting_data, 0, bArr, 0, iMin);
        rec.formula_scale = this.formula_scale.copy();
        rec.ext_opts = this.ext_opts;
        rec.priority = this.priority;
        rec.template_type = this.template_type;
        rec.template_param_length = this.template_param_length;
        int i = this.template_param_length;
        byte[] bArr2 = new byte[i];
        rec.template_params = bArr2;
        System.arraycopy(this.template_params, 0, bArr2, 0, i);
        ColorGradientFormatting colorGradientFormatting = this.color_gradient;
        if (colorGradientFormatting != null) {
            rec.color_gradient = (ColorGradientFormatting) colorGradientFormatting.clone();
        }
        org.apache.poi.hssf.record.cf.IconMultiStateFormatting iconMultiStateFormatting = this.multistate;
        if (iconMultiStateFormatting != null) {
            rec.multistate = (org.apache.poi.hssf.record.cf.IconMultiStateFormatting) iconMultiStateFormatting.clone();
        }
        DataBarFormatting dataBarFormatting = this.data_bar;
        if (dataBarFormatting != null) {
            rec.data_bar = (DataBarFormatting) dataBarFormatting.clone();
        }
        byte[] bArr3 = this.filter_data;
        if (bArr3 != null) {
            byte[] bArr4 = new byte[bArr3.length];
            rec.filter_data = bArr4;
            byte[] bArr5 = this.filter_data;
            System.arraycopy(bArr5, 0, bArr4, 0, bArr5.length);
        }
        return rec;
    }

    @Override // org.apache.poi.hssf.record.common.FutureRecord
    public short getFutureRecordType() {
        return this.futureHeader.getRecordType();
    }

    @Override // org.apache.poi.hssf.record.common.FutureRecord
    public FtrHeader getFutureHeader() {
        return this.futureHeader;
    }

    @Override // org.apache.poi.hssf.record.common.FutureRecord
    public CellRangeAddress getAssociatedRange() {
        return this.futureHeader.getAssociatedRange();
    }
}
