package org.apache.poi.xssf.eventusermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* JADX INFO: loaded from: classes.dex */
public class ReadOnlySharedStringsTable extends DefaultHandler {
    private StringBuffer characters;
    private int count;
    private boolean inRPh;
    private final boolean includePhoneticRuns;
    private Map<Integer, String> phoneticStrings;
    private List<String> strings;
    private boolean tIsOpen;
    private int uniqueCount;

    public ReadOnlySharedStringsTable(OPCPackage pkg) throws SAXException, IOException {
        this(pkg, true);
    }

    public ReadOnlySharedStringsTable(OPCPackage pkg, boolean includePhoneticRuns) throws SAXException, IOException {
        this.includePhoneticRuns = includePhoneticRuns;
        ArrayList<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        if (parts.size() > 0) {
            PackagePart sstPart = parts.get(0);
            readFrom(sstPart.getInputStream());
        }
    }

    public ReadOnlySharedStringsTable(PackagePart part) throws SAXException, IOException {
        this(part, true);
    }

    public ReadOnlySharedStringsTable(PackagePart part, boolean includePhoneticRuns) throws SAXException, IOException {
        this.includePhoneticRuns = includePhoneticRuns;
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws SAXException, IOException {
        PushbackInputStream pis = new PushbackInputStream(is, 1);
        int emptyTest = pis.read();
        if (emptyTest > -1) {
            pis.unread(emptyTest);
            InputSource sheetSource = new InputSource(pis);
            try {
                XMLReader sheetParser = SAXHelper.newXMLReader();
                sheetParser.setContentHandler(this);
                sheetParser.parse(sheetSource);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
            }
        }
    }

    public int getCount() {
        return this.count;
    }

    public int getUniqueCount() {
        return this.uniqueCount;
    }

    public String getEntryAt(int idx) {
        return this.strings.get(idx);
    }

    public List<String> getItems() {
        return this.strings;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        if (uri != null && !uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
            return;
        }
        if ("sst".equals(localName)) {
            String count = attributes.getValue("count");
            if (count != null) {
                this.count = Integer.parseInt(count);
            }
            String uniqueCount = attributes.getValue("uniqueCount");
            if (uniqueCount != null) {
                this.uniqueCount = Integer.parseInt(uniqueCount);
            }
            this.strings = new ArrayList(this.uniqueCount);
            this.phoneticStrings = new HashMap();
            this.characters = new StringBuffer();
            return;
        }
        if ("si".equals(localName)) {
            this.characters.setLength(0);
            return;
        }
        if ("t".equals(localName)) {
            this.tIsOpen = true;
            return;
        }
        if ("rPh".equals(localName)) {
            this.inRPh = true;
            if (this.includePhoneticRuns && this.characters.length() > 0) {
                this.characters.append(" ");
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (uri != null && !uri.equals(XSSFRelation.NS_SPREADSHEETML)) {
            return;
        }
        if ("si".equals(localName)) {
            this.strings.add(this.characters.toString());
        } else if ("t".equals(localName)) {
            this.tIsOpen = false;
        } else if ("rPh".equals(localName)) {
            this.inRPh = false;
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.tIsOpen) {
            boolean z = this.inRPh;
            if (z && this.includePhoneticRuns) {
                this.characters.append(ch, start, length);
            } else if (!z) {
                this.characters.append(ch, start, length);
            }
        }
    }
}
