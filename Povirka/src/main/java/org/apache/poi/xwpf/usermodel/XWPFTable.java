package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblCellMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

/* JADX INFO: loaded from: classes.dex */
public class XWPFTable implements IBodyElement, ISDTContents {
    private static HashMap<Integer, XWPFBorderType> stBorderTypeMap;
    private static EnumMap<XWPFBorderType, STBorder.Enum> xwpfBorderTypeMap;
    private CTTbl ctTbl;
    protected IBody part;
    protected List<XWPFTableRow> tableRows;
    protected StringBuffer text;

    public enum XWPFBorderType {
        NIL,
        NONE,
        SINGLE,
        THICK,
        DOUBLE,
        DOTTED,
        DASHED,
        DOT_DASH
    }

    static {
        EnumMap<XWPFBorderType, STBorder.Enum> enumMap = new EnumMap<>(XWPFBorderType.class);
        xwpfBorderTypeMap = enumMap;
        enumMap.put(XWPFBorderType.NIL, STBorder.Enum.forInt(1));
        xwpfBorderTypeMap.put(XWPFBorderType.NONE, STBorder.Enum.forInt(2));
        xwpfBorderTypeMap.put(XWPFBorderType.SINGLE, STBorder.Enum.forInt(3));
        xwpfBorderTypeMap.put(XWPFBorderType.THICK, STBorder.Enum.forInt(4));
        xwpfBorderTypeMap.put(XWPFBorderType.DOUBLE, STBorder.Enum.forInt(5));
        xwpfBorderTypeMap.put(XWPFBorderType.DOTTED, STBorder.Enum.forInt(6));
        xwpfBorderTypeMap.put(XWPFBorderType.DASHED, STBorder.Enum.forInt(7));
        xwpfBorderTypeMap.put(XWPFBorderType.DOT_DASH, STBorder.Enum.forInt(8));
        HashMap<Integer, XWPFBorderType> map = new HashMap<>();
        stBorderTypeMap = map;
        map.put(1, XWPFBorderType.NIL);
        stBorderTypeMap.put(2, XWPFBorderType.NONE);
        stBorderTypeMap.put(3, XWPFBorderType.SINGLE);
        stBorderTypeMap.put(4, XWPFBorderType.THICK);
        stBorderTypeMap.put(5, XWPFBorderType.DOUBLE);
        stBorderTypeMap.put(6, XWPFBorderType.DOTTED);
        stBorderTypeMap.put(7, XWPFBorderType.DASHED);
        stBorderTypeMap.put(8, XWPFBorderType.DOT_DASH);
    }

    public XWPFTable(CTTbl table, IBody part, int row, int col) {
        this(table, part);
        for (int i = 0; i < row; i++) {
            XWPFTableRow tabRow = getRow(i) == null ? createRow() : getRow(i);
            for (int k = 0; k < col; k++) {
                if (tabRow.getCell(k) == null) {
                    tabRow.createCell();
                }
            }
        }
    }

    public XWPFTable(CTTbl table, IBody part) {
        IBody iBody = part;
        this.text = new StringBuffer();
        this.part = iBody;
        this.ctTbl = table;
        this.tableRows = new ArrayList();
        if (table.sizeOfTrArray() == 0) {
            createEmptyTable(table);
        }
        CTRow[] arr$ = table.getTrArray();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            CTRow row = arr$[i$];
            StringBuilder rowText = new StringBuilder();
            XWPFTableRow tabRow = new XWPFTableRow(row, this);
            this.tableRows.add(tabRow);
            CTTc[] arr$2 = row.getTcArray();
            int len$2 = arr$2.length;
            int i$2 = 0;
            while (i$2 < len$2) {
                CTTc cell = arr$2[i$2];
                CTP[] arr$3 = cell.getPArray();
                int len$3 = arr$3.length;
                int i$3 = 0;
                while (i$3 < len$3) {
                    CTP ctp = arr$3[i$3];
                    CTRow[] arr$4 = arr$;
                    XWPFParagraph p = new XWPFParagraph(ctp, iBody);
                    if (rowText.length() > 0) {
                        rowText.append('\t');
                    }
                    rowText.append(p.getText());
                    i$3++;
                    iBody = part;
                    arr$ = arr$4;
                }
                i$2++;
                iBody = part;
            }
            CTRow[] arr$5 = arr$;
            if (rowText.length() > 0) {
                this.text.append((CharSequence) rowText);
                this.text.append('\n');
            }
            i$++;
            iBody = part;
            arr$ = arr$5;
        }
    }

    private void createEmptyTable(CTTbl table) {
        table.addNewTr().addNewTc().addNewP();
        CTTblPr tblpro = table.addNewTblPr();
        tblpro.addNewTblW().setW(new BigInteger("0"));
        tblpro.getTblW().setType(STTblWidth.AUTO);
        CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewInsideH().setVal(STBorder.SINGLE);
        borders.addNewInsideV().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);
        getRows();
    }

    @Internal
    public CTTbl getCTTbl() {
        return this.ctTbl;
    }

    public String getText() {
        return this.text.toString();
    }

    public void addNewRowBetween(int start, int end) {
    }

    public void addNewCol() {
        if (this.ctTbl.sizeOfTrArray() == 0) {
            createRow();
        }
        for (int i = 0; i < this.ctTbl.sizeOfTrArray(); i++) {
            XWPFTableRow tabRow = new XWPFTableRow(this.ctTbl.getTrArray(i), this);
            tabRow.createCell();
        }
    }

    public XWPFTableRow createRow() {
        int sizeCol = this.ctTbl.sizeOfTrArray() > 0 ? this.ctTbl.getTrArray(0).sizeOfTcArray() : 0;
        XWPFTableRow tabRow = new XWPFTableRow(this.ctTbl.addNewTr(), this);
        addColumn(tabRow, sizeCol);
        this.tableRows.add(tabRow);
        return tabRow;
    }

    public XWPFTableRow getRow(int pos) {
        if (pos >= 0 && pos < this.ctTbl.sizeOfTrArray()) {
            return getRows().get(pos);
        }
        return null;
    }

    public int getWidth() {
        CTTblPr tblPr = getTrPr();
        if (tblPr.isSetTblW()) {
            return tblPr.getTblW().getW().intValue();
        }
        return -1;
    }

    public void setWidth(int width) {
        CTTblPr tblPr = getTrPr();
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(new BigInteger("" + width));
    }

    public int getNumberOfRows() {
        return this.ctTbl.sizeOfTrArray();
    }

    private CTTblPr getTrPr() {
        return this.ctTbl.getTblPr() != null ? this.ctTbl.getTblPr() : this.ctTbl.addNewTblPr();
    }

    private void addColumn(XWPFTableRow tabRow, int sizeCol) {
        if (sizeCol > 0) {
            for (int i = 0; i < sizeCol; i++) {
                tabRow.createCell();
            }
        }
    }

    public String getStyleID() {
        CTString styleStr;
        CTTblPr tblPr = this.ctTbl.getTblPr();
        if (tblPr == null || (styleStr = tblPr.getTblStyle()) == null) {
            return null;
        }
        String styleId = styleStr.getVal();
        return styleId;
    }

    public void setStyleID(String styleName) {
        CTTblPr tblPr = getTrPr();
        CTString styleStr = tblPr.getTblStyle();
        if (styleStr == null) {
            styleStr = tblPr.addNewTblStyle();
        }
        styleStr.setVal(styleName);
    }

    public XWPFBorderType getInsideHBorderType() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return null;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideH()) {
            return null;
        }
        CTBorder border = ctb.getInsideH();
        XWPFBorderType bt = stBorderTypeMap.get(Integer.valueOf(border.getVal().intValue()));
        return bt;
    }

    public int getInsideHBorderSize() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return -1;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideH()) {
            return -1;
        }
        CTBorder border = ctb.getInsideH();
        int size = border.getSz().intValue();
        return size;
    }

    public int getInsideHBorderSpace() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return -1;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideH()) {
            return -1;
        }
        CTBorder border = ctb.getInsideH();
        int space = border.getSpace().intValue();
        return space;
    }

    public String getInsideHBorderColor() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return null;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideH()) {
            return null;
        }
        CTBorder border = ctb.getInsideH();
        String color = border.xgetColor().getStringValue();
        return color;
    }

    public XWPFBorderType getInsideVBorderType() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return null;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideV()) {
            return null;
        }
        CTBorder border = ctb.getInsideV();
        XWPFBorderType bt = stBorderTypeMap.get(Integer.valueOf(border.getVal().intValue()));
        return bt;
    }

    public int getInsideVBorderSize() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return -1;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideV()) {
            return -1;
        }
        CTBorder border = ctb.getInsideV();
        int size = border.getSz().intValue();
        return size;
    }

    public int getInsideVBorderSpace() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return -1;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideV()) {
            return -1;
        }
        CTBorder border = ctb.getInsideV();
        int space = border.getSpace().intValue();
        return space;
    }

    public String getInsideVBorderColor() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblBorders()) {
            return null;
        }
        CTTblBorders ctb = tblPr.getTblBorders();
        if (!ctb.isSetInsideV()) {
            return null;
        }
        CTBorder border = ctb.getInsideV();
        String color = border.xgetColor().getStringValue();
        return color;
    }

    public int getRowBandSize() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblStyleRowBandSize()) {
            return 0;
        }
        CTDecimalNumber rowSize = tblPr.getTblStyleRowBandSize();
        int size = rowSize.getVal().intValue();
        return size;
    }

    public void setRowBandSize(int size) {
        CTTblPr tblPr = getTrPr();
        CTDecimalNumber rowSize = tblPr.isSetTblStyleRowBandSize() ? tblPr.getTblStyleRowBandSize() : tblPr.addNewTblStyleRowBandSize();
        rowSize.setVal(BigInteger.valueOf(size));
    }

    public int getColBandSize() {
        CTTblPr tblPr = getTrPr();
        if (!tblPr.isSetTblStyleColBandSize()) {
            return 0;
        }
        CTDecimalNumber colSize = tblPr.getTblStyleColBandSize();
        int size = colSize.getVal().intValue();
        return size;
    }

    public void setColBandSize(int size) {
        CTTblPr tblPr = getTrPr();
        CTDecimalNumber colSize = tblPr.isSetTblStyleColBandSize() ? tblPr.getTblStyleColBandSize() : tblPr.addNewTblStyleColBandSize();
        colSize.setVal(BigInteger.valueOf(size));
    }

    public void setInsideHBorder(XWPFBorderType type, int size, int space, String rgbColor) {
        CTTblPr tblPr = getTrPr();
        CTTblBorders ctb = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        CTBorder b = ctb.isSetInsideH() ? ctb.getInsideH() : ctb.addNewInsideH();
        b.setVal(xwpfBorderTypeMap.get(type));
        b.setSz(BigInteger.valueOf(size));
        b.setSpace(BigInteger.valueOf(space));
        b.setColor(rgbColor);
    }

    public void setInsideVBorder(XWPFBorderType type, int size, int space, String rgbColor) {
        CTTblPr tblPr = getTrPr();
        CTTblBorders ctb = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        CTBorder b = ctb.isSetInsideV() ? ctb.getInsideV() : ctb.addNewInsideV();
        b.setVal(xwpfBorderTypeMap.get(type));
        b.setSz(BigInteger.valueOf(size));
        b.setSpace(BigInteger.valueOf(space));
        b.setColor(rgbColor);
    }

    public int getCellMarginTop() {
        CTTblWidth tw;
        CTTblPr tblPr = getTrPr();
        CTTblCellMar tcm = tblPr.getTblCellMar();
        if (tcm == null || (tw = tcm.getTop()) == null) {
            return 0;
        }
        int margin = tw.getW().intValue();
        return margin;
    }

    public int getCellMarginLeft() {
        CTTblWidth tw;
        CTTblPr tblPr = getTrPr();
        CTTblCellMar tcm = tblPr.getTblCellMar();
        if (tcm == null || (tw = tcm.getLeft()) == null) {
            return 0;
        }
        int margin = tw.getW().intValue();
        return margin;
    }

    public int getCellMarginBottom() {
        CTTblWidth tw;
        CTTblPr tblPr = getTrPr();
        CTTblCellMar tcm = tblPr.getTblCellMar();
        if (tcm == null || (tw = tcm.getBottom()) == null) {
            return 0;
        }
        int margin = tw.getW().intValue();
        return margin;
    }

    public int getCellMarginRight() {
        CTTblWidth tw;
        CTTblPr tblPr = getTrPr();
        CTTblCellMar tcm = tblPr.getTblCellMar();
        if (tcm == null || (tw = tcm.getRight()) == null) {
            return 0;
        }
        int margin = tw.getW().intValue();
        return margin;
    }

    public void setCellMargins(int top, int left, int bottom, int right) {
        CTTblPr tblPr = getTrPr();
        CTTblCellMar tcm = tblPr.isSetTblCellMar() ? tblPr.getTblCellMar() : tblPr.addNewTblCellMar();
        CTTblWidth tw = tcm.isSetLeft() ? tcm.getLeft() : tcm.addNewLeft();
        tw.setType(STTblWidth.DXA);
        tw.setW(BigInteger.valueOf(left));
        CTTblWidth tw2 = tcm.isSetTop() ? tcm.getTop() : tcm.addNewTop();
        tw2.setType(STTblWidth.DXA);
        tw2.setW(BigInteger.valueOf(top));
        CTTblWidth tw3 = tcm.isSetBottom() ? tcm.getBottom() : tcm.addNewBottom();
        tw3.setType(STTblWidth.DXA);
        tw3.setW(BigInteger.valueOf(bottom));
        CTTblWidth tw4 = tcm.isSetRight() ? tcm.getRight() : tcm.addNewRight();
        tw4.setType(STTblWidth.DXA);
        tw4.setW(BigInteger.valueOf(right));
    }

    public void addRow(XWPFTableRow row) {
        this.ctTbl.addNewTr();
        this.ctTbl.setTrArray(getNumberOfRows() - 1, row.getCtRow());
        this.tableRows.add(row);
    }

    public boolean addRow(XWPFTableRow row, int pos) {
        if (pos >= 0 && pos <= this.tableRows.size()) {
            this.ctTbl.insertNewTr(pos);
            this.ctTbl.setTrArray(pos, row.getCtRow());
            this.tableRows.add(pos, row);
            return true;
        }
        return false;
    }

    public XWPFTableRow insertNewTableRow(int pos) {
        if (pos >= 0 && pos <= this.tableRows.size()) {
            CTRow row = this.ctTbl.insertNewTr(pos);
            XWPFTableRow tableRow = new XWPFTableRow(row, this);
            this.tableRows.add(pos, tableRow);
            return tableRow;
        }
        return null;
    }

    public boolean removeRow(int pos) throws IndexOutOfBoundsException {
        if (pos >= 0 && pos < this.tableRows.size()) {
            if (this.ctTbl.sizeOfTrArray() > 0) {
                this.ctTbl.removeTr(pos);
            }
            this.tableRows.remove(pos);
            return true;
        }
        return false;
    }

    public List<XWPFTableRow> getRows() {
        return this.tableRows;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBodyElement
    public BodyElementType getElementType() {
        return BodyElementType.TABLE;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBodyElement
    public IBody getBody() {
        return this.part;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBodyElement, org.apache.poi.xwpf.usermodel.IRunBody
    public POIXMLDocumentPart getPart() {
        IBody iBody = this.part;
        if (iBody != null) {
            return iBody.getPart();
        }
        return null;
    }

    @Override // org.apache.poi.xwpf.usermodel.IBodyElement
    public BodyType getPartType() {
        return this.part.getPartType();
    }

    public XWPFTableRow getRow(CTRow row) {
        for (int i = 0; i < getRows().size(); i++) {
            if (getRows().get(i).getCtRow() == row) {
                return getRow(i);
            }
        }
        return null;
    }
}
