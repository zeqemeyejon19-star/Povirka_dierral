package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes.dex */
public final class FooterRecord extends HeaderFooterBase implements Cloneable {
    public static final short sid = 21;

    public FooterRecord(String text) {
        super(text);
    }

    public FooterRecord(RecordInputStream in) {
        super(in);
    }

    @Override // org.apache.poi.hssf.record.Record
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FOOTER]\n");
        buffer.append("    .footer = ").append(getText()).append("\n");
        buffer.append("[/FOOTER]\n");
        return buffer.toString();
    }

    @Override // org.apache.poi.hssf.record.Record
    public short getSid() {
        return (short) 21;
    }

    @Override // org.apache.poi.hssf.record.Record
    public FooterRecord clone() {
        return new FooterRecord(getText());
    }
}
