package org.apache.poi.ddf;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Removal;

/* JADX INFO: loaded from: classes.dex */
public final class EscherContainerRecord extends EscherRecord implements Iterable<EscherRecord> {
    public static final short BSTORE_CONTAINER = -4095;
    public static final short DGG_CONTAINER = -4096;
    public static final short DG_CONTAINER = -4094;
    public static final short SOLVER_CONTAINER = -4091;
    public static final short SPGR_CONTAINER = -4093;
    public static final short SP_CONTAINER = -4092;
    private static final POILogger log = POILogFactory.getLogger((Class<?>) EscherContainerRecord.class);
    private final List<EscherRecord> _childRecords = new ArrayList();
    private int _remainingLength;

    @Override // org.apache.poi.ddf.EscherRecord
    public int fillFields(byte[] data, int pOffset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, pOffset);
        int bytesWritten = 8;
        int offset = pOffset + 8;
        while (bytesRemaining > 0 && offset < data.length) {
            EscherRecord child = recordFactory.createRecord(data, offset);
            int childBytesWritten = child.fillFields(data, offset, recordFactory);
            bytesWritten += childBytesWritten;
            offset += childBytesWritten;
            bytesRemaining -= childBytesWritten;
            addChildRecord(child);
            if (offset >= data.length && bytesRemaining > 0) {
                this._remainingLength = bytesRemaining;
                POILogger pOILogger = log;
                if (pOILogger.check(5)) {
                    pOILogger.log(5, "Not enough Escher data: " + bytesRemaining + " bytes remaining but no space left");
                }
            }
        }
        return bytesWritten;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);
        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        int remainingBytes = 0;
        for (EscherRecord r : this) {
            remainingBytes += r.getRecordSize();
        }
        LittleEndian.putInt(data, offset + 4, remainingBytes + this._remainingLength);
        int pos = offset + 8;
        for (EscherRecord r2 : this) {
            pos += r2.serialize(pos, data, listener);
        }
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public int getRecordSize() {
        int childRecordsSize = 0;
        for (EscherRecord r : this) {
            childRecordsSize += r.getRecordSize();
        }
        return childRecordsSize + 8;
    }

    public boolean hasChildOfType(short recordId) {
        for (EscherRecord r : this) {
            if (r.getRecordId() == recordId) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public EscherRecord getChild(int index) {
        return this._childRecords.get(index);
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public List<EscherRecord> getChildRecords() {
        return new ArrayList(this._childRecords);
    }

    @Removal(version = "3.18")
    @Deprecated
    public Iterator<EscherRecord> getChildIterator() {
        return iterator();
    }

    @Override // java.lang.Iterable
    public Iterator<EscherRecord> iterator() {
        return Collections.unmodifiableList(this._childRecords).iterator();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void setChildRecords(List<EscherRecord> childRecords) {
        List<EscherRecord> list = this._childRecords;
        if (childRecords == list) {
            throw new IllegalStateException("Child records private data member has escaped");
        }
        list.clear();
        this._childRecords.addAll(childRecords);
    }

    public boolean removeChildRecord(EscherRecord toBeRemoved) {
        return this._childRecords.remove(toBeRemoved);
    }

    public List<EscherContainerRecord> getChildContainers() {
        List<EscherContainerRecord> containers = new ArrayList<>();
        for (EscherRecord r : this) {
            if (r instanceof EscherContainerRecord) {
                containers.add((EscherContainerRecord) r);
            }
        }
        return containers;
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        switch (getRecordId()) {
            case -4096:
                return "DggContainer";
            case -4095:
                return "BStoreContainer";
            case -4094:
                return "DgContainer";
            case -4093:
                return "SpgrContainer";
            case -4092:
                return "SpContainer";
            case -4091:
                return "SolverContainer";
            default:
                return "Container 0x" + HexDump.toHex(getRecordId());
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void display(PrintWriter w, int indent) {
        super.display(w, indent);
        for (EscherRecord escherRecord : this) {
            escherRecord.display(w, indent + 1);
        }
    }

    public void addChildRecord(EscherRecord record) {
        this._childRecords.add(record);
    }

    public void addChildBefore(EscherRecord record, int insertBeforeRecordId) {
        int idx = 0;
        for (EscherRecord rec : this) {
            if (rec.getRecordId() == ((short) insertBeforeRecordId)) {
                break;
            } else {
                idx++;
            }
        }
        this._childRecords.add(idx, record);
    }

    public <T extends EscherRecord> T getChildById(short recordId) {
        Iterator<EscherRecord> it = iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (t.getRecordId() == recordId) {
                return t;
            }
        }
        return null;
    }

    public void getRecordsById(short recordId, List<EscherRecord> out) {
        for (EscherRecord r : this) {
            if (r instanceof EscherContainerRecord) {
                EscherContainerRecord c = (EscherContainerRecord) r;
                c.getRecordsById(recordId, out);
            } else if (r.getRecordId() == recordId) {
                out.add(r);
            }
        }
    }

    @Override // org.apache.poi.ddf.EscherRecord
    protected Object[][] getAttributeMap() {
        List<Object> chList = new ArrayList<>((this._childRecords.size() * 2) + 2);
        chList.add("children");
        chList.add(Integer.valueOf(this._childRecords.size()));
        int count = 0;
        for (EscherRecord record : this) {
            chList.add("Child " + count);
            chList.add(record);
            count++;
        }
        return new Object[][]{new Object[]{"isContainer", Boolean.valueOf(isContainerRecord())}, chList.toArray()};
    }
}
