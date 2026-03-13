package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BitFieldFactory {
    private static Map<Integer, BitField> instances = new HashMap();

    public static BitField getInstance(int mask) {
        BitField f = instances.get(Integer.valueOf(mask));
        if (f == null) {
            BitField f2 = new BitField(mask);
            instances.put(Integer.valueOf(mask), f2);
            return f2;
        }
        return f;
    }
}
