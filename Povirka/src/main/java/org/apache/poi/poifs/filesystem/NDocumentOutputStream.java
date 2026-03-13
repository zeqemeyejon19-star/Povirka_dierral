package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.property.DocumentProperty;

/* JADX INFO: loaded from: classes.dex */
public final class NDocumentOutputStream extends OutputStream {
    private ByteArrayOutputStream _buffer = new ByteArrayOutputStream(4096);
    private boolean _closed;
    private NPOIFSDocument _document;
    private int _document_size;
    private DocumentProperty _property;
    private NPOIFSStream _stream;
    private OutputStream _stream_output;

    public NDocumentOutputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        this._document_size = 0;
        this._closed = false;
        this._property = (DocumentProperty) ((DocumentNode) document).getProperty();
        NPOIFSDocument nPOIFSDocument = new NPOIFSDocument((DocumentNode) document);
        this._document = nPOIFSDocument;
        nPOIFSDocument.free();
    }

    public NDocumentOutputStream(DirectoryEntry parent, String name) throws IOException {
        if (!(parent instanceof DirectoryNode)) {
            throw new IOException("Cannot open internal directory storage, " + parent + " not a Directory Node");
        }
        this._document_size = 0;
        this._closed = false;
        DocumentEntry doc = parent.createDocument(name, new ByteArrayInputStream(new byte[0]));
        this._property = (DocumentProperty) ((DocumentNode) doc).getProperty();
        this._document = new NPOIFSDocument((DocumentNode) doc);
    }

    private void dieIfClosed() throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }

    private void checkBufferSize() throws IOException {
        if (this._buffer.size() > 4096) {
            byte[] data = this._buffer.toByteArray();
            this._buffer = null;
            write(data, 0, data.length);
        }
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        dieIfClosed();
        ByteArrayOutputStream byteArrayOutputStream = this._buffer;
        if (byteArrayOutputStream != null) {
            byteArrayOutputStream.write(b);
            checkBufferSize();
        } else {
            write(new byte[]{(byte) b});
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] b) throws IOException {
        dieIfClosed();
        ByteArrayOutputStream byteArrayOutputStream = this._buffer;
        if (byteArrayOutputStream != null) {
            byteArrayOutputStream.write(b);
            checkBufferSize();
        } else {
            write(b, 0, b.length);
        }
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        dieIfClosed();
        ByteArrayOutputStream byteArrayOutputStream = this._buffer;
        if (byteArrayOutputStream != null) {
            byteArrayOutputStream.write(b, off, len);
            checkBufferSize();
            return;
        }
        if (this._stream == null) {
            NPOIFSStream nPOIFSStream = new NPOIFSStream(this._document.getFileSystem());
            this._stream = nPOIFSStream;
            this._stream_output = nPOIFSStream.getOutputStream();
        }
        this._stream_output.write(b, off, len);
        this._document_size += len;
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this._buffer != null) {
            this._document.replaceContents(new ByteArrayInputStream(this._buffer.toByteArray()));
        } else {
            this._stream_output.close();
            this._property.updateSize(this._document_size);
            this._property.setStartBlock(this._stream.getStartBlock());
        }
        this._closed = true;
    }
}
