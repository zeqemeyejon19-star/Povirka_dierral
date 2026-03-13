package org.apache.poi.openxml4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* JADX INFO: loaded from: classes.dex */
public class ZipFileZipEntrySource implements ZipEntrySource {
    private ZipFile zipArchive;

    public ZipFileZipEntrySource(ZipFile zipFile) {
        this.zipArchive = zipFile;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        ZipFile zipFile = this.zipArchive;
        if (zipFile != null) {
            zipFile.close();
        }
        this.zipArchive = null;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public boolean isClosed() {
        return this.zipArchive == null;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public Enumeration<? extends ZipEntry> getEntries() {
        ZipFile zipFile = this.zipArchive;
        if (zipFile == null) {
            throw new IllegalStateException("Zip File is closed");
        }
        return zipFile.entries();
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public InputStream getInputStream(ZipEntry entry) throws IOException {
        ZipFile zipFile = this.zipArchive;
        if (zipFile == null) {
            throw new IllegalStateException("Zip File is closed");
        }
        return zipFile.getInputStream(entry);
    }
}
