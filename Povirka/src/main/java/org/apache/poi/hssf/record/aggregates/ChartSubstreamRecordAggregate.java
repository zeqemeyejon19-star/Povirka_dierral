package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.HeaderFooterRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;

/* JADX INFO: loaded from: classes.dex */
public final class ChartSubstreamRecordAggregate extends RecordAggregate {
    private final BOFRecord _bofRec;
    private PageSettingsBlock _psBlock;
    private final List<RecordBase> _recs;

    public ChartSubstreamRecordAggregate(RecordStream rs) {
        this._bofRec = (BOFRecord) rs.getNext();
        List<RecordBase> temp = new ArrayList<>();
        while (rs.peekNextClass() != EOFRecord.class) {
            if (PageSettingsBlock.isComponentRecord(rs.peekNextSid())) {
                if (this._psBlock != null) {
                    if (rs.peekNextSid() == 2204) {
                        this._psBlock.addLateHeaderFooter((HeaderFooterRecord) rs.getNext());
                    } else {
                        throw new IllegalStateException("Found more than one PageSettingsBlock in chart sub-stream, had sid: " + rs.peekNextSid());
                    }
                } else {
                    PageSettingsBlock pageSettingsBlock = new PageSettingsBlock(rs);
                    this._psBlock = pageSettingsBlock;
                    temp.add(pageSettingsBlock);
                }
            } else {
                temp.add(rs.getNext());
            }
        }
        this._recs = temp;
        Record eof = rs.getNext();
        if (!(eof instanceof EOFRecord)) {
            throw new IllegalStateException("Bad chart EOF");
        }
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate
    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        if (this._recs.isEmpty()) {
            return;
        }
        rv.visitRecord(this._bofRec);
        for (int i = 0; i < this._recs.size(); i++) {
            RecordBase rb = this._recs.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate) rb).visitContainedRecords(rv);
            } else {
                rv.visitRecord((Record) rb);
            }
        }
        rv.visitRecord(EOFRecord.instance);
    }
}
