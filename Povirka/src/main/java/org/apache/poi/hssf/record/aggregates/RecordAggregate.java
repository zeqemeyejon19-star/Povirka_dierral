package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordBase;

/* JADX INFO: loaded from: classes.dex */
public abstract class RecordAggregate extends RecordBase {

    public interface RecordVisitor {
        void visitRecord(Record record);
    }

    public abstract void visitContainedRecords(RecordVisitor recordVisitor);

    @Override // org.apache.poi.hssf.record.RecordBase
    public final int serialize(int offset, byte[] data) {
        SerializingRecordVisitor srv = new SerializingRecordVisitor(data, offset);
        visitContainedRecords(srv);
        return srv.countBytesWritten();
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public int getRecordSize() {
        RecordSizingVisitor rsv = new RecordSizingVisitor();
        visitContainedRecords(rsv);
        return rsv.getTotalSize();
    }

    private static final class SerializingRecordVisitor implements RecordVisitor {
        private int _countBytesWritten = 0;
        private final byte[] _data;
        private final int _startOffset;

        public SerializingRecordVisitor(byte[] data, int startOffset) {
            this._data = data;
            this._startOffset = startOffset;
        }

        public int countBytesWritten() {
            return this._countBytesWritten;
        }

        @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor
        public void visitRecord(Record r) {
            int i = this._startOffset;
            int i2 = this._countBytesWritten;
            int currentOffset = i + i2;
            this._countBytesWritten = i2 + r.serialize(currentOffset, this._data);
        }
    }

    private static final class RecordSizingVisitor implements RecordVisitor {
        private int _totalSize = 0;

        public int getTotalSize() {
            return this._totalSize;
        }

        @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor
        public void visitRecord(Record r) {
            this._totalSize += r.getRecordSize();
        }
    }

    public static final class PositionTrackingVisitor implements RecordVisitor {
        private int _position;
        private final RecordVisitor _rv;

        public PositionTrackingVisitor(RecordVisitor rv, int initialPosition) {
            this._rv = rv;
            this._position = initialPosition;
        }

        @Override // org.apache.poi.hssf.record.aggregates.RecordAggregate.RecordVisitor
        public void visitRecord(Record r) {
            this._position += r.getRecordSize();
            this._rv.visitRecord(r);
        }

        public void setPosition(int position) {
            this._position = position;
        }

        public int getPosition() {
            return this._position;
        }
    }
}
