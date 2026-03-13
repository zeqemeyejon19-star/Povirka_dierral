package org.apache.poi.poifs.crypt.dsig.facets;

import java.security.Key;
import java.security.KeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfo;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public class KeyInfoSignatureFacet extends SignatureFacet {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) KeyInfoSignatureFacet.class);

    @Override // org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet
    public void postSign(Document document) throws MarshalException {
        LOG.log(1, "postSign");
        NodeList nl = document.getElementsByTagNameNS(SignatureFacet.XML_DIGSIG_NS, "Object");
        Node nextSibling = nl.getLength() == 0 ? null : nl.item(0);
        KeyInfoFactory keyInfoFactory = this.signatureConfig.getKeyInfoFactory();
        List<Object> x509DataObjects = new ArrayList<>();
        X509Certificate signingCertificate = this.signatureConfig.getSigningCertificateChain().get(0);
        List<XMLStructure> keyInfoContent = new ArrayList<>();
        if (this.signatureConfig.isIncludeKeyValue()) {
            try {
                KeyValue keyValue = keyInfoFactory.newKeyValue(signingCertificate.getPublicKey());
                keyInfoContent.add(keyValue);
            } catch (KeyException e) {
                throw new RuntimeException("key exception: " + e.getMessage(), e);
            }
        }
        if (this.signatureConfig.isIncludeIssuerSerial()) {
            x509DataObjects.add(keyInfoFactory.newX509IssuerSerial(signingCertificate.getIssuerX500Principal().toString(), signingCertificate.getSerialNumber()));
        }
        if (this.signatureConfig.isIncludeEntireCertificateChain()) {
            x509DataObjects.addAll(this.signatureConfig.getSigningCertificateChain());
        } else {
            x509DataObjects.add(signingCertificate);
        }
        if (!x509DataObjects.isEmpty()) {
            X509Data x509Data = keyInfoFactory.newX509Data(x509DataObjects);
            keyInfoContent.add(x509Data);
        }
        DOMKeyInfo domKeyInfo = keyInfoFactory.newKeyInfo(keyInfoContent);
        Key key = new Key() { // from class: org.apache.poi.poifs.crypt.dsig.facets.KeyInfoSignatureFacet.1
            private static final long serialVersionUID = 1;

            @Override // java.security.Key
            public String getAlgorithm() {
                return null;
            }

            @Override // java.security.Key
            public byte[] getEncoded() {
                return null;
            }

            @Override // java.security.Key
            public String getFormat() {
                return null;
            }
        };
        Element n = document.getDocumentElement();
        DOMSignContext domSignContext = nextSibling == null ? new DOMSignContext(key, n) : new DOMSignContext(key, n, nextSibling);
        for (Map.Entry<String, String> me : this.signatureConfig.getNamespacePrefixes().entrySet()) {
            domSignContext.putNamespacePrefix(me.getKey(), me.getValue());
        }
        DOMStructure domStructure = new DOMStructure(n);
        domKeyInfo.marshal(domStructure, domSignContext);
        if (nextSibling != null) {
            NodeList kiNl = document.getElementsByTagNameNS(SignatureFacet.XML_DIGSIG_NS, "KeyInfo");
            if (kiNl.getLength() != 1) {
                throw new RuntimeException("KeyInfo wasn't set");
            }
            nextSibling.getParentNode().insertBefore(kiNl.item(0), nextSibling);
        }
    }
}
