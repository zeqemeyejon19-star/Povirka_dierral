package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import org.apache.poi.hssf.record.crypto.Biff8DecryptingStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class RecordInputStream implements LittleEndianInput {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int DATA_LEN_NEEDS_TO_BE_READ = -1;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final int INVALID_SID_VALUE = -1;
    public static final short MAX_RECORD_DATA_SIZE = 8224;
    private final BiffHeaderInput _bhi;
    private int _currentDataLength;
    private int _currentDataOffset;
    private int _currentSid;
    private final LittleEndianInput _dataInput;
    private int _markedDataOffset;
    private int _nextSid;

    public static final class LeftoverDataException extends RuntimeException {
        public LeftoverDataException(int sid, int remainingByteCount) {
            super("Initialisation of record 0x" + Integer.toHexString(sid).toUpperCase(Locale.ROOT) + "(" + getRecordName(sid) + ") left " + remainingByteCount + " bytes remaining still to be read.");
        }

        private static String getRecordName(int sid) {
            Class<? extends Record> recordClass = RecordFactory.getRecordClass(sid);
            if (recordClass == null) {
                return null;
            }
            return recordClass.getSimpleName();
        }
    }

    private static final class SimpleHeaderInput implements BiffHeaderInput {
        private final LittleEndianInput _lei;

        public SimpleHeaderInput(InputStream in) {
            this._lei = RecordInputStream.getLEI(in);
        }

        @Override // org.apache.poi.hssf.record.BiffHeaderInput
        public int available() {
            return this._lei.available();
        }

        @Override // org.apache.poi.hssf.record.BiffHeaderInput
        public int readDataSize() {
            return this._lei.readUShort();
        }

        @Override // org.apache.poi.hssf.record.BiffHeaderInput
        public int readRecordSID() {
            return this._lei.readUShort();
        }
    }

    public RecordInputStream(InputStream in) throws RecordFormatException {
        this(in, null, 0);
    }

    public RecordInputStream(InputStream in, EncryptionInfo key, int initialOffset) throws RecordFormatException {
        if (key == null) {
            this._dataInput = getLEI(in);
            this._bhi = new SimpleHeaderInput(in);
        } else {
            Biff8DecryptingStream bds = new Biff8DecryptingStream(in, initialOffset, key);
            this._dataInput = bds;
            this._bhi = bds;
        }
        this._nextSid = readNextSid();
    }

    /* JADX WARN: Multi-variable type inference failed */
    static LittleEndianInput getLEI(InputStream inputStream) {
        if (inputStream instanceof LittleEndianInput) {
            return (LittleEndianInput) inputStream;
        }
        return new LittleEndianInputStream(inputStream);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int available() {
        return remaining();
    }

    public int read(byte[] b, int off, int len) {
        int limit = Math.min(len, remaining());
        if (limit == 0) {
            return 0;
        }
        readFully(b, off, limit);
        return limit;
    }

    public short getSid() {
        return (short) this._currentSid;
    }

    public boolean hasNextRecord() throws LeftoverDataException {
        int i = this._currentDataLength;
        if (i != -1 && i != this._currentDataOffset) {
            throw new LeftoverDataException(this._currentSid, remaining());
        }
        if (i != -1) {
            this._nextSid = readNextSid();
        }
        return this._nextSid != -1;
    }

    private int readNextSid() {
        int nAvailable = this._bhi.available();
        if (nAvailable < 4) {
            return -1;
        }
        int result = this._bhi.readRecordSID();
        if (result == -1) {
            throw new RecordFormatException("Found invalid sid (" + result + ")");
        }
        this._currentDataLength = -1;
        return result;
    }

    public void nextRecord() throws RecordFormatException {
        int i = this._nextSid;
        if (i == -1) {
            throw new IllegalStateException("EOF - next record not available");
        }
        if (this._currentDataLength != -1) {
            throw new IllegalStateException("Cannot call nextRecord() without checking hasNextRecord() first");
        }
        this._currentSid = i;
        this._currentDataOffset = 0;
        int dataSize = this._bhi.readDataSize();
        this._currentDataLength = dataSize;
        if (dataSize > 8224) {
            throw new RecordFormatException("The content of an excel record cannot exceed 8224 bytes");
        }
    }

    private void checkRecordPosition(int requiredByteCount) {
        int nAvailable = remaining();
        if (nAvailable >= requiredByteCount) {
            return;
        }
        if (nAvailable == 0 && isContinueNext()) {
            nextRecord();
            return;
        }
        throw new RecordFormatException("Not enough data (" + nAvailable + ") to read requested (" + requiredByteCount + ") bytes");
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public byte readByte() {
        checkRecordPosition(1);
        this._currentDataOffset++;
        return this._dataInput.readByte();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public short readShort() {
        checkRecordPosition(2);
        this._currentDataOffset += 2;
        return this._dataInput.readShort();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readInt() {
        checkRecordPosition(4);
        this._currentDataOffset += 4;
        return this._dataInput.readInt();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public long readLong() {
        checkRecordPosition(8);
        this._currentDataOffset += 8;
        return this._dataInput.readLong();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUByte() {
        return readByte() & 255;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public int readUShort() {
        checkRecordPosition(2);
        this._currentDataOffset += 2;
        return this._dataInput.readUShort();
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public double readDouble() {
        long valueLongBits = readLong();
        double result = Double.longBitsToDouble(valueLongBits);
        Double.isNaN(result);
        return result;
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readPlain(byte[] buf, int off, int len) {
        readFully(buf, 0, buf.length, true);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length, false);
    }

    @Override // org.apache.poi.util.LittleEndianInput
    public void readFully(byte[] buf, int off, int len) {
        readFully(buf, off, len, false);
    }

    protected void readFully(byte[] buf, int off, int len, boolean isPlain) {
        if (buf == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > buf.length - off) {
            throw new IndexOutOfBoundsException();
        }
        while (len > 0) {
            int nextChunk = Math.min(available(), len);
            if (nextChunk == 0) {
                if (!hasNextRecord()) {
                    throw new RecordFormatException("Can't read the remaining " + len + " bytes of the requested " + len + " bytes. No further record exists.");
                }
                nextRecord();
                nextChunk = Math.min(available(), len);
                if (nextChunk <= 0) {
                    throw new AssertionError();
                }
            }
            checkRecordPosition(nextChunk);
            if (isPlain) {
                this._dataInput.readPlain(buf, off, nextChunk);
            } else {
                this._dataInput.readFully(buf, off, nextChunk);
            }
            this._currentDataOffset += nextChunk;
            off += nextChunk;
            len -= nextChunk;
        }
    }

    public String readString() {
        int requestedLength = readUShort();
        byte compressFlag = readByte();
        return readStringCommon(requestedLength, compressFlag == 0);
    }

    public String readUnicodeLEString(int requestedLength) {
        return readStringCommon(requestedLength, false);
    }

    public String readCompressedUnicode(int requestedLength) {
        return readStringCommon(requestedLength, true);
    }

    private String readStringCommon(int requestedLength, boolean pIsCompressedEncoding) {
        int uByte;
        int uByte2;
        if (requestedLength < 0 || requestedLength > 1048576) {
            throw new IllegalArgumentException("Bad requested string length (" + requestedLength + ")");
        }
        char[] buf = new char[requestedLength];
        boolean isCompressedEncoding = pIsCompressedEncoding;
        int curLen = 0;
        while (true) {
            int availableChars = remaining();
            if (!isCompressedEncoding) {
                availableChars /= 2;
            }
            if (requestedLength - curLen <= availableChars) {
                while (curLen < requestedLength) {
                    if (isCompressedEncoding) {
                        uByte = readUByte();
                    } else {
                        uByte = readShort();
                    }
                    char ch = (char) uByte;
                    buf[curLen] = ch;
                    curLen++;
                }
                return new String(buf);
            }
            while (availableChars > 0) {
                if (isCompressedEncoding) {
                    uByte2 = readUByte();
                } else {
                    uByte2 = readShort();
                }
                char ch2 = (char) uByte2;
                buf[curLen] = ch2;
                curLen++;
                availableChars--;
            }
            if (!isContinueNext()) {
                throw new RecordFormatException("Expected to find a ContinueRecord in order to read remaining " + (requestedLength - curLen) + " of " + requestedLength + " chars");
            }
            if (remaining() != 0) {
                throw new RecordFormatException("Odd number of bytes(" + remaining() + ") left behind");
            }
            nextRecord();
            byte compressFlag = readByte();
            boolean z = true;
            if (compressFlag != 0 && compressFlag != 1) {
                throw new AssertionError();
            }
            if (compressFlag != 0) {
                z = false;
            }
            isCompressedEncoding = z;
        }
    }

    public byte[] readRemainder() {
        int size = remaining();
        if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        readFully(result);
        return result;
    }

    @Deprecated
    public byte[] readAllContinuedRemainder() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(16448);
        while (true) {
            byte[] b = readRemainder();
            out.write(b, 0, b.length);
            if (isContinueNext()) {
                nextRecord();
            } else {
                return out.toByteArray();
            }
        }
    }

    public int remaining() {
        int i = this._currentDataLength;
        if (i == -1) {
            return 0;
        }
        return i - this._currentDataOffset;
    }

    private boolean isContinueNext() {
        int i = this._currentDataLength;
        if (i == -1 || this._currentDataOffset == i) {
            return hasNextRecord() && this._nextSid == 60;
        }
        throw new IllegalStateException("Should never be called before end of current record");
    }

    public int getNextSid() {
        return this._nextSid;
    }

    @Internal
    public void mark(int readlimit) {
        ((InputStream) this._dataInput).mark(readlimit);
        this._markedDataOffset = this._currentDataOffset;
    }

    @Internal
    public void reset() throws IOException {
        ((InputStream) this._dataInput).reset();
        this._currentDataOffset = this._markedDataOffset;
    }
}
