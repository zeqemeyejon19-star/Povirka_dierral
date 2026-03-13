package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;

/* JADX INFO: loaded from: classes.dex */
public class XDGFPageSheet extends XDGFSheet {
    PageSheetType _pageSheet;

    public XDGFPageSheet(PageSheetType sheet, XDGFDocument document) {
        super(sheet, document);
        this._pageSheet = sheet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    /* JADX INFO: renamed from: getXmlObject, reason: merged with bridge method [inline-methods] */
    public PageSheetType mo24getXmlObject() {
        return this._pageSheet;
    }
}
