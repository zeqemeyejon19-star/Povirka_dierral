package org.apache.poi.xssf.usermodel;

import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellAlignment;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPatternFill;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFCellStyle implements CellStyle {
    private XSSFCellAlignment _cellAlignment;
    private final CTXf _cellStyleXf;
    private CTXf _cellXf;
    private int _cellXfId;
    private XSSFFont _font;
    private final StylesTable _stylesSource;
    private ThemesTable _theme;

    public XSSFCellStyle(int cellXfId, int cellStyleXfId, StylesTable stylesSource, ThemesTable theme) {
        this._cellXfId = cellXfId;
        this._stylesSource = stylesSource;
        this._cellXf = stylesSource.getCellXfAt(cellXfId);
        this._cellStyleXf = cellStyleXfId == -1 ? null : stylesSource.getCellStyleXfAt(cellStyleXfId);
        this._theme = theme;
    }

    @Internal
    public CTXf getCoreXf() {
        return this._cellXf;
    }

    @Internal
    public CTXf getStyleXf() {
        return this._cellStyleXf;
    }

    public XSSFCellStyle(StylesTable stylesSource) {
        this._stylesSource = stylesSource;
        this._cellXf = CTXf.Factory.newInstance();
        this._cellStyleXf = null;
    }

    public void verifyBelongsToStylesSource(StylesTable src) {
        if (this._stylesSource != src) {
            throw new IllegalArgumentException("This Style does not belong to the supplied Workbook Stlyes Source. Are you trying to assign a style from one workbook to the cell of a differnt workbook?");
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void cloneStyleFrom(CellStyle source) {
        if (source instanceof XSSFCellStyle) {
            XSSFCellStyle src = (XSSFCellStyle) source;
            if (src._stylesSource == this._stylesSource) {
                this._cellXf.set(src.getCoreXf());
                this._cellStyleXf.set(src.getStyleXf());
            } else {
                try {
                    if (this._cellXf.isSetAlignment()) {
                        this._cellXf.unsetAlignment();
                    }
                    if (this._cellXf.isSetExtLst()) {
                        this._cellXf.unsetExtLst();
                    }
                    this._cellXf = CTXf.Factory.parse(src.getCoreXf().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    CTFill fill = CTFill.Factory.parse(src.getCTFill().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    addFill(fill);
                    CTBorder border = CTBorder.Factory.parse(src.getCTBorder().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    addBorder(border);
                    this._stylesSource.replaceCellXfAt(this._cellXfId, this._cellXf);
                    String fmt = src.getDataFormatString();
                    setDataFormat(new XSSFDataFormat(this._stylesSource).getFormat(fmt));
                    try {
                        CTFont ctFont = CTFont.Factory.parse(src.getFont().getCTFont().toString(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                        XSSFFont font = new XSSFFont(ctFont);
                        font.registerTo(this._stylesSource);
                        setFont(font);
                    } catch (XmlException e) {
                        throw new POIXMLException((Throwable) e);
                    }
                } catch (XmlException e2) {
                    throw new POIXMLException((Throwable) e2);
                }
            }
            this._font = null;
            this._cellAlignment = null;
            return;
        }
        throw new IllegalArgumentException("Can only clone from one XSSFCellStyle to another, not between HSSFCellStyle and XSSFCellStyle");
    }

    private void addFill(CTFill fill) {
        int idx = this._stylesSource.putFill(new XSSFCellFill(fill, this._stylesSource.getIndexedColors()));
        this._cellXf.setFillId(idx);
        this._cellXf.setApplyFill(true);
    }

    private void addBorder(CTBorder border) {
        int idx = this._stylesSource.putBorder(new XSSFCellBorder(border, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId(idx);
        this._cellXf.setApplyBorder(true);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getAlignment() {
        return getAlignmentEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public HorizontalAlignment getAlignmentEnum() {
        CTCellAlignment align = this._cellXf.getAlignment();
        if (align != null && align.isSetHorizontal()) {
            return HorizontalAlignment.forInt(align.getHorizontal().intValue() - 1);
        }
        return HorizontalAlignment.GENERAL;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public BorderStyle getBorderBottomEnum() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        int idx = (int) this._cellXf.getBorderId();
        CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        STBorderStyle.Enum ptrn = ct.isSetBottom() ? ct.getBottom().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short) (ptrn.intValue() - 1));
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getBorderBottom() {
        return getBorderBottomEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public BorderStyle getBorderLeftEnum() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        int idx = (int) this._cellXf.getBorderId();
        CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        STBorderStyle.Enum ptrn = ct.isSetLeft() ? ct.getLeft().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short) (ptrn.intValue() - 1));
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getBorderLeft() {
        return getBorderLeftEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public BorderStyle getBorderRightEnum() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        int idx = (int) this._cellXf.getBorderId();
        CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        STBorderStyle.Enum ptrn = ct.isSetRight() ? ct.getRight().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short) (ptrn.intValue() - 1));
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getBorderRight() {
        return getBorderRightEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public BorderStyle getBorderTopEnum() {
        if (!this._cellXf.getApplyBorder()) {
            return BorderStyle.NONE;
        }
        int idx = (int) this._cellXf.getBorderId();
        CTBorder ct = this._stylesSource.getBorderAt(idx).getCTBorder();
        STBorderStyle.Enum ptrn = ct.isSetTop() ? ct.getTop().getStyle() : null;
        if (ptrn == null) {
            return BorderStyle.NONE;
        }
        return BorderStyle.valueOf((short) (ptrn.intValue() - 1));
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getBorderTop() {
        return getBorderTopEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getBottomBorderColor() {
        XSSFColor clr = getBottomBorderXSSFColor();
        return clr == null ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }

    public XSSFColor getBottomBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        int idx = (int) this._cellXf.getBorderId();
        XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.BOTTOM);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getDataFormat() {
        return (short) this._cellXf.getNumFmtId();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public String getDataFormatString() {
        int idx = getDataFormat();
        return new XSSFDataFormat(this._stylesSource).getFormat((short) idx);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getFillBackgroundColor() {
        XSSFColor clr = getFillBackgroundXSSFColor();
        return clr == null ? IndexedColors.AUTOMATIC.getIndex() : clr.getIndexed();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public XSSFColor getFillBackgroundColorColor() {
        return getFillBackgroundXSSFColor();
    }

    public XSSFColor getFillBackgroundXSSFColor() {
        ThemesTable themesTable;
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return null;
        }
        int fillIndex = (int) this._cellXf.getFillId();
        XSSFCellFill fg = this._stylesSource.getFillAt(fillIndex);
        XSSFColor fillBackgroundColor = fg.getFillBackgroundColor();
        if (fillBackgroundColor != null && (themesTable = this._theme) != null) {
            themesTable.inheritFromThemeAsRequired(fillBackgroundColor);
        }
        return fillBackgroundColor;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getFillForegroundColor() {
        XSSFColor clr = getFillForegroundXSSFColor();
        return clr == null ? IndexedColors.AUTOMATIC.getIndex() : clr.getIndexed();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public XSSFColor getFillForegroundColorColor() {
        return getFillForegroundXSSFColor();
    }

    public XSSFColor getFillForegroundXSSFColor() {
        ThemesTable themesTable;
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return null;
        }
        int fillIndex = (int) this._cellXf.getFillId();
        XSSFCellFill fg = this._stylesSource.getFillAt(fillIndex);
        XSSFColor fillForegroundColor = fg.getFillForegroundColor();
        if (fillForegroundColor != null && (themesTable = this._theme) != null) {
            themesTable.inheritFromThemeAsRequired(fillForegroundColor);
        }
        return fillForegroundColor;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getFillPattern() {
        return getFillPatternEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public FillPatternType getFillPatternEnum() {
        if (this._cellXf.isSetApplyFill() && !this._cellXf.getApplyFill()) {
            return FillPatternType.NO_FILL;
        }
        int fillIndex = (int) this._cellXf.getFillId();
        XSSFCellFill fill = this._stylesSource.getFillAt(fillIndex);
        STPatternType.Enum ptrn = fill.getPatternType();
        return ptrn == null ? FillPatternType.NO_FILL : FillPatternType.forInt(ptrn.intValue() - 1);
    }

    public XSSFFont getFont() {
        if (this._font == null) {
            this._font = this._stylesSource.getFontAt(getFontId());
        }
        return this._font;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getFontIndex() {
        return (short) getFontId();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public boolean getHidden() {
        if (!this._cellXf.isSetProtection() || !this._cellXf.getProtection().isSetHidden()) {
            return false;
        }
        return this._cellXf.getProtection().getHidden();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getIndention() {
        CTCellAlignment align = this._cellXf.getAlignment();
        return (short) (align == null ? 0L : align.getIndent());
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getIndex() {
        return (short) this._cellXfId;
    }

    protected int getUIndex() {
        return this._cellXfId;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getLeftBorderColor() {
        XSSFColor clr = getLeftBorderXSSFColor();
        return clr == null ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }

    public XSSFColor getLeftBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        int idx = (int) this._cellXf.getBorderId();
        XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.LEFT);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public boolean getLocked() {
        if (!this._cellXf.isSetProtection() || !this._cellXf.getProtection().isSetLocked()) {
            return true;
        }
        return this._cellXf.getProtection().getLocked();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public boolean getQuotePrefixed() {
        return this._cellXf.getQuotePrefix();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getRightBorderColor() {
        XSSFColor clr = getRightBorderXSSFColor();
        return clr == null ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }

    public XSSFColor getRightBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        int idx = (int) this._cellXf.getBorderId();
        XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.RIGHT);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getRotation() {
        CTCellAlignment align = this._cellXf.getAlignment();
        return (short) (align == null ? 0L : align.getTextRotation());
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public boolean getShrinkToFit() {
        CTCellAlignment align = this._cellXf.getAlignment();
        return align != null && align.getShrinkToFit();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getTopBorderColor() {
        XSSFColor clr = getTopBorderXSSFColor();
        return clr == null ? IndexedColors.BLACK.getIndex() : clr.getIndexed();
    }

    public XSSFColor getTopBorderXSSFColor() {
        if (!this._cellXf.getApplyBorder()) {
            return null;
        }
        int idx = (int) this._cellXf.getBorderId();
        XSSFCellBorder border = this._stylesSource.getBorderAt(idx);
        return border.getBorderColor(XSSFCellBorder.BorderSide.TOP);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public short getVerticalAlignment() {
        return getVerticalAlignmentEnum().getCode();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public VerticalAlignment getVerticalAlignmentEnum() {
        CTCellAlignment align = this._cellXf.getAlignment();
        if (align != null && align.isSetVertical()) {
            return VerticalAlignment.forInt(align.getVertical().intValue() - 1);
        }
        return VerticalAlignment.BOTTOM;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public boolean getWrapText() {
        CTCellAlignment align = this._cellXf.getAlignment();
        return align != null && align.getWrapText();
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setAlignment(HorizontalAlignment align) {
        getCellAlignment().setHorizontal(align);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setBorderBottom(BorderStyle border) {
        CTBorder ct = getCTBorder();
        CTBorderPr pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
        if (border == BorderStyle.NONE) {
            ct.unsetBottom();
        } else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId(idx);
        this._cellXf.setApplyBorder(true);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setBorderLeft(BorderStyle border) {
        CTBorder ct = getCTBorder();
        CTBorderPr pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
        if (border == BorderStyle.NONE) {
            ct.unsetLeft();
        } else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId(idx);
        this._cellXf.setApplyBorder(true);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setBorderRight(BorderStyle border) {
        CTBorder ct = getCTBorder();
        CTBorderPr pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
        if (border == BorderStyle.NONE) {
            ct.unsetRight();
        } else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId(idx);
        this._cellXf.setApplyBorder(true);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setBorderTop(BorderStyle border) {
        CTBorder ct = getCTBorder();
        CTBorderPr pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
        if (border == BorderStyle.NONE) {
            ct.unsetTop();
        } else {
            pr.setStyle(STBorderStyle.Enum.forInt(border.getCode() + 1));
        }
        int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
        this._cellXf.setBorderId(idx);
        this._cellXf.setApplyBorder(true);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setBottomBorderColor(short color) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(color);
        setBottomBorderColor(clr);
    }

    public void setBottomBorderColor(XSSFColor color) {
        CTBorder ct = getCTBorder();
        if (color != null || ct.isSetBottom()) {
            CTBorderPr pr = ct.isSetBottom() ? ct.getBottom() : ct.addNewBottom();
            if (color != null) {
                pr.setColor(color.getCTColor());
            } else {
                pr.unsetColor();
            }
            int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
            this._cellXf.setBorderId(idx);
            this._cellXf.setApplyBorder(true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setDataFormat(short fmt) {
        setDataFormat(65535 & fmt);
    }

    public void setDataFormat(int fmt) {
        this._cellXf.setApplyNumberFormat(true);
        this._cellXf.setNumFmtId(fmt);
    }

    public void setFillBackgroundColor(XSSFColor color) {
        CTFill ct = getCTFill();
        CTPatternFill ptrn = ct.getPatternFill();
        if (color == null) {
            if (ptrn != null && ptrn.isSetBgColor()) {
                ptrn.unsetBgColor();
            }
        } else {
            if (ptrn == null) {
                ptrn = ct.addNewPatternFill();
            }
            ptrn.setBgColor(color.getCTColor());
        }
        addFill(ct);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setFillBackgroundColor(short bg) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(bg);
        setFillBackgroundColor(clr);
    }

    public void setFillForegroundColor(XSSFColor color) {
        CTFill ct = getCTFill();
        CTPatternFill ptrn = ct.getPatternFill();
        if (color == null) {
            if (ptrn != null && ptrn.isSetFgColor()) {
                ptrn.unsetFgColor();
            }
        } else {
            if (ptrn == null) {
                ptrn = ct.addNewPatternFill();
            }
            ptrn.setFgColor(color.getCTColor());
        }
        addFill(ct);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setFillForegroundColor(short fg) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(fg);
        setFillForegroundColor(clr);
    }

    private CTFill getCTFill() {
        if (!this._cellXf.isSetApplyFill() || this._cellXf.getApplyFill()) {
            int fillIndex = (int) this._cellXf.getFillId();
            XSSFCellFill cf = this._stylesSource.getFillAt(fillIndex);
            CTFill ct = cf.getCTFill().copy();
            return ct;
        }
        CTFill ct2 = CTFill.Factory.newInstance();
        return ct2;
    }

    private CTBorder getCTBorder() {
        if (this._cellXf.getApplyBorder()) {
            int idx = (int) this._cellXf.getBorderId();
            XSSFCellBorder cf = this._stylesSource.getBorderAt(idx);
            CTBorder ct = cf.getCTBorder().copy();
            return ct;
        }
        CTBorder ct2 = CTBorder.Factory.newInstance();
        return ct2;
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setFillPattern(FillPatternType pattern) {
        CTFill ct = getCTFill();
        CTPatternFill ctptrn = ct.isSetPatternFill() ? ct.getPatternFill() : ct.addNewPatternFill();
        if (pattern == FillPatternType.NO_FILL && ctptrn.isSetPatternType()) {
            ctptrn.unsetPatternType();
        } else {
            ctptrn.setPatternType(STPatternType.Enum.forInt(pattern.getCode() + 1));
        }
        addFill(ct);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setFont(Font font) {
        if (font != null) {
            long index = font.getIndex();
            this._cellXf.setFontId(index);
            this._cellXf.setApplyFont(true);
            return;
        }
        this._cellXf.setApplyFont(false);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setHidden(boolean hidden) {
        if (!this._cellXf.isSetProtection()) {
            this._cellXf.addNewProtection();
        }
        this._cellXf.getProtection().setHidden(hidden);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setIndention(short indent) {
        getCellAlignment().setIndent(indent);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setLeftBorderColor(short color) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(color);
        setLeftBorderColor(clr);
    }

    public void setLeftBorderColor(XSSFColor color) {
        CTBorder ct = getCTBorder();
        if (color != null || ct.isSetLeft()) {
            CTBorderPr pr = ct.isSetLeft() ? ct.getLeft() : ct.addNewLeft();
            if (color != null) {
                pr.setColor(color.getCTColor());
            } else {
                pr.unsetColor();
            }
            int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
            this._cellXf.setBorderId(idx);
            this._cellXf.setApplyBorder(true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setLocked(boolean locked) {
        if (!this._cellXf.isSetProtection()) {
            this._cellXf.addNewProtection();
        }
        this._cellXf.getProtection().setLocked(locked);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setQuotePrefixed(boolean quotePrefix) {
        this._cellXf.setQuotePrefix(quotePrefix);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setRightBorderColor(short color) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(color);
        setRightBorderColor(clr);
    }

    public void setRightBorderColor(XSSFColor color) {
        CTBorder ct = getCTBorder();
        if (color != null || ct.isSetRight()) {
            CTBorderPr pr = ct.isSetRight() ? ct.getRight() : ct.addNewRight();
            if (color != null) {
                pr.setColor(color.getCTColor());
            } else {
                pr.unsetColor();
            }
            int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
            this._cellXf.setBorderId(idx);
            this._cellXf.setApplyBorder(true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setRotation(short rotation) {
        getCellAlignment().setTextRotation(rotation);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setTopBorderColor(short color) {
        XSSFColor clr = new XSSFColor();
        clr.setIndexed(color);
        setTopBorderColor(clr);
    }

    public void setTopBorderColor(XSSFColor color) {
        CTBorder ct = getCTBorder();
        if (color != null || ct.isSetTop()) {
            CTBorderPr pr = ct.isSetTop() ? ct.getTop() : ct.addNewTop();
            if (color != null) {
                pr.setColor(color.getCTColor());
            } else {
                pr.unsetColor();
            }
            int idx = this._stylesSource.putBorder(new XSSFCellBorder(ct, this._theme, this._stylesSource.getIndexedColors()));
            this._cellXf.setBorderId(idx);
            this._cellXf.setApplyBorder(true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setVerticalAlignment(VerticalAlignment align) {
        getCellAlignment().setVertical(align);
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setWrapText(boolean wrapped) {
        getCellAlignment().setWrapText(wrapped);
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.XSSFCellStyle$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide;

        static {
            int[] iArr = new int[XSSFCellBorder.BorderSide.values().length];
            $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide = iArr;
            try {
                iArr[XSSFCellBorder.BorderSide.BOTTOM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[XSSFCellBorder.BorderSide.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[XSSFCellBorder.BorderSide.TOP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[XSSFCellBorder.BorderSide.LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public XSSFColor getBorderColor(XSSFCellBorder.BorderSide side) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[side.ordinal()];
        if (i == 1) {
            return getBottomBorderXSSFColor();
        }
        if (i == 2) {
            return getRightBorderXSSFColor();
        }
        if (i == 3) {
            return getTopBorderXSSFColor();
        }
        if (i == 4) {
            return getLeftBorderXSSFColor();
        }
        throw new IllegalArgumentException("Unknown border: " + side);
    }

    public void setBorderColor(XSSFCellBorder.BorderSide side, XSSFColor color) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[side.ordinal()];
        if (i == 1) {
            setBottomBorderColor(color);
            return;
        }
        if (i == 2) {
            setRightBorderColor(color);
        } else if (i == 3) {
            setTopBorderColor(color);
        } else if (i == 4) {
            setLeftBorderColor(color);
        }
    }

    @Override // org.apache.poi.ss.usermodel.CellStyle
    public void setShrinkToFit(boolean shrinkToFit) {
        getCellAlignment().setShrinkToFit(shrinkToFit);
    }

    private int getFontId() {
        if (this._cellXf.isSetFontId()) {
            return (int) this._cellXf.getFontId();
        }
        return (int) this._cellStyleXf.getFontId();
    }

    protected XSSFCellAlignment getCellAlignment() {
        if (this._cellAlignment == null) {
            this._cellAlignment = new XSSFCellAlignment(getCTCellAlignment());
        }
        return this._cellAlignment;
    }

    private CTCellAlignment getCTCellAlignment() {
        if (this._cellXf.getAlignment() == null) {
            this._cellXf.setAlignment(CTCellAlignment.Factory.newInstance());
        }
        return this._cellXf.getAlignment();
    }

    public int hashCode() {
        return this._cellXf.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof XSSFCellStyle)) {
            return false;
        }
        XSSFCellStyle cf = (XSSFCellStyle) o;
        return this._cellXf.toString().equals(cf.getCoreXf().toString());
    }

    public Object clone() {
        CTXf xf = this._cellXf.copy();
        int xfSize = this._stylesSource._getStyleXfsSize();
        int indexXf = this._stylesSource.putCellXf(xf);
        return new XSSFCellStyle(indexXf - 1, xfSize - 1, this._stylesSource, this._theme);
    }
}
