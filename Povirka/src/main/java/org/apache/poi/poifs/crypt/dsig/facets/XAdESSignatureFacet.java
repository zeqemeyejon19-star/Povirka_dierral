package org.apache.poi.poifs.crypt.dsig.facets;

import java.security.MessageDigest;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.services.SignaturePolicyService;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.CertIDListType;
import org.etsi.uri.x01903.v13.CertIDType;
import org.etsi.uri.x01903.v13.ClaimedRolesListType;
import org.etsi.uri.x01903.v13.DataObjectFormatType;
import org.etsi.uri.x01903.v13.DigestAlgAndValueType;
import org.etsi.uri.x01903.v13.IdentifierType;
import org.etsi.uri.x01903.v13.ObjectIdentifierType;
import org.etsi.uri.x01903.v13.QualifyingPropertiesDocument;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.etsi.uri.x01903.v13.SigPolicyQualifiersListType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdType;
import org.etsi.uri.x01903.v13.SignaturePolicyIdentifierType;
import org.etsi.uri.x01903.v13.SignedDataObjectPropertiesType;
import org.etsi.uri.x01903.v13.SignedPropertiesType;
import org.etsi.uri.x01903.v13.SignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.SignerRoleType;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class XAdESSignatureFacet extends SignatureFacet {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XAdESSignatureFacet.class);
    private static final String XADES_TYPE = "http://uri.etsi.org/01903#SignedProperties";
    private Map<String, String> dataObjectFormatMimeTypes = new HashMap();

    @Override // org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet
    public void preSign(Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        LOG.log(1, "preSign");
        QualifyingPropertiesDocument qualDoc = QualifyingPropertiesDocument.Factory.newInstance();
        QualifyingPropertiesType qualifyingProperties = qualDoc.addNewQualifyingProperties();
        qualifyingProperties.setTarget("#" + this.signatureConfig.getPackageSignatureId());
        SignedPropertiesType signedProperties = qualifyingProperties.addNewSignedProperties();
        signedProperties.setId(this.signatureConfig.getXadesSignatureId());
        SignedSignaturePropertiesType signedSignatureProperties = signedProperties.addNewSignedSignatureProperties();
        Calendar xmlGregorianCalendar = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ROOT);
        xmlGregorianCalendar.setTime(this.signatureConfig.getExecutionTime());
        xmlGregorianCalendar.clear(14);
        signedSignatureProperties.setSigningTime(xmlGregorianCalendar);
        if (this.signatureConfig.getSigningCertificateChain() == null || this.signatureConfig.getSigningCertificateChain().isEmpty()) {
            throw new RuntimeException("no signing certificate chain available");
        }
        CertIDListType signingCertificates = signedSignatureProperties.addNewSigningCertificate();
        CertIDType certId = signingCertificates.addNewCert();
        X509Certificate certificate = this.signatureConfig.getSigningCertificateChain().get(0);
        setCertID(certId, this.signatureConfig, this.signatureConfig.isXadesIssuerNameNoReverseOrder(), certificate);
        String role = this.signatureConfig.getXadesRole();
        if (role != null && !role.isEmpty()) {
            SignerRoleType signerRole = signedSignatureProperties.addNewSignerRole();
            signedSignatureProperties.setSignerRole(signerRole);
            ClaimedRolesListType claimedRolesList = signerRole.addNewClaimedRoles();
            AnyType claimedRole = claimedRolesList.addNewClaimedRole();
            XmlString roleString = XmlString.Factory.newInstance();
            roleString.setStringValue(role);
            insertXChild(claimedRole, roleString);
        }
        SignaturePolicyService policyService = this.signatureConfig.getSignaturePolicyService();
        if (policyService != null) {
            SignaturePolicyIdentifierType signaturePolicyIdentifier = signedSignatureProperties.addNewSignaturePolicyIdentifier();
            SignaturePolicyIdType signaturePolicyId = signaturePolicyIdentifier.addNewSignaturePolicyId();
            ObjectIdentifierType objectIdentifier = signaturePolicyId.addNewSigPolicyId();
            objectIdentifier.setDescription(policyService.getSignaturePolicyDescription());
            IdentifierType identifier = objectIdentifier.addNewIdentifier();
            identifier.setStringValue(policyService.getSignaturePolicyIdentifier());
            byte[] signaturePolicyDocumentData = policyService.getSignaturePolicyDocument();
            DigestAlgAndValueType sigPolicyHash = signaturePolicyId.addNewSigPolicyHash();
            setDigestAlgAndValue(sigPolicyHash, signaturePolicyDocumentData, this.signatureConfig.getDigestAlgo());
            String signaturePolicyDownloadUrl = policyService.getSignaturePolicyDownloadUrl();
            if (signaturePolicyDownloadUrl != null) {
                SigPolicyQualifiersListType sigPolicyQualifiers = signaturePolicyId.addNewSigPolicyQualifiers();
                AnyType sigPolicyQualifier = sigPolicyQualifiers.addNewSigPolicyQualifier();
                XmlString spUriElement = XmlString.Factory.newInstance();
                spUriElement.setStringValue(signaturePolicyDownloadUrl);
                insertXChild(sigPolicyQualifier, spUriElement);
            }
        } else if (this.signatureConfig.isXadesSignaturePolicyImplied()) {
            SignaturePolicyIdentifierType signaturePolicyIdentifier2 = signedSignatureProperties.addNewSignaturePolicyIdentifier();
            signaturePolicyIdentifier2.addNewSignaturePolicyImplied();
        }
        if (!this.dataObjectFormatMimeTypes.isEmpty()) {
            SignedDataObjectPropertiesType signedDataObjectProperties = signedProperties.addNewSignedDataObjectProperties();
            List<DataObjectFormatType> dataObjectFormats = signedDataObjectProperties.getDataObjectFormatList();
            for (Iterator<Map.Entry<String, String>> it = this.dataObjectFormatMimeTypes.entrySet().iterator(); it.hasNext(); it = it) {
                Map.Entry<String, String> dataObjectFormatMimeType = it.next();
                DataObjectFormatType dataObjectFormat = DataObjectFormatType.Factory.newInstance();
                dataObjectFormat.setObjectReference("#" + dataObjectFormatMimeType.getKey());
                dataObjectFormat.setMimeType(dataObjectFormatMimeType.getValue());
                dataObjectFormats.add(dataObjectFormat);
                signedDataObjectProperties = signedDataObjectProperties;
            }
        }
        List<XMLStructure> xadesObjectContent = new ArrayList<>();
        Element qualDocElSrc = (Element) qualifyingProperties.getDomNode();
        Element qualDocEl = (Element) document.importNode(qualDocElSrc, true);
        xadesObjectContent.add(new DOMStructure(qualDocEl));
        XMLObject xadesObject = getSignatureFactory().newXMLObject(xadesObjectContent, (String) null, (String) null, (String) null);
        objects.add(xadesObject);
        List<Transform> transforms = new ArrayList<>();
        Transform exclusiveTransform = newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        transforms.add(exclusiveTransform);
        Reference reference = newReference("#" + this.signatureConfig.getXadesSignatureId(), transforms, XADES_TYPE, null, null);
        references.add(reference);
    }

    protected static void setDigestAlgAndValue(DigestAlgAndValueType digestAlgAndValue, byte[] data, HashAlgorithm digestAlgo) {
        DigestMethodType digestMethod = digestAlgAndValue.addNewDigestMethod();
        digestMethod.setAlgorithm(SignatureConfig.getDigestMethodUri(digestAlgo));
        MessageDigest messageDigest = CryptoFunctions.getMessageDigest(digestAlgo);
        byte[] digestValue = messageDigest.digest(data);
        digestAlgAndValue.setDigestValue(digestValue);
    }

    protected static void setCertID(CertIDType certId, SignatureConfig signatureConfig, boolean issuerNameNoReverseOrder, X509Certificate certificate) {
        String issuerName;
        X509IssuerSerialType issuerSerial = certId.addNewIssuerSerial();
        if (issuerNameNoReverseOrder) {
            issuerName = certificate.getIssuerDN().getName().replace(",", ", ");
        } else {
            issuerName = certificate.getIssuerX500Principal().toString();
        }
        issuerSerial.setX509IssuerName(issuerName);
        issuerSerial.setX509SerialNumber(certificate.getSerialNumber());
        try {
            byte[] encodedCertificate = certificate.getEncoded();
            DigestAlgAndValueType certDigest = certId.addNewCertDigest();
            setDigestAlgAndValue(certDigest, encodedCertificate, signatureConfig.getXadesDigestAlgo());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("certificate encoding error: " + e.getMessage(), e);
        }
    }

    public void addMimeType(String dsReferenceUri, String mimetype) {
        this.dataObjectFormatMimeTypes.put(dsReferenceUri, mimetype);
    }

    protected static void insertXChild(XmlObject root, XmlObject child) {
        XmlCursor rootCursor = root.newCursor();
        rootCursor.toEndToken();
        XmlCursor childCursor = child.newCursor();
        childCursor.toNextToken();
        childCursor.moveXml(rootCursor);
        childCursor.dispose();
        rootCursor.dispose();
    }
}
