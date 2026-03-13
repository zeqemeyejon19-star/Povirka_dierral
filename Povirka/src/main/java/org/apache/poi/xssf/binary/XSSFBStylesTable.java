package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBStylesTable extends XSSFBParser {
    private boolean inCellXFS;
    private boolean inFmts;
    private final SortedMap<Short, String> numberFormats;
    private final List<Short> styleIds;

    public XSSFBStylesTable(InputStream is) throws IOException {
        super(is);
        this.numberFormats = new TreeMap();
        this.styleIds = new ArrayList();
        this.inCellXFS = false;
        this.inFmts = false;
        parse();
    }

    String getNumberFormatString(int idx) {
        short numberFormatIdx = getNumberFormatIndex(idx);
        if (this.numberFormats.containsKey(Short.valueOf(numberFormatIdx))) {
            return this.numberFormats.get(Short.valueOf(numberFormatIdx));
        }
        return BuiltinFormats.getBuiltinFormat(numberFormatIdx);
    }

    short getNumberFormatIndex(int idx) {
        return this.styleIds.get(idx).shortValue();
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.binary.XSSFBStylesTable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType;

        static {
            int[] iArr = new int[XSSFBRecordType.values().length];
            $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType = iArr;
            try {
                iArr[XSSFBRecordType.BrtBeginCellXFs.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtEndCellXFs.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtXf.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtBeginFmts.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtEndFmts.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtFmt.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    @Override // org.apache.poi.xssf.binary.XSSFBParser
    public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
        XSSFBRecordType type = XSSFBRecordType.lookup(recordType);
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[type.ordinal()]) {
            case 1:
                this.inCellXFS = true;
                break;
            case 2:
                this.inCellXFS = false;
                break;
            case 3:
                if (this.inCellXFS) {
                    handleBrtXFInCellXF(data);
                }
                break;
            case 4:
                this.inFmts = true;
                break;
            case 5:
                this.inFmts = false;
                break;
            case 6:
                if (this.inFmts) {
                    handleFormat(data);
                }
                break;
        }
    }

    private void handleFormat(byte[] data) {
        int ifmt = data[0] & 255;
        if (ifmt > 32767) {
            throw new POIXMLException("Format id must be a short");
        }
        StringBuilder sb = new StringBuilder();
        XSSFBUtils.readXLWideString(data, 2, sb);
        String fmt = sb.toString();
        this.numberFormats.put(Short.valueOf((short) ifmt), fmt);
    }

    private void handleBrtXFInCellXF(byte[] data) {
        int ifmt = data[2] & 255;
        this.styleIds.add(Short.valueOf((short) ifmt));
    }
}
