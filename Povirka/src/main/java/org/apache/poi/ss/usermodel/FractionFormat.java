package org.apache.poi.ss.usermodel;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.format.SimpleFraction;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class FractionFormat extends Format {
    private static final int MAX_DENOM_POW = 4;
    private final int exactDenom;
    private final int maxDenom;
    private final String wholePartFormatString;
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) FractionFormat.class);
    private static final Pattern DENOM_FORMAT_PATTERN = Pattern.compile("(?:(#+)|(\\d+))");

    public FractionFormat(String wholePartFormatString, String denomFormatString) {
        this.wholePartFormatString = wholePartFormatString;
        Matcher m = DENOM_FORMAT_PATTERN.matcher(denomFormatString);
        int tmpExact = -1;
        int tmpMax = -1;
        if (m.find()) {
            if (m.group(2) != null) {
                try {
                    tmpExact = Integer.parseInt(m.group(2));
                    if (tmpExact == 0) {
                        tmpExact = -1;
                    }
                } catch (NumberFormatException e) {
                }
            } else if (m.group(1) != null) {
                int len = m.group(1).length();
                tmpMax = (int) Math.pow(10.0d, len <= 4 ? len : 4);
            } else {
                tmpExact = 100;
            }
        }
        if (tmpExact <= 0 && tmpMax <= 0) {
            tmpExact = 100;
        }
        this.exactDenom = tmpExact;
        this.maxDenom = tmpMax;
    }

    public String format(Number num) {
        SimpleFraction fract;
        double doubleValue = num.doubleValue();
        boolean isNeg = doubleValue < 0.0d;
        double absDoubleValue = Math.abs(doubleValue);
        double wholePart = Math.floor(absDoubleValue);
        double decPart = absDoubleValue - wholePart;
        if (wholePart + decPart == 0.0d) {
            return "0";
        }
        if (Double.compare(decPart, 0.0d) == 0) {
            StringBuilder sb = new StringBuilder();
            if (isNeg) {
                sb.append("-");
            }
            sb.append((int) wholePart);
            return sb.toString();
        }
        try {
            int i = this.exactDenom;
            if (i > 0) {
                fract = SimpleFraction.buildFractionExactDenominator(decPart, i);
            } else {
                fract = SimpleFraction.buildFractionMaxDenominator(decPart, this.maxDenom);
            }
            StringBuilder sb2 = new StringBuilder();
            if (isNeg) {
                sb2.append("-");
            }
            if ("".equals(this.wholePartFormatString)) {
                int trueNum = (fract.getDenominator() * ((int) wholePart)) + fract.getNumerator();
                sb2.append(trueNum).append("/").append(fract.getDenominator());
                return sb2.toString();
            }
            if (fract.getNumerator() == 0) {
                sb2.append(Integer.toString((int) wholePart));
                return sb2.toString();
            }
            if (fract.getNumerator() == fract.getDenominator()) {
                sb2.append(Integer.toString(((int) wholePart) + 1));
                return sb2.toString();
            }
            if (wholePart > 0.0d) {
                sb2.append(Integer.toString((int) wholePart)).append(" ");
            }
            sb2.append(fract.getNumerator()).append("/").append(fract.getDenominator());
            return sb2.toString();
        } catch (RuntimeException e) {
            LOGGER.log(5, "Can't format fraction", e);
            return Double.toString(doubleValue);
        }
    }

    @Override // java.text.Format
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        return toAppendTo.append(format((Number) obj));
    }

    @Override // java.text.Format
    public Object parseObject(String source, ParsePosition pos) {
        throw new NotImplementedException("Reverse parsing not supported");
    }
}
