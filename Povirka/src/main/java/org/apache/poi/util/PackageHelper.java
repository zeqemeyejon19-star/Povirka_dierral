package org.apache.poi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;

/* JADX INFO: loaded from: classes.dex */
public final class PackageHelper {
    public static OPCPackage open(InputStream is) throws IOException {
        try {
            return OPCPackage.open(is);
        } catch (InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }

    public static OPCPackage clone(OPCPackage pkg, File file) throws OpenXML4JException, IOException {
        String path = file.getAbsolutePath();
        OPCPackage dest = OPCPackage.create(path);
        PackageRelationshipCollection rels = pkg.getRelationships();
        for (PackageRelationship rel : rels) {
            PackagePart part = pkg.getPart(rel);
            if (rel.getRelationshipType().equals(PackageRelationshipTypes.CORE_PROPERTIES)) {
                copyProperties(pkg.getPackageProperties(), dest.getPackageProperties());
            } else {
                dest.addRelationship(part.getPartName(), rel.getTargetMode(), rel.getRelationshipType());
                PackagePart part_tgt = dest.createPart(part.getPartName(), part.getContentType());
                OutputStream out = part_tgt.getOutputStream();
                IOUtils.copy(part.getInputStream(), out);
                out.close();
                if (part.hasRelationships()) {
                    copy(pkg, part, dest, part_tgt);
                }
            }
        }
        dest.close();
        new File(path).deleteOnExit();
        return OPCPackage.open(path);
    }

    private static void copy(OPCPackage pkg, PackagePart part, OPCPackage tgt, PackagePart part_tgt) throws OpenXML4JException, IOException {
        PackageRelationshipCollection rels = part.getRelationships();
        if (rels != null) {
            for (PackageRelationship rel : rels) {
                if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                    part_tgt.addExternalRelationship(rel.getTargetURI().toString(), rel.getRelationshipType(), rel.getId());
                } else {
                    URI uri = rel.getTargetURI();
                    if (uri.getRawFragment() != null) {
                        part_tgt.addRelationship(uri, rel.getTargetMode(), rel.getRelationshipType(), rel.getId());
                    } else {
                        PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
                        PackagePart p = pkg.getPart(relName);
                        part_tgt.addRelationship(p.getPartName(), rel.getTargetMode(), rel.getRelationshipType(), rel.getId());
                        if (!tgt.containPart(p.getPartName())) {
                            PackagePart dest = tgt.createPart(p.getPartName(), p.getContentType());
                            OutputStream out = dest.getOutputStream();
                            IOUtils.copy(p.getInputStream(), out);
                            out.close();
                            copy(pkg, p, tgt, dest);
                        }
                    }
                }
            }
        }
    }

    private static void copyProperties(PackageProperties src, PackageProperties tgt) {
        tgt.setCategoryProperty(src.getCategoryProperty().getValue());
        tgt.setContentStatusProperty(src.getContentStatusProperty().getValue());
        tgt.setContentTypeProperty(src.getContentTypeProperty().getValue());
        tgt.setCreatorProperty(src.getCreatorProperty().getValue());
        tgt.setDescriptionProperty(src.getDescriptionProperty().getValue());
        tgt.setIdentifierProperty(src.getIdentifierProperty().getValue());
        tgt.setKeywordsProperty(src.getKeywordsProperty().getValue());
        tgt.setLanguageProperty(src.getLanguageProperty().getValue());
        tgt.setRevisionProperty(src.getRevisionProperty().getValue());
        tgt.setSubjectProperty(src.getSubjectProperty().getValue());
        tgt.setTitleProperty(src.getTitleProperty().getValue());
        tgt.setVersionProperty(src.getVersionProperty().getValue());
    }
}
