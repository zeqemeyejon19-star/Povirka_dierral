package org.apache.poi.openxml4j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import org.apache.poi.openxml4j.util.ZipSecureFile;

/* JADX INFO: loaded from: classes.dex */
public class ZipInputStreamZipEntrySource implements ZipEntrySource {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private ArrayList<FakeZipEntry> zipEntries = new ArrayList<>();

    public ZipInputStreamZipEntrySource(ZipSecureFile.ThresholdInputStream inp) throws IOException {
        boolean going = true;
        while (going) {
            ZipEntry zipEntry = inp.getNextEntry();
            if (zipEntry == null) {
                going = false;
            } else {
                FakeZipEntry entry = new FakeZipEntry(zipEntry, inp);
                inp.closeEntry();
                this.zipEntries.add(entry);
            }
        }
        inp.close();
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public Enumeration<? extends ZipEntry> getEntries() {
        return new EntryEnumerator();
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public InputStream getInputStream(ZipEntry zipEntry) {
        if (!(zipEntry instanceof FakeZipEntry)) {
            throw new AssertionError();
        }
        FakeZipEntry entry = (FakeZipEntry) zipEntry;
        return entry.getInputStream();
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.zipEntries = null;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public boolean isClosed() {
        return this.zipEntries == null;
    }

    private class EntryEnumerator implements Enumeration<ZipEntry> {
        private Iterator<? extends ZipEntry> iterator;

        private EntryEnumerator() {
            this.iterator = ZipInputStreamZipEntrySource.this.zipEntries.iterator();
        }

        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        @Override // java.util.Enumeration
        public ZipEntry nextElement() {
            return this.iterator.next();
        }
    }

    public static class FakeZipEntry extends ZipEntry {
        private byte[] data;

        public FakeZipEntry(ZipEntry entry, InputStream inp) throws IOException {
            ByteArrayOutputStream baos;
            super(entry.getName());
            long entrySize = entry.getSize();
            if (entrySize == -1) {
                baos = new ByteArrayOutputStream();
            } else {
                if (entrySize >= 2147483647L) {
                    throw new IOException("ZIP entry size is too large");
                }
                baos = new ByteArrayOutputStream((int) entrySize);
            }
            byte[] buffer = new byte[4096];
            while (true) {
                int read = inp.read(buffer);
                if (read != -1) {
                    baos.write(buffer, 0, read);
                } else {
                    this.data = baos.toByteArray();
                    return;
                }
            }
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.data);
        }
    }
}
