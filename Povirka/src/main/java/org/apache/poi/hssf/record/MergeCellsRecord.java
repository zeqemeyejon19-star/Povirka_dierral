package org.apache.poi.hssf.record;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public final class MergeCellsRecord extends StandardRecord implements Cloneable {
    public static final short sid = 229;
    private final int _numberOfRegions;
    private final CellRangeAddress[] _regions;
    private final int _startIndex;

    public MergeCellsRecord(CellRangeAddress[] regions, int startIndex, int numberOfRegions) {
        this._regions = regions;
        this._startIndex = startIndex;
        this._numberOfRegions = numberOfRegions;
    }

    public MergeCellsRecord(RecordInputStream in) {
        int nRegions = in.readUShort();
        CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        for (int i = 0; i < nRegions; i++) {
            cras[i] = new CellRangeAddress(in);
        }
        this._numberOfRegions = nRegions;
        this._startIndex = 0;
        this._regions = cras;
    }

    public short getNumAreas() {
        return (short) this._numberOfRegions;
    }

    public CellRangeAddress getAreaAt(int index) {
        return this._regions[this._startIndex + index];
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return CellRangeAddressList.getEncodedSize(this._numberOfRegions);
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return sid;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public void serialize(LittleEndianOutput out) {
        int nItems = this._numberOfRegions;
        out.writeShort(nItems);
        for (int i = 0; i < this._numberOfRegions; i++) {
            this._regions[this._startIndex + i].serialize(out);
        }
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer retval = new StringBuffer();
        retval.append("[MERGEDCELLS]").append("\n");
        retval.append("     .numregions =").append((int) getNumAreas()).append("\n");
        for (int k = 0; k < this._numberOfRegions; k++) {
            CellRangeAddress r = this._regions[this._startIndex + k];
            retval.append("     .rowfrom =").append(r.getFirstRow()).append("\n");
            retval.append("     .rowto   =").append(r.getLastRow()).append("\n");
            retval.append("     .colfrom =").append(r.getFirstColumn()).append("\n");
            retval.append("     .colto   =").append(r.getLastColumn()).append("\n");
        }
        retval.append("[MERGEDCELLS]").append("\n");
        return retval.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public MergeCellsRecord clone() {
        int nRegions = this._numberOfRegions;
        CellRangeAddress[] clonedRegions = new CellRangeAddress[nRegions];
        for (int i = 0; i < clonedRegions.length; i++) {
            clonedRegions[i] = this._regions[this._startIndex + i].copy();
        }
        return new MergeCellsRecord(clonedRegions, 0, nRegions);
    }
}
