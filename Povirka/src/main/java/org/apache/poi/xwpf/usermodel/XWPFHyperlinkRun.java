package org.apache.poi.xwpf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

/* JADX INFO: loaded from: classes.dex */
public class XWPFHyperlinkRun extends XWPFRun {
    private CTHyperlink hyperlink;

    public XWPFHyperlinkRun(CTHyperlink hyperlink, CTR run, IRunBody p) {
        super(run, p);
        this.hyperlink = hyperlink;
    }

    @Internal
    public CTHyperlink getCTHyperlink() {
        return this.hyperlink;
    }

    public String getAnchor() {
        return this.hyperlink.getAnchor();
    }

    public String getHyperlinkId() {
        return this.hyperlink.getId();
    }

    public void setHyperlinkId(String id) {
        this.hyperlink.setId(id);
    }

    public XWPFHyperlink getHyperlink(XWPFDocument document) {
        String id = getHyperlinkId();
        if (id == null) {
            return null;
        }
        return document.getHyperlinkByID(id);
    }
}
