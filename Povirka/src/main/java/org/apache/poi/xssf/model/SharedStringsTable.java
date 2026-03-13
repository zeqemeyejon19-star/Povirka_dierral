package org.apache.poi.xssf.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SstDocument;

/* JADX INFO: loaded from: classes.dex */
public class SharedStringsTable extends POIXMLDocumentPart {
    private static final XmlOptions options;
    private SstDocument _sstDoc;
    private int count;
    private final Map<String, Integer> stmap;
    private final List<CTRst> strings;
    private int uniqueCount;

    static {
        XmlOptions xmlOptions = new XmlOptions();
        options = xmlOptions;
        xmlOptions.put("SAVE_INNER");
        xmlOptions.put("SAVE_AGGRESSIVE_NAMESPACES");
        xmlOptions.put("SAVE_USE_DEFAULT_NAMESPACE");
        xmlOptions.setSaveImplicitNamespaces(Collections.singletonMap("", XSSFRelation.NS_SPREADSHEETML));
    }

    public SharedStringsTable() {
        this.strings = new ArrayList();
        this.stmap = new HashMap();
        SstDocument sstDocumentNewInstance = SstDocument.Factory.newInstance();
        this._sstDoc = sstDocumentNewInstance;
        sstDocumentNewInstance.addNewSst();
    }

    public SharedStringsTable(PackagePart part) throws IOException {
        super(part);
        this.strings = new ArrayList();
        this.stmap = new HashMap();
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        int cnt = 0;
        try {
            SstDocument sstDocument = SstDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this._sstDoc = sstDocument;
            CTSst sst = sstDocument.getSst();
            this.count = (int) sst.getCount();
            this.uniqueCount = (int) sst.getUniqueCount();
            CTRst[] arr$ = sst.getSiArray();
            for (CTRst st : arr$) {
                this.stmap.put(getKey(st), Integer.valueOf(cnt));
                this.strings.add(st);
                cnt++;
            }
        } catch (XmlException e) {
            throw new IOException("unable to parse shared strings table", e);
        }
    }

    private String getKey(CTRst st) {
        return st.xmlText(options);
    }

    public CTRst getEntryAt(int idx) {
        return this.strings.get(idx);
    }

    public int getCount() {
        return this.count;
    }

    public int getUniqueCount() {
        return this.uniqueCount;
    }

    public int addEntry(CTRst st) {
        String s = getKey(st);
        this.count++;
        if (this.stmap.containsKey(s)) {
            return this.stmap.get(s).intValue();
        }
        this.uniqueCount++;
        CTRst newSt = this._sstDoc.getSst().addNewSi();
        newSt.set(st);
        int idx = this.strings.size();
        this.stmap.put(s, Integer.valueOf(idx));
        this.strings.add(newSt);
        return idx;
    }

    public List<CTRst> getItems() {
        return Collections.unmodifiableList(this.strings);
    }

    public void writeTo(OutputStream out) throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveCDataLengthThreshold(1000000);
        xmlOptions.setSaveCDataEntityCountThreshold(-1);
        CTSst sst = this._sstDoc.getSst();
        sst.setCount(this.count);
        sst.setUniqueCount(this.uniqueCount);
        this._sstDoc.save(out, xmlOptions);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }
}
