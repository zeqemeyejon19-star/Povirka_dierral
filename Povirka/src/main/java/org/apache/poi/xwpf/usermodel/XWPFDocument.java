package org.apache.poi.xwpf.usermodel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.IdentifierManager;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.PackageHelper;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTComment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CommentsDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.DocumentDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.EndnotesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.NumberingDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.StylesDocument;

/* JADX INFO: loaded from: classes.dex */
public class XWPFDocument extends POIXMLDocument implements Document, IBody {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XWPFDocument.class);
    protected List<IBodyElement> bodyElements;
    protected List<XWPFComment> comments;
    protected List<XWPFSDT> contentControls;
    private CTDocument1 ctDocument;
    private IdentifierManager drawingIdManager;
    protected Map<Integer, XWPFFootnote> endnotes;
    protected List<XWPFFooter> footers;
    protected XWPFFootnotes footnotes;
    private XWPFHeaderFooterPolicy headerFooterPolicy;
    protected List<XWPFHeader> headers;
    protected List<XWPFHyperlink> hyperlinks;
    protected XWPFNumbering numbering;
    protected Map<Long, List<XWPFPictureData>> packagePictures;
    protected List<XWPFParagraph> paragraphs;
    protected List<XWPFPictureData> pictures;
    private XWPFSettings settings;
    protected XWPFStyles styles;
    protected List<XWPFTable> tables;

    public XWPFDocument(OPCPackage pkg) throws IOException {
        super(pkg);
        this.footers = new ArrayList();
        this.headers = new ArrayList();
        this.comments = new ArrayList();
        this.hyperlinks = new ArrayList();
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.contentControls = new ArrayList();
        this.bodyElements = new ArrayList();
        this.pictures = new ArrayList();
        this.packagePictures = new HashMap();
        this.endnotes = new HashMap();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        load(XWPFFactory.getInstance());
    }

    public XWPFDocument(InputStream is) throws IOException {
        super(PackageHelper.open(is));
        this.footers = new ArrayList();
        this.headers = new ArrayList();
        this.comments = new ArrayList();
        this.hyperlinks = new ArrayList();
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.contentControls = new ArrayList();
        this.bodyElements = new ArrayList();
        this.pictures = new ArrayList();
        this.packagePictures = new HashMap();
        this.endnotes = new HashMap();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        load(XWPFFactory.getInstance());
    }

    public XWPFDocument() {
        super(newPackage());
        this.footers = new ArrayList();
        this.headers = new ArrayList();
        this.comments = new ArrayList();
        this.hyperlinks = new ArrayList();
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.contentControls = new ArrayList();
        this.bodyElements = new ArrayList();
        this.pictures = new ArrayList();
        this.packagePictures = new HashMap();
        this.endnotes = new HashMap();
        this.drawingIdManager = new IdentifierManager(0L, 4294967295L);
        onDocumentCreate();
    }

    protected static OPCPackage newPackage() {
        try {
            OPCPackage pkg = OPCPackage.create(new ByteArrayOutputStream());
            PackagePartName corePartName = PackagingURIHelper.createPartName(XWPFRelation.DOCUMENT.getDefaultFileName());
            pkg.addRelationship(corePartName, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
            pkg.createPart(corePartName, XWPFRelation.DOCUMENT.getContentType());
            pkg.getPackageProperties().setCreatorProperty(POIXMLDocument.DOCUMENT_CREATOR);
            return pkg;
        } catch (Exception e) {
            throw new POIXMLException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws Throwable {
        try {
            DocumentDocument doc = DocumentDocument.Factory.parse(getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctDocument = doc.getDocument();
            initFootnotes();
            XmlCursor docCursor = this.ctDocument.newCursor();
            docCursor.selectPath("./*");
            while (docCursor.toNextSelection()) {
                XmlObject o = docCursor.getObject();
                if (o instanceof CTBody) {
                    XmlCursor bodyCursor = o.newCursor();
                    bodyCursor.selectPath("./*");
                    while (bodyCursor.toNextSelection()) {
                        CTP object = bodyCursor.getObject();
                        if (object instanceof CTP) {
                            XWPFParagraph p = new XWPFParagraph(object, this);
                            this.bodyElements.add(p);
                            this.paragraphs.add(p);
                        } else if (object instanceof CTTbl) {
                            XWPFTable t = new XWPFTable((CTTbl) object, this);
                            this.bodyElements.add(t);
                            this.tables.add(t);
                        } else if (object instanceof CTSdtBlock) {
                            XWPFSDT c = new XWPFSDT((CTSdtBlock) object, this);
                            this.bodyElements.add(c);
                            this.contentControls.add(c);
                        }
                    }
                    bodyCursor.dispose();
                }
            }
            docCursor.dispose();
            if (doc.getDocument().getBody().getSectPr() != null) {
                this.headerFooterPolicy = new XWPFHeaderFooterPolicy(this);
            }
            for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
                POIXMLDocumentPart p2 = rp.getDocumentPart();
                String relation = rp.getRelationship().getRelationshipType();
                if (relation.equals(XWPFRelation.STYLES.getRelation())) {
                    XWPFStyles xWPFStyles = (XWPFStyles) p2;
                    this.styles = xWPFStyles;
                    xWPFStyles.onDocumentRead();
                } else if (relation.equals(XWPFRelation.NUMBERING.getRelation())) {
                    XWPFNumbering xWPFNumbering = (XWPFNumbering) p2;
                    this.numbering = xWPFNumbering;
                    xWPFNumbering.onDocumentRead();
                } else if (relation.equals(XWPFRelation.FOOTER.getRelation())) {
                    XWPFFooter footer = (XWPFFooter) p2;
                    this.footers.add(footer);
                    footer.onDocumentRead();
                } else if (relation.equals(XWPFRelation.HEADER.getRelation())) {
                    XWPFHeader header = (XWPFHeader) p2;
                    this.headers.add(header);
                    header.onDocumentRead();
                } else if (relation.equals(XWPFRelation.COMMENT.getRelation())) {
                    CommentsDocument cmntdoc = CommentsDocument.Factory.parse(p2.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    CTComment[] arr$ = cmntdoc.getComments().getCommentArray();
                    for (CTComment ctcomment : arr$) {
                        this.comments.add(new XWPFComment(ctcomment, this));
                    }
                } else if (relation.equals(XWPFRelation.SETTINGS.getRelation())) {
                    XWPFSettings xWPFSettings = (XWPFSettings) p2;
                    this.settings = xWPFSettings;
                    xWPFSettings.onDocumentRead();
                } else if (relation.equals(XWPFRelation.IMAGES.getRelation())) {
                    XWPFPictureData picData = (XWPFPictureData) p2;
                    picData.onDocumentRead();
                    registerPackagePictureData(picData);
                    this.pictures.add(picData);
                } else if (relation.equals(XWPFRelation.GLOSSARY_DOCUMENT.getRelation())) {
                    for (POIXMLDocumentPart gp : p2.getRelations()) {
                        POIXMLDocumentPart._invokeOnDocumentRead(gp);
                    }
                }
            }
            initHyperlinks();
        } catch (XmlException e) {
            throw new POIXMLException((Throwable) e);
        }
    }

    private void initHyperlinks() {
        try {
            for (PackageRelationship rel : getPackagePart().getRelationshipsByType(XWPFRelation.HYPERLINK.getRelation())) {
                this.hyperlinks.add(new XWPFHyperlink(rel.getId(), rel.getTargetURI().toString()));
            }
        } catch (InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }

    private void initFootnotes() throws XmlException, IOException {
        for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
            POIXMLDocumentPart p = rp.getDocumentPart();
            String relation = rp.getRelationship().getRelationshipType();
            if (relation.equals(XWPFRelation.FOOTNOTE.getRelation())) {
                XWPFFootnotes xWPFFootnotes = (XWPFFootnotes) p;
                this.footnotes = xWPFFootnotes;
                xWPFFootnotes.onDocumentRead();
            } else if (relation.equals(XWPFRelation.ENDNOTE.getRelation())) {
                EndnotesDocument endnotesDocument = EndnotesDocument.Factory.parse(p.getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                CTFtnEdn[] arr$ = endnotesDocument.getEndnotes().getEndnoteArray();
                for (CTFtnEdn ctFtnEdn : arr$) {
                    this.endnotes.put(Integer.valueOf(ctFtnEdn.getId().intValue()), new XWPFFootnote(this, ctFtnEdn));
                }
            }
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentCreate() {
        CTDocument1 cTDocument1NewInstance = CTDocument1.Factory.newInstance();
        this.ctDocument = cTDocument1NewInstance;
        cTDocument1NewInstance.addNewBody();
        this.settings = (XWPFSettings) createRelationship(XWPFRelation.SETTINGS, XWPFFactory.getInstance());
        POIXMLProperties.ExtendedProperties expProps = getProperties().getExtendedProperties();
        expProps.getUnderlyingProperties().setApplication(POIXMLDocument.DOCUMENT_CREATOR);
    }

    @Internal
    public CTDocument1 getDocument() {
        return this.ctDocument;
    }

    IdentifierManager getDrawingIdManager() {
        return this.drawingIdManager;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList(this.bodyElements);
    }

    public Iterator<IBodyElement> getBodyElementsIterator() {
        return this.bodyElements.iterator();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList(this.paragraphs);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList(this.tables);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTableArray(int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }

    public List<XWPFFooter> getFooterList() {
        return Collections.unmodifiableList(this.footers);
    }

    public XWPFFooter getFooterArray(int pos) {
        if (pos >= 0 && pos < this.footers.size()) {
            return this.footers.get(pos);
        }
        return null;
    }

    public List<XWPFHeader> getHeaderList() {
        return Collections.unmodifiableList(this.headers);
    }

    public XWPFHeader getHeaderArray(int pos) {
        if (pos >= 0 && pos < this.headers.size()) {
            return this.headers.get(pos);
        }
        return null;
    }

    public String getTblStyle(XWPFTable table) {
        return table.getStyleID();
    }

    public XWPFHyperlink getHyperlinkByID(String id) {
        for (XWPFHyperlink link : this.hyperlinks) {
            if (link.getId().equals(id)) {
                return link;
            }
        }
        return null;
    }

    public XWPFFootnote getFootnoteByID(int id) {
        XWPFFootnotes xWPFFootnotes = this.footnotes;
        if (xWPFFootnotes == null) {
            return null;
        }
        return xWPFFootnotes.getFootnoteById(id);
    }

    public XWPFFootnote getEndnoteByID(int id) {
        Map<Integer, XWPFFootnote> map = this.endnotes;
        if (map == null) {
            return null;
        }
        return map.get(Integer.valueOf(id));
    }

    public List<XWPFFootnote> getFootnotes() {
        XWPFFootnotes xWPFFootnotes = this.footnotes;
        if (xWPFFootnotes == null) {
            return Collections.emptyList();
        }
        return xWPFFootnotes.getFootnotesList();
    }

    public XWPFHyperlink[] getHyperlinks() {
        List<XWPFHyperlink> list = this.hyperlinks;
        return (XWPFHyperlink[]) list.toArray(new XWPFHyperlink[list.size()]);
    }

    public XWPFComment getCommentByID(String id) {
        for (XWPFComment comment : this.comments) {
            if (comment.getId().equals(id)) {
                return comment;
            }
        }
        return null;
    }

    public XWPFComment[] getComments() {
        List<XWPFComment> list = this.comments;
        return (XWPFComment[]) list.toArray(new XWPFComment[list.size()]);
    }

    public PackagePart getPartById(String id) {
        try {
            PackagePart corePart = getCorePart();
            return corePart.getRelatedPart(corePart.getRelationship(id));
        } catch (InvalidFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public XWPFHeaderFooterPolicy getHeaderFooterPolicy() {
        return this.headerFooterPolicy;
    }

    public XWPFHeaderFooterPolicy createHeaderFooterPolicy() {
        if (this.headerFooterPolicy == null) {
            this.headerFooterPolicy = new XWPFHeaderFooterPolicy(this);
        }
        return this.headerFooterPolicy;
    }

    public XWPFHeader createHeader(HeaderFooterType type) {
        XWPFHeaderFooterPolicy hfPolicy = createHeaderFooterPolicy();
        if (type == HeaderFooterType.FIRST) {
            CTSectPr ctSectPr = getSection();
            if (!ctSectPr.isSetTitlePg()) {
                CTOnOff titlePg = ctSectPr.addNewTitlePg();
                titlePg.setVal(STOnOff.ON);
            }
        }
        return hfPolicy.createHeader(STHdrFtr.Enum.forInt(type.toInt()));
    }

    public XWPFFooter createFooter(HeaderFooterType type) {
        XWPFHeaderFooterPolicy hfPolicy = createHeaderFooterPolicy();
        if (type == HeaderFooterType.FIRST) {
            CTSectPr ctSectPr = getSection();
            if (!ctSectPr.isSetTitlePg()) {
                CTOnOff titlePg = ctSectPr.addNewTitlePg();
                titlePg.setVal(STOnOff.ON);
            }
        }
        return hfPolicy.createFooter(STHdrFtr.Enum.forInt(type.toInt()));
    }

    private CTSectPr getSection() {
        CTBody ctBody = getDocument().getBody();
        return ctBody.isSetSectPr() ? ctBody.getSectPr() : ctBody.addNewSectPr();
    }

    @Internal
    public CTStyles getStyle() throws XmlException, IOException {
        try {
            PackagePart[] parts = getRelatedByType(XWPFRelation.STYLES.getRelation());
            if (parts.length != 1) {
                throw new IllegalStateException("Expecting one Styles document part, but found " + parts.length);
            }
            StylesDocument sd = StylesDocument.Factory.parse(parts[0].getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            return sd.getStyles();
        } catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // org.apache.poi.POIXMLDocument
    public List<PackagePart> getAllEmbedds() throws OpenXML4JException {
        List<PackagePart> embedds = new LinkedList<>();
        PackagePart part = getPackagePart();
        for (PackageRelationship rel : getPackagePart().getRelationshipsByType(POIXMLDocument.OLE_OBJECT_REL_TYPE)) {
            embedds.add(part.getRelatedPart(rel));
        }
        for (PackageRelationship rel2 : getPackagePart().getRelationshipsByType(POIXMLDocument.PACK_OBJECT_REL_TYPE)) {
            embedds.add(part.getRelatedPart(rel2));
        }
        return embedds;
    }

    private int getBodyElementSpecificPos(int pos, List<? extends IBodyElement> list) {
        if (list.size() != 0 && pos >= 0 && pos < this.bodyElements.size()) {
            IBodyElement needle = this.bodyElements.get(pos);
            if (needle.getElementType() != list.get(0).getElementType()) {
                return -1;
            }
            int startPos = Math.min(pos, list.size() - 1);
            for (int i = startPos; i >= 0; i--) {
                if (list.get(i) == needle) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int getParagraphPos(int pos) {
        return getBodyElementSpecificPos(pos, this.paragraphs);
    }

    public int getTablePos(int pos) {
        return getBodyElementSpecificPos(pos, this.tables);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (isCursorInBody(cursor)) {
            String uri = CTP.type.getName().getNamespaceURI();
            cursor.beginElement("p", uri);
            cursor.toParent();
            CTP p = cursor.getObject();
            XWPFParagraph newP = new XWPFParagraph(p, this);
            XmlObject o = null;
            while (!(o instanceof CTP) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTP) || ((CTP) o) == p) {
                this.paragraphs.add(0, newP);
            } else {
                int pos = this.paragraphs.indexOf(getParagraph((CTP) o)) + 1;
                this.paragraphs.add(pos, newP);
            }
            XmlCursor newParaPos = p.newCursor();
            int i = 0;
            try {
                cursor.toCursor(newParaPos);
                while (cursor.toPrevSibling()) {
                    XmlObject o2 = cursor.getObject();
                    if ((o2 instanceof CTP) || (o2 instanceof CTTbl)) {
                        i++;
                    }
                }
                this.bodyElements.add(i, newP);
                cursor.toCursor(newParaPos);
                cursor.toEndToken();
                return newP;
            } finally {
                newParaPos.dispose();
            }
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (isCursorInBody(cursor)) {
            String uri = CTTbl.type.getName().getNamespaceURI();
            cursor.beginElement("tbl", uri);
            cursor.toParent();
            CTTbl t = cursor.getObject();
            XWPFTable newT = new XWPFTable(t, this);
            XmlObject o = null;
            while (!(o instanceof CTTbl) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTTbl)) {
                this.tables.add(0, newT);
            } else {
                int pos = this.tables.indexOf(getTable((CTTbl) o)) + 1;
                this.tables.add(pos, newT);
            }
            int i = 0;
            XmlCursor tableCursor = t.newCursor();
            try {
                cursor.toCursor(tableCursor);
                while (cursor.toPrevSibling()) {
                    XmlObject o2 = cursor.getObject();
                    if ((o2 instanceof CTP) || (o2 instanceof CTTbl)) {
                        i++;
                    }
                }
                this.bodyElements.add(i, newT);
                cursor.toCursor(tableCursor);
                cursor.toEndToken();
                return newT;
            } finally {
                tableCursor.dispose();
            }
        }
        return null;
    }

    private boolean isCursorInBody(XmlCursor cursor) {
        XmlCursor verify = cursor.newCursor();
        verify.toParent();
        boolean result = verify.getObject() == this.ctDocument.getBody();
        verify.dispose();
        return result;
    }

    private int getPosOfBodyElement(IBodyElement needle) {
        BodyElementType type = needle.getElementType();
        for (int i = 0; i < this.bodyElements.size(); i++) {
            IBodyElement current = this.bodyElements.get(i);
            if (current.getElementType() == type && current.equals(needle)) {
                return i;
            }
        }
        return -1;
    }

    public int getPosOfParagraph(XWPFParagraph p) {
        return getPosOfBodyElement(p);
    }

    public int getPosOfTable(XWPFTable t) {
        return getPosOfBodyElement(t);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTDocument1.type.getName().getNamespaceURI(), "document"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.ctDocument.save(out, xmlOptions);
        out.close();
    }

    private int getRelationIndex(XWPFRelation relation) {
        int i = 1;
        for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
            if (rp.getRelationship().getRelationshipType().equals(relation.getRelation())) {
                i++;
            }
        }
        return i;
    }

    public XWPFParagraph createParagraph() {
        XWPFParagraph p = new XWPFParagraph(this.ctDocument.getBody().addNewP(), this);
        this.bodyElements.add(p);
        this.paragraphs.add(p);
        return p;
    }

    public XWPFNumbering createNumbering() {
        if (this.numbering == null) {
            NumberingDocument numberingDoc = NumberingDocument.Factory.newInstance();
            XWPFRelation relation = XWPFRelation.NUMBERING;
            int i = getRelationIndex(relation);
            XWPFNumbering wrapper = (XWPFNumbering) createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setNumbering(numberingDoc.addNewNumbering());
            this.numbering = wrapper;
        }
        return this.numbering;
    }

    public XWPFStyles createStyles() {
        if (this.styles == null) {
            StylesDocument stylesDoc = StylesDocument.Factory.newInstance();
            XWPFRelation relation = XWPFRelation.STYLES;
            int i = getRelationIndex(relation);
            XWPFStyles wrapper = (XWPFStyles) createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setStyles(stylesDoc.addNewStyles());
            this.styles = wrapper;
        }
        return this.styles;
    }

    public XWPFFootnotes createFootnotes() {
        if (this.footnotes == null) {
            FootnotesDocument footnotesDoc = FootnotesDocument.Factory.newInstance();
            XWPFRelation relation = XWPFRelation.FOOTNOTE;
            int i = getRelationIndex(relation);
            XWPFFootnotes wrapper = (XWPFFootnotes) createRelationship(relation, XWPFFactory.getInstance(), i);
            wrapper.setFootnotes(footnotesDoc.addNewFootnotes());
            this.footnotes = wrapper;
        }
        return this.footnotes;
    }

    public XWPFFootnote addFootnote(CTFtnEdn note) {
        return this.footnotes.addFootnote(note);
    }

    public XWPFFootnote addEndnote(CTFtnEdn note) {
        XWPFFootnote endnote = new XWPFFootnote(this, note);
        this.endnotes.put(Integer.valueOf(note.getId().intValue()), endnote);
        return endnote;
    }

    public boolean removeBodyElement(int pos) {
        if (pos >= 0 && pos < this.bodyElements.size()) {
            BodyElementType type = this.bodyElements.get(pos).getElementType();
            if (type == BodyElementType.TABLE) {
                int tablePos = getTablePos(pos);
                this.tables.remove(tablePos);
                this.ctDocument.getBody().removeTbl(tablePos);
            }
            if (type == BodyElementType.PARAGRAPH) {
                int paraPos = getParagraphPos(pos);
                this.paragraphs.remove(paraPos);
                this.ctDocument.getBody().removeP(paraPos);
            }
            this.bodyElements.remove(pos);
            return true;
        }
        return false;
    }

    public void setParagraph(XWPFParagraph paragraph, int pos) {
        this.paragraphs.set(pos, paragraph);
        this.ctDocument.getBody().setPArray(pos, paragraph.getCTP());
    }

    public XWPFParagraph getLastParagraph() {
        int lastPos = this.paragraphs.toArray().length - 1;
        return this.paragraphs.get(lastPos);
    }

    public XWPFTable createTable() {
        XWPFTable table = new XWPFTable(this.ctDocument.getBody().addNewTbl(), this);
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }

    public XWPFTable createTable(int rows, int cols) {
        XWPFTable table = new XWPFTable(this.ctDocument.getBody().addNewTbl(), this, rows, cols);
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }

    public void createTOC() {
        CTSdtBlock block = getDocument().getBody().addNewSdt();
        TOC toc = new TOC(block);
        for (XWPFParagraph par : this.paragraphs) {
            String parStyle = par.getStyle();
            if (parStyle != null && parStyle.startsWith("Heading")) {
                try {
                    int level = Integer.parseInt(parStyle.substring("Heading".length()));
                    toc.addRow(level, par.getText(), 1, "112723803");
                } catch (NumberFormatException e) {
                    LOG.log(7, "can't format number in TOC heading", e);
                }
            }
        }
    }

    public void setTable(int pos, XWPFTable table) {
        this.tables.set(pos, table);
        this.ctDocument.getBody().setTblArray(pos, table.getCTTbl());
    }

    public boolean isEnforcedProtection() {
        return this.settings.isEnforcedWith();
    }

    public boolean isEnforcedReadonlyProtection() {
        return this.settings.isEnforcedWith(STDocProtect.READ_ONLY);
    }

    public boolean isEnforcedFillingFormsProtection() {
        return this.settings.isEnforcedWith(STDocProtect.FORMS);
    }

    public boolean isEnforcedCommentsProtection() {
        return this.settings.isEnforcedWith(STDocProtect.COMMENTS);
    }

    public boolean isEnforcedTrackedChangesProtection() {
        return this.settings.isEnforcedWith(STDocProtect.TRACKED_CHANGES);
    }

    public boolean isEnforcedUpdateFields() {
        return this.settings.isUpdateFields();
    }

    public void enforceReadonlyProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.READ_ONLY);
    }

    public void enforceReadonlyProtection(String password, HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.READ_ONLY, password, hashAlgo);
    }

    public void enforceFillingFormsProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.FORMS);
    }

    public void enforceFillingFormsProtection(String password, HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.FORMS, password, hashAlgo);
    }

    public void enforceCommentsProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.COMMENTS);
    }

    public void enforceCommentsProtection(String password, HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.COMMENTS, password, hashAlgo);
    }

    public void enforceTrackedChangesProtection() {
        this.settings.setEnforcementEditValue(STDocProtect.TRACKED_CHANGES);
    }

    public void enforceTrackedChangesProtection(String password, HashAlgorithm hashAlgo) {
        this.settings.setEnforcementEditValue(STDocProtect.TRACKED_CHANGES, password, hashAlgo);
    }

    public boolean validateProtectionPassword(String password) {
        return this.settings.validateProtectionPassword(password);
    }

    public void removeProtectionEnforcement() {
        this.settings.removeEnforcement();
    }

    public void enforceUpdateFields() {
        this.settings.setUpdateFields();
    }

    public boolean isTrackRevisions() {
        return this.settings.isTrackRevisions();
    }

    public void setTrackRevisions(boolean enable) {
        this.settings.setTrackRevisions(enable);
    }

    public long getZoomPercent() {
        return this.settings.getZoomPercent();
    }

    public void setZoomPercent(long zoomPercent) {
        this.settings.setZoomPercent(zoomPercent);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        CTTbl[] arr$ = this.ctDocument.getBody().getTblArray();
        for (CTTbl tbl : arr$) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            i++;
        }
        this.tables.add(i, table);
    }

    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList(this.pictures);
    }

    public List<XWPFPictureData> getAllPackagePictures() {
        List<XWPFPictureData> result = new ArrayList<>();
        Collection<List<XWPFPictureData>> values = this.packagePictures.values();
        for (List<XWPFPictureData> list : values) {
            result.addAll(list);
        }
        return Collections.unmodifiableList(result);
    }

    void registerPackagePictureData(XWPFPictureData picData) {
        List<XWPFPictureData> list = this.packagePictures.get(picData.getChecksum());
        if (list == null) {
            list = new ArrayList(1);
            this.packagePictures.put(picData.getChecksum(), list);
        }
        if (!list.contains(picData)) {
            list.add(picData);
        }
    }

    XWPFPictureData findPackagePictureData(byte[] pictureData, int format) {
        long checksum = IOUtils.calculateChecksum(pictureData);
        XWPFPictureData xwpfPicData = null;
        List<XWPFPictureData> xwpfPicDataList = this.packagePictures.get(Long.valueOf(checksum));
        if (xwpfPicDataList != null) {
            Iterator<XWPFPictureData> iter = xwpfPicDataList.iterator();
            while (iter.hasNext() && xwpfPicData == null) {
                XWPFPictureData curElem = iter.next();
                if (Arrays.equals(pictureData, curElem.getData())) {
                    xwpfPicData = curElem;
                }
            }
        }
        return xwpfPicData;
    }

    public String addPictureData(byte[] pictureData, int format) throws InvalidFormatException {
        XWPFPictureData xwpfPicData = findPackagePictureData(pictureData, format);
        POIXMLRelation relDesc = XWPFPictureData.RELATIONS[format];
        if (xwpfPicData == null) {
            int idx = getNextPicNameNumber(format);
            XWPFPictureData xwpfPicData2 = (XWPFPictureData) createRelationship(relDesc, XWPFFactory.getInstance(), idx);
            PackagePart picDataPart = xwpfPicData2.getPackagePart();
            OutputStream out = null;
            try {
                try {
                    out = picDataPart.getOutputStream();
                    out.write(pictureData);
                    registerPackagePictureData(xwpfPicData2);
                    this.pictures.add(xwpfPicData2);
                    return getRelationId(xwpfPicData2);
                } catch (IOException e) {
                    throw new POIXMLException(e);
                }
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e2) {
                    }
                }
            }
        }
        if (!getRelations().contains(xwpfPicData)) {
            POIXMLDocumentPart.RelationPart rp = addRelation(null, XWPFRelation.IMAGES, xwpfPicData);
            return rp.getRelationship().getId();
        }
        return getRelationId(xwpfPicData);
    }

    public String addPictureData(InputStream is, int format) throws InvalidFormatException {
        try {
            byte[] data = IOUtils.toByteArray(is);
            return addPictureData(data, format);
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    public int getNextPicNameNumber(int format) throws InvalidFormatException {
        int img = getAllPackagePictures().size() + 1;
        String proposal = XWPFPictureData.RELATIONS[format].getFileName(img);
        PackagePartName createPartName = PackagingURIHelper.createPartName(proposal);
        while (getPackage().getPart(createPartName) != null) {
            img++;
            String proposal2 = XWPFPictureData.RELATIONS[format].getFileName(img);
            createPartName = PackagingURIHelper.createPartName(proposal2);
        }
        return img;
    }

    public XWPFPictureData getPictureDataByID(String blipID) {
        POIXMLDocumentPart relatedPart = getRelationById(blipID);
        if (relatedPart instanceof XWPFPictureData) {
            XWPFPictureData xwpfPicData = (XWPFPictureData) relatedPart;
            return xwpfPicData;
        }
        return null;
    }

    public XWPFNumbering getNumbering() {
        return this.numbering;
    }

    public XWPFStyles getStyles() {
        return this.styles;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraph(CTP p) {
        for (int i = 0; i < getParagraphs().size(); i++) {
            if (getParagraphs().get(i).getCTP() == p) {
                return getParagraphs().get(i);
            }
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTable(CTTbl ctTbl) {
        for (int i = 0; i < this.tables.size(); i++) {
            if (getTables().get(i).getCTTbl() == ctTbl) {
                return getTables().get(i);
            }
        }
        return null;
    }

    public Iterator<XWPFTable> getTablesIterator() {
        return this.tables.iterator();
    }

    public Iterator<XWPFParagraph> getParagraphsIterator() {
        return this.paragraphs.iterator();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public POIXMLDocumentPart getPart() {
        return this;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public BodyType getPartType() {
        return BodyType.DOCUMENT;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTableCell getTableCell(CTTc cell) {
        XWPFTableRow tableRow;
        XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            return null;
        }
        CTRow row = (CTRow) o;
        cursor.toParent();
        XmlObject o2 = cursor.getObject();
        cursor.dispose();
        if (!(o2 instanceof CTTbl)) {
            return null;
        }
        CTTbl tbl = (CTTbl) o2;
        XWPFTable table = getTable(tbl);
        if (table == null || (tableRow = table.getRow(row)) == null) {
            return null;
        }
        return tableRow.getTableCell(cell);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFDocument getXWPFDocument() {
        return this;
    }
}
