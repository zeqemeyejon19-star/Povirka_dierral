package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.BorderFormatting;
import org.apache.poi.ss.usermodel.DifferentialStyleProvider;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDxf;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumFmt;

/* JADX INFO: loaded from: classes.dex */
public class XSSFDxfStyleProvider implements DifferentialStyleProvider {
    private final BorderFormatting border;
    private final IndexedColorMap colorMap;
    private final PatternFormatting fill;
    private final FontFormatting font;
    private final ExcelNumberFormat number;
    private final int stripeSize;

    public XSSFDxfStyleProvider(CTDxf dxf, int stripeSize, IndexedColorMap colorMap) {
        this.stripeSize = stripeSize;
        this.colorMap = colorMap;
        if (dxf == null) {
            this.border = null;
            this.font = null;
            this.number = null;
            this.fill = null;
            return;
        }
        this.border = dxf.isSetBorder() ? new XSSFBorderFormatting(dxf.getBorder(), colorMap) : null;
        this.font = dxf.isSetFont() ? new XSSFFontFormatting(dxf.getFont(), colorMap) : null;
        if (dxf.isSetNumFmt()) {
            CTNumFmt numFmt = dxf.getNumFmt();
            this.number = new ExcelNumberFormat((int) numFmt.getNumFmtId(), numFmt.getFormatCode());
        } else {
            this.number = null;
        }
        this.fill = dxf.isSetFill() ? new XSSFPatternFormatting(dxf.getFill(), colorMap) : null;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public BorderFormatting getBorderFormatting() {
        return this.border;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public FontFormatting getFontFormatting() {
        return this.font;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public ExcelNumberFormat getNumberFormat() {
        return this.number;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public PatternFormatting getPatternFormatting() {
        return this.fill;
    }

    @Override // org.apache.poi.ss.usermodel.DifferentialStyleProvider
    public int getStripeSize() {
        return this.stripeSize;
    }
}
