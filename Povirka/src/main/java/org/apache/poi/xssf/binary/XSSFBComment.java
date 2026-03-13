package org.apache.poi.xssf.binary;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFComment;

/* JADX INFO: loaded from: classes.dex */
@Internal
class XSSFBComment extends XSSFComment {
    private final String author;
    private final CellAddress cellAddress;
    private final XSSFBRichTextString comment;
    private boolean visible;

    XSSFBComment(CellAddress cellAddress, String author, String comment) {
        super(null, null, null);
        this.visible = true;
        this.cellAddress = cellAddress;
        this.author = author;
        this.comment = new XSSFBRichTextString(comment);
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setVisible(boolean visible) {
        throw new IllegalArgumentException("XSSFBComment is read only.");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public boolean isVisible() {
        return this.visible;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public CellAddress getAddress() {
        return this.cellAddress;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setAddress(CellAddress addr) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setAddress(int row, int col) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public int getRow() {
        return this.cellAddress.getRow();
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setRow(int row) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public int getColumn() {
        return this.cellAddress.getColumn();
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setColumn(int col) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public String getAuthor() {
        return this.author;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setAuthor(String author) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public XSSFBRichTextString getString() {
        return this.comment;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public void setString(RichTextString string) {
        throw new IllegalArgumentException("XSSFBComment is read only");
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFComment, org.apache.poi.ss.usermodel.Comment
    public ClientAnchor getClientAnchor() {
        return null;
    }
}
