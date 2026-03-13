package org.apache.poi.xslf.usermodel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.sl.usermodel.GroupShape;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Units;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;

/* JADX INFO: loaded from: classes.dex */
public class XSLFGroupShape extends XSLFShape implements XSLFShapeContainer, GroupShape<XSLFShape, XSLFTextParagraph> {
    private static final POILogger _logger = POILogFactory.getLogger((Class<?>) XSLFGroupShape.class);
    private XSLFDrawing _drawing;
    private final CTGroupShapeProperties _grpSpPr;
    private final List<XSLFShape> _shapes;

    protected XSLFGroupShape(CTGroupShape shape, XSLFSheet sheet) {
        super(shape, sheet);
        this._shapes = XSLFSheet.buildShapes(shape, sheet);
        this._grpSpPr = shape.getGrpSpPr();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape
    protected CTGroupShapeProperties getGrpSpPr() {
        return this._grpSpPr;
    }

    protected CTGroupTransform2D getSafeXfrm() {
        CTGroupTransform2D xfrm = getXfrm();
        return xfrm == null ? getGrpSpPr().addNewXfrm() : xfrm;
    }

    protected CTGroupTransform2D getXfrm() {
        return getGrpSpPr().getXfrm();
    }

    @Override // org.apache.poi.sl.usermodel.Shape, org.apache.poi.sl.usermodel.PlaceableShape
    public Rectangle2D getAnchor() {
        CTGroupTransform2D xfrm = getXfrm();
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
        CTGroupTransform2D xfrm = getSafeXfrm();
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

    @Override // org.apache.poi.sl.usermodel.GroupShape
    public Rectangle2D getInteriorAnchor() {
        CTGroupTransform2D xfrm = getXfrm();
        CTPoint2D off = xfrm.getChOff();
        double x = Units.toPoints(off.getX());
        double y = Units.toPoints(off.getY());
        CTPositiveSize2D ext = xfrm.getChExt();
        double cx = Units.toPoints(ext.getCx());
        double cy = Units.toPoints(ext.getCy());
        return new Rectangle2D.Double(x, y, cx, cy);
    }

    @Override // org.apache.poi.sl.usermodel.GroupShape
    public void setInteriorAnchor(Rectangle2D anchor) {
        CTGroupTransform2D xfrm = getSafeXfrm();
        CTPoint2D off = xfrm.isSetChOff() ? xfrm.getChOff() : xfrm.addNewChOff();
        long x = Units.toEMU(anchor.getX());
        long y = Units.toEMU(anchor.getY());
        off.setX(x);
        off.setY(y);
        CTPositiveSize2D ext = xfrm.isSetChExt() ? xfrm.getChExt() : xfrm.addNewChExt();
        long cx = Units.toEMU(anchor.getWidth());
        long cy = Units.toEMU(anchor.getHeight());
        ext.setCx(cx);
        ext.setCy(cy);
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public List<XSLFShape> getShapes() {
        return this._shapes;
    }

    @Override // java.lang.Iterable
    public Iterator<XSLFShape> iterator() {
        return this._shapes.iterator();
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public boolean removeShape(XSLFShape xShape) {
        XmlObject obj = xShape.getXmlObject();
        CTGroupShape grpSp = getXmlObject();
        if (obj instanceof CTShape) {
            grpSp.getSpList().remove(obj);
        } else if (obj instanceof CTGroupShape) {
            grpSp.getGrpSpList().remove(obj);
        } else if (obj instanceof CTConnector) {
            grpSp.getCxnSpList().remove(obj);
        } else if (obj instanceof CTGraphicalObjectFrame) {
            grpSp.getGraphicFrameList().remove(obj);
        } else if (obj instanceof CTPicture) {
            XSLFPictureShape ps = (XSLFPictureShape) xShape;
            XSLFSheet sh = getSheet();
            if (sh != null) {
                sh.removePictureRelation(ps);
            }
            grpSp.getPicList().remove(obj);
        } else {
            throw new IllegalArgumentException("Unsupported shape: " + xShape);
        }
        return this._shapes.remove(xShape);
    }

    static CTGroupShape prototype(int shapeId) {
        CTGroupShape ct = CTGroupShape.Factory.newInstance();
        CTGroupShapeNonVisual nvSpPr = ct.addNewNvGrpSpPr();
        CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Group " + shapeId);
        cnv.setId(shapeId + 1);
        nvSpPr.addNewCNvGrpSpPr();
        nvSpPr.addNewNvPr();
        ct.addNewGrpSpPr();
        return ct;
    }

    private XSLFDrawing getDrawing() {
        if (this._drawing == null) {
            this._drawing = new XSLFDrawing(getSheet(), getXmlObject());
        }
        return this._drawing;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFAutoShape createAutoShape() {
        XSLFAutoShape sh = getDrawing().createAutoShape();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFFreeformShape createFreeform() {
        XSLFFreeformShape sh = getDrawing().createFreeform();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFTextBox createTextBox() {
        XSLFTextBox sh = getDrawing().createTextBox();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFConnectorShape createConnector() {
        XSLFConnectorShape sh = getDrawing().createConnector();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFGroupShape createGroup() {
        XSLFGroupShape sh = getDrawing().createGroup();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFPictureShape createPicture(PictureData pictureData) {
        if (!(pictureData instanceof XSLFPictureData)) {
            throw new IllegalArgumentException("pictureData needs to be of type XSLFPictureData");
        }
        XSLFPictureData xPictureData = (XSLFPictureData) pictureData;
        PackagePart pic = xPictureData.getPackagePart();
        PackageRelationship rel = getSheet().getPackagePart().addRelationship(pic.getPartName(), TargetMode.INTERNAL, XSLFRelation.IMAGES.getRelation());
        XSLFPictureShape sh = getDrawing().createPicture(rel.getId());
        new DrawPictureShape(sh).resize();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    public XSLFTable createTable() {
        XSLFTable sh = getDrawing().createTable();
        this._shapes.add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFTable createTable(int numRows, int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        XSLFTable sh = getDrawing().createTable();
        this._shapes.add(sh);
        sh.setParent(this);
        for (int r = 0; r < numRows; r++) {
            XSLFTableRow row = sh.addRow();
            for (int c = 0; c < numCols; c++) {
                row.addCell();
            }
        }
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipHorizontal(boolean flip) {
        getSafeXfrm().setFlipH(flip);
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setFlipVertical(boolean flip) {
        getSafeXfrm().setFlipV(flip);
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipHorizontal() {
        CTGroupTransform2D xfrm = getXfrm();
        return xfrm != null && xfrm.isSetFlipH() && xfrm.getFlipH();
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public boolean getFlipVertical() {
        CTGroupTransform2D xfrm = getXfrm();
        return xfrm != null && xfrm.isSetFlipV() && xfrm.getFlipV();
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public void setRotation(double theta) {
        getSafeXfrm().setRot((int) (60000.0d * theta));
    }

    @Override // org.apache.poi.sl.usermodel.PlaceableShape
    public double getRotation() {
        CTGroupTransform2D xfrm = getXfrm();
        if (xfrm == null || !xfrm.isSetRot()) {
            return 0.0d;
        }
        return ((double) xfrm.getRot()) / 60000.0d;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShape
    void copy(XSLFShape src) {
        XSLFShape newShape;
        XSLFGroupShape gr = (XSLFGroupShape) src;
        List<XSLFShape> tgtShapes = getShapes();
        List<XSLFShape> srcShapes = gr.getShapes();
        if (tgtShapes.size() == srcShapes.size()) {
            for (int i = 0; i < tgtShapes.size(); i++) {
                XSLFShape s1 = srcShapes.get(i);
                XSLFShape s2 = tgtShapes.get(i);
                s2.copy(s1);
            }
            return;
        }
        clear();
        for (XSLFShape shape : srcShapes) {
            if (shape instanceof XSLFTextBox) {
                newShape = createTextBox();
            } else if (shape instanceof XSLFAutoShape) {
                newShape = createAutoShape();
            } else if (shape instanceof XSLFConnectorShape) {
                newShape = createConnector();
            } else if (shape instanceof XSLFFreeformShape) {
                newShape = createFreeform();
            } else if (shape instanceof XSLFPictureShape) {
                XSLFPictureShape p = (XSLFPictureShape) shape;
                XSLFPictureData pd = p.getPictureData();
                XSLFPictureData pdNew = getSheet().getSlideShow().addPicture(pd.getData(), pd.getType());
                newShape = createPicture((PictureData) pdNew);
            } else if (shape instanceof XSLFGroupShape) {
                newShape = createGroup();
            } else if (shape instanceof XSLFTable) {
                newShape = createTable();
            } else {
                _logger.log(5, "copying of class " + shape.getClass() + " not supported.");
            }
            newShape.copy(shape);
        }
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShapeContainer
    public void clear() {
        List<XSLFShape> shapes = new ArrayList<>(getShapes());
        for (XSLFShape shape : shapes) {
            removeShape(shape);
        }
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public void addShape(XSLFShape shape) {
        throw new UnsupportedOperationException("Adding a shape from a different container is not supported - create it from scratch with XSLFGroupShape.create* methods");
    }
}
