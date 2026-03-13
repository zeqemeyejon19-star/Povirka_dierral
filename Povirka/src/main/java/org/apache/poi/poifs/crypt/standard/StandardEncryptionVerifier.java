package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class StandardEncryptionVerifier extends EncryptionVerifier implements EncryptionRecord, Cloneable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int SPIN_COUNT = 50000;
    private final int verifierHashSize;

    protected StandardEncryptionVerifier(LittleEndianInput is, StandardEncryptionHeader header) {
        int saltSize = is.readInt();
        if (saltSize != 16) {
            throw new RuntimeException("Salt size != 16 !?");
        }
        byte[] salt = new byte[16];
        is.readFully(salt);
        setSalt(salt);
        byte[] encryptedVerifier = new byte[16];
        is.readFully(encryptedVerifier);
        setEncryptedVerifier(encryptedVerifier);
        this.verifierHashSize = is.readInt();
        byte[] encryptedVerifierHash = new byte[header.getCipherAlgorithm().encryptedVerifierHashLength];
        is.readFully(encryptedVerifierHash);
        setEncryptedVerifierHash(encryptedVerifierHash);
        setSpinCount(SPIN_COUNT);
        setCipherAlgorithm(header.getCipherAlgorithm());
        setChainingMode(header.getChainingMode());
        setEncryptedKey(null);
        setHashAlgorithm(header.getHashAlgorithm());
    }

    protected StandardEncryptionVerifier(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        setCipherAlgorithm(cipherAlgorithm);
        setHashAlgorithm(hashAlgorithm);
        setChainingMode(chainingMode);
        setSpinCount(SPIN_COUNT);
        this.verifierHashSize = hashAlgorithm.hashSize;
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setSalt(byte[] salt) {
        if (salt == null || salt.length != 16) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
        super.setSalt(salt);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setEncryptedVerifier(byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setEncryptedVerifierHash(byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }

    @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
    public void write(LittleEndianByteArrayOutputStream bos) {
        byte[] salt = getSalt();
        if (salt.length != 16) {
            throw new AssertionError();
        }
        bos.writeInt(salt.length);
        bos.write(salt);
        byte[] encryptedVerifier = getEncryptedVerifier();
        if (encryptedVerifier.length != 16) {
            throw new AssertionError();
        }
        bos.write(encryptedVerifier);
        bos.writeInt(20);
        byte[] encryptedVerifierHash = getEncryptedVerifierHash();
        if (encryptedVerifierHash.length != getCipherAlgorithm().encryptedVerifierHashLength) {
            throw new AssertionError();
        }
        bos.write(encryptedVerifierHash);
    }

    protected int getVerifierHashSize() {
        return this.verifierHashSize;
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    public StandardEncryptionVerifier clone() throws CloneNotSupportedException {
        return (StandardEncryptionVerifier) super.clone();
    }
}
