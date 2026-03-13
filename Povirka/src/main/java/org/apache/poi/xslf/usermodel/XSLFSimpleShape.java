package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.Guide;
import org.apache.poi.sl.draw.geom.PresetGeometries;
import org.apache.poi.sl.usermodel.FillStyle;
import org.apache.poi.sl.usermodel.LineDecoration;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFPropertiesDelegate;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBaseStyles;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleItem;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOuterShadowEffect;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetLineDashProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STCompoundLine;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineCap;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndLength;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndType;
import org.openxmlformats.schemas.drawingml.x2006.main.STLineEndWidth;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetLineDashVal;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSLFSimpleShape extends XSLFShape implements SimpleShape<XSLFShape, XSLFTextParagraph> {
    private static CTOuterShadowEffect NO_SHADOW = CTOuterShadowEffect.Factory.newInstance();
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XSLFSimpleShape.class);

    XSLFSimpleShape(XmlObject shape, XSLFSheet sheet) {
        super(shape, sheet);
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public void setShapeType(ShapeType type) {
        XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(getShapeProperties());
        if (gp == null) {
            return;
        }
        if (gp.isSetCustGeom()) {
            gp.unsetCustGeom();
        }
        CTPresetGeometry2D prst = gp.isSetPrstGeom() ? gp.getPrstGeom() : gp.addNewPrstGeom();
        prst.setPrst(STShapeType.Enum.forInt(type.ooxmlId));
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public ShapeType getShapeType() {
        STShapeType.Enum geom;
        XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(getShapeProperties());
        if (gp != null && gp.isSetPrstGeom() && (geom = gp.getPrstGeom().getPrst()) != null) {
            return ShapeType.forId(geom.intValue(), true);
        }
        return null;
    }

    protected CTTransform2D getXfrm(boolean create) {
        PropertyFetcher<CTTransform2D> fetcher = new PropertyFetcher<CTTransform2D>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.1
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                CTShapeProperties shapeProperties = shape.getShapeProperties();
                if ((shapeProperties instanceof CTShapeProperties) && shapeProperties.isSetXfrm()) {
                    setValue(shapeProperties.getXfrm());
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        CTTransform2D xfrm = fetcher.getValue();
        if (!create || xfrm != null) {
            return xfrm;
        }
        CTShapeProperties shapeProperties = getShapeProperties();
        if (shapeProperties instanceof CTShapeProperties) {
            return shapeProperties.addNewXfrm();
        }
        LOG.log(5, getClass() + " doesn't have xfrm element.");
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.Shape, org.apache.poi.sl.usermodel.PlaceableShape
    public Rectangle2D getAnchor() {
        CTTransform2D xfrm = getXfrm(false);
        if (xfrm == null) {
            return null;
        }
        CTPoint2D off = xfrm.getOff();
        double x = Units.toPoints(off.getX());
        double y = Units.toPoints(off.getY());
        CTPositiveSize2D ext = xfrm.getExt();
        double cx = Units.toPoints(ext.getCx());
        double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setAnchor(Rectangle2D anchor) {
        CTTransform2D xfrm = getXfrm(true);
        if (xfrm == null) {
            return;
        }
        CTPoint2D off = xfrm.isSetOff() ? xfrm.getOff() : xfrm.addNewOff();
        long x = Units.toEMU(anchor.getX());
        long y = Units.toEMU(anchor.getY());
        off.setX(x);
        off.setY(y);
        CTPositiveSize2D ext = xfrm.isSetExt() ? xfrm.getExt() : xfrm.addNewExt();
        long cx = Units.toEMU(anchor.getWidth());
        long cy = Units.toEMU(anchor.getHeight());
        ext.setCx(cx);
        ext.setCy(cy);
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setRotation(double theta) {
        CTTransform2D xfrm = getXfrm(true);
        if (xfrm != null) {
            xfrm.setRot((int) (60000.0d * theta));
        }
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public double getRotation() {
        CTTransform2D xfrm = getXfrm(false);
        if (xfrm == null || !xfrm.isSetRot()) {
            return 0.0d;
        }
        return ((double) xfrm.getRot()) / 60000.0d;
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipHorizontal(boolean flip) {
        CTTransform2D xfrm = getXfrm(true);
        if (xfrm != null) {
            xfrm.setFlipH(flip);
        }
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipVertical(boolean flip) {
        CTTransform2D xfrm = getXfrm(true);
        if (xfrm != null) {
            xfrm.setFlipV(flip);
        }
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipHorizontal() {
        CTTransform2D xfrm = getXfrm(false);
        if (xfrm == null || !xfrm.isSetFlipH()) {
            return false;
        }
        return xfrm.getFlipH();
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipVertical() {
        CTTransform2D xfrm = getXfrm(false);
        if (xfrm == null || !xfrm.isSetFlipV()) {
            return false;
        }
        return xfrm.getFlipV();
    }

    CTLineProperties getDefaultLineProperties() {
        CTStyleMatrixReference lnRef;
        CTBaseStyles styles;
        CTStyleMatrix styleMatrix;
        CTLineStyleList lineStyles;
        CTShapeStyle style = getSpStyle();
        if (style == null || (lnRef = style.getLnRef()) == null) {
            return null;
        }
        int idx = (int) lnRef.getIdx();
        XSLFTheme theme = getSheet().getTheme();
        if (theme == null || (styles = theme.getXmlObject().getThemeElements()) == null || (styleMatrix = styles.getFmtScheme()) == null || (lineStyles = styleMatrix.getLnStyleLst()) == null || lineStyles.sizeOfLnArray() < idx) {
            return null;
        }
        return lineStyles.getLnArray(idx - 1);
    }

    public void setLineColor(Color color) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (ln.isSetSolidFill()) {
            ln.unsetSolidFill();
        }
        if (ln.isSetGradFill()) {
            ln.unsetGradFill();
        }
        if (ln.isSetPattFill()) {
            ln.unsetPattFill();
        }
        if (ln.isSetNoFill()) {
            ln.unsetNoFill();
        }
        if (color == null) {
            ln.addNewNoFill();
            return;
        }
        CTSolidColorFillProperties fill = ln.addNewSolidFill();
        XSLFColor col = new XSLFColor(fill, getSheet().getTheme(), fill.getSchemeClr());
        col.setColor(color);
    }

    public Color getLineColor() {
        PaintStyle ps = getLinePaint();
        if (ps instanceof PaintStyle.SolidPaint) {
            return ((PaintStyle.SolidPaint) ps).getSolidColor().getColor();
        }
        return null;
    }

    protected PaintStyle getLinePaint() {
        XSLFSheet sheet = getSheet();
        final XSLFTheme theme = sheet.getTheme();
        final boolean hasPlaceholder = getPlaceholder() != null;
        PropertyFetcher<PaintStyle> fetcher = new PropertyFetcher<PaintStyle>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.2
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                CTLineProperties spPr = XSLFSimpleShape.getLn(shape, false);
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(spPr);
                if (fp != null && fp.isSetNoFill()) {
                    setValue(null);
                    return true;
                }
                PackagePart pp = shape.getSheet().getPackagePart();
                PaintStyle paint = XSLFShape.selectPaint(fp, null, pp, theme, hasPlaceholder);
                if (paint != null) {
                    setValue(paint);
                    return true;
                }
                CTShapeStyle style = shape.getSpStyle();
                if (style != null && (paint = XSLFShape.selectPaint(XSLFPropertiesDelegate.getFillDelegate(style.getLnRef()), null, pp, theme, hasPlaceholder)) == null) {
                    paint = getThemePaint(style, pp);
                }
                if (paint == null) {
                    return false;
                }
                setValue(paint);
                return true;
            }

            PaintStyle getThemePaint(CTShapeStyle style, PackagePart pp) {
                CTStyleMatrixReference lnRef = style.getLnRef();
                if (lnRef == null) {
                    return null;
                }
                int idx = (int) lnRef.getIdx();
                CTSchemeColor phClr = lnRef.getSchemeClr();
                if (idx <= 0) {
                    return null;
                }
                CTLineProperties props = theme.getXmlObject().getThemeElements().getFmtScheme().getLnStyleLst().getLnArray(idx - 1);
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(props);
                return XSLFShape.selectPaint(fp, phClr, pp, theme, hasPlaceholder);
            }
        };
        fetchShapeProperty(fetcher);
        return fetcher.getValue();
    }

    public void setLineWidth(double width) {
        CTLineProperties lnPr = getLn(this, true);
        if (lnPr == null) {
            return;
        }
        if (width == 0.0d) {
            if (lnPr.isSetW()) {
                lnPr.unsetW();
            }
            if (!lnPr.isSetNoFill()) {
                lnPr.addNewNoFill();
            }
            if (lnPr.isSetSolidFill()) {
                lnPr.unsetSolidFill();
            }
            if (lnPr.isSetGradFill()) {
                lnPr.unsetGradFill();
            }
            if (lnPr.isSetPattFill()) {
                lnPr.unsetPattFill();
                return;
            }
            return;
        }
        if (lnPr.isSetNoFill()) {
            lnPr.unsetNoFill();
        }
        lnPr.setW(Units.toEMU(width));
    }

    public double getLineWidth() {
        PropertyFetcher<Double> fetcher = new PropertyFetcher<Double>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.3
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                CTLineProperties ln = XSLFSimpleShape.getLn(shape, false);
                if (ln != null) {
                    if (ln.isSetNoFill()) {
                        setValue(Double.valueOf(0.0d));
                        return true;
                    }
                    if (ln.isSetW()) {
                        setValue(Double.valueOf(Units.toPoints(ln.getW())));
                        return true;
                    }
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        if (fetcher.getValue() == null) {
            CTLineProperties defaultLn = getDefaultLineProperties();
            if (defaultLn == null || !defaultLn.isSetW()) {
                return 0.0d;
            }
            double lineWidth = Units.toPoints(defaultLn.getW());
            return lineWidth;
        }
        double lineWidth2 = fetcher.getValue().doubleValue();
        return lineWidth2;
    }

    public void setLineCompound(StrokeStyle.LineCompound compound) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (compound == null) {
            if (ln.isSetCmpd()) {
                ln.unsetCmpd();
            }
        } else {
            int i = AnonymousClass11.$SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound[compound.ordinal()];
            STCompoundLine.Enum xCmpd = i != 2 ? i != 3 ? i != 4 ? i != 5 ? STCompoundLine.SNG : STCompoundLine.TRI : STCompoundLine.THIN_THICK : STCompoundLine.THICK_THIN : STCompoundLine.DBL;
            ln.setCmpd(xCmpd);
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xslf.usermodel.XSLFSimpleShape$11, reason: invalid class name */
    static /* synthetic */ class AnonymousClass11 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound;

        static {
            int[] iArr = new int[StrokeStyle.LineCompound.values().length];
            $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound = iArr;
            try {
                iArr[StrokeStyle.LineCompound.SINGLE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound[StrokeStyle.LineCompound.DOUBLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound[StrokeStyle.LineCompound.THICK_THIN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound[StrokeStyle.LineCompound.THIN_THICK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$sl$usermodel$StrokeStyle$LineCompound[StrokeStyle.LineCompound.TRIPLE.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public StrokeStyle.LineCompound getLineCompound() {
        CTLineProperties defaultLn;
        PropertyFetcher<Integer> fetcher = new PropertyFetcher<Integer>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.4
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                STCompoundLine.Enum stCmpd;
                CTLineProperties ln = XSLFSimpleShape.getLn(shape, false);
                if (ln == null || (stCmpd = ln.getCmpd()) == null) {
                    return false;
                }
                setValue(Integer.valueOf(stCmpd.intValue()));
                return true;
            }
        };
        fetchShapeProperty(fetcher);
        Integer cmpd = fetcher.getValue();
        if (cmpd == null && (defaultLn = getDefaultLineProperties()) != null && defaultLn.isSetCmpd()) {
            int iIntValue = defaultLn.getCmpd().intValue();
            if (iIntValue == 2) {
                return StrokeStyle.LineCompound.DOUBLE;
            }
            if (iIntValue == 3) {
                return StrokeStyle.LineCompound.THICK_THIN;
            }
            if (iIntValue == 4) {
                return StrokeStyle.LineCompound.THIN_THICK;
            }
            if (iIntValue != 5) {
                return StrokeStyle.LineCompound.SINGLE;
            }
            return StrokeStyle.LineCompound.TRIPLE;
        }
        return null;
    }

    public void setLineDash(StrokeStyle.LineDash dash) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (dash == null) {
            if (ln.isSetPrstDash()) {
                ln.unsetPrstDash();
            }
        } else {
            CTPresetLineDashProperties ldp = ln.isSetPrstDash() ? ln.getPrstDash() : ln.addNewPrstDash();
            ldp.setVal(STPresetLineDashVal.Enum.forInt(dash.ooxmlId));
        }
    }

    public StrokeStyle.LineDash getLineDash() {
        CTLineProperties defaultLn;
        PropertyFetcher<StrokeStyle.LineDash> fetcher = new PropertyFetcher<StrokeStyle.LineDash>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.5
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                CTLineProperties ln = XSLFSimpleShape.getLn(shape, false);
                if (ln == null || !ln.isSetPrstDash()) {
                    return false;
                }
                setValue(StrokeStyle.LineDash.fromOoxmlId(ln.getPrstDash().getVal().intValue()));
                return true;
            }
        };
        fetchShapeProperty(fetcher);
        StrokeStyle.LineDash dash = fetcher.getValue();
        if (dash == null && (defaultLn = getDefaultLineProperties()) != null && defaultLn.isSetPrstDash()) {
            return StrokeStyle.LineDash.fromOoxmlId(defaultLn.getPrstDash().getVal().intValue());
        }
        return dash;
    }

    public void setLineCap(StrokeStyle.LineCap cap) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        if (cap == null) {
            if (ln.isSetCap()) {
                ln.unsetCap();
                return;
            }
            return;
        }
        ln.setCap(STLineCap.Enum.forInt(cap.ooxmlId));
    }

    public StrokeStyle.LineCap getLineCap() {
        CTLineProperties defaultLn;
        PropertyFetcher<StrokeStyle.LineCap> fetcher = new PropertyFetcher<StrokeStyle.LineCap>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.6
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                CTLineProperties ln = XSLFSimpleShape.getLn(shape, false);
                if (ln == null || !ln.isSetCap()) {
                    return false;
                }
                setValue(StrokeStyle.LineCap.fromOoxmlId(ln.getCap().intValue()));
                return true;
            }
        };
        fetchShapeProperty(fetcher);
        StrokeStyle.LineCap cap = fetcher.getValue();
        if (cap == null && (defaultLn = getDefaultLineProperties()) != null && defaultLn.isSetCap()) {
            return StrokeStyle.LineCap.fromOoxmlId(defaultLn.getCap().intValue());
        }
        return cap;
    }

    public void setFillColor(Color color) {
        XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(getShapeProperties());
        if (fp == null) {
            return;
        }
        if (color == null) {
            if (fp.isSetSolidFill()) {
                fp.unsetSolidFill();
            }
            if (fp.isSetGradFill()) {
                fp.unsetGradFill();
            }
            if (fp.isSetPattFill()) {
                fp.unsetGradFill();
            }
            if (fp.isSetBlipFill()) {
                fp.unsetBlipFill();
            }
            if (!fp.isSetNoFill()) {
                fp.addNewNoFill();
                return;
            }
            return;
        }
        if (fp.isSetNoFill()) {
            fp.unsetNoFill();
        }
        CTSolidColorFillProperties fill = fp.isSetSolidFill() ? fp.getSolidFill() : fp.addNewSolidFill();
        XSLFColor col = new XSLFColor(fill, getSheet().getTheme(), fill.getSchemeClr());
        col.setColor(color);
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public Color getFillColor() {
        PaintStyle ps = getFillPaint();
        if (ps instanceof PaintStyle.SolidPaint) {
            return DrawPaint.applyColorTransform(((PaintStyle.SolidPaint) ps).getSolidColor());
        }
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public XSLFShadow getShadow() {
        CTShapeStyle style;
        int idx;
        PropertyFetcher<CTOuterShadowEffect> fetcher = new PropertyFetcher<CTOuterShadowEffect>() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.7
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                XSLFPropertiesDelegate.XSLFEffectProperties ep = XSLFPropertiesDelegate.getEffectDelegate(shape.getShapeProperties());
                if (ep != null && ep.isSetEffectLst()) {
                    CTOuterShadowEffect obj = ep.getEffectLst().getOuterShdw();
                    setValue(obj == null ? XSLFSimpleShape.NO_SHADOW : obj);
                    return true;
                }
                return false;
            }
        };
        fetchShapeProperty(fetcher);
        CTOuterShadowEffect obj = fetcher.getValue();
        if (obj == null && (style = getSpStyle()) != null && style.getEffectRef() != null && (idx = (int) style.getEffectRef().getIdx()) != 0) {
            CTStyleMatrix styleMatrix = getSheet().getTheme().getXmlObject().getThemeElements().getFmtScheme();
            CTEffectStyleItem ef = styleMatrix.getEffectStyleLst().getEffectStyleArray(idx - 1);
            obj = ef.getEffectLst().getOuterShdw();
        }
        if (obj == null || obj == NO_SHADOW) {
            return null;
        }
        return new XSLFShadow(obj, this);
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public CustomGeometry getGeometry() {
        XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(getShapeProperties());
        if (gp == null) {
            return null;
        }
        PresetGeometries dict = PresetGeometries.getInstance();
        if (gp.isSetPrstGeom()) {
            String name = gp.getPrstGeom().getPrst().toString();
            CustomGeometry geom = (CustomGeometry) dict.get(name);
            if (geom == null) {
                throw new IllegalStateException("Unknown shape geometry: " + name + ", available geometries are: " + dict.keySet());
            }
            return geom;
        }
        if (gp.isSetCustGeom()) {
            XMLStreamReader staxReader = gp.getCustGeom().newXMLStreamReader();
            CustomGeometry geom2 = PresetGeometries.convertCustomGeometry(staxReader);
            try {
                staxReader.close();
                return geom2;
            } catch (XMLStreamException e) {
                LOG.log(5, "An error occurred while closing a Custom Geometry XML Stream Reader: " + e.getMessage());
                return geom2;
            }
        }
        return (CustomGeometry) dict.get("rect");
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape
    void copy(XSLFShape sh) {
        super.copy(sh);
        XSLFSimpleShape s = (XSLFSimpleShape) sh;
        Color srsSolidFill = s.getFillColor();
        Color tgtSoliFill = getFillColor();
        if (srsSolidFill != null && !srsSolidFill.equals(tgtSoliFill)) {
            setFillColor(srsSolidFill);
        }
        XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(getShapeProperties());
        if (fp != null && fp.isSetBlipFill()) {
            CTBlip blip = fp.getBlipFill().getBlip();
            String blipId = blip.getEmbed();
            String relId = getSheet().importBlip(blipId, s.getSheet().getPackagePart());
            blip.setEmbed(relId);
        }
        Color srcLineColor = s.getLineColor();
        Color tgtLineColor = getLineColor();
        if (srcLineColor != null && !srcLineColor.equals(tgtLineColor)) {
            setLineColor(srcLineColor);
        }
        double srcLineWidth = s.getLineWidth();
        double tgtLineWidth = getLineWidth();
        if (srcLineWidth != tgtLineWidth) {
            setLineWidth(srcLineWidth);
        }
        StrokeStyle.LineDash srcLineDash = s.getLineDash();
        StrokeStyle.LineDash tgtLineDash = getLineDash();
        if (srcLineDash != null && srcLineDash != tgtLineDash) {
            setLineDash(srcLineDash);
        }
        StrokeStyle.LineCap srcLineCap = s.getLineCap();
        StrokeStyle.LineCap tgtLineCap = getLineCap();
        if (srcLineCap != null && srcLineCap != tgtLineCap) {
            setLineCap(srcLineCap);
        }
    }

    public void setLineHeadDecoration(LineDecoration.DecorationShape style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetType()) {
                lnEnd.unsetType();
                return;
            }
            return;
        }
        lnEnd.setType(STLineEndType.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationShape getLineHeadDecoration() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationShape ds = LineDecoration.DecorationShape.NONE;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetType()) {
            return LineDecoration.DecorationShape.fromOoxmlId(ln.getHeadEnd().getType().intValue());
        }
        return ds;
    }

    public void setLineHeadWidth(LineDecoration.DecorationSize style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetW()) {
                lnEnd.unsetW();
                return;
            }
            return;
        }
        lnEnd.setW(STLineEndWidth.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationSize getLineHeadWidth() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetW()) {
            return LineDecoration.DecorationSize.fromOoxmlId(ln.getHeadEnd().getW().intValue());
        }
        return ds;
    }

    public void setLineHeadLength(LineDecoration.DecorationSize style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetHeadEnd() ? ln.getHeadEnd() : ln.addNewHeadEnd();
        if (style == null) {
            if (lnEnd.isSetLen()) {
                lnEnd.unsetLen();
                return;
            }
            return;
        }
        lnEnd.setLen(STLineEndLength.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationSize getLineHeadLength() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetHeadEnd() && ln.getHeadEnd().isSetLen()) {
            return LineDecoration.DecorationSize.fromOoxmlId(ln.getHeadEnd().getLen().intValue());
        }
        return ds;
    }

    public void setLineTailDecoration(LineDecoration.DecorationShape style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetType()) {
                lnEnd.unsetType();
                return;
            }
            return;
        }
        lnEnd.setType(STLineEndType.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationShape getLineTailDecoration() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationShape ds = LineDecoration.DecorationShape.NONE;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetType()) {
            return LineDecoration.DecorationShape.fromOoxmlId(ln.getTailEnd().getType().intValue());
        }
        return ds;
    }

    public void setLineTailWidth(LineDecoration.DecorationSize style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetW()) {
                lnEnd.unsetW();
                return;
            }
            return;
        }
        lnEnd.setW(STLineEndWidth.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationSize getLineTailWidth() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetW()) {
            return LineDecoration.DecorationSize.fromOoxmlId(ln.getTailEnd().getW().intValue());
        }
        return ds;
    }

    public void setLineTailLength(LineDecoration.DecorationSize style) {
        CTLineProperties ln = getLn(this, true);
        if (ln == null) {
            return;
        }
        CTLineEndProperties lnEnd = ln.isSetTailEnd() ? ln.getTailEnd() : ln.addNewTailEnd();
        if (style == null) {
            if (lnEnd.isSetLen()) {
                lnEnd.unsetLen();
                return;
            }
            return;
        }
        lnEnd.setLen(STLineEndLength.Enum.forInt(style.ooxmlId));
    }

    public LineDecoration.DecorationSize getLineTailLength() {
        CTLineProperties ln = getLn(this, false);
        LineDecoration.DecorationSize ds = LineDecoration.DecorationSize.MEDIUM;
        if (ln != null && ln.isSetTailEnd() && ln.getTailEnd().isSetLen()) {
            return LineDecoration.DecorationSize.fromOoxmlId(ln.getTailEnd().getLen().intValue());
        }
        return ds;
    }

    public boolean isPlaceholder() {
        CTPlaceholder ph = getCTPlaceholder();
        return ph != null;
    }

    @Override // org.apache.poi.sl.draw.geom.IAdjustableShape
    public Guide getAdjustValue(String name) {
        XSLFPropertiesDelegate.XSLFGeometryProperties gp = XSLFPropertiesDelegate.getGeometryDelegate(getShapeProperties());
        if (gp != null && gp.isSetPrstGeom() && gp.getPrstGeom().isSetAvLst()) {
            CTGeomGuide[] arr$ = gp.getPrstGeom().getAvLst().getGdArray();
            for (CTGeomGuide g : arr$) {
                if (g.getName().equals(name)) {
                    return new Guide(g.getName(), g.getFmla());
                }
            }
            return null;
        }
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public LineDecoration getLineDecoration() {
        return new LineDecoration() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.8
            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationShape getHeadShape() {
                return XSLFSimpleShape.this.getLineHeadDecoration();
            }

            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationSize getHeadWidth() {
                return XSLFSimpleShape.this.getLineHeadWidth();
            }

            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationSize getHeadLength() {
                return XSLFSimpleShape.this.getLineHeadLength();
            }

            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationShape getTailShape() {
                return XSLFSimpleShape.this.getLineTailDecoration();
            }

            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationSize getTailWidth() {
                return XSLFSimpleShape.this.getLineTailWidth();
            }

            @Override // org.apache.poi.sl.usermodel.LineDecoration
            public LineDecoration.DecorationSize getTailLength() {
                return XSLFSimpleShape.this.getLineTailLength();
            }
        };
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public FillStyle getFillStyle() {
        return new FillStyle() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.9
            @Override // org.apache.poi.sl.usermodel.FillStyle
            public PaintStyle getPaint() {
                return XSLFSimpleShape.this.getFillPaint();
            }
        };
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public StrokeStyle getStrokeStyle() {
        return new StrokeStyle() { // from class: org.apache.poi.xslf.usermodel.XSLFSimpleShape.10
            @Override // org.apache.poi.sl.usermodel.StrokeStyle
            public PaintStyle getPaint() {
                return XSLFSimpleShape.this.getLinePaint();
            }

            @Override // org.apache.poi.sl.usermodel.StrokeStyle
            public StrokeStyle.LineCap getLineCap() {
                return XSLFSimpleShape.this.getLineCap();
            }

            @Override // org.apache.poi.sl.usermodel.StrokeStyle
            public StrokeStyle.LineDash getLineDash() {
                return XSLFSimpleShape.this.getLineDash();
            }

            @Override // org.apache.poi.sl.usermodel.StrokeStyle
            public double getLineWidth() {
                return XSLFSimpleShape.this.getLineWidth();
            }

            @Override // org.apache.poi.sl.usermodel.StrokeStyle
            public StrokeStyle.LineCompound getLineCompound() {
                return XSLFSimpleShape.this.getLineCompound();
            }
        };
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public void setStrokeStyle(Object... styles) {
        if (styles.length == 0) {
            setLineColor(null);
            return;
        }
        for (Object st : styles) {
            if (st instanceof Number) {
                setLineWidth(((Number) st).doubleValue());
            } else if (st instanceof StrokeStyle.LineCap) {
                setLineCap((StrokeStyle.LineCap) st);
            } else if (st instanceof StrokeStyle.LineDash) {
                setLineDash((StrokeStyle.LineDash) st);
            } else if (st instanceof StrokeStyle.LineCompound) {
                setLineCompound((StrokeStyle.LineCompound) st);
            } else if (st instanceof Color) {
                setLineColor((Color) st);
            }
        }
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape, org.apache.poi.sl.usermodel.SimpleShape
    public void setPlaceholder(Placeholder placeholder) {
        super.setPlaceholder(placeholder);
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public XSLFHyperlink getHyperlink() {
        CTNonVisualDrawingProps cNvPr = getCNvPr();
        if (!cNvPr.isSetHlinkClick()) {
            return null;
        }
        return new XSLFHyperlink(cNvPr.getHlinkClick(), getSheet());
    }

    @Override // org.apache.poi.sl.usermodel.SimpleShape
    public XSLFHyperlink createHyperlink() {
        XSLFHyperlink hl = getHyperlink();
        if (hl == null) {
            CTNonVisualDrawingProps cNvPr = getCNvPr();
            return new XSLFHyperlink(cNvPr.addNewHlinkClick(), getSheet());
        }
        return hl;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static CTLineProperties getLn(XSLFShape shape, boolean create) {
        CTShapeProperties shapeProperties = shape.getShapeProperties();
        if (!(shapeProperties instanceof CTShapeProperties)) {
            LOG.log(5, shape.getClass() + " doesn't have line properties");
            return null;
        }
        CTShapeProperties spr = shapeProperties;
        return (spr.isSetLn() || !create) ? spr.getLn() : spr.addNewLn();
    }
}
