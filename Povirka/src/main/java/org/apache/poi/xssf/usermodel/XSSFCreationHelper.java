package org.apache.poi.xssf.usermodel;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

/* JADX INFO: loaded from: classes.dex */
public class XSSFCreationHelper implements CreationHelper {
    private final XSSFWorkbook workbook;

    @Internal
    public XSSFCreationHelper(XSSFWorkbook wb) {
        this.workbook = wb;
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFRichTextString createRichTextString(String text) {
        XSSFRichTextString rt = new XSSFRichTextString(text);
        rt.setStylesTableReference(this.workbook.getStylesSource());
        return rt;
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFDataFormat createDataFormat() {
        return this.workbook.createDataFormat();
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFColor createExtendedColor() {
        return new XSSFColor(CTColor.Factory.newInstance(), this.workbook.getStylesSource().getIndexedColors());
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFHyperlink createHyperlink(HyperlinkType type) {
        return new XSSFHyperlink(type);
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFFormulaEvaluator createFormulaEvaluator() {
        return new XSSFFormulaEvaluator(this.workbook);
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public XSSFClientAnchor createClientAnchor() {
        return new XSSFClientAnchor();
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public AreaReference createAreaReference(String reference) {
        return new AreaReference(reference, this.workbook.getSpreadsheetVersion());
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public AreaReference createAreaReference(CellReference topLeft, CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.workbook.getSpreadsheetVersion());
    }
}
