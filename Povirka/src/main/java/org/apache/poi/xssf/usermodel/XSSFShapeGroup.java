package org.apache.poi.xssf.usermodel;

import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.ShapeContainer;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTConnector;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFShapeGroup extends XSSFShape implements ShapeContainer<XSSFShape> {
    private static CTGroupShape prototype = null;
    private CTGroupShape ctGroup;

    protected XSSFShapeGroup(XSSFDrawing drawing, CTGroupShape ctGroup) {
        this.drawing = drawing;
        this.ctGroup = ctGroup;
    }

    protected static CTGroupShape prototype() {
        if (prototype == null) {
            CTGroupShape shape = CTGroupShape.Factory.newInstance();
            CTGroupShapeNonVisual nv = shape.addNewNvGrpSpPr();
            CTNonVisualDrawingProps nvpr = nv.addNewCNvPr();
            nvpr.setId(0L);
            nvpr.setName("Group 0");
            nv.addNewCNvGrpSpPr();
            CTGroupShapeProperties sp = shape.addNewGrpSpPr();
            CTGroupTransform2D t2d = sp.addNewXfrm();
            CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            CTPositiveSize2D p3 = t2d.addNewChExt();
            p3.setCx(0L);
            p3.setCy(0L);
            CTPoint2D p4 = t2d.addNewChOff();
            p4.setX(0L);
            p4.setY(0L);
            prototype = shape;
        }
        return prototype;
    }

    public XSSFTextBox createTextbox(XSSFChildAnchor anchor) {
        CTShape ctShape = this.ctGroup.addNewSp();
        ctShape.set(XSSFSimpleShape.prototype());
        XSSFTextBox shape = new XSSFTextBox(getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.getCTShape().getSpPr().setXfrm(anchor.getCTTransform2D());
        return shape;
    }

    public XSSFSimpleShape createSimpleShape(XSSFChildAnchor anchor) {
        CTShape ctShape = this.ctGroup.addNewSp();
        ctShape.set(XSSFSimpleShape.prototype());
        XSSFSimpleShape shape = new XSSFSimpleShape(getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.getCTShape().getSpPr().setXfrm(anchor.getCTTransform2D());
        return shape;
    }

    public XSSFConnector createConnector(XSSFChildAnchor anchor) {
        CTConnector ctShape = this.ctGroup.addNewCxnSp();
        ctShape.set(XSSFConnector.prototype());
        XSSFConnector shape = new XSSFConnector(getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.getCTConnector().getSpPr().setXfrm(anchor.getCTTransform2D());
        return shape;
    }

    public XSSFPicture createPicture(XSSFClientAnchor anchor, int pictureIndex) {
        PackageRelationship rel = getDrawing().addPictureReference(pictureIndex);
        CTPicture ctShape = this.ctGroup.addNewPic();
        ctShape.set(XSSFPicture.prototype());
        XSSFPicture shape = new XSSFPicture(getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        shape.setPictureReference(rel);
        return shape;
    }

    public XSSFShapeGroup createGroup(XSSFChildAnchor anchor) {
        CTGroupShape ctShape = this.ctGroup.addNewGrpSp();
        ctShape.set(prototype());
        XSSFShapeGroup shape = new XSSFShapeGroup(getDrawing(), ctShape);
        shape.parent = this;
        shape.anchor = anchor;
        CTGroupTransform2D xfrm = shape.getCTGroupShape().getGrpSpPr().getXfrm();
        CTTransform2D t2 = anchor.getCTTransform2D();
        xfrm.setOff(t2.getOff());
        xfrm.setExt(t2.getExt());
        xfrm.setChExt(t2.getExt());
        xfrm.setFlipH(t2.getFlipH());
        xfrm.setFlipV(t2.getFlipV());
        return shape;
    }

    @Internal
    public CTGroupShape getCTGroupShape() {
        return this.ctGroup;
    }

    public void setCoordinates(int x1, int y1, int x2, int y2) {
        CTGroupTransform2D t2d = this.ctGroup.getGrpSpPr().getXfrm();
        CTPoint2D off = t2d.getOff();
        off.setX(x1);
        off.setY(y1);
        CTPositiveSize2D ext = t2d.getExt();
        ext.setCx(x2);
        ext.setCy(y2);
        CTPoint2D chOff = t2d.getChOff();
        chOff.setX(x1);
        chOff.setY(y1);
        CTPositiveSize2D chExt = t2d.getChExt();
        chExt.setCx(x2);
        chExt.setCy(y2);
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFShape
    protected CTShapeProperties getShapeProperties() {
        throw new IllegalStateException("Not supported for shape group");
    }

    @Override // java.lang.Iterable
    public Iterator<XSSFShape> iterator() {
        return getDrawing().getShapes(this).iterator();
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public String getShapeName() {
        return this.ctGroup.getNvGrpSpPr().getCNvPr().getName();
    }
}
