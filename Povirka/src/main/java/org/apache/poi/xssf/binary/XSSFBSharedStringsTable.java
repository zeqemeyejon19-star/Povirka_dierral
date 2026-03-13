package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBSharedStringsTable {
    private int count;
    private List<String> strings = new ArrayList();
    private int uniqueCount;

    public XSSFBSharedStringsTable(OPCPackage pkg) throws SAXException, IOException {
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFBRelation.SHARED_STRINGS_BINARY.getContentType());
        if (parts.size() > 0) {
            PackagePart sstPart = parts.get(0);
            readFrom(sstPart.getInputStream());
        }
    }

    XSSFBSharedStringsTable(PackagePart part) throws SAXException, IOException {
        readFrom(part.getInputStream());
    }

    private void readFrom(InputStream inputStream) throws IOException {
        SSTBinaryReader reader = new SSTBinaryReader(inputStream);
        reader.parse();
    }

    public List<String> getItems() {
        List<String> ret = new ArrayList<>(this.strings.size());
        ret.addAll(this.strings);
        return ret;
    }

    public String getEntryAt(int i) {
        return this.strings.get(i);
    }

    public int getCount() {
        return this.count;
    }

    public int getUniqueCount() {
        return this.uniqueCount;
    }

    private class SSTBinaryReader extends XSSFBParser {
        SSTBinaryReader(InputStream is) {
            super(is);
        }

        @Override // org.apache.poi.xssf.binary.XSSFBParser
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            XSSFBRecordType type = XSSFBRecordType.lookup(recordType);
            int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[type.ordinal()];
            if (i == 1) {
                XSSFBRichStr rstr = XSSFBRichStr.build(data, 0);
                XSSFBSharedStringsTable.this.strings.add(rstr.getString());
            } else if (i == 2) {
                XSSFBSharedStringsTable.this.count = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 0));
                XSSFBSharedStringsTable.this.uniqueCount = XSSFBUtils.castToInt(LittleEndian.getUInt(data, 4));
            }
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.binary.XSSFBSharedStringsTable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType;

        static {
            int[] iArr = new int[XSSFBRecordType.values().length];
            $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType = iArr;
            try {
                iArr[XSSFBRecordType.BrtSstItem.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtBeginSst.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
