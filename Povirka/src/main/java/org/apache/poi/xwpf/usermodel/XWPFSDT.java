package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;

/* JADX INFO: loaded from: classes.dex */
public class XWPFSDT extends AbstractXWPFSDT implements IBodyElement, IRunBody, ISDTContents, IRunElement {
    private final ISDTContent content;

    public XWPFSDT(CTSdtRun sdtRun, IBody part) {
        super(sdtRun.getSdtPr(), part);
        this.content = new XWPFSDTContent(sdtRun.getSdtContent(), part, this);
    }

    public XWPFSDT(CTSdtBlock block, IBody part) {
        super(block.getSdtPr(), part);
        this.content = new XWPFSDTContent(block.getSdtContent(), part, this);
    }

    @Override // org.apache.poi.xwpf.usermodel.AbstractXWPFSDT
    public ISDTContent getContent() {
        return this.content;
    }
}
