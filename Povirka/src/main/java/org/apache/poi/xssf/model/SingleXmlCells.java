package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SingleXmlCellsDocument;

/* JADX INFO: loaded from: classes.dex */
public class SingleXmlCells extends POIXMLDocumentPart {
    private CTSingleXmlCells singleXMLCells;

    public SingleXmlCells() {
        this.singleXMLCells = CTSingleXmlCells.Factory.newInstance();
    }

    public SingleXmlCells(PackagePart part) throws IOException {
        super(part);
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            SingleXmlCellsDocument doc = SingleXmlCellsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.singleXMLCells = doc.getSingleXmlCells();
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public XSSFSheet getXSSFSheet() {
        return (XSSFSheet) getParent();
    }

    protected void writeTo(OutputStream out) throws IOException {
        SingleXmlCellsDocument doc = SingleXmlCellsDocument.Factory.newInstance();
        doc.setSingleXmlCells(this.singleXMLCells);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }

    public CTSingleXmlCells getCTSingleXMLCells() {
        return this.singleXMLCells;
    }

    public List<XSSFSingleXmlCell> getAllSimpleXmlCell() {
        List<XSSFSingleXmlCell> list = new Vector<>();
        CTSingleXmlCell[] arr$ = this.singleXMLCells.getSingleXmlCellArray();
        for (CTSingleXmlCell singleXmlCell : arr$) {
            list.add(new XSSFSingleXmlCell(singleXmlCell, this));
        }
        return list;
    }
}
