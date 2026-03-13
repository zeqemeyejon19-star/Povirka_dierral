package org.apache.poi.openxml4j.opc;

import java.net.URI;
import java.net.URISyntaxException;

/* JADX INFO: loaded from: classes.dex */
public final class PackageRelationship {
    public static final String ID_ATTRIBUTE_NAME = "Id";
    public static final String RELATIONSHIPS_TAG_NAME = "Relationships";
    public static final String RELATIONSHIP_TAG_NAME = "Relationship";
    public static final String TARGET_ATTRIBUTE_NAME = "Target";
    public static final String TARGET_MODE_ATTRIBUTE_NAME = "TargetMode";
    public static final String TYPE_ATTRIBUTE_NAME = "Type";
    private static URI containerRelationshipPart;
    private OPCPackage container;
    private String id;
    private String relationshipType;
    private PackagePart source;
    private TargetMode targetMode;
    private URI targetUri;

    static {
        try {
            containerRelationshipPart = new URI("/_rels/.rels");
        } catch (URISyntaxException e) {
        }
    }

    public PackageRelationship(OPCPackage pkg, PackagePart sourcePart, URI targetUri, TargetMode targetMode, String relationshipType, String id) {
        if (pkg == null) {
            throw new IllegalArgumentException("pkg");
        }
        if (targetUri == null) {
            throw new IllegalArgumentException("targetUri");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        this.container = pkg;
        this.source = sourcePart;
        this.targetUri = targetUri;
        this.targetMode = targetMode;
        this.relationshipType = relationshipType;
        this.id = id;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PackageRelationship)) {
            return false;
        }
        PackageRelationship rel = (PackageRelationship) obj;
        if (!this.id.equals(rel.id) || !this.relationshipType.equals(rel.relationshipType)) {
            return false;
        }
        PackagePart packagePart = rel.source;
        return (packagePart == null || packagePart.equals(this.source)) && this.targetMode == rel.targetMode && this.targetUri.equals(rel.targetUri);
    }

    public int hashCode() {
        int iHashCode = this.id.hashCode() + this.relationshipType.hashCode();
        PackagePart packagePart = this.source;
        return iHashCode + (packagePart == null ? 0 : packagePart.hashCode()) + this.targetMode.hashCode() + this.targetUri.hashCode();
    }

    public static URI getContainerPartRelationship() {
        return containerRelationshipPart;
    }

    public OPCPackage getPackage() {
        return this.container;
    }

    public String getId() {
        return this.id;
    }

    public String getRelationshipType() {
        return this.relationshipType;
    }

    public PackagePart getSource() {
        return this.source;
    }

    public URI getSourceURI() {
        PackagePart packagePart = this.source;
        if (packagePart == null) {
            return PackagingURIHelper.PACKAGE_ROOT_URI;
        }
        return packagePart._partName.getURI();
    }

    public TargetMode getTargetMode() {
        return this.targetMode;
    }

    public URI getTargetURI() {
        if (this.targetMode == TargetMode.EXTERNAL) {
            return this.targetUri;
        }
        if (!this.targetUri.toASCIIString().startsWith("/")) {
            return PackagingURIHelper.resolvePartUri(getSourceURI(), this.targetUri);
        }
        return this.targetUri;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id == null ? "id=null" : "id=" + this.id);
        sb.append(this.container == null ? " - container=null" : " - container=" + this.container);
        sb.append(this.relationshipType == null ? " - relationshipType=null" : " - relationshipType=" + this.relationshipType);
        sb.append(this.source == null ? " - source=null" : " - source=" + getSourceURI().toASCIIString());
        sb.append(this.targetUri == null ? " - target=null" : " - target=" + getTargetURI().toASCIIString());
        sb.append(this.targetMode == null ? ",targetMode=null" : ",targetMode=" + this.targetMode);
        return sb.toString();
    }
}
