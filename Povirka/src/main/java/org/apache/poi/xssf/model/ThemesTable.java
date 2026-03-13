package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.openxmlformats.schemas.drawingml.x2006.main.ThemeDocument;

/* JADX INFO: loaded from: classes.dex */
public class ThemesTable extends POIXMLDocumentPart {
    private IndexedColorMap colorMap;
    private ThemeDocument theme;

    public enum ThemeElement {
        LT1(0, "Lt1"),
        DK1(1, "Dk1"),
        LT2(2, "Lt2"),
        DK2(3, "Dk2"),
        ACCENT1(4, "Accent1"),
        ACCENT2(5, "Accent2"),
        ACCENT3(6, "Accent3"),
        ACCENT4(7, "Accent4"),
        ACCENT5(8, "Accent5"),
        ACCENT6(9, "Accent6"),
        HLINK(10, "Hlink"),
        FOLHLINK(11, "FolHlink"),
        UNKNOWN(-1, null);

        public final int idx;
        public final String name;

        public static ThemeElement byId(int idx) {
            if (idx >= values().length || idx < 0) {
                return UNKNOWN;
            }
            return values()[idx];
        }

        ThemeElement(int idx, String name) {
            this.idx = idx;
            this.name = name;
        }
    }

    public ThemesTable() {
        ThemeDocument themeDocumentNewInstance = ThemeDocument.Factory.newInstance();
        this.theme = themeDocumentNewInstance;
        themeDocumentNewInstance.addNewTheme().addNewThemeElements();
    }

    public ThemesTable(PackagePart part) throws IOException {
        super(part);
        try {
            this.theme = ThemeDocument.Factory.parse(part.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }
    }

    public ThemesTable(ThemeDocument theme) {
        this.theme = theme;
    }

    protected void setColorMap(IndexedColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public XSSFColor getThemeColor(int idx) {
        CTColor ctColor;
        byte[] rgb;
        CTColorScheme colorScheme = this.theme.getTheme().getThemeElements().getClrScheme();
        switch (AnonymousClass1.$SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.byId(idx).ordinal()]) {
            case 1:
                ctColor = colorScheme.getLt1();
                break;
            case 2:
                ctColor = colorScheme.getDk1();
                break;
            case 3:
                ctColor = colorScheme.getLt2();
                break;
            case 4:
                ctColor = colorScheme.getDk2();
                break;
            case 5:
                ctColor = colorScheme.getAccent1();
                break;
            case 6:
                ctColor = colorScheme.getAccent2();
                break;
            case 7:
                ctColor = colorScheme.getAccent3();
                break;
            case 8:
                ctColor = colorScheme.getAccent4();
                break;
            case 9:
                ctColor = colorScheme.getAccent5();
                break;
            case 10:
                ctColor = colorScheme.getAccent6();
                break;
            case 11:
                ctColor = colorScheme.getHlink();
                break;
            case 12:
                ctColor = colorScheme.getFolHlink();
                break;
            default:
                return null;
        }
        if (ctColor.isSetSrgbClr()) {
            rgb = ctColor.getSrgbClr().getVal();
        } else {
            if (!ctColor.isSetSysClr()) {
                return null;
            }
            rgb = ctColor.getSysClr().getLastClr();
        }
        return new XSSFColor(rgb, this.colorMap);
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.model.ThemesTable$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement;

        static {
            int[] iArr = new int[ThemeElement.values().length];
            $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement = iArr;
            try {
                iArr[ThemeElement.LT1.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.DK1.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.LT2.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.DK2.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT1.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT2.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT3.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT4.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT5.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.ACCENT6.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.HLINK.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$model$ThemesTable$ThemeElement[ThemeElement.FOLHLINK.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public void inheritFromThemeAsRequired(XSSFColor color) {
        if (color == null || !color.getCTColor().isSetTheme()) {
            return;
        }
        XSSFColor themeColor = getThemeColor(color.getTheme());
        color.getCTColor().setRgb(themeColor.getCTColor().getRgb());
    }

    public void writeTo(OutputStream out) throws IOException {
        this.theme.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }
}
