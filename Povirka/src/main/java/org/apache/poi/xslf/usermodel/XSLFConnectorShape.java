package org.apache.poi.xslf.usermodel;

import org.apache.poi.POIXMLException;
import org.apache.poi.sl.usermodel.ConnectorShape;
import org.apache.poi.sl.usermodel.Placeholder;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnectorNonVisual;

/* JADX INFO: loaded from: classes.dex */
public class XSLFConnectorShape extends XSLFSimpleShape implements ConnectorShape<XSLFShape, XSLFTextParagraph> {
    XSLFConnectorShape(CTConnector shape, XSLFSheet sheet) {
        super(shape, sheet);
    }

    static CTConnector prototype(int shapeId) {
        CTConnector ct = CTConnector.Factory.newInstance();
        CTConnectorNonVisual nvSpPr = ct.addNewNvCxnSpPr();
        CTNonVisualDrawingProps cnv = nvSpPr.addNewCNvPr();
        cnv.setName("Connector " + shapeId);
        cnv.setId(shapeId + 1);
        nvSpPr.addNewCNvCxnSpPr();
        nvSpPr.addNewNvPr();
        CTShapeProperties spPr = ct.addNewSpPr();
        CTPresetGeometry2D prst = spPr.addNewPrstGeom();
        prst.setPrst(STShapeType.LINE);
        prst.addNewAvLst();
        spPr.addNewLn();
        return ct;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.sl.usermodel.SimpleShape
    public XSLFShadow getShadow() {
        return null;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSimpleShape, org.apache.poi.xslf.usermodel.XSLFShape, org.apache.poi.sl.usermodel.SimpleShape
    public void setPlaceholder(Placeholder placeholder) {
        throw new POIXMLException("A connector shape can't be a placeholder.");
    }
}
