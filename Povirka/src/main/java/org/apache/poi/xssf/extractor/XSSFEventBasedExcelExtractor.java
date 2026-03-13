package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.xmlbeans.XmlException;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* JADX INFO: loaded from: classes.dex */
public class XSSFEventBasedExcelExtractor extends POIXMLTextExtractor implements ExcelExtractor {
    private static final POILogger LOGGER = POILogFactory.getLogger((Class<?>) XSSFEventBasedExcelExtractor.class);
    private boolean concatenatePhoneticRuns;
    private OPCPackage container;
    private boolean formulasNotResults;
    private boolean includeCellComments;
    private boolean includeHeadersFooters;
    private boolean includeSheetNames;
    private boolean includeTextBoxes;
    private Locale locale;
    private POIXMLProperties properties;

    public XSSFEventBasedExcelExtractor(String path) throws XmlException, OpenXML4JException, IOException {
        this(OPCPackage.open(path));
    }

    public XSSFEventBasedExcelExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        super(null);
        this.includeTextBoxes = true;
        this.includeSheetNames = true;
        this.includeCellComments = false;
        this.includeHeadersFooters = true;
        this.formulasNotResults = false;
        this.concatenatePhoneticRuns = true;
        this.container = container;
        this.properties = new POIXMLProperties(container);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFEventBasedExcelExtractor <filename.xlsx>");
            System.exit(1);
        }
        POIXMLTextExtractor extractor = new XSSFEventBasedExcelExtractor(args[0]);
        System.out.println(extractor.getText());
        extractor.close();
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeSheetNames(boolean includeSheetNames) {
        this.includeSheetNames = includeSheetNames;
    }

    public boolean getIncludeSheetNames() {
        return this.includeSheetNames;
    }

    public void setFormulasNotResults(boolean formulasNotResults) {
        this.formulasNotResults = formulasNotResults;
    }

    public boolean getFormulasNotResults() {
        return this.formulasNotResults;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
        this.includeHeadersFooters = includeHeadersFooters;
    }

    public boolean getIncludeHeadersFooters() {
        return this.includeHeadersFooters;
    }

    public void setIncludeTextBoxes(boolean includeTextBoxes) {
        this.includeTextBoxes = includeTextBoxes;
    }

    public boolean getIncludeTextBoxes() {
        return this.includeTextBoxes;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeCellComments(boolean includeCellComments) {
        this.includeCellComments = includeCellComments;
    }

    public boolean getIncludeCellComments() {
        return this.includeCellComments;
    }

    public void setConcatenatePhoneticRuns(boolean concatenatePhoneticRuns) {
        this.concatenatePhoneticRuns = concatenatePhoneticRuns;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    @Override // org.apache.poi.POIXMLTextExtractor
    public OPCPackage getPackage() {
        return this.container;
    }

    @Override // org.apache.poi.POIXMLTextExtractor
    public POIXMLProperties.CoreProperties getCoreProperties() {
        return this.properties.getCoreProperties();
    }

    @Override // org.apache.poi.POIXMLTextExtractor
    public POIXMLProperties.ExtendedProperties getExtendedProperties() {
        return this.properties.getExtendedProperties();
    }

    @Override // org.apache.poi.POIXMLTextExtractor
    public POIXMLProperties.CustomProperties getCustomProperties() {
        return this.properties.getCustomProperties();
    }

    public void processSheet(XSSFSheetXMLHandler.SheetContentsHandler sheetContentsExtractor, StylesTable styles, CommentsTable comments, ReadOnlySharedStringsTable strings, InputStream sheetInputStream) throws SAXException, IOException {
        DataFormatter formatter;
        if (this.locale == null) {
            formatter = new DataFormatter();
        } else {
            formatter = new DataFormatter(this.locale);
        }
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, comments, strings, sheetContentsExtractor, formatter, this.formulasNotResults);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        try {
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.container, this.concatenatePhoneticRuns);
            XSSFReader xssfReader = new XSSFReader(this.container);
            StylesTable styles = xssfReader.getStylesTable();
            XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            StringBuffer text = new StringBuffer();
            SheetTextExtractor sheetExtractor = new SheetTextExtractor();
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                if (this.includeSheetNames) {
                    text.append(iter.getSheetName());
                    text.append('\n');
                }
                CommentsTable comments = this.includeCellComments ? iter.getSheetComments() : null;
                processSheet(sheetExtractor, styles, comments, strings, stream);
                if (this.includeHeadersFooters) {
                    sheetExtractor.appendHeaderText(text);
                }
                sheetExtractor.appendCellText(text);
                if (this.includeTextBoxes) {
                    processShapes(iter.getShapes(), text);
                }
                if (this.includeHeadersFooters) {
                    sheetExtractor.appendFooterText(text);
                }
                sheetExtractor.reset();
                stream.close();
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

    void processShapes(List<XSSFShape> shapes, StringBuffer text) {
        String sText;
        if (shapes == null) {
            return;
        }
        for (XSSFShape shape : shapes) {
            if ((shape instanceof XSSFSimpleShape) && (sText = ((XSSFSimpleShape) shape).getText()) != null && sText.length() > 0) {
                text.append(sText).append('\n');
            }
        }
    }

    @Override // org.apache.poi.POIXMLTextExtractor, org.apache.poi.POITextExtractor, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        OPCPackage oPCPackage = this.container;
        if (oPCPackage != null) {
            oPCPackage.close();
            this.container = null;
        }
        super.close();
    }

    protected class SheetTextExtractor implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final Map<String, String> headerFooterMap;
        private final StringBuffer output = new StringBuffer();
        private boolean firstCellOfRow = true;

        protected SheetTextExtractor() {
            this.headerFooterMap = XSSFEventBasedExcelExtractor.this.includeHeadersFooters ? new HashMap() : null;
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler
        public void startRow(int rowNum) {
            this.firstCellOfRow = true;
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler
        public void endRow(int rowNum) {
            this.output.append('\n');
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler
        public void cell(String cellRef, String formattedValue, XSSFComment comment) {
            if (this.firstCellOfRow) {
                this.firstCellOfRow = false;
            } else {
                this.output.append('\t');
            }
            if (formattedValue != null) {
                XSSFEventBasedExcelExtractor.this.checkMaxTextSize(this.output, formattedValue);
                this.output.append(formattedValue);
            }
            if (XSSFEventBasedExcelExtractor.this.includeCellComments && comment != null) {
                String commentText = comment.getString().getString().replace('\n', ' ');
                this.output.append(formattedValue != null ? " Comment by " : "Comment by ");
                XSSFEventBasedExcelExtractor.this.checkMaxTextSize(this.output, commentText);
                if (commentText.startsWith(comment.getAuthor() + ": ")) {
                    this.output.append(commentText);
                } else {
                    this.output.append(comment.getAuthor()).append(": ").append(commentText);
                }
            }
        }

        @Override // org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler
        public void headerFooter(String text, boolean isHeader, String tagName) {
            Map<String, String> map = this.headerFooterMap;
            if (map != null) {
                map.put(tagName, text);
            }
        }

        private void appendHeaderFooterText(StringBuffer buffer, String name) {
            String text = this.headerFooterMap.get(name);
            if (text != null && text.length() > 0) {
                buffer.append(handleHeaderFooterDelimiter(handleHeaderFooterDelimiter(handleHeaderFooterDelimiter(text, "&L"), "&C"), "&R")).append('\n');
            }
        }

        private String handleHeaderFooterDelimiter(String text, String delimiter) {
            int index = text.indexOf(delimiter);
            if (index == 0) {
                return text.substring(2);
            }
            if (index > 0) {
                return text.substring(0, index) + "\t" + text.substring(index + 2);
            }
            return text;
        }

        void appendHeaderText(StringBuffer buffer) {
            appendHeaderFooterText(buffer, "firstHeader");
            appendHeaderFooterText(buffer, "oddHeader");
            appendHeaderFooterText(buffer, "evenHeader");
        }

        void appendFooterText(StringBuffer buffer) {
            appendHeaderFooterText(buffer, "firstFooter");
            appendHeaderFooterText(buffer, "oddFooter");
            appendHeaderFooterText(buffer, "evenFooter");
        }

        void appendCellText(StringBuffer buffer) {
            XSSFEventBasedExcelExtractor.this.checkMaxTextSize(buffer, this.output.toString());
            buffer.append(this.output);
        }

        void reset() {
            this.output.setLength(0);
            this.firstCellOfRow = true;
            Map<String, String> map = this.headerFooterMap;
            if (map != null) {
                map.clear();
            }
        }
    }
}
