package org.apache.poi.poifs.crypt.dsig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.crypto.Data;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
public class OOXMLURIDereferencer implements URIDereferencer, SignatureConfig.SignatureConfigurable {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) OOXMLURIDereferencer.class);
    private URIDereferencer baseUriDereferencer;
    private SignatureConfig signatureConfig;

    @Override // org.apache.poi.poifs.crypt.dsig.SignatureConfig.SignatureConfigurable
    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.URIReferenceException */
    public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
        IOException e;
        if (this.baseUriDereferencer == null) {
            this.baseUriDereferencer = this.signatureConfig.getSignatureFactory().getURIDereferencer();
        }
        if (uriReference == null) {
            throw new NullPointerException("URIReference cannot be null");
        }
        if (context == null) {
            throw new NullPointerException("XMLCrytoContext cannot be null");
        }
        try {
            URI uri = new URI(uriReference.getURI());
            PackagePart part = findPart(uri);
            if (part == null) {
                LOG.log(1, "cannot resolve, delegating to base DOM URI dereferencer", uri);
                return this.baseUriDereferencer.dereference(uriReference, context);
            }
            try {
                InputStream dataStream = part.getInputStream();
                try {
                    if (part.getPartName().toString().endsWith(".rels")) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        while (true) {
                            int ch = dataStream.read();
                            if (ch == -1) {
                                break;
                            }
                            if (ch != 10 && ch != 13) {
                                bos.write(ch);
                            }
                        }
                        dataStream = new ByteArrayInputStream(bos.toByteArray());
                    }
                    return new OctetStreamData(dataStream, uri.toString(), (String) null);
                } catch (IOException e2) {
                    e = e2;
                    throw new URIReferenceException("I/O error: " + e.getMessage(), e);
                }
            } catch (IOException e3) {
                e = e3;
            }
        } catch (URISyntaxException e4) {
            throw new URIReferenceException("could not URL decode the uri: " + uriReference.getURI(), e4);
        }
    }

    private PackagePart findPart(URI uri) {
        POILogger pOILogger = LOG;
        pOILogger.log(1, "dereference", uri);
        String path = uri.getPath();
        if (path == null || "".equals(path)) {
            pOILogger.log(1, "illegal part name (expected)", uri);
            return null;
        }
        try {
            PackagePartName ppn = PackagingURIHelper.createPartName(path);
            return this.signatureConfig.getOpcPackage().getPart(ppn);
        } catch (InvalidFormatException e) {
            LOG.log(5, "illegal part name (not expected)", uri);
            return null;
        }
    }
}
