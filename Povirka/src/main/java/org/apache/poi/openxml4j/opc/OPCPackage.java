package org.apache.poi.openxml4j.opc;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import org.apache.poi.openxml4j.opc.internal.ContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.opc.internal.PartUnmarshaller;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.marshallers.DefaultMarshaller;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPackagePropertiesMarshaller;
import org.apache.poi.openxml4j.opc.internal.unmarshallers.PackagePropertiesUnmarshaller;
import org.apache.poi.openxml4j.opc.internal.unmarshallers.UnmarshallContext;
import org.apache.poi.openxml4j.util.Nullable;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.NotImplemented;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public abstract class OPCPackage implements RelationshipSource, Closeable {
    protected ContentTypeManager contentTypeManager;
    protected PartMarshaller defaultPartMarshaller;
    protected boolean isDirty = false;
    protected String originalPackagePath;
    protected OutputStream output;
    private PackageAccess packageAccess;
    protected PackagePropertiesPart packageProperties;
    protected PackagePartCollection partList;
    protected Map<ContentType, PartMarshaller> partMarshallers;
    protected Map<ContentType, PartUnmarshaller> partUnmarshallers;
    protected PackageRelationshipCollection relationships;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) OPCPackage.class);
    protected static final PackageAccess defaultPackageAccess = PackageAccess.READ_WRITE;

    protected abstract void closeImpl() throws IOException;

    protected abstract PackagePart createPartImpl(PackagePartName packagePartName, String str, boolean z);

    protected abstract void flushImpl();

    protected abstract PackagePart getPartImpl(PackagePartName packagePartName);

    protected abstract PackagePart[] getPartsImpl() throws InvalidFormatException;

    protected abstract void removePartImpl(PackagePartName packagePartName);

    protected abstract void revertImpl();

    protected abstract void saveImpl(OutputStream outputStream) throws IOException;

    OPCPackage(PackageAccess access) {
        if (getClass() != ZipPackage.class) {
            throw new IllegalArgumentException("PackageBase may not be subclassed");
        }
        init();
        this.packageAccess = access;
    }

    private void init() {
        this.partMarshallers = new HashMap(5);
        HashMap map = new HashMap(2);
        this.partUnmarshallers = map;
        try {
            map.put(new ContentType(ContentTypes.CORE_PROPERTIES_PART), new PackagePropertiesUnmarshaller());
            this.defaultPartMarshaller = new DefaultMarshaller();
            this.partMarshallers.put(new ContentType(ContentTypes.CORE_PROPERTIES_PART), new ZipPackagePropertiesMarshaller());
        } catch (InvalidFormatException e) {
            throw new OpenXML4JRuntimeException("Package.init() : this exception should never happen, if you read this message please send a mail to the developers team. : " + e.getMessage(), e);
        }
    }

    public static OPCPackage open(String path) throws InvalidFormatException {
        return open(path, defaultPackageAccess);
    }

    public static OPCPackage open(File file) throws InvalidFormatException {
        return open(file, defaultPackageAccess);
    }

    public static OPCPackage open(ZipEntrySource zipEntry) throws InvalidFormatException {
        OPCPackage pack = new ZipPackage(zipEntry, PackageAccess.READ);
        try {
            if (pack.partList == null) {
                pack.getParts();
            }
            return pack;
        } catch (RuntimeException e) {
            IOUtils.closeQuietly(pack);
            throw e;
        } catch (InvalidFormatException e2) {
            IOUtils.closeQuietly(pack);
            throw e2;
        }
    }

    public static OPCPackage open(String path, PackageAccess access) throws InvalidFormatException, InvalidOperationException {
        if (path == null || "".equals(path.trim())) {
            throw new IllegalArgumentException("'path' must be given");
        }
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            throw new IllegalArgumentException("path must not be a directory");
        }
        OPCPackage pack = new ZipPackage(path, access);
        boolean success = false;
        if (pack.partList == null && access != PackageAccess.WRITE) {
            try {
                pack.getParts();
                success = true;
            } finally {
                if (!success) {
                    IOUtils.closeQuietly(pack);
                }
            }
        }
        pack.originalPackagePath = new File(path).getAbsolutePath();
        return pack;
    }

    public static OPCPackage open(File file, PackageAccess access) throws InvalidFormatException {
        if (file == null) {
            throw new IllegalArgumentException("'file' must be given");
        }
        if (file.exists() && file.isDirectory()) {
            throw new IllegalArgumentException("file must not be a directory");
        }
        OPCPackage pack = new ZipPackage(file, access);
        try {
            if (pack.partList == null && access != PackageAccess.WRITE) {
                pack.getParts();
            }
            pack.originalPackagePath = file.getAbsolutePath();
            return pack;
        } catch (RuntimeException e) {
            IOUtils.closeQuietly(pack);
            throw e;
        } catch (InvalidFormatException e2) {
            IOUtils.closeQuietly(pack);
            throw e2;
        }
    }

    public static OPCPackage open(InputStream in) throws InvalidFormatException, IOException {
        OPCPackage pack = new ZipPackage(in, PackageAccess.READ_WRITE);
        try {
            if (pack.partList == null) {
                pack.getParts();
            }
            return pack;
        } catch (RuntimeException e) {
            IOUtils.closeQuietly(pack);
            throw e;
        } catch (InvalidFormatException e2) {
            IOUtils.closeQuietly(pack);
            throw e2;
        }
    }

    public static OPCPackage openOrCreate(File file) throws InvalidFormatException {
        if (file.exists()) {
            return open(file.getAbsolutePath());
        }
        return create(file);
    }

    public static OPCPackage create(String path) {
        return create(new File(path));
    }

    public static OPCPackage create(File file) {
        if (file == null || (file.exists() && file.isDirectory())) {
            throw new IllegalArgumentException("file");
        }
        if (file.exists()) {
            throw new InvalidOperationException("This package (or file) already exists : use the open() method or delete the file.");
        }
        OPCPackage pkg = new ZipPackage();
        pkg.originalPackagePath = file.getAbsolutePath();
        configurePackage(pkg);
        return pkg;
    }

    public static OPCPackage create(OutputStream output) {
        OPCPackage pkg = new ZipPackage();
        pkg.originalPackagePath = null;
        pkg.output = output;
        configurePackage(pkg);
        return pkg;
    }

    private static void configurePackage(OPCPackage pkg) {
        try {
            ZipContentTypeManager zipContentTypeManager = new ZipContentTypeManager(null, pkg);
            pkg.contentTypeManager = zipContentTypeManager;
            zipContentTypeManager.addContentType(PackagingURIHelper.createPartName(PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_URI), ContentTypes.RELATIONSHIPS_PART);
            pkg.contentTypeManager.addContentType(PackagingURIHelper.createPartName("/default.xml"), ContentTypes.PLAIN_OLD_XML);
            PackagePropertiesPart packagePropertiesPart = new PackagePropertiesPart(pkg, PackagingURIHelper.CORE_PROPERTIES_PART_NAME);
            pkg.packageProperties = packagePropertiesPart;
            packagePropertiesPart.setCreatorProperty("Generated by Apache POI OpenXML4J");
            pkg.packageProperties.setCreatedProperty(new Nullable<>(new Date()));
        } catch (InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }

    public void flush() {
        throwExceptionIfReadOnly();
        PackagePropertiesPart packagePropertiesPart = this.packageProperties;
        if (packagePropertiesPart != null) {
            packagePropertiesPart.flush();
        }
        flushImpl();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.packageAccess == PackageAccess.READ) {
            logger.log(5, "The close() method is intended to SAVE a package. This package is open in READ ONLY mode, use the revert() method instead !");
            revert();
            return;
        }
        if (this.contentTypeManager == null) {
            logger.log(5, "Unable to call close() on a package that hasn't been fully opened yet");
            revert();
            return;
        }
        ReentrantReadWriteLock l = new ReentrantReadWriteLock();
        try {
            l.writeLock().lock();
            String str = this.originalPackagePath;
            if (str != null && !"".equals(str.trim())) {
                File targetFile = new File(this.originalPackagePath);
                if (!targetFile.exists() || !this.originalPackagePath.equalsIgnoreCase(targetFile.getAbsolutePath())) {
                    save(targetFile);
                } else {
                    closeImpl();
                }
            } else {
                OutputStream outputStream = this.output;
                if (outputStream != null) {
                    save(outputStream);
                    this.output.close();
                }
            }
            l.writeLock().unlock();
            this.contentTypeManager.clearAll();
        } catch (Throwable th) {
            l.writeLock().unlock();
            throw th;
        }
    }

    public void revert() {
        revertImpl();
    }

    public void addThumbnail(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path");
        }
        String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        FileInputStream is = new FileInputStream(path);
        try {
            addThumbnail(name, is);
        } finally {
            is.close();
        }
    }

    public void addThumbnail(String filename, InputStream data) throws InvalidFormatException, IOException {
        PackagePartName thumbnailPartName;
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("filename");
        }
        String contentType = ContentTypes.getContentTypeFromFileExtension(filename);
        try {
            thumbnailPartName = PackagingURIHelper.createPartName("/docProps/" + filename);
        } catch (InvalidFormatException e) {
            String partName = "/docProps/thumbnail" + filename.substring(filename.lastIndexOf(".") + 1);
            try {
                thumbnailPartName = PackagingURIHelper.createPartName(partName);
            } catch (InvalidFormatException e2) {
                throw new InvalidOperationException("Can't add a thumbnail file named '" + filename + "'", e2);
            }
        }
        if (getPart(thumbnailPartName) != null) {
            throw new InvalidOperationException("You already add a thumbnail named '" + filename + "'");
        }
        PackagePart thumbnailPart = createPart(thumbnailPartName, contentType, false);
        addRelationship(thumbnailPartName, TargetMode.INTERNAL, PackageRelationshipTypes.THUMBNAIL);
        StreamHelper.copyStream(data, thumbnailPart.getOutputStream());
    }

    void throwExceptionIfReadOnly() throws InvalidOperationException {
        if (this.packageAccess == PackageAccess.READ) {
            throw new InvalidOperationException("Operation not allowed, document open in read only mode!");
        }
    }

    void throwExceptionIfWriteOnly() throws InvalidOperationException {
        if (this.packageAccess == PackageAccess.WRITE) {
            throw new InvalidOperationException("Operation not allowed, document open in write only mode!");
        }
    }

    public PackageProperties getPackageProperties() throws InvalidFormatException {
        throwExceptionIfWriteOnly();
        if (this.packageProperties == null) {
            this.packageProperties = new PackagePropertiesPart(this, PackagingURIHelper.CORE_PROPERTIES_PART_NAME);
        }
        return this.packageProperties;
    }

    public PackagePart getPart(PackagePartName partName) {
        throwExceptionIfWriteOnly();
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (this.partList == null) {
            try {
                getParts();
            } catch (InvalidFormatException e) {
                return null;
            }
        }
        return getPartImpl(partName);
    }

    public ArrayList<PackagePart> getPartsByContentType(String contentType) {
        ArrayList<PackagePart> retArr = new ArrayList<>();
        for (PackagePart part : this.partList.sortedValues()) {
            if (part.getContentType().equals(contentType)) {
                retArr.add(part);
            }
        }
        return retArr;
    }

    public ArrayList<PackagePart> getPartsByRelationshipType(String relationshipType) {
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        ArrayList<PackagePart> retArr = new ArrayList<>();
        for (PackageRelationship rel : getRelationshipsByType(relationshipType)) {
            PackagePart part = getPart(rel);
            if (part != null) {
                retArr.add(part);
            }
        }
        Collections.sort(retArr);
        return retArr;
    }

    public List<PackagePart> getPartsByName(Pattern namePattern) {
        if (namePattern == null) {
            throw new IllegalArgumentException("name pattern must not be null");
        }
        Matcher matcher = namePattern.matcher("");
        ArrayList<PackagePart> result = new ArrayList<>();
        for (PackagePart part : this.partList.sortedValues()) {
            PackagePartName partName = part.getPartName();
            if (matcher.reset(partName.getName()).matches()) {
                result.add(part);
            }
        }
        return result;
    }

    public PackagePart getPart(PackageRelationship partRel) {
        ensureRelationships();
        for (PackageRelationship rel : this.relationships) {
            if (rel.getRelationshipType().equals(partRel.getRelationshipType())) {
                try {
                    PackagePart retPart = getPart(PackagingURIHelper.createPartName(rel.getTargetURI()));
                    return retPart;
                } catch (InvalidFormatException e) {
                }
            }
        }
        return null;
    }

    public ArrayList<PackagePart> getParts() throws InvalidFormatException {
        boolean hasCorePropertiesPart;
        throwExceptionIfWriteOnly();
        if (this.partList == null) {
            boolean hasCorePropertiesPart2 = false;
            boolean needCorePropertiesPart = true;
            PackagePart[] parts = getPartsImpl();
            this.partList = new PackagePartCollection();
            int len$ = parts.length;
            int i$ = 0;
            while (i$ < len$) {
                PackagePart part = parts[i$];
                if (this.partList.containsKey(part._partName)) {
                    throw new InvalidFormatException("A part with the name '" + part._partName + "' already exist : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
                }
                if (part.getContentType().equals(ContentTypes.CORE_PROPERTIES_PART)) {
                    if (!hasCorePropertiesPart2) {
                        hasCorePropertiesPart = true;
                    } else {
                        logger.log(5, "OPC Compliance error [M4.1]: there is more than one core properties relationship in the package! POI will use only the first, but other software may reject this file.");
                        hasCorePropertiesPart = hasCorePropertiesPart2;
                    }
                } else {
                    hasCorePropertiesPart = hasCorePropertiesPart2;
                }
                PartUnmarshaller partUnmarshaller = this.partUnmarshallers.get(part._contentType);
                if (partUnmarshaller != null) {
                    UnmarshallContext context = new UnmarshallContext(this, part._partName);
                    try {
                        PackagePart unmarshallPart = partUnmarshaller.unmarshall(context, part.getInputStream());
                        this.partList.put(unmarshallPart._partName, unmarshallPart);
                        if ((unmarshallPart instanceof PackagePropertiesPart) && hasCorePropertiesPart && needCorePropertiesPart) {
                            this.packageProperties = (PackagePropertiesPart) unmarshallPart;
                            needCorePropertiesPart = false;
                        }
                    } catch (IOException e) {
                        logger.log(5, "Unmarshall operation : IOException for " + part._partName);
                    } catch (InvalidOperationException invoe) {
                        throw new InvalidFormatException(invoe.getMessage(), invoe);
                    }
                } else {
                    try {
                        this.partList.put(part._partName, part);
                    } catch (InvalidOperationException e2) {
                        throw new InvalidFormatException(e2.getMessage(), e2);
                    }
                }
                i$++;
                hasCorePropertiesPart2 = hasCorePropertiesPart;
            }
        }
        return new ArrayList<>(this.partList.sortedValues());
    }

    public PackagePart createPart(PackagePartName partName, String contentType) {
        return createPart(partName, contentType, true);
    }

    PackagePart createPart(PackagePartName partName, String contentType, boolean loadRelationships) {
        throwExceptionIfReadOnly();
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (contentType == null || contentType.equals("")) {
            throw new IllegalArgumentException("contentType");
        }
        if (this.partList.containsKey(partName) && !this.partList.get(partName).isDeleted()) {
            throw new PartAlreadyExistsException("A part with the name '" + partName.getName() + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
        }
        if (contentType.equals(ContentTypes.CORE_PROPERTIES_PART) && this.packageProperties != null) {
            throw new InvalidOperationException("OPC Compliance error [M4.1]: you try to add more than one core properties relationship in the package !");
        }
        PackagePart part = createPartImpl(partName, contentType, loadRelationships);
        this.contentTypeManager.addContentType(partName, contentType);
        this.partList.put(partName, part);
        this.isDirty = true;
        return part;
    }

    public PackagePart createPart(PackagePartName partName, String contentType, ByteArrayOutputStream content) {
        PackagePart addedPart = createPart(partName, contentType);
        if (addedPart == null || content == null) {
            return null;
        }
        try {
            OutputStream partOutput = addedPart.getOutputStream();
            if (partOutput == null) {
                return null;
            }
            partOutput.write(content.toByteArray(), 0, content.size());
            partOutput.close();
            return addedPart;
        } catch (IOException e) {
            return null;
        }
    }

    protected PackagePart addPackagePart(PackagePart part) {
        throwExceptionIfReadOnly();
        if (part == null) {
            throw new IllegalArgumentException("part");
        }
        if (this.partList.containsKey(part._partName)) {
            if (!this.partList.get(part._partName).isDeleted()) {
                throw new InvalidOperationException("A part with the name '" + part._partName.getName() + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
            }
            part.setDeleted(false);
            this.partList.remove(part._partName);
        }
        this.partList.put(part._partName, part);
        this.isDirty = true;
        return part;
    }

    public void removePart(PackagePart part) {
        if (part != null) {
            removePart(part.getPartName());
        }
    }

    public void removePart(PackagePartName partName) {
        PackagePart part;
        throwExceptionIfReadOnly();
        if (partName == null || !containPart(partName)) {
            throw new IllegalArgumentException("partName");
        }
        if (this.partList.containsKey(partName)) {
            this.partList.get(partName).setDeleted(true);
            removePartImpl(partName);
            this.partList.remove(partName);
        } else {
            removePartImpl(partName);
        }
        this.contentTypeManager.removeContentType(partName);
        if (partName.isRelationshipPartURI()) {
            URI sourceURI = PackagingURIHelper.getSourcePartUriFromRelationshipPartUri(partName.getURI());
            try {
                PackagePartName sourcePartName = PackagingURIHelper.createPartName(sourceURI);
                if (sourcePartName.getURI().equals(PackagingURIHelper.PACKAGE_ROOT_URI)) {
                    clearRelationships();
                } else if (containPart(sourcePartName) && (part = getPart(sourcePartName)) != null) {
                    part.clearRelationships();
                }
            } catch (InvalidFormatException e) {
                logger.log(7, "Part name URI '" + sourceURI + "' is not valid ! This message is not intended to be displayed !");
                return;
            }
        }
        this.isDirty = true;
    }

    public void removePartRecursive(PackagePartName partName) throws InvalidFormatException {
        PackagePart relPart = this.partList.get(PackagingURIHelper.getRelationshipPartName(partName));
        PackagePart partToRemove = this.partList.get(partName);
        if (relPart != null) {
            PackageRelationshipCollection partRels = new PackageRelationshipCollection(partToRemove);
            for (PackageRelationship rel : partRels) {
                PackagePartName partNameToRemove = PackagingURIHelper.createPartName(PackagingURIHelper.resolvePartUri(rel.getSourceURI(), rel.getTargetURI()));
                removePart(partNameToRemove);
            }
            removePart(relPart._partName);
        }
        removePart(partToRemove._partName);
    }

    public void deletePart(PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        removePart(partName);
        removePart(PackagingURIHelper.getRelationshipPartName(partName));
    }

    public void deletePartRecursive(PackagePartName partName) {
        if (partName == null || !containPart(partName)) {
            throw new IllegalArgumentException("partName");
        }
        PackagePart partToDelete = getPart(partName);
        removePart(partName);
        try {
            for (PackageRelationship relationship : partToDelete.getRelationships()) {
                PackagePartName targetPartName = PackagingURIHelper.createPartName(PackagingURIHelper.resolvePartUri(partName.getURI(), relationship.getTargetURI()));
                deletePartRecursive(targetPartName);
            }
            PackagePartName relationshipPartName = PackagingURIHelper.getRelationshipPartName(partName);
            if (relationshipPartName != null && containPart(relationshipPartName)) {
                removePart(relationshipPartName);
            }
        } catch (InvalidFormatException e) {
            logger.log(5, "An exception occurs while deleting part '" + partName.getName() + "'. Some parts may remain in the package. - " + e.getMessage());
        }
    }

    public boolean containPart(PackagePartName partName) {
        return getPart(partName) != null;
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addRelationship(PackagePartName targetPartName, TargetMode targetMode, String relationshipType, String relID) {
        if (relationshipType.equals(PackageRelationshipTypes.CORE_PROPERTIES) && this.packageProperties != null) {
            throw new InvalidOperationException("OPC Compliance error [M4.1]: can't add another core properties part ! Use the built-in package method instead.");
        }
        if (targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        ensureRelationships();
        PackageRelationship retRel = this.relationships.addRelationship(targetPartName.getURI(), targetMode, relationshipType, relID);
        this.isDirty = true;
        return retRel;
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addRelationship(PackagePartName targetPartName, TargetMode targetMode, String relationshipType) {
        return addRelationship(targetPartName, targetMode, relationshipType, null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addExternalRelationship(String target, String relationshipType) {
        return addExternalRelationship(target, relationshipType, null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship addExternalRelationship(String target, String relationshipType, String id) {
        if (target == null) {
            throw new IllegalArgumentException("target");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        try {
            URI targetURI = new URI(target);
            ensureRelationships();
            PackageRelationship retRel = this.relationships.addRelationship(targetURI, TargetMode.EXTERNAL, relationshipType, id);
            this.isDirty = true;
            return retRel;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public void removeRelationship(String id) {
        PackageRelationshipCollection packageRelationshipCollection = this.relationships;
        if (packageRelationshipCollection != null) {
            packageRelationshipCollection.removeRelationship(id);
            this.isDirty = true;
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationshipCollection getRelationships() {
        return getRelationshipsHelper(null);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationshipCollection getRelationshipsByType(String relationshipType) {
        throwExceptionIfWriteOnly();
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        return getRelationshipsHelper(relationshipType);
    }

    private PackageRelationshipCollection getRelationshipsHelper(String id) {
        throwExceptionIfWriteOnly();
        ensureRelationships();
        return this.relationships.getRelationships(id);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public void clearRelationships() {
        PackageRelationshipCollection packageRelationshipCollection = this.relationships;
        if (packageRelationshipCollection != null) {
            packageRelationshipCollection.clear();
            this.isDirty = true;
        }
    }

    public void ensureRelationships() {
        if (this.relationships == null) {
            try {
                this.relationships = new PackageRelationshipCollection(this);
            } catch (InvalidFormatException e) {
                this.relationships = new PackageRelationshipCollection();
            }
        }
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public PackageRelationship getRelationship(String id) {
        return this.relationships.getRelationshipByID(id);
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public boolean hasRelationships() {
        return this.relationships.size() > 0;
    }

    @Override // org.apache.poi.openxml4j.opc.RelationshipSource
    public boolean isRelationshipExists(PackageRelationship rel) {
        for (PackageRelationship r : this.relationships) {
            if (r == rel) {
                return true;
            }
        }
        return false;
    }

    public void addMarshaller(String contentType, PartMarshaller marshaller) {
        try {
            this.partMarshallers.put(new ContentType(contentType), marshaller);
        } catch (InvalidFormatException e) {
            logger.log(5, "The specified content type is not valid: '" + e.getMessage() + "'. The marshaller will not be added !");
        }
    }

    public void addUnmarshaller(String contentType, PartUnmarshaller unmarshaller) {
        try {
            this.partUnmarshallers.put(new ContentType(contentType), unmarshaller);
        } catch (InvalidFormatException e) {
            logger.log(5, "The specified content type is not valid: '" + e.getMessage() + "'. The unmarshaller will not be added !");
        }
    }

    public void removeMarshaller(String contentType) {
        try {
            this.partMarshallers.remove(new ContentType(contentType));
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUnmarshaller(String contentType) {
        try {
            this.partUnmarshallers.remove(new ContentType(contentType));
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public PackageAccess getPackageAccess() {
        return this.packageAccess;
    }

    @NotImplemented
    public boolean validatePackage(OPCPackage pkg) throws InvalidFormatException {
        throw new InvalidOperationException("Not implemented yet !!!");
    }

    public void save(File targetFile) throws IOException {
        if (targetFile == null) {
            throw new IllegalArgumentException("targetFile");
        }
        throwExceptionIfReadOnly();
        if (targetFile.exists() && targetFile.getAbsolutePath().equals(this.originalPackagePath)) {
            throw new InvalidOperationException("You can't call save(File) to save to the currently open file. To save to the current file, please just call close()");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            save(fos);
            fos.close();
        } catch (Throwable th) {
            if (fos != null) {
                fos.close();
            }
            throw th;
        }
    }

    public void save(OutputStream outputStream) throws IOException {
        throwExceptionIfReadOnly();
        saveImpl(outputStream);
    }

    public boolean replaceContentType(String oldContentType, String newContentType) {
        boolean success = false;
        ArrayList<PackagePart> list = getPartsByContentType(oldContentType);
        for (PackagePart packagePart : list) {
            if (packagePart.getContentType().equals(oldContentType)) {
                PackagePartName partName = packagePart.getPartName();
                this.contentTypeManager.addContentType(partName, newContentType);
                try {
                    packagePart.setContentType(newContentType);
                    success = true;
                    this.isDirty = true;
                } catch (InvalidFormatException e) {
                    throw new OpenXML4JRuntimeException("invalid content type - " + newContentType, e);
                }
            }
        }
        return success;
    }

    public void registerPartAndContentType(PackagePart part) {
        addPackagePart(part);
        this.contentTypeManager.addContentType(part.getPartName(), part.getContentType());
        this.isDirty = true;
    }

    public void unregisterPartAndContentType(PackagePartName partName) {
        removePart(partName);
        this.contentTypeManager.removeContentType(partName);
        this.isDirty = true;
    }
}
