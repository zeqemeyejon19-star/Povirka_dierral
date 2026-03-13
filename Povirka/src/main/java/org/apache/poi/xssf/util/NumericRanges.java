package org.apache.poi.xssf.util;

/* JADX INFO: loaded from: classes.dex */
public class NumericRanges {
    public static final int NO_OVERLAPS = -1;
    public static final int OVERLAPS_1_MINOR = 0;
    public static final int OVERLAPS_1_WRAPS = 2;
    public static final int OVERLAPS_2_MINOR = 1;
    public static final int OVERLAPS_2_WRAPS = 3;

    public static long[] getOverlappingRange(long[] range1, long[] range2) {
        int overlappingType = getOverlappingType(range1, range2);
        if (overlappingType == 0) {
            return new long[]{range2[0], range1[1]};
        }
        if (overlappingType == 1) {
            return new long[]{range1[0], range2[1]};
        }
        if (overlappingType == 2) {
            return range2;
        }
        if (overlappingType == 3) {
            return range1;
        }
        return new long[]{-1, -1};
    }

    public static int getOverlappingType(long[] range1, long[] range2) {
        long min1 = range1[0];
        long max1 = range1[1];
        long min2 = range2[0];
        long max2 = range2[1];
        if (min1 >= min2) {
            if (max1 <= max2) {
                return 3;
            }
            if (min1 <= max2) {
                return 1;
            }
            return -1;
        }
        if (max1 >= max2) {
            return 2;
        }
        if (max1 >= min2) {
            return 0;
        }
        return -1;
    }
}
