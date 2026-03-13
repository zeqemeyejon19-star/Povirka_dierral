package org.apache.poi.hssf.record;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInputStream;
import org.apache.poi.util.RecordFormatException;

/* JADX INFO: loaded from: classes.dex */
public final class ObjRecord extends Record implements Cloneable {
    private static int MAX_PAD_ALIGNMENT = 4;
    private static final int NORMAL_PAD_ALIGNMENT = 2;
    public static final short sid = 93;
    private boolean _isPaddedToQuadByteMultiple;
    private final byte[] _uninterpretedData;
    private List<SubRecord> subrecords;

    public ObjRecord() {
        this.subrecords = new ArrayList(2);
        this._uninterpretedData = null;
    }

    public ObjRecord(RecordInputStream in) {
        SubRecord subRecord;
        byte[] subRecordData = in.readRemainder();
        if (LittleEndian.getUShort(subRecordData, 0) != 21) {
            this._uninterpretedData = subRecordData;
            this.subrecords = null;
            return;
        }
        this.subrecords = new ArrayList();
        ByteArrayInputStream bais = new ByteArrayInputStream(subRecordData);
        LittleEndianInputStream subRecStream = new LittleEndianInputStream(bais);
        CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) SubRecord.createSubRecord(subRecStream, 0);
        this.subrecords.add(cmo);
        do {
            subRecord = SubRecord.createSubRecord(subRecStream, cmo.getObjectType());
            this.subrecords.add(subRecord);
        } while (!subRecord.isTerminating());
        int nRemainingBytes = bais.available();
        if (nRemainingBytes > 0) {
            int length = subRecordData.length;
            int i = MAX_PAD_ALIGNMENT;
            boolean z = length % i == 0;
            this._isPaddedToQuadByteMultiple = z;
            if (nRemainingBytes >= (z ? i : 2)) {
                if (!canPaddingBeDiscarded(subRecordData, nRemainingBytes)) {
                    String msg = "Leftover " + nRemainingBytes + " bytes in subrecord data " + HexDump.toHex(subRecordData);
                    throw new RecordFormatException(msg);
                }
                this._isPaddedToQuadByteMultiple = false;
            }
        } else {
            this._isPaddedToQuadByteMultiple = false;
        }
        this._uninterpretedData = null;
    }

    private static boolean canPaddingBeDiscarded(byte[] data, int nRemainingBytes) {
        for (int i = data.length - nRemainingBytes; i < data.length; i++) {
            if (data[i] != 0) {
                return false;
            }
        }
        return true;
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[OBJ]\n");
        List<SubRecord> list = this.subrecords;
        if (list != null) {
            for (SubRecord record : list) {
                sb.append("SUBRECORD: ").append(record);
            }
        }
        sb.append("[/OBJ]\n");
        return sb.toString();
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public int getRecordSize() {
        byte[] bArr = this._uninterpretedData;
        if (bArr != null) {
            return bArr.length + 4;
        }
        int size = 0;
        for (SubRecord record : this.subrecords) {
            size += record.getDataSize() + 4;
        }
        if (this._isPaddedToQuadByteMultiple) {
            while (size % MAX_PAD_ALIGNMENT != 0) {
                size++;
            }
        } else {
            while (size % 2 != 0) {
                size++;
            }
        }
        return size + 4;
    }

    @Override // org.apache.poi.hssf.record.RecordBase
    public int serialize(int offset, byte[] data) {
        int recSize = getRecordSize();
        int dataSize = recSize - 4;
        LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize);
        out.writeShort(93);
        out.writeShort(dataSize);
        byte[] bArr = this._uninterpretedData;
        if (bArr == null) {
            for (int i = 0; i < this.subrecords.size(); i++) {
                SubRecord record = this.subrecords.get(i);
                record.serialize(out);
            }
            int i2 = offset + dataSize;
            while (out.getWriteIndex() < i2) {
                out.writeByte(0);
            }
        } else {
            out.write(bArr);
        }
        return recSize;
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 93;
    }

    public List<SubRecord> getSubRecords() {
        return this.subrecords;
    }

    public void clearSubRecords() {
        this.subrecords.clear();
    }

    public void addSubRecord(int index, SubRecord element) {
        this.subrecords.add(index, element);
    }

    public boolean addSubRecord(SubRecord o) {
        return this.subrecords.add(o);
    }

    @Override // org.apache.poi.hssf.record.Record
    public ObjRecord clone() {
        ObjRecord rec = new ObjRecord();
        for (SubRecord record : this.subrecords) {
            rec.addSubRecord(record.mo9clone());
        }
        return rec;
    }
}
