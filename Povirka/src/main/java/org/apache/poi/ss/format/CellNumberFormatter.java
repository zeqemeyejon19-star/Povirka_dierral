package org.apache.poi.ss.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class CellNumberFormatter extends CellFormatter {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) CellNumberFormatter.class);
    private final CellFormatter SIMPLE_NUMBER;
    private final Special afterFractional;
    private final Special afterInteger;
    private final DecimalFormat decimalFmt;
    private final Special decimalPoint;
    private final String denominatorFmt;
    private final List<Special> denominatorSpecials;
    private final String desc;
    private final Special exponent;
    private final List<Special> exponentDigitSpecials;
    private final List<Special> exponentSpecials;
    private final List<Special> fractionalSpecials;
    private final boolean improperFraction;
    private final List<Special> integerSpecials;
    private final int maxDenominator;
    private final Special numerator;
    private final String numeratorFmt;
    private final List<Special> numeratorSpecials;
    private final String printfFmt;
    private final double scale;
    private final boolean showGroupingSeparator;
    private final Special slash;
    private final List<Special> specials;

    private static class GeneralNumberFormatter extends CellFormatter {
        private GeneralNumberFormatter(Locale locale) {
            super(locale, "General");
        }

        @Override // org.apache.poi.ss.format.CellFormatter
        public void formatValue(StringBuffer toAppendTo, Object value) {
            CellFormatter cf;
            if (value == null) {
                return;
            }
            if (value instanceof Number) {
                Number num = (Number) value;
                double dDoubleValue = num.doubleValue() % 1.0d;
                Locale locale = this.locale;
                cf = dDoubleValue == 0.0d ? new CellNumberFormatter(locale, "#") : new CellNumberFormatter(locale, "#.#");
            } else {
                cf = CellTextFormatter.SIMPLE_TEXT;
            }
            cf.formatValue(toAppendTo, value);
        }

        @Override // org.apache.poi.ss.format.CellFormatter
        public void simpleValue(StringBuffer toAppendTo, Object value) {
            formatValue(toAppendTo, value);
        }
    }

    static class Special {
        final char ch;
        int pos;

        Special(char ch, int pos) {
            this.ch = ch;
            this.pos = pos;
        }

        public String toString() {
            return "'" + this.ch + "' @ " + this.pos;
        }
    }

    public CellNumberFormatter(String format) {
        this(LocaleUtil.getUserLocale(), format);
    }

    public CellNumberFormatter(Locale locale, String format) {
        int fractionPartWidth;
        int i;
        boolean first;
        super(locale, format);
        ArrayList arrayList = new ArrayList();
        this.specials = arrayList;
        ArrayList arrayList2 = new ArrayList();
        this.integerSpecials = arrayList2;
        ArrayList arrayList3 = new ArrayList();
        this.fractionalSpecials = arrayList3;
        ArrayList arrayList4 = new ArrayList();
        this.numeratorSpecials = arrayList4;
        ArrayList arrayList5 = new ArrayList();
        this.denominatorSpecials = arrayList5;
        ArrayList arrayList6 = new ArrayList();
        this.exponentSpecials = arrayList6;
        ArrayList arrayList7 = new ArrayList();
        this.exponentDigitSpecials = arrayList7;
        this.SIMPLE_NUMBER = new GeneralNumberFormatter(this.locale);
        CellNumberPartHandler ph = new CellNumberPartHandler();
        StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.NUMBER, ph);
        Special exponent = ph.getExponent();
        this.exponent = exponent;
        arrayList.addAll(ph.getSpecials());
        this.improperFraction = ph.isImproperFraction();
        if ((ph.getDecimalPoint() == null && ph.getExponent() == null) || ph.getSlash() == null) {
            this.slash = ph.getSlash();
            this.numerator = ph.getNumerator();
        } else {
            this.slash = null;
            this.numerator = null;
        }
        int precision = interpretPrecision(ph.getDecimalPoint(), arrayList);
        if (ph.getDecimalPoint() != null) {
            int fractionPartWidth2 = precision + 1;
            if (precision != 0) {
                this.decimalPoint = ph.getDecimalPoint();
            } else {
                arrayList.remove(ph.getDecimalPoint());
                this.decimalPoint = null;
            }
            fractionPartWidth = fractionPartWidth2;
        } else {
            this.decimalPoint = null;
            fractionPartWidth = 0;
        }
        Special special = this.decimalPoint;
        if (special != null) {
            this.afterInteger = special;
        } else if (exponent != null) {
            this.afterInteger = exponent;
        } else {
            Special special2 = this.numerator;
            if (special2 != null) {
                this.afterInteger = special2;
            } else {
                this.afterInteger = null;
            }
        }
        if (exponent != null) {
            this.afterFractional = exponent;
        } else {
            Special special3 = this.numerator;
            if (special3 != null) {
                this.afterFractional = special3;
            } else {
                this.afterFractional = null;
            }
        }
        double[] scaleByRef = {ph.getScale()};
        this.showGroupingSeparator = interpretIntegerCommas(descBuf, arrayList, this.decimalPoint, integerEnd(), fractionalEnd(), scaleByRef);
        if (exponent == null) {
            this.scale = scaleByRef[0];
        } else {
            this.scale = 1.0d;
        }
        if (precision == 0) {
            i = 1;
        } else {
            i = 1;
            arrayList3.addAll(arrayList.subList(arrayList.indexOf(this.decimalPoint) + 1, fractionalEnd()));
        }
        if (exponent != null) {
            int exponentPos = arrayList.indexOf(exponent);
            arrayList6.addAll(specialsFor(exponentPos, 2));
            arrayList7.addAll(specialsFor(exponentPos + 2));
        }
        if (this.slash != null) {
            Special special4 = this.numerator;
            if (special4 != null) {
                arrayList4.addAll(specialsFor(arrayList.indexOf(special4)));
            }
            arrayList5.addAll(specialsFor(arrayList.indexOf(this.slash) + i));
            if (!arrayList5.isEmpty()) {
                this.maxDenominator = maxValue(arrayList5);
                this.numeratorFmt = singleNumberFormat(arrayList4);
                this.denominatorFmt = singleNumberFormat(arrayList5);
            } else {
                arrayList4.clear();
                this.maxDenominator = i;
                this.numeratorFmt = null;
                this.denominatorFmt = null;
            }
        } else {
            this.maxDenominator = i;
            this.numeratorFmt = null;
            this.denominatorFmt = null;
        }
        arrayList2.addAll(arrayList.subList(0, integerEnd()));
        if (exponent == null) {
            StringBuffer fmtBuf = new StringBuffer("%");
            int integerPartWidth = calculateIntegerPartWidth();
            int totalWidth = integerPartWidth + fractionPartWidth;
            fmtBuf.append('0').append(totalWidth).append('.').append(precision);
            fmtBuf.append("f");
            this.printfFmt = fmtBuf.toString();
            this.decimalFmt = null;
        } else {
            StringBuffer fmtBuf2 = new StringBuffer();
            boolean first2 = true;
            List<Special> specialList = this.integerSpecials;
            if (arrayList2.size() == i) {
                fmtBuf2.append("0");
                first = false;
            } else {
                for (Special s : specialList) {
                    if (isDigitFmt(s)) {
                        fmtBuf2.append(first2 ? '#' : '0');
                        first2 = false;
                    }
                }
                first = first2;
            }
            if (this.fractionalSpecials.size() > 0) {
                fmtBuf2.append('.');
                for (Special s2 : this.fractionalSpecials) {
                    if (isDigitFmt(s2)) {
                        if (!first) {
                            fmtBuf2.append('0');
                        }
                        first = false;
                    }
                }
            }
            fmtBuf2.append('E');
            List<Special> list = this.exponentSpecials;
            placeZeros(fmtBuf2, list.subList(2, list.size()));
            this.decimalFmt = new DecimalFormat(fmtBuf2.toString(), getDecimalFormatSymbols());
            this.printfFmt = null;
        }
        this.desc = descBuf.toString();
    }

    private DecimalFormatSymbols getDecimalFormatSymbols() {
        return DecimalFormatSymbols.getInstance(this.locale);
    }

    private static void placeZeros(StringBuffer sb, List<Special> specials) {
        for (Special s : specials) {
            if (isDigitFmt(s)) {
                sb.append('0');
            }
        }
    }

    private static CellNumberStringMod insertMod(Special special, CharSequence toAdd, int where) {
        return new CellNumberStringMod(special, toAdd, where);
    }

    private static CellNumberStringMod deleteMod(Special start, boolean startInclusive, Special end, boolean endInclusive) {
        return new CellNumberStringMod(start, startInclusive, end, endInclusive);
    }

    private static CellNumberStringMod replaceMod(Special start, boolean startInclusive, Special end, boolean endInclusive, char withChar) {
        return new CellNumberStringMod(start, startInclusive, end, endInclusive, withChar);
    }

    private static String singleNumberFormat(List<Special> numSpecials) {
        return "%0" + numSpecials.size() + "d";
    }

    private static int maxValue(List<Special> s) {
        return (int) Math.round(Math.pow(10.0d, s.size()) - 1.0d);
    }

    private List<Special> specialsFor(int pos, int takeFirst) {
        if (pos >= this.specials.size()) {
            return Collections.emptyList();
        }
        ListIterator<Special> it = this.specials.listIterator(pos + takeFirst);
        Special last = it.next();
        int end = pos + takeFirst;
        while (it.hasNext()) {
            Special s = it.next();
            if (!isDigitFmt(s) || s.pos - last.pos > 1) {
                break;
            }
            end++;
            last = s;
        }
        return this.specials.subList(pos, end + 1);
    }

    private List<Special> specialsFor(int pos) {
        return specialsFor(pos, 0);
    }

    private static boolean isDigitFmt(Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }

    private int calculateIntegerPartWidth() {
        Special s;
        int digitCount = 0;
        Iterator<Special> it = this.specials.iterator();
        while (it.hasNext() && (s = it.next()) != this.afterInteger) {
            if (isDigitFmt(s)) {
                digitCount++;
            }
        }
        return digitCount;
    }

    private static int interpretPrecision(Special decimalPoint, List<Special> specials) {
        int idx = specials.indexOf(decimalPoint);
        int precision = 0;
        if (idx != -1) {
            ListIterator<Special> it = specials.listIterator(idx + 1);
            while (it.hasNext()) {
                Special s = it.next();
                if (!isDigitFmt(s)) {
                    break;
                }
                precision++;
            }
        }
        return precision;
    }

    private static boolean interpretIntegerCommas(StringBuffer sb, List<Special> specials, Special decimalPoint, int integerEnd, int fractionalEnd, double[] scale) {
        ListIterator<Special> it = specials.listIterator(integerEnd);
        boolean stillScaling = true;
        boolean integerCommas = false;
        while (it.hasPrevious()) {
            if (it.previous().ch != ',') {
                stillScaling = false;
            } else if (stillScaling) {
                scale[0] = scale[0] / 1000.0d;
            } else {
                integerCommas = true;
            }
        }
        if (decimalPoint != null) {
            ListIterator<Special> it2 = specials.listIterator(fractionalEnd);
            while (it2.hasPrevious() && it2.previous().ch == ',') {
                scale[0] = scale[0] / 1000.0d;
            }
        }
        ListIterator<Special> it3 = specials.listIterator();
        int removed = 0;
        while (it3.hasNext()) {
            Special s = it3.next();
            s.pos -= removed;
            if (s.ch == ',') {
                removed++;
                it3.remove();
                sb.deleteCharAt(s.pos);
            }
        }
        return integerCommas;
    }

    private int integerEnd() {
        Special special = this.afterInteger;
        return special == null ? this.specials.size() : this.specials.indexOf(special);
    }

    private int fractionalEnd() {
        Special special = this.afterFractional;
        return special == null ? this.specials.size() : this.specials.indexOf(special);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:42:0x0127
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:226)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:196)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:63)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:125)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    @Override // org.apache.poi.ss.format.CellFormatter
    public void formatValue(java.lang.StringBuffer r29, java.lang.Object r30) {
        /*
            Method dump skipped, instruction units count: 581
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.format.CellNumberFormatter.formatValue(java.lang.StringBuffer, java.lang.Object):void");
    }

    private void writeScientific(double value, StringBuffer output, Set<CellNumberStringMod> mods) {
        char expSignRes;
        StringBuffer result = new StringBuffer();
        FieldPosition fractionPos = new FieldPosition(1);
        this.decimalFmt.format(value, result, fractionPos);
        writeInteger(result, output, this.integerSpecials, mods, this.showGroupingSeparator);
        writeFractional(result, output);
        int ePos = fractionPos.getEndIndex();
        int signPos = ePos + 1;
        char expSignRes2 = result.charAt(signPos);
        if (expSignRes2 == '-') {
            expSignRes = expSignRes2;
        } else {
            result.insert(signPos, '+');
            expSignRes = '+';
        }
        ListIterator<Special> it = this.exponentSpecials.listIterator(1);
        Special expSign = it.next();
        char expSignFmt = expSign.ch;
        if (expSignRes == '-' || expSignFmt == '+') {
            mods.add(replaceMod(expSign, true, expSign, true, expSignRes));
        } else {
            mods.add(deleteMod(expSign, true, expSign, true));
        }
        StringBuffer exponentNum = new StringBuffer(result.substring(signPos + 1));
        writeInteger(exponentNum, output, this.exponentDigitSpecials, mods, false);
    }

    private void writeFraction(double value, StringBuffer result, double fractional, StringBuffer output, Set<CellNumberStringMod> mods) {
        int n;
        int d;
        int n2;
        CellNumberStringMod sm;
        if (!this.improperFraction) {
            if (fractional == 0.0d && !hasChar('0', this.numeratorSpecials)) {
                writeInteger(result, output, this.integerSpecials, mods, false);
                Special start = lastSpecial(this.integerSpecials);
                Special end = lastSpecial(this.denominatorSpecials);
                if (hasChar('?', this.integerSpecials, this.numeratorSpecials, this.denominatorSpecials)) {
                    mods.add(replaceMod(start, false, end, true, ' '));
                    return;
                } else {
                    mods.add(deleteMod(start, false, end, true));
                    return;
                }
            }
            boolean numNoZero = !hasChar('0', this.numeratorSpecials);
            boolean intNoZero = !hasChar('0', this.integerSpecials);
            boolean intOnlyHash = this.integerSpecials.isEmpty() || (this.integerSpecials.size() == 1 && hasChar('#', this.integerSpecials));
            boolean removeBecauseZero = fractional == 0.0d && (intOnlyHash || numNoZero);
            boolean removeBecauseFraction = fractional != 0.0d && intNoZero;
            if (value == 0.0d && (removeBecauseZero || removeBecauseFraction)) {
                Special start2 = lastSpecial(this.integerSpecials);
                boolean hasPlaceHolder = hasChar('?', this.integerSpecials, this.numeratorSpecials);
                if (!hasPlaceHolder) {
                    sm = deleteMod(start2, true, this.numerator, false);
                } else {
                    sm = replaceMod(start2, true, this.numerator, false, ' ');
                }
                mods.add(sm);
            } else {
                writeInteger(result, output, this.integerSpecials, mods, false);
            }
        }
        if (fractional != 0.0d) {
            try {
                if (this.improperFraction && fractional % 1.0d == 0.0d) {
                    n = (int) Math.round(fractional);
                    d = 1;
                } else {
                    SimpleFraction frac = SimpleFraction.buildFractionMaxDenominator(fractional, this.maxDenominator);
                    n = frac.getNumerator();
                    d = frac.getDenominator();
                }
            } catch (RuntimeException ignored) {
                LOG.log(7, "error while fraction evaluation", ignored);
                return;
            }
        } else {
            n = (int) Math.round(fractional);
            d = 1;
        }
        if (!this.improperFraction) {
            n2 = n;
        } else {
            n2 = (int) (((long) n) + Math.round(((double) d) * value));
        }
        writeSingleInteger(this.numeratorFmt, n2, output, this.numeratorSpecials, mods);
        writeSingleInteger(this.denominatorFmt, d, output, this.denominatorSpecials, mods);
    }

    private String localiseFormat(String format) {
        DecimalFormatSymbols dfs = getDecimalFormatSymbols();
        if (format.contains(",") && dfs.getGroupingSeparator() != ',') {
            if (format.contains(".") && dfs.getDecimalSeparator() != '.') {
                return replaceLast(format, "\\.", "[DECIMAL_SEPARATOR]").replace(',', dfs.getGroupingSeparator()).replace("[DECIMAL_SEPARATOR]", Character.toString(dfs.getDecimalSeparator()));
            }
            return format.replace(',', dfs.getGroupingSeparator());
        }
        if (format.contains(".") && dfs.getDecimalSeparator() != '.') {
            return format.replace('.', dfs.getDecimalSeparator());
        }
        return format;
    }

    private static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    private static boolean hasChar(char ch, List<Special>... numSpecials) {
        for (List<Special> specials : numSpecials) {
            for (Special s : specials) {
                if (s.ch == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    private void writeSingleInteger(String fmt, int num, StringBuffer output, List<Special> numSpecials, Set<CellNumberStringMod> mods) {
        StringBuffer sb = new StringBuffer();
        Formatter formatter = new Formatter(sb, this.locale);
        try {
            formatter.format(this.locale, fmt, Integer.valueOf(num));
            formatter.close();
            writeInteger(sb, output, numSpecials, mods, false);
        } catch (Throwable th) {
            formatter.close();
            throw th;
        }
    }

    private void writeInteger(StringBuffer result, StringBuffer output, List<Special> numSpecials, Set<CellNumberStringMod> mods, boolean showGroupingSeparator) {
        char c;
        char resultCh;
        char resultCh2;
        DecimalFormatSymbols dfs = getDecimalFormatSymbols();
        String decimalSeparator = Character.toString(dfs.getDecimalSeparator());
        String groupingSeparator = Character.toString(dfs.getGroupingSeparator());
        int pos = result.indexOf(decimalSeparator) - 1;
        if (pos < 0) {
            if (this.exponent != null && numSpecials == this.integerSpecials) {
                pos = result.indexOf("E") - 1;
            } else {
                pos = result.length() - 1;
            }
        }
        int strip = 0;
        while (true) {
            c = '0';
            if (strip >= pos || !((resultCh2 = result.charAt(strip)) == '0' || resultCh2 == dfs.getGroupingSeparator())) {
                break;
            } else {
                strip++;
            }
        }
        ListIterator<Special> it = numSpecials.listIterator(numSpecials.size());
        Special lastOutputIntegerDigit = null;
        int digit = 0;
        while (it.hasPrevious()) {
            if (pos >= 0) {
                resultCh = result.charAt(pos);
            } else {
                resultCh = '0';
            }
            Special s = it.previous();
            boolean followWithGroupingSeparator = showGroupingSeparator && digit > 0 && digit % 3 == 0;
            boolean zeroStrip = false;
            if (resultCh != c || s.ch == c || s.ch == '?' || pos >= strip) {
                zeroStrip = s.ch == '?' && pos < strip;
                output.setCharAt(s.pos, zeroStrip ? ' ' : resultCh);
                lastOutputIntegerDigit = s;
            }
            if (followWithGroupingSeparator) {
                mods.add(insertMod(s, zeroStrip ? " " : groupingSeparator, 2));
            }
            digit++;
            pos--;
            c = '0';
        }
        new StringBuffer();
        if (pos >= 0) {
            int pos2 = pos + 1;
            StringBuffer extraLeadingDigits = new StringBuffer(result.substring(0, pos2));
            if (showGroupingSeparator) {
                while (pos2 > 0) {
                    if (digit > 0 && digit % 3 == 0) {
                        extraLeadingDigits.insert(pos2, groupingSeparator);
                    }
                    digit++;
                    pos2--;
                }
            }
            mods.add(insertMod(lastOutputIntegerDigit, extraLeadingDigits, 1));
        }
    }

    private void writeFractional(StringBuffer result, StringBuffer output) {
        int strip;
        if (this.fractionalSpecials.size() > 0) {
            String decimalSeparator = Character.toString(getDecimalFormatSymbols().getDecimalSeparator());
            int digit = result.indexOf(decimalSeparator) + 1;
            if (this.exponent != null) {
                strip = result.indexOf("e");
            } else {
                strip = result.length();
            }
            do {
                strip--;
                if (strip <= digit) {
                    break;
                }
            } while (result.charAt(strip) == '0');
            for (Special s : this.fractionalSpecials) {
                char resultCh = result.charAt(digit);
                if (resultCh != '0' || s.ch == '0' || digit < strip) {
                    output.setCharAt(s.pos, resultCh);
                } else if (s.ch == '?') {
                    output.setCharAt(s.pos, ' ');
                }
                digit++;
            }
        }
    }

    @Override // org.apache.poi.ss.format.CellFormatter
    public void simpleValue(StringBuffer toAppendTo, Object value) {
        this.SIMPLE_NUMBER.formatValue(toAppendTo, value);
    }

    private static Special lastSpecial(List<Special> s) {
        return s.get(s.size() - 1);
    }
}
