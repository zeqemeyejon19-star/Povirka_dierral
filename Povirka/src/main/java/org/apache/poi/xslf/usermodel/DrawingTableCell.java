package org.apache.poi.xslf.usermodel;

import org.apache.poi.util.Removal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;

/* JADX INFO: loaded from: classes.dex */
@Removal(version = "3.18")
public class DrawingTableCell {
    private final CTTableCell cell;
    private final DrawingTextBody drawingTextBody;

    public DrawingTableCell(CTTableCell cell) {
        this.cell = cell;
        this.drawingTextBody = new DrawingTextBody(cell.getTxBody());
    }

    public DrawingTextBody getTextBody() {
        return this.drawingTextBody;
    }
}
