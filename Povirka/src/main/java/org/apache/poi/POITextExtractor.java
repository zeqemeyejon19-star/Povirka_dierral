package org.apache.poi;

import java.io.Closeable;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public abstract class POITextExtractor implements Closeable {
    private Closeable fsToClose = null;

    public abstract POITextExtractor getMetadataTextExtractor();

    public abstract String getText();

    public void setFilesystem(Closeable fs) {
        this.fsToClose = fs;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        Closeable closeable = this.fsToClose;
        if (closeable != null) {
            closeable.close();
        }
    }
}
