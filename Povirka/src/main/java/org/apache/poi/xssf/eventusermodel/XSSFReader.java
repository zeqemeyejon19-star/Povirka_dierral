package org.apache.poi.xssf.eventusermodel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.model.ThemesTable;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: classes.dex */
public class XSSFReader {
    protected OPCPackage pkg;
    protected PackagePart workbookPart;
    private static final Set<String> WORKSHEET_RELS = Collections.unmodifiableSet(new HashSet(Arrays.asList(XSSFRelation.WORKSHEET.getRelation(), XSSFRelation.CHARTSHEET.getRelation())));
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) XSSFReader.class);

    public XSSFReader(OPCPackage pkg) throws OpenXML4JException, IOException {
        this.pkg = pkg;
        PackageRelationship coreDocRelationship = pkg.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument").getRelationship(0);
        if (coreDocRelationship == null) {
            if (this.pkg.getRelationshipsByType(PackageRelationshipTypes.STRICT_CORE_DOCUMENT).getRelationship(0) != null) {
                throw new POIXMLException("Strict OOXML isn't currently supported, please see bug #57699");
            }
            throw new POIXMLException("OOXML file structure broken/invalid - no core document found!");
        }
        this.workbookPart = this.pkg.getPart(coreDocRelationship);
    }

    public SharedStringsTable getSharedStringsTable() throws InvalidFormatException, IOException {
        ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        if (parts.size() == 0) {
            return null;
        }
        return new SharedStringsTable(parts.get(0));
    }

    public StylesTable getStylesTable() throws InvalidFormatException, IOException {
        ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFRelation.STYLES.getContentType());
        if (parts.size() == 0) {
            return null;
        }
        StylesTable styles = new StylesTable(parts.get(0));
        ArrayList<PackagePart> parts2 = this.pkg.getPartsByContentType(XSSFRelation.THEME.getContentType());
        if (parts2.size() != 0) {
            styles.setTheme(new ThemesTable(parts2.get(0)));
        }
        return styles;
    }

    public InputStream getSharedStringsData() throws InvalidFormatException, IOException {
        return XSSFRelation.SHARED_STRINGS.getContents(this.workbookPart);
    }

    public InputStream getStylesData() throws InvalidFormatException, IOException {
        return XSSFRelation.STYLES.getContents(this.workbookPart);
    }

    public InputStream getThemesData() throws InvalidFormatException, IOException {
        return XSSFRelation.THEME.getContents(this.workbookPart);
    }

    public InputStream getWorkbookData() throws InvalidFormatException, IOException {
        return this.workbookPart.getInputStream();
    }

    public InputStream getSheet(String relId) throws InvalidFormatException, IOException {
        PackageRelationship rel = this.workbookPart.getRelationship(relId);
        if (rel == null) {
            throw new IllegalArgumentException("No Sheet found with r:id " + relId);
        }
        PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
        PackagePart sheet = this.pkg.getPart(relName);
        if (sheet == null) {
            throw new IllegalArgumentException("No data found for Sheet with r:id " + relId);
        }
        return sheet.getInputStream();
    }

    public Iterator<InputStream> getSheetsData() throws InvalidFormatException, IOException {
        return new SheetIterator(this.workbookPart);
    }

    public static class SheetIterator implements Iterator<InputStream> {
        final Iterator<XSSFSheetRef> sheetIterator;
        private final Map<String, PackagePart> sheetMap;
        XSSFSheetRef xssfSheetRef;

        SheetIterator(PackagePart wb) throws IOException {
            try {
                this.sheetMap = new HashMap();
                OPCPackage pkg = wb.getPackage();
                Set<String> worksheetRels = getSheetRelationships();
                for (PackageRelationship rel : wb.getRelationships()) {
                    String relType = rel.getRelationshipType();
                    if (worksheetRels.contains(relType)) {
                        PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
                        this.sheetMap.put(rel.getId(), pkg.getPart(relName));
                    }
                }
                this.sheetIterator = createSheetIteratorFromWB(wb);
            } catch (InvalidFormatException e) {
                throw new POIXMLException(e);
            }
        }

        Iterator<XSSFSheetRef> createSheetIteratorFromWB(PackagePart wb) throws IOException {
            XMLSheetRefReader xmlSheetRefReader = new XMLSheetRefReader();
            try {
                XMLReader xmlReader = SAXHelper.newXMLReader();
                xmlReader.setContentHandler(xmlSheetRefReader);
                try {
                    xmlReader.parse(new InputSource(wb.getInputStream()));
                    List<XSSFSheetRef> validSheets = new ArrayList<>();
                    for (XSSFSheetRef xssfSheetRef : xmlSheetRefReader.getSheetRefs()) {
                        String sheetId = xssfSheetRef.getId();
                        if (sheetId != null && sheetId.length() > 0) {
                            validSheets.add(xssfSheetRef);
                        }
                    }
                    return validSheets.iterator();
                } catch (SAXException e) {
                    throw new POIXMLException(e);
                }
            } catch (ParserConfigurationException e2) {
                throw new POIXMLException(e2);
            } catch (SAXException e3) {
                throw new POIXMLException(e3);
            }
        }

        Set<String> getSheetRelationships() {
            return XSSFReader.WORKSHEET_RELS;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.sheetIterator.hasNext();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public InputStream next() {
            XSSFSheetRef next = this.sheetIterator.next();
            this.xssfSheetRef = next;
            String sheetId = next.getId();
            try {
                PackagePart sheetPkg = this.sheetMap.get(sheetId);
                return sheetPkg.getInputStream();
            } catch (IOException e) {
                throw new POIXMLException(e);
            }
        }

        public String getSheetName() {
            return this.xssfSheetRef.getName();
        }

        public CommentsTable getSheetComments() {
            PackagePart sheetPkg = getSheetPart();
            try {
                PackageRelationshipCollection commentsList = sheetPkg.getRelationshipsByType(XSSFRelation.SHEET_COMMENTS.getRelation());
                if (commentsList.size() <= 0) {
                    return null;
                }
                PackageRelationship comments = commentsList.getRelationship(0);
                PackagePartName commentsName = PackagingURIHelper.createPartName(comments.getTargetURI());
                PackagePart commentsPart = sheetPkg.getPackage().getPart(commentsName);
                return new CommentsTable(commentsPart);
            } catch (IOException e) {
                return null;
            } catch (InvalidFormatException e2) {
                return null;
            }
        }

        public List<XSSFShape> getShapes() {
            PackagePart sheetPkg = getSheetPart();
            List<XSSFShape> shapes = new LinkedList<>();
            try {
                PackageRelationshipCollection drawingsList = sheetPkg.getRelationshipsByType(XSSFRelation.DRAWINGS.getRelation());
                for (int i = 0; i < drawingsList.size(); i++) {
                    PackageRelationship drawings = drawingsList.getRelationship(i);
                    PackagePartName drawingsName = PackagingURIHelper.createPartName(drawings.getTargetURI());
                    PackagePart drawingsPart = sheetPkg.getPackage().getPart(drawingsName);
                    if (drawingsPart == null) {
                        XSSFReader.LOGGER.log(5, "Missing drawing: " + drawingsName + ". Skipping it.");
                    } else {
                        XSSFDrawing drawing = new XSSFDrawing(drawingsPart);
                        shapes.addAll(drawing.getShapes());
                    }
                }
                return shapes;
            } catch (IOException e) {
                return null;
            } catch (InvalidFormatException e2) {
                return null;
            } catch (XmlException e3) {
                return null;
            }
        }

        public PackagePart getSheetPart() {
            String sheetId = this.xssfSheetRef.getId();
            return this.sheetMap.get(sheetId);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new IllegalStateException("Not supported");
        }
    }

    protected static final class XSSFSheetRef {
        private final String id;
        private final String name;

        public XSSFSheetRef(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }

    private static class XMLSheetRefReader extends DefaultHandler {
        private static final String ID = "id";
        private static final String NAME = "name";
        private static final String SHEET = "sheet";
        private final List<XSSFSheetRef> sheetRefs;

        private XMLSheetRefReader() {
            this.sheetRefs = new LinkedList();
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
            if (localName.equalsIgnoreCase(SHEET)) {
                String name = null;
                String id = null;
                for (int i = 0; i < attrs.getLength(); i++) {
                    String attrName = attrs.getLocalName(i);
                    if (attrName.equalsIgnoreCase(NAME)) {
                        name = attrs.getValue(i);
                    } else if (attrName.equalsIgnoreCase(ID)) {
                        id = attrs.getValue(i);
                    }
                    if (name != null && id != null) {
                        this.sheetRefs.add(new XSSFSheetRef(id, name));
                        return;
                    }
                }
            }
        }

        List<XSSFSheetRef> getSheetRefs() {
            return Collections.unmodifiableList(this.sheetRefs);
        }
    }
}
