package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.MasterType;
import com.microsoft.schemas.office.visio.x2012.main.MastersDocument;
import com.microsoft.schemas.office.visio.x2012.main.MastersType;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.xdgf.xml.XDGFXMLDocumentPart;
import org.apache.xmlbeans.XmlException;

/* JADX INFO: loaded from: classes.dex */
public class XDGFMasters extends XDGFXMLDocumentPart {
    protected Map<Long, XDGFMaster> _masters;
    MastersType _mastersObject;

    public XDGFMasters(PackagePart part, XDGFDocument document) {
        super(part, document);
        this._masters = new HashMap();
    }

    @Internal
    protected MastersType getXmlObject() {
        return this._mastersObject;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() {
        try {
            try {
                try {
                    this._mastersObject = MastersDocument.Factory.parse(getPackagePart().getInputStream()).getMasters();
                    Map<String, MasterType> masterSettings = new HashMap<>();
                    MasterType[] arr$ = this._mastersObject.getMasterArray();
                    for (MasterType master : arr$) {
                        masterSettings.put(master.getRel().getId(), master);
                    }
                    for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
                        POIXMLDocumentPart part = rp.getDocumentPart();
                        String relId = rp.getRelationship().getId();
                        MasterType settings = masterSettings.get(relId);
                        if (settings == null) {
                            throw new POIXMLException("Master relationship for " + relId + " not found");
                        }
                        if (!(part instanceof XDGFMasterContents)) {
                            throw new POIXMLException("Unexpected masters relationship for " + relId + ": " + part);
                        }
                        XDGFMasterContents contents = (XDGFMasterContents) part;
                        contents.onDocumentRead();
                        XDGFMaster master2 = new XDGFMaster(settings, contents, this._document);
                        this._masters.put(Long.valueOf(master2.getID()), master2);
                    }
                } catch (IOException e) {
                    throw new POIXMLException(e);
                }
            } catch (XmlException e2) {
                throw new POIXMLException((Throwable) e2);
            }
        } catch (POIXMLException e3) {
            throw XDGFException.wrap(this, e3);
        }
    }

    public Collection<XDGFMaster> getMastersList() {
        return Collections.unmodifiableCollection(this._masters.values());
    }

    public XDGFMaster getMasterById(long masterId) {
        return this._masters.get(Long.valueOf(masterId));
    }
}
