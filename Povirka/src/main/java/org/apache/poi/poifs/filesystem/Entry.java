package org.apache.poi.poifs.filesystem;

/* JADX INFO: loaded from: classes.dex */
public interface Entry {
    boolean delete();

    String getName();

    DirectoryEntry getParent();

    boolean isDirectoryEntry();

    boolean isDocumentEntry();

    boolean renameTo(String str);
}
