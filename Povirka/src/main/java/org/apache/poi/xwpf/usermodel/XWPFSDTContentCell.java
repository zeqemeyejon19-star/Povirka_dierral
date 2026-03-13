package org.apache.poi.xwpf.usermodel;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtContentCell;

/* JADX INFO: loaded from: classes.dex */
public class XWPFSDTContentCell implements ISDTContent {
    private String text;

    public XWPFSDTContentCell(CTSdtContentCell sdtContentCell, XWPFTableRow xwpfTableRow, IBody part) {
        this.text = "";
        if (sdtContentCell == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        XmlCursor cursor = sdtContentCell.newCursor();
        int tcCnt = 0;
        int iBodyCnt = 0;
        int depth = 1;
        while (cursor.hasNextToken() && depth > 0) {
            XmlCursor.TokenType t = cursor.toNextToken();
            if (t.isText()) {
                sb.append(cursor.getTextValue());
            } else if (isStartToken(cursor, "tr")) {
                tcCnt = 0;
                iBodyCnt = 0;
            } else if (isStartToken(cursor, "tc")) {
                int tcCnt2 = tcCnt + 1;
                if (tcCnt > 0) {
                    sb.append("\t");
                }
                iBodyCnt = 0;
                tcCnt = tcCnt2;
            } else if (isStartToken(cursor, "p") || isStartToken(cursor, "tbl") || isStartToken(cursor, "sdt")) {
                if (iBodyCnt > 0) {
                    sb.append("\n");
                }
                iBodyCnt++;
            }
            if (cursor.isStart()) {
                depth++;
            } else if (cursor.isEnd()) {
                depth--;
            }
        }
        this.text = sb.toString();
        cursor.dispose();
    }

    private boolean isStartToken(XmlCursor cursor, String string) {
        QName qName;
        return cursor.isStart() && (qName = cursor.getName()) != null && qName.getLocalPart() != null && qName.getLocalPart().equals(string);
    }

    @Override // org.apache.poi.xwpf.usermodel.ISDTContent
    public String getText() {
        return this.text;
    }

    @Override // org.apache.poi.xwpf.usermodel.ISDTContent
    public String toString() {
        return getText();
    }
}
