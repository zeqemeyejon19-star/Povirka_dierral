package org.apache.poi.ddf;

import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class EscherOptRecord extends AbstractEscherOptRecord {
    public static final String RECORD_DESCRIPTION = "msofbtOPT";
    public static final short RECORD_ID = -4085;

    @Override // org.apache.poi.ddf.EscherRecord
    public short getInstance() {
        setInstance((short) getEscherProperties().size());
        return super.getInstance();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    @Internal
    public short getOptions() {
        getInstance();
        getVersion();
        return super.getOptions();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public String getRecordName() {
        return "Opt";
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public short getVersion() {
        setVersion((short) 3);
        return super.getVersion();
    }

    @Override // org.apache.poi.ddf.EscherRecord
    public void setVersion(short value) {
        if (value != 3) {
            throw new IllegalArgumentException("msofbtOPT can have only '0x3' version");
        }
        super.setVersion(value);
    }
}
