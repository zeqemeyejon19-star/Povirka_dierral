package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

/* JADX INFO: loaded from: classes.dex */
public final class MergedCellsTable extends RecordAggregate {
    private static int MAX_MERGED_REGIONS = 1027;
    private final List<CellRangeAddress> _mergedRegions = new ArrayList();

    public void read(RecordStream rs) {
        List<CellRangeAddress> temp = this._mergedRegions;
        while (rs.peekNextClass() == MergeCellsRecord.class) {
            MergeCellsRecord mcr = (MergeCellsRecord) rs.getNext();
            int nRegions = mcr.getNumAreas();
            for (int i = 0; i < nRegions; i++) {
                CellRangeAddress cra = mcr.getAreaAt(i);
                temp.add(cra);
            }
        }
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate, org.apache.poi.hssf.record.RecordBase
    public int getRecordSize() {
        int nRegions = this._mergedRegions.size();
        if (nRegions < 1) {
            return 0;
        }
        int i = MAX_MERGED_REGIONS;
        int nMergedCellsRecords = nRegions / i;
        int nLeftoverMergedRegions = nRegions % i;
        int result = ((CellRangeAddressList.getEncodedSize(i) + 4) * nMergedCellsRecords) + 4 + CellRangeAddressList.getEncodedSize(nLeftoverMergedRegions);
        return result;
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate
    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        int nRegions = this._mergedRegions.size();
        if (nRegions < 1) {
            return;
        }
        int i = MAX_MERGED_REGIONS;
        int nFullMergedCellsRecords = nRegions / i;
        int nLeftoverMergedRegions = nRegions % i;
        CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        this._mergedRegions.toArray(cras);
        for (int i2 = 0; i2 < nFullMergedCellsRecords; i2++) {
            int startIx = MAX_MERGED_REGIONS * i2;
            rv.visitRecord(new MergeCellsRecord(cras, startIx, MAX_MERGED_REGIONS));
        }
        if (nLeftoverMergedRegions > 0) {
            int startIx2 = MAX_MERGED_REGIONS * nFullMergedCellsRecords;
            rv.visitRecord(new MergeCellsRecord(cras, startIx2, nLeftoverMergedRegions));
        }
    }

    public void addRecords(MergeCellsRecord[] mcrs) {
        for (MergeCellsRecord mergeCellsRecord : mcrs) {
            addMergeCellsRecord(mergeCellsRecord);
        }
    }

    private void addMergeCellsRecord(MergeCellsRecord mcr) {
        int nRegions = mcr.getNumAreas();
        for (int i = 0; i < nRegions; i++) {
            CellRangeAddress cra = mcr.getAreaAt(i);
            this._mergedRegions.add(cra);
        }
    }

    public CellRangeAddress get(int index) {
        checkIndex(index);
        return this._mergedRegions.get(index);
    }

    public void remove(int index) {
        checkIndex(index);
        this._mergedRegions.remove(index);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this._mergedRegions.size()) {
            throw new IllegalArgumentException("Specified CF index " + index + " is outside the allowable range (0.." + (this._mergedRegions.size() - 1) + ")");
        }
    }

    public void addArea(int rowFrom, int colFrom, int rowTo, int colTo) {
        this._mergedRegions.add(new CellRangeAddress(rowFrom, rowTo, colFrom, colTo));
    }

    public int getNumberOfMergedRegions() {
        return this._mergedRegions.size();
    }
}
