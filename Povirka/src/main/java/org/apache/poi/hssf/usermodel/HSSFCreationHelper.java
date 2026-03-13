package org.apache.poi.hssf.usermodel;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;

/* JADX INFO: loaded from: classes.dex */
public class HSSFCreationHelper implements CreationHelper {
    private final HSSFWorkbook workbook;

    @Internal(since = "3.15 beta 3")
    HSSFCreationHelper(HSSFWorkbook wb) {
        this.workbook = wb;
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFRichTextString createRichTextString(String text) {
        return new HSSFRichTextString(text);
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFDataFormat createDataFormat() {
        return this.workbook.createDataFormat();
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFHyperlink createHyperlink(HyperlinkType type) {
        return new HSSFHyperlink(type);
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFExtendedColor createExtendedColor() {
        return new HSSFExtendedColor(new ExtendedColor());
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFFormulaEvaluator createFormulaEvaluator() {
        return new HSSFFormulaEvaluator(this.workbook);
    }

    @Override // org.apache.poi.ss.usermodel.CreationHelper
    public HSSFClientAnchor createClientAnchor() {
        return new HSSFClientAnchor();
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
