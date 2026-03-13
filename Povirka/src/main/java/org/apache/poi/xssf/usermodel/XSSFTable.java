package org.apache.poi.xssf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.usermodel.TableStyleInfo;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.TableDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSSFTable extends POIXMLDocumentPart implements Table {
    private transient HashMap<String, Integer> columnMap;
    private transient String commonXPath;
    private transient CTTableColumn[] ctColumns;
    private CTTable ctTable;
    private transient CellReference endCellReference;
    private transient String name;
    private transient CellReference startCellReference;
    private transient String styleName;
    private transient List<XSSFXmlColumnPr> xmlColumnPr;

    public XSSFTable() {
        this.ctTable = CTTable.Factory.newInstance();
    }

    public XSSFTable(PackagePart part) throws IOException {
        super(part);
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            TableDocument doc = TableDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.ctTable = doc.getTable();
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public XSSFSheet getXSSFSheet() {
        return (XSSFSheet) getParent();
    }

    public void writeTo(OutputStream out) throws IOException {
        updateHeaders();
        TableDocument doc = TableDocument.Factory.newInstance();
        doc.setTable(this.ctTable);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        writeTo(out);
        out.close();
    }

    @Internal(since = "POI 3.15 beta 3")
    public CTTable getCTTable() {
        return this.ctTable;
    }

    public boolean mapsTo(long id) {
        List<XSSFXmlColumnPr> pointers = getXmlColumnPrs();
        for (XSSFXmlColumnPr pointer : pointers) {
            if (pointer.getMapId() == id) {
                return true;
            }
        }
        return false;
    }

    private CTTableColumn[] getTableColumns() {
        if (this.ctColumns == null) {
            this.ctColumns = this.ctTable.getTableColumns().getTableColumnArray();
        }
        return this.ctColumns;
    }

    public String getCommonXpath() {
        if (this.commonXPath == null) {
            String[] commonTokens = new String[0];
            CTTableColumn[] arr$ = getTableColumns();
            for (CTTableColumn column : arr$) {
                if (column.getXmlColumnPr() != null) {
                    String xpath = column.getXmlColumnPr().getXpath();
                    String[] tokens = xpath.split("/");
                    if (commonTokens.length == 0) {
                        commonTokens = tokens;
                    } else {
                        int maxLength = Math.min(commonTokens.length, tokens.length);
                        int i = 0;
                        while (true) {
                            if (i >= maxLength) {
                                break;
                            }
                            if (!commonTokens[i].equals(tokens[i])) {
                                List<String> subCommonTokens = Arrays.asList(commonTokens).subList(0, i);
                                String[] container = new String[0];
                                commonTokens = (String[]) subCommonTokens.toArray(container);
                                break;
                            }
                            i++;
                        }
                    }
                }
            }
            commonTokens[0] = "";
            this.commonXPath = StringUtil.join(commonTokens, "/");
        }
        return this.commonXPath;
    }

    public List<XSSFXmlColumnPr> getXmlColumnPrs() {
        if (this.xmlColumnPr == null) {
            this.xmlColumnPr = new ArrayList();
            CTTableColumn[] arr$ = getTableColumns();
            for (CTTableColumn column : arr$) {
                if (column.getXmlColumnPr() != null) {
                    XSSFXmlColumnPr columnPr = new XSSFXmlColumnPr(this, column, column.getXmlColumnPr());
                    this.xmlColumnPr.add(columnPr);
                }
            }
        }
        return this.xmlColumnPr;
    }

    @Internal("Return type likely to change")
    public void addColumn() {
        CTTableColumns columns = this.ctTable.getTableColumns();
        if (columns == null) {
            columns = this.ctTable.addNewTableColumns();
        }
        CTTableColumn column = columns.addNewTableColumn();
        int num = columns.sizeOfTableColumnArray();
        columns.setCount(num);
        column.setId(num);
        updateHeaders();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public String getName() {
        if (this.name == null) {
            setName(this.ctTable.getName());
        }
        return this.name;
    }

    public void setName(String newName) {
        if (newName == null) {
            this.ctTable.unsetName();
            this.name = null;
        } else {
            this.ctTable.setName(newName);
            this.name = newName;
        }
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public String getStyleName() {
        if (this.styleName == null && this.ctTable.isSetTableStyleInfo()) {
            setStyleName(this.ctTable.getTableStyleInfo().getName());
        }
        return this.styleName;
    }

    public void setStyleName(String newStyleName) {
        if (newStyleName == null) {
            if (this.ctTable.isSetTableStyleInfo()) {
                this.ctTable.getTableStyleInfo().unsetName();
            }
            this.styleName = null;
        } else {
            if (!this.ctTable.isSetTableStyleInfo()) {
                this.ctTable.addNewTableStyleInfo();
            }
            this.ctTable.getTableStyleInfo().setName(newStyleName);
            this.styleName = newStyleName;
        }
    }

    public String getDisplayName() {
        return this.ctTable.getDisplayName();
    }

    public void setDisplayName(String name) {
        this.ctTable.setDisplayName(name);
    }

    public long getNumberOfMappedColumns() {
        return this.ctTable.getTableColumns().getCount();
    }

    public long getNumerOfMappedColumns() {
        return getNumberOfMappedColumns();
    }

    public AreaReference getCellReferences() {
        return new AreaReference(getStartCellReference(), getEndCellReference(), SpreadsheetVersion.EXCEL2007);
    }

    public void setCellReferences(AreaReference refs) {
        String ref = refs.formatAsString();
        if (ref.indexOf(33) != -1) {
            ref = ref.substring(ref.indexOf(33) + 1);
        }
        this.ctTable.setRef(ref);
        if (this.ctTable.isSetAutoFilter()) {
            this.ctTable.getAutoFilter().setRef(ref);
        }
        updateReferences();
        updateHeaders();
    }

    public CellReference getStartCellReference() {
        if (this.startCellReference == null) {
            setCellReferences();
        }
        return this.startCellReference;
    }

    public CellReference getEndCellReference() {
        if (this.endCellReference == null) {
            setCellReferences();
        }
        return this.endCellReference;
    }

    private void setCellReferences() {
        String ref = this.ctTable.getRef();
        if (ref != null) {
            String[] boundaries = ref.split(":", 2);
            String from = boundaries[0];
            String to = boundaries[1];
            this.startCellReference = new CellReference(from);
            this.endCellReference = new CellReference(to);
        }
    }

    public void updateReferences() {
        this.startCellReference = null;
        this.endCellReference = null;
    }

    public int getRowCount() {
        CellReference from = getStartCellReference();
        CellReference to = getEndCellReference();
        if (from == null || to == null) {
            return 0;
        }
        int rowCount = (to.getRow() - from.getRow()) + 1;
        return rowCount;
    }

    public void updateHeaders() {
        XSSFSheet sheet = (XSSFSheet) getParent();
        CellReference ref = getStartCellReference();
        if (ref == null) {
            return;
        }
        int headerRow = ref.getRow();
        int firstHeaderColumn = ref.getCol();
        XSSFRow row = sheet.getRow(headerRow);
        DataFormatter formatter = new DataFormatter();
        if (row != null && row.getCTRow().validate()) {
            int cellnum = firstHeaderColumn;
            CTTableColumn[] arr$ = getCTTable().getTableColumns().getTableColumnArray();
            for (CTTableColumn col : arr$) {
                XSSFCell cell = row.getCell(cellnum);
                if (cell != null) {
                    col.setName(formatter.formatCellValue(cell));
                }
                cellnum++;
            }
            this.ctColumns = null;
            this.columnMap = null;
            this.xmlColumnPr = null;
            this.commonXPath = null;
        }
    }

    private static String caseInsensitive(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int findColumnIndex(String columnHeader) {
        if (columnHeader == null) {
            return -1;
        }
        if (this.columnMap == null) {
            int count = getTableColumns().length;
            this.columnMap = new HashMap<>((count * 3) / 2);
            int i = 0;
            CTTableColumn[] arr$ = getTableColumns();
            for (CTTableColumn column : arr$) {
                String columnName = column.getName();
                this.columnMap.put(caseInsensitive(columnName), Integer.valueOf(i));
                i++;
            }
        }
        Integer idx = this.columnMap.get(caseInsensitive(columnHeader.replace("'", "")));
        if (idx == null) {
            return -1;
        }
        return idx.intValue();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public String getSheetName() {
        return getXSSFSheet().getSheetName();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public boolean isHasTotalsRow() {
        return this.ctTable.getTotalsRowShown();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getTotalsRowCount() {
        return (int) this.ctTable.getTotalsRowCount();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getHeaderRowCount() {
        return (int) this.ctTable.getHeaderRowCount();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getStartColIndex() {
        return getStartCellReference().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getStartRowIndex() {
        return getStartCellReference().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getEndColIndex() {
        return getEndCellReference().getCol();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public int getEndRowIndex() {
        return getEndCellReference().getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public TableStyleInfo getStyle() {
        if (this.ctTable.isSetTableStyleInfo()) {
            return new XSSFTableStyleInfo(((XSSFSheet) getParent()).getWorkbook().getStylesSource(), this.ctTable.getTableStyleInfo());
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Table
    public boolean contains(Cell cell) {
        if (cell == null || !getSheetName().equals(cell.getSheet().getSheetName()) || cell.getRowIndex() < getStartRowIndex() || cell.getRowIndex() > getEndRowIndex() || cell.getColumnIndex() < getStartColIndex() || cell.getColumnIndex() > getEndColIndex()) {
            return false;
        }
        return true;
    }

    protected void onTableDelete() {
        for (POIXMLDocumentPart.RelationPart part : getRelationParts()) {
            removeRelation(part.getDocumentPart(), true);
        }
    }
}
