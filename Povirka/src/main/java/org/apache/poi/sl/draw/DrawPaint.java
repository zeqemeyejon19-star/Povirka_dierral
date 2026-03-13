package org.apache.poi.sl.draw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class DrawPaint {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) DrawPaint.class);
    private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.0f);
    protected PlaceableShape<?, ?> shape;

    public DrawPaint(PlaceableShape<?, ?> shape) {
        this.shape = shape;
    }

    private static class SimpleSolidPaint implements PaintStyle.SolidPaint {
        private final ColorStyle solidColor;

        SimpleSolidPaint(final Color color) {
            if (color == null) {
                throw new NullPointerException("Color needs to be specified");
            }
            this.solidColor = new ColorStyle() { // from class: org.apache.poi.sl.draw.DrawPaint.SimpleSolidPaint.1
                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public Color getColor() {
                    return new Color(color.getRed(), color.getGreen(), color.getBlue());
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getAlpha() {
                    return (int) Math.round((((double) color.getAlpha()) * 100000.0d) / 255.0d);
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getHueOff() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getHueMod() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getSatOff() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getSatMod() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getLumOff() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getLumMod() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getShade() {
                    return -1;
                }

                @Override // org.apache.poi.sl.usermodel.ColorStyle
                public int getTint() {
                    return -1;
                }
            };
        }

        SimpleSolidPaint(ColorStyle color) {
            if (color == null) {
                throw new NullPointerException("Color needs to be specified");
            }
            this.solidColor = color;
        }

        @Override // org.apache.poi.sl.usermodel.PaintStyle.SolidPaint
        public ColorStyle getSolidColor() {
            return this.solidColor;
        }
    }

    public static PaintStyle.SolidPaint createSolidPaint(Color color) {
        if (color == null) {
            return null;
        }
        return new SimpleSolidPaint(color);
    }

    public static PaintStyle.SolidPaint createSolidPaint(ColorStyle color) {
        if (color == null) {
            return null;
        }
        return new SimpleSolidPaint(color);
    }

    public Paint getPaint(Graphics2D graphics, PaintStyle paint) {
        return getPaint(graphics, paint, PaintStyle.PaintModifier.NORM);
    }

    public Paint getPaint(Graphics2D graphics, PaintStyle paint, PaintStyle.PaintModifier modifier) {
        if (modifier == PaintStyle.PaintModifier.NONE) {
            return null;
        }
        if (paint instanceof PaintStyle.SolidPaint) {
            return getSolidPaint((PaintStyle.SolidPaint) paint, graphics, modifier);
        }
        if (paint instanceof PaintStyle.GradientPaint) {
            return getGradientPaint((PaintStyle.GradientPaint) paint, graphics);
        }
        if (paint instanceof PaintStyle.TexturePaint) {
            return getTexturePaint((PaintStyle.TexturePaint) paint, graphics);
        }
        return null;
    }

    protected Paint getSolidPaint(PaintStyle.SolidPaint fill, Graphics2D graphics, final PaintStyle.PaintModifier modifier) {
        final ColorStyle orig = fill.getSolidColor();
        ColorStyle cs = new ColorStyle() { // from class: org.apache.poi.sl.draw.DrawPaint.1
            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public Color getColor() {
                return orig.getColor();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getAlpha() {
                return orig.getAlpha();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getHueOff() {
                return orig.getHueOff();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getHueMod() {
                return orig.getHueMod();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getSatOff() {
                return orig.getSatOff();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getSatMod() {
                return orig.getSatMod();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getLumOff() {
                return orig.getLumOff();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getLumMod() {
                return orig.getLumMod();
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getShade() {
                int shade = orig.getShade();
                int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier[modifier.ordinal()];
                if (i == 1) {
                    return Math.min(100000, Math.max(0, shade) + 40000);
                }
                if (i == 2) {
                    return Math.min(100000, Math.max(0, shade) + 20000);
                }
                return shade;
            }

            @Override // org.apache.poi.sl.usermodel.ColorStyle
            public int getTint() {
                int tint = orig.getTint();
                int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier[modifier.ordinal()];
                if (i == 3) {
                    return Math.min(100000, Math.max(0, tint) + 40000);
                }
                if (i == 4) {
                    return Math.min(100000, Math.max(0, tint) + 20000);
                }
                return tint;
            }
        };
        return applyColorTransform(cs);
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawPaint$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$GradientPaint$GradientType;
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier;

        static {
            int[] iArr = new int[PaintStyle.GradientPaint.GradientType.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$GradientPaint$GradientType = iArr;
            try {
                iArr[PaintStyle.GradientPaint.GradientType.linear.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$GradientPaint$GradientType[PaintStyle.GradientPaint.GradientType.circular.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$GradientPaint$GradientType[PaintStyle.GradientPaint.GradientType.shape.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            int[] iArr2 = new int[PaintStyle.PaintModifier.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier = iArr2;
            try {
                iArr2[PaintStyle.PaintModifier.DARKEN.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier[PaintStyle.PaintModifier.DARKEN_LESS.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier[PaintStyle.PaintModifier.LIGHTEN.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$PaintModifier[PaintStyle.PaintModifier.LIGHTEN_LESS.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    protected Paint getGradientPaint(PaintStyle.GradientPaint fill, Graphics2D graphics) {
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$sl$usermodel$PaintStyle$GradientPaint$GradientType[fill.getGradientType().ordinal()];
        if (i == 1) {
            return createLinearGradientPaint(fill, graphics);
        }
        if (i == 2) {
            return createRadialGradientPaint(fill, graphics);
        }
        if (i == 3) {
            return createPathGradientPaint(fill, graphics);
        }
        throw new UnsupportedOperationException("gradient fill of type " + fill + " not supported.");
    }

    protected Paint getTexturePaint(PaintStyle.TexturePaint fill, Graphics2D graphics) {
        BufferedImage image;
        InputStream is = fill.getImageData();
        if (is == null) {
            return null;
        }
        if (graphics == null) {
            throw new AssertionError();
        }
        ImageRenderer renderer = DrawPictureShape.getImageRenderer(graphics, fill.getContentType());
        try {
            try {
                renderer.loadImage(is, fill.getContentType());
                int alpha = fill.getAlpha();
                if (alpha >= 0 && alpha < 100000) {
                    renderer.setAlpha(alpha / 100000.0f);
                }
                Rectangle2D textAnchor = this.shape.getAnchor();
                if ("image/x-wmf".equals(fill.getContentType())) {
                    image = renderer.getImage(new Dimension((int) textAnchor.getWidth(), (int) textAnchor.getHeight()));
                } else {
                    image = renderer.getImage();
                }
                if (image == null) {
                    LOG.log(7, "Can't load image data");
                    return null;
                }
                return new TexturePaint(image, textAnchor);
            } finally {
                is.close();
            }
        } catch (IOException e) {
            LOG.log(7, "Can't load image data - using transparent color", e);
            return null;
        }
    }

    public static Color applyColorTransform(ColorStyle color) {
        if (color == null || color.getColor() == null) {
            return TRANSPARENT;
        }
        Color result = color.getColor();
        double alpha = getAlpha(result, color);
        double[] hsl = RGB2HSL(result);
        applyHslModOff(hsl, 0, color.getHueMod(), color.getHueOff());
        applyHslModOff(hsl, 1, color.getSatMod(), color.getSatOff());
        applyHslModOff(hsl, 2, color.getLumMod(), color.getLumOff());
        applyShade(hsl, color);
        applyTint(hsl, color);
        return HSL2RGB(hsl[0], hsl[1], hsl[2], alpha);
    }

    private static double getAlpha(Color c, ColorStyle fc) {
        double alpha = ((double) c.getAlpha()) / 255.0d;
        int fcAlpha = fc.getAlpha();
        if (fcAlpha != -1) {
            alpha *= ((double) fcAlpha) / 100000.0d;
        }
        return Math.min(1.0d, Math.max(0.0d, alpha));
    }

    private static void applyHslModOff(double[] hsl, int hslPart, int mod, int off) {
        if (mod == -1) {
            mod = 100000;
        }
        if (off == -1) {
            off = 0;
        }
        if (mod != 100000 || off != 0) {
            double fOff = ((double) off) / 1000.0d;
            double fMod = ((double) mod) / 100000.0d;
            hsl[hslPart] = (hsl[hslPart] * fMod) + fOff;
        }
    }

    private static void applyShade(double[] hsl, ColorStyle fc) {
        int shade = fc.getShade();
        if (shade == -1) {
            return;
        }
        double shadePct = ((double) shade) / 100000.0d;
        hsl[2] = hsl[2] * (1.0d - shadePct);
    }

    private static void applyTint(double[] hsl, ColorStyle fc) {
        int tint = fc.getTint();
        if (tint == -1) {
            return;
        }
        double tintPct = ((double) tint) / 100000.0d;
        hsl[2] = (hsl[2] * (1.0d - tintPct)) + (100.0d - ((1.0d - tintPct) * 100.0d));
    }

    protected Paint createLinearGradientPaint(PaintStyle.GradientPaint fill, Graphics2D graphics) {
        double angle = fill.getGradientAngle();
        if (!fill.isRotatedWithShape()) {
            angle -= this.shape.getRotation();
        }
        Rectangle2D anchor = DrawShape.getAnchor(graphics, this.shape);
        double h = anchor.getHeight();
        double w = anchor.getWidth();
        double x = anchor.getX();
        double y = anchor.getY();
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(angle), anchor.getCenterX(), anchor.getCenterY());
        double diagonal = Math.sqrt((h * h) + (w * w));
        Point2D p1 = at.transform(new Point2D.Double((x + (w / 2.0d)) - (diagonal / 2.0d), y + (h / 2.0d)), (Point2D) null);
        Point2D p2 = at.transform(new Point2D.Double(x + w, y + (h / 2.0d)), (Point2D) null);
        if (p1.equals(p2)) {
            return null;
        }
        float[] fractions = fill.getGradientFractions();
        Color[] colors = new Color[fractions.length];
        int i = 0;
        ColorStyle[] arr$ = fill.getGradientColors();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            ColorStyle fc = arr$[i$];
            int i2 = i + 1;
            colors[i] = fc == null ? TRANSPARENT : applyColorTransform(fc);
            i$++;
            i = i2;
        }
        return new LinearGradientPaint(p1, p2, fractions, colors);
    }

    protected Paint createRadialGradientPaint(PaintStyle.GradientPaint fill, Graphics2D graphics) {
        Rectangle2D anchor = DrawShape.getAnchor(graphics, this.shape);
        Point2D.Double r1 = new Point2D.Double(anchor.getX() + (anchor.getWidth() / 2.0d), anchor.getY() + (anchor.getHeight() / 2.0d));
        float radius = (float) Math.max(anchor.getWidth(), anchor.getHeight());
        float[] fractions = fill.getGradientFractions();
        Color[] colors = new Color[fractions.length];
        int i = 0;
        ColorStyle[] arr$ = fill.getGradientColors();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            ColorStyle fc = arr$[i$];
            colors[i] = applyColorTransform(fc);
            i$++;
            i++;
        }
        return new RadialGradientPaint(r1, radius, fractions, colors);
    }

    protected Paint createPathGradientPaint(PaintStyle.GradientPaint fill, Graphics2D graphics) {
        float[] fractions = fill.getGradientFractions();
        Color[] colors = new Color[fractions.length];
        int i = 0;
        ColorStyle[] arr$ = fill.getGradientColors();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            ColorStyle fc = arr$[i$];
            colors[i] = applyColorTransform(fc);
            i$++;
            i++;
        }
        return new PathGradientPaint(colors, fractions);
    }

    protected void snapToAnchor(Point2D p, Rectangle2D anchor) {
        if (p.getX() < anchor.getX()) {
            p.setLocation(anchor.getX(), p.getY());
        } else if (p.getX() > anchor.getX() + anchor.getWidth()) {
            p.setLocation(anchor.getX() + anchor.getWidth(), p.getY());
        }
        if (p.getY() < anchor.getY()) {
            p.setLocation(p.getX(), anchor.getY());
        } else if (p.getY() > anchor.getY() + anchor.getHeight()) {
            p.setLocation(p.getX(), anchor.getY() + anchor.getHeight());
        }
    }

    public static Color HSL2RGB(double h, double s, double l, double alpha) {
        double s2 = Math.max(0.0d, Math.min(100.0d, s));
        double l2 = Math.max(0.0d, Math.min(100.0d, l));
        if (alpha < 0.0d || alpha > 1.0d) {
            String message = "Color parameter outside of expected range - Alpha: " + alpha;
            throw new IllegalArgumentException(message);
        }
        double h2 = (h % 360.0d) / 360.0d;
        double s3 = s2 / 100.0d;
        double l3 = l2 / 100.0d;
        double q = l3 < 0.5d ? (s3 + 1.0d) * l3 : (l3 + s3) - (s3 * l3);
        double p = (2.0d * l3) - q;
        double d = q;
        double r = Math.max(0.0d, HUE2RGB(p, d, h2 + 0.3333333333333333d));
        double s4 = HUE2RGB(p, d, h2);
        double g = Math.max(0.0d, s4);
        double b = Math.max(0.0d, HUE2RGB(p, d, h2 - 0.3333333333333333d));
        return new Color((float) Math.min(r, 1.0d), (float) Math.min(g, 1.0d), (float) Math.min(b, 1.0d), (float) alpha);
    }

    private static double HUE2RGB(double p, double q, double h) {
        if (h < 0.0d) {
            h += 1.0d;
        }
        if (h > 1.0d) {
            h -= 1.0d;
        }
        if (h * 6.0d < 1.0d) {
            return ((q - p) * 6.0d * h) + p;
        }
        if (h * 2.0d < 1.0d) {
            return q;
        }
        if (3.0d * h < 2.0d) {
            return ((q - p) * 6.0d * (0.6666666666666666d - h)) + p;
        }
        return p;
    }

    private static double[] RGB2HSL(Color color) {
        double s;
        float[] rgb = color.getRGBColorComponents((float[]) null);
        double r = rgb[0];
        double g = rgb[1];
        double b = rgb[2];
        double min = Math.min(r, Math.min(g, b));
        double max = Math.max(r, Math.max(g, b));
        double h = 0.0d;
        if (max == min) {
            h = 0.0d;
        } else if (max == r) {
            h = ((((g - b) * 60.0d) / (max - min)) + 360.0d) % 360.0d;
        } else if (max == g) {
            h = (((b - r) * 60.0d) / (max - min)) + 120.0d;
        } else if (max == b) {
            h = (((r - g) * 60.0d) / (max - min)) + 240.0d;
        }
        double l = (max + min) / 2.0d;
        if (max == min) {
            s = 0.0d;
        } else if (l <= 0.5d) {
            s = (max - min) / (max + min);
        } else {
            s = (max - min) / ((2.0d - max) - min);
        }
        return new double[]{h, s * 100.0d, 100.0d * l};
    }

    public static int srgb2lin(float sRGB) {
        if (sRGB <= 0.04045d) {
            return (int) Math.rint((((double) sRGB) * 100000.0d) / 12.92d);
        }
        return (int) Math.rint(Math.pow((((double) sRGB) + 0.055d) / 1.055d, 2.4d) * 100000.0d);
    }

    public static float lin2srgb(int linRGB) {
        if (linRGB <= 0.0031308d) {
            return (float) ((((double) linRGB) / 100000.0d) * 12.92d);
        }
        return (float) ((Math.pow(((double) linRGB) / 100000.0d, 0.4166666666666667d) * 1.055d) - 0.055d);
    }
}
