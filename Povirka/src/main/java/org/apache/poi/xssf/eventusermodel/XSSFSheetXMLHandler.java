package org.apache.poi.xssf.eventusermodel;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: classes.dex */
public class XSSFSheetXMLHandler extends DefaultHandler {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) XSSFSheetXMLHandler.class);
    private String cellRef;
    private Queue<CellAddress> commentCellRefs;
    private CommentsTable commentsTable;
    private boolean fIsOpen;
    private short formatIndex;
    private String formatString;
    private final DataFormatter formatter;
    private StringBuffer formula;
    private boolean formulasNotResults;
    private StringBuffer headerFooter;
    private boolean hfIsOpen;
    private boolean isIsOpen;
    private xssfDataType nextDataType;
    private int nextRowNum;
    private final SheetContentsHandler output;
    private int rowNum;
    private ReadOnlySharedStringsTable sharedStringsTable;
    private StylesTable stylesTable;
    private boolean vIsOpen;
    private StringBuffer value;

    private enum EmptyCellCommentsCheckType {
        CELL,
        END_OF_ROW,
        END_OF_SHEET_DATA
    }

    public interface SheetContentsHandler {
        void cell(String str, String str2, XSSFComment xSSFComment);

        void endRow(int i);

        void headerFooter(String str, boolean z, String str2);

        void startRow(int i);
    }

    enum xssfDataType {
        BOOLEAN,
        ERROR,
        FORMULA,
        INLINE_STRING,
        SST_STRING,
        NUMBER
    }

    public XSSFSheetXMLHandler(StylesTable styles, CommentsTable comments, ReadOnlySharedStringsTable strings, SheetContentsHandler sheetContentsHandler, DataFormatter dataFormatter, boolean formulasNotResults) {
        this.value = new StringBuffer();
        this.formula = new StringBuffer();
        this.headerFooter = new StringBuffer();
        this.stylesTable = styles;
        this.commentsTable = comments;
        this.sharedStringsTable = strings;
        this.output = sheetContentsHandler;
        this.formulasNotResults = formulasNotResults;
        this.nextDataType = xssfDataType.NUMBER;
        this.formatter = dataFormatter;
        init();
    }

    public XSSFSheetXMLHandler(StylesTable styles, ReadOnlySharedStringsTable strings, SheetContentsHandler sheetContentsHandler, DataFormatter dataFormatter, boolean formulasNotResults) {
        this(styles, null, strings, sheetContentsHandler, dataFormatter, formulasNotResults);
    }

    public XSSFSheetXMLHandler(StylesTable styles, ReadOnlySharedStringsTable strings, SheetContentsHandler sheetContentsHandler, boolean formulasNotResults) {
        this(styles, strings, sheetContentsHandler, new DataFormatter(), formulasNotResults);
    }

    private void init() {
        if (this.commentsTable != null) {
            this.commentCellRefs = new LinkedList();
            CTComment[] arr$ = this.commentsTable.getCTComments().getCommentList().getCommentArray();
            for (CTComment comment : arr$) {
                this.commentCellRefs.add(new CellAddress(comment.getRef()));
            }
        }
    }

    private boolean isTextTag(String name) {
        if ("v".equals(name) || "inlineStr".equals(name)) {
            return true;
        }
        return "t".equals(name) && this.isIsOpen;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (uri != null && !uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
            return;
        }
        if (isTextTag(localName)) {
            this.vIsOpen = true;
            this.value.setLength(0);
            return;
        }
        if ("is".equals(localName)) {
            this.isIsOpen = true;
            return;
        }
        if ("f".equals(localName)) {
            this.formula.setLength(0);
            if (this.nextDataType == xssfDataType.NUMBER) {
                this.nextDataType = xssfDataType.FORMULA;
            }
            String type = attributes.getValue("t");
            if (type != null && type.equals("shared")) {
                String ref = attributes.getValue("ref");
                attributes.getValue("si");
                if (ref != null) {
                    this.fIsOpen = true;
                    return;
                } else {
                    if (this.formulasNotResults) {
                        logger.log(5, "shared formulas not yet supported!");
                        return;
                    }
                    return;
                }
            }
            this.fIsOpen = true;
            return;
        }
        if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName) || "firstFooter".equals(localName) || "oddFooter".equals(localName) || "evenFooter".equals(localName)) {
            this.hfIsOpen = true;
            this.headerFooter.setLength(0);
            return;
        }
        if ("row".equals(localName)) {
            String rowNumStr = attributes.getValue("r");
            if (rowNumStr != null) {
                this.rowNum = Integer.parseInt(rowNumStr) - 1;
            } else {
                this.rowNum = this.nextRowNum;
            }
            this.output.startRow(this.rowNum);
            return;
        }
        if ("c".equals(localName)) {
            this.nextDataType = xssfDataType.NUMBER;
            this.formatIndex = (short) -1;
            this.formatString = null;
            this.cellRef = attributes.getValue("r");
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType)) {
                this.nextDataType = xssfDataType.BOOLEAN;
                return;
            }
            if ("e".equals(cellType)) {
                this.nextDataType = xssfDataType.ERROR;
                return;
            }
            if ("inlineStr".equals(cellType)) {
                this.nextDataType = xssfDataType.INLINE_STRING;
                return;
            }
            if ("s".equals(cellType)) {
                this.nextDataType = xssfDataType.SST_STRING;
                return;
            }
            if ("str".equals(cellType)) {
                this.nextDataType = xssfDataType.FORMULA;
                return;
            }
            XSSFCellStyle style = null;
            StylesTable stylesTable = this.stylesTable;
            if (stylesTable != null) {
                if (cellStyleStr != null) {
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    style = this.stylesTable.getStyleAt(styleIndex);
                } else if (stylesTable.getNumCellStyles() > 0) {
                    style = this.stylesTable.getStyleAt(0);
                }
            }
            if (style != null) {
                this.formatIndex = style.getDataFormat();
                String dataFormatString = style.getDataFormatString();
                this.formatString = dataFormatString;
                if (dataFormatString == null) {
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                }
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String thisStr;
        if (uri == null || uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
            String thisStr2 = null;
            if (!isTextTag(localName)) {
                if ("f".equals(localName)) {
                    this.fIsOpen = false;
                    return;
                }
                if ("is".equals(localName)) {
                    this.isIsOpen = false;
                    return;
                }
                if ("row".equals(localName)) {
                    checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_ROW);
                    this.output.endRow(this.rowNum);
                    this.nextRowNum = this.rowNum + 1;
                    return;
                }
                if ("sheetData".equals(localName)) {
                    checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_SHEET_DATA);
                    return;
                }
                if ("oddHeader".equals(localName) || "evenHeader".equals(localName) || "firstHeader".equals(localName)) {
                    this.hfIsOpen = false;
                    this.output.headerFooter(this.headerFooter.toString(), true, localName);
                    return;
                } else {
                    if ("oddFooter".equals(localName) || "evenFooter".equals(localName) || "firstFooter".equals(localName)) {
                        this.hfIsOpen = false;
                        this.output.headerFooter(this.headerFooter.toString(), false, localName);
                        return;
                    }
                    return;
                }
            }
            this.vIsOpen = false;
            switch (AnonymousClass1.$SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[this.nextDataType.ordinal()]) {
                case 1:
                    char first = this.value.charAt(0);
                    thisStr2 = first == '0' ? "FALSE" : "TRUE";
                    break;
                case 2:
                    thisStr2 = "ERROR:" + ((Object) this.value);
                    break;
                case 3:
                    if (!this.formulasNotResults) {
                        String fv = this.value.toString();
                        if (this.formatString == null) {
                            thisStr2 = fv;
                        } else {
                            try {
                                double d = Double.parseDouble(fv);
                                thisStr = this.formatter.formatRawCellContents(d, this.formatIndex, this.formatString);
                            } catch (NumberFormatException e) {
                                thisStr = fv;
                            }
                            thisStr2 = thisStr;
                        }
                    } else {
                        thisStr2 = this.formula.toString();
                    }
                    break;
                case 4:
                    XSSFRichTextString rtsi = new XSSFRichTextString(this.value.toString());
                    thisStr2 = rtsi.toString();
                    break;
                case 5:
                    String sstIndex = this.value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(this.sharedStringsTable.getEntryAt(idx));
                        thisStr2 = rtss.toString();
                    } catch (NumberFormatException ex) {
                        logger.log(7, "Failed to parse SST index '" + sstIndex, ex);
                    }
                    break;
                case 6:
                    String n = this.value.toString();
                    thisStr2 = (this.formatString != null && n.length() > 0) ? this.formatter.formatRawCellContents(Double.parseDouble(n), this.formatIndex, this.formatString) : n;
                    break;
                default:
                    thisStr2 = "(TODO: Unexpected type: " + this.nextDataType + ")";
                    break;
            }
            checkForEmptyCellComments(EmptyCellCommentsCheckType.CELL);
            CommentsTable commentsTable = this.commentsTable;
            XSSFComment comment = commentsTable != null ? commentsTable.findCellComment(new CellAddress(this.cellRef)) : null;
            this.output.cell(this.cellRef, thisStr2, comment);
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType;

        static {
            int[] iArr = new int[xssfDataType.values().length];
            $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType = iArr;
            try {
                iArr[xssfDataType.BOOLEAN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[xssfDataType.ERROR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[xssfDataType.FORMULA.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[xssfDataType.INLINE_STRING.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[xssfDataType.SST_STRING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$eventusermodel$XSSFSheetXMLHandler$xssfDataType[xssfDataType.NUMBER.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.vIsOpen) {
            this.value.append(ch, start, length);
        }
        if (this.fIsOpen) {
            this.formula.append(ch, start, length);
        }
        if (this.hfIsOpen) {
            this.headerFooter.append(ch, start, length);
        }
    }

    private void checkForEmptyCellComments(EmptyCellCommentsCheckType type) {
        CellAddress nextCommentCellRef;
        Queue<CellAddress> queue = this.commentCellRefs;
        if (queue != null && !queue.isEmpty()) {
            if (type == EmptyCellCommentsCheckType.END_OF_SHEET_DATA) {
                while (!this.commentCellRefs.isEmpty()) {
                    outputEmptyCellComment(this.commentCellRefs.remove());
                }
                return;
            }
            if (this.cellRef == null) {
                if (type == EmptyCellCommentsCheckType.END_OF_ROW) {
                    while (!this.commentCellRefs.isEmpty() && this.commentCellRefs.peek().getRow() == this.rowNum) {
                        outputEmptyCellComment(this.commentCellRefs.remove());
                    }
                    return;
                }
                throw new IllegalStateException("Cell ref should be null only if there are only empty cells in the row; rowNum: " + this.rowNum);
            }
            do {
                CellAddress cellAddress = new CellAddress(this.cellRef);
                CellAddress cellAddressPeek = this.commentCellRefs.peek();
                if (type == EmptyCellCommentsCheckType.CELL && cellAddress.equals(cellAddressPeek)) {
                    this.commentCellRefs.remove();
                    return;
                }
                int comparison = cellAddressPeek.compareTo(cellAddress);
                if (comparison > 0 && type == EmptyCellCommentsCheckType.END_OF_ROW && cellAddressPeek.getRow() <= this.rowNum) {
                    nextCommentCellRef = this.commentCellRefs.remove();
                    outputEmptyCellComment(nextCommentCellRef);
                } else if (comparison < 0 && type == EmptyCellCommentsCheckType.CELL && cellAddressPeek.getRow() <= this.rowNum) {
                    nextCommentCellRef = this.commentCellRefs.remove();
                    outputEmptyCellComment(nextCommentCellRef);
                } else {
                    nextCommentCellRef = null;
                }
                if (nextCommentCellRef == null) {
                    return;
                }
            } while (!this.commentCellRefs.isEmpty());
        }
    }

    private void outputEmptyCellComment(CellAddress cellRef) {
        XSSFComment comment = this.commentsTable.findCellComment(cellRef);
        this.output.cell(cellRef.formatAsString(), null, comment);
    }
}
