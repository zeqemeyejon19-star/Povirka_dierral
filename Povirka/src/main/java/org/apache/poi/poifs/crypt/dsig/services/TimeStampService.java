package org.apache.poi.poifs.crypt.dsig.services;

import org.apache.poi.poifs.crypt.dsig.SignatureConfig;

/* JADX INFO: loaded from: classes.dex */
public interface TimeStampService extends SignatureConfig.SignatureConfigurable {
    byte[] timeStamp(byte[] bArr, RevocationData revocationData) throws Exception;
}
