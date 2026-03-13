package org.apache.poi.xssf.usermodel;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.vml.CTShape;
import java.math.BigInteger;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.CommentsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;

/* JADX INFO: loaded from: classes.dex */
public class XSSFComment implements Comment {
    private final CTComment _comment;
    private final CommentsTable _comments;
    private XSSFRichTextString _str;
    private final CTShape _vmlShape;

    public XSSFComment(CommentsTable comments, CTComment comment, CTShape vmlShape) {
        this._comment = comment;
        this._comments = comments;
        this._vmlShape = vmlShape;
        if (vmlShape != null && vmlShape.sizeOfClientDataArray() > 0) {
            CellReference ref = new CellReference(comment.getRef());
            CTClientData clientData = vmlShape.getClientDataArray(0);
            clientData.setRowArray(0, new BigInteger(String.valueOf(ref.getRow())));
            clientData.setColumnArray(0, new BigInteger(String.valueOf((int) ref.getCol())));
            avoidXmlbeansCorruptPointer(vmlShape);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public String getAuthor() {
        return this._comments.getAuthor((int) this._comment.getAuthorId());
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setAuthor(String author) {
        this._comment.setAuthorId(this._comments.findAuthor(author));
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public int getColumn() {
        return getAddress().getColumn();
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public int getRow() {
        return getAddress().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public boolean isVisible() {
        CTShape cTShape = this._vmlShape;
        if (cTShape == null) {
            return false;
        }
        String style = cTShape.getStyle();
        boolean visible = style != null && style.contains("visibility:visible");
        return visible;
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setVisible(boolean visible) {
        CTShape cTShape = this._vmlShape;
        if (cTShape != null) {
            String style = visible ? "position:absolute;visibility:visible" : "position:absolute;visibility:hidden";
            cTShape.setStyle(style);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public CellAddress getAddress() {
        return new CellAddress(this._comment.getRef());
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setAddress(int row, int col) {
        setAddress(new CellAddress(row, col));
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setAddress(CellAddress address) {
        CellAddress oldRef = new CellAddress(this._comment.getRef());
        if (address.equals(oldRef)) {
            return;
        }
        this._comment.setRef(address.formatAsString());
        this._comments.referenceUpdated(oldRef, this._comment);
        CTShape cTShape = this._vmlShape;
        if (cTShape != null) {
            CTClientData clientData = cTShape.getClientDataArray(0);
            clientData.setRowArray(0, new BigInteger(String.valueOf(address.getRow())));
            clientData.setColumnArray(0, new BigInteger(String.valueOf(address.getColumn())));
            avoidXmlbeansCorruptPointer(this._vmlShape);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setColumn(int col) {
        setAddress(getRow(), col);
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setRow(int row) {
        setAddress(row, getColumn());
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public XSSFRichTextString getString() {
        if (this._str == null) {
            CTRst rst = this._comment.getText();
            if (rst != null) {
                this._str = new XSSFRichTextString(this._comment.getText());
            }
        }
        return this._str;
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public void setString(RichTextString string) {
        if (!(string instanceof XSSFRichTextString)) {
            throw new IllegalArgumentException("Only XSSFRichTextString argument is supported");
        }
        XSSFRichTextString xSSFRichTextString = (XSSFRichTextString) string;
        this._str = xSSFRichTextString;
        this._comment.setText(xSSFRichTextString.getCTRst());
    }

    public void setString(String string) {
        setString(new XSSFRichTextString(string));
    }

    @Override // org.apache.poi.ss.usermodel.Comment
    public ClientAnchor getClientAnchor() {
        String position = this._vmlShape.getClientDataArray(0).getAnchorArray(0);
        int[] pos = new int[8];
        int i = 0;
        String[] arr$ = position.split(",");
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String s = arr$[i$];
            pos[i] = Integer.parseInt(s.trim());
            i$++;
            i++;
        }
        XSSFClientAnchor ca = new XSSFClientAnchor(pos[1] * 9525, pos[3] * 9525, pos[5] * 9525, pos[7] * 9525, pos[0], pos[2], pos[4], pos[6]);
        return ca;
    }

    protected CTComment getCTComment() {
        return this._comment;
    }

    protected CTShape getCTShape() {
        return this._vmlShape;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof XSSFComment)) {
            return false;
        }
        XSSFComment other = (XSSFComment) obj;
        return getCTComment() == other.getCTComment() && getCTShape() == other.getCTShape();
    }

    public int hashCode() {
        return ((getRow() * 17) + getColumn()) * 31;
    }

    private static void avoidXmlbeansCorruptPointer(CTShape vmlShape) {
        vmlShape.getClientDataList().toString();
    }
}
