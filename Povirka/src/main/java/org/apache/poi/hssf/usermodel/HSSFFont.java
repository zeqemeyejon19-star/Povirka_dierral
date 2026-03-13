package org.apache.poi.hssf.usermodel;

import androidx.core.view.InputDeviceCompat;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFFont implements Font {
    static final short BOLDWEIGHT_BOLD = 700;
    static final short BOLDWEIGHT_NORMAL = 400;
    public static final String FONT_ARIAL = "Arial";
    private FontRecord font;
    private short index;

    protected HSSFFont(short index, FontRecord rec) {
        this.font = rec;
        this.index = index;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontName(String name) {
        this.font.setFontName(name);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public String getFontName() {
        return this.font.getFontName();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getIndex() {
        return this.index;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontHeight(short height) {
        this.font.setFontHeight(height);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setFontHeightInPoints(short height) {
        this.font.setFontHeight((short) (height * 20));
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getFontHeight() {
        return this.font.getFontHeight();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getFontHeightInPoints() {
        return (short) (this.font.getFontHeight() / 20);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setItalic(boolean italic) {
        this.font.setItalic(italic);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getItalic() {
        return this.font.isItalic();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setStrikeout(boolean strikeout) {
        this.font.setStrikeout(strikeout);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getStrikeout() {
        return this.font.isStruckout();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setColor(short color) {
        this.font.setColorPaletteIndex(color);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getColor() {
        return this.font.getColorPaletteIndex();
    }

    public HSSFColor getHSSFColor(HSSFWorkbook wb) {
        HSSFPalette pallette = wb.getCustomPalette();
        return pallette.getColor(getColor());
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setBold(boolean bold) {
        if (bold) {
            this.font.setBoldWeight((short) 700);
        } else {
            this.font.setBoldWeight((short) 400);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public boolean getBold() {
        return this.font.getBoldWeight() == 700;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setTypeOffset(short offset) {
        this.font.setSuperSubScript(offset);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public short getTypeOffset() {
        return this.font.getSuperSubScript();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setUnderline(byte underline) {
        this.font.setUnderline(underline);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public byte getUnderline() {
        return this.font.getUnderline();
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public int getCharSet() {
        byte charset = this.font.getCharset();
        if (charset >= 0) {
            return charset;
        }
        return charset + 256;
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setCharSet(int charset) {
        byte cs = (byte) charset;
        if (charset > 127) {
            cs = (byte) (charset + InputDeviceCompat.SOURCE_ANY);
        }
        setCharSet(cs);
    }

    @Override // org.apache.poi.ss.usermodel.Font
    public void setCharSet(byte charset) {
        this.font.setCharset(charset);
    }

    public String toString() {
        return "org.apache.poi.hssf.usermodel.HSSFFont{" + this.font + "}";
    }

    public int hashCode() {
        int i = 1 * 31;
        FontRecord fontRecord = this.font;
        int result = i + (fontRecord == null ? 0 : fontRecord.hashCode());
        return (result * 31) + this.index;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof HSSFFont)) {
            return false;
        }
        HSSFFont other = (HSSFFont) obj;
        FontRecord fontRecord = this.font;
        if (fontRecord == null) {
            if (other.font != null) {
                return false;
            }
        } else if (!fontRecord.equals(other.font)) {
            return false;
        }
        if (this.index == other.index) {
            return true;
        }
        return false;
    }
}
