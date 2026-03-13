package org.apache.poi.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/* JADX INFO: loaded from: classes.dex */
public class FontMetricsDumper {
    public static void main(String[] args) throws IOException {
        char c;
        Properties props = new Properties();
        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font allFont : allFonts) {
            String fontName = allFont.getFontName();
            Font font = new Font(fontName, 1, 10);
            FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
            int fontHeight = fontMetrics.getHeight();
            props.setProperty("font." + fontName + ".height", fontHeight + "");
            StringBuilder characters = new StringBuilder();
            char c2 = 'a';
            while (true) {
                if (c2 > 'z') {
                    break;
                }
                characters.append(c2).append(", ");
                c2 = (char) (c2 + 1);
            }
            for (char c3 = 'A'; c3 <= 'Z'; c3 = (char) (c3 + 1)) {
                characters.append(c3).append(", ");
            }
            for (char c4 = '0'; c4 <= '9'; c4 = (char) (c4 + 1)) {
                characters.append(c4).append(", ");
            }
            StringBuilder widths = new StringBuilder();
            char c5 = 'a';
            for (c = 'z'; c5 <= c; c = 'z') {
                widths.append(fontMetrics.getWidths()[c5]).append(", ");
                c5 = (char) (c5 + 1);
            }
            for (char c6 = 'A'; c6 <= 'Z'; c6 = (char) (c6 + 1)) {
                widths.append(fontMetrics.getWidths()[c6]).append(", ");
            }
            for (char c7 = '0'; c7 <= '9'; c7 = (char) (c7 + 1)) {
                widths.append(fontMetrics.getWidths()[c7]).append(", ");
            }
            props.setProperty("font." + fontName + ".characters", characters.toString());
            props.setProperty("font." + fontName + ".widths", widths.toString());
        }
        OutputStream fileOut = new FileOutputStream("font_metrics.properties");
        try {
            props.store(fileOut, "Font Metrics");
        } finally {
            fileOut.close();
        }
    }
}
