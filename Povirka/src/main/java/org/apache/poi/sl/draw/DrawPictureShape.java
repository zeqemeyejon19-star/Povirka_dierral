package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.RectAlign;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class DrawPictureShape extends DrawSimpleShape {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) DrawPictureShape.class);
    private static final String WMF_IMAGE_RENDERER = "org.apache.poi.hwmf.draw.HwmfSLImageRenderer";

    public DrawPictureShape(PictureShape<?, ?> shape) {
        super(shape);
    }

    @Override // org.apache.poi.sl.draw.DrawShape, org.apache.poi.sl.draw.Drawable
    public void drawContent(Graphics2D graphics) {
        PictureData data = getShape().getPictureData();
        if (data == null) {
            return;
        }
        Rectangle2D anchor = getAnchor(graphics, getShape());
        Insets insets = getShape().getClipping();
        try {
            ImageRenderer renderer = getImageRenderer(graphics, data.getContentType());
            renderer.loadImage(data.getData(), data.getContentType());
            renderer.drawImage(graphics, anchor, insets);
        } catch (IOException e) {
            LOG.log(7, "image can't be loaded/rendered.", e);
        }
    }

    public static ImageRenderer getImageRenderer(Graphics2D graphics, String contentType) {
        ImageRenderer renderer = (ImageRenderer) graphics.getRenderingHint(Drawable.IMAGE_RENDERER);
        if (renderer != null) {
            return renderer;
        }
        if (PictureData.PictureType.WMF.contentType.equals(contentType)) {
            try {
                return (ImageRenderer) Thread.currentThread().getContextClassLoader().loadClass(WMF_IMAGE_RENDERER).newInstance();
            } catch (Exception e) {
                LOG.log(7, "WMF image renderer is not on the classpath - include poi-scratchpad jar!", e);
            }
        }
        return new BitmapImageRenderer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.poi.sl.draw.DrawSimpleShape, org.apache.poi.sl.draw.DrawShape
    public PictureShape<?, ?> getShape() {
        return (PictureShape) this.shape;
    }

    public void resize() {
        PictureShape<?, ?> ps = getShape();
        Dimension dim = ps.getPictureData().getImageDimension();
        Rectangle2D origRect = ps.getAnchor();
        double x = origRect.getX();
        double y = origRect.getY();
        double w = dim.getWidth();
        double h = dim.getHeight();
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }

    public void resize(Rectangle2D target) {
        resize(target, RectAlign.CENTER);
    }

    public void resize(Rectangle2D target, RectAlign align) {
        double w;
        double y;
        PictureShape<?, ?> ps = getShape();
        Dimension dim = ps.getPictureData().getImageDimension();
        if (dim.width <= 0 || dim.height <= 0) {
            ps.setAnchor(target);
            return;
        }
        double w2 = target.getWidth();
        double h = target.getHeight();
        double sx = w2 / ((double) dim.width);
        double sy = h / ((double) dim.height);
        double dx = 0.0d;
        double dy = 0.0d;
        if (sx > sy) {
            w = ((double) dim.width) * sy;
            dx = target.getWidth() - w;
        } else if (sy > sx) {
            h = sx * ((double) dim.height);
            dy = target.getHeight() - h;
            w = w2;
        } else {
            ps.setAnchor(target);
            return;
        }
        double x = target.getX();
        double y2 = target.getY();
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$sl$usermodel$RectAlign[align.ordinal()]) {
            case 1:
                x += dx / 2.0d;
                y = y2;
                break;
            case 2:
                x += dx;
                y = y2;
                break;
            case 3:
                x += dx;
                y = y2 + (dy / 2.0d);
                break;
            case 4:
                x += dx;
                y = y2 + dy;
                break;
            case 5:
                x += dx / 2.0d;
                y = y2 + dy;
                break;
            case 6:
                y = y2 + dy;
                break;
            case 7:
                y = y2 + (dy / 2.0d);
                break;
            case 8:
                y = y2;
                break;
            default:
                x += dx / 2.0d;
                y = y2 + (dy / 2.0d);
                break;
        }
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }

    /* JADX INFO: renamed from: org.apache.poi.sl.draw.DrawPictureShape$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$RectAlign;

        static {
            int[] iArr = new int[RectAlign.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$RectAlign = iArr;
            try {
                iArr[RectAlign.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.TOP_RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.BOTTOM_RIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.BOTTOM.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.BOTTOM_LEFT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.LEFT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$RectAlign[RectAlign.TOP_LEFT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }
}
