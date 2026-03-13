package org.apache.poi.hssf.eventmodel;

import java.io.InputStream;
import java.util.Arrays;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class EventRecordFactory {
    private final ERFListener _listener;
    private final short[] _sids;

    public EventRecordFactory(ERFListener listener, short[] sids) {
        this._listener = listener;
        if (sids == null) {
            this._sids = null;
            return;
        }
        short[] sArr = (short[]) sids.clone();
        this._sids = sArr;
        Arrays.sort(sArr);
    }

    private boolean isSidIncluded(short sid) {
        short[] sArr = this._sids;
        return sArr == null || Arrays.binarySearch(sArr, sid) >= 0;
    }

    private boolean processRecord(Record record) {
        if (!isSidIncluded(record.getSid())) {
            return true;
        }
        return this._listener.processRecord(record);
    }

    public void processRecords(InputStream in) throws RecordFormatException {
        Record last_record = null;
        RecordInputStream recStream = new RecordInputStream(in);
        while (recStream.hasNextRecord()) {
            recStream.nextRecord();
            Record[] recs = RecordFactory.createRecord(recStream);
            if (recs.length > 1) {
                for (Record rec : recs) {
                    if (last_record != null && !processRecord(last_record)) {
                        return;
                    }
                    last_record = rec;
                }
            } else {
                Record record = recs[0];
                if (record == null) {
                    continue;
                } else if (last_record != null && !processRecord(last_record)) {
                    return;
                } else {
                    last_record = record;
                }
            }
        }
        if (last_record != null) {
            processRecord(last_record);
        }
    }
}
