package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class XDGFStyleSheet extends XDGFSheet {
    public XDGFStyleSheet(StyleSheetType styleSheet, XDGFDocument document) {
        super(styleSheet, document);
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFSheet
    @Internal
    /* JADX INFO: renamed from: getXmlObject, reason: merged with bridge method [inline-methods] */
    public StyleSheetType mo24getXmlObject() {
        return this._sheet;
    }
}
