package org.apache.poi.ss.format;

import org.apache.poi.ss.format.CellNumberFormatter;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class CellNumberStringMod implements Comparable<CellNumberStringMod> {
    public static final int AFTER = 2;
    public static final int BEFORE = 1;
    public static final int REPLACE = 3;
    private CellNumberFormatter.Special end;
    private boolean endInclusive;
    private final int op;
    private final CellNumberFormatter.Special special;
    private boolean startInclusive;
    private CharSequence toAdd;

    public CellNumberStringMod(CellNumberFormatter.Special special, CharSequence toAdd, int op) {
        this.special = special;
        this.toAdd = toAdd;
        this.op = op;
    }

    public CellNumberStringMod(CellNumberFormatter.Special start, boolean startInclusive, CellNumberFormatter.Special end, boolean endInclusive, char toAdd) {
        this(start, startInclusive, end, endInclusive);
        this.toAdd = toAdd + "";
    }

    public CellNumberStringMod(CellNumberFormatter.Special start, boolean startInclusive, CellNumberFormatter.Special end, boolean endInclusive) {
        this.special = start;
        this.startInclusive = startInclusive;
        this.end = end;
        this.endInclusive = endInclusive;
        this.op = 3;
        this.toAdd = "";
    }

    @Override // java.lang.Comparable
    public int compareTo(CellNumberStringMod that) {
        int diff = this.special.pos - that.special.pos;
        return diff != 0 ? diff : this.op - that.op;
    }

    public boolean equals(Object that) {
        return (that instanceof CellNumberStringMod) && compareTo((CellNumberStringMod) that) == 0;
    }

    public int hashCode() {
        return this.special.hashCode() + this.op;
    }

    public CellNumberFormatter.Special getSpecial() {
        return this.special;
    }

    public int getOp() {
        return this.op;
    }

    public CharSequence getToAdd() {
        return this.toAdd;
    }

    public CellNumberFormatter.Special getEnd() {
        return this.end;
    }

    public boolean isStartInclusive() {
        return this.startInclusive;
    }

    public boolean isEndInclusive() {
        return this.endInclusive;
    }
}
