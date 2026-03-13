package org.apache.poi.openxml4j.opc.internal.unmarshallers;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageNamespaces;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.internal.PartUnmarshaller;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.util.DocumentHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public final class PackagePropertiesUnmarshaller implements PartUnmarshaller {
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

    @Override // org.apache.poi.openxml4j.opc.internal.PartUnmarshaller
    public PackagePart unmarshall(UnmarshallContext context, InputStream in) throws InvalidFormatException, IOException {
        Document xmlDoc;
        PackagePropertiesPart coreProps = new PackagePropertiesPart(context.getPackage(), context.getPartName());
        if (in == null) {
            if (context.getZipEntry() != null) {
                in = ((ZipPackage) context.getPackage()).getZipArchive().getInputStream(context.getZipEntry());
            } else if (context.getPackage() != null) {
                ZipEntry zipEntry = ZipHelper.getCorePropertiesZipEntry((ZipPackage) context.getPackage());
                in = ((ZipPackage) context.getPackage()).getZipArchive().getInputStream(zipEntry);
            } else {
                throw new IOException("Error while trying to get the part input stream.");
            }
        }
        try {
            xmlDoc = DocumentHelper.readDocument(in);
        } catch (SAXException e) {
            e = e;
        }
        try {
            checkElementForOPCCompliance(xmlDoc.getDocumentElement());
            coreProps.setCategoryProperty(loadCategory(xmlDoc));
            coreProps.setContentStatusProperty(loadContentStatus(xmlDoc));
            coreProps.setContentTypeProperty(loadContentType(xmlDoc));
            coreProps.setCreatedProperty(loadCreated(xmlDoc));
            coreProps.setCreatorProperty(loadCreator(xmlDoc));
            coreProps.setDescriptionProperty(loadDescription(xmlDoc));
            coreProps.setIdentifierProperty(loadIdentifier(xmlDoc));
            coreProps.setKeywordsProperty(loadKeywords(xmlDoc));
            coreProps.setLanguageProperty(loadLanguage(xmlDoc));
            coreProps.setLastModifiedByProperty(loadLastModifiedBy(xmlDoc));
            coreProps.setLastPrintedProperty(loadLastPrinted(xmlDoc));
            coreProps.setModifiedProperty(loadModified(xmlDoc));
            coreProps.setRevisionProperty(loadRevision(xmlDoc));
            coreProps.setSubjectProperty(loadSubject(xmlDoc));
            coreProps.setTitleProperty(loadTitle(xmlDoc));
            coreProps.setVersionProperty(loadVersion(xmlDoc));
            return coreProps;
        } catch (SAXException e2) {
            e = e2;
            throw new IOException(e.getMessage());
        }
    }

    private String readElement(Document xmlDoc, String localName, String namespaceURI) {
        Element el = (Element) xmlDoc.getDocumentElement().getElementsByTagNameNS(namespaceURI, localName).item(0);
        if (el == null) {
            return null;
        }
        return el.getTextContent();
    }

    private String loadCategory(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_CATEGORY, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadContentStatus(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_CONTENT_STATUS, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadContentType(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_CONTENT_TYPE, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadCreated(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_CREATED, "http://purl.org/dc/terms/");
    }

    private String loadCreator(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_CREATOR, "http://purl.org/dc/elements/1.1/");
    }

    private String loadDescription(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_DESCRIPTION, "http://purl.org/dc/elements/1.1/");
    }

    private String loadIdentifier(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_IDENTIFIER, "http://purl.org/dc/elements/1.1/");
    }

    private String loadKeywords(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_KEYWORDS, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadLanguage(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_LANGUAGE, "http://purl.org/dc/elements/1.1/");
    }

    private String loadLastModifiedBy(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_LAST_MODIFIED_BY, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadLastPrinted(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_LAST_PRINTED, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadModified(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_MODIFIED, "http://purl.org/dc/terms/");
    }

    private String loadRevision(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_REVISION, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    private String loadSubject(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_SUBJECT, "http://purl.org/dc/elements/1.1/");
    }

    private String loadTitle(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_TITLE, "http://purl.org/dc/elements/1.1/");
    }

    private String loadVersion(Document xmlDoc) {
        return readElement(xmlDoc, KEYWORD_VERSION, "http://schemas.openxmlformats.org/package/2006/metadata/core-properties");
    }

    public void checkElementForOPCCompliance(Element el) throws InvalidFormatException {
        NamedNodeMap namedNodeMap = el.getAttributes();
        int namedNodeCount = namedNodeMap.getLength();
        for (int i = 0; i < namedNodeCount; i++) {
            Attr attr = (Attr) namedNodeMap.item(0);
            if (attr.getNamespaceURI().equals(SignatureFacet.XML_NS) && attr.getValue().equals(PackageNamespaces.MARKUP_COMPATIBILITY)) {
                throw new InvalidFormatException("OPC Compliance error [M4.2]: A format consumer shall consider the use of the Markup Compatibility namespace to be an error.");
            }
        }
        String elName = el.getLocalName();
        if (el.getNamespaceURI().equals("http://purl.org/dc/terms/") && !elName.equals(KEYWORD_CREATED) && !elName.equals(KEYWORD_MODIFIED)) {
            throw new InvalidFormatException("OPC Compliance error [M4.3]: Producers shall not create a document element that contains refinements to the Dublin Core elements, except for the two specified in the schema: <dcterms:created> and <dcterms:modified> Consumers shall consider a document element that violates this constraint to be an error.");
        }
        if (el.getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "lang") != null) {
            throw new InvalidFormatException("OPC Compliance error [M4.4]: Producers shall not create a document element that contains the xml:lang attribute. Consumers shall consider a document element that violates this constraint to be an error.");
        }
        if (el.getNamespaceURI().equals("http://purl.org/dc/terms/")) {
            if (!elName.equals(KEYWORD_CREATED) && !elName.equals(KEYWORD_MODIFIED)) {
                throw new InvalidFormatException("Namespace error : " + elName + " shouldn't have the following naemspace -> http://purl.org/dc/terms/");
            }
            Attr typeAtt = el.getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
            if (typeAtt == null) {
                throw new InvalidFormatException("The element '" + elName + "' must have the 'xsi:type' attribute present !");
            }
            if (!typeAtt.getValue().equals(el.getPrefix() + ":W3CDTF")) {
                throw new InvalidFormatException("The element '" + elName + "' must have the 'xsi:type' attribute with the value '" + el.getPrefix() + ":W3CDTF', but had '" + typeAtt.getValue() + "' !");
            }
        }
        NodeList childElements = el.getElementsByTagName("*");
        int childElementCount = childElements.getLength();
        for (int i2 = 0; i2 < childElementCount; i2++) {
            checkElementForOPCCompliance((Element) childElements.item(i2));
        }
    }
}
