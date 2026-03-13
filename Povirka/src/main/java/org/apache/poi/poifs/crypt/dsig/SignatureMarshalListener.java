package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;

/* JADX INFO: loaded from: classes.dex */
public class SignatureMarshalListener implements EventListener, SignatureConfig.SignatureConfigurable {
    SignatureConfig signatureConfig;
    ThreadLocal<EventTarget> target = new ThreadLocal<>();

    public void setEventTarget(EventTarget target) {
        this.target.set(target);
    }

    public void handleEvent(Event e) {
        if (!(e instanceof MutationEvent)) {
            return;
        }
        MutationEvent mutEvt = (MutationEvent) e;
        EventTarget et = mutEvt.getTarget();
        if (!(et instanceof Element)) {
            return;
        }
        handleElement((Element) et);
    }

    public void handleElement(Element el) {
        EventTarget target = this.target.get();
        if (el.hasAttribute(PackageRelationship.ID_ATTRIBUTE_NAME)) {
            el.setIdAttribute(PackageRelationship.ID_ATTRIBUTE_NAME, true);
        }
        setListener(target, this, false);
        if ("http://schemas.openxmlformats.org/package/2006/digital-signature".equals(el.getNamespaceURI())) {
            String parentNS = el.getParentNode().getNamespaceURI();
            if (!"http://schemas.openxmlformats.org/package/2006/digital-signature".equals(parentNS) && !el.hasAttributeNS(SignatureFacet.XML_NS, "mdssi")) {
                el.setAttributeNS(SignatureFacet.XML_NS, "xmlns:mdssi", "http://schemas.openxmlformats.org/package/2006/digital-signature");
            }
        }
        setPrefix(el);
        setListener(target, this, true);
    }

    public static void setListener(EventTarget target, EventListener listener, boolean enabled) {
        if (enabled) {
            target.addEventListener("DOMSubtreeModified", listener, false);
        } else {
            target.removeEventListener("DOMSubtreeModified", listener, false);
        }
    }

    protected void setPrefix(Node el) {
        String prefix = this.signatureConfig.getNamespacePrefixes().get(el.getNamespaceURI());
        if (prefix != null && el.getPrefix() == null) {
            el.setPrefix(prefix);
        }
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            setPrefix(nl.item(i));
        }
    }

    @Override // org.apache.poi.poifs.crypt.dsig.SignatureConfig.SignatureConfigurable
    public void setSignatureConfig(SignatureConfig signatureConfig) {
        this.signatureConfig = signatureConfig;
    }
}
