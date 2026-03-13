package org.apache.poi.xssf.usermodel.helpers;

/* JADX INFO: loaded from: classes.dex */
public class HeaderFooterHelper {
    private static final String HeaderFooterEntity_C = "&C";
    public static final String HeaderFooterEntity_Date = "&D";
    public static final String HeaderFooterEntity_File = "&F";
    private static final String HeaderFooterEntity_L = "&L";
    private static final String HeaderFooterEntity_R = "&R";
    public static final String HeaderFooterEntity_Time = "&T";

    public String getLeftSection(String string) {
        return getParts(string)[0];
    }

    public String getCenterSection(String string) {
        return getParts(string)[1];
    }

    public String getRightSection(String string) {
        return getParts(string)[2];
    }

    public String setLeftSection(String string, String newLeft) {
        String[] parts = getParts(string);
        parts[0] = newLeft;
        return joinParts(parts);
    }

    public String setCenterSection(String string, String newCenter) {
        String[] parts = getParts(string);
        parts[1] = newCenter;
        return joinParts(parts);
    }

    public String setRightSection(String string, String newRight) {
        String[] parts = getParts(string);
        parts[2] = newRight;
        return joinParts(parts);
    }

    private String[] getParts(String string) {
        int cAt;
        int rAt;
        String[] parts = {"", "", ""};
        if (string == null) {
            return parts;
        }
        while (true) {
            int lAt = string.indexOf(HeaderFooterEntity_L);
            if (lAt <= -2 || (cAt = string.indexOf(HeaderFooterEntity_C)) <= -2 || (rAt = string.indexOf(HeaderFooterEntity_R)) <= -2 || (lAt <= -1 && cAt <= -1 && rAt <= -1)) {
                break;
            }
            if (rAt > cAt && rAt > lAt) {
                parts[2] = string.substring(HeaderFooterEntity_R.length() + rAt);
                string = string.substring(0, rAt);
            } else if (cAt <= rAt || cAt <= lAt) {
                parts[0] = string.substring(HeaderFooterEntity_L.length() + lAt);
                string = string.substring(0, lAt);
            } else {
                parts[1] = string.substring(HeaderFooterEntity_C.length() + cAt);
                string = string.substring(0, cAt);
            }
        }
        return parts;
    }

    private String joinParts(String[] parts) {
        return joinParts(parts[0], parts[1], parts[2]);
    }

    private String joinParts(String l, String c, String r) {
        StringBuffer ret = new StringBuffer();
        if (c.length() > 0) {
            ret.append(HeaderFooterEntity_C);
            ret.append(c);
        }
        if (l.length() > 0) {
            ret.append(HeaderFooterEntity_L);
            ret.append(l);
        }
        if (r.length() > 0) {
            ret.append(HeaderFooterEntity_R);
            ret.append(r);
        }
        return ret.toString();
    }
}
