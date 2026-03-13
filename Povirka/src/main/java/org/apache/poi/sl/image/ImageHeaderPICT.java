package org.apache.poi.sl.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class ImageHeaderPICT {
    public static final double DEFAULT_RESOLUTION = 72.0d;
    public static final int PICT_HEADER_OFFSET = 512;
    private static final byte[] V2_HEADER = {0, RangePtg.sid, 2, -1, 12, 0, -1, -2, 0, 0};
    private final Rectangle bounds;
    private final double hRes;
    private final double vRes;

    public ImageHeaderPICT(byte[] data, int off) {
        int offset = off + 2;
        int y1 = readUnsignedShort(data, offset);
        int offset2 = offset + 2;
        int x1 = readUnsignedShort(data, offset2);
        int offset3 = offset2 + 2;
        int y2 = readUnsignedShort(data, offset3);
        int offset4 = offset3 + 2;
        int x2 = readUnsignedShort(data, offset4);
        int offset5 = offset4 + 2;
        boolean isV2 = true;
        byte[] arr$ = V2_HEADER;
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            int b = arr$[i$];
            int offset6 = offset5 + 1;
            if (b == data[offset5]) {
                i$++;
                offset5 = offset6;
            } else {
                isV2 = false;
                offset5 = offset6;
                break;
            }
        }
        if (isV2) {
            this.hRes = readFixedPoint(data, offset5);
            int offset7 = offset5 + 4;
            this.vRes = readFixedPoint(data, offset7);
            int i = offset7 + 4;
        } else {
            this.hRes = 72.0d;
            this.vRes = 72.0d;
        }
        this.bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    public Dimension getSize() {
        int height = (int) Math.round((((double) this.bounds.height) * 72.0d) / this.vRes);
        int width = (int) Math.round((((double) this.bounds.width) * 72.0d) / this.hRes);
        return new Dimension(width, height);
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    private static int readUnsignedShort(byte[] data, int offset) {
        int b0 = data[offset] & 255;
        int b1 = data[offset + 1] & 255;
        return (b0 << 8) | b1;
    }

    private static double readFixedPoint(byte[] data, int offset) {
        int b0 = data[offset] & 255;
        int b1 = data[offset + 1] & 255;
        int b2 = data[offset + 2] & 255;
        int b3 = data[offset + 3] & 255;
        int i = (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        return ((double) i) / 65536.0d;
    }
}
