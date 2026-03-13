package org.apache.poi.util;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/* JADX INFO: loaded from: classes.dex */
public final class StaxHelper {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) StaxHelper.class);

    private StaxHelper() {
    }

    public static XMLInputFactory newXMLInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        trySetProperty(factory, "javax.xml.stream.isNamespaceAware", true);
        trySetProperty(factory, "javax.xml.stream.isValidating", false);
        trySetProperty(factory, "javax.xml.stream.supportDTD", false);
        trySetProperty(factory, "javax.xml.stream.isSupportingExternalEntities", false);
        return factory;
    }

    public static XMLOutputFactory newXMLOutputFactory() {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        trySetProperty(factory, "javax.xml.stream.isRepairingNamespaces", true);
        return factory;
    }

    public static XMLEventFactory newXMLEventFactory() {
        return XMLEventFactory.newFactory();
    }

    private static void trySetProperty(XMLInputFactory factory, String feature, boolean flag) {
        try {
            factory.setProperty(feature, Boolean.valueOf(flag));
        } catch (AbstractMethodError ame) {
            logger.log(5, "Cannot set StAX property because outdated StAX parser in classpath", feature, ame);
        } catch (Exception e) {
            logger.log(5, "StAX Property unsupported", feature, e);
        }
    }

    private static void trySetProperty(XMLOutputFactory factory, String feature, boolean flag) {
        try {
            factory.setProperty(feature, Boolean.valueOf(flag));
        } catch (AbstractMethodError ame) {
            logger.log(5, "Cannot set StAX property because outdated StAX parser in classpath", feature, ame);
        } catch (Exception e) {
            logger.log(5, "StAX Property unsupported", feature, e);
        }
    }
}
