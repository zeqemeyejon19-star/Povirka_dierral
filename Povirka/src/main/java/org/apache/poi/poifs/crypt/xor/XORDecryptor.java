package org.apache.poi.poifs.crypt.xor;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class XORDecryptor extends Decryptor implements Cloneable {
    private long length = -1;
    private int chunkSize = 512;

    protected XORDecryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public boolean verifyPassword(String password) {
        XOREncryptionVerifier ver = (XOREncryptionVerifier) getEncryptionInfo().getVerifier();
        int keyVer = LittleEndian.getUShort(ver.getEncryptedKey());
        int verifierVer = LittleEndian.getUShort(ver.getEncryptedVerifier());
        int keyComp = CryptoFunctions.createXorKey1(password);
        int verifierComp = CryptoFunctions.createXorVerifier1(password);
        if (keyVer == keyComp && verifierVer == verifierComp) {
            byte[] xorArray = CryptoFunctions.createXorArray1(password);
            setSecretKey(new SecretKeySpec(xorArray, "XOR"));
            return true;
        }
        return false;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public Cipher initCipherForBlock(Cipher cipher, int block) throws GeneralSecurityException {
        return null;
    }

    protected static Cipher initCipherForBlock(Cipher cipher, int block, EncryptionInfo encryptionInfo, SecretKey skey, int encryptMode) throws GeneralSecurityException {
        return null;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public ChunkedCipherInputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        throw new EncryptedDocumentException("not supported");
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public InputStream getDataStream(InputStream stream, int size, int initialPos) throws GeneralSecurityException, IOException {
        return new XORCipherInputStream(stream, initialPos);
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public long getLength() {
        long j = this.length;
        if (j == -1) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return j;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public XORDecryptor clone() throws CloneNotSupportedException {
        return (XORDecryptor) super.clone();
    }

    private class XORCipherInputStream extends ChunkedCipherInputStream {
        private final int initialOffset;
        private int recordEnd;
        private int recordStart;

        public XORCipherInputStream(InputStream stream, int initialPos) throws GeneralSecurityException {
            super(stream, 2147483647L, XORDecryptor.this.chunkSize);
            this.recordStart = 0;
            this.recordEnd = 0;
            this.initialOffset = initialPos;
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        protected Cipher initCipherForBlock(Cipher existing, int block) throws GeneralSecurityException {
            return XORDecryptor.this.initCipherForBlock(existing, block);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        protected int invokeCipher(int totalBytes, boolean doFinal) {
            int pos = (int) getPos();
            byte[] xorArray = XORDecryptor.this.getEncryptionInfo().getDecryptor().getSecretKey().getEncoded();
            byte[] chunk = getChunk();
            byte[] plain = getPlain();
            int posInChunk = getChunkMask() & pos;
            int xorArrayIndex = this.initialOffset + this.recordEnd + (pos - this.recordStart);
            for (int i = 0; pos + i < this.recordEnd && i < totalBytes; i++) {
                byte value = plain[posInChunk + i];
                chunk[posInChunk + i] = (byte) (xorArray[(xorArrayIndex + i) & 15] ^ rotateLeft(value, 3));
            }
            return totalBytes;
        }

        private byte rotateLeft(byte bits, int shift) {
            return (byte) (((bits & 255) << shift) | ((bits & 255) >>> (8 - shift)));
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        public void setNextRecordSize(int recordSize) {
            int pos = (int) getPos();
            byte[] chunk = getChunk();
            int chunkMask = getChunkMask();
            this.recordStart = pos;
            this.recordEnd = pos + recordSize;
            int nextBytes = Math.min(recordSize, chunk.length - (pos & chunkMask));
            invokeCipher(nextBytes, true);
        }
    }
}
