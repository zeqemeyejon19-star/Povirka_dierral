package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.util.LittleEndianOutput;

/* JADX INFO: loaded from: classes.dex */
public abstract class PageBreakRecord extends StandardRecord {
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private Map<Integer, Break> _breakMap;
    private List<Break> _breaks;

    public static final class Break {
        public static final int ENCODED_SIZE = 6;
        public int main;
        public int subFrom;
        public int subTo;

        public Break(int main, int subFrom, int subTo) {
            this.main = main;
            this.subFrom = subFrom;
            this.subTo = subTo;
        }

        public Break(RecordInputStream in) {
            this.main = in.readUShort() - 1;
            this.subFrom = in.readUShort();
            this.subTo = in.readUShort();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this.main + 1);
            out.writeShort(this.subFrom);
            out.writeShort(this.subTo);
        }
    }

    protected PageBreakRecord() {
        this._breaks = new ArrayList();
        this._breakMap = new HashMap();
    }

    public PageBreakRecord(RecordInputStream in) {
        int nBreaks = in.readShort();
        this._breaks = new ArrayList(nBreaks + 2);
        this._breakMap = new HashMap();
        for (int k = 0; k < nBreaks; k++) {
            Break br = new Break(in);
            this._breaks.add(br);
            this._breakMap.put(Integer.valueOf(br.main), br);
        }
    }

    public boolean isEmpty() {
        return this._breaks.isEmpty();
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    protected int getDataSize() {
        return (this._breaks.size() * 6) + 2;
    }

    @Override // org.apache.poi.hssf.record.StandardRecord
    public final void serialize(LittleEndianOutput out) {
        int nBreaks = this._breaks.size();
        out.writeShort(nBreaks);
        for (int i = 0; i < nBreaks; i++) {
            this._breaks.get(i).serialize(out);
        }
    }

    public int getNumBreaks() {
        return this._breaks.size();
    }

    public final Iterator<Break> getBreaksIterator() {
        return this._breaks.iterator();
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        String label;
        String mainLabel;
        String subLabel;
        StringBuffer retval = new StringBuffer();
        if (getSid() == 27) {
            label = "HORIZONTALPAGEBREAK";
            mainLabel = "row";
            subLabel = "col";
        } else {
            label = "VERTICALPAGEBREAK";
            mainLabel = "column";
            subLabel = "row";
        }
        retval.append("[" + label + "]").append("\n");
        retval.append("     .sid        =").append((int) getSid()).append("\n");
        retval.append("     .numbreaks =").append(getNumBreaks()).append("\n");
        Iterator<Break> iterator = getBreaksIterator();
        for (int k = 0; k < getNumBreaks(); k++) {
            Break region = iterator.next();
            retval.append("     .").append(mainLabel).append(" (zero-based) =").append(region.main).append("\n");
            retval.append("     .").append(subLabel).append("From    =").append(region.subFrom).append("\n");
            retval.append("     .").append(subLabel).append("To      =").append(region.subTo).append("\n");
        }
        retval.append("[" + label + "]").append("\n");
        return retval.toString();
    }

    public void addBreak(int main, int subFrom, int subTo) {
        Integer key = Integer.valueOf(main);
        Break region = this._breakMap.get(key);
        if (region == null) {
            Break region2 = new Break(main, subFrom, subTo);
            this._breakMap.put(key, region2);
            this._breaks.add(region2);
        } else {
            region.main = main;
            region.subFrom = subFrom;
            region.subTo = subTo;
        }
    }

    public final void removeBreak(int main) {
        Integer rowKey = Integer.valueOf(main);
        Break region = this._breakMap.get(rowKey);
        this._breaks.remove(region);
        this._breakMap.remove(rowKey);
    }

    public final Break getBreak(int main) {
        Integer rowKey = Integer.valueOf(main);
        return this._breakMap.get(rowKey);
    }

    public final int[] getBreaks() {
        int count = getNumBreaks();
        if (count < 1) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            Break breakItem = this._breaks.get(i);
            result[i] = breakItem.main;
        }
        return result;
    }
}
