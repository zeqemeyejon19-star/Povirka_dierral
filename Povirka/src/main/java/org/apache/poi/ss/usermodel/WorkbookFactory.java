package org.apache.poi.ss.usermodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* JADX INFO: loaded from: classes.dex */
public class WorkbookFactory {
    public static Workbook create(POIFSFileSystem fs) throws IOException {
        return new HSSFWorkbook(fs);
    }

    public static Workbook create(NPOIFSFileSystem fs) throws IOException {
        try {
            return create(fs, (String) null);
        } catch (InvalidFormatException e) {
            throw new IOException(e);
        }
    }

    private static Workbook create(NPOIFSFileSystem fs, String password) throws InvalidFormatException, IOException {
        DirectoryNode root = fs.getRoot();
        if (root.hasEntry(Decryptor.DEFAULT_POIFS_ENTRY)) {
            InputStream stream = DocumentFactoryHelper.getDecryptedStream(fs, password);
            OPCPackage pkg = OPCPackage.open(stream);
            return create(pkg);
        }
        boolean passwordSet = false;
        if (password != null) {
            Biff8EncryptionKey.setCurrentUserPassword(password);
            passwordSet = true;
        }
        try {
            return new HSSFWorkbook(root, true);
        } finally {
            if (passwordSet) {
                Biff8EncryptionKey.setCurrentUserPassword(null);
            }
        }
    }

    public static Workbook create(OPCPackage pkg) throws IOException {
        return new XSSFWorkbook(pkg);
    }

    public static Workbook create(InputStream inp) throws InvalidFormatException, EncryptedDocumentException, IOException {
        return create(inp, (String) null);
    }

    public static Workbook create(InputStream inp, String password) throws InvalidFormatException, EncryptedDocumentException, IOException {
        InputStream is = FileMagic.prepareToCheckMagic(inp);
        FileMagic fm = FileMagic.valueOf(is);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[fm.ordinal()];
        if (i == 1) {
            NPOIFSFileSystem fs = new NPOIFSFileSystem(is);
            return create(fs, password);
        }
        if (i == 2) {
            return new XSSFWorkbook(OPCPackage.open(is));
        }
        throw new InvalidFormatException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
    }

    /* JADX INFO: renamed from: org.apache.poi.ss.usermodel.WorkbookFactory$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic;

        static {
            int[] iArr = new int[FileMagic.values().length];
            $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic = iArr;
            try {
                iArr[FileMagic.OLE2.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.OOXML.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static Workbook create(File file) throws InvalidFormatException, EncryptedDocumentException, IOException {
        return create(file, (String) null);
    }

    public static Workbook create(File file, String password) throws InvalidFormatException, EncryptedDocumentException, IOException {
        return create(file, password, false);
    }

    public static Workbook create(File file, String password, boolean readOnly) throws InvalidFormatException, EncryptedDocumentException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        try {
            NPOIFSFileSystem fs = new NPOIFSFileSystem(file, readOnly);
            try {
                return create(fs, password);
            } catch (RuntimeException e) {
                IOUtils.closeQuietly(fs);
                throw e;
            }
        } catch (OfficeXmlFileException e2) {
            OPCPackage pkg = OPCPackage.open(file, readOnly ? PackageAccess.READ : PackageAccess.READ_WRITE);
            try {
                return new XSSFWorkbook(pkg);
            } catch (Exception ioe) {
                pkg.revert();
                if (ioe instanceof IOException) {
                    throw ((IOException) ioe);
                }
                if (ioe instanceof RuntimeException) {
                    throw ((RuntimeException) ioe);
                }
                throw new IOException(ioe);
            }
        }
    }
}
