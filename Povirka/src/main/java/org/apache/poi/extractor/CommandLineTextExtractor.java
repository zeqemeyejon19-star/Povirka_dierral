package org.apache.poi.extractor;

import java.io.File;
import org.apache.poi.POITextExtractor;

/* JADX INFO: loaded from: classes.dex */
public class CommandLineTextExtractor {
    public static final String DIVIDER = "=======================";

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.apache.xmlbeans.XmlException */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   CommandLineTextExtractor <filename> [filename] [filename]");
            System.exit(1);
        }
        for (String arg : args) {
            System.out.println(DIVIDER);
            File f = new File(arg);
            System.out.println(f);
            POITextExtractor extractor = ExtractorFactory.createExtractor(f);
            try {
                POITextExtractor metadataExtractor = extractor.getMetadataTextExtractor();
                System.out.println("   =======================");
                String metaData = metadataExtractor.getText();
                System.out.println(metaData);
                System.out.println("   =======================");
                String text = extractor.getText();
                System.out.println(text);
                System.out.println(DIVIDER);
                System.out.println("Had " + metaData.length() + " characters of metadata and " + text.length() + " characters of text");
                extractor.close();
            } catch (Throwable th) {
                extractor.close();
                throw th;
            }
        }
    }
}
