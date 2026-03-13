package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIDecryptor;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class CryptoAPIEncryptor extends Encryptor implements Cloneable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private int chunkSize = 512;

    protected CryptoAPIEncryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password) {
        Random r = new SecureRandom();
        byte[] salt = new byte[16];
        byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        confirmPassword(password, null, null, verifier, salt, null);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void confirmPassword(String password, byte[] keySpec, byte[] keySalt, byte[] verifier, byte[] verifierSalt, byte[] integritySalt) {
        if (verifier == null || verifierSalt == null) {
            throw new AssertionError();
        }
        CryptoAPIEncryptionVerifier ver = (CryptoAPIEncryptionVerifier) getEncryptionInfo().getVerifier();
        ver.setSalt(verifierSalt);
        SecretKey skey = CryptoAPIDecryptor.generateSecretKey(password, ver);
        setSecretKey(skey);
        try {
            Cipher cipher = initCipherForBlock(null, 0);
            byte[] encryptedVerifier = new byte[verifier.length];
            cipher.update(verifier, 0, verifier.length, encryptedVerifier);
            ver.setEncryptedVerifier(encryptedVerifier);
            HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            byte[] calcVerifierHash = hashAlg.digest(verifier);
            byte[] encryptedVerifierHash = cipher.doFinal(calcVerifierHash);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        } catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }

    public Cipher initCipherForBlock(Cipher cipher, int block) throws GeneralSecurityException {
        return CryptoAPIDecryptor.initCipherForBlock(cipher, block, getEncryptionInfo(), getSecretKey(), 1);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public ChunkedCipherOutputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        throw new IOException("not supported");
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public CryptoAPICipherOutputStream getDataStream(OutputStream stream, int initialOffset) throws GeneralSecurityException, IOException {
        return new CryptoAPICipherOutputStream(stream);
    }

    public void setSummaryEntries(DirectoryNode dir, String encryptedStream, NPOIFSFileSystem entries) throws GeneralSecurityException, IOException {
        CryptoAPIDocumentOutputStream bos = new CryptoAPIDocumentOutputStream(this);
        byte[] buf = new byte[8];
        bos.write(buf, 0, 8);
        List<CryptoAPIDecryptor.StreamDescriptorEntry> descList = new ArrayList<>();
        int block = 0;
        for (Entry entry : entries.getRoot()) {
            if (!entry.isDirectoryEntry()) {
                CryptoAPIDecryptor.StreamDescriptorEntry descEntry = new CryptoAPIDecryptor.StreamDescriptorEntry();
                descEntry.block = block;
                descEntry.streamOffset = bos.size();
                descEntry.streamName = entry.getName();
                descEntry.flags = CryptoAPIDecryptor.StreamDescriptorEntry.flagStream.setValue(0, 1);
                descEntry.reserved2 = 0;
                bos.setBlock(block);
                DocumentInputStream dis = dir.createDocumentInputStream(entry);
                IOUtils.copy(dis, bos);
                dis.close();
                descEntry.streamSize = bos.size() - descEntry.streamOffset;
                descList.add(descEntry);
                block++;
            }
        }
        int streamDescriptorArrayOffset = bos.size();
        bos.setBlock(0);
        LittleEndian.putUInt(buf, 0, descList.size());
        bos.write(buf, 0, 4);
        for (CryptoAPIDecryptor.StreamDescriptorEntry sde : descList) {
            LittleEndian.putUInt(buf, 0, sde.streamOffset);
            bos.write(buf, 0, 4);
            LittleEndian.putUInt(buf, 0, sde.streamSize);
            bos.write(buf, 0, 4);
            LittleEndian.putUShort(buf, 0, sde.block);
            bos.write(buf, 0, 2);
            LittleEndian.putUByte(buf, 0, (short) sde.streamName.length());
            bos.write(buf, 0, 1);
            LittleEndian.putUByte(buf, 0, (short) sde.flags);
            bos.write(buf, 0, 1);
            LittleEndian.putUInt(buf, 0, sde.reserved2);
            bos.write(buf, 0, 4);
            byte[] nameBytes = StringUtil.getToUnicodeLE(sde.streamName);
            bos.write(nameBytes, 0, nameBytes.length);
            LittleEndian.putShort(buf, 0, (short) 0);
            bos.write(buf, 0, 2);
        }
        int savedSize = bos.size();
        int streamDescriptorArraySize = savedSize - streamDescriptorArrayOffset;
        LittleEndian.putUInt(buf, 0, streamDescriptorArrayOffset);
        LittleEndian.putUInt(buf, 4, streamDescriptorArraySize);
        bos.reset();
        bos.setBlock(0);
        bos.write(buf, 0, 8);
        bos.setSize(savedSize);
        dir.createDocument(encryptedStream, new ByteArrayInputStream(bos.getBuf(), 0, savedSize));
    }

    protected int getKeySizeInBytes() {
        return getEncryptionInfo().getHeader().getKeySize() / 8;
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    protected void createEncryptionInfoEntry(DirectoryNode dir) throws IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final EncryptionInfo info = getEncryptionInfo();
        final CryptoAPIEncryptionHeader header = (CryptoAPIEncryptionHeader) getEncryptionInfo().getHeader();
        final CryptoAPIEncryptionVerifier verifier = (CryptoAPIEncryptionVerifier) getEncryptionInfo().getVerifier();
        EncryptionRecord er = new EncryptionRecord() { // from class: org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptor.1
            @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
            public void write(LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                header.write(bos);
                verifier.write(bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }

    @Override // org.apache.poi.poifs.crypt.Encryptor
    public CryptoAPIEncryptor clone() throws CloneNotSupportedException {
        return (CryptoAPIEncryptor) super.clone();
    }

    protected class CryptoAPICipherOutputStream extends ChunkedCipherOutputStream {
        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected Cipher initCipherForBlock(Cipher cipher, int block, boolean lastChunk) throws GeneralSecurityException, IOException {
            flush();
            EncryptionInfo ei = CryptoAPIEncryptor.this.getEncryptionInfo();
            SecretKey sk = CryptoAPIEncryptor.this.getSecretKey();
            return CryptoAPIDecryptor.initCipherForBlock(cipher, block, ei, sk, 1);
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void calculateChecksum(File file, int i) {
        }

        @Override // org.apache.poi.poifs.crypt.ChunkedCipherOutputStream
        protected void createEncryptionInfoEntry(DirectoryNode dir, File tmpFile) throws GeneralSecurityException, IOException {
            throw new EncryptedDocumentException("createEncryptionInfoEntry not supported");
        }

        public CryptoAPICipherOutputStream(OutputStream stream) throws GeneralSecurityException, IOException {
            super(stream, CryptoAPIEncryptor.this.chunkSize);
        }

        @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
        public void flush() throws IOException {
            writeChunk(false);
            super.flush();
        }
    }
}
