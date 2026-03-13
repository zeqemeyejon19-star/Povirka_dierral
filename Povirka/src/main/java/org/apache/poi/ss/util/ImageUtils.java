package org.apache.poi.ss.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public class ImageUtils {
    public static final int PIXEL_DPI = 96;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) ImageUtils.class);

    public static Dimension getImageDimension(InputStream is, int type) {
        Dimension size = new Dimension();
        if (type != 5 && type != 6 && type != 7) {
            logger.log(5, "Only JPEG, PNG and DIB pictures can be automatically sized");
        } else {
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(is);
                try {
                    Iterator<ImageReader> i = ImageIO.getImageReaders(iis);
                    ImageReader r = i.next();
                    try {
                        r.setInput(iis);
                        BufferedImage img = r.read(0);
                        int[] dpi = getResolution(r);
                        if (dpi[0] == 0) {
                            dpi[0] = 96;
                        }
                        if (dpi[1] == 0) {
                            dpi[1] = 96;
                        }
                        size.width = (img.getWidth() * 96) / dpi[0];
                        size.height = (img.getHeight() * 96) / dpi[1];
                    } finally {
                        r.dispose();
                    }
                } finally {
                    iis.close();
                }
            } catch (IOException e) {
                logger.log(5, e);
            }
        }
        return size;
    }

    public static int[] getResolution(ImageReader r) throws IOException {
        int hdpi = 96;
        int vdpi = 96;
        Element node = (Element) r.getImageMetadata(0).getAsTree("javax_imageio_1.0");
        NodeList lst = node.getElementsByTagName("HorizontalPixelSize");
        if (lst != null && lst.getLength() == 1) {
            hdpi = (int) (25.4d / ((double) Float.parseFloat(((Element) lst.item(0)).getAttribute("value"))));
        }
        NodeList lst2 = node.getElementsByTagName("VerticalPixelSize");
        if (lst2 != null && lst2.getLength() == 1) {
            vdpi = (int) (25.4d / ((double) Float.parseFloat(((Element) lst2.item(0)).getAttribute("value"))));
        }
        return new int[]{hdpi, vdpi};
    }

    public static Dimension setPreferredSize(Picture picture, double scaleX, double scaleY) {
        double w;
        int col2;
        int row2;
        int dy2;
        int col22;
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = anchor instanceof HSSFClientAnchor;
        PictureData data = picture.getPictureData();
        Sheet sheet = picture.getSheet();
        Dimension imgSize = getImageDimension(new ByteArrayInputStream(data.getData()), data.getPictureType());
        Dimension anchorSize = getDimensionFromAnchor(picture);
        double scaledWidth = scaleX == Double.MAX_VALUE ? imgSize.getWidth() : (anchorSize.getWidth() / 9525.0d) * scaleX;
        double scaledHeight = scaleY == Double.MAX_VALUE ? imgSize.getHeight() : (anchorSize.getHeight() / 9525.0d) * scaleY;
        int col23 = anchor.getCol1();
        int dx2 = 0;
        int col24 = col23 + 1;
        double w2 = sheet.getColumnWidthInPixels(col23);
        if (isHSSF) {
            w = w2 * (1.0d - (((double) anchor.getDx1()) / 1024.0d));
        } else {
            w = w2 - (((double) anchor.getDx1()) / 9525.0d);
        }
        while (true) {
            col2 = col24;
            if (w >= scaledWidth) {
                break;
            }
            col24 = col2 + 1;
            w += (double) sheet.getColumnWidthInPixels(col2);
        }
        if (w > scaledWidth) {
            int col25 = col2 - 1;
            double cw = sheet.getColumnWidthInPixels(col25);
            double delta = w - scaledWidth;
            if (isHSSF) {
                col22 = col25;
                dx2 = (int) (((cw - delta) / cw) * 1024.0d);
            } else {
                col22 = col25;
                dx2 = (int) ((cw - delta) * 9525.0d);
            }
            if (dx2 < 0) {
                dx2 = 0;
            }
            col2 = col22;
        }
        anchor.setCol2(col2);
        anchor.setDx2(dx2);
        int row22 = anchor.getRow1();
        int row23 = row22 + 1;
        double h = getRowHeightInPixels(sheet, row22);
        double h2 = isHSSF ? h * (1.0d - (((double) anchor.getDy1()) / 256.0d)) : h - (((double) anchor.getDy1()) / 9525.0d);
        while (true) {
            row2 = row23;
            if (h2 >= scaledHeight) {
                break;
            }
            row23 = row2 + 1;
            h2 += getRowHeightInPixels(sheet, row2);
        }
        if (h2 <= scaledHeight) {
            dy2 = 0;
        } else {
            row2--;
            double ch = getRowHeightInPixels(sheet, row2);
            double delta2 = h2 - scaledHeight;
            int dy22 = isHSSF ? (int) (((ch - delta2) / ch) * 256.0d) : (int) ((ch - delta2) * 9525.0d);
            if (dy22 < 0) {
                dy22 = 0;
            }
            dy2 = dy22;
        }
        anchor.setRow2(row2);
        anchor.setDy2(dy2);
        Dimension dim = new Dimension((int) Math.round(scaledWidth * 9525.0d), (int) Math.round(9525.0d * scaledHeight));
        return dim;
    }

    public static Dimension getDimensionFromAnchor(Picture picture) {
        double w;
        double w2;
        double h;
        double d;
        double h2;
        ClientAnchor anchor = picture.getClientAnchor();
        boolean isHSSF = anchor instanceof HSSFClientAnchor;
        Sheet sheet = picture.getSheet();
        int col2 = anchor.getCol1();
        int col22 = col2 + 1;
        double w3 = sheet.getColumnWidthInPixels(col2);
        if (isHSSF) {
            w = w3 * (1.0d - (((double) anchor.getDx1()) / 1024.0d));
        } else {
            w = w3 - (((double) anchor.getDx1()) / 9525.0d);
        }
        while (col22 < anchor.getCol2()) {
            w += (double) sheet.getColumnWidthInPixels(col22);
            col22++;
        }
        if (isHSSF) {
            w2 = w + (((double) (sheet.getColumnWidthInPixels(col22) * anchor.getDx2())) / 1024.0d);
        } else {
            w2 = w + (((double) anchor.getDx2()) / 9525.0d);
        }
        int row2 = anchor.getRow1();
        int row22 = row2 + 1;
        double h3 = getRowHeightInPixels(sheet, row2);
        if (isHSSF) {
            h = h3 * (1.0d - (((double) anchor.getDy1()) / 256.0d));
        } else {
            h = h3 - (((double) anchor.getDy1()) / 9525.0d);
        }
        while (row22 < anchor.getRow2()) {
            h += getRowHeightInPixels(sheet, row22);
            row22++;
        }
        if (isHSSF) {
            h2 = h + ((getRowHeightInPixels(sheet, row22) * ((double) anchor.getDy2())) / 256.0d);
            d = 9525.0d;
        } else {
            d = 9525.0d;
            h2 = h + (((double) anchor.getDy2()) / 9525.0d);
        }
        return new Dimension((int) Math.rint(w2 * d), (int) Math.rint(h2 * d));
    }

    public static double getRowHeightInPixels(Sheet sheet, int rowNum) {
        Row r = sheet.getRow(rowNum);
        double points = r == null ? sheet.getDefaultRowHeightInPoints() : r.getHeightInPoints();
        return ((double) Units.toEMU(points)) / 9525.0d;
    }
}
