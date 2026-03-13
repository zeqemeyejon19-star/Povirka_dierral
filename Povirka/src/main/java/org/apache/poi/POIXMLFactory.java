package org.apache.poi;

import java.lang.reflect.InvocationTargetException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public abstract class POIXMLFactory {
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) POIXMLFactory.class);
    private static final Class<?>[] PARENT_PART = {POIXMLDocumentPart.class, PackagePart.class};
    private static final Class<?>[] ORPHAN_PART = {PackagePart.class};

    protected abstract POIXMLDocumentPart createDocumentPart(Class<? extends POIXMLDocumentPart> cls, Class<?>[] clsArr, Object[] objArr) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, InvocationTargetException;

    protected abstract POIXMLRelation getDescriptor(String str);

    public POIXMLDocumentPart createDocumentPart(POIXMLDocumentPart parent, PackagePart part) {
        PackageRelationship rel = getPackageRelationship(parent, part);
        POIXMLRelation descriptor = getDescriptor(rel.getRelationshipType());
        if (descriptor == null || descriptor.getRelationClass() == null) {
            LOGGER.log(1, "using default POIXMLDocumentPart for " + rel.getRelationshipType());
            return new POIXMLDocumentPart(parent, part);
        }
        Class<? extends POIXMLDocumentPart> cls = descriptor.getRelationClass();
        try {
            try {
                return createDocumentPart(cls, PARENT_PART, new Object[]{parent, part});
            } catch (NoSuchMethodException e) {
                return createDocumentPart(cls, ORPHAN_PART, new Object[]{part});
            }
        } catch (Exception e2) {
            throw new POIXMLException(e2);
        }
    }

    public POIXMLDocumentPart newDocumentPart(POIXMLRelation descriptor) {
        Class<? extends POIXMLDocumentPart> cls = descriptor.getRelationClass();
        try {
            return createDocumentPart(cls, null, null);
        } catch (Exception e) {
            throw new POIXMLException(e);
        }
    }

    protected PackageRelationship getPackageRelationship(POIXMLDocumentPart parent, PackagePart part) {
        try {
            String partName = part.getPartName().getName();
            for (PackageRelationship pr : parent.getPackagePart().getRelationships()) {
                String packName = pr.getTargetURI().toASCIIString();
                if (packName.equalsIgnoreCase(partName)) {
                    return pr;
                }
            }
            throw new POIXMLException("package part isn't a child of the parent document.");
        } catch (InvalidFormatException e) {
            throw new POIXMLException("error while determining package relations", e);
        }
    }
}
