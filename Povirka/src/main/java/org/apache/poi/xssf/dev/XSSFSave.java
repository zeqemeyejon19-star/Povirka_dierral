package org.apache.poi.xssf.dev;

import java.io.FileOutputStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/* JADX INFO: loaded from: classes.dex */
public final class XSSFSave {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < args.length; i++) {
            OPCPackage pkg = OPCPackage.open(args[i]);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            int sep = args[i].lastIndexOf(46);
            String outfile = args[i].substring(0, sep) + "-save.xls" + (wb.isMacroEnabled() ? "m" : "x");
            FileOutputStream out = new FileOutputStream(outfile);
            wb.write(out);
            out.close();
            pkg.close();
        }
    }
}
