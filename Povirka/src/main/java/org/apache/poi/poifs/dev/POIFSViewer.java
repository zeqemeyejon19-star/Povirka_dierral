package org.apache.poi.poifs.dev;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;

/* JADX INFO: loaded from: classes.dex */
public class POIFSViewer {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        boolean printNames = args.length > 1;
        for (String str : args) {
            viewFile(str, printNames);
        }
    }

    private static void viewFile(String filename, boolean printName) {
        if (printName) {
            StringBuffer flowerbox = new StringBuffer();
            flowerbox.append(".");
            for (int j = 0; j < filename.length(); j++) {
                flowerbox.append("-");
            }
            flowerbox.append(".");
            System.out.println(flowerbox);
            System.out.println("|" + filename + "|");
            System.out.println(flowerbox);
        }
        try {
            NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(filename));
            List<String> strings = POIFSViewEngine.inspectViewable(fs, true, 0, "  ");
            for (String s : strings) {
                System.out.print(s);
            }
            fs.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
