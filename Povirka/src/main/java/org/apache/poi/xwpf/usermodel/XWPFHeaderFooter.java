package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;

/* JADX INFO: loaded from: classes.dex */
public abstract class XWPFHeaderFooter extends POIXMLDocumentPart implements IBody {
    List<IBodyElement> bodyElements;
    XWPFDocument document;
    CTHdrFtr headerFooter;
    List<XWPFParagraph> paragraphs;
    List<XWPFPictureData> pictures;
    List<XWPFTable> tables;

    XWPFHeaderFooter(XWPFDocument doc, CTHdrFtr hdrFtr) {
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.pictures = new ArrayList();
        this.bodyElements = new ArrayList();
        if (doc == null) {
            throw new NullPointerException();
        }
        this.document = doc;
        this.headerFooter = hdrFtr;
        readHdrFtr();
    }

    protected XWPFHeaderFooter() {
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.pictures = new ArrayList();
        this.bodyElements = new ArrayList();
        this.headerFooter = CTHdrFtr.Factory.newInstance();
        readHdrFtr();
    }

    public XWPFHeaderFooter(POIXMLDocumentPart parent, PackagePart part) throws IOException {
        super(parent, part);
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        this.pictures = new ArrayList();
        this.bodyElements = new ArrayList();
        XWPFDocument xWPFDocument = (XWPFDocument) getParent();
        this.document = xWPFDocument;
        if (xWPFDocument == null) {
            throw new NullPointerException();
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() throws IOException {
        for (POIXMLDocumentPart poixmlDocumentPart : getRelations()) {
            if (poixmlDocumentPart instanceof XWPFPictureData) {
                XWPFPictureData xwpfPicData = (XWPFPictureData) poixmlDocumentPart;
                this.pictures.add(xwpfPicData);
                this.document.registerPackagePictureData(xwpfPicData);
            }
        }
    }

    @Internal
    public CTHdrFtr _getHdrFtr() {
        return this.headerFooter;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList(this.bodyElements);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList(this.paragraphs);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public List<XWPFTable> getTables() throws ArrayIndexOutOfBoundsException {
        return Collections.unmodifiableList(this.tables);
    }

    public String getText() {
        String text;
        StringBuffer t = new StringBuffer();
        for (int i = 0; i < this.paragraphs.size(); i++) {
            if (!this.paragraphs.get(i).isEmpty() && (text = this.paragraphs.get(i).getText()) != null && text.length() > 0) {
                t.append(text);
                t.append('\n');
            }
        }
        for (int i2 = 0; i2 < this.tables.size(); i2++) {
            String text2 = this.tables.get(i2).getText();
            if (text2 != null && text2.length() > 0) {
                t.append(text2);
                t.append('\n');
            }
        }
        for (IBodyElement bodyElement : getBodyElements()) {
            if (bodyElement instanceof XWPFSDT) {
                t.append(((XWPFSDT) bodyElement).getContent().getText() + '\n');
            }
        }
        return t.toString();
    }

    public void setHeaderFooter(CTHdrFtr headerFooter) {
        this.headerFooter = headerFooter;
        readHdrFtr();
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

    public List<XWPFParagraph> getListParagraph() {
        return this.paragraphs;
    }

    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList(this.pictures);
    }

    public List<XWPFPictureData> getAllPackagePictures() {
        return this.document.getAllPackagePictures();
    }

    public String addPictureData(byte[] pictureData, int format) throws InvalidFormatException {
        XWPFPictureData xwpfPicData = this.document.findPackagePictureData(pictureData, format);
        POIXMLRelation relDesc = XWPFPictureData.RELATIONS[format];
        if (xwpfPicData == null) {
            int idx = this.document.getNextPicNameNumber(format);
            XWPFPictureData xwpfPicData2 = (XWPFPictureData) createRelationship(relDesc, XWPFFactory.getInstance(), idx);
            PackagePart picDataPart = xwpfPicData2.getPackagePart();
            OutputStream out = null;
            try {
                try {
                    out = picDataPart.getOutputStream();
                    out.write(pictureData);
                    this.document.registerPackagePictureData(xwpfPicData2);
                    this.pictures.add(xwpfPicData2);
                    return getRelationId(xwpfPicData2);
                } catch (IOException e) {
                    throw new POIXMLException(e);
                }
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e2) {
                    }
                }
            }
        }
        if (!getRelations().contains(xwpfPicData)) {
            POIXMLDocumentPart.RelationPart rp = addRelation(null, XWPFRelation.IMAGES, xwpfPicData);
            this.pictures.add(xwpfPicData);
            return rp.getRelationship().getId();
        }
        return getRelationId(xwpfPicData);
    }

    public String addPictureData(InputStream is, int format) throws InvalidFormatException, IOException {
        byte[] data = IOUtils.toByteArray(is);
        return addPictureData(data, format);
    }

    public XWPFPictureData getPictureDataByID(String blipID) {
        POIXMLDocumentPart relatedPart = getRelationById(blipID);
        if (relatedPart != null && (relatedPart instanceof XWPFPictureData)) {
            return (XWPFPictureData) relatedPart;
        }
        return null;
    }

    public XWPFParagraph createParagraph() {
        XWPFParagraph paragraph = new XWPFParagraph(this.headerFooter.addNewP(), this);
        this.paragraphs.add(paragraph);
        this.bodyElements.add(paragraph);
        return paragraph;
    }

    public XWPFTable createTable(int rows, int cols) {
        XWPFTable table = new XWPFTable(this.headerFooter.addNewTbl(), this, rows, cols);
        this.tables.add(table);
        this.bodyElements.add(table);
        return table;
    }

    public void removeParagraph(XWPFParagraph paragraph) {
        if (this.paragraphs.contains(paragraph)) {
            CTP ctP = paragraph.getCTP();
            XmlCursor c = ctP.newCursor();
            c.removeXml();
            c.dispose();
            this.paragraphs.remove(paragraph);
            this.bodyElements.remove(paragraph);
        }
    }

    public void removeTable(XWPFTable table) {
        if (this.tables.contains(table)) {
            CTTbl ctTbl = table.getCTTbl();
            XmlCursor c = ctTbl.newCursor();
            c.removeXml();
            c.dispose();
            this.tables.remove(table);
            this.bodyElements.remove(table);
        }
    }

    public void clearHeaderFooter() {
        XmlCursor c = this.headerFooter.newCursor();
        c.removeXmlContents();
        c.dispose();
        this.paragraphs.clear();
        this.tables.clear();
        this.bodyElements.clear();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFParagraph insertNewParagraph(XmlCursor cursor) {
        if (isCursorInHdrF(cursor)) {
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

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (isCursorInHdrF(cursor)) {
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

    private boolean isCursorInHdrF(XmlCursor cursor) {
        XmlCursor verify = cursor.newCursor();
        verify.toParent();
        boolean result = verify.getObject() == this.headerFooter;
        verify.dispose();
        return result;
    }

    public POIXMLDocumentPart getOwner() {
        return this;
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
        CTTbl[] arr$ = this.headerFooter.getTblArray();
        for (CTTbl tbl : arr$) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            i++;
        }
        this.tables.add(i, table);
    }

    public void readHdrFtr() {
        this.bodyElements = new ArrayList();
        this.paragraphs = new ArrayList();
        this.tables = new ArrayList();
        XmlCursor cursor = this.headerFooter.newCursor();
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
        }
        cursor.dispose();
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFTableCell getTableCell(CTTc cell) {
        XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            cursor.dispose();
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
        if (table == null) {
            return null;
        }
        XWPFTableRow tableRow = table.getRow(row);
        return tableRow.getTableCell(cell);
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public XWPFDocument getXWPFDocument() {
        XWPFDocument xWPFDocument = this.document;
        if (xWPFDocument != null) {
            return xWPFDocument;
        }
        return (XWPFDocument) getParent();
    }

    public void setXWPFDocument(XWPFDocument doc) {
        this.document = doc;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBody
    public POIXMLDocumentPart getPart() {
        return this;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void prepareForCommit() {
        if (this.bodyElements.size() == 0) {
            createParagraph();
        }
        for (XWPFTable tbl : this.tables) {
            for (XWPFTableRow row : tbl.tableRows) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    if (cell.getBodyElements().size() == 0) {
                        cell.addParagraph();
                    }
                }
            }
        }
        super.prepareForCommit();
    }
}
