package org.apache.poi.poifs.crypt.agile;

import com.microsoft.schemas.office.x2006.encryption.CTDataIntegrity;
import com.microsoft.schemas.office.x2006.encryption.CTEncryption;
import com.microsoft.schemas.office.x2006.encryption.CTKeyData;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptor;
import com.microsoft.schemas.office.x2006.encryption.CTKeyEncryptors;
import com.microsoft.schemas.office.x2006.encryption.EncryptionDocument;
import com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm;
import com.microsoft.schemas.office.x2006.encryption.STCipherChaining;
import com.microsoft.schemas.office.x2006.encryption.STHashAlgorithm;
import com.microsoft.schemas.office.x2006.keyEncryptor.certificate.CTCertificateKeyEncryptor;
import com.microsoft.schemas.office.x2006.keyEncryptor.password.CTPasswordKeyEncryptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionVerifier;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.xmlbeans.XmlOptions;

/* JADX INFO: loaded from: classes.dex */
public class AgileEncryptor extends Encryptor implements Cloneable {
    private byte[] integritySalt;
    private byte[] pwHash;
    private final CTKeyEncryptor.Uri.Enum passwordUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_PASSWORD;
    private final CTKeyEncryptor.Uri.Enum certificateUri = CTKeyEncryptor.Uri.HTTP_SCHEMAS_MICROSOFT_COM_OFFICE_2006_KEY_ENCRYPTOR_CERTIFICATE;

    protected AgileEncryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password) {
        Random r = new SecureRandom();
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        int blockSize = header.getBlockSize();
        int keySize = header.getKeySize() / 8;
        int hashSize = header.getHashAlgorithm().hashSize;
        byte[] newVerifierSalt = new byte[blockSize];
        byte[] newVerifier = new byte[blockSize];
        byte[] newKeySalt = new byte[blockSize];
        byte[] newKeySpec = new byte[keySize];
        byte[] newIntegritySalt = new byte[hashSize];
        r.nextBytes(newVerifierSalt);
        r.nextBytes(newVerifier);
        r.nextBytes(newKeySalt);
        r.nextBytes(newKeySpec);
        r.nextBytes(newIntegritySalt);
        confirmPassword(password, newKeySpec, newKeySalt, newVerifierSalt, newVerifier, newIntegritySalt);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password, byte[] keySpec, byte[] keySalt, byte[] verifier, byte[] verifierSalt, byte[] integritySalt) {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier) getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        ver.setSalt(verifierSalt);
        header.setKeySalt(keySalt);
        int blockSize = header.getBlockSize();
        byte[] bArrHashPassword = CryptoFunctions.hashPassword(password, ver.getHashAlgorithm(), verifierSalt, ver.getSpinCount());
        this.pwHash = bArrHashPassword;
        byte[] encryptedVerifier = AgileDecryptor.hashInput(ver, bArrHashPassword, AgileDecryptor.kVerifierInputBlock, verifier, 1);
        ver.setEncryptedVerifier(encryptedVerifier);
        MessageDigest hashMD = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
        byte[] hashedVerifier = hashMD.digest(verifier);
        byte[] encryptedVerifierHash = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kHashedVerifierBlock, hashedVerifier, 1);
        ver.setEncryptedVerifierHash(encryptedVerifierHash);
        byte[] encryptedKey = AgileDecryptor.hashInput(ver, this.pwHash, AgileDecryptor.kCryptoKeyBlock, keySpec, 1);
        ver.setEncryptedKey(encryptedKey);
        SecretKey secretKey = new SecretKeySpec(keySpec, header.getCipherAlgorithm().jceId);
        setSecretKey(secretKey);
        this.integritySalt = (byte[]) integritySalt.clone();
        try {
            byte[] vec = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityKeyBlock, header.getBlockSize());
            Cipher cipher = CryptoFunctions.getCipher(secretKey, header.getCipherAlgorithm(), header.getChainingMode(), vec, 1);
            byte[] bArr = this.integritySalt;
            byte[] hmacKey = CryptoFunctions.getBlock0(bArr, AgileDecryptor.getNextBlockSize(bArr.length, blockSize));
            byte[] encryptedHmacKey = cipher.doFinal(hmacKey);
            header.setEncryptedHmacKey(encryptedHmacKey);
            Cipher cipher2 = Cipher.getInstance("RSA");
            for (AgileEncryptionVerifier.AgileCertificateEntry ace : ver.getCertificates()) {
                byte[] vec2 = vec;
                byte[] hmacKey2 = hmacKey;
                cipher2.init(1, ace.x509.getPublicKey());
                ace.encryptedKey = cipher2.doFinal(getSecretKey().getEncoded());
                Mac x509Hmac = CryptoFunctions.getMac(header.getHashAlgorithm());
                x509Hmac.init(getSecretKey());
                ace.certVerifier = x509Hmac.doFinal(ace.x509.getEncoded());
                vec = vec2;
                hmacKey = hmacKey2;
            }
        } catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public OutputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        AgileCipherOutputStream countStream = new AgileCipherOutputStream(dir);
        return countStream;
    }

    protected void updateIntegrityHMAC(File tmpFile, int oleStreamSize) throws GeneralSecurityException, IOException {
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        int blockSize = header.getBlockSize();
        HashAlgorithm hashAlgo = header.getHashAlgorithm();
        Mac integrityMD = CryptoFunctions.getMac(hashAlgo);
        byte[] bArr = this.integritySalt;
        byte[] hmacKey = CryptoFunctions.getBlock0(bArr, AgileDecryptor.getNextBlockSize(bArr.length, blockSize));
        integrityMD.init(new SecretKeySpec(hmacKey, hashAlgo.jceHmacId));
        byte[] buf = new byte[1024];
        LittleEndian.putLong(buf, 0, oleStreamSize);
        integrityMD.update(buf, 0, 8);
        InputStream fis = new FileInputStream(tmpFile);
        while (true) {
            try {
                int readBytes = fis.read(buf);
                if (readBytes != -1) {
                    integrityMD.update(buf, 0, readBytes);
                } else {
                    fis.close();
                    byte[] hmacValue = integrityMD.doFinal();
                    byte[] hmacValueFilled = CryptoFunctions.getBlock0(hmacValue, AgileDecryptor.getNextBlockSize(hmacValue.length, blockSize));
                    byte[] iv = CryptoFunctions.generateIv(header.getHashAlgorithm(), header.getKeySalt(), AgileDecryptor.kIntegrityValueBlock, blockSize);
                    Cipher cipher = CryptoFunctions.getCipher(getSecretKey(), header.getCipherAlgorithm(), header.getChainingMode(), iv, 1);
                    byte[] encryptedHmacValue = cipher.doFinal(hmacValueFilled);
                    header.setEncryptedHmacValue(encryptedHmacValue);
                    return;
                }
            } catch (Throwable th) {
                fis.close();
                throw th;
            }
        }
    }

    protected EncryptionDocument createEncryptionDocument() {
        AgileEncryptionVerifier ver = (AgileEncryptionVerifier) getEncryptionInfo().getVerifier();
        AgileEncryptionHeader header = (AgileEncryptionHeader) getEncryptionInfo().getHeader();
        EncryptionDocument ed = EncryptionDocument.Factory.newInstance();
        CTEncryption edRoot = ed.addNewEncryption();
        CTKeyData keyData = edRoot.addNewKeyData();
        CTKeyEncryptors keyEncList = edRoot.addNewKeyEncryptors();
        CTKeyEncryptor keyEnc = keyEncList.addNewKeyEncryptor();
        keyEnc.setUri(this.passwordUri);
        CTPasswordKeyEncryptor keyPass = keyEnc.addNewEncryptedPasswordKey();
        keyPass.setSpinCount(ver.getSpinCount());
        keyData.setSaltSize(header.getBlockSize());
        keyPass.setSaltSize(ver.getBlockSize());
        keyData.setBlockSize(header.getBlockSize());
        keyPass.setBlockSize(ver.getBlockSize());
        keyData.setKeyBits(header.getKeySize());
        keyPass.setKeyBits(ver.getKeySize());
        keyData.setHashSize(header.getHashAlgorithm().hashSize);
        keyPass.setHashSize(ver.getHashAlgorithm().hashSize);
        if (!header.getCipherAlgorithm().xmlId.equals(ver.getCipherAlgorithm().xmlId)) {
            throw new EncryptedDocumentException("Cipher algorithm of header and verifier have to match");
        }
        STCipherAlgorithm.Enum xmlCipherAlgo = STCipherAlgorithm.Enum.forString(header.getCipherAlgorithm().xmlId);
        if (xmlCipherAlgo == null) {
            throw new EncryptedDocumentException("CipherAlgorithm " + header.getCipherAlgorithm() + " not supported.");
        }
        keyData.setCipherAlgorithm(xmlCipherAlgo);
        keyPass.setCipherAlgorithm(xmlCipherAlgo);
        int i = AnonymousClass2.$SwitchMap$org$apache$poi$poifs$crypt$ChainingMode[header.getChainingMode().ordinal()];
        if (i == 1) {
            keyData.setCipherChaining(STCipherChaining.CHAINING_MODE_CBC);
            keyPass.setCipherChaining(STCipherChaining.CHAINING_MODE_CBC);
        } else if (i == 2) {
            keyData.setCipherChaining(STCipherChaining.CHAINING_MODE_CFB);
            keyPass.setCipherChaining(STCipherChaining.CHAINING_MODE_CFB);
        } else {
            throw new EncryptedDocumentException("ChainingMode " + header.getChainingMode() + " not supported.");
        }
        keyData.setHashAlgorithm(mapHashAlgorithm(header.getHashAlgorithm()));
        keyPass.setHashAlgorithm(mapHashAlgorithm(ver.getHashAlgorithm()));
        keyData.setSaltValue(header.getKeySalt());
        keyPass.setSaltValue(ver.getSalt());
        keyPass.setEncryptedVerifierHashInput(ver.getEncryptedVerifier());
        keyPass.setEncryptedVerifierHashValue(ver.getEncryptedVerifierHash());
        keyPass.setEncryptedKeyValue(ver.getEncryptedKey());
        CTDataIntegrity hmacData = edRoot.addNewDataIntegrity();
        hmacData.setEncryptedHmacKey(header.getEncryptedHmacKey());
        hmacData.setEncryptedHmacValue(header.getEncryptedHmacValue());
        for (AgileEncryptionVerifier.AgileCertificateEntry ace : ver.getCertificates()) {
            CTKeyEncryptor keyEnc2 = keyEncList.addNewKeyEncryptor();
            keyEnc2.setUri(this.certificateUri);
            CTCertificateKeyEncryptor certData = keyEnc2.addNewEncryptedCertificateKey();
            try {
                certData.setX509Certificate(ace.x509.getEncoded());
                certData.setEncryptedKeyValue(ace.encryptedKey);
                certData.setCertVerifier(ace.certVerifier);
            } catch (CertificateEncodingException e) {
                throw new EncryptedDocumentException(e);
            }
        }
        return ed;
    }

    /* JADX INFO: renamed from: org.apache.poi.poifs.crypt.agile.AgileEncryptor$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$crypt$ChainingMode;

        static {
            int[] iArr = new int[ChainingMode.values().length];
            $SwitchMap$org$apache$poi$poifs$crypt$ChainingMode = iArr;
            try {
                iArr[ChainingMode.cbc.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$ChainingMode[ChainingMode.cfb.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private static STHashAlgorithm.Enum mapHashAlgorithm(HashAlgorithm hashAlgo) {
        STHashAlgorithm.Enum xmlHashAlgo = STHashAlgorithm.Enum.forString(hashAlgo.ecmaString);
        if (xmlHashAlgo == null) {
            throw new EncryptedDocumentException("HashAlgorithm " + hashAlgo + " not supported.");
        }
        return xmlHashAlgo;
    }

    protected void marshallEncryptionDocument(EncryptionDocument ed, LittleEndianByteArrayOutputStream os) {
        XmlOptions xo = new XmlOptions();
        xo.setCharacterEncoding("UTF-8");
        Map<String, String> nsMap = new HashMap<>();
        nsMap.put(this.passwordUri.toString(), "p");
        nsMap.put(this.certificateUri.toString(), "c");
        xo.setUseDefaultNamespace();
        xo.setSaveSuggestedPrefixes(nsMap);
        xo.setSaveNamespacesFirst();
        xo.setSaveAggressiveNamespaces();
        xo.setSaveNoXmlDecl();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n".getBytes("UTF-8"));
            ed.save(bos, xo);
            bos.writeTo(os);
        } catch (IOException e) {
            throw new EncryptedDocumentException("error marshalling encryption info document", e);
        }
    }

    protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws GeneralSecurityException, IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final EncryptionInfo info = getEncryptionInfo();
        EncryptionRecord er = new EncryptionRecord() { // from class: org.apache.poi.poifs.crypt.agile.AgileEncryptor.1
            @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
            public void write(LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                bos.writeInt(info.getEncryptionFlags());
                EncryptionDocument ed = AgileEncryptor.this.createEncryptionDocument();
                AgileEncryptor.this.marshallEncryptionDocument(ed, bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }

    private class AgileCipherOutputStream extends ChunkedCipherOutputStream {
        public AgileCipherOutputStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
            super(dir, 4096);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected Cipher initCipherForBlock(Cipher existing, int block, boolean lastChunk) throws GeneralSecurityException {
            return AgileDecryptor.initCipherForBlock(existing, block, lastChunk, AgileEncryptor.this.getEncryptionInfo(), AgileEncryptor.this.getSecretKey(), 1);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void calculateChecksum(File fileOut, int oleStreamSize) throws GeneralSecurityException, IOException {
            AgileEncryptor.this.updateIntegrityHMAC(fileOut, oleStreamSize);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws GeneralSecurityException, IOException {
            AgileEncryptor.this.createEncryptionInfoEntry(dir, tmpFile);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public AgileEncryptor clone() throws CloneNotSupportedException {
        AgileEncryptor other = (AgileEncryptor) super.clone();
        byte[] bArr = this.integritySalt;
        other.integritySalt = bArr == null ? null : (byte[]) bArr.clone();
        byte[] bArr2 = this.pwHash;
        other.pwHash = bArr2 != null ? (byte[]) bArr2.clone() : null;
        return other;
    }
}
