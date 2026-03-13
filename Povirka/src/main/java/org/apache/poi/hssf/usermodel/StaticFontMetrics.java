package org.apache.poi.hssf.usermodel;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
final class StaticFontMetrics {
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) StaticFontMetrics.class);
    private static final Map<String, FontDetails> fontDetailsMap = new HashMap();
    private static Properties fontMetricsProps;

    private StaticFontMetrics() {
    }

    public static synchronized FontDetails getFontDetails(Font font) {
        FontDetails fontDetails;
        if (fontMetricsProps == null) {
            try {
                fontMetricsProps = loadMetrics();
            } catch (IOException e) {
                throw new RuntimeException("Could not load font metrics", e);
            }
        }
        String fontName = font.getName();
        String fontStyle = font.isPlain() ? "plain" : "";
        if (font.isBold()) {
            fontStyle = fontStyle + "bold";
        }
        if (font.isItalic()) {
            fontStyle = fontStyle + "italic";
        }
        String fontHeight = FontDetails.buildFontHeightProperty(fontName);
        String styleHeight = FontDetails.buildFontHeightProperty(fontName + "." + fontStyle);
        if (fontMetricsProps.get(fontHeight) == null && fontMetricsProps.get(styleHeight) != null) {
            fontName = fontName + "." + fontStyle;
        }
        Map<String, FontDetails> map = fontDetailsMap;
        fontDetails = map.get(fontName);
        if (fontDetails == null) {
            fontDetails = FontDetails.create(fontName, fontMetricsProps);
            map.put(fontName, fontDetails);
        }
        return fontDetails;
    }

    private static Properties loadMetrics() throws IOException {
        InputStream metricsIn;
        File propFile = null;
        try {
            String propFileName = System.getProperty("font.metrics.filename");
            if (propFileName != null) {
                propFile = new File(propFileName);
                if (!propFile.exists()) {
                    LOGGER.log(5, "font_metrics.properties not found at path " + propFile.getAbsolutePath());
                    propFile = null;
                }
            }
        } catch (SecurityException e) {
            LOGGER.log(5, "Can't access font.metrics.filename system property", e);
        }
        InputStream metricsIn2 = null;
        try {
            if (propFile != null) {
                metricsIn = new FileInputStream(propFile);
            } else {
                metricsIn = FontDetails.class.getResourceAsStream("/font_metrics.properties");
                if (metricsIn == null) {
                    throw new IOException("font_metrics.properties not found in classpath");
                }
            }
            Properties props = new Properties();
            props.load(metricsIn2);
            return props;
        } finally {
            if (metricsIn2 != null) {
                metricsIn2.close();
            }
        }
    }
}
