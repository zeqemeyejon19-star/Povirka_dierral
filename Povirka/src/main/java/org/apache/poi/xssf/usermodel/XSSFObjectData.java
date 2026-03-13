package org.apache.poi.xssf.usermodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.ObjectData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtension;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShapeNonVisual;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;

/* JADX INFO: loaded from: classes.dex */
public class XSSFObjectData extends XSSFSimpleShape implements ObjectData {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XSSFObjectData.class);
    private static CTShape prototype = null;
    private CTOleObject oleObject;

    protected XSSFObjectData(XSSFDrawing drawing, CTShape ctShape) {
        super(drawing, ctShape);
    }

    protected static CTShape prototype() {
        if (prototype == null) {
            CTShape shape = CTShape.Factory.newInstance();
            CTShapeNonVisual nv = shape.addNewNvSpPr();
            CTNonVisualDrawingProps nvp = nv.addNewCNvPr();
            nvp.setId(1L);
            nvp.setName("Shape 1");
            CTOfficeArtExtensionList extLst = nvp.addNewExtLst();
            CTOfficeArtExtension ext = extLst.addNewExt();
            ext.setUri("{63B3BB69-23CF-44E3-9099-C40C66FF867C}");
            XmlCursor cur = ext.newCursor();
            cur.toEndToken();
            cur.beginElement(new QName("http://schemas.microsoft.com/office/drawing/2010/main", "compatExt", "a14"));
            cur.insertNamespace("a14", "http://schemas.microsoft.com/office/drawing/2010/main");
            cur.insertAttributeWithValue("spid", "_x0000_s1");
            cur.dispose();
            nv.addNewCNvSpPr();
            CTShapeProperties sp = shape.addNewSpPr();
            CTTransform2D t2d = sp.addNewXfrm();
            CTPositiveSize2D p1 = t2d.addNewExt();
            p1.setCx(0L);
            p1.setCy(0L);
            CTPoint2D p2 = t2d.addNewOff();
            p2.setX(0L);
            p2.setY(0L);
            CTPresetGeometry2D geom = sp.addNewPrstGeom();
            geom.setPrst(STShapeType.RECT);
            geom.addNewAvLst();
            prototype = shape;
        }
        return prototype;
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public String getOLE2ClassName() {
        return getOleObject().getProgId();
    }

    public CTOleObject getOleObject() {
        if (this.oleObject == null) {
            long shapeId = getCTShape().getNvSpPr().getCNvPr().getId();
            CTOleObject oleObject = getSheet().readOleObject(shapeId);
            this.oleObject = oleObject;
            if (oleObject == null) {
                throw new POIXMLException("Ole object not found in sheet container - it's probably a control element");
            }
        }
        return this.oleObject;
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public byte[] getObjectData() throws IOException {
        InputStream is = getObjectPart().getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(is, bos);
        is.close();
        return bos.toByteArray();
    }

    public PackagePart getObjectPart() {
        if (!getOleObject().isSetId()) {
            throw new POIXMLException("Invalid ole object found in sheet container");
        }
        POIXMLDocumentPart pdp = getSheet().getRelationById(getOleObject().getId());
        if (pdp == null) {
            return null;
        }
        return pdp.getPackagePart();
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public boolean hasDirectoryEntry() {
        InputStream is = null;
        try {
            is = FileMagic.prepareToCheckMagic(getObjectPart().getInputStream());
            return FileMagic.valueOf(is) == FileMagic.OLE2;
        } catch (IOException e) {
            LOG.log(5, "can't determine if directory entry exists", e);
            return false;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public DirectoryEntry getDirectory() throws IOException {
        InputStream is = null;
        try {
            is = getObjectPart().getInputStream();
            return new POIFSFileSystem(is).getRoot();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public String getFileName() {
        return getObjectPart().getPartName().getName();
    }

    protected XSSFSheet getSheet() {
        return (XSSFSheet) getDrawing().getParent();
    }

    @Override // org.apache.poi.ss.usermodel.ObjectData
    public XSSFPictureData getPictureData() {
        XmlCursor cur = getOleObject().newCursor();
        try {
            if (cur.toChild(XSSFRelation.NS_SPREADSHEETML, "objectPr")) {
                String blipId = cur.getAttributeText(new QName(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376_NS, "id"));
                return (XSSFPictureData) getSheet().getRelationById(blipId);
            }
            return null;
        } finally {
            cur.dispose();
        }
    }
}
