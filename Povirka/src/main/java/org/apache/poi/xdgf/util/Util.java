package org.apache.poi.xdgf.util;

/* JADX INFO: loaded from: classes.dex */
public class Util {
    public static int countLines(String str) {
        int lines = 1;
        int pos = 0;
        while (true) {
            int iIndexOf = str.indexOf("\n", pos) + 1;
            pos = iIndexOf;
            if (iIndexOf != 0) {
                lines++;
            } else {
                return lines;
            }
        }
    }

    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*\"?|<>]", "_");
    }
}
