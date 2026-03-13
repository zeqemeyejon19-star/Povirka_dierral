package org.apache.poi.xslf.extractor;

import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommentAuthors;
import org.apache.poi.xslf.usermodel.XSLFComments;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFShapeContainer;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;

/* JADX INFO: loaded from: classes.dex */
public class XSLFPowerPointExtractor extends POIXMLTextExtractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final XSLFRelation[] SUPPORTED_TYPES = {XSLFRelation.MAIN, XSLFRelation.MACRO, XSLFRelation.MACRO_TEMPLATE, XSLFRelation.PRESENTATIONML, XSLFRelation.PRESENTATIONML_TEMPLATE, XSLFRelation.PRESENTATION_MACRO};
    private boolean masterByDefault;
    private boolean notesByDefault;
    private boolean slidesByDefault;
    private XMLSlideShow slideshow;

    public XSLFPowerPointExtractor(XMLSlideShow slideshow) {
        super(slideshow);
        this.slidesByDefault = true;
        this.notesByDefault = false;
        this.masterByDefault = false;
        this.slideshow = slideshow;
    }

    public XSLFPowerPointExtractor(XSLFSlideShow slideshow) throws XmlException, IOException {
        this(new XMLSlideShow(slideshow.getPackage()));
    }

    public XSLFPowerPointExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XSLFSlideShow(container));
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSLFPowerPointExtractor <filename.pptx>");
            System.exit(1);
        }
        POIXMLTextExtractor extractor = new XSLFPowerPointExtractor(new XSLFSlideShow(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }

    public void setSlidesByDefault(boolean slidesByDefault) {
        this.slidesByDefault = slidesByDefault;
    }

    public void setNotesByDefault(boolean notesByDefault) {
        this.notesByDefault = notesByDefault;
    }

    public void setMasterByDefault(boolean masterByDefault) {
        this.masterByDefault = masterByDefault;
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        return getText(this.slidesByDefault, this.notesByDefault);
    }

    public String getText(boolean slideText, boolean notesText) {
        return getText(slideText, notesText, this.masterByDefault);
    }

    public String getText(boolean slideText, boolean notesText, boolean masterText) {
        StringBuilder text = new StringBuilder();
        for (XSLFSlide slide : this.slideshow.getSlides()) {
            text.append(getText(slide, slideText, notesText, masterText));
        }
        return text.toString();
    }

    public static String getText(XSLFSlide slide, boolean slideText, boolean notesText, boolean masterText) {
        CTCommentAuthor author;
        StringBuilder text = new StringBuilder();
        XSLFCommentAuthors commentAuthors = slide.getSlideShow().getCommentAuthors();
        XSLFNotes notes = slide.getNotes();
        XSLFComments comments = slide.getComments();
        XSLFSlideLayout layout = slide.getSlideLayout();
        XSLFSlideMaster master = layout.getSlideMaster();
        if (slideText) {
            extractText(slide, false, text);
            if (masterText) {
                if (layout == null) {
                    throw new AssertionError();
                }
                extractText(layout, true, text);
                if (master == null) {
                    throw new AssertionError();
                }
                extractText(master, true, text);
            }
            if (comments != null) {
                CTComment[] arr$ = comments.getCTCommentsList().getCmArray();
                for (CTComment comment : arr$) {
                    if (commentAuthors != null && (author = commentAuthors.getAuthorById(comment.getAuthorId())) != null) {
                        text.append(author.getName() + ": ");
                    }
                    text.append(comment.getText());
                    text.append("\n");
                }
            }
        }
        if (notesText && notes != null) {
            extractText(notes, false, text);
        }
        return text.toString();
    }

    private static void extractText(XSLFShapeContainer data, boolean skipPlaceholders, StringBuilder text) {
        Iterator i$ = data.iterator();
        while (i$.hasNext()) {
            Shape shape = (XSLFShape) i$.next();
            if (shape instanceof XSLFShapeContainer) {
                extractText((XSLFShapeContainer) shape, skipPlaceholders, text);
            } else if (shape instanceof XSLFTextShape) {
                XSLFTextShape ts = (XSLFTextShape) shape;
                if (!skipPlaceholders || !ts.isPlaceholder()) {
                    text.append(ts.getText());
                    text.append("\n");
                }
            } else if (shape instanceof XSLFTable) {
                for (XSLFTableRow r : (XSLFTable) shape) {
                    for (XSLFTableCell c : r) {
                        text.append(c.getText());
                        text.append("\t");
                    }
                    text.append("\n");
                }
            }
        }
    }
}
