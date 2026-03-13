package org.apache.poi.ss.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public final class CellUtil {
    public static final String ALIGNMENT = "alignment";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    private static final POILogger log = POILogFactory.getLogger((Class<?>) CellUtil.class);
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String INDENTION = "indention";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FONT = "font";
    public static final String ROTATION = "rotation";
    private static final Set<String> shortValues = Collections.unmodifiableSet(new HashSet(Arrays.asList(BOTTOM_BORDER_COLOR, LEFT_BORDER_COLOR, RIGHT_BORDER_COLOR, TOP_BORDER_COLOR, FILL_FOREGROUND_COLOR, FILL_BACKGROUND_COLOR, INDENTION, DATA_FORMAT, FONT, ROTATION)));
    public static final String LOCKED = "locked";
    public static final String HIDDEN = "hidden";
    public static final String WRAP_TEXT = "wrapText";
    private static final Set<String> booleanValues = Collections.unmodifiableSet(new HashSet(Arrays.asList(LOCKED, HIDDEN, WRAP_TEXT)));
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    private static final Set<String> borderTypeValues = Collections.unmodifiableSet(new HashSet(Arrays.asList(BORDER_BOTTOM, BORDER_LEFT, BORDER_RIGHT, BORDER_TOP)));
    private static UnicodeMapping[] unicodeMappings = {um("alpha", "α"), um("beta", "β"), um("gamma", "γ"), um("delta", "δ"), um("epsilon", "ε"), um("zeta", "ζ"), um("eta", "η"), um("theta", "θ"), um("iota", "ι"), um("kappa", "κ"), um("lambda", "λ"), um("mu", "μ"), um("nu", "ν"), um("xi", "ξ"), um("omicron", "ο")};

    private static final class UnicodeMapping {
        public final String entityName;
        public final String resolvedValue;

        public UnicodeMapping(String pEntityName, String pResolvedValue) {
            this.entityName = "&" + pEntityName + ";";
            this.resolvedValue = pResolvedValue;
        }
    }

    private CellUtil() {
    }

    public static Row getRow(int rowIndex, Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return sheet.createRow(rowIndex);
        }
        return row;
    }

    public static Cell getCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return row.createCell(columnIndex);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = getCell(row, column);
        cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static Cell createCell(Row row, int column, String value) {
        return createCell(row, column, value, null);
    }

    public static void setAlignment(Cell cell, HorizontalAlignment align) {
        setCellStyleProperty(cell, ALIGNMENT, align);
    }

    public static void setVerticalAlignment(Cell cell, VerticalAlignment align) {
        setCellStyleProperty(cell, VERTICAL_ALIGNMENT, align);
    }

    public static void setFont(Cell cell, Font font) {
        Workbook wb = cell.getSheet().getWorkbook();
        short fontIndex = font.getIndex();
        if (!wb.getFontAt(fontIndex).equals(font)) {
            throw new IllegalArgumentException("Font does not belong to this workbook");
        }
        setCellStyleProperty(cell, FONT, Short.valueOf(fontIndex));
    }

    public static void setCellStyleProperties(Cell cell, Map<String, Object> properties) {
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle originalStyle = cell.getCellStyle();
        CellStyle newStyle = null;
        Map<String, Object> values = getFormatProperties(originalStyle);
        putAll(properties, values);
        int numberCellStyles = workbook.getNumCellStyles();
        int i = 0;
        while (true) {
            if (i >= numberCellStyles) {
                break;
            }
            CellStyle wbStyle = workbook.getCellStyleAt(i);
            Map<String, Object> wbStyleMap = getFormatProperties(wbStyle);
            if (!wbStyleMap.equals(values)) {
                i++;
            } else {
                newStyle = wbStyle;
                break;
            }
        }
        if (newStyle == null) {
            newStyle = workbook.createCellStyle();
            setFormatProperties(newStyle, workbook, values);
        }
        cell.setCellStyle(newStyle);
    }

    public static void setCellStyleProperty(Cell cell, String propertyName, Object propertyValue) {
        Map<String, Object> property = Collections.singletonMap(propertyName, propertyValue);
        setCellStyleProperties(cell, property);
    }

    private static Map<String, Object> getFormatProperties(CellStyle style) {
        Map<String, Object> properties = new HashMap<>();
        put(properties, ALIGNMENT, style.getAlignmentEnum());
        put(properties, VERTICAL_ALIGNMENT, style.getVerticalAlignmentEnum());
        put(properties, BORDER_BOTTOM, style.getBorderBottomEnum());
        put(properties, BORDER_LEFT, style.getBorderLeftEnum());
        put(properties, BORDER_RIGHT, style.getBorderRightEnum());
        put(properties, BORDER_TOP, style.getBorderTopEnum());
        put(properties, BOTTOM_BORDER_COLOR, Short.valueOf(style.getBottomBorderColor()));
        put(properties, DATA_FORMAT, Short.valueOf(style.getDataFormat()));
        put(properties, FILL_PATTERN, style.getFillPatternEnum());
        put(properties, FILL_FOREGROUND_COLOR, Short.valueOf(style.getFillForegroundColor()));
        put(properties, FILL_BACKGROUND_COLOR, Short.valueOf(style.getFillBackgroundColor()));
        put(properties, FONT, Short.valueOf(style.getFontIndex()));
        put(properties, HIDDEN, Boolean.valueOf(style.getHidden()));
        put(properties, INDENTION, Short.valueOf(style.getIndention()));
        put(properties, LEFT_BORDER_COLOR, Short.valueOf(style.getLeftBorderColor()));
        put(properties, LOCKED, Boolean.valueOf(style.getLocked()));
        put(properties, RIGHT_BORDER_COLOR, Short.valueOf(style.getRightBorderColor()));
        put(properties, ROTATION, Short.valueOf(style.getRotation()));
        put(properties, TOP_BORDER_COLOR, Short.valueOf(style.getTopBorderColor()));
        put(properties, WRAP_TEXT, Boolean.valueOf(style.getWrapText()));
        return properties;
    }

    private static void putAll(Map<String, Object> src, Map<String, Object> dest) {
        for (String key : src.keySet()) {
            if (shortValues.contains(key)) {
                dest.put(key, Short.valueOf(getShort(src, key)));
            } else if (booleanValues.contains(key)) {
                dest.put(key, Boolean.valueOf(getBoolean(src, key)));
            } else if (borderTypeValues.contains(key)) {
                dest.put(key, getBorderStyle(src, key));
            } else if (ALIGNMENT.equals(key)) {
                dest.put(key, getHorizontalAlignment(src, key));
            } else if (VERTICAL_ALIGNMENT.equals(key)) {
                dest.put(key, getVerticalAlignment(src, key));
            } else if (FILL_PATTERN.equals(key)) {
                dest.put(key, getFillPattern(src, key));
            } else {
                POILogger pOILogger = log;
                if (pOILogger.check(3)) {
                    pOILogger.log(3, "Ignoring unrecognized CellUtil format properties key: " + key);
                }
            }
        }
    }

    private static void setFormatProperties(CellStyle style, Workbook workbook, Map<String, Object> properties) {
        style.setAlignment(getHorizontalAlignment(properties, ALIGNMENT));
        style.setVerticalAlignment(getVerticalAlignment(properties, VERTICAL_ALIGNMENT));
        style.setBorderBottom(getBorderStyle(properties, BORDER_BOTTOM));
        style.setBorderLeft(getBorderStyle(properties, BORDER_LEFT));
        style.setBorderRight(getBorderStyle(properties, BORDER_RIGHT));
        style.setBorderTop(getBorderStyle(properties, BORDER_TOP));
        style.setBottomBorderColor(getShort(properties, BOTTOM_BORDER_COLOR));
        style.setDataFormat(getShort(properties, DATA_FORMAT));
        style.setFillPattern(getFillPattern(properties, FILL_PATTERN));
        style.setFillForegroundColor(getShort(properties, FILL_FOREGROUND_COLOR));
        style.setFillBackgroundColor(getShort(properties, FILL_BACKGROUND_COLOR));
        style.setFont(workbook.getFontAt(getShort(properties, FONT)));
        style.setHidden(getBoolean(properties, HIDDEN));
        style.setIndention(getShort(properties, INDENTION));
        style.setLeftBorderColor(getShort(properties, LEFT_BORDER_COLOR));
        style.setLocked(getBoolean(properties, LOCKED));
        style.setRightBorderColor(getShort(properties, RIGHT_BORDER_COLOR));
        style.setRotation(getShort(properties, ROTATION));
        style.setTopBorderColor(getShort(properties, TOP_BORDER_COLOR));
        style.setWrapText(getBoolean(properties, WRAP_TEXT));
    }

    private static short getShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Short) {
            return ((Short) value).shortValue();
        }
        return (short) 0;
    }

    private static BorderStyle getBorderStyle(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof BorderStyle) {
            BorderStyle border = (BorderStyle) value;
            return border;
        }
        if (value instanceof Short) {
            POILogger pOILogger = log;
            if (pOILogger.check(5)) {
                pOILogger.log(5, "Deprecation warning: CellUtil properties map uses Short values for " + name + ". Should use BorderStyle enums instead.");
            }
            short code = ((Short) value).shortValue();
            BorderStyle border2 = BorderStyle.valueOf(code);
            return border2;
        }
        if (value == null) {
            BorderStyle border3 = BorderStyle.NONE;
            return border3;
        }
        throw new RuntimeException("Unexpected border style class. Must be BorderStyle or Short (deprecated).");
    }

    private static FillPatternType getFillPattern(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof FillPatternType) {
            FillPatternType pattern = (FillPatternType) value;
            return pattern;
        }
        if (value instanceof Short) {
            POILogger pOILogger = log;
            if (pOILogger.check(5)) {
                pOILogger.log(5, "Deprecation warning: CellUtil properties map uses Short values for " + name + ". Should use FillPatternType enums instead.");
            }
            short code = ((Short) value).shortValue();
            FillPatternType pattern2 = FillPatternType.forInt(code);
            return pattern2;
        }
        if (value == null) {
            FillPatternType pattern3 = FillPatternType.NO_FILL;
            return pattern3;
        }
        throw new RuntimeException("Unexpected fill pattern style class. Must be FillPatternType or Short (deprecated).");
    }

    private static HorizontalAlignment getHorizontalAlignment(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof HorizontalAlignment) {
            HorizontalAlignment align = (HorizontalAlignment) value;
            return align;
        }
        if (value instanceof Short) {
            POILogger pOILogger = log;
            if (pOILogger.check(5)) {
                pOILogger.log(5, "Deprecation warning: CellUtil properties map used a Short value for " + name + ". Should use HorizontalAlignment enums instead.");
            }
            short code = ((Short) value).shortValue();
            HorizontalAlignment align2 = HorizontalAlignment.forInt(code);
            return align2;
        }
        if (value == null) {
            HorizontalAlignment align3 = HorizontalAlignment.GENERAL;
            return align3;
        }
        throw new RuntimeException("Unexpected horizontal alignment style class. Must be HorizontalAlignment or Short (deprecated).");
    }

    private static VerticalAlignment getVerticalAlignment(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof VerticalAlignment) {
            VerticalAlignment align = (VerticalAlignment) value;
            return align;
        }
        if (value instanceof Short) {
            POILogger pOILogger = log;
            if (pOILogger.check(5)) {
                pOILogger.log(5, "Deprecation warning: CellUtil properties map used a Short value for " + name + ". Should use VerticalAlignment enums instead.");
            }
            short code = ((Short) value).shortValue();
            VerticalAlignment align2 = VerticalAlignment.forInt(code);
            return align2;
        }
        if (value == null) {
            VerticalAlignment align3 = VerticalAlignment.BOTTOM;
            return align3;
        }
        throw new RuntimeException("Unexpected vertical alignment style class. Must be VerticalAlignment or Short (deprecated).");
    }

    private static boolean getBoolean(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    private static void put(Map<String, Object> properties, String name, Object value) {
        properties.put(name, value);
    }

    public static Cell translateUnicodeValues(Cell cell) {
        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        String lowerCaseStr = s.toLowerCase(Locale.ROOT);
        UnicodeMapping[] arr$ = unicodeMappings;
        for (UnicodeMapping entry : arr$) {
            String key = entry.entityName;
            if (lowerCaseStr.contains(key)) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(s));
        }
        return cell;
    }

    private static UnicodeMapping um(String entityName, String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }
}
