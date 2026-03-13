package org.apache.poi.hpsf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIDocument;
import org.apache.poi.poifs.filesystem.EntryUtils;
import org.apache.poi.poifs.filesystem.FilteringDirectoryNode;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes.dex */
public class HPSFPropertiesOnlyDocument extends POIDocument {
    public HPSFPropertiesOnlyDocument(NPOIFSFileSystem fs) {
        super(fs.getRoot());
    }

    public HPSFPropertiesOnlyDocument(OPOIFSFileSystem fs) {
        super(fs);
    }

    public HPSFPropertiesOnlyDocument(POIFSFileSystem fs) {
        super(fs);
    }

    @Override // org.apache.poi.POIDocument
    public void write() throws IOException {
        NPOIFSFileSystem fs = getDirectory().getFileSystem();
        validateInPlaceWritePossible();
        writeProperties(fs, null);
        fs.writeFilesystem();
    }

    @Override // org.apache.poi.POIDocument
    public void write(File newFile) throws IOException {
        POIFSFileSystem fs = POIFSFileSystem.create(newFile);
        try {
            write(fs);
            fs.writeFilesystem();
        } finally {
            fs.close();
        }
    }

    @Override // org.apache.poi.POIDocument
    public void write(OutputStream out) throws IOException {
        NPOIFSFileSystem fs = new NPOIFSFileSystem();
        try {
            write(fs);
            fs.writeFilesystem(out);
        } finally {
            fs.close();
        }
    }

    private void write(NPOIFSFileSystem fs) throws IOException {
        List<String> excepts = new ArrayList<>(2);
        writeProperties(fs, excepts);
        FilteringDirectoryNode src = new FilteringDirectoryNode(getDirectory(), excepts);
        FilteringDirectoryNode dest = new FilteringDirectoryNode(fs.getRoot(), excepts);
        EntryUtils.copyNodes(src, dest);
    }
}
