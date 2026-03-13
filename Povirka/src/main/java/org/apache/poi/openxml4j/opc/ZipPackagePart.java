package org.apache.poi.openxml4j.opc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.util.NotImplemented;

/* JADX INFO: loaded from: classes.dex */
public class ZipPackagePart extends PackagePart {
    private ZipEntry zipEntry;

    public ZipPackagePart(OPCPackage container, PackagePartName partName, String contentType) throws InvalidFormatException {
        super(container, partName, contentType);
    }

    public ZipPackagePart(OPCPackage container, ZipEntry zipEntry, PackagePartName partName, String contentType) throws InvalidFormatException {
        super(container, partName, contentType);
        this.zipEntry = zipEntry;
    }

    public ZipEntry getZipArchive() {
        return this.zipEntry;
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected InputStream getInputStreamImpl() throws IOException {
        return ((ZipPackage) this._container).getZipArchive().getInputStream(this.zipEntry);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected OutputStream getOutputStreamImpl() {
        return null;
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public long getSize() {
        return this.zipEntry.getSize();
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public boolean save(OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    @NotImplemented
    public boolean load(InputStream ios) {
        throw new InvalidOperationException("Method not implemented !");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    @NotImplemented
    public void close() {
        throw new InvalidOperationException("Method not implemented !");
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    @NotImplemented
    public void flush() {
        throw new InvalidOperationException("Method not implemented !");
    }
}
