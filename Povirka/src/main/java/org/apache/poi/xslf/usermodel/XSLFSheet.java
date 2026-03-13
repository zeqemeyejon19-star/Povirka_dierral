package org.apache.poi.xslf.usermodel;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageNamespaces;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.DrawPictureShape;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTConnector;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGraphicalObjectFrame;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSLFSheet extends POIXMLDocumentPart implements XSLFShapeContainer, Sheet<XSLFShape, XSLFTextParagraph> {
    private static POILogger LOG = POILogFactory.getLogger((Class<?>) XSLFSheet.class);
    private XSLFCommonSlideData _commonSlideData;
    private XSLFDrawing _drawing;
    private Map<Integer, XSLFSimpleShape> _placeholderByIdMap;
    private Map<Integer, XSLFSimpleShape> _placeholderByTypeMap;
    private List<XSLFTextShape> _placeholders;
    private List<XSLFShape> _shapes;
    private CTGroupShape _spTree;

    protected abstract String getRootElementName();

    public abstract XmlObject getXmlObject();

    public XSLFSheet() {
    }

    public XSLFSheet(PackagePart part) {
        super(part);
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public XMLSlideShow getSlideShow() {
        for (POIXMLDocumentPart p = getParent(); p != null; p = p.getParent()) {
            if (p instanceof XMLSlideShow) {
                return (XMLSlideShow) p;
            }
        }
        throw new IllegalStateException("SlideShow was not found");
    }

    protected static List<XSLFShape> buildShapes(CTGroupShape spTree, XSLFSheet sheet) {
        List<XSLFShape> shapes = new ArrayList<>();
        XmlCursor cur = spTree.newCursor();
        try {
            for (boolean b = cur.toFirstChild(); b; b = cur.toNextSibling()) {
                CTShape object = cur.getObject();
                if (object instanceof CTShape) {
                    XSLFAutoShape shape = XSLFAutoShape.create(object, sheet);
                    shapes.add(shape);
                } else if (object instanceof CTGroupShape) {
                    shapes.add(new XSLFGroupShape((CTGroupShape) object, sheet));
                } else if (object instanceof CTConnector) {
                    shapes.add(new XSLFConnectorShape((CTConnector) object, sheet));
                } else if (object instanceof CTPicture) {
                    shapes.add(new XSLFPictureShape((CTPicture) object, sheet));
                } else if (object instanceof CTGraphicalObjectFrame) {
                    XSLFGraphicFrame shape2 = XSLFGraphicFrame.create((CTGraphicalObjectFrame) object, sheet);
                    shapes.add(shape2);
                } else if (object instanceof XmlAnyTypeImpl) {
                    cur.push();
                    if (cur.toChild(PackageNamespaces.MARKUP_COMPATIBILITY, "Choice") && cur.toFirstChild()) {
                        try {
                            CTGroupShape grp = CTGroupShape.Factory.parse(cur.newXMLStreamReader());
                            shapes.addAll(buildShapes(grp, sheet));
                        } catch (XmlException e) {
                            LOG.log(1, "unparsable alternate content", e);
                        }
                    }
                    cur.pop();
                }
            }
            return shapes;
        } finally {
            cur.dispose();
        }
    }

    @Removal(version = "3.18")
    @Internal
    public XSLFCommonSlideData getCommonSlideData() {
        return this._commonSlideData;
    }

    @Removal(version = "3.18")
    protected void setCommonSlideData(CTCommonSlideData data) {
        if (data == null) {
            this._commonSlideData = null;
        } else {
            this._commonSlideData = new XSLFCommonSlideData(data);
        }
    }

    private XSLFDrawing getDrawing() {
        initDrawingAndShapes();
        return this._drawing;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public List<XSLFShape> getShapes() {
        initDrawingAndShapes();
        return this._shapes;
    }

    private void initDrawingAndShapes() {
        CTGroupShape cgs = getSpTree();
        if (this._drawing == null) {
            this._drawing = new XSLFDrawing(this, cgs);
        }
        if (this._shapes == null) {
            this._shapes = buildShapes(cgs, this);
        }
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFAutoShape createAutoShape() {
        XSLFAutoShape sh = getDrawing().createAutoShape();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFFreeformShape createFreeform() {
        XSLFFreeformShape sh = getDrawing().createFreeform();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFTextBox createTextBox() {
        XSLFTextBox sh = getDrawing().createTextBox();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFConnectorShape createConnector() {
        XSLFConnectorShape sh = getDrawing().createConnector();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFGroupShape createGroup() {
        XSLFGroupShape sh = getDrawing().createGroup();
        getShapes().add(sh);
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
        POIXMLDocumentPart.RelationPart rp = addRelation(null, XSLFRelation.IMAGES, new XSLFPictureData(pic));
        XSLFPictureShape sh = getDrawing().createPicture(rp.getRelationship().getId());
        new DrawPictureShape(sh).resize();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    public XSLFTable createTable() {
        XSLFTable sh = getDrawing().createTable();
        getShapes().add(sh);
        sh.setParent(this);
        return sh;
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public XSLFTable createTable(int numRows, int numCols) {
        if (numRows < 1 || numCols < 1) {
            throw new IllegalArgumentException("numRows and numCols must be greater than 0");
        }
        XSLFTable sh = getDrawing().createTable();
        getShapes().add(sh);
        sh.setParent(this);
        for (int r = 0; r < numRows; r++) {
            XSLFTableRow row = sh.addRow();
            for (int c = 0; c < numCols; c++) {
                row.addCell();
            }
        }
        return sh;
    }

    @Override // java.lang.Iterable
    public Iterator<XSLFShape> iterator() {
        return getShapes().iterator();
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public void addShape(XSLFShape shape) {
        throw new UnsupportedOperationException("Adding a shape from a different container is not supported - create it from scratch witht XSLFSheet.create* methods");
    }

    @Override // org.apache.poi.sl.usermodel.ShapeContainer
    public boolean removeShape(XSLFShape xShape) {
        XmlObject obj = xShape.getXmlObject();
        CTGroupShape spTree = getSpTree();
        if (obj instanceof CTShape) {
            spTree.getSpList().remove(obj);
        } else if (obj instanceof CTGroupShape) {
            spTree.getGrpSpList().remove(obj);
        } else if (obj instanceof CTConnector) {
            spTree.getCxnSpList().remove(obj);
        } else if (obj instanceof CTGraphicalObjectFrame) {
            spTree.getGraphicFrameList().remove(obj);
        } else if (obj instanceof CTPicture) {
            XSLFPictureShape ps = (XSLFPictureShape) xShape;
            removePictureRelation(ps);
            spTree.getPicList().remove(obj);
        } else {
            throw new IllegalArgumentException("Unsupported shape: " + xShape);
        }
        return getShapes().remove(xShape);
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFShapeContainer
    public void clear() {
        List<XSLFShape> shapes = new ArrayList<>(getShapes());
        for (XSLFShape shape : shapes) {
            removeShape(shape);
        }
    }

    protected CTGroupShape getSpTree() {
        if (this._spTree == null) {
            XmlObject root = getXmlObject();
            CTGroupShape[] cTGroupShapeArrSelectPath = root.selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:spTree");
            if (cTGroupShapeArrSelectPath.length == 0) {
                throw new IllegalStateException("CTGroupShape was not found");
            }
            this._spTree = cTGroupShapeArrSelectPath[0];
        }
        return this._spTree;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected final void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        String docName = getRootElementName();
        if (docName != null) {
            xmlOptions.setSaveSyntheticDocumentElement(new QName("http://schemas.openxmlformats.org/presentationml/2006/main", docName));
        }
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        getXmlObject().save(out, xmlOptions);
        out.close();
    }

    public XSLFSheet importContent(XSLFSheet src) {
        this._shapes = null;
        this._spTree = null;
        this._drawing = null;
        this._spTree = null;
        this._placeholders = null;
        getSpTree().set(src.getSpTree());
        List<XSLFShape> tgtShapes = getShapes();
        List<XSLFShape> srcShapes = src.getShapes();
        for (int i = 0; i < tgtShapes.size(); i++) {
            XSLFShape s1 = srcShapes.get(i);
            XSLFShape s2 = tgtShapes.get(i);
            s2.copy(s1);
        }
        return this;
    }

    public XSLFSheet appendContent(XSLFSheet src) {
        CTGroupShape spTree = getSpTree();
        int numShapes = getShapes().size();
        CTGroupShape srcTree = src.getSpTree();
        XmlObject[] arr$ = srcTree.selectPath("*");
        for (XmlObject ch : arr$) {
            if (ch instanceof CTShape) {
                spTree.addNewSp().set(ch);
            } else if (ch instanceof CTGroupShape) {
                spTree.addNewGrpSp().set(ch);
            } else if (ch instanceof CTConnector) {
                spTree.addNewCxnSp().set(ch);
            } else if (ch instanceof CTPicture) {
                spTree.addNewPic().set(ch);
            } else if (ch instanceof CTGraphicalObjectFrame) {
                spTree.addNewGraphicFrame().set(ch);
            }
        }
        this._shapes = null;
        this._spTree = null;
        this._drawing = null;
        this._spTree = null;
        this._placeholders = null;
        List<XSLFShape> tgtShapes = getShapes();
        List<XSLFShape> srcShapes = src.getShapes();
        for (int i = 0; i < srcShapes.size(); i++) {
            XSLFShape s1 = srcShapes.get(i);
            XSLFShape s2 = tgtShapes.get(numShapes + i);
            s2.copy(s1);
        }
        return this;
    }

    XSLFTheme getTheme() {
        return null;
    }

    protected XSLFTextShape getTextShapeByType(Placeholder type) {
        for (XSLFShape shape : getShapes()) {
            if (shape instanceof XSLFTextShape) {
                XSLFTextShape txt = (XSLFTextShape) shape;
                if (txt.getTextType() == type) {
                    return txt;
                }
            }
        }
        return null;
    }

    XSLFSimpleShape getPlaceholder(CTPlaceholder ph) {
        XSLFSimpleShape shape = null;
        if (ph.isSetIdx()) {
            shape = getPlaceholderById((int) ph.getIdx());
        }
        if (shape == null && ph.isSetType()) {
            XSLFSimpleShape shape2 = getPlaceholderByType(ph.getType().intValue());
            return shape2;
        }
        return shape;
    }

    void initPlaceholders() {
        XSLFTextShape sShape;
        CTPlaceholder ph;
        if (this._placeholders == null) {
            this._placeholders = new ArrayList();
            this._placeholderByIdMap = new HashMap();
            this._placeholderByTypeMap = new HashMap();
            for (XSLFShape sh : getShapes()) {
                if ((sh instanceof XSLFTextShape) && (ph = (sShape = (XSLFTextShape) sh).getCTPlaceholder()) != null) {
                    this._placeholders.add(sShape);
                    if (ph.isSetIdx()) {
                        int idx = (int) ph.getIdx();
                        this._placeholderByIdMap.put(Integer.valueOf(idx), sShape);
                    }
                    if (ph.isSetType()) {
                        this._placeholderByTypeMap.put(Integer.valueOf(ph.getType().intValue()), sShape);
                    }
                }
            }
        }
    }

    XSLFSimpleShape getPlaceholderById(int id) {
        initPlaceholders();
        return this._placeholderByIdMap.get(Integer.valueOf(id));
    }

    XSLFSimpleShape getPlaceholderByType(int ordinal) {
        initPlaceholders();
        return this._placeholderByTypeMap.get(Integer.valueOf(ordinal));
    }

    public XSLFTextShape getPlaceholder(int idx) {
        initPlaceholders();
        return this._placeholders.get(idx);
    }

    public XSLFTextShape[] getPlaceholders() {
        initPlaceholders();
        List<XSLFTextShape> list = this._placeholders;
        return (XSLFTextShape[]) list.toArray(new XSLFTextShape[list.size()]);
    }

    protected boolean canDraw(XSLFShape shape) {
        return true;
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public boolean getFollowMasterGraphics() {
        return false;
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public XSLFBackground getBackground() {
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public void draw(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        Drawable draw = drawFact.getDrawable(this);
        draw.draw(graphics);
    }

    String importBlip(String blipId, PackagePart packagePart) {
        PackageRelationship blipRel = packagePart.getRelationship(blipId);
        try {
            PackagePart blipPart = packagePart.getRelatedPart(blipRel);
            XSLFPictureData data = new XSLFPictureData(blipPart);
            XMLSlideShow ppt = getSlideShow();
            XSLFPictureData pictureData = ppt.addPicture(data.getData(), data.getType());
            PackagePart pic = pictureData.getPackagePart();
            POIXMLDocumentPart.RelationPart rp = addRelation(blipId, XSLFRelation.IMAGES, new XSLFPictureData(pic));
            return rp.getRelationship().getId();
        } catch (InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }

    PackagePart importPart(PackageRelationship srcRel, PackagePart srcPafrt) {
        PackagePart destPP = getPackagePart();
        PackagePartName srcPPName = srcPafrt.getPartName();
        OPCPackage pkg = destPP.getPackage();
        if (pkg.containPart(srcPPName)) {
            return pkg.getPart(srcPPName);
        }
        destPP.addRelationship(srcPPName, TargetMode.INTERNAL, srcRel.getRelationshipType());
        PackagePart part = pkg.createPart(srcPPName, srcPafrt.getContentType());
        try {
            OutputStream out = part.getOutputStream();
            InputStream is = srcPafrt.getInputStream();
            IOUtils.copy(is, out);
            is.close();
            out.close();
            return part;
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    void removePictureRelation(XSLFPictureShape pictureShape) {
        POIXMLDocumentPart pd = getRelationById(pictureShape.getBlipId());
        removeRelation(pd);
    }
}
