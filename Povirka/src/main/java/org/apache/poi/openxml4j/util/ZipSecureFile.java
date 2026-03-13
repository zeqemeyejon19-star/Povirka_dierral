package org.apache.poi.openxml4j.util;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class ZipSecureFile extends ZipFile {
    private static final long GRACE_ENTRY_SIZE = 102400;
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ZipSecureFile.class);
    private static double MIN_INFLATE_RATIO = 0.01d;
    private static long MAX_ENTRY_SIZE = 4294967295L;
    private static long MAX_TEXT_SIZE = 10485760;

    public static void setMinInflateRatio(double ratio) {
        MIN_INFLATE_RATIO = ratio;
    }

    public static double getMinInflateRatio() {
        return MIN_INFLATE_RATIO;
    }

    public static void setMaxEntrySize(long maxEntrySize) {
        if (maxEntrySize < 0 || maxEntrySize > 4294967295L) {
            throw new IllegalArgumentException("Max entry size is bounded [0-4GB], but had " + maxEntrySize);
        }
        MAX_ENTRY_SIZE = maxEntrySize;
    }

    public static long getMaxEntrySize() {
        return MAX_ENTRY_SIZE;
    }

    public static void setMaxTextSize(long maxTextSize) {
        if (maxTextSize < 0 || maxTextSize > 4294967295L) {
            throw new IllegalArgumentException("Max text size is bounded [0-4GB], but had " + maxTextSize);
        }
        MAX_TEXT_SIZE = maxTextSize;
    }

    public static long getMaxTextSize() {
        return MAX_TEXT_SIZE;
    }

    public ZipSecureFile(File file, int mode) throws IOException {
        super(file, mode);
    }

    public ZipSecureFile(File file) throws IOException {
        super(file);
    }

    public ZipSecureFile(String name) throws IOException {
        super(name);
    }

    @Override // java.util.zip.ZipFile
    public InputStream getInputStream(ZipEntry entry) throws IOException {
        InputStream zipIS = super.getInputStream(entry);
        return addThreshold(zipIS);
    }

    public static ThresholdInputStream addThreshold(final InputStream zipIS) throws IOException {
        ThresholdInputStream newInner;
        if (zipIS instanceof InflaterInputStream) {
            newInner = (ThresholdInputStream) AccessController.doPrivileged(new PrivilegedAction<ThresholdInputStream>() { // from class: org.apache.poi.openxml4j.util.ZipSecureFile.1
                @Override // java.security.PrivilegedAction
                public ThresholdInputStream run() {
                    try {
                        Field f = FilterInputStream.class.getDeclaredField("in");
                        f.setAccessible(true);
                        InputStream oldInner = (InputStream) f.get(zipIS);
                        ThresholdInputStream newInner2 = new ThresholdInputStream(oldInner, null);
                        f.set(zipIS, newInner2);
                        return newInner2;
                    } catch (Exception ex) {
                        ZipSecureFile.LOG.log(5, "SecurityManager doesn't allow manipulation via reflection for zipbomb detection - continue with original input stream", ex);
                        return null;
                    }
                }
            });
        } else {
            newInner = null;
        }
        return new ThresholdInputStream(zipIS, newInner);
    }

    public static class ThresholdInputStream extends PushbackInputStream {
        ThresholdInputStream cis;
        long counter;
        long markPos;

        public ThresholdInputStream(InputStream is, ThresholdInputStream cis) {
            super(is);
            this.counter = 0L;
            this.markPos = 0L;
            this.cis = cis;
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read() throws IOException {
            int b = this.in.read();
            if (b > -1) {
                advance(1);
            }
            return b;
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int cnt = this.in.read(b, off, len);
            if (cnt > -1) {
                advance(cnt);
            }
            return cnt;
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public long skip(long n) throws IOException {
            long s = this.in.skip(n);
            this.counter += s;
            return s;
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public synchronized void reset() throws IOException {
            this.counter = this.markPos;
            super.reset();
        }

        public void advance(int advance) throws IOException {
            long j = this.counter + ((long) advance);
            this.counter = j;
            if (j > ZipSecureFile.MAX_ENTRY_SIZE) {
                StringBuilder sbAppend = new StringBuilder().append("Zip bomb detected! The file would exceed the max size of the expanded data in the zip-file. This may indicates that the file is used to inflate memory usage and thus could pose a security risk. You can adjust this limit via ZipSecureFile.setMaxEntrySize() if you need to work with files which are very large. Counter: ").append(this.counter).append(", cis.counter: ");
                ThresholdInputStream thresholdInputStream = this.cis;
                throw new IOException(sbAppend.append(thresholdInputStream == null ? 0L : thresholdInputStream.counter).append("Limits: MAX_ENTRY_SIZE: ").append(ZipSecureFile.MAX_ENTRY_SIZE).toString());
            }
            if (this.cis == null) {
                return;
            }
            long j2 = this.counter;
            if (j2 <= ZipSecureFile.GRACE_ENTRY_SIZE) {
                return;
            }
            double ratio = r0.counter / j2;
            if (ratio >= ZipSecureFile.MIN_INFLATE_RATIO) {
            } else {
                throw new IOException("Zip bomb detected! The file would exceed the max. ratio of compressed file size to the size of the expanded data.\nThis may indicate that the file is used to inflate memory usage and thus could pose a security risk.\nYou can adjust this limit via ZipSecureFile.setMinInflateRatio() if you need to work with files which exceed this limit.\nCounter: " + this.counter + ", cis.counter: " + this.cis.counter + ", ratio: " + ratio + "\nLimits: MIN_INFLATE_RATIO: " + ZipSecureFile.MIN_INFLATE_RATIO);
            }
        }

        public ZipEntry getNextEntry() throws IOException {
            if (!(this.in instanceof ZipInputStream)) {
                throw new UnsupportedOperationException("underlying stream is not a ZipInputStream");
            }
            this.counter = 0L;
            return ((ZipInputStream) this.in).getNextEntry();
        }

        public void closeEntry() throws IOException {
            if (!(this.in instanceof ZipInputStream)) {
                throw new UnsupportedOperationException("underlying stream is not a ZipInputStream");
            }
            this.counter = 0L;
            ((ZipInputStream) this.in).closeEntry();
        }

        @Override // java.io.PushbackInputStream
        public void unread(int b) throws IOException {
            if (!(this.in instanceof PushbackInputStream)) {
                throw new UnsupportedOperationException("underlying stream is not a PushbackInputStream");
            }
            long j = this.counter - 1;
            this.counter = j;
            if (j < 0) {
                this.counter = 0L;
            }
            ((PushbackInputStream) this.in).unread(b);
        }

        @Override // java.io.PushbackInputStream
        public void unread(byte[] b, int off, int len) throws IOException {
            if (!(this.in instanceof PushbackInputStream)) {
                throw new UnsupportedOperationException("underlying stream is not a PushbackInputStream");
            }
            long j = this.counter - ((long) len);
            this.counter = j;
            long j2 = j - 1;
            this.counter = j2;
            if (j2 < 0) {
                this.counter = 0L;
            }
            ((PushbackInputStream) this.in).unread(b, off, len);
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public int available() throws IOException {
            return this.in.available();
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public boolean markSupported() {
            return this.in.markSupported();
        }

        @Override // java.io.PushbackInputStream, java.io.FilterInputStream, java.io.InputStream
        public synchronized void mark(int readlimit) {
            this.markPos = this.counter;
            this.in.mark(readlimit);
        }
    }
}
