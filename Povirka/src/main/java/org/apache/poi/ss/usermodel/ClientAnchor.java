package org.apache.poi.ss.usermodel;

import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public interface ClientAnchor {
    AnchorType getAnchorType();

    short getCol1();

    short getCol2();

    int getDx1();

    int getDx2();

    int getDy1();

    int getDy2();

    int getRow1();

    int getRow2();

    void setAnchorType(AnchorType anchorType);

    void setCol1(int i);

    void setCol2(int i);

    void setDx1(int i);

    void setDx2(int i);

    void setDy1(int i);

    void setDy2(int i);

    void setRow1(int i);

    void setRow2(int i);

    public enum AnchorType {
        MOVE_AND_RESIZE(0),
        DONT_MOVE_DO_RESIZE(1),
        MOVE_DONT_RESIZE(2),
        DONT_MOVE_AND_RESIZE(3);

        public final short value;

        AnchorType(int value) {
            this.value = (short) value;
        }

        @Internal
        public static AnchorType byId(int value) {
            return values()[value];
        }
    }
}
