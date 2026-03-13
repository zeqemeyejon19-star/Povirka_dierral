package org.apache.poi.poifs.filesystem;

import androidx.fragment.app.FragmentTransaction;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.util.HexDump;

/* JADX INFO: loaded from: classes.dex */
public final class NPOIFSDocument implements POIFSViewable {
    private int _block_size;
    private NPOIFSFileSystem _filesystem;
    private DocumentProperty _property;
    private NPOIFSStream _stream;

    public NPOIFSDocument(DocumentNode document) throws IOException {
        this((DocumentProperty) document.getProperty(), ((DirectoryNode) document.getParent()).getNFileSystem());
    }

    public NPOIFSDocument(DocumentProperty property, NPOIFSFileSystem filesystem) throws IOException {
        this._property = property;
        this._filesystem = filesystem;
        if (property.getSize() < 4096) {
            this._stream = new NPOIFSStream(this._filesystem.getMiniStore(), property.getStartBlock());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        } else {
            this._stream = new NPOIFSStream(this._filesystem, property.getStartBlock());
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
    }

    public NPOIFSDocument(String name, NPOIFSFileSystem filesystem, InputStream stream) throws IOException {
        this._filesystem = filesystem;
        int length = store(stream);
        DocumentProperty documentProperty = new DocumentProperty(name, length);
        this._property = documentProperty;
        documentProperty.setStartBlock(this._stream.getStartBlock());
    }

    public NPOIFSDocument(String name, int size, NPOIFSFileSystem filesystem, POIFSWriterListener writer) throws IOException {
        this._filesystem = filesystem;
        if (size < 4096) {
            this._stream = new NPOIFSStream(filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        } else {
            this._stream = new NPOIFSStream(filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        OutputStream innerOs = this._stream.getOutputStream();
        DocumentOutputStream os = new DocumentOutputStream(innerOs, size);
        POIFSDocumentPath path = new POIFSDocumentPath(name.split("\\\\"));
        String docName = path.getComponent(path.length() - 1);
        POIFSWriterEvent event = new POIFSWriterEvent(os, path, docName, size);
        writer.processPOIFSWriterEvent(event);
        innerOs.close();
        DocumentProperty documentProperty = new DocumentProperty(name, size);
        this._property = documentProperty;
        documentProperty.setStartBlock(this._stream.getStartBlock());
    }

    private int store(InputStream stream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        bis.mark(4096);
        if (bis.skip(4096L) < 4096) {
            this._stream = new NPOIFSStream(this._filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        } else {
            this._stream = new NPOIFSStream(this._filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        bis.reset();
        OutputStream os = this._stream.getOutputStream();
        byte[] buf = new byte[1024];
        int length = 0;
        while (true) {
            int readBytes = bis.read(buf);
            if (readBytes == -1) {
                break;
            }
            os.write(buf, 0, readBytes);
            length += readBytes;
        }
        int i = this._block_size;
        int usedInBlock = length % i;
        if (usedInBlock != 0 && usedInBlock != i) {
            int toBlockEnd = i - usedInBlock;
            byte[] padding = new byte[toBlockEnd];
            Arrays.fill(padding, (byte) -1);
            os.write(padding);
        }
        os.close();
        return length;
    }

    void free() throws IOException {
        this._stream.free();
        this._property.setStartBlock(-2);
    }

    NPOIFSFileSystem getFileSystem() {
        return this._filesystem;
    }

    int getDocumentBlockSize() {
        return this._block_size;
    }

    Iterator<ByteBuffer> getBlockIterator() {
        if (getSize() > 0) {
            return this._stream.getBlockIterator();
        }
        List<ByteBuffer> empty = Collections.emptyList();
        return empty.iterator();
    }

    public int getSize() {
        return this._property.getSize();
    }

    public void replaceContents(InputStream stream) throws IOException {
        free();
        int size = store(stream);
        this._property.setStartBlock(this._stream.getStartBlock());
        this._property.updateSize(size);
    }

    DocumentProperty getDocumentProperty() {
        return this._property;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Object[] getViewableArray() {
        String result = "<NO DATA>";
        if (getSize() > 0) {
            byte[] data = new byte[getSize()];
            int offset = 0;
            for (ByteBuffer buffer : this._stream) {
                int length = Math.min(this._block_size, data.length - offset);
                buffer.get(data, offset, length);
                offset += length;
            }
            result = HexDump.dump(data, 0L, 0);
        }
        return new String[]{result};
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public Iterator<Object> getViewableIterator() {
        return Collections.emptyList().iterator();
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public boolean preferArray() {
        return true;
    }

    @Override // org.apache.poi.poifs.dev.POIFSViewable
    public String getShortDescription() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Document: \"").append(this._property.getName()).append("\"");
        buffer.append(" size = ").append(getSize());
        return buffer.toString();
    }
}
