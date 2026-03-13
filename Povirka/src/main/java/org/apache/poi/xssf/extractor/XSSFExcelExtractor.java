package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlException;

/* JADX INFO: loaded from: classes.dex */
public class XSSFExcelExtractor extends POIXMLTextExtractor implements ExcelExtractor {
    public static final XSSFRelation[] SUPPORTED_TYPES = {XSSFRelation.WORKBOOK, XSSFRelation.MACRO_TEMPLATE_WORKBOOK, XSSFRelation.MACRO_ADDIN_WORKBOOK, XSSFRelation.TEMPLATE_WORKBOOK, XSSFRelation.MACROS_WORKBOOK};
    private boolean formulasNotResults;
    private boolean includeCellComments;
    private boolean includeHeadersFooters;
    private boolean includeSheetNames;
    private boolean includeTextBoxes;
    private Locale locale;
    private XSSFWorkbook workbook;

    public XSSFExcelExtractor(OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XSSFWorkbook(container));
    }

    public XSSFExcelExtractor(XSSFWorkbook workbook) {
        super(workbook);
        this.includeSheetNames = true;
        this.includeHeadersFooters = true;
        this.includeTextBoxes = true;
        this.workbook = workbook;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSSFExcelExtractor <filename.xlsx>");
            System.exit(1);
        }
        OPCPackage pkg = OPCPackage.create(args[0]);
        POIXMLTextExtractor extractor = new XSSFExcelExtractor(pkg);
        try {
            System.out.println(extractor.getText());
        } finally {
            extractor.close();
        }
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeSheetNames(boolean includeSheetNames) {
        this.includeSheetNames = includeSheetNames;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setFormulasNotResults(boolean formulasNotResults) {
        this.formulasNotResults = formulasNotResults;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeCellComments(boolean includeCellComments) {
        this.includeCellComments = includeCellComments;
    }

    @Override // org.apache.poi.ss.extractor.ExcelExtractor
    public void setIncludeHeadersFooters(boolean includeHeadersFooters) {
        this.includeHeadersFooters = includeHeadersFooters;
    }

    public void setIncludeTextBoxes(boolean includeTextBoxes) {
        this.includeTextBoxes = includeTextBoxes;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override // org.apache.poi.POITextExtractor
    public String getText() {
        DataFormatter formatter;
        XSSFDrawing drawing;
        if (this.locale == null) {
            formatter = new DataFormatter();
        } else {
            formatter = new DataFormatter(this.locale);
        }
        StringBuffer text = new StringBuffer();
        for (Sheet sh : this.workbook) {
            XSSFSheet sheet = (XSSFSheet) sh;
            if (this.includeSheetNames) {
                text.append(sheet.getSheetName()).append("\n");
            }
            if (this.includeHeadersFooters) {
                text.append(extractHeaderFooter(sheet.getFirstHeader()));
                text.append(extractHeaderFooter(sheet.getOddHeader()));
                text.append(extractHeaderFooter(sheet.getEvenHeader()));
            }
            Iterator<Row> it = sheet.iterator();
            while (true) {
                char c = '\n';
                if (!it.hasNext()) {
                    break;
                }
                Object rawR = it.next();
                Row row = (Row) rawR;
                Iterator<Cell> ri = row.cellIterator();
                while (ri.hasNext()) {
                    Cell cell = ri.next();
                    if (cell.getCellTypeEnum() == CellType.FORMULA) {
                        if (this.formulasNotResults) {
                            String contents = cell.getCellFormula();
                            checkMaxTextSize(text, contents);
                            text.append(contents);
                        } else if (cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
                            handleStringCell(text, cell);
                        } else {
                            handleNonStringCell(text, cell, formatter);
                        }
                    } else if (cell.getCellTypeEnum() == CellType.STRING) {
                        handleStringCell(text, cell);
                    } else {
                        handleNonStringCell(text, cell, formatter);
                    }
                    Comment comment = cell.getCellComment();
                    if (this.includeCellComments && comment != null) {
                        String commentText = comment.getString().getString().replace(c, ' ');
                        checkMaxTextSize(text, commentText);
                        text.append(" Comment by ").append(comment.getAuthor()).append(": ").append(commentText);
                    }
                    if (ri.hasNext()) {
                        text.append("\t");
                    }
                    c = '\n';
                }
                text.append("\n");
            }
            if (this.includeTextBoxes && (drawing = sheet.getDrawingPatriarch()) != null) {
                for (XSSFShape shape : drawing.getShapes()) {
                    if (shape instanceof XSSFSimpleShape) {
                        String boxText = ((XSSFSimpleShape) shape).getText();
                        if (boxText.length() > 0) {
                            text.append(boxText);
                            text.append('\n');
                        }
                    }
                }
            }
            if (this.includeHeadersFooters) {
                text.append(extractHeaderFooter(sheet.getFirstFooter()));
                text.append(extractHeaderFooter(sheet.getOddFooter()));
                text.append(extractHeaderFooter(sheet.getEvenFooter()));
            }
        }
        return text.toString();
    }

    private void handleStringCell(StringBuffer text, Cell cell) {
        String contents = cell.getRichStringCellValue().getString();
        checkMaxTextSize(text, contents);
        text.append(contents);
    }

    private void handleNonStringCell(StringBuffer text, Cell cell, DataFormatter formatter) {
        CellStyle cs;
        CellType type = cell.getCellTypeEnum();
        if (type == CellType.FORMULA) {
            type = cell.getCachedFormulaResultTypeEnum();
        }
        if (type == CellType.NUMERIC && (cs = cell.getCellStyle()) != null && cs.getDataFormatString() != null) {
            String contents = formatter.formatRawCellContents(cell.getNumericCellValue(), cs.getDataFormat(), cs.getDataFormatString());
            checkMaxTextSize(text, contents);
            text.append(contents);
        } else {
            String contents2 = ((XSSFCell) cell).getRawValue();
            if (contents2 != null) {
                checkMaxTextSize(text, contents2);
                text.append(contents2);
            }
        }
    }

    private String extractHeaderFooter(HeaderFooter hf) {
        return org.apache.poi.hssf.extractor.ExcelExtractor._extractHeaderFooter(hf);
    }
}
