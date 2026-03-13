package org.apache.poi.xslf.usermodel;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawPaint;
import org.apache.poi.sl.usermodel.ColorStyle;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.model.PropertyFetcher;
import org.apache.poi.xslf.usermodel.XSLFPropertiesDelegate;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientStop;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathShadeType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.presentationml.x2006.main.STPlaceholderType;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSLFShape implements Shape<XSLFShape, XSLFTextParagraph> {
    protected static final String PML_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    private CTNonVisualDrawingProps _nvPr;
    private XSLFShapeContainer _parent;
    private CTPlaceholder _ph;
    private final XmlObject _shape;
    private final XSLFSheet _sheet;
    private CTShapeStyle _spStyle;

    protected XSLFShape(XmlObject shape, XSLFSheet sheet) {
        this._shape = shape;
        this._sheet = sheet;
    }

    public final XmlObject getXmlObject() {
        return this._shape;
    }

    @Override // org.apache.poi.sl.usermodel.Shape
    public XSLFSheet getSheet() {
        return this._sheet;
    }

    public String getShapeName() {
        return getCNvPr().getName();
    }

    public int getShapeId() {
        return (int) getCNvPr().getId();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Internal
    void copy(XSLFShape sh) {
        if (!getClass().isInstance(sh)) {
            throw new IllegalArgumentException("Can't copy " + sh.getClass().getSimpleName() + " into " + getClass().getSimpleName());
        }
        if (this instanceof PlaceableShape) {
            PlaceableShape<?, ?> ps = (PlaceableShape) this;
            ps.setAnchor(sh.getAnchor());
        }
    }

    public void setParent(XSLFShapeContainer parent) {
        this._parent = parent;
    }

    @Override // org.apache.poi.sl.usermodel.Shape
    public XSLFShapeContainer getParent() {
        return this._parent;
    }

    protected PaintStyle getFillPaint() {
        final XSLFTheme theme = getSheet().getTheme();
        final boolean hasPlaceholder = getPlaceholder() != null;
        PropertyFetcher<PaintStyle> fetcher = new PropertyFetcher<PaintStyle>() { // from class: org.apache.poi.xslf.usermodel.XSLFShape.1
            @Override // org.apache.poi.xslf.model.PropertyFetcher
            public boolean fetch(XSLFShape shape) {
                XSLFPropertiesDelegate.XSLFFillProperties fp = XSLFPropertiesDelegate.getFillDelegate(shape.getShapeProperties());
                if (fp == null) {
                    return false;
                }
                if (fp.isSetNoFill()) {
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
                if (style != null) {
                    paint = XSLFShape.selectPaint(XSLFPropertiesDelegate.getFillDelegate(style.getFillRef()), null, pp, theme, hasPlaceholder);
                }
                if (paint == null) {
                    return false;
                }
                setValue(paint);
                return true;
            }
        };
        fetchShapeProperty(fetcher);
        return fetcher.getValue();
    }

    protected CTBackgroundProperties getBgPr() {
        return getChild(CTBackgroundProperties.class, PML_NS, "bgPr");
    }

    protected CTStyleMatrixReference getBgRef() {
        return getChild(CTStyleMatrixReference.class, PML_NS, "bgRef");
    }

    protected CTGroupShapeProperties getGrpSpPr() {
        return getChild(CTGroupShapeProperties.class, PML_NS, "grpSpPr");
    }

    protected CTNonVisualDrawingProps getCNvPr() {
        if (this._nvPr == null) {
            this._nvPr = selectProperty(CTNonVisualDrawingProps.class, "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:cNvPr");
        }
        return this._nvPr;
    }

    protected CTShapeStyle getSpStyle() {
        if (this._spStyle == null) {
            this._spStyle = getChild(CTShapeStyle.class, PML_NS, "style");
        }
        return this._spStyle;
    }

    protected <T extends XmlObject> T getChild(Class<T> cls, String str, String str2) {
        XmlCursor xmlCursorNewCursor = getXmlObject().newCursor();
        T t = null;
        if (xmlCursorNewCursor.toChild(str, str2)) {
            t = (T) xmlCursorNewCursor.getObject();
        }
        if (xmlCursorNewCursor.toChild(XSSFRelation.NS_DRAWINGML, str2)) {
            t = (T) xmlCursorNewCursor.getObject();
        }
        xmlCursorNewCursor.dispose();
        return t;
    }

    protected CTPlaceholder getCTPlaceholder() {
        if (this._ph == null) {
            this._ph = selectProperty(CTPlaceholder.class, "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:nvPr/p:ph");
        }
        return this._ph;
    }

    public Placeholder getPlaceholder() {
        CTPlaceholder ph = getCTPlaceholder();
        if (ph == null) {
            return null;
        }
        if (!ph.isSetType() && !ph.isSetIdx()) {
            return null;
        }
        return Placeholder.lookupOoxml(ph.getType().intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPlaceholder(Placeholder placeholder) {
        CTApplicationNonVisualDrawingProps nv = selectProperty(CTApplicationNonVisualDrawingProps.class, "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:nvPr");
        if (nv == null) {
            return;
        }
        if (placeholder == null) {
            if (nv.isSetPh()) {
                nv.unsetPh();
            }
            this._ph = null;
            return;
        }
        nv.addNewPh().setType(STPlaceholderType.Enum.forInt(placeholder.ooxmlId));
    }

    protected <T extends XmlObject> T selectProperty(Class<T> cls, String str) {
        XmlObject[] xmlObjectArrSelectPath = getXmlObject().selectPath(str);
        if (xmlObjectArrSelectPath.length != 0 && cls.isInstance(xmlObjectArrSelectPath[0])) {
            return (T) xmlObjectArrSelectPath[0];
        }
        return null;
    }

    protected boolean fetchShapeProperty(PropertyFetcher<?> visitor) {
        if (visitor.fetch(this)) {
            return true;
        }
        CTPlaceholder ph = getCTPlaceholder();
        if (ph == null) {
            return false;
        }
        MasterSheet<XSLFShape, XSLFTextParagraph> sm = getSheet().getMasterSheet();
        if (sm instanceof XSLFSlideLayout) {
            XSLFSlideLayout slideLayout = (XSLFSlideLayout) sm;
            XSLFSimpleShape placeholderShape = slideLayout.getPlaceholder(ph);
            if (placeholderShape != null && visitor.fetch(placeholderShape)) {
                return true;
            }
            sm = slideLayout.getMasterSheet();
        }
        if (sm instanceof XSLFSlideMaster) {
            XSLFSlideMaster master = (XSLFSlideMaster) sm;
            int textType = getPlaceholderType(ph);
            XSLFSimpleShape masterShape = master.getPlaceholderByType(textType);
            if (masterShape != null && visitor.fetch(masterShape)) {
                return true;
            }
        }
        return false;
    }

    private static int getPlaceholderType(CTPlaceholder ph) {
        if (!ph.isSetType()) {
            return 2;
        }
        int iIntValue = ph.getType().intValue();
        if (iIntValue == 1 || iIntValue == 3) {
            return 1;
        }
        if (iIntValue == 5 || iIntValue == 6 || iIntValue == 7) {
            return ph.getType().intValue();
        }
        return 2;
    }

    protected static PaintStyle selectPaint(XSLFPropertiesDelegate.XSLFFillProperties fp, CTSchemeColor phClr, PackagePart parentPart, XSLFTheme theme, boolean hasPlaceholder) {
        if (fp == null || fp.isSetNoFill()) {
            return null;
        }
        if (fp.isSetSolidFill()) {
            return selectPaint(fp.getSolidFill(), phClr, theme);
        }
        if (fp.isSetBlipFill()) {
            return selectPaint(fp.getBlipFill(), parentPart);
        }
        if (fp.isSetGradFill()) {
            return selectPaint(fp.getGradFill(), phClr, theme);
        }
        if (!fp.isSetMatrixStyle()) {
            return null;
        }
        return selectPaint(fp.getMatrixStyle(), theme, fp.isLineStyle(), hasPlaceholder);
    }

    protected static PaintStyle selectPaint(CTSolidColorFillProperties solidFill, CTSchemeColor phClr, XSLFTheme theme) {
        if (solidFill.isSetSchemeClr() && phClr == null) {
            phClr = solidFill.getSchemeClr();
        }
        XSLFColor c = new XSLFColor(solidFill, theme, phClr);
        return DrawPaint.createSolidPaint(c.getColorStyle());
    }

    protected static PaintStyle selectPaint(CTBlipFillProperties blipFill, final PackagePart parentPart) {
        final CTBlip blip = blipFill.getBlip();
        return new PaintStyle.TexturePaint() { // from class: org.apache.poi.xslf.usermodel.XSLFShape.2
            private PackagePart getPart() {
                try {
                    String blipId = blip.getEmbed();
                    PackageRelationship rel = parentPart.getRelationship(blipId);
                    return parentPart.getRelatedPart(rel);
                } catch (InvalidFormatException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.TexturePaint
            public InputStream getImageData() {
                try {
                    return getPart().getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.TexturePaint
            public String getContentType() {
                return getPart().getContentType();
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.TexturePaint
            public int getAlpha() {
                if (blip.sizeOfAlphaModFixArray() > 0) {
                    return blip.getAlphaModFixArray(0).getAmt();
                }
                return 100000;
            }
        };
    }

    protected static PaintStyle selectPaint(final CTGradientFillProperties gradFill, CTSchemeColor phClr, XSLFTheme theme) {
        CTGradientStop[] gs = gradFill.getGsLst().getGsArray();
        Arrays.sort(gs, new Comparator<CTGradientStop>() { // from class: org.apache.poi.xslf.usermodel.XSLFShape.3
            @Override // java.util.Comparator
            public int compare(CTGradientStop o1, CTGradientStop o2) {
                Integer pos1 = Integer.valueOf(o1.getPos());
                Integer pos2 = Integer.valueOf(o2.getPos());
                return pos1.compareTo(pos2);
            }
        });
        final ColorStyle[] cs = new ColorStyle[gs.length];
        final float[] fractions = new float[gs.length];
        int i = 0;
        for (CTGradientStop cgs : gs) {
            CTSchemeColor phClrCgs = phClr;
            if (phClrCgs == null && cgs.isSetSchemeClr()) {
                phClrCgs = cgs.getSchemeClr();
            }
            cs[i] = new XSLFColor(cgs, theme, phClrCgs).getColorStyle();
            fractions[i] = cgs.getPos() / 100000.0f;
            i++;
        }
        return new PaintStyle.GradientPaint() { // from class: org.apache.poi.xslf.usermodel.XSLFShape.4
            @Override // org.apache.poi.sl.usermodel.PaintStyle.GradientPaint
            public double getGradientAngle() {
                if (gradFill.isSetLin()) {
                    return ((double) gradFill.getLin().getAng()) / 60000.0d;
                }
                return 0.0d;
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.GradientPaint
            public ColorStyle[] getGradientColors() {
                return cs;
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.GradientPaint
            public float[] getGradientFractions() {
                return fractions;
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.GradientPaint
            public boolean isRotatedWithShape() {
                return gradFill.getRotWithShape();
            }

            @Override // org.apache.poi.sl.usermodel.PaintStyle.GradientPaint
            public PaintStyle.GradientPaint.GradientType getGradientType() {
                if (gradFill.isSetLin()) {
                    return PaintStyle.GradientPaint.GradientType.linear;
                }
                if (gradFill.isSetPath()) {
                    STPathShadeType.Enum ps = gradFill.getPath().getPath();
                    if (ps == STPathShadeType.CIRCLE) {
                        return PaintStyle.GradientPaint.GradientType.circular;
                    }
                    if (ps == STPathShadeType.SHAPE) {
                        return PaintStyle.GradientPaint.GradientType.shape;
                    }
                }
                return PaintStyle.GradientPaint.GradientType.linear;
            }
        };
    }

    protected static PaintStyle selectPaint(CTStyleMatrixReference fillRef, XSLFTheme theme, boolean isLineStyle, boolean hasPlaceholder) {
        int childIdx;
        CTLineStyleList bgFillStyleLst;
        if (fillRef == null) {
            return null;
        }
        int idx = (int) fillRef.getIdx();
        CTStyleMatrix matrix = theme.getXmlObject().getThemeElements().getFmtScheme();
        if (idx >= 1 && idx <= 999) {
            childIdx = idx - 1;
            bgFillStyleLst = isLineStyle ? matrix.getLnStyleLst() : matrix.getFillStyleLst();
        } else {
            if (idx < 1001) {
                return null;
            }
            childIdx = idx - 1001;
            bgFillStyleLst = matrix.getBgFillStyleLst();
        }
        XmlCursor cur = bgFillStyleLst.newCursor();
        XSLFPropertiesDelegate.XSLFFillProperties fp = null;
        if (cur.toChild(childIdx)) {
            fp = XSLFPropertiesDelegate.getFillDelegate(cur.getObject());
        }
        cur.dispose();
        CTSchemeColor phClr = fillRef.getSchemeClr();
        PaintStyle res = selectPaint(fp, phClr, theme.getPackagePart(), theme, hasPlaceholder);
        if (res != null || hasPlaceholder) {
            return res;
        }
        XSLFColor col = new XSLFColor(fillRef, theme, phClr);
        return DrawPaint.createSolidPaint(col.getColorStyle());
    }

    @Override // org.apache.poi.sl.usermodel.Shape
    public void draw(Graphics2D graphics, Rectangle2D bounds) {
        DrawFactory.getInstance(graphics).drawShape(graphics, this, bounds);
    }

    protected XmlObject getShapeProperties() {
        return getChild(CTShapeProperties.class, PML_NS, "spPr");
    }
}
