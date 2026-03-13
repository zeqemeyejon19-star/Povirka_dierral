package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class NameRecord extends ContinuableRecord {
    public static final byte BUILTIN_AUTO_ACTIVATE = 10;
    public static final byte BUILTIN_AUTO_CLOSE = 3;
    public static final byte BUILTIN_AUTO_DEACTIVATE = 11;
    public static final byte BUILTIN_AUTO_OPEN = 2;
    public static final byte BUILTIN_CONSOLIDATE_AREA = 1;
    public static final byte BUILTIN_CRITERIA = 5;
    public static final byte BUILTIN_DATABASE = 4;
    public static final byte BUILTIN_DATA_FORM = 9;
    public static final byte BUILTIN_FILTER_DB = 13;
    public static final byte BUILTIN_PRINT_AREA = 6;
    public static final byte BUILTIN_PRINT_TITLE = 7;
    public static final byte BUILTIN_RECORDER = 8;
    public static final byte BUILTIN_SHEET_TITLE = 12;
    public static final short sid = 24;
    private boolean field_11_nameIsMultibyte;
    private byte field_12_built_in_code;
    private String field_12_name_text;
    private Formula field_13_name_definition;
    private String field_14_custom_menu_text;
    private String field_15_description_text;
    private String field_16_help_topic_text;
    private String field_17_status_bar_text;
    private short field_1_option_flag;
    private byte field_2_keyboard_shortcut;
    private short field_5_externSheetIndex_plus1;
    private int field_6_sheetNumber;

    private static final class Option {
        public static final int OPT_BINDATA = 4096;
        public static final int OPT_BUILTIN = 32;
        public static final int OPT_COMMAND_NAME = 4;
        public static final int OPT_COMPLEX = 16;
        public static final int OPT_FUNCTION_NAME = 2;
        public static final int OPT_HIDDEN_NAME = 1;
        public static final int OPT_MACRO = 8;

        private Option() {
        }

        public static final boolean isFormula(int optValue) {
            return (optValue & 15) == 0;
        }
    }

    public NameRecord() {
        this.field_13_name_definition = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        this.field_12_name_text = "";
        this.field_14_custom_menu_text = "";
        this.field_15_description_text = "";
        this.field_16_help_topic_text = "";
        this.field_17_status_bar_text = "";
    }

    public NameRecord(byte builtin, int sheetNumber) {
        this();
        this.field_12_built_in_code = builtin;
        setOptionFlag((short) (this.field_1_option_flag | 32));
        this.field_6_sheetNumber = sheetNumber;
    }

    public void setOptionFlag(short flag) {
        this.field_1_option_flag = flag;
    }

    public void setKeyboardShortcut(byte shortcut) {
        this.field_2_keyboard_shortcut = shortcut;
    }

    public int getSheetNumber() {
        return this.field_6_sheetNumber;
    }

    public byte getFnGroup() {
        int masked = this.field_1_option_flag & 4032;
        return (byte) (masked >> 4);
    }

    public void setSheetNumber(int value) {
        this.field_6_sheetNumber = value;
    }

    public void setNameText(String name) {
        this.field_12_name_text = name;
        this.field_11_nameIsMultibyte = StringUtil.hasMultibyte(name);
    }

    public void setCustomMenuText(String text) {
        this.field_14_custom_menu_text = text;
    }

    public void setDescriptionText(String text) {
        this.field_15_description_text = text;
    }

    public void setHelpTopicText(String text) {
        this.field_16_help_topic_text = text;
    }

    public void setStatusBarText(String text) {
        this.field_17_status_bar_text = text;
    }

    public short getOptionFlag() {
        return this.field_1_option_flag;
    }

    public byte getKeyboardShortcut() {
        return this.field_2_keyboard_shortcut;
    }

    private int getNameTextLength() {
        if (isBuiltInName()) {
            return 1;
        }
        return this.field_12_name_text.length();
    }

    public boolean isHiddenName() {
        return (this.field_1_option_flag & 1) != 0;
    }

    public void setHidden(boolean b) {
        if (b) {
            this.field_1_option_flag = (short) (this.field_1_option_flag | 1);
        } else {
            this.field_1_option_flag = (short) (this.field_1_option_flag & (-2));
        }
    }

    public boolean isFunctionName() {
        return (this.field_1_option_flag & 2) != 0;
    }

    public void setFunction(boolean function) {
        if (function) {
            this.field_1_option_flag = (short) (this.field_1_option_flag | 2);
        } else {
            this.field_1_option_flag = (short) (this.field_1_option_flag & (-3));
        }
    }

    public boolean hasFormula() {
        return Option.isFormula(this.field_1_option_flag) && this.field_13_name_definition.getEncodedTokenSize() > 0;
    }

    public boolean isCommandName() {
        return (this.field_1_option_flag & 4) != 0;
    }

    public boolean isMacro() {
        return (this.field_1_option_flag & 8) != 0;
    }

    public boolean isComplexFunction() {
        return (this.field_1_option_flag & 16) != 0;
    }

    public boolean isBuiltInName() {
        return (this.field_1_option_flag & 32) != 0;
    }

    public String getNameText() {
        return isBuiltInName() ? translateBuiltInName(getBuiltInName()) : this.field_12_name_text;
    }

    public byte getBuiltInName() {
        return this.field_12_built_in_code;
    }

    public Ptg[] getNameDefinition() {
        return this.field_13_name_definition.getTokens();
    }

    public void setNameDefinition(Ptg[] ptgs) {
        this.field_13_name_definition = Formula.create(ptgs);
    }

    public String getCustomMenuText() {
        return this.field_14_custom_menu_text;
    }

    public String getDescriptionText() {
        return this.field_15_description_text;
    }

    public String getHelpTopicText() {
        return this.field_16_help_topic_text;
    }

    public String getStatusBarText() {
        return this.field_17_status_bar_text;
    }

    @Override // org.apache.poi.hssf.record.cont.ContinuableRecord
    public void serialize(ContinuableRecordOutput continuableRecordOutput) {
        int length = this.field_14_custom_menu_text.length();
        int length2 = this.field_15_description_text.length();
        int length3 = this.field_16_help_topic_text.length();
        int length4 = this.field_17_status_bar_text.length();
        continuableRecordOutput.writeShort(getOptionFlag());
        continuableRecordOutput.writeByte(getKeyboardShortcut());
        continuableRecordOutput.writeByte(getNameTextLength());
        continuableRecordOutput.writeShort(this.field_13_name_definition.getEncodedTokenSize());
        continuableRecordOutput.writeShort(this.field_5_externSheetIndex_plus1);
        continuableRecordOutput.writeShort(this.field_6_sheetNumber);
        continuableRecordOutput.writeByte(length);
        continuableRecordOutput.writeByte(length2);
        continuableRecordOutput.writeByte(length3);
        continuableRecordOutput.writeByte(length4);
        continuableRecordOutput.writeByte(this.field_11_nameIsMultibyte ? 1 : 0);
        if (isBuiltInName()) {
            continuableRecordOutput.writeByte(this.field_12_built_in_code);
        } else {
            String str = this.field_12_name_text;
            if (this.field_11_nameIsMultibyte) {
                StringUtil.putUnicodeLE(str, continuableRecordOutput);
            } else {
                StringUtil.putCompressedUnicode(str, continuableRecordOutput);
            }
        }
        this.field_13_name_definition.serializeTokens(continuableRecordOutput);
        this.field_13_name_definition.serializeArrayConstantData(continuableRecordOutput);
        StringUtil.putCompressedUnicode(getCustomMenuText(), continuableRecordOutput);
        StringUtil.putCompressedUnicode(getDescriptionText(), continuableRecordOutput);
        StringUtil.putCompressedUnicode(getHelpTopicText(), continuableRecordOutput);
        StringUtil.putCompressedUnicode(getStatusBarText(), continuableRecordOutput);
    }

    private int getNameRawSize() {
        if (isBuiltInName()) {
            return 1;
        }
        int nChars = this.field_12_name_text.length();
        if (this.field_11_nameIsMultibyte) {
            return nChars * 2;
        }
        return nChars;
    }

    protected int getDataSize() {
        return getNameRawSize() + 13 + this.field_14_custom_menu_text.length() + this.field_15_description_text.length() + this.field_16_help_topic_text.length() + this.field_17_status_bar_text.length() + this.field_13_name_definition.getEncodedSize();
    }

    public int getExternSheetNumber() {
        Ptg[] tokens = this.field_13_name_definition.getTokens();
        if (tokens.length == 0) {
            return 0;
        }
        Ptg ptg = tokens[0];
        if (ptg.getClass() == Area3DPtg.class) {
            return ((Area3DPtg) ptg).getExternSheetIndex();
        }
        if (ptg.getClass() == Ref3DPtg.class) {
            return ((Ref3DPtg) ptg).getExternSheetIndex();
        }
        return 0;
    }

    public NameRecord(RecordInputStream ris) {
        byte[] remainder = ris.readAllContinuedRemainder();
        LittleEndianInput in = new LittleEndianByteArrayInputStream(remainder);
        this.field_1_option_flag = in.readShort();
        this.field_2_keyboard_shortcut = in.readByte();
        int field_3_length_name_text = in.readUByte();
        int field_4_length_name_definition = in.readShort();
        this.field_5_externSheetIndex_plus1 = in.readShort();
        this.field_6_sheetNumber = in.readUShort();
        int f7_customMenuLen = in.readUByte();
        int f8_descriptionTextLen = in.readUByte();
        int f9_helpTopicTextLen = in.readUByte();
        int f10_statusBarTextLen = in.readUByte();
        this.field_11_nameIsMultibyte = in.readByte() != 0;
        if (isBuiltInName()) {
            this.field_12_built_in_code = in.readByte();
        } else if (this.field_11_nameIsMultibyte) {
            this.field_12_name_text = StringUtil.readUnicodeLE(in, field_3_length_name_text);
        } else {
            this.field_12_name_text = StringUtil.readCompressedUnicode(in, field_3_length_name_text);
        }
        int nBytesAvailable = in.available() - (((f7_customMenuLen + f8_descriptionTextLen) + f9_helpTopicTextLen) + f10_statusBarTextLen);
        this.field_13_name_definition = Formula.read(field_4_length_name_definition, in, nBytesAvailable);
        this.field_14_custom_menu_text = StringUtil.readCompressedUnicode(in, f7_customMenuLen);
        this.field_15_description_text = StringUtil.readCompressedUnicode(in, f8_descriptionTextLen);
        this.field_16_help_topic_text = StringUtil.readCompressedUnicode(in, f9_helpTopicTextLen);
        this.field_17_status_bar_text = StringUtil.readCompressedUnicode(in, f10_statusBarTextLen);
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 24;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[NAME]\n");
        sb.append("    .option flags           = ").append(HexDump.shortToHex(this.field_1_option_flag)).append("\n");
        sb.append("    .keyboard shortcut      = ").append(HexDump.byteToHex(this.field_2_keyboard_shortcut)).append("\n");
        sb.append("    .length of the name     = ").append(getNameTextLength()).append("\n");
        sb.append("    .extSheetIx(1-based, 0=Global)= ").append((int) this.field_5_externSheetIndex_plus1).append("\n");
        sb.append("    .sheetTabIx             = ").append(this.field_6_sheetNumber).append("\n");
        sb.append("    .Menu text length       = ").append(this.field_14_custom_menu_text.length()).append("\n");
        sb.append("    .Description text length= ").append(this.field_15_description_text.length()).append("\n");
        sb.append("    .Help topic text length = ").append(this.field_16_help_topic_text.length()).append("\n");
        sb.append("    .Status bar text length = ").append(this.field_17_status_bar_text.length()).append("\n");
        sb.append("    .NameIsMultibyte        = ").append(this.field_11_nameIsMultibyte).append("\n");
        sb.append("    .Name (Unicode text)    = ").append(getNameText()).append("\n");
        Ptg[] ptgs = this.field_13_name_definition.getTokens();
        sb.append("    .Formula (nTokens=").append(ptgs.length).append("):").append("\n");
        for (Ptg ptg : ptgs) {
            sb.append("       " + ptg).append(ptg.getRVAType()).append("\n");
        }
        sb.append("    .Menu text       = ").append(this.field_14_custom_menu_text).append("\n");
        sb.append("    .Description text= ").append(this.field_15_description_text).append("\n");
        sb.append("    .Help topic text = ").append(this.field_16_help_topic_text).append("\n");
        sb.append("    .Status bar text = ").append(this.field_17_status_bar_text).append("\n");
        sb.append("[/NAME]\n");
        return sb.toString();
    }

    private static String translateBuiltInName(byte name) {
        switch (name) {
            case 1:
                return "Consolidate_Area";
            case 2:
                return "Auto_Open";
            case 3:
                return "Auto_Close";
            case 4:
                return "Database";
            case 5:
                return "Criteria";
            case 6:
                return "Print_Area";
            case 7:
                return "Print_Titles";
            case 8:
                return "Recorder";
            case 9:
                return "Data_Form";
            case 10:
                return "Auto_Activate";
            case 11:
                return "Auto_Deactivate";
            case 12:
                return "Sheet_Title";
            case 13:
                return "_FilterDatabase";
            default:
                return "Unknown";
        }
    }
}
