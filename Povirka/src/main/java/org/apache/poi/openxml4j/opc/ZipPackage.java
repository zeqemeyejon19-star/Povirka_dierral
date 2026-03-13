package org.apache.poi.openxml4j.opc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.ODFNotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.internal.ContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.FileHelper;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePart;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.TempFile;

/* JADX INFO: loaded from: classes.dex */
public final class ZipPackage extends OPCPackage {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) ZipPackage.class);
    private static final String MIMETYPE = "mimetype";
    private static final String SETTINGS_XML = "settings.xml";
    private final ZipEntrySource zipArchive;

    public ZipPackage() {
        super(defaultPackageAccess);
        this.zipArchive = null;
        try {
            this.contentTypeManager = new ZipContentTypeManager(null, this);
        } catch (InvalidFormatException e) {
            LOG.log(5, "Could not parse ZipPackage", e);
        }
    }

    ZipPackage(InputStream in, PackageAccess access) throws IOException {
        super(access);
        ZipSecureFile.ThresholdInputStream zis = ZipHelper.openZipStream(in);
        try {
            this.zipArchive = new ZipInputStreamZipEntrySource(zis);
        } catch (IOException e) {
            IOUtils.closeQuietly(zis);
            throw new IOException("Failed to read zip entry source", e);
        }
    }

    ZipPackage(String path, PackageAccess access) throws InvalidOperationException {
        this(new File(path), access);
    }

    ZipPackage(File file, PackageAccess access) throws InvalidOperationException {
        ZipEntrySource ze;
        super(access);
        try {
            ZipFile zipFile = ZipHelper.openZipFile(file);
            ze = new ZipFileZipEntrySource(zipFile);
        } catch (IOException e) {
            if (access == PackageAccess.WRITE) {
                throw new InvalidOperationException("Can't open the specified file: '" + file + "'", e);
            }
            LOG.log(7, "Error in zip file " + file + " - falling back to stream processing (i.e. ignoring zip central directory)");
            ze = openZipEntrySourceStream(file);
        }
        this.zipArchive = ze;
    }

    private static ZipEntrySource openZipEntrySourceStream(File file) throws InvalidOperationException {
        try {
            FileInputStream fis = new FileInputStream(file);
            try {
                return openZipEntrySourceStream(fis);
            } catch (Exception e) {
                IOUtils.closeQuietly(fis);
                if (e instanceof InvalidOperationException) {
                    throw ((InvalidOperationException) e);
                }
                throw new InvalidOperationException("Failed to read the file input stream from file: '" + file + "'", e);
            }
        } catch (FileNotFoundException e2) {
            throw new InvalidOperationException("Can't open the specified file input stream from file: '" + file + "'", e2);
        }
    }

    private static ZipEntrySource openZipEntrySourceStream(FileInputStream fis) throws InvalidOperationException {
        try {
            ZipSecureFile.ThresholdInputStream zis = ZipHelper.openZipStream(fis);
            try {
                return openZipEntrySourceStream(zis);
            } catch (Exception e) {
                IOUtils.closeQuietly(zis);
                if (e instanceof InvalidOperationException) {
                    throw ((InvalidOperationException) e);
                }
                throw new InvalidOperationException("Failed to read the zip entry source stream", e);
            }
        } catch (IOException e2) {
            throw new InvalidOperationException("Could not open the file input stream", e2);
        }
    }

    private static ZipEntrySource openZipEntrySourceStream(ZipSecureFile.ThresholdInputStream zis) throws InvalidOperationException {
        try {
            return new ZipInputStreamZipEntrySource(zis);
        } catch (IOException e) {
            throw new InvalidOperationException("Could not open the specified zip entry source stream", e);
        }
    }

    ZipPackage(ZipEntrySource zipEntry, PackageAccess access) {
        super(access);
        this.zipArchive = zipEntry;
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected PackagePart[] getPartsImpl() throws InvalidFormatException {
        String contentType;
        String contentType2;
        if (this.partList == null) {
            this.partList = new PackagePartCollection();
        }
        ZipEntrySource zipEntrySource = this.zipArchive;
        if (zipEntrySource == null) {
            return (PackagePart[]) this.partList.sortedValues().toArray(new PackagePart[this.partList.size()]);
        }
        Enumeration<? extends ZipEntry> entries = zipEntrySource.getEntries();
        while (true) {
            if (!entries.hasMoreElements()) {
                break;
            }
            ZipEntry entry = entries.nextElement();
            if (entry.getName().equalsIgnoreCase(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                try {
                    this.contentTypeManager = new ZipContentTypeManager(getZipArchive().getInputStream(entry), this);
                    break;
                } catch (IOException e) {
                    throw new InvalidFormatException(e.getMessage(), e);
                }
            }
        }
        if (this.contentTypeManager == null) {
            int numEntries = 0;
            boolean hasMimetype = false;
            boolean hasSettingsXML = false;
            Enumeration<? extends ZipEntry> entries2 = this.zipArchive.getEntries();
            while (entries2.hasMoreElements()) {
                String name = entries2.nextElement().getName();
                if (MIMETYPE.equals(name)) {
                    hasMimetype = true;
                }
                if (SETTINGS_XML.equals(name)) {
                    hasSettingsXML = true;
                }
                numEntries++;
            }
            if (hasMimetype && hasSettingsXML) {
                throw new ODFNotOfficeXmlFileException("The supplied data appears to be in ODF (Open Document) Format. Formats like these (eg ODS, ODP) are not supported, try Apache ODFToolkit");
            }
            if (numEntries == 0) {
                throw new NotOfficeXmlFileException("No valid entries or contents found, this is not a valid OOXML (Office Open XML) file");
            }
            throw new InvalidFormatException("Package should contain a content type part [M1.13]");
        }
        Enumeration<? extends ZipEntry> entries3 = this.zipArchive.getEntries();
        while (entries3.hasMoreElements()) {
            ZipEntry entry2 = entries3.nextElement();
            PackagePartName partName = buildPartName(entry2);
            if (partName != null && (contentType2 = this.contentTypeManager.getContentType(partName)) != null && contentType2.equals(ContentTypes.RELATIONSHIPS_PART)) {
                try {
                    PackagePart part = new ZipPackagePart(this, entry2, partName, contentType2);
                    this.partList.put(partName, part);
                } catch (InvalidOperationException e2) {
                    throw new InvalidFormatException(e2.getMessage(), e2);
                }
            }
        }
        Enumeration<? extends ZipEntry> entries4 = this.zipArchive.getEntries();
        while (entries4.hasMoreElements()) {
            ZipEntry entry3 = entries4.nextElement();
            PackagePartName partName2 = buildPartName(entry3);
            if (partName2 != null && ((contentType = this.contentTypeManager.getContentType(partName2)) == null || !contentType.equals(ContentTypes.RELATIONSHIPS_PART))) {
                if (contentType != null) {
                    try {
                        PackagePart part2 = new ZipPackagePart(this, entry3, partName2, contentType);
                        this.partList.put(partName2, part2);
                    } catch (InvalidOperationException e3) {
                        throw new InvalidFormatException(e3.getMessage(), e3);
                    }
                } else {
                    throw new InvalidFormatException("The part " + partName2.getURI().getPath() + " does not have any content type ! Rule: Package require content types when retrieving a part from a package. [M.1.14]");
                }
            }
        }
        return (PackagePart[]) this.partList.sortedValues().toArray(new PackagePart[this.partList.size()]);
    }

    private PackagePartName buildPartName(ZipEntry entry) {
        try {
            if (entry.getName().equalsIgnoreCase(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                return null;
            }
            return PackagingURIHelper.createPartName(ZipHelper.getOPCNameFromZipItemName(entry.getName()));
        } catch (Exception e) {
            LOG.log(5, "Entry " + entry.getName() + " is not valid, so this part won't be add to the package.", e);
            return null;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected PackagePart createPartImpl(PackagePartName partName, String contentType, boolean loadRelationships) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType");
        }
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        try {
            return new MemoryPackagePart(this, partName, contentType, loadRelationships);
        } catch (InvalidFormatException e) {
            LOG.log(5, e);
            return null;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected void removePartImpl(PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partUri");
        }
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected void flushImpl() {
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected void closeImpl() throws IOException {
        flush();
        if (this.originalPackagePath == null || "".equals(this.originalPackagePath)) {
            return;
        }
        File targetFile = new File(this.originalPackagePath);
        if (!targetFile.exists()) {
            throw new InvalidOperationException("Can't close a package not previously open with the open() method !");
        }
        String tempFileName = generateTempFileName(FileHelper.getDirectory(targetFile));
        File tempFile = TempFile.createTempFile(tempFileName, ".tmp");
        try {
            save(tempFile);
            IOUtils.closeQuietly(this.zipArchive);
            try {
                FileHelper.copyFile(tempFile, targetFile);
                if (tempFile.delete()) {
                    return;
                }
                LOG.log(5, "The temporary file: '" + targetFile.getAbsolutePath() + "' cannot be deleted ! Make sure that no other application use it.");
            } catch (Throwable th) {
                if (!tempFile.delete()) {
                    LOG.log(5, "The temporary file: '" + targetFile.getAbsolutePath() + "' cannot be deleted ! Make sure that no other application use it.");
                }
                throw th;
            }
        } catch (Throwable th2) {
            IOUtils.closeQuietly(this.zipArchive);
            try {
                FileHelper.copyFile(tempFile, targetFile);
                if (!tempFile.delete()) {
                    LOG.log(5, "The temporary file: '" + targetFile.getAbsolutePath() + "' cannot be deleted ! Make sure that no other application use it.");
                }
                throw th2;
            } catch (Throwable th3) {
                if (!tempFile.delete()) {
                    LOG.log(5, "The temporary file: '" + targetFile.getAbsolutePath() + "' cannot be deleted ! Make sure that no other application use it.");
                }
                throw th3;
            }
        }
    }

    private synchronized String generateTempFileName(File directory) {
        File tmpFilename;
        do {
            tmpFilename = new File(directory.getAbsoluteFile() + File.separator + "OpenXML4J" + System.nanoTime());
        } while (tmpFilename.exists());
        return FileHelper.getFilename(tmpFilename.getAbsoluteFile());
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected void revertImpl() {
        try {
            ZipEntrySource zipEntrySource = this.zipArchive;
            if (zipEntrySource != null) {
                zipEntrySource.close();
            }
        } catch (IOException e) {
        }
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    protected PackagePart getPartImpl(PackagePartName partName) {
        if (this.partList.containsKey(partName)) {
            return this.partList.get(partName);
        }
        return null;
    }

    @Override // org.apache.poi.openxml4j.opc.OPCPackage
    public void saveImpl(OutputStream outputStream) {
        ZipOutputStream zos;
        throwExceptionIfReadOnly();
        try {
            if (!(outputStream instanceof ZipOutputStream)) {
                zos = new ZipOutputStream(outputStream);
            } else {
                zos = (ZipOutputStream) outputStream;
            }
        } catch (OpenXML4JRuntimeException e) {
            throw e;
        } catch (Exception e2) {
            e = e2;
        }
        try {
            if (getPartsByRelationshipType(PackageRelationshipTypes.CORE_PROPERTIES).size() == 0 && getPartsByRelationshipType(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376).size() == 0) {
                LOG.log(1, "Save core properties part");
                getPackageProperties();
                addPackagePart(this.packageProperties);
                this.relationships.addRelationship(this.packageProperties.getPartName().getURI(), TargetMode.INTERNAL, PackageRelationshipTypes.CORE_PROPERTIES, null);
                if (!this.contentTypeManager.isContentTypeRegister(ContentTypes.CORE_PROPERTIES_PART)) {
                    this.contentTypeManager.addContentType(this.packageProperties.getPartName(), ContentTypes.CORE_PROPERTIES_PART);
                }
            }
            POILogger pOILogger = LOG;
            pOILogger.log(1, "Save package relationships");
            ZipPartMarshaller.marshallRelationshipPart(getRelationships(), PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_PART_NAME, zos);
            pOILogger.log(1, "Save content types part");
            this.contentTypeManager.save(zos);
            for (PackagePart part : getParts()) {
                if (!part.isRelationshipPart()) {
                    PackagePartName ppn = part.getPartName();
                    LOG.log(1, "Save part '" + ZipHelper.getZipItemNameFromOPCName(ppn.getName()) + "'");
                    PartMarshaller marshaller = this.partMarshallers.get(part._contentType);
                    String errMsg = "The part " + ppn.getURI() + " failed to be saved in the stream with marshaller ";
                    if (marshaller != null) {
                        if (!marshaller.marshall(part, zos)) {
                            throw new OpenXML4JException(errMsg + marshaller);
                        }
                    } else if (!this.defaultPartMarshaller.marshall(part, zos)) {
                        throw new OpenXML4JException(errMsg + this.defaultPartMarshaller);
                    }
                }
            }
            zos.close();
        } catch (OpenXML4JRuntimeException e3) {
            throw e3;
        } catch (Exception e4) {
            e = e4;
            throw new OpenXML4JRuntimeException("Fail to save: an error occurs while saving the package : " + e.getMessage(), e);
        }
    }

    public ZipEntrySource getZipArchive() {
        return this.zipArchive;
    }
}
