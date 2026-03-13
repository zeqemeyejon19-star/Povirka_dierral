package org.apache.poi.util;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes.dex */
public final class SAXHelper {
    private static long lastLog;
    private static final SAXParserFactory saxFactory;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) SAXHelper.class);
    static final EntityResolver IGNORING_ENTITY_RESOLVER = new EntityResolver() { // from class: org.apache.poi.util.SAXHelper.1
        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    };

    static {
        try {
            SAXParserFactory sAXParserFactoryNewInstance = SAXParserFactory.newInstance();
            saxFactory = sAXParserFactoryNewInstance;
            sAXParserFactoryNewInstance.setValidating(false);
            sAXParserFactoryNewInstance.setNamespaceAware(true);
        } catch (Error e) {
            logger.log(5, "Failed to create SAXParserFactory", e);
            throw e;
        } catch (RuntimeException re) {
            logger.log(5, "Failed to create SAXParserFactory", re);
            throw re;
        } catch (Exception e2) {
            logger.log(5, "Failed to create SAXParserFactory", e2);
            throw new RuntimeException("Failed to create SAXParserFactory", e2);
        }
    }

    private SAXHelper() {
    }

    public static synchronized XMLReader newXMLReader() throws ParserConfigurationException, SAXException {
        XMLReader xmlReader;
        xmlReader = saxFactory.newSAXParser().getXMLReader();
        xmlReader.setEntityResolver(IGNORING_ENTITY_RESOLVER);
        trySetSAXFeature(xmlReader, "http://javax.xml.XMLConstants/feature/secure-processing");
        trySetXercesSecurityManager(xmlReader);
        return xmlReader;
    }

    private static void trySetSAXFeature(XMLReader xmlReader, String feature) {
        try {
            xmlReader.setFeature(feature, true);
        } catch (AbstractMethodError ame) {
            logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        } catch (Exception e) {
            logger.log(5, "SAX Feature unsupported", feature, e);
        }
    }

    private static void trySetXercesSecurityManager(XMLReader xmlReader) {
        String[] arr$ = {"com.sun.org.apache.xerces.internal.util.SecurityManager", "org.apache.xerces.util.SecurityManager"};
        for (String securityManagerClassName : arr$) {
            try {
                Object mgr = Class.forName(securityManagerClassName).newInstance();
                Method setLimit = mgr.getClass().getMethod("setEntityExpansionLimit", Integer.TYPE);
                setLimit.invoke(mgr, 4096);
                xmlReader.setProperty("http://apache.org/xml/properties/security-manager", mgr);
                return;
            } catch (Throwable e) {
                if (System.currentTimeMillis() > lastLog + TimeUnit.MINUTES.toMillis(5L)) {
                    logger.log(5, "SAX Security Manager could not be setup [log suppressed for 5 minutes]", e);
                    lastLog = System.currentTimeMillis();
                }
            }
        }
    }
}
