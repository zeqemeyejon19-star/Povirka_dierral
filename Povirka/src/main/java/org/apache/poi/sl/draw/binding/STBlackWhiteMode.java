package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRelation;

/* JADX INFO: loaded from: classes.dex */
@XmlEnum
@XmlType(name = "ST_BlackWhiteMode", namespace = XSSFRelation.NS_DRAWINGML)
public enum STBlackWhiteMode {
    CLR("clr"),
    AUTO("auto"),
    GRAY("gray"),
    LT_GRAY("ltGray"),
    INV_GRAY("invGray"),
    GRAY_WHITE("grayWhite"),
    BLACK_GRAY("blackGray"),
    BLACK_WHITE("blackWhite"),
    BLACK("black"),
    WHITE("white"),
    HIDDEN(CellUtil.HIDDEN);

    private final String value;

    STBlackWhiteMode(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static STBlackWhiteMode fromValue(String v) {
        STBlackWhiteMode[] arr$ = values();
        for (STBlackWhiteMode c : arr$) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
