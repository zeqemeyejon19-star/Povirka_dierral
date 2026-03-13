package org.apache.poi.hpsf.extractor;

import java.io.File;
import java.io.IOException;
import org.apache.poi.POIDocument;
import org.apache.poi.POIOLE2TextExtractor;
import org.apache.poi.POITextExtractor;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.HPSFPropertiesOnlyDocument;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.wellknown.PropertyIDMap;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/* JADX INFO: loaded from: classes.dex */
public class HPSFPropertiesExtractor extends POIOLE2TextExtractor {
    public HPSFPropertiesExtractor(POIOLE2TextExtractor mainExtractor) {
        super(mainExtractor);
    }

    public HPSFPropertiesExtractor(POIDocument doc) {
        super(doc);
    }

    public HPSFPropertiesExtractor(POIFSFileSystem fs) {
        super(new HPSFPropertiesOnlyDocument(fs));
    }

    public HPSFPropertiesExtractor(NPOIFSFileSystem fs) {
        super(new HPSFPropertiesOnlyDocument(fs));
    }

    public String getDocumentSummaryInformationText() {
        if (this.document == null) {
            return "";
        }
        DocumentSummaryInformation dsi = this.document.getDocumentSummaryInformation();
        StringBuilder text = new StringBuilder();
        text.append(getPropertiesText(dsi));
        CustomProperties cps = dsi == null ? null : dsi.getCustomProperties();
        if (cps != null) {
            for (String key : cps.nameSet()) {
                String val = getPropertyValueText(cps.get(key));
                text.append(key).append(" = ").append(val).append("\n");
            }
        }
        return text.toString();
    }

    public String getSummaryInformationText() {
        if (this.document == null) {
            return "";
        }
        SummaryInformation si = this.document.getSummaryInformation();
        return getPropertiesText(si);
    }

    private static String getPropertiesText(PropertySet ps) {
        if (ps == null) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        PropertyIDMap idMap = ps.getPropertySetIDMap();
        Property[] props = ps.getProperties();
        for (Property prop : props) {
            String type = Long.toString(prop.getID());
            Object typeObj = idMap == null ? null : idMap.get((Object) Long.valueOf(prop.getID()));
            if (typeObj != null) {
                type = typeObj.toString();
            }
            String val = getPropertyValueText(prop.getValue());
            text.append(type).append(" = ").append(val).append("\n");
        }
        return text.toString();
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        return getSummaryInformationText() + getDocumentSummaryInformationText();
    }

    @Override // org.apache.poi.POIOLE2TextExtractor, org.apache.poi.POITextExtractor
    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }

    private static String getPropertyValueText(Object val) {
        return val == null ? "(not set)" : PropertySet.getPropertyStringValue(val);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static void main(String[] args) throws IOException {
        for (String file : args) {
            HPSFPropertiesExtractor ext = new HPSFPropertiesExtractor(new NPOIFSFileSystem(new File(file)));
            try {
                System.out.println(ext.getText());
                ext.close();
            } catch (Throwable th) {
                ext.close();
                throw th;
            }
        }
    }
}
