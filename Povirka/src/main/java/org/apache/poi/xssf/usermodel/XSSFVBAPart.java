package org.apache.poi.xssf.usermodel;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.opc.PackagePart;

/* JADX INFO: loaded from: classes.dex */
public class XSSFVBAPart extends POIXMLDocumentPart {
    protected XSSFVBAPart() {
    }

    protected XSSFVBAPart(PackagePart part) {
        super(part);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void prepareForCommit() {
    }
}
