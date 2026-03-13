package org.apache.poi.xdgf.xml;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xdgf.usermodel.XDGFDocument;

/* JADX INFO: loaded from: classes.dex */
public class XDGFXMLDocumentPart extends POIXMLDocumentPart {
    protected XDGFDocument _document;

    public XDGFXMLDocumentPart(PackagePart part, XDGFDocument document) {
        super(part);
        this._document = document;
    }
}
