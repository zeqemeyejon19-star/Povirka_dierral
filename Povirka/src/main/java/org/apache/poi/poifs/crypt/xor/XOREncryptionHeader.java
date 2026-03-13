package org.apache.poi.poifs.crypt.xor;

import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class XOREncryptionHeader extends EncryptionHeader implements EncryptionRecord, Cloneable {
    protected XOREncryptionHeader() {
    }

    @Override // org.apache.poi.poifs.crypt.standard.EncryptionRecord
    public void write(LittleEndianByteArrayOutputStream leos) {
    }

    @Override // org.apache.poi.poifs.crypt.EncryptionHeader
    public XOREncryptionHeader clone() throws CloneNotSupportedException {
        return (XOREncryptionHeader) super.clone();
    }
}
