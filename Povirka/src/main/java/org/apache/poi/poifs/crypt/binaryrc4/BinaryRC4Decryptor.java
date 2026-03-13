package org.apache.poi.poifs.crypt.binaryrc4;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class BinaryRC4Decryptor extends Decryptor implements Cloneable {
    private long length = -1;
    private int chunkSize = 512;

    private class BinaryRC4CipherInputStream extends ChunkedCipherInputStream {
        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        protected Cipher initCipherForBlock(Cipher existing, int block) throws GeneralSecurityException {
            return BinaryRC4Decryptor.this.initCipherForBlock(existing, block);
        }

        public BinaryRC4CipherInputStream(DocumentInputStream stream, long size) throws GeneralSecurityException {
            super(stream, size, BinaryRC4Decryptor.this.chunkSize);
        }

        public BinaryRC4CipherInputStream(InputStream stream, int size, int initialPos) throws GeneralSecurityException {
            super(stream, size, BinaryRC4Decryptor.this.chunkSize, initialPos);
        }
    }

    protected BinaryRC4Decryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public boolean verifyPassword(String password) {
        EncryptionVerifier ver = getEncryptionInfo().getVerifier();
        SecretKey skey = generateSecretKey(password, ver);
        try {
            Cipher cipher = initCipherForBlock(null, 0, getEncryptionInfo(), skey, 2);
            byte[] encryptedVerifier = ver.getEncryptedVerifier();
            byte[] verifier = new byte[encryptedVerifier.length];
            cipher.update(encryptedVerifier, 0, encryptedVerifier.length, verifier);
            setVerifier(verifier);
            byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            byte[] verifierHash = cipher.doFinal(encryptedVerifierHash);
            HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            byte[] calcVerifierHash = hashAlg.digest(verifier);
            if (!Arrays.equals(calcVerifierHash, verifierHash)) {
                return false;
            }
            setSecretKey(skey);
            return true;
        } catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public Cipher initCipherForBlock(Cipher cipher, int block) throws GeneralSecurityException {
        return initCipherForBlock(cipher, block, getEncryptionInfo(), getSecretKey(), 2);
    }

    protected static Cipher initCipherForBlock(Cipher cipher, int block, EncryptionInfo encryptionInfo, SecretKey skey, int encryptMode) throws GeneralSecurityException {
        EncryptionVerifier ver = encryptionInfo.getVerifier();
        HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        byte[] encKey = CryptoFunctions.generateKey(skey.getEncoded(), hashAlgo, blockKey, 16);
        SecretKey key = new SecretKeySpec(encKey, skey.getAlgorithm());
        if (cipher == null) {
            EncryptionHeader em = encryptionInfo.getHeader();
            return CryptoFunctions.getCipher(key, em.getCipherAlgorithm(), null, null, encryptMode);
        }
        cipher.init(encryptMode, key);
        return cipher;
    }

    protected static SecretKey generateSecretKey(String password, EncryptionVerifier ver) {
        if (password.length() > 255) {
            password = password.substring(0, 255);
        }
        HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        byte[] salt = ver.getSalt();
        hashAlg.reset();
        for (int i = 0; i < 16; i++) {
            hashAlg.update(hash, 0, 5);
            hashAlg.update(salt);
        }
        byte[] hash2 = new byte[5];
        System.arraycopy(hashAlg.digest(), 0, hash2, 0, 5);
        SecretKey skey = new SecretKeySpec(hash2, ver.getCipherAlgorithm().jceId);
        return skey;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public ChunkedCipherInputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        DocumentInputStream dis = dir.createDocumentInputStream(Decryptor.DEFAULT_POIFS_ENTRY);
        this.length = dis.readLong();
        return new BinaryRC4CipherInputStream(dis, this.length);
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public InputStream getDataStream(InputStream stream, int size, int initialPos) throws GeneralSecurityException, IOException {
        return new BinaryRC4CipherInputStream(stream, size, initialPos);
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
    public BinaryRC4Decryptor clone() throws CloneNotSupportedException {
        return (BinaryRC4Decryptor) super.clone();
    }
}
