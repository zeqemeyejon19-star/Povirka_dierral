package org.apache.poi.xssf.eventusermodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.binary.XSSFBParseException;
import org.apache.poi.xssf.binary.XSSFBParser;
import org.apache.poi.xssf.binary.XSSFBRecordType;
import org.apache.poi.xssf.binary.XSSFBRelation;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
public class XSSFBReader extends XSSFReader {
    private static final POILogger log = POILogFactory.getLogger((Class<?>) XSSFBReader.class);
    private static final Set<String> WORKSHEET_RELS = Collections.unmodifiableSet(new HashSet(Arrays.asList(XSSFRelation.WORKSHEET.getRelation(), XSSFRelation.CHARTSHEET.getRelation(), XSSFRelation.MACRO_SHEET_BIN.getRelation(), XSSFRelation.INTL_MACRO_SHEET_BIN.getRelation(), XSSFRelation.DIALOG_SHEET_BIN.getRelation())));

    public XSSFBReader(OPCPackage pkg) throws OpenXML4JException, IOException {
        super(pkg);
    }

    public String getAbsPathMetadata() throws IOException {
        InputStream is = null;
        try {
            is = this.workbookPart.getInputStream();
            PathExtractor p = new PathExtractor(this.workbookPart.getInputStream());
            p.parse();
            return p.getPath();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override // org.apache.poi.xssf.eventusermodel.XSSFReader
    public Iterator<InputStream> getSheetsData() throws InvalidFormatException, IOException {
        return new SheetIterator(this.workbookPart);
    }

    public XSSFBStylesTable getXSSFBStylesTable() throws IOException {
        ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFBRelation.STYLES_BINARY.getContentType());
        if (parts.size() == 0) {
            return null;
        }
        return new XSSFBStylesTable(parts.get(0).getInputStream());
    }

    public static class SheetIterator extends XSSFReader.SheetIterator {
        private SheetIterator(PackagePart wb) throws IOException {
            super(wb);
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator
        Set<String> getSheetRelationships() {
            return XSSFBReader.WORKSHEET_RELS;
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator
        Iterator<XSSFReader.XSSFSheetRef> createSheetIteratorFromWB(PackagePart wb) throws IOException {
            SheetRefLoader sheetRefLoader = new SheetRefLoader(wb.getInputStream());
            sheetRefLoader.parse();
            return sheetRefLoader.getSheets().iterator();
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator
        public CommentsTable getSheetComments() {
            throw new IllegalArgumentException("Please use getXSSFBSheetComments");
        }

        public XSSFBCommentsTable getXSSFBSheetComments() {
            PackageRelationship comments;
            PackagePart sheetPkg = getSheetPart();
            try {
                PackageRelationshipCollection commentsList = sheetPkg.getRelationshipsByType(XSSFRelation.SHEET_COMMENTS.getRelation());
                if (commentsList.size() > 0 && (comments = commentsList.getRelationship(0)) != null && comments.getTargetURI() != null) {
                    PackagePartName commentsName = PackagingURIHelper.createPartName(comments.getTargetURI());
                    PackagePart commentsPart = sheetPkg.getPackage().getPart(commentsName);
                    return new XSSFBCommentsTable(commentsPart.getInputStream());
                }
                return null;
            } catch (IOException e) {
                return null;
            } catch (InvalidFormatException e2) {
                return null;
            }
        }
    }

    private static class PathExtractor extends XSSFBParser {
        private static BitSet RECORDS;
        private String path;

        static {
            BitSet bitSet = new BitSet();
            RECORDS = bitSet;
            bitSet.set(XSSFBRecordType.BrtAbsPath15.getId());
        }

        public PathExtractor(InputStream is) {
            super(is, RECORDS);
            this.path = null;
        }

        @Override // org.apache.poi.xssf.binary.XSSFBParser
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            if (recordType != XSSFBRecordType.BrtAbsPath15.getId()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            XSSFBUtils.readXLWideString(data, 0, sb);
            this.path = sb.toString();
        }

        String getPath() {
            return this.path;
        }
    }

    private static class SheetRefLoader extends XSSFBParser {
        List<XSSFReader.XSSFSheetRef> sheets;

        private SheetRefLoader(InputStream is) {
            super(is);
            this.sheets = new LinkedList();
        }

        @Override // org.apache.poi.xssf.binary.XSSFBParser
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            if (recordType == XSSFBRecordType.BrtBundleSh.getId()) {
                addWorksheet(data);
            }
        }

        private void addWorksheet(byte[] data) {
            try {
                tryToAddWorksheet(data);
            } catch (XSSFBParseException e) {
                if (tryOldFormat(data)) {
                    XSSFBReader.log.log(5, "This file was written with a beta version of Excel. POI will try to parse the file as a regular xlsb.");
                    return;
                }
                throw e;
            }
        }

        private void tryToAddWorksheet(byte[] data) throws XSSFBParseException {
            LittleEndian.getUInt(data, 0);
            int offset = 0 + 4;
            long iTabID = LittleEndian.getUInt(data, offset);
            int offset2 = offset + 4;
            if (iTabID < 1 || iTabID > 65535) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            StringBuilder sb = new StringBuilder();
            int offset3 = offset2 + XSSFBUtils.readXLWideString(data, offset2, sb);
            String relId = sb.toString();
            sb.setLength(0);
            int xLWideString = offset3 + XSSFBUtils.readXLWideString(data, offset3, sb);
            String name = sb.toString();
            if (relId != null && relId.trim().length() > 0) {
                this.sheets.add(new XSSFReader.XSSFSheetRef(relId, name));
            }
        }

        private boolean tryOldFormat(byte[] data) throws XSSFBParseException {
            long iTabID = LittleEndian.getUInt(data, 8);
            int offset = 8 + 4;
            if (iTabID < 1 || iTabID > 65535) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            StringBuilder sb = new StringBuilder();
            int offset2 = offset + XSSFBUtils.readXLWideString(data, offset, sb);
            String relId = sb.toString();
            sb.setLength(0);
            int offset3 = offset2 + XSSFBUtils.readXLWideString(data, offset2, sb);
            String name = sb.toString();
            if (relId != null && relId.trim().length() > 0) {
                this.sheets.add(new XSSFReader.XSSFSheetRef(relId, name));
            }
            if (offset3 != data.length) {
                return false;
            }
            return true;
        }

        List<XSSFReader.XSSFSheetRef> getSheets() {
            return this.sheets;
        }
    }
}
