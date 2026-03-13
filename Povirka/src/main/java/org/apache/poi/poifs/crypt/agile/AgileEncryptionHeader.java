package org.apache.poi.poifs.crypt.agile;

import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import com.microsoft.schemas.office.x2006.encryption.CTKeyData;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.HashAlgorithm;

/* JADX INFO: loaded from: classes.dex */
public class AgileEncryptionHeader extends EncryptionHeader implements Cloneable {
    private byte[] encryptedHmacKey;
    private byte[] encryptedHmacValue;

    public AgileEncryptionHeader(String descriptor) {
        this(AgileEncryptionInfoBuilder.parseDescriptor(descriptor));
    }

    protected AgileEncryptionHeader(EncryptionDocument ed) {
        try {
            CTKeyData keyData = ed.getEncryption().getKeyData();
            if (keyData == null) {
                try {
                    throw new NullPointerException("keyData not set");
                } catch (Exception e) {
                    throw new EncryptedDocumentException("Unable to parse keyData");
                }
            }
            int keyBits = (int) keyData.getKeyBits();
            CipherAlgorithm ca = CipherAlgorithm.fromXmlId(keyData.getCipherAlgorithm().toString(), keyBits);
            setCipherAlgorithm(ca);
            setCipherProvider(ca.provider);
            setKeySize(keyBits);
            setFlags(0);
            setSizeExtra(0);
            setCspName(null);
            setBlockSize(keyData.getBlockSize());
            int iIntValue = keyData.getCipherChaining().intValue();
            if (iIntValue == 1) {
                setChainingMode(ChainingMode.cbc);
            } else if (iIntValue == 2) {
                setChainingMode(ChainingMode.cfb);
            } else {
                throw new EncryptedDocumentException("Unsupported chaining mode - " + keyData.getCipherChaining());
            }
            int hashSize = keyData.getHashSize();
            HashAlgorithm ha = HashAlgorithm.fromEcmaId(keyData.getHashAlgorithm().toString());
            setHashAlgorithm(ha);
            if (getHashAlgorithm().hashSize != hashSize) {
                throw new EncryptedDocumentException("Unsupported hash algorithm: " + keyData.getHashAlgorithm() + " @ " + hashSize + " bytes");
            }
            int saltLength = keyData.getSaltSize();
            setKeySalt(keyData.getSaltValue());
            if (getKeySalt().length != saltLength) {
                throw new EncryptedDocumentException("Invalid salt length");
            }
            CTDataIntegrity di = ed.getEncryption().getDataIntegrity();
            setEncryptedHmacKey(di.getEncryptedHmacKey());
            setEncryptedHmacValue(di.getEncryptedHmacValue());
        } catch (Exception e2) {
        }
    }

    public AgileEncryptionHeader(CipherAlgorithm algorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        setCipherAlgorithm(algorithm);
        setHashAlgorithm(hashAlgorithm);
        setKeySize(keyBits);
        setBlockSize(blockSize);
        setChainingMode(chainingMode);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionHeader
    protected void setKeySalt(byte[] salt) {
        if (salt == null || salt.length != getBlockSize()) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
        super.setKeySalt(salt);
    }

    public byte[] getEncryptedHmacKey() {
        return this.encryptedHmacKey;
    }

    protected void setEncryptedHmacKey(byte[] encryptedHmacKey) {
        this.encryptedHmacKey = encryptedHmacKey == null ? null : (byte[]) encryptedHmacKey.clone();
    }

    public byte[] getEncryptedHmacValue() {
        return this.encryptedHmacValue;
    }

    protected void setEncryptedHmacValue(byte[] encryptedHmacValue) {
        this.encryptedHmacValue = encryptedHmacValue == null ? null : (byte[]) encryptedHmacValue.clone();
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionHeader
    public AgileEncryptionHeader clone() throws CloneNotSupportedException {
        AgileEncryptionHeader other = (AgileEncryptionHeader) super.clone();
        byte[] bArr = this.encryptedHmacKey;
        other.encryptedHmacKey = bArr == null ? null : (byte[]) bArr.clone();
        byte[] bArr2 = this.encryptedHmacValue;
        other.encryptedHmacValue = bArr2 != null ? (byte[]) bArr2.clone() : null;
        return other;
    }
}
