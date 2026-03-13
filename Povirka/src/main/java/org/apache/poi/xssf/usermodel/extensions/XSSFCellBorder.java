package org.apache.poi.xssf.usermodel.extensions;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorder;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorderPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

/* JADX INFO: loaded from: classes.dex */
public class XSSFCellBorder {
    private IndexedColorMap _indexedColorMap;
    private ThemesTable _theme;
    private CTBorder border;

    public enum BorderSide {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }

    public XSSFCellBorder(CTBorder border, ThemesTable theme, IndexedColorMap colorMap) {
        this(border, colorMap);
        this._theme = theme;
    }

    public XSSFCellBorder(CTBorder border) {
        this(border, null);
    }

    public XSSFCellBorder(CTBorder border, IndexedColorMap colorMap) {
        this.border = border;
        this._indexedColorMap = colorMap;
    }

    public XSSFCellBorder() {
        this.border = CTBorder.Factory.newInstance();
    }

    public void setThemesTable(ThemesTable themes) {
        this._theme = themes;
    }

    @Internal
    public CTBorder getCTBorder() {
        return this.border;
    }

    public BorderStyle getBorderStyle(BorderSide side) {
        CTBorderPr ctBorder = getBorder(side);
        STBorderStyle.Enum border = ctBorder == null ? STBorderStyle.NONE : ctBorder.getStyle();
        return BorderStyle.values()[border.intValue() - 1];
    }

    public void setBorderStyle(BorderSide side, BorderStyle style) {
        getBorder(side, true).setStyle(STBorderStyle.Enum.forInt(style.ordinal() + 1));
    }

    public XSSFColor getBorderColor(BorderSide side) {
        CTBorderPr borderPr = getBorder(side);
        if (borderPr != null && borderPr.isSetColor()) {
            XSSFColor clr = new XSSFColor(borderPr.getColor(), this._indexedColorMap);
            ThemesTable themesTable = this._theme;
            if (themesTable != null) {
                themesTable.inheritFromThemeAsRequired(clr);
            }
            return clr;
        }
        return null;
    }

    public void setBorderColor(BorderSide side, XSSFColor color) {
        CTBorderPr borderPr = getBorder(side, true);
        if (color != null) {
            borderPr.setColor(color.getCTColor());
        } else {
            borderPr.unsetColor();
        }
    }

    private CTBorderPr getBorder(BorderSide side) {
        return getBorder(side, false);
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide;

        static {
            int[] iArr = new int[BorderSide.values().length];
            $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide = iArr;
            try {
                iArr[BorderSide.TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[BorderSide.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[BorderSide.BOTTOM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[BorderSide.LEFT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private CTBorderPr getBorder(BorderSide side, boolean ensure) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$usermodel$extensions$XSSFCellBorder$BorderSide[side.ordinal()];
        if (i == 1) {
            CTBorderPr borderPr = this.border.getTop();
            return (ensure && borderPr == null) ? this.border.addNewTop() : borderPr;
        }
        if (i == 2) {
            CTBorderPr borderPr2 = this.border.getRight();
            return (ensure && borderPr2 == null) ? this.border.addNewRight() : borderPr2;
        }
        if (i == 3) {
            CTBorderPr borderPr3 = this.border.getBottom();
            return (ensure && borderPr3 == null) ? this.border.addNewBottom() : borderPr3;
        }
        if (i != 4) {
            throw new IllegalArgumentException("No suitable side specified for the border");
        }
        CTBorderPr borderPr4 = this.border.getLeft();
        return (ensure && borderPr4 == null) ? this.border.addNewLeft() : borderPr4;
    }

    public int hashCode() {
        return this.border.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof XSSFCellBorder)) {
            return false;
        }
        XSSFCellBorder cf = (XSSFCellBorder) o;
        return this.border.toString().equals(cf.getCTBorder().toString());
    }
}
