package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.BlockStore;

/* JADX INFO: loaded from: classes.dex */
public class NPOIFSStream implements Iterable<ByteBuffer> {
    private BlockStore blockStore;
    private OutputStream outStream;
    private int startBlock;

    public NPOIFSStream(BlockStore blockStore, int startBlock) {
        this.blockStore = blockStore;
        this.startBlock = startBlock;
    }

    public NPOIFSStream(BlockStore blockStore) {
        this.blockStore = blockStore;
        this.startBlock = -2;
    }

    public int getStartBlock() {
        return this.startBlock;
    }

    @Override // java.lang.Iterable
    public Iterator<ByteBuffer> iterator() {
        return getBlockIterator();
    }

    public Iterator<ByteBuffer> getBlockIterator() {
        if (this.startBlock == -2) {
            throw new IllegalStateException("Can't read from a new stream before it has been written to");
        }
        return new StreamBlockByteBufferIterator(this.startBlock);
    }

    public void updateContents(byte[] contents) throws IOException {
        OutputStream os = getOutputStream();
        os.write(contents);
        os.close();
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.outStream == null) {
            this.outStream = new StreamBlockByteBuffer();
        }
        return this.outStream;
    }

    public void free() throws IOException {
        BlockStore.ChainLoopDetector loopDetector = this.blockStore.getChainLoopDetector();
        free(loopDetector);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void free(BlockStore.ChainLoopDetector loopDetector) {
        int nextBlock = this.startBlock;
        while (nextBlock != -2) {
            int thisBlock = nextBlock;
            loopDetector.claim(thisBlock);
            nextBlock = this.blockStore.getNextBlock(thisBlock);
            this.blockStore.setNextBlock(thisBlock, -1);
        }
        this.startBlock = -2;
    }

    protected class StreamBlockByteBufferIterator implements Iterator<ByteBuffer> {
        private BlockStore.ChainLoopDetector loopDetector;
        private int nextBlock;

        protected StreamBlockByteBufferIterator(int firstBlock) {
            this.nextBlock = firstBlock;
            try {
                this.loopDetector = NPOIFSStream.this.blockStore.getChainLoopDetector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.nextBlock == -2) {
                return false;
            }
            return true;
        }

        @Override // java.util.Iterator
        public ByteBuffer next() {
            int i = this.nextBlock;
            if (i == -2) {
                throw new IndexOutOfBoundsException("Can't read past the end of the stream");
            }
            try {
                this.loopDetector.claim(i);
                ByteBuffer data = NPOIFSStream.this.blockStore.getBlockAt(this.nextBlock);
                this.nextBlock = NPOIFSStream.this.blockStore.getNextBlock(this.nextBlock);
                return data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected class StreamBlockByteBuffer extends OutputStream {
        ByteBuffer buffer;
        BlockStore.ChainLoopDetector loopDetector;
        int nextBlock;
        byte[] oneByte = new byte[1];
        int prevBlock = -2;

        protected StreamBlockByteBuffer() throws IOException {
            this.loopDetector = NPOIFSStream.this.blockStore.getChainLoopDetector();
            this.nextBlock = NPOIFSStream.this.startBlock;
        }

        protected void createBlockIfNeeded() throws IOException {
            ByteBuffer byteBuffer = this.buffer;
            if (byteBuffer == null || !byteBuffer.hasRemaining()) {
                int thisBlock = this.nextBlock;
                if (thisBlock == -2) {
                    thisBlock = NPOIFSStream.this.blockStore.getFreeBlock();
                    this.loopDetector.claim(thisBlock);
                    this.nextBlock = -2;
                    if (this.prevBlock != -2) {
                        NPOIFSStream.this.blockStore.setNextBlock(this.prevBlock, thisBlock);
                    }
                    NPOIFSStream.this.blockStore.setNextBlock(thisBlock, -2);
                    if (NPOIFSStream.this.startBlock == -2) {
                        NPOIFSStream.this.startBlock = thisBlock;
                    }
                } else {
                    this.loopDetector.claim(thisBlock);
                    this.nextBlock = NPOIFSStream.this.blockStore.getNextBlock(thisBlock);
                }
                this.buffer = NPOIFSStream.this.blockStore.createBlockIfNeeded(thisBlock);
                this.prevBlock = thisBlock;
            }
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            byte[] bArr = this.oneByte;
            bArr[0] = (byte) (b & 255);
            write(bArr);
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            do {
                createBlockIfNeeded();
                int writeBytes = Math.min(this.buffer.remaining(), len);
                this.buffer.put(b, off, writeBytes);
                off += writeBytes;
                len -= writeBytes;
            } while (len > 0);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            NPOIFSStream toFree = new NPOIFSStream(NPOIFSStream.this.blockStore, this.nextBlock);
            toFree.free(this.loopDetector);
            if (this.prevBlock != -2) {
                NPOIFSStream.this.blockStore.setNextBlock(this.prevBlock, -2);
            }
        }
    }
}
