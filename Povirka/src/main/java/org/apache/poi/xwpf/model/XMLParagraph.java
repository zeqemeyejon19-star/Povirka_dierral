package org.apache.poi.xwpf.model;

import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
@Deprecated
public class XMLParagraph {
    protected CTP paragraph;

    public XMLParagraph(CTP paragraph) {
        this.paragraph = paragraph;
    }

    public CTP getCTP() {
        return this.paragraph;
    }
}
