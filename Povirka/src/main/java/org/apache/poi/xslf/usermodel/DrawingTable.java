package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableRow;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class DrawingTable {
    private final CTTable table;

    public DrawingTable(CTTable table) {
        this.table = table;
    }

    public DrawingTableRow[] getRows() {
        CTTableRow[] ctTableRows = this.table.getTrArray();
        DrawingTableRow[] o = new DrawingTableRow[ctTableRows.length];
        for (int i = 0; i < o.length; i++) {
            o[i] = new DrawingTableRow(ctTableRows[i]);
        }
        return o;
    }
}
