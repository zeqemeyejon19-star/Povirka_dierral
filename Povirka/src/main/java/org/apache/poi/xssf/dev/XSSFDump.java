package org.apache.poi.xssf.dev;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.IOUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFDump {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            System.out.println("Dumping " + args[i]);
            ZipFile zip = ZipHelper.openZipFile(args[i]);
            try {
                dump(zip);
                zip.close();
            } catch (Throwable th) {
                zip.close();
                throw th;
            }
        }
    }

    private static void createDirIfMissing(File directory) throws RuntimeException {
        if (!directory.exists()) {
            boolean dirWasCreated = directory.mkdir();
            if (!dirWasCreated) {
                throw new RuntimeException("Unable to create directory: " + directory);
            }
        }
    }

    private static void recursivelyCreateDirIfMissing(File directory) throws RuntimeException {
        if (!directory.exists()) {
            boolean dirsWereCreated = directory.mkdirs();
            if (!dirsWereCreated) {
                throw new RuntimeException("Unable to recursively create directory: " + directory);
            }
        }
    }

    public static void dump(ZipFile zip) throws Exception {
        String zipname = zip.getName();
        int sep = zipname.lastIndexOf(46);
        File root = new File(zipname.substring(0, sep));
        createDirIfMissing(root);
        System.out.println("Dumping to directory " + root);
        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry entry = en.nextElement();
            String name = entry.getName();
            int idx = name.lastIndexOf(47);
            if (idx != -1) {
                File bs = new File(root, name.substring(0, idx));
                recursivelyCreateDirIfMissing(bs);
            }
            File f = new File(root, entry.getName());
            OutputStream out = new FileOutputStream(f);
            try {
                if (entry.getName().endsWith(".xml") || entry.getName().endsWith(".vml") || entry.getName().endsWith(".rels")) {
                    try {
                        Document doc = DocumentHelper.readDocument(zip.getInputStream(entry));
                        XmlObject xml = XmlObject.Factory.parse(doc, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                        XmlOptions options = new XmlOptions();
                        options.setSavePrettyPrint();
                        xml.save(out, options);
                    } catch (XmlException e) {
                        System.err.println("Failed to parse " + entry.getName() + ", dumping raw content");
                        IOUtils.copy(zip.getInputStream(entry), out);
                    }
                } else {
                    IOUtils.copy(zip.getInputStream(entry), out);
                }
            } finally {
                out.close();
            }
        }
    }
}
