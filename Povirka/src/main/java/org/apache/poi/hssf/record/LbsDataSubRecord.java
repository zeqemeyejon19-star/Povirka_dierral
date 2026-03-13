package org.apache.poi.hssf.record;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class LbsDataSubRecord extends SubRecord {
    public static final int sid = 19;
    private boolean[] _bsels;
    private int _cLines;
    private int _cbFContinued;
    private LbsDropData _dropData;
    private int _flags;
    private int _iSel;
    private int _idEdit;
    private Ptg _linkPtg;
    private String[] _rgLines;
    private Byte _unknownPostFormulaByte;
    private int _unknownPreFormulaInt;

    public LbsDataSubRecord(LittleEndianInput in, int cbFContinued, int cmoOt) {
        this._cbFContinued = cbFContinued;
        int encodedTokenLen = in.readUShort();
        if (encodedTokenLen > 0) {
            int formulaSize = in.readUShort();
            this._unknownPreFormulaInt = in.readInt();
            Ptg[] ptgs = Ptg.readTokens(formulaSize, in);
            if (ptgs.length != 1) {
                throw new RecordFormatException("Read " + ptgs.length + " tokens but expected exactly 1");
            }
            this._linkPtg = ptgs[0];
            int i = (encodedTokenLen - formulaSize) - 6;
            if (i == 0) {
                this._unknownPostFormulaByte = null;
            } else if (i == 1) {
                this._unknownPostFormulaByte = Byte.valueOf(in.readByte());
            } else {
                throw new RecordFormatException("Unexpected leftover bytes");
            }
        }
        this._cLines = in.readUShort();
        this._iSel = in.readUShort();
        this._flags = in.readUShort();
        this._idEdit = in.readUShort();
        if (cmoOt == 20) {
            this._dropData = new LbsDropData(in);
        }
        if ((this._flags & 2) != 0) {
            this._rgLines = new String[this._cLines];
            for (int i2 = 0; i2 < this._cLines; i2++) {
                this._rgLines[i2] = StringUtil.readUnicodeString(in);
            }
        }
        int i3 = this._flags;
        if (((i3 >> 4) & 2) != 0) {
            this._bsels = new boolean[this._cLines];
            for (int i4 = 0; i4 < this._cLines; i4++) {
                this._bsels[i4] = in.readByte() == 1;
            }
        }
    }

    LbsDataSubRecord() {
    }

    public static LbsDataSubRecord newAutoFilterInstance() {
        LbsDataSubRecord lbs = new LbsDataSubRecord();
        lbs._cbFContinued = 8174;
        lbs._iSel = 0;
        lbs._flags = 769;
        LbsDropData lbsDropData = new LbsDropData();
        lbs._dropData = lbsDropData;
        lbsDropData._wStyle = 2;
        lbs._dropData._cLine = 8;
        return lbs;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    public boolean isTerminating() {
        return true;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    protected int getDataSize() {
        int result = 2;
        Ptg ptg = this._linkPtg;
        if (ptg != null) {
            int result2 = 2 + 2;
            result = result2 + 4 + ptg.getSize();
            if (this._unknownPostFormulaByte != null) {
                result++;
            }
        }
        int result3 = result + 8;
        LbsDropData lbsDropData = this._dropData;
        if (lbsDropData != null) {
            result3 += lbsDropData.getDataSize();
        }
        if (this._rgLines != null) {
            String[] arr$ = this._rgLines;
            for (String str : arr$) {
                result3 += StringUtil.getEncodedSize(str);
            }
        }
        boolean[] zArr = this._bsels;
        if (zArr != null) {
            return result3 + zArr.length;
        }
        return result3;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        littleEndianOutput.writeShort(19);
        littleEndianOutput.writeShort(this._cbFContinued);
        Ptg ptg = this._linkPtg;
        if (ptg == null) {
            littleEndianOutput.writeShort(0);
        } else {
            int size = ptg.getSize();
            int i = size + 6;
            if (this._unknownPostFormulaByte != null) {
                i++;
            }
            littleEndianOutput.writeShort(i);
            littleEndianOutput.writeShort(size);
            littleEndianOutput.writeInt(this._unknownPreFormulaInt);
            this._linkPtg.write(littleEndianOutput);
            Byte b = this._unknownPostFormulaByte;
            if (b != null) {
                littleEndianOutput.writeByte(b.intValue());
            }
        }
        littleEndianOutput.writeShort(this._cLines);
        littleEndianOutput.writeShort(this._iSel);
        littleEndianOutput.writeShort(this._flags);
        littleEndianOutput.writeShort(this._idEdit);
        LbsDropData lbsDropData = this._dropData;
        if (lbsDropData != null) {
            lbsDropData.serialize(littleEndianOutput);
        }
        if (this._rgLines != null) {
            for (String str : this._rgLines) {
                StringUtil.writeUnicodeString(littleEndianOutput, str);
            }
        }
        if (this._bsels != null) {
            for (boolean z : this._bsels) {
                littleEndianOutput.writeByte(z ? 1 : 0);
            }
        }
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    /* JADX INFO: renamed from: clone */
    public LbsDataSubRecord mo9clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(256);
        sb.append("[ftLbsData]\n");
        sb.append("    .unknownShort1 =").append(HexDump.shortToHex(this._cbFContinued)).append("\n");
        sb.append("    .formula        = ").append('\n');
        Ptg ptg = this._linkPtg;
        if (ptg != null) {
            sb.append(ptg).append(this._linkPtg.getRVAType()).append('\n');
        }
        sb.append("    .nEntryCount   =").append(HexDump.shortToHex(this._cLines)).append("\n");
        sb.append("    .selEntryIx    =").append(HexDump.shortToHex(this._iSel)).append("\n");
        sb.append("    .style         =").append(HexDump.shortToHex(this._flags)).append("\n");
        sb.append("    .unknownShort10=").append(HexDump.shortToHex(this._idEdit)).append("\n");
        if (this._dropData != null) {
            sb.append('\n').append(this._dropData);
        }
        sb.append("[/ftLbsData]\n");
        return sb.toString();
    }

    public Ptg getFormula() {
        return this._linkPtg;
    }

    public int getNumberOfItems() {
        return this._cLines;
    }

    public static class LbsDropData {
        public static final int STYLE_COMBO_DROPDOWN = 0;
        public static final int STYLE_COMBO_EDIT_DROPDOWN = 1;
        public static final int STYLE_COMBO_SIMPLE_DROPDOWN = 2;
        private int _cLine;
        private int _dxMin;
        private final String _str;
        private Byte _unused;
        private int _wStyle;

        public LbsDropData() {
            this._str = "";
            this._unused = (byte) 0;
        }

        public LbsDropData(LittleEndianInput in) {
            this._wStyle = in.readUShort();
            this._cLine = in.readUShort();
            this._dxMin = in.readUShort();
            String unicodeString = StringUtil.readUnicodeString(in);
            this._str = unicodeString;
            if (StringUtil.getEncodedSize(unicodeString) % 2 != 0) {
                this._unused = Byte.valueOf(in.readByte());
            }
        }

        public void setStyle(int style) {
            this._wStyle = style;
        }

        public void setNumLines(int num) {
            this._cLine = num;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._wStyle);
            out.writeShort(this._cLine);
            out.writeShort(this._dxMin);
            StringUtil.writeUnicodeString(out, this._str);
            Byte b = this._unused;
            if (b != null) {
                out.writeByte(b.byteValue());
            }
        }

        public int getDataSize() {
            int size = 6 + StringUtil.getEncodedSize(this._str);
            if (this._unused != null) {
                return size + 1;
            }
            return size;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[LbsDropData]\n");
            sb.append("  ._wStyle:  ").append(this._wStyle).append('\n');
            sb.append("  ._cLine:  ").append(this._cLine).append('\n');
            sb.append("  ._dxMin:  ").append(this._dxMin).append('\n');
            sb.append("  ._str:  ").append(this._str).append('\n');
            if (this._unused != null) {
                sb.append("  ._unused:  ").append(this._unused).append('\n');
            }
            sb.append("[/LbsDropData]\n");
            return sb.toString();
        }
    }
}
