package org.apache.poi.ddf;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

/* JADX INFO: loaded from: classes.dex */
public class DefaultEscherRecordFactory implements EscherRecordFactory {
    private static Class<?>[] escherRecordClasses;
    private static Map<Short, Constructor<? extends EscherRecord>> recordsMap;

    static {
        Class<?>[] clsArr = {EscherBSERecord.class, EscherOptRecord.class, EscherTertiaryOptRecord.class, EscherClientAnchorRecord.class, EscherDgRecord.class, EscherSpgrRecord.class, EscherSpRecord.class, EscherClientDataRecord.class, EscherDggRecord.class, EscherSplitMenuColorsRecord.class, EscherChildAnchorRecord.class, EscherTextboxRecord.class};
        escherRecordClasses = clsArr;
        recordsMap = recordsToMap(clsArr);
    }

    @Override // org.apache.poi.ddf.EscherRecordFactory
    public EscherRecord createRecord(byte[] data, int offset) {
        EscherBlipRecord r;
        short options = LittleEndian.getShort(data, offset);
        short recordId = LittleEndian.getShort(data, offset + 2);
        if (isContainer(options, recordId)) {
            EscherContainerRecord r2 = new EscherContainerRecord();
            r2.setRecordId(recordId);
            r2.setOptions(options);
            return r2;
        }
        if (recordId >= -4072 && recordId <= -3817) {
            if (recordId == -4065 || recordId == -4067 || recordId == -4066) {
                r = new EscherBitmapBlip();
            } else if (recordId == -4070 || recordId == -4069 || recordId == -4068) {
                r = new EscherMetafileBlip();
            } else {
                r = new EscherBlipRecord();
            }
            r.setRecordId(recordId);
            r.setOptions(options);
            return r;
        }
        Constructor<? extends EscherRecord> recordConstructor = recordsMap.get(Short.valueOf(recordId));
        if (recordConstructor == null) {
            return new UnknownEscherRecord();
        }
        try {
            EscherRecord escherRecord = recordConstructor.newInstance(new Object[0]);
            escherRecord.setRecordId(recordId);
            escherRecord.setOptions(options);
            return escherRecord;
        } catch (Exception e) {
            return new UnknownEscherRecord();
        }
    }

    protected static Map<Short, Constructor<? extends EscherRecord>> recordsToMap(Class<?>[] recClasses) {
        HashMap map = new HashMap();
        Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
        for (Class<?> recClass : recClasses) {
            try {
                short sid = recClass.getField("RECORD_ID").getShort(null);
                try {
                    map.put(Short.valueOf(sid), recClass.getConstructor(EMPTY_CLASS_ARRAY));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2);
            } catch (IllegalArgumentException e3) {
                throw new RuntimeException(e3);
            } catch (NoSuchFieldException e4) {
                throw new RuntimeException(e4);
            }
        }
        return map;
    }

    public static boolean isContainer(short options, short recordId) {
        if (recordId < -4096 || recordId > -4091) {
            return recordId != -4083 && (options & 15) == 15;
        }
        return true;
    }
}
