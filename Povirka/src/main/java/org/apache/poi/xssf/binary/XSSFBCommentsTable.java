package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBCommentsTable extends XSSFBParser {
    private StringBuilder authorBuffer;
    private int authorId;
    private List<String> authors;
    private CellAddress cellAddress;
    private XSSFBCellRange cellRange;
    private String comment;
    private Queue<CellAddress> commentAddresses;
    private Map<CellAddress, XSSFBComment> comments;

    public XSSFBCommentsTable(InputStream is) throws IOException {
        super(is);
        this.comments = new TreeMap();
        this.commentAddresses = new LinkedList();
        this.authors = new ArrayList();
        this.authorId = -1;
        this.cellAddress = null;
        this.cellRange = null;
        this.comment = null;
        this.authorBuffer = new StringBuilder();
        parse();
        this.commentAddresses.addAll(this.comments.keySet());
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.binary.XSSFBCommentsTable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType;

        static {
            int[] iArr = new int[XSSFBRecordType.values().length];
            $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType = iArr;
            try {
                iArr[XSSFBRecordType.BrtBeginComment.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCommentText.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtEndComment.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[XSSFBRecordType.BrtCommentAuthor.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @Override // org.apache.poi.xssf.binary.XSSFBParser
    public void handleRecord(int id, byte[] data) throws XSSFBParseException {
        XSSFBRecordType recordType = XSSFBRecordType.lookup(id);
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$binary$XSSFBRecordType[recordType.ordinal()];
        if (i == 1) {
            this.authorId = XSSFBUtils.castToInt(LittleEndian.getUInt(data));
            int offset = 0 + 4;
            this.cellRange = XSSFBCellRange.parse(data, offset, this.cellRange);
            int i2 = offset + 16;
            this.cellAddress = new CellAddress(this.cellRange.firstRow, this.cellRange.firstCol);
            return;
        }
        if (i == 2) {
            XSSFBRichStr xssfbRichStr = XSSFBRichStr.build(data, 0);
            this.comment = xssfbRichStr.getString();
        } else if (i == 3) {
            this.comments.put(this.cellAddress, new XSSFBComment(this.cellAddress, this.authors.get(this.authorId), this.comment));
            this.authorId = -1;
            this.cellAddress = null;
        } else if (i == 4) {
            this.authorBuffer.setLength(0);
            XSSFBUtils.readXLWideString(data, 0, this.authorBuffer);
            this.authors.add(this.authorBuffer.toString());
        }
    }

    public Queue<CellAddress> getAddresses() {
        return this.commentAddresses;
    }

    public XSSFBComment get(CellAddress cellAddress) {
        if (cellAddress == null) {
            return null;
        }
        return this.comments.get(cellAddress);
    }
}
