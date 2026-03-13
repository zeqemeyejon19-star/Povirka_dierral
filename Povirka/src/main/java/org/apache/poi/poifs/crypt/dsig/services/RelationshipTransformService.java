package org.apache.poi.poifs.crypt.dsig.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.OctetStreamData;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.apache.jcp.xml.dsig.internal.dom.ApacheNodeSetData;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTRelationshipReference;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.RelationshipReferenceDocument;
import org.w3.x2000.x09.xmldsig.TransformDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: classes.dex */
public class RelationshipTransformService extends TransformService {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) RelationshipTransformService.class);
    public static final String TRANSFORM_URI = "http://schemas.openxmlformats.org/package/2006/RelationshipTransform";
    private final List<String> sourceIds;

    public static class RelationshipTransformParameterSpec implements TransformParameterSpec {
        List<String> sourceIds = new ArrayList();

        public void addRelationshipReference(String relationshipId) {
            this.sourceIds.add(relationshipId);
        }

        public boolean hasSourceIds() {
            return !this.sourceIds.isEmpty();
        }
    }

    public RelationshipTransformService() {
        LOG.log(1, "constructor");
        this.sourceIds = new ArrayList();
    }

    public static synchronized void registerDsigProvider() {
        if (Security.getProperty("POIXmlDsigProvider") == null) {
            Provider p = new Provider("POIXmlDsigProvider", 1.0d, "POIXmlDsigProvider") { // from class: org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService.1
                static final long serialVersionUID = 1;
            };
            p.put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform", RelationshipTransformService.class.getName());
            p.put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform MechanismType", "DOM");
            Security.addProvider(p);
        }
    }

    public void init(TransformParameterSpec params) throws InvalidAlgorithmParameterException {
        LOG.log(1, "init(params)");
        if (!(params instanceof RelationshipTransformParameterSpec)) {
            throw new InvalidAlgorithmParameterException();
        }
        RelationshipTransformParameterSpec relParams = (RelationshipTransformParameterSpec) params;
        for (String sourceId : relParams.sourceIds) {
            this.sourceIds.add(sourceId);
        }
    }

    public void init(XMLStructure parent, XMLCryptoContext context) throws InvalidAlgorithmParameterException {
        POILogger pOILogger = LOG;
        pOILogger.log(1, "init(parent,context)");
        pOILogger.log(1, "parent java type: " + parent.getClass().getName());
        DOMStructure domParent = (DOMStructure) parent;
        Node parentNode = domParent.getNode();
        try {
            TransformDocument transDoc = TransformDocument.Factory.parse(parentNode, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            CTRelationshipReference[] cTRelationshipReferenceArrSelectChildren = transDoc.getTransform().selectChildren(RelationshipReferenceDocument.type.getDocumentElementName());
            if (cTRelationshipReferenceArrSelectChildren.length == 0) {
                pOILogger.log(5, "no RelationshipReference/@SourceId parameters present");
            }
            for (CTRelationshipReference cTRelationshipReference : cTRelationshipReferenceArrSelectChildren) {
                String sourceId = cTRelationshipReference.getSourceId();
                LOG.log(1, "sourceId: ", sourceId);
                try {
                    this.sourceIds.add(sourceId);
                } catch (XmlException e) {
                    e = e;
                    throw new InvalidAlgorithmParameterException((Throwable) e);
                }
            }
        } catch (XmlException e2) {
            e = e2;
        }
    }

    public void marshalParams(XMLStructure parent, XMLCryptoContext context) throws MarshalException {
        LOG.log(1, "marshallParams(parent,context)");
        DOMStructure domParent = (DOMStructure) parent;
        Element parentNode = (Element) domParent.getNode();
        Document doc = parentNode.getOwnerDocument();
        for (String sourceId : this.sourceIds) {
            Element el = doc.createElementNS("http://schemas.openxmlformats.org/package/2006/digital-signature", "mdssi:RelationshipReference");
            el.setAttributeNS(SignatureFacet.XML_NS, "xmlns:mdssi", "http://schemas.openxmlformats.org/package/2006/digital-signature");
            el.setAttribute("SourceId", sourceId);
            parentNode.appendChild(el);
        }
    }

    public AlgorithmParameterSpec getParameterSpec() {
        LOG.log(1, "getParameterSpec");
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.TransformException */
    public Data transform(Data data, XMLCryptoContext context) throws TransformException {
        POILogger pOILogger = LOG;
        pOILogger.log(1, "transform(data,context)");
        pOILogger.log(1, "data java type: " + data.getClass().getName());
        OctetStreamData octetStreamData = (OctetStreamData) data;
        pOILogger.log(1, "URI: " + octetStreamData.getURI());
        InputStream octetStream = octetStreamData.getOctetStream();
        try {
            Document doc = DocumentHelper.readDocument(octetStream);
            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();
            TreeMap<String, Element> rsList = new TreeMap<>();
            for (int i = nl.getLength() - 1; i >= 0; i--) {
                Node n = nl.item(i);
                if (PackageRelationship.RELATIONSHIP_TAG_NAME.equals(n.getLocalName())) {
                    Element el = (Element) n;
                    String id = el.getAttribute(PackageRelationship.ID_ATTRIBUTE_NAME);
                    if (this.sourceIds.contains(id)) {
                        String targetMode = el.getAttribute(PackageRelationship.TARGET_MODE_ATTRIBUTE_NAME);
                        if ("".equals(targetMode)) {
                            el.setAttribute(PackageRelationship.TARGET_MODE_ATTRIBUTE_NAME, "Internal");
                        }
                        rsList.put(id, el);
                    }
                }
                root.removeChild(n);
            }
            Iterator<Element> it = rsList.values().iterator();
            while (it.hasNext()) {
                root.appendChild(it.next());
            }
            LOG.log(1, "# Relationship elements: ", Integer.valueOf(rsList.size()));
            return new ApacheNodeSetData(new XMLSignatureInput(root));
        } catch (Exception e) {
            throw new TransformException(e.getMessage(), e);
        }
    }

    public Data transform(Data data, XMLCryptoContext context, OutputStream os) throws TransformException {
        LOG.log(1, "transform(data,context,os)");
        return null;
    }

    public boolean isFeatureSupported(String feature) {
        LOG.log(1, "isFeatureSupported(feature)");
        return false;
    }
}
