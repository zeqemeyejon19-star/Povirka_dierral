package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBHyperlinksTable {
    private static final BitSet RECORDS;
    private final List<XSSFHyperlinkRecord> hyperlinkRecords = new ArrayList();
    private Map<String, String> relIdToHyperlink = new HashMap();

    static {
        BitSet bitSet = new BitSet();
        RECORDS = bitSet;
        bitSet.set(XSSFBRecordType.BrtHLink.getId());
    }

    public XSSFBHyperlinksTable(PackagePart sheetPart) throws IOException {
        loadUrlsFromSheetRels(sheetPart);
        HyperlinkSheetScraper scraper = new HyperlinkSheetScraper(sheetPart.getInputStream());
        scraper.parse();
    }

    public Map<CellAddress, List<XSSFHyperlinkRecord>> getHyperLinks() {
        Map<CellAddress, List<XSSFHyperlinkRecord>> hyperlinkMap = new TreeMap<>(new TopLeftCellAddressComparator());
        for (XSSFHyperlinkRecord hyperlinkRecord : this.hyperlinkRecords) {
            CellAddress cellAddress = new CellAddress(hyperlinkRecord.getCellRangeAddress().getFirstRow(), hyperlinkRecord.getCellRangeAddress().getFirstColumn());
            List<XSSFHyperlinkRecord> list = hyperlinkMap.get(cellAddress);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(hyperlinkRecord);
            hyperlinkMap.put(cellAddress, list);
        }
        return hyperlinkMap;
    }

    public List<XSSFHyperlinkRecord> findHyperlinkRecord(CellAddress cellAddress) {
        List<XSSFHyperlinkRecord> overlapping = null;
        CellRangeAddress targetCellRangeAddress = new CellRangeAddress(cellAddress.getRow(), cellAddress.getRow(), cellAddress.getColumn(), cellAddress.getColumn());
        for (XSSFHyperlinkRecord record : this.hyperlinkRecords) {
            if (CellRangeUtil.intersect(targetCellRangeAddress, record.getCellRangeAddress()) != 1) {
                if (overlapping == null) {
                    overlapping = new ArrayList<>();
                }
                overlapping.add(record);
            }
        }
        return overlapping;
    }

    private void loadUrlsFromSheetRels(PackagePart sheetPart) {
        try {
            for (PackageRelationship rel : sheetPart.getRelationshipsByType(XSSFRelation.SHEET_HYPERLINKS.getRelation())) {
                this.relIdToHyperlink.put(rel.getId(), rel.getTargetURI().toString());
            }
        } catch (InvalidFormatException e) {
        }
    }

    private class HyperlinkSheetScraper extends XSSFBParser {
        private XSSFBCellRange hyperlinkCellRange;
        private final StringBuilder xlWideStringBuffer;

        HyperlinkSheetScraper(InputStream is) {
            super(is, XSSFBHyperlinksTable.RECORDS);
            this.hyperlinkCellRange = new XSSFBCellRange();
            this.xlWideStringBuffer = new StringBuilder();
        }

        @Override // org.apache.poi.xssf.binary.XSSFBParser
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            if (recordType == XSSFBRecordType.BrtHLink.getId()) {
                this.hyperlinkCellRange = XSSFBCellRange.parse(data, 0, this.hyperlinkCellRange);
                int offset = 0 + 16;
                this.xlWideStringBuffer.setLength(0);
                int offset2 = offset + XSSFBUtils.readXLNullableWideString(data, offset, this.xlWideStringBuffer);
                String relId = this.xlWideStringBuffer.toString();
                this.xlWideStringBuffer.setLength(0);
                int offset3 = offset2 + XSSFBUtils.readXLWideString(data, offset2, this.xlWideStringBuffer);
                String location = this.xlWideStringBuffer.toString();
                this.xlWideStringBuffer.setLength(0);
                int offset4 = offset3 + XSSFBUtils.readXLWideString(data, offset3, this.xlWideStringBuffer);
                String toolTip = this.xlWideStringBuffer.toString();
                this.xlWideStringBuffer.setLength(0);
                int xLWideString = offset4 + XSSFBUtils.readXLWideString(data, offset4, this.xlWideStringBuffer);
                String display = this.xlWideStringBuffer.toString();
                CellRangeAddress cellRangeAddress = new CellRangeAddress(this.hyperlinkCellRange.firstRow, this.hyperlinkCellRange.lastRow, this.hyperlinkCellRange.firstCol, this.hyperlinkCellRange.lastCol);
                String url = (String) XSSFBHyperlinksTable.this.relIdToHyperlink.get(relId);
                if (location == null || location.length() == 0) {
                    location = url;
                }
                XSSFBHyperlinksTable.this.hyperlinkRecords.add(new XSSFHyperlinkRecord(cellRangeAddress, relId, location, toolTip, display));
            }
        }
    }

    private static class TopLeftCellAddressComparator implements Comparator<CellAddress>, Serializable {
        private static final long serialVersionUID = 1;

        private TopLeftCellAddressComparator() {
        }

        @Override // java.util.Comparator
        public int compare(CellAddress o1, CellAddress o2) {
            if (o1.getRow() < o2.getRow()) {
                return -1;
            }
            if (o1.getRow() > o2.getRow()) {
                return 1;
            }
            if (o1.getColumn() < o2.getColumn()) {
                return -1;
            }
            return o1.getColumn() > o2.getColumn() ? 1 : 0;
        }
    }
}
