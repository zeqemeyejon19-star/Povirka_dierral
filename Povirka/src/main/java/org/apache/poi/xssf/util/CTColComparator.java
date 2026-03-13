package org.apache.poi.xssf.util;

import java.util.Comparator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

/* JADX INFO: loaded from: classes.dex */
public class CTColComparator {
    public static final Comparator<CTCol> BY_MAX = new Comparator<CTCol>() { // from class: org.apache.poi.xssf.util.CTColComparator.1
        @Override // java.util.Comparator
        public int compare(CTCol col1, CTCol col2) {
            long col1max = col1.getMax();
            long col2max = col2.getMax();
            if (col1max < col2max) {
                return -1;
            }
            return col1max > col2max ? 1 : 0;
        }
    };
    public static final Comparator<CTCol> BY_MIN_MAX = new Comparator<CTCol>() { // from class: org.apache.poi.xssf.util.CTColComparator.2
        @Override // java.util.Comparator
        public int compare(CTCol col1, CTCol col2) {
            long col11min = col1.getMin();
            long col2min = col2.getMin();
            if (col11min < col2min) {
                return -1;
            }
            if (col11min > col2min) {
                return 1;
            }
            return CTColComparator.BY_MAX.compare(col1, col2);
        }
    };

    private CTColComparator() {
    }
}
