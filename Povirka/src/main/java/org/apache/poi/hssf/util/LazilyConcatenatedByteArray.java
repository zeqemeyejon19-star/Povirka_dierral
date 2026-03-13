package org.apache.poi.hssf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LazilyConcatenatedByteArray {
    private final List<byte[]> arrays = new ArrayList(1);

    public void clear() {
        this.arrays.clear();
    }

    public void concatenate(byte[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array cannot be null");
        }
        this.arrays.add(array);
    }

    public byte[] toArray() {
        if (this.arrays.isEmpty()) {
            return null;
        }
        if (this.arrays.size() > 1) {
            int totalLength = 0;
            Iterator<byte[]> it = this.arrays.iterator();
            while (it.hasNext()) {
                totalLength += it.next().length;
            }
            byte[] concatenated = new byte[totalLength];
            int destPos = 0;
            for (byte[] array : this.arrays) {
                System.arraycopy(array, 0, concatenated, destPos, array.length);
                destPos += array.length;
            }
            this.arrays.clear();
            this.arrays.add(concatenated);
        }
        return this.arrays.get(0);
    }
}
