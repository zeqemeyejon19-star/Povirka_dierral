package org.apache.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.WritingNotSupportedException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIDecryptor;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public abstract class POIDocument implements Closeable {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) POIDocument.class);
    private DirectoryNode directory;
    private DocumentSummaryInformation dsInf;
    private boolean initialized;
    private SummaryInformation sInf;

    public abstract void write() throws IOException;

    public abstract void write(File file) throws IOException;

    public abstract void write(OutputStream outputStream) throws IOException;

    protected POIDocument(DirectoryNode dir) {
        this.directory = dir;
    }

    protected POIDocument(OPOIFSFileSystem fs) {
        this(fs.getRoot());
    }

    protected POIDocument(NPOIFSFileSystem fs) {
        this(fs.getRoot());
    }

    protected POIDocument(POIFSFileSystem fs) {
        this(fs.getRoot());
    }

    public DocumentSummaryInformation getDocumentSummaryInformation() {
        if (!this.initialized) {
            readProperties();
        }
        return this.dsInf;
    }

    public SummaryInformation getSummaryInformation() {
        if (!this.initialized) {
            readProperties();
        }
        return this.sInf;
    }

    public void createInformationProperties() {
        if (!this.initialized) {
            readProperties();
        }
        if (this.sInf == null) {
            this.sInf = PropertySetFactory.newSummaryInformation();
        }
        if (this.dsInf == null) {
            this.dsInf = PropertySetFactory.newDocumentSummaryInformation();
        }
    }

    protected void readProperties() {
        if (this.initialized) {
            return;
        }
        DocumentSummaryInformation dsi = (DocumentSummaryInformation) readPropertySet(DocumentSummaryInformation.class, DocumentSummaryInformation.DEFAULT_STREAM_NAME);
        if (dsi != null) {
            this.dsInf = dsi;
        }
        SummaryInformation si = (SummaryInformation) readPropertySet(SummaryInformation.class, SummaryInformation.DEFAULT_STREAM_NAME);
        if (si != null) {
            this.sInf = si;
        }
        this.initialized = true;
    }

    private <T> T readPropertySet(Class<T> cls, String str) {
        String strSubstring = cls.getName().substring(cls.getName().lastIndexOf(46) + 1);
        try {
            T t = (T) getPropertySet(str);
            if (cls.isInstance(t)) {
                return t;
            }
            if (t != null) {
                logger.log(5, strSubstring + " property set came back with wrong class - " + t.getClass().getName());
            } else {
                logger.log(5, strSubstring + " property set came back as null");
            }
            return null;
        } catch (IOException e) {
            logger.log(7, "can't retrieve property set", e);
            return null;
        }
    }

    protected PropertySet getPropertySet(String setName) throws IOException {
        return getPropertySet(setName, getEncryptionInfo());
    }

    protected PropertySet getPropertySet(String setName, EncryptionInfo encryptionInfo) throws IOException {
        DirectoryNode dirNode = this.directory;
        NPOIFSFileSystem encPoifs = null;
        try {
            if (encryptionInfo != null) {
                try {
                    if (encryptionInfo.isDocPropsEncrypted()) {
                        String encryptedStream = getEncryptedPropertyStreamName();
                        if (!dirNode.hasEntry(encryptedStream)) {
                            throw new EncryptedDocumentException("can't find encrypted property stream '" + encryptedStream + "'");
                        }
                        CryptoAPIDecryptor dec = (CryptoAPIDecryptor) encryptionInfo.getDecryptor();
                        encPoifs = dec.getSummaryEntries(dirNode, encryptedStream);
                        dirNode = encPoifs.getRoot();
                    }
                } catch (IOException e) {
                    throw e;
                } catch (Exception e2) {
                    throw new IOException("Error getting property set with name " + setName, e2);
                }
            }
            if (dirNode != null && dirNode.hasEntry(setName)) {
                DocumentInputStream dis = dirNode.createDocumentInputStream(dirNode.getEntry(setName));
                try {
                    PropertySet propertySetCreate = PropertySetFactory.create(dis);
                    IOUtils.closeQuietly(encPoifs);
                    return propertySetCreate;
                } finally {
                    dis.close();
                }
            }
            IOUtils.closeQuietly(encPoifs);
            return null;
        } catch (Throwable th) {
            IOUtils.closeQuietly(null);
            throw th;
        }
    }

    protected void writeProperties() throws IOException {
        validateInPlaceWritePossible();
        writeProperties(this.directory.getFileSystem(), null);
    }

    protected void writeProperties(NPOIFSFileSystem outFS) throws IOException {
        writeProperties(outFS, null);
    }

    protected void writeProperties(NPOIFSFileSystem outFS, List<String> writtenEntries) throws IOException {
        EncryptionInfo ei = getEncryptionInfo();
        boolean encryptProps = ei != null && ei.isDocPropsEncrypted();
        NPOIFSFileSystem fs = encryptProps ? new NPOIFSFileSystem() : outFS;
        SummaryInformation si = getSummaryInformation();
        if (si != null) {
            writePropertySet(SummaryInformation.DEFAULT_STREAM_NAME, si, fs);
            if (writtenEntries != null) {
                writtenEntries.add(SummaryInformation.DEFAULT_STREAM_NAME);
            }
        }
        DocumentSummaryInformation dsi = getDocumentSummaryInformation();
        if (dsi != null) {
            writePropertySet(DocumentSummaryInformation.DEFAULT_STREAM_NAME, dsi, fs);
            if (writtenEntries != null) {
                writtenEntries.add(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
            }
        }
        if (!encryptProps) {
            return;
        }
        writePropertySet(DocumentSummaryInformation.DEFAULT_STREAM_NAME, PropertySetFactory.newDocumentSummaryInformation(), outFS);
        if (outFS.getRoot().hasEntry(SummaryInformation.DEFAULT_STREAM_NAME)) {
            outFS.getRoot().getEntry(SummaryInformation.DEFAULT_STREAM_NAME).delete();
        }
        Encryptor encGen = ei.getEncryptor();
        if (!(encGen instanceof CryptoAPIEncryptor)) {
            throw new EncryptedDocumentException("Using " + ei.getEncryptionMode() + " encryption. Only CryptoAPI encryption supports encrypted property sets!");
        }
        CryptoAPIEncryptor enc = (CryptoAPIEncryptor) encGen;
        try {
            try {
                enc.setSummaryEntries(outFS.getRoot(), getEncryptedPropertyStreamName(), fs);
            } catch (GeneralSecurityException e) {
                throw new IOException(e);
            }
        } finally {
            fs.close();
        }
    }

    protected void writePropertySet(String name, PropertySet set, NPOIFSFileSystem outFS) throws IOException {
        try {
            PropertySet mSet = new PropertySet(set);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            mSet.write(bOut);
            byte[] data = bOut.toByteArray();
            ByteArrayInputStream bIn = new ByteArrayInputStream(data);
            outFS.createOrUpdateDocument(bIn, name);
            logger.log(3, "Wrote property set " + name + " of size " + data.length);
        } catch (WritingNotSupportedException e) {
            logger.log(7, "Couldn't write property set with name " + name + " as not supported by HPSF yet");
        }
    }

    protected void validateInPlaceWritePossible() throws IllegalStateException {
        DirectoryNode directoryNode = this.directory;
        if (directoryNode == null) {
            throw new IllegalStateException("Newly created Document, cannot save in-place");
        }
        if (directoryNode.getParent() != null) {
            throw new IllegalStateException("This is not the root Document, cannot save embedded resource in-place");
        }
        if (this.directory.getFileSystem() == null || !this.directory.getFileSystem().isInPlaceWriteable()) {
            throw new IllegalStateException("Opened read-only or via an InputStream, a Writeable File is required");
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        DirectoryNode directoryNode = this.directory;
        if (directoryNode != null && directoryNode.getNFileSystem() != null) {
            this.directory.getNFileSystem().close();
            clearDirectory();
        }
    }

    @Internal
    public DirectoryNode getDirectory() {
        return this.directory;
    }

    @Internal
    protected void clearDirectory() {
        this.directory = null;
    }

    @Internal
    protected boolean initDirectory() {
        if (this.directory == null) {
            this.directory = new NPOIFSFileSystem().getRoot();
            return true;
        }
        return false;
    }

    @Internal
    protected DirectoryNode replaceDirectory(DirectoryNode newDirectory) {
        DirectoryNode dn = this.directory;
        this.directory = newDirectory;
        return dn;
    }

    protected String getEncryptedPropertyStreamName() {
        return "encryption";
    }

    public EncryptionInfo getEncryptionInfo() throws IOException {
        return null;
    }
}
