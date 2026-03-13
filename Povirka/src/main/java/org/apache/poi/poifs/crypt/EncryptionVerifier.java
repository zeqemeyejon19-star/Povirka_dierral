package org.apache.poi.poifs.crypt;

import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public abstract class EncryptionVerifier implements Cloneable {
    private ChainingMode chainingMode;
    private CipherAlgorithm cipherAlgorithm;
    private byte[] encryptedKey;
    private byte[] encryptedVerifier;
    private byte[] encryptedVerifierHash;
    private HashAlgorithm hashAlgorithm;
    private byte[] salt;
    private int spinCount;

    protected EncryptionVerifier() {
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public byte[] getEncryptedVerifier() {
        return this.encryptedVerifier;
    }

    public byte[] getEncryptedVerifierHash() {
        return this.encryptedVerifierHash;
    }

    public int getSpinCount() {
        return this.spinCount;
    }

    @Removal(version = "3.18")
    public int getCipherMode() {
        return this.chainingMode.ecmaId;
    }

    public int getAlgorithm() {
        return this.cipherAlgorithm.ecmaId;
    }

    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }

    public CipherAlgorithm getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }

    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public ChainingMode getChainingMode() {
        return this.chainingMode;
    }

    protected void setSalt(byte[] salt) {
        this.salt = salt == null ? null : (byte[]) salt.clone();
    }

    protected void setEncryptedVerifier(byte[] encryptedVerifier) {
        this.encryptedVerifier = encryptedVerifier == null ? null : (byte[]) encryptedVerifier.clone();
    }

    protected void setEncryptedVerifierHash(byte[] encryptedVerifierHash) {
        this.encryptedVerifierHash = encryptedVerifierHash == null ? null : (byte[]) encryptedVerifierHash.clone();
    }

    protected void setEncryptedKey(byte[] encryptedKey) {
        this.encryptedKey = encryptedKey == null ? null : (byte[]) encryptedKey.clone();
    }

    protected void setSpinCount(int spinCount) {
        this.spinCount = spinCount;
    }

    protected void setCipherAlgorithm(CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    protected void setChainingMode(ChainingMode chainingMode) {
        this.chainingMode = chainingMode;
    }

    protected void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override // 
    public EncryptionVerifier clone() throws CloneNotSupportedException {
        EncryptionVerifier other = (EncryptionVerifier) super.clone();
        byte[] bArr = this.salt;
        other.salt = bArr == null ? null : (byte[]) bArr.clone();
        byte[] bArr2 = this.encryptedVerifier;
        other.encryptedVerifier = bArr2 == null ? null : (byte[]) bArr2.clone();
        byte[] bArr3 = this.encryptedVerifierHash;
        other.encryptedVerifierHash = bArr3 == null ? null : (byte[]) bArr3.clone();
        byte[] bArr4 = this.encryptedKey;
        other.encryptedKey = bArr4 != null ? (byte[]) bArr4.clone() : null;
        return other;
    }
}
