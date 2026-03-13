package org.apache.poi.hssf.record.aggregates;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.HeaderFooterRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;

/* JADX INFO: loaded from: classes.dex */
public final class CustomViewSettingsRecordAggregate extends RecordAggregate {
    private final Record _begin;
    private final Record _end;
    private PageSettingsBlock _psBlock;
    private final List<RecordBase> _recs;

    public CustomViewSettingsRecordAggregate(RecordStream rs) {
        Record next = rs.getNext();
        this._begin = next;
        if (next.getSid() != 426) {
            throw new IllegalStateException("Bad begin record");
        }
        List<RecordBase> temp = new ArrayList<>();
        while (rs.peekNextSid() != 427) {
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
        Record next2 = rs.getNext();
        this._end = next2;
        if (next2.getSid() != 427) {
            throw new IllegalStateException("Bad custom view settings end record");
        }
    }

    @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate
    public void visitContainedRecords(RecordAggregate.RecordVisitor rv) {
        if (this._recs.isEmpty()) {
            return;
        }
        rv.visitRecord(this._begin);
        for (int i = 0; i < this._recs.size(); i++) {
            RecordBase rb = this._recs.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate) rb).visitContainedRecords(rv);
            } else {
                rv.visitRecord((Record) rb);
            }
        }
        rv.visitRecord(this._end);
    }

    public static boolean isBeginRecord(int sid) {
        return sid == 426;
    }

    public void append(RecordBase r) {
        this._recs.add(r);
    }
}
