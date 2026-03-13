package org.apache.poi.poifs.crypt.dsig;

import java.io.Serializable;
import org.apache.poi.poifs.crypt.HashAlgorithm;

/* JADX INFO: loaded from: classes.dex */
public class DigestInfo implements Serializable {
    private static final long serialVersionUID = 1;
    public final String description;
    public final byte[] digestValue;
    public final HashAlgorithm hashAlgo;

    public DigestInfo(byte[] digestValue, HashAlgorithm hashAlgo, String description) {
        this.digestValue = (byte[]) digestValue.clone();
        this.hashAlgo = hashAlgo;
        this.description = description;
    }
}
