package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.BoundedInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.StringUtil;

/* JADX INFO: loaded from: classes.dex */
public class CryptoAPIDecryptor extends Decryptor implements Cloneable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private long length = -1;
    private int chunkSize = -1;

    static class StreamDescriptorEntry {
        static BitField flagStream = BitFieldFactory.getInstance(1);
        int block;
        int flags;
        int reserved2;
        String streamName;
        int streamOffset;
        int streamSize;

        StreamDescriptorEntry() {
        }
    }

    protected CryptoAPIDecryptor() {
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public boolean verifyPassword(String password) {
        EncryptionVerifier ver = getEncryptionInfo().getVerifier();
        SecretKey skey = generateSecretKey(password, ver);
        try {
            Cipher cipher = initCipherForBlock(null, 0, getEncryptionInfo(), skey, 2);
            byte[] encryptedVerifier = ver.getEncryptedVerifier();
            byte[] verifier = new byte[encryptedVerifier.length];
            cipher.update(encryptedVerifier, 0, encryptedVerifier.length, verifier);
            setVerifier(verifier);
            byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            byte[] verifierHash = cipher.doFinal(encryptedVerifierHash);
            HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            byte[] calcVerifierHash = hashAlg.digest(verifier);
            if (!Arrays.equals(calcVerifierHash, verifierHash)) {
                return false;
            }
            setSecretKey(skey);
            return true;
        } catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public Cipher initCipherForBlock(Cipher cipher, int block) throws GeneralSecurityException {
        EncryptionInfo ei = getEncryptionInfo();
        SecretKey sk = getSecretKey();
        return initCipherForBlock(cipher, block, ei, sk, 2);
    }

    protected static Cipher initCipherForBlock(Cipher cipher, int block, EncryptionInfo encryptionInfo, SecretKey skey, int encryptMode) throws GeneralSecurityException {
        EncryptionVerifier ver = encryptionInfo.getVerifier();
        HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        hashAlg.update(skey.getEncoded());
        byte[] encKey = hashAlg.digest(blockKey);
        EncryptionHeader header = encryptionInfo.getHeader();
        int keyBits = header.getKeySize();
        byte[] encKey2 = CryptoFunctions.getBlock0(encKey, keyBits / 8);
        if (keyBits == 40) {
            encKey2 = CryptoFunctions.getBlock0(encKey2, 16);
        }
        SecretKey key = new SecretKeySpec(encKey2, skey.getAlgorithm());
        if (cipher == null) {
            return CryptoFunctions.getCipher(key, header.getCipherAlgorithm(), null, null, encryptMode);
        }
        cipher.init(encryptMode, key);
        return cipher;
    }

    protected static SecretKey generateSecretKey(String password, EncryptionVerifier ver) {
        if (password.length() > 255) {
            password = password.substring(0, 255);
        }
        HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        hashAlg.update(ver.getSalt());
        byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        SecretKey skey = new SecretKeySpec(hash, ver.getCipherAlgorithm().jceId);
        return skey;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public ChunkedCipherInputStream getDataStream(DirectoryNode dir) throws GeneralSecurityException, IOException {
        throw new IOException("not supported");
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public ChunkedCipherInputStream getDataStream(InputStream stream, int size, int initialPos) throws GeneralSecurityException, IOException {
        return new CryptoAPICipherInputStream(stream, size, initialPos);
    }

    public POIFSFileSystem getSummaryEntries(DirectoryNode root, String encryptedStream) throws Exception {
        DocumentNode es = (DocumentNode) root.getEntry(encryptedStream);
        DocumentInputStream dis = root.createDocumentInputStream(es);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(dis, bos);
        dis.close();
        CryptoAPIDocumentInputStream sbis = new CryptoAPIDocumentInputStream(this, bos.toByteArray());
        LittleEndianInputStream leis = new LittleEndianInputStream(sbis);
        try {
            try {
                int streamDescriptorArrayOffset = (int) leis.readUInt();
                leis.readUInt();
                long skipN = ((long) streamDescriptorArrayOffset) - 8;
                try {
                    if (sbis.skip(skipN) < skipN) {
                        throw new EOFException("buffer underrun");
                    }
                    sbis.setBlock(0);
                    int encryptedStreamDescriptorCount = (int) leis.readUInt();
                    StreamDescriptorEntry[] entries = new StreamDescriptorEntry[encryptedStreamDescriptorCount];
                    int i = 0;
                    while (i < encryptedStreamDescriptorCount) {
                        StreamDescriptorEntry entry = new StreamDescriptorEntry();
                        entries[i] = entry;
                        int streamDescriptorArrayOffset2 = streamDescriptorArrayOffset;
                        DocumentNode es2 = es;
                        entry.streamOffset = (int) leis.readUInt();
                        entry.streamSize = (int) leis.readUInt();
                        entry.block = leis.readUShort();
                        int nameSize = leis.readUByte();
                        entry.flags = leis.readUByte();
                        entry.reserved2 = leis.readInt();
                        entry.streamName = StringUtil.readUnicodeLE(leis, nameSize);
                        leis.readShort();
                        if (entry.streamName.length() == nameSize) {
                            i++;
                            es = es2;
                            streamDescriptorArrayOffset = streamDescriptorArrayOffset2;
                        } else {
                            throw new AssertionError();
                        }
                    }
                    POIFSFileSystem fsOut = new POIFSFileSystem();
                    StreamDescriptorEntry[] arr$ = entries;
                    int len$ = arr$.length;
                    int i$ = 0;
                    while (i$ < len$) {
                        StreamDescriptorEntry entry2 = arr$[i$];
                        StreamDescriptorEntry[] arr$2 = arr$;
                        sbis.seek(entry2.streamOffset);
                        sbis.setBlock(entry2.block);
                        int len$2 = len$;
                        int len$3 = entry2.streamSize;
                        InputStream is = new BoundedInputStream(sbis, len$3);
                        fsOut.createDocument(is, entry2.streamName);
                        is.close();
                        i$++;
                        arr$ = arr$2;
                        len$ = len$2;
                    }
                    IOUtils.closeQuietly(leis);
                    IOUtils.closeQuietly(sbis);
                    return fsOut;
                } catch (Exception e) {
                    e = e;
                }
            } catch (Exception e2) {
                e = e2;
            } catch (Throwable th) {
                e = th;
                IOUtils.closeQuietly(leis);
                IOUtils.closeQuietly(sbis);
                throw e;
            }
            IOUtils.closeQuietly(null);
            if (e instanceof GeneralSecurityException) {
                throw ((GeneralSecurityException) e);
            }
            if (e instanceof IOException) {
                throw ((IOException) e);
            }
            throw new IOException("summary entries can't be read", e);
        } catch (Throwable th2) {
            e = th2;
            IOUtils.closeQuietly(leis);
            IOUtils.closeQuietly(sbis);
            throw e;
        }
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public long getLength() {
        long j = this.length;
        if (j == -1) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return j;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override // org.apache.poi.poifs.crypt.Decryptor
    public CryptoAPIDecryptor clone() throws CloneNotSupportedException {
        return (CryptoAPIDecryptor) super.clone();
    }

    private class CryptoAPICipherInputStream extends ChunkedCipherInputStream {
        @Override // org.apache.poi.poifs.crypt.ChunkedCipherInputStream
        protected Cipher initCipherForBlock(Cipher existing, int block) throws GeneralSecurityException {
            return CryptoAPIDecryptor.this.initCipherForBlock(existing, block);
        }

        public CryptoAPICipherInputStream(InputStream stream, long size, int initialPos) throws GeneralSecurityException {
            super(stream, size, CryptoAPIDecryptor.this.chunkSize, initialPos);
        }
    }
}
