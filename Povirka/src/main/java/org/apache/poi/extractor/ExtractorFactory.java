package org.apache.poi.extractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.POITextExtractor;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.extractor.OutlookTextExtactor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xdgf.extractor.XDGFVisioExtractor;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.xmlbeans.XmlException;

/* JADX INFO: loaded from: classes.dex */
public class ExtractorFactory {
    public static final String CORE_DOCUMENT_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";
    protected static final String STRICT_DOCUMENT_REL = "http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument";
    protected static final String VISIO_DOCUMENT_REL = "http://schemas.microsoft.com/visio/2010/relationships/document";
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) ExtractorFactory.class);

    public static boolean getThreadPrefersEventExtractors() {
        return OLE2ExtractorFactory.getThreadPrefersEventExtractors();
    }

    public static Boolean getAllThreadsPreferEventExtractors() {
        return OLE2ExtractorFactory.getAllThreadsPreferEventExtractors();
    }

    public static void setThreadPrefersEventExtractors(boolean preferEventExtractors) {
        OLE2ExtractorFactory.setThreadPrefersEventExtractors(preferEventExtractors);
    }

    public static void setAllThreadsPreferEventExtractors(Boolean preferEventExtractors) {
        OLE2ExtractorFactory.setAllThreadsPreferEventExtractors(preferEventExtractors);
    }

    protected static boolean getPreferEventExtractor() {
        return OLE2ExtractorFactory.getPreferEventExtractor();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    public static POITextExtractor createExtractor(File f) throws XmlException, OpenXML4JException, IOException {
        try {
            NPOIFSFileSystem fs = new NPOIFSFileSystem(f);
            if (fs.getRoot().hasEntry(Decryptor.DEFAULT_POIFS_ENTRY)) {
                return createEncyptedOOXMLExtractor(fs);
            }
            POIOLE2TextExtractor extractor = createExtractor(fs);
            extractor.setFilesystem(fs);
            return extractor;
        } catch (IOException e) {
            IOUtils.closeQuietly(null);
            throw e;
        } catch (Error e2) {
            IOUtils.closeQuietly(null);
            throw e2;
        } catch (OfficeXmlFileException e3) {
            IOUtils.closeQuietly(null);
            return createExtractor(OPCPackage.open(f.toString(), PackageAccess.READ));
        } catch (RuntimeException e4) {
            IOUtils.closeQuietly(null);
            throw e4;
        } catch (OpenXML4JException e5) {
            IOUtils.closeQuietly(null);
            throw e5;
        } catch (NotOLE2FileException e6) {
            IOUtils.closeQuietly(null);
            throw new IllegalArgumentException("Your File was neither an OLE2 file, nor an OOXML file");
        } catch (XmlException e7) {
            IOUtils.closeQuietly(null);
            throw e7;
        }
    }

    public static POITextExtractor createExtractor(InputStream inp) throws XmlException, OpenXML4JException, IOException {
        InputStream is = FileMagic.prepareToCheckMagic(inp);
        FileMagic fm = FileMagic.valueOf(is);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$filesystem$FileMagic[fm.ordinal()];
        if (i != 1) {
            if (i == 2) {
                return createExtractor(OPCPackage.open(is));
            }
            throw new IllegalArgumentException("Your InputStream was neither an OLE2 stream, nor an OOXML stream");
        }
        NPOIFSFileSystem fs = new NPOIFSFileSystem(is);
        boolean isEncrypted = fs.getRoot().hasEntry(Decryptor.DEFAULT_POIFS_ENTRY);
        return isEncrypted ? createEncyptedOOXMLExtractor(fs) : createExtractor(fs);
    }

    /* JADX INFO: renamed from: org.apache.poi.extractor.ExtractorFactory$1, reason: invalid class name */
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

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    public static POIXMLTextExtractor createExtractor(OPCPackage pkg) throws XmlException, OpenXML4JException, IOException {
        try {
            PackageRelationshipCollection core = pkg.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
            if (core.size() == 0) {
                core = pkg.getRelationshipsByType("http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument");
            }
            if (core.size() == 0) {
                core = pkg.getRelationshipsByType("http://schemas.microsoft.com/visio/2010/relationships/document");
                if (core.size() == 1) {
                    return new XDGFVisioExtractor(pkg);
                }
            }
            if (core.size() != 1) {
                throw new IllegalArgumentException("Invalid OOXML Package received - expected 1 core document, found " + core.size());
            }
            PackagePart corePart = pkg.getPart(core.getRelationship(0));
            String contentType = corePart.getContentType();
            XSSFRelation[] arr$ = XSSFExcelExtractor.SUPPORTED_TYPES;
            for (XSSFRelation rel : arr$) {
                if (rel.getContentType().equals(contentType)) {
                    if (getPreferEventExtractor()) {
                        return new XSSFEventBasedExcelExtractor(pkg);
                    }
                    return new XSSFExcelExtractor(pkg);
                }
            }
            XWPFRelation[] arr$2 = XWPFWordExtractor.SUPPORTED_TYPES;
            for (XWPFRelation rel2 : arr$2) {
                if (rel2.getContentType().equals(contentType)) {
                    return new XWPFWordExtractor(pkg);
                }
            }
            XSLFRelation[] arr$3 = XSLFPowerPointExtractor.SUPPORTED_TYPES;
            for (XSLFRelation rel3 : arr$3) {
                if (rel3.getContentType().equals(contentType)) {
                    return new XSLFPowerPointExtractor(pkg);
                }
            }
            if (XSLFRelation.THEME_MANAGER.getContentType().equals(contentType)) {
                return new XSLFPowerPointExtractor(new XSLFSlideShow(pkg));
            }
            XSSFRelation[] arr$4 = XSSFBEventBasedExcelExtractor.SUPPORTED_TYPES;
            for (XSSFRelation rel4 : arr$4) {
                if (rel4.getContentType().equals(contentType)) {
                    return new XSSFBEventBasedExcelExtractor(pkg);
                }
            }
            throw new IllegalArgumentException("No supported documents found in the OOXML package (found " + contentType + ")");
        } catch (IOException e) {
            pkg.revert();
            throw e;
        } catch (Error e2) {
            pkg.revert();
            throw e2;
        } catch (RuntimeException e3) {
            pkg.revert();
            throw e3;
        } catch (XmlException e4) {
            pkg.revert();
            throw e4;
        } catch (OpenXML4JException e5) {
            pkg.revert();
            throw e5;
        }
    }

    public static POIOLE2TextExtractor createExtractor(POIFSFileSystem fs) throws XmlException, OpenXML4JException, IOException {
        return OLE2ExtractorFactory.createExtractor(fs);
    }

    public static POIOLE2TextExtractor createExtractor(NPOIFSFileSystem fs) throws XmlException, OpenXML4JException, IOException {
        return OLE2ExtractorFactory.createExtractor(fs);
    }

    public static POIOLE2TextExtractor createExtractor(OPOIFSFileSystem fs) throws XmlException, OpenXML4JException, IOException {
        return OLE2ExtractorFactory.createExtractor(fs);
    }

    public static POITextExtractor createExtractor(DirectoryNode poifsDir) throws XmlException, OpenXML4JException, IOException {
        for (String entryName : poifsDir.getEntryNames()) {
            if (entryName.equals("Package")) {
                OPCPackage pkg = OPCPackage.open(poifsDir.createDocumentInputStream("Package"));
                return createExtractor(pkg);
            }
        }
        return OLE2ExtractorFactory.createExtractor(poifsDir);
    }

    public static POITextExtractor[] getEmbededDocsTextExtractors(POIOLE2TextExtractor ext) throws XmlException, OpenXML4JException, IOException {
        ArrayList<Entry> dirs = new ArrayList<>();
        ArrayList<InputStream> nonPOIFS = new ArrayList<>();
        DirectoryEntry root = ext.getRoot();
        if (root == null) {
            throw new IllegalStateException("The extractor didn't know which POIFS it came from!");
        }
        if (ext instanceof ExcelExtractor) {
            Iterator<Entry> it = root.getEntries();
            while (it.hasNext()) {
                Entry entry = it.next();
                if (entry.getName().startsWith("MBD")) {
                    dirs.add(entry);
                }
            }
        } else if (ext instanceof WordExtractor) {
            try {
                DirectoryEntry op = (DirectoryEntry) root.getEntry("ObjectPool");
                Iterator<Entry> it2 = op.getEntries();
                while (it2.hasNext()) {
                    Entry entry2 = it2.next();
                    if (entry2.getName().startsWith("_")) {
                        dirs.add(entry2);
                    }
                }
            } catch (FileNotFoundException e) {
                logger.log(3, "Ignoring FileNotFoundException while extracting Word document", e.getLocalizedMessage());
            }
        } else if (ext instanceof OutlookTextExtactor) {
            MAPIMessage msg = ((OutlookTextExtactor) ext).getMAPIMessage();
            AttachmentChunks[] arr$ = msg.getAttachmentFiles();
            for (AttachmentChunks attachment : arr$) {
                if (attachment.getAttachData() != null) {
                    byte[] data = attachment.getAttachData().getValue();
                    nonPOIFS.add(new ByteArrayInputStream(data));
                } else if (attachment.getAttachmentDirectory() != null) {
                    dirs.add(attachment.getAttachmentDirectory().getDirectory());
                }
            }
        }
        if (dirs.size() == 0 && nonPOIFS.size() == 0) {
            return new POITextExtractor[0];
        }
        ArrayList<POITextExtractor> textExtractors = new ArrayList<>();
        for (Entry dir : dirs) {
            textExtractors.add(createExtractor((DirectoryNode) dir));
        }
        for (InputStream nonPOIF : nonPOIFS) {
            try {
                textExtractors.add(createExtractor(nonPOIF));
            } catch (IllegalArgumentException e2) {
                logger.log(3, "Format not supported yet", e2.getLocalizedMessage());
            } catch (OpenXML4JException e3) {
                throw new IOException(e3.getMessage(), e3);
            } catch (XmlException e4) {
                throw new IOException(e4.getMessage(), e4);
            }
        }
        return (POITextExtractor[]) textExtractors.toArray(new POITextExtractor[textExtractors.size()]);
    }

    @NotImplemented
    public static POITextExtractor[] getEmbededDocsTextExtractors(POIXMLTextExtractor ext) {
        throw new IllegalStateException("Not yet supported");
    }

    private static POIXMLTextExtractor createEncyptedOOXMLExtractor(NPOIFSFileSystem fs) throws IOException {
        String pass = Biff8EncryptionKey.getCurrentUserPassword();
        if (pass == null) {
            pass = Decryptor.DEFAULT_PASSWORD;
        }
        EncryptionInfo ei = new EncryptionInfo(fs);
        Decryptor dec = ei.getDecryptor();
        InputStream is = null;
        try {
            try {
                if (!dec.verifyPassword(pass)) {
                    throw new EncryptedDocumentException("Invalid password specified - use Biff8EncryptionKey.setCurrentUserPassword() before calling extractor");
                }
                is = dec.getDataStream(fs);
                return createExtractor(OPCPackage.open(is));
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new EncryptedDocumentException(e2);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
