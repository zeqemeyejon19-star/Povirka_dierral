package org.apache.poi.xslf.usermodel;

import java.awt.Graphics2D;
import java.io.IOException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.draw.DrawFactory;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.NotImplemented;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommonSlideData;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTGroupShapeNonVisual;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.SldDocument;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public final class XSLFSlide extends XSLFSheet implements Slide<XSLFShape, XSLFTextParagraph> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private XSLFComments _comments;
    private XSLFSlideLayout _layout;
    private XSLFNotes _notes;
    private final CTSlide _slide;

    XSLFSlide() {
        CTSlide cTSlidePrototype = prototype();
        this._slide = cTSlidePrototype;
        setCommonSlideData(cTSlidePrototype.getCSld());
    }

    XSLFSlide(PackagePart part) throws XmlException, IOException {
        super(part);
        try {
            Document _doc = DocumentHelper.readDocument(getPackagePart().getInputStream());
            SldDocument doc = SldDocument.Factory.parse(_doc, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            CTSlide sld = doc.getSld();
            this._slide = sld;
            setCommonSlideData(sld.getCSld());
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    private static CTSlide prototype() {
        CTSlide ctSlide = CTSlide.Factory.newInstance();
        CTCommonSlideData cSld = ctSlide.addNewCSld();
        CTGroupShape spTree = cSld.addNewSpTree();
        CTGroupShapeNonVisual nvGrpSpPr = spTree.addNewNvGrpSpPr();
        CTNonVisualDrawingProps cnvPr = nvGrpSpPr.addNewCNvPr();
        cnvPr.setId(1L);
        cnvPr.setName("");
        nvGrpSpPr.addNewCNvGrpSpPr();
        nvGrpSpPr.addNewNvPr();
        CTGroupShapeProperties grpSpr = spTree.addNewGrpSpPr();
        CTGroupTransform2D xfrm = grpSpr.addNewXfrm();
        CTPoint2D off = xfrm.addNewOff();
        off.setX(0L);
        off.setY(0L);
        CTPositiveSize2D ext = xfrm.addNewExt();
        ext.setCx(0L);
        ext.setCy(0L);
        CTPoint2D choff = xfrm.addNewChOff();
        choff.setX(0L);
        choff.setY(0L);
        CTPositiveSize2D chExt = xfrm.addNewChExt();
        chExt.setCx(0L);
        chExt.setCy(0L);
        ctSlide.addNewClrMapOvr().addNewMasterClrMapping();
        return ctSlide;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public CTSlide getXmlObject() {
        return this._slide;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    protected String getRootElementName() {
        return "sld";
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public XSLFSlideLayout getMasterSheet() {
        return getSlideLayout();
    }

    public XSLFSlideLayout getSlideLayout() {
        if (this._layout == null) {
            for (POIXMLDocumentPart p : getRelations()) {
                if (p instanceof XSLFSlideLayout) {
                    this._layout = (XSLFSlideLayout) p;
                }
            }
        }
        XSLFSlideLayout xSLFSlideLayout = this._layout;
        if (xSLFSlideLayout == null) {
            throw new IllegalArgumentException("SlideLayout was not found for " + this);
        }
        return xSLFSlideLayout;
    }

    public XSLFSlideMaster getSlideMaster() {
        return getSlideLayout().getSlideMaster();
    }

    public XSLFComments getComments() {
        if (this._comments == null) {
            for (POIXMLDocumentPart p : getRelations()) {
                if (p instanceof XSLFComments) {
                    this._comments = (XSLFComments) p;
                }
            }
        }
        XSLFComments xSLFComments = this._comments;
        if (xSLFComments == null) {
            return null;
        }
        return xSLFComments;
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public XSLFNotes getNotes() {
        if (this._notes == null) {
            for (POIXMLDocumentPart p : getRelations()) {
                if (p instanceof XSLFNotes) {
                    this._notes = (XSLFNotes) p;
                }
            }
        }
        XSLFNotes xSLFNotes = this._notes;
        if (xSLFNotes == null) {
            return null;
        }
        return xSLFNotes;
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public String getTitle() {
        XSLFTextShape txt = getTextShapeByType(Placeholder.TITLE);
        if (txt == null) {
            return null;
        }
        return txt.getText();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public XSLFTheme getTheme() {
        return getSlideLayout().getSlideMaster().getTheme();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet, org.apache.poi.sl.usermodel.Sheet
    public XSLFBackground getBackground() {
        CTBackground bg = this._slide.getCSld().getBg();
        if (bg != null) {
            return new XSLFBackground(bg, this);
        }
        return getMasterSheet().getBackground();
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet, org.apache.poi.sl.usermodel.Sheet
    public boolean getFollowMasterGraphics() {
        return this._slide.getShowMasterSp();
    }

    public void setFollowMasterGraphics(boolean value) {
        this._slide.setShowMasterSp(value);
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public boolean getFollowMasterObjects() {
        return getFollowMasterGraphics();
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public void setFollowMasterObjects(boolean follow) {
        setFollowMasterGraphics(follow);
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public XSLFSlide importContent(XSLFSheet src) {
        CTBackground bgOther;
        super.importContent(src);
        if (!(src instanceof XSLFSlide) || (bgOther = ((XSLFSlide) src)._slide.getCSld().getBg()) == null) {
            return this;
        }
        CTBackground bgThis = this._slide.getCSld().getBg();
        if (bgThis != null) {
            if (bgThis.isSetBgPr() && bgThis.getBgPr().isSetBlipFill()) {
                String oldId = bgThis.getBgPr().getBlipFill().getBlip().getEmbed();
                removeRelation(getRelationById(oldId));
            }
            this._slide.getCSld().unsetBg();
        }
        CTBackground bgThis2 = this._slide.getCSld().addNewBg().set(bgOther);
        if (bgOther.isSetBgPr() && bgOther.getBgPr().isSetBlipFill()) {
            String idOther = bgOther.getBgPr().getBlipFill().getBlip().getEmbed();
            String idThis = importBlip(idOther, src.getPackagePart());
            bgThis2.getBgPr().getBlipFill().getBlip().setEmbed(idThis);
        }
        return this;
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public boolean getFollowMasterBackground() {
        return false;
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    @NotImplemented
    public void setFollowMasterBackground(boolean follow) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public boolean getFollowMasterColourScheme() {
        return false;
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    @NotImplemented
    public void setFollowMasterColourScheme(boolean follow) {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    @NotImplemented
    public void setNotes(Notes<XSLFShape, XSLFTextParagraph> notes) {
        if (!(notes instanceof XSLFNotes)) {
            throw new AssertionError();
        }
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public int getSlideNumber() {
        int idx = getSlideShow().getSlides().indexOf(this);
        return idx == -1 ? idx : idx + 1;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet, org.apache.poi.sl.usermodel.Sheet
    public void draw(Graphics2D graphics) {
        DrawFactory drawFact = DrawFactory.getInstance(graphics);
        Drawable draw = drawFact.getDrawable((Slide<?, ?>) this);
        draw.draw(graphics);
    }

    @Override // org.apache.poi.sl.usermodel.Slide
    public boolean getDisplayPlaceholder(Placeholder placeholder) {
        return false;
    }
}
