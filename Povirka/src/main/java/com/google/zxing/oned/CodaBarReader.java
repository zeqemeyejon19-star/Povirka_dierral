package com.google.zxing.oned;

import androidx.appcompat.widget.ActivityChooserView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class CodaBarReader extends OneDReader {
    private static final float MAX_ACCEPTABLE = 2.0f;
    private static final int MIN_CHARACTER_LENGTH = 3;
    private static final float PADDING = 1.5f;
    private static final String ALPHABET_STRING = "0123456789-$:/.+ABCD";
    static final char[] ALPHABET = ALPHABET_STRING.toCharArray();
    static final int[] CHARACTER_ENCODINGS = {3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 69, 81, 84, 21, 26, 41, 11, 14};
    private static final char[] STARTEND_ENCODING = {'A', 'B', 'C', 'D'};
    private final StringBuilder decodeRowResult = new StringBuilder(20);
    private int[] counters = new int[80];
    private int counterLength = 0;

    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
        CodaBarReader codaBarReader = this;
        Map<DecodeHintType, ?> map = hints;
        int i = 0;
        Arrays.fill(codaBarReader.counters, 0);
        codaBarReader.setCounters(row);
        int nextStart = findStartPattern();
        codaBarReader.decodeRowResult.setLength(0);
        while (true) {
            int charOffset = codaBarReader.toNarrowWidePattern(nextStart);
            if (charOffset != -1) {
                codaBarReader.decodeRowResult.append((char) charOffset);
                nextStart += 8;
                if ((codaBarReader.decodeRowResult.length() > 1 && arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) || nextStart >= codaBarReader.counterLength) {
                    break;
                }
                i = 0;
                codaBarReader = this;
                map = hints;
            } else {
                throw NotFoundException.getNotFoundInstance();
            }
        }
        int trailingWhitespace = codaBarReader.counters[nextStart - 1];
        int lastPatternSize = 0;
        for (int i2 = -8; i2 < -1; i2++) {
            lastPatternSize += codaBarReader.counters[nextStart + i2];
        }
        if (nextStart < codaBarReader.counterLength && trailingWhitespace < lastPatternSize / 2) {
            throw NotFoundException.getNotFoundInstance();
        }
        codaBarReader.validatePattern(nextStart);
        for (int i3 = 0; i3 < codaBarReader.decodeRowResult.length(); i3++) {
            StringBuilder sb = codaBarReader.decodeRowResult;
            sb.setCharAt(i3, ALPHABET[sb.charAt(i3)]);
        }
        char startchar = codaBarReader.decodeRowResult.charAt(i);
        char[] cArr = STARTEND_ENCODING;
        if (!arrayContains(cArr, startchar)) {
            throw NotFoundException.getNotFoundInstance();
        }
        StringBuilder sb2 = codaBarReader.decodeRowResult;
        char endchar = sb2.charAt(sb2.length() - 1);
        if (!arrayContains(cArr, endchar)) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (codaBarReader.decodeRowResult.length() <= 3) {
            throw NotFoundException.getNotFoundInstance();
        }
        if (map == null || !map.containsKey(DecodeHintType.RETURN_CODABAR_START_END)) {
            StringBuilder sb3 = codaBarReader.decodeRowResult;
            sb3.deleteCharAt(sb3.length() - 1);
            codaBarReader.decodeRowResult.deleteCharAt(i);
        }
        int runningCount = 0;
        for (int i4 = 0; i4 < nextStart; i4++) {
            runningCount += codaBarReader.counters[i4];
        }
        float left = runningCount;
        for (int i5 = nextStart; i5 < nextStart - 1; i5++) {
            runningCount += codaBarReader.counters[i5];
        }
        float right = runningCount;
        return new Result(codaBarReader.decodeRowResult.toString(), null, new ResultPoint[]{new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber)}, BarcodeFormat.CODABAR);
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x00be, code lost:
    
        throw com.google.zxing.NotFoundException.getNotFoundInstance();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void validatePattern(int r14) throws com.google.zxing.NotFoundException {
        /*
            Method dump skipped, instruction units count: 224
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.oned.CodaBarReader.validatePattern(int):void");
    }

    private void setCounters(BitArray row) throws NotFoundException {
        this.counterLength = 0;
        int i = row.getNextUnset(0);
        int end = row.getSize();
        if (i >= end) {
            throw NotFoundException.getNotFoundInstance();
        }
        boolean isWhite = true;
        int count = 0;
        while (i < end) {
            if (row.get(i) != isWhite) {
                count++;
            } else {
                counterAppend(count);
                count = 1;
                isWhite = !isWhite;
            }
            i++;
        }
        counterAppend(count);
    }

    private void counterAppend(int e) {
        int[] iArr = this.counters;
        int i = this.counterLength;
        iArr[i] = e;
        int i2 = i + 1;
        this.counterLength = i2;
        if (i2 >= iArr.length) {
            int[] temp = new int[i2 << 1];
            System.arraycopy(iArr, 0, temp, 0, i2);
            this.counters = temp;
        }
    }

    private int findStartPattern() throws NotFoundException {
        for (int i = 1; i < this.counterLength; i += 2) {
            int charOffset = toNarrowWidePattern(i);
            if (charOffset != -1 && arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) {
                int patternSize = 0;
                for (int j = i; j < i + 7; j++) {
                    patternSize += this.counters[j];
                }
                if (i == 1 || this.counters[i - 1] >= patternSize / 2) {
                    return i;
                }
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    static boolean arrayContains(char[] array, char key) {
        if (array != null) {
            for (char c : array) {
                if (c == key) {
                    return true;
                }
            }
        }
        return false;
    }

    private int toNarrowWidePattern(int position) {
        int end = position + 7;
        if (end >= this.counterLength) {
            return -1;
        }
        int[] theCounters = this.counters;
        int maxBar = 0;
        int minBar = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (int j = position; j < end; j += 2) {
            int currentCounter = theCounters[j];
            if (currentCounter < minBar) {
                minBar = currentCounter;
            }
            if (currentCounter > maxBar) {
                maxBar = currentCounter;
            }
        }
        int j2 = minBar + maxBar;
        int thresholdBar = j2 / 2;
        int maxSpace = 0;
        int minSpace = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        for (int j3 = position + 1; j3 < end; j3 += 2) {
            int currentCounter2 = theCounters[j3];
            if (currentCounter2 < minSpace) {
                minSpace = currentCounter2;
            }
            if (currentCounter2 > maxSpace) {
                maxSpace = currentCounter2;
            }
        }
        int thresholdSpace = (minSpace + maxSpace) / 2;
        int bitmask = 128;
        int pattern = 0;
        for (int i = 0; i < 7; i++) {
            int threshold = (i & 1) == 0 ? thresholdBar : thresholdSpace;
            bitmask >>= 1;
            if (theCounters[position + i] > threshold) {
                pattern |= bitmask;
            }
        }
        int i2 = 0;
        while (true) {
            int[] iArr = CHARACTER_ENCODINGS;
            if (i2 >= iArr.length) {
                return -1;
            }
            if (iArr[i2] != pattern) {
                i2++;
            } else {
                return i2;
            }
        }
    }
}
