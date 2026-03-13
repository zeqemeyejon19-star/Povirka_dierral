package org.apache.poi.xssf.usermodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ChartsheetDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSSFChartSheet extends XSSFSheet {
    private static final byte[] BLANK_WORKSHEET = blankWorksheet();
    protected CTChartsheet chartsheet;

    protected XSSFChartSheet(PackagePart part) {
        super(part);
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFSheet
    protected void read(InputStream is) throws IOException {
        super.read(new ByteArrayInputStream(BLANK_WORKSHEET));
        try {
            this.chartsheet = ChartsheetDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getChartsheet();
        } catch (XmlException e) {
            throw new POIXMLException((Throwable) e);
        }
    }

    public CTChartsheet getCTChartsheet() {
        return this.chartsheet;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFSheet
    protected CTDrawing getCTDrawing() {
        return this.chartsheet.getDrawing();
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFSheet
    protected CTLegacyDrawing getCTLegacyDrawing() {
        return this.chartsheet.getLegacyDrawing();
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFSheet
    protected void write(OutputStream out) throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartsheet.type.getName().getNamespaceURI(), "chartsheet"));
        this.chartsheet.save(out, xmlOptions);
    }

    private static byte[] blankWorksheet() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new XSSFSheet().write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
