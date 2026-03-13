package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.MasterContentsDocument;
import java.io.IOException;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.xmlbeans.XmlException;

/* JADX INFO: loaded from: classes.dex */
public class XDGFMasterContents extends XDGFBaseContents {
    protected XDGFMaster _master;

    public XDGFMasterContents(PackagePart part, XDGFDocument document) {
        super(part, document);
    }

    @Override // org.apache.poi.xdgf.usermodel.XDGFBaseContents, org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() {
        try {
            try {
                this._pageContents = MasterContentsDocument.Factory.parse(getPackagePart().getInputStream()).getMasterContents();
                super.onDocumentRead();
            } catch (IOException e) {
                throw new POIXMLException(e);
            } catch (XmlException e2) {
                throw new POIXMLException((Throwable) e2);
            }
        } catch (POIXMLException e3) {
            throw XDGFException.wrap(this, e3);
        }
    }

    public XDGFMaster getMaster() {
        return this._master;
    }

    protected void setMaster(XDGFMaster master) {
        this._master = master;
    }
}
