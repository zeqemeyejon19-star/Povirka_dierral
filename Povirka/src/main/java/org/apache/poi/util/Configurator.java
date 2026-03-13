package org.apache.poi.util;

/* JADX INFO: loaded from: classes.dex */
public class Configurator {
    private static POILogger logger = POILogFactory.getLogger((Class<?>) Configurator.class);

    public static int getIntValue(String systemProperty, int defaultValue) {
        String property = System.getProperty(systemProperty);
        try {
            int result = Integer.parseInt(property);
            return result;
        } catch (Exception e) {
            logger.log(7, "System property -D" + systemProperty + " do not contains a valid integer " + property);
            return defaultValue;
        }
    }
}
