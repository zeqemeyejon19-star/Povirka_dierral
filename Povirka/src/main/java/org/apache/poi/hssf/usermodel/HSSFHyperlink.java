package org.apache.poi.hssf.usermodel;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class HSSFHyperlink implements Hyperlink {
    protected final HyperlinkType link_type;
    protected final HyperlinkRecord record;

    @Internal(since = "3.15 beta 3")
    protected HSSFHyperlink(HyperlinkType type) {
        this.link_type = type;
        HyperlinkRecord hyperlinkRecord = new HyperlinkRecord();
        this.record = hyperlinkRecord;
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[type.ordinal()];
        if (i == 1 || i == 2) {
            hyperlinkRecord.newUrlLink();
        } else if (i == 3) {
            hyperlinkRecord.newFileLink();
        } else {
            if (i == 4) {
                hyperlinkRecord.newDocumentLink();
                return;
            }
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.hssf.usermodel.HSSFHyperlink$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType;

        static {
            int[] iArr = new int[HyperlinkType.values().length];
            $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType = iArr;
            try {
                iArr[HyperlinkType.URL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.EMAIL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.FILE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$common$usermodel$HyperlinkType[HyperlinkType.DOCUMENT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    protected HSSFHyperlink(HyperlinkRecord record) {
        this.record = record;
        this.link_type = getType(record);
    }

    private static HyperlinkType getType(HyperlinkRecord record) {
        if (record.isFileLink()) {
            HyperlinkType link_type = HyperlinkType.FILE;
            return link_type;
        }
        if (record.isDocumentLink()) {
            HyperlinkType link_type2 = HyperlinkType.DOCUMENT;
            return link_type2;
        }
        if (record.getAddress() != null && record.getAddress().startsWith("mailto:")) {
            HyperlinkType link_type3 = HyperlinkType.EMAIL;
            return link_type3;
        }
        HyperlinkType link_type4 = HyperlinkType.URL;
        return link_type4;
    }

    protected HSSFHyperlink(Hyperlink other) {
        if (other instanceof HSSFHyperlink) {
            HSSFHyperlink hlink = (HSSFHyperlink) other;
            HyperlinkRecord hyperlinkRecordClone = hlink.record.clone();
            this.record = hyperlinkRecordClone;
            this.link_type = getType(hyperlinkRecordClone);
            return;
        }
        this.link_type = other.getTypeEnum();
        this.record = new HyperlinkRecord();
        setFirstRow(other.getFirstRow());
        setFirstColumn(other.getFirstColumn());
        setLastRow(other.getLastRow());
        setLastColumn(other.getLastColumn());
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getFirstRow() {
        return this.record.getFirstRow();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setFirstRow(int row) {
        this.record.setFirstRow(row);
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getLastRow() {
        return this.record.getLastRow();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setLastRow(int row) {
        this.record.setLastRow(row);
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getFirstColumn() {
        return this.record.getFirstColumn();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setFirstColumn(int col) {
        this.record.setFirstColumn((short) col);
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public int getLastColumn() {
        return this.record.getLastColumn();
    }

    @Override // org.apache.poi.ss.usermodel.Hyperlink
    public void setLastColumn(int col) {
        this.record.setLastColumn((short) col);
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public String getAddress() {
        return this.record.getAddress();
    }

    public String getTextMark() {
        return this.record.getTextMark();
    }

    public void setTextMark(String textMark) {
        this.record.setTextMark(textMark);
    }

    public String getShortFilename() {
        return this.record.getShortFilename();
    }

    public void setShortFilename(String shortFilename) {
        this.record.setShortFilename(shortFilename);
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public void setAddress(String address) {
        this.record.setAddress(address);
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public String getLabel() {
        return this.record.getLabel();
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public void setLabel(String label) {
        this.record.setLabel(label);
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public int getType() {
        return this.link_type.getCode();
    }

    @Override // org.apache.poi.common.usermodel.Hyperlink
    public HyperlinkType getTypeEnum() {
        return this.link_type;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HSSFHyperlink)) {
            return false;
        }
        HSSFHyperlink otherLink = (HSSFHyperlink) other;
        return this.record == otherLink.record;
    }

    public int hashCode() {
        return this.record.hashCode();
    }
}
