package org.apache.poi.xssf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLocation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableDefinition;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STAxis;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STItemType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;

/* JADX INFO: loaded from: classes.dex */
public class XSSFPivotTable extends POIXMLDocumentPart {
    protected static final short CREATED_VERSION = 3;
    protected static final short MIN_REFRESHABLE_VERSION = 3;
    protected static final short UPDATED_VERSION = 3;
    private Sheet dataSheet;
    private Sheet parentSheet;
    private XSSFPivotCache pivotCache;
    private XSSFPivotCacheDefinition pivotCacheDefinition;
    private XSSFPivotCacheRecords pivotCacheRecords;
    private CTPivotTableDefinition pivotTableDefinition;

    protected interface PivotTableReferenceConfigurator {
        void configureReference(CTWorksheetSource cTWorksheetSource);
    }

    protected XSSFPivotTable() {
        this.pivotTableDefinition = CTPivotTableDefinition.Factory.newInstance();
        this.pivotCache = new XSSFPivotCache();
        this.pivotCacheDefinition = new XSSFPivotCacheDefinition();
        this.pivotCacheRecords = new XSSFPivotCacheRecords();
    }

    protected XSSFPivotTable(PackagePart part) throws IOException {
        super(part);
        readFrom(part.getInputStream());
    }

    public void readFrom(InputStream is) throws IOException {
        try {
            XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            options.setLoadReplaceDocumentElement((QName) null);
            this.pivotTableDefinition = CTPivotTableDefinition.Factory.parse(is, options);
        } catch (XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }

    public void setPivotCache(XSSFPivotCache pivotCache) {
        this.pivotCache = pivotCache;
    }

    public XSSFPivotCache getPivotCache() {
        return this.pivotCache;
    }

    public Sheet getParentSheet() {
        return this.parentSheet;
    }

    public void setParentSheet(XSSFSheet parentSheet) {
        this.parentSheet = parentSheet;
    }

    @Internal
    public CTPivotTableDefinition getCTPivotTableDefinition() {
        return this.pivotTableDefinition;
    }

    @Internal
    public void setCTPivotTableDefinition(CTPivotTableDefinition pivotTableDefinition) {
        this.pivotTableDefinition = pivotTableDefinition;
    }

    public XSSFPivotCacheDefinition getPivotCacheDefinition() {
        return this.pivotCacheDefinition;
    }

    public void setPivotCacheDefinition(XSSFPivotCacheDefinition pivotCacheDefinition) {
        this.pivotCacheDefinition = pivotCacheDefinition;
    }

    public XSSFPivotCacheRecords getPivotCacheRecords() {
        return this.pivotCacheRecords;
    }

    public void setPivotCacheRecords(XSSFPivotCacheRecords pivotCacheRecords) {
        this.pivotCacheRecords = pivotCacheRecords;
    }

    public Sheet getDataSheet() {
        return this.dataSheet;
    }

    private void setDataSheet(Sheet dataSheet) {
        this.dataSheet = dataSheet;
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTPivotTableDefinition.type.getName().getNamespaceURI(), "pivotTableDefinition"));
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        this.pivotTableDefinition.save(out, xmlOptions);
        out.close();
    }

    protected void setDefaultPivotTableDefinition() {
        this.pivotTableDefinition.setMultipleFieldFilters(false);
        this.pivotTableDefinition.setIndent(0L);
        this.pivotTableDefinition.setCreatedVersion((short) 3);
        this.pivotTableDefinition.setMinRefreshableVersion((short) 3);
        this.pivotTableDefinition.setUpdatedVersion((short) 3);
        this.pivotTableDefinition.setItemPrintTitles(true);
        this.pivotTableDefinition.setUseAutoFormatting(true);
        this.pivotTableDefinition.setApplyNumberFormats(false);
        this.pivotTableDefinition.setApplyWidthHeightFormats(true);
        this.pivotTableDefinition.setApplyAlignmentFormats(false);
        this.pivotTableDefinition.setApplyPatternFormats(false);
        this.pivotTableDefinition.setApplyFontFormats(false);
        this.pivotTableDefinition.setApplyBorderFormats(false);
        this.pivotTableDefinition.setCacheId(this.pivotCache.getCTPivotCache().getCacheId());
        this.pivotTableDefinition.setName("PivotTable" + this.pivotTableDefinition.getCacheId());
        this.pivotTableDefinition.setDataCaption("Values");
        CTPivotTableStyle style = this.pivotTableDefinition.addNewPivotTableStyleInfo();
        style.setName("PivotStyleLight16");
        style.setShowLastColumn(true);
        style.setShowColStripes(false);
        style.setShowRowStripes(false);
        style.setShowColHeaders(true);
        style.setShowRowHeaders(true);
    }

    protected AreaReference getPivotArea() {
        Workbook wb = getDataSheet().getWorkbook();
        AreaReference pivotArea = getPivotCacheDefinition().getPivotArea(wb);
        return pivotArea;
    }

    private void checkColumnIndex(int columnIndex) throws IndexOutOfBoundsException {
        AreaReference pivotArea = getPivotArea();
        int size = (pivotArea.getLastCell().getCol() - pivotArea.getFirstCell().getCol()) + 1;
        if (columnIndex < 0 || columnIndex >= size) {
            throw new IndexOutOfBoundsException("Column Index: " + columnIndex + ", Size: " + size);
        }
    }

    public void addRowLabel(int columnIndex) {
        CTRowFields rowFields;
        checkColumnIndex(columnIndex);
        AreaReference pivotArea = getPivotArea();
        int lastRowIndex = pivotArea.getLastCell().getRow() - pivotArea.getFirstCell().getRow();
        CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        CTPivotField pivotField = CTPivotField.Factory.newInstance();
        CTItems items = pivotField.addNewItems();
        pivotField.setAxis(STAxis.AXIS_ROW);
        pivotField.setShowAll(false);
        for (int i = 0; i <= lastRowIndex; i++) {
            items.addNewItem().setT(STItemType.DEFAULT);
        }
        int i2 = items.sizeOfItemArray();
        items.setCount(i2);
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
        if (this.pivotTableDefinition.getRowFields() != null) {
            rowFields = this.pivotTableDefinition.getRowFields();
        } else {
            rowFields = this.pivotTableDefinition.addNewRowFields();
        }
        rowFields.addNewField().setX(columnIndex);
        rowFields.setCount(rowFields.sizeOfFieldArray());
    }

    public List<Integer> getRowLabelColumns() {
        if (this.pivotTableDefinition.getRowFields() != null) {
            List<Integer> columnIndexes = new ArrayList<>();
            CTField[] arr$ = this.pivotTableDefinition.getRowFields().getFieldArray();
            for (CTField f : arr$) {
                columnIndexes.add(Integer.valueOf(f.getX()));
            }
            return columnIndexes;
        }
        return Collections.emptyList();
    }

    public void addColumnLabel(DataConsolidateFunction function, int columnIndex, String valueFieldName) {
        CTColFields colFields;
        checkColumnIndex(columnIndex);
        addDataColumn(columnIndex, true);
        addDataField(function, columnIndex, valueFieldName);
        if (this.pivotTableDefinition.getDataFields().getCount() == 2) {
            if (this.pivotTableDefinition.getColFields() != null) {
                colFields = this.pivotTableDefinition.getColFields();
            } else {
                colFields = this.pivotTableDefinition.addNewColFields();
            }
            colFields.addNewField().setX(-2);
            colFields.setCount(colFields.sizeOfFieldArray());
        }
    }

    public void addColumnLabel(DataConsolidateFunction function, int columnIndex) {
        addColumnLabel(function, columnIndex, function.getName());
    }

    private void addDataField(DataConsolidateFunction function, int columnIndex, String valueFieldName) {
        CTDataFields dataFields;
        checkColumnIndex(columnIndex);
        AreaReference pivotArea = getPivotArea();
        if (this.pivotTableDefinition.getDataFields() != null) {
            dataFields = this.pivotTableDefinition.getDataFields();
        } else {
            dataFields = this.pivotTableDefinition.addNewDataFields();
        }
        CTDataField dataField = dataFields.addNewDataField();
        dataField.setSubtotal(STDataConsolidateFunction.Enum.forInt(function.getValue()));
        Cell cell = getDataSheet().getRow(pivotArea.getFirstCell().getRow()).getCell(pivotArea.getFirstCell().getCol() + columnIndex);
        cell.setCellType(CellType.STRING);
        dataField.setName(valueFieldName);
        dataField.setFld(columnIndex);
        dataFields.setCount(dataFields.sizeOfDataFieldArray());
    }

    public void addDataColumn(int columnIndex, boolean isDataField) {
        checkColumnIndex(columnIndex);
        CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        CTPivotField pivotField = CTPivotField.Factory.newInstance();
        pivotField.setDataField(isDataField);
        pivotField.setShowAll(false);
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
    }

    public void addReportFilter(int columnIndex) {
        CTPageFields pageFields;
        checkColumnIndex(columnIndex);
        AreaReference pivotArea = getPivotArea();
        int lastRowIndex = pivotArea.getLastCell().getRow() - pivotArea.getFirstCell().getRow();
        CTPivotFields pivotFields = this.pivotTableDefinition.getPivotFields();
        CTPivotField pivotField = CTPivotField.Factory.newInstance();
        CTItems items = pivotField.addNewItems();
        pivotField.setAxis(STAxis.AXIS_PAGE);
        pivotField.setShowAll(false);
        for (int i = 0; i <= lastRowIndex; i++) {
            items.addNewItem().setT(STItemType.DEFAULT);
        }
        int i2 = items.sizeOfItemArray();
        items.setCount(i2);
        pivotFields.setPivotFieldArray(columnIndex, pivotField);
        if (this.pivotTableDefinition.getPageFields() != null) {
            pageFields = this.pivotTableDefinition.getPageFields();
            this.pivotTableDefinition.setMultipleFieldFilters(true);
        } else {
            pageFields = this.pivotTableDefinition.addNewPageFields();
        }
        CTPageField pageField = pageFields.addNewPageField();
        pageField.setHier(-1);
        pageField.setFld(columnIndex);
        pageFields.setCount(pageFields.sizeOfPageFieldArray());
        this.pivotTableDefinition.getLocation().setColPageCount(pageFields.getCount());
    }

    protected void createSourceReferences(CellReference position, Sheet sourceSheet, PivotTableReferenceConfigurator refConfig) {
        CTLocation location;
        AreaReference destination = new AreaReference(position, new CellReference(position.getRow() + 1, position.getCol() + 1), SpreadsheetVersion.EXCEL2007);
        if (this.pivotTableDefinition.getLocation() == null) {
            location = this.pivotTableDefinition.addNewLocation();
            location.setFirstDataCol(1L);
            location.setFirstDataRow(1L);
            location.setFirstHeaderRow(1L);
        } else {
            location = this.pivotTableDefinition.getLocation();
        }
        location.setRef(destination.formatAsString());
        this.pivotTableDefinition.setLocation(location);
        CTPivotCacheDefinition cacheDef = getPivotCacheDefinition().getCTPivotCacheDefinition();
        CTCacheSource cacheSource = cacheDef.addNewCacheSource();
        cacheSource.setType(STSourceType.WORKSHEET);
        CTWorksheetSource worksheetSource = cacheSource.addNewWorksheetSource();
        worksheetSource.setSheet(sourceSheet.getSheetName());
        setDataSheet(sourceSheet);
        refConfig.configureReference(worksheetSource);
        if (worksheetSource.getName() == null && worksheetSource.getRef() == null) {
            throw new IllegalArgumentException("Pivot table source area reference or name must be specified.");
        }
    }

    protected void createDefaultDataColumns() {
        CTPivotFields pivotFields;
        if (this.pivotTableDefinition.getPivotFields() != null) {
            pivotFields = this.pivotTableDefinition.getPivotFields();
        } else {
            pivotFields = this.pivotTableDefinition.addNewPivotFields();
        }
        AreaReference sourceArea = getPivotArea();
        int firstColumn = sourceArea.getFirstCell().getCol();
        int lastColumn = sourceArea.getLastCell().getCol();
        for (int i = firstColumn; i <= lastColumn; i++) {
            CTPivotField pivotField = pivotFields.addNewPivotField();
            pivotField.setDataField(false);
            pivotField.setShowAll(false);
        }
        int i2 = pivotFields.sizeOfPivotFieldArray();
        pivotFields.setCount(i2);
    }
}
