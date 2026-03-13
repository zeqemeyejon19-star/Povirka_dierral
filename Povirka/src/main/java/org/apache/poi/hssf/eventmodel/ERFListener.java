package org.apache.poi.hssf.eventmodel;

import org.apache.poi.hssf.record.Record;

/* JADX INFO: loaded from: classes.dex */
public interface ERFListener {
    boolean processRecord(Record record);
}
