package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes.dex */
public final class HeaderRecord extends HeaderFooterBase implements Cloneable {
    public static final short sid = 20;

    public HeaderRecord(String text) {
        super(text);
    }

    public HeaderRecord(RecordInputStream in) {
        super(in);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[HEADER]\n");
        buffer.append("    .header = ").append(getText()).append("\n");
        buffer.append("[/HEADER]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 20;
    }

    @Override // org.apache.poi.hssf.record.Record
    public HeaderRecord clone() {
        return new HeaderRecord(getText());
    }
}
