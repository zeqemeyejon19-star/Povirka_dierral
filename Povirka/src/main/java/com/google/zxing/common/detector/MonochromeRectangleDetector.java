package com.google.zxing.common.detector;

import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class MonochromeRectangleDetector {
    private static final int MAX_MODULES = 32;
    private final BitMatrix image;

    public MonochromeRectangleDetector(BitMatrix image) {
        this.image = image;
    }

    public ResultPoint[] detect() throws NotFoundException {
        int height = this.image.getHeight();
        int width = this.image.getWidth();
        int halfHeight = height / 2;
        int halfWidth = width / 2;
        int deltaY = Math.max(1, height / 256);
        int deltaX = Math.max(1, width / 256);
        int top = ((int) findCornerFromCenter(halfWidth, 0, 0, width, halfHeight, -deltaY, 0, height, halfWidth / 2).getY()) - 1;
        ResultPoint pointB = findCornerFromCenter(halfWidth, -deltaX, 0, width, halfHeight, 0, top, height, halfHeight / 2);
        int left = ((int) pointB.getX()) - 1;
        ResultPoint pointC = findCornerFromCenter(halfWidth, deltaX, left, width, halfHeight, 0, top, height, halfHeight / 2);
        int right = ((int) pointC.getX()) + 1;
        ResultPoint pointD = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, deltaY, top, height, halfWidth / 2);
        int bottom = ((int) pointD.getY()) + 1;
        ResultPoint pointA = findCornerFromCenter(halfWidth, 0, left, right, halfHeight, -deltaY, top, bottom, halfWidth / 4);
        return new ResultPoint[]{pointA, pointB, pointC, pointD};
    }

    private ResultPoint findCornerFromCenter(int centerX, int deltaX, int left, int right, int centerY, int deltaY, int top, int bottom, int maxWhiteRun) throws NotFoundException {
        int[] range;
        int[] lastRange = null;
        int y = centerY;
        int x = centerX;
        while (y < bottom && y >= top && x < right && x >= left) {
            if (deltaX == 0) {
                range = blackWhiteRange(y, maxWhiteRun, left, right, true);
            } else {
                range = blackWhiteRange(x, maxWhiteRun, top, bottom, false);
            }
            if (range == null) {
                if (lastRange == null) {
                    throw NotFoundException.getNotFoundInstance();
                }
                if (deltaX == 0) {
                    int lastY = y - deltaY;
                    if (lastRange[0] < centerX) {
                        if (lastRange[1] > centerX) {
                            return new ResultPoint(lastRange[deltaY > 0 ? (char) 0 : (char) 1], lastY);
                        }
                        return new ResultPoint(lastRange[0], lastY);
                    }
                    return new ResultPoint(lastRange[1], lastY);
                }
                int lastY2 = x - deltaX;
                if (lastRange[0] < centerY) {
                    if (lastRange[1] > centerY) {
                        return new ResultPoint(lastY2, lastRange[deltaX < 0 ? (char) 0 : (char) 1]);
                    }
                    return new ResultPoint(lastY2, lastRange[0]);
                }
                return new ResultPoint(lastY2, lastRange[1]);
            }
            lastRange = range;
            y += deltaY;
            x += deltaX;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0022  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0033 A[EDGE_INSN: B:72:0x0033->B:22:0x0033 BREAK  A[LOOP:1: B:13:0x001e->B:75:0x001e], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:88:0x006a A[EDGE_INSN: B:88:0x006a->B:48:0x006a BREAK  A[LOOP:3: B:39:0x0056->B:93:0x0056], SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int[] blackWhiteRange(int r9, int r10, int r11, int r12, boolean r13) {
        /*
            r8 = this;
            int r0 = r11 + r12
            r1 = 2
            int r0 = r0 / r1
            r2 = 0
            r3 = r2
            r3 = r0
        L7:
            if (r0 < r11) goto L3c
            com.google.zxing.common.BitMatrix r4 = r8.image
            if (r13 == 0) goto L14
            boolean r4 = r4.get(r0, r9)
            if (r4 == 0) goto L1d
            goto L1a
        L14:
            boolean r4 = r4.get(r9, r0)
            if (r4 == 0) goto L1d
        L1a:
            int r0 = r0 + (-1)
            goto L7
        L1d:
            r4 = r0
        L1e:
            int r0 = r0 + (-1)
            if (r0 < r11) goto L33
            com.google.zxing.common.BitMatrix r5 = r8.image
            if (r13 == 0) goto L2d
            boolean r5 = r5.get(r0, r9)
            if (r5 == 0) goto L1e
            goto L33
        L2d:
            boolean r5 = r5.get(r9, r0)
            if (r5 == 0) goto L1e
        L33:
            int r5 = r4 - r0
            if (r0 < r11) goto L3b
            if (r5 <= r10) goto L3a
            goto L3b
        L3a:
            goto L7
        L3b:
            r0 = r4
        L3c:
            r4 = 1
            int r0 = r0 + r4
            r5 = r3
        L3f:
            if (r5 >= r12) goto L73
            com.google.zxing.common.BitMatrix r6 = r8.image
            if (r13 == 0) goto L4c
            boolean r6 = r6.get(r5, r9)
            if (r6 == 0) goto L55
            goto L52
        L4c:
            boolean r6 = r6.get(r9, r5)
            if (r6 == 0) goto L55
        L52:
            int r5 = r5 + 1
            goto L3f
        L55:
            r6 = r5
        L56:
            int r5 = r5 + r4
            if (r5 >= r12) goto L6a
            com.google.zxing.common.BitMatrix r7 = r8.image
            if (r13 == 0) goto L64
            boolean r7 = r7.get(r5, r9)
            if (r7 == 0) goto L56
            goto L6a
        L64:
            boolean r7 = r7.get(r9, r5)
            if (r7 == 0) goto L56
        L6a:
            int r7 = r5 - r6
            if (r5 >= r12) goto L72
            if (r7 <= r10) goto L71
            goto L72
        L71:
            goto L3f
        L72:
            r5 = r6
        L73:
            int r5 = r5 + (-1)
            if (r5 <= r0) goto L7e
            int[] r1 = new int[r1]
            r1[r2] = r0
            r1[r4] = r5
            return r1
        L7e:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.common.detector.MonochromeRectangleDetector.blackWhiteRange(int, int, int, int, boolean):int[]");
    }
}
