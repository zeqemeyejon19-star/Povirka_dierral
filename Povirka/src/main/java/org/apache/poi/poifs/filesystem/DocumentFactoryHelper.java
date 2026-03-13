package org.apache.poi.poifs.filesystem;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class DocumentFactoryHelper {
    public static InputStream getDecryptedStream(final NPOIFSFileSystem fs, String password) throws IOException {
        EncryptionInfo info = new EncryptionInfo(fs);
        Decryptor d = Decryptor.getInstance(info);
        boolean passwordCorrect = false;
        if (password != null) {
            try {
                if (d.verifyPassword(password)) {
                    passwordCorrect = true;
                }
            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            }
        }
        if (!passwordCorrect && d.verifyPassword(Decryptor.DEFAULT_PASSWORD)) {
            passwordCorrect = true;
        }
        if (passwordCorrect) {
            return new FilterInputStream(d.getDataStream(fs.getRoot())) { // from class: org.apache.poi.poifs.filesystem.DocumentFactoryHelper.1
                @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                public void close() throws IOException {
                    fs.close();
                    super.close();
                }
            };
        }
        if (password != null) {
            throw new EncryptedDocumentException("Password incorrect");
        }
        throw new EncryptedDocumentException("The supplied spreadsheet is protected, but no password was supplied");
    }

    @Removal(version = "4.0")
    @Deprecated
    public static boolean hasOOXMLHeader(InputStream inp) throws IOException {
        return FileMagic.valueOf(inp) == FileMagic.OOXML;
    }
}
