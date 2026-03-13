package org.apache.poi.openxml4j.opc.internal.marshallers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackageNamespaces;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public final class ZipPartMarshaller implements PartMarshaller {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) ZipPartMarshaller.class);

    @Override // org.apache.poi.openxml4j.opc.internal.PartMarshaller
    public boolean marshall(PackagePart part, OutputStream os) throws OpenXML4JException {
        int resultRead;
        if (!(os instanceof ZipOutputStream)) {
            logger.log(7, "Unexpected class " + os.getClass().getName());
            throw new OpenXML4JException("ZipOutputStream expected !");
        }
        if (part.getSize() == 0 && part.getPartName().getName().equals(XSSFRelation.SHARED_STRINGS.getDefaultFileName())) {
            return true;
        }
        ZipOutputStream zos = (ZipOutputStream) os;
        ZipEntry partEntry = new ZipEntry(ZipHelper.getZipItemNameFromOPCName(part.getPartName().getURI().getPath()));
        try {
            zos.putNextEntry(partEntry);
            InputStream ins = part.getInputStream();
            byte[] buff = new byte[8192];
            while (ins.available() > 0 && (resultRead = ins.read(buff)) != -1) {
                zos.write(buff, 0, resultRead);
            }
            zos.closeEntry();
            if (part.hasRelationships()) {
                PackagePartName relationshipPartName = PackagingURIHelper.getRelationshipPartName(part.getPartName());
                marshallRelationshipPart(part.getRelationships(), relationshipPartName, zos);
            }
            return true;
        } catch (IOException ioe) {
            logger.log(7, "Cannot write: " + part.getPartName() + ": in ZIP", ioe);
            return false;
        }
    }

    public static boolean marshallRelationshipPart(PackageRelationshipCollection rels, PackagePartName relPartName, ZipOutputStream zos) {
        String targetValue;
        Document xmlOutDoc = DocumentHelper.createDocument();
        Element root = xmlOutDoc.createElementNS(PackageNamespaces.RELATIONSHIPS, PackageRelationship.RELATIONSHIPS_TAG_NAME);
        xmlOutDoc.appendChild(root);
        URI sourcePartURI = PackagingURIHelper.getSourcePartUriFromRelationshipPartUri(relPartName.getURI());
        for (PackageRelationship rel : rels) {
            Element relElem = xmlOutDoc.createElementNS(PackageNamespaces.RELATIONSHIPS, PackageRelationship.RELATIONSHIP_TAG_NAME);
            root.appendChild(relElem);
            relElem.setAttribute(PackageRelationship.ID_ATTRIBUTE_NAME, rel.getId());
            relElem.setAttribute(PackageRelationship.TYPE_ATTRIBUTE_NAME, rel.getRelationshipType());
            URI uri = rel.getTargetURI();
            if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                targetValue = uri.toString();
                relElem.setAttribute(PackageRelationship.TARGET_MODE_ATTRIBUTE_NAME, "External");
            } else {
                URI targetURI = rel.getTargetURI();
                targetValue = PackagingURIHelper.relativizeURI(sourcePartURI, targetURI, true).toString();
            }
            relElem.setAttribute(PackageRelationship.TARGET_ATTRIBUTE_NAME, targetValue);
        }
        xmlOutDoc.normalize();
        ZipEntry ctEntry = new ZipEntry(ZipHelper.getZipURIFromOPCName(relPartName.getURI().toASCIIString()).getPath());
        try {
            zos.putNextEntry(ctEntry);
            if (!StreamHelper.saveXmlInStream(xmlOutDoc, zos)) {
                return false;
            }
            zos.closeEntry();
            return true;
        } catch (IOException e) {
            logger.log(7, "Cannot create zip entry " + relPartName, e);
            return false;
        }
    }
}
