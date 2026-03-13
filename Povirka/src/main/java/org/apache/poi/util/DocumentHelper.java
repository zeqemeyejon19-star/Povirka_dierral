package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Namespace;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: classes.dex */
public final class DocumentHelper {
    private static final DocumentBuilderFactory documentBuilderFactory;
    private static final DocumentBuilder documentBuilderSingleton;
    private static POILogger logger = POILogFactory.getLogger((Class<?>) DocumentHelper.class);

    static {
        DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
        documentBuilderFactory = documentBuilderFactoryNewInstance;
        documentBuilderFactoryNewInstance.setNamespaceAware(true);
        documentBuilderFactoryNewInstance.setValidating(false);
        trySetSAXFeature(documentBuilderFactoryNewInstance, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetXercesSecurityManager(documentBuilderFactoryNewInstance);
        documentBuilderSingleton = newDocumentBuilder();
    }

    private DocumentHelper() {
    }

    private static class DocHelperErrorHandler implements ErrorHandler {
        private DocHelperErrorHandler() {
        }

        @Override // org.xml.sax.ErrorHandler
        public void warning(SAXParseException exception) throws SAXException {
            printError(5, exception);
        }

        @Override // org.xml.sax.ErrorHandler
        public void error(SAXParseException exception) throws SAXException {
            printError(7, exception);
        }

        @Override // org.xml.sax.ErrorHandler
        public void fatalError(SAXParseException exception) throws SAXException {
            printError(9, exception);
            throw exception;
        }

        private void printError(int type, SAXParseException ex) {
            StringBuilder sb = new StringBuilder();
            String systemId = ex.getSystemId();
            if (systemId != null) {
                int index = systemId.lastIndexOf(47);
                if (index != -1) {
                    systemId = systemId.substring(index + 1);
                }
                sb.append(systemId);
            }
            sb.append(':');
            sb.append(ex.getLineNumber());
            sb.append(':');
            sb.append(ex.getColumnNumber());
            sb.append(": ");
            sb.append(ex.getMessage());
            DocumentHelper.logger.log(type, sb.toString(), ex);
        }
    }

    public static synchronized DocumentBuilder newDocumentBuilder() {
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setEntityResolver(SAXHelper.IGNORING_ENTITY_RESOLVER);
            documentBuilder.setErrorHandler(new DocHelperErrorHandler());
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("cannot create a DocumentBuilder", e);
        }
        return documentBuilder;
    }

    private static void trySetSAXFeature(DocumentBuilderFactory dbf, String feature, boolean enabled) {
        try {
            dbf.setFeature(feature, enabled);
        } catch (AbstractMethodError ame) {
            logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        } catch (Exception e) {
            logger.log(5, "SAX Feature unsupported", feature, e);
        }
    }

    private static void trySetXercesSecurityManager(DocumentBuilderFactory dbf) {
        String[] arr$ = {"com.sun.org.apache.xerces.internal.util.SecurityManager", "org.apache.xerces.util.SecurityManager"};
        for (String securityManagerClassName : arr$) {
            try {
                Object mgr = Class.forName(securityManagerClassName).newInstance();
                Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, 4096);
                dbf.setAttribute("http://apache.org/xml/properties/security-manager", mgr);
                return;
            } catch (Throwable e) {
                logger.log(5, "SAX Security Manager could not be setup", e);
            }
        }
    }

    public static Document readDocument(InputStream inp) throws SAXException, IOException {
        return newDocumentBuilder().parse(inp);
    }

    public static Document readDocument(InputSource inp) throws SAXException, IOException {
        return newDocumentBuilder().parse(inp);
    }

    public static synchronized Document createDocument() {
        return documentBuilderSingleton.newDocument();
    }

    public static void addNamespaceDeclaration(Element element, String namespacePrefix, String namespaceURI) {
        element.setAttributeNS(SignatureFacet.XML_NS, "xmlns:" + namespacePrefix, namespaceURI);
    }

    public static void addNamespaceDeclaration(Element element, Namespace namespace) {
        addNamespaceDeclaration(element, namespace.getPrefix(), namespace.getNamespaceURI());
    }
}
