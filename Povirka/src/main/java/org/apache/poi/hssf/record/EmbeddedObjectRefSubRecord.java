package org.apache.poi.hssf.record;

import java.io.ByteArrayInputStream;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public final class EmbeddedObjectRefSubRecord extends SubRecord implements Cloneable {
    public static final short sid = 9;
    private int field_1_unknown_int;
    private Ptg field_2_refPtg;
    private byte[] field_2_unknownFormulaData;
    private boolean field_3_unicode_flag;
    private String field_4_ole_classname;
    private Byte field_4_unknownByte;
    private Integer field_5_stream_id;
    private byte[] field_6_unknown;
    private static POILogger logger = POILogFactory.getLogger((Class<?>) EmbeddedObjectRefSubRecord.class);
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    public EmbeddedObjectRefSubRecord() {
        this.field_2_unknownFormulaData = new byte[]{2, 108, 106, MissingArgPtg.sid, 1};
        this.field_6_unknown = EMPTY_BYTE_ARRAY;
        this.field_4_ole_classname = null;
    }

    public short getSid() {
        return (short) 9;
    }

    public EmbeddedObjectRefSubRecord(LittleEndianInput in, int size) {
        int stringByteCount;
        int streamIdOffset = in.readShort();
        int remaining = size - 2;
        int dataLenAfterFormula = remaining - streamIdOffset;
        int formulaSize = in.readUShort();
        this.field_1_unknown_int = in.readInt();
        byte[] formulaRawBytes = readRawData(in, formulaSize);
        int remaining2 = ((remaining - 2) - 4) - formulaSize;
        Ptg refPtg = readRefPtg(formulaRawBytes);
        this.field_2_refPtg = refPtg;
        if (refPtg == null) {
            this.field_2_unknownFormulaData = formulaRawBytes;
        } else {
            this.field_2_unknownFormulaData = null;
        }
        if (remaining2 >= dataLenAfterFormula + 3) {
            int tag = in.readByte();
            if (tag != 3) {
                throw new RecordFormatException("Expected byte 0x03 here");
            }
            int nChars = in.readUShort();
            stringByteCount = 1 + 2;
            if (nChars <= 0) {
                this.field_4_ole_classname = "";
            } else {
                boolean z = (in.readByte() & 1) != 0;
                this.field_3_unicode_flag = z;
                int stringByteCount2 = stringByteCount + 1;
                if (z) {
                    this.field_4_ole_classname = StringUtil.readUnicodeLE(in, nChars);
                    stringByteCount = stringByteCount2 + (nChars * 2);
                } else {
                    this.field_4_ole_classname = StringUtil.readCompressedUnicode(in, nChars);
                    stringByteCount = stringByteCount2 + nChars;
                }
            }
        } else {
            this.field_4_ole_classname = null;
            stringByteCount = 0;
        }
        int remaining3 = remaining2 - stringByteCount;
        if ((stringByteCount + formulaSize) % 2 != 0) {
            int b = in.readByte();
            remaining3--;
            if (this.field_2_refPtg != null && this.field_4_ole_classname == null) {
                this.field_4_unknownByte = Byte.valueOf((byte) b);
            }
        }
        int b2 = remaining3 - dataLenAfterFormula;
        if (b2 > 0) {
            logger.log(7, "Discarding " + b2 + " unexpected padding bytes ");
            readRawData(in, b2);
            remaining3 -= b2;
        }
        if (dataLenAfterFormula >= 4) {
            this.field_5_stream_id = Integer.valueOf(in.readInt());
            remaining3 -= 4;
        } else {
            this.field_5_stream_id = null;
        }
        this.field_6_unknown = readRawData(in, remaining3);
    }

    private static Ptg readRefPtg(byte[] formulaRawBytes) {
        LittleEndianInput in = new LittleEndianInputStream(new ByteArrayInputStream(formulaRawBytes));
        byte ptgSid = in.readByte();
        if (ptgSid == 36) {
            return new RefPtg(in);
        }
        if (ptgSid == 37) {
            return new AreaPtg(in);
        }
        if (ptgSid == 58) {
            return new Ref3DPtg(in);
        }
        if (ptgSid == 59) {
            return new Area3DPtg(in);
        }
        return null;
    }

    private static byte[] readRawData(LittleEndianInput in, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative size (" + size + ")");
        }
        if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        in.readFully(result);
        return result;
    }

    private int getStreamIDOffset(int formulaSize) {
        int result = 6 + formulaSize;
        String str = this.field_4_ole_classname;
        if (str != null) {
            result += 3;
            int stringLen = str.length();
            if (stringLen > 0) {
                int result2 = result + 1;
                if (this.field_3_unicode_flag) {
                    result = result2 + (stringLen * 2);
                } else {
                    result = result2 + stringLen;
                }
            }
        }
        if (result % 2 != 0) {
            return result + 1;
        }
        return result;
    }

    private int getDataSize(int idOffset) {
        int result = idOffset + 2;
        if (this.field_5_stream_id != null) {
            result += 4;
        }
        return this.field_6_unknown.length + result;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    protected int getDataSize() {
        Ptg ptg = this.field_2_refPtg;
        int formulaSize = ptg == null ? this.field_2_unknownFormulaData.length : ptg.getSize();
        int idOffset = getStreamIDOffset(formulaSize);
        return getDataSize(idOffset);
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    public void serialize(LittleEndianOutput littleEndianOutput) {
        Ptg ptg = this.field_2_refPtg;
        int length = ptg == null ? this.field_2_unknownFormulaData.length : ptg.getSize();
        int streamIDOffset = getStreamIDOffset(length);
        int dataSize = getDataSize(streamIDOffset);
        littleEndianOutput.writeShort(9);
        littleEndianOutput.writeShort(dataSize);
        littleEndianOutput.writeShort(streamIDOffset);
        littleEndianOutput.writeShort(length);
        littleEndianOutput.writeInt(this.field_1_unknown_int);
        Ptg ptg2 = this.field_2_refPtg;
        if (ptg2 == null) {
            littleEndianOutput.write(this.field_2_unknownFormulaData);
        } else {
            ptg2.write(littleEndianOutput);
        }
        int i = 12 + length;
        if (this.field_4_ole_classname != null) {
            littleEndianOutput.writeByte(3);
            int length2 = this.field_4_ole_classname.length();
            littleEndianOutput.writeShort(length2);
            i = i + 1 + 2;
            if (length2 > 0) {
                littleEndianOutput.writeByte(this.field_3_unicode_flag ? 1 : 0);
                int i2 = i + 1;
                if (this.field_3_unicode_flag) {
                    StringUtil.putUnicodeLE(this.field_4_ole_classname, littleEndianOutput);
                    i = i2 + (length2 * 2);
                } else {
                    StringUtil.putCompressedUnicode(this.field_4_ole_classname, littleEndianOutput);
                    i = i2 + length2;
                }
            }
        }
        int i3 = streamIDOffset - (i - 6);
        if (i3 != 0) {
            if (i3 == 1) {
                Byte b = this.field_4_unknownByte;
                littleEndianOutput.writeByte(b == null ? 0 : b.intValue());
                i++;
            } else {
                throw new IllegalStateException("Bad padding calculation (" + streamIDOffset + ", " + i + ")");
            }
        }
        Integer num = this.field_5_stream_id;
        if (num != null) {
            littleEndianOutput.writeInt(num.intValue());
            int i4 = i + 4;
        }
        littleEndianOutput.write(this.field_6_unknown);
    }

    public Integer getStreamId() {
        return this.field_5_stream_id;
    }

    public String getOLEClassName() {
        return this.field_4_ole_classname;
    }

    public byte[] getObjectData() {
        return this.field_6_unknown;
    }

    @Override // org.apache.poi.hssf.record.SubRecord
    /* JADX INFO: renamed from: clone */
    public EmbeddedObjectRefSubRecord mo9clone() {
        return this;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[ftPictFmla]\n");
        sb.append("    .f2unknown     = ").append(HexDump.intToHex(this.field_1_unknown_int)).append("\n");
        if (this.field_2_refPtg == null) {
            sb.append("    .f3unknown     = ").append(HexDump.toHex(this.field_2_unknownFormulaData)).append("\n");
        } else {
            sb.append("    .formula       = ").append(this.field_2_refPtg).append("\n");
        }
        if (this.field_4_ole_classname != null) {
            sb.append("    .unicodeFlag   = ").append(this.field_3_unicode_flag).append("\n");
            sb.append("    .oleClassname  = ").append(this.field_4_ole_classname).append("\n");
        }
        if (this.field_4_unknownByte != null) {
            sb.append("    .f4unknown   = ").append(HexDump.byteToHex(this.field_4_unknownByte.intValue())).append("\n");
        }
        if (this.field_5_stream_id != null) {
            sb.append("    .streamId      = ").append(HexDump.intToHex(this.field_5_stream_id.intValue())).append("\n");
        }
        if (this.field_6_unknown.length > 0) {
            sb.append("    .f7unknown     = ").append(HexDump.toHex(this.field_6_unknown)).append("\n");
        }
        sb.append("[/ftPictFmla]");
        return sb.toString();
    }

    public void setUnknownFormulaData(byte[] formularData) {
        this.field_2_unknownFormulaData = formularData;
    }

    public void setOleClassname(String oleClassname) {
        this.field_4_ole_classname = oleClassname;
    }

    public void setStorageId(int storageId) {
        this.field_5_stream_id = Integer.valueOf(storageId);
    }
}
