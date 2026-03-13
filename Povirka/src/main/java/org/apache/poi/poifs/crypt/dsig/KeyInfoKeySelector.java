package org.apache.poi.poifs.crypt.dsig;

import java.security.Key;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class KeyInfoKeySelector extends KeySelector implements KeySelectorResult {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) KeyInfoKeySelector.class);
    private List<X509Certificate> certChain = new ArrayList();

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.KeySelectorException */
    public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context) throws KeySelectorException {
        LOG.log(1, "select key");
        if (keyInfo == null) {
            throw new KeySelectorException("no ds:KeyInfo present");
        }
        List<XMLStructure> keyInfoContent = keyInfo.getContent();
        this.certChain.clear();
        for (XMLStructure xMLStructure : keyInfoContent) {
            if (xMLStructure instanceof X509Data) {
                X509Data x509Data = (X509Data) xMLStructure;
                List<?> x509DataList = x509Data.getContent();
                for (Object x509DataObject : x509DataList) {
                    if (x509DataObject instanceof X509Certificate) {
                        X509Certificate certificate = (X509Certificate) x509DataObject;
                        LOG.log(1, "certificate", certificate.getSubjectX500Principal());
                        this.certChain.add(certificate);
                    }
                }
            }
        }
        if (!this.certChain.isEmpty()) {
            return this;
        }
        throw new KeySelectorException("No key found!");
    }

    public Key getKey() {
        if (this.certChain.isEmpty()) {
            return null;
        }
        return this.certChain.get(0).getPublicKey();
    }

    public X509Certificate getSigner() {
        if (this.certChain.isEmpty()) {
            return null;
        }
        return this.certChain.get(0);
    }

    public List<X509Certificate> getCertChain() {
        return this.certChain;
    }
}
