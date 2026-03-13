package org.apache.poi.openxml4j.opc.internal.marshallers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;

/* JADX INFO: loaded from: classes.dex */
public final class ZipPackagePropertiesMarshaller extends PackagePropertiesMarshaller {
    @Override // org.apache.poi.openxml4j.opc.internal.marshallers.PackagePropertiesMarshaller, org.apache.poi.openxml4j.opc.internal.PartMarshaller
    public boolean marshall(PackagePart part, OutputStream out) throws OpenXML4JException {
        if (!(out instanceof ZipOutputStream)) {
            throw new IllegalArgumentException("ZipOutputStream expected!");
        }
        ZipOutputStream zos = (ZipOutputStream) out;
        ZipEntry ctEntry = new ZipEntry(ZipHelper.getZipItemNameFromOPCName(part.getPartName().getURI().toString()));
        try {
            zos.putNextEntry(ctEntry);
            super.marshall(part, out);
            if (!StreamHelper.saveXmlInStream(this.xmlDoc, out)) {
                return false;
            }
            zos.closeEntry();
            return true;
        } catch (IOException e) {
            throw new OpenXML4JException(e.getLocalizedMessage(), e);
        }
    }
}
