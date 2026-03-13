package org.apache.poi.openxml4j.opc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.internal.ContentType;

/* JADX INFO: loaded from: classes.dex */
public abstract class PackagePart implements RelationshipSource, Comparable<PackagePart> {
    protected OPCPackage _container;
    protected ContentType _contentType;
    private boolean _isDeleted;
    private boolean _isRelationshipPart;
    protected PackagePartName _partName;
    private PackageRelationshipCollection _relationships;

    public abstract void close();

    public abstract void flush();

    protected abstract InputStream getInputStreamImpl() throws IOException;

    protected abstract OutputStream getOutputStreamImpl();

    public abstract boolean load(InputStream inputStream) throws InvalidFormatException;

    public abstract boolean save(OutputStream outputStream) throws OpenXML4JException;

    protected PackagePart(OPCPackage pack, PackagePartName partName, ContentType contentType) throws InvalidFormatException {
        this(pack, partName, contentType, true);
    }

    protected PackagePart(OPCPackage pack, PackagePartName partName, ContentType contentType, boolean loadRelationships) throws InvalidFormatException {
        this._partName = partName;
        this._contentType = contentType;
        this._container = pack;
        this._isRelationshipPart = partName.isRelationshipPartURI();
        if (loadRelationships) {
            loadRelationships();
        }
    }

    public PackagePart(OPCPackage pack, PackagePartName partName, String contentType) throws InvalidFormatException {
        this(pack, partName, new ContentType(contentType));
    }

    public PackageRelationship findExistingRelation(PackagePart packagePart) {
        return this._relationships.findExistingInternalRelation(packagePart);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addExternalRelationship(String target, String relationshipType) {
        return addExternalRelationship(target, relationshipType, null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addExternalRelationship(String target, String relationshipType, String id) {
        if (target == null) {
            throw new IllegalArgumentException("target is null for type " + relationshipType);
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        try {
            URI targetURI = new URI(target);
            return this._relationships.addRelationship(targetURI, TargetMode.EXTERNAL, relationshipType, id);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addRelationship(PackagePartName targetPartName, TargetMode targetMode, String relationshipType) {
        return addRelationship(targetPartName, targetMode, relationshipType, (String) null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addRelationship(PackagePartName targetPartName, TargetMode targetMode, String relationshipType, String id) {
        this._container.throwExceptionIfReadOnly();
        if (targetPartName == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._isRelationshipPart || targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        return this._relationships.addRelationship(targetPartName.getURI(), targetMode, relationshipType, id);
    }

    public PackageRelationship addRelationship(URI targetURI, TargetMode targetMode, String relationshipType) {
        return addRelationship(targetURI, targetMode, relationshipType, (String) null);
    }

    public PackageRelationship addRelationship(URI targetURI, TargetMode targetMode, String relationshipType, String id) {
        this._container.throwExceptionIfReadOnly();
        if (targetURI == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._isRelationshipPart || PackagingURIHelper.isRelationshipPartURI(targetURI)) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        return this._relationships.addRelationship(targetURI, targetMode, relationshipType, id);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public void clearRelationships() {
        PackageRelationshipCollection packageRelationshipCollection = this._relationships;
        if (packageRelationshipCollection != null) {
            packageRelationshipCollection.clear();
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public void removeRelationship(String id) {
        this._container.throwExceptionIfReadOnly();
        PackageRelationshipCollection packageRelationshipCollection = this._relationships;
        if (packageRelationshipCollection != null) {
            packageRelationshipCollection.removeRelationship(id);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationshipCollection getRelationships() throws InvalidFormatException {
        return getRelationshipsCore(null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship getRelationship(String id) {
        return this._relationships.getRelationshipByID(id);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationshipCollection getRelationshipsByType(String relationshipType) throws InvalidFormatException {
        this._container.throwExceptionIfWriteOnly();
        return getRelationshipsCore(relationshipType);
    }

    private PackageRelationshipCollection getRelationshipsCore(String filter) throws InvalidFormatException {
        this._container.throwExceptionIfWriteOnly();
        if (this._relationships == null) {
            throwExceptionIfRelationship();
            this._relationships = new PackageRelationshipCollection(this);
        }
        return new PackageRelationshipCollection(this._relationships, filter);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public boolean hasRelationships() {
        PackageRelationshipCollection packageRelationshipCollection;
        return (this._isRelationshipPart || (packageRelationshipCollection = this._relationships) == null || packageRelationshipCollection.size() <= 0) ? false : true;
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public boolean isRelationshipExists(PackageRelationship rel) {
        return this._relationships.getRelationshipByID(rel.getId()) != null;
    }

    public PackagePart getRelatedPart(PackageRelationship rel) throws InvalidFormatException {
        if (!isRelationshipExists(rel)) {
            throw new IllegalArgumentException("Relationship " + rel + " doesn't start with this part " + this._partName);
        }
        URI target = rel.getTargetURI();
        if (target.getFragment() != null) {
            String t = target.toString();
            try {
                target = new URI(t.substring(0, t.indexOf(35)));
            } catch (URISyntaxException e) {
                throw new InvalidFormatException("Invalid target URI: " + target);
            }
        }
        PackagePartName relName = PackagingURIHelper.createPartName(target);
        PackagePart part = this._container.getPart(relName);
        if (part == null) {
            throw new IllegalArgumentException("No part found for relationship " + rel);
        }
        return part;
    }

    public InputStream getInputStream() throws IOException {
        InputStream inStream = getInputStreamImpl();
        if (inStream == null) {
            throw new IOException("Can't obtain the input stream from " + this._partName.getName());
        }
        return inStream;
    }

    public OutputStream getOutputStream() {
        if (this instanceof ZipPackagePart) {
            this._container.removePart(this._partName);
            PackagePart part = this._container.createPart(this._partName, this._contentType.toString(), false);
            if (part == null) {
                throw new InvalidOperationException("Can't create a temporary part !");
            }
            part._relationships = this._relationships;
            OutputStream outStream = part.getOutputStreamImpl();
            return outStream;
        }
        OutputStream outStream2 = getOutputStreamImpl();
        return outStream2;
    }

    private void throwExceptionIfRelationship() throws InvalidOperationException {
        if (this._isRelationshipPart) {
            throw new InvalidOperationException("Can do this operation on a relationship part !");
        }
    }

    private void loadRelationships() throws InvalidFormatException {
        if (this._relationships == null && !this._isRelationshipPart) {
            throwExceptionIfRelationship();
            this._relationships = new PackageRelationshipCollection(this);
        }
    }

    public PackagePartName getPartName() {
        return this._partName;
    }

    public String getContentType() {
        return this._contentType.toString();
    }

    public ContentType getContentTypeDetails() {
        return this._contentType;
    }

    public void setContentType(String contentType) throws InvalidFormatException {
        OPCPackage oPCPackage = this._container;
        if (oPCPackage == null) {
            this._contentType = new ContentType(contentType);
            return;
        }
        oPCPackage.unregisterPartAndContentType(this._partName);
        this._contentType = new ContentType(contentType);
        this._container.registerPartAndContentType(this);
    }

    public OPCPackage getPackage() {
        return this._container;
    }

    public boolean isRelationshipPart() {
        return this._isRelationshipPart;
    }

    public boolean isDeleted() {
        return this._isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this._isDeleted = isDeleted;
    }

    public long getSize() {
        return -1L;
    }

    public String toString() {
        return "Name: " + this._partName + " - Content Type: " + this._contentType;
    }

    @Override // java.lang.Comparable
    public int compareTo(PackagePart other) {
        if (other == null) {
            return -1;
        }
        return PackagePartName.compare(this._partName, other._partName);
    }

    public void clear() {
    }
}
