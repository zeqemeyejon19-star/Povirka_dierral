package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class RecordFactoryInputStream {
    private int _bofDepth;
    private DrawingRecord _lastDrawingRecord = new DrawingRecord();
    private Record _lastRecord;
    private boolean _lastRecordWasEOFLevelZero;
    private final RecordInputStream _recStream;
    private final boolean _shouldIncludeContinueRecords;
    private Record[] _unreadRecordBuffer;
    private int _unreadRecordIndex;

    private static final class StreamEncryptionInfo {
        private final FilePassRecord _filePassRec;
        private final boolean _hasBOFRecord;
        private final int _initialRecordsSize;
        private final Record _lastRecord;

        public StreamEncryptionInfo(RecordInputStream rs, List<Record> outputRecs) {
            rs.nextRecord();
            int recSize = rs.remaining() + 4;
            Record rec = RecordFactory.createSingleRecord(rs);
            outputRecs.add(rec);
            if (rec instanceof BOFRecord) {
                this._hasBOFRecord = true;
                if (rs.hasNextRecord()) {
                    rs.nextRecord();
                    rec = RecordFactory.createSingleRecord(rs);
                    recSize += rec.getRecordSize();
                    outputRecs.add(rec);
                    if ((rec instanceof WriteProtectRecord) && rs.hasNextRecord()) {
                        rs.nextRecord();
                        rec = RecordFactory.createSingleRecord(rs);
                        recSize += rec.getRecordSize();
                        outputRecs.add(rec);
                    }
                    fpr = rec instanceof FilePassRecord ? (FilePassRecord) rec : null;
                    if (rec instanceof EOFRecord) {
                        throw new IllegalStateException("Nothing between BOF and EOF");
                    }
                }
            } else {
                this._hasBOFRecord = false;
            }
            this._initialRecordsSize = recSize;
            this._filePassRec = fpr;
            this._lastRecord = rec;
        }

        public RecordInputStream createDecryptingStream(InputStream original) {
            FilePassRecord fpr = this._filePassRec;
            String userPassword = Biff8EncryptionKey.getCurrentUserPassword();
            if (userPassword == null) {
                userPassword = Decryptor.DEFAULT_PASSWORD;
            }
            EncryptionInfo info = fpr.getEncryptionInfo();
            try {
                if (!info.getDecryptor().verifyPassword(userPassword)) {
                    throw new EncryptedDocumentException((Decryptor.DEFAULT_PASSWORD.equals(userPassword) ? "Default" : "Supplied") + " password is invalid for salt/verifier/verifierHash");
                }
                return new RecordInputStream(original, info, this._initialRecordsSize);
            } catch (GeneralSecurityException e) {
                throw new EncryptedDocumentException(e);
            }
        }

        public boolean hasEncryption() {
            return this._filePassRec != null;
        }

        public Record getLastRecord() {
            return this._lastRecord;
        }

        public boolean hasBOFRecord() {
            return this._hasBOFRecord;
        }
    }

    public RecordFactoryInputStream(InputStream inputStream, boolean z) {
        this._unreadRecordIndex = -1;
        this._lastRecord = null;
        RecordInputStream recordInputStream = new RecordInputStream(inputStream);
        ArrayList arrayList = new ArrayList();
        StreamEncryptionInfo streamEncryptionInfo = new StreamEncryptionInfo(recordInputStream, arrayList);
        recordInputStream = streamEncryptionInfo.hasEncryption() ? streamEncryptionInfo.createDecryptingStream(inputStream) : recordInputStream;
        if (!arrayList.isEmpty()) {
            Record[] recordArr = new Record[arrayList.size()];
            this._unreadRecordBuffer = recordArr;
            arrayList.toArray(recordArr);
            this._unreadRecordIndex = 0;
        }
        this._recStream = recordInputStream;
        this._shouldIncludeContinueRecords = z;
        this._lastRecord = streamEncryptionInfo.getLastRecord();
        this._bofDepth = streamEncryptionInfo.hasBOFRecord() ? 1 : 0;
        this._lastRecordWasEOFLevelZero = false;
    }

    public Record nextRecord() {
        Record r = getNextUnreadRecord();
        if (r != null) {
            return r;
        }
        while (this._recStream.hasNextRecord()) {
            if (this._lastRecordWasEOFLevelZero && this._recStream.getNextSid() != 2057) {
                return null;
            }
            this._recStream.nextRecord();
            Record r2 = readNextRecord();
            if (r2 != null) {
                return r2;
            }
        }
        return null;
    }

    private Record getNextUnreadRecord() {
        Record[] recordArr = this._unreadRecordBuffer;
        if (recordArr != null) {
            int ix = this._unreadRecordIndex;
            if (ix < recordArr.length) {
                Record result = recordArr[ix];
                this._unreadRecordIndex = ix + 1;
                return result;
            }
            this._unreadRecordIndex = -1;
            this._unreadRecordBuffer = null;
        }
        return null;
    }

    private Record readNextRecord() {
        Record record = RecordFactory.createSingleRecord(this._recStream);
        this._lastRecordWasEOFLevelZero = false;
        if (record instanceof BOFRecord) {
            this._bofDepth++;
            return record;
        }
        if (record instanceof EOFRecord) {
            int i = this._bofDepth - 1;
            this._bofDepth = i;
            if (i < 1) {
                this._lastRecordWasEOFLevelZero = true;
            }
            return record;
        }
        if (record instanceof DBCellRecord) {
            return null;
        }
        if (record instanceof RKRecord) {
            return RecordFactory.convertToNumberRecord((RKRecord) record);
        }
        if (record instanceof MulRKRecord) {
            Record[] records = RecordFactory.convertRKRecords((MulRKRecord) record);
            this._unreadRecordBuffer = records;
            this._unreadRecordIndex = 1;
            return records[0];
        }
        if (record.getSid() == 235) {
            Record record2 = this._lastRecord;
            if (record2 instanceof DrawingGroupRecord) {
                DrawingGroupRecord lastDGRecord = (DrawingGroupRecord) record2;
                lastDGRecord.join((AbstractEscherHolderRecord) record);
                return null;
            }
        }
        if (record.getSid() == 60) {
            ContinueRecord contRec = (ContinueRecord) record;
            Record record3 = this._lastRecord;
            if ((record3 instanceof ObjRecord) || (record3 instanceof TextObjectRecord)) {
                this._lastDrawingRecord.processContinueRecord(contRec.getData());
                if (this._shouldIncludeContinueRecords) {
                    return record;
                }
                return null;
            }
            if (record3 instanceof DrawingGroupRecord) {
                ((DrawingGroupRecord) record3).processContinueRecord(contRec.getData());
                return null;
            }
            if (record3 instanceof DrawingRecord) {
                return contRec;
            }
            if ((record3 instanceof UnknownRecord) || (record3 instanceof EOFRecord)) {
                return record;
            }
            throw new RecordFormatException("Unhandled Continue Record followining " + this._lastRecord.getClass());
        }
        this._lastRecord = record;
        if (record instanceof DrawingRecord) {
            this._lastDrawingRecord = (DrawingRecord) record;
        }
        return record;
    }
}
