package org.apache.poi.poifs.crypt.dsig.facets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.crypto.MarshalException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xmlbeans.XmlException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RespID;
import org.etsi.uri.x01903.v13.CRLIdentifierType;
import org.etsi.uri.x01903.v13.CRLRefType;
import org.etsi.uri.x01903.v13.CRLRefsType;
import org.etsi.uri.x01903.v13.CRLValuesType;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.CertIDType;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.etsi.uri.x01903.v13.CompleteCertificateRefsType;
import org.etsi.uri.x01903.v13.CompleteRevocationRefsType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.etsi.uri.x01903.v13.OCSPIdentifierType;
import org.etsi.uri.x01903.v13.OCSPRefType;
import org.etsi.uri.x01903.v13.OCSPRefsType;
import org.etsi.uri.x01903.v13.OCSPValuesType;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.etsi.uri.x01903.v13.ResponderIDType;
import org.etsi.uri.x01903.v13.RevocationValuesType;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;
import org.etsi.uri.x01903.v14.ValidationDataType;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public class XAdESXLSignatureFacet extends SignatureFacet {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XAdESXLSignatureFacet.class);
    private final CertificateFactory certificateFactory;

    public XAdESXLSignatureFacet() {
        try {
            this.certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException("X509 JCA error: " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet
    public void postSign(Document document) throws MarshalException {
        NodeList qualNl;
        POILogger pOILogger = LOG;
        pOILogger.log(1, "XAdES-X-L post sign phase");
        NodeList qualNl2 = document.getElementsByTagNameNS(SignatureFacet.XADES_132_NS, "QualifyingProperties");
        if (qualNl2.getLength() == 1) {
            try {
                QualifyingPropertiesDocument qualDoc = QualifyingPropertiesDocument.Factory.parse(qualNl2.item(0), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                QualifyingPropertiesType qualProps = qualDoc.getQualifyingProperties();
                UnsignedPropertiesType unsignedProps = qualProps.getUnsignedProperties();
                if (unsignedProps == null) {
                    unsignedProps = qualProps.addNewUnsignedProperties();
                }
                UnsignedSignaturePropertiesType unsignedSigProps = unsignedProps.getUnsignedSignatureProperties();
                if (unsignedSigProps == null) {
                    unsignedSigProps = unsignedProps.addNewUnsignedSignatureProperties();
                }
                NodeList nlSigVal = document.getElementsByTagNameNS(SignatureFacet.XML_DIGSIG_NS, "SignatureValue");
                if (nlSigVal.getLength() != 1) {
                    throw new IllegalArgumentException("SignatureValue is not set.");
                }
                RevocationData tsaRevocationDataXadesT = new RevocationData();
                pOILogger.log(1, "creating XAdES-T time-stamp");
                XAdESTimeStampType signatureTimeStamp = createXAdESTimeStamp(Collections.singletonList(nlSigVal.item(0)), tsaRevocationDataXadesT);
                unsignedSigProps.addNewSignatureTimeStamp().set(signatureTimeStamp);
                if (tsaRevocationDataXadesT.hasRevocationDataEntries()) {
                    ValidationDataType validationData = createValidationData(tsaRevocationDataXadesT);
                    XAdESSignatureFacet.insertXChild(unsignedSigProps, validationData);
                }
                if (this.signatureConfig.getRevocationDataService() == null) {
                    return;
                }
                CompleteCertificateRefsType completeCertificateRefs = unsignedSigProps.addNewCompleteCertificateRefs();
                CertIDListType certIdList = completeCertificateRefs.addNewCertRefs();
                List<X509Certificate> certChain = this.signatureConfig.getSigningCertificateChain();
                int chainSize = certChain.size();
                if (chainSize > 1) {
                    for (X509Certificate cert : certChain.subList(1, chainSize)) {
                        CertIDType certId = certIdList.addNewCert();
                        XAdESSignatureFacet.setCertID(certId, this.signatureConfig, false, cert);
                        qualDoc = qualDoc;
                        chainSize = chainSize;
                    }
                }
                CompleteRevocationRefsType completeRevocationRefs = unsignedSigProps.addNewCompleteRevocationRefs();
                RevocationData revocationData = this.signatureConfig.getRevocationDataService().getRevocationData(certChain);
                if (!revocationData.hasCRLs()) {
                    qualNl = qualNl2;
                } else {
                    CRLRefsType crlRefs = completeRevocationRefs.addNewCRLRefs();
                    completeRevocationRefs.setCRLRefs(crlRefs);
                    X509CRL x509crl = null;
                    for (byte[] encodedCrl : revocationData.getCRLs()) {
                        UnsignedPropertiesType unsignedProps2 = unsignedProps;
                        CRLRefType crlRef = crlRefs.addNewCRLRef();
                        try {
                            CRLRefsType crlRefs2 = crlRefs;
                            try {
                                X509CRL crl = (X509CRL) this.certificateFactory.generateCRL(new ByteArrayInputStream(encodedCrl));
                                CRLIdentifierType crlIdentifier = crlRef.addNewCRLIdentifier();
                                RevocationData tsaRevocationDataXadesT2 = tsaRevocationDataXadesT;
                                CertIDListType certIdList2 = certIdList;
                                NodeList qualNl3 = qualNl2;
                                String issuerName = crl.getIssuerDN().getName().replace(",", ", ");
                                crlIdentifier.setIssuer(issuerName);
                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
                                cal.setTime(crl.getThisUpdate());
                                crlIdentifier.setIssueTime(cal);
                                crlIdentifier.setNumber(getCrlNumber(crl));
                                DigestAlgAndValueType digestAlgAndValue = crlRef.addNewDigestAlgAndValue();
                                x509crl = crl;
                                XAdESSignatureFacet.setDigestAlgAndValue(digestAlgAndValue, encodedCrl, this.signatureConfig.getDigestAlgo());
                                unsignedProps = unsignedProps2;
                                crlRefs = crlRefs2;
                                tsaRevocationDataXadesT = tsaRevocationDataXadesT2;
                                certIdList = certIdList2;
                                qualNl2 = qualNl3;
                            } catch (CRLException e) {
                                e = e;
                                throw new RuntimeException("CRL parse error: " + e.getMessage(), e);
                            }
                        } catch (CRLException e2) {
                            e = e2;
                        }
                    }
                    qualNl = qualNl2;
                }
                if (revocationData.hasOCSPs()) {
                    OCSPRefsType ocspRefs = completeRevocationRefs.addNewOCSPRefs();
                    Iterator<byte[]> it = revocationData.getOCSPs().iterator();
                    while (it.hasNext()) {
                        byte[] ocsp = it.next();
                        try {
                            OCSPRefType ocspRef = ocspRefs.addNewOCSPRef();
                            DigestAlgAndValueType digestAlgAndValue2 = ocspRef.addNewDigestAlgAndValue();
                            XAdESSignatureFacet.setDigestAlgAndValue(digestAlgAndValue2, ocsp, this.signatureConfig.getDigestAlgo());
                            OCSPIdentifierType ocspIdentifier = ocspRef.addNewOCSPIdentifier();
                            OCSPResp ocspResp = new OCSPResp(ocsp);
                            BasicOCSPResp basicOcspResp = (BasicOCSPResp) ocspResp.getResponseObject();
                            OCSPRefsType ocspRefs2 = ocspRefs;
                            try {
                                Calendar cal2 = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
                                cal2.setTime(basicOcspResp.getProducedAt());
                                ocspIdentifier.setProducedAt(cal2);
                                ResponderIDType responderId = ocspIdentifier.addNewResponderID();
                                RespID respId = basicOcspResp.getResponderId();
                                ResponderID ocspResponderId = respId.toASN1Primitive();
                                DERTaggedObject derTaggedObject = ocspResponderId.toASN1Primitive();
                                Iterator<byte[]> it2 = it;
                                if (2 == derTaggedObject.getTagNo()) {
                                    try {
                                        ASN1OctetString keyHashOctetString = derTaggedObject.getObject();
                                        byte[] key = keyHashOctetString.getOctets();
                                        responderId.setByKey(key);
                                    } catch (Exception e3) {
                                        e = e3;
                                        throw new RuntimeException("OCSP decoding error: " + e.getMessage(), e);
                                    }
                                } else {
                                    X500Name name = X500Name.getInstance(derTaggedObject.getObject());
                                    String nameStr = name.toString();
                                    responderId.setByName(nameStr);
                                }
                                ocspRefs = ocspRefs2;
                                it = it2;
                            } catch (Exception e4) {
                                e = e4;
                            }
                        } catch (Exception e5) {
                            e = e5;
                        }
                    }
                }
                List<Node> timeStampNodesXadesX1 = new ArrayList<>();
                timeStampNodesXadesX1.add(nlSigVal.item(0));
                timeStampNodesXadesX1.add(signatureTimeStamp.getDomNode());
                timeStampNodesXadesX1.add(completeCertificateRefs.getDomNode());
                timeStampNodesXadesX1.add(completeRevocationRefs.getDomNode());
                RevocationData tsaRevocationDataXadesX1 = new RevocationData();
                LOG.log(1, "creating XAdES-X time-stamp");
                XAdESTimeStampType timeStampXadesX1 = createXAdESTimeStamp(timeStampNodesXadesX1, tsaRevocationDataXadesX1);
                if (tsaRevocationDataXadesX1.hasRevocationDataEntries()) {
                    ValidationDataType timeStampXadesX1ValidationData = createValidationData(tsaRevocationDataXadesX1);
                    XAdESSignatureFacet.insertXChild(unsignedSigProps, timeStampXadesX1ValidationData);
                }
                unsignedSigProps.addNewSigAndRefsTimeStamp().set(timeStampXadesX1);
                CertificateValuesType certificateValues = unsignedSigProps.addNewCertificateValues();
                for (X509Certificate certificate : certChain) {
                    CompleteRevocationRefsType completeRevocationRefs2 = completeRevocationRefs;
                    EncapsulatedPKIDataType encapsulatedPKIDataType = certificateValues.addNewEncapsulatedX509Certificate();
                    try {
                        encapsulatedPKIDataType.setByteArrayValue(certificate.getEncoded());
                        completeRevocationRefs = completeRevocationRefs2;
                    } catch (CertificateEncodingException e6) {
                        throw new RuntimeException("certificate encoding error: " + e6.getMessage(), e6);
                    }
                }
                RevocationValuesType revocationValues = unsignedSigProps.addNewRevocationValues();
                createRevocationValues(revocationValues, revocationData);
                Node n = document.importNode(qualProps.getDomNode(), true);
                NodeList qualNl4 = qualNl;
                qualNl4.item(0).getParentNode().replaceChild(n, qualNl4.item(0));
                return;
            } catch (XmlException e7) {
                throw new MarshalException(e7);
            }
        }
        throw new MarshalException("no XAdES-BES extension present");
    }

    public static byte[] getC14nValue(List<Node> nodeList, String c14nAlgoId) {
        ByteArrayOutputStream c14nValue = new ByteArrayOutputStream();
        try {
            for (Node node : nodeList) {
                Canonicalizer c14n = Canonicalizer.getInstance(c14nAlgoId);
                c14nValue.write(c14n.canonicalizeSubtree(node));
            }
            return c14nValue.toByteArray();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e2) {
            throw new RuntimeException("c14n error: " + e2.getMessage(), e2);
        }
    }

    private BigInteger getCrlNumber(X509CRL crl) {
        byte[] crlNumberExtensionValue = crl.getExtensionValue(Extension.cRLNumber.getId());
        if (crlNumberExtensionValue == null) {
            return null;
        }
        ASN1InputStream asn1IS1 = null;
        ASN1InputStream asn1IS2 = null;
        try {
            try {
                asn1IS1 = new ASN1InputStream(crlNumberExtensionValue);
                ASN1OctetString octetString = asn1IS1.readObject();
                byte[] octets = octetString.getOctets();
                asn1IS2 = new ASN1InputStream(octets);
                ASN1Integer integer = asn1IS2.readObject();
                return integer.getPositiveValue();
            } finally {
                IOUtils.closeQuietly(asn1IS2);
                IOUtils.closeQuietly(asn1IS1);
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O error: " + e.getMessage(), e);
        }
    }

    private XAdESTimeStampType createXAdESTimeStamp(List<Node> nodeList, RevocationData revocationData) {
        byte[] c14nSignatureValueElement = getC14nValue(nodeList, this.signatureConfig.getXadesCanonicalizationMethod());
        return createXAdESTimeStamp(c14nSignatureValueElement, revocationData);
    }

    private XAdESTimeStampType createXAdESTimeStamp(byte[] data, RevocationData revocationData) {
        try {
            byte[] timeStampToken = this.signatureConfig.getTspService().timeStamp(data, revocationData);
            XAdESTimeStampType xadesTimeStamp = XAdESTimeStampType.Factory.newInstance();
            xadesTimeStamp.setId("time-stamp-" + UUID.randomUUID());
            CanonicalizationMethodType c14nMethod = xadesTimeStamp.addNewCanonicalizationMethod();
            c14nMethod.setAlgorithm(this.signatureConfig.getXadesCanonicalizationMethod());
            EncapsulatedPKIDataType encapsulatedTimeStamp = xadesTimeStamp.addNewEncapsulatedTimeStamp();
            encapsulatedTimeStamp.setByteArrayValue(timeStampToken);
            encapsulatedTimeStamp.setId("time-stamp-token-" + UUID.randomUUID());
            return xadesTimeStamp;
        } catch (Exception e) {
            throw new RuntimeException("error while creating a time-stamp: " + e.getMessage(), e);
        }
    }

    private ValidationDataType createValidationData(RevocationData revocationData) {
        ValidationDataType validationData = ValidationDataType.Factory.newInstance();
        RevocationValuesType revocationValues = validationData.addNewRevocationValues();
        createRevocationValues(revocationValues, revocationData);
        return validationData;
    }

    private void createRevocationValues(RevocationValuesType revocationValues, RevocationData revocationData) {
        if (revocationData.hasCRLs()) {
            CRLValuesType crlValues = revocationValues.addNewCRLValues();
            for (byte[] crl : revocationData.getCRLs()) {
                EncapsulatedPKIDataType encapsulatedCrlValue = crlValues.addNewEncapsulatedCRLValue();
                encapsulatedCrlValue.setByteArrayValue(crl);
            }
        }
        if (revocationData.hasOCSPs()) {
            OCSPValuesType ocspValues = revocationValues.addNewOCSPValues();
            for (byte[] ocsp : revocationData.getOCSPs()) {
                EncapsulatedPKIDataType encapsulatedOcspValue = ocspValues.addNewEncapsulatedOCSPValue();
                encapsulatedOcspValue.setByteArrayValue(ocsp);
            }
        }
    }
}
