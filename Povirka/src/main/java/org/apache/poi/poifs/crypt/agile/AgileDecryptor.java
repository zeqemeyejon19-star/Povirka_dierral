package org.apache.poi.poifs.crypt.agile;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Iterator;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionVerifier;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.RefNPtg;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class AgileDecryptor extends Decryptor implements Cloneable {
    private long _length = -1;
    static final byte[] kVerifierInputBlock = {-2, -89, -46, 118, Area3DPtg.sid, 75, -98, 121};
    static final byte[] kHashedVerifierBlock = {-41, -86, IntersectionPtg.sid, 109, 48, 97, 52, 78};
    static final byte[] kCryptoKeyBlock = {PercentPtg.sid, 110, 11, -25, -85, -84, -48, -42};
    static final byte[] kIntegrityKeyBlock = {95, -78, -83, 1, 12, -71, -31, -10};
    static final byte[] kIntegrityValueBlock = {-96, 103, 127, 2, -78, RefNPtg.sid, -124, 51};

    protected AgileDecryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public boolean verifyPassword(String password) throws GeneralSecurityException {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier) getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        int blockSize = header.getBlockSize();
        byte[] pwHash = CryptoFunctions.hashPassword(password, ver.getHashAlgorithm(), ver.getSalt(), ver.getSpinCount());
        byte[] verfierInputEnc = hashInput(ver, pwHash, kVerifierInputBlock, ver.getEncryptedVerifier(), 2);
        setVerifier(verfierInputEnc);
        MessageDigest hashMD = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
        byte[] verifierHash = hashMD.digest(verfierInputEnc);
        byte[] verifierHashDec = hashInput(ver, pwHash, kHashedVerifierBlock, ver.getEncryptedVerifierHash(), 2);
        byte[] verifierHashDec2 = CryptoFunctions.getBlock0(verifierHashDec, ver.getHashAlgorithm().hashSize);
        byte[] keyspec = hashInput(ver, pwHash, kCryptoKeyBlock, ver.getEncryptedKey(), 2);
        SecretKeySpec secretKey = new SecretKeySpec(CryptoFunctions.getBlock0(keyspec, header.getKeySize() / 8), header.getCipherAlgorithm().jceId);
        byte[] vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), kIntegrityKeyBlock, blockSize);
        CipherAlgorithm cipherAlgo = header.getCipherAlgorithm();
        Cipher cipher = CryptoFunctions.getCipher(secretKey, cipherAlgo, header.getChainingMode(), vec, 2);
        byte[] hmacKey = cipher.doFinal(header.getEncryptedHmacKey());
        byte[] hmacKey2 = CryptoFunctions.getBlock0(hmacKey, header.getHashAlgorithm().hashSize);
        byte[] vec2 = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), kIntegrityValueBlock, blockSize);
        Cipher cipher2 = CryptoFunctions.getCipher(secretKey, cipherAlgo, ver.getChainingMode(), vec2, 2);
        byte[] hmacValue = cipher2.doFinal(header.getEncryptedHmacValue());
        byte[] hmacValue2 = CryptoFunctions.getBlock0(hmacValue, header.getHashAlgorithm().hashSize);
        if (Arrays.equals(verifierHashDec2, verifierHash)) {
            setSecretKey(secretKey);
            setIntegrityHmacKey(hmacKey2);
            setIntegrityHmacValue(hmacValue2);
            return true;
        }
        return false;
    }

    public boolean verifyPassword(KeyPair keyPair, X509Certificate x509) throws GeneralSecurityException {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier) getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        HashAlgorithm hashAlgo = header.getHashAlgorithm();
        CipherAlgorithm cipherAlgo = header.getCipherAlgorithm();
        int blockSize = header.getBlockSize();
        AgileEncryptionVerifier.AgileCertificateEntry ace = null;
        Iterator<AgileEncryptionVerifier.AgileCertificateEntry> it = ver.getCertificates().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AgileEncryptionVerifier.AgileCertificateEntry aceEntry = it.next();
            if (x509.equals(aceEntry.x509)) {
                ace = aceEntry;
                break;
            }
        }
        if (ace == null) {
            return false;
        }
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, keyPair.getPrivate());
        byte[] keyspec = cipher.doFinal(ace.encryptedKey);
        SecretKeySpec secretKey = new SecretKeySpec(keyspec, ver.getCipherAlgorithm().jceId);
        Mac x509Hmac = CryptoFunctions.getMac(hashAlgo);
        x509Hmac.init(secretKey);
        byte[] certVerifier = x509Hmac.doFinal(ace.x509.getEncoded());
        byte[] vec = CryptoFunctions.generateIv(hashAlgo, header.getKeySalt(), kIntegrityKeyBlock, blockSize);
        byte[] hmacKey = CryptoFunctions.getCipher(secretKey, cipherAlgo, header.getChainingMode(), vec, 2).doFinal(header.getEncryptedHmacKey());
        byte[] hmacKey2 = CryptoFunctions.getBlock0(hmacKey, hashAlgo.hashSize);
        byte[] hmacKey3 = header.getKeySalt();
        byte[] vec2 = CryptoFunctions.generateIv(hashAlgo, hmacKey3, kIntegrityValueBlock, blockSize);
        byte[] hmacValue = CryptoFunctions.getCipher(secretKey, cipherAlgo, header.getChainingMode(), vec2, 2).doFinal(header.getEncryptedHmacValue());
        byte[] hmacValue2 = CryptoFunctions.getBlock0(hmacValue, hashAlgo.hashSize);
        if (Arrays.equals(ace.certVerifier, certVerifier)) {
            setSecretKey(secretKey);
            setIntegrityHmacKey(hmacKey2);
            setIntegrityHmacValue(hmacValue2);
            return true;
        }
        return false;
    }

    protected static int getNextBlockSize(int inputLen, int blockSize) {
        int fillSize = blockSize;
        while (fillSize < inputLen) {
            fillSize += blockSize;
        }
        return fillSize;
    }

    static byte[] hashInput(AgileEncryptionVerifier ver, byte[] pwHash, byte[] blockKey, byte[] inputKey, int cipherMode) {
        CipherAlgorithm cipherAlgo = ver.getCipherAlgorithm();
        ChainingMode chainMode = ver.getChainingMode();
        int keySize = ver.getKeySize() / 8;
        int blockSize = ver.getBlockSize();
        HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        byte[] intermedKey = CryptoFunctions.generateKey(pwHash, hashAlgo, blockKey, keySize);
        SecretKey skey = new SecretKeySpec(intermedKey, cipherAlgo.jceId);
        byte[] iv = CryptoFunctions.generateIv(hashAlgo, ver.getSalt(), null, blockSize);
        Cipher cipher = CryptoFunctions.getCipher(skey, cipherAlgo, chainMode, iv, cipherMode);
        try {
        } catch (GeneralSecurityException e) {
            e = e;
        }
        try {
            byte[] hashFinal = cipher.doFinal(CryptoFunctions.getBlock0(inputKey, getNextBlockSize(inputKey.length, blockSize)));
            return hashFinal;
        } catch (GeneralSecurityException e2) {
            e = e2;
            throw new EncryptedDocumentException(e);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public InputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        DocumentInputStream dis = dir.createDocumentInputStream(Decryptor.DEFAULT_POIFS_ENTRY);
        this._length = dis.readLong();
        return new AgileCipherInputStream(dis, this._length);
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public long getLength() {
        long j = this._length;
        if (j == -1) {
            throw new IllegalStateException("EcmaDecryptor.getDataStream() was not called");
        }
        return j;
    }

    protected static Cipher initCipherForBlock(Cipher existing, int block, boolean lastChunk, EncryptionInfo encryptionInfo, SecretKey skey, int encryptionMode) throws GeneralSecurityException {
        AlgorithmParameterSpec aps;
        EncryptionHeader header = encryptionInfo.getHeader();
        String padding = lastChunk ? "PKCS5Padding" : "NoPadding";
        if (existing == null || !existing.getAlgorithm().endsWith(padding)) {
            existing = CryptoFunctions.getCipher(skey, header.getCipherAlgorithm(), header.getChainingMode(), header.getKeySalt(), encryptionMode, padding);
        }
        byte[] blockKey = new byte[4];
        LittleEndian.putInt(blockKey, 0, block);
        byte[] iv = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), blockKey, header.getBlockSize());
        if (header.getCipherAlgorithm() == CipherAlgorithm.rc2) {
            aps = new RC2ParameterSpec(skey.getEncoded().length * 8, iv);
        } else {
            aps = new IvParameterSpec(iv);
        }
        existing.init(encryptionMode, skey, aps);
        return existing;
    }

    private class AgileCipherInputStream extends ChunkedCipherInputStream {
        public AgileCipherInputStream(DocumentInputStream stream, long size) throws GeneralSecurityException {
            super(stream, size, 4096);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        protected Cipher initCipherForBlock(Cipher cipher, int block) throws GeneralSecurityException {
            return AgileDecryptor.initCipherForBlock(cipher, block, false, AgileDecryptor.this.getEncryptionInfo(), AgileDecryptor.this.getSecretKey(), 2);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public AgileDecryptor clone() throws CloneNotSupportedException {
        return (AgileDecryptor) super.clone();
    }
}
