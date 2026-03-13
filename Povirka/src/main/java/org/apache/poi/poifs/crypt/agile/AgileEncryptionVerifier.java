package org.apache.poi.poifs.crypt.agile;

import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;

/* JADX INFO: loaded from: classes.dex */
public class AgileEncryptionVerifier extends EncryptionVerifier implements Cloneable {
    private int blockSize;
    private List<AgileCertificateEntry> certList;
    private int keyBits;

    public static class AgileCertificateEntry {
        byte[] certVerifier;
        byte[] encryptedKey;
        X509Certificate x509;
    }

    public AgileEncryptionVerifier(String descriptor) {
        this(AgileEncryptionInfoBuilder.parseDescriptor(descriptor));
    }

    protected AgileEncryptionVerifier(EncryptionDocument ed) {
        this.certList = new ArrayList();
        this.keyBits = -1;
        this.blockSize = -1;
        Iterator<CTKeyEncryptor> encList = ed.getEncryption().getKeyEncryptors().getKeyEncryptorList().iterator();
        try {
            CTPasswordKeyEncryptor keyData = encList.next().getEncryptedPasswordKey();
            if (keyData == null) {
                try {
                    throw new NullPointerException("encryptedKey not set");
                } catch (Exception e) {
                    e = e;
                    throw new EncryptedDocumentException("Unable to parse keyData", e);
                }
            }
            int kb = (int) keyData.getKeyBits();
            CipherAlgorithm ca = CipherAlgorithm.fromXmlId(keyData.getCipherAlgorithm().toString(), kb);
            setCipherAlgorithm(ca);
            setKeySize(kb);
            int blockSize = keyData.getBlockSize();
            setBlockSize(blockSize);
            int hashSize = keyData.getHashSize();
            HashAlgorithm ha = HashAlgorithm.fromEcmaId(keyData.getHashAlgorithm().toString());
            setHashAlgorithm(ha);
            if (getHashAlgorithm().hashSize != hashSize) {
                throw new EncryptedDocumentException("Unsupported hash algorithm: " + keyData.getHashAlgorithm() + " @ " + hashSize + " bytes");
            }
            setSpinCount(keyData.getSpinCount());
            setEncryptedVerifier(keyData.getEncryptedVerifierHashInput());
            setSalt(keyData.getSaltValue());
            setEncryptedKey(keyData.getEncryptedKeyValue());
            setEncryptedVerifierHash(keyData.getEncryptedVerifierHashValue());
            int saltSize = keyData.getSaltSize();
            if (saltSize != getSalt().length) {
                throw new EncryptedDocumentException("Invalid salt size");
            }
            int iIntValue = keyData.getCipherChaining().intValue();
            if (iIntValue == 1) {
                setChainingMode(ChainingMode.cbc);
            } else if (iIntValue == 2) {
                setChainingMode(ChainingMode.cfb);
            } else {
                throw new EncryptedDocumentException("Unsupported chaining mode - " + keyData.getCipherChaining());
            }
            if (!encList.hasNext()) {
                return;
            }
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                while (encList.hasNext()) {
                    CTCertificateKeyEncryptor certKey = encList.next().getEncryptedCertificateKey();
                    AgileCertificateEntry ace = new AgileCertificateEntry();
                    ace.certVerifier = certKey.getCertVerifier();
                    ace.encryptedKey = certKey.getEncryptedKeyValue();
                    ace.x509 = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certKey.getX509Certificate()));
                    this.certList.add(ace);
                }
            } catch (GeneralSecurityException e2) {
                throw new EncryptedDocumentException("can't parse X509 certificate", e2);
            }
        } catch (Exception e3) {
            e = e3;
        }
    }

    public AgileEncryptionVerifier(CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        this.certList = new ArrayList();
        this.keyBits = -1;
        this.blockSize = -1;
        setCipherAlgorithm(cipherAlgorithm);
        setHashAlgorithm(hashAlgorithm);
        setChainingMode(chainingMode);
        setKeySize(keyBits);
        setBlockSize(blockSize);
        setSpinCount(100000);
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setSalt(byte[] salt) {
        if (salt == null || salt.length != getCipherAlgorithm().blockSize) {
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

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected void setEncryptedKey(byte[] encryptedKey) {
        super.setEncryptedKey(encryptedKey);
    }

    public void addCertificate(X509Certificate x509) {
        AgileCertificateEntry ace = new AgileCertificateEntry();
        ace.x509 = x509;
        this.certList.add(ace);
    }

    public List<AgileCertificateEntry> getCertificates() {
        return this.certList;
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    public AgileEncryptionVerifier clone() throws CloneNotSupportedException {
        AgileEncryptionVerifier other = (AgileEncryptionVerifier) super.clone();
        other.certList = new ArrayList(this.certList);
        return other;
    }

    public int getKeySize() {
        return this.keyBits;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    protected void setKeySize(int keyBits) {
        this.keyBits = keyBits;
        int[] arr$ = getCipherAlgorithm().allowedKeySize;
        for (int allowedBits : arr$) {
            if (allowedBits == keyBits) {
                return;
            }
        }
        throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for cipher " + getCipherAlgorithm());
    }

    protected void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionVerifier
    protected final void setCipherAlgorithm(CipherAlgorithm cipherAlgorithm) {
        super.setCipherAlgorithm(cipherAlgorithm);
        if (cipherAlgorithm.allowedKeySize.length == 1) {
            setKeySize(cipherAlgorithm.defaultKeySize);
        }
    }
}
