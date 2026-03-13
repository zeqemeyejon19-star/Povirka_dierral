package org.apache.poi.ss.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: classes.dex */
final class NormalisedDecimal {
    private static final BigDecimal BD_2_POW_24 = new BigDecimal(BigInteger.ONE.shiftLeft(24));
    private static final int C_2_POW_19 = 524288;
    private static final int EXPONENT_OFFSET = 14;
    private static final int FRAC_HALF = 8388608;
    private static final int LOG_BASE_10_OF_2_TIMES_2_POW_20 = 315653;
    private static final long MAX_REP_WHOLE_PART = 1000000000000000L;
    private final int _fractionalPart;
    private final int _relativeDecimalExponent;
    private final long _wholePart;

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0056  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0065  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static org.apache.poi.ss.util.NormalisedDecimal create(java.math.BigInteger r5, int r6) {
        /*
            r0 = 49
            if (r6 > r0) goto Lb
            r0 = 46
            if (r6 >= r0) goto L9
            goto Lb
        L9:
            r0 = 0
            goto L1a
        Lb:
            r0 = 15204352(0xe80000, float:2.1305835E-38)
            r1 = 315653(0x4d105, float:4.42324E-40)
            int r1 = r1 * r6
            int r0 = r0 - r1
            r1 = 524288(0x80000, float:7.34684E-40)
            int r0 = r0 + r1
            int r1 = r0 >> 20
            int r0 = -r1
        L1a:
            org.apache.poi.ss.util.MutableFPNumber r1 = new org.apache.poi.ss.util.MutableFPNumber
            r1.<init>(r5, r6)
            if (r0 == 0) goto L25
            int r2 = -r0
            r1.multiplyByPowerOfTen(r2)
        L25:
            int r2 = r1.get64BitNormalisedExponent()
            switch(r2) {
                case 44: goto L65;
                case 45: goto L65;
                case 46: goto L5e;
                case 47: goto L5d;
                case 48: goto L5d;
                case 49: goto L4f;
                case 50: goto L56;
                default: goto L2c;
            }
        L2c:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Bad binary exp "
            java.lang.StringBuilder r3 = r3.append(r4)
            int r4 = r1.get64BitNormalisedExponent()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = "."
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L4f:
            boolean r2 = r1.isBelowMaxRep()
            if (r2 == 0) goto L56
            goto L6c
        L56:
            r2 = -1
            r1.multiplyByPowerOfTen(r2)
            int r0 = r0 + 1
            goto L6c
        L5d:
            goto L6c
        L5e:
            boolean r2 = r1.isAboveMinRep()
            if (r2 == 0) goto L65
            goto L6c
        L65:
            r2 = 1
            r1.multiplyByPowerOfTen(r2)
            int r0 = r0 + (-1)
        L6c:
            r1.normalise64bit()
            org.apache.poi.ss.util.NormalisedDecimal r2 = r1.createNormalisedDecimal(r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.util.NormalisedDecimal.create(java.math.BigInteger, int):org.apache.poi.ss.util.NormalisedDecimal");
    }

    public NormalisedDecimal roundUnits() {
        long wholePart = this._wholePart;
        if (this._fractionalPart >= 8388608) {
            wholePart++;
        }
        int de = this._relativeDecimalExponent;
        if (wholePart < MAX_REP_WHOLE_PART) {
            return new NormalisedDecimal(wholePart, 0, de);
        }
        return new NormalisedDecimal(wholePart / 10, 0, de + 1);
    }

    NormalisedDecimal(long wholePart, int fracPart, int decimalExponent) {
        this._wholePart = wholePart;
        this._fractionalPart = fracPart;
        this._relativeDecimalExponent = decimalExponent;
    }

    public ExpandedDouble normaliseBaseTwo() {
        MutableFPNumber cc = new MutableFPNumber(composeFrac(), 39);
        cc.multiplyByPowerOfTen(this._relativeDecimalExponent);
        cc.normalise64bit();
        return cc.createExpandedDouble();
    }

    BigInteger composeFrac() {
        long wp = this._wholePart;
        int fp = this._fractionalPart;
        return new BigInteger(new byte[]{(byte) (wp >> 56), (byte) (wp >> 48), (byte) (wp >> 40), (byte) (wp >> 32), (byte) (wp >> 24), (byte) (wp >> 16), (byte) (wp >> 8), (byte) (wp >> 0), (byte) (fp >> 16), (byte) (fp >> 8), (byte) (fp >> 0)});
    }

    public String getSignificantDecimalDigits() {
        return Long.toString(this._wholePart);
    }

    public String getSignificantDecimalDigitsLastDigitRounded() {
        long wp = this._wholePart + 5;
        StringBuilder sb = new StringBuilder(24);
        sb.append(wp);
        sb.setCharAt(sb.length() - 1, '0');
        return sb.toString();
    }

    public int getDecimalExponent() {
        return this._relativeDecimalExponent + 14;
    }

    public int compareNormalised(NormalisedDecimal other) {
        int cmp = this._relativeDecimalExponent - other._relativeDecimalExponent;
        if (cmp != 0) {
            return cmp;
        }
        long j = this._wholePart;
        long j2 = other._wholePart;
        if (j > j2) {
            return 1;
        }
        if (j < j2) {
            return -1;
        }
        return this._fractionalPart - other._fractionalPart;
    }

    public BigDecimal getFractionalPart() {
        return new BigDecimal(this._fractionalPart).divide(BD_2_POW_24);
    }

    private String getFractionalDigits() {
        if (this._fractionalPart == 0) {
            return "0";
        }
        return getFractionalPart().toString().substring(2);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" [");
        String ws = String.valueOf(this._wholePart);
        sb.append(ws.charAt(0));
        sb.append('.');
        sb.append(ws.substring(1));
        sb.append(' ');
        sb.append(getFractionalDigits());
        sb.append("E");
        sb.append(getDecimalExponent());
        sb.append("]");
        return sb.toString();
    }
}
