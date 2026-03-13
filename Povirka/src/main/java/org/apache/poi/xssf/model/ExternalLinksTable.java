package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.ss.usermodel.Name;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalDefinedName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalSheetName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ExternalLinkDocument;

/* JADX INFO: loaded from: classes.dex */
public class ExternalLinksTable extends POIXMLDocumentPart {
    private CTExternalLink link;

    public ExternalLinksTable() {
        CTExternalLink cTExternalLinkNewInstance = CTExternalLink.Factory.newInstance();
        this.link = cTExternalLinkNewInstance;
        cTExternalLinkNewInstance.addNewExternalBook();
    }

    public ExternalLinksTable(PackagePart part) throws IOException {
        super(part);
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            ExternalLinkDocument doc = ExternalLinkDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.link = doc.getExternalLink();
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        ExternalLinkDocument doc = ExternalLinkDocument.Factory.newInstance();
        doc.setExternalLink(this.link);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }

    public CTExternalLink getCTExternalLink() {
        return this.link;
    }

    public String getLinkedFileName() {
        String rId = this.link.getExternalBook().getId();
        PackageRelationship rel = getPackagePart().getRelationship(rId);
        if (rel != null && rel.getTargetMode() == TargetMode.EXTERNAL) {
            return rel.getTargetURI().toString();
        }
        return null;
    }

    public void setLinkedFileName(String target) {
        String rId = this.link.getExternalBook().getId();
        if (rId != null && !rId.isEmpty()) {
            getPackagePart().removeRelationship(rId);
        }
        PackageRelationship newRel = getPackagePart().addExternalRelationship(target, PackageRelationshipTypes.EXTERNAL_LINK_PATH);
        this.link.getExternalBook().setId(newRel.getId());
    }

    public List<String> getSheetNames() {
        CTExternalSheetName[] sheetNames = this.link.getExternalBook().getSheetNames().getSheetNameArray();
        List<String> names = new ArrayList<>(sheetNames.length);
        for (CTExternalSheetName name : sheetNames) {
            names.add(name.getVal());
        }
        return names;
    }

    public List<Name> getDefinedNames() {
        CTExternalDefinedName[] extNames = this.link.getExternalBook().getDefinedNames().getDefinedNameArray();
        List<Name> names = new ArrayList<>(extNames.length);
        for (CTExternalDefinedName extName : extNames) {
            names.add(new ExternalName(extName));
        }
        return names;
    }

    protected class ExternalName implements Name {
        private CTExternalDefinedName name;

        protected ExternalName(CTExternalDefinedName name) {
            this.name = name;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public String getNameName() {
            return this.name.getName();
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public void setNameName(String name) {
            this.name.setName(name);
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public String getSheetName() {
            int sheetId = getSheetIndex();
            if (sheetId >= 0) {
                return ExternalLinksTable.this.getSheetNames().get(sheetId);
            }
            return null;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public int getSheetIndex() {
            if (this.name.isSetSheetId()) {
                return (int) this.name.getSheetId();
            }
            return -1;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public void setSheetIndex(int sheetId) {
            this.name.setSheetId(sheetId);
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public String getRefersToFormula() {
            return this.name.getRefersTo().substring(1);
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public void setRefersToFormula(String formulaText) {
            this.name.setRefersTo('=' + formulaText);
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public boolean isFunctionName() {
            return false;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public boolean isDeleted() {
            return false;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public String getComment() {
            return null;
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public void setComment(String comment) {
            throw new IllegalStateException("Not Supported");
        }

        @Override // org.apache.poi.ss.usermodel.Name
        public void setFunction(boolean value) {
            throw new IllegalStateException("Not Supported");
        }
    }
}
