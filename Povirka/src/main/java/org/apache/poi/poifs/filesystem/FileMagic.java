package org.apache.poi.poifs.filesystem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.storage.HeaderBlockConstants;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LocaleUtil;

/* JADX INFO: loaded from: classes.dex */
public enum FileMagic {
    OLE2(HeaderBlockConstants._signature),
    OOXML(POIFSConstants.OOXML_FILE_HEADER),
    XML(POIFSConstants.RAW_XML_FILE_HEADER),
    BIFF2(new byte[]{9, 0, 4, 0, 0, 0, 112, 0}),
    BIFF3(new byte[]{9, 2, 6, 0, 0, 0, 112, 0}),
    BIFF4(new byte[]{9, 4, 6, 0, 0, 0, 112, 0}, new byte[]{9, 4, 6, 0, 0, 0, 0, 1}),
    MSWRITE(new byte[]{49, -66, 0, 0}, new byte[]{50, -66, 0, 0}),
    RTF("{\\rtf"),
    PDF("%PDF"),
    UNKNOWN(new byte[0]);

    final byte[][] magic;

    FileMagic(long magic) {
        byte[][] bArr = (byte[][]) Array.newInstance((Class<?>) byte.class, 1, 8);
        this.magic = bArr;
        LittleEndian.putLong(bArr[0], 0, magic);
    }

    FileMagic(byte[]... magic) {
        this.magic = magic;
    }

    FileMagic(String magic) {
        this(magic.getBytes(LocaleUtil.CHARSET_1252));
    }

    public static FileMagic valueOf(byte[] magic) {
        FileMagic[] arr$ = values();
        for (FileMagic fm : arr$) {
            int i = 0;
            boolean found = true;
            byte[][] arr$2 = fm.magic;
            for (byte[] ma : arr$2) {
                int len$ = ma.length;
                int i$ = 0;
                while (i$ < len$) {
                    byte m = ma[i$];
                    int i2 = i + 1;
                    byte d = magic[i];
                    if (d == m || (m == 112 && (d == 16 || d == 32 || d == 64))) {
                        i$++;
                        i = i2;
                    } else {
                        found = false;
                        i = i2;
                        break;
                    }
                }
                if (found) {
                    return fm;
                }
            }
        }
        return UNKNOWN;
    }

    public static FileMagic valueOf(InputStream inp) throws IOException {
        if (!inp.markSupported()) {
            throw new IOException("getFileMagic() only operates on streams which support mark(int)");
        }
        byte[] data = IOUtils.peekFirst8Bytes(inp);
        return valueOf(data);
    }

    public static InputStream prepareToCheckMagic(InputStream stream) {
        if (stream.markSupported()) {
            return stream;
        }
        return new BufferedInputStream(stream);
    }
}
