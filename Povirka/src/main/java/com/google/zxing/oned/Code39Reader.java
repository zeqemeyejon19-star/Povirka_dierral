package com.google.zxing.oned;

import androidx.appcompat.widget.ActivityChooserView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFShapeTypes;

/* JADX INFO: loaded from: classes.dex */
public final class Code39Reader extends OneDReader {
    static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";
    static final int ASTERISK_ENCODING = 148;
    static final int[] CHARACTER_ENCODINGS = {52, 289, 97, 352, 49, 304, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, HSSFShapeTypes.ActionButtonForwardNext, 448, 145, 400, 208, 133, 388, HSSFShapeTypes.ActionButtonBeginning, 168, 162, 138, 42};
    private final int[] counters;
    private final StringBuilder decodeRowResult;
    private final boolean extendedMode;
    private final boolean usingCheckDigit;

    public Code39Reader() {
        this(false);
    }

    public Code39Reader(boolean usingCheckDigit) {
        this(usingCheckDigit, false);
    }

    public Code39Reader(boolean usingCheckDigit, boolean extendedMode) {
        this.usingCheckDigit = usingCheckDigit;
        this.extendedMode = extendedMode;
        this.decodeRowResult = new StringBuilder(20);
        this.counters = new int[9];
    }

    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        String resultString;
        Code39Reader code39Reader = this;
        BitArray bitArray = row;
        int[] theCounters = code39Reader.counters;
        Arrays.fill(theCounters, 0);
        StringBuilder result = code39Reader.decodeRowResult;
        result.setLength(0);
        int[] start = findAsteriskPattern(bitArray, theCounters);
        int i = 1;
        int nextStart = bitArray.getNextSet(start[1]);
        int end = row.getSize();
        while (true) {
            recordPattern(bitArray, nextStart, theCounters);
            int pattern = toNarrowWidePattern(theCounters);
            if (pattern < 0) {
                throw NotFoundException.getNotFoundInstance();
            }
            char decodedChar = patternToChar(pattern);
            result.append(decodedChar);
            int lastStart = nextStart;
            for (int counter : theCounters) {
                nextStart += counter;
            }
            nextStart = bitArray.getNextSet(nextStart);
            if (decodedChar == '*') {
                result.setLength(result.length() - i);
                int lastPatternSize = 0;
                for (int counter2 : theCounters) {
                    lastPatternSize += counter2;
                }
                int whiteSpaceAfterEnd = (nextStart - lastStart) - lastPatternSize;
                if (nextStart != end && (whiteSpaceAfterEnd << 1) < lastPatternSize) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (code39Reader.usingCheckDigit) {
                    int max = result.length() - i;
                    int total = 0;
                    for (int i2 = 0; i2 < max; i2++) {
                        total += ALPHABET_STRING.indexOf(code39Reader.decodeRowResult.charAt(i2));
                    }
                    int i3 = result.charAt(max);
                    if (i3 != ALPHABET_STRING.charAt(total % 43)) {
                        throw ChecksumException.getChecksumInstance();
                    }
                    result.setLength(max);
                }
                if (result.length() == 0) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (code39Reader.extendedMode) {
                    resultString = decodeExtended(result);
                } else {
                    resultString = result.toString();
                }
                float left = (start[1] + start[0]) / 2.0f;
                float right = lastStart + (lastPatternSize / 2.0f);
                return new Result(resultString, null, new ResultPoint[]{new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber)}, BarcodeFormat.CODE_39);
            }
            bitArray = row;
            start = start;
            i = 1;
            code39Reader = this;
        }
    }

    private static int[] findAsteriskPattern(BitArray row, int[] counters) throws NotFoundException {
        int width = row.getSize();
        int rowOffset = row.getNextSet(0);
        int counterPosition = 0;
        int patternStart = rowOffset;
        boolean isWhite = false;
        int patternLength = counters.length;
        for (int i = rowOffset; i < width; i++) {
            if (row.get(i) != isWhite) {
                counters[counterPosition] = counters[counterPosition] + 1;
            } else {
                if (counterPosition == patternLength - 1) {
                    if (toNarrowWidePattern(counters) == 148 && row.isRange(Math.max(0, patternStart - ((i - patternStart) / 2)), patternStart, false)) {
                        return new int[]{patternStart, i};
                    }
                    patternStart += counters[0] + counters[1];
                    System.arraycopy(counters, 2, counters, 0, counterPosition - 1);
                    counters[counterPosition - 1] = 0;
                    counters[counterPosition] = 0;
                    counterPosition--;
                } else {
                    counterPosition++;
                }
                counters[counterPosition] = 1;
                isWhite = isWhite ? false : true;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int toNarrowWidePattern(int[] counters) {
        int wideCounters;
        int numCounters = counters.length;
        int maxNarrowCounter = 0;
        do {
            int minCounter = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            for (int counter : counters) {
                if (counter < minCounter && counter > maxNarrowCounter) {
                    minCounter = counter;
                }
            }
            maxNarrowCounter = minCounter;
            wideCounters = 0;
            int totalWideCountersWidth = 0;
            int pattern = 0;
            for (int i = 0; i < numCounters; i++) {
                int counter2 = counters[i];
                if (counter2 > maxNarrowCounter) {
                    pattern |= 1 << ((numCounters - 1) - i);
                    wideCounters++;
                    totalWideCountersWidth += counter2;
                }
            }
            if (wideCounters == 3) {
                for (int i2 = 0; i2 < numCounters && wideCounters > 0; i2++) {
                    int counter3 = counters[i2];
                    if (counter3 > maxNarrowCounter) {
                        wideCounters--;
                        if ((counter3 << 1) >= totalWideCountersWidth) {
                            return -1;
                        }
                    }
                }
                return pattern;
            }
        } while (wideCounters > 3);
        return -1;
    }

    private static char patternToChar(int pattern) throws NotFoundException {
        int i = 0;
        while (true) {
            int[] iArr = CHARACTER_ENCODINGS;
            if (i < iArr.length) {
                if (iArr[i] != pattern) {
                    i++;
                } else {
                    return ALPHABET_STRING.charAt(i);
                }
            } else {
                if (pattern == 148) {
                    return '*';
                }
                throw NotFoundException.getNotFoundInstance();
            }
        }
    }

    private static String decodeExtended(CharSequence encoded) throws FormatException {
        int length = encoded.length();
        StringBuilder decoded = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            char c = encoded.charAt(i);
            if (c == '+' || c == '$' || c == '%' || c == '/') {
                char next = encoded.charAt(i + 1);
                char decodedChar = 0;
                if (c != '$') {
                    if (c != '%') {
                        if (c != '+') {
                            if (c == '/') {
                                if (next >= 'A' && next <= 'O') {
                                    decodedChar = (char) (next - ' ');
                                } else if (next == 'Z') {
                                    decodedChar = ':';
                                } else {
                                    throw FormatException.getFormatInstance();
                                }
                            }
                        } else if (next >= 'A' && next <= 'Z') {
                            decodedChar = (char) (next + ' ');
                        } else {
                            throw FormatException.getFormatInstance();
                        }
                    } else if (next >= 'A' && next <= 'E') {
                        decodedChar = (char) (next - '&');
                    } else if (next >= 'F' && next <= 'J') {
                        decodedChar = (char) (next - 11);
                    } else if (next >= 'K' && next <= 'O') {
                        decodedChar = (char) (next + 16);
                    } else if (next >= 'P' && next <= 'T') {
                        decodedChar = (char) (next + '+');
                    } else if (next == 'U') {
                        decodedChar = 0;
                    } else if (next == 'V') {
                        decodedChar = '@';
                    } else if (next == 'W') {
                        decodedChar = '`';
                    } else if (next == 'X' || next == 'Y' || next == 'Z') {
                        decodedChar = 127;
                    } else {
                        throw FormatException.getFormatInstance();
                    }
                } else if (next >= 'A' && next <= 'Z') {
                    decodedChar = (char) (next - '@');
                } else {
                    throw FormatException.getFormatInstance();
                }
                decoded.append(decodedChar);
                i++;
            } else {
                decoded.append(c);
            }
            i++;
        }
        return decoded.toString();
    }
}
