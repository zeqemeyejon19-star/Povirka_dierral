package org.apache.poi.common.usermodel.fonts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/* JADX INFO: loaded from: classes.dex */
public enum FontGroup {
    LATIN,
    EAST_ASIAN,
    SYMBOL,
    COMPLEX_SCRIPT;

    private static NavigableMap<Integer, Range> UCS_RANGES;

    static {
        FontGroup fontGroup = LATIN;
        FontGroup fontGroup2 = EAST_ASIAN;
        FontGroup fontGroup3 = SYMBOL;
        FontGroup fontGroup4 = COMPLEX_SCRIPT;
        TreeMap treeMap = new TreeMap();
        UCS_RANGES = treeMap;
        treeMap.put(0, new Range(127, fontGroup));
        UCS_RANGES.put(128, new Range(166, fontGroup));
        UCS_RANGES.put(169, new Range(175, fontGroup));
        UCS_RANGES.put(178, new Range(179, fontGroup));
        UCS_RANGES.put(181, new Range(214, fontGroup));
        UCS_RANGES.put(216, new Range(246, fontGroup));
        UCS_RANGES.put(248, new Range(1423, fontGroup));
        UCS_RANGES.put(1424, new Range(1871, fontGroup4));
        UCS_RANGES.put(1920, new Range(1983, fontGroup4));
        UCS_RANGES.put(2304, new Range(4255, fontGroup4));
        UCS_RANGES.put(4256, new Range(4351, fontGroup));
        UCS_RANGES.put(4608, new Range(4991, fontGroup));
        UCS_RANGES.put(5024, new Range(6015, fontGroup));
        UCS_RANGES.put(7424, new Range(7551, fontGroup));
        UCS_RANGES.put(7680, new Range(8191, fontGroup));
        UCS_RANGES.put(6016, new Range(6319, fontGroup4));
        UCS_RANGES.put(8192, new Range(8203, fontGroup));
        UCS_RANGES.put(8204, new Range(8207, fontGroup4));
        UCS_RANGES.put(8208, new Range(8233, fontGroup));
        UCS_RANGES.put(8234, new Range(8239, fontGroup4));
        UCS_RANGES.put(8240, new Range(8262, fontGroup));
        UCS_RANGES.put(8266, new Range(9311, fontGroup));
        UCS_RANGES.put(9840, new Range(9841, fontGroup4));
        UCS_RANGES.put(10176, new Range(11263, fontGroup));
        UCS_RANGES.put(12441, new Range(12442, fontGroup2));
        UCS_RANGES.put(55349, new Range(55349, fontGroup));
        UCS_RANGES.put(61440, new Range(61695, fontGroup3));
        UCS_RANGES.put(64256, new Range(64279, fontGroup));
        UCS_RANGES.put(64285, new Range(64335, fontGroup4));
        UCS_RANGES.put(65104, new Range(65135, fontGroup));
    }

    public static class FontGroupRange {
        private FontGroup fontGroup;
        private int len;

        static /* synthetic */ int access$112(FontGroupRange x0, int x1) {
            int i = x0.len + x1;
            x0.len = i;
            return i;
        }

        public int getLength() {
            return this.len;
        }

        public FontGroup getFontGroup() {
            return this.fontGroup;
        }
    }

    private static class Range {
        FontGroup fontGroup;
        int upper;

        Range(int upper, FontGroup fontGroup) {
            this.upper = upper;
            this.fontGroup = fontGroup;
        }
    }

    public static List<FontGroupRange> getFontGroupRanges(String runText) {
        FontGroup tt;
        List<FontGroupRange> ttrList = new ArrayList<>();
        FontGroupRange ttrLast = null;
        int rlen = runText != null ? runText.length() : 0;
        int i = 0;
        while (i < rlen) {
            int cp = runText.codePointAt(i);
            int charCount = Character.charCount(cp);
            if (ttrLast != null && " \n\r".indexOf(cp) > -1) {
                tt = ttrLast.fontGroup;
            } else {
                tt = lookup(cp);
            }
            if (ttrLast == null || ttrLast.fontGroup != tt) {
                ttrLast = new FontGroupRange();
                ttrLast.fontGroup = tt;
                ttrList.add(ttrLast);
            }
            FontGroupRange.access$112(ttrLast, charCount);
            i += charCount;
        }
        return ttrList;
    }

    public static FontGroup getFontGroupFirst(String runText) {
        return (runText == null || runText.isEmpty()) ? LATIN : lookup(runText.codePointAt(0));
    }

    private static FontGroup lookup(int codepoint) {
        Map.Entry<Integer, Range> entry = UCS_RANGES.floorEntry(Integer.valueOf(codepoint));
        Range range = entry != null ? entry.getValue() : null;
        return (range == null || codepoint > range.upper) ? EAST_ASIAN : range.fontGroup;
    }
}
