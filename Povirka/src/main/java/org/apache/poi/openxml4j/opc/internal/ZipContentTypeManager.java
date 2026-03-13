package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public class ZipContentTypeManager extends ContentTypeManager {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) ZipContentTypeManager.class);

    public ZipContentTypeManager(InputStream in, OPCPackage pkg) throws InvalidFormatException {
        super(in, pkg);
    }

    @Override // org.apache.poi.openxml4j.opc.internal.ContentTypeManager
    public boolean saveImpl(Document content, OutputStream out) {
        ZipOutputStream zos;
        if (out instanceof ZipOutputStream) {
            zos = (ZipOutputStream) out;
        } else {
            zos = new ZipOutputStream(out);
        }
        ZipEntry partEntry = new ZipEntry(ContentTypeManager.CONTENT_TYPES_PART_NAME);
        try {
            zos.putNextEntry(partEntry);
            if (!StreamHelper.saveXmlInStream(content, zos)) {
                return false;
            }
            zos.closeEntry();
            return true;
        } catch (IOException ioe) {
            logger.log(7, "Cannot write: [Content_Types].xml in Zip !", ioe);
            return false;
        }
    }
}
