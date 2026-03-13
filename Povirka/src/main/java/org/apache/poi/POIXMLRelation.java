package org.apache.poi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public abstract class POIXMLRelation {
    private static final POILogger log = POILogFactory.getLogger((Class<?>) POIXMLRelation.class);
    private Class<? extends POIXMLDocumentPart> _cls;
    private String _defaultName;
    private String _relation;
    private String _type;

    public POIXMLRelation(String type, String rel, String defaultName, Class<? extends POIXMLDocumentPart> cls) {
        this._type = type;
        this._relation = rel;
        this._defaultName = defaultName;
        this._cls = cls;
    }

    public POIXMLRelation(String type, String rel, String defaultName) {
        this(type, rel, defaultName, null);
    }

    public String getContentType() {
        return this._type;
    }

    public String getRelation() {
        return this._relation;
    }

    public String getDefaultFileName() {
        return this._defaultName;
    }

    public String getFileName(int index) {
        if (!this._defaultName.contains("#")) {
            return getDefaultFileName();
        }
        return this._defaultName.replace("#", Integer.toString(index));
    }

    public Integer getFileNameIndex(POIXMLDocumentPart part) {
        String regex = this._defaultName.replace("#", "(\\d+)");
        return Integer.valueOf(part.getPackagePart().getPartName().getName().replaceAll(regex, "$1"));
    }

    public Class<? extends POIXMLDocumentPart> getRelationClass() {
        return this._cls;
    }

    public InputStream getContents(PackagePart corePart) throws InvalidFormatException, IOException {
        PackageRelationshipCollection prc = corePart.getRelationshipsByType(getRelation());
        Iterator<PackageRelationship> it = prc.iterator();
        if (it.hasNext()) {
            PackageRelationship rel = it.next();
            PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
            PackagePart part = corePart.getPackage().getPart(relName);
            return part.getInputStream();
        }
        log.log(5, "No part " + getDefaultFileName() + " found");
        return null;
    }
}
