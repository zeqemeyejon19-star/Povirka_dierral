package org.apache.poi.poifs.crypt.dsig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.crypto.Cipher;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.jcp.xml.dsig.internal.dom.DOMReference;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xml.security.Init;
import org.apache.xml.security.utils.Base64;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class SignatureInfo implements SignatureConfig.SignatureConfigurable {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) SignatureInfo.class);
    private static boolean isInitialized = false;
    private SignatureConfig signatureConfig;

    public class SignaturePart {
        private List<X509Certificate> certChain;
        private final PackagePart signaturePart;
        private X509Certificate signer;

        private SignaturePart(PackagePart signaturePart) {
            this.signaturePart = signaturePart;
        }

        public PackagePart getPackagePart() {
            return this.signaturePart;
        }

        public X509Certificate getSigner() {
            return this.signer;
        }

        public List<X509Certificate> getCertChain() {
            return this.certChain;
        }

        public SignatureDocument getSignatureDocument() throws XmlException, IOException {
            return SignatureDocument.Factory.parse(this.signaturePart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }

        public boolean validate() {
            KeyInfoKeySelector keySelector = new KeyInfoKeySelector();
            try {
                Document doc = DocumentHelper.readDocument(this.signaturePart.getInputStream());
                XPath xpath = XPathFactory.newInstance().newXPath();
                NodeList nl = (NodeList) xpath.compile("//*[@Id]").evaluate(doc, XPathConstants.NODESET);
                int length = nl.getLength();
                for (int i = 0; i < length; i++) {
                    ((Element) nl.item(i)).setIdAttribute(PackageRelationship.ID_ATTRIBUTE_NAME, true);
                }
                DOMValidateContext domValidateContext = new DOMValidateContext(keySelector, doc);
                domValidateContext.setProperty("org.jcp.xml.dsig.validateManifests", Boolean.TRUE);
                domValidateContext.setURIDereferencer(SignatureInfo.this.signatureConfig.getUriDereferencer());
                SignatureInfo.this.brokenJvmWorkaround((XMLValidateContext) domValidateContext);
                XMLSignatureFactory xmlSignatureFactory = SignatureInfo.this.signatureConfig.getSignatureFactory();
                XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
                for (Reference ref : xmlSignature.getSignedInfo().getReferences()) {
                    SignatureFacet.brokenJvmWorkaround(ref);
                }
                for (XMLObject xo : xmlSignature.getObjects()) {
                    for (Manifest manifest : xo.getContent()) {
                        if (manifest instanceof Manifest) {
                            for (Reference ref2 : manifest.getReferences()) {
                                SignatureFacet.brokenJvmWorkaround(ref2);
                            }
                        }
                    }
                }
                boolean valid = xmlSignature.validate(domValidateContext);
                if (valid) {
                    this.signer = keySelector.getSigner();
                    this.certChain = keySelector.getCertChain();
                }
                return valid;
            } catch (SAXException e) {
                SignatureInfo.LOG.log(7, "error in parsing document", e);
                throw new EncryptedDocumentException("error in parsing document", e);
            } catch (MarshalException e2) {
                SignatureInfo.LOG.log(7, "error in unmarshalling the signature", e2);
                throw new EncryptedDocumentException("error in unmarshalling the signature", e2);
            } catch (IOException e3) {
                SignatureInfo.LOG.log(7, "error in reading document", e3);
                throw new EncryptedDocumentException("error in reading document", e3);
            } catch (XPathExpressionException e4) {
                SignatureInfo.LOG.log(7, "error in searching document with xpath expression", e4);
                throw new EncryptedDocumentException("error in searching document with xpath expression", e4);
            } catch (XMLSignatureException e5) {
                SignatureInfo.LOG.log(7, "error in validating the signature", e5);
                throw new EncryptedDocumentException("error in validating the signature", e5);
            }
        }
    }

    public SignatureInfo() {
        initXmlProvider();
    }

    public SignatureConfig getSignatureConfig() {
        return this.signatureConfig;
    }

    @Override // org.apache.poi.poifs.crypt.dsig.SignatureConfig.SignatureConfigurable
    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }

    public boolean verifySignature() {
        Iterator<SignaturePart> it = getSignatureParts().iterator();
        if (it.hasNext()) {
            SignaturePart sp = it.next();
            return sp.validate();
        }
        return false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void confirmSignature() throws MarshalException, XMLSignatureException {
        Document document = DocumentHelper.createDocument();
        DigestInfo digestInfo = preSign(document, null);
        byte[] signatureValue = signDigest(digestInfo.digestValue);
        postSign(document, signatureValue);
    }

    public byte[] signDigest(byte[] digest) {
        Cipher cipher = CryptoFunctions.getCipher(this.signatureConfig.getKey(), CipherAlgorithm.rsa, ChainingMode.ecb, null, 1, "PKCS1Padding");
        try {
            ByteArrayOutputStream digestInfoValueBuf = new ByteArrayOutputStream();
            digestInfoValueBuf.write(this.signatureConfig.getHashMagic());
            digestInfoValueBuf.write(digest);
            byte[] digestInfoValue = digestInfoValueBuf.toByteArray();
            byte[] signatureValue = cipher.doFinal(digestInfoValue);
            return signatureValue;
        } catch (Exception e) {
            throw new EncryptedDocumentException(e);
        }
    }

    public Iterable<SignaturePart> getSignatureParts() {
        this.signatureConfig.init(true);
        return new Iterable<SignaturePart>() { // from class: org.apache.poi.poifs.crypt.dsig.SignatureInfo.1
            @Override // java.lang.Iterable
            public Iterator<SignaturePart> iterator() {
                return new Iterator<SignaturePart>() { // from class: org.apache.poi.poifs.crypt.dsig.SignatureInfo.1.1
                    OPCPackage pkg;
                    Iterator<PackageRelationship> sigOrigRels;
                    PackagePart sigPart;
                    Iterator<PackageRelationship> sigRels;

                    {
                        OPCPackage opcPackage = SignatureInfo.this.signatureConfig.getOpcPackage();
                        this.pkg = opcPackage;
                        this.sigOrigRels = opcPackage.getRelationshipsByType(PackageRelationshipTypes.DIGITAL_SIGNATURE_ORIGIN).iterator();
                        this.sigRels = null;
                        this.sigPart = null;
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        while (true) {
                            Iterator<PackageRelationship> it = this.sigRels;
                            if (it != null && it.hasNext()) {
                                return true;
                            }
                            if (!this.sigOrigRels.hasNext()) {
                                return false;
                            }
                            this.sigPart = this.pkg.getPart(this.sigOrigRels.next());
                            SignatureInfo.LOG.log(1, "Digital Signature Origin part", this.sigPart);
                            try {
                                this.sigRels = this.sigPart.getRelationshipsByType(PackageRelationshipTypes.DIGITAL_SIGNATURE).iterator();
                            } catch (InvalidFormatException e) {
                                SignatureInfo.LOG.log(5, "Reference to signature is invalid.", e);
                            }
                        }
                    }

                    @Override // java.util.Iterator
                    public SignaturePart next() {
                        PackagePart sigRelPart = null;
                        do {
                            try {
                                if (!hasNext()) {
                                    throw new NoSuchElementException();
                                }
                                sigRelPart = this.sigPart.getRelatedPart(this.sigRels.next());
                                SignatureInfo.LOG.log(1, "XML Signature part", sigRelPart);
                            } catch (InvalidFormatException e) {
                                SignatureInfo.LOG.log(5, "Reference to signature is invalid.", e);
                            }
                        } while (this.sigPart == null);
                        return new SignaturePart(sigRelPart);
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    protected static synchronized void initXmlProvider() {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        try {
            Init.init();
            RelationshipTransformService.registerDsigProvider();
            CryptoFunctions.registerBouncyCastle();
        } catch (Exception e) {
            throw new RuntimeException("Xml & BouncyCastle-Provider initialization failed", e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public DigestInfo preSign(Document document, List<DigestInfo> digestInfos) throws MarshalException, XMLSignatureException {
        this.signatureConfig.init(false);
        EventTarget target = (EventTarget) document;
        EventListener creationListener = this.signatureConfig.getSignatureMarshalListener();
        int i = 1;
        if (creationListener != null) {
            if (creationListener instanceof SignatureMarshalListener) {
                ((SignatureMarshalListener) creationListener).setEventTarget(target);
            }
            SignatureMarshalListener.setListener(target, creationListener, true);
        }
        DOMSignContext dOMSignContext = new DOMSignContext(this.signatureConfig.getKey(), document);
        URIDereferencer uriDereferencer = this.signatureConfig.getUriDereferencer();
        if (uriDereferencer != null) {
            dOMSignContext.setURIDereferencer(uriDereferencer);
        }
        for (Map.Entry<String, String> me : this.signatureConfig.getNamespacePrefixes().entrySet()) {
            dOMSignContext.putNamespacePrefix(me.getKey(), me.getValue());
        }
        dOMSignContext.setDefaultNamespacePrefix("");
        brokenJvmWorkaround((XMLSignContext) dOMSignContext);
        XMLSignatureFactory signatureFactory = this.signatureConfig.getSignatureFactory();
        List<Reference> references = new ArrayList<>();
        for (DigestInfo digestInfo : safe(digestInfos)) {
            byte[] documentDigestValue = digestInfo.digestValue;
            String uri = new File(digestInfo.description).getName();
            Reference reference = SignatureFacet.newReference(uri, null, null, null, documentDigestValue, this.signatureConfig);
            references.add(reference);
        }
        List<XMLObject> objects = new ArrayList<>();
        for (SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            POILogger pOILogger = LOG;
            Object[] objArr = new Object[i];
            objArr[0] = "invoking signature facet: " + signatureFacet.getClass().getSimpleName();
            pOILogger.log(1, objArr);
            signatureFacet.preSign(document, references, objects);
            i = 1;
        }
        try {
            SignatureMethod signatureMethod = signatureFactory.newSignatureMethod(this.signatureConfig.getSignatureMethodUri(), (SignatureMethodParameterSpec) null);
            CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(this.signatureConfig.getCanonicalizationMethod(), (C14NMethodParameterSpec) null);
            DOMSignedInfo dOMSignedInfoNewSignedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, references);
            String signatureValueId = this.signatureConfig.getPackageSignatureId() + "-signature-value";
            XMLSignature xmlSignature = signatureFactory.newXMLSignature(dOMSignedInfoNewSignedInfo, (KeyInfo) null, objects, this.signatureConfig.getPackageSignatureId(), signatureValueId);
            xmlSignature.sign(dOMSignContext);
            for (XMLObject object : objects) {
                LOG.log(1, "object java type: " + object.getClass().getName());
                List<XMLStructure> objectContentList = object.getContent();
                for (XMLStructure xMLStructure : objectContentList) {
                    List<XMLStructure> objectContentList2 = objectContentList;
                    EventTarget target2 = target;
                    LOG.log(1, "object content java type: " + xMLStructure.getClass().getName());
                    if (xMLStructure instanceof Manifest) {
                        Manifest manifest = (Manifest) xMLStructure;
                        List<Reference> manifestReferences = manifest.getReferences();
                        for (Reference reference2 : manifestReferences) {
                            if (reference2.getDigestValue() == null) {
                                DOMReference manifestDOMReference = (DOMReference) reference2;
                                manifestDOMReference.digest(dOMSignContext);
                            }
                        }
                        objectContentList = objectContentList2;
                        target = target2;
                    } else {
                        objectContentList = objectContentList2;
                        target = target2;
                    }
                }
                target = target;
            }
            List<Reference> signedInfoReferences = dOMSignedInfoNewSignedInfo.getReferences();
            Iterator<Reference> it = signedInfoReferences.iterator();
            while (it.hasNext()) {
                DOMReference domReference = (DOMReference) it.next();
                if (domReference.getDigestValue() == null) {
                    domReference.digest(dOMSignContext);
                }
            }
            DOMSignedInfo domSignedInfo = dOMSignedInfoNewSignedInfo;
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            domSignedInfo.canonicalize(dOMSignContext, dataStream);
            byte[] octets = dataStream.toByteArray();
            MessageDigest md = CryptoFunctions.getMessageDigest(this.signatureConfig.getDigestAlgo());
            byte[] digestValue = md.digest(octets);
            String description = this.signatureConfig.getSignatureDescription();
            return new DigestInfo(digestValue, this.signatureConfig.getDigestAlgo(), description);
        } catch (GeneralSecurityException e) {
            throw new XMLSignatureException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public void postSign(Document document, byte[] signatureValue) throws MarshalException {
        LOG.log(1, "postSign");
        String signatureId = this.signatureConfig.getPackageSignatureId();
        if (!signatureId.equals(document.getDocumentElement().getAttribute(PackageRelationship.ID_ATTRIBUTE_NAME))) {
            throw new RuntimeException("ds:Signature not found for @Id: " + signatureId);
        }
        NodeList sigValNl = document.getElementsByTagNameNS(SignatureFacet.XML_DIGSIG_NS, "SignatureValue");
        if (sigValNl.getLength() != 1) {
            throw new RuntimeException("preSign has to be called before postSign");
        }
        sigValNl.item(0).setTextContent(Base64.encode(signatureValue));
        for (SignatureFacet signatureFacet : this.signatureConfig.getSignatureFacets()) {
            signatureFacet.postSign(document);
        }
        writeDocument(document);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    protected void writeDocument(Document document) throws MarshalException {
        XmlOptions xo = new XmlOptions();
        Map<String, String> namespaceMap = new HashMap<>();
        for (Map.Entry<String, String> entry : this.signatureConfig.getNamespacePrefixes().entrySet()) {
            namespaceMap.put(entry.getValue(), entry.getKey());
        }
        xo.setSaveSuggestedPrefixes(namespaceMap);
        xo.setUseDefaultNamespace();
        LOG.log(1, "output signed Office OpenXML document");
        OPCPackage pkg = this.signatureConfig.getOpcPackage();
        try {
            PackagePartName sigPartName = PackagingURIHelper.createPartName("/_xmlsignatures/sig1.xml");
            try {
                PackagePartName sigsPartName = PackagingURIHelper.createPartName("/_xmlsignatures/origin.sigs");
                PackagePart sigPart = pkg.getPart(sigPartName);
                if (sigPart == null) {
                    sigPart = pkg.createPart(sigPartName, ContentTypes.DIGITAL_SIGNATURE_XML_SIGNATURE_PART);
                }
                try {
                    OutputStream os = sigPart.getOutputStream();
                    SignatureDocument sigDoc = SignatureDocument.Factory.parse(document, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
                    sigDoc.save(os, xo);
                    os.close();
                    PackagePart sigsPart = pkg.getPart(sigsPartName);
                    if (sigsPart == null) {
                        sigsPart = pkg.createPart(sigsPartName, ContentTypes.DIGITAL_SIGNATURE_ORIGIN_PART);
                    }
                    PackageRelationshipCollection relCol = pkg.getRelationshipsByType(PackageRelationshipTypes.DIGITAL_SIGNATURE_ORIGIN);
                    for (PackageRelationship pr : relCol) {
                        pkg.removeRelationship(pr.getId());
                    }
                    pkg.addRelationship(sigsPartName, TargetMode.INTERNAL, PackageRelationshipTypes.DIGITAL_SIGNATURE_ORIGIN);
                    sigsPart.addRelationship(sigPartName, TargetMode.INTERNAL, PackageRelationshipTypes.DIGITAL_SIGNATURE);
                } catch (Exception e) {
                    throw new MarshalException("Unable to write signature document", e);
                }
            } catch (InvalidFormatException e2) {
                e = e2;
                throw new MarshalException(e);
            }
        } catch (InvalidFormatException e3) {
            e = e3;
        }
    }

    private static <T> List<T> safe(List<T> other) {
        List<T> emptyList = Collections.emptyList();
        return other == null ? emptyList : other;
    }

    private void brokenJvmWorkaround(XMLSignContext context) {
        Provider bcProv = Security.getProvider("BC");
        if (bcProv != null) {
            context.setProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider", bcProv);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void brokenJvmWorkaround(XMLValidateContext context) {
        Provider bcProv = Security.getProvider("BC");
        if (bcProv != null) {
            context.setProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider", bcProv);
        }
    }
}
