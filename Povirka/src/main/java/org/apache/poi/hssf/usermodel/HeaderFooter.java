package org.apache.poi.hssf.usermodel;

import org.apache.poi.xssf.usermodel.helpers.HeaderFooterHelper;

/* JADX INFO: loaded from: classes.dex */
public abstract class HeaderFooter implements org.apache.poi.ss.usermodel.HeaderFooter {
    protected abstract String getRawText();

    protected abstract void setHeaderFooterText(String str);

    protected HeaderFooter() {
    }

    private String[] splitParts() {
        String text = getRawText();
        String _left = "";
        String _center = "";
        String _right = "";
        while (true) {
            if (text.length() <= 1) {
                break;
            }
            if (text.charAt(0) != '&') {
                _center = text;
                break;
            }
            int pos = text.length();
            char cCharAt = text.charAt(1);
            if (cCharAt == 'C') {
                if (text.contains("&L")) {
                    pos = Math.min(pos, text.indexOf("&L"));
                }
                if (text.contains("&R")) {
                    pos = Math.min(pos, text.indexOf("&R"));
                }
                _center = text.substring(2, pos);
                text = text.substring(pos);
            } else if (cCharAt == 'L') {
                if (text.contains("&C")) {
                    pos = Math.min(pos, text.indexOf("&C"));
                }
                if (text.contains("&R")) {
                    pos = Math.min(pos, text.indexOf("&R"));
                }
                _left = text.substring(2, pos);
                text = text.substring(pos);
            } else if (cCharAt == 'R') {
                if (text.contains("&C")) {
                    pos = Math.min(pos, text.indexOf("&C"));
                }
                if (text.contains("&L")) {
                    pos = Math.min(pos, text.indexOf("&L"));
                }
                _right = text.substring(2, pos);
                text = text.substring(pos);
            } else {
                _center = text;
                break;
            }
        }
        return new String[]{_left, _center, _right};
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final String getLeft() {
        return splitParts()[0];
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final void setLeft(String newLeft) {
        updatePart(0, newLeft);
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final String getCenter() {
        return splitParts()[1];
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final void setCenter(String newCenter) {
        updatePart(1, newCenter);
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final String getRight() {
        return splitParts()[2];
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public final void setRight(String newRight) {
        updatePart(2, newRight);
    }

    private void updatePart(int partIndex, String newValue) {
        String[] parts = splitParts();
        parts[partIndex] = newValue == null ? "" : newValue;
        updateHeaderFooterText(parts);
    }

    private void updateHeaderFooterText(String[] parts) {
        String _left = parts[0];
        String _center = parts[1];
        String _right = parts[2];
        if (_center.length() < 1 && _left.length() < 1 && _right.length() < 1) {
            setHeaderFooterText("");
            return;
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append("&C");
        sb.append(_center);
        sb.append("&L");
        sb.append(_left);
        sb.append("&R");
        sb.append(_right);
        String text = sb.toString();
        setHeaderFooterText(text);
    }

    public static String fontSize(short size) {
        return "&" + ((int) size);
    }

    public static String font(String font, String style) {
        return "&\"" + font + "," + style + "\"";
    }

    public static String page() {
        return MarkupTag.PAGE_FIELD.getRepresentation();
    }

    public static String numPages() {
        return MarkupTag.NUM_PAGES_FIELD.getRepresentation();
    }

    public static String date() {
        return MarkupTag.DATE_FIELD.getRepresentation();
    }

    public static String time() {
        return MarkupTag.TIME_FIELD.getRepresentation();
    }

    public static String file() {
        return MarkupTag.FILE_FIELD.getRepresentation();
    }

    public static String tab() {
        return MarkupTag.SHEET_NAME_FIELD.getRepresentation();
    }

    public static String startBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }

    public static String endBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }

    public static String startUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }

    public static String endUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }

    public static String startDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }

    public static String endDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }

    public static String stripFields(String pText) {
        if (pText == null || pText.length() == 0) {
            return pText;
        }
        String text = pText;
        MarkupTag[] arr$ = MarkupTag.values();
        for (MarkupTag mt : arr$) {
            String seq = mt.getRepresentation();
            while (true) {
                int pos = text.indexOf(seq);
                if (pos >= 0) {
                    text = text.substring(0, pos) + text.substring(seq.length() + pos);
                }
            }
        }
        return text.replaceAll("\\&\\d+", "").replaceAll("\\&\".*?,.*?\"", "");
    }

    private enum MarkupTag {
        SHEET_NAME_FIELD("&A", false),
        DATE_FIELD(HeaderFooterHelper.HeaderFooterEntity_Date, false),
        FILE_FIELD(HeaderFooterHelper.HeaderFooterEntity_File, false),
        FULL_FILE_FIELD("&Z", false),
        PAGE_FIELD("&P", false),
        TIME_FIELD(HeaderFooterHelper.HeaderFooterEntity_Time, false),
        NUM_PAGES_FIELD("&N", false),
        PICTURE_FIELD("&G", false),
        BOLD_FIELD("&B", true),
        ITALIC_FIELD("&I", true),
        STRIKETHROUGH_FIELD("&S", true),
        SUBSCRIPT_FIELD("&Y", true),
        SUPERSCRIPT_FIELD("&X", true),
        UNDERLINE_FIELD("&U", true),
        DOUBLE_UNDERLINE_FIELD("&E", true);

        private final boolean _occursInPairs;
        private final String _representation;

        MarkupTag(String sequence, boolean occursInPairs) {
            this._representation = sequence;
            this._occursInPairs = occursInPairs;
        }

        public String getRepresentation() {
            return this._representation;
        }

        public boolean occursPairs() {
            return this._occursInPairs;
        }
    }
}
