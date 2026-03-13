package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesMaster;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesMasterDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSLFNotesMaster extends XSLFSheet implements MasterSheet<XSLFShape, XSLFTextParagraph> {
    private CTNotesMaster _slide;
    private XSLFTheme _theme;

    XSLFNotesMaster() {
        this._slide = prototype();
    }

    protected XSLFNotesMaster(PackagePart part) throws XmlException, IOException {
        super(part);
        NotesMasterDocument doc = NotesMasterDocument.Factory.parse(getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        CTNotesMaster notesMaster = doc.getNotesMaster();
        this._slide = notesMaster;
        setCommonSlideData(notesMaster.getCSld());
    }

    private static CTNotesMaster prototype() {
        InputStream is = XSLFNotesMaster.class.getResourceAsStream("notesMaster.xml");
        try {
            if (is == null) {
                throw new POIXMLException("Missing resource 'notesMaster.xml'");
            }
            try {
                NotesMasterDocument doc = NotesMasterDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                CTNotesMaster slide = doc.getNotesMaster();
                return slide;
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new POIXMLException("Can't initialize NotesMaster", e);
        }
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public CTNotesMaster getXmlObject() {
        return this._slide;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    protected String getRootElementName() {
        return "notesMaster";
    }

    @Override // org.apache.poi.sl.usermodel.Sheet
    public MasterSheet<XSLFShape, XSLFTextParagraph> getMasterSheet() {
        return null;
    }

    @Override // org.apache.poi.xslf.usermodel.XSLFSheet
    public XSLFTheme getTheme() {
        if (this._theme == null) {
            Iterator<POIXMLDocumentPart> it = getRelations().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                POIXMLDocumentPart p = it.next();
                if (p instanceof XSLFTheme) {
                    this._theme = (XSLFTheme) p;
                    CTColorMapping cmap = this._slide.getClrMap();
                    if (cmap != null) {
                        this._theme.initColorMap(cmap);
                    }
                }
            }
        }
        return this._theme;
    }
}
