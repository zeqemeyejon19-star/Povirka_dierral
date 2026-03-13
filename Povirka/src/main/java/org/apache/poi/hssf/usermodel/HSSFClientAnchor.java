package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.IEEEDouble;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFClientAnchor extends HSSFAnchor implements ClientAnchor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int MAX_COL = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
    public static final int MAX_ROW = SpreadsheetVersion.EXCEL97.getLastRowIndex();
    private EscherClientAnchorRecord _escherClientAnchor;

    public HSSFClientAnchor(EscherClientAnchorRecord escherClientAnchorRecord) {
        this._escherClientAnchor = escherClientAnchorRecord;
    }

    public HSSFClientAnchor() {
    }

    public HSSFClientAnchor(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2) {
        super(dx1, dy1, dx2, dy2);
        checkRange(dx1, 0, IEEEDouble.EXPONENT_BIAS, "dx1");
        checkRange(dx2, 0, IEEEDouble.EXPONENT_BIAS, "dx2");
        checkRange(dy1, 0, 255, "dy1");
        checkRange(dy2, 0, 255, "dy2");
        int i = MAX_COL;
        checkRange(col1, 0, i, "col1");
        checkRange(col2, 0, i, "col2");
        int i2 = MAX_ROW;
        checkRange(row1, 0, i2, "row1");
        checkRange(row2, 0, i2, "row2");
        setCol1((short) Math.min((int) col1, (int) col2));
        setCol2((short) Math.max((int) col1, (int) col2));
        setRow1(Math.min(row1, row2));
        setRow2(Math.max(row1, row2));
        if (col1 > col2) {
            this._isHorizontallyFlipped = true;
        }
        if (row1 > row2) {
            this._isVerticallyFlipped = true;
        }
    }

    public float getAnchorHeightInPoints(HSSFSheet sheet) {
        int y1 = getDy1();
        int y2 = getDy2();
        int row1 = Math.min(getRow1(), getRow2());
        int row2 = Math.max(getRow1(), getRow2());
        if (row1 == row2) {
            float points = ((y2 - y1) / 256.0f) * getRowHeightInPoints(sheet, row2);
            return points;
        }
        float points2 = y1;
        float points3 = 0.0f + (((256.0f - points2) / 256.0f) * getRowHeightInPoints(sheet, row1));
        for (int i = row1 + 1; i < row2; i++) {
            points3 += getRowHeightInPoints(sheet, i);
        }
        return ((y2 / 256.0f) * getRowHeightInPoints(sheet, row2)) + points3;
    }

    private float getRowHeightInPoints(HSSFSheet sheet, int rowNum) {
        HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            return sheet.getDefaultRowHeightInPoints();
        }
        return row.getHeightInPoints();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public short getCol1() {
        return this._escherClientAnchor.getCol1();
    }

    public void setCol1(short col1) {
        checkRange(col1, 0, MAX_COL, "col1");
        this._escherClientAnchor.setCol1(col1);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setCol1(int col1) {
        setCol1((short) col1);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public short getCol2() {
        return this._escherClientAnchor.getCol2();
    }

    public void setCol2(short col2) {
        checkRange(col2, 0, MAX_COL, "col2");
        this._escherClientAnchor.setCol2(col2);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setCol2(int col2) {
        setCol2((short) col2);
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public int getRow1() {
        return unsignedValue(this._escherClientAnchor.getRow1());
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setRow1(int row1) {
        checkRange(row1, 0, MAX_ROW, "row1");
        this._escherClientAnchor.setRow1(Integer.valueOf(row1).shortValue());
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public int getRow2() {
        return unsignedValue(this._escherClientAnchor.getRow2());
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setRow2(int row2) {
        checkRange(row2, 0, MAX_ROW, "row2");
        this._escherClientAnchor.setRow2(Integer.valueOf(row2).shortValue());
    }

    public void setAnchor(short col1, int row1, int x1, int y1, short col2, int row2, int x2, int y2) {
        checkRange(getDx1(), 0, IEEEDouble.EXPONENT_BIAS, "dx1");
        checkRange(getDx2(), 0, IEEEDouble.EXPONENT_BIAS, "dx2");
        checkRange(getDy1(), 0, 255, "dy1");
        checkRange(getDy2(), 0, 255, "dy2");
        short col12 = getCol1();
        int i = MAX_COL;
        checkRange(col12, 0, i, "col1");
        checkRange(getCol2(), 0, i, "col2");
        int row12 = getRow1();
        int i2 = MAX_ROW;
        checkRange(row12, 0, i2, "row1");
        checkRange(getRow2(), 0, i2, "row2");
        setCol1(col1);
        setRow1(row1);
        setDx1(x1);
        setDy1(y1);
        setCol2(col2);
        setRow2(row2);
        setDx2(x2);
        setDy2(y2);
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    public boolean isHorizontallyFlipped() {
        return this._isHorizontallyFlipped;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    public boolean isVerticallyFlipped() {
        return this._isVerticallyFlipped;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    protected EscherRecord getEscherAnchor() {
        return this._escherClientAnchor;
    }

    @Override // org.apache.poi.hssf.usermodel.HSSFAnchor
    protected void createEscherAnchor() {
        this._escherClientAnchor = new EscherClientAnchorRecord();
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public ClientAnchor.AnchorType getAnchorType() {
        return ClientAnchor.AnchorType.byId(this._escherClientAnchor.getFlag());
    }

    @Override // org.apache.poi.ss.usermodel.ClientAnchor
    public void setAnchorType(ClientAnchor.AnchorType anchorType) {
        this._escherClientAnchor.setFlag(anchorType.value);
    }

    private void checkRange(int value, int minRange, int maxRange, String varName) {
        if (value < minRange || value > maxRange) {
            throw new IllegalArgumentException(varName + " must be between " + minRange + " and " + maxRange + ", but was: " + value);
        }
    }

    private static int unsignedValue(short s) {
        return s < 0 ? 65536 + s : s;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        HSSFClientAnchor anchor = (HSSFClientAnchor) obj;
        return anchor.getCol1() == getCol1() && anchor.getCol2() == getCol2() && anchor.getDx1() == getDx1() && anchor.getDx2() == getDx2() && anchor.getDy1() == getDy1() && anchor.getDy2() == getDy2() && anchor.getRow1() == getRow1() && anchor.getRow2() == getRow2() && anchor.getAnchorType() == getAnchorType();
    }

    public int hashCode() {
        throw new AssertionError("hashCode not designed");
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDx1() {
        return this._escherClientAnchor.getDx1();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDx1(int dx1) {
        this._escherClientAnchor.setDx1(Integer.valueOf(dx1).shortValue());
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDy1() {
        return this._escherClientAnchor.getDy1();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDy1(int dy1) {
        this._escherClientAnchor.setDy1(Integer.valueOf(dy1).shortValue());
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDy2() {
        return this._escherClientAnchor.getDy2();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDy2(int dy2) {
        this._escherClientAnchor.setDy2(Integer.valueOf(dy2).shortValue());
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public int getDx2() {
        return this._escherClientAnchor.getDx2();
    }

    @Override // org.apache.poi.ss.usermodel.ChildAnchor
    public void setDx2(int dx2) {
        this._escherClientAnchor.setDx2(Integer.valueOf(dx2).shortValue());
    }
}
