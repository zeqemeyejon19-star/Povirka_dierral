package org.apache.poi.xslf.usermodel;

import java.awt.geom.Rectangle2D;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.sl.usermodel.GraphicalFrame;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;

/* JADX INFO: loaded from: classes.dex */
public class XSLFGraphicFrame extends XSLFShape implements GraphicalFrame<XSLFShape, XSLFTextParagraph> {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XSLFGraphicFrame.class);

    XSLFGraphicFrame(CTGraphicalObjectFrame shape, XSLFSheet sheet) {
        super(shape, sheet);
    }

    public ShapeType getShapeType() {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.poi.sl.usermodel.Shape, org.apache.poi.sl.usermodel.PlaceableShape
    public Rectangle2D getAnchor() {
        CTTransform2D xfrm = getXmlObject().getXfrm();
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
        CTTransform2D xfrm = getXmlObject().getXfrm();
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

    static XSLFGraphicFrame create(CTGraphicalObjectFrame shape, XSLFSheet sheet) {
        String uri = shape.getGraphic().getGraphicData().getUri();
        if ("http://schemas.openxmlformats.org/drawingml/2006/table".equals(uri)) {
            return new XSLFTable(shape, sheet);
        }
        return new XSLFGraphicFrame(shape, sheet);
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setRotation(double theta) {
        throw new IllegalArgumentException("Operation not supported");
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public double getRotation() {
        return 0.0d;
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipHorizontal(boolean flip) {
        throw new IllegalArgumentException("Operation not supported");
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipVertical(boolean flip) {
        throw new IllegalArgumentException("Operation not supported");
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipHorizontal() {
        return false;
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipVertical() {
        return false;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape
    void copy(XSLFShape sh) {
        super.copy(sh);
        CTGraphicalObjectData data = getXmlObject().getGraphic().getGraphicData();
        String uri = data.getUri();
        if (uri.equals("http://schemas.openxmlformats.org/drawingml/2006/diagram")) {
            copyDiagram(data, (XSLFGraphicFrame) sh);
        }
    }

    private void copyDiagram(CTGraphicalObjectData objData, XSLFGraphicFrame srcShape) {
        XmlObject[] obj = objData.selectPath("declare namespace dgm='http://schemas.openxmlformats.org/drawingml/2006/diagram' $this//dgm:relIds");
        if (obj != null && obj.length == 1) {
            XmlCursor c = obj[0].newCursor();
            XSLFSheet sheet = srcShape.getSheet();
            try {
                String dm = c.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "dm"));
                PackageRelationship dmRel = sheet.getPackagePart().getRelationship(dm);
                PackagePart dmPart = sheet.getPackagePart().getRelatedPart(dmRel);
                getSheet().importPart(dmRel, dmPart);
                String lo = c.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "lo"));
                PackageRelationship loRel = sheet.getPackagePart().getRelationship(lo);
                PackagePart loPart = sheet.getPackagePart().getRelatedPart(loRel);
                getSheet().importPart(loRel, loPart);
                String qs = c.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "qs"));
                PackageRelationship qsRel = sheet.getPackagePart().getRelationship(qs);
                PackagePart qsPart = sheet.getPackagePart().getRelatedPart(qsRel);
                getSheet().importPart(qsRel, qsPart);
                try {
                    String cs = c.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "cs"));
                    PackageRelationship csRel = sheet.getPackagePart().getRelationship(cs);
                    PackagePart csPart = sheet.getPackagePart().getRelatedPart(csRel);
                    getSheet().importPart(csRel, csPart);
                    c.dispose();
                } catch (InvalidFormatException e) {
                    e = e;
                    throw new POIXMLException(e);
                }
            } catch (InvalidFormatException e2) {
                e = e2;
            }
        }
    }

    @Override // org.apache.poi.sl.usermodel.GraphicalFrame
    public XSLFPictureShape getFallbackPicture() {
        XmlObject xo = selectProperty(XmlObject.class, "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main'; declare namespace mc='http://schemas.openxmlformats.org/markup-compatibility/2006' .//mc:Fallback/*/p:pic");
        if (xo == null) {
            return null;
        }
        try {
            CTGroupShape gs = CTGroupShape.Factory.parse(xo.newDomNode());
            if (gs.sizeOfPicArray() == 0) {
                return null;
            }
            return new XSLFPictureShape(gs.getPicArray(0), getSheet());
        } catch (XmlException e) {
            LOG.log(5, "Can't parse fallback picture stream of graphical frame", e);
            return null;
        }
    }
}
