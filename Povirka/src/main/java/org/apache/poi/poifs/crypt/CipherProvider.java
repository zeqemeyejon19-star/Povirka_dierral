package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;

/* JADX INFO: loaded from: classes.dex */
public enum CipherProvider {
    rc4("RC4", 1, "Microsoft Base Cryptographic Provider v1.0"),
    aes("AES", 24, "Microsoft Enhanced RSA and AES Cryptographic Provider");

    public final String cipherProviderName;
    public final int ecmaId;
    public final String jceId;

    public static CipherProvider fromEcmaId(int ecmaId) {
        CipherProvider[] arr$ = values();
        for (CipherProvider cp : arr$) {
            if (cp.ecmaId == ecmaId) {
                return cp;
            }
        }
        throw new EncryptedDocumentException("cipher provider not found");
    }

    CipherProvider(String jceId, int ecmaId, String cipherProviderName) {
        this.jceId = jceId;
        this.ecmaId = ecmaId;
        this.cipherProviderName = cipherProviderName;
    }
}
