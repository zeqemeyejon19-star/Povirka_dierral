package org.apache.poi.sl.usermodel;

/* JADX INFO: loaded from: classes.dex */
public interface LineDecoration {
    DecorationSize getHeadLength();

    DecorationShape getHeadShape();

    DecorationSize getHeadWidth();

    DecorationSize getTailLength();

    DecorationShape getTailShape();

    DecorationSize getTailWidth();

    public enum DecorationShape {
        NONE(0, 1),
        TRIANGLE(1, 2),
        STEALTH(2, 3),
        DIAMOND(3, 4),
        OVAL(4, 5),
        ARROW(5, 6);

        public final int nativeId;
        public final int ooxmlId;

        DecorationShape(int nativeId, int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }

        public static DecorationShape fromNativeId(int nativeId) {
            DecorationShape[] arr$ = values();
            for (DecorationShape ld : arr$) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }

        public static DecorationShape fromOoxmlId(int ooxmlId) {
            DecorationShape[] arr$ = values();
            for (DecorationShape ds : arr$) {
                if (ds.ooxmlId == ooxmlId) {
                    return ds;
                }
            }
            return null;
        }
    }

    public enum DecorationSize {
        SMALL(0, 1),
        MEDIUM(1, 2),
        LARGE(2, 3);

        public final int nativeId;
        public final int ooxmlId;

        DecorationSize(int nativeId, int ooxmlId) {
            this.nativeId = nativeId;
            this.ooxmlId = ooxmlId;
        }

        public static DecorationSize fromNativeId(int nativeId) {
            DecorationSize[] arr$ = values();
            for (DecorationSize ld : arr$) {
                if (ld.nativeId == nativeId) {
                    return ld;
                }
            }
            return null;
        }

        public static DecorationSize fromOoxmlId(int ooxmlId) {
            DecorationSize[] arr$ = values();
            for (DecorationSize ds : arr$) {
                if (ds.ooxmlId == ooxmlId) {
                    return ds;
                }
            }
            return null;
        }
    }
}
