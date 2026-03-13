package org.apache.poi.ss.format;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.poi.ss.format.CellFormatPart;
import org.apache.poi.ss.format.CellNumberFormatter;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class CellNumberPartHandler implements CellFormatPart.PartHandler {
    private CellNumberFormatter.Special decimalPoint;
    private CellNumberFormatter.Special exponent;
    private boolean improperFraction;
    private char insertSignForExponent;
    private CellNumberFormatter.Special numerator;
    private CellNumberFormatter.Special slash;
    private double scale = 1.0d;
    private final List<CellNumberFormatter.Special> specials = new LinkedList();

    /* JADX WARN: Removed duplicated region for block: B:35:0x0098  */
    @Override // org.apache.poi.ss.format.CellFormatPart.PartHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String handlePart(java.util.regex.Matcher r9, java.lang.String r10, org.apache.poi.ss.format.CellFormatType r11, java.lang.StringBuffer r12) {
        /*
            Method dump skipped, instruction units count: 216
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.ss.format.CellNumberPartHandler.handlePart(java.util.regex.Matcher, java.lang.String, org.apache.poi.ss.format.CellFormatType, java.lang.StringBuffer):java.lang.String");
    }

    public double getScale() {
        return this.scale;
    }

    public CellNumberFormatter.Special getDecimalPoint() {
        return this.decimalPoint;
    }

    public CellNumberFormatter.Special getSlash() {
        return this.slash;
    }

    public CellNumberFormatter.Special getExponent() {
        return this.exponent;
    }

    public CellNumberFormatter.Special getNumerator() {
        return this.numerator;
    }

    public List<CellNumberFormatter.Special> getSpecials() {
        return this.specials;
    }

    public boolean isImproperFraction() {
        return this.improperFraction;
    }

    private CellNumberFormatter.Special previousNumber() {
        CellNumberFormatter.Special last;
        List<CellNumberFormatter.Special> list = this.specials;
        ListIterator<CellNumberFormatter.Special> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            CellNumberFormatter.Special s = it.previous();
            if (isDigitFmt(s)) {
                do {
                    last = s;
                    if (!it.hasPrevious()) {
                        break;
                    }
                    s = it.previous();
                    if (last.pos - s.pos > 1) {
                        break;
                    }
                } while (isDigitFmt(s));
                return last;
            }
        }
        return null;
    }

    private static boolean isDigitFmt(CellNumberFormatter.Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }

    private static CellNumberFormatter.Special firstDigit(List<CellNumberFormatter.Special> specials) {
        for (CellNumberFormatter.Special s : specials) {
            if (isDigitFmt(s)) {
                return s;
            }
        }
        return null;
    }
}
