package org.apache.poi.poifs.crypt.dsig.services;

import java.security.cert.X509Certificate;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface TimeStampServiceValidator {
    void validate(List<X509Certificate> list, RevocationData revocationData) throws Exception;
}
