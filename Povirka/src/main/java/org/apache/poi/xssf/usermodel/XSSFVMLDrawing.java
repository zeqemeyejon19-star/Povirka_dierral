package org.apache.poi.xssf.usermodel;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.excel.STObjectType;
import com.microsoft.schemas.office.office.CTIdMap;
import com.microsoft.schemas.office.office.CTShapeLayout;
import com.microsoft.schemas.office.office.STConnectType;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTShapetype;
import com.microsoft.schemas.vml.STExt;
import com.microsoft.schemas.vml.STStrokeJoinStyle;
import com.microsoft.schemas.vml.STTrueFalse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.ReplacingInputStream;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFVMLDrawing extends POIXMLDocumentPart {
    private static final String COMMENT_SHAPE_TYPE_ID = "_x0000_t202";
    private List<XmlObject> _items;
    private List<QName> _qnames;
    private int _shapeId;
    private String _shapeTypeId;
    private static final QName QNAME_SHAPE_LAYOUT = new QName("urn:schemas-microsoft-com:office:office", "shapelayout");
    private static final QName QNAME_SHAPE_TYPE = new QName("urn:schemas-microsoft-com:vml", "shapetype");
    private static final QName QNAME_SHAPE = new QName("urn:schemas-microsoft-com:vml", "shape");
    private static final Pattern ptrn_shapeId = Pattern.compile("_x0000_s(\\d+)");

    protected XSSFVMLDrawing() {
        this._qnames = new ArrayList();
        this._items = new ArrayList();
        this._shapeId = 1024;
        newDrawing();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    protected XSSFVMLDrawing(PackagePart part) throws XmlException, IOException {
        super(part);
        this._qnames = new ArrayList();
        this._items = new ArrayList();
        this._shapeId = 1024;
        read(getPackagePart().getInputStream());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    protected void read(InputStream is) throws XmlException, IOException {
        try {
            Document doc = DocumentHelper.readDocument(new ReplacingInputStream(is, "<br>", "<br/>"));
            XmlObject root = XmlObject.Factory.parse(doc, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this._qnames = new ArrayList();
            this._items = new ArrayList();
            XmlObject[] arr$ = root.selectPath("$this/xml/*");
            for (XmlObject obj : arr$) {
                Node nd = obj.getDomNode();
                QName qname = new QName(nd.getNamespaceURI(), nd.getLocalName());
                if (qname.equals(QNAME_SHAPE_LAYOUT)) {
                    this._items.add(CTShapeLayout.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
                } else if (qname.equals(QNAME_SHAPE_TYPE)) {
                    XmlObject xmlObject = CTShapetype.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    this._items.add(xmlObject);
                    this._shapeTypeId = xmlObject.getId();
                } else if (qname.equals(QNAME_SHAPE)) {
                    XmlObject xmlObject2 = CTShape.Factory.parse(obj.xmlText(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    String id = xmlObject2.getId();
                    if (id != null) {
                        Matcher m = ptrn_shapeId.matcher(id);
                        if (m.find()) {
                            this._shapeId = Math.max(this._shapeId, Integer.parseInt(m.group(1)));
                        }
                    }
                    this._items.add(xmlObject2);
                } else {
                    try {
                        InputSource is2 = new InputSource(new StringReader(obj.xmlText()));
                        Document doc2 = DocumentHelper.readDocument(is2);
                        this._items.add(XmlObject.Factory.parse(doc2, POIXMLTypeLoader.DEFAULT_XML_OPTIONS));
                    } catch (SAXException e) {
                        throw new XmlException(e.getMessage(), e);
                    }
                }
                this._qnames.add(qname);
            }
        } catch (SAXException e2) {
            throw new XmlException(e2.getMessage(), e2);
        }
    }

    protected List<XmlObject> getItems() {
        return this._items;
    }

    protected void write(OutputStream out) throws IOException {
        XmlObject rootObject = XmlObject.Factory.newInstance();
        XmlCursor rootCursor = rootObject.newCursor();
        rootCursor.toNextToken();
        rootCursor.beginElement(ContentTypes.EXTENSION_XML);
        for (int i = 0; i < this._items.size(); i++) {
            XmlCursor xc = this._items.get(i).newCursor();
            rootCursor.beginElement(this._qnames.get(i));
            while (xc.toNextToken() == XmlCursor.TokenType.ATTR) {
                Node anode = xc.getDomNode();
                rootCursor.insertAttributeWithValue(anode.getLocalName(), anode.getNamespaceURI(), anode.getNodeValue());
            }
            xc.toStartDoc();
            xc.copyXmlContents(rootCursor);
            rootCursor.toNextToken();
            xc.dispose();
        }
        rootCursor.dispose();
        rootObject.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        write(out);
        out.close();
    }

    private void newDrawing() {
        XmlObject xmlObjectNewInstance = CTShapeLayout.Factory.newInstance();
        xmlObjectNewInstance.setExt(STExt.EDIT);
        CTIdMap idmap = xmlObjectNewInstance.addNewIdmap();
        idmap.setExt(STExt.EDIT);
        idmap.setData("1");
        this._items.add(xmlObjectNewInstance);
        this._qnames.add(QNAME_SHAPE_LAYOUT);
        XmlObject xmlObjectNewInstance2 = CTShapetype.Factory.newInstance();
        this._shapeTypeId = COMMENT_SHAPE_TYPE_ID;
        xmlObjectNewInstance2.setId(COMMENT_SHAPE_TYPE_ID);
        xmlObjectNewInstance2.setCoordsize("21600,21600");
        xmlObjectNewInstance2.setSpt(202.0f);
        xmlObjectNewInstance2.setPath2("m,l,21600r21600,l21600,xe");
        xmlObjectNewInstance2.addNewStroke().setJoinstyle(STStrokeJoinStyle.MITER);
        CTPath path = xmlObjectNewInstance2.addNewPath();
        path.setGradientshapeok(STTrueFalse.T);
        path.setConnecttype(STConnectType.RECT);
        this._items.add(xmlObjectNewInstance2);
        this._qnames.add(QNAME_SHAPE_TYPE);
    }

    protected CTShape newCommentShape() {
        XmlObject xmlObjectNewInstance = CTShape.Factory.newInstance();
        StringBuilder sbAppend = new StringBuilder().append("_x0000_s");
        int i = this._shapeId + 1;
        this._shapeId = i;
        xmlObjectNewInstance.setId(sbAppend.append(i).toString());
        xmlObjectNewInstance.setType("#" + this._shapeTypeId);
        xmlObjectNewInstance.setStyle("position:absolute; visibility:hidden");
        xmlObjectNewInstance.setFillcolor("#ffffe1");
        xmlObjectNewInstance.setInsetmode(STInsetMode.AUTO);
        xmlObjectNewInstance.addNewFill().setColor("#ffffe1");
        CTShadow shadow = xmlObjectNewInstance.addNewShadow();
        shadow.setOn(STTrueFalse.T);
        shadow.setColor("black");
        shadow.setObscured(STTrueFalse.T);
        xmlObjectNewInstance.addNewPath().setConnecttype(STConnectType.NONE);
        xmlObjectNewInstance.addNewTextbox().setStyle("mso-direction-alt:auto");
        CTClientData cldata = xmlObjectNewInstance.addNewClientData();
        cldata.setObjectType(STObjectType.NOTE);
        cldata.addNewMoveWithCells();
        cldata.addNewSizeWithCells();
        cldata.addNewAnchor().setStringValue("1, 15, 0, 2, 3, 15, 3, 16");
        cldata.addNewAutoFill().setStringValue("False");
        cldata.addNewRow().setBigIntegerValue(new BigInteger("0"));
        cldata.addNewColumn().setBigIntegerValue(new BigInteger("0"));
        this._items.add(xmlObjectNewInstance);
        this._qnames.add(QNAME_SHAPE);
        return xmlObjectNewInstance;
    }

    protected CTShape findCommentShape(int row, int col) {
        Iterator<XmlObject> it = this._items.iterator();
        while (it.hasNext()) {
            CTShape cTShape = (XmlObject) it.next();
            if (cTShape instanceof CTShape) {
                CTShape sh = cTShape;
                if (sh.sizeOfClientDataArray() > 0) {
                    CTClientData cldata = sh.getClientDataArray(0);
                    if (cldata.getObjectType() == STObjectType.NOTE) {
                        int crow = cldata.getRowArray(0).intValue();
                        int ccol = cldata.getColumnArray(0).intValue();
                        if (crow == row && ccol == col) {
                            return sh;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    protected boolean removeCommentShape(int row, int col) {
        CTShape shape = findCommentShape(row, col);
        return shape != null && this._items.remove(shape);
    }
}
