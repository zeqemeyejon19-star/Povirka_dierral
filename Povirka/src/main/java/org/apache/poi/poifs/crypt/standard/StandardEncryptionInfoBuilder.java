package org.apache.poi.poifs.crypt.standard;

import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class StandardEncryptionInfoBuilder implements EncryptionInfoBuilder {
    @Override // org.apache.poi.poifs.crypt.EncryptionInfoBuilder
    public void initialize(EncryptionInfo info, LittleEndianInput dis) throws IOException {
        dis.readInt();
        StandardEncryptionHeader header = new StandardEncryptionHeader(dis);
        info.setHeader(header);
        info.setVerifier(new StandardEncryptionVerifier(dis, header));
        if (info.getVersionMinor() == 2) {
            if (info.getVersionMajor() == 3 || info.getVersionMajor() == 4) {
                StandardDecryptor dec = new StandardDecryptor();
                dec.setEncryptionInfo(info);
                info.setDecryptor(dec);
            }
        }
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionInfoBuilder
    public void initialize(EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.aes128;
        }
        if (cipherAlgorithm != CipherAlgorithm.aes128 && cipherAlgorithm != CipherAlgorithm.aes192 && cipherAlgorithm != CipherAlgorithm.aes256) {
            throw new EncryptedDocumentException("Standard encryption only supports AES128/192/256.");
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (hashAlgorithm != HashAlgorithm.sha1) {
            throw new EncryptedDocumentException("Standard encryption only supports SHA-1.");
        }
        if (chainingMode == null) {
            chainingMode = ChainingMode.ecb;
        }
        if (chainingMode != ChainingMode.ecb) {
            throw new EncryptedDocumentException("Standard encryption only supports ECB chaining.");
        }
        if (keyBits == -1) {
            keyBits = cipherAlgorithm.defaultKeySize;
        }
        if (blockSize == -1) {
            blockSize = cipherAlgorithm.blockSize;
        }
        int[] arr$ = cipherAlgorithm.allowedKeySize;
        int len$ = arr$.length;
        boolean found = false;
        for (int i$ = 0; i$ < len$; i$++) {
            int ks = arr$[i$];
            found |= ks == keyBits;
        }
        if (!found) {
            throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for Cipher " + cipherAlgorithm);
        }
        CipherAlgorithm cipherAlgorithm2 = cipherAlgorithm;
        HashAlgorithm hashAlgorithm2 = hashAlgorithm;
        int i = keyBits;
        int i2 = blockSize;
        ChainingMode chainingMode2 = chainingMode;
        info.setHeader(new StandardEncryptionHeader(cipherAlgorithm2, hashAlgorithm2, i, i2, chainingMode2));
        info.setVerifier(new StandardEncryptionVerifier(cipherAlgorithm2, hashAlgorithm2, i, i2, chainingMode2));
        StandardDecryptor dec = new StandardDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        StandardEncryptor enc = new StandardEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
