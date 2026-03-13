package org.apache.poi.ss.util.cellwalk;

import org.apache.poi.ss.usermodel.Cell;

/* JADX INFO: loaded from: classes.dex */
public interface CellHandler {
    void onCell(Cell cell, CellWalkContext cellWalkContext);
}
