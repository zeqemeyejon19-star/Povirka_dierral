package org.apache.poi.hssf.eventusermodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;

/* JADX INFO: loaded from: classes.dex */
public class HSSFRequest {
    private final Map<Short, List<HSSFListener>> _records = new HashMap(50);

    public void addListener(HSSFListener lsnr, short sid) {
        List<HSSFListener> list = this._records.get(Short.valueOf(sid));
        if (list == null) {
            list = new ArrayList(1);
            this._records.put(Short.valueOf(sid), list);
        }
        list.add(lsnr);
    }

    public void addListenerForAllRecords(HSSFListener lsnr) {
        short[] rectypes = RecordFactory.getAllKnownRecordSIDs();
        for (short rectype : rectypes) {
            addListener(lsnr, rectype);
        }
    }

    protected short processRecord(Record rec) throws HSSFUserException {
        List<HSSFListener> listeners = this._records.get(Short.valueOf(rec.getSid()));
        short userCode = 0;
        if (listeners != null) {
            for (int k = 0; k < listeners.size(); k++) {
                Object listenObj = listeners.get(k);
                if (listenObj instanceof AbortableHSSFListener) {
                    AbortableHSSFListener listener = (AbortableHSSFListener) listenObj;
                    userCode = listener.abortableProcessRecord(rec);
                    if (userCode != 0) {
                        break;
                    }
                } else {
                    HSSFListener listener2 = (HSSFListener) listenObj;
                    listener2.processRecord(rec);
                }
            }
        }
        return userCode;
    }
}
