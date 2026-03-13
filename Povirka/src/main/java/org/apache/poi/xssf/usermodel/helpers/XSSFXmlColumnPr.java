package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.xssf.usermodel.XSSFTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFXmlColumnPr {
    private CTTableColumn ctTableColumn;
    private CTXmlColumnPr ctXmlColumnPr;
    private XSSFTable table;

    public XSSFXmlColumnPr(XSSFTable table, CTTableColumn ctTableColum, CTXmlColumnPr ctXmlColumnPr) {
        this.table = table;
        this.ctTableColumn = ctTableColum;
        this.ctXmlColumnPr = ctXmlColumnPr;
    }

    public long getMapId() {
        return this.ctXmlColumnPr.getMapId();
    }

    public String getXPath() {
        return this.ctXmlColumnPr.getXpath();
    }

    public long getId() {
        return this.ctTableColumn.getId();
    }

    public String getLocalXPath() {
        StringBuilder localXPath = new StringBuilder();
        int numberOfCommonXPathAxis = this.table.getCommonXpath().split("/").length - 1;
        String[] xPathTokens = this.ctXmlColumnPr.getXpath().split("/");
        for (int i = numberOfCommonXPathAxis; i < xPathTokens.length; i++) {
            localXPath.append("/" + xPathTokens[i]);
        }
        return localXPath.toString();
    }

    public STXmlDataType.Enum getXmlDataType() {
        return this.ctXmlColumnPr.getXmlDataType();
    }
}
