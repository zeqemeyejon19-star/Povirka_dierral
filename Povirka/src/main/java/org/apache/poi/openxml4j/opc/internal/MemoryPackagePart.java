package org.apache.poi.openxml4j.opc.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public final class MemoryPackagePart extends PackagePart {
    protected byte[] data;

    public MemoryPackagePart(OPCPackage pack, PackagePartName partName, String contentType) throws InvalidFormatException {
        super(pack, partName, contentType);
    }

    public MemoryPackagePart(OPCPackage pack, PackagePartName partName, String contentType, boolean loadRelationships) throws InvalidFormatException {
        super(pack, partName, new ContentType(contentType), loadRelationships);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected InputStream getInputStreamImpl() {
        if (this.data == null) {
            this.data = new byte[0];
        }
        return new ByteArrayInputStream(this.data);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    protected OutputStream getOutputStreamImpl() {
        return new MemoryPackagePartOutputStream(this);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public long getSize() {
        if (this.data == null) {
            return 0L;
        }
        return r0.length;
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public void clear() {
        this.data = null;
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public boolean save(OutputStream os) throws OpenXML4JException {
        return new ZipPartMarshaller().marshall(this, os);
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public boolean load(InputStream ios) throws InvalidFormatException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(ios, baos);
            this.data = baos.toByteArray();
            return true;
        } catch (IOException e) {
            throw new InvalidFormatException(e.getMessage());
        }
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public void close() {
    }

    @Override // org.apache.poi.openxml4j.opc.PackagePart
    public void flush() {
    }
}
