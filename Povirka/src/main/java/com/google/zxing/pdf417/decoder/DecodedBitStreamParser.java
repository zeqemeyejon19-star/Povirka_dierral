package com.google.zxing.pdf417.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.pdf417.PDF417ResultMetadata;
import java.math.BigInteger;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
final class DecodedBitStreamParser {
    private static final int AL = 28;
    private static final int AS = 27;
    private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
    private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
    private static final int BYTE_COMPACTION_MODE_LATCH = 901;
    private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
    private static final int ECI_CHARSET = 927;
    private static final int ECI_GENERAL_PURPOSE = 926;
    private static final int ECI_USER_DEFINED = 925;
    private static final BigInteger[] EXP900;
    private static final int LL = 27;
    private static final int MACRO_PDF417_TERMINATOR = 922;
    private static final int MAX_NUMERIC_CODEWORDS = 15;
    private static final int ML = 28;
    private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
    private static final int NUMBER_OF_SEQUENCE_CODEWORDS = 2;
    private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
    private static final int PAL = 29;
    private static final int PL = 25;
    private static final int PS = 29;
    private static final int TEXT_COMPACTION_MODE_LATCH = 900;
    private static final char[] PUNCT_CHARS = ";<>@[\\]_`~!\r\t,:\n-.$/\"|*()?{}'".toCharArray();
    private static final char[] MIXED_CHARS = "0123456789&\r\t,:#-.$/+%*=^".toCharArray();

    private enum Mode {
        ALPHA,
        LOWER,
        MIXED,
        PUNCT,
        ALPHA_SHIFT,
        PUNCT_SHIFT
    }

    static {
        BigInteger[] bigIntegerArr = new BigInteger[16];
        EXP900 = bigIntegerArr;
        bigIntegerArr[0] = BigInteger.ONE;
        BigInteger nineHundred = BigInteger.valueOf(900L);
        bigIntegerArr[1] = nineHundred;
        int i = 2;
        while (true) {
            BigInteger[] bigIntegerArr2 = EXP900;
            if (i < bigIntegerArr2.length) {
                bigIntegerArr2[i] = bigIntegerArr2[i - 1].multiply(nineHundred);
                i++;
            } else {
                return;
            }
        }
    }

    private DecodedBitStreamParser() {
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0052  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static com.google.zxing.common.DecoderResult decode(int[] r9, java.lang.String r10) throws com.google.zxing.FormatException {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            int r1 = r9.length
            r2 = 1
            int r1 = r1 << r2
            r0.<init>(r1)
            java.nio.charset.Charset r1 = java.nio.charset.StandardCharsets.ISO_8859_1
            r3 = 1
            int r3 = r3 + r2
            r2 = r9[r2]
            com.google.zxing.pdf417.PDF417ResultMetadata r4 = new com.google.zxing.pdf417.PDF417ResultMetadata
            r4.<init>()
            r5 = 0
            r6 = r5
        L15:
            r7 = 0
            r7 = r9[r7]
            if (r3 >= r7) goto L73
            r7 = 913(0x391, float:1.28E-42)
            if (r2 == r7) goto L5c
            switch(r2) {
                case 900: goto L57;
                case 901: goto L52;
                case 902: goto L4d;
                default: goto L21;
            }
        L21:
            switch(r2) {
                case 922: goto L48;
                case 923: goto L48;
                case 924: goto L52;
                case 925: goto L45;
                case 926: goto L42;
                case 927: goto L30;
                case 928: goto L2b;
                default: goto L24;
            }
        L24:
            int r3 = r3 + (-1)
            int r3 = textCompaction(r9, r3, r0)
            goto L65
        L2b:
            int r3 = decodeMacroBlock(r9, r3, r4)
            goto L65
        L30:
            int r7 = r3 + 1
            r3 = r9[r3]
            com.google.zxing.common.CharacterSetECI r3 = com.google.zxing.common.CharacterSetECI.getCharacterSetECIByValue(r3)
            java.lang.String r3 = r3.name()
            java.nio.charset.Charset r1 = java.nio.charset.Charset.forName(r3)
            r3 = r7
            goto L65
        L42:
            int r3 = r3 + 2
            goto L65
        L45:
            int r3 = r3 + 1
            goto L65
        L48:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        L4d:
            int r3 = numericCompaction(r9, r3, r0)
            goto L65
        L52:
            int r3 = byteCompaction(r2, r9, r1, r3, r0)
            goto L65
        L57:
            int r3 = textCompaction(r9, r3, r0)
            goto L65
        L5c:
            int r7 = r3 + 1
            r3 = r9[r3]
            char r3 = (char) r3
            r0.append(r3)
            r3 = r7
        L65:
            int r7 = r9.length
            if (r3 >= r7) goto L6e
            int r7 = r3 + 1
            r2 = r9[r3]
            r3 = r7
            goto L15
        L6e:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        L73:
            int r7 = r0.length()
            if (r7 == 0) goto L88
            com.google.zxing.common.DecoderResult r7 = new com.google.zxing.common.DecoderResult
            java.lang.String r8 = r0.toString()
            r7.<init>(r5, r8, r5, r10)
            r5 = r6
            r5 = r7
            r7.setOther(r4)
            return r5
        L88:
            com.google.zxing.FormatException r5 = com.google.zxing.FormatException.getFormatInstance()
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.DecodedBitStreamParser.decode(int[], java.lang.String):com.google.zxing.common.DecoderResult");
    }

    private static int decodeMacroBlock(int[] codewords, int codeIndex, PDF417ResultMetadata resultMetadata) throws FormatException {
        if (codeIndex + 2 > codewords[0]) {
            throw FormatException.getFormatInstance();
        }
        int[] segmentIndexArray = new int[2];
        int i = 0;
        while (i < 2) {
            segmentIndexArray[i] = codewords[codeIndex];
            i++;
            codeIndex++;
        }
        resultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(segmentIndexArray, 2)));
        StringBuilder fileId = new StringBuilder();
        int codeIndex2 = textCompaction(codewords, codeIndex, fileId);
        resultMetadata.setFileId(fileId.toString());
        int i2 = codewords[codeIndex2];
        if (i2 == MACRO_PDF417_TERMINATOR) {
            resultMetadata.setLastSegment(true);
            return codeIndex2 + 1;
        }
        if (i2 == BEGIN_MACRO_PDF417_OPTIONAL_FIELD) {
            int codeIndex3 = codeIndex2 + 1;
            int[] additionalOptionCodeWords = new int[codewords[0] - codeIndex3];
            int additionalOptionCodeWordsIndex = 0;
            boolean end = false;
            while (codeIndex3 < codewords[0] && !end) {
                int codeIndex4 = codeIndex3 + 1;
                int code = codewords[codeIndex3];
                if (code < TEXT_COMPACTION_MODE_LATCH) {
                    additionalOptionCodeWords[additionalOptionCodeWordsIndex] = code;
                    additionalOptionCodeWordsIndex++;
                    codeIndex3 = codeIndex4;
                } else if (code == MACRO_PDF417_TERMINATOR) {
                    resultMetadata.setLastSegment(true);
                    codeIndex3 = codeIndex4 + 1;
                    end = true;
                } else {
                    throw FormatException.getFormatInstance();
                }
            }
            resultMetadata.setOptionalData(Arrays.copyOf(additionalOptionCodeWords, additionalOptionCodeWordsIndex));
            return codeIndex3;
        }
        return codeIndex2;
    }

    private static int textCompaction(int[] codewords, int codeIndex, StringBuilder result) {
        int[] textCompactionData = new int[(codewords[0] - codeIndex) << 1];
        int[] byteCompactionData = new int[(codewords[0] - codeIndex) << 1];
        int index = 0;
        boolean end = false;
        while (codeIndex < codewords[0] && !end) {
            int codeIndex2 = codeIndex + 1;
            int code = codewords[codeIndex];
            if (code >= TEXT_COMPACTION_MODE_LATCH) {
                if (code != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                    if (code != 928) {
                        switch (code) {
                            case TEXT_COMPACTION_MODE_LATCH /* 900 */:
                                textCompactionData[index] = TEXT_COMPACTION_MODE_LATCH;
                                index++;
                                codeIndex = codeIndex2;
                                continue;
                            case BYTE_COMPACTION_MODE_LATCH /* 901 */:
                            case NUMERIC_COMPACTION_MODE_LATCH /* 902 */:
                                break;
                            default:
                                switch (code) {
                                    case MACRO_PDF417_TERMINATOR /* 922 */:
                                    case BEGIN_MACRO_PDF417_OPTIONAL_FIELD /* 923 */:
                                    case BYTE_COMPACTION_MODE_LATCH_6 /* 924 */:
                                        break;
                                    default:
                                        codeIndex = codeIndex2;
                                        continue;
                                }
                                break;
                        }
                    }
                    codeIndex = codeIndex2 - 1;
                    end = true;
                } else {
                    textCompactionData[index] = MODE_SHIFT_TO_BYTE_COMPACTION_MODE;
                    codeIndex = codeIndex2 + 1;
                    byteCompactionData[index] = codewords[codeIndex2];
                    index++;
                }
            } else {
                textCompactionData[index] = code / 30;
                textCompactionData[index + 1] = code % 30;
                index += 2;
                codeIndex = codeIndex2;
            }
        }
        decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
        return codeIndex;
    }

    private static void decodeTextCompaction(int[] textCompactionData, int[] byteCompactionData, int length, StringBuilder result) {
        Mode subMode = Mode.ALPHA;
        Mode priorToShiftMode = Mode.ALPHA;
        for (int i = 0; i < length; i++) {
            int subModeCh = textCompactionData[i];
            char ch = 0;
            switch (AnonymousClass1.$SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[subMode.ordinal()]) {
                case 1:
                    if (subModeCh < 26) {
                        ch = (char) (subModeCh + 65);
                        break;
                    } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                        break;
                    } else if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                        switch (subModeCh) {
                            case 26:
                                ch = ' ';
                                break;
                            case 27:
                                subMode = Mode.LOWER;
                                break;
                            case 28:
                                subMode = Mode.MIXED;
                                break;
                            case 29:
                                priorToShiftMode = subMode;
                                subMode = Mode.PUNCT_SHIFT;
                                break;
                        }
                    } else {
                        result.append((char) byteCompactionData[i]);
                        break;
                    }
                    break;
                case 2:
                    if (subModeCh < 26) {
                        ch = (char) (subModeCh + 97);
                        break;
                    } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                        break;
                    } else if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                        switch (subModeCh) {
                            case 26:
                                ch = ' ';
                                break;
                            case 27:
                                priorToShiftMode = subMode;
                                subMode = Mode.ALPHA_SHIFT;
                                break;
                            case 28:
                                subMode = Mode.MIXED;
                                break;
                            case 29:
                                priorToShiftMode = subMode;
                                subMode = Mode.PUNCT_SHIFT;
                                break;
                        }
                    } else {
                        result.append((char) byteCompactionData[i]);
                        break;
                    }
                    break;
                case 3:
                    if (subModeCh < 25) {
                        ch = MIXED_CHARS[subModeCh];
                        break;
                    } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                        break;
                    } else if (subModeCh != MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                        switch (subModeCh) {
                            case 25:
                                subMode = Mode.PUNCT;
                                break;
                            case 26:
                                ch = ' ';
                                break;
                            case 27:
                                subMode = Mode.LOWER;
                                break;
                            case 28:
                                subMode = Mode.ALPHA;
                                break;
                            case 29:
                                priorToShiftMode = subMode;
                                subMode = Mode.PUNCT_SHIFT;
                                break;
                        }
                    } else {
                        result.append((char) byteCompactionData[i]);
                        break;
                    }
                    break;
                case 4:
                    if (subModeCh < 29) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else if (subModeCh == 29 || subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                    } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                        result.append((char) byteCompactionData[i]);
                    }
                    break;
                case 5:
                    subMode = priorToShiftMode;
                    if (subModeCh < 26) {
                        ch = (char) (subModeCh + 65);
                    } else if (subModeCh == 26) {
                        ch = ' ';
                    } else if (subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                    }
                    break;
                case 6:
                    subMode = priorToShiftMode;
                    if (subModeCh < 29) {
                        ch = PUNCT_CHARS[subModeCh];
                    } else if (subModeCh == 29 || subModeCh == TEXT_COMPACTION_MODE_LATCH) {
                        subMode = Mode.ALPHA;
                    } else if (subModeCh == MODE_SHIFT_TO_BYTE_COMPACTION_MODE) {
                        result.append((char) byteCompactionData[i]);
                    }
                    break;
            }
            if (ch != 0) {
                result.append(ch);
            }
        }
    }

    /* JADX INFO: renamed from: com.google.zxing.pdf417.decoder.DecodedBitStreamParser$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode;

        static {
            int[] iArr = new int[Mode.values().length];
            $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode = iArr;
            try {
                iArr[Mode.ALPHA.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.LOWER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.MIXED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.ALPHA_SHIFT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$google$zxing$pdf417$decoder$DecodedBitStreamParser$Mode[Mode.PUNCT_SHIFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x0048 A[FALL_THROUGH] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int byteCompaction(int r19, int[] r20, java.nio.charset.Charset r21, int r22, java.lang.StringBuilder r23) {
        /*
            Method dump skipped, instruction units count: 266
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.DecodedBitStreamParser.byteCompaction(int, int[], java.nio.charset.Charset, int, java.lang.StringBuilder):int");
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x002d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int numericCompaction(int[] r5, int r6, java.lang.StringBuilder r7) throws com.google.zxing.FormatException {
        /*
            r0 = 0
            r1 = 0
            r2 = 15
            int[] r2 = new int[r2]
        L6:
            r3 = 0
            r4 = r5[r3]
            if (r6 >= r4) goto L46
            if (r1 != 0) goto L46
            int r4 = r6 + 1
            r6 = r5[r6]
            r3 = r5[r3]
            if (r4 != r3) goto L16
            r1 = 1
        L16:
            r3 = 900(0x384, float:1.261E-42)
            if (r6 >= r3) goto L1f
            r2[r0] = r6
            int r0 = r0 + 1
            goto L30
        L1f:
            if (r6 == r3) goto L2d
            r3 = 901(0x385, float:1.263E-42)
            if (r6 == r3) goto L2d
            r3 = 928(0x3a0, float:1.3E-42)
            if (r6 == r3) goto L2d
            switch(r6) {
                case 922: goto L2d;
                case 923: goto L2d;
                case 924: goto L2d;
                default: goto L2c;
            }
        L2c:
            goto L30
        L2d:
            int r4 = r4 + (-1)
            r1 = 1
        L30:
            int r3 = r0 % 15
            if (r3 == 0) goto L3a
            r3 = 902(0x386, float:1.264E-42)
            if (r6 == r3) goto L3a
            if (r1 == 0) goto L44
        L3a:
            if (r0 <= 0) goto L44
            java.lang.String r3 = decodeBase900toBase10(r2, r0)
            r7.append(r3)
            r0 = 0
        L44:
            r6 = r4
            goto L6
        L46:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.pdf417.decoder.DecodedBitStreamParser.numericCompaction(int[], int, java.lang.StringBuilder):int");
    }

    private static String decodeBase900toBase10(int[] iArr, int i) throws FormatException {
        BigInteger bigIntegerAdd = BigInteger.ZERO;
        for (int i2 = 0; i2 < i; i2++) {
            bigIntegerAdd = bigIntegerAdd.add(EXP900[(i - i2) - 1].multiply(BigInteger.valueOf(iArr[i2])));
        }
        String string = bigIntegerAdd.toString();
        if (string.charAt(0) != '1') {
            throw FormatException.getFormatInstance();
        }
        return string.substring(1);
    }
}
