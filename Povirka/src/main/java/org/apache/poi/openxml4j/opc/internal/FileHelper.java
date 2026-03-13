package org.apache.poi.openxml4j.opc.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/* JADX INFO: loaded from: classes.dex */
public final class FileHelper {
    public static File getDirectory(File f) {
        char ch1;
        if (f != null) {
            String path = f.getPath();
            int len = path.length();
            int num2 = len;
            do {
                num2--;
                if (num2 >= 0) {
                    ch1 = path.charAt(num2);
                } else {
                    return null;
                }
            } while (ch1 != File.separatorChar);
            return new File(path.substring(0, num2));
        }
        return null;
    }

    public static void copyFile(File in, File out) throws IOException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        FileChannel sourceChannel = fis.getChannel();
        FileChannel destinationChannel = fos.getChannel();
        sourceChannel.transferTo(0L, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
        fos.close();
        fis.close();
    }

    public static String getFilename(File file) {
        char ch1;
        if (file != null) {
            String path = file.getPath();
            int len = path.length();
            int num2 = len;
            do {
                num2--;
                if (num2 >= 0) {
                    ch1 = path.charAt(num2);
                } else {
                    return "";
                }
            } while (ch1 != File.separatorChar);
            return path.substring(num2 + 1, len);
        }
        return "";
    }
}
