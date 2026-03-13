package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.DocumentHelper;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class XSSFExportToXml implements Comparator<String> {
    private static final POILogger LOG = POILogFactory.getLogger((Class<?>) XSSFExportToXml.class);
    private XSSFMap map;

    public XSSFExportToXml(XSSFMap map) {
        this.map = map;
    }

    public void exportToXML(OutputStream os, boolean validate) throws TransformerException, SAXException {
        exportToXML(os, "UTF-8", validate);
    }

    public void exportToXML(OutputStream os, String encoding, boolean validate) throws TransformerException, SAXException {
        Element root;
        List<XSSFSingleXmlCell> singleXMLCells;
        List<XSSFTable> tables;
        String rootElement;
        Map<String, XSSFSingleXmlCell> singleXmlCellsMappings;
        List<CTTableColumn> tableColumns;
        XSSFRow row;
        short startColumnIndex;
        XSSFCell cell;
        List<XSSFSingleXmlCell> singleXMLCells2 = this.map.getRelatedSingleXMLCell();
        List<XSSFTable> tables2 = this.map.getRelatedTables();
        String rootElement2 = this.map.getCtMap().getRootElement();
        Document doc = DocumentHelper.createDocument();
        String str = "";
        if (isNamespaceDeclared()) {
            root = doc.createElementNS(getNamespace(), rootElement2);
        } else {
            root = doc.createElementNS("", rootElement2);
        }
        doc.appendChild(root);
        List<String> xpaths = new Vector<>();
        Map<String, XSSFSingleXmlCell> singleXmlCellsMappings2 = new HashMap<>();
        Map<String, XSSFTable> tableMappings = new HashMap<>();
        for (XSSFSingleXmlCell simpleXmlCell : singleXMLCells2) {
            xpaths.add(simpleXmlCell.getXpath());
            singleXmlCellsMappings2.put(simpleXmlCell.getXpath(), simpleXmlCell);
        }
        for (XSSFTable table : tables2) {
            String commonXPath = table.getCommonXpath();
            xpaths.add(commonXPath);
            tableMappings.put(commonXPath, table);
        }
        Collections.sort(xpaths, this);
        for (String xpath : xpaths) {
            XSSFSingleXmlCell simpleXmlCell2 = singleXmlCellsMappings2.get(xpath);
            XSSFTable table2 = tableMappings.get(xpath);
            if (xpath.matches(".*\\[.*")) {
                singleXMLCells = singleXMLCells2;
                tables = tables2;
                rootElement = rootElement2;
            } else {
                if (simpleXmlCell2 == null || (cell = simpleXmlCell2.getReferencedCell()) == null) {
                    singleXMLCells = singleXMLCells2;
                } else {
                    singleXMLCells = singleXMLCells2;
                    Node currentNode = getNodeByXPath(xpath, doc.getFirstChild(), doc, false);
                    mapCellOnNode(cell, currentNode);
                    if (str.equals(currentNode.getTextContent()) && currentNode.getParentNode() != null) {
                        currentNode.getParentNode().removeChild(currentNode);
                    }
                }
                if (table2 == null) {
                    tables = tables2;
                    rootElement = rootElement2;
                } else {
                    List<CTTableColumn> tableColumns2 = table2.getCTTable().getTableColumns().getTableColumnList();
                    XSSFSheet sheet = table2.getXSSFSheet();
                    int startRow = table2.getStartCellReference().getRow();
                    tables = tables2;
                    int endRow = table2.getEndCellReference().getRow();
                    rootElement = rootElement2;
                    int i = startRow + 1;
                    while (i <= endRow) {
                        int endRow2 = endRow;
                        XSSFRow row2 = sheet.getRow(i);
                        Element root2 = root;
                        String str2 = str;
                        List<String> xpaths2 = xpaths;
                        Node tableRootNode = getNodeByXPath(table2.getCommonXpath(), doc.getFirstChild(), doc, true);
                        short startColumnIndex2 = table2.getStartCellReference().getCol();
                        int j = startColumnIndex2;
                        while (true) {
                            singleXmlCellsMappings = singleXmlCellsMappings2;
                            if (j <= table2.getEndCellReference().getCol()) {
                                XSSFCell cell2 = row2.getCell(j);
                                if (cell2 == null) {
                                    tableColumns = tableColumns2;
                                    row = row2;
                                    startColumnIndex = startColumnIndex2;
                                } else {
                                    row = row2;
                                    int tableColumnIndex = j - startColumnIndex2;
                                    startColumnIndex = startColumnIndex2;
                                    if (tableColumnIndex >= tableColumns2.size()) {
                                        tableColumns = tableColumns2;
                                    } else {
                                        CTTableColumn ctTableColumn = tableColumns2.get(tableColumnIndex);
                                        if (ctTableColumn.getXmlColumnPr() == null) {
                                            tableColumns = tableColumns2;
                                        } else {
                                            tableColumns = tableColumns2;
                                            XSSFXmlColumnPr pointer = new XSSFXmlColumnPr(table2, ctTableColumn, ctTableColumn.getXmlColumnPr());
                                            String localXPath = pointer.getLocalXPath();
                                            mapCellOnNode(cell2, getNodeByXPath(localXPath, tableRootNode, doc, false));
                                        }
                                    }
                                }
                                j++;
                                row2 = row;
                                singleXmlCellsMappings2 = singleXmlCellsMappings;
                                startColumnIndex2 = startColumnIndex;
                                tableColumns2 = tableColumns;
                            }
                        }
                        i++;
                        endRow = endRow2;
                        root = root2;
                        str = str2;
                        xpaths = xpaths2;
                        singleXmlCellsMappings2 = singleXmlCellsMappings;
                        tableColumns2 = tableColumns2;
                    }
                }
            }
            singleXMLCells2 = singleXMLCells;
            tables2 = tables;
            rootElement2 = rootElement;
            root = root;
            str = str;
            xpaths = xpaths;
            singleXmlCellsMappings2 = singleXmlCellsMappings2;
        }
        boolean isValid = true;
        if (validate) {
            isValid = isValid(doc);
        }
        if (isValid) {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty("omit-xml-declaration", "yes");
            trans.setOutputProperty("indent", "yes");
            trans.setOutputProperty("encoding", encoding);
            StreamResult result = new StreamResult(os);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
        }
    }

    private boolean isValid(Document xml) throws SAXException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Source source = new DOMSource(this.map.getSchema());
            Schema schema = factory.newSchema(source);
            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(xml));
            return true;
        } catch (IOException e) {
            LOG.log(7, "document is not valid", e);
            return false;
        }
    }

    /* JADX INFO: renamed from: org.apache.poi.xssf.extractor.XSSFExportToXml$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$apache$poi$ss$usermodel$CellType;

        static {
            int[] iArr = new int[CellType.values().length];
            $SwitchMap$org$apache$poi$ss$usermodel$CellType = iArr;
            try {
                iArr[CellType.STRING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.FORMULA.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$org$apache$poi$ss$usermodel$CellType[CellType.NUMERIC.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private void mapCellOnNode(XSSFCell cell, Node node) {
        String value = "";
        int i = AnonymousClass1.$SwitchMap$org$apache$poi$ss$usermodel$CellType[cell.getCellTypeEnum().ordinal()];
        if (i == 1) {
            value = cell.getStringCellValue();
        } else if (i == 2) {
            value = "" + cell.getBooleanCellValue();
        } else if (i == 3) {
            value = cell.getErrorCellString();
        } else if (i == 4) {
            value = cell.getCachedFormulaResultTypeEnum() == CellType.STRING ? cell.getStringCellValue() : DateUtil.isCellDateFormatted(cell) ? getFormattedDate(cell) : "" + cell.getNumericCellValue();
        } else if (i == 5) {
            value = DateUtil.isCellDateFormatted(cell) ? getFormattedDate(cell) : "" + cell.getRawValue();
        }
        if (node instanceof Element) {
            Element currentElement = (Element) node;
            currentElement.setTextContent(value);
        } else {
            node.setNodeValue(value);
        }
    }

    private String removeNamespace(String elementName) {
        return elementName.matches(".*:.*") ? elementName.split(":")[1] : elementName;
    }

    private String getFormattedDate(XSSFCell cell) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        sdf.setTimeZone(LocaleUtil.getUserTimeZone());
        return sdf.format(cell.getDateCellValue());
    }

    private Node getNodeByXPath(String xpath, Node rootNode, Document doc, boolean createMultipleInstances) {
        String[] xpathTokens = xpath.split("/");
        Node currentNode = rootNode;
        for (int i = 2; i < xpathTokens.length; i++) {
            String axisName = removeNamespace(xpathTokens[i]);
            if (!axisName.startsWith("@")) {
                NodeList list = currentNode.getChildNodes();
                Node selectedNode = null;
                if (!createMultipleInstances || i != xpathTokens.length - 1) {
                    selectedNode = selectNode(axisName, list);
                }
                if (selectedNode == null) {
                    selectedNode = createElement(doc, currentNode, axisName);
                }
                currentNode = selectedNode;
            } else {
                currentNode = createAttribute(doc, currentNode, axisName);
            }
        }
        return currentNode;
    }

    private Node createAttribute(Document doc, Node currentNode, String axisName) {
        String attributeName = axisName.substring(1);
        NamedNodeMap attributesMap = currentNode.getAttributes();
        Node attribute = attributesMap.getNamedItem(attributeName);
        if (attribute == null) {
            Node attribute2 = doc.createAttributeNS("", attributeName);
            attributesMap.setNamedItem(attribute2);
            return attribute2;
        }
        return attribute;
    }

    private Node createElement(Document doc, Node currentNode, String axisName) {
        Node selectedNode;
        if (isNamespaceDeclared()) {
            selectedNode = doc.createElementNS(getNamespace(), axisName);
        } else {
            selectedNode = doc.createElementNS("", axisName);
        }
        currentNode.appendChild(selectedNode);
        return selectedNode;
    }

    private Node selectNode(String axisName, NodeList list) {
        for (int j = 0; j < list.getLength(); j++) {
            Node node = list.item(j);
            if (node.getNodeName().equals(axisName)) {
                return node;
            }
        }
        return null;
    }

    private boolean isNamespaceDeclared() {
        String schemaNamespace = getNamespace();
        return (schemaNamespace == null || schemaNamespace.equals("")) ? false : true;
    }

    private String getNamespace() {
        return this.map.getCTSchema().getNamespace();
    }

    @Override // java.util.Comparator
    public int compare(String leftXpath, String rightXpath) {
        Node xmlSchema = this.map.getSchema();
        String[] leftTokens = leftXpath.split("/");
        String[] rightTokens = rightXpath.split("/");
        int minLength = leftTokens.length < rightTokens.length ? leftTokens.length : rightTokens.length;
        Node localComplexTypeRootNode = xmlSchema;
        for (int i = 1; i < minLength; i++) {
            String leftElementName = leftTokens[i];
            String rightElementName = rightTokens[i];
            if (leftElementName.equals(rightElementName)) {
                localComplexTypeRootNode = getComplexTypeForElement(leftElementName, xmlSchema, localComplexTypeRootNode);
            } else {
                int leftIndex = indexOfElementInComplexType(leftElementName, localComplexTypeRootNode);
                int rightIndex = indexOfElementInComplexType(rightElementName, localComplexTypeRootNode);
                if (leftIndex != -1 && rightIndex != -1) {
                    if (leftIndex < rightIndex) {
                        return -1;
                    }
                    if (leftIndex > rightIndex) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    private int indexOfElementInComplexType(String elementName, Node complexType) {
        if (complexType == null) {
            return -1;
        }
        NodeList list = complexType.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if ((node instanceof Element) && node.getLocalName().equals("element")) {
                Node element = getNameOrRefElement(node);
                if (element.getNodeValue().equals(removeNamespace(elementName))) {
                    int indexOf = i;
                    return indexOf;
                }
            }
        }
        return -1;
    }

    private Node getNameOrRefElement(Node node) {
        Node returnNode = node.getAttributes().getNamedItem("name");
        if (returnNode != null) {
            return returnNode;
        }
        return node.getAttributes().getNamedItem("ref");
    }

    private Node getComplexTypeForElement(String elementName, Node xmlSchema, Node localComplexTypeRootNode) {
        String elementNameWithoutNamespace = removeNamespace(elementName);
        String complexTypeName = getComplexTypeNameFromChildren(localComplexTypeRootNode, elementNameWithoutNamespace);
        if ("".equals(complexTypeName)) {
            return null;
        }
        Node complexTypeNode = getComplexTypeNodeFromSchemaChildren(xmlSchema, null, complexTypeName);
        return complexTypeNode;
    }

    private String getComplexTypeNameFromChildren(Node localComplexTypeRootNode, String elementNameWithoutNamespace) {
        Node complexTypeAttribute;
        if (localComplexTypeRootNode == null) {
            return "";
        }
        NodeList list = localComplexTypeRootNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if ((node instanceof Element) && node.getLocalName().equals("element")) {
                Node nameAttribute = getNameOrRefElement(node);
                if (nameAttribute.getNodeValue().equals(elementNameWithoutNamespace) && (complexTypeAttribute = node.getAttributes().getNamedItem("type")) != null) {
                    String complexTypeName = complexTypeAttribute.getNodeValue();
                    return complexTypeName;
                }
            }
        }
        return "";
    }

    private Node getComplexTypeNodeFromSchemaChildren(Node xmlSchema, Node complexTypeNode, String complexTypeName) {
        NodeList complexTypeList = xmlSchema.getChildNodes();
        for (int i = 0; i < complexTypeList.getLength(); i++) {
            Node node = complexTypeList.item(i);
            if ((node instanceof Element) && node.getLocalName().equals("complexType")) {
                Node nameAttribute = getNameOrRefElement(node);
                if (nameAttribute.getNodeValue().equals(complexTypeName)) {
                    NodeList complexTypeChildList = node.getChildNodes();
                    for (int j = 0; j < complexTypeChildList.getLength(); j++) {
                        Node sequence = complexTypeChildList.item(j);
                        if ((sequence instanceof Element) && (sequence.getLocalName().equals("sequence") || sequence.getLocalName().equals("all"))) {
                            complexTypeNode = sequence;
                            break;
                        }
                    }
                    if (complexTypeNode != null) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        return complexTypeNode;
    }
}
