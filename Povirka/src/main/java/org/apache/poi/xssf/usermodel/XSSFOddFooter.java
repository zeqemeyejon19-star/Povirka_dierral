package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;

/* JADX INFO: loaded from: classes.dex */
public class XSSFOddFooter extends XSSFHeaderFooter implements Footer {
    public XSSFOddFooter(CTHeaderFooter headerFooter) {
        super(headerFooter);
    }

    @Override // org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter
    public String getText() {
        return getHeaderFooter().getOddFooter();
    }

    @Override // org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter
    public void setText(String text) {
        if (text == null) {
            getHeaderFooter().unsetOddFooter();
        } else {
            getHeaderFooter().setOddFooter(text);
        }
    }
}
