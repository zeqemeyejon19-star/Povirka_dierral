package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlEnum
@XmlType(name = "ST_RectAlignment", namespace = XSSFRelation.NS_DRAWINGML)
public enum STRectAlignment {
    TL("tl"),
    T("t"),
    TR("tr"),
    L("l"),
    CTR("ctr"),
    R("r"),
    BL("bl"),
    B("b"),
    BR("br");

    private final String value;

    STRectAlignment(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static STRectAlignment fromValue(String v) {
        STRectAlignment[] arr$ = values();
        for (STRectAlignment c : arr$) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
