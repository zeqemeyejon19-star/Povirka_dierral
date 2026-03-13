package org.apache.poi.xssf.binary;

import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBHeaderFooters {
    private XSSFBHeaderFooter footer;
    private XSSFBHeaderFooter footerEven;
    private XSSFBHeaderFooter footerFirst;
    private XSSFBHeaderFooter header;
    private XSSFBHeaderFooter headerEven;
    private XSSFBHeaderFooter headerFirst;

    XSSFBHeaderFooters() {
    }

    public static XSSFBHeaderFooters parse(byte[] data) {
        XSSFBHeaderFooters xssfbHeaderFooter = new XSSFBHeaderFooters();
        xssfbHeaderFooter.header = new XSSFBHeaderFooter("header", true);
        xssfbHeaderFooter.footer = new XSSFBHeaderFooter("footer", false);
        xssfbHeaderFooter.headerEven = new XSSFBHeaderFooter("evenHeader", true);
        xssfbHeaderFooter.footerEven = new XSSFBHeaderFooter("evenFooter", false);
        xssfbHeaderFooter.headerFirst = new XSSFBHeaderFooter("firstHeader", true);
        xssfbHeaderFooter.footerFirst = new XSSFBHeaderFooter("firstFooter", false);
        int offset = 2 + readHeaderFooter(data, 2, xssfbHeaderFooter.header);
        int offset2 = offset + readHeaderFooter(data, offset, xssfbHeaderFooter.footer);
        int offset3 = offset2 + readHeaderFooter(data, offset2, xssfbHeaderFooter.headerEven);
        int offset4 = offset3 + readHeaderFooter(data, offset3, xssfbHeaderFooter.footerEven);
        readHeaderFooter(data, offset4 + readHeaderFooter(data, offset4, xssfbHeaderFooter.headerFirst), xssfbHeaderFooter.footerFirst);
        return xssfbHeaderFooter;
    }

    private static int readHeaderFooter(byte[] data, int offset, XSSFBHeaderFooter headerFooter) {
        if (offset + 4 >= data.length) {
            return 0;
        }
        StringBuilder sb = new StringBuilder();
        int bytesRead = XSSFBUtils.readXLNullableWideString(data, offset, sb);
        headerFooter.setRawString(sb.toString());
        return bytesRead;
    }

    public XSSFBHeaderFooter getHeader() {
        return this.header;
    }

    public XSSFBHeaderFooter getFooter() {
        return this.footer;
    }

    public XSSFBHeaderFooter getHeaderEven() {
        return this.headerEven;
    }

    public XSSFBHeaderFooter getFooterEven() {
        return this.footerEven;
    }

    public XSSFBHeaderFooter getHeaderFirst() {
        return this.headerFirst;
    }

    public XSSFBHeaderFooter getFooterFirst() {
        return this.footerFirst;
    }
}
