package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.binary.XSSFBHyperlinksTable;
import org.apache.poi.xssf.binary.XSSFBSharedStringsTable;
import org.apache.poi.xssf.binary.XSSFBSheetHandler;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.eventusermodel.XSSFBReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class XSSFBEventBasedExcelExtractor extends XSSFEventBasedExcelExtractor implements ExcelExtractor {
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) XSSFBEventBasedExcelExtractor.class);
    public static final XSSFRelation[] SUPPORTED_TYPES = {XSSFRelation.XLSB_BINARY_WORKBOOK};
    private boolean handleHyperlinksInCells;

    public XSSFBEventBasedExcelExtractor(String path) throws XmlException, OpenXML4JException, IOException {
        super(path);
        this.handleHyperlinksInCells = false;
    }

    public XSSFBEventBasedExcelExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        super(container);
        this.handleHyperlinksInCells = false;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFBEventBasedExcelExtractor <filename.xlsb>");
            System.exit(1);
        }
        POIXMLTextExtractor extractor = new XSSFBEventBasedExcelExtractor(args[0]);
        System.out.println(extractor.getText());
        extractor.close();
    }

    public void setHandleHyperlinksInCells(boolean handleHyperlinksInCells) {
        this.handleHyperlinksInCells = handleHyperlinksInCells;
    }

    @Override // org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor, org.apache.poi.ss.extractor.ExcelExtractor
    public void setFormulasNotResults(boolean formulasNotResults) {
        throw new IllegalArgumentException("Not currently supported");
    }

    public void processSheet(XSSFSheetXMLHandler.SheetContentsHandler sheetContentsExtractor, XSSFBStylesTable styles, XSSFBCommentsTable comments, XSSFBSharedStringsTable strings, InputStream sheetInputStream) throws SAXException, IOException {
        DataFormatter formatter;
        if (getLocale() == null) {
            formatter = new DataFormatter();
        } else {
            formatter = new DataFormatter(getLocale());
        }
        XSSFBSheetHandler xssfbSheetHandler = new XSSFBSheetHandler(sheetInputStream, styles, comments, strings, sheetContentsExtractor, formatter, getFormulasNotResults());
        xssfbSheetHandler.parse();
    }

    @Override // org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor, org.apache.poi.POITextExtractor
    public String getText() {
        XSSFBHyperlinksTable hyperlinksTable;
        try {
            XSSFBSharedStringsTable strings = new XSSFBSharedStringsTable(getPackage());
            XSSFBReader xssfbReader = new XSSFBReader(getPackage());
            XSSFBStylesTable styles = xssfbReader.getXSSFBStylesTable();
            XSSFBReader.SheetIterator iter = (XSSFBReader.SheetIterator) xssfbReader.getSheetsData();
            StringBuffer text = new StringBuffer();
            XSSFEventBasedExcelExtractor.SheetTextExtractor sheetExtractor = new XSSFEventBasedExcelExtractor.SheetTextExtractor();
            XSSFBHyperlinksTable hyperlinksTable2 = null;
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                if (getIncludeSheetNames()) {
                    text.append(iter.getSheetName());
                    text.append('\n');
                }
                if (!this.handleHyperlinksInCells) {
                    hyperlinksTable = hyperlinksTable2;
                } else {
                    XSSFBHyperlinksTable hyperlinksTable3 = new XSSFBHyperlinksTable(iter.getSheetPart());
                    hyperlinksTable = hyperlinksTable3;
                }
                XSSFBCommentsTable comments = getIncludeCellComments() ? iter.getXSSFBSheetComments() : null;
                processSheet(sheetExtractor, styles, comments, strings, stream);
                if (getIncludeHeadersFooters()) {
                    sheetExtractor.appendHeaderText(text);
                }
                sheetExtractor.appendCellText(text);
                if (getIncludeTextBoxes()) {
                    processShapes(iter.getShapes(), text);
                }
                if (getIncludeHeadersFooters()) {
                    sheetExtractor.appendFooterText(text);
                }
                sheetExtractor.reset();
                stream.close();
                hyperlinksTable2 = hyperlinksTable;
            }
            return text.toString();
        } catch (IOException e) {
            LOGGER.log(5, e);
            return null;
        } catch (OpenXML4JException o4je) {
            LOGGER.log(5, o4je);
            return null;
        } catch (SAXException se) {
            LOGGER.log(5, se);
            return null;
        }
    }
}
