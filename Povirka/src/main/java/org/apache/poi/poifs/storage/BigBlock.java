package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

/* JADX INFO: loaded from: classes.dex */
abstract class BigBlock implements BlockWritable {
    protected POIFSBigBlockSize bigBlockSize;

    abstract void writeData(OutputStream outputStream) throws IOException;

    protected BigBlock(POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
    }

    protected void doWriteData(OutputStream stream, byte[] data) throws IOException {
        stream.write(data);
    }

    @Override // org.apache.poi.poifs.storage.BlockWritable
    public void writeBlocks(OutputStream stream) throws IOException {
        writeData(stream);
    }
}
