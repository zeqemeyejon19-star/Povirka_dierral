package org.apache.poi.poifs.crypt.temp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.TempFile;

/* JADX INFO: loaded from: classes.dex */
public class AesZipFileZipEntrySource implements ZipEntrySource {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) AesZipFileZipEntrySource.class);
    private final Cipher ci;
    private boolean closed = false;
    private final File tmpFile;
    private final ZipFile zipFile;

    public AesZipFileZipEntrySource(File tmpFile, Cipher ci) throws IOException {
        this.tmpFile = tmpFile;
        this.zipFile = new ZipFile(tmpFile);
        this.ci = ci;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public Enumeration<? extends ZipEntry> getEntries() {
        return this.zipFile.entries();
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public InputStream getInputStream(ZipEntry entry) throws IOException {
        InputStream is = this.zipFile.getInputStream(entry);
        return new CipherInputStream(is, this.ci);
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.closed) {
            this.zipFile.close();
            if (!this.tmpFile.delete()) {
                LOG.log(5, this.tmpFile.getAbsolutePath() + " can't be removed (or was already removed.");
            }
        }
        this.closed = true;
    }

    @Override // org.apache.poi.openxml4j.util.ZipEntrySource
    public boolean isClosed() {
        return this.closed;
    }

    public static AesZipFileZipEntrySource createZipEntrySource(InputStream is) throws GeneralSecurityException, IOException {
        SecureRandom sr = new SecureRandom();
        byte[] ivBytes = new byte[16];
        byte[] keyBytes = new byte[16];
        sr.nextBytes(ivBytes);
        sr.nextBytes(keyBytes);
        File tmpFile = TempFile.createTempFile("protectedXlsx", ".zip");
        copyToFile(is, tmpFile, CipherAlgorithm.aes128, keyBytes, ivBytes);
        IOUtils.closeQuietly(is);
        return fileToSource(tmpFile, CipherAlgorithm.aes128, keyBytes, ivBytes);
    }

    private static void copyToFile(InputStream is, File tmpFile, CipherAlgorithm cipherAlgorithm, byte[] keyBytes, byte[] ivBytes) throws GeneralSecurityException, IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, cipherAlgorithm.jceId);
        Cipher ciEnc = CryptoFunctions.getCipher(skeySpec, cipherAlgorithm, ChainingMode.cbc, ivBytes, 1, "PKCS5Padding");
        ZipInputStream zis = new ZipInputStream(is);
        FileOutputStream fos = new FileOutputStream(tmpFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        while (true) {
            ZipEntry ze = zis.getNextEntry();
            if (ze != null) {
                ZipEntry zeNew = new ZipEntry(ze.getName());
                zeNew.setComment(ze.getComment());
                zeNew.setExtra(ze.getExtra());
                zeNew.setTime(ze.getTime());
                zos.putNextEntry(zeNew);
                FilterOutputStream fos2 = new FilterOutputStream(zos) { // from class: org.apache.poi.poifs.crypt.temp.AesZipFileZipEntrySource.1
                    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
                    public void close() {
                    }
                };
                CipherOutputStream cos = new CipherOutputStream(fos2, ciEnc);
                IOUtils.copy(zis, cos);
                cos.close();
                fos2.close();
                zos.closeEntry();
                zis.closeEntry();
            } else {
                zos.close();
                fos.close();
                zis.close();
                return;
            }
        }
    }

    private static AesZipFileZipEntrySource fileToSource(File tmpFile, CipherAlgorithm cipherAlgorithm, byte[] keyBytes, byte[] ivBytes) throws IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, cipherAlgorithm.jceId);
        Cipher ciDec = CryptoFunctions.getCipher(skeySpec, cipherAlgorithm, ChainingMode.cbc, ivBytes, 2, "PKCS5Padding");
        return new AesZipFileZipEntrySource(tmpFile, ciDec);
    }
}
