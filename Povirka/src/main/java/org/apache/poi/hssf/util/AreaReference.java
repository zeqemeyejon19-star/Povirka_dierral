package org.apache.poi.hssf.util;

import org.apache.poi.ss.SpreadsheetVersion;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class AreaReference extends org.apache.poi.ss.util.AreaReference {
    public AreaReference(String reference) {
        super(reference, SpreadsheetVersion.EXCEL97);
    }

    public AreaReference(CellReference topLeft, CellReference botRight) {
        super(topLeft, botRight);
    }
}
