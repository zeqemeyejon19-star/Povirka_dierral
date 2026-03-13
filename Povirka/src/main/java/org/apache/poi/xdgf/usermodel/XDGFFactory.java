package org.apache.poi.xdgf.usermodel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLFactory;
import org.apache.poi.POIXMLRelation;

/* JADX INFO: loaded from: classes.dex */
public class XDGFFactory extends POIXMLFactory {
    private final XDGFDocument document;

    public XDGFFactory(XDGFDocument document) {
        this.document = document;
    }

    @Override // org.apache.poi.POIXMLFactory
    protected POIXMLRelation getDescriptor(String relationshipType) {
        return XDGFRelation.getInstance(relationshipType);
    }

    @Override // org.apache.poi.POIXMLFactory
    protected POIXMLDocumentPart createDocumentPart(Class<? extends POIXMLDocumentPart> cls, Class<?>[] classes, Object[] values) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, InvocationTargetException {
        Class<?>[] cl;
        Object[] vals;
        if (classes != null) {
            cl = new Class[classes.length + 1];
            System.arraycopy(classes, 0, cl, 0, classes.length);
            cl[classes.length] = XDGFDocument.class;
            vals = new Object[values.length + 1];
            System.arraycopy(values, 0, vals, 0, values.length);
            vals[values.length] = this.document;
        } else {
            cl = new Class[]{XDGFDocument.class};
            vals = new Object[]{this.document};
        }
        Constructor<? extends POIXMLDocumentPart> constructor = cls.getDeclaredConstructor(cl);
        return constructor.newInstance(vals);
    }
}
