package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import org.apache.poi.poifs.storage.DataInputBlock;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class ODocumentInputStream extends DocumentInputStream {
    private boolean _closed;
    private DataInputBlock _currentBlock;
    private int _current_offset;
    private final OPOIFSDocument _document;
    private final int _document_size;
    private int _marked_offset;

    public ODocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage");
        }
        DocumentNode documentNode = (DocumentNode) document;
        if (documentNode.getDocument() == null) {
            throw new IOException("Cannot open internal document storage");
        }
        this._current_offset = 0;
        this._marked_offset = 0;
        int size = document.getSize();
        this._document_size = size;
        if (size < 0) {
            throw new RecordFormatException("document_size cannot be < 0");
        }
        this._closed = false;
        this._document = documentNode.getDocument();
        this._currentBlock = getDataInputBlock(0);
    }

    public ODocumentInputStream(OPOIFSDocument document) {
        this._current_offset = 0;
        this._marked_offset = 0;
        this._document_size = document.getSize();
        this._closed = false;
        this._document = document;
        this._currentBlock = getDataInputBlock(0);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream, org.apache.poi.util.LittleEndianInput
    public int available() {
        if (this._closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        return this._document_size - this._current_offset;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this._closed = true;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public void mark(int ignoredReadlimit) {
        this._marked_offset = this._current_offset;
    }

    private DataInputBlock getDataInputBlock(int offset) {
        return this._document.getDataInputBlock(offset);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public int read() throws IOException {
        dieIfClosed();
        if (atEOD()) {
            return -1;
        }
        int result = this._currentBlock.readUByte();
        this._current_offset++;
        if (this._currentBlock.available() < 1) {
            this._currentBlock = getDataInputBlock(this._current_offset);
        }
        return result;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        dieIfClosed();
        if (b == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (off < 0 || len < 0 || b.length < off + len) {
            throw new IndexOutOfBoundsException("can't read past buffer boundaries");
        }
        if (len == 0) {
            return 0;
        }
        if (atEOD()) {
            return -1;
        }
        int limit = Math.min(available(), len);
        readFully(b, off, limit);
        return limit;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public void reset() {
        int i = this._marked_offset;
        this._current_offset = i;
        this._currentBlock = getDataInputBlock(i);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        dieIfClosed();
        if (n < 0) {
            return 0L;
        }
        int i = this._current_offset;
        int new_offset = ((int) n) + i;
        if (new_offset < i || new_offset > this._document_size) {
            new_offset = this._document_size;
        }
        long rval = new_offset - i;
        this._current_offset = new_offset;
        this._currentBlock = getDataInputBlock(new_offset);
        return rval;
    }

    private void dieIfClosed() throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }

    private boolean atEOD() {
        return this._current_offset == this._document_size;
    }

    private void checkAvaliable(int requestedSize) {
        if (this._closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        if (requestedSize > this._document_size - this._current_offset) {
            throw new RuntimeException("Buffer underrun - requested " + requestedSize + " bytes but " + (this._document_size - this._current_offset) + " was available");
        }
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public byte readByte() {
        return (byte) readUByte();
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public short readShort() {
        return (short) readUShort();
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buf, int off, int len) {
        int reqSize;
        checkAvaliable(len);
        int blockAvailable = this._currentBlock.available();
        if (blockAvailable > len) {
            this._currentBlock.readFully(buf, off, len);
            this._current_offset += len;
            return;
        }
        int remaining = len;
        int writePos = off;
        while (remaining > 0) {
            boolean blockIsExpiring = remaining >= blockAvailable;
            if (blockIsExpiring) {
                reqSize = blockAvailable;
            } else {
                reqSize = remaining;
            }
            this._currentBlock.readFully(buf, writePos, reqSize);
            remaining -= reqSize;
            writePos += reqSize;
            int i = this._current_offset + reqSize;
            this._current_offset = i;
            if (blockIsExpiring) {
                if (i == this._document_size) {
                    if (remaining > 0) {
                        throw new IllegalStateException("reached end of document stream unexpectedly");
                    }
                    this._currentBlock = null;
                    return;
                } else {
                    DataInputBlock dataInputBlock = getDataInputBlock(i);
                    this._currentBlock = dataInputBlock;
                    blockAvailable = dataInputBlock.available();
                }
            }
        }
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public long readLong() {
        long result;
        long result2;
        checkAvaliable(8);
        int blockAvailable = this._currentBlock.available();
        if (blockAvailable > 8) {
            result2 = this._currentBlock.readLongLE();
        } else {
            DataInputBlock nextBlock = getDataInputBlock(this._current_offset + blockAvailable);
            if (blockAvailable == 8) {
                result = this._currentBlock.readLongLE();
            } else {
                result = nextBlock.readLongLE(this._currentBlock, blockAvailable);
            }
            this._currentBlock = nextBlock;
            result2 = result;
        }
        this._current_offset += 8;
        return result2;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readInt() {
        int result;
        int result2;
        checkAvaliable(4);
        int blockAvailable = this._currentBlock.available();
        if (blockAvailable > 4) {
            result2 = this._currentBlock.readIntLE();
        } else {
            int result3 = this._current_offset;
            DataInputBlock nextBlock = getDataInputBlock(result3 + blockAvailable);
            if (blockAvailable == 4) {
                result = this._currentBlock.readIntLE();
            } else {
                result = nextBlock.readIntLE(this._currentBlock, blockAvailable);
            }
            this._currentBlock = nextBlock;
            result2 = result;
        }
        int result4 = this._current_offset;
        this._current_offset = result4 + 4;
        return result2;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readUShort() {
        int result;
        int result2;
        checkAvaliable(2);
        int blockAvailable = this._currentBlock.available();
        if (blockAvailable > 2) {
            result2 = this._currentBlock.readUShortLE();
        } else {
            int result3 = this._current_offset;
            DataInputBlock nextBlock = getDataInputBlock(result3 + blockAvailable);
            if (blockAvailable == 2) {
                result = this._currentBlock.readUShortLE();
            } else {
                result = nextBlock.readUShortLE(this._currentBlock);
            }
            this._currentBlock = nextBlock;
            result2 = result;
        }
        int result4 = this._current_offset;
        this._current_offset = result4 + 2;
        return result2;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readUByte() {
        checkAvaliable(1);
        int result = this._currentBlock.readUByte();
        this._current_offset++;
        if (this._currentBlock.available() < 1) {
            this._currentBlock = getDataInputBlock(this._current_offset);
        }
        return result;
    }
}
