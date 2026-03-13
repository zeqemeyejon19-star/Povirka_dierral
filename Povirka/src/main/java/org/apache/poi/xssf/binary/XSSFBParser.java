package org.apache.poi.xssf.binary;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

/* JADX INFO: loaded from: classes.dex */
@Internal
public abstract class XSSFBParser {
    private final LittleEndianInputStream is;
    private final BitSet records;

    public abstract void handleRecord(int i, byte[] bArr) throws XSSFBParseException;

    public XSSFBParser(InputStream is) {
        this.is = new LittleEndianInputStream(is);
        this.records = null;
    }

    protected XSSFBParser(InputStream is, BitSet bitSet) {
        this.is = new LittleEndianInputStream(is);
        this.records = bitSet;
    }

    public void parse() throws IOException {
        while (true) {
            int bInt = this.is.read();
            if (bInt == -1) {
                return;
            } else {
                readNext((byte) bInt);
            }
        }
    }

    private void readNext(byte b1) throws IOException {
        int recordId;
        if (((b1 >> 7) & 1) == 1) {
            byte b2 = this.is.readByte();
            recordId = (((byte) (b2 & (-129))) << 7) + ((byte) (b1 & (-129)));
        } else {
            recordId = b1;
        }
        long recordLength = 0;
        boolean halt = false;
        for (int i = 0; i < 4 && !halt; i++) {
            byte b = this.is.readByte();
            halt = ((b >> 7) & 1) == 0;
            recordLength += (long) (((byte) (b & (-129))) << (i * 7));
        }
        BitSet bitSet = this.records;
        if (bitSet == null || bitSet.get(recordId)) {
            byte[] buff = new byte[(int) recordLength];
            this.is.readFully(buff);
            handleRecord(recordId, buff);
        } else {
            long length = this.is.skip(recordLength);
            if (length != recordLength) {
                throw new XSSFBParseException("End of file reached before expected.\tTried to skip " + recordLength + ", but only skipped " + length);
            }
        }
    }
}
