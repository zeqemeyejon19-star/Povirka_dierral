package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthorList;
import org.openxmlformats.schemas.presentationml.x2006.main.CmAuthorLstDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSLFCommentAuthors extends POIXMLDocumentPart {
    private final CTCommentAuthorList _authors;

    XSLFCommentAuthors() {
        CmAuthorLstDocument doc = CmAuthorLstDocument.Factory.newInstance();
        this._authors = doc.addNewCmAuthorLst();
    }

    XSLFCommentAuthors(PackagePart part) throws XmlException, IOException {
        super(part);
        CmAuthorLstDocument doc = CmAuthorLstDocument.Factory.parse(getPackagePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this._authors = doc.getCmAuthorLst();
    }

    public CTCommentAuthorList getCTCommentAuthorsList() {
        return this._authors;
    }

    public CTCommentAuthor getAuthorById(long id) {
        CTCommentAuthor[] arr$ = this._authors.getCmAuthorArray();
        for (CTCommentAuthor author : arr$) {
            if (author.getId() == id) {
                return author;
            }
        }
        return null;
    }
}
