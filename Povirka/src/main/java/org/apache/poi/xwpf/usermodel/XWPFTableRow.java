package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.Internal;
import org.apache.poi.xwpf.model.WMLHelper;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;

/* JADX INFO: loaded from: classes.dex */
public class XWPFTableRow {
    private CTRow ctRow;
    private XWPFTable table;
    private List<XWPFTableCell> tableCells;

    public XWPFTableRow(CTRow row, XWPFTable table) {
        this.table = table;
        this.ctRow = row;
        getTableCells();
    }

    @Internal
    public CTRow getCtRow() {
        return this.ctRow;
    }

    public XWPFTableCell createCell() {
        XWPFTableCell tableCell = new XWPFTableCell(this.ctRow.addNewTc(), this, this.table.getBody());
        this.tableCells.add(tableCell);
        return tableCell;
    }

    public XWPFTableCell getCell(int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            return getTableCells().get(pos);
        }
        return null;
    }

    public void removeCell(int pos) {
        if (pos >= 0 && pos < this.ctRow.sizeOfTcArray()) {
            this.tableCells.remove(pos);
        }
    }

    public XWPFTableCell addNewTableCell() {
        CTTc cell = this.ctRow.addNewTc();
        XWPFTableCell tableCell = new XWPFTableCell(cell, this, this.table.getBody());
        this.tableCells.add(tableCell);
        return tableCell;
    }

    public int getHeight() {
        CTTrPr properties = getTrPr();
        if (properties.sizeOfTrHeightArray() == 0) {
            return 0;
        }
        return properties.getTrHeightArray(0).getVal().intValue();
    }

    public void setHeight(int height) {
        CTTrPr properties = getTrPr();
        CTHeight h = properties.sizeOfTrHeightArray() == 0 ? properties.addNewTrHeight() : properties.getTrHeightArray(0);
        h.setVal(new BigInteger("" + height));
    }

    private CTTrPr getTrPr() {
        return this.ctRow.isSetTrPr() ? this.ctRow.getTrPr() : this.ctRow.addNewTrPr();
    }

    public XWPFTable getTable() {
        return this.table;
    }

    public List<ICell> getTableICells() {
        List<ICell> cells = new ArrayList<>();
        XmlCursor cursor = this.ctRow.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            CTSdtCell object = cursor.getObject();
            if (object instanceof CTTc) {
                cells.add(new XWPFTableCell((CTTc) object, this, this.table.getBody()));
            } else if (object instanceof CTSdtCell) {
                cells.add(new XWPFSDTCell(object, this, this.table.getBody()));
            }
        }
        cursor.dispose();
        return cells;
    }

    public List<XWPFTableCell> getTableCells() {
        if (this.tableCells == null) {
            List<XWPFTableCell> cells = new ArrayList<>();
            CTTc[] arr$ = this.ctRow.getTcArray();
            for (CTTc tableCell : arr$) {
                cells.add(new XWPFTableCell(tableCell, this, this.table.getBody()));
            }
            this.tableCells = cells;
        }
        return this.tableCells;
    }

    public XWPFTableCell getTableCell(CTTc cell) {
        for (int i = 0; i < this.tableCells.size(); i++) {
            if (this.tableCells.get(i).getCTTc() == cell) {
                return this.tableCells.get(i);
            }
        }
        return null;
    }

    public boolean isCantSplitRow() {
        if (!this.ctRow.isSetTrPr()) {
            return false;
        }
        CTTrPr trpr = getTrPr();
        if (trpr.sizeOfCantSplitArray() <= 0) {
            return false;
        }
        CTOnOff onoff = trpr.getCantSplitArray(0);
        boolean isCant = onoff.isSetVal() ? WMLHelper.convertSTOnOffToBoolean(onoff.getVal()) : true;
        return isCant;
    }

    public void setCantSplitRow(boolean split) {
        CTTrPr trpr = getTrPr();
        CTOnOff onoff = trpr.sizeOfCantSplitArray() > 0 ? trpr.getCantSplitArray(0) : trpr.addNewCantSplit();
        onoff.setVal(WMLHelper.convertBooleanToSTOnOff(split));
    }

    public boolean isRepeatHeader() {
        boolean repeat = false;
        for (XWPFTableRow row : this.table.getRows()) {
            repeat = row.getRepeat();
            if (row == this || !repeat) {
                break;
            }
        }
        return repeat;
    }

    private boolean getRepeat() {
        if (!this.ctRow.isSetTrPr()) {
            return false;
        }
        CTTrPr trpr = getTrPr();
        if (trpr.sizeOfTblHeaderArray() <= 0) {
            return false;
        }
        CTOnOff rpt = trpr.getTblHeaderArray(0);
        boolean repeat = rpt.isSetVal() ? WMLHelper.convertSTOnOffToBoolean(rpt.getVal()) : true;
        return repeat;
    }

    public void setRepeatHeader(boolean repeat) {
        CTTrPr trpr = getTrPr();
        CTOnOff onoff = trpr.sizeOfTblHeaderArray() > 0 ? trpr.getTblHeaderArray(0) : trpr.addNewTblHeader();
        onoff.setVal(WMLHelper.convertBooleanToSTOnOff(repeat));
    }
}
