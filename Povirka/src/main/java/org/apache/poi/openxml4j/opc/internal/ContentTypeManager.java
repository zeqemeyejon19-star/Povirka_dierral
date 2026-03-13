package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public abstract class ContentTypeManager {
    public static final String CONTENT_TYPES_PART_NAME = "[Content_Types].xml";
    private static final String CONTENT_TYPE_ATTRIBUTE_NAME = "ContentType";
    private static final String DEFAULT_TAG_NAME = "Default";
    private static final String EXTENSION_ATTRIBUTE_NAME = "Extension";
    private static final String OVERRIDE_TAG_NAME = "Override";
    private static final String PART_NAME_ATTRIBUTE_NAME = "PartName";
    public static final String TYPES_NAMESPACE_URI = "http://schemas.openxmlformats.org/package/2006/content-types";
    private static final String TYPES_TAG_NAME = "Types";
    protected OPCPackage container;
    private TreeMap<String, String> defaultContentType = new TreeMap<>();
    private TreeMap<PackagePartName, String> overrideContentType;

    public abstract boolean saveImpl(Document document, OutputStream outputStream);

    public ContentTypeManager(InputStream in, OPCPackage pkg) throws InvalidFormatException {
        this.container = pkg;
        if (in != null) {
            try {
                parseContentTypesFile(in);
            } catch (InvalidFormatException e) {
                InvalidFormatException ex = new InvalidFormatException("Can't read content types part !");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    public void addContentType(PackagePartName partName, String contentType) {
        boolean defaultCTExists = this.defaultContentType.containsValue(contentType);
        String extension = partName.getExtension().toLowerCase(Locale.ROOT);
        if (extension.length() == 0 || (this.defaultContentType.containsKey(extension) && !defaultCTExists)) {
            addOverrideContentType(partName, contentType);
        } else if (!defaultCTExists) {
            addDefaultContentType(extension, contentType);
        }
    }

    private void addOverrideContentType(PackagePartName partName, String contentType) {
        if (this.overrideContentType == null) {
            this.overrideContentType = new TreeMap<>();
        }
        this.overrideContentType.put(partName, contentType);
    }

    private void addDefaultContentType(String extension, String contentType) {
        this.defaultContentType.put(extension.toLowerCase(Locale.ROOT), contentType);
    }

    public void removeContentType(PackagePartName partName) throws InvalidOperationException {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        TreeMap<PackagePartName, String> treeMap = this.overrideContentType;
        if (treeMap != null && treeMap.get(partName) != null) {
            this.overrideContentType.remove(partName);
            return;
        }
        String extensionToDelete = partName.getExtension();
        boolean deleteDefaultContentTypeFlag = true;
        OPCPackage oPCPackage = this.container;
        if (oPCPackage != null) {
            try {
                Iterator<PackagePart> it = oPCPackage.getParts().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    PackagePart part = it.next();
                    if (!part.getPartName().equals(partName) && part.getPartName().getExtension().equalsIgnoreCase(extensionToDelete)) {
                        deleteDefaultContentTypeFlag = false;
                        break;
                    }
                }
            } catch (InvalidFormatException e) {
                throw new InvalidOperationException(e.getMessage());
            }
        }
        if (deleteDefaultContentTypeFlag) {
            this.defaultContentType.remove(extensionToDelete);
        }
        OPCPackage oPCPackage2 = this.container;
        if (oPCPackage2 != null) {
            try {
                for (PackagePart part2 : oPCPackage2.getParts()) {
                    if (!part2.getPartName().equals(partName) && getContentType(part2.getPartName()) == null) {
                        throw new InvalidOperationException("Rule M2.4 is not respected: Nor a default element or override element is associated with the part: " + part2.getPartName().getName());
                    }
                }
            } catch (InvalidFormatException e2) {
                throw new InvalidOperationException(e2.getMessage());
            }
        }
    }

    public boolean isContentTypeRegister(String contentType) {
        TreeMap<PackagePartName, String> treeMap;
        if (contentType != null) {
            return this.defaultContentType.values().contains(contentType) || ((treeMap = this.overrideContentType) != null && treeMap.values().contains(contentType));
        }
        throw new IllegalArgumentException("contentType");
    }

    public String getContentType(PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        TreeMap<PackagePartName, String> treeMap = this.overrideContentType;
        if (treeMap != null && treeMap.containsKey(partName)) {
            return this.overrideContentType.get(partName);
        }
        String extension = partName.getExtension().toLowerCase(Locale.ROOT);
        if (this.defaultContentType.containsKey(extension)) {
            return this.defaultContentType.get(extension);
        }
        OPCPackage oPCPackage = this.container;
        if (oPCPackage != null && oPCPackage.getPart(partName) != null) {
            throw new OpenXML4JRuntimeException("Rule M2.4 exception : this error should NEVER happen! If you can provide the triggering file, then please raise a bug at https://bz.apache.org/bugzilla/enter_bug.cgi?product=POI and attach the file that triggers it, thanks!");
        }
        return null;
    }

    public void clearAll() {
        this.defaultContentType.clear();
        TreeMap<PackagePartName, String> treeMap = this.overrideContentType;
        if (treeMap != null) {
            treeMap.clear();
        }
    }

    public void clearOverrideContentTypes() {
        TreeMap<PackagePartName, String> treeMap = this.overrideContentType;
        if (treeMap != null) {
            treeMap.clear();
        }
    }

    private void parseContentTypesFile(InputStream in) throws InvalidFormatException {
        try {
            Document xmlContentTypetDoc = DocumentHelper.readDocument(in);
            NodeList defaultTypes = xmlContentTypetDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/content-types", DEFAULT_TAG_NAME);
            int defaultTypeCount = defaultTypes.getLength();
            for (int i = 0; i < defaultTypeCount; i++) {
                Element element = (Element) defaultTypes.item(i);
                String extension = element.getAttribute(EXTENSION_ATTRIBUTE_NAME);
                String contentType = element.getAttribute(CONTENT_TYPE_ATTRIBUTE_NAME);
                addDefaultContentType(extension, contentType);
            }
            NodeList overrideTypes = xmlContentTypetDoc.getDocumentElement().getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/content-types", OVERRIDE_TAG_NAME);
            int overrideTypeCount = overrideTypes.getLength();
            for (int i2 = 0; i2 < overrideTypeCount; i2++) {
                Element element2 = (Element) overrideTypes.item(i2);
                URI uri = new URI(element2.getAttribute(PART_NAME_ATTRIBUTE_NAME));
                PackagePartName partName = PackagingURIHelper.createPartName(uri);
                String contentType2 = element2.getAttribute(CONTENT_TYPE_ATTRIBUTE_NAME);
                addOverrideContentType(partName, contentType2);
            }
        } catch (IOException e) {
            throw new InvalidFormatException(e.getMessage());
        } catch (URISyntaxException urie) {
            throw new InvalidFormatException(urie.getMessage());
        } catch (SAXException e2) {
            throw new InvalidFormatException(e2.getMessage());
        }
    }

    public boolean save(OutputStream outStream) {
        Document xmlOutDoc = DocumentHelper.createDocument();
        Element typesElem = xmlOutDoc.createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", TYPES_TAG_NAME);
        xmlOutDoc.appendChild(typesElem);
        for (Map.Entry<String, String> entry : this.defaultContentType.entrySet()) {
            appendDefaultType(typesElem, entry);
        }
        TreeMap<PackagePartName, String> treeMap = this.overrideContentType;
        if (treeMap != null) {
            for (Map.Entry<PackagePartName, String> entry2 : treeMap.entrySet()) {
                appendSpecificTypes(typesElem, entry2);
            }
        }
        xmlOutDoc.normalize();
        return saveImpl(xmlOutDoc, outStream);
    }

    private void appendSpecificTypes(Element root, Map.Entry<PackagePartName, String> entry) {
        Element specificType = root.getOwnerDocument().createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", OVERRIDE_TAG_NAME);
        specificType.setAttribute(PART_NAME_ATTRIBUTE_NAME, entry.getKey().getName());
        specificType.setAttribute(CONTENT_TYPE_ATTRIBUTE_NAME, entry.getValue());
        root.appendChild(specificType);
    }

    private void appendDefaultType(Element root, Map.Entry<String, String> entry) {
        Element defaultType = root.getOwnerDocument().createElementNS("http://schemas.openxmlformats.org/package/2006/content-types", DEFAULT_TAG_NAME);
        defaultType.setAttribute(EXTENSION_ATTRIBUTE_NAME, entry.getKey());
        defaultType.setAttribute(CONTENT_TYPE_ATTRIBUTE_NAME, entry.getValue());
        root.appendChild(defaultType);
    }
}
