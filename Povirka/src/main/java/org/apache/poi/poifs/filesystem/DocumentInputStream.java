package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class DocumentInputStream extends InputStream implements LittleEndianInput {
    protected static final int EOF = -1;
    protected static final int SIZE_INT = 4;
    protected static final int SIZE_LONG = 8;
    protected static final int SIZE_SHORT = 2;
    private DocumentInputStream delegate;

    protected DocumentInputStream() {
    }

    public DocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage");
        }
        DocumentNode documentNode = (DocumentNode) document;
        DirectoryNode parentNode = (DirectoryNode) document.getParent();
        if (documentNode.getDocument() != null) {
            this.delegate = new ODocumentInputStream(document);
        } else if (parentNode.getOFileSystem() != null) {
            this.delegate = new ODocumentInputStream(document);
        } else {
            if (parentNode.getNFileSystem() != null) {
                this.delegate = new NDocumentInputStream(document);
                return;
            }
            throw new IOException("No FileSystem bound on the parent, can't read contents");
        }
    }

    public DocumentInputStream(OPOIFSDocument document) {
        this.delegate = new ODocumentInputStream(document);
    }

    public DocumentInputStream(NPOIFSDocument document) {
        this.delegate = new NDocumentInputStream(document);
    }

    @Override // java.io.InputStream, org.apache.poi.util.LittleEndianInput
    public int available() {
        return this.delegate.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.delegate.close();
    }

    @Override // java.io.InputStream
    public void mark(int ignoredReadlimit) {
        this.delegate.mark(ignoredReadlimit);
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        return this.delegate.read();
    }

    @Override // java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        return this.delegate.read(b, off, len);
    }

    @Override // java.io.InputStream
    public void reset() {
        this.delegate.reset();
    }

    @Override // java.io.InputStream
    public long skip(long n) throws IOException {
        return this.delegate.skip(n);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public byte readByte() {
        return this.delegate.readByte();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public double readDouble() {
        return this.delegate.readDouble();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public short readShort() {
        return (short) readUShort();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buf, int off, int len) {
        this.delegate.readFully(buf, off, len);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public long readLong() {
        return this.delegate.readLong();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readInt() {
        return this.delegate.readInt();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUShort() {
        return this.delegate.readUShort();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUByte() {
        return this.delegate.readUByte();
    }

    public long readUInt() {
        int i = readInt();
        return ((long) i) & 4294967295L;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readPlain(byte[] buf, int off, int len) {
        readFully(buf, off, len);
    }
}
