package org.apache.poi.xssf.usermodel;

import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FontCharset;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.FontScheme;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBooleanProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIntProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontScheme;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STVerticalAlignRun;

/* JADX INFO: loaded from: classes.dex */
public class XSSFFont implements Font {
    public static final short DEFAULT_FONT_COLOR = IndexedColors.BLACK.getIndex();
    public static final String DEFAULT_FONT_NAME = "Calibri";
    public static final short DEFAULT_FONT_SIZE = 11;
    private CTFont _ctFont;
    private short _index;
    private IndexedColorMap _indexedColorMap;
    private ThemesTable _themes;

    public XSSFFont(CTFont font) {
        this._ctFont = font;
        this._index = (short) 0;
    }

    public XSSFFont(CTFont font, int index, IndexedColorMap colorMap) {
        this._ctFont = font;
        this._index = (short) index;
        this._indexedColorMap = colorMap;
    }

    protected XSSFFont() {
        this._ctFont = CTFont.Factory.newInstance();
        setFontName(DEFAULT_FONT_NAME);
        setFontHeight(11.0d);
    }

    @Internal
    public CTFont getCTFont() {
        return this._ctFont;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getBold() {
        CTBooleanProperty bold = this._ctFont.sizeOfBArray() == 0 ? null : this._ctFont.getBArray(0);
        return bold != null && bold.getVal();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public int getCharSet() {
        CTIntProperty charset = this._ctFont.sizeOfCharsetArray() == 0 ? null : this._ctFont.getCharsetArray(0);
        int val = (charset == null ? FontCharset.ANSI : FontCharset.valueOf(charset.getVal())).getValue();
        return val;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getColor() {
        CTColor color = this._ctFont.sizeOfColorArray() == 0 ? null : this._ctFont.getColorArray(0);
        if (color == null) {
            return IndexedColors.BLACK.getIndex();
        }
        long index = color.getIndexed();
        if (index == DEFAULT_FONT_COLOR) {
            return IndexedColors.BLACK.getIndex();
        }
        if (index == IndexedColors.RED.getIndex()) {
            return IndexedColors.RED.getIndex();
        }
        return (short) index;
    }

    public XSSFColor getXSSFColor() {
        CTColor ctColor = this._ctFont.sizeOfColorArray() == 0 ? null : this._ctFont.getColorArray(0);
        if (ctColor == null) {
            return null;
        }
        XSSFColor color = new XSSFColor(ctColor, this._indexedColorMap);
        ThemesTable themesTable = this._themes;
        if (themesTable != null) {
            themesTable.inheritFromThemeAsRequired(color);
        }
        return color;
    }

    public short getThemeColor() {
        CTColor color = this._ctFont.sizeOfColorArray() == 0 ? null : this._ctFont.getColorArray(0);
        long index = color == null ? 0L : color.getTheme();
        return (short) index;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getFontHeight() {
        return (short) (getFontHeightRaw() * 20.0d);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getFontHeightInPoints() {
        return (short) getFontHeightRaw();
    }

    private double getFontHeightRaw() {
        CTFontSize size = this._ctFont.sizeOfSzArray() == 0 ? null : this._ctFont.getSzArray(0);
        if (size != null) {
            double fontHeight = size.getVal();
            return fontHeight;
        }
        return 11.0d;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public String getFontName() {
        CTFontName name = this._ctFont.sizeOfNameArray() == 0 ? null : this._ctFont.getNameArray(0);
        return name == null ? DEFAULT_FONT_NAME : name.getVal();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getItalic() {
        CTBooleanProperty italic = this._ctFont.sizeOfIArray() == 0 ? null : this._ctFont.getIArray(0);
        return italic != null && italic.getVal();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getStrikeout() {
        CTBooleanProperty strike = this._ctFont.sizeOfStrikeArray() == 0 ? null : this._ctFont.getStrikeArray(0);
        return strike != null && strike.getVal();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getTypeOffset() {
        int val;
        CTVerticalAlignFontProperty vAlign = this._ctFont.sizeOfVertAlignArray() == 0 ? null : this._ctFont.getVertAlignArray(0);
        if (vAlign == null || (val = vAlign.getVal().intValue()) == 1) {
            return (short) 0;
        }
        if (val == 2) {
            return (short) 1;
        }
        if (val == 3) {
            return (short) 2;
        }
        throw new POIXMLException("Wrong offset value " + val);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public byte getUnderline() {
        CTUnderlineProperty underline = this._ctFont.sizeOfUArray() == 0 ? null : this._ctFont.getUArray(0);
        if (underline == null) {
            return (byte) 0;
        }
        FontUnderline val = FontUnderline.valueOf(underline.getVal().intValue());
        return val.getByteValue();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setBold(boolean bold) {
        if (bold) {
            CTBooleanProperty ctBold = this._ctFont.sizeOfBArray() == 0 ? this._ctFont.addNewB() : this._ctFont.getBArray(0);
            ctBold.setVal(bold);
        } else {
            this._ctFont.setBArray((CTBooleanProperty[]) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setCharSet(byte charset) {
        int cs = charset & 255;
        setCharSet(cs);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setCharSet(int charset) {
        FontCharset fontCharset = FontCharset.valueOf(charset);
        if (fontCharset != null) {
            setCharSet(fontCharset);
            return;
        }
        throw new POIXMLException("Attention: an attempt to set a type of unknow charset and charset");
    }

    public void setCharSet(FontCharset charSet) {
        CTIntProperty charsetProperty;
        if (this._ctFont.sizeOfCharsetArray() == 0) {
            charsetProperty = this._ctFont.addNewCharset();
        } else {
            charsetProperty = this._ctFont.getCharsetArray(0);
        }
        charsetProperty.setVal(charSet.getValue());
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setColor(short color) {
        CTColor ctColor = this._ctFont.sizeOfColorArray() == 0 ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
        if (color == 10) {
            ctColor.setIndexed(IndexedColors.RED.getIndex());
        } else if (color == Short.MAX_VALUE) {
            ctColor.setIndexed(DEFAULT_FONT_COLOR);
        } else {
            ctColor.setIndexed(color);
        }
    }

    public void setColor(XSSFColor color) {
        if (color != null) {
            CTColor ctColor = this._ctFont.sizeOfColorArray() == 0 ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
            if (ctColor.isSetIndexed()) {
                ctColor.unsetIndexed();
            }
            ctColor.setRgb(color.getRGB());
            return;
        }
        this._ctFont.setColorArray((CTColor[]) null);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontHeight(short height) {
        setFontHeight(((double) height) / 20.0d);
    }

    public void setFontHeight(double height) {
        CTFontSize fontSize = this._ctFont.sizeOfSzArray() == 0 ? this._ctFont.addNewSz() : this._ctFont.getSzArray(0);
        fontSize.setVal(height);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontHeightInPoints(short height) {
        setFontHeight(height);
    }

    public void setThemeColor(short theme) {
        CTColor ctColor = this._ctFont.sizeOfColorArray() == 0 ? this._ctFont.addNewColor() : this._ctFont.getColorArray(0);
        ctColor.setTheme(theme);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontName(String name) {
        CTFontName fontName = this._ctFont.sizeOfNameArray() == 0 ? this._ctFont.addNewName() : this._ctFont.getNameArray(0);
        fontName.setVal(name == null ? DEFAULT_FONT_NAME : name);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setItalic(boolean italic) {
        if (italic) {
            CTBooleanProperty bool = this._ctFont.sizeOfIArray() == 0 ? this._ctFont.addNewI() : this._ctFont.getIArray(0);
            bool.setVal(italic);
        } else {
            this._ctFont.setIArray((CTBooleanProperty[]) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setStrikeout(boolean strikeout) {
        if (strikeout) {
            CTBooleanProperty strike = this._ctFont.sizeOfStrikeArray() == 0 ? this._ctFont.addNewStrike() : this._ctFont.getStrikeArray(0);
            strike.setVal(strikeout);
        } else {
            this._ctFont.setStrikeArray((CTBooleanProperty[]) null);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setTypeOffset(short offset) {
        if (offset == 0) {
            this._ctFont.setVertAlignArray((CTVerticalAlignFontProperty[]) null);
            return;
        }
        CTVerticalAlignFontProperty offsetProperty = this._ctFont.sizeOfVertAlignArray() == 0 ? this._ctFont.addNewVertAlign() : this._ctFont.getVertAlignArray(0);
        if (offset == 0) {
            offsetProperty.setVal(STVerticalAlignRun.BASELINE);
        } else if (offset == 1) {
            offsetProperty.setVal(STVerticalAlignRun.SUPERSCRIPT);
        } else {
            if (offset == 2) {
                offsetProperty.setVal(STVerticalAlignRun.SUBSCRIPT);
                return;
            }
            throw new IllegalStateException("Invalid type offset: " + ((int) offset));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setUnderline(byte underline) {
        setUnderline(FontUnderline.valueOf(underline));
    }

    public void setUnderline(FontUnderline underline) {
        if (underline == FontUnderline.NONE && this._ctFont.sizeOfUArray() > 0) {
            this._ctFont.setUArray((CTUnderlineProperty[]) null);
            return;
        }
        CTUnderlineProperty ctUnderline = this._ctFont.sizeOfUArray() == 0 ? this._ctFont.addNewU() : this._ctFont.getUArray(0);
        STUnderlineValues.Enum val = STUnderlineValues.Enum.forInt(underline.getValue());
        ctUnderline.setVal(val);
    }

    public String toString() {
        return this._ctFont.toString();
    }

    public long registerTo(StylesTable styles) {
        this._themes = styles.getTheme();
        short idx = (short) styles.putFont(this, true);
        this._index = idx;
        return idx;
    }

    public void setThemesTable(ThemesTable themes) {
        this._themes = themes;
    }

    public FontScheme getScheme() {
        CTFontScheme scheme = this._ctFont.sizeOfSchemeArray() == 0 ? null : this._ctFont.getSchemeArray(0);
        return scheme == null ? FontScheme.NONE : FontScheme.valueOf(scheme.getVal().intValue());
    }

    public void setScheme(FontScheme scheme) {
        CTFontScheme ctFontScheme = this._ctFont.sizeOfSchemeArray() == 0 ? this._ctFont.addNewScheme() : this._ctFont.getSchemeArray(0);
        STFontScheme.Enum val = STFontScheme.Enum.forInt(scheme.getValue());
        ctFontScheme.setVal(val);
    }

    public int getFamily() {
        CTIntProperty family = this._ctFont.sizeOfFamilyArray() == 0 ? null : this._ctFont.getFamilyArray(0);
        return (family == null ? FontFamily.NOT_APPLICABLE : FontFamily.valueOf(family.getVal())).getValue();
    }

    public void setFamily(int value) {
        CTIntProperty family = this._ctFont.sizeOfFamilyArray() == 0 ? this._ctFont.addNewFamily() : this._ctFont.getFamilyArray(0);
        family.setVal(value);
    }

    public void setFamily(FontFamily family) {
        setFamily(family.getValue());
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getIndex() {
        return this._index;
    }

    public int hashCode() {
        return this._ctFont.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof XSSFFont)) {
            return false;
        }
        XSSFFont cf = (XSSFFont) o;
        return this._ctFont.toString().equals(cf.getCTFont().toString());
    }
}
