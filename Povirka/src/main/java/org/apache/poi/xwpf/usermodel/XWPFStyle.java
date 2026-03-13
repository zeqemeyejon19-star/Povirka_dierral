package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

/* JADX INFO: loaded from: classes.dex */
public class XWPFStyle {
    private CTStyle ctStyle;
    protected XWPFStyles styles;

    public XWPFStyle(CTStyle style) {
        this(style, null);
    }

    public XWPFStyle(CTStyle style, XWPFStyles styles) {
        this.ctStyle = style;
        this.styles = styles;
    }

    public String getStyleId() {
        return this.ctStyle.getStyleId();
    }

    public void setStyleId(String styleId) {
        this.ctStyle.setStyleId(styleId);
    }

    public STStyleType.Enum getType() {
        return this.ctStyle.getType();
    }

    public void setType(STStyleType.Enum type) {
        this.ctStyle.setType(type);
    }

    public void setStyle(CTStyle style) {
        this.ctStyle = style;
    }

    public CTStyle getCTStyle() {
        return this.ctStyle;
    }

    public XWPFStyles getStyles() {
        return this.styles;
    }

    public String getBasisStyleID() {
        if (this.ctStyle.getBasedOn() != null) {
            return this.ctStyle.getBasedOn().getVal();
        }
        return null;
    }

    public String getLinkStyleID() {
        if (this.ctStyle.getLink() != null) {
            return this.ctStyle.getLink().getVal();
        }
        return null;
    }

    public String getNextStyleID() {
        if (this.ctStyle.getNext() != null) {
            return this.ctStyle.getNext().getVal();
        }
        return null;
    }

    public String getName() {
        if (this.ctStyle.isSetName()) {
            return this.ctStyle.getName().getVal();
        }
        return null;
    }

    public boolean hasSameName(XWPFStyle compStyle) {
        CTStyle ctCompStyle = compStyle.getCTStyle();
        String name = ctCompStyle.getName().getVal();
        return name.equals(this.ctStyle.getName().getVal());
    }
}
