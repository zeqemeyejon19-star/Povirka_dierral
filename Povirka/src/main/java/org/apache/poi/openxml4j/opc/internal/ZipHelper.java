package org.apache.poi.openxml4j.opc.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public final class ZipHelper {
    private static final String FORWARD_SLASH = "/";

    @Removal(version = "3.18")
    @Deprecated
    public static final int READ_WRITE_FILE_BUFFER_SIZE = 8192;

    private ZipHelper() {
    }

    public static ZipEntry getCorePropertiesZipEntry(ZipPackage pkg) {
        PackageRelationship corePropsRel = pkg.getRelationshipsByType(PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);
        if (corePropsRel == null) {
            return null;
        }
        return new ZipEntry(corePropsRel.getTargetURI().getPath());
    }

    public static ZipEntry getContentTypeZipEntry(ZipPackage pkg) {
        Enumeration<? extends ZipEntry> entries = pkg.getZipArchive().getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equals(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                return entry;
            }
        }
        return null;
    }

    public static String getOPCNameFromZipItemName(String zipItemName) {
        if (zipItemName == null) {
            throw new IllegalArgumentException("zipItemName cannot be null");
        }
        if (!zipItemName.startsWith(FORWARD_SLASH)) {
            return FORWARD_SLASH + zipItemName;
        }
        return zipItemName;
    }

    public static String getZipItemNameFromOPCName(String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName cannot be null");
        }
        String retVal = opcItemName;
        while (retVal.startsWith(FORWARD_SLASH)) {
            retVal = retVal.substring(1);
        }
        return retVal;
    }

    public static URI getZipURIFromOPCName(String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName");
        }
        String retVal = opcItemName;
        while (retVal.startsWith(FORWARD_SLASH)) {
            retVal = retVal.substring(1);
        }
        try {
            return new URI(retVal);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static void verifyZipHeader(InputStream stream) throws NotOfficeXmlFileException, IOException {
        InputStream is = FileMagic.prepareToCheckMagic(stream);
        FileMagic fm = FileMagic.valueOf(is);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[fm.ordinal()];
        if (i == 1) {
            throw new OLE2NotOfficeXmlFileException("The supplied data appears to be in the OLE2 Format. You are calling the part of POI that deals with OOXML (Office Open XML) Documents. You need to call a different part of POI to process this data (eg HSSF instead of XSSF)");
        }
        if (i == 2) {
            throw new NotOfficeXmlFileException("The supplied data appears to be a raw XML file. Formats such as Office 2003 XML are not supported");
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.openxml4j.opc.internal.ZipHelper$1, reason: invalid class name */
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
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.XML.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.OOXML.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[FileMagic.UNKNOWN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public static ZipSecureFile.ThresholdInputStream openZipStream(InputStream stream) throws IOException {
        InputStream checkedStream = FileMagic.prepareToCheckMagic(stream);
        verifyZipHeader(checkedStream);
        InputStream zis = new ZipInputStream(checkedStream);
        return ZipSecureFile.addThreshold(zis);
    }

    public static ZipFile openZipFile(File file) throws NotOfficeXmlFileException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File does not exist");
        }
        if (file.isDirectory()) {
            throw new IOException("File is a directory");
        }
        FileInputStream input = new FileInputStream(file);
        try {
            verifyZipHeader(input);
            input.close();
            return new ZipSecureFile(file);
        } catch (Throwable th) {
            input.close();
            throw th;
        }
    }

    public static ZipFile openZipFile(String path) throws IOException {
        return openZipFile(new File(path));
    }
}
