package org.apache.poi.poifs.crypt.xor;

import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class XOREncryptionVerifier extends EncryptionVerifier implements EncryptionRecord, Cloneable {
    protected XOREncryptionVerifier() {
        setEncryptedKey(new byte[2]);
        setEncryptedVerifier(new byte[2]);
    }

    protected XOREncryptionVerifier(LittleEndianInput is) {
        byte[] key = new byte[2];
        is.readFully(key);
        setEncryptedKey(key);
        byte[] verifier = new byte[2];
        is.readFully(verifier);
        setEncryptedVerifier(verifier);
    }

    @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
    public void write(LittleEndianByteArrayOutputStream bos) {
        bos.write(getEncryptedKey());
        bos.write(getEncryptedVerifier());
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    public XOREncryptionVerifier clone() throws CloneNotSupportedException {
        return (XOREncryptionVerifier) super.clone();
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected final void setEncryptedVerifier(byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected final void setEncryptedKey(byte[] encryptedKey) {
        super.setEncryptedKey(encryptedKey);
    }
}
