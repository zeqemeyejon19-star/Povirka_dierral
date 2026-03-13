package org.apache.poi.sl.draw;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.StrokeStyle;

/* JADX INFO: loaded from: classes.dex */
public class DrawShape implements Drawable {
    protected final Shape<?, ?> shape;

    public DrawShape(Shape<?, ?> shape) {
        this.shape = shape;
    }

    protected static boolean isHSLF(Shape<?, ?> shape) {
        return shape.getClass().getCanonicalName().toLowerCase(Locale.ROOT).contains("hslf");
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void applyTransform(Graphics2D graphics) {
        boolean isHSLF;
        PlaceableShape<?, ?> ps;
        Rectangle2D anchor;
        char[] cmds;
        char[] arr$;
        int len$;
        int i$;
        AffineTransform tx;
        AffineTransform txs;
        int quadrant;
        double scaleX;
        double scaleY;
        int quadrant2;
        Shape<?, ?> shape = this.shape;
        if (!(shape instanceof PlaceableShape)) {
            return;
        }
        PlaceableShape<?, ?> ps2 = (PlaceableShape) shape;
        boolean isHSLF2 = isHSLF(shape);
        AffineTransform tx2 = (AffineTransform) graphics.getRenderingHint(Drawable.GROUP_TRANSFORM);
        if (tx2 == null) {
            tx2 = new AffineTransform();
        }
        Rectangle2D anchor2 = tx2.createTransformedShape(ps2.getAnchor()).getBounds2D();
        char[] cmds2 = {'r', 'h', 'v'};
        if (isHSLF2) {
            // fill-array-data instruction
            cmds2[0] = 'h';
            cmds2[1] = 'v';
            cmds2[2] = 'r';
        }
        char[] arr$2 = cmds2;
        int len$2 = arr$2.length;
        int i$2 = 0;
        double rotation = 0.0d;
        while (i$2 < len$2) {
            char ch = arr$2[i$2];
            if (ch == 'h') {
                isHSLF = isHSLF2;
                ps = ps2;
                anchor = anchor2;
                cmds = cmds2;
                arr$ = arr$2;
                len$ = len$2;
                i$ = i$2;
                tx = tx2;
                if (ps.getFlipHorizontal()) {
                    graphics.translate(anchor.getX() + anchor.getWidth(), anchor.getY());
                    graphics.scale(-1.0d, 1.0d);
                    graphics.translate(-anchor.getX(), -anchor.getY());
                }
            } else if (ch == 'r') {
                cmds = cmds2;
                rotation = ps2.getRotation();
                if (rotation == 0.0d) {
                    isHSLF = isHSLF2;
                    ps = ps2;
                    anchor = anchor2;
                    arr$ = arr$2;
                    len$ = len$2;
                    i$ = i$2;
                    tx = tx2;
                } else {
                    double centerX = anchor2.getCenterX();
                    double centerY = anchor2.getCenterY();
                    rotation %= 360.0d;
                    if (rotation < 0.0d) {
                        rotation += 360.0d;
                    }
                    int quadrant3 = ((((int) rotation) + 45) / 90) % 4;
                    arr$ = arr$2;
                    if (quadrant3 == 1 || quadrant3 == 3) {
                        len$ = len$2;
                        if (isHSLF2) {
                            txs = new AffineTransform(tx2);
                            quadrant = quadrant3;
                            isHSLF = isHSLF2;
                            i$ = i$2;
                        } else {
                            txs = new AffineTransform();
                            txs.translate(centerX, centerY);
                            quadrant = quadrant3;
                            txs.rotate(1.5707963267948966d);
                            isHSLF = isHSLF2;
                            i$ = i$2;
                            txs.translate(-centerX, -centerY);
                            txs.concatenate(tx2);
                        }
                        txs.translate(centerX, centerY);
                        txs.rotate(1.5707963267948966d);
                        txs.translate(-centerX, -centerY);
                        Rectangle2D anchor22 = txs.createTransformedShape(ps2.getAnchor()).getBounds2D();
                        ps = ps2;
                        double scaleX2 = safeScale(anchor2.getWidth(), anchor22.getWidth());
                        double scaleY2 = safeScale(anchor2.getHeight(), anchor22.getHeight());
                        scaleX = scaleX2;
                        scaleY = scaleY2;
                        quadrant2 = quadrant;
                    } else {
                        quadrant2 = 0;
                        isHSLF = isHSLF2;
                        ps = ps2;
                        len$ = len$2;
                        i$ = i$2;
                        scaleX = 1.0d;
                        scaleY = 1.0d;
                    }
                    graphics.translate(centerX, centerY);
                    tx = tx2;
                    anchor = anchor2;
                    double rot = Math.toRadians(rotation - (((double) quadrant2) * 90.0d));
                    if (rot != 0.0d) {
                        graphics.rotate(rot);
                    }
                    graphics.scale(scaleX, scaleY);
                    double scaleX3 = quadrant2;
                    double rot2 = Math.toRadians(scaleX3 * 90.0d);
                    if (rot2 != 0.0d) {
                        graphics.rotate(rot2);
                    }
                    graphics.translate(-centerX, -centerY);
                }
            } else if (ch == 'v') {
                if (ps2.getFlipVertical()) {
                    cmds = cmds2;
                    graphics.translate(anchor2.getX(), anchor2.getY() + anchor2.getHeight());
                    graphics.scale(1.0d, -1.0d);
                    graphics.translate(-anchor2.getX(), -anchor2.getY());
                    isHSLF = isHSLF2;
                    ps = ps2;
                    anchor = anchor2;
                    arr$ = arr$2;
                    len$ = len$2;
                    i$ = i$2;
                    tx = tx2;
                } else {
                    cmds = cmds2;
                    isHSLF = isHSLF2;
                    ps = ps2;
                    anchor = anchor2;
                    arr$ = arr$2;
                    len$ = len$2;
                    i$ = i$2;
                    tx = tx2;
                }
            } else {
                throw new RuntimeException("unexpected transform code " + ch);
            }
            tx2 = tx;
            len$2 = len$;
            arr$2 = arr$;
            ps2 = ps;
            anchor2 = anchor;
            cmds2 = cmds;
            isHSLF2 = isHSLF;
            i$2 = i$ + 1;
        }
    }

    private static double safeScale(double dim1, double dim2) {
        if (dim1 == 0.0d || dim2 == 0.0d) {
            return 1.0d;
        }
        return dim1 / dim2;
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void draw(Graphics2D graphics) {
    }

    @Override // org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D graphics) {
    }

    public static Rectangle2D getAnchor(Graphics2D graphics, PlaceableShape<?, ?> shape) {
        return getAnchor(graphics, shape.getAnchor());
    }

    public static Rectangle2D getAnchor(Graphics2D graphics, Rectangle2D anchor) {
        AffineTransform tx;
        if (graphics != null && (tx = (AffineTransform) graphics.getRenderingHint(Drawable.GROUP_TRANSFORM)) != null && !tx.isIdentity()) {
            return tx.createTransformedShape(anchor).getBounds2D();
        }
        return anchor;
    }

    protected Shape<?, ?> getShape() {
        return this.shape;
    }

    protected static BasicStroke getStroke(StrokeStyle strokeStyle) {
        StrokeStyle.LineDash lineDash;
        float[] dashPatF;
        StrokeStyle.LineCap lineCapE;
        int lineCap;
        float lineWidth = (float) strokeStyle.getLineWidth();
        if (lineWidth == 0.0f) {
            lineWidth = 0.25f;
        }
        StrokeStyle.LineDash lineDash2 = strokeStyle.getLineDash();
        if (lineDash2 != null) {
            lineDash = lineDash2;
        } else {
            lineDash = StrokeStyle.LineDash.SOLID;
        }
        int[] dashPatI = lineDash.pattern;
        if (dashPatI == null) {
            dashPatF = null;
        } else {
            float[] dashPatF2 = new float[dashPatI.length];
            for (int i = 0; i < dashPatI.length; i++) {
                dashPatF2[i] = dashPatI[i] * Math.max(1.0f, lineWidth);
            }
            dashPatF = dashPatF2;
        }
        StrokeStyle.LineCap lineCapE2 = strokeStyle.getLineCap();
        if (lineCapE2 != null) {
            lineCapE = lineCapE2;
        } else {
            lineCapE = StrokeStyle.LineCap.FLAT;
        }
        int i2 = AnonymousClass1.$SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCap[lineCapE.ordinal()];
        if (i2 == 1) {
            lineCap = 1;
        } else if (i2 == 2) {
            lineCap = 2;
        } else {
            lineCap = 0;
        }
        return new BasicStroke(lineWidth, lineCap, 1, lineWidth, dashPatF, 0.0f);
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawShape$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCap;

        static {
            int[] iArr = new int[StrokeStyle.LineCap.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCap = iArr;
            try {
                iArr[StrokeStyle.LineCap.ROUND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCap[StrokeStyle.LineCap.SQUARE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCap[StrokeStyle.LineCap.FLAT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
