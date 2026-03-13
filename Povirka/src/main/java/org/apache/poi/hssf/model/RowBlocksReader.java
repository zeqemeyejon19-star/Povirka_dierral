package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.aggregates.SharedValueManager;
import org.apache.poi.ss.util.CellReference;

/* JADX INFO: loaded from: classes.dex */
public final class RowBlocksReader {
    private final MergeCellsRecord[] _mergedCellsRecords;
    private final List<Record> _plainRecords;
    private final SharedValueManager _sfm;

    public RowBlocksReader(RecordStream rs) {
        List<Record> plainRecords = new ArrayList<>();
        List<Record> shFrmRecords = new ArrayList<>();
        List<CellReference> firstCellRefs = new ArrayList<>();
        List<Record> arrayRecords = new ArrayList<>();
        List<Record> tableRecords = new ArrayList<>();
        List<Record> mergeCellRecords = new ArrayList<>();
        Record prevRec = null;
        List<Record> dest = null;
        while (!RecordOrderer.isEndOfRowBlock(rs.peekNextSid())) {
            if (!rs.hasNext()) {
                throw new RuntimeException("Failed to find end of row/cell records");
            }
            Record rec = rs.getNext();
            short sid = rec.getSid();
            if (sid == 229) {
                dest = mergeCellRecords;
            } else if (sid == 545) {
                dest = arrayRecords;
            } else if (sid == 566) {
                dest = tableRecords;
            } else if (sid == 1212) {
                if (!(prevRec instanceof FormulaRecord)) {
                    throw new RuntimeException("Shared formula record should follow a FormulaRecord");
                }
                FormulaRecord fr = (FormulaRecord) prevRec;
                firstCellRefs.add(new CellReference(fr.getRow(), fr.getColumn()));
                dest = shFrmRecords;
            } else {
                dest = plainRecords;
            }
            dest.add(rec);
            prevRec = rec;
        }
        SharedFormulaRecord[] sharedFormulaRecs = new SharedFormulaRecord[shFrmRecords.size()];
        CellReference[] firstCells = new CellReference[firstCellRefs.size()];
        ArrayRecord[] arrayRecs = new ArrayRecord[arrayRecords.size()];
        TableRecord[] tableRecs = new TableRecord[tableRecords.size()];
        shFrmRecords.toArray(sharedFormulaRecs);
        firstCellRefs.toArray(firstCells);
        arrayRecords.toArray(arrayRecs);
        tableRecords.toArray(tableRecs);
        this._plainRecords = plainRecords;
        this._sfm = SharedValueManager.create(sharedFormulaRecs, firstCells, arrayRecs, tableRecs);
        MergeCellsRecord[] mergeCellsRecordArr = new MergeCellsRecord[mergeCellRecords.size()];
        this._mergedCellsRecords = mergeCellsRecordArr;
        mergeCellRecords.toArray(mergeCellsRecordArr);
    }

    public MergeCellsRecord[] getLooseMergedCells() {
        return this._mergedCellsRecords;
    }

    public SharedValueManager getSharedFormulaManager() {
        return this._sfm;
    }

    public RecordStream getPlainRecordStream() {
        return new RecordStream(this._plainRecords, 0);
    }
}
