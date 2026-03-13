package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class RawDataBlock implements ListManagedBlock {
    static POILogger log = POILogFactory.getLogger((Class<?>) RawDataBlock.class);
    private byte[] _data;
    private boolean _eof;
    private boolean _hasData;

    public RawDataBlock(InputStream stream) throws IOException {
        this(stream, 512);
    }

    public RawDataBlock(InputStream stream, int blockSize) throws IOException {
        byte[] bArr = new byte[blockSize];
        this._data = bArr;
        int count = IOUtils.readFully(stream, bArr);
        this._hasData = count > 0;
        if (count == -1) {
            this._eof = true;
        } else {
            if (count != blockSize) {
                this._eof = true;
                String type = " byte" + (count == 1 ? "" : "s");
                log.log(7, "Unable to read entire block; " + count + type + " read before EOF; expected " + blockSize + " bytes. Your document was either written by software that ignores the spec, or has been truncated!");
                return;
            }
            this._eof = false;
        }
    }

    public boolean eof() {
        return this._eof;
    }

    public boolean hasData() {
        return this._hasData;
    }

    public String toString() {
        return "RawDataBlock of size " + this._data.length;
    }

    @Override // org.apache.poi.poifs.storage.ListManagedBlock
    public byte[] getData() throws IOException {
        if (!hasData()) {
            throw new IOException("Cannot return empty data");
        }
        return this._data;
    }

    public int getBigBlockSize() {
        return this._data.length;
    }
}
