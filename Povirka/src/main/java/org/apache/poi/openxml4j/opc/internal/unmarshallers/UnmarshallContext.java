package org.apache.poi.openxml4j.opc.internal.unmarshallers;

import java.util.zip.ZipEntry;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePartName;

/* JADX INFO: loaded from: classes.dex */
public final class UnmarshallContext {
    private OPCPackage _package;
    private PackagePartName partName;
    private ZipEntry zipEntry;

    public UnmarshallContext(OPCPackage targetPackage, PackagePartName partName) {
        this._package = targetPackage;
        this.partName = partName;
    }

    OPCPackage getPackage() {
        return this._package;
    }

    public void setPackage(OPCPackage container) {
        this._package = container;
    }

    PackagePartName getPartName() {
        return this.partName;
    }

    public void setPartName(PackagePartName partName) {
        this.partName = partName;
    }

    ZipEntry getZipEntry() {
        return this.zipEntry;
    }

    public void setZipEntry(ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
    }
}
