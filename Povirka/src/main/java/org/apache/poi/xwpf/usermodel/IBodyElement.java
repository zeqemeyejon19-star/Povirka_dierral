package org.apache.poi.xwpf.usermodel;

import org.apache.poi.POIXMLDocumentPart;

/* JADX INFO: loaded from: classes.dex */
public interface IBodyElement {
    IBody getBody();

    BodyElementType getElementType();

    POIXMLDocumentPart getPart();

    BodyType getPartType();
}
