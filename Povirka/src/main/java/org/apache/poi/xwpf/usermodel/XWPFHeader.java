package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.HdrDocument;

/* JADX INFO: loaded from: classes.dex */
public class XWPFHeader extends XWPFHeaderFooter {
    public XWPFHeader() {
    }

    public XWPFHeader(POIXMLDocumentPart parent, PackagePart part) throws IOException {
        super(parent, part);
    }

    public XWPFHeader(XWPFDocument doc, CTHdrFtr hdrFtr) {
        super(doc, hdrFtr);
        XmlCursor cursor = this.headerFooter.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            CTP object = cursor.getObject();
            if (object instanceof CTP) {
                XWPFParagraph p = new XWPFParagraph(object, this);
                this.paragraphs.add(p);
            }
            if (object instanceof CTTbl) {
                XWPFTable t = new XWPFTable((CTTbl) object, this);
                this.tables.add(t);
            }
        }
        cursor.dispose();
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTNumbering.type.getName().getNamespaceURI(), "hdr"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        super._getHdrFtr().save(out, xmlOptions);
        out.close();
    }

    @Override // org.apache.poi.xwpf.usermodel.XWPFHeaderFooter, org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
        InputStream is = null;
        try {
            try {
                is = getPackagePart().getInputStream();
                HdrDocument hdrDocument = HdrDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                this.headerFooter = hdrDocument.getHdr();
                XmlCursor cursor = this.headerFooter.newCursor();
                cursor.selectPath("./*");
                while (cursor.toNextSelection()) {
                    CTP object = cursor.getObject();
                    if (object instanceof CTP) {
                        XWPFParagraph p = new XWPFParagraph(object, this);
                        this.paragraphs.add(p);
                        this.bodyElements.add(p);
                    }
                    if (object instanceof CTTbl) {
                        XWPFTable t = new XWPFTable((CTTbl) object, this);
                        this.tables.add(t);
                        this.bodyElements.add(t);
                    }
                    if (object instanceof CTSdtBlock) {
                        XWPFSDT c = new XWPFSDT((CTSdtBlock) object, this);
                        this.bodyElements.add(c);
                    }
                }
                cursor.dispose();
            } catch (XmlException e) {
                throw new POIXMLException((Throwable) e);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public BodyType getPartType() {
        return BodyType.HEADER;
    }
}
