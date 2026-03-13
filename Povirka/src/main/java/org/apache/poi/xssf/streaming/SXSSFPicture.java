package org.apache.poi.xssf.streaming;

import java.awt.Dimension;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Shape;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

/* JADX INFO: loaded from: classes.dex */
public final class SXSSFPicture implements Picture {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final XSSFPicture _picture;
    private final SXSSFWorkbook _wb;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SXSSFPicture.class);
    private static float DEFAULT_COLUMN_WIDTH = 9.140625f;

    SXSSFPicture(SXSSFWorkbook _wb, XSSFPicture _picture) {
        this._wb = _wb;
        this._picture = _picture;
    }

    @Internal
    public CTPicture getCTPicture() {
        return this._picture.getCTPicture();
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize() {
        resize(1.0d);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize(double scale) {
        XSSFClientAnchor anchor = getClientAnchor();
        XSSFClientAnchor pref = getPreferredSize(scale);
        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());
        anchor.setCol2(col2);
        anchor.setDx1(0);
        anchor.setDx2(pref.getDx2());
        anchor.setRow2(row2);
        anchor.setDy1(0);
        anchor.setDy2(pref.getDy2());
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public XSSFClientAnchor getPreferredSize() {
        return getPreferredSize(1.0d);
    }

    public XSSFClientAnchor getPreferredSize(double scale) {
        XSSFClientAnchor anchor = getClientAnchor();
        XSSFPictureData data = getPictureData();
        Dimension size = getImageDimension(data.getPackagePart(), data.getPictureType());
        double scaledWidth = size.getWidth() * scale;
        double scaledHeight = size.getHeight() * scale;
        float w = 0.0f;
        int col2 = anchor.getCol1() - 1;
        while (w <= scaledWidth) {
            col2++;
            w += getColumnWidthInPixels(col2);
        }
        if (w <= scaledWidth) {
            throw new AssertionError();
        }
        double cw = getColumnWidthInPixels(col2);
        double deltaW = ((double) w) - scaledWidth;
        int dx2 = (int) ((cw - deltaW) * 9525.0d);
        anchor.setCol2(col2);
        anchor.setDx2(dx2);
        double h = 0.0d;
        int row2 = anchor.getRow1() - 1;
        while (h <= scaledHeight) {
            row2++;
            h += (double) getRowHeightInPixels(row2);
            data = data;
            size = size;
        }
        if (h <= scaledHeight) {
            throw new AssertionError();
        }
        double ch = getRowHeightInPixels(row2);
        double deltaH = h - scaledHeight;
        int dy2 = (int) ((ch - deltaH) * 9525.0d);
        anchor.setRow2(row2);
        anchor.setDy2(dy2);
        CTPositiveSize2D size2d = getCTPicture().getSpPr().getXfrm().getExt();
        size2d.setCx((long) (scaledWidth * 9525.0d));
        size2d.setCy((long) (scaledHeight * 9525.0d));
        return anchor;
    }

    private float getColumnWidthInPixels(int columnIndex) {
        XSSFSheet sheet = getSheet();
        CTCol col = sheet.getColumnHelper().getColumn(columnIndex, false);
        double numChars = (col == null || !col.isSetWidth()) ? DEFAULT_COLUMN_WIDTH : col.getWidth();
        return ((float) numChars) * 7.0017f;
    }

    private float getRowHeightInPixels(int rowIndex) {
        XSSFSheet xssfSheet = getSheet();
        SXSSFSheet sheet = this._wb.getSXSSFSheet(xssfSheet);
        Row row = sheet.getRow(rowIndex);
        float height = row != null ? row.getHeightInPoints() : sheet.getDefaultRowHeightInPoints();
        return (96.0f * height) / 72.0f;
    }

    protected static Dimension getImageDimension(PackagePart part, int type) {
        try {
            return ImageUtils.getImageDimension(part.getInputStream(), type);
        } catch (IOException e) {
            logger.log(5, e);
            return new Dimension();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public XSSFPictureData getPictureData() {
        return this._picture.getPictureData();
    }

    protected CTShapeProperties getShapeProperties() {
        return getCTPicture().getSpPr();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public XSSFAnchor getAnchor() {
        return this._picture.getAnchor();
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public void resize(double scaleX, double scaleY) {
        this._picture.resize(scaleX, scaleY);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public XSSFClientAnchor getPreferredSize(double scaleX, double scaleY) {
        return this._picture.getPreferredSize(scaleX, scaleY);
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public Dimension getImageDimension() {
        return this._picture.getImageDimension();
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public XSSFClientAnchor getClientAnchor() {
        XSSFAnchor a = getAnchor();
        if (a instanceof XSSFClientAnchor) {
            return (XSSFClientAnchor) a;
        }
        return null;
    }

    public XSSFDrawing getDrawing() {
        return this._picture.getDrawing();
    }

    @Override // org.apache.poi.ss.usermodel.Picture
    public XSSFSheet getSheet() {
        return this._picture.getSheet();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public String getShapeName() {
        return this._picture.getShapeName();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public Shape getParent() {
        return this._picture.getParent();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public boolean isNoFill() {
        return this._picture.isNoFill();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setNoFill(boolean noFill) {
        this._picture.setNoFill(noFill);
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setFillColor(int red, int green, int blue) {
        this._picture.setFillColor(red, green, blue);
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public void setLineStyleColor(int red, int green, int blue) {
        this._picture.setLineStyleColor(red, green, blue);
    }
}
