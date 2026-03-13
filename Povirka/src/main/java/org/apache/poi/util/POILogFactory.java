package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@Internal
public final class POILogFactory {
    private static final Map<String, POILogger> _loggers = new HashMap();
    private static final POILogger _nullLogger = new NullLogger();
    static String _loggerClassName = null;

    private POILogFactory() {
    }

    public static POILogger getLogger(Class<?> theclass) {
        return getLogger(theclass.getName());
    }

    public static POILogger getLogger(String cat) {
        if (_loggerClassName == null) {
            try {
                _loggerClassName = System.getProperty("org.apache.poi.util.POILogger");
            } catch (Exception e) {
            }
            if (_loggerClassName == null) {
                _loggerClassName = _nullLogger.getClass().getName();
            }
        }
        String str = _loggerClassName;
        POILogger pOILogger = _nullLogger;
        if (str.equals(pOILogger.getClass().getName())) {
            return pOILogger;
        }
        POILogger logger = _loggers.get(cat);
        if (logger == null) {
            try {
                logger = (POILogger) Class.forName(_loggerClassName).newInstance();
                logger.initialize(cat);
            } catch (Exception e2) {
                logger = _nullLogger;
                _loggerClassName = _nullLogger.getClass().getName();
            }
            _loggers.put(cat, logger);
        }
        return logger;
    }
}
