package org.apache.poi.poifs.crypt.dsig.facets;

import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureException;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: classes.dex */
public class EnvelopedSignatureFacet extends SignatureFacet {
    @Override // org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet
    public void preSign(Document document, List<Reference> references, List<XMLObject> objects) throws XMLSignatureException {
        List<Transform> transforms = new ArrayList<>();
        Transform envelopedTransform = newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.add(envelopedTransform);
        Transform exclusiveTransform = newTransform("http://www.w3.org/2001/10/xml-exc-c14n#");
        transforms.add(exclusiveTransform);
        Reference reference = newReference("", transforms, null, null, null);
        references.add(reference);
    }
}
