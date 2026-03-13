package org.apache.poi.sl.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class ImageHeaderWMF {
    public static final int APMHEADER_KEY = -1698247209;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ImageHeaderWMF.class);
    private final int bottom;
    private int checksum;
    private final int handle;
    private final int inch;
    private final int left;
    private final int reserved;
    private final int right;
    private final int top;

    public ImageHeaderWMF(Rectangle dim) {
        this.handle = 0;
        this.left = dim.x;
        this.top = dim.y;
        this.right = dim.x + dim.width;
        this.bottom = dim.y + dim.height;
        this.inch = 72;
        this.reserved = 0;
    }

    public ImageHeaderWMF(byte[] data, int off) {
        int key = LittleEndian.getInt(data, off);
        int offset = off + 4;
        if (key != -1698247209) {
            LOG.log(5, "WMF file doesn't contain a placeable header - ignore parsing");
            this.handle = 0;
            this.left = 0;
            this.top = 0;
            this.right = 200;
            this.bottom = 200;
            this.inch = 72;
            this.reserved = 0;
            return;
        }
        this.handle = LittleEndian.getUShort(data, offset);
        int offset2 = offset + 2;
        this.left = LittleEndian.getShort(data, offset2);
        int offset3 = offset2 + 2;
        this.top = LittleEndian.getShort(data, offset3);
        int offset4 = offset3 + 2;
        this.right = LittleEndian.getShort(data, offset4);
        int offset5 = offset4 + 2;
        this.bottom = LittleEndian.getShort(data, offset5);
        int offset6 = offset5 + 2;
        this.inch = LittleEndian.getUShort(data, offset6);
        int offset7 = offset6 + 2;
        this.reserved = LittleEndian.getInt(data, offset7);
        int offset8 = offset7 + 4;
        short s = LittleEndian.getShort(data, offset8);
        this.checksum = s;
        int i = offset8 + 2;
        if (s != getChecksum()) {
            LOG.log(5, "WMF checksum does not match the header data");
        }
    }

    public int getChecksum() {
        int cs = 0 ^ 52695;
        return (((((cs ^ (-25914)) ^ this.left) ^ this.top) ^ this.right) ^ this.bottom) ^ this.inch;
    }

    public void write(OutputStream out) throws IOException {
        byte[] header = new byte[22];
        LittleEndian.putInt(header, 0, APMHEADER_KEY);
        int pos = 0 + 4;
        LittleEndian.putUShort(header, pos, 0);
        int pos2 = pos + 2;
        LittleEndian.putUShort(header, pos2, this.left);
        int pos3 = pos2 + 2;
        LittleEndian.putUShort(header, pos3, this.top);
        int pos4 = pos3 + 2;
        LittleEndian.putUShort(header, pos4, this.right);
        int pos5 = pos4 + 2;
        LittleEndian.putUShort(header, pos5, this.bottom);
        int pos6 = pos5 + 2;
        LittleEndian.putUShort(header, pos6, this.inch);
        int pos7 = pos6 + 2;
        LittleEndian.putInt(header, pos7, 0);
        int checksum = getChecksum();
        this.checksum = checksum;
        LittleEndian.putUShort(header, pos7 + 4, checksum);
        out.write(header);
    }

    public Dimension getSize() {
        double coeff = 72.0d / ((double) this.inch);
        return new Dimension((int) Math.round(((double) (this.right - this.left)) * coeff), (int) Math.round(((double) (this.bottom - this.top)) * coeff));
    }

    public Rectangle getBounds() {
        int i = this.left;
        int i2 = this.top;
        return new Rectangle(i, i2, this.right - i, this.bottom - i2);
    }

    public int getLength() {
        return 22;
    }
}
