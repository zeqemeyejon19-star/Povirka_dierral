package org.apache.poi.util;

import java.io.FilterInputStream;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class CloseIgnoringInputStream extends FilterInputStream {
    public CloseIgnoringInputStream(InputStream in) {
        super(in);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
    }
}
