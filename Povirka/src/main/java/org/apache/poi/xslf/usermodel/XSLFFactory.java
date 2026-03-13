package org.apache.poi.xslf.usermodel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLFactory;
import org.apache.poi.POIXMLRelation;

/* JADX INFO: loaded from: classes.dex */
public final class XSLFFactory extends POIXMLFactory {
    private static final XSLFFactory inst = new XSLFFactory();

    private XSLFFactory() {
    }

    public static XSLFFactory getInstance() {
        return inst;
    }

    @Override // org.apache.poi.POIXMLFactory
    protected POIXMLRelation getDescriptor(String relationshipType) {
        return XSLFRelation.getInstance(relationshipType);
    }

    @Override // org.apache.poi.POIXMLFactory
    protected POIXMLDocumentPart createDocumentPart(Class<? extends POIXMLDocumentPart> cls, Class<?>[] classes, Object[] values) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, InvocationTargetException {
        Constructor<? extends POIXMLDocumentPart> constructor = cls.getDeclaredConstructor(classes);
        return constructor.newInstance(values);
    }
}
