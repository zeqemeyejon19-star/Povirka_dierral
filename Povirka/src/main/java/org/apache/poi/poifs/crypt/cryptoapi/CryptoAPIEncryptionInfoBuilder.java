package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.IOException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class CryptoAPIEncryptionInfoBuilder implements EncryptionInfoBuilder {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    @Override // org.apache.poi.poifs.crypt.EncryptionInfoBuilder
    public void initialize(EncryptionInfo info, LittleEndianInput dis) throws IOException {
        dis.readInt();
        CryptoAPIEncryptionHeader header = new CryptoAPIEncryptionHeader(dis);
        info.setHeader(header);
        info.setVerifier(new CryptoAPIEncryptionVerifier(dis, header));
        CryptoAPIDecryptor dec = new CryptoAPIDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        CryptoAPIEncryptor enc = new CryptoAPIEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionInfoBuilder
    public void initialize(EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.rc4;
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (keyBits == -1) {
            keyBits = 40;
        }
        if (cipherAlgorithm != CipherAlgorithm.rc4 || hashAlgorithm != HashAlgorithm.sha1) {
            throw new AssertionError();
        }
        CipherAlgorithm cipherAlgorithm2 = cipherAlgorithm;
        HashAlgorithm hashAlgorithm2 = hashAlgorithm;
        int i = keyBits;
        info.setHeader(new CryptoAPIEncryptionHeader(cipherAlgorithm2, hashAlgorithm2, i, blockSize, chainingMode));
        info.setVerifier(new CryptoAPIEncryptionVerifier(cipherAlgorithm2, hashAlgorithm2, i, blockSize, chainingMode));
        CryptoAPIDecryptor dec = new CryptoAPIDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        CryptoAPIEncryptor enc = new CryptoAPIEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }
}
