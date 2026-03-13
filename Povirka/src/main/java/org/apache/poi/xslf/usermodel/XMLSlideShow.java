package org.apache.poi.xslf.usermodel;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.Resources;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.PackageHelper;
import org.apache.poi.util.Units;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideSize;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;

/* JADX INFO: loaded from: classes.dex */
public class XMLSlideShow extends POIXMLDocument implements SlideShow<XSLFShape, XSLFTextParagraph> {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XMLSlideShow.class);
    private XSLFCommentAuthors _commentAuthors;
    private List<XSLFSlideMaster> _masters;
    private XSLFNotesMaster _notesMaster;
    private List<XSLFPictureData> _pictures;
    private CTPresentation _presentation;
    private List<XSLFSlide> _slides;
    private XSLFTableStyles _tableStyles;

    public XMLSlideShow() {
        this(empty());
    }

    public XMLSlideShow(OPCPackage pkg) {
        super(pkg);
        try {
            if (getCorePart().getContentType().equals(XSLFRelation.THEME_MANAGER.getContentType())) {
                rebase(getPackage());
            }
            load(XSLFFactory.getInstance());
        } catch (Exception e) {
            throw new POIXMLException(e);
        }
    }

    public XMLSlideShow(InputStream is) throws IOException {
        this(PackageHelper.open(is));
    }

    static OPCPackage empty() {
        InputStream is = XMLSlideShow.class.getResourceAsStream("empty.pptx");
        try {
            if (is == null) {
                throw new POIXMLException("Missing resource 'empty.pptx'");
            }
            try {
                return OPCPackage.open(is);
            } catch (Exception e) {
                throw new POIXMLException(e);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws IOException {
        try {
            PresentationDocument doc = PresentationDocument.Factory.parse(getCorePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this._presentation = doc.getPresentation();
            Map<String, XSLFSlideMaster> masterMap = new HashMap<>();
            Map<String, XSLFSlide> shIdMap = new HashMap<>();
            for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
                POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof XSLFSlide) {
                    shIdMap.put(rp.getRelationship().getId(), (XSLFSlide) p);
                } else if (p instanceof XSLFSlideMaster) {
                    masterMap.put(getRelationId(p), (XSLFSlideMaster) p);
                } else if (p instanceof XSLFTableStyles) {
                    this._tableStyles = (XSLFTableStyles) p;
                } else if (p instanceof XSLFNotesMaster) {
                    this._notesMaster = (XSLFNotesMaster) p;
                } else if (p instanceof XSLFCommentAuthors) {
                    this._commentAuthors = (XSLFCommentAuthors) p;
                }
            }
            this._masters = new ArrayList(masterMap.size());
            for (CTSlideMasterIdListEntry masterId : this._presentation.getSldMasterIdLst().getSldMasterIdList()) {
                XSLFSlideMaster master = masterMap.get(masterId.getId2());
                this._masters.add(master);
            }
            this._slides = new ArrayList(shIdMap.size());
            if (this._presentation.isSetSldIdLst()) {
                for (CTSlideIdListEntry slId : this._presentation.getSldIdLst().getSldIdList()) {
                    XSLFSlide sh = shIdMap.get(slId.getId2());
                    if (sh == null) {
                        LOG.log(5, "Slide with r:id " + slId.getId() + " was defined, but didn't exist in package, skipping");
                    } else {
                        this._slides.add(sh);
                    }
                }
            }
        } catch (XmlException e) {
            throw new POIXMLException((Throwable) e);
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this._presentation.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        out.close();
    }

    @Override // org.apache.poi.POIXMLDocument
    public List<PackagePart> getAllEmbedds() throws OpenXML4JException {
        return Collections.unmodifiableList(getPackage().getPartsByName(Pattern.compile("/ppt/embeddings/.*?")));
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public List<XSLFPictureData> getPictureData() {
        if (this._pictures == null) {
            List<PackagePart> mediaParts = getPackage().getPartsByName(Pattern.compile("/ppt/media/.*?"));
            this._pictures = new ArrayList(mediaParts.size());
            for (PackagePart part : mediaParts) {
                XSLFPictureData pd = new XSLFPictureData(part);
                pd.setIndex(this._pictures.size());
                this._pictures.add(pd);
            }
        }
        return Collections.unmodifiableList(this._pictures);
    }

    public XSLFSlide createSlide(XSLFSlideLayout layout) {
        CTSlideIdList slideList;
        int slideNumber = 256;
        int cnt = 1;
        if (!this._presentation.isSetSldIdLst()) {
            slideList = this._presentation.addNewSldIdLst();
        } else {
            slideList = this._presentation.getSldIdLst();
            CTSlideIdListEntry[] arr$ = slideList.getSldIdArray();
            for (CTSlideIdListEntry cTSlideIdListEntry : arr$) {
                slideNumber = (int) Math.max(cTSlideIdListEntry.getId() + 1, slideNumber);
                cnt++;
            }
            while (true) {
                String slideName = XSLFRelation.SLIDE.getFileName(cnt);
                boolean found = false;
                Iterator<POIXMLDocumentPart> it = getRelations().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    POIXMLDocumentPart relation = it.next();
                    if (relation.getPackagePart() != null && slideName.equals(relation.getPackagePart().getPartName().getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found && getPackage().getPartsByName(Pattern.compile(Pattern.quote(slideName))).size() > 0) {
                    found = true;
                }
                if (!found) {
                    break;
                }
                cnt++;
            }
        }
        POIXMLDocumentPart.RelationPart rp = createRelationship(XSLFRelation.SLIDE, XSLFFactory.getInstance(), cnt, false);
        XSLFSlide slide = (XSLFSlide) rp.getDocumentPart();
        CTSlideIdListEntry slideId = slideList.addNewSldId();
        slideId.setId(slideNumber);
        slideId.setId2(rp.getRelationship().getId());
        layout.copyLayout(slide);
        slide.addRelation(null, XSLFRelation.SLIDE_LAYOUT, layout);
        this._slides.add(slide);
        return slide;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public XSLFSlide createSlide() {
        XSLFSlideMaster sm = this._masters.get(0);
        XSLFSlideLayout layout = sm.getLayout(SlideLayout.BLANK);
        if (layout == null) {
            LOG.log(5, "Blank layout was not found - defaulting to first slide layout in master");
            XSLFSlideLayout[] sl = sm.getSlideLayouts();
            if (sl.length == 0) {
                throw new POIXMLException("SlideMaster must contain a SlideLayout.");
            }
            layout = sl[0];
        }
        return createSlide(layout);
    }

    public XSLFNotes getNotesSlide(XSLFSlide slide) {
        XSLFNotes notesSlide = slide.getNotes();
        if (notesSlide == null) {
            return createNotesSlide(slide);
        }
        return notesSlide;
    }

    private XSLFNotes createNotesSlide(XSLFSlide slide) {
        if (this._notesMaster == null) {
            createNotesMaster();
        }
        Integer slideIndex = XSLFRelation.SLIDE.getFileNameIndex(slide);
        while (true) {
            String slideName = XSLFRelation.NOTES.getFileName(slideIndex.intValue());
            boolean found = false;
            Iterator<POIXMLDocumentPart> it = getRelations().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                POIXMLDocumentPart relation = it.next();
                if (relation.getPackagePart() != null && slideName.equals(relation.getPackagePart().getPartName().getName())) {
                    found = true;
                    break;
                }
            }
            if (!found && getPackage().getPartsByName(Pattern.compile(Pattern.quote(slideName))).size() > 0) {
                found = true;
            }
            if (found) {
                slideIndex = Integer.valueOf(slideIndex.intValue() + 1);
            } else {
                XSLFNotes notesSlide = (XSLFNotes) createRelationship(XSLFRelation.NOTES, XSLFFactory.getInstance(), slideIndex.intValue());
                slide.addRelation(null, XSLFRelation.NOTES, notesSlide);
                notesSlide.addRelation(null, XSLFRelation.NOTES_MASTER, this._notesMaster);
                notesSlide.addRelation(null, XSLFRelation.SLIDE, slide);
                notesSlide.importContent(this._notesMaster);
                return notesSlide;
            }
        }
    }

    public void createNotesMaster() {
        POIXMLDocumentPart.RelationPart rp = createRelationship(XSLFRelation.NOTES_MASTER, XSLFFactory.getInstance(), 1, false);
        this._notesMaster = (XSLFNotesMaster) rp.getDocumentPart();
        CTNotesMasterIdList notesMasterIdList = this._presentation.addNewNotesMasterIdLst();
        CTNotesMasterIdListEntry notesMasterId = notesMasterIdList.addNewNotesMasterId();
        notesMasterId.setId(rp.getRelationship().getId());
        Integer themeIndex = 1;
        List<Integer> themeIndexList = new ArrayList<>();
        for (POIXMLDocumentPart p : getRelations()) {
            if (p instanceof XSLFTheme) {
                themeIndexList.add(XSLFRelation.THEME.getFileNameIndex(p));
            }
        }
        if (!themeIndexList.isEmpty()) {
            Boolean found = false;
            for (Integer i = 1; i.intValue() <= themeIndexList.size(); i = Integer.valueOf(i.intValue() + 1)) {
                if (!themeIndexList.contains(i)) {
                    found = true;
                    themeIndex = i;
                }
            }
            if (!found.booleanValue()) {
                themeIndex = Integer.valueOf(themeIndexList.size() + 1);
            }
        }
        XSLFTheme theme = (XSLFTheme) createRelationship(XSLFRelation.THEME, XSLFFactory.getInstance(), themeIndex.intValue());
        theme.importTheme(getSlides().get(0).getTheme());
        this._notesMaster.addRelation(null, XSLFRelation.THEME, theme);
    }

    public XSLFNotesMaster getNotesMaster() {
        return this._notesMaster;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public List<XSLFSlideMaster> getSlideMasters() {
        return this._masters;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public List<XSLFSlide> getSlides() {
        return this._slides;
    }

    public XSLFCommentAuthors getCommentAuthors() {
        return this._commentAuthors;
    }

    public void setSlideOrder(XSLFSlide slide, int newIndex) {
        int oldIndex = this._slides.indexOf(slide);
        if (oldIndex == -1) {
            throw new IllegalArgumentException("Slide not found");
        }
        if (oldIndex == newIndex) {
            return;
        }
        List<XSLFSlide> list = this._slides;
        list.add(newIndex, list.remove(oldIndex));
        CTSlideIdList sldIdLst = this._presentation.getSldIdLst();
        CTSlideIdListEntry[] entries = sldIdLst.getSldIdArray();
        CTSlideIdListEntry oldEntry = entries[oldIndex];
        if (oldIndex < newIndex) {
            System.arraycopy(entries, oldIndex + 1, entries, oldIndex, newIndex - oldIndex);
        } else {
            System.arraycopy(entries, newIndex, entries, newIndex + 1, oldIndex - newIndex);
        }
        entries[newIndex] = oldEntry;
        sldIdLst.setSldIdArray(entries);
    }

    public XSLFSlide removeSlide(int index) {
        XSLFSlide slide = this._slides.remove(index);
        removeRelation(slide);
        this._presentation.getSldIdLst().removeSldId(index);
        return slide;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public Dimension getPageSize() {
        CTSlideSize sz = this._presentation.getSldSz();
        int cx = sz.getCx();
        int cy = sz.getCy();
        return new Dimension((int) Units.toPoints(cx), (int) Units.toPoints(cy));
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public void setPageSize(Dimension pgSize) {
        CTSlideSize sz = CTSlideSize.Factory.newInstance();
        sz.setCx(Units.toEMU(pgSize.getWidth()));
        sz.setCy(Units.toEMU(pgSize.getHeight()));
        this._presentation.setSldSz(sz);
    }

    @Internal
    public CTPresentation getCTPresentation() {
        return this._presentation;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public XSLFPictureData addPicture(byte[] pictureData, PictureData.PictureType format) {
        XSLFPictureData img = findPictureData(pictureData);
        if (img != null) {
            return img;
        }
        int imageNumber = this._pictures.size();
        XSLFRelation relType = XSLFPictureData.getRelationForType(format);
        if (relType == null) {
            throw new IllegalArgumentException("Picture type " + format + " is not supported.");
        }
        XSLFPictureData img2 = (XSLFPictureData) createRelationship(relType, XSLFFactory.getInstance(), imageNumber + 1, true).getDocumentPart();
        img2.setIndex(imageNumber);
        this._pictures.add(img2);
        try {
            OutputStream out = img2.getPackagePart().getOutputStream();
            out.write(pictureData);
            out.close();
            return img2;
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public XSLFPictureData addPicture(InputStream is, PictureData.PictureType format) throws IOException {
        return addPicture(IOUtils.toByteArray(is), format);
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public XSLFPictureData addPicture(File pict, PictureData.PictureType format) throws IOException {
        int length = (int) pict.length();
        byte[] data = new byte[length];
        FileInputStream is = new FileInputStream(pict);
        try {
            IOUtils.readFully(is, data);
            is.close();
            return addPicture(data, format);
        } catch (Throwable th) {
            is.close();
            throw th;
        }
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public XSLFPictureData findPictureData(byte[] pictureData) {
        long checksum = IOUtils.calculateChecksum(pictureData);
        byte[] cs = new byte[8];
        LittleEndian.putLong(cs, 0, checksum);
        for (XSLFPictureData pic : getPictureData()) {
            if (Arrays.equals(pic.getChecksum(), cs)) {
                return pic;
            }
        }
        return null;
    }

    public XSLFSlideLayout findLayout(String name) {
        for (XSLFSlideMaster master : getSlideMasters()) {
            XSLFSlideLayout layout = master.getLayout(name);
            if (layout != null) {
                return layout;
            }
        }
        return null;
    }

    public XSLFTableStyles getTableStyles() {
        return this._tableStyles;
    }

    CTTextParagraphProperties getDefaultParagraphStyle(int level) {
        CTTextParagraphProperties[] cTTextParagraphPropertiesArrSelectPath = this._presentation.selectPath("declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//p:defaultTextStyle/a:lvl" + (level + 1) + "pPr");
        if (cTTextParagraphPropertiesArrSelectPath.length == 1) {
            return cTTextParagraphPropertiesArrSelectPath[0];
        }
        return null;
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public MasterSheet<XSLFShape, XSLFTextParagraph> createMasterSheet() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override // org.apache.poi.sl.usermodel.SlideShow
    public Resources getResources() {
        throw new UnsupportedOperationException();
    }
}
