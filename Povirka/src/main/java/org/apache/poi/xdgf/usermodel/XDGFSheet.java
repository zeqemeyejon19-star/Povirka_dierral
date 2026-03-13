package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.exceptions.XDGFException;
import org.apache.poi.xdgf.usermodel.section.CharacterSection;
import org.apache.poi.xdgf.usermodel.section.GeometrySection;
import org.apache.poi.xdgf.usermodel.section.XDGFSection;

/* JADX INFO: loaded from: classes.dex */
public abstract class XDGFSheet {
    protected CharacterSection _character;
    protected XDGFDocument _document;
    protected SheetType _sheet;
    protected Map<String, XDGFCell> _cells = new HashMap();
    protected Map<String, XDGFSection> _sections = new HashMap();
    protected SortedMap<Long, GeometrySection> _geometry = new TreeMap();

    /* JADX INFO: renamed from: getXmlObject */
    abstract SheetType mo24getXmlObject();

    public XDGFSheet(SheetType sheet, XDGFDocument document) {
        this._character = null;
        try {
            this._sheet = sheet;
            this._document = document;
            CellType[] arr$ = sheet.getCellArray();
            for (CellType cell : arr$) {
                if (this._cells.containsKey(cell.getN())) {
                    throw new POIXMLException("Unexpected duplicate cell " + cell.getN());
                }
                this._cells.put(cell.getN(), new XDGFCell(cell));
            }
            SectionType[] arr$2 = sheet.getSectionArray();
            for (SectionType section : arr$2) {
                String name = section.getN();
                if (name.equals("Geometry")) {
                    this._geometry.put(Long.valueOf(section.getIX()), new GeometrySection(section, this));
                } else if (name.equals("Character")) {
                    this._character = new CharacterSection(section, this);
                } else {
                    this._sections.put(name, XDGFSection.load(section, this));
                }
            }
        } catch (POIXMLException e) {
            throw XDGFException.wrap(toString(), e);
        }
    }

    public XDGFDocument getDocument() {
        return this._document;
    }

    public XDGFCell getCell(String cellName) {
        return this._cells.get(cellName);
    }

    public XDGFSection getSection(String sectionName) {
        return this._sections.get(sectionName);
    }

    public XDGFStyleSheet getLineStyle() {
        if (!this._sheet.isSetLineStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getLineStyle());
    }

    public XDGFStyleSheet getFillStyle() {
        if (!this._sheet.isSetFillStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getFillStyle());
    }

    public XDGFStyleSheet getTextStyle() {
        if (!this._sheet.isSetTextStyle()) {
            return null;
        }
        return this._document.getStyleById(this._sheet.getTextStyle());
    }

    public Color getFontColor() {
        Color fontColor;
        CharacterSection characterSection = this._character;
        if (characterSection != null && (fontColor = characterSection.getFontColor()) != null) {
            return fontColor;
        }
        XDGFStyleSheet style = getTextStyle();
        if (style != null) {
            return style.getFontColor();
        }
        return null;
    }

    public Double getFontSize() {
        Double fontSize;
        CharacterSection characterSection = this._character;
        if (characterSection != null && (fontSize = characterSection.getFontSize()) != null) {
            return fontSize;
        }
        XDGFStyleSheet style = getTextStyle();
        if (style != null) {
            return style.getFontSize();
        }
        return null;
    }

    public Integer getLineCap() {
        Integer lineCap = XDGFCell.maybeGetInteger(this._cells, "LineCap");
        if (lineCap != null) {
            return lineCap;
        }
        XDGFStyleSheet style = getLineStyle();
        if (style != null) {
            return style.getLineCap();
        }
        return null;
    }

    public Color getLineColor() {
        String lineColor = XDGFCell.maybeGetString(this._cells, "LineColor");
        if (lineColor != null) {
            return Color.decode(lineColor);
        }
        XDGFStyleSheet style = getLineStyle();
        if (style != null) {
            return style.getLineColor();
        }
        return null;
    }

    public Integer getLinePattern() {
        Integer linePattern = XDGFCell.maybeGetInteger(this._cells, "LinePattern");
        if (linePattern != null) {
            return linePattern;
        }
        XDGFStyleSheet style = getLineStyle();
        if (style != null) {
            return style.getLinePattern();
        }
        return null;
    }

    public Double getLineWeight() {
        Double lineWeight = XDGFCell.maybeGetDouble(this._cells, "LineWeight");
        if (lineWeight != null) {
            return lineWeight;
        }
        XDGFStyleSheet style = getLineStyle();
        if (style != null) {
            return style.getLineWeight();
        }
        return null;
    }
}
