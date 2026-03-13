package org.apache.poi.poifs.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.util.CloseIgnoringInputStream;

/* JADX INFO: loaded from: classes.dex */
public class POIFSFileSystem extends NPOIFSFileSystem implements POIFSViewable {
    public static InputStream createNonClosingInputStream(InputStream is) {
        return new CloseIgnoringInputStream(is);
    }

    public POIFSFileSystem() {
    }

    public POIFSFileSystem(InputStream stream) throws IOException {
        super(stream);
    }

    public POIFSFileSystem(File file, boolean readOnly) throws IOException {
        super(file, readOnly);
    }

    public POIFSFileSystem(File file) throws IOException {
        super(file);
    }

    public static POIFSFileSystem create(File file) throws IOException {
        POIFSFileSystem tmp = new POIFSFileSystem();
        try {
            OutputStream out = new FileOutputStream(file);
            try {
                tmp.writeFilesystem(out);
                tmp.close();
                return new POIFSFileSystem(file, false);
            } finally {
                out.close();
            }
        } catch (Throwable th) {
            tmp.close();
            throw th;
        }
    }

    public static void main(String[] args) throws IOException {
        OPOIFSFileSystem.main(args);
    }
}
