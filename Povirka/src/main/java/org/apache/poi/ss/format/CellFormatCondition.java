package org.apache.poi.ss.format;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class CellFormatCondition {
    private static final int EQ = 4;
    private static final int GE = 3;
    private static final int GT = 2;
    private static final int LE = 1;
    private static final int LT = 0;
    private static final int NE = 5;
    private static final Map<String, Integer> TESTS;

    public abstract boolean pass(double d);

    static {
        HashMap map = new HashMap();
        TESTS = map;
        map.put("<", 0);
        map.put("<=", 1);
        map.put(">", 2);
        map.put(">=", 3);
        map.put("=", 4);
        map.put("==", 4);
        map.put("!=", 5);
        map.put("<>", 5);
    }

    public static CellFormatCondition getInstance(String opString, String constStr) {
        Map<String, Integer> map = TESTS;
        if (!map.containsKey(opString)) {
            throw new IllegalArgumentException("Unknown test: " + opString);
        }
        int test = map.get(opString).intValue();
        final double c = Double.parseDouble(constStr);
        if (test == 0) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.1
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value < c;
                }
            };
        }
        if (test == 1) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.2
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value <= c;
                }
            };
        }
        if (test == 2) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.3
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value > c;
                }
            };
        }
        if (test == 3) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.4
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value >= c;
                }
            };
        }
        if (test == 4) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.5
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value == c;
                }
            };
        }
        if (test == 5) {
            return new CellFormatCondition() { // from class: org.apache.poi.ss.format.CellFormatCondition.6
                @Override // org.apache.poi.ss.format.CellFormatCondition
                public boolean pass(double value) {
                    return value != c;
                }
            };
        }
        throw new IllegalArgumentException("Cannot create for test number " + test + "(\"" + opString + "\")");
    }
}
