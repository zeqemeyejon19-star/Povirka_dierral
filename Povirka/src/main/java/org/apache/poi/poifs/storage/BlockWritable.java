package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public interface BlockWritable {
    void writeBlocks(OutputStream outputStream) throws IOException;
}
