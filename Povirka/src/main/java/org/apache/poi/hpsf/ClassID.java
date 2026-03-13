package org.apache.poi.hpsf;

import java.util.Arrays;
import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes.dex */
public class ClassID {
    public static final int LENGTH = 16;
    private final byte[] bytes;
    public static final ClassID OLE10_PACKAGE = new ClassID("{0003000C-0000-0000-C000-000000000046}");
    public static final ClassID PPT_SHOW = new ClassID("{64818D10-4F9B-11CF-86EA-00AA00B929E8}");
    public static final ClassID XLS_WORKBOOK = new ClassID("{00020841-0000-0000-C000-000000000046}");
    public static final ClassID TXT_ONLY = new ClassID("{5e941d80-bf96-11cd-b579-08002b30bfeb}");
    public static final ClassID EXCEL_V3 = new ClassID("{00030000-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL_V3_CHART = new ClassID("{00030001-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL_V3_MACRO = new ClassID("{00030002-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL95 = new ClassID("{00020810-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL95_CHART = new ClassID("{00020811-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL97 = new ClassID("{00020820-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL97_CHART = new ClassID("{00020821-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2003 = new ClassID("{00020812-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2007 = new ClassID("{00020830-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2007_MACRO = new ClassID("{00020832-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2007_XLSB = new ClassID("{00020833-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2010 = new ClassID("{00024500-0000-0000-C000-000000000046}");
    public static final ClassID EXCEL2010_CHART = new ClassID("{00024505-0014-0000-C000-000000000046}");
    public static final ClassID EXCEL2010_ODS = new ClassID("{EABCECDB-CC1C-4A6F-B4E3-7F888A5ADFC8}");
    public static final ClassID WORD97 = new ClassID("{00020906-0000-0000-C000-000000000046}");
    public static final ClassID WORD95 = new ClassID("{00020900-0000-0000-C000-000000000046}");
    public static final ClassID WORD2007 = new ClassID("{F4754C9B-64F5-4B40-8AF4-679732AC0607}");
    public static final ClassID WORD2007_MACRO = new ClassID("{18A06B6B-2F3F-4E2B-A611-52BE631B2D22}");
    public static final ClassID POWERPOINT97 = new ClassID("{64818D10-4F9B-11CF-86EA-00AA00B929E8}");
    public static final ClassID POWERPOINT95 = new ClassID("{EA7BAE70-FB3B-11CD-A903-00AA00510EA3}");
    public static final ClassID POWERPOINT2007 = new ClassID("{CF4F55F4-8F87-4D47-80BB-5808164BB3F8}");
    public static final ClassID POWERPOINT2007_MACRO = new ClassID("{DC020317-E6E2-4A62-B9FA-B3EFE16626F4}");
    public static final ClassID EQUATION30 = new ClassID("{0002CE02-0000-0000-C000-000000000046}");

    public ClassID(byte[] src, int offset) {
        this.bytes = new byte[16];
        read(src, offset);
    }

    public ClassID() {
        byte[] bArr = new byte[16];
        this.bytes = bArr;
        Arrays.fill(bArr, (byte) 0);
    }

    public ClassID(String externalForm) {
        this.bytes = new byte[16];
        String clsStr = externalForm.replaceAll("[{}-]", "");
        for (int i = 0; i < clsStr.length(); i += 2) {
            this.bytes[i / 2] = (byte) Integer.parseInt(clsStr.substring(i, i + 2), 16);
        }
    }

    public int length() {
        return 16;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, this.bytes, 0, 16);
    }

    public byte[] read(byte[] src, int offset) {
        byte[] bArr = this.bytes;
        bArr[0] = src[offset + 3];
        bArr[1] = src[offset + 2];
        bArr[2] = src[offset + 1];
        bArr[3] = src[offset + 0];
        bArr[4] = src[offset + 5];
        bArr[5] = src[offset + 4];
        bArr[6] = src[offset + 7];
        bArr[7] = src[offset + 6];
        System.arraycopy(src, offset + 8, bArr, 8, 8);
        return this.bytes;
    }

    public void write(byte[] dst, int offset) throws ArrayStoreException {
        if (dst.length < 16) {
            throw new ArrayStoreException("Destination byte[] must have room for at least 16 bytes, but has a length of only " + dst.length + ".");
        }
        byte[] bArr = this.bytes;
        dst[offset + 0] = bArr[3];
        dst[offset + 1] = bArr[2];
        dst[offset + 2] = bArr[1];
        dst[offset + 3] = bArr[0];
        dst[offset + 4] = bArr[5];
        dst[offset + 5] = bArr[4];
        dst[offset + 6] = bArr[7];
        dst[offset + 7] = bArr[6];
        System.arraycopy(bArr, 8, dst, offset + 8, 8);
    }

    public boolean equals(Object o) {
        return (o instanceof ClassID) && Arrays.equals(this.bytes, ((ClassID) o).bytes);
    }

    public boolean equalsInverted(ClassID o) {
        byte[] bArr = o.bytes;
        byte b = bArr[0];
        byte[] bArr2 = this.bytes;
        return b == bArr2[3] && bArr[1] == bArr2[2] && bArr[2] == bArr2[1] && bArr[3] == bArr2[0] && bArr[4] == bArr2[5] && bArr[5] == bArr2[4] && bArr[6] == bArr2[7] && bArr[7] == bArr2[6] && bArr[8] == bArr2[8] && bArr[9] == bArr2[9] && bArr[10] == bArr2[10] && bArr[11] == bArr2[11] && bArr[12] == bArr2[12] && bArr[13] == bArr2[13] && bArr[14] == bArr2[14] && bArr[15] == bArr2[15];
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String toString() {
        StringBuilder sbClassId = new StringBuilder(38);
        sbClassId.append('{');
        for (int i = 0; i < 16; i++) {
            sbClassId.append(HexDump.toHex(this.bytes[i]));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                sbClassId.append('-');
            }
        }
        sbClassId.append('}');
        return sbClassId.toString();
    }
}
