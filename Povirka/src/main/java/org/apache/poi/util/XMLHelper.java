package org.apache.poi.util;

import javax.xml.parsers.DocumentBuilderFactory;

/* JADX INFO: loaded from: classes.dex */
public final class XMLHelper {
    private static POILogger logger = POILogFactory.getLogger((Class<?>) XMLHelper.class);

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        return factory;
    }

    private static void trySetSAXFeature(DocumentBuilderFactory documentBuilderFactory, String feature, boolean enabled) {
        try {
            documentBuilderFactory.setFeature(feature, enabled);
        } catch (AbstractMethodError ame) {
            logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        } catch (Exception e) {
            logger.log(5, "SAX Feature unsupported", feature, e);
        }
    }
}
