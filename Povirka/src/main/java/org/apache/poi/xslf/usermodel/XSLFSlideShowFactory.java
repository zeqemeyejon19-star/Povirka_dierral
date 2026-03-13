package org.apache.poi.xslf.usermodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSLFSlideShowFactory extends SlideShowFactory {
    public static SlideShow<?, ?> createSlideShow(OPCPackage pkg) throws IOException {
        try {
            return new XMLSlideShow(pkg);
        } catch (IllegalArgumentException ioe) {
            pkg.revert();
            throw ioe;
        }
    }

    public static SlideShow<?, ?> createSlideShow(File file, boolean readOnly) throws InvalidFormatException, IOException {
        OPCPackage pkg = OPCPackage.open(file, readOnly ? PackageAccess.READ : PackageAccess.READ_WRITE);
        return createSlideShow(pkg);
    }

    public static SlideShow<?, ?> createSlideShow(InputStream stream) throws InvalidFormatException, IOException {
        OPCPackage pkg = OPCPackage.open(stream);
        return createSlideShow(pkg);
    }
}
