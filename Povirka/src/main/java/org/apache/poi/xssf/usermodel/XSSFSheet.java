package org.apache.poi.xssf.usermodel;

import androidx.appcompat.widget.ActivityChooserView;
import androidx.core.view.MotionEventCompat;
import com.microsoft.schemas.vml.CTShape;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.POIXMLTypeLoader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.PaneInformation;
import org.apache.poi.ss.util.SSCellRange;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.helpers.ColumnHelper;
import org.apache.poi.xssf.usermodel.helpers.XSSFIgnoredErrorHelper;
import org.apache.poi.xssf.usermodel.helpers.XSSFPasswordHelper;
import org.apache.poi.xssf.usermodel.helpers.XSSFRowShifter;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredErrors;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOutlinePr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetUpPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRow;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetCalcPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTablePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableParts;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCalcMode;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPaneState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;

/* JADX INFO: loaded from: classes.dex */
public class XSSFSheet extends POIXMLDocumentPart implements Sheet {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final double DEFAULT_MARGIN_BOTTOM = 0.75d;
    private static final double DEFAULT_MARGIN_FOOTER = 0.3d;
    private static final double DEFAULT_MARGIN_HEADER = 0.3d;
    private static final double DEFAULT_MARGIN_LEFT = 0.7d;
    private static final double DEFAULT_MARGIN_RIGHT = 0.7d;
    private static final double DEFAULT_MARGIN_TOP = 0.75d;
    private static final double DEFAULT_ROW_HEIGHT = 15.0d;
    public static final int TWIPS_PER_POINT = 20;
    private static final POILogger logger = POILogFactory.getLogger((Class<?>) XSSFSheet.class);
    private final SortedMap<Integer, XSSFRow> _rows;
    private List<CellRangeAddress> arrayFormulas;
    private ColumnHelper columnHelper;
    private XSSFDataValidationHelper dataValidationHelper;
    private List<XSSFHyperlink> hyperlinks;
    private Map<Integer, CTCellFormula> sharedFormulas;
    protected CTSheet sheet;
    private CommentsTable sheetComments;
    private SortedMap<String, XSSFTable> tables;
    protected CTWorksheet worksheet;

    protected XSSFSheet() {
        this._rows = new TreeMap();
        this.dataValidationHelper = new XSSFDataValidationHelper(this);
        onDocumentCreate();
    }

    protected XSSFSheet(PackagePart part) {
        super(part);
        this._rows = new TreeMap();
        this.dataValidationHelper = new XSSFDataValidationHelper(this);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFWorkbook getWorkbook() {
        return (XSSFWorkbook) getParent();
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentRead() {
        try {
            read(getPackagePart().getInputStream());
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    protected void read(InputStream is) throws IOException {
        try {
            CTWorksheet worksheet = WorksheetDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getWorksheet();
            this.worksheet = worksheet;
            initRows(worksheet);
            this.columnHelper = new ColumnHelper(this.worksheet);
            for (POIXMLDocumentPart.RelationPart rp : getRelationParts()) {
                POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof CommentsTable) {
                    this.sheetComments = (CommentsTable) p;
                }
                if (p instanceof XSSFTable) {
                    this.tables.put(rp.getRelationship().getId(), (XSSFTable) p);
                }
                if (p instanceof XSSFPivotTable) {
                    getWorkbook().getPivotTables().add((XSSFPivotTable) p);
                }
            }
            initHyperlinks();
        } catch (XmlException e) {
            throw new POIXMLException((Throwable) e);
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void onDocumentCreate() {
        CTWorksheet cTWorksheetNewSheet = newSheet();
        this.worksheet = cTWorksheetNewSheet;
        initRows(cTWorksheetNewSheet);
        this.columnHelper = new ColumnHelper(this.worksheet);
        this.hyperlinks = new ArrayList();
    }

    private void initRows(CTWorksheet worksheetParam) {
        this._rows.clear();
        this.tables = new TreeMap();
        this.sharedFormulas = new HashMap();
        this.arrayFormulas = new ArrayList();
        CTRow[] arr$ = worksheetParam.getSheetData().getRowArray();
        for (CTRow row : arr$) {
            XSSFRow r = new XSSFRow(row, this);
            Integer rownumI = new Integer(r.getRowNum());
            this._rows.put(rownumI, r);
        }
    }

    private void initHyperlinks() {
        this.hyperlinks = new ArrayList();
        if (!this.worksheet.isSetHyperlinks()) {
            return;
        }
        try {
            PackageRelationshipCollection hyperRels = getPackagePart().getRelationshipsByType(XSSFRelation.SHEET_HYPERLINKS.getRelation());
            CTHyperlink[] arr$ = this.worksheet.getHyperlinks().getHyperlinkArray();
            for (CTHyperlink hyperlink : arr$) {
                PackageRelationship hyperRel = null;
                if (hyperlink.getId() != null) {
                    hyperRel = hyperRels.getRelationshipByID(hyperlink.getId());
                }
                this.hyperlinks.add(new XSSFHyperlink(hyperlink, hyperRel));
            }
        } catch (InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }

    private static CTWorksheet newSheet() {
        CTWorksheet worksheet = CTWorksheet.Factory.newInstance();
        CTSheetFormatPr ctFormat = worksheet.addNewSheetFormatPr();
        ctFormat.setDefaultRowHeight(DEFAULT_ROW_HEIGHT);
        CTSheetView ctView = worksheet.addNewSheetViews().addNewSheetView();
        ctView.setWorkbookViewId(0L);
        worksheet.addNewDimension().setRef("A1");
        worksheet.addNewSheetData();
        CTPageMargins ctMargins = worksheet.addNewPageMargins();
        ctMargins.setBottom(0.75d);
        ctMargins.setFooter(0.3d);
        ctMargins.setHeader(0.3d);
        ctMargins.setLeft(0.7d);
        ctMargins.setRight(0.7d);
        ctMargins.setTop(0.75d);
        return worksheet;
    }

    @Internal
    public CTWorksheet getCTWorksheet() {
        return this.worksheet;
    }

    public ColumnHelper getColumnHelper() {
        return this.columnHelper;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public String getSheetName() {
        return this.sheet.getName();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int addMergedRegion(CellRangeAddress region) {
        return addMergedRegion(region, true);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int addMergedRegionUnsafe(CellRangeAddress region) {
        return addMergedRegion(region, false);
    }

    private int addMergedRegion(CellRangeAddress region, boolean validate) {
        if (region.getNumberOfCells() < 2) {
            throw new IllegalArgumentException("Merged region " + region.formatAsString() + " must contain 2 or more cells");
        }
        region.validate(SpreadsheetVersion.EXCEL2007);
        if (validate) {
            validateArrayFormulas(region);
            validateMergedRegions(region);
        }
        CTMergeCells ctMergeCells = this.worksheet.isSetMergeCells() ? this.worksheet.getMergeCells() : this.worksheet.addNewMergeCells();
        CTMergeCell ctMergeCell = ctMergeCells.addNewMergeCell();
        ctMergeCell.setRef(region.formatAsString());
        return ctMergeCells.sizeOfMergeCellArray();
    }

    private void validateArrayFormulas(CellRangeAddress region) {
        int firstRow = region.getFirstRow();
        int firstColumn = region.getFirstColumn();
        int lastRow = region.getLastRow();
        int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            XSSFRow row = getRow(rowIn);
            if (row != null) {
                for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                    XSSFCell cell = row.getCell(colIn);
                    if (cell != null && cell.isPartOfArrayFormulaGroup()) {
                        CellRangeAddress arrayRange = cell.getArrayFormulaRange();
                        if (arrayRange.getNumberOfCells() > 1 && region.intersects(arrayRange)) {
                            String msg = "The range " + region.formatAsString() + " intersects with a multi-cell array formula. You cannot merge cells of an array.";
                            throw new IllegalStateException(msg);
                        }
                    }
                }
            }
        }
    }

    private void checkForMergedRegionsIntersectingArrayFormulas() {
        for (CellRangeAddress region : getMergedRegions()) {
            validateArrayFormulas(region);
        }
    }

    private void validateMergedRegions(CellRangeAddress candidateRegion) {
        for (CellRangeAddress existingRegion : getMergedRegions()) {
            if (existingRegion.intersects(candidateRegion)) {
                throw new IllegalStateException("Cannot add merged region " + candidateRegion.formatAsString() + " to sheet because it overlaps with an existing merged region (" + existingRegion.formatAsString() + ").");
            }
        }
    }

    private void checkForIntersectingMergedRegions() {
        List<CellRangeAddress> regions = getMergedRegions();
        int size = regions.size();
        for (int i = 0; i < size; i++) {
            CellRangeAddress region = regions.get(i);
            for (CellRangeAddress other : regions.subList(i + 1, regions.size())) {
                if (region.intersects(other)) {
                    String msg = "The range " + region.formatAsString() + " intersects with another merged region " + other.formatAsString() + " in this sheet";
                    throw new IllegalStateException(msg);
                }
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void validateMergedRegions() {
        checkForMergedRegionsIntersectingArrayFormulas();
        checkForIntersectingMergedRegions();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void autoSizeColumn(int column) {
        autoSizeColumn(column, false);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void autoSizeColumn(int column, boolean useMergedCells) {
        double width = SheetUtil.getColumnWidth(this, column, useMergedCells);
        if (width != -1.0d) {
            double width2 = width * 256.0d;
            if (width2 > MotionEventCompat.ACTION_POINTER_INDEX_MASK) {
                width2 = MotionEventCompat.ACTION_POINTER_INDEX_MASK;
            }
            setColumnWidth(column, (int) width2);
            this.columnHelper.setColBestFit(column, true);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFDrawing getDrawingPatriarch() {
        CTDrawing ctDrawing = getCTDrawing();
        if (ctDrawing != null) {
            Iterator<POIXMLDocumentPart.RelationPart> it = getRelationParts().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                POIXMLDocumentPart.RelationPart rp = it.next();
                POIXMLDocumentPart p = rp.getDocumentPart();
                if (p instanceof XSSFDrawing) {
                    XSSFDrawing dr = (XSSFDrawing) p;
                    String drId = rp.getRelationship().getId();
                    if (drId.equals(ctDrawing.getId())) {
                        return dr;
                    }
                }
            }
            logger.log(7, "Can't find drawing with id=" + ctDrawing.getId() + " in the list of the sheet's relationships");
            return null;
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFDrawing createDrawingPatriarch() {
        CTDrawing ctDrawing = getCTDrawing();
        if (ctDrawing != null) {
            return getDrawingPatriarch();
        }
        int drawingNumber = getPackagePart().getPackage().getPartsByContentType(XSSFRelation.DRAWINGS.getContentType()).size() + 1;
        POIXMLDocumentPart.RelationPart rp = createRelationship(XSSFRelation.DRAWINGS, XSSFFactory.getInstance(), getNextPartNumber(XSSFRelation.DRAWINGS, drawingNumber), false);
        XSSFDrawing drawing = (XSSFDrawing) rp.getDocumentPart();
        String relId = rp.getRelationship().getId();
        CTDrawing ctDrawing2 = this.worksheet.addNewDrawing();
        ctDrawing2.setId(relId);
        return drawing;
    }

    protected XSSFVMLDrawing getVMLDrawing(boolean autoCreate) {
        XSSFVMLDrawing drawing = null;
        CTLegacyDrawing ctDrawing = getCTLegacyDrawing();
        if (ctDrawing == null) {
            if (!autoCreate) {
                return null;
            }
            int drawingNumber = getPackagePart().getPackage().getPartsByContentType(XSSFRelation.VML_DRAWINGS.getContentType()).size() + 1;
            POIXMLDocumentPart.RelationPart rp = createRelationship(XSSFRelation.VML_DRAWINGS, XSSFFactory.getInstance(), drawingNumber, false);
            XSSFVMLDrawing drawing2 = (XSSFVMLDrawing) rp.getDocumentPart();
            String relId = rp.getRelationship().getId();
            this.worksheet.addNewLegacyDrawing().setId(relId);
            return drawing2;
        }
        String id = ctDrawing.getId();
        Iterator<POIXMLDocumentPart.RelationPart> it = getRelationParts().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            POIXMLDocumentPart.RelationPart rp2 = it.next();
            POIXMLDocumentPart p = rp2.getDocumentPart();
            if (p instanceof XSSFVMLDrawing) {
                XSSFVMLDrawing dr = (XSSFVMLDrawing) p;
                String drId = rp2.getRelationship().getId();
                if (drId.equals(id)) {
                    drawing = dr;
                    break;
                }
            }
        }
        if (drawing == null) {
            logger.log(7, "Can't find VML drawing with id=" + id + " in the list of the sheet's relationships");
            return drawing;
        }
        return drawing;
    }

    protected CTDrawing getCTDrawing() {
        return this.worksheet.getDrawing();
    }

    protected CTLegacyDrawing getCTLegacyDrawing() {
        return this.worksheet.getLegacyDrawing();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void createFreezePane(int colSplit, int rowSplit) {
        createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void createFreezePane(int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        CTSheetView ctView = getDefaultSheetView();
        if (colSplit == 0 && rowSplit == 0) {
            if (ctView.isSetPane()) {
                ctView.unsetPane();
            }
            ctView.setSelectionArray((CTSelection[]) null);
            return;
        }
        if (!ctView.isSetPane()) {
            ctView.addNewPane();
        }
        CTPane pane = ctView.getPane();
        if (colSplit > 0) {
            pane.setXSplit(colSplit);
        } else if (pane.isSetXSplit()) {
            pane.unsetXSplit();
        }
        if (rowSplit > 0) {
            pane.setYSplit(rowSplit);
        } else if (pane.isSetYSplit()) {
            pane.unsetYSplit();
        }
        pane.setState(STPaneState.FROZEN);
        if (rowSplit == 0) {
            pane.setTopLeftCell(new CellReference(0, leftmostColumn).formatAsString());
            pane.setActivePane(STPane.TOP_RIGHT);
        } else if (colSplit == 0) {
            pane.setTopLeftCell(new CellReference(topRow, 0).formatAsString());
            pane.setActivePane(STPane.BOTTOM_LEFT);
        } else {
            pane.setTopLeftCell(new CellReference(topRow, leftmostColumn).formatAsString());
            pane.setActivePane(STPane.BOTTOM_RIGHT);
        }
        ctView.setSelectionArray((CTSelection[]) null);
        CTSelection sel = ctView.addNewSelection();
        sel.setPane(pane.getActivePane());
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFRow createRow(int rownum) {
        CTRow ctRow;
        Integer rownumI = new Integer(rownum);
        XSSFRow prev = this._rows.get(rownumI);
        if (prev != null) {
            while (prev.getFirstCellNum() != -1) {
                prev.removeCell(prev.getCell((int) prev.getFirstCellNum()));
            }
            ctRow = prev.getCTRow();
            ctRow.set(CTRow.Factory.newInstance());
        } else if (this._rows.isEmpty() || rownum > this._rows.lastKey().intValue()) {
            ctRow = this.worksheet.getSheetData().addNewRow();
        } else {
            int idx = this._rows.headMap(rownumI).size();
            ctRow = this.worksheet.getSheetData().insertNewRow(idx);
        }
        XSSFRow r = new XSSFRow(ctRow, this);
        r.setRowNum(rownum);
        this._rows.put(rownumI, r);
        return r;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void createSplitPane(int xSplitPos, int ySplitPos, int leftmostColumn, int topRow, int activePane) {
        createFreezePane(xSplitPos, ySplitPos, leftmostColumn, topRow);
        getPane().setState(STPaneState.SPLIT);
        getPane().setActivePane(STPane.Enum.forInt(activePane));
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFComment getCellComment(CellAddress address) {
        if (this.sheetComments == null) {
            return null;
        }
        int row = address.getRow();
        int column = address.getColumn();
        CellAddress ref = new CellAddress(row, column);
        CTComment ctComment = this.sheetComments.getCTComment(ref);
        if (ctComment == null) {
            return null;
        }
        XSSFVMLDrawing vml = getVMLDrawing(false);
        return new XSSFComment(this.sheetComments, ctComment, vml != null ? vml.findCommentShape(row, column) : null);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public Map<CellAddress, XSSFComment> getCellComments() {
        CommentsTable commentsTable = this.sheetComments;
        if (commentsTable == null) {
            return Collections.emptyMap();
        }
        return commentsTable.getCellComments();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFHyperlink getHyperlink(int row, int column) {
        return getHyperlink(new CellAddress(row, column));
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFHyperlink getHyperlink(CellAddress addr) {
        String ref = addr.formatAsString();
        for (XSSFHyperlink hyperlink : this.hyperlinks) {
            if (hyperlink.getCellRef().equals(ref)) {
                return hyperlink;
            }
        }
        return null;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public List<XSSFHyperlink> getHyperlinkList() {
        return Collections.unmodifiableList(this.hyperlinks);
    }

    private int[] getBreaks(CTPageBreak ctPageBreak) {
        CTBreak[] brkArray = ctPageBreak.getBrkArray();
        int[] breaks = new int[brkArray.length];
        for (int i = 0; i < brkArray.length; i++) {
            breaks[i] = ((int) brkArray[i].getId()) - 1;
        }
        return breaks;
    }

    private void removeBreak(int index, CTPageBreak ctPageBreak) {
        int index1 = index + 1;
        CTBreak[] brkArray = ctPageBreak.getBrkArray();
        for (int i = 0; i < brkArray.length; i++) {
            if (brkArray[i].getId() == index1) {
                ctPageBreak.removeBrk(i);
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int[] getColumnBreaks() {
        return this.worksheet.isSetColBreaks() ? getBreaks(this.worksheet.getColBreaks()) : new int[0];
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getColumnWidth(int columnIndex) {
        CTCol col = this.columnHelper.getColumn(columnIndex, false);
        double width = (col == null || !col.isSetWidth()) ? getDefaultColumnWidth() : col.getWidth();
        return (int) (256.0d * width);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public float getColumnWidthInPixels(int columnIndex) {
        float widthIn256 = getColumnWidth(columnIndex);
        return (float) ((((double) widthIn256) / 256.0d) * 7.001699924468994d);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getDefaultColumnWidth() {
        CTSheetFormatPr pr = this.worksheet.getSheetFormatPr();
        if (pr == null) {
            return 8;
        }
        return (int) pr.getBaseColWidth();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public short getDefaultRowHeight() {
        return (short) (getDefaultRowHeightInPoints() * 20.0f);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public float getDefaultRowHeightInPoints() {
        CTSheetFormatPr pr = this.worksheet.getSheetFormatPr();
        return (float) (pr == null ? 0.0d : pr.getDefaultRowHeight());
    }

    private CTSheetFormatPr getSheetTypeSheetFormatPr() {
        return this.worksheet.isSetSheetFormatPr() ? this.worksheet.getSheetFormatPr() : this.worksheet.addNewSheetFormatPr();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellStyle getColumnStyle(int column) {
        int idx = this.columnHelper.getColDefaultStyle(column);
        return getWorkbook().getCellStyleAt((int) ((short) (idx == -1 ? 0 : idx)));
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRightToLeft(boolean value) {
        CTSheetView view = getDefaultSheetView();
        view.setRightToLeft(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isRightToLeft() {
        CTSheetView view = getDefaultSheetView();
        return view != null && view.getRightToLeft();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getDisplayGuts() {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        CTOutlinePr outlinePr = sheetPr.getOutlinePr() == null ? CTOutlinePr.Factory.newInstance() : sheetPr.getOutlinePr();
        return outlinePr.getShowOutlineSymbols();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDisplayGuts(boolean value) {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        CTOutlinePr outlinePr = sheetPr.getOutlinePr() == null ? sheetPr.addNewOutlinePr() : sheetPr.getOutlinePr();
        outlinePr.setShowOutlineSymbols(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isDisplayZeros() {
        CTSheetView view = getDefaultSheetView();
        return view == null || view.getShowZeros();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDisplayZeros(boolean value) {
        CTSheetView view = getSheetTypeSheetView();
        view.setShowZeros(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getFirstRowNum() {
        if (this._rows.isEmpty()) {
            return 0;
        }
        return this._rows.firstKey().intValue();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getFitToPage() {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        CTPageSetUpPr psSetup = (sheetPr == null || !sheetPr.isSetPageSetUpPr()) ? CTPageSetUpPr.Factory.newInstance() : sheetPr.getPageSetUpPr();
        return psSetup.getFitToPage();
    }

    private CTSheetPr getSheetTypeSheetPr() {
        if (this.worksheet.getSheetPr() == null) {
            this.worksheet.setSheetPr(CTSheetPr.Factory.newInstance());
        }
        return this.worksheet.getSheetPr();
    }

    private CTHeaderFooter getSheetTypeHeaderFooter() {
        if (this.worksheet.getHeaderFooter() == null) {
            this.worksheet.setHeaderFooter(CTHeaderFooter.Factory.newInstance());
        }
        return this.worksheet.getHeaderFooter();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public Footer getFooter() {
        return getOddFooter();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public Header getHeader() {
        return getOddHeader();
    }

    public Footer getOddFooter() {
        return new XSSFOddFooter(getSheetTypeHeaderFooter());
    }

    public Footer getEvenFooter() {
        return new XSSFEvenFooter(getSheetTypeHeaderFooter());
    }

    public Footer getFirstFooter() {
        return new XSSFFirstFooter(getSheetTypeHeaderFooter());
    }

    public Header getOddHeader() {
        return new XSSFOddHeader(getSheetTypeHeaderFooter());
    }

    public Header getEvenHeader() {
        return new XSSFEvenHeader(getSheetTypeHeaderFooter());
    }

    public Header getFirstHeader() {
        return new XSSFFirstHeader(getSheetTypeHeaderFooter());
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getHorizontallyCenter() {
        CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getHorizontalCentered();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getLastRowNum() {
        if (this._rows.isEmpty()) {
            return 0;
        }
        return this._rows.lastKey().intValue();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public short getLeftCol() {
        String cellRef = this.worksheet.getSheetViews().getSheetViewArray(0).getTopLeftCell();
        if (cellRef == null) {
            return (short) 0;
        }
        CellReference cellReference = new CellReference(cellRef);
        return cellReference.getCol();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public double getMargin(short margin) {
        if (!this.worksheet.isSetPageMargins()) {
            return 0.0d;
        }
        CTPageMargins pageMargins = this.worksheet.getPageMargins();
        if (margin == 0) {
            return pageMargins.getLeft();
        }
        if (margin == 1) {
            return pageMargins.getRight();
        }
        if (margin == 2) {
            return pageMargins.getTop();
        }
        if (margin == 3) {
            return pageMargins.getBottom();
        }
        if (margin == 4) {
            return pageMargins.getHeader();
        }
        if (margin == 5) {
            return pageMargins.getFooter();
        }
        throw new IllegalArgumentException("Unknown margin constant:  " + ((int) margin));
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setMargin(short margin, double size) {
        CTPageMargins pageMargins = this.worksheet.isSetPageMargins() ? this.worksheet.getPageMargins() : this.worksheet.addNewPageMargins();
        if (margin == 0) {
            pageMargins.setLeft(size);
            return;
        }
        if (margin == 1) {
            pageMargins.setRight(size);
            return;
        }
        if (margin == 2) {
            pageMargins.setTop(size);
            return;
        }
        if (margin == 3) {
            pageMargins.setBottom(size);
        } else if (margin == 4) {
            pageMargins.setHeader(size);
        } else {
            if (margin == 5) {
                pageMargins.setFooter(size);
                return;
            }
            throw new IllegalArgumentException("Unknown margin constant:  " + ((int) margin));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellRangeAddress getMergedRegion(int index) {
        CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        if (ctMergeCells == null) {
            throw new IllegalStateException("This worksheet does not contain merged regions");
        }
        CTMergeCell ctMergeCell = ctMergeCells.getMergeCellArray(index);
        String ref = ctMergeCell.getRef();
        return CellRangeAddress.valueOf(ref);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public List<CellRangeAddress> getMergedRegions() {
        List<CellRangeAddress> addresses = new ArrayList<>();
        CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        if (ctMergeCells == null) {
            return addresses;
        }
        CTMergeCell[] arr$ = ctMergeCells.getMergeCellArray();
        for (CTMergeCell ctMergeCell : arr$) {
            String ref = ctMergeCell.getRef();
            addresses.add(CellRangeAddress.valueOf(ref));
        }
        return addresses;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getNumMergedRegions() {
        CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        if (ctMergeCells == null) {
            return 0;
        }
        return ctMergeCells.sizeOfMergeCellArray();
    }

    public int getNumHyperlinks() {
        return this.hyperlinks.size();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public PaneInformation getPaneInformation() {
        CTPane pane = getDefaultSheetView().getPane();
        if (pane == null) {
            return null;
        }
        CellReference cellRef = pane.isSetTopLeftCell() ? new CellReference(pane.getTopLeftCell()) : null;
        return new PaneInformation((short) pane.getXSplit(), (short) pane.getYSplit(), (short) (cellRef == null ? 0 : cellRef.getRow()), cellRef == null ? (short) 0 : cellRef.getCol(), (byte) (pane.getActivePane().intValue() - 1), pane.getState() == STPaneState.FROZEN);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getPhysicalNumberOfRows() {
        return this._rows.size();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFPrintSetup getPrintSetup() {
        return new XSSFPrintSetup(this.worksheet);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getProtect() {
        return isSheetLocked();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void protectSheet(String password) {
        if (password != null) {
            CTSheetProtection sheetProtection = safeGetProtectionField();
            setSheetPassword(password, null);
            sheetProtection.setSheet(true);
            sheetProtection.setScenarios(true);
            sheetProtection.setObjects(true);
            return;
        }
        this.worksheet.unsetSheetProtection();
    }

    public void setSheetPassword(String password, HashAlgorithm hashAlgo) {
        if (password == null && !isSheetProtectionEnabled()) {
            return;
        }
        XSSFPasswordHelper.setPassword(safeGetProtectionField(), password, hashAlgo, null);
    }

    public boolean validateSheetPassword(String password) {
        if (isSheetProtectionEnabled()) {
            return XSSFPasswordHelper.validatePassword(safeGetProtectionField(), password, null);
        }
        return password == null;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFRow getRow(int rownum) {
        Integer rownumI = new Integer(rownum);
        return this._rows.get(rownumI);
    }

    private List<XSSFRow> getRows(int startRowNum, int endRowNum, boolean createRowIfMissing) {
        if (startRowNum > endRowNum) {
            throw new IllegalArgumentException("getRows: startRowNum must be less than or equal to endRowNum");
        }
        List<XSSFRow> rows = new ArrayList<>();
        if (createRowIfMissing) {
            for (int i = startRowNum; i <= endRowNum; i++) {
                XSSFRow row = getRow(i);
                if (row == null) {
                    row = createRow(i);
                }
                rows.add(row);
            }
        } else {
            Integer startI = new Integer(startRowNum);
            Integer endI = new Integer(endRowNum + 1);
            Collection<XSSFRow> inclusive = this._rows.subMap(startI, endI).values();
            rows.addAll(inclusive);
        }
        return rows;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int[] getRowBreaks() {
        return this.worksheet.isSetRowBreaks() ? getBreaks(this.worksheet.getRowBreaks()) : new int[0];
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getRowSumsBelow() {
        CTSheetPr sheetPr = this.worksheet.getSheetPr();
        CTOutlinePr outlinePr = (sheetPr == null || !sheetPr.isSetOutlinePr()) ? null : sheetPr.getOutlinePr();
        return outlinePr == null || outlinePr.getSummaryBelow();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRowSumsBelow(boolean value) {
        ensureOutlinePr().setSummaryBelow(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getRowSumsRight() {
        CTSheetPr sheetPr = this.worksheet.getSheetPr();
        CTOutlinePr outlinePr = (sheetPr == null || !sheetPr.isSetOutlinePr()) ? CTOutlinePr.Factory.newInstance() : sheetPr.getOutlinePr();
        return outlinePr.getSummaryRight();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRowSumsRight(boolean value) {
        ensureOutlinePr().setSummaryRight(value);
    }

    private CTOutlinePr ensureOutlinePr() {
        CTSheetPr sheetPr = this.worksheet.isSetSheetPr() ? this.worksheet.getSheetPr() : this.worksheet.addNewSheetPr();
        return sheetPr.isSetOutlinePr() ? sheetPr.getOutlinePr() : sheetPr.addNewOutlinePr();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getScenarioProtect() {
        return this.worksheet.isSetSheetProtection() && this.worksheet.getSheetProtection().getScenarios();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public short getTopRow() {
        String cellRef = getSheetTypeSheetView().getTopLeftCell();
        if (cellRef == null) {
            return (short) 0;
        }
        CellReference cellReference = new CellReference(cellRef);
        return (short) cellReference.getRow();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getVerticallyCenter() {
        CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getVerticalCentered();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void groupColumn(int fromColumn, int toColumn) {
        groupColumn1Based(fromColumn + 1, toColumn + 1);
    }

    private void groupColumn1Based(int fromColumn, int toColumn) {
        CTCols ctCols = this.worksheet.getColsArray(0);
        CTCol ctCol = CTCol.Factory.newInstance();
        CTCol fixCol_before = this.columnHelper.getColumn1Based(toColumn, false);
        if (fixCol_before != null) {
            fixCol_before = (CTCol) fixCol_before.copy();
        }
        ctCol.setMin(fromColumn);
        ctCol.setMax(toColumn);
        this.columnHelper.addCleanColIntoCols(ctCols, ctCol);
        CTCol fixCol_after = this.columnHelper.getColumn1Based(toColumn, false);
        if (fixCol_before != null && fixCol_after != null) {
            this.columnHelper.setColumnAttributes(fixCol_before, fixCol_after);
        }
        int index = fromColumn;
        while (index <= toColumn) {
            CTCol col = this.columnHelper.getColumn1Based(index, false);
            short outlineLevel = col.getOutlineLevel();
            col.setOutlineLevel((short) (outlineLevel + 1));
            int index2 = (int) col.getMax();
            index = index2 + 1;
        }
        this.worksheet.setColsArray(0, ctCols);
        setSheetFormatPrOutlineLevelCol();
    }

    private void setColWidthAttribute(CTCols ctCols) {
        CTCol[] arr$ = ctCols.getColArray();
        for (CTCol col : arr$) {
            if (!col.isSetWidth()) {
                col.setWidth(getDefaultColumnWidth());
                col.setCustomWidth(false);
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void groupRow(int fromRow, int toRow) {
        for (int i = fromRow; i <= toRow; i++) {
            XSSFRow xrow = getRow(i);
            if (xrow == null) {
                xrow = createRow(i);
            }
            CTRow ctrow = xrow.getCTRow();
            short outlineLevel = ctrow.getOutlineLevel();
            ctrow.setOutlineLevel((short) (outlineLevel + 1));
        }
        setSheetFormatPrOutlineLevelRow();
    }

    private short getMaxOutlineLevelRows() {
        int outlineLevel = 0;
        for (XSSFRow xrow : this._rows.values()) {
            outlineLevel = Math.max(outlineLevel, (int) xrow.getCTRow().getOutlineLevel());
        }
        return (short) outlineLevel;
    }

    private short getMaxOutlineLevelCols() {
        CTCols ctCols = this.worksheet.getColsArray(0);
        int outlineLevel = 0;
        CTCol[] arr$ = ctCols.getColArray();
        for (CTCol col : arr$) {
            outlineLevel = Math.max(outlineLevel, (int) col.getOutlineLevel());
        }
        return (short) outlineLevel;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isColumnBroken(int column) {
        int[] arr$ = getColumnBreaks();
        for (int colBreak : arr$) {
            if (colBreak == column) {
                return true;
            }
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isColumnHidden(int columnIndex) {
        CTCol col = this.columnHelper.getColumn(columnIndex, false);
        return col != null && col.getHidden();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isDisplayFormulas() {
        return getSheetTypeSheetView().getShowFormulas();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isDisplayGridlines() {
        return getSheetTypeSheetView().getShowGridLines();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDisplayGridlines(boolean show) {
        getSheetTypeSheetView().setShowGridLines(show);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isDisplayRowColHeadings() {
        return getSheetTypeSheetView().getShowRowColHeaders();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDisplayRowColHeadings(boolean show) {
        getSheetTypeSheetView().setShowRowColHeaders(show);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isPrintGridlines() {
        CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getGridLines();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setPrintGridlines(boolean value) {
        CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setGridLines(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isPrintRowAndColumnHeadings() {
        CTPrintOptions opts = this.worksheet.getPrintOptions();
        return opts != null && opts.getHeadings();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setPrintRowAndColumnHeadings(boolean value) {
        CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setHeadings(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isRowBroken(int row) {
        int[] arr$ = getRowBreaks();
        for (int rowBreak : arr$) {
            if (rowBreak == row) {
                return true;
            }
        }
        return false;
    }

    private void setBreak(int id, CTPageBreak ctPgBreak, int lastIndex) {
        CTBreak brk = ctPgBreak.addNewBrk();
        brk.setId(id + 1);
        brk.setMan(true);
        brk.setMax(lastIndex);
        int nPageBreaks = ctPgBreak.sizeOfBrkArray();
        ctPgBreak.setCount(nPageBreaks);
        ctPgBreak.setManualBreakCount(nPageBreaks);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRowBreak(int row) {
        if (!isRowBroken(row)) {
            CTPageBreak pgBreak = this.worksheet.isSetRowBreaks() ? this.worksheet.getRowBreaks() : this.worksheet.addNewRowBreaks();
            setBreak(row, pgBreak, SpreadsheetVersion.EXCEL2007.getLastColumnIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void removeColumnBreak(int column) {
        if (this.worksheet.isSetColBreaks()) {
            removeBreak(column, this.worksheet.getColBreaks());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void removeMergedRegion(int index) {
        if (!this.worksheet.isSetMergeCells()) {
            return;
        }
        CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        int size = ctMergeCells.sizeOfMergeCellArray();
        if (index < 0 || index >= size) {
            throw new AssertionError();
        }
        if (size > 1) {
            ctMergeCells.removeMergeCell(index);
        } else {
            this.worksheet.unsetMergeCells();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void removeMergedRegions(Collection<Integer> indices) {
        if (!this.worksheet.isSetMergeCells()) {
            return;
        }
        CTMergeCells ctMergeCells = this.worksheet.getMergeCells();
        List<CTMergeCell> newMergeCells = new ArrayList<>(ctMergeCells.sizeOfMergeCellArray());
        int idx = 0;
        CTMergeCell[] arr$ = ctMergeCells.getMergeCellArray();
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            CTMergeCell mc = arr$[i$];
            int idx2 = idx + 1;
            if (!indices.contains(Integer.valueOf(idx))) {
                newMergeCells.add(mc);
            }
            i$++;
            idx = idx2;
        }
        if (newMergeCells.isEmpty()) {
            this.worksheet.unsetMergeCells();
        } else {
            CTMergeCell[] newMergeCellsArray = new CTMergeCell[newMergeCells.size()];
            ctMergeCells.setMergeCellArray((CTMergeCell[]) newMergeCells.toArray(newMergeCellsArray));
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void removeRow(Row row) {
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        ArrayList<XSSFCell> cellsToDelete = new ArrayList<>();
        for (Cell cell : row) {
            cellsToDelete.add((XSSFCell) cell);
        }
        for (XSSFCell cell2 : cellsToDelete) {
            row.removeCell(cell2);
        }
        int rowNum = row.getRowNum();
        Integer rowNumI = new Integer(rowNum);
        int idx = this._rows.headMap(rowNumI).size();
        this._rows.remove(rowNumI);
        this.worksheet.getSheetData().removeRow(idx);
        if (this.sheetComments != null) {
            for (CellAddress ref : getCellComments().keySet()) {
                if (ref.getRow() == rowNum) {
                    this.sheetComments.removeComment(ref);
                }
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void removeRowBreak(int row) {
        if (this.worksheet.isSetRowBreaks()) {
            removeBreak(row, this.worksheet.getRowBreaks());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setForceFormulaRecalculation(boolean value) {
        CTCalcPr calcPr = getWorkbook().getCTWorkbook().getCalcPr();
        if (this.worksheet.isSetSheetCalcPr()) {
            CTSheetCalcPr calc = this.worksheet.getSheetCalcPr();
            calc.setFullCalcOnLoad(value);
        } else if (value) {
            CTSheetCalcPr calc2 = this.worksheet.addNewSheetCalcPr();
            calc2.setFullCalcOnLoad(value);
        }
        if (value && calcPr != null && calcPr.getCalcMode() == STCalcMode.MANUAL) {
            calcPr.setCalcMode(STCalcMode.AUTO);
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getForceFormulaRecalculation() {
        if (this.worksheet.isSetSheetCalcPr()) {
            CTSheetCalcPr calc = this.worksheet.getSheetCalcPr();
            return calc.getFullCalcOnLoad();
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public Iterator<Row> rowIterator() {
        return this._rows.values().iterator();
    }

    @Override // java.lang.Iterable
    public Iterator<Row> iterator() {
        return rowIterator();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean getAutobreaks() {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        CTPageSetUpPr psSetup = (sheetPr == null || !sheetPr.isSetPageSetUpPr()) ? CTPageSetUpPr.Factory.newInstance() : sheetPr.getPageSetUpPr();
        return psSetup.getAutoPageBreaks();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setAutobreaks(boolean value) {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        CTPageSetUpPr psSetup = sheetPr.isSetPageSetUpPr() ? sheetPr.getPageSetUpPr() : sheetPr.addNewPageSetUpPr();
        psSetup.setAutoPageBreaks(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setColumnBreak(int column) {
        if (!isColumnBroken(column)) {
            CTPageBreak pgBreak = this.worksheet.isSetColBreaks() ? this.worksheet.getColBreaks() : this.worksheet.addNewColBreaks();
            setBreak(column, pgBreak, SpreadsheetVersion.EXCEL2007.getLastRowIndex());
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setColumnGroupCollapsed(int columnNumber, boolean collapsed) {
        if (collapsed) {
            collapseColumn(columnNumber);
        } else {
            expandColumn(columnNumber);
        }
    }

    private void collapseColumn(int columnNumber) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol col = this.columnHelper.getColumn(columnNumber, false);
        int colInfoIx = this.columnHelper.getIndexOfColumn(cols, col);
        if (colInfoIx == -1) {
            return;
        }
        int groupStartColInfoIx = findStartOfColumnOutlineGroup(colInfoIx);
        CTCol columnInfo = cols.getColArray(groupStartColInfoIx);
        int lastColMax = setGroupHidden(groupStartColInfoIx, columnInfo.getOutlineLevel(), true);
        setColumn(lastColMax + 1, 0, null, null, Boolean.TRUE);
    }

    private void setColumn(int targetColumnIx, Integer style, Integer level, Boolean hidden, Boolean collapsed) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol ci = null;
        CTCol[] arr$ = cols.getColArray();
        int len$ = arr$.length;
        int i$ = 0;
        while (true) {
            if (i$ >= len$) {
                break;
            }
            CTCol tci = arr$[i$];
            long tciMin = tci.getMin();
            long tciMax = tci.getMax();
            if (tciMin >= targetColumnIx && tciMax <= targetColumnIx) {
                ci = tci;
                break;
            } else if (tciMin > targetColumnIx) {
                break;
            } else {
                i$++;
            }
        }
        if (ci == null) {
            CTCol nci = CTCol.Factory.newInstance();
            nci.setMin(targetColumnIx);
            nci.setMax(targetColumnIx);
            unsetCollapsed(collapsed.booleanValue(), nci);
            this.columnHelper.addCleanColIntoCols(cols, nci);
            return;
        }
        boolean styleChanged = (style == null || ci.getStyle() == ((long) style.intValue())) ? false : true;
        boolean levelChanged = (level == null || ci.getOutlineLevel() == level.intValue()) ? false : true;
        boolean hiddenChanged = (hidden == null || ci.getHidden() == hidden.booleanValue()) ? false : true;
        boolean collapsedChanged = (collapsed == null || ci.getCollapsed() == collapsed.booleanValue()) ? false : true;
        boolean columnChanged = levelChanged || hiddenChanged || collapsedChanged || styleChanged;
        if (!columnChanged) {
            return;
        }
        long ciMin = ci.getMin();
        long ciMax = ci.getMax();
        if (ciMin == targetColumnIx && ciMax == targetColumnIx) {
            unsetCollapsed(collapsed.booleanValue(), ci);
            return;
        }
        if (ciMin == targetColumnIx || ciMax == targetColumnIx) {
            if (ciMin == targetColumnIx) {
                ci.setMin(targetColumnIx + 1);
            } else {
                ci.setMax(targetColumnIx - 1);
            }
            CTCol nci2 = this.columnHelper.cloneCol(cols, ci);
            nci2.setMin(targetColumnIx);
            unsetCollapsed(collapsed.booleanValue(), nci2);
            this.columnHelper.addCleanColIntoCols(cols, nci2);
            return;
        }
        CTCol ciMid = this.columnHelper.cloneCol(cols, ci);
        CTCol ciEnd = this.columnHelper.cloneCol(cols, ci);
        int lastcolumn = (int) ciMax;
        ci.setMax(targetColumnIx - 1);
        ciMid.setMin(targetColumnIx);
        ciMid.setMax(targetColumnIx);
        unsetCollapsed(collapsed.booleanValue(), ciMid);
        this.columnHelper.addCleanColIntoCols(cols, ciMid);
        ciEnd.setMin(targetColumnIx + 1);
        ciEnd.setMax(lastcolumn);
        this.columnHelper.addCleanColIntoCols(cols, ciEnd);
    }

    private void unsetCollapsed(boolean collapsed, CTCol ci) {
        if (collapsed) {
            ci.setCollapsed(collapsed);
        } else {
            ci.unsetCollapsed();
        }
    }

    private int setGroupHidden(int pIdx, int level, boolean hidden) {
        CTCols cols = this.worksheet.getColsArray(0);
        int idx = pIdx;
        CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[idx];
        while (idx < colArray.length) {
            columnInfo.setHidden(hidden);
            if (idx + 1 < colArray.length) {
                CTCol nextColumnInfo = colArray[idx + 1];
                if (!isAdjacentBefore(columnInfo, nextColumnInfo) || nextColumnInfo.getOutlineLevel() < level) {
                    break;
                }
                columnInfo = nextColumnInfo;
            }
            idx++;
        }
        return (int) columnInfo.getMax();
    }

    private boolean isAdjacentBefore(CTCol col, CTCol otherCol) {
        return col.getMax() == otherCol.getMin() - 1;
    }

    private int findStartOfColumnOutlineGroup(int pIdx) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[pIdx];
        int level = columnInfo.getOutlineLevel();
        int idx = pIdx;
        while (idx != 0) {
            CTCol prevColumnInfo = colArray[idx - 1];
            if (!isAdjacentBefore(prevColumnInfo, columnInfo) || prevColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx--;
            columnInfo = prevColumnInfo;
        }
        return idx;
    }

    private int findEndOfColumnOutlineGroup(int colInfoIndex) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[colInfoIndex];
        int level = columnInfo.getOutlineLevel();
        int idx = colInfoIndex;
        int lastIdx = colArray.length - 1;
        while (idx < lastIdx) {
            CTCol nextColumnInfo = colArray[idx + 1];
            if (!isAdjacentBefore(columnInfo, nextColumnInfo) || nextColumnInfo.getOutlineLevel() < level) {
                break;
            }
            idx++;
            columnInfo = nextColumnInfo;
        }
        return idx;
    }

    private void expandColumn(int columnIndex) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol col = this.columnHelper.getColumn(columnIndex, false);
        int colInfoIx = this.columnHelper.getIndexOfColumn(cols, col);
        int idx = findColInfoIdx((int) col.getMax(), colInfoIx);
        if (idx == -1 || !isColumnGroupCollapsed(idx)) {
            return;
        }
        int startIdx = findStartOfColumnOutlineGroup(idx);
        int endIdx = findEndOfColumnOutlineGroup(idx);
        CTCol[] colArray = cols.getColArray();
        CTCol columnInfo = colArray[endIdx];
        if (!isColumnGroupHiddenByParent(idx)) {
            short outlineLevel = columnInfo.getOutlineLevel();
            boolean nestedGroup = false;
            for (int i = startIdx; i <= endIdx; i++) {
                CTCol ci = colArray[i];
                if (outlineLevel == ci.getOutlineLevel()) {
                    ci.unsetHidden();
                    if (nestedGroup) {
                        nestedGroup = false;
                        ci.setCollapsed(true);
                    }
                } else {
                    nestedGroup = true;
                }
            }
        }
        setColumn(1 + ((int) columnInfo.getMax()), null, null, Boolean.FALSE, Boolean.FALSE);
    }

    private boolean isColumnGroupHiddenByParent(int idx) {
        CTCols cols = this.worksheet.getColsArray(0);
        int endLevel = 0;
        boolean endHidden = false;
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        CTCol[] colArray = cols.getColArray();
        if (endOfOutlineGroupIdx < colArray.length) {
            CTCol nextInfo = colArray[endOfOutlineGroupIdx + 1];
            if (isAdjacentBefore(colArray[endOfOutlineGroupIdx], nextInfo)) {
                endLevel = nextInfo.getOutlineLevel();
                endHidden = nextInfo.getHidden();
            }
        }
        int startLevel = 0;
        boolean startHidden = false;
        int startOfOutlineGroupIdx = findStartOfColumnOutlineGroup(idx);
        if (startOfOutlineGroupIdx > 0) {
            CTCol prevInfo = colArray[startOfOutlineGroupIdx - 1];
            if (isAdjacentBefore(prevInfo, colArray[startOfOutlineGroupIdx])) {
                startLevel = prevInfo.getOutlineLevel();
                startHidden = prevInfo.getHidden();
            }
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }

    private int findColInfoIdx(int columnValue, int fromColInfoIdx) {
        CTCols cols = this.worksheet.getColsArray(0);
        if (columnValue < 0) {
            throw new IllegalArgumentException("column parameter out of range: " + columnValue);
        }
        if (fromColInfoIdx < 0) {
            throw new IllegalArgumentException("fromIdx parameter out of range: " + fromColInfoIdx);
        }
        CTCol[] colArray = cols.getColArray();
        for (int k = fromColInfoIdx; k < colArray.length; k++) {
            CTCol ci = colArray[k];
            if (containsColumn(ci, columnValue)) {
                return k;
            }
            if (ci.getMin() > fromColInfoIdx) {
                return -1;
            }
        }
        return -1;
    }

    private boolean containsColumn(CTCol col, int columnIndex) {
        return col.getMin() <= ((long) columnIndex) && ((long) columnIndex) <= col.getMax();
    }

    private boolean isColumnGroupCollapsed(int idx) {
        CTCols cols = this.worksheet.getColsArray(0);
        CTCol[] colArray = cols.getColArray();
        int endOfOutlineGroupIdx = findEndOfColumnOutlineGroup(idx);
        int nextColInfoIx = endOfOutlineGroupIdx + 1;
        if (nextColInfoIx >= colArray.length) {
            return false;
        }
        CTCol nextColInfo = colArray[nextColInfoIx];
        CTCol col = colArray[endOfOutlineGroupIdx];
        if (isAdjacentBefore(col, nextColInfo)) {
            return nextColInfo.getCollapsed();
        }
        return false;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setColumnHidden(int columnIndex, boolean hidden) {
        this.columnHelper.setColHidden(columnIndex, hidden);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setColumnWidth(int columnIndex, int width) {
        if (width > 65280) {
            throw new IllegalArgumentException("The maximum column width for an individual cell is 255 characters.");
        }
        this.columnHelper.setColWidth(columnIndex, ((double) width) / 256.0d);
        this.columnHelper.setCustomWidth(columnIndex, true);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDefaultColumnStyle(int column, CellStyle style) {
        this.columnHelper.setColDefaultStyle(column, style);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDefaultColumnWidth(int width) {
        getSheetTypeSheetFormatPr().setBaseColWidth(width);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDefaultRowHeight(short height) {
        setDefaultRowHeightInPoints(height / 20.0f);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDefaultRowHeightInPoints(float height) {
        CTSheetFormatPr pr = getSheetTypeSheetFormatPr();
        pr.setDefaultRowHeight(height);
        pr.setCustomHeight(true);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setDisplayFormulas(boolean show) {
        getSheetTypeSheetView().setShowFormulas(show);
    }

    private CTSheetView getSheetTypeSheetView() {
        if (getDefaultSheetView() == null) {
            getSheetTypeSheetViews().setSheetViewArray(0, CTSheetView.Factory.newInstance());
        }
        return getDefaultSheetView();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setFitToPage(boolean b) {
        getSheetTypePageSetUpPr().setFitToPage(b);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setHorizontallyCenter(boolean value) {
        CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setHorizontalCentered(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setVerticallyCenter(boolean value) {
        CTPrintOptions opts = this.worksheet.isSetPrintOptions() ? this.worksheet.getPrintOptions() : this.worksheet.addNewPrintOptions();
        opts.setVerticalCentered(value);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRowGroupCollapsed(int rowIndex, boolean collapse) {
        if (collapse) {
            collapseRow(rowIndex);
        } else {
            expandRow(rowIndex);
        }
    }

    private void collapseRow(int rowIndex) {
        XSSFRow row = getRow(rowIndex);
        if (row != null) {
            int startRow = findStartOfRowOutlineGroup(rowIndex);
            int lastRow = writeHidden(row, startRow, true);
            if (getRow(lastRow) != null) {
                getRow(lastRow).getCTRow().setCollapsed(true);
            } else {
                XSSFRow newRow = createRow(lastRow);
                newRow.getCTRow().setCollapsed(true);
            }
        }
    }

    private int findStartOfRowOutlineGroup(int rowIndex) {
        short level = getRow(rowIndex).getCTRow().getOutlineLevel();
        int currentRow = rowIndex;
        while (getRow(currentRow) != null) {
            if (getRow(currentRow).getCTRow().getOutlineLevel() < level) {
                return currentRow + 1;
            }
            currentRow--;
        }
        return currentRow;
    }

    private int writeHidden(XSSFRow xRow, int rowIndex, boolean hidden) {
        short level = xRow.getCTRow().getOutlineLevel();
        Iterator<Row> it = rowIterator();
        while (it.hasNext()) {
            XSSFRow xRow2 = (XSSFRow) it.next();
            if (xRow2.getRowNum() >= rowIndex && xRow2.getCTRow().getOutlineLevel() >= level) {
                xRow2.getCTRow().setHidden(hidden);
                rowIndex++;
            }
        }
        return rowIndex;
    }

    private void expandRow(int rowNumber) {
        if (rowNumber == -1) {
            return;
        }
        XSSFRow row = getRow(rowNumber);
        if (!row.getCTRow().isSetHidden()) {
            return;
        }
        int startIdx = findStartOfRowOutlineGroup(rowNumber);
        int endIdx = findEndOfRowOutlineGroup(rowNumber);
        short level = row.getCTRow().getOutlineLevel();
        if (!isRowGroupHiddenByParent(rowNumber)) {
            for (int i = startIdx; i < endIdx; i++) {
                if (level == getRow(i).getCTRow().getOutlineLevel()) {
                    getRow(i).getCTRow().unsetHidden();
                } else if (!isRowGroupCollapsed(i)) {
                    getRow(i).getCTRow().unsetHidden();
                }
            }
        }
        CTRow ctRow = getRow(endIdx).getCTRow();
        if (ctRow.getCollapsed()) {
            ctRow.unsetCollapsed();
        }
    }

    public int findEndOfRowOutlineGroup(int row) {
        short level = getRow(row).getCTRow().getOutlineLevel();
        int lastRowNum = getLastRowNum();
        int currentRow = row;
        while (currentRow < lastRowNum && getRow(currentRow) != null && getRow(currentRow).getCTRow().getOutlineLevel() >= level) {
            currentRow++;
        }
        return currentRow;
    }

    private boolean isRowGroupHiddenByParent(int row) {
        int endLevel;
        boolean endHidden;
        int startLevel;
        boolean startHidden;
        int endOfOutlineGroupIdx = findEndOfRowOutlineGroup(row);
        if (getRow(endOfOutlineGroupIdx) == null) {
            endLevel = 0;
            endHidden = false;
        } else {
            endLevel = getRow(endOfOutlineGroupIdx).getCTRow().getOutlineLevel();
            endHidden = getRow(endOfOutlineGroupIdx).getCTRow().getHidden();
        }
        int startOfOutlineGroupIdx = findStartOfRowOutlineGroup(row);
        if (startOfOutlineGroupIdx < 0 || getRow(startOfOutlineGroupIdx) == null) {
            startLevel = 0;
            startHidden = false;
        } else {
            startLevel = getRow(startOfOutlineGroupIdx).getCTRow().getOutlineLevel();
            startHidden = getRow(startOfOutlineGroupIdx).getCTRow().getHidden();
        }
        if (endLevel > startLevel) {
            return endHidden;
        }
        return startHidden;
    }

    private boolean isRowGroupCollapsed(int row) {
        int collapseRow = findEndOfRowOutlineGroup(row) + 1;
        if (getRow(collapseRow) == null) {
            return false;
        }
        return getRow(collapseRow).getCTRow().getCollapsed();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setZoom(int scale) {
        if (scale < 10 || scale > 400) {
            throw new IllegalArgumentException("Valid scale values range from 10 to 400");
        }
        getSheetTypeSheetView().setZoomScale(scale);
    }

    public void copyRows(List<? extends Row> srcRows, int destStartRow, CellCopyPolicy policy) {
        int shift;
        if (srcRows == null || srcRows.size() == 0) {
            throw new IllegalArgumentException("No rows to copy");
        }
        Row srcStartRow = srcRows.get(0);
        Row srcEndRow = srcRows.get(srcRows.size() - 1);
        if (srcStartRow == null) {
            throw new IllegalArgumentException("copyRows: First row cannot be null");
        }
        int srcStartRowNum = srcStartRow.getRowNum();
        int srcEndRowNum = srcEndRow.getRowNum();
        int size = srcRows.size();
        for (int index = 1; index < size; index++) {
            Row curRow = srcRows.get(index);
            if (curRow == null) {
                throw new IllegalArgumentException("srcRows may not contain null rows. Found null row at index " + index + ".");
            }
            if (srcStartRow.getSheet().getWorkbook() != curRow.getSheet().getWorkbook()) {
                throw new IllegalArgumentException("All rows in srcRows must belong to the same sheet in the same workbook.Expected all rows from same workbook (" + srcStartRow.getSheet().getWorkbook() + "). Got srcRows[" + index + "] from different workbook (" + curRow.getSheet().getWorkbook() + ").");
            }
            if (srcStartRow.getSheet() != curRow.getSheet()) {
                throw new IllegalArgumentException("All rows in srcRows must belong to the same sheet. Expected all rows from " + srcStartRow.getSheet().getSheetName() + ". Got srcRows[" + index + "] from " + curRow.getSheet().getSheetName());
            }
        }
        CellCopyPolicy options = new CellCopyPolicy(policy);
        options.setCopyMergedRegions(false);
        int destRowNum = destStartRow;
        for (Row srcRow : srcRows) {
            if (policy.isCondenseRows()) {
                shift = destRowNum + 1;
            } else {
                int r = srcRow.getRowNum();
                int i = destStartRow + (r - srcStartRowNum);
                shift = destRowNum;
                destRowNum = i;
            }
            XSSFRow destRow = createRow(destRowNum);
            destRow.copyRowFrom(srcRow, options);
            destRowNum = shift;
        }
        if (policy.isCopyMergedRegions()) {
            int shift2 = destStartRow - srcStartRowNum;
            for (CellRangeAddress srcRegion : srcStartRow.getSheet().getMergedRegions()) {
                if (srcStartRowNum <= srcRegion.getFirstRow() && srcRegion.getLastRow() <= srcEndRowNum) {
                    CellRangeAddress destRegion = srcRegion.copy();
                    destRegion.setFirstRow(destRegion.getFirstRow() + shift2);
                    destRegion.setLastRow(destRegion.getLastRow() + shift2);
                    addMergedRegion(destRegion);
                }
            }
        }
    }

    public void copyRows(int srcStartRow, int srcEndRow, int destStartRow, CellCopyPolicy cellCopyPolicy) {
        List<XSSFRow> srcRows = getRows(srcStartRow, srcEndRow, false);
        copyRows(srcRows, destStartRow, cellCopyPolicy);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void shiftRows(int startRow, int endRow, int n) {
        shiftRows(startRow, endRow, n, false, false);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void shiftRows(int startRow, int endRow, final int n, boolean copyRowHeight, boolean resetOriginalRowHeight) {
        Iterator<Row> it;
        int newrownum;
        CTComment[] arr$;
        int len$;
        CTShape cTShapeFindCommentShape;
        Iterator<Row> it2;
        Integer rownumI;
        XSSFVMLDrawing vml = getVMLDrawing(false);
        Iterator<Row> it3 = rowIterator();
        while (it3.hasNext()) {
            XSSFRow row = (XSSFRow) it3.next();
            int rownum = row.getRowNum();
            if (!shouldRemoveRow(startRow, endRow, n, rownum)) {
                it2 = it3;
            } else {
                Integer rownumI2 = new Integer(row.getRowNum());
                int idx = this._rows.headMap(rownumI2).size();
                this.worksheet.getSheetData().removeRow(idx);
                it3.remove();
                CommentsTable commentsTable = this.sheetComments;
                if (commentsTable == null) {
                    it2 = it3;
                } else {
                    CTComment[] arr$2 = commentsTable.getCTComments().getCommentList().getCommentArray();
                    int len$2 = arr$2.length;
                    int i$ = 0;
                    while (i$ < len$2) {
                        String strRef = arr$2[i$].getRef();
                        Iterator<Row> it4 = it3;
                        CellAddress ref = new CellAddress(strRef);
                        XSSFRow row2 = row;
                        if (ref.getRow() != rownum) {
                            rownumI = rownumI2;
                        } else {
                            this.sheetComments.removeComment(ref);
                            rownumI = rownumI2;
                            vml.removeCommentShape(ref.getRow(), ref.getColumn());
                        }
                        i$++;
                        it3 = it4;
                        row = row2;
                        rownumI2 = rownumI;
                    }
                    it2 = it3;
                }
                if (this.hyperlinks != null) {
                    for (XSSFHyperlink link : new ArrayList(this.hyperlinks)) {
                        if (new CellReference(link.getCellRef()).getRow() == rownum) {
                            this.hyperlinks.remove(link);
                        }
                    }
                }
            }
            it3 = it2;
        }
        SortedMap<XSSFComment, Integer> commentsToShift = new TreeMap<>(new Comparator<XSSFComment>() { // from class: org.apache.poi.xssf.usermodel.XSSFSheet.1
            @Override // java.util.Comparator
            public int compare(XSSFComment o1, XSSFComment o2) {
                int row1 = o1.getRow();
                int row22 = o2.getRow();
                if (row1 == row22) {
                    return o1.hashCode() - o2.hashCode();
                }
                return n > 0 ? row1 < row22 ? 1 : -1 : row1 > row22 ? 1 : -1;
            }
        });
        Iterator<Row> it5 = rowIterator();
        while (it5.hasNext()) {
            XSSFRow row3 = (XSSFRow) it5.next();
            int rownum2 = row3.getRowNum();
            if (this.sheetComments == null || (newrownum = shiftedRowNum(startRow, endRow, n, rownum2)) == rownum2) {
                it = it5;
            } else {
                CTCommentList lst = this.sheetComments.getCTComments().getCommentList();
                CTComment[] arr$3 = lst.getCommentArray();
                int len$3 = arr$3.length;
                int i$2 = 0;
                while (i$2 < len$3) {
                    CTComment comment = arr$3[i$2];
                    String oldRef = comment.getRef();
                    Iterator<Row> it6 = it5;
                    CellReference ref2 = new CellReference(oldRef);
                    CTCommentList lst2 = lst;
                    if (ref2.getRow() == rownum2) {
                        arr$ = arr$3;
                        CommentsTable commentsTable2 = this.sheetComments;
                        if (vml == null) {
                            len$ = len$3;
                            cTShapeFindCommentShape = null;
                        } else {
                            len$ = len$3;
                            int len$4 = ref2.getCol();
                            cTShapeFindCommentShape = vml.findCommentShape(rownum2, len$4);
                        }
                        XSSFComment xssfComment = new XSSFComment(commentsTable2, comment, cTShapeFindCommentShape);
                        commentsToShift.put(xssfComment, Integer.valueOf(newrownum));
                    } else {
                        arr$ = arr$3;
                        len$ = len$3;
                    }
                    i$2++;
                    it5 = it6;
                    lst = lst2;
                    arr$3 = arr$;
                    len$3 = len$;
                }
                it = it5;
            }
            if (rownum2 < startRow) {
                it5 = it;
            } else if (rownum2 > endRow) {
                it5 = it;
            } else {
                if (!copyRowHeight) {
                    row3.setHeight((short) -1);
                }
                row3.shift(n);
                it5 = it;
            }
        }
        for (Map.Entry<XSSFComment, Integer> entry : commentsToShift.entrySet()) {
            entry.getKey().setRow(entry.getValue().intValue());
        }
        XSSFRowShifter rowShifter = new XSSFRowShifter(this);
        int sheetIndex = getWorkbook().getSheetIndex(this);
        String sheetName = getWorkbook().getSheetName(sheetIndex);
        FormulaShifter shifter = FormulaShifter.createForRowShift(sheetIndex, sheetName, startRow, endRow, n, SpreadsheetVersion.EXCEL2007);
        rowShifter.updateNamedRanges(shifter);
        rowShifter.updateFormulas(shifter);
        rowShifter.shiftMergedRegions(startRow, endRow, n);
        rowShifter.updateConditionalFormatting(shifter);
        rowShifter.updateHyperlinks(shifter);
        Map<Integer, XSSFRow> map = new HashMap<>();
        for (XSSFRow r : this._rows.values()) {
            map.put(new Integer(r.getRowNum()), r);
        }
        this._rows.clear();
        this._rows.putAll(map);
    }

    private int shiftedRowNum(int startRow, int endRow, int n, int rownum) {
        if (rownum < startRow && (n > 0 || startRow - rownum > n)) {
            return rownum;
        }
        if (rownum > endRow && (n < 0 || rownum - endRow > n)) {
            return rownum;
        }
        if (rownum < startRow) {
            return (endRow - startRow) + rownum;
        }
        if (rownum > endRow) {
            return rownum - (endRow - startRow);
        }
        return rownum + n;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void showInPane(int toprow, int leftcol) {
        CellReference cellReference = new CellReference(toprow, leftcol);
        String cellRef = cellReference.formatAsString();
        getPane().setTopLeftCell(cellRef);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void ungroupColumn(int fromColumn, int toColumn) {
        CTCols cols = this.worksheet.getColsArray(0);
        int index = fromColumn;
        while (index <= toColumn) {
            CTCol col = this.columnHelper.getColumn(index, false);
            if (col != null) {
                short outlineLevel = col.getOutlineLevel();
                col.setOutlineLevel((short) (outlineLevel - 1));
                index = (int) col.getMax();
                if (col.getOutlineLevel() <= 0) {
                    int colIndex = this.columnHelper.getIndexOfColumn(cols, col);
                    this.worksheet.getColsArray(0).removeCol(colIndex);
                }
            }
            index++;
        }
        this.worksheet.setColsArray(0, cols);
        setSheetFormatPrOutlineLevelCol();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void ungroupRow(int fromRow, int toRow) {
        for (int i = fromRow; i <= toRow; i++) {
            XSSFRow xrow = getRow(i);
            if (xrow != null) {
                CTRow ctRow = xrow.getCTRow();
                int outlineLevel = ctRow.getOutlineLevel();
                ctRow.setOutlineLevel((short) (outlineLevel - 1));
                if (outlineLevel == 1 && xrow.getFirstCellNum() == -1) {
                    removeRow(xrow);
                }
            }
        }
        setSheetFormatPrOutlineLevelRow();
    }

    private void setSheetFormatPrOutlineLevelRow() {
        short maxLevelRow = getMaxOutlineLevelRows();
        getSheetTypeSheetFormatPr().setOutlineLevelRow(maxLevelRow);
    }

    private void setSheetFormatPrOutlineLevelCol() {
        short maxLevelCol = getMaxOutlineLevelCols();
        getSheetTypeSheetFormatPr().setOutlineLevelCol(maxLevelCol);
    }

    private CTSheetViews getSheetTypeSheetViews() {
        if (this.worksheet.getSheetViews() == null) {
            this.worksheet.setSheetViews(CTSheetViews.Factory.newInstance());
            this.worksheet.getSheetViews().addNewSheetView();
        }
        return this.worksheet.getSheetViews();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public boolean isSelected() {
        CTSheetView view = getDefaultSheetView();
        return view != null && view.getTabSelected();
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setSelected(boolean value) {
        CTSheetViews views = getSheetTypeSheetViews();
        CTSheetView[] arr$ = views.getSheetViewArray();
        for (CTSheetView view : arr$) {
            view.setTabSelected(value);
        }
    }

    @Internal
    public void addHyperlink(XSSFHyperlink hyperlink) {
        this.hyperlinks.add(hyperlink);
    }

    @Internal
    public void removeHyperlink(int row, int column) {
        String ref = new CellReference(row, column).formatAsString();
        Iterator<XSSFHyperlink> it = this.hyperlinks.iterator();
        while (it.hasNext()) {
            XSSFHyperlink hyperlink = it.next();
            if (hyperlink.getCellRef().equals(ref)) {
                it.remove();
                return;
            }
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellAddress getActiveCell() {
        String address = getSheetTypeSelection().getActiveCell();
        if (address == null) {
            return null;
        }
        return new CellAddress(address);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setActiveCell(CellAddress address) {
        String ref = address.formatAsString();
        CTSelection ctsel = getSheetTypeSelection();
        ctsel.setActiveCell(ref);
        ctsel.setSqref(Arrays.asList(ref));
    }

    public boolean hasComments() {
        CommentsTable commentsTable = this.sheetComments;
        return commentsTable != null && commentsTable.getNumberOfComments() > 0;
    }

    protected int getNumberOfComments() {
        CommentsTable commentsTable = this.sheetComments;
        if (commentsTable == null) {
            return 0;
        }
        return commentsTable.getNumberOfComments();
    }

    private CTSelection getSheetTypeSelection() {
        if (getSheetTypeSheetView().sizeOfSelectionArray() == 0) {
            getSheetTypeSheetView().insertNewSelection(0);
        }
        return getSheetTypeSheetView().getSelectionArray(0);
    }

    private CTSheetView getDefaultSheetView() {
        int sz;
        CTSheetViews views = getSheetTypeSheetViews();
        if (views == null || (sz = views.sizeOfSheetViewArray()) == 0) {
            return null;
        }
        return views.getSheetViewArray(sz - 1);
    }

    protected CommentsTable getCommentsTable(boolean create) {
        if (this.sheetComments == null && create) {
            try {
                this.sheetComments = (CommentsTable) createRelationship(XSSFRelation.SHEET_COMMENTS, XSSFFactory.getInstance(), (int) this.sheet.getSheetId());
            } catch (PartAlreadyExistsException e) {
                this.sheetComments = (CommentsTable) createRelationship(XSSFRelation.SHEET_COMMENTS, XSSFFactory.getInstance(), -1);
            }
        }
        return this.sheetComments;
    }

    private CTPageSetUpPr getSheetTypePageSetUpPr() {
        CTSheetPr sheetPr = getSheetTypeSheetPr();
        return sheetPr.isSetPageSetUpPr() ? sheetPr.getPageSetUpPr() : sheetPr.addNewPageSetUpPr();
    }

    private static boolean shouldRemoveRow(int startRow, int endRow, int n, int rownum) {
        if (rownum < startRow + n || rownum > endRow + n) {
            return false;
        }
        if (n > 0 && rownum > endRow) {
            return true;
        }
        if (n < 0 && rownum < startRow) {
            return true;
        }
        return false;
    }

    private CTPane getPane() {
        if (getDefaultSheetView().getPane() == null) {
            getDefaultSheetView().addNewPane();
        }
        return getDefaultSheetView().getPane();
    }

    @Internal
    public CTCellFormula getSharedFormula(int sid) {
        return this.sharedFormulas.get(Integer.valueOf(sid));
    }

    void onReadCell(XSSFCell cell) {
        CTCell ct = cell.getCTCell();
        CTCellFormula f = ct.getF();
        if (f != null && f.getT() == STCellFormulaType.SHARED && f.isSetRef() && f.getStringValue() != null) {
            CTCellFormula sf = f.copy();
            CellRangeAddress sfRef = CellRangeAddress.valueOf(sf.getRef());
            CellReference cellRef = new CellReference(cell);
            if (cellRef.getCol() > sfRef.getFirstColumn() || cellRef.getRow() > sfRef.getFirstRow()) {
                String effectiveRef = new CellRangeAddress(Math.max(cellRef.getRow(), sfRef.getFirstRow()), sfRef.getLastRow(), Math.max((int) cellRef.getCol(), sfRef.getFirstColumn()), sfRef.getLastColumn()).formatAsString();
                sf.setRef(effectiveRef);
            }
            this.sharedFormulas.put(Integer.valueOf((int) f.getSi()), sf);
        }
        if (f != null && f.getT() == STCellFormulaType.ARRAY && f.getRef() != null) {
            this.arrayFormulas.add(CellRangeAddress.valueOf(f.getRef()));
        }
    }

    @Override // org.apache.poi.POIXMLDocumentPart
    protected void commit() throws IOException {
        PackagePart part = getPackagePart();
        OutputStream out = part.getOutputStream();
        write(out);
        out.close();
    }

    protected void write(OutputStream out) throws IOException {
        boolean setToNull = false;
        if (this.worksheet.sizeOfColsArray() == 1) {
            CTCols col = this.worksheet.getColsArray(0);
            if (col.sizeOfColArray() == 0) {
                setToNull = true;
                this.worksheet.setColsArray((CTCols[]) null);
            } else {
                setColWidthAttribute(col);
            }
        }
        if (this.hyperlinks.size() > 0) {
            if (this.worksheet.getHyperlinks() == null) {
                this.worksheet.addNewHyperlinks();
            }
            CTHyperlink[] ctHls = new CTHyperlink[this.hyperlinks.size()];
            for (int i = 0; i < ctHls.length; i++) {
                XSSFHyperlink hyperlink = this.hyperlinks.get(i);
                hyperlink.generateRelationIfNeeded(getPackagePart());
                ctHls[i] = hyperlink.getCTHyperlink();
            }
            this.worksheet.getHyperlinks().setHyperlinkArray(ctHls);
        } else if (this.worksheet.getHyperlinks() != null) {
            int count = this.worksheet.getHyperlinks().sizeOfHyperlinkArray();
            for (int i2 = count - 1; i2 >= 0; i2--) {
                this.worksheet.getHyperlinks().removeHyperlink(i2);
            }
            this.worksheet.unsetHyperlinks();
        }
        int minCell = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int maxCell = Integer.MIN_VALUE;
        for (XSSFRow row : this._rows.values()) {
            row.onDocumentWrite();
            if (row.getFirstCellNum() != -1) {
                minCell = Math.min(minCell, (int) row.getFirstCellNum());
            }
            if (row.getLastCellNum() != -1) {
                maxCell = Math.max(maxCell, (int) row.getLastCellNum());
            }
        }
        if (minCell != Integer.MAX_VALUE) {
            String ref = new CellRangeAddress(getFirstRowNum(), getLastRowNum(), minCell, maxCell).formatAsString();
            if (this.worksheet.isSetDimension()) {
                this.worksheet.getDimension().setRef(ref);
            } else {
                this.worksheet.addNewDimension().setRef(ref);
            }
        }
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTWorksheet.type.getName().getNamespaceURI(), "worksheet"));
        this.worksheet.save(out, xmlOptions);
        if (setToNull) {
            this.worksheet.addNewCols();
        }
    }

    public boolean isAutoFilterLocked() {
        return isSheetLocked() && safeGetProtectionField().getAutoFilter();
    }

    public boolean isDeleteColumnsLocked() {
        return isSheetLocked() && safeGetProtectionField().getDeleteColumns();
    }

    public boolean isDeleteRowsLocked() {
        return isSheetLocked() && safeGetProtectionField().getDeleteRows();
    }

    public boolean isFormatCellsLocked() {
        return isSheetLocked() && safeGetProtectionField().getFormatCells();
    }

    public boolean isFormatColumnsLocked() {
        return isSheetLocked() && safeGetProtectionField().getFormatColumns();
    }

    public boolean isFormatRowsLocked() {
        return isSheetLocked() && safeGetProtectionField().getFormatRows();
    }

    public boolean isInsertColumnsLocked() {
        return isSheetLocked() && safeGetProtectionField().getInsertColumns();
    }

    public boolean isInsertHyperlinksLocked() {
        return isSheetLocked() && safeGetProtectionField().getInsertHyperlinks();
    }

    public boolean isInsertRowsLocked() {
        return isSheetLocked() && safeGetProtectionField().getInsertRows();
    }

    public boolean isPivotTablesLocked() {
        return isSheetLocked() && safeGetProtectionField().getPivotTables();
    }

    public boolean isSortLocked() {
        return isSheetLocked() && safeGetProtectionField().getSort();
    }

    public boolean isObjectsLocked() {
        return isSheetLocked() && safeGetProtectionField().getObjects();
    }

    public boolean isScenariosLocked() {
        return isSheetLocked() && safeGetProtectionField().getScenarios();
    }

    public boolean isSelectLockedCellsLocked() {
        return isSheetLocked() && safeGetProtectionField().getSelectLockedCells();
    }

    public boolean isSelectUnlockedCellsLocked() {
        return isSheetLocked() && safeGetProtectionField().getSelectUnlockedCells();
    }

    public boolean isSheetLocked() {
        return this.worksheet.isSetSheetProtection() && safeGetProtectionField().getSheet();
    }

    public void enableLocking() {
        safeGetProtectionField().setSheet(true);
    }

    public void disableLocking() {
        safeGetProtectionField().setSheet(false);
    }

    public void lockAutoFilter(boolean enabled) {
        safeGetProtectionField().setAutoFilter(enabled);
    }

    public void lockDeleteColumns(boolean enabled) {
        safeGetProtectionField().setDeleteColumns(enabled);
    }

    public void lockDeleteRows(boolean enabled) {
        safeGetProtectionField().setDeleteRows(enabled);
    }

    public void lockFormatCells(boolean enabled) {
        safeGetProtectionField().setFormatCells(enabled);
    }

    public void lockFormatColumns(boolean enabled) {
        safeGetProtectionField().setFormatColumns(enabled);
    }

    public void lockFormatRows(boolean enabled) {
        safeGetProtectionField().setFormatRows(enabled);
    }

    public void lockInsertColumns(boolean enabled) {
        safeGetProtectionField().setInsertColumns(enabled);
    }

    public void lockInsertHyperlinks(boolean enabled) {
        safeGetProtectionField().setInsertHyperlinks(enabled);
    }

    public void lockInsertRows(boolean enabled) {
        safeGetProtectionField().setInsertRows(enabled);
    }

    public void lockPivotTables(boolean enabled) {
        safeGetProtectionField().setPivotTables(enabled);
    }

    public void lockSort(boolean enabled) {
        safeGetProtectionField().setSort(enabled);
    }

    public void lockObjects(boolean enabled) {
        safeGetProtectionField().setObjects(enabled);
    }

    public void lockScenarios(boolean enabled) {
        safeGetProtectionField().setScenarios(enabled);
    }

    public void lockSelectLockedCells(boolean enabled) {
        safeGetProtectionField().setSelectLockedCells(enabled);
    }

    public void lockSelectUnlockedCells(boolean enabled) {
        safeGetProtectionField().setSelectUnlockedCells(enabled);
    }

    private CTSheetProtection safeGetProtectionField() {
        if (!isSheetProtectionEnabled()) {
            return this.worksheet.addNewSheetProtection();
        }
        return this.worksheet.getSheetProtection();
    }

    boolean isSheetProtectionEnabled() {
        return this.worksheet.isSetSheetProtection();
    }

    boolean isCellInArrayFormulaContext(XSSFCell cell) {
        for (CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }

    XSSFCell getFirstCellInArrayFormula(XSSFCell cell) {
        for (CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return getRow(range.getFirstRow()).getCell(range.getFirstColumn());
            }
        }
        return null;
    }

    private CellRange<XSSFCell> getCellRange(CellRangeAddress range) {
        int firstRow = range.getFirstRow();
        int firstColumn = range.getFirstColumn();
        int lastRow = range.getLastRow();
        int lastColumn = range.getLastColumn();
        int height = (lastRow - firstRow) + 1;
        int width = (lastColumn - firstColumn) + 1;
        List<XSSFCell> temp = new ArrayList<>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; rowIn++) {
            for (int colIn = firstColumn; colIn <= lastColumn; colIn++) {
                XSSFRow row = getRow(rowIn);
                if (row == null) {
                    row = createRow(rowIn);
                }
                XSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return SSCellRange.create(firstRow, firstColumn, height, width, temp, XSSFCell.class);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellRange<XSSFCell> setArrayFormula(String formula, CellRangeAddress range) {
        CellRange<XSSFCell> cr = getCellRange(range);
        XSSFCell mainArrayFormulaCell = (XSSFCell) cr.getTopLeftCell();
        mainArrayFormulaCell.setCellArrayFormula(formula, range);
        this.arrayFormulas.add(range);
        return cr;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellRange<XSSFCell> removeArrayFormula(Cell cell) {
        if (cell.getSheet() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
        }
        for (CellRangeAddress range : this.arrayFormulas) {
            if (range.isInRange(cell)) {
                this.arrayFormulas.remove(range);
                CellRange<XSSFCell> cr = getCellRange(range);
                for (XSSFCell c : cr) {
                    c.setCellType(CellType.BLANK);
                }
                return cr;
            }
        }
        String ref = ((XSSFCell) cell).getCTCell().getR();
        throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public DataValidationHelper getDataValidationHelper() {
        return this.dataValidationHelper;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public List<XSSFDataValidation> getDataValidations() {
        List<XSSFDataValidation> xssfValidations = new ArrayList<>();
        CTDataValidations dataValidations = this.worksheet.getDataValidations();
        if (dataValidations != null && dataValidations.getCount() > 0) {
            CTDataValidation[] arr$ = dataValidations.getDataValidationArray();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                CTDataValidation ctDataValidation = arr$[i$];
                CellRangeAddressList addressList = new CellRangeAddressList();
                List<String> sqref = ctDataValidation.getSqref();
                Iterator<String> it = sqref.iterator();
                while (it.hasNext()) {
                    String stRef = it.next();
                    String[] regions = stRef.split(" ");
                    int len$2 = regions.length;
                    int i$2 = 0;
                    while (i$2 < len$2) {
                        String region = regions[i$2];
                        String[] parts = region.split(":");
                        CTDataValidations dataValidations2 = dataValidations;
                        CTDataValidation[] arr$2 = arr$;
                        CellReference begin = new CellReference(parts[0]);
                        int len$3 = len$;
                        CellReference end = parts.length > 1 ? new CellReference(parts[1]) : begin;
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(begin.getRow(), end.getRow(), begin.getCol(), end.getCol());
                        addressList.addCellRangeAddress(cellRangeAddress);
                        i$2++;
                        dataValidations = dataValidations2;
                        len$ = len$3;
                        arr$ = arr$2;
                        sqref = sqref;
                        it = it;
                    }
                }
                XSSFDataValidation xssfDataValidation = new XSSFDataValidation(addressList, ctDataValidation);
                xssfValidations.add(xssfDataValidation);
            }
        }
        return xssfValidations;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void addValidationData(DataValidation dataValidation) {
        XSSFDataValidation xssfDataValidation = (XSSFDataValidation) dataValidation;
        CTDataValidations dataValidations = this.worksheet.getDataValidations();
        if (dataValidations == null) {
            dataValidations = this.worksheet.addNewDataValidations();
        }
        int currentCount = dataValidations.sizeOfDataValidationArray();
        CTDataValidation newval = dataValidations.addNewDataValidation();
        newval.set(xssfDataValidation.getCtDdataValidation());
        dataValidations.setCount(currentCount + 1);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFAutoFilter setAutoFilter(CellRangeAddress range) {
        CTAutoFilter af = this.worksheet.getAutoFilter();
        if (af == null) {
            af = this.worksheet.addNewAutoFilter();
        }
        CellRangeAddress norm = new CellRangeAddress(range.getFirstRow(), range.getLastRow(), range.getFirstColumn(), range.getLastColumn());
        String ref = norm.formatAsString();
        af.setRef(ref);
        XSSFWorkbook wb = getWorkbook();
        int sheetIndex = getWorkbook().getSheetIndex(this);
        XSSFName name = wb.getBuiltInName(XSSFName.BUILTIN_FILTER_DB, sheetIndex);
        if (name == null) {
            name = wb.createBuiltInName(XSSFName.BUILTIN_FILTER_DB, sheetIndex);
        }
        name.getCTName().setHidden(true);
        CellReference r1 = new CellReference(getSheetName(), range.getFirstRow(), range.getFirstColumn(), true, true);
        CellReference r2 = new CellReference(null, range.getLastRow(), range.getLastColumn(), true, true);
        String fmla = r1.formatAsString() + ":" + r2.formatAsString();
        name.setRefersToFormula(fmla);
        return new XSSFAutoFilter(this);
    }

    public XSSFTable createTable() {
        if (!this.worksheet.isSetTableParts()) {
            this.worksheet.addNewTableParts();
        }
        CTTableParts tblParts = this.worksheet.getTableParts();
        CTTablePart tbl = tblParts.addNewTablePart();
        int tableNumber = getPackagePart().getPackage().getPartsByContentType(XSSFRelation.TABLE.getContentType()).size() + 1;
        POIXMLDocumentPart.RelationPart rp = createRelationship(XSSFRelation.TABLE, XSSFFactory.getInstance(), tableNumber, false);
        XSSFTable table = (XSSFTable) rp.getDocumentPart();
        tbl.setId(rp.getRelationship().getId());
        table.getCTTable().setId(tableNumber);
        this.tables.put(tbl.getId(), table);
        return table;
    }

    public List<XSSFTable> getTables() {
        return new ArrayList(this.tables.values());
    }

    public void removeTable(XSSFTable t) {
        long id = t.getCTTable().getId();
        Map.Entry<String, XSSFTable> toDelete = null;
        for (Map.Entry<String, XSSFTable> entry : this.tables.entrySet()) {
            if (entry.getValue().getCTTable().getId() == id) {
                toDelete = entry;
            }
        }
        if (toDelete != null) {
            removeRelation(getRelationById(toDelete.getKey()), true);
            this.tables.remove(toDelete.getKey());
            toDelete.getValue().onTableDelete();
        }
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public XSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new XSSFSheetConditionalFormatting(this);
    }

    public XSSFColor getTabColor() {
        CTSheetPr pr = this.worksheet.getSheetPr();
        if (pr == null) {
            pr = this.worksheet.addNewSheetPr();
        }
        if (!pr.isSetTabColor()) {
            return null;
        }
        return new XSSFColor(pr.getTabColor(), getWorkbook().getStylesSource().getIndexedColors());
    }

    public void setTabColor(XSSFColor color) {
        CTSheetPr pr = this.worksheet.getSheetPr();
        if (pr == null) {
            pr = this.worksheet.addNewSheetPr();
        }
        pr.setTabColor(color.getCTColor());
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellRangeAddress getRepeatingRows() {
        return getRepeatingRowsOrColums(true);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public CellRangeAddress getRepeatingColumns() {
        return getRepeatingRowsOrColums(false);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRepeatingRows(CellRangeAddress rowRangeRef) {
        CellRangeAddress columnRangeRef = getRepeatingColumns();
        setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public void setRepeatingColumns(CellRangeAddress columnRangeRef) {
        CellRangeAddress rowRangeRef = getRepeatingRows();
        setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }

    private void setRepeatingRowsAndColumns(CellRangeAddress rowDef, CellRangeAddress colDef) {
        int col1 = -1;
        int col2 = -1;
        int row1 = -1;
        int row2 = -1;
        if (rowDef != null) {
            row1 = rowDef.getFirstRow();
            row2 = rowDef.getLastRow();
            if ((row1 == -1 && row2 != -1) || row1 < -1 || row2 < -1 || row1 > row2) {
                throw new IllegalArgumentException("Invalid row range specification");
            }
        }
        if (colDef != null) {
            col1 = colDef.getFirstColumn();
            col2 = colDef.getLastColumn();
            if ((col1 == -1 && col2 != -1) || col1 < -1 || col2 < -1 || col1 > col2) {
                throw new IllegalArgumentException("Invalid column range specification");
            }
        }
        int sheetIndex = getWorkbook().getSheetIndex(this);
        boolean removeAll = rowDef == null && colDef == null;
        XSSFName name = getWorkbook().getBuiltInName(XSSFName.BUILTIN_PRINT_TITLE, sheetIndex);
        if (removeAll) {
            if (name != null) {
                getWorkbook().removeName(name);
                return;
            }
            return;
        }
        if (name == null) {
            name = getWorkbook().createBuiltInName(XSSFName.BUILTIN_PRINT_TITLE, sheetIndex);
        }
        String reference = getReferenceBuiltInRecord(name.getSheetName(), col1, col2, row1, row2);
        name.setRefersToFormula(reference);
        if (!this.worksheet.isSetPageSetup() || !this.worksheet.isSetPageMargins()) {
            getPrintSetup().setValidSettings(false);
        }
    }

    private static String getReferenceBuiltInRecord(String sheetName, int startC, int endC, int startR, int endR) {
        CellReference colRef = new CellReference(sheetName, 0, startC, true, true);
        CellReference colRef2 = new CellReference(sheetName, 0, endC, true, true);
        CellReference rowRef = new CellReference(sheetName, startR, 0, true, true);
        CellReference rowRef2 = new CellReference(sheetName, endR, 0, true, true);
        String escapedName = SheetNameFormatter.format(sheetName);
        String c = "";
        String r = "";
        if (startC != -1 || endC != -1) {
            String col1 = colRef.getCellRefParts()[2];
            String col2 = colRef2.getCellRefParts()[2];
            c = escapedName + "!$" + col1 + ":$" + col2;
        }
        if (startR != -1 || endR != -1) {
            String row1 = rowRef.getCellRefParts()[1];
            String row2 = rowRef2.getCellRefParts()[1];
            if (!row1.equals("0") && !row2.equals("0")) {
                r = escapedName + "!$" + row1 + ":$" + row2;
            }
        }
        StringBuilder rng = new StringBuilder();
        rng.append(c);
        if (rng.length() > 0 && r.length() > 0) {
            rng.append(',');
        }
        rng.append(r);
        return rng.toString();
    }

    private CellRangeAddress getRepeatingRowsOrColums(boolean rows) {
        String refStr;
        int sheetIndex = getWorkbook().getSheetIndex(this);
        XSSFName name = getWorkbook().getBuiltInName(XSSFName.BUILTIN_PRINT_TITLE, sheetIndex);
        if (name == null || (refStr = name.getRefersToFormula()) == null) {
            return null;
        }
        String[] parts = refStr.split(",");
        int maxRowIndex = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        int maxColIndex = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        for (String part : parts) {
            CellRangeAddress range = CellRangeAddress.valueOf(part);
            if ((range.getFirstColumn() == 0 && range.getLastColumn() == maxColIndex) || (range.getFirstColumn() == -1 && range.getLastColumn() == -1)) {
                if (rows) {
                    return range;
                }
            } else if (((range.getFirstRow() == 0 && range.getLastRow() == maxRowIndex) || (range.getFirstRow() == -1 && range.getLastRow() == -1)) && !rows) {
                return range;
            }
        }
        return null;
    }

    private XSSFPivotTable createPivotTable() {
        XSSFWorkbook wb = getWorkbook();
        List<XSSFPivotTable> pivotTables = wb.getPivotTables();
        int tableId = getWorkbook().getPivotTables().size() + 1;
        XSSFPivotTable pivotTable = (XSSFPivotTable) createRelationship(XSSFRelation.PIVOT_TABLE, XSSFFactory.getInstance(), tableId);
        pivotTable.setParentSheet(this);
        pivotTables.add(pivotTable);
        XSSFWorkbook workbook = getWorkbook();
        XSSFPivotCacheDefinition pivotCacheDefinition = (XSSFPivotCacheDefinition) workbook.createRelationship(XSSFRelation.PIVOT_CACHE_DEFINITION, XSSFFactory.getInstance(), tableId);
        String rId = workbook.getRelationId(pivotCacheDefinition);
        PackagePart pivotPackagePart = pivotTable.getPackagePart();
        pivotPackagePart.addRelationship(pivotCacheDefinition.getPackagePart().getPartName(), TargetMode.INTERNAL, XSSFRelation.PIVOT_CACHE_DEFINITION.getRelation());
        pivotTable.setPivotCacheDefinition(pivotCacheDefinition);
        pivotTable.setPivotCache(new XSSFPivotCache(workbook.addPivotCache(rId)));
        XSSFPivotCacheRecords pivotCacheRecords = (XSSFPivotCacheRecords) pivotCacheDefinition.createRelationship(XSSFRelation.PIVOT_CACHE_RECORDS, XSSFFactory.getInstance(), tableId);
        pivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition().setId(pivotCacheDefinition.getRelationId(pivotCacheRecords));
        wb.setPivotTables(pivotTables);
        return pivotTable;
    }

    public XSSFPivotTable createPivotTable(final AreaReference source, CellReference position, Sheet sourceSheet) {
        String sourceSheetName = source.getFirstCell().getSheetName();
        if (sourceSheetName != null && !sourceSheetName.equalsIgnoreCase(sourceSheet.getSheetName())) {
            throw new IllegalArgumentException("The area is referenced in another sheet than the defined source sheet " + sourceSheet.getSheetName() + ".");
        }
        return createPivotTable(position, sourceSheet, new XSSFPivotTable.PivotTableReferenceConfigurator() { // from class: org.apache.poi.xssf.usermodel.XSSFSheet.2
            @Override // org.apache.poi.xssf.usermodel.XSSFPivotTable.PivotTableReferenceConfigurator
            public void configureReference(CTWorksheetSource wsSource) {
                String[] firstCell = source.getFirstCell().getCellRefParts();
                String firstRow = firstCell[1];
                String firstCol = firstCell[2];
                String[] lastCell = source.getLastCell().getCellRefParts();
                String lastRow = lastCell[1];
                String lastCol = lastCell[2];
                String ref = firstCol + firstRow + ':' + lastCol + lastRow;
                wsSource.setRef(ref);
            }
        });
    }

    private XSSFPivotTable createPivotTable(CellReference position, Sheet sourceSheet, XSSFPivotTable.PivotTableReferenceConfigurator refConfig) {
        XSSFPivotTable pivotTable = createPivotTable();
        pivotTable.setDefaultPivotTableDefinition();
        pivotTable.createSourceReferences(position, sourceSheet, refConfig);
        pivotTable.getPivotCacheDefinition().createCacheFields(sourceSheet);
        pivotTable.createDefaultDataColumns();
        return pivotTable;
    }

    public XSSFPivotTable createPivotTable(AreaReference source, CellReference position) {
        String sourceSheetName = source.getFirstCell().getSheetName();
        if (sourceSheetName != null && !sourceSheetName.equalsIgnoreCase(getSheetName())) {
            XSSFSheet sourceSheet = getWorkbook().getSheet(sourceSheetName);
            return createPivotTable(source, position, sourceSheet);
        }
        return createPivotTable(source, position, this);
    }

    public XSSFPivotTable createPivotTable(final Name source, CellReference position, Sheet sourceSheet) {
        if (source.getSheetName() != null && !source.getSheetName().equals(sourceSheet.getSheetName())) {
            throw new IllegalArgumentException("The named range references another sheet than the defined source sheet " + sourceSheet.getSheetName() + ".");
        }
        return createPivotTable(position, sourceSheet, new XSSFPivotTable.PivotTableReferenceConfigurator() { // from class: org.apache.poi.xssf.usermodel.XSSFSheet.3
            @Override // org.apache.poi.xssf.usermodel.XSSFPivotTable.PivotTableReferenceConfigurator
            public void configureReference(CTWorksheetSource wsSource) {
                wsSource.setName(source.getNameName());
            }
        });
    }

    public XSSFPivotTable createPivotTable(Name source, CellReference position) {
        return createPivotTable(source, position, getWorkbook().getSheet(source.getSheetName()));
    }

    public XSSFPivotTable createPivotTable(final Table source, CellReference position) {
        return createPivotTable(position, getWorkbook().getSheet(source.getSheetName()), new XSSFPivotTable.PivotTableReferenceConfigurator() { // from class: org.apache.poi.xssf.usermodel.XSSFSheet.4
            @Override // org.apache.poi.xssf.usermodel.XSSFPivotTable.PivotTableReferenceConfigurator
            public void configureReference(CTWorksheetSource wsSource) {
                wsSource.setName(source.getName());
            }
        });
    }

    public List<XSSFPivotTable> getPivotTables() {
        List<XSSFPivotTable> tables = new ArrayList<>();
        for (XSSFPivotTable table : getWorkbook().getPivotTables()) {
            if (table.getParent() == this) {
                tables.add(table);
            }
        }
        return tables;
    }

    @Override // org.apache.poi.ss.usermodel.Sheet
    public int getColumnOutlineLevel(int columnIndex) {
        CTCol col = this.columnHelper.getColumn(columnIndex, false);
        if (col == null) {
            return 0;
        }
        return col.getOutlineLevel();
    }

    public void addIgnoredErrors(CellReference cell, IgnoredErrorType... ignoredErrorTypes) {
        addIgnoredErrors(cell.formatAsString(), ignoredErrorTypes);
    }

    public void addIgnoredErrors(CellRangeAddress region, IgnoredErrorType... ignoredErrorTypes) {
        region.validate(SpreadsheetVersion.EXCEL2007);
        addIgnoredErrors(region.formatAsString(), ignoredErrorTypes);
    }

    public Map<IgnoredErrorType, Set<CellRangeAddress>> getIgnoredErrors() {
        Map<IgnoredErrorType, Set<CellRangeAddress>> result = new LinkedHashMap<>();
        if (this.worksheet.isSetIgnoredErrors()) {
            for (CTIgnoredError err : this.worksheet.getIgnoredErrors().getIgnoredErrorList()) {
                for (IgnoredErrorType errType : XSSFIgnoredErrorHelper.getErrorTypes(err)) {
                    if (!result.containsKey(errType)) {
                        result.put(errType, new LinkedHashSet<>());
                    }
                    for (Object ref : err.getSqref()) {
                        result.get(errType).add(CellRangeAddress.valueOf(ref.toString()));
                    }
                }
            }
        }
        return result;
    }

    private void addIgnoredErrors(String ref, IgnoredErrorType... ignoredErrorTypes) {
        CTIgnoredErrors ctIgnoredErrors = this.worksheet.isSetIgnoredErrors() ? this.worksheet.getIgnoredErrors() : this.worksheet.addNewIgnoredErrors();
        CTIgnoredError ctIgnoredError = ctIgnoredErrors.addNewIgnoredError();
        XSSFIgnoredErrorHelper.addIgnoredErrors(ctIgnoredError, ref, ignoredErrorTypes);
    }

    protected void onSheetDelete() {
        for (POIXMLDocumentPart.RelationPart part : getRelationParts()) {
            if (part.getDocumentPart() instanceof XSSFTable) {
                removeTable((XSSFTable) part.getDocumentPart());
            } else {
                removeRelation(part.getDocumentPart(), true);
            }
        }
    }

    protected CTOleObject readOleObject(long shapeId) {
        CTOleObject coo;
        if (!getCTWorksheet().isSetOleObjects()) {
            return null;
        }
        XmlCursor cur = getCTWorksheet().getOleObjects().newCursor();
        try {
            cur.selectPath("declare namespace p='http://schemas.openxmlformats.org/spreadsheetml/2006/main' .//p:oleObject");
            coo = null;
        } finally {
        }
        while (cur.toNextSelection()) {
            String sId = cur.getAttributeText(new QName(null, "shapeId"));
            if (sId != null && Long.parseLong(sId) == shapeId) {
                XmlObject xObj = cur.getObject();
                if (!(xObj instanceof CTOleObject)) {
                    XMLStreamReader reader = cur.newXMLStreamReader();
                    try {
                        try {
                            CTOleObjects coos = CTOleObjects.Factory.parse(reader);
                            if (coos.sizeOfOleObjectArray() == 0) {
                                try {
                                    reader.close();
                                } catch (XMLStreamException e) {
                                    logger.log(3, "can't close reader", e);
                                }
                            } else {
                                coo = coos.getOleObjectArray(0);
                                try {
                                    reader.close();
                                } catch (XMLStreamException e2) {
                                    logger.log(3, "can't close reader", e2);
                                }
                            }
                        } catch (XmlException e3) {
                            logger.log(3, "can't parse CTOleObjects", e3);
                            try {
                                reader.close();
                            } catch (XMLStreamException e4) {
                                logger.log(3, "can't close reader", e4);
                            }
                        }
                    } catch (Throwable th) {
                        try {
                            reader.close();
                            throw th;
                        } catch (XMLStreamException e5) {
                            logger.log(3, "can't close reader", e5);
                            throw th;
                        }
                    }
                    cur.dispose();
                }
                coo = (CTOleObject) xObj;
                if (cur.toChild(XSSFRelation.NS_SPREADSHEETML, "objectPr")) {
                    break;
                }
            }
        }
        return coo != null ? coo : null;
    }
}
