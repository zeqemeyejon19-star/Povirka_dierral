package org.apache.poi.openxml4j.opc.internal.marshallers;

import java.io.OutputStream;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Namespace;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.util.Nullable;
import org.apache.poi.util.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class PackagePropertiesMarshaller implements PartMarshaller {
    protected static final String KEYWORD_CATEGORY = "category";
    protected static final String KEYWORD_CONTENT_STATUS = "contentStatus";
    protected static final String KEYWORD_CONTENT_TYPE = "contentType";
    protected static final String KEYWORD_CREATED = "created";
    protected static final String KEYWORD_CREATOR = "creator";
    protected static final String KEYWORD_DESCRIPTION = "description";
    protected static final String KEYWORD_IDENTIFIER = "identifier";
    protected static final String KEYWORD_KEYWORDS = "keywords";
    protected static final String KEYWORD_LANGUAGE = "language";
    protected static final String KEYWORD_LAST_MODIFIED_BY = "lastModifiedBy";
    protected static final String KEYWORD_LAST_PRINTED = "lastPrinted";
    protected static final String KEYWORD_MODIFIED = "modified";
    protected static final String KEYWORD_REVISION = "revision";
    protected static final String KEYWORD_SUBJECT = "subject";
    protected static final String KEYWORD_TITLE = "title";
    protected static final String KEYWORD_VERSION = "version";
    private static final Namespace namespaceCoreProperties;
    private static final Namespace namespaceDC;
    private static final Namespace namespaceDcTerms;
    private static final Namespace namespaceXSI;
    PackagePropertiesPart propsPart;
    Document xmlDoc = null;

    static {
        XMLEventFactory f = XMLEventFactory.newInstance();
        namespaceDC = f.createNamespace("dc", "http://purl.org/dc/elements/1.1/");
        namespaceCoreProperties = f.createNamespace("cp", "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
        namespaceDcTerms = f.createNamespace("dcterms", "http://purl.org/dc/terms/");
        namespaceXSI = f.createNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }

    @Override // org.apache.poi.openxml4j.opc.internal.PartMarshaller
    public boolean marshall(PackagePart part, OutputStream out) throws OpenXML4JException {
        if (!(part instanceof PackagePropertiesPart)) {
            throw new IllegalArgumentException("'part' must be a PackagePropertiesPart instance.");
        }
        this.propsPart = (PackagePropertiesPart) part;
        Document documentCreateDocument = DocumentHelper.createDocument();
        this.xmlDoc = documentCreateDocument;
        Namespace namespace = namespaceCoreProperties;
        Element rootElem = documentCreateDocument.createElementNS(namespace.getNamespaceURI(), getQName("coreProperties", namespace));
        DocumentHelper.addNamespaceDeclaration(rootElem, namespace);
        DocumentHelper.addNamespaceDeclaration(rootElem, namespaceDC);
        DocumentHelper.addNamespaceDeclaration(rootElem, namespaceDcTerms);
        DocumentHelper.addNamespaceDeclaration(rootElem, namespaceXSI);
        this.xmlDoc.appendChild(rootElem);
        addCategory();
        addContentStatus();
        addContentType();
        addCreated();
        addCreator();
        addDescription();
        addIdentifier();
        addKeywords();
        addLanguage();
        addLastModifiedBy();
        addLastPrinted();
        addModified();
        addRevision();
        addSubject();
        addTitle();
        addVersion();
        return true;
    }

    private Element setElementTextContent(String localName, Namespace namespace, Nullable<String> property) {
        return setElementTextContent(localName, namespace, property, property.getValue());
    }

    private String getQName(String localName, Namespace namespace) {
        return namespace.getPrefix().isEmpty() ? localName : namespace.getPrefix() + ':' + localName;
    }

    private Element setElementTextContent(String localName, Namespace namespace, Nullable<?> property, String propertyValue) {
        if (!property.hasValue()) {
            return null;
        }
        Element root = this.xmlDoc.getDocumentElement();
        Element elem = (Element) root.getElementsByTagNameNS(namespace.getNamespaceURI(), localName).item(0);
        if (elem == null) {
            elem = this.xmlDoc.createElementNS(namespace.getNamespaceURI(), getQName(localName, namespace));
            root.appendChild(elem);
        }
        elem.setTextContent(propertyValue);
        return elem;
    }

    private Element setElementTextContent(String localName, Namespace namespace, Nullable<?> property, String propertyValue, String xsiType) {
        Element element = setElementTextContent(localName, namespace, property, propertyValue);
        if (element != null) {
            Namespace namespace2 = namespaceXSI;
            element.setAttributeNS(namespace2.getNamespaceURI(), getQName("type", namespace2), xsiType);
        }
        return element;
    }

    private void addCategory() {
        setElementTextContent(KEYWORD_CATEGORY, namespaceCoreProperties, this.propsPart.getCategoryProperty());
    }

    private void addContentStatus() {
        setElementTextContent(KEYWORD_CONTENT_STATUS, namespaceCoreProperties, this.propsPart.getContentStatusProperty());
    }

    private void addContentType() {
        setElementTextContent(KEYWORD_CONTENT_TYPE, namespaceCoreProperties, this.propsPart.getContentTypeProperty());
    }

    private void addCreated() {
        setElementTextContent(KEYWORD_CREATED, namespaceDcTerms, this.propsPart.getCreatedProperty(), this.propsPart.getCreatedPropertyString(), "dcterms:W3CDTF");
    }

    private void addCreator() {
        setElementTextContent(KEYWORD_CREATOR, namespaceDC, this.propsPart.getCreatorProperty());
    }

    private void addDescription() {
        setElementTextContent(KEYWORD_DESCRIPTION, namespaceDC, this.propsPart.getDescriptionProperty());
    }

    private void addIdentifier() {
        setElementTextContent(KEYWORD_IDENTIFIER, namespaceDC, this.propsPart.getIdentifierProperty());
    }

    private void addKeywords() {
        setElementTextContent(KEYWORD_KEYWORDS, namespaceCoreProperties, this.propsPart.getKeywordsProperty());
    }

    private void addLanguage() {
        setElementTextContent(KEYWORD_LANGUAGE, namespaceDC, this.propsPart.getLanguageProperty());
    }

    private void addLastModifiedBy() {
        setElementTextContent(KEYWORD_LAST_MODIFIED_BY, namespaceCoreProperties, this.propsPart.getLastModifiedByProperty());
    }

    private void addLastPrinted() {
        setElementTextContent(KEYWORD_LAST_PRINTED, namespaceCoreProperties, this.propsPart.getLastPrintedProperty(), this.propsPart.getLastPrintedPropertyString());
    }

    private void addModified() {
        setElementTextContent(KEYWORD_MODIFIED, namespaceDcTerms, this.propsPart.getModifiedProperty(), this.propsPart.getModifiedPropertyString(), "dcterms:W3CDTF");
    }

    private void addRevision() {
        setElementTextContent(KEYWORD_REVISION, namespaceCoreProperties, this.propsPart.getRevisionProperty());
    }

    private void addSubject() {
        setElementTextContent(KEYWORD_SUBJECT, namespaceDC, this.propsPart.getSubjectProperty());
    }

    private void addTitle() {
        setElementTextContent(KEYWORD_TITLE, namespaceDC, this.propsPart.getTitleProperty());
    }

    private void addVersion() {
        setElementTextContent(KEYWORD_VERSION, namespaceCoreProperties, this.propsPart.getVersionProperty());
    }
}
