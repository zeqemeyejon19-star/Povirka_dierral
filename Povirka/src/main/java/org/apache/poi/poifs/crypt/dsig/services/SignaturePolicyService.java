package org.apache.poi.poifs.crypt.dsig.services;

/* JADX INFO: loaded from: classes.dex */
public interface SignaturePolicyService {
    String getSignaturePolicyDescription();

    byte[] getSignaturePolicyDocument();

    String getSignaturePolicyDownloadUrl();

    String getSignaturePolicyIdentifier();
}
