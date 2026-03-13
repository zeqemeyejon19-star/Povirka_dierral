package org.apache.poi.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HexRead {
    public static byte[] readData(String filename) throws IOException {
        File file = new File(filename);
        InputStream stream = new FileInputStream(file);
        try {
            return readData(stream, -1);
        } finally {
            stream.close();
        }
    }

    public static byte[] readData(InputStream stream, String section) throws IOException {
        try {
            StringBuffer sectionText = new StringBuffer();
            boolean inSection = false;
            int c = stream.read();
            while (c != -1) {
                if (c == 10 || c == 13) {
                    inSection = false;
                    sectionText = new StringBuffer();
                } else if (c == 91) {
                    inSection = true;
                } else if (c == 93) {
                    inSection = false;
                    if (sectionText.toString().equals(section)) {
                        return readData(stream, 91);
                    }
                    sectionText = new StringBuffer();
                } else if (inSection) {
                    sectionText.append((char) c);
                }
                c = stream.read();
            }
            stream.close();
            throw new IOException("Section '" + section + "' not found");
        } finally {
            stream.close();
        }
    }

    public static byte[] readData(String filename, String section) throws IOException {
        return readData(new FileInputStream(filename), section);
    }

    public static byte[] readData(InputStream stream, int eofChar) throws IOException {
        int characterCount = 0;
        byte b = 0;
        List<Byte> bytes = new ArrayList<>();
        while (true) {
            int count = stream.read();
            int digitValue = -1;
            if (48 > count || count > 57) {
                if (65 > count || count > 70) {
                    if (97 > count || count > 102) {
                        if (35 == count) {
                            readToEOL(stream);
                        } else if (-1 == count || eofChar == count) {
                            break;
                        }
                    } else {
                        digitValue = count - 87;
                    }
                } else {
                    digitValue = count - 55;
                }
            } else {
                digitValue = count - 48;
            }
            if (digitValue != -1) {
                b = (byte) (((byte) digitValue) + ((byte) (b << 4)));
                characterCount++;
                if (characterCount == 2) {
                    bytes.add(Byte.valueOf(b));
                    characterCount = 0;
                    b = 0;
                }
            }
        }
        Byte[] polished = (Byte[]) bytes.toArray(new Byte[bytes.size()]);
        byte[] rval = new byte[polished.length];
        for (int j = 0; j < polished.length; j++) {
            rval[j] = polished[j].byteValue();
        }
        return rval;
    }

    public static byte[] readFromString(String data) {
        try {
            return readData(new ByteArrayInputStream(data.getBytes(StringUtil.UTF8)), -1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readToEOL(InputStream stream) throws IOException {
        int c = stream.read();
        while (c != -1 && c != 10 && c != 13) {
            c = stream.read();
        }
    }
}
