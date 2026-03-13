package org.apache.poi.hssf.record.cf;

import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public final class ColorGradientFormatting implements Cloneable {
    private ExtendedColor[] colors;
    private byte options;
    private ColorGradientThreshold[] thresholds;
    private static POILogger log = POILogFactory.getLogger((Class<?>) ColorGradientFormatting.class);
    private static BitField clamp = BitFieldFactory.getInstance(1);
    private static BitField background = BitFieldFactory.getInstance(2);

    public ColorGradientFormatting() {
        this.options = (byte) 0;
        this.options = (byte) 3;
        this.thresholds = new ColorGradientThreshold[3];
        this.colors = new ExtendedColor[3];
    }

    public ColorGradientFormatting(LittleEndianInput in) {
        this.options = (byte) 0;
        in.readShort();
        in.readByte();
        int numI = in.readByte();
        int numG = in.readByte();
        if (numI != numG) {
            log.log(5, "Inconsistent Color Gradient defintion, found " + numI + " vs " + numG + " entries");
        }
        this.options = in.readByte();
        this.thresholds = new ColorGradientThreshold[numI];
        int i = 0;
        while (true) {
            ColorGradientThreshold[] colorGradientThresholdArr = this.thresholds;
            if (i >= colorGradientThresholdArr.length) {
                break;
            }
            colorGradientThresholdArr[i] = new ColorGradientThreshold(in);
            i++;
        }
        this.colors = new ExtendedColor[numG];
        for (int i2 = 0; i2 < this.colors.length; i2++) {
            in.readDouble();
            this.colors[i2] = new ExtendedColor(in);
        }
    }

    public int getNumControlPoints() {
        return this.thresholds.length;
    }

    public void setNumControlPoints(int num) {
        ColorGradientThreshold[] colorGradientThresholdArr = this.thresholds;
        if (num != colorGradientThresholdArr.length) {
            ColorGradientThreshold[] nt = new ColorGradientThreshold[num];
            ExtendedColor[] nc = new ExtendedColor[num];
            int copy = Math.min(colorGradientThresholdArr.length, num);
            System.arraycopy(this.thresholds, 0, nt, 0, copy);
            System.arraycopy(this.colors, 0, nc, 0, copy);
            this.thresholds = nt;
            this.colors = nc;
            updateThresholdPositions();
        }
    }

    public ColorGradientThreshold[] getThresholds() {
        return this.thresholds;
    }

    public void setThresholds(ColorGradientThreshold[] thresholds) {
        this.thresholds = thresholds == null ? null : (ColorGradientThreshold[]) thresholds.clone();
        updateThresholdPositions();
    }

    public ExtendedColor[] getColors() {
        return this.colors;
    }

    public void setColors(ExtendedColor[] colors) {
        this.colors = colors == null ? null : (ExtendedColor[]) colors.clone();
    }

    public boolean isClampToCurve() {
        return getOptionFlag(clamp);
    }

    public boolean isAppliesToBackground() {
        return getOptionFlag(background);
    }

    private boolean getOptionFlag(BitField field) {
        int value = field.getValue(this.options);
        return value != 0;
    }

    private void updateThresholdPositions() {
        double step = 1.0d / ((double) (this.thresholds.length - 1));
        int i = 0;
        while (true) {
            ColorGradientThreshold[] colorGradientThresholdArr = this.thresholds;
            if (i < colorGradientThresholdArr.length) {
                colorGradientThresholdArr[i].setPosition(((double) i) * step);
                i++;
            } else {
                return;
            }
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    [Color Gradient Formatting]\n");
        buffer.append("          .clamp     = ").append(isClampToCurve()).append("\n");
        buffer.append("          .background= ").append(isAppliesToBackground()).append("\n");
        Threshold[] arr$ = this.thresholds;
        for (Threshold t : arr$) {
            buffer.append(t);
        }
        ExtendedColor[] arr$2 = this.colors;
        for (ExtendedColor c : arr$2) {
            buffer.append(c);
        }
        buffer.append("    [/Color Gradient Formatting]\n");
        return buffer.toString();
    }

    public Object clone() {
        ColorGradientFormatting rec = new ColorGradientFormatting();
        rec.options = this.options;
        ColorGradientThreshold[] colorGradientThresholdArr = new ColorGradientThreshold[this.thresholds.length];
        rec.thresholds = colorGradientThresholdArr;
        rec.colors = new ExtendedColor[this.colors.length];
        ColorGradientThreshold[] colorGradientThresholdArr2 = this.thresholds;
        System.arraycopy(colorGradientThresholdArr2, 0, colorGradientThresholdArr, 0, colorGradientThresholdArr2.length);
        ExtendedColor[] extendedColorArr = this.colors;
        System.arraycopy(extendedColorArr, 0, rec.colors, 0, extendedColorArr.length);
        return rec;
    }

    public int getDataLength() {
        int len = 6;
        Threshold[] arr$ = this.thresholds;
        for (Threshold t : arr$) {
            len += t.getDataLength();
        }
        ExtendedColor[] arr$2 = this.colors;
        for (ExtendedColor c : arr$2) {
            len = len + c.getDataLength() + 8;
        }
        return len;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.options);
        ColorGradientThreshold[] arr$ = this.thresholds;
        for (ColorGradientThreshold t : arr$) {
            t.serialize(out);
        }
        double step = 1.0d / ((double) (this.colors.length - 1));
        for (int i = 0; i < this.colors.length; i++) {
            out.writeDouble(((double) i) * step);
            ExtendedColor c = this.colors[i];
            c.serialize(out);
        }
    }
}
