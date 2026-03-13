package org.apache.poi.hssf.record.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.cont.ContinuableRecordInput;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class UnicodeString implements Comparable<UnicodeString> {
    private short field_1_charCount;
    private byte field_2_optionflags;
    private String field_3_string;
    private List<FormatRun> field_4_format_runs;
    private ExtRst field_5_ext_rst;
    private static POILogger _logger = POILogFactory.getLogger((Class<?>) UnicodeString.class);
    private static final BitField highByte = BitFieldFactory.getInstance(1);
    private static final BitField extBit = BitFieldFactory.getInstance(4);
    private static final BitField richText = BitFieldFactory.getInstance(8);

    public static class FormatRun implements Comparable<FormatRun> {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        final short _character;
        short _fontIndex;

        public FormatRun(short character, short fontIndex) {
            this._character = character;
            this._fontIndex = fontIndex;
        }

        public FormatRun(LittleEndianInput in) {
            this(in.readShort(), in.readShort());
        }

        public short getCharacterPos() {
            return this._character;
        }

        public short getFontIndex() {
            return this._fontIndex;
        }

        public boolean equals(Object o) {
            if (!(o instanceof FormatRun)) {
                return false;
            }
            FormatRun other = (FormatRun) o;
            return this._character == other._character && this._fontIndex == other._fontIndex;
        }

        @Override // java.lang.Comparable
        public int compareTo(FormatRun r) {
            short s = this._character;
            short s2 = r._character;
            if (s == s2 && this._fontIndex == r._fontIndex) {
                return 0;
            }
            if (s == s2) {
                return this._fontIndex - r._fontIndex;
            }
            return s - s2;
        }

        public int hashCode() {
            throw new AssertionError("hashCode not designed");
        }

        public String toString() {
            return "character=" + ((int) this._character) + ",fontIndex=" + ((int) this._fontIndex);
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._character);
            out.writeShort(this._fontIndex);
        }
    }

    public static class ExtRst implements Comparable<ExtRst> {
        private byte[] extraData;
        private short formattingFontIndex;
        private short formattingOptions;
        private int numberOfRuns;
        private PhRun[] phRuns;
        private String phoneticText;
        private short reserved;

        private void populateEmpty() {
            this.reserved = (short) 1;
            this.phoneticText = "";
            this.phRuns = new PhRun[0];
            this.extraData = new byte[0];
        }

        protected ExtRst() {
            populateEmpty();
        }

        protected ExtRst(LittleEndianInput in, int expectedLength) {
            short s = in.readShort();
            this.reserved = s;
            if (s == -1) {
                populateEmpty();
                return;
            }
            if (s != 1) {
                UnicodeString._logger.log(5, "Warning - ExtRst has wrong magic marker, expecting 1 but found " + ((int) this.reserved) + " - ignoring");
                for (int i = 0; i < expectedLength - 2; i++) {
                    in.readByte();
                }
                populateEmpty();
                return;
            }
            short stringDataSize = in.readShort();
            this.formattingFontIndex = in.readShort();
            this.formattingOptions = in.readShort();
            this.numberOfRuns = in.readUShort();
            short length1 = in.readShort();
            short length2 = in.readShort();
            if (length1 == 0 && length2 > 0) {
                length2 = 0;
            }
            if (length1 != length2) {
                throw new IllegalStateException("The two length fields of the Phonetic Text don't agree! " + ((int) length1) + " vs " + ((int) length2));
            }
            String unicodeLE = StringUtil.readUnicodeLE(in, length1);
            this.phoneticText = unicodeLE;
            int runData = ((stringDataSize - 4) - 6) - (unicodeLE.length() * 2);
            int numRuns = runData / 6;
            this.phRuns = new PhRun[numRuns];
            int i2 = 0;
            while (true) {
                PhRun[] phRunArr = this.phRuns;
                if (i2 >= phRunArr.length) {
                    break;
                }
                phRunArr[i2] = new PhRun(in);
                i2++;
            }
            int i3 = numRuns * 6;
            int extraDataLength = runData - i3;
            if (extraDataLength < 0) {
                UnicodeString._logger.log(5, "Warning - ExtRst overran by " + (0 - extraDataLength) + " bytes");
                extraDataLength = 0;
            }
            this.extraData = new byte[extraDataLength];
            int i4 = 0;
            while (true) {
                byte[] bArr = this.extraData;
                if (i4 < bArr.length) {
                    bArr[i4] = in.readByte();
                    i4++;
                } else {
                    return;
                }
            }
        }

        protected int getDataSize() {
            return (this.phoneticText.length() * 2) + 10 + (this.phRuns.length * 6) + this.extraData.length;
        }

        protected void serialize(ContinuableRecordOutput out) {
            int dataSize = getDataSize();
            out.writeContinueIfRequired(8);
            out.writeShort(this.reserved);
            out.writeShort(dataSize);
            out.writeShort(this.formattingFontIndex);
            out.writeShort(this.formattingOptions);
            out.writeContinueIfRequired(6);
            out.writeShort(this.numberOfRuns);
            out.writeShort(this.phoneticText.length());
            out.writeShort(this.phoneticText.length());
            out.writeContinueIfRequired(this.phoneticText.length() * 2);
            StringUtil.putUnicodeLE(this.phoneticText, out);
            int i = 0;
            while (true) {
                PhRun[] phRunArr = this.phRuns;
                if (i >= phRunArr.length) {
                    out.write(this.extraData);
                    return;
                } else {
                    phRunArr[i].serialize(out);
                    i++;
                }
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof ExtRst)) {
                return false;
            }
            ExtRst other = (ExtRst) obj;
            return compareTo(other) == 0;
        }

        @Override // java.lang.Comparable
        public int compareTo(ExtRst o) {
            int result = this.reserved - o.reserved;
            if (result != 0) {
                return result;
            }
            int result2 = this.formattingFontIndex - o.formattingFontIndex;
            if (result2 != 0) {
                return result2;
            }
            int result3 = this.formattingOptions - o.formattingOptions;
            if (result3 != 0) {
                return result3;
            }
            int result4 = this.numberOfRuns - o.numberOfRuns;
            if (result4 != 0) {
                return result4;
            }
            int result5 = this.phoneticText.compareTo(o.phoneticText);
            if (result5 != 0) {
                return result5;
            }
            int result6 = this.phRuns.length - o.phRuns.length;
            if (result6 != 0) {
                return result6;
            }
            int i = 0;
            while (true) {
                PhRun[] phRunArr = this.phRuns;
                if (i >= phRunArr.length) {
                    return Arrays.hashCode(this.extraData) - Arrays.hashCode(o.extraData);
                }
                int result7 = phRunArr[i].phoneticTextFirstCharacterOffset - o.phRuns[i].phoneticTextFirstCharacterOffset;
                if (result7 != 0) {
                    return result7;
                }
                int result8 = this.phRuns[i].realTextFirstCharacterOffset - o.phRuns[i].realTextFirstCharacterOffset;
                if (result8 != 0) {
                    return result8;
                }
                int result9 = this.phRuns[i].realTextLength - o.phRuns[i].realTextLength;
                if (result9 == 0) {
                    i++;
                } else {
                    return result9;
                }
            }
        }

        public int hashCode() {
            int hash = (((((((this.reserved * 31) + this.formattingFontIndex) * 31) + this.formattingOptions) * 31) + this.numberOfRuns) * 31) + this.phoneticText.hashCode();
            if (this.phRuns != null) {
                PhRun[] arr$ = this.phRuns;
                for (PhRun ph : arr$) {
                    hash = (((((hash * 31) + ph.phoneticTextFirstCharacterOffset) * 31) + ph.realTextFirstCharacterOffset) * 31) + ph.realTextLength;
                }
            }
            return hash;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public ExtRst clone() {
            ExtRst ext = new ExtRst();
            ext.reserved = this.reserved;
            ext.formattingFontIndex = this.formattingFontIndex;
            ext.formattingOptions = this.formattingOptions;
            ext.numberOfRuns = this.numberOfRuns;
            ext.phoneticText = this.phoneticText;
            ext.phRuns = new PhRun[this.phRuns.length];
            int i = 0;
            while (true) {
                PhRun[] phRunArr = ext.phRuns;
                if (i < phRunArr.length) {
                    phRunArr[i] = new PhRun(this.phRuns[i].phoneticTextFirstCharacterOffset, this.phRuns[i].realTextFirstCharacterOffset, this.phRuns[i].realTextLength);
                    i++;
                } else {
                    return ext;
                }
            }
        }

        public short getFormattingFontIndex() {
            return this.formattingFontIndex;
        }

        public short getFormattingOptions() {
            return this.formattingOptions;
        }

        public int getNumberOfRuns() {
            return this.numberOfRuns;
        }

        public String getPhoneticText() {
            return this.phoneticText;
        }

        public PhRun[] getPhRuns() {
            return this.phRuns;
        }
    }

    public static class PhRun {
        private int phoneticTextFirstCharacterOffset;
        private int realTextFirstCharacterOffset;
        private int realTextLength;

        public PhRun(int phoneticTextFirstCharacterOffset, int realTextFirstCharacterOffset, int realTextLength) {
            this.phoneticTextFirstCharacterOffset = phoneticTextFirstCharacterOffset;
            this.realTextFirstCharacterOffset = realTextFirstCharacterOffset;
            this.realTextLength = realTextLength;
        }

        private PhRun(LittleEndianInput in) {
            this.phoneticTextFirstCharacterOffset = in.readUShort();
            this.realTextFirstCharacterOffset = in.readUShort();
            this.realTextLength = in.readUShort();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void serialize(ContinuableRecordOutput out) {
            out.writeContinueIfRequired(6);
            out.writeShort(this.phoneticTextFirstCharacterOffset);
            out.writeShort(this.realTextFirstCharacterOffset);
            out.writeShort(this.realTextLength);
        }
    }

    private UnicodeString() {
    }

    public UnicodeString(String str) {
        setString(str);
    }

    public int hashCode() {
        int stringHash = 0;
        String str = this.field_3_string;
        if (str != null) {
            stringHash = str.hashCode();
        }
        return this.field_1_charCount + stringHash;
    }

    public boolean equals(Object o) {
        int size;
        if (!(o instanceof UnicodeString)) {
            return false;
        }
        UnicodeString other = (UnicodeString) o;
        if (this.field_1_charCount != other.field_1_charCount || this.field_2_optionflags != other.field_2_optionflags || !this.field_3_string.equals(other.field_3_string)) {
            return false;
        }
        List<FormatRun> list = this.field_4_format_runs;
        if (list == null) {
            return other.field_4_format_runs == null;
        }
        if (other.field_4_format_runs == null || (size = list.size()) != other.field_4_format_runs.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            FormatRun run1 = this.field_4_format_runs.get(i);
            FormatRun run2 = other.field_4_format_runs.get(i);
            if (!run1.equals(run2)) {
                return false;
            }
        }
        ExtRst extRst = this.field_5_ext_rst;
        if (extRst == null) {
            return other.field_5_ext_rst == null;
        }
        ExtRst extRst2 = other.field_5_ext_rst;
        if (extRst2 == null) {
            return false;
        }
        return extRst.equals(extRst2);
    }

    public UnicodeString(RecordInputStream in) {
        this.field_1_charCount = in.readShort();
        this.field_2_optionflags = in.readByte();
        int runCount = isRichText() ? in.readShort() : 0;
        int extensionLength = isExtendedText() ? in.readInt() : 0;
        boolean isCompressed = (this.field_2_optionflags & 1) == 0;
        int cc = getCharCount();
        this.field_3_string = isCompressed ? in.readCompressedUnicode(cc) : in.readUnicodeLEString(cc);
        if (isRichText() && runCount > 0) {
            this.field_4_format_runs = new ArrayList(runCount);
            for (int i = 0; i < runCount; i++) {
                this.field_4_format_runs.add(new FormatRun(in));
            }
        }
        if (isExtendedText() && extensionLength > 0) {
            ExtRst extRst = new ExtRst(new ContinuableRecordInput(in), extensionLength);
            this.field_5_ext_rst = extRst;
            if (extRst.getDataSize() + 4 != extensionLength) {
                _logger.log(5, "ExtRst was supposed to be " + extensionLength + " bytes long, but seems to actually be " + (this.field_5_ext_rst.getDataSize() + 4));
            }
        }
    }

    public int getCharCount() {
        short s = this.field_1_charCount;
        if (s < 0) {
            return s + 65536;
        }
        return s;
    }

    public short getCharCountShort() {
        return this.field_1_charCount;
    }

    public void setCharCount(short cc) {
        this.field_1_charCount = cc;
    }

    public byte getOptionFlags() {
        return this.field_2_optionflags;
    }

    public void setOptionFlags(byte of) {
        this.field_2_optionflags = of;
    }

    public String getString() {
        return this.field_3_string;
    }

    public void setString(String string) {
        this.field_3_string = string;
        setCharCount((short) string.length());
        boolean useUTF16 = false;
        int strlen = string.length();
        int j = 0;
        while (true) {
            if (j >= strlen) {
                break;
            }
            if (string.charAt(j) <= 255) {
                j++;
            } else {
                useUTF16 = true;
                break;
            }
        }
        if (useUTF16) {
            this.field_2_optionflags = highByte.setByte(this.field_2_optionflags);
        } else {
            this.field_2_optionflags = highByte.clearByte(this.field_2_optionflags);
        }
    }

    public int getFormatRunCount() {
        List<FormatRun> list = this.field_4_format_runs;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public FormatRun getFormatRun(int index) {
        List<FormatRun> list = this.field_4_format_runs;
        if (list != null && index >= 0 && index < list.size()) {
            return this.field_4_format_runs.get(index);
        }
        return null;
    }

    private int findFormatRunAt(int characterPos) {
        int size = this.field_4_format_runs.size();
        for (int i = 0; i < size; i++) {
            FormatRun r = this.field_4_format_runs.get(i);
            if (r._character == characterPos) {
                return i;
            }
            if (r._character > characterPos) {
                return -1;
            }
        }
        return -1;
    }

    public void addFormatRun(FormatRun r) {
        if (this.field_4_format_runs == null) {
            this.field_4_format_runs = new ArrayList();
        }
        int index = findFormatRunAt(r._character);
        if (index != -1) {
            this.field_4_format_runs.remove(index);
        }
        this.field_4_format_runs.add(r);
        Collections.sort(this.field_4_format_runs);
        this.field_2_optionflags = richText.setByte(this.field_2_optionflags);
    }

    public Iterator<FormatRun> formatIterator() {
        List<FormatRun> list = this.field_4_format_runs;
        if (list != null) {
            return list.iterator();
        }
        return null;
    }

    public void removeFormatRun(FormatRun r) {
        this.field_4_format_runs.remove(r);
        if (this.field_4_format_runs.size() == 0) {
            this.field_4_format_runs = null;
            this.field_2_optionflags = richText.clearByte(this.field_2_optionflags);
        }
    }

    public void clearFormatting() {
        this.field_4_format_runs = null;
        this.field_2_optionflags = richText.clearByte(this.field_2_optionflags);
    }

    public ExtRst getExtendedRst() {
        return this.field_5_ext_rst;
    }

    void setExtendedRst(ExtRst ext_rst) {
        if (ext_rst != null) {
            this.field_2_optionflags = extBit.setByte(this.field_2_optionflags);
        } else {
            this.field_2_optionflags = extBit.clearByte(this.field_2_optionflags);
        }
        this.field_5_ext_rst = ext_rst;
    }

    public void swapFontUse(short oldFontIndex, short newFontIndex) {
        for (FormatRun run : this.field_4_format_runs) {
            if (run._fontIndex == oldFontIndex) {
                run._fontIndex = newFontIndex;
            }
        }
    }

    public String toString() {
        return getString();
    }

    public String getDebugInfo() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[UNICODESTRING]\n");
        buffer.append("    .charcount       = ").append(Integer.toHexString(getCharCount())).append("\n");
        buffer.append("    .optionflags     = ").append(Integer.toHexString(getOptionFlags())).append("\n");
        buffer.append("    .string          = ").append(getString()).append("\n");
        if (this.field_4_format_runs != null) {
            for (int i = 0; i < this.field_4_format_runs.size(); i++) {
                FormatRun r = this.field_4_format_runs.get(i);
                buffer.append("      .format_run" + i + "          = ").append(r).append("\n");
            }
        }
        if (this.field_5_ext_rst != null) {
            buffer.append("    .field_5_ext_rst          = ").append("\n");
            buffer.append(this.field_5_ext_rst).append("\n");
        }
        buffer.append("[/UNICODESTRING]\n");
        return buffer.toString();
    }

    public void serialize(ContinuableRecordOutput out) {
        ExtRst extRst;
        ExtRst extRst2;
        List<FormatRun> list;
        int numberOfRichTextRuns = 0;
        int extendedDataSize = 0;
        if (isRichText() && (list = this.field_4_format_runs) != null) {
            numberOfRichTextRuns = list.size();
        }
        if (isExtendedText() && (extRst2 = this.field_5_ext_rst) != null) {
            extendedDataSize = extRst2.getDataSize() + 4;
        }
        out.writeString(this.field_3_string, numberOfRichTextRuns, extendedDataSize);
        if (numberOfRichTextRuns > 0) {
            for (int i = 0; i < numberOfRichTextRuns; i++) {
                if (out.getAvailableSpace() < 4) {
                    out.writeContinue();
                }
                FormatRun r = this.field_4_format_runs.get(i);
                r.serialize(out);
            }
        }
        if (extendedDataSize > 0 && (extRst = this.field_5_ext_rst) != null) {
            extRst.serialize(out);
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(UnicodeString str) {
        int result = getString().compareTo(str.getString());
        if (result != 0) {
            return result;
        }
        List<FormatRun> list = this.field_4_format_runs;
        if (list == null) {
            return str.field_4_format_runs == null ? 0 : 1;
        }
        if (str.field_4_format_runs == null) {
            return -1;
        }
        int size = list.size();
        if (size != str.field_4_format_runs.size()) {
            return size - str.field_4_format_runs.size();
        }
        for (int i = 0; i < size; i++) {
            FormatRun run1 = this.field_4_format_runs.get(i);
            FormatRun run2 = str.field_4_format_runs.get(i);
            int result2 = run1.compareTo(run2);
            if (result2 != 0) {
                return result2;
            }
        }
        ExtRst extRst = this.field_5_ext_rst;
        if (extRst == null) {
            return str.field_5_ext_rst == null ? 0 : 1;
        }
        ExtRst extRst2 = str.field_5_ext_rst;
        if (extRst2 == null) {
            return -1;
        }
        return extRst.compareTo(extRst2);
    }

    private boolean isRichText() {
        return richText.isSet(getOptionFlags());
    }

    private boolean isExtendedText() {
        return extBit.isSet(getOptionFlags());
    }

    public Object clone() {
        UnicodeString str = new UnicodeString();
        str.field_1_charCount = this.field_1_charCount;
        str.field_2_optionflags = this.field_2_optionflags;
        str.field_3_string = this.field_3_string;
        if (this.field_4_format_runs != null) {
            str.field_4_format_runs = new ArrayList();
            for (FormatRun r : this.field_4_format_runs) {
                str.field_4_format_runs.add(new FormatRun(r._character, r._fontIndex));
            }
        }
        ExtRst extRst = this.field_5_ext_rst;
        if (extRst != null) {
            str.field_5_ext_rst = extRst.clone();
        }
        return str;
    }
}
