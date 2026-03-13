package org.apache.poi.xdgf.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.POIXMLException;
import org.apache.xmlbeans.XmlObject;

/* JADX INFO: loaded from: classes.dex */
public class ObjectFactory<T, X extends XmlObject> {
    Map<String, Constructor<? extends T>> _types = new HashMap();

    public void put(String typeName, Class<? extends T> cls, Class<?>... varargs) throws NoSuchMethodException, SecurityException {
        this._types.put(typeName, cls.getDeclaredConstructor(varargs));
    }

    public T load(String name, Object... varargs) {
        Constructor<? extends T> constructor = this._types.get(name);
        if (constructor == null) {
            String typeName = ((XmlObject) varargs[0]).schemaType().getName().getLocalPart();
            throw new POIXMLException("Invalid '" + typeName + "' name '" + name + "'");
        }
        try {
            return constructor.newInstance(varargs);
        } catch (InvocationTargetException e) {
            throw new POIXMLException(e.getCause());
        } catch (Exception e2) {
            throw new POIXMLException(e2);
        }
    }
}
