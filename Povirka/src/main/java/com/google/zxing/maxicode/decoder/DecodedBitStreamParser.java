package com.google.zxing.maxicode.decoder;

import com.google.zxing.common.DecoderResult;
import java.text.DecimalFormat;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.RefNPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;

/* JADX INFO: loaded from: classes.dex */
final class DecodedBitStreamParser {
    private static final char ECI = 65530;
    private static final char FS = 28;
    private static final char GS = 29;
    private static final char LATCHA = 65527;
    private static final char LATCHB = 65528;
    private static final char LOCK = 65529;
    private static final char NS = 65531;
    private static final char PAD = 65532;
    private static final char RS = 30;
    private static final String[] SETS = {"\nABCDEFGHIJKLMNOPQRSTUVWXYZ\ufffa\u001c\u001d\u001e\ufffb ￼\"#$%&'()*+,-./0123456789:\ufff1\ufff2\ufff3\ufff4\ufff8", "`abcdefghijklmnopqrstuvwxyz\ufffa\u001c\u001d\u001e\ufffb{￼}~\u007f;<=>?[\\]^_ ,./:@!|￼\ufff5\ufff6￼\ufff0\ufff2\ufff3\ufff4\ufff7", "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚ\ufffa\u001c\u001d\u001eÛÜÝÞßª¬±²³µ¹º¼½¾\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\ufff7 \ufff9\ufff3\ufff4\ufff8", "àáâãäåæçèéêëìíîïðñòóôõö÷øùú\ufffa\u001c\u001d\u001e\ufffbûüýþÿ¡¨«¯°´·¸»¿\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\ufff7 \ufff2\ufff9\ufff4\ufff8", "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\ufffa￼￼\u001b\ufffb\u001c\u001d\u001e\u001f\u009f ¢£¤¥¦§©\u00ad®¶\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\ufff7 \ufff2\ufff3\ufff9\ufff8", "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?"};
    private static final char SHIFTA = 65520;
    private static final char SHIFTB = 65521;
    private static final char SHIFTC = 65522;
    private static final char SHIFTD = 65523;
    private static final char SHIFTE = 65524;
    private static final char THREESHIFTA = 65526;
    private static final char TWOSHIFTA = 65525;

    private DecodedBitStreamParser() {
    }

    static DecoderResult decode(byte[] bArr, int i) {
        String postCode3;
        StringBuilder sb = new StringBuilder(144);
        if (i == 2 || i == 3) {
            if (i == 2) {
                postCode3 = new DecimalFormat("0000000000".substring(0, getPostCode2Length(bArr))).format(getPostCode2(bArr));
            } else {
                postCode3 = getPostCode3(bArr);
            }
            DecimalFormat decimalFormat = new DecimalFormat("000");
            String str = decimalFormat.format(getCountry(bArr));
            String str2 = decimalFormat.format(getServiceClass(bArr));
            sb.append(getMessage(bArr, 10, 84));
            if (!sb.toString().startsWith("[)>\u001e01\u001d")) {
                sb.insert(0, postCode3 + GS + str + GS + str2 + GS);
            } else {
                sb.insert(9, postCode3 + GS + str + GS + str2 + GS);
            }
        } else if (i == 4) {
            sb.append(getMessage(bArr, 1, 93));
        } else if (i == 5) {
            sb.append(getMessage(bArr, 1, 77));
        }
        return new DecoderResult(bArr, sb.toString(), null, String.valueOf(i));
    }

    private static int getBit(int bit, byte[] bytes) {
        int bit2 = bit - 1;
        return (bytes[bit2 / 6] & (1 << (5 - (bit2 % 6)))) == 0 ? 0 : 1;
    }

    private static int getInt(byte[] bytes, byte[] x) {
        if (x.length == 0) {
            throw new IllegalArgumentException();
        }
        int val = 0;
        for (int i = 0; i < x.length; i++) {
            val += getBit(x[i], bytes) << ((x.length - i) - 1);
        }
        return val;
    }

    private static int getCountry(byte[] bytes) {
        return getInt(bytes, new byte[]{53, 54, AreaErrPtg.sid, RefNPtg.sid, 45, 46, 47, 48, 37, 38});
    }

    private static int getServiceClass(byte[] bytes) {
        return getInt(bytes, new byte[]{55, PaletteRecord.STANDARD_PALETTE_SIZE, 57, Ref3DPtg.sid, Area3DPtg.sid, DeletedRef3DPtg.sid, 49, 50, 51, 52});
    }

    private static int getPostCode2Length(byte[] bytes) {
        return getInt(bytes, new byte[]{39, 40, MemFuncPtg.sid, RefErrorPtg.sid, NumberPtg.sid, 32});
    }

    private static int getPostCode2(byte[] bytes) {
        return getInt(bytes, new byte[]{33, 34, 35, RefPtg.sid, AttrPtg.sid, 26, 27, 28, BoolPtg.sid, IntPtg.sid, UnaryMinusPtg.sid, PercentPtg.sid, ParenthesisPtg.sid, MissingArgPtg.sid, StringPtg.sid, 24, 13, NotEqualPtg.sid, IntersectionPtg.sid, UnionPtg.sid, RangePtg.sid, UnaryPlusPtg.sid, 7, 8, 9, 10, 11, 12, 1, 2});
    }

    private static String getPostCode3(byte[] bytes) {
        String[] strArr = SETS;
        return String.valueOf(new char[]{strArr[0].charAt(getInt(bytes, new byte[]{39, 40, MemFuncPtg.sid, RefErrorPtg.sid, NumberPtg.sid, 32})), strArr[0].charAt(getInt(bytes, new byte[]{33, 34, 35, RefPtg.sid, AttrPtg.sid, 26})), strArr[0].charAt(getInt(bytes, new byte[]{27, 28, BoolPtg.sid, IntPtg.sid, UnaryMinusPtg.sid, PercentPtg.sid})), strArr[0].charAt(getInt(bytes, new byte[]{ParenthesisPtg.sid, MissingArgPtg.sid, StringPtg.sid, 24, 13, NotEqualPtg.sid})), strArr[0].charAt(getInt(bytes, new byte[]{IntersectionPtg.sid, UnionPtg.sid, RangePtg.sid, UnaryPlusPtg.sid, 7, 8})), strArr[0].charAt(getInt(bytes, new byte[]{9, 10, 11, 12, 1, 2}))});
    }

    /* JADX WARN: Incorrect condition in loop: B:20:0x0076 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String getMessage(byte[] r10, int r11, int r12) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = -1
            r2 = 0
            r3 = 0
            r4 = r11
            r5 = 0
        La:
            int r6 = r11 + r12
            if (r4 >= r6) goto L72
            java.lang.String[] r6 = com.google.zxing.maxicode.decoder.DecodedBitStreamParser.SETS
            r6 = r6[r2]
            r7 = r10[r4]
            char r6 = r6.charAt(r7)
            r5 = r6
            switch(r6) {
                case 65520: goto L60;
                case 65521: goto L60;
                case 65522: goto L60;
                case 65523: goto L60;
                case 65524: goto L60;
                case 65525: goto L5c;
                case 65526: goto L58;
                case 65527: goto L55;
                case 65528: goto L52;
                case 65529: goto L50;
                case 65530: goto L1c;
                case 65531: goto L20;
                default: goto L1c;
            }
        L1c:
            r0.append(r5)
            goto L68
        L20:
            int r4 = r4 + 1
            r6 = r10[r4]
            int r6 = r6 << 24
            int r4 = r4 + 1
            r7 = r10[r4]
            int r7 = r7 << 18
            int r6 = r6 + r7
            int r4 = r4 + 1
            r7 = r10[r4]
            int r7 = r7 << 12
            int r6 = r6 + r7
            int r4 = r4 + 1
            r7 = r10[r4]
            int r7 = r7 << 6
            int r6 = r6 + r7
            int r4 = r4 + 1
            r7 = r10[r4]
            int r6 = r6 + r7
            java.text.DecimalFormat r7 = new java.text.DecimalFormat
            java.lang.String r8 = "000000000"
            r7.<init>(r8)
            long r8 = (long) r6
            java.lang.String r7 = r7.format(r8)
            r0.append(r7)
            goto L68
        L50:
            r1 = -1
            goto L68
        L52:
            r2 = 1
            r1 = -1
            goto L68
        L55:
            r2 = 0
            r1 = -1
            goto L68
        L58:
            r3 = r2
            r2 = 0
            r1 = 3
            goto L68
        L5c:
            r3 = r2
            r2 = 0
            r1 = 2
            goto L68
        L60:
            r3 = r2
            r6 = 65520(0xfff0, float:9.1813E-41)
            int r2 = r5 - r6
            r1 = 1
        L68:
            int r6 = r1 + (-1)
            if (r1 != 0) goto L6e
            r1 = r3
            r2 = r1
        L6e:
            int r4 = r4 + 1
            r1 = r6
            goto La
        L72:
            int r4 = r0.length()
            if (r4 <= 0) goto L91
            int r4 = r0.length()
            int r4 = r4 + (-1)
            char r4 = r0.charAt(r4)
            r5 = 65532(0xfffc, float:9.183E-41)
            if (r4 != r5) goto L91
            int r4 = r0.length()
            int r4 = r4 + (-1)
            r0.setLength(r4)
            goto L72
        L91:
            java.lang.String r4 = r0.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.maxicode.decoder.DecodedBitStreamParser.getMessage(byte[], int, int):java.lang.String");
    }
}
