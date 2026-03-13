package org.apache.poi.ddf;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public final class UnknownEscherRecord extends EscherRecord implements Cloneable {
    private static final byte[] NO_BYTES = new byte[0];
    private byte[] thedata = NO_BYTES;
    private List<EscherRecord> _childRecords = new ArrayList();

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int available = data.length - (offset + 8);
        if (bytesRemaining > available) {
            bytesRemaining = available;
        }
        if (isContainerRecord()) {
            this.thedata = new byte[0];
            int offset2 = offset + 8;
            int bytesWritten = 0 + 8;
            while (bytesRemaining > 0) {
                EscherRecord child = recordFactory.createRecord(data, offset2);
                int childBytesWritten = child.fillFields(data, offset2, recordFactory);
                bytesWritten += childBytesWritten;
                offset2 += childBytesWritten;
                bytesRemaining -= childBytesWritten;
                getChildRecords().add(child);
            }
            return bytesWritten;
        }
        if (bytesRemaining < 0) {
            bytesRemaining = 0;
        }
        byte[] bArr = new byte[bytesRemaining];
        this.thedata = bArr;
        System.arraycopy(data, offset + 8, bArr, 0, bytesRemaining);
        return bytesRemaining + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        int remainingBytes = this.thedata.length;
        for (EscherRecord r : this._childRecords) {
            remainingBytes += r.getRecordSize();
        }
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        byte[] bArr = this.thedata;
        System.arraycopy(bArr, 0, data, offset + 8, bArr.length);
        int pos = offset + 8 + this.thedata.length;
        for (EscherRecord r2 : this._childRecords) {
            pos += r2.serialize(pos, data, listener);
        }
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    public byte[] getData() {
        return this.thedata;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        return this.thedata.length + 8;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public List<EscherRecord> getChildRecords() {
        return this._childRecords;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void setChildRecords(List<EscherRecord> childRecords) {
        this._childRecords = childRecords;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public UnknownEscherRecord clone() {
        UnknownEscherRecord uer = new UnknownEscherRecord();
        uer.thedata = (byte[]) this.thedata.clone();
        uer.setOptions(getOptions());
        uer.setRecordId(getRecordId());
        return uer;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Unknown 0x" + HexDump.toHex(getRecordId());
    }

    public void addChildRecord(EscherRecord childRecord) {
        getChildRecords().add(childRecord);
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        int numCh = getChildRecords().size();
        List<Object> chLst = new ArrayList<>((numCh * 2) + 2);
        chLst.add("children");
        chLst.add(Integer.valueOf(numCh));
        for (EscherRecord er : this._childRecords) {
            chLst.add(er.getRecordName());
            chLst.add(er);
        }
        return new Object[][]{new Object[]{"isContainer", Boolean.valueOf(isContainerRecord())}, chLst.toArray(), new Object[]{"Extra Data", this.thedata}};
    }
}
