package org.apache.poi.hssf.model;

import java.util.List;
import org.apache.poi.hssf.record.Record;

/* JADX INFO: loaded from: classes.dex */
public final class RecordStream {
    private int _countRead;
    private final int _endIx;
    private final List<Record> _list;
    private int _nextIndex;

    public RecordStream(List<Record> inputList, int startIndex, int endIx) {
        this._list = inputList;
        this._nextIndex = startIndex;
        this._endIx = endIx;
        this._countRead = 0;
    }

    public RecordStream(List<Record> records, int startIx) {
        this(records, startIx, records.size());
    }

    public boolean hasNext() {
        return this._nextIndex < this._endIx;
    }

    public Record getNext() {
        if (!hasNext()) {
            throw new RuntimeException("Attempt to read past end of record stream");
        }
        this._countRead++;
        List<Record> list = this._list;
        int i = this._nextIndex;
        this._nextIndex = i + 1;
        return list.get(i);
    }

    public Class<? extends Record> peekNextClass() {
        if (!hasNext()) {
            return null;
        }
        return this._list.get(this._nextIndex).getClass();
    }

    public int peekNextSid() {
        if (!hasNext()) {
            return -1;
        }
        return this._list.get(this._nextIndex).getSid();
    }

    public int getCountRead() {
        return this._countRead;
    }
}
