package org.apache.poi.xslf.usermodel;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;

/* JADX INFO: loaded from: classes.dex */
public class XSLFDrawing {
    private int _shapeId;
    private XSLFSheet _sheet;
    private CTGroupShape _spTree;

    XSLFDrawing(XSLFSheet sheet, CTGroupShape spTree) {
        this._shapeId = 1;
        this._sheet = sheet;
        this._spTree = spTree;
        for (CTNonVisualDrawingProps cTNonVisualDrawingProps : sheet.getSpTree().selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:cNvPr")) {
            if (cTNonVisualDrawingProps instanceof CTNonVisualDrawingProps) {
                CTNonVisualDrawingProps p = cTNonVisualDrawingProps;
                this._shapeId = (int) Math.max(this._shapeId, p.getId());
            }
        }
    }

    public XSLFAutoShape createAutoShape() {
        CTShape sp = this._spTree.addNewSp();
        int i = this._shapeId;
        this._shapeId = i + 1;
        sp.set(XSLFAutoShape.prototype(i));
        XSLFAutoShape shape = new XSLFAutoShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }

    public XSLFFreeformShape createFreeform() {
        CTShape sp = this._spTree.addNewSp();
        int i = this._shapeId;
        this._shapeId = i + 1;
        sp.set(XSLFFreeformShape.prototype(i));
        XSLFFreeformShape shape = new XSLFFreeformShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }

    public XSLFTextBox createTextBox() {
        CTShape sp = this._spTree.addNewSp();
        int i = this._shapeId;
        this._shapeId = i + 1;
        sp.set(XSLFTextBox.prototype(i));
        XSLFTextBox shape = new XSLFTextBox(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }

    public XSLFConnectorShape createConnector() {
        CTConnector sp = this._spTree.addNewCxnSp();
        int i = this._shapeId;
        this._shapeId = i + 1;
        sp.set(XSLFConnectorShape.prototype(i));
        XSLFConnectorShape shape = new XSLFConnectorShape(sp, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        shape.setLineColor(Color.black);
        shape.setLineWidth(0.75d);
        return shape;
    }

    public XSLFGroupShape createGroup() {
        CTGroupShape obj = this._spTree.addNewGrpSp();
        int i = this._shapeId;
        this._shapeId = i + 1;
        obj.set(XSLFGroupShape.prototype(i));
        XSLFGroupShape shape = new XSLFGroupShape(obj, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }

    public XSLFPictureShape createPicture(String rel) {
        CTPicture obj = this._spTree.addNewPic();
        int i = this._shapeId;
        this._shapeId = i + 1;
        obj.set(XSLFPictureShape.prototype(i, rel));
        XSLFPictureShape shape = new XSLFPictureShape(obj, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }

    public XSLFTable createTable() {
        CTGraphicalObjectFrame obj = this._spTree.addNewGraphicFrame();
        int i = this._shapeId;
        this._shapeId = i + 1;
        obj.set(XSLFTable.prototype(i));
        XSLFTable shape = new XSLFTable(obj, this._sheet);
        shape.setAnchor(new Rectangle2D.Double());
        return shape;
    }
}
