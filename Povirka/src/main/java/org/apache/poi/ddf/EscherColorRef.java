package org.apache.poi.ddf;

import androidx.core.view.MotionEventCompat;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class EscherColorRef {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private int colorRef;
    private int opid;
    private static final BitField FLAG_SYS_INDEX = new BitField(268435456);
    private static final BitField FLAG_SCHEME_INDEX = new BitField(134217728);
    private static final BitField FLAG_SYSTEM_RGB = new BitField(67108864);
    private static final BitField FLAG_PALETTE_RGB = new BitField(33554432);
    private static final BitField FLAG_PALETTE_INDEX = new BitField(16777216);
    private static final BitField FLAG_BLUE = new BitField(16711680);
    private static final BitField FLAG_GREEN = new BitField(MotionEventCompat.ACTION_POINTER_INDEX_MASK);
    private static final BitField FLAG_RED = new BitField(255);

    public enum SysIndexSource {
        FILL_COLOR(240),
        LINE_OR_FILL_COLOR(241),
        LINE_COLOR(242),
        SHADOW_COLOR(243),
        CURRENT_OR_LAST_COLOR(244),
        FILL_BACKGROUND_COLOR(245),
        LINE_BACKGROUND_COLOR(246),
        FILL_OR_LINE_COLOR(247);

        private int value;

        SysIndexSource(int value) {
            this.value = value;
        }
    }

    public enum SysIndexProcedure {
        DARKEN_COLOR(1),
        LIGHTEN_COLOR(2),
        ADD_GRAY_LEVEL(3),
        SUB_GRAY_LEVEL(4),
        REVERSE_GRAY_LEVEL(5),
        THRESHOLD(6),
        INVERT_AFTER(32),
        INVERT_HIGHBIT_AFTER(64);

        private BitField mask;

        SysIndexProcedure(int mask) {
            this.mask = new BitField(mask);
        }
    }

    public EscherColorRef(int colorRef) {
        this.opid = -1;
        this.colorRef = 0;
        this.colorRef = colorRef;
    }

    public EscherColorRef(byte[] source, int start, int len) {
        this.opid = -1;
        this.colorRef = 0;
        if (len != 4 && len != 6) {
            throw new AssertionError();
        }
        int offset = start;
        if (len == 6) {
            this.opid = LittleEndian.getUShort(source, offset);
            offset += 2;
        }
        this.colorRef = LittleEndian.getInt(source, offset);
    }

    public boolean hasSysIndexFlag() {
        return FLAG_SYS_INDEX.isSet(this.colorRef);
    }

    public void setSysIndexFlag(boolean flag) {
        this.colorRef = FLAG_SYS_INDEX.setBoolean(this.colorRef, flag);
    }

    public boolean hasSchemeIndexFlag() {
        return FLAG_SCHEME_INDEX.isSet(this.colorRef);
    }

    public void setSchemeIndexFlag(boolean flag) {
        this.colorRef = FLAG_SCHEME_INDEX.setBoolean(this.colorRef, flag);
    }

    public boolean hasSystemRGBFlag() {
        return FLAG_SYSTEM_RGB.isSet(this.colorRef);
    }

    public void setSystemRGBFlag(boolean flag) {
        this.colorRef = FLAG_SYSTEM_RGB.setBoolean(this.colorRef, flag);
    }

    public boolean hasPaletteRGBFlag() {
        return FLAG_PALETTE_RGB.isSet(this.colorRef);
    }

    public void setPaletteRGBFlag(boolean flag) {
        this.colorRef = FLAG_PALETTE_RGB.setBoolean(this.colorRef, flag);
    }

    public boolean hasPaletteIndexFlag() {
        return FLAG_PALETTE_INDEX.isSet(this.colorRef);
    }

    public void setPaletteIndexFlag(boolean flag) {
        this.colorRef = FLAG_PALETTE_INDEX.setBoolean(this.colorRef, flag);
    }

    public int[] getRGB() {
        int[] rgb = {FLAG_RED.getValue(this.colorRef), FLAG_GREEN.getValue(this.colorRef), FLAG_BLUE.getValue(this.colorRef)};
        return rgb;
    }

    public SysIndexSource getSysIndexSource() {
        if (!hasSysIndexFlag()) {
            return null;
        }
        int val = FLAG_RED.getValue(this.colorRef);
        SysIndexSource[] arr$ = SysIndexSource.values();
        for (SysIndexSource sis : arr$) {
            if (sis.value == val) {
                return sis;
            }
        }
        return null;
    }

    public SysIndexProcedure getSysIndexProcedure() {
        if (!hasSysIndexFlag()) {
            return null;
        }
        int val = FLAG_GREEN.getValue(this.colorRef);
        SysIndexProcedure[] arr$ = SysIndexProcedure.values();
        for (SysIndexProcedure sip : arr$) {
            if (sip != SysIndexProcedure.INVERT_AFTER && sip != SysIndexProcedure.INVERT_HIGHBIT_AFTER && sip.mask.isSet(val)) {
                return sip;
            }
        }
        return null;
    }

    public int getSysIndexInvert() {
        if (!hasSysIndexFlag()) {
            return 0;
        }
        int val = FLAG_GREEN.getValue(this.colorRef);
        if (SysIndexProcedure.INVERT_AFTER.mask.isSet(val)) {
            return 1;
        }
        return SysIndexProcedure.INVERT_HIGHBIT_AFTER.mask.isSet(val) ? 2 : 0;
    }

    public int getSchemeIndex() {
        if (!hasSchemeIndexFlag()) {
            return -1;
        }
        return FLAG_RED.getValue(this.colorRef);
    }

    public int getPaletteIndex() {
        if (hasPaletteIndexFlag()) {
            return getIndex();
        }
        return -1;
    }

    public int getSysIndex() {
        if (hasSysIndexFlag()) {
            return getIndex();
        }
        return -1;
    }

    private int getIndex() {
        return (FLAG_GREEN.getValue(this.colorRef) << 8) | FLAG_RED.getValue(this.colorRef);
    }
}
