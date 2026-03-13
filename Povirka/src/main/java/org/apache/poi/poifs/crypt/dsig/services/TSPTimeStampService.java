package org.apache.poi.poifs.crypt.dsig.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.DatatypeConverter;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: classes.dex */
public class TSPTimeStampService implements TimeStampService {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) TSPTimeStampService.class);
    private SignatureConfig signatureConfig;

    /* JADX INFO: renamed from: org.apache.poi.poifs.crypt.dsig.services.TSPTimeStampService$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm;

        static {
            int[] iArr = new int[HashAlgorithm.values().length];
            $SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm = iArr;
            try {
                iArr[HashAlgorithm.sha1.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm[HashAlgorithm.sha256.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm[HashAlgorithm.sha384.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm[HashAlgorithm.sha512.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public ASN1ObjectIdentifier mapDigestAlgoToOID(HashAlgorithm digestAlgo) {
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$poifs$crypt$HashAlgorithm[digestAlgo.ordinal()];
        if (i == 1) {
            return X509ObjectIdentifiers.id_SHA1;
        }
        if (i == 2) {
            return NISTObjectIdentifiers.id_sha256;
        }
        if (i == 3) {
            return NISTObjectIdentifiers.id_sha384;
        }
        if (i == 4) {
            return NISTObjectIdentifiers.id_sha512;
        }
        throw new IllegalArgumentException("unsupported digest algo: " + digestAlgo);
    }

    @Override // org.apache.poi.poifs.crypt.dsig.services.TimeStampService
    public byte[] timeStamp(byte[] data, RevocationData revocationData) throws Exception {
        POILogger pOILogger;
        MessageDigest messageDigest = CryptoFunctions.getMessageDigest(this.signatureConfig.getTspDigestAlgo());
        byte[] digest = messageDigest.digest(data);
        BigInteger nonce = new BigInteger(128, new SecureRandom());
        TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
        requestGenerator.setCertReq(true);
        String requestPolicy = this.signatureConfig.getTspRequestPolicy();
        if (requestPolicy != null) {
            requestGenerator.setReqPolicy(new ASN1ObjectIdentifier(requestPolicy));
        }
        ASN1ObjectIdentifier digestAlgoOid = mapDigestAlgoToOID(this.signatureConfig.getTspDigestAlgo());
        TimeStampRequest request = requestGenerator.generate(digestAlgoOid, digest, nonce);
        byte[] encodedRequest = request.getEncoded();
        Proxy proxy = Proxy.NO_PROXY;
        if (this.signatureConfig.getProxyUrl() != null) {
            URL proxyUrl = new URL(this.signatureConfig.getProxyUrl());
            String host = proxyUrl.getHost();
            int port = proxyUrl.getPort();
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(host), port == -1 ? 80 : port));
        }
        HttpURLConnection huc = (HttpURLConnection) new URL(this.signatureConfig.getTspUrl()).openConnection(proxy);
        if (this.signatureConfig.getTspUser() != null) {
            String userPassword = this.signatureConfig.getTspUser() + ":" + this.signatureConfig.getTspPass();
            String encoding = DatatypeConverter.printBase64Binary(userPassword.getBytes(Charset.forName("iso-8859-1")));
            huc.setRequestProperty("Authorization", "Basic " + encoding);
        }
        huc.setRequestMethod("POST");
        huc.setConnectTimeout(20000);
        huc.setReadTimeout(20000);
        huc.setDoOutput(true);
        huc.setRequestProperty("User-Agent", this.signatureConfig.getUserAgent());
        huc.setRequestProperty("Content-Type", this.signatureConfig.isTspOldProtocol() ? "application/timestamp-request" : "application/timestamp-query");
        OutputStream hucOut = huc.getOutputStream();
        hucOut.write(encodedRequest);
        huc.connect();
        int statusCode = huc.getResponseCode();
        if (statusCode != 200) {
            LOG.log(7, "Error contacting TSP server ", this.signatureConfig.getTspUrl() + ", had status code " + statusCode + "/" + huc.getResponseMessage());
            throw new IOException("Error contacting TSP server " + this.signatureConfig.getTspUrl() + ", had status code " + statusCode + "/" + huc.getResponseMessage());
        }
        String contentType = huc.getHeaderField("Content-Type");
        if (contentType == null) {
            throw new RuntimeException("missing Content-Type header");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(huc.getInputStream(), bos);
        POILogger pOILogger2 = LOG;
        pOILogger2.log(1, "response content: ", HexDump.dump(bos.toByteArray(), 0L, 0));
        if (!contentType.startsWith(this.signatureConfig.isTspOldProtocol() ? "application/timestamp-response" : "application/timestamp-reply")) {
            throw new RuntimeException("invalid Content-Type: " + contentType + ": " + HexDump.dump(bos.toByteArray(), 0L, 0, 200));
        }
        if (bos.size() == 0) {
            throw new RuntimeException("Content-Length is zero");
        }
        TimeStampResponse timeStampResponse = new TimeStampResponse(bos.toByteArray());
        timeStampResponse.validate(request);
        if (timeStampResponse.getStatus() != 0) {
            pOILogger2.log(1, "status: " + timeStampResponse.getStatus());
            pOILogger2.log(1, "status string: " + timeStampResponse.getStatusString());
            PKIFailureInfo failInfo = timeStampResponse.getFailInfo();
            if (failInfo != null) {
                pOILogger2.log(1, "fail info int value: " + failInfo.intValue());
                if (256 == failInfo.intValue()) {
                    pOILogger2.log(1, "unaccepted policy");
                }
            }
            throw new RuntimeException("timestamp response status != 0: " + timeStampResponse.getStatus());
        }
        TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
        SignerId signerId = timeStampToken.getSID();
        BigInteger signerCertSerialNumber = signerId.getSerialNumber();
        X500Name signerCertIssuer = signerId.getIssuer();
        pOILogger2.log(1, "signer cert serial number: " + signerCertSerialNumber);
        pOILogger2.log(1, "signer cert issuer: " + signerCertIssuer);
        Collection<X509CertificateHolder> certificates = timeStampToken.getCertificates().getMatches((Selector) null);
        X509CertificateHolder signerCert = null;
        Map<X500Name, X509CertificateHolder> certificateMap = new HashMap<>();
        for (X509CertificateHolder certificate : certificates) {
            Collection<X509CertificateHolder> certificates2 = certificates;
            ASN1ObjectIdentifier digestAlgoOid2 = digestAlgoOid;
            if (signerCertIssuer.equals(certificate.getIssuer()) && signerCertSerialNumber.equals(certificate.getSerialNumber())) {
                signerCert = certificate;
            }
            certificateMap.put(certificate.getSubject(), certificate);
            certificates = certificates2;
            digestAlgoOid = digestAlgoOid2;
        }
        if (signerCert == null) {
            throw new RuntimeException("TSP response token has no signer certificate");
        }
        List<X509Certificate> tspCertificateChain = new ArrayList<>();
        JcaX509CertificateConverter x509converter = new JcaX509CertificateConverter();
        x509converter.setProvider("BC");
        X509CertificateHolder certificate2 = signerCert;
        while (true) {
            X509CertificateHolder signerCert2 = signerCert;
            pOILogger = LOG;
            TimeStampRequest request2 = request;
            byte[] encodedRequest2 = encodedRequest;
            Proxy proxy2 = proxy;
            pOILogger.log(1, "adding to certificate chain: " + certificate2.getSubject());
            tspCertificateChain.add(x509converter.getCertificate(certificate2));
            if (certificate2.getSubject().equals(certificate2.getIssuer()) || (certificate2 = certificateMap.get(certificate2.getIssuer())) == null) {
                break;
            }
            signerCert = signerCert2;
            request = request2;
            encodedRequest = encodedRequest2;
            proxy = proxy2;
            signerCertSerialNumber = signerCertSerialNumber;
        }
        X509CertificateHolder holder = new X509CertificateHolder(tspCertificateChain.get(0).getEncoded());
        DefaultCMSSignatureAlgorithmNameGenerator nameGen = new DefaultCMSSignatureAlgorithmNameGenerator();
        DefaultSignatureAlgorithmIdentifierFinder sigAlgoFinder = new DefaultSignatureAlgorithmIdentifierFinder();
        DefaultDigestAlgorithmIdentifierFinder hashAlgoFinder = new DefaultDigestAlgorithmIdentifierFinder();
        BcDigestCalculatorProvider calculator = new BcDigestCalculatorProvider();
        BcRSASignerInfoVerifierBuilder verifierBuilder = new BcRSASignerInfoVerifierBuilder(nameGen, sigAlgoFinder, hashAlgoFinder, calculator);
        SignerInformationVerifier verifier = verifierBuilder.build(holder);
        timeStampToken.validate(verifier);
        if (this.signatureConfig.getTspValidator() != null) {
            this.signatureConfig.getTspValidator().validate(tspCertificateChain, revocationData);
        }
        pOILogger.log(1, "time-stamp token time: " + timeStampToken.getTimeStampInfo().getGenTime());
        return timeStampToken.getEncoded();
    }

    @Override // org.apache.poi.poifs.crypt.dsig.SignatureConfig.SignatureConfigurable
    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
}
