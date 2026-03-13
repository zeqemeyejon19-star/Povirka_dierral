package org.apache.poi;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.xmlbeans.impl.common.SystemCache;

/* JADX INFO: loaded from: classes.dex */
public abstract class POIXMLDocument extends POIXMLDocumentPart implements Closeable {
    public static final String DOCUMENT_CREATOR = "Apache POI";
    public static final String OLE_OBJECT_REL_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject";
    public static final String PACK_OBJECT_REL_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package";
    private OPCPackage pkg;
    private POIXMLProperties properties;

    public abstract List<PackagePart> getAllEmbedds() throws OpenXML4JException;

    protected POIXMLDocument(OPCPackage pkg) {
        super(pkg);
        init(pkg);
    }

    protected POIXMLDocument(OPCPackage pkg, String coreDocumentRel) {
        super(pkg, coreDocumentRel);
        init(pkg);
    }

    private void init(OPCPackage p) {
        this.pkg = p;
        SystemCache.get().setSaxLoader((Object) null);
    }

    public static OPCPackage openPackage(String path) throws IOException {
        try {
            return OPCPackage.open(path);
        } catch (InvalidFormatException e) {
            throw new IOException(e.toString(), e);
        }
    }

    public OPCPackage getPackage() {
        return this.pkg;
    }

    protected PackagePart getCorePart() {
        return getPackagePart();
    }

    protected PackagePart[] getRelatedByType(String contentType) throws InvalidFormatException {
        PackageRelationshipCollection partsC = getPackagePart().getRelationshipsByType(contentType);
        PackagePart[] parts = new PackagePart[partsC.size()];
        int count = 0;
        for (PackageRelationship rel : partsC) {
            parts[count] = getPackagePart().getRelatedPart(rel);
            count++;
        }
        return parts;
    }

    public POIXMLProperties getProperties() {
        if (this.properties == null) {
            try {
                this.properties = new POIXMLProperties(this.pkg);
            } catch (Exception e) {
                throw new POIXMLException(e);
            }
        }
        return this.properties;
    }

    protected final void load(POIXMLFactory factory) throws IOException {
        Map<PackagePart, POIXMLDocumentPart> context = new HashMap<>();
        try {
            read(factory, context);
            onDocumentRead();
            context.clear();
        } catch (OpenXML4JException e) {
            throw new POIXMLException(e);
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        OPCPackage oPCPackage = this.pkg;
        if (oPCPackage != null) {
            if (oPCPackage.getPackageAccess() == PackageAccess.READ) {
                this.pkg.revert();
            } else {
                this.pkg.close();
            }
            this.pkg = null;
        }
    }

    public final void write(OutputStream stream) throws IOException {
        OPCPackage p = getPackage();
        if (p == null) {
            throw new IOException("Cannot write data, document seems to have been closed already");
        }
        Set<PackagePart> context = new HashSet<>();
        onSave(context);
        context.clear();
        getProperties().commit();
        p.save(stream);
    }
}
