package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class Code39Writer extends OneDimensionalCodeWriter {
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.CODE_39) {
            throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
        }
        return super.encode(contents, format, width, height, hints);
    }

    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String contents) {
        int length = contents.length();
        int length2 = length;
        if (length > 80) {
            throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length2);
        }
        int i = 0;
        while (true) {
            if (i >= length2) {
                break;
            }
            if ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%".indexOf(contents.charAt(i)) >= 0) {
                i++;
            } else {
                String strTryToConvertToExtendedMode = tryToConvertToExtendedMode(contents);
                contents = strTryToConvertToExtendedMode;
                int length3 = strTryToConvertToExtendedMode.length();
                length2 = length3;
                if (length3 > 80) {
                    throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length2 + " (extended full ASCII mode)");
                }
            }
        }
        int[] widths = new int[9];
        int codeWidth = length2 + 25;
        for (int i2 = 0; i2 < length2; i2++) {
            int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%".indexOf(contents.charAt(i2));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            for (int i3 = 0; i3 < 9; i3++) {
                int width = widths[i3];
                codeWidth += width;
            }
        }
        boolean[] result = new boolean[codeWidth];
        toIntArray(148, widths);
        int pos = appendPattern(result, 0, widths, true);
        int[] narrowWhite = {1};
        int pos2 = pos + appendPattern(result, pos, narrowWhite, false);
        for (int i4 = 0; i4 < length2; i4++) {
            int indexInString2 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%".indexOf(contents.charAt(i4));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString2], widths);
            int pos3 = appendPattern(result, pos2, widths, true) + pos2;
            pos2 = pos3 + appendPattern(result, pos3, narrowWhite, false);
        }
        toIntArray(148, widths);
        appendPattern(result, pos2, widths, true);
        return result;
    }

    private static void toIntArray(int a, int[] toReturn) {
        for (int i = 0; i < 9; i++) {
            int i2 = 1;
            int temp = (1 << (8 - i)) & a;
            if (temp != 0) {
                i2 = 2;
            }
            toReturn[i] = i2;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:59:0x0100  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String tryToConvertToExtendedMode(java.lang.String r10) {
        /*
            Method dump skipped, instruction units count: 275
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.Code39Writer.tryToConvertToExtendedMode(java.lang.String):java.lang.String");
    }
}
