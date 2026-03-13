package org.apache.poi.xssf.binary;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;

/* JADX INFO: loaded from: classes.dex */
@Internal
public class XSSFBRelation extends POIXMLRelation {
    private static final POILogger log = POILogFactory.getLogger((Class<?>) XSSFBRelation.class);
    static final XSSFBRelation SHARED_STRINGS_BINARY = new XSSFBRelation("application/vnd.ms-excel.sharedStrings", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings", "/xl/sharedStrings.bin", null);
    public static final XSSFBRelation STYLES_BINARY = new XSSFBRelation("application/vnd.ms-excel.styles", PackageRelationshipTypes.STYLE_PART, "/xl/styles.bin", null);

    private XSSFBRelation(String type, String rel, String defaultName, Class<? extends POIXMLDocumentPart> cls) {
        super(type, rel, defaultName, cls);
    }
}
