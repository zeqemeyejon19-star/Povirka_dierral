package org.apache.poi.xslf.usermodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.util.Removal;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObjectData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.presentationml.x2006.main.CTApplicationNonVisualDrawingProps;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class XSLFCommonSlideData {
    private final CTCommonSlideData data;

    public XSLFCommonSlideData(CTCommonSlideData data) {
        this.data = data;
    }

    public List<DrawingTextBody> getDrawingText() {
        CTGroupShape gs = this.data.getSpTree();
        List<DrawingTextBody> out = new ArrayList<>();
        processShape(gs, out);
        CTGroupShape[] arr$ = gs.getGrpSpArray();
        for (CTGroupShape shape : arr$) {
            processShape(shape, out);
        }
        CTGraphicalObjectFrame[] arr$2 = gs.getGraphicFrameArray();
        for (CTGraphicalObjectFrame frame : arr$2) {
            CTGraphicalObjectData data = frame.getGraphic().getGraphicData();
            XmlCursor c = data.newCursor();
            c.selectPath("declare namespace pic='" + CTTable.type.getName().getNamespaceURI() + "' .//pic:tbl");
            while (c.toNextSelection()) {
                CTTable object = c.getObject();
                if (object instanceof XmlAnyTypeImpl) {
                    try {
                        object = CTTable.Factory.parse(object.toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    } catch (XmlException e) {
                        throw new POIXMLException((Throwable) e);
                    }
                }
                if (object instanceof CTTable) {
                    DrawingTable table = new DrawingTable(object);
                    DrawingTableRow[] arr$3 = table.getRows();
                    int len$ = arr$3.length;
                    int i$ = 0;
                    while (i$ < len$) {
                        DrawingTableRow row = arr$3[i$];
                        DrawingTableCell[] arr$4 = row.getCells();
                        DrawingTable table2 = table;
                        int len$2 = arr$4.length;
                        int i$2 = 0;
                        while (i$2 < len$2) {
                            DrawingTableCell cell = arr$4[i$2];
                            int len$3 = len$2;
                            DrawingTextBody textBody = cell.getTextBody();
                            out.add(textBody);
                            i$2++;
                            len$2 = len$3;
                        }
                        i$++;
                        table = table2;
                    }
                }
            }
            c.dispose();
        }
        return out;
    }

    public List<DrawingParagraph> getText() {
        List<DrawingParagraph> paragraphs = new ArrayList<>();
        for (DrawingTextBody textBody : getDrawingText()) {
            paragraphs.addAll(Arrays.asList(textBody.getParagraphs()));
        }
        return paragraphs;
    }

    private void processShape(CTGroupShape gs, List<DrawingTextBody> out) {
        DrawingTextBody textBody;
        CTShape[] arr$ = gs.getSpArray();
        for (CTShape shape : arr$) {
            CTTextBody ctTextBody = shape.getTxBody();
            if (ctTextBody != null) {
                CTApplicationNonVisualDrawingProps nvpr = shape.getNvSpPr().getNvPr();
                if (nvpr.isSetPh()) {
                    textBody = new DrawingTextPlaceholder(ctTextBody, nvpr.getPh());
                } else {
                    textBody = new DrawingTextBody(ctTextBody);
                }
                out.add(textBody);
            }
        }
    }
}
