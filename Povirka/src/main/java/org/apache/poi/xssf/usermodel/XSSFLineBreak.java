package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

/* JADX INFO: loaded from: classes.dex */
class XSSFLineBreak extends XSSFTextRun {
    private final CTTextCharacterProperties _brProps;

    XSSFLineBreak(CTRegularTextRun r, XSSFTextParagraph p, CTTextCharacterProperties brProps) {
        super(r, p);
        this._brProps = brProps;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFTextRun
    protected CTTextCharacterProperties getRPr() {
        return this._brProps;
    }

    @Override // org.apache.poi.xssf.usermodel.XSSFTextRun
    public void setText(String text) {
        throw new IllegalStateException("You cannot change text of a line break, it is always '\\n'");
    }
}
