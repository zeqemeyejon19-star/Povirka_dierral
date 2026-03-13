package org.apache.poi.poifs.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.BitSet;
import javax.crypto.Cipher;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.TempFile;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class ChunkedCipherOutputStream extends FilterOutputStream {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ChunkedCipherOutputStream.class);
    private static final int STREAMING = -1;
    private final byte[] chunk;
    private final int chunkBits;
    private final int chunkSize;
    private Cipher cipher;
    private final DirectoryNode dir;
    private final File fileOut;
    private boolean isClosed;
    private final BitSet plainByteFlags;
    private long pos;
    private long totalPos;
    private long written;

    protected abstract void calculateChecksum(File file, int i) throws GeneralSecurityException, IOException;

    protected abstract void createEncryptionInfoEntry(DirectoryNode directoryNode, File file) throws GeneralSecurityException, IOException;

    protected abstract Cipher initCipherForBlock(Cipher cipher, int i, boolean z) throws GeneralSecurityException, IOException;

    public ChunkedCipherOutputStream(DirectoryNode dir, int chunkSize) throws GeneralSecurityException, IOException {
        super(null);
        this.isClosed = false;
        this.chunkSize = chunkSize;
        int cs = chunkSize == -1 ? 4096 : chunkSize;
        this.chunk = new byte[cs];
        this.plainByteFlags = new BitSet(cs);
        this.chunkBits = Integer.bitCount(cs - 1);
        File fileCreateTempFile = TempFile.createTempFile("encrypted_package", "crypt");
        this.fileOut = fileCreateTempFile;
        fileCreateTempFile.deleteOnExit();
        this.out = new FileOutputStream(fileCreateTempFile);
        this.dir = dir;
        this.cipher = initCipherForBlock(null, 0, false);
    }

    public ChunkedCipherOutputStream(OutputStream stream, int chunkSize) throws GeneralSecurityException, IOException {
        super(stream);
        this.isClosed = false;
        this.chunkSize = chunkSize;
        int cs = chunkSize == -1 ? 4096 : chunkSize;
        this.chunk = new byte[cs];
        this.plainByteFlags = new BitSet(cs);
        this.chunkBits = Integer.bitCount(cs - 1);
        this.fileOut = null;
        this.dir = null;
        this.cipher = initCipherForBlock(null, 0, false);
    }

    public final Cipher initCipherForBlock(int block, boolean lastChunk) throws GeneralSecurityException, IOException {
        return initCipherForBlock(this.cipher, block, lastChunk);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b});
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        write(b, off, len, false);
    }

    public void writePlain(byte[] b, int off, int len) throws IOException {
        write(b, off, len, true);
    }

    protected void write(byte[] b, int off, int len, boolean writePlain) throws IOException {
        if (len == 0) {
            return;
        }
        if (len < 0 || b.length < off + len) {
            throw new IOException("not enough bytes in your input buffer");
        }
        int chunkMask = getChunkMask();
        while (len > 0) {
            int posInChunk = (int) (this.pos & ((long) chunkMask));
            int nextLen = Math.min(this.chunk.length - posInChunk, len);
            System.arraycopy(b, off, this.chunk, posInChunk, nextLen);
            if (writePlain) {
                this.plainByteFlags.set(posInChunk, posInChunk + nextLen);
            }
            long j = this.pos + ((long) nextLen);
            this.pos = j;
            this.totalPos += (long) nextLen;
            off += nextLen;
            len -= nextLen;
            if ((j & ((long) chunkMask)) == 0) {
                writeChunk(len > 0);
            }
        }
    }

    protected int getChunkMask() {
        return this.chunk.length - 1;
    }

    protected void writeChunk(boolean continued) throws IOException {
        boolean lastChunk;
        long j = this.pos;
        if (j == 0 || this.totalPos == this.written) {
            return;
        }
        int posInChunk = (int) (j & ((long) getChunkMask()));
        long oldPos = this.pos;
        int index = (int) (oldPos >> this.chunkBits);
        if (posInChunk == 0) {
            index--;
            posInChunk = this.chunk.length;
            lastChunk = false;
        } else {
            lastChunk = true;
        }
        boolean doFinal = true;
        try {
            this.pos = 0L;
            if (this.chunkSize == -1) {
                if (continued) {
                    doFinal = false;
                }
            } else {
                this.cipher = initCipherForBlock(this.cipher, index, lastChunk);
                this.pos = oldPos;
            }
            int ciLen = invokeCipher(posInChunk, doFinal);
            this.out.write(this.chunk, 0, ciLen);
            this.plainByteFlags.clear();
            this.written += (long) ciLen;
        } catch (GeneralSecurityException e) {
            throw new IOException("can't re-/initialize cipher", e);
        }
    }

    protected int invokeCipher(int posInChunk, boolean doFinal) throws GeneralSecurityException {
        byte[] plain = this.plainByteFlags.isEmpty() ? null : (byte[]) this.chunk.clone();
        Cipher cipher = this.cipher;
        byte[] bArr = this.chunk;
        int ciLen = doFinal ? cipher.doFinal(bArr, 0, posInChunk, bArr) : cipher.update(bArr, 0, posInChunk, bArr);
        int i = this.plainByteFlags.nextSetBit(0);
        while (i >= 0 && i < posInChunk) {
            this.chunk[i] = plain[i];
            i = this.plainByteFlags.nextSetBit(i + 1);
        }
        return ciLen;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.isClosed) {
            LOG.log(1, "ChunkedCipherOutputStream was already closed - ignoring");
            return;
        }
        this.isClosed = true;
        try {
            writeChunk(false);
            super.close();
            File file = this.fileOut;
            if (file != null) {
                int oleStreamSize = (int) (file.length() + 8);
                calculateChecksum(this.fileOut, (int) this.pos);
                this.dir.createDocument(Decryptor.DEFAULT_POIFS_ENTRY, oleStreamSize, new EncryptedPackageWriter());
                createEncryptionInfoEntry(this.dir, this.fileOut);
            }
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    protected byte[] getChunk() {
        return this.chunk;
    }

    protected BitSet getPlainByteFlags() {
        return this.plainByteFlags;
    }

    protected long getPos() {
        return this.pos;
    }

    protected long getTotalPos() {
        return this.totalPos;
    }

    public void setNextRecordSize(int recordSize, boolean isPlain) {
    }

    private class EncryptedPackageWriter implements POIFSWriterListener {
        private EncryptedPackageWriter() {
        }

        @Override // org.apache.poi.poifs.filesystem.POIFSWriterListener
        public void processPOIFSWriterEvent(POIFSWriterEvent event) {
            try {
                OutputStream os = event.getStream();
                byte[] buf = new byte[8];
                LittleEndian.putLong(buf, 0, ChunkedCipherOutputStream.this.pos);
                os.write(buf);
                FileInputStream fis = new FileInputStream(ChunkedCipherOutputStream.this.fileOut);
                try {
                    IOUtils.copy(fis, os);
                    fis.close();
                    os.close();
                    if (!ChunkedCipherOutputStream.this.fileOut.delete()) {
                        ChunkedCipherOutputStream.LOG.log(7, "Can't delete temporary encryption file: " + ChunkedCipherOutputStream.this.fileOut);
                    }
                } catch (Throwable th) {
                    fis.close();
                    throw th;
                }
            } catch (IOException e) {
                throw new EncryptedDocumentException(e);
            }
        }
    }
}
