package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.util.Internal;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

/* JADX INFO: loaded from: classes.dex */
public class XSSFClientAnchor extends XSSFAnchor implements ClientAnchor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final CTMarker EMPTY_MARKER = CTMarker.Factory.newInstance();
    private ClientAnchor.AnchorType anchorType;
    private CTMarker cell1;
    private CTMarker cell2;
    private CTPoint2D position;
    private XSSFSheet sheet;
    private CTPositiveSize2D size;

    public XSSFClientAnchor() {
        this(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public XSSFClientAnchor(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE;
        CTMarker cTMarkerNewInstance = CTMarker.Factory.newInstance();
        this.cell1 = cTMarkerNewInstance;
        cTMarkerNewInstance.setCol(col1);
        this.cell1.setColOff(dx1);
        this.cell1.setRow(row1);
        this.cell1.setRowOff(dy1);
        CTMarker cTMarkerNewInstance2 = CTMarker.Factory.newInstance();
        this.cell2 = cTMarkerNewInstance2;
        cTMarkerNewInstance2.setCol(col2);
        this.cell2.setColOff(dx2);
        this.cell2.setRow(row2);
        this.cell2.setRowOff(dy2);
    }

    protected XSSFClientAnchor(CTMarker cell1, CTMarker cell2) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE;
        this.cell1 = cell1;
        this.cell2 = cell2;
    }

    protected XSSFClientAnchor(XSSFSheet sheet, CTMarker cell1, CTPositiveSize2D size) {
        this.anchorType = ClientAnchor.AnchorType.MOVE_DONT_RESIZE;
        this.sheet = sheet;
        this.size = size;
        this.cell1 = cell1;
    }

    protected XSSFClientAnchor(XSSFSheet sheet, CTPoint2D position, CTPositiveSize2D size) {
        this.anchorType = ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE;
        this.sheet = sheet;
        this.position = position;
        this.size = size;
    }

    private CTMarker calcCell(CTMarker cell, long w, long h) {
        CTMarker c2 = CTMarker.Factory.newInstance();
        int r = cell.getRow();
        int c = cell.getCol();
        int cw = Units.columnWidthToEMU(this.sheet.getColumnWidth(c));
        long wPos = ((long) cw) - cell.getColOff();
        while (wPos < w) {
            c++;
            cw = Units.columnWidthToEMU(this.sheet.getColumnWidth(c));
            wPos += (long) cw;
        }
        c2.setCol(c);
        c2.setColOff(((long) cw) - (wPos - w));
        int rh = Units.toEMU(getRowHeight(this.sheet, r));
        long hPos = ((long) rh) - cell.getRowOff();
        while (hPos < h) {
            r++;
            rh = Units.toEMU(getRowHeight(this.sheet, r));
            hPos += (long) rh;
        }
        c2.setRow(r);
        c2.setRowOff(((long) rh) - (hPos - h));
        return c2;
    }

    private static float getRowHeight(XSSFSheet sheet, int row) {
        XSSFRow r = sheet.getRow(row);
        return r == null ? sheet.getDefaultRowHeightInPoints() : r.getHeightInPoints();
    }

    private CTMarker getCell1() {
        CTMarker cTMarker = this.cell1;
        return cTMarker != null ? cTMarker : calcCell(EMPTY_MARKER, this.position.getX(), this.position.getY());
    }

    private CTMarker getCell2() {
        CTMarker cTMarker = this.cell2;
        return cTMarker != null ? cTMarker : calcCell(getCell1(), this.size.getCx(), this.size.getCy());
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public short getCol1() {
        return (short) getCell1().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setCol1(int col1) {
        this.cell1.setCol(col1);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public short getCol2() {
        return (short) getCell2().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setCol2(int col2) {
        this.cell2.setCol(col2);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public int getRow1() {
        return getCell1().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setRow1(int row1) {
        this.cell1.setRow(row1);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public int getRow2() {
        return getCell2().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setRow2(int row2) {
        this.cell2.setRow(row2);
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDx1() {
        return (int) getCell1().getColOff();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDx1(int dx1) {
        this.cell1.setColOff(dx1);
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDy1() {
        return (int) getCell1().getRowOff();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDy1(int dy1) {
        this.cell1.setRowOff(dy1);
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDy2() {
        return (int) getCell2().getRowOff();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDy2(int dy2) {
        this.cell2.setRowOff(dy2);
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDx2() {
        return (int) getCell2().getColOff();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDx2(int dx2) {
        this.cell2.setColOff(dx2);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof XSSFClientAnchor)) {
            return false;
        }
        XSSFClientAnchor anchor = (XSSFClientAnchor) o;
        return getDx1() == anchor.getDx1() && getDx2() == anchor.getDx2() && getDy1() == anchor.getDy1() && getDy2() == anchor.getDy2() && getCol1() == anchor.getCol1() && getCol2() == anchor.getCol2() && getRow1() == anchor.getRow1() && getRow2() == anchor.getRow2();
    }

    public int hashCode() {
        throw new AssertionError("hashCode not designed");
    }

    public String toString() {
        return "from : " + getCell1() + "; to: " + getCell2();
    }

    @Internal
    public CTMarker getFrom() {
        return getCell1();
    }

    protected void setFrom(CTMarker from) {
        this.cell1 = from;
    }

    @Internal
    public CTMarker getTo() {
        return getCell2();
    }

    protected void setTo(CTMarker to) {
        this.cell2 = to;
    }

    public CTPoint2D getPosition() {
        return this.position;
    }

    public void setPosition(CTPoint2D position) {
        this.position = position;
    }

    public CTPositiveSize2D getSize() {
        return this.size;
    }

    public void setSize(CTPositiveSize2D size) {
        this.size = size;
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setAnchorType(ClientAnchor.AnchorType anchorType) {
        this.anchorType = anchorType;
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public ClientAnchor.AnchorType getAnchorType() {
        return this.anchorType;
    }

    public boolean isSet() {
        CTMarker c1 = getCell1();
        CTMarker c2 = getCell2();
        return (c1.getCol() == 0 && c2.getCol() == 0 && c1.getRow() == 0 && c2.getRow() == 0) ? false : true;
    }
}
