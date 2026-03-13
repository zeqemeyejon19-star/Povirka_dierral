package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class BitmapImageRenderer implements ImageRenderer {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ImageRenderer.class);
    protected BufferedImage img;

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public void loadImage(InputStream data, String contentType) throws IOException {
        this.img = readImage(data, contentType);
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public void loadImage(byte[] data, String contentType) throws IOException {
        this.img = readImage(new ByteArrayInputStream(data), contentType);
    }

    /* JADX WARN: Code restructure failed: missing block: B:80:0x0176, code lost:
    
        r4.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x017a, code lost:
    
        if (r3 != null) goto L87;
     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x017c, code lost:
    
        if (r2 != null) goto L85;
     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x017e, code lost:
    
        org.apache.poi.sl.draw.BitmapImageRenderer.LOG.log(5, "Content-type: " + r21 + " is not support. Image ignored.");
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01a5, code lost:
    
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01a8, code lost:
    
        throw r2;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01b0, code lost:
    
        if (r3.getType() == 2) goto L91;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01b2, code lost:
    
        r0 = new java.awt.image.BufferedImage(r3.getWidth(), r3.getHeight(), 2);
        r1 = r0.getGraphics();
        r1.drawImage(r3, 0, 0, (java.awt.image.ImageObserver) null);
        r1.dispose();
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x01ca, code lost:
    
        return r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01cb, code lost:
    
        return r3;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.awt.image.BufferedImage readImage(java.io.InputStream r20, java.lang.String r21) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 467
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.sl.draw.BitmapImageRenderer.readImage(java.io.InputStream, java.lang.String):java.awt.image.BufferedImage");
    }

    private static int findTruncatedBlackBox(BufferedImage img, int width, int height) {
        for (int h = height - 1; h > 0; h--) {
            int w = width - 1;
            while (w > 0) {
                int p = img.getRGB(w, h);
                if (p == -16777216) {
                    int p2 = width / 10;
                    w -= p2;
                } else {
                    return h + 1;
                }
            }
        }
        return 0;
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public BufferedImage getImage() {
        return this.img;
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public BufferedImage getImage(Dimension dim) {
        double w_old = this.img.getWidth();
        double h_old = this.img.getHeight();
        BufferedImage scaled = new BufferedImage((int) w_old, (int) h_old, 2);
        double w_new = dim.getWidth();
        double h_new = dim.getHeight();
        AffineTransform at = new AffineTransform();
        at.scale(w_new / w_old, h_new / h_old);
        AffineTransformOp scaleOp = new AffineTransformOp(at, 2);
        scaleOp.filter(this.img, scaled);
        return scaled;
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public Dimension getDimension() {
        return this.img == null ? new Dimension(0, 0) : new Dimension(this.img.getWidth(), this.img.getHeight());
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public void setAlpha(double alpha) {
        if (this.img == null) {
            return;
        }
        Dimension dim = getDimension();
        BufferedImage newImg = new BufferedImage((int) dim.getWidth(), (int) dim.getHeight(), 2);
        Graphics2D g = newImg.createGraphics();
        RescaleOp op = new RescaleOp(new float[]{1.0f, 1.0f, 1.0f, (float) alpha}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, (RenderingHints) null);
        g.drawImage(this.img, op, 0, 0);
        g.dispose();
        this.img = newImg;
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor) {
        return drawImage(graphics, anchor, null);
    }

    @Override // org.apache.poi.sl.draw.ImageRenderer
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor, Insets clip) {
        Insets clip2;
        if (this.img == null) {
            return false;
        }
        boolean isClipped = true;
        if (clip != null) {
            clip2 = clip;
        } else {
            isClipped = false;
            clip2 = new Insets(0, 0, 0, 0);
        }
        int iw = this.img.getWidth();
        int ih = this.img.getHeight();
        double cw = ((double) ((100000 - clip2.left) - clip2.right)) / 100000.0d;
        double ch = ((double) ((100000 - clip2.top) - clip2.bottom)) / 100000.0d;
        double sx = anchor.getWidth() / (((double) iw) * cw);
        double sy = anchor.getHeight() / (((double) ih) * ch);
        double x = anchor.getX();
        double ch2 = iw;
        double cw2 = clip2.left;
        double tx = x - (((ch2 * sx) * cw2) / 100000.0d);
        double ty = anchor.getY() - (((((double) ih) * sy) * ((double) clip2.top)) / 100000.0d);
        AffineTransform at = new AffineTransform(sx, 0.0d, 0.0d, sy, tx, ty);
        Shape clipOld = graphics.getClip();
        if (isClipped) {
            graphics.clip(anchor.getBounds2D());
        }
        graphics.drawRenderedImage(this.img, at);
        graphics.setClip(clipOld);
        return true;
    }
}
