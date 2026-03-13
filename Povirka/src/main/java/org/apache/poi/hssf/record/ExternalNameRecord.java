package org.apache.poi.hssf.record;

import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.constant.ConstantValueParser;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class ExternalNameRecord extends StandardRecord {
    private static final int OPT_AUTOMATIC_LINK = 2;
    private static final int OPT_BUILTIN_NAME = 1;
    private static final int OPT_ICONIFIED_PICTURE_LINK = 32768;
    private static final int OPT_OLE_LINK = 16;
    private static final int OPT_PICTURE_LINK = 4;
    private static final int OPT_STD_DOCUMENT_NAME = 8;
    public static final short sid = 35;
    private Object[] _ddeValues;
    private int _nColumns;
    private int _nRows;
    private short field_1_option_flag;
    private short field_2_ixals;
    private short field_3_not_used;
    private String field_4_name;
    private Formula field_5_name_definition;

    public boolean isBuiltInName() {
        return (this.field_1_option_flag & 1) != 0;
    }

    public boolean isAutomaticLink() {
        return (this.field_1_option_flag & 2) != 0;
    }

    public boolean isPicureLink() {
        return (this.field_1_option_flag & 4) != 0;
    }

    public boolean isStdDocumentNameIdentifier() {
        return (this.field_1_option_flag & 8) != 0;
    }

    public boolean isOLELink() {
        return (this.field_1_option_flag & 16) != 0;
    }

    public boolean isIconifiedPictureLink() {
        return (this.field_1_option_flag & Short.MIN_VALUE) != 0;
    }

    public String getText() {
        return this.field_4_name;
    }

    public void setText(String str) {
        this.field_4_name = str;
    }

    public short getIx() {
        return this.field_2_ixals;
    }

    public void setIx(short ix) {
        this.field_2_ixals = ix;
    }

    public Ptg[] getParsedExpression() {
        return Formula.getTokens(this.field_5_name_definition);
    }

    public void setParsedExpression(Ptg[] ptgs) {
        this.field_5_name_definition = Formula.create(ptgs);
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        int result = 6 + (StringUtil.getEncodedSize(this.field_4_name) - 1);
        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                Object[] objArr = this._ddeValues;
                if (objArr != null) {
                    return result + 3 + ConstantValueParser.getEncodedSize(objArr);
                }
                return result;
            }
            return result + this.field_5_name_definition.getEncodedSize();
        }
        return result;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_option_flag);
        out.writeShort(this.field_2_ixals);
        out.writeShort(this.field_3_not_used);
        out.writeByte(this.field_4_name.length());
        StringUtil.writeUnicodeStringFlagAndData(out, this.field_4_name);
        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                if (this._ddeValues != null) {
                    out.writeByte(this._nColumns - 1);
                    out.writeShort(this._nRows - 1);
                    ConstantValueParser.encode(out, this._ddeValues);
                    return;
                }
                return;
            }
            this.field_5_name_definition.serialize(out);
        }
    }

    public ExternalNameRecord() {
        this.field_2_ixals = (short) 0;
    }

    public ExternalNameRecord(RecordInputStream in) {
        this.field_1_option_flag = in.readShort();
        this.field_2_ixals = in.readShort();
        this.field_3_not_used = in.readShort();
        int numChars = in.readUByte();
        this.field_4_name = StringUtil.readUnicodeString(in, numChars);
        if (!isOLELink() && !isStdDocumentNameIdentifier()) {
            if (isAutomaticLink()) {
                if (in.available() > 0) {
                    int nColumns = in.readUByte() + 1;
                    int nRows = in.readShort() + 1;
                    int totalCount = nRows * nColumns;
                    this._ddeValues = ConstantValueParser.parse(in, totalCount);
                    this._nColumns = nColumns;
                    this._nRows = nRows;
                    return;
                }
                return;
            }
            int formulaLen = in.readUShort();
            this.field_5_name_definition = Formula.read(formulaLen, in);
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 35;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[EXTERNALNAME]\n");
        sb.append("    .options = ").append((int) this.field_1_option_flag).append("\n");
        sb.append("    .ix      = ").append((int) this.field_2_ixals).append("\n");
        sb.append("    .name    = ").append(this.field_4_name).append("\n");
        Formula formula = this.field_5_name_definition;
        if (formula != null) {
            Ptg[] ptgs = formula.getTokens();
            for (Ptg ptg : ptgs) {
                sb.append("    .namedef = ").append(ptg).append(ptg.getRVAType()).append("\n");
            }
        }
        sb.append("[/EXTERNALNAME]\n");
        return sb.toString();
    }
}
