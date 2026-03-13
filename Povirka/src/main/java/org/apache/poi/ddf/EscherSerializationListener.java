package org.apache.poi.ddf;

/* JADX INFO: loaded from: classes.dex */
public interface EscherSerializationListener {
    void afterRecordSerialize(int i, short s, int i2, EscherRecord escherRecord);

    void beforeRecordSerialize(int i, short s, EscherRecord escherRecord);
}
