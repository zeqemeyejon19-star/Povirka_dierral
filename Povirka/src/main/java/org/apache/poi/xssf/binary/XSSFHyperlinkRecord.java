package org.apache.poi.xssf.binary;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFHyperlinkRecord {
    private final CellRangeAddress cellRangeAddress;
    private String display;
    private String location;
    private final String relId;
    private String toolTip;

    XSSFHyperlinkRecord(CellRangeAddress cellRangeAddress, String relId, String location, String toolTip, String display) {
        this.cellRangeAddress = cellRangeAddress;
        this.relId = relId;
        this.location = location;
        this.toolTip = toolTip;
        this.display = display;
    }

    void setLocation(String location) {
        this.location = location;
    }

    void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    void setDisplay(String display) {
        this.display = display;
    }

    CellRangeAddress getCellRangeAddress() {
        return this.cellRangeAddress;
    }

    public String getRelId() {
        return this.relId;
    }

    public String getLocation() {
        return this.location;
    }

    public String getToolTip() {
        return this.toolTip;
    }

    public String getDisplay() {
        return this.display;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XSSFHyperlinkRecord that = (XSSFHyperlinkRecord) o;
        CellRangeAddress cellRangeAddress = this.cellRangeAddress;
        if (cellRangeAddress == null ? that.cellRangeAddress != null : !cellRangeAddress.equals(that.cellRangeAddress)) {
            return false;
        }
        String str = this.relId;
        if (str == null ? that.relId != null : !str.equals(that.relId)) {
            return false;
        }
        String str2 = this.location;
        if (str2 == null ? that.location != null : !str2.equals(that.location)) {
            return false;
        }
        String str3 = this.toolTip;
        if (str3 == null ? that.toolTip != null : !str3.equals(that.toolTip)) {
            return false;
        }
        String str4 = this.display;
        return str4 != null ? str4.equals(that.display) : that.display == null;
    }

    public int hashCode() {
        CellRangeAddress cellRangeAddress = this.cellRangeAddress;
        int result = cellRangeAddress != null ? cellRangeAddress.hashCode() : 0;
        int i = result * 31;
        String str = this.relId;
        int result2 = i + (str != null ? str.hashCode() : 0);
        int result3 = result2 * 31;
        String str2 = this.location;
        int result4 = (result3 + (str2 != null ? str2.hashCode() : 0)) * 31;
        String str3 = this.toolTip;
        int result5 = (result4 + (str3 != null ? str3.hashCode() : 0)) * 31;
        String str4 = this.display;
        return result5 + (str4 != null ? str4.hashCode() : 0);
    }

    public String toString() {
        return "XSSFHyperlinkRecord{cellRangeAddress=" + this.cellRangeAddress + ", relId='" + this.relId + "', location='" + this.location + "', toolTip='" + this.toolTip + "', display='" + this.display + "'}";
    }
}
