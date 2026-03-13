package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class AreaPtgBase extends OperandPtg implements AreaI {
    private int field_1_first_row;
    private int field_2_last_row;
    private int field_3_first_column;
    private int field_4_last_column;
    private static final BitField rowRelative = BitFieldFactory.getInstance(32768);
    private static final BitField colRelative = BitFieldFactory.getInstance(16384);
    private static final BitField columnMask = BitFieldFactory.getInstance(16383);

    protected final RuntimeException notImplemented() {
        return new RuntimeException("Coding Error: This method should never be called. This ptg should be converted");
    }

    protected AreaPtgBase() {
    }

    protected AreaPtgBase(AreaReference ar) {
        CellReference firstCell = ar.getFirstCell();
        CellReference lastCell = ar.getLastCell();
        setFirstRow(firstCell.getRow());
        setFirstColumn(firstCell.getCol() == -1 ? (short) 0 : firstCell.getCol());
        setLastRow(lastCell.getRow());
        setLastColumn(lastCell.getCol() == -1 ? (short) 255 : lastCell.getCol());
        setFirstColRelative(!firstCell.isColAbsolute());
        setLastColRelative(!lastCell.isColAbsolute());
        setFirstRowRelative(!firstCell.isRowAbsolute());
        setLastRowRelative(!lastCell.isRowAbsolute());
    }

    protected AreaPtgBase(int firstRow, int lastRow, int firstColumn, int lastColumn, boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {
        if (lastRow >= firstRow) {
            setFirstRow(firstRow);
            setLastRow(lastRow);
            setFirstRowRelative(firstRowRelative);
            setLastRowRelative(lastRowRelative);
        } else {
            setFirstRow(lastRow);
            setLastRow(firstRow);
            setFirstRowRelative(lastRowRelative);
            setLastRowRelative(firstRowRelative);
        }
        if (lastColumn >= firstColumn) {
            setFirstColumn(firstColumn);
            setLastColumn(lastColumn);
            setFirstColRelative(firstColRelative);
            setLastColRelative(lastColRelative);
            return;
        }
        setFirstColumn(lastColumn);
        setLastColumn(firstColumn);
        setFirstColRelative(lastColRelative);
        setLastColRelative(firstColRelative);
    }

    public void sortTopLeftToBottomRight() {
        if (getFirstRow() > getLastRow()) {
            int firstRow = getFirstRow();
            boolean firstRowRel = isFirstRowRelative();
            setFirstRow(getLastRow());
            setFirstRowRelative(isLastRowRelative());
            setLastRow(firstRow);
            setLastRowRelative(firstRowRel);
        }
        int firstRow2 = getFirstColumn();
        if (firstRow2 > getLastColumn()) {
            int firstCol = getFirstColumn();
            boolean firstColRel = isFirstColRelative();
            setFirstColumn(getLastColumn());
            setFirstColRelative(isLastColRelative());
            setLastColumn(firstCol);
            setLastColRelative(firstColRel);
        }
    }

    protected final void readCoordinates(LittleEndianInput in) {
        this.field_1_first_row = in.readUShort();
        this.field_2_last_row = in.readUShort();
        this.field_3_first_column = in.readUShort();
        this.field_4_last_column = in.readUShort();
    }

    protected final void writeCoordinates(LittleEndianOutput out) {
        out.writeShort(this.field_1_first_row);
        out.writeShort(this.field_2_last_row);
        out.writeShort(this.field_3_first_column);
        out.writeShort(this.field_4_last_column);
    }

    @Override // org.apache.poi.ss.formula.ptg.AreaI
    public final int getFirstRow() {
        return this.field_1_first_row;
    }

    public final void setFirstRow(int rowIx) {
        this.field_1_first_row = rowIx;
    }

    @Override // org.apache.poi.ss.formula.ptg.AreaI
    public final int getLastRow() {
        return this.field_2_last_row;
    }

    public final void setLastRow(int rowIx) {
        this.field_2_last_row = rowIx;
    }

    @Override // org.apache.poi.ss.formula.ptg.AreaI
    public final int getFirstColumn() {
        return columnMask.getValue(this.field_3_first_column);
    }

    public final short getFirstColumnRaw() {
        return (short) this.field_3_first_column;
    }

    public final boolean isFirstRowRelative() {
        return rowRelative.isSet(this.field_3_first_column);
    }

    public final void setFirstRowRelative(boolean rel) {
        this.field_3_first_column = rowRelative.setBoolean(this.field_3_first_column, rel);
    }

    public final boolean isFirstColRelative() {
        return colRelative.isSet(this.field_3_first_column);
    }

    public final void setFirstColRelative(boolean rel) {
        this.field_3_first_column = colRelative.setBoolean(this.field_3_first_column, rel);
    }

    public final void setFirstColumn(int colIx) {
        this.field_3_first_column = columnMask.setValue(this.field_3_first_column, colIx);
    }

    public final void setFirstColumnRaw(int column) {
        this.field_3_first_column = column;
    }

    @Override // org.apache.poi.ss.formula.ptg.AreaI
    public final int getLastColumn() {
        return columnMask.getValue(this.field_4_last_column);
    }

    public final short getLastColumnRaw() {
        return (short) this.field_4_last_column;
    }

    public final boolean isLastRowRelative() {
        return rowRelative.isSet(this.field_4_last_column);
    }

    public final void setLastRowRelative(boolean rel) {
        this.field_4_last_column = rowRelative.setBoolean(this.field_4_last_column, rel);
    }

    public final boolean isLastColRelative() {
        return colRelative.isSet(this.field_4_last_column);
    }

    public final void setLastColRelative(boolean rel) {
        this.field_4_last_column = colRelative.setBoolean(this.field_4_last_column, rel);
    }

    public final void setLastColumn(int colIx) {
        this.field_4_last_column = columnMask.setValue(this.field_4_last_column, colIx);
    }

    public final void setLastColumnRaw(short column) {
        this.field_4_last_column = column;
    }

    protected final String formatReferenceAsString() {
        CellReference topLeft = new CellReference(getFirstRow(), getFirstColumn(), !isFirstRowRelative(), !isFirstColRelative());
        CellReference botRight = new CellReference(getLastRow(), getLastColumn(), !isLastRowRelative(), !isLastColRelative());
        if (AreaReference.isWholeColumnReference(SpreadsheetVersion.EXCEL97, topLeft, botRight)) {
            return new AreaReference(topLeft, botRight, SpreadsheetVersion.EXCEL97).formatAsString();
        }
        return topLeft.formatAsString() + ":" + botRight.formatAsString();
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public String toFormulaString() {
        return formatReferenceAsString();
    }

    @Override // org.apache.poi.ss.formula.ptg.Ptg
    public byte getDefaultOperandClass() {
        return (byte) 0;
    }
}
