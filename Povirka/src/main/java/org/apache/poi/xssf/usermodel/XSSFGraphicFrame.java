package org.apache.poi.xssf.usermodel;

import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTGraphicalObjectFrameNonVisual;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFGraphicFrame extends XSSFShape {
    private static CTGraphicalObjectFrame prototype = null;
    private CTGraphicalObjectFrame graphicFrame;

    protected XSSFGraphicFrame(XSSFDrawing drawing, CTGraphicalObjectFrame ctGraphicFrame) {
        this.drawing = drawing;
        this.graphicFrame = ctGraphicFrame;
        CTGraphicalObjectData graphicData = ctGraphicFrame.getGraphic().getGraphicData();
        if (graphicData != null) {
            NodeList nodes = graphicData.getDomNode().getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeName().equals("c:chart")) {
                    POIXMLDocumentPart relation = drawing.getRelationById(node.getAttributes().getNamedItem("r:id").getNodeValue());
                    if (relation instanceof XSSFChart) {
                        ((XSSFChart) relation).setGraphicFrame(this);
                    }
                }
            }
        }
    }

    @Internal
    public CTGraphicalObjectFrame getCTGraphicalObjectFrame() {
        return this.graphicFrame;
    }

    protected static CTGraphicalObjectFrame prototype() {
        if (prototype == null) {
            CTGraphicalObjectFrame graphicFrame = CTGraphicalObjectFrame.Factory.newInstance();
            CTGraphicalObjectFrameNonVisual nvGraphic = graphicFrame.addNewNvGraphicFramePr();
            CTNonVisualDrawingProps props = nvGraphic.addNewCNvPr();
            props.setId(0L);
            props.setName("Diagramm 1");
            nvGraphic.addNewCNvGraphicFramePr();
            CTTransform2D transform = graphicFrame.addNewXfrm();
            CTPositiveSize2D extPoint = transform.addNewExt();
            CTPoint2D offPoint = transform.addNewOff();
            extPoint.setCx(0L);
            extPoint.setCy(0L);
            offPoint.setX(0L);
            offPoint.setY(0L);
            graphicFrame.addNewGraphic();
            prototype = graphicFrame;
        }
        return prototype;
    }

    public void setMacro(String macro) {
        this.graphicFrame.setMacro(macro);
    }

    public void setName(String name) {
        getNonVisualProperties().setName(name);
    }

    public String getName() {
        return getNonVisualProperties().getName();
    }

    private CTNonVisualDrawingProps getNonVisualProperties() {
        CTGraphicalObjectFrameNonVisual nvGraphic = this.graphicFrame.getNvGraphicFramePr();
        return nvGraphic.getCNvPr();
    }

    protected void setAnchor(XSSFClientAnchor anchor) {
        this.anchor = anchor;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFShape, org.apache.poi.ss.usermodel.Shape
    public XSSFClientAnchor getAnchor() {
        return (XSSFClientAnchor) this.anchor;
    }

    protected void setChart(XSSFChart chart, String relId) {
        CTGraphicalObjectData data = this.graphicFrame.getGraphic().addNewGraphicData();
        appendChartElement(data, relId);
        chart.setGraphicFrame(this);
    }

    public long getId() {
        return this.graphicFrame.getNvGraphicFramePr().getCNvPr().getId();
    }

    protected void setId(long id) {
        this.graphicFrame.getNvGraphicFramePr().getCNvPr().setId(id);
    }

    private void appendChartElement(CTGraphicalObjectData data, String id) {
        String r_namespaceUri = STRelationshipId.type.getName().getNamespaceURI();
        XmlCursor cursor = data.newCursor();
        cursor.toNextToken();
        cursor.beginElement(new QName(XSSFRelation.NS_CHART, "chart", "c"));
        cursor.insertAttributeWithValue(new QName(r_namespaceUri, "id", "r"), id);
        cursor.dispose();
        data.setUri(XSSFRelation.NS_CHART);
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFShape
    protected CTShapeProperties getShapeProperties() {
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Shape
    public String getShapeName() {
        return this.graphicFrame.getNvGraphicFramePr().getCNvPr().getName();
    }
}
