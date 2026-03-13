package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellRangeUtil;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class CFHeaderBase extends StandardRecord implements Cloneable {
    private int field_1_numcf;
    private int field_2_need_recalculation_and_id;
    private CellRangeAddress field_3_enclosing_cell_range;
    private CellRangeAddressList field_4_cell_ranges;

    @Override // org.apache.poi.hssf.record.Record
    public abstract CFHeaderBase clone();

    protected abstract String getRecordName();

    protected CFHeaderBase() {
    }

    protected CFHeaderBase(CellRangeAddress[] regions, int nRules) {
        CellRangeAddress[] mergeCellRanges = CellRangeUtil.mergeCellRanges(regions);
        setCellRanges(mergeCellRanges);
        this.field_1_numcf = nRules;
    }

    protected void createEmpty() {
        this.field_3_enclosing_cell_range = new CellRangeAddress(0, 0, 0, 0);
        this.field_4_cell_ranges = new CellRangeAddressList();
    }

    protected void read(RecordInputStream in) {
        this.field_1_numcf = in.readShort();
        this.field_2_need_recalculation_and_id = in.readShort();
        this.field_3_enclosing_cell_range = new CellRangeAddress(in);
        this.field_4_cell_ranges = new CellRangeAddressList(in);
    }

    public int getNumberOfConditionalFormats() {
        return this.field_1_numcf;
    }

    public void setNumberOfConditionalFormats(int n) {
        this.field_1_numcf = n;
    }

    public boolean getNeedRecalculation() {
        return (this.field_2_need_recalculation_and_id & 1) == 1;
    }

    public void setNeedRecalculation(boolean b) {
        if (b == getNeedRecalculation()) {
            return;
        }
        if (b) {
            this.field_2_need_recalculation_and_id++;
        } else {
            this.field_2_need_recalculation_and_id--;
        }
    }

    public int getID() {
        return this.field_2_need_recalculation_and_id >> 1;
    }

    public void setID(int id) {
        boolean needsRecalc = getNeedRecalculation();
        int i = id << 1;
        this.field_2_need_recalculation_and_id = i;
        if (needsRecalc) {
            this.field_2_need_recalculation_and_id = i + 1;
        }
    }

    public CellRangeAddress getEnclosingCellRange() {
        return this.field_3_enclosing_cell_range;
    }

    public void setEnclosingCellRange(CellRangeAddress cr) {
        this.field_3_enclosing_cell_range = cr;
    }

    public void setCellRanges(CellRangeAddress[] cellRanges) {
        if (cellRanges == null) {
            throw new IllegalArgumentException("cellRanges must not be null");
        }
        CellRangeAddressList cral = new CellRangeAddressList();
        CellRangeAddress enclosingRange = null;
        for (CellRangeAddress cr : cellRanges) {
            enclosingRange = CellRangeUtil.createEnclosingCellRange(cr, enclosingRange);
            cral.addCellRangeAddress(cr);
        }
        this.field_3_enclosing_cell_range = enclosingRange;
        this.field_4_cell_ranges = cral;
    }

    public CellRangeAddress[] getCellRanges() {
        return this.field_4_cell_ranges.getCellRangeAddresses();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[").append(getRecordName()).append("]\n");
        buffer.append("\t.numCF             = ").append(getNumberOfConditionalFormats()).append("\n");
        buffer.append("\t.needRecalc        = ").append(getNeedRecalculation()).append("\n");
        buffer.append("\t.id                = ").append(getID()).append("\n");
        buffer.append("\t.enclosingCellRange= ").append(getEnclosingCellRange()).append("\n");
        buffer.append("\t.cfranges=[");
        int i = 0;
        while (i < this.field_4_cell_ranges.countRanges()) {
            buffer.append(i == 0 ? "" : ",").append(this.field_4_cell_ranges.getCellRangeAddress(i));
            i++;
        }
        buffer.append("]\n");
        buffer.append("[/").append(getRecordName()).append("]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return this.field_4_cell_ranges.getSize() + 12;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numcf);
        out.writeShort(this.field_2_need_recalculation_and_id);
        this.field_3_enclosing_cell_range.serialize(out);
        this.field_4_cell_ranges.serialize(out);
    }

    protected void copyTo(CFHeaderBase result) {
        result.field_1_numcf = this.field_1_numcf;
        result.field_2_need_recalculation_and_id = this.field_2_need_recalculation_and_id;
        result.field_3_enclosing_cell_range = this.field_3_enclosing_cell_range.copy();
        result.field_4_cell_ranges = this.field_4_cell_ranges.copy();
    }
}
