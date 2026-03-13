package org.apache.poi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.StreamHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.util.Nullable;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument;

/* JADX INFO: loaded from: classes.dex */
public class POIXMLProperties {
    private static final PropertiesDocument NEW_CUST_INSTANCE;
    private static final org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument NEW_EXT_INSTANCE;
    private CoreProperties core;
    private CustomProperties cust;
    private PackagePart custPart;
    private ExtendedProperties ext;
    private PackagePart extPart;
    private OPCPackage pkg;

    static {
        org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument propertiesDocumentNewInstance = PropertiesDocument.Factory.newInstance();
        NEW_EXT_INSTANCE = propertiesDocumentNewInstance;
        propertiesDocumentNewInstance.addNewProperties();
        org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument propertiesDocumentNewInstance2 = PropertiesDocument.Factory.newInstance();
        NEW_CUST_INSTANCE = propertiesDocumentNewInstance2;
        propertiesDocumentNewInstance2.addNewProperties();
    }

    public POIXMLProperties(OPCPackage docPackage) throws XmlException, OpenXML4JException, IOException {
        this.pkg = docPackage;
        this.core = new CoreProperties((PackagePropertiesPart) this.pkg.getPackageProperties());
        PackageRelationshipCollection extRel = this.pkg.getRelationshipsByType(PackageRelationshipTypes.EXTENDED_PROPERTIES);
        if (extRel.size() == 1) {
            PackagePart part = this.pkg.getPart(extRel.getRelationship(0));
            this.extPart = part;
            org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument props = PropertiesDocument.Factory.parse(part.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ext = new ExtendedProperties(props);
        } else {
            this.extPart = null;
            this.ext = new ExtendedProperties(NEW_EXT_INSTANCE.copy());
        }
        PackageRelationshipCollection custRel = this.pkg.getRelationshipsByType(PackageRelationshipTypes.CUSTOM_PROPERTIES);
        if (custRel.size() == 1) {
            PackagePart part2 = this.pkg.getPart(custRel.getRelationship(0));
            this.custPart = part2;
            org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument props2 = PropertiesDocument.Factory.parse(part2.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.cust = new CustomProperties(props2);
            return;
        }
        this.custPart = null;
        this.cust = new CustomProperties(NEW_CUST_INSTANCE.copy());
    }

    public CoreProperties getCoreProperties() {
        return this.core;
    }

    public ExtendedProperties getExtendedProperties() {
        return this.ext;
    }

    public CustomProperties getCustomProperties() {
        return this.cust;
    }

    protected PackagePart getThumbnailPart() {
        PackageRelationshipCollection rels = this.pkg.getRelationshipsByType(PackageRelationshipTypes.THUMBNAIL);
        if (rels.size() == 1) {
            return this.pkg.getPart(rels.getRelationship(0));
        }
        return null;
    }

    public String getThumbnailFilename() {
        PackagePart tPart = getThumbnailPart();
        if (tPart == null) {
            return null;
        }
        String name = tPart.getPartName().getName();
        return name.substring(name.lastIndexOf(47));
    }

    public InputStream getThumbnailImage() throws IOException {
        PackagePart tPart = getThumbnailPart();
        if (tPart == null) {
            return null;
        }
        return tPart.getInputStream();
    }

    public void setThumbnail(String filename, InputStream imageData) throws InvalidFormatException, IOException {
        PackagePart tPart = getThumbnailPart();
        if (tPart == null) {
            this.pkg.addThumbnail(filename, imageData);
            return;
        }
        String newType = ContentTypes.getContentTypeFromFileExtension(filename);
        if (!newType.equals(tPart.getContentType())) {
            throw new IllegalArgumentException("Can't set a Thumbnail of type " + newType + " when existing one is of a different type " + tPart.getContentType());
        }
        StreamHelper.copyStream(imageData, tPart.getOutputStream());
    }

    public void commit() throws IOException {
        if (this.extPart == null && !NEW_EXT_INSTANCE.toString().equals(this.ext.props.toString())) {
            try {
                PackagePartName prtname = PackagingURIHelper.createPartName("/docProps/app.xml");
                this.pkg.addRelationship(prtname, TargetMode.INTERNAL, PackageRelationshipTypes.EXTENDED_PROPERTIES);
                this.extPart = this.pkg.createPart(prtname, "application/vnd.openxmlformats-officedocument.extended-properties+xml");
            } catch (InvalidFormatException e) {
                throw new POIXMLException(e);
            }
        }
        if (this.custPart == null && !NEW_CUST_INSTANCE.toString().equals(this.cust.props.toString())) {
            try {
                PackagePartName prtname2 = PackagingURIHelper.createPartName("/docProps/custom.xml");
                this.pkg.addRelationship(prtname2, TargetMode.INTERNAL, PackageRelationshipTypes.CUSTOM_PROPERTIES);
                this.custPart = this.pkg.createPart(prtname2, "application/vnd.openxmlformats-officedocument.custom-properties+xml");
            } catch (InvalidFormatException e2) {
                throw new POIXMLException(e2);
            }
        }
        PackagePart packagePart = this.extPart;
        if (packagePart != null) {
            OutputStream out = packagePart.getOutputStream();
            if (this.extPart.getSize() > 0) {
                this.extPart.clear();
            }
            this.ext.props.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            out.close();
        }
        PackagePart packagePart2 = this.custPart;
        if (packagePart2 != null) {
            OutputStream out2 = packagePart2.getOutputStream();
            this.cust.props.save(out2, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            out2.close();
        }
    }

    public static class CoreProperties {
        private PackagePropertiesPart part;

        private CoreProperties(PackagePropertiesPart part) {
            this.part = part;
        }

        public String getCategory() {
            return this.part.getCategoryProperty().getValue();
        }

        public void setCategory(String category) {
            this.part.setCategoryProperty(category);
        }

        public String getContentStatus() {
            return this.part.getContentStatusProperty().getValue();
        }

        public void setContentStatus(String contentStatus) {
            this.part.setContentStatusProperty(contentStatus);
        }

        public String getContentType() {
            return this.part.getContentTypeProperty().getValue();
        }

        public void setContentType(String contentType) {
            this.part.setContentTypeProperty(contentType);
        }

        public Date getCreated() {
            return this.part.getCreatedProperty().getValue();
        }

        public void setCreated(Nullable<Date> date) {
            this.part.setCreatedProperty(date);
        }

        public void setCreated(String date) {
            this.part.setCreatedProperty(date);
        }

        public String getCreator() {
            return this.part.getCreatorProperty().getValue();
        }

        public void setCreator(String creator) {
            this.part.setCreatorProperty(creator);
        }

        public String getDescription() {
            return this.part.getDescriptionProperty().getValue();
        }

        public void setDescription(String description) {
            this.part.setDescriptionProperty(description);
        }

        public String getIdentifier() {
            return this.part.getIdentifierProperty().getValue();
        }

        public void setIdentifier(String identifier) {
            this.part.setIdentifierProperty(identifier);
        }

        public String getKeywords() {
            return this.part.getKeywordsProperty().getValue();
        }

        public void setKeywords(String keywords) {
            this.part.setKeywordsProperty(keywords);
        }

        public Date getLastPrinted() {
            return this.part.getLastPrintedProperty().getValue();
        }

        public void setLastPrinted(Nullable<Date> date) {
            this.part.setLastPrintedProperty(date);
        }

        public void setLastPrinted(String date) {
            this.part.setLastPrintedProperty(date);
        }

        public String getLastModifiedByUser() {
            return this.part.getLastModifiedByProperty().getValue();
        }

        public void setLastModifiedByUser(String user) {
            this.part.setLastModifiedByProperty(user);
        }

        public Date getModified() {
            return this.part.getModifiedProperty().getValue();
        }

        public void setModified(Nullable<Date> date) {
            this.part.setModifiedProperty(date);
        }

        public void setModified(String date) {
            this.part.setModifiedProperty(date);
        }

        public String getSubject() {
            return this.part.getSubjectProperty().getValue();
        }

        public void setSubjectProperty(String subject) {
            this.part.setSubjectProperty(subject);
        }

        public void setTitle(String title) {
            this.part.setTitleProperty(title);
        }

        public String getTitle() {
            return this.part.getTitleProperty().getValue();
        }

        public String getRevision() {
            return this.part.getRevisionProperty().getValue();
        }

        public void setRevision(String revision) {
            try {
                Long.valueOf(revision);
                this.part.setRevisionProperty(revision);
            } catch (NumberFormatException e) {
            }
        }

        public PackagePropertiesPart getUnderlyingProperties() {
            return this.part;
        }
    }

    public static class ExtendedProperties {
        private org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument props;

        private ExtendedProperties(org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.PropertiesDocument props) {
            this.props = props;
        }

        public CTProperties getUnderlyingProperties() {
            return this.props.getProperties();
        }

        public String getTemplate() {
            if (this.props.getProperties().isSetTemplate()) {
                return this.props.getProperties().getTemplate();
            }
            return null;
        }

        public String getManager() {
            if (this.props.getProperties().isSetManager()) {
                return this.props.getProperties().getManager();
            }
            return null;
        }

        public String getCompany() {
            if (this.props.getProperties().isSetCompany()) {
                return this.props.getProperties().getCompany();
            }
            return null;
        }

        public String getPresentationFormat() {
            if (this.props.getProperties().isSetPresentationFormat()) {
                return this.props.getProperties().getPresentationFormat();
            }
            return null;
        }

        public String getApplication() {
            if (this.props.getProperties().isSetApplication()) {
                return this.props.getProperties().getApplication();
            }
            return null;
        }

        public String getAppVersion() {
            if (this.props.getProperties().isSetAppVersion()) {
                return this.props.getProperties().getAppVersion();
            }
            return null;
        }

        public int getPages() {
            if (this.props.getProperties().isSetPages()) {
                return this.props.getProperties().getPages();
            }
            return -1;
        }

        public int getWords() {
            if (this.props.getProperties().isSetWords()) {
                return this.props.getProperties().getWords();
            }
            return -1;
        }

        public int getCharacters() {
            if (this.props.getProperties().isSetCharacters()) {
                return this.props.getProperties().getCharacters();
            }
            return -1;
        }

        public int getCharactersWithSpaces() {
            if (this.props.getProperties().isSetCharactersWithSpaces()) {
                return this.props.getProperties().getCharactersWithSpaces();
            }
            return -1;
        }

        public int getLines() {
            if (this.props.getProperties().isSetLines()) {
                return this.props.getProperties().getLines();
            }
            return -1;
        }

        public int getParagraphs() {
            if (this.props.getProperties().isSetParagraphs()) {
                return this.props.getProperties().getParagraphs();
            }
            return -1;
        }

        public int getSlides() {
            if (this.props.getProperties().isSetSlides()) {
                return this.props.getProperties().getSlides();
            }
            return -1;
        }

        public int getNotes() {
            if (this.props.getProperties().isSetNotes()) {
                return this.props.getProperties().getNotes();
            }
            return -1;
        }

        public int getTotalTime() {
            if (this.props.getProperties().isSetTotalTime()) {
                return this.props.getProperties().getTotalTime();
            }
            return -1;
        }

        public int getHiddenSlides() {
            if (this.props.getProperties().isSetHiddenSlides()) {
                return this.props.getProperties().getHiddenSlides();
            }
            return -1;
        }

        public int getMMClips() {
            if (this.props.getProperties().isSetMMClips()) {
                return this.props.getProperties().getMMClips();
            }
            return -1;
        }

        public String getHyperlinkBase() {
            if (this.props.getProperties().isSetHyperlinkBase()) {
                return this.props.getProperties().getHyperlinkBase();
            }
            return null;
        }
    }

    public static class CustomProperties {
        public static final String FORMAT_ID = "{D5CDD505-2E9C-101B-9397-08002B2CF9AE}";
        private org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument props;

        private CustomProperties(org.openxmlformats.schemas.officeDocument.x2006.customProperties.PropertiesDocument props) {
            this.props = props;
        }

        public org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties getUnderlyingProperties() {
            return this.props.getProperties();
        }

        private CTProperty add(String name) {
            if (contains(name)) {
                throw new IllegalArgumentException("A property with this name already exists in the custom properties");
            }
            CTProperty p = this.props.getProperties().addNewProperty();
            int pid = nextPid();
            p.setPid(pid);
            p.setFmtid(FORMAT_ID);
            p.setName(name);
            return p;
        }

        public void addProperty(String name, String value) {
            CTProperty p = add(name);
            p.setLpwstr(value);
        }

        public void addProperty(String name, double value) {
            CTProperty p = add(name);
            p.setR8(value);
        }

        public void addProperty(String name, int value) {
            CTProperty p = add(name);
            p.setI4(value);
        }

        public void addProperty(String name, boolean value) {
            CTProperty p = add(name);
            p.setBool(value);
        }

        protected int nextPid() {
            int propid = 1;
            CTProperty[] arr$ = this.props.getProperties().getPropertyArray();
            for (CTProperty p : arr$) {
                if (p.getPid() > propid) {
                    propid = p.getPid();
                }
            }
            return propid + 1;
        }

        public boolean contains(String name) {
            CTProperty[] arr$ = this.props.getProperties().getPropertyArray();
            for (CTProperty p : arr$) {
                if (p.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }

        public CTProperty getProperty(String name) {
            CTProperty[] arr$ = this.props.getProperties().getPropertyArray();
            for (CTProperty p : arr$) {
                if (p.getName().equals(name)) {
                    return p;
                }
            }
            return null;
        }
    }
}
