package org.apache.poi.xwpf.usermodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

/* JADX INFO: loaded from: classes.dex */
public class XWPFTableCell implements IBody, ICell {
    private static EnumMap<XWPFVertAlign, STVerticalJc.Enum> alignMap;
    private static HashMap<Integer, XWPFVertAlign> stVertAlignTypeMap;
    protected List<IBodyElement> bodyElements;
    private final CTTc ctTc;
    protected List<XWPFParagraph> paragraphs;
    protected IBody part;
    private XWPFTableRow tableRow;
    protected List<XWPFTable> tables;

    public enum XWPFVertAlign {
        TOP,
        CENTER,
        BOTH,
        BOTTOM
    }

    static {
        EnumMap<XWPFVertAlign, STVerticalJc.Enum> enumMap = new EnumMap<>(XWPFVertAlign.class);
        alignMap = enumMap;
        enumMap.put(XWPFVertAlign.TOP, STVerticalJc.Enum.forInt(1));
        alignMap.put(XWPFVertAlign.CENTER, STVerticalJc.Enum.forInt(2));
        alignMap.put(XWPFVertAlign.BOTH, STVerticalJc.Enum.forInt(3));
        alignMap.put(XWPFVertAlign.BOTTOM, STVerticalJc.Enum.forInt(4));
        HashMap<Integer, XWPFVertAlign> map = new HashMap<>();
        stVertAlignTypeMap = map;
        map.put(1, XWPFVertAlign.TOP);
        stVertAlignTypeMap.put(2, XWPFVertAlign.CENTER);
        stVertAlignTypeMap.put(3, XWPFVertAlign.BOTH);
        stVertAlignTypeMap.put(4, XWPFVertAlign.BOTTOM);
    }

    public XWPFTableCell(CTTc cell, XWPFTableRow tableRow, IBody part) {
        this.paragraphs = null;
        this.tables = null;
        this.bodyElements = null;
        this.tableRow = null;
        this.ctTc = cell;
        this.part = part;
        this.tableRow = tableRow;
        if (cell.sizeOfPArray() < 1) {
            cell.addNewP();
        }
        this.bodyElements = new ArrayList();
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        XmlCursor cursor = cell.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            CTP object = cursor.getObject();
            if (object instanceof CTP) {
                XWPFParagraph p = new XWPFParagraph(object, this);
                this.paragraphs.add(p);
                this.bodyElements.add(p);
            }
            if (object instanceof CTTbl) {
                XWPFTable t = new XWPFTable((CTTbl) object, this);
                this.tables.add(t);
                this.bodyElements.add(t);
            }
            if (object instanceof CTSdtBlock) {
                this.bodyElements.add(new XWPFSDT((CTSdtBlock) object, this));
            }
            if (object instanceof CTSdtRun) {
                XWPFSDT c = new XWPFSDT((CTSdtRun) object, this);
                System.out.println(c.getContent().getText());
                this.bodyElements.add(c);
            }
        }
        cursor.dispose();
    }

    @Internal
    public CTTc getCTTc() {
        return this.ctTc;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList(this.bodyElements);
    }

    public void setParagraph(XWPFParagraph p) {
        if (this.ctTc.sizeOfPArray() == 0) {
            this.ctTc.addNewP();
        }
        this.ctTc.setPArray(0, p.getCTP());
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFParagraph> getParagraphs() {
        return this.paragraphs;
    }

    public XWPFParagraph addParagraph() {
        XWPFParagraph p = new XWPFParagraph(this.ctTc.addNewP(), this);
        addParagraph(p);
        return p;
    }

    public void addParagraph(XWPFParagraph p) {
        this.paragraphs.add(p);
    }

    public void removeParagraph(int pos) {
        this.paragraphs.remove(pos);
        this.ctTc.removeP(pos);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraph(CTP p) {
        for (XWPFParagraph paragraph : this.paragraphs) {
            if (p.equals(paragraph.getCTP())) {
                return paragraph;
            }
        }
        return null;
    }

    public XWPFTableRow getTableRow() {
        return this.tableRow;
    }

    public String getColor() {
        CTShd ctshd;
        CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr == null || (ctshd = tcpr.getShd()) == null) {
            return null;
        }
        String color = ctshd.xgetFill().getStringValue();
        return color;
    }

    public void setColor(String rgbStr) {
        CTTcPr tcpr = this.ctTc.isSetTcPr() ? this.ctTc.getTcPr() : this.ctTc.addNewTcPr();
        CTShd ctshd = tcpr.isSetShd() ? tcpr.getShd() : tcpr.addNewShd();
        ctshd.setColor("auto");
        ctshd.setVal(STShd.CLEAR);
        ctshd.setFill(rgbStr);
    }

    public XWPFVertAlign getVerticalAlignment() {
        CTVerticalJc va;
        CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr == null || (va = tcpr.getVAlign()) == null) {
            return null;
        }
        XWPFVertAlign vAlign = stVertAlignTypeMap.get(Integer.valueOf(va.getVal().intValue()));
        return vAlign;
    }

    public void setVerticalAlignment(XWPFVertAlign vAlign) {
        CTTcPr tcpr = this.ctTc.isSetTcPr() ? this.ctTc.getTcPr() : this.ctTc.addNewTcPr();
        CTVerticalJc va = tcpr.addNewVAlign();
        va.setVal(alignMap.get(vAlign));
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (!isCursorInTableCell(cursor)) {
            return null;
        }
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
        p22.dispose();
        cursor.toEndToken();
        return newP;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (isCursorInTableCell(cursor)) {
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
            cursor2.dispose();
            this.bodyElements.add(i, newT);
            XmlCursor cursor22 = t.newCursor();
            cursor.toCursor(cursor22);
            cursor.toEndToken();
            cursor22.dispose();
            return newT;
        }
        return null;
    }

    private boolean isCursorInTableCell(XmlCursor cursor) {
        XmlCursor verify = cursor.newCursor();
        verify.toParent();
        boolean result = verify.getObject() == this.ctTc;
        verify.dispose();
        return result;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph getParagraphArray(int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public POIXMLDocumentPart getPart() {
        return this.tableRow.getTable().getPart();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public BodyType getPartType() {
        return BodyType.TABLECELL;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTable(CTTbl ctTable) {
        for (int i = 0; i < this.tables.size(); i++) {
            if (getTables().get(i).getCTTbl() == ctTable) {
                return getTables().get(i);
            }
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable getTableArray(int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList(this.tables);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public void insertTable(int pos, XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        CTTbl[] arr$ = this.ctTc.getTblArray();
        for (CTTbl tbl : arr$) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            i++;
        }
        this.tables.add(i, table);
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (XWPFParagraph p : this.paragraphs) {
            text.append(p.getText());
        }
        return text.toString();
    }

    public void setText(String text) {
        CTP ctP = this.ctTc.sizeOfPArray() == 0 ? this.ctTc.addNewP() : this.ctTc.getPArray(0);
        XWPFParagraph par = new XWPFParagraph(ctP, this);
        par.createRun().setText(text);
    }

    public String getTextRecursively() {
        StringBuffer text = new StringBuffer();
        for (int i = 0; i < this.bodyElements.size(); i++) {
            boolean z = true;
            if (i != this.bodyElements.size() - 1) {
                z = false;
            }
            boolean isLast = z;
            appendBodyElementText(text, this.bodyElements.get(i), isLast);
        }
        return text.toString();
    }

    private void appendBodyElementText(StringBuffer text, IBodyElement e, boolean isLast) {
        if (e instanceof XWPFParagraph) {
            text.append(((XWPFParagraph) e).getText());
            if (!isLast) {
                text.append('\t');
                return;
            }
            return;
        }
        if (e instanceof XWPFTable) {
            XWPFTable eTable = (XWPFTable) e;
            for (XWPFTableRow row : eTable.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    List<IBodyElement> localBodyElements = cell.getBodyElements();
                    for (int i = 0; i < localBodyElements.size(); i++) {
                        boolean z = true;
                        if (i != localBodyElements.size() - 1) {
                            z = false;
                        }
                        boolean localIsLast = z;
                        appendBodyElementText(text, localBodyElements.get(i), localIsLast);
                    }
                }
            }
            if (!isLast) {
                text.append('\n');
                return;
            }
            return;
        }
        if (e instanceof XWPFSDT) {
            text.append(((XWPFSDT) e).getContent().getText());
            if (!isLast) {
                text.append('\t');
            }
        }
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTableCell getTableCell(CTTc cell) {
        XWPFTableRow tr;
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
        if (table == null || (tr = table.getRow(row)) == null) {
            return null;
        }
        return tr.getTableCell(cell);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFDocument getXWPFDocument() {
        return this.part.getXWPFDocument();
    }
}
