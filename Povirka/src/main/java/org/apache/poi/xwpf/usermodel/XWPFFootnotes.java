package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;

/* JADX INFO: loaded from: classes.dex */
public class XWPFFootnotes extends POIXMLDocumentPart {
    private CTFootnotes ctFootnotes;
    protected XWPFDocument document;
    private List<XWPFFootnote> listFootnote;

    public XWPFFootnotes(PackagePart part) throws OpenXML4JException, IOException {
        super(part);
        this.listFootnote = new ArrayList();
    }

    public XWPFFootnotes() {
        this.listFootnote = new ArrayList();
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0044  */
    @Override // org.apache.poi.POIXMLDocumentPart
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void onDocumentRead() throws java.io.IOException {
        /*
            r8 = this;
            r0 = 0
            r1 = 0
            org.apache.poi.openxml4j.opc.PackagePart r2 = r8.getPackagePart()     // Catch: java.lang.Throwable -> L38 org.apache.xmlbeans.XmlException -> L3a
            java.io.InputStream r2 = r2.getInputStream()     // Catch: java.lang.Throwable -> L38 org.apache.xmlbeans.XmlException -> L3a
            r0 = r2
            org.apache.xmlbeans.XmlOptions r2 = org.apache.poi.POIXMLTypeLoader.DEFAULT_XML_OPTIONS     // Catch: java.lang.Throwable -> L38 org.apache.xmlbeans.XmlException -> L3a
            org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument r1 = org.openxmlformats.schemas.wordprocessingml.x2006.main.FootnotesDocument.Factory.parse(r0, r2)     // Catch: java.lang.Throwable -> L38 org.apache.xmlbeans.XmlException -> L3a
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes r2 = r1.getFootnotes()     // Catch: org.apache.xmlbeans.XmlException -> L36 java.lang.Throwable -> L41
            r8.ctFootnotes = r2     // Catch: org.apache.xmlbeans.XmlException -> L36 java.lang.Throwable -> L41
            if (r0 == 0) goto L1c
            r0.close()
        L1c:
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFootnotes r2 = r8.ctFootnotes
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn[] r2 = r2.getFootnoteArray()
            int r3 = r2.length
            r4 = 0
        L24:
            if (r4 >= r3) goto L35
            r5 = r2[r4]
            java.util.List<org.apache.poi.xwpf.usermodel.XWPFFootnote> r6 = r8.listFootnote
            org.apache.poi.xwpf.usermodel.XWPFFootnote r7 = new org.apache.poi.xwpf.usermodel.XWPFFootnote
            r7.<init>(r5, r8)
            r6.add(r7)
            int r4 = r4 + 1
            goto L24
        L35:
            return
        L36:
            r2 = move-exception
            goto L3b
        L38:
            r2 = move-exception
            goto L42
        L3a:
            r2 = move-exception
        L3b:
            org.apache.poi.POIXMLException r3 = new org.apache.poi.POIXMLException     // Catch: java.lang.Throwable -> L41
            r3.<init>()     // Catch: java.lang.Throwable -> L41
            throw r3     // Catch: java.lang.Throwable -> L41
        L41:
            r2 = move-exception
        L42:
            if (r0 == 0) goto L47
            r0.close()
        L47:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.poi.xwpf.usermodel.XWPFFootnotes.onDocumentRead():void");
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTFootnotes.type.getName().getNamespaceURI(), "footnotes"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.ctFootnotes.save(out, xmlOptions);
        out.close();
    }

    public List<XWPFFootnote> getFootnotesList() {
        return this.listFootnote;
    }

    public XWPFFootnote getFootnoteById(int id) {
        for (XWPFFootnote note : this.listFootnote) {
            if (note.getCTFtnEdn().getId().intValue() == id) {
                return note;
            }
        }
        return null;
    }

    public void setFootnotes(CTFootnotes footnotes) {
        this.ctFootnotes = footnotes;
    }

    public void addFootnote(XWPFFootnote footnote) {
        this.listFootnote.add(footnote);
        this.ctFootnotes.addNewFootnote().set(footnote.getCTFtnEdn());
    }

    public XWPFFootnote addFootnote(CTFtnEdn note) {
        CTFtnEdn newNote = this.ctFootnotes.addNewFootnote();
        newNote.set(note);
        XWPFFootnote xNote = new XWPFFootnote(newNote, this);
        this.listFootnote.add(xNote);
        return xNote;
    }

    public XWPFDocument getXWPFDocument() {
        XWPFDocument xWPFDocument = this.document;
        if (xWPFDocument != null) {
            return xWPFDocument;
        }
        return (XWPFDocument) getParent();
    }

    public void setXWPFDocument(XWPFDocument doc) {
        this.document = doc;
    }
}
