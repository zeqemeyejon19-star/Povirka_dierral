package org.apache.poi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/* JADX INFO: loaded from: classes.dex */
public final class OOXMLLite {
    private File _destDest;
    private File _ooxmlJar;
    private File _testDir;

    OOXMLLite(String dest, String test, String ooxmlJar) {
        this._destDest = new File(dest);
        this._testDir = new File(test);
        this._ooxmlJar = new File(ooxmlJar);
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String dest = null;
        String test = null;
        String ooxml = null;
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-dest")) {
                i++;
                dest = args[i];
            } else if (args[i].equals("-test")) {
                i++;
                test = args[i];
            } else if (args[i].equals("-ooxml")) {
                i++;
                ooxml = args[i];
            }
            i++;
        }
        OOXMLLite builder = new OOXMLLite(dest, test, ooxml);
        builder.build();
    }

    void build() throws ClassNotFoundException, IOException {
        List<Class<?>> lst = new ArrayList<>();
        String exclude = StringUtil.join("|", "BaseTestXWorkbook", "BaseTestXSheet", "BaseTestXRow", "BaseTestXCell", "BaseTestXSSFPivotTable", "TestSXSSFWorkbook\\$\\d", "TestUnfixedBugs", "MemoryUsage", "TestDataProvider", "TestDataSamples", "All.+Tests", "ZipFileAssert", "AesZipFileZipEntrySource", "TempFileRecordingSXSSFWorkbookWithCustomZipEntrySource", "PkiTestUtils", "TestCellFormatPart\\$\\d", "TestSignatureInfo\\$\\d", "TestCertificateEncryption\\$CertData", "TestPOIXMLDocument\\$OPCParser", "TestPOIXMLDocument\\$TestFactory", "TestXSLFTextParagraph\\$DrawTextParagraphProxy", "TestXSSFExportToXML\\$\\d", "TestXSSFExportToXML\\$DummyEntityResolver", "TestFormulaEvaluatorOnXSSF\\$Result", "TestFormulaEvaluatorOnXSSF\\$SS", "TestMultiSheetFormulaEvaluatorOnXSSF\\$Result", "TestMultiSheetFormulaEvaluatorOnXSSF\\$SS", "TestXSSFBugs\\$\\d", "AddImageBench", "AddImageBench_jmhType_B\\d", "AddImageBench_benchCreatePicture_jmhTest", "TestEvilUnclosedBRFixingInputStream\\$EvilUnclosedBRFixingInputStream", "TempFileRecordingSXSSFWorkbookWithCustomZipEntrySource\\$TempFileRecordingSheetDataWriterWithDecorator", "TestXSSFBReader\\$1", "TestXSSFBReader\\$TestSheetHandler", "TestFormulaEvaluatorOnXSSF\\$1", "TestMultiSheetFormulaEvaluatorOnXSSF\\$1", "TestZipPackagePropertiesMarshaller\\$1", "SLCommonUtils", "TestPPTX2PNG\\$1");
        System.out.println("Collecting unit tests from " + this._testDir);
        File file = this._testDir;
        collectTests(file, file, lst, ".+.class$", ".+(" + exclude + ").class");
        System.out.println("Found " + lst.size() + " classes");
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new TextListener(System.out));
        Result result = jUnitCore.run((Class[]) lst.toArray(new Class[lst.size()]));
        if (!result.wasSuccessful()) {
            throw new RuntimeException("Tests did not succeed, cannot build ooxml-lite jar");
        }
        System.out.println("Copying classes to " + this._destDest);
        Map<String, Class<?>> classes = getLoadedClasses(this._ooxmlJar.getName());
        Iterator<Class<?>> it = classes.values().iterator();
        while (it.hasNext()) {
            Class<?> cls = it.next();
            String className = cls.getName();
            String classRef = className.replace('.', '/') + ".class";
            File destFile = new File(this._destDest, classRef);
            copyFile(cls.getResourceAsStream('/' + classRef), destFile);
            if (cls.isInterface()) {
                Class<?>[] declaredClasses = cls.getDeclaredClasses();
                int len$ = declaredClasses.length;
                int i$ = 0;
                while (i$ < len$) {
                    Class<?> fc = declaredClasses[i$];
                    String className2 = fc.getName();
                    Iterator<Class<?>> it2 = it;
                    String classRef2 = className2.replace('.', '/') + ".class";
                    File destFile2 = new File(this._destDest, classRef2);
                    copyFile(fc.getResourceAsStream('/' + classRef2), destFile2);
                    i$++;
                    exclude = exclude;
                    it = it2;
                    lst = lst;
                    jUnitCore = jUnitCore;
                }
            }
            exclude = exclude;
            it = it;
            lst = lst;
            jUnitCore = jUnitCore;
        }
        System.out.println("Copying .xsb resources");
        JarFile jar = new JarFile(this._ooxmlJar);
        Pattern p = Pattern.compile("schemaorg_apache_xmlbeans/(system|element)/.*\\.xsb");
        try {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if (p.matcher(je.getName()).matches()) {
                    File destFile3 = new File(this._destDest, je.getName());
                    copyFile(jar.getInputStream(je), destFile3);
                }
            }
        } finally {
            jar.close();
        }
    }

    private static boolean checkForTestAnnotation(Class<?> testclass) {
        Method[] arr$ = testclass.getDeclaredMethods();
        for (Method m : arr$) {
            if (m.isAnnotationPresent(Test.class)) {
                return true;
            }
        }
        if (testclass.getSuperclass() != null) {
            Method[] arr$2 = testclass.getSuperclass().getDeclaredMethods();
            for (Method m2 : arr$2) {
                if (m2.isAnnotationPresent(Test.class)) {
                    return true;
                }
            }
        }
        System.out.println("Class " + testclass.getName() + " does not derive from TestCase and does not have a @Test annotation");
        return false;
    }

    private static void collectTests(File root, File arg, List<Class<?>> out, String ptrn, String exclude) throws ClassNotFoundException {
        if (arg.isDirectory()) {
            File[] files = arg.listFiles();
            if (files != null) {
                for (File f : files) {
                    collectTests(root, f, out, ptrn, exclude);
                }
                return;
            }
            return;
        }
        String path = arg.getAbsolutePath();
        String prefix = root.getAbsolutePath();
        String cls = path.substring(prefix.length() + 1).replace(File.separator, ".");
        if (cls.matches(ptrn) && !cls.matches(exclude)) {
            if (cls.indexOf(36) != -1) {
                System.out.println("Inner class " + cls + " not included");
                return;
            }
            String cls2 = cls.replace(".class", "");
            try {
                Class<?> testclass = Class.forName(cls2);
                if (TestCase.class.isAssignableFrom(testclass) || checkForTestAnnotation(testclass)) {
                    out.add(testclass);
                }
            } catch (Throwable th) {
                System.out.println("Class " + cls2 + " is not in classpath");
            }
        }
    }

    private static Map<String, Class<?>> getLoadedClasses(String ptrn) {
        CodeSource cs;
        URL loc;
        Field _classes = (Field) AccessController.doPrivileged(new PrivilegedAction<Field>() { // from class: org.apache.poi.util.OOXMLLite.1
            @Override // java.security.PrivilegedAction
            public Field run() {
                try {
                    Field fld = ClassLoader.class.getDeclaredField("classes");
                    fld.setAccessible(true);
                    return fld;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ClassLoader appLoader = ClassLoader.getSystemClassLoader();
        try {
            Vector<Class<?>> classes = (Vector) _classes.get(appLoader);
            Map<String, Class<?>> map = new HashMap<>();
            for (Class<?> cls : classes) {
                ProtectionDomain pd = cls.getProtectionDomain();
                if (pd != null && (cs = pd.getCodeSource()) != null && (loc = cs.getLocation()) != null) {
                    String jar = loc.toString();
                    if (jar.contains(ptrn)) {
                        map.put(cls.getName(), cls);
                    }
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFile(InputStream srcStream, File destFile) throws IOException {
        File destDirectory = destFile.getParentFile();
        if (!destDirectory.exists() && !destDirectory.mkdirs()) {
            throw new RuntimeException("Can't create destination directory: " + destDirectory);
        }
        OutputStream destStream = new FileOutputStream(destFile);
        try {
            IOUtils.copy(srcStream, destStream);
        } finally {
            destStream.close();
        }
    }
}
