package org.apache.poi.xssf.usermodel.extensions;

import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.helpers.HeaderFooterHelper;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;

/* JADX INFO: loaded from: classes.dex */
public abstract class XSSFHeaderFooter implements HeaderFooter {
    private CTHeaderFooter headerFooter;
    private boolean stripFields = false;
    private HeaderFooterHelper helper = new HeaderFooterHelper();

    public abstract String getText();

    protected abstract void setText(String str);

    public XSSFHeaderFooter(CTHeaderFooter headerFooter) {
        this.headerFooter = headerFooter;
    }

    @Internal
    public CTHeaderFooter getHeaderFooter() {
        return this.headerFooter;
    }

    public String getValue() {
        String value = getText();
        if (value == null) {
            return "";
        }
        return value;
    }

    public boolean areFieldsStripped() {
        return this.stripFields;
    }

    public void setAreFieldsStripped(boolean stripFields) {
        this.stripFields = stripFields;
    }

    public static String stripFields(String text) {
        return org.apache.poi.hssf.usermodel.HeaderFooter.stripFields(text);
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public String getCenter() {
        String text = this.helper.getCenterSection(getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public String getLeft() {
        String text = this.helper.getLeftSection(getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public String getRight() {
        String text = this.helper.getRightSection(getText());
        if (this.stripFields) {
            return stripFields(text);
        }
        return text;
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public void setCenter(String newCenter) {
        setText(this.helper.setCenterSection(getText(), newCenter));
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public void setLeft(String newLeft) {
        setText(this.helper.setLeftSection(getText(), newLeft));
    }

    @Override // org.apache.poi.ss.usermodel.HeaderFooter
    public void setRight(String newRight) {
        setText(this.helper.setRightSection(getText(), newRight));
    }
}
