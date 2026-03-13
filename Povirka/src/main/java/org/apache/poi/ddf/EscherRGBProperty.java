package org.apache.poi.ddf;

import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes.dex */
public class EscherRGBProperty extends EscherSimpleProperty {
    public EscherRGBProperty(short propertyNumber, int rgbColor) {
        super(propertyNumber, rgbColor);
    }

    public int getRgbColor() {
        return getPropertyValue();
    }

    public byte getRed() {
        return (byte) (getRgbColor() & 255);
    }

    public byte getGreen() {
        return (byte) ((getRgbColor() >> 8) & 255);
    }

    public byte getBlue() {
        return (byte) ((getRgbColor() >> 16) & 255);
    }

    @Override // org.apache.poi.ddf.EscherSimpleProperty, org.apache.poi.ddf.EscherProperty
    public String toXml(String tab) {
        StringBuilder builder = new StringBuilder();
        builder.append(tab).append("<").append(getClass().getSimpleName()).append(" id=\"0x").append(HexDump.toHex(getId())).append("\" name=\"").append(getName()).append("\" blipId=\"").append(isBlipId()).append("\" value=\"0x").append(HexDump.toHex(getRgbColor())).append("\"/>");
        return builder.toString();
    }
}
