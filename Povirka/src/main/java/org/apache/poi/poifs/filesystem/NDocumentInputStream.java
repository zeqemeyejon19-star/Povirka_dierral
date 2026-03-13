package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public final class NDocumentInputStream extends DocumentInputStream {
    private ByteBuffer _buffer;
    private boolean _closed;
    private int _current_block_count;
    private int _current_offset;
    private Iterator<ByteBuffer> _data;
    private final NPOIFSDocument _document;
    private final int _document_size;
    private int _marked_offset;
    private int _marked_offset_count;

    public NDocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        this._current_offset = 0;
        this._current_block_count = 0;
        this._marked_offset = 0;
        this._marked_offset_count = 0;
        this._document_size = document.getSize();
        this._closed = false;
        DocumentNode doc = (DocumentNode) document;
        DocumentProperty property = (DocumentProperty) doc.getProperty();
        NPOIFSDocument nPOIFSDocument = new NPOIFSDocument(property, ((DirectoryNode) doc.getParent()).getNFileSystem());
        this._document = nPOIFSDocument;
        this._data = nPOIFSDocument.getBlockIterator();
    }

    public NDocumentInputStream(NPOIFSDocument document) {
        this._current_offset = 0;
        this._current_block_count = 0;
        this._marked_offset = 0;
        this._marked_offset_count = 0;
        this._document_size = document.getSize();
        this._closed = false;
        this._document = document;
        this._data = document.getBlockIterator();
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream, org.apache.poi.util.LittleEndianInput
    public int available() {
        return remainingBytes();
    }

    private int remainingBytes() {
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
        this._marked_offset_count = Math.max(0, this._current_block_count - 1);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public int read() throws IOException {
        dieIfClosed();
        if (atEOD()) {
            return -1;
        }
        byte[] b = new byte[1];
        int result = read(b, 0, 1);
        if (result >= 0) {
            if (b[0] < 0) {
                return b[0] + 256;
            }
            return b[0];
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
        int limit = Math.min(remainingBytes(), len);
        readFully(b, off, limit);
        return limit;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public void reset() {
        int i;
        int i2;
        int i3 = this._marked_offset;
        if (i3 == 0 && (i2 = this._marked_offset_count) == 0) {
            this._current_block_count = i2;
            this._current_offset = i3;
            this._data = this._document.getBlockIterator();
            this._buffer = null;
            return;
        }
        this._data = this._document.getBlockIterator();
        this._current_offset = 0;
        int i4 = 0;
        while (true) {
            i = this._marked_offset_count;
            if (i4 >= i) {
                break;
            }
            ByteBuffer next = this._data.next();
            this._buffer = next;
            this._current_offset += next.remaining();
            i4++;
        }
        this._current_block_count = i;
        if (this._current_offset != this._marked_offset) {
            ByteBuffer next2 = this._data.next();
            this._buffer = next2;
            this._current_block_count++;
            int skipBy = this._marked_offset - this._current_offset;
            next2.position(next2.position() + skipBy);
        }
        this._current_offset = this._marked_offset;
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        dieIfClosed();
        if (n < 0) {
            return 0L;
        }
        int i = this._current_offset;
        long new_offset = ((long) i) + n;
        if (new_offset < i) {
            new_offset = this._document_size;
        } else {
            int i2 = this._document_size;
            if (new_offset > i2) {
                new_offset = i2;
            }
        }
        long rval = new_offset - ((long) i);
        byte[] skip = new byte[(int) rval];
        readFully(skip);
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
    public void readFully(byte[] buf, int off, int len) {
        if (len < 0) {
            throw new RuntimeException("Can't read negative number of bytes");
        }
        checkAvaliable(len);
        int read = 0;
        while (read < len) {
            ByteBuffer byteBuffer = this._buffer;
            if (byteBuffer == null || byteBuffer.remaining() == 0) {
                this._current_block_count++;
                this._buffer = this._data.next();
            }
            int limit = Math.min(len - read, this._buffer.remaining());
            this._buffer.get(buf, off + read, limit);
            this._current_offset += limit;
            read += limit;
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
    public long readLong() {
        checkAvaliable(8);
        byte[] data = new byte[8];
        readFully(data, 0, 8);
        return LittleEndian.getLong(data, 0);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public short readShort() {
        checkAvaliable(2);
        byte[] data = new byte[2];
        readFully(data, 0, 2);
        return LittleEndian.getShort(data);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readInt() {
        checkAvaliable(4);
        byte[] data = new byte[4];
        readFully(data, 0, 4);
        return LittleEndian.getInt(data);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readUShort() {
        checkAvaliable(2);
        byte[] data = new byte[2];
        readFully(data, 0, 2);
        return LittleEndian.getUShort(data);
    }

    @Override // org.apache.poi.poifs.filesystem.DocumentInputStream, org.apache.poi.util.LittleEndianInput
    public int readUByte() {
        checkAvaliable(1);
        byte[] data = new byte[1];
        readFully(data, 0, 1);
        if (data[0] >= 0) {
            return data[0];
        }
        return data[0] + 256;
    }
}
