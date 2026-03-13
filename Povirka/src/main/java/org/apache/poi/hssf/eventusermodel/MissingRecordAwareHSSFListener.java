package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingRowDummyRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.StringRecord;

/* JADX INFO: loaded from: classes.dex */
public final class MissingRecordAwareHSSFListener implements HSSFListener {
    private HSSFListener childListener;
    private int lastCellColumn;
    private int lastCellRow;
    private int lastRowRow;

    public MissingRecordAwareHSSFListener(HSSFListener listener) {
        resetCounts();
        this.childListener = listener;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.poi.hssf.eventusermodel.HSSFListener
    public void processRecord(Record record) {
        int thisRow;
        int thisColumn;
        CellValueRecordInterface[] expandedRecords = null;
        if (record instanceof CellValueRecordInterface) {
            CellValueRecordInterface valueRec = (CellValueRecordInterface) record;
            thisRow = valueRec.getRow();
            thisColumn = valueRec.getColumn();
        } else {
            if (record instanceof StringRecord) {
                this.childListener.processRecord(record);
                return;
            }
            thisRow = -1;
            thisColumn = -1;
            short sid = record.getSid();
            if (sid == 28) {
                NoteRecord nrec = (NoteRecord) record;
                thisRow = nrec.getRow();
                thisColumn = nrec.getColumn();
            } else if (sid == 520) {
                RowRecord rowrec = (RowRecord) record;
                if (this.lastRowRow + 1 < rowrec.getRowNumber()) {
                    int i = this.lastRowRow;
                    while (true) {
                        i++;
                        if (i >= rowrec.getRowNumber()) {
                            break;
                        }
                        MissingRowDummyRecord dr = new MissingRowDummyRecord(i);
                        this.childListener.processRecord(dr);
                    }
                }
                int i2 = rowrec.getRowNumber();
                this.lastRowRow = i2;
                this.lastCellColumn = -1;
            } else {
                if (sid == 1212) {
                    this.childListener.processRecord(record);
                    return;
                }
                if (sid == 2057) {
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == 5 || bof.getType() == 16) {
                        resetCounts();
                    }
                } else if (sid == 189) {
                    MulRKRecord mrk = (MulRKRecord) record;
                    expandedRecords = RecordFactory.convertRKRecords(mrk);
                } else if (sid == 190) {
                    MulBlankRecord mbr = (MulBlankRecord) record;
                    expandedRecords = RecordFactory.convertBlankRecords(mbr);
                }
            }
        }
        if (expandedRecords != null && expandedRecords.length > 0) {
            thisRow = expandedRecords[0].getRow();
            thisColumn = expandedRecords[0].getColumn();
        }
        int i3 = this.lastCellRow;
        if (thisRow != i3 && thisRow > 0) {
            if (i3 == -1) {
                this.lastCellRow = 0;
            }
            for (int i4 = this.lastCellRow; i4 < thisRow; i4++) {
                int cols = -1;
                if (i4 == this.lastCellRow) {
                    cols = this.lastCellColumn;
                }
                this.childListener.processRecord(new LastCellOfRowDummyRecord(i4, cols));
            }
        }
        int i5 = this.lastCellRow;
        if (i5 != -1 && this.lastCellColumn != -1 && thisRow == -1) {
            this.childListener.processRecord(new LastCellOfRowDummyRecord(this.lastCellRow, this.lastCellColumn));
            this.lastCellRow = -1;
            this.lastCellColumn = -1;
        }
        if (thisRow != this.lastCellRow) {
            this.lastCellColumn = -1;
        }
        if (this.lastCellColumn != thisColumn - 1) {
            for (int i6 = r4 + 1; i6 < thisColumn; i6++) {
                this.childListener.processRecord(new MissingCellDummyRecord(thisRow, i6));
            }
        }
        if (expandedRecords != null && expandedRecords.length > 0) {
            thisColumn = expandedRecords[expandedRecords.length - 1].getColumn();
        }
        if (thisColumn != -1) {
            this.lastCellColumn = thisColumn;
            this.lastCellRow = thisRow;
        }
        if (expandedRecords == null || expandedRecords.length <= 0) {
            this.childListener.processRecord(record);
            return;
        }
        for (Object obj : expandedRecords) {
            this.childListener.processRecord((Record) obj);
        }
    }

    private void resetCounts() {
        this.lastRowRow = -1;
        this.lastCellRow = -1;
        this.lastCellColumn = -1;
    }
}
