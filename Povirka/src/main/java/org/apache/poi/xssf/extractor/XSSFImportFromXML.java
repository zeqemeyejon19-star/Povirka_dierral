package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.poi.openxml4j.opc.ContentTypes;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXmlDataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class XSSFImportFromXML {
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) XSSFImportFromXML.class);
    private final XSSFMap _map;

    public XSSFImportFromXML(XSSFMap map) {
        this._map = map;
    }

    public void importFromXML(String xmlInputString) throws XPathExpressionException, SAXException, IOException {
        Document doc;
        XPath xpath;
        Iterator<XSSFSingleXmlCell> it;
        DocumentBuilder builder = DocumentHelper.newDocumentBuilder();
        Document doc2 = builder.parse(new InputSource(new StringReader(xmlInputString.trim())));
        List<XSSFSingleXmlCell> singleXmlCells = this._map.getRelatedSingleXMLCell();
        List<XSSFTable> tables = this._map.getRelatedTables();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath2 = xpathFactory.newXPath();
        xpath2.setNamespaceContext(new DefaultNamespaceContext(doc2));
        Iterator<XSSFSingleXmlCell> it2 = singleXmlCells.iterator();
        while (true) {
            DocumentBuilder builder2 = builder;
            if (!it2.hasNext()) {
                break;
            }
            XSSFSingleXmlCell singleXmlCell = it2.next();
            STXmlDataType.Enum xmlDataType = singleXmlCell.getXmlDataType();
            List<XSSFSingleXmlCell> singleXmlCells2 = singleXmlCells;
            String xpathString = singleXmlCell.getXpath();
            XPathFactory xpathFactory2 = xpathFactory;
            Node result = (Node) xpath2.evaluate(xpathString, doc2, XPathConstants.NODE);
            if (result == null) {
                doc = doc2;
                xpath = xpath2;
                it = it2;
            } else {
                it = it2;
                String textContent = result.getTextContent();
                POILogger pOILogger = logger;
                doc = doc2;
                xpath = xpath2;
                pOILogger.log(1, "Extracting with xpath " + xpathString + " : value is '" + textContent + "'");
                XSSFCell cell = singleXmlCell.getReferencedCell();
                pOILogger.log(1, "Setting '" + textContent + "' to cell " + cell.getColumnIndex() + "-" + cell.getRowIndex() + " in sheet " + cell.getSheet().getSheetName());
                setCellValue(textContent, cell, xmlDataType);
            }
            builder = builder2;
            singleXmlCells = singleXmlCells2;
            xpathFactory = xpathFactory2;
            it2 = it;
            doc2 = doc;
            xpath2 = xpath;
        }
        Document doc3 = doc2;
        XPath xpath3 = xpath2;
        Iterator<XSSFTable> it3 = tables.iterator();
        while (it3.hasNext()) {
            XSSFTable table = it3.next();
            String commonXPath = table.getCommonXpath();
            Document doc4 = doc3;
            XPath xpath4 = xpath3;
            NodeList result2 = (NodeList) xpath4.evaluate(commonXPath, doc4, XPathConstants.NODESET);
            Iterator<XSSFTable> it4 = it3;
            int rowOffset = table.getStartCellReference().getRow() + 1;
            int columnOffset = table.getStartCellReference().getCol() - 1;
            int i = 0;
            while (true) {
                String commonXPath2 = commonXPath;
                if (i < result2.getLength()) {
                    List<XSSFTable> tables2 = tables;
                    Node singleNode = result2.item(i).cloneNode(true);
                    Iterator<XSSFXmlColumnPr> it5 = table.getXmlColumnPrs().iterator();
                    while (it5.hasNext()) {
                        XSSFXmlColumnPr xmlColumnPr = it5.next();
                        Iterator<XSSFXmlColumnPr> it6 = it5;
                        NodeList result3 = result2;
                        int localColumnId = (int) xmlColumnPr.getId();
                        int rowId = rowOffset + i;
                        Document doc5 = doc4;
                        int columnId = columnOffset + localColumnId;
                        String localXPath = xmlColumnPr.getLocalXPath();
                        int i2 = i;
                        int rowOffset2 = rowOffset;
                        String localXPath2 = localXPath.substring(localXPath.substring(1).indexOf(47) + 2);
                        String value = (String) xpath4.evaluate(localXPath2, singleNode, XPathConstants.STRING);
                        POILogger pOILogger2 = logger;
                        Node singleNode2 = singleNode;
                        XPath xpath5 = xpath4;
                        pOILogger2.log(1, "Extracting with xpath " + localXPath2 + " : value is '" + value + "'");
                        XSSFRow row = table.getXSSFSheet().getRow(rowId);
                        if (row == null) {
                            row = table.getXSSFSheet().createRow(rowId);
                        }
                        XSSFCell cell2 = row.getCell(columnId);
                        if (cell2 == null) {
                            cell2 = row.createCell(columnId);
                        }
                        pOILogger2.log(1, "Setting '" + value + "' to cell " + cell2.getColumnIndex() + "-" + cell2.getRowIndex() + " in sheet " + table.getXSSFSheet().getSheetName());
                        setCellValue(value, cell2, xmlColumnPr.getXmlDataType());
                        result2 = result3;
                        it5 = it6;
                        doc4 = doc5;
                        rowOffset = rowOffset2;
                        i = i2;
                        singleNode = singleNode2;
                        xpath4 = xpath5;
                    }
                    i++;
                    commonXPath = commonXPath2;
                    tables = tables2;
                }
            }
            it3 = it4;
            doc3 = doc4;
            xpath3 = xpath4;
        }
    }

    private enum DataType {
        BOOLEAN(STXmlDataType.BOOLEAN),
        DOUBLE(STXmlDataType.DOUBLE),
        INTEGER(STXmlDataType.INT, STXmlDataType.UNSIGNED_INT, STXmlDataType.INTEGER),
        STRING(STXmlDataType.STRING),
        DATE(STXmlDataType.DATE);

        private Set<STXmlDataType.Enum> xmlDataTypes;

        DataType(STXmlDataType.Enum... xmlDataTypes) {
            this.xmlDataTypes = new HashSet(Arrays.asList(xmlDataTypes));
        }

        public static DataType getDataType(STXmlDataType.Enum xmlDataType) {
            DataType[] arr$ = values();
            for (DataType dataType : arr$) {
                if (dataType.xmlDataTypes.contains(xmlDataType)) {
                    return dataType;
                }
            }
            return null;
        }
    }

    private void setCellValue(String value, XSSFCell cell, STXmlDataType.Enum xmlDataType) {
        DataType type = DataType.getDataType(xmlDataType);
        try {
            if (!value.isEmpty() && type != null) {
                int i = AnonymousClass1.$SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType[type.ordinal()];
                if (i == 1) {
                    cell.setCellValue(Boolean.parseBoolean(value));
                    return;
                }
                if (i == 2) {
                    cell.setCellValue(Double.parseDouble(value));
                    return;
                }
                if (i == 3) {
                    cell.setCellValue(Integer.parseInt(value));
                    return;
                }
                if (i == 4) {
                    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", LocaleUtil.getUserLocale());
                    Date date = sdf.parse(value);
                    cell.setCellValue(date);
                    if (!DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                        cell.setCellValue(value);
                        return;
                    }
                    return;
                }
                cell.setCellValue(value.trim());
                return;
            }
            cell.setCellValue((String) null);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(LocaleUtil.getUserLocale(), "Unable to format value '%s' as %s for cell %s", value, type, new CellReference(cell).formatAsString()));
        } catch (ParseException e2) {
            throw new IllegalArgumentException(String.format(LocaleUtil.getUserLocale(), "Unable to format value '%s' as %s for cell %s", value, type, new CellReference(cell).formatAsString()));
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.extractor.XSSFImportFromXML$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType;

        static {
            int[] iArr = new int[DataType.values().length];
            $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType = iArr;
            try {
                iArr[DataType.BOOLEAN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType[DataType.DOUBLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType[DataType.INTEGER.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType[DataType.DATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$xssf$extractor$XSSFImportFromXML$DataType[DataType.STRING.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private static final class DefaultNamespaceContext implements NamespaceContext {
        private final Element _docElem;

        public DefaultNamespaceContext(Document doc) {
            this._docElem = doc.getDocumentElement();
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getNamespaceURI(String prefix) {
            return getNamespaceForPrefix(prefix);
        }

        private String getNamespaceForPrefix(String prefix) {
            if (prefix.equals(ContentTypes.EXTENSION_XML)) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            Node parent = this._docElem;
            while (parent != null) {
                int type = parent.getNodeType();
                if (type == 1) {
                    if (parent.getNodeName().startsWith(prefix + ":")) {
                        return parent.getNamespaceURI();
                    }
                    NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); i++) {
                        Node attr = nnm.item(i);
                        String aname = attr.getNodeName();
                        boolean isPrefix = aname.startsWith("xmlns:");
                        if (isPrefix || aname.equals("xmlns")) {
                            int index = aname.indexOf(58);
                            String p = isPrefix ? aname.substring(index + 1) : "";
                            if (p.equals(prefix)) {
                                return attr.getNodeValue();
                            }
                        }
                    }
                    parent = parent.getParentNode();
                } else if (type != 5) {
                    return null;
                }
            }
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public Iterator<?> getPrefixes(String val) {
            return null;
        }

        @Override // javax.xml.namespace.NamespaceContext
        public String getPrefix(String uri) {
            return null;
        }
    }
}
