package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class CryptoAPIEncryptionVerifier extends StandardEncryptionVerifier implements Cloneable {
    protected CryptoAPIEncryptionVerifier(LittleEndianInput is, CryptoAPIEncryptionHeader header) {
        super(is, header);
    }

    protected CryptoAPIEncryptionVerifier(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        super(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }

    @Override // org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier, org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setSalt(byte[] salt) {
        super.setSalt(salt);
    }

    @Override // org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier, org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setEncryptedVerifier(byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }

    @Override // org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier, org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setEncryptedVerifierHash(byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }

    @Override // org.apache.poi.poifs.crypt.standard.StandardEncryptionVerifier, org.apache.poi.poifs.crypt.EncryptionVerifier
    public CryptoAPIEncryptionVerifier clone() throws CloneNotSupportedException {
        return (CryptoAPIEncryptionVerifier) super.clone();
    }
}
