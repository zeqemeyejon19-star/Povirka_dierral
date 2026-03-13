package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class StringPtg extends ScalarConstantPtg {
    private static final char FORMULA_DELIMITER = '\"';
    public static final byte sid = 23;
    private final boolean _is16bitUnicode;
    private final String field_3_string;

    public StringPtg(LittleEndianInput in) {
        int nChars = in.readUByte();
        boolean z = (in.readByte() & 1) != 0;
        this._is16bitUnicode = z;
        if (z) {
            this.field_3_string = StringUtil.readUnicodeLE(in, nChars);
        } else {
            this.field_3_string = StringUtil.readCompressedUnicode(in, nChars);
        }
    }

    public StringPtg(String value) {
        if (value.length() > 255) {
            throw new IllegalArgumentException("String literals in formulas can't be bigger than 255 characters ASCII");
        }
        this._is16bitUnicode = StringUtil.hasMultibyte(value);
        this.field_3_string = value;
    }

    public String getValue() {
        return this.field_3_string;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public void write(LittleEndianOutput littleEndianOutput) {
        littleEndianOutput.writeByte(getPtgClass() + sid);
        littleEndianOutput.writeByte(this.field_3_string.length());
        littleEndianOutput.writeByte(this._is16bitUnicode ? 1 : 0);
        if (this._is16bitUnicode) {
            StringUtil.putUnicodeLE(this.field_3_string, littleEndianOutput);
        } else {
            StringUtil.putCompressedUnicode(this.field_3_string, littleEndianOutput);
        }
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public int getSize() {
        return (this.field_3_string.length() * (this._is16bitUnicode ? 2 : 1)) + 3;
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public String toFormulaString() {
        String value = this.field_3_string;
        int len = value.length();
        StringBuffer sb = new StringBuffer(len + 4);
        sb.append(FORMULA_DELIMITER);
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c == '\"') {
                sb.append(FORMULA_DELIMITER);
            }
            sb.append(c);
        }
        sb.append(FORMULA_DELIMITER);
        return sb.toString();
    }
}
