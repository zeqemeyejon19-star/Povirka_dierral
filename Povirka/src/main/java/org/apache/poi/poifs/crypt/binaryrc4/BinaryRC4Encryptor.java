package org.apache.poi.poifs.crypt.binaryrc4;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class BinaryRC4Encryptor extends Encryptor implements Cloneable {
    private int chunkSize = 512;

    protected BinaryRC4Encryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password) {
        Random r = new SecureRandom();
        byte[] salt = new byte[16];
        byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        confirmPassword(password, null, null, verifier, salt, null);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password, byte[] keySpec, byte[] keySalt, byte[] verifier, byte[] verifierSalt, byte[] integritySalt) {
        BinaryRC4EncryptionVerifier ver = (BinaryRC4EncryptionVerifier) getEncryptionInfo().getVerifier();
        ver.setSalt(verifierSalt);
        SecretKey skey = BinaryRC4Decryptor.generateSecretKey(password, ver);
        setSecretKey(skey);
        try {
            Cipher cipher = BinaryRC4Decryptor.initCipherForBlock(null, 0, getEncryptionInfo(), skey, 1);
            byte[] encryptedVerifier = new byte[16];
            cipher.update(verifier, 0, 16, encryptedVerifier);
            ver.setEncryptedVerifier(encryptedVerifier);
            HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            byte[] calcVerifierHash = hashAlg.digest(verifier);
            byte[] encryptedVerifierHash = cipher.doFinal(calcVerifierHash);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        } catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public OutputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        OutputStream countStream = new BinaryRC4CipherOutputStream(dir);
        return countStream;
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public BinaryRC4CipherOutputStream getDataStream(OutputStream stream, int initialOffset) throws GeneralSecurityException, IOException {
        return new BinaryRC4CipherOutputStream(stream);
    }

    protected int getKeySizeInBytes() {
        return getEncryptionInfo().getHeader().getKeySize() / 8;
    }

    protected void createEncryptionInfoEntry(DirectoryNode dir) throws IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final EncryptionInfo info = getEncryptionInfo();
        final BinaryRC4EncryptionHeader header = (BinaryRC4EncryptionHeader) info.getHeader();
        final BinaryRC4EncryptionVerifier verifier = (BinaryRC4EncryptionVerifier) info.getVerifier();
        EncryptionRecord er = new EncryptionRecord() { // from class: org.apache.poi.poifs.crypt.binaryrc4.BinaryRC4Encryptor.1
            @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
            public void write(LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                header.write(bos);
                verifier.write(bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public BinaryRC4Encryptor clone() throws CloneNotSupportedException {
        return (BinaryRC4Encryptor) super.clone();
    }

    protected class BinaryRC4CipherOutputStream extends ChunkedCipherOutputStream {
        public BinaryRC4CipherOutputStream(OutputStream stream) throws GeneralSecurityException, IOException {
            super(stream, BinaryRC4Encryptor.this.chunkSize);
        }

        public BinaryRC4CipherOutputStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
            super(dir, BinaryRC4Encryptor.this.chunkSize);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected Cipher initCipherForBlock(Cipher cipher, int block, boolean lastChunk) throws GeneralSecurityException {
            return BinaryRC4Decryptor.initCipherForBlock(cipher, block, BinaryRC4Encryptor.this.getEncryptionInfo(), BinaryRC4Encryptor.this.getSecretKey(), 1);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void calculateChecksum(File file, int i) {
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws GeneralSecurityException, IOException {
            BinaryRC4Encryptor.this.createEncryptionInfoEntry(dir);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            writeChunk(false);
            super.flush();
        }
    }
}
