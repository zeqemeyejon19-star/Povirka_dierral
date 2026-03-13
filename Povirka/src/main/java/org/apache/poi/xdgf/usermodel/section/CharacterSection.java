package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.RowType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xdgf.usermodel.XDGFCell;
import org.apache.poi.xdgf.usermodel.XDGFSheet;

/* JADX INFO: loaded from: classes.dex */
public class CharacterSection extends XDGFSection {
    Map<String, XDGFCell> _characterCells;
    Color _fontColor;
    Double _fontSize;

    public CharacterSection(SectionType section, XDGFSheet containingSheet) {
        super(section, containingSheet);
        this._fontSize = null;
        this._fontColor = null;
        this._characterCells = new HashMap();
        RowType row = section.getRowArray(0);
        CellType[] arr$ = row.getCellArray();
        for (CellType cell : arr$) {
            this._characterCells.put(cell.getN(), new XDGFCell(cell));
        }
        this._fontSize = XDGFCell.maybeGetDouble(this._characterCells, "Size");
        String tmpColor = XDGFCell.maybeGetString(this._characterCells, "Color");
        if (tmpColor != null) {
            this._fontColor = Color.decode(tmpColor);
        }
    }

    public Double getFontSize() {
        return this._fontSize;
    }

    public Color getFontColor() {
        return this._fontColor;
    }

    @Override // org.apache.poi.xdgf.usermodel.section.XDGFSection
    public void setupMaster(XDGFSection section) {
    }
}
