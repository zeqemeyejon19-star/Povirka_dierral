package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public final class DocumentBlock extends BigBlock {
    private static final byte _default_value = -1;
    private int _bytes_read;
    private byte[] _data;

    @Override // org.apache.poi.poifs.storage.BigBlock, org.apache.poi.poifs.storage.BlockWritable
    public /* bridge */ /* synthetic */ void writeBlocks(OutputStream x0) throws IOException {
        super.writeBlocks(x0);
    }

    public DocumentBlock(RawDataBlock block) throws IOException {
        super(block.getBigBlockSize() == 512 ? POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS : POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS);
        byte[] data = block.getData();
        this._data = data;
        this._bytes_read = data.length;
    }

    public DocumentBlock(InputStream stream, POIFSBigBlockSize bigBlockSize) throws IOException {
        this(bigBlockSize);
        int count = IOUtils.readFully(stream, this._data);
        this._bytes_read = count == -1 ? 0 : count;
    }

    private DocumentBlock(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        byte[] bArr = new byte[bigBlockSize.getBigBlockSize()];
        this._data = bArr;
        Arrays.fill(bArr, _default_value);
    }

    public int size() {
        return this._bytes_read;
    }

    public boolean partiallyRead() {
        return this._bytes_read != this.bigBlockSize.getBigBlockSize();
    }

    public static byte getFillByte() {
        return _default_value;
    }

    public static DocumentBlock[] convert(POIFSBigBlockSize bigBlockSize, byte[] array, int size) {
        DocumentBlock[] rval = new DocumentBlock[((bigBlockSize.getBigBlockSize() + size) - 1) / bigBlockSize.getBigBlockSize()];
        int offset = 0;
        for (int k = 0; k < rval.length; k++) {
            rval[k] = new DocumentBlock(bigBlockSize);
            if (offset < array.length) {
                int length = Math.min(bigBlockSize.getBigBlockSize(), array.length - offset);
                System.arraycopy(array, offset, rval[k]._data, 0, length);
                if (length != bigBlockSize.getBigBlockSize()) {
                    Arrays.fill(rval[k]._data, length, bigBlockSize.getBigBlockSize(), _default_value);
                }
            } else {
                Arrays.fill(rval[k]._data, _default_value);
            }
            offset += bigBlockSize.getBigBlockSize();
        }
        return rval;
    }

    public static DataInputBlock getDataInputBlock(DocumentBlock[] blocks, int offset) {
        if (blocks == null || blocks.length == 0) {
            return null;
        }
        POIFSBigBlockSize bigBlockSize = blocks[0].bigBlockSize;
        int BLOCK_SHIFT = bigBlockSize.getHeaderValue();
        int BLOCK_SIZE = bigBlockSize.getBigBlockSize();
        int BLOCK_MASK = BLOCK_SIZE - 1;
        int firstBlockIndex = offset >> BLOCK_SHIFT;
        int firstBlockOffset = offset & BLOCK_MASK;
        return new DataInputBlock(blocks[firstBlockIndex]._data, firstBlockOffset);
    }

    @Override // org.apache.poi.poifs.storage.BigBlock
    void writeData(OutputStream stream) throws IOException {
        doWriteData(stream, this._data);
    }
}
