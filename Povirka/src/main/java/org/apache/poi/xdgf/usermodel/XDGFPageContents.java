package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.PageContentsDocument;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.xmlbeans.XmlException;

/* JADX INFO: loaded from: classes.dex */
public class XDGFPageContents extends XDGFBaseContents {
    protected Map<Long, XDGFMaster> _masters;
    protected XDGFPage _page;

    public XDGFPageContents(PackagePart part, XDGFDocument document) {
        super(part, document);
        this._masters = new HashMap();
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFBaseContents, org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() {
        try {
            try {
                this._pageContents = PageContentsDocument.Factory.parse(getPackagePart().getInputStream()).getPageContents();
                for (POIXMLDocumentPart part : getRelations()) {
                    if (part instanceof XDGFMasterContents) {
                        XDGFMaster master = ((XDGFMasterContents) part).getMaster();
                        this._masters.put(Long.valueOf(master.getID()), master);
                    }
                }
                super.onDocumentRead();
                for (XDGFShape shape : this._shapes.values()) {
                    if (shape.isTopmost()) {
                        shape.setupMaster(this, null);
                    }
                }
            } catch (IOException e) {
                throw new POIXMLException(e);
            } catch (XmlException e2) {
                throw new POIXMLException((Throwable) e2);
            }
        } catch (POIXMLException e3) {
            throw XDGFException.wrap(this, e3);
        }
    }

    public XDGFPage getPage() {
        return this._page;
    }

    protected void setPage(XDGFPage page) {
        this._page = page;
    }

    public XDGFMaster getMasterById(long id) {
        return this._masters.get(Long.valueOf(id));
    }
}
