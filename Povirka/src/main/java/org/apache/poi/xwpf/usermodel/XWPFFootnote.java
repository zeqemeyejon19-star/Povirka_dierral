package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

/* JADX INFO: loaded from: classes.dex */
public class XWPFFootnote implements Iterable<XWPFParagraph>, IBody {
    private CTFtnEdn ctFtnEdn;
    private XWPFDocument document;
    private XWPFFootnotes footnotes;
    private List<XWPFParagraph> paragraphs = new ArrayList();
    private List<XWPFTable> tables = new ArrayList();
    private List<XWPFPictureData> pictures = new ArrayList();
    private List<IBodyElement> bodyElements = new ArrayList();

    public XWPFFootnote(CTFtnEdn note, XWPFFootnotes xFootnotes) {
        this.footnotes = xFootnotes;
        this.ctFtnEdn = note;
        this.document = xFootnotes.getXWPFDocument();
        init();
    }

    public XWPFFootnote(XWPFDocument document, CTFtnEdn body) {
        this.ctFtnEdn = body;
        this.document = document;
        init();
    }

    private void init() {
        XmlCursor cursor = this.ctFtnEdn.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            CTSdtBlock object = cursor.getObject();
            if (object instanceof CTP) {
                XWPFParagraph p = new XWPFParagraph((CTP) object, this);
                this.bodyElements.add(p);
                this.paragraphs.add(p);
            } else if (object instanceof CTTbl) {
                XWPFTable t = new XWPFTable((CTTbl) object, this);
                this.bodyElements.add(t);
                this.tables.add(t);
            } else if (object instanceof CTSdtBlock) {
                XWPFSDT c = new XWPFSDT(object, this);
                this.bodyElements.add(c);
            }
        }
        cursor.dispose();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFParagraph> getParagraphs() {
        return this.paragraphs;
    }

    @Override // java.lang.Iterable
    public Iterator<XWPFParagraph> iterator() {
        return this.paragraphs.iterator();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFTable> getTables() {
        return this.tables;
    }

    public List<XWPFPictureData> getPictures() {
        return this.pictures;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<IBodyElement> getBodyElements() {
        return this.bodyElements;
    }

    public CTFtnEdn getCTFtnEdn() {
        return this.ctFtnEdn;
    }

    public void setCTFtnEdn(CTFtnEdn footnote) {
        this.ctFtnEdn = footnote;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTableArray(int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        CTTbl[] arr$ = this.ctFtnEdn.getTblArray();
        for (CTTbl tbl : arr$) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            i++;
        }
        this.tables.add(i, table);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTable(CTTbl ctTable) {
        XWPFTable table;
        Iterator<XWPFTable> it = this.tables.iterator();
        while (it.hasNext() && (table = it.next()) != null) {
            if (table.getCTTbl().equals(ctTable)) {
                return table;
            }
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraph(CTP p) {
        for (XWPFParagraph paragraph : this.paragraphs) {
            if (paragraph.getCTP().equals(p)) {
                return paragraph;
            }
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTableCell getTableCell(CTTc cell) {
        XWPFTableRow tableRow;
        XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            return null;
        }
        CTRow row = (CTRow) o;
        cursor.toParent();
        XmlObject o2 = cursor.getObject();
        cursor.dispose();
        if (!(o2 instanceof CTTbl)) {
            return null;
        }
        CTTbl tbl = (CTTbl) o2;
        XWPFTable table = getTable(tbl);
        if (table == null || (tableRow = table.getRow(row)) == null) {
            return null;
        }
        return tableRow.getTableCell(cell);
    }

    private boolean isCursorInFtn(XmlCursor cursor) {
        XmlCursor verify = cursor.newCursor();
        verify.toParent();
        if (verify.getObject() == this.ctFtnEdn) {
            return true;
        }
        return false;
    }

    public POIXMLDocumentPart getOwner() {
        return this.footnotes;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (isCursorInFtn(cursor)) {
            String uri = CTTbl.type.getName().getNamespaceURI();
            cursor.beginElement("tbl", uri);
            cursor.toParent();
            CTTbl t = cursor.getObject();
            XWPFTable newT = new XWPFTable(t, this);
            cursor.removeXmlContents();
            XmlObject o = null;
            while (!(o instanceof CTTbl) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTTbl)) {
                this.tables.add(0, newT);
            } else {
                int pos = this.tables.indexOf(getTable((CTTbl) o)) + 1;
                this.tables.add(pos, newT);
            }
            int i = 0;
            XmlCursor cursor2 = t.newCursor();
            while (cursor2.toPrevSibling()) {
                XmlObject o2 = cursor2.getObject();
                if ((o2 instanceof CTP) || (o2 instanceof CTTbl)) {
                    i++;
                }
            }
            this.bodyElements.add(i, newT);
            XmlCursor c2 = t.newCursor();
            cursor2.toCursor(c2);
            cursor2.toEndToken();
            c2.dispose();
            return newT;
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (isCursorInFtn(cursor)) {
            String uri = CTP.type.getName().getNamespaceURI();
            cursor.beginElement("p", uri);
            cursor.toParent();
            CTP p = cursor.getObject();
            XWPFParagraph newP = new XWPFParagraph(p, this);
            XmlObject o = null;
            while (!(o instanceof CTP) && cursor.toPrevSibling()) {
                o = cursor.getObject();
            }
            if (!(o instanceof CTP) || ((CTP) o) == p) {
                this.paragraphs.add(0, newP);
            } else {
                int pos = this.paragraphs.indexOf(getParagraph((CTP) o)) + 1;
                this.paragraphs.add(pos, newP);
            }
            int i = 0;
            XmlCursor p2 = p.newCursor();
            cursor.toCursor(p2);
            p2.dispose();
            while (cursor.toPrevSibling()) {
                XmlObject o2 = cursor.getObject();
                if ((o2 instanceof CTP) || (o2 instanceof CTTbl)) {
                    i++;
                }
            }
            this.bodyElements.add(i, newP);
            XmlCursor p22 = p.newCursor();
            cursor.toCursor(p22);
            cursor.toEndToken();
            p22.dispose();
            return newP;
        }
        return null;
    }

    public XWPFTable addNewTbl(CTTbl table) {
        CTTbl newTable = this.ctFtnEdn.addNewTbl();
        newTable.set(table);
        XWPFTable xTable = new XWPFTable(newTable, this);
        this.tables.add(xTable);
        return xTable;
    }

    public XWPFParagraph addNewParagraph(CTP paragraph) {
        CTP newPara = this.ctFtnEdn.addNewP();
        newPara.set(paragraph);
        XWPFParagraph xPara = new XWPFParagraph(newPara, this);
        this.paragraphs.add(xPara);
        return xPara;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFDocument getXWPFDocument() {
        return this.document;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public POIXMLDocumentPart getPart() {
        return this.footnotes;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public BodyType getPartType() {
        return BodyType.FOOTNOTE;
    }
}
