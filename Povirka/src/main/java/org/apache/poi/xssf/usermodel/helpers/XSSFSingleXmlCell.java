package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SingleXmlCells;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlCellPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFSingleXmlCell {
    private SingleXmlCells parent;
    private CTSingleXmlCell singleXmlCell;

    public XSSFSingleXmlCell(CTSingleXmlCell singleXmlCell, SingleXmlCells parent) {
        this.singleXmlCell = singleXmlCell;
        this.parent = parent;
    }

    public XSSFCell getReferencedCell() {
        CellReference cellReference = new CellReference(this.singleXmlCell.getR());
        XSSFRow row = this.parent.getXSSFSheet().getRow(cellReference.getRow());
        if (row == null) {
            row = this.parent.getXSSFSheet().createRow(cellReference.getRow());
        }
        XSSFCell cell = row.getCell((int) cellReference.getCol());
        if (cell == null) {
            return row.createCell((int) cellReference.getCol());
        }
        return cell;
    }

    public String getXpath() {
        CTXmlCellPr xmlCellPr = this.singleXmlCell.getXmlCellPr();
        CTXmlPr xmlPr = xmlCellPr.getXmlPr();
        String xpath = xmlPr.getXpath();
        return xpath;
    }

    public long getMapId() {
        return this.singleXmlCell.getXmlCellPr().getXmlPr().getMapId();
    }

    public STXmlDataType.Enum getXmlDataType() {
        CTXmlCellPr xmlCellPr = this.singleXmlCell.getXmlCellPr();
        CTXmlPr xmlPr = xmlCellPr.getXmlPr();
        return xmlPr.getXmlDataType();
    }
}
