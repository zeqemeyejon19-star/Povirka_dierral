package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.PresetColor;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveFixedPercentage;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSLFColor {
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) XSLFColor.class);
    private Color _color;
    private CTSchemeColor _phClr;
    private XmlObject _xmlObject;

    public XSLFColor(XmlObject obj, XSLFTheme theme, CTSchemeColor phClr) {
        this._xmlObject = obj;
        this._phClr = phClr;
        this._color = toColor(obj, theme);
    }

    @Internal
    public XmlObject getXmlObject() {
        return this._xmlObject;
    }

    public Color getColor() {
        return DrawPaint.applyColorTransform(getColorStyle());
    }

    public ColorStyle getColorStyle() {
        return new ColorStyle() { // from class: org.apache.poi.xslf.usermodel.XSLFColor.1
            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public Color getColor() {
                return XSLFColor.this._color;
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getAlpha() {
                return XSLFColor.this.getRawValue("alpha");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getHueOff() {
                return XSLFColor.this.getRawValue("hueOff");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getHueMod() {
                return XSLFColor.this.getRawValue("hueMod");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getSatOff() {
                return XSLFColor.this.getRawValue("satOff");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getSatMod() {
                return XSLFColor.this.getRawValue("satMod");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getLumOff() {
                return XSLFColor.this.getRawValue("lumOff");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getLumMod() {
                return XSLFColor.this.getRawValue("lumMod");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getShade() {
                return XSLFColor.this.getRawValue("shade");
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getTint() {
                return XSLFColor.this.getRawValue("tint");
            }
        };
    }

    Color toColor(XmlObject obj, XSLFTheme theme) {
        Color color = null;
        for (CTHslColor cTHslColor : obj.selectPath("*")) {
            if (cTHslColor instanceof CTHslColor) {
                CTHslColor hsl = cTHslColor;
                int h = hsl.getHue2();
                int s = hsl.getSat2();
                int l = hsl.getLum2();
                color = DrawPaint.HSL2RGB(((double) h) / 60000.0d, ((double) s) / 1000.0d, ((double) l) / 1000.0d, 1.0d);
            } else if (cTHslColor instanceof CTPresetColor) {
                CTPresetColor prst = (CTPresetColor) cTHslColor;
                String colorName = prst.getVal().toString();
                PresetColor pc = PresetColor.valueOfOoxmlId(colorName);
                if (pc != null) {
                    color = pc.color;
                }
            } else if (cTHslColor instanceof CTSchemeColor) {
                CTSchemeColor schemeColor = (CTSchemeColor) cTHslColor;
                String colorRef = schemeColor.getVal().toString();
                CTSchemeColor cTSchemeColor = this._phClr;
                if (cTSchemeColor != null) {
                    colorRef = cTSchemeColor.getVal().toString();
                }
                CTColor ctColor = theme.getCTColor(colorRef);
                if (ctColor != null) {
                    color = toColor(ctColor, null);
                }
            } else if (cTHslColor instanceof CTScRgbColor) {
                CTScRgbColor scrgb = (CTScRgbColor) cTHslColor;
                color = new Color(DrawPaint.lin2srgb(scrgb.getR()), DrawPaint.lin2srgb(scrgb.getG()), DrawPaint.lin2srgb(scrgb.getB()));
            } else if (cTHslColor instanceof CTSRgbColor) {
                CTSRgbColor srgb = (CTSRgbColor) cTHslColor;
                byte[] val = srgb.getVal();
                color = new Color(val[0] & 255, val[1] & 255, val[2] & 255);
            } else if (cTHslColor instanceof CTSystemColor) {
                CTSystemColor sys = (CTSystemColor) cTHslColor;
                if (sys.isSetLastClr()) {
                    byte[] val2 = sys.getLastClr();
                    color = new Color(val2[0] & 255, val2[1] & 255, val2[2] & 255);
                } else {
                    String colorName2 = sys.getVal().toString();
                    PresetColor pc2 = PresetColor.valueOfOoxmlId(colorName2);
                    if (pc2 != null) {
                        color = pc2.color;
                    }
                    if (color == null) {
                        color = Color.black;
                    }
                }
            } else if (!(cTHslColor instanceof CTFontReference)) {
                throw new IllegalArgumentException("Unexpected color choice: " + cTHslColor.getClass());
            }
        }
        return color;
    }

    @Internal
    protected void setColor(Color color) {
        CTSolidColorFillProperties cTSolidColorFillProperties = this._xmlObject;
        if (!(cTSolidColorFillProperties instanceof CTSolidColorFillProperties)) {
            LOGGER.log(7, "XSLFColor.setColor currently only supports CTSolidColorFillProperties");
            return;
        }
        CTSolidColorFillProperties fill = cTSolidColorFillProperties;
        if (fill.isSetSrgbClr()) {
            fill.unsetSrgbClr();
        }
        if (fill.isSetScrgbClr()) {
            fill.unsetScrgbClr();
        }
        if (fill.isSetHslClr()) {
            fill.unsetHslClr();
        }
        if (fill.isSetPrstClr()) {
            fill.unsetPrstClr();
        }
        if (fill.isSetSchemeClr()) {
            fill.unsetSchemeClr();
        }
        if (fill.isSetSysClr()) {
            fill.unsetSysClr();
        }
        CTPositiveFixedPercentage alphaPct = null;
        float[] rgbaf = color.getRGBComponents((float[]) null);
        boolean addAlpha = rgbaf.length == 4 && rgbaf[3] < 1.0f;
        if (isInt(rgbaf[0]) && isInt(rgbaf[1]) && isInt(rgbaf[2])) {
            CTSRgbColor rgb = fill.addNewSrgbClr();
            byte[] rgbBytes = {(byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue()};
            rgb.setVal(rgbBytes);
            if (addAlpha) {
                alphaPct = rgb.addNewAlpha();
            }
        } else {
            CTScRgbColor rgb2 = fill.addNewScrgbClr();
            rgb2.setR(DrawPaint.srgb2lin(rgbaf[0]));
            rgb2.setG(DrawPaint.srgb2lin(rgbaf[1]));
            rgb2.setB(DrawPaint.srgb2lin(rgbaf[2]));
            if (addAlpha) {
                alphaPct = rgb2.addNewAlpha();
            }
        }
        if (alphaPct != null) {
            alphaPct.setVal((int) (rgbaf[3] * 100000.0f));
        }
    }

    private static boolean isInt(float f) {
        return Math.abs(((double) (f * 255.0f)) - Math.rint((double) (255.0f * f))) < 9.999999747378752E-6d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getRawValue(String elem) {
        Node attr;
        Node attr2;
        String query = "declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' $this//a:" + elem;
        CTSchemeColor cTSchemeColor = this._phClr;
        if (cTSchemeColor != null) {
            XmlObject[] obj = cTSchemeColor.selectPath(query);
            if (obj.length == 1 && (attr2 = obj[0].getDomNode().getAttributes().getNamedItem("val")) != null) {
                return Integer.parseInt(attr2.getNodeValue());
            }
        }
        XmlObject[] obj2 = this._xmlObject.selectPath(query);
        if (obj2.length == 1 && (attr = obj2[0].getDomNode().getAttributes().getNamedItem("val")) != null) {
            return Integer.parseInt(attr.getNodeValue());
        }
        return -1;
    }

    private int getPercentageValue(String elem) {
        int val = getRawValue(elem);
        return val == -1 ? val : val / 1000;
    }

    private int getAngleValue(String elem) {
        int val = getRawValue(elem);
        return val == -1 ? val : val / 60000;
    }

    int getAlpha() {
        return getPercentageValue("alpha");
    }

    int getAlphaMod() {
        return getPercentageValue("alphaMod");
    }

    int getAlphaOff() {
        return getPercentageValue("alphaOff");
    }

    int getHue() {
        return getAngleValue("hue");
    }

    int getHueMod() {
        return getPercentageValue("hueMod");
    }

    int getHueOff() {
        return getPercentageValue("hueOff");
    }

    int getLum() {
        return getPercentageValue("lum");
    }

    int getLumMod() {
        return getPercentageValue("lumMod");
    }

    int getLumOff() {
        return getPercentageValue("lumOff");
    }

    int getSat() {
        return getPercentageValue("sat");
    }

    int getSatMod() {
        return getPercentageValue("satMod");
    }

    int getSatOff() {
        return getPercentageValue("satOff");
    }

    int getRed() {
        return getPercentageValue("red");
    }

    int getRedMod() {
        return getPercentageValue("redMod");
    }

    int getRedOff() {
        return getPercentageValue("redOff");
    }

    int getGreen() {
        return getPercentageValue("green");
    }

    int getGreenMod() {
        return getPercentageValue("greenMod");
    }

    int getGreenOff() {
        return getPercentageValue("greenOff");
    }

    int getBlue() {
        return getPercentageValue("blue");
    }

    int getBlueMod() {
        return getPercentageValue("blueMod");
    }

    int getBlueOff() {
        return getPercentageValue("blueOff");
    }

    public int getShade() {
        return getPercentageValue("shade");
    }

    public int getTint() {
        return getPercentageValue("tint");
    }
}
