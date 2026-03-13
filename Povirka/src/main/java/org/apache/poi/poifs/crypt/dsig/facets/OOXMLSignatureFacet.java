package org.apache.poi.poifs.crypt.dsig.facets;

import com.microsoft.schemas.office.x2006.digsig.CTSignatureInfoV1;
import com.microsoft.schemas.office.x2006.digsig.SignatureInfoV1Document;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.dsig.services.RelationshipTransformService;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.CTSignatureTime;
import org.openxmlformats.schemas.xpackage.x2006.digitalSignature.SignatureTimeDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: classes.dex */
public class OOXMLSignatureFacet extends SignatureFacet {
    private static final String ID_PACKAGE_OBJECT = "idPackageObject";
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) OOXMLSignatureFacet.class);
    private static final Set<String> signed = Collections.unmodifiableSet(new HashSet(Arrays.asList("activeXControlBinary", "aFChunk", "attachedTemplate", "attachedToolbars", "audio", "calcChain", "chart", "chartColorStyle", "chartLayout", "chartsheet", "chartStyle", "chartUserShapes", "commentAuthors", "comments", "connections", "connectorXml", "control", "ctrlProp", "customData", "customData", "customProperty", "customXml", "diagram", "diagramColors", "diagramColorsHeader", "diagramData", "diagramDrawing", "diagramLayout", "diagramLayoutHeader", "diagramQuickStyle", "diagramQuickStyleHeader", "dialogsheet", "dictionary", "documentParts", "downRev", "drawing", "endnotes", "externalLink", "externalLinkPath", CellUtil.FONT, "fontTable", "footer", "footnotes", "functionPrototypes", "glossaryDocument", "graphicFrameDoc", "groupShapeXml", "handoutMaster", "hdphoto", "header", "hyperlink", "image", "ink", "inkXml", "keyMapCustomizations", "legacyDiagramText", "legacyDocTextInfo", "mailMergeHeaderSource", "mailMergeRecipientData", "mailMergeSource", "media", "notesMaster", "notesSlide", "numbering", "officeDocument", "officeDocument", "oleObject", "package", "pictureXml", "pivotCacheDefinition", "pivotCacheRecords", "pivotTable", "powerPivotData", "presProps", "printerSettings", "queryTable", "recipientData", "settings", "shapeXml", "sharedStrings", "sheetMetadata", "slicer", "slicer", "slicerCache", "slicerCache", "slide", "slideLayout", "slideMaster", "slideUpdateInfo", "slideUpdateUrl", "smartTags", "styles", "stylesWithEffects", "table", "tableSingleCells", "tableStyles", "tags", "theme", "themeOverride", "timeline", "timelineCache", "transform", "ui/altText", "ui/buttonSize", "ui/controlID", "ui/description", "ui/enabled", "ui/extensibility", "ui/extensibility", "ui/helperText", "ui/imageID", "ui/imageMso", "ui/keyTip", "ui/label", "ui/lcid", "ui/loud", "ui/pressed", "ui/progID", "ui/ribbonID", "ui/showImage", "ui/showLabel", "ui/supertip", "ui/target", "ui/text", "ui/title", "ui/tooltip", "ui/userCustomization", "ui/visible", "userXmlData", "vbaProject", "video", "viewProps", "vmlDrawing", "volatileDependencies", "webSettings", "wordVbaData", "worksheet", "wsSortMap", "xlBinaryIndex", "xlExternalLinkPath/xlAlternateStartup", "xlExternalLinkPath/xlLibrary", "xlExternalLinkPath/xlPathMissing", "xlExternalLinkPath/xlStartup", "xlIntlMacrosheet", "xlMacrosheet", "xmlMaps")));

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    @Override // org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet
    public void preSign(Document document, List<Reference> references, List<XMLObject> objects) throws URISyntaxException, XMLSignatureException {
        LOG.log(1, "pre sign");
        addManifestObject(document, references, objects);
        addSignatureInfo(document, references, objects);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    protected void addManifestObject(Document document, List<Reference> references, List<XMLObject> objects) throws URISyntaxException, XMLSignatureException {
        List<Reference> manifestReferences = new ArrayList<>();
        addManifestReferences(manifestReferences);
        Manifest manifest = getSignatureFactory().newManifest(manifestReferences);
        List<XMLStructure> objectContent = new ArrayList<>();
        objectContent.add(manifest);
        addSignatureTime(document, objectContent);
        XMLObject xo = getSignatureFactory().newXMLObject(objectContent, ID_PACKAGE_OBJECT, (String) null, (String) null);
        objects.add(xo);
        Reference reference = newReference("#idPackageObject", null, "http://www.w3.org/2000/09/xmldsig#Object", null, null);
        references.add(reference);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    protected void addManifestReferences(List<Reference> manifestReferences) throws URISyntaxException, XMLSignatureException {
        RelationshipTransformService.RelationshipTransformParameterSpec parameterSpec;
        OPCPackage ooxml = this.signatureConfig.getOpcPackage();
        List<PackagePart> relsEntryNames = ooxml.getPartsByContentType(ContentTypes.RELATIONSHIPS_PART);
        Set<String> digestedPartNames = new HashSet<>();
        String str = null;
        PackageRelationshipCollection packageRelationshipCollection = null;
        for (PackagePart pp : relsEntryNames) {
            String baseUri = pp.getPartName().getName().replaceFirst("(.*)/_rels/.*", "$1");
            try {
                PackageRelationshipCollection prc = new PackageRelationshipCollection(ooxml);
                try {
                    prc.parseRelationshipsPart(pp);
                    RelationshipTransformService.RelationshipTransformParameterSpec parameterSpec2 = new RelationshipTransformService.RelationshipTransformParameterSpec();
                    String str2 = str;
                    for (PackageRelationship relationship : prc) {
                        String relationshipType = relationship.getRelationshipType();
                        if (TargetMode.EXTERNAL != relationship.getTargetMode() && isSignedRelationship(relationshipType)) {
                            parameterSpec2.addRelationshipReference(relationship.getId());
                            String partName = normalizePartName(relationship.getTargetURI(), baseUri);
                            if (digestedPartNames.contains(partName)) {
                                continue;
                            } else {
                                digestedPartNames.add(partName);
                                try {
                                    PackagePartName relName = PackagingURIHelper.createPartName(partName);
                                    PackagePart pp2 = ooxml.getPart(relName);
                                    String contentType = pp2.getContentType();
                                    if (relationshipType.endsWith("customXml") && !contentType.equals("inkml+xml") && !contentType.equals(ContentTypes.XML)) {
                                        LOG.log(1, "skipping customXml with content type: " + contentType);
                                        parameterSpec = parameterSpec2;
                                    } else {
                                        String uri = partName + "?ContentType=" + contentType;
                                        parameterSpec = parameterSpec2;
                                        Reference reference = newReference(uri, null, null, null, null);
                                        manifestReferences.add(reference);
                                    }
                                    str2 = contentType;
                                    parameterSpec2 = parameterSpec;
                                } catch (InvalidFormatException e) {
                                    throw new XMLSignatureException(e);
                                }
                            }
                        }
                    }
                    RelationshipTransformService.RelationshipTransformParameterSpec parameterSpec3 = parameterSpec2;
                    if (parameterSpec3.hasSourceIds()) {
                        List<Transform> transforms = new ArrayList<>();
                        transforms.add(newTransform(RelationshipTransformService.TRANSFORM_URI, parameterSpec3));
                        transforms.add(newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315"));
                        String uri2 = normalizePartName(pp.getPartName().getURI(), baseUri) + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
                        Reference reference2 = newReference(uri2, transforms, null, null, null);
                        manifestReferences.add(reference2);
                    }
                    packageRelationshipCollection = prc;
                    str = str2;
                } catch (InvalidFormatException e2) {
                    e = e2;
                    throw new XMLSignatureException("Invalid relationship descriptor: " + pp.getPartName().getName(), e);
                }
            } catch (InvalidFormatException e3) {
                e = e3;
            }
        }
        Collections.sort(manifestReferences, new Comparator<Reference>() { // from class: org.apache.poi.poifs.crypt.dsig.facets.OOXMLSignatureFacet.1
            @Override // java.util.Comparator
            public int compare(Reference o1, Reference o2) {
                return o1.getURI().compareTo(o2.getURI());
            }
        });
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private static String normalizePartName(URI partName, String baseUri) throws XMLSignatureException {
        String pn = partName.toASCIIString();
        if (!pn.startsWith(baseUri)) {
            pn = baseUri + pn;
        }
        try {
            String pn2 = new URI(pn).normalize().getPath().replace('\\', '/');
            LOG.log(1, "part name: " + pn2);
            return pn2;
        } catch (URISyntaxException e) {
            throw new XMLSignatureException(e);
        }
    }

    protected void addSignatureTime(Document document, List<XMLStructure> objectContent) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT);
        fmt.setTimeZone(LocaleUtil.TIMEZONE_UTC);
        String nowStr = fmt.format(this.signatureConfig.getExecutionTime());
        LOG.log(1, "now: " + nowStr);
        SignatureTimeDocument sigTime = SignatureTimeDocument.Factory.newInstance();
        CTSignatureTime ctTime = sigTime.addNewSignatureTime();
        ctTime.setFormat("YYYY-MM-DDThh:mm:ssTZD");
        ctTime.setValue(nowStr);
        Element n = (Element) document.importNode(ctTime.getDomNode(), true);
        List<XMLStructure> signatureTimeContent = new ArrayList<>();
        signatureTimeContent.add(new DOMStructure(n));
        SignatureProperty signatureTimeSignatureProperty = getSignatureFactory().newSignatureProperty(signatureTimeContent, "#" + this.signatureConfig.getPackageSignatureId(), "idSignatureTime");
        List<SignatureProperty> signaturePropertyContent = new ArrayList<>();
        signaturePropertyContent.add(signatureTimeSignatureProperty);
        SignatureProperties signatureProperties = getSignatureFactory().newSignatureProperties(signaturePropertyContent, (String) null);
        objectContent.add(signatureProperties);
    }

    protected void addSignatureInfo(Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        List<XMLStructure> objectContent = new ArrayList<>();
        SignatureInfoV1Document sigV1 = SignatureInfoV1Document.Factory.newInstance();
        CTSignatureInfoV1 ctSigV1 = sigV1.addNewSignatureInfoV1();
        ctSigV1.setManifestHashAlgorithm(this.signatureConfig.getDigestMethodUri());
        Element n = (Element) document.importNode(ctSigV1.getDomNode(), true);
        n.setAttributeNS(SignatureFacet.XML_NS, "xmlns", SignatureFacet.MS_DIGSIG_NS);
        List<XMLStructure> signatureInfoContent = new ArrayList<>();
        signatureInfoContent.add(new DOMStructure(n));
        SignatureProperty signatureInfoSignatureProperty = getSignatureFactory().newSignatureProperty(signatureInfoContent, "#" + this.signatureConfig.getPackageSignatureId(), "idOfficeV1Details");
        List<SignatureProperty> signaturePropertyContent = new ArrayList<>();
        signaturePropertyContent.add(signatureInfoSignatureProperty);
        SignatureProperties signatureProperties = getSignatureFactory().newSignatureProperties(signaturePropertyContent, (String) null);
        objectContent.add(signatureProperties);
        objects.add(getSignatureFactory().newXMLObject(objectContent, "idOfficeObject", (String) null, (String) null));
        Reference reference = newReference("#idOfficeObject", null, "http://www.w3.org/2000/09/xmldsig#Object", null, null);
        references.add(reference);
    }

    protected static String getRelationshipReferenceURI(String zipEntryName) {
        return "/" + zipEntryName + "?ContentType=application/vnd.openxmlformats-package.relationships+xml";
    }

    protected static String getResourceReferenceURI(String resourceName, String contentType) {
        return "/" + resourceName + "?ContentType=" + contentType;
    }

    protected static boolean isSignedRelationship(String relationshipType) {
        LOG.log(1, "relationship type: " + relationshipType);
        String rt = relationshipType.replaceFirst(".*/relationships/", "");
        return signed.contains(rt) || rt.endsWith("customXml");
    }
}
