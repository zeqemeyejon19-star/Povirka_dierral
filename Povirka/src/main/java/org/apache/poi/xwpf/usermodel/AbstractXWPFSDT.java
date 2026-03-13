package org.apache.poi.xwpf.usermodel;

import org.apache.poi.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractXWPFSDT implements ISDTContents {
    private final IBody part;
    private final String tag;
    private final String title;

    public abstract ISDTContent getContent();

    public AbstractXWPFSDT(CTSdtPr pr, IBody part) {
        if (pr == null) {
            this.title = "";
            this.tag = "";
        } else {
            CTString[] aliases = pr.getAliasArray();
            if (aliases != null && aliases.length > 0) {
                this.title = aliases[0].getVal();
            } else {
                this.title = "";
            }
            CTString[] tags = pr.getTagArray();
            if (tags != null && tags.length > 0) {
                this.tag = tags[0].getVal();
            } else {
                this.tag = "";
            }
        }
        this.part = part;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTag() {
        return this.tag;
    }

    public IBody getBody() {
        return null;
    }

    public POIXMLDocumentPart getPart() {
        return this.part.getPart();
    }

    public BodyType getPartType() {
        return BodyType.CONTENTCONTROL;
    }

    public BodyElementType getElementType() {
        return BodyElementType.CONTENTCONTROL;
    }

    public XWPFDocument getDocument() {
        return this.part.getXWPFDocument();
    }
}
