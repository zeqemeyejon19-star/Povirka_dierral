package org.apache.poi.hssf.record.common;

import org.apache.poi.ss.util.CellRangeAddress;

/* JADX INFO: loaded from: classes.dex */
public interface FutureRecord {
    CellRangeAddress getAssociatedRange();

    FtrHeader getFutureHeader();

    short getFutureRecordType();
}
