package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.ss.usermodel.Header;

/* JADX INFO: loaded from: classes.dex */
public final class HSSFHeader extends HeaderFooter implements Header {
    private final PageSettingsBlock _psb;

    protected HSSFHeader(PageSettingsBlock psb) {
        this._psb = psb;
    }

    @Override // org.apache.poi.hssf.usermodel.HeaderFooter
    protected String getRawText() {
        HeaderRecord hf = this._psb.getHeader();
        if (hf == null) {
            return "";
        }
        return hf.getText();
    }

    @Override // org.apache.poi.hssf.usermodel.HeaderFooter
    protected void setHeaderFooterText(String text) {
        HeaderRecord hfr = this._psb.getHeader();
        if (hfr == null) {
            this._psb.setHeader(new HeaderRecord(text));
        } else {
            hfr.setText(text);
        }
    }
}
