package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;

/* JADX INFO: loaded from: classes.dex */
public class XWPFAbstractNum {
    private CTAbstractNum ctAbstractNum;
    protected XWPFNumbering numbering;

    protected XWPFAbstractNum() {
        this.ctAbstractNum = null;
        this.numbering = null;
    }

    public XWPFAbstractNum(CTAbstractNum abstractNum) {
        this.ctAbstractNum = abstractNum;
    }

    public XWPFAbstractNum(CTAbstractNum ctAbstractNum, XWPFNumbering numbering) {
        this.ctAbstractNum = ctAbstractNum;
        this.numbering = numbering;
    }

    public CTAbstractNum getAbstractNum() {
        return this.ctAbstractNum;
    }

    public XWPFNumbering getNumbering() {
        return this.numbering;
    }

    public void setNumbering(XWPFNumbering numbering) {
        this.numbering = numbering;
    }

    public CTAbstractNum getCTAbstractNum() {
        return this.ctAbstractNum;
    }
}
