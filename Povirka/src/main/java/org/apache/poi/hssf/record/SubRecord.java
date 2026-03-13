package org.apache.poi.hssf.record;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianOutputStream;

/* JADX INFO: loaded from: classes.dex */
public abstract class SubRecord {
    @Override // 
    /* JADX INFO: renamed from: clone */
    public abstract SubRecord mo9clone();

    protected abstract int getDataSize();

    public abstract void serialize(LittleEndianOutput littleEndianOutput);

    protected SubRecord() {
    }

    public static SubRecord createSubRecord(LittleEndianInput in, int cmoOt) {
        int sid = in.readUShort();
        int secondUShort = in.readUShort();
        if (sid == 0) {
            return new EndSubRecord(in, secondUShort);
        }
        if (sid == 19) {
            return new LbsDataSubRecord(in, secondUShort, cmoOt);
        }
        if (sid == 21) {
            return new CommonObjectDataSubRecord(in, secondUShort);
        }
        if (sid == 12) {
            return new FtCblsSubRecord(in, secondUShort);
        }
        if (sid != 13) {
            switch (sid) {
                case 6:
                    return new GroupMarkerSubRecord(in, secondUShort);
                case 7:
                    return new FtCfSubRecord(in, secondUShort);
                case 8:
                    return new FtPioGrbitSubRecord(in, secondUShort);
                case 9:
                    return new EmbeddedObjectRefSubRecord(in, secondUShort);
                default:
                    return new UnknownSubRecord(in, sid, secondUShort);
            }
        }
        return new NoteStructureSubRecord(in, secondUShort);
    }

    public byte[] serialize() {
        int size = getDataSize() + 4;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        serialize(new LittleEndianOutputStream(baos));
        if (baos.size() != size) {
            throw new RuntimeException("write size mismatch");
        }
        return baos.toByteArray();
    }

    public boolean isTerminating() {
        return false;
    }

    private static final class UnknownSubRecord extends SubRecord {
        private final byte[] _data;
        private final int _sid;

        public UnknownSubRecord(LittleEndianInput in, int sid, int size) {
            this._sid = sid;
            byte[] buf = new byte[size];
            in.readFully(buf);
            this._data = buf;
        }

        @Override // org.apache.poi.hssf.record.SubRecord
        protected int getDataSize() {
            return this._data.length;
        }

        @Override // org.apache.poi.hssf.record.SubRecord
        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._sid);
            out.writeShort(this._data.length);
            out.write(this._data);
        }

        @Override // org.apache.poi.hssf.record.SubRecord
        /* JADX INFO: renamed from: clone */
        public UnknownSubRecord mo9clone() {
            return this;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(getClass().getName()).append(" [");
            sb.append("sid=").append(HexDump.shortToHex(this._sid));
            sb.append(" size=").append(this._data.length);
            sb.append(" : ").append(HexDump.toHex(this._data));
            sb.append("]\n");
            return sb.toString();
        }
    }
}
