package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.IOException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CipherProvider;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionHeader;
import org.apache.poi.util.LittleEndianInput;

/* JADX INFO: loaded from: classes.dex */
public class CryptoAPIEncryptionHeader extends StandardEncryptionHeader implements Cloneable {
    public CryptoAPIEncryptionHeader(LittleEndianInput is) throws IOException {
        super(is);
    }

    protected CryptoAPIEncryptionHeader(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        super(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionHeader
    public void setKeySize(int keyBits) {
        boolean found = false;
        int[] arr$ = getCipherAlgorithm().allowedKeySize;
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            int size = arr$[i$];
            if (size != keyBits) {
                i$++;
            } else {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new EncryptedDocumentException("invalid keysize " + keyBits + " for cipher algorithm " + getCipherAlgorithm());
        }
        super.setKeySize(keyBits);
        if (keyBits > 40) {
            setCspName("Microsoft Enhanced Cryptographic Provider v1.0");
        } else {
            setCspName(CipherProvider.rc4.cipherProviderName);
        }
    }

    @Override // org.apache.poi.poifs.crypt.standard.StandardEncryptionHeader, org.apache.poi.poifs.crypt.EncryptionHeader
    public CryptoAPIEncryptionHeader clone() throws CloneNotSupportedException {
        return (CryptoAPIEncryptionHeader) super.clone();
    }
}
